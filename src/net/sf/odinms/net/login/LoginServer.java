package net.sf.odinms.net.login;

import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MapleServerHandler;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.login.remote.LoginWorldInterface;
import net.sf.odinms.net.mina.MapleCodecFactory;
import net.sf.odinms.net.world.remote.WorldLoginInterface;
import net.sf.odinms.net.world.remote.WorldRegistry;
import net.sf.odinms.server.TimerManager;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

public class LoginServer implements Runnable, LoginServerMBean {

    public static final int PORT = 11111;
    private IoAcceptor acceptor;
    private static WorldRegistry worldRegistry = null;
    private Map<Integer, String> channelServer = new HashMap<Integer, String>();
    private LoginWorldInterface lwi;
    private WorldLoginInterface wli;
    private Properties prop = new Properties();
    private Properties initialProp = new Properties();
    private Boolean worldReady = Boolean.TRUE;
    private Properties subnetInfo = new Properties();
    private Map<Integer, Integer> load = new HashMap<Integer, Integer>();
    private String serverName, eventMessage;
    private int flag, maxCharacters, userLimit;
    private static LoginServer instance = new LoginServer();
    private String arraystring;

    static {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.registerMBean(instance, new ObjectName("net.sf.odinms.net.login:type=LoginServer,name=LoginServer"));
        } catch (Exception e) {
            System.err.println("MBEAN ERROR" + e);
        }
    }

    public static LoginServer getInstance() {
        return instance;
    }

    public Set<Integer> getChannels() {
        return channelServer.keySet();
    }

    public void addChannel(int channel, String ip) {
        channelServer.put(channel, ip);
        load.put(channel, 0);
    }

    public void removeChannel(int channel) {
        channelServer.remove(channel);
        load.remove(channel);
    }

    public String getIP(int channel) {
        return channelServer.get(channel);
    }

    public void reconnectWorld() {
        // check if the connection is really gone
        try {
            wli.isAvailable();
        } catch (RemoteException ex) {
            synchronized (worldReady) {
                worldReady = Boolean.FALSE;
            }
            synchronized (lwi) {
                synchronized (worldReady) {
                    if (worldReady) {
                        return;
                    }
                }
                System.out.println("Reconnecting to world server");
                synchronized (wli) {
                    // completely re-establish the rmi connection
                    try {
                        FileReader fileReader = new FileReader(System.getProperty("net.sf.odinms.login.config"));
                        initialProp.load(fileReader);
                        fileReader.close();
                        Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"), Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
                        worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
                        lwi = new LoginWorldInterfaceImpl();
                        wli = worldRegistry.registerLoginServer(initialProp.getProperty("net.sf.odinms.login.key"), lwi);
                        Properties dbProp = new Properties();
                        fileReader = new FileReader("db.properties");
                        dbProp.load(fileReader);
                        fileReader.close();
                        DatabaseConnection.setProps(dbProp);
                        DatabaseConnection.getConnection();
                        prop = wli.getWorldProperties();
                        userLimit = Integer.parseInt(prop.getProperty("net.sf.odinms.login.userlimit"));
                        serverName = prop.getProperty("net.sf.odinms.login.serverName");
                        eventMessage = prop.getProperty("net.sf.odinms.login.eventMessage");
                        flag = Integer.parseInt(prop.getProperty("net.sf.odinms.login.flag"));
                        maxCharacters = Integer.parseInt(prop.getProperty("net.sf.odinms.login.maxCharacters"));
                        try {
                            fileReader = new FileReader("subnet.properties");
                            subnetInfo.load(fileReader);
                            fileReader.close();
                        } catch (Exception e) {
                            System.err.println("Could not load subnet configuration, falling back to world defaults" + e);
                        }
                    } catch (Exception e) {
                        System.err.println("Reconnecting failed" + e);
                    }
                    worldReady = Boolean.TRUE;
                }
            }
            synchronized (worldReady) {
                worldReady.notifyAll();
            }
        }
    }

    @Override
    public void run() {
        try {
            FileReader fileReader = new FileReader(System.getProperty("net.sf.odinms.login.config"));
            initialProp.load(fileReader);
            fileReader.close();
            Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"),
                    Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
            worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
            lwi = new LoginWorldInterfaceImpl();
            wli = worldRegistry.registerLoginServer(initialProp.getProperty("net.sf.odinms.login.key"), lwi);
            Properties dbProp = new Properties();
            fileReader = new FileReader("db.properties");
            dbProp.load(fileReader);
            fileReader.close();
            DatabaseConnection.setProps(dbProp);
            DatabaseConnection.getConnection();
            prop = wli.getWorldProperties();
            userLimit = Integer.parseInt(prop.getProperty("net.sf.odinms.login.userlimit"));
            serverName = prop.getProperty("net.sf.odinms.login.serverName");
            eventMessage = prop.getProperty("net.sf.odinms.login.eventMessage");
            flag = Integer.parseInt(prop.getProperty("net.sf.odinms.login.flag"));
            maxCharacters = Integer.parseInt(prop.getProperty("net.sf.odinms.login.maxCharacters"));
            try {
                fileReader = new FileReader("subnet.properties");
                subnetInfo.load(fileReader);
                fileReader.close();
            } catch (Exception e) {
                System.err.println("subnet.properties could not be found, falling back to world defaults\n" + e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to world server.", e);
        }

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        acceptor = new SocketAcceptor();

        SocketAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        cfg.getSessionConfig().setTcpNoDelay(true);
        //    logOffAllNoobs();
        TimerManager tMan = TimerManager.getInstance();       
        tMan.start();
        getArrayString();
        tMan.register(new RankingWorker(), 3600000);
        try {
            acceptor.bind(new InetSocketAddress(PORT), new MapleServerHandler(PacketProcessor.getProcessor(PacketProcessor.Mode.LOGINSERVER)), cfg);
            System.out.println("Listening on port " + PORT + ".");
        } catch (IOException e) {
            System.err.println("Binding to port " + PORT + " failed" + e);
        }
    }

    public void shutdown() {
        System.out.println("Shutting down...");
        try {
            worldRegistry.deregisterLoginServer(lwi);
        } catch (RemoteException e) {
            // doesn't matter we're shutting down anyway
        }
        TimerManager.getInstance().stop();
        System.exit(0);
    }

    public WorldLoginInterface getWorldInterface() {
        synchronized (worldReady) {
            while (!worldReady) {
                try {
                    worldReady.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return wli;
    }

    public static void main(String args[]) {
        try {
            LoginServer.getInstance().run();
        } catch (Exception ex) {
            System.err.println("Error initializing loginserver" + ex);
        }
    }

    public Properties getSubnetInfo() {
        return subnetInfo;
    }

    public String getServerName() {
        return serverName;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public int getFlag() {
        return flag;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public Map<Integer, Integer> getLoad() {
        return load;
    }

    public void setLoad(Map<Integer, Integer> load) {
        this.load = load;
    }

    public void setEventMessage(String newMessage) {
        this.eventMessage = newMessage;
    }

    public void setFlag(int newflag) {
        flag = newflag;
    }

    public int getNumberOfSessions() {
        return acceptor.getManagedSessions(new InetSocketAddress(PORT)).size();
    }

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int newLimit) {
        userLimit = newLimit;
    }

    private void getArrayString() {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT array FROM `array_string` WHERE id = 1;";
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                arraystring = rs.getString("array");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally{
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void updateArrayString(String arraylar) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "UPDATE `array_string` SET array = ?;";
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                arraystring = rs.getString("array");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally{
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
