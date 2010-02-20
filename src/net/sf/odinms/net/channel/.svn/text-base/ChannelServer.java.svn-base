/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.net.channel;

import java.util.logging.Level;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.client.NinjaMS.MapleFML;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.MapleServerHandler;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.channel.OliveroMatic.AutomagicShit;
import net.sf.odinms.net.channel.remote.ChannelWorldInterface;
import net.sf.odinms.net.mina.MapleCodecFactory;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.net.world.guild.MapleGuildCharacter;
import net.sf.odinms.net.world.guild.MapleGuildSummary;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.net.world.remote.WorldRegistry;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.scripting.event.EventScriptManager;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleSquad;
import net.sf.odinms.server.MapleSquadType;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.ShutdownServer;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.maps.MapMonitor;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.net.channel.storage.*;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.CloseFuture;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.jibble.pircbot.Colors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelServer implements Runnable, ChannelServerMBean {

    private static int uniqueID = 1;
    private int port = 7575;
    private static Properties initialProp;
    private static final Logger log = LoggerFactory.getLogger(ChannelServer.class);
    private static ServerPlayerStorage serverplayerstorage = new ServerPlayerStorage();
    //private static ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
    private static WorldRegistry worldRegistry;
    private PlayerStorage players = new PlayerStorage();
    // private Map<String, MapleCharacter> clients = new LinkedHashMap<String, MapleCharacter>();
    private String serverMessage;
    private int expRate;
    private int mesoRate;
    private int dropRate;
    private int bossdropRate;
    private int petExpRate;
    private boolean gmWhiteText;
    private boolean cashshop;
    private boolean dropUndroppables;
    private boolean moreThanOne;
    private int channel;
    private String key;
    private Properties props = new Properties();
    private ChannelWorldInterface cwi;
    private WorldChannelInterface wci = null;
    private IoAcceptor acceptor;
    private String ip;
    private boolean shutdown = false;
    private boolean finishedShutdown = false;
    private MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private static Map<Integer, ChannelServer> instances = new HashMap<Integer, ChannelServer>();
    private static Map<String, ChannelServer> pendingInstances = new HashMap<String, ChannelServer>();
    private Map<Integer, MapleGuildSummary> gsStore = new HashMap<Integer, MapleGuildSummary>();
    private Boolean worldReady = true;
    private Map<MapleSquadType, MapleSquad> mapleSquads = new HashMap<MapleSquadType, MapleSquad>();
    private static boolean shuttingdown;
    private Map<Integer, MapMonitor> mapMonitors = new HashMap<Integer, MapMonitor>();
    private boolean isRebooting = false;

    public void addMapMonitor(int mapId, MapMonitor monitor) {
        mapMonitors.put(mapId, monitor);
    }

    public void removeMapMonitor(int mapId) {
        if (mapMonitors.containsKey(mapId)) {
            mapMonitors.remove(mapId);
        }
    }

    private ChannelServer(String key) {
        mapFactory = new MapleMapFactory(MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Map.wz")), MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")));
        this.key = key;
    }

    public static WorldRegistry getWorldRegistry() {
        return worldRegistry;
    }

    public void reconnectWorld() {
        // check if the connection is really gone
        try {
            wci.isAvailable();
        } catch (RemoteException ex) {
            synchronized (worldReady) {
                worldReady = false;
            }
            synchronized (cwi) {
                synchronized (worldReady) {
                    if (worldReady) {
                        return;
                    }
                }
                log.warn("Reconnecting to world server");
                synchronized (wci) {
                    // completely re-establish the rmi connection
                    try {
                        initialProp = new Properties();
                        FileReader fr = new FileReader(System.getProperty("net.sf.odinms.channel.config"));
                        initialProp.load(fr);
                        fr.close();
                        Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"),
                                Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
                        worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
                        cwi = new ChannelWorldInterfaceImpl(this);
                        wci = worldRegistry.registerChannelServer(key, cwi);
                        props = wci.getGameProperties();
                        expRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.exp"));
                        mesoRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.meso"));
                        dropRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.drop"));
                        bossdropRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.bossdrop"));
                        petExpRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.petExp"));
                        dropUndroppables = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.alldrop", "false"));
                        moreThanOne = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.morethanone", "false"));
                        gmWhiteText = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.gmWhiteText", "false"));
                        cashshop = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.cashshop", "false"));
                        Properties dbProp = new Properties();
                        fr = new FileReader("db.properties");
                        dbProp.load(fr);
                        fr.close();
                        DatabaseConnection.setProps(dbProp);
                        DatabaseConnection.getConnection();
                        wci.serverReady();
                    } catch (Exception e) {
                        log.error("Reconnecting failed", e);
                    }
                    worldReady = true;
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
            cwi = new ChannelWorldInterfaceImpl(this);
            wci = worldRegistry.registerChannelServer(key, cwi);
            props = wci.getGameProperties();
            expRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.exp"));
            mesoRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.meso"));
            dropRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.drop"));
            bossdropRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.bossdrop"));
            petExpRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.petExp"));
            dropUndroppables = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.alldrop", "false"));
            moreThanOne = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.morethanone", "false"));
            eventSM = new EventScriptManager(this, props.getProperty("net.sf.odinms.channel.events").split(","));
            gmWhiteText = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.gmWhiteText", "false"));
            cashshop = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.cashshop", "false"));
            Properties dbProp = new Properties();
            FileReader fileReader = new FileReader("db.properties");
            dbProp.load(fileReader);
            fileReader.close();
            DatabaseConnection.setProps(dbProp);
            DatabaseConnection.getConnection();
            // MapleShopFactory.getInstance().loadAllShops();
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0");
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE characters SET mutality = 0");
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                System.err.println("Could not reset databases loggedin state" + ex);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        port = Integer.parseInt(props.getProperty("net.sf.odinms.channel.net.port"));
        ip = props.getProperty("net.sf.odinms.channel.net.interface") + ":" + port;
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        acceptor = new SocketAcceptor();
        SocketAcceptorConfig cfg = new SocketAcceptorConfig();
        // cfg.setThreadModel(ThreadModel.MANUAL); // *fingers crossed*, I hope the executor filter handles everything
        // executor = new ThreadPoolExecutor(16, 16, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        // cfg.getFilterChain().addLast("executor", new ExecutorFilter(executor));
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        cfg.getSessionConfig().setTcpNoDelay(true);
        // Item.loadInitialDataFromDB();
        TimerManager tMan = TimerManager.getInstance();
        tMan.start();
        tMan.register(AutobanManager.getInstance(), 60000);
        setServerMessage();
        if (channel == 1) {
            AutomagicShit.getInstance().start();
        }
        if (channel == 9) {
            sendIRCNotice();
        }
        tMan.register(new spawnMobs(), 10000);
        tMan.register(new autoSave(), 5 * 1000 * 60 * Integer.parseInt(initialProp.getProperty("net.sf.odinms.channel.count")), 10 * 1000 * 60 * (channel - 1) + (60000));
        try {
            MapleServerHandler serverHandler = new MapleServerHandler(PacketProcessor.getProcessor(PacketProcessor.Mode.CHANNELSERVER), channel);
            acceptor.bind(new InetSocketAddress(port), serverHandler, cfg);
            log.info("Channel {}: Listening on port {}", getChannel(), port);
            wci.serverReady();
            eventSM.init();
        } catch (IOException e) {
            log.error("Binding to port " + port + " failed (ch: " + getChannel() + ")", e);
        }
    }

    public void shutdown() {
        // dc all clients by hand so we get sessionClosed...
        shutdown = true;
        List<CloseFuture> futures = new LinkedList<CloseFuture>();
        Collection<MapleCharacter> allchars = players.getAllCharacters();
        MapleCharacter chrs[] = allchars.toArray(new MapleCharacter[allchars.size()]);
        for (MapleCharacter chr : chrs) {
            if (chr.getTrade() != null) {
                MapleTrade.cancelTrade(chr);
            }
            if (chr.getEventInstance() != null) {
                chr.getEventInstance().playerDisconnected(chr);
            }
            chr.forceSave(true, true);
            if (chr.getCheatTracker() != null) {
                chr.getCheatTracker().dispose();
            }
            removePlayer(chr);
        }
        for (MapleCharacter chr : chrs) {
            futures.add(chr.getClient().getSession().close());
        }
        for (CloseFuture future : futures) {
            future.join(500);
        }
        try {
            getWorldRegistry().deregisterChannelServer(channel);
        } catch (RemoteException ex) {
            java.util.logging.Logger.getLogger(ChannelServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        finishedShutdown = true;
        wci = null;
        cwi = null;
    }

    public void unbind() {
        acceptor.unbindAll();
    }

    public boolean hasFinishedShutdown() {
        return finishedShutdown;
    }

    public MapleMapFactory getMapFactory() {
        return mapFactory;
    }

    public static ChannelServer newInstance(String key) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
        ChannelServer instance = new ChannelServer(key);
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanServer.registerMBean(instance, new ObjectName("net.sf.odinms.net.channel:type=ChannelServer,name=ChannelServer" + uniqueID++));
        pendingInstances.put(key, instance);
        return instance;
    }

    public static ChannelServer getInstance(int channel) {
        return instances.get(channel);
    }

    public void addPlayer(MapleCharacter chr) {
        players.registerPlayer(chr);
        chr.getClient().getSession().write(MaplePacketCreator.serverMessage(serverMessage));
    }

    public IPlayerStorage getPlayerStorage() {
        return players;
    }

    public void removePlayer(MapleCharacter chr) {
        players.deregisterPlayer(chr);
    }

    public int getConnectedClients() {
        return players.getAllCharacters().size();
    }

    @Override
    public String getServerMessage() {
        return serverMessage;
    }

    @Override
    public void setServerMessage(String newMessage) {
        serverMessage = newMessage;
        broadcastPacket(MaplePacketCreator.serverMessage(serverMessage));
    }

    public void setServerMessage() {
        try {
            serverMessage = getWorldInterface().getArrayString();
        } catch (RemoteException ex) {
            java.util.logging.Logger.getLogger(ChannelServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void broadcastPacket(MaplePacket data) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            chr.getClient().getSession().write(data);
        }
    }

    @Override
    public int getExpRate() {
        return expRate;
    }

    @Override
    public void setExpRate(int expRate) {
        this.expRate = expRate;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        if (pendingInstances.containsKey(key)) {
            pendingInstances.remove(key);
        }
        if (instances.containsKey(channel)) {
            instances.remove(channel);
        }
        instances.put(channel, this);
        this.channel = channel;
        this.mapFactory.setChannel(channel);
    }

    public static Collection<ChannelServer> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public String getIP() {
        return ip;
    }

    public String getIP(int channel) {
        try {
            return getWorldInterface().getIP(channel);
        } catch (RemoteException e) {
            log.error("Lost connection to world server", e);
            throw new RuntimeException("Lost connection to world server");
        }
    }

    public WorldChannelInterface getWorldInterface() {
        synchronized (worldReady) {
            while (!worldReady) {
                try {
                    worldReady.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
        return wci;
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }

    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void shutdown(int time) {
        broadcastPacket(MaplePacketCreator.serverNotice(0, "The world will be shut down in " + (time / 60000) + " minutes, please log off safely"));
        TimerManager.getInstance().schedule(new ShutdownServer(getChannel()), time);
    }

    @Override
    public void shutdownWorld(int time) {
        try {
            getWorldInterface().shutdown(time);
        } catch (RemoteException e) {
            reconnectWorld();
        }
    }

    public int getLoadedMaps() {
        return mapFactory.getLoadedMaps();
    }

    public EventScriptManager getEventSM() {
        return eventSM;
    }

    public void reloadEvents() {
        eventSM.cancel();
        eventSM = new EventScriptManager(this, props.getProperty("net.sf.odinms.channel.events").split(","));
        eventSM.init();
    }

    @Override
    public int getMesoRate() {
        return mesoRate;
    }

    @Override
    public void setMesoRate(int mesoRate) {
        this.mesoRate = mesoRate;
    }

    @Override
    public int getDropRate() {
        return dropRate;
    }

    @Override
    public void setDropRate(int dropRate) {
        this.dropRate = dropRate;
    }

    @Override
    public int getBossDropRate() {
        return bossdropRate;
    }

    @Override
    public void setBossDropRate(int bossdropRate) {
        this.bossdropRate = bossdropRate;
    }

    @Override
    public int getPetExpRate() {
        return petExpRate;
    }

    @Override
    public void setPetExpRate(int petExpRate) {
        this.petExpRate = petExpRate;
    }

    public boolean allowUndroppablesDrop() {
        return dropUndroppables;
    }

    public boolean allowMoreThanOne() {
        return moreThanOne;
    }

    public boolean allowGmWhiteText() {
        return gmWhiteText;
    }

    public boolean allowCashshop() {
        return cashshop;
    }

    public MapleGuild getGuild(MapleGuildCharacter mgc) {
        int gid = mgc.getGuildId();
        MapleGuild g = null;
        try {
            g = this.getWorldInterface().getGuild(gid, mgc);
        } catch (RemoteException re) {
            log.error("RemoteException while fetching MapleGuild.", re);
            return null;
        }
        if (gsStore.get(gid) == null) {
            gsStore.put(gid, new MapleGuildSummary(g));
        }
        return g;
    }
    public MapleGuildSummary getGuildSummary(int gid) {
        if (gsStore.containsKey(gid)) {
            return gsStore.get(gid);
        } else {		//this shouldn't happen much, if ever, but if we're caught
            //without the summary, we'll have to do a worldop
            try {
                MapleGuild g = this.getWorldInterface().getGuild(gid, null);
                if (g != null) {
                    gsStore.put(gid, new MapleGuildSummary(g));
                }
                return gsStore.get(gid);	//if g is null, we will end up returning null

            } catch (RemoteException re) {
                log.error("RemoteException while fetching GuildSummary.", re);
                return null;
            }
        }
    }

    public void updateGuildSummary(int gid, MapleGuildSummary mgs) {
        gsStore.put(gid, mgs);
    }

    public void reloadGuildSummary() {
        try {
            MapleGuild g;
            for (int i : gsStore.keySet()) {
                g = this.getWorldInterface().getGuild(i, null);
                if (g != null) {
                    gsStore.put(i, new MapleGuildSummary(g));
                } else {
                    gsStore.remove(i);
                }
            }
        } catch (RemoteException re) {
            log.error("RemoteException while reloading GuildSummary.", re);
        }
    }

    public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
        initialProp = new Properties();
        initialProp.load(new FileReader(System.getProperty("net.sf.odinms.channel.config")));
        Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"), Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
        worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
        for (int i = 0; i < Integer.parseInt(initialProp.getProperty("net.sf.odinms.channel.count", "0")); i++) {
            newInstance(initialProp.getProperty("net.sf.odinms.channel." + i + ".key")).run();
        }
        DatabaseConnection.getConnection(); // touch - so we see database problems early...
        CommandProcessor.registerMBean();
        MapleQuest.getAllQuests();
        SpecialStuff.getInstance();
        MainIRC.getInstance();
    }

    public MapleSquad getMapleSquad(MapleSquadType type) {
        return mapleSquads.get(type);
    }

    public boolean addMapleSquad(MapleSquad squad, MapleSquadType type) {
        if (mapleSquads.get(type) == null) {
            mapleSquads.remove(type);
            mapleSquads.put(type, squad);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeMapleSquad(MapleSquad squad, MapleSquadType type) {
        if (mapleSquads.containsKey(type)) {
            if (mapleSquads.get(type) == squad) {
                mapleSquads.remove(type);
                return true;
            }
        }
        return false;
    }

    public static boolean isShuttingDown() {
        return shuttingdown;
    }

    public void setShuttingDown(boolean fuck) {
        shuttingdown = fuck;
    }

    public void broadcastGMPacket(MaplePacket data) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            if (chr.isJounin()) {
                chr.getClient().getSession().write(data);
            }
        }
    }

    public void broadcastStaffPacket(MaplePacket data) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            if (chr.isChunin()) {
                chr.getClient().getSession().write(data);
            }
        }
    }    

    private class spawnMobs implements Runnable {

        @Override
        public void run() {
            for (Entry<Integer, MapleMap> map : mapFactory.getMaps().entrySet()) {
                if (map.getValue() == null) {
                    log.error("Channel " + getChannel() + " mob factory failed to load " + map.getValue().getMapName() + " (" + map.getValue().getId() + ").");
                    map.getValue().deleteAndReloadMap();
                    continue;
                }
                map.getValue().respawn();
            }
        }
    }

    private class autoSave implements Runnable {

        @Override
        public void run() {
            int i = 0;
            for (final MapleCharacter lols : getPlayerStorage().getAllCharacters()) {
                i++;
                TimerManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if (lols != null) {
                            lols.runAutoSave();
                            lols.dropMessage(5, "Your Progress Has been saved AutoMagically.");
                        }
                    }
                }, i * 1000);
            }
            log.info("Saving characters to database for channel " + channel);
        }
    }

    public boolean isIsRebooting() {
        return isRebooting;
    }

    public void setIsRebooting(boolean isRebooting) {
        this.isRebooting = isRebooting;
    }

    public void scheduleReboot(){
        this.isRebooting = true;
        int i = this.channel;
        for (ChannelServer cserv : getAllInstances()) {
            cserv.broadcastPacket(MaplePacketCreator.serverNotice(6, "The Channel Is Now Rebooting in 30 seconds. Please CC safely to avoid problems"));
        }
        TimerManager.getInstance().schedule(new scheduleReboot(), 30 * 1000);
    }
    private class scheduleReboot implements Runnable {
        final int i = channel;
        @Override
        public void run() {
            instances.get(i).shutdown();
            instances.remove(i);
            log.info("Channel "+i+"Shut Down");
            try {
                newInstance(initialProp.getProperty("net.sf.odinms.channel." + i + ".key")).run();
            } catch (InstanceAlreadyExistsException ex) {
                java.util.logging.Logger.getLogger(ChannelServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MBeanRegistrationException ex) {
                java.util.logging.Logger.getLogger(ChannelServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotCompliantMBeanException ex) {
                java.util.logging.Logger.getLogger(ChannelServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedObjectNameException ex) {
                java.util.logging.Logger.getLogger(ChannelServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            log.info("Channel "+i+"Rebooted");
        }
        
    }

    public void restarttimers() {
        TimerManager tMan = TimerManager.getInstance();
        tMan.start();
        tMan.register(AutobanManager.getInstance(), 60000);
        tMan.register(new spawnMobs(), 10000);
        tMan.register(new autoSave(), 5 * 1000 * 60 * Integer.parseInt(initialProp.getProperty("net.sf.odinms.channel.count")), 10 * 1000 * 60 * (channel - 1) + (60000));
    }

    private void sendIRCNotice() {
        MainIRC.getInstance().sendGlobalMessage(Colors.DARK_GREEN + Colors.REVERSE + "The Server Is Up. See you in game");
        MainIRC.getInstance().sendGlobalMessage("******************************************************************************** ");
    }

    public void yellowWorldMessage(String msg) {
        for (MapleCharacter mc : getPlayerStorage().getAllCharacters()) {
            mc.getClient().getSession().write(MaplePacketCreator.sendYellowTip(msg));
        }
        if (channel == 1) {
            MainIRC.getInstance().sendIrcMessage("# Ninja Tip :" + msg);
        }
    }

    public List<MapleCharacter> getPartyMembers(MapleParty party) {
        List<MapleCharacter> partym = new LinkedList<MapleCharacter>();
        for (MaplePartyCharacter partychar : party.getMembers()) {
            if (partychar.getChannel() == getChannel()) { // Make sure the thing doesn't get duplicate plays due to ccing bug.
                MapleCharacter chr = getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null) {
                    partym.add(chr);
                }
            }
        }
        return partym;
    }

    public static ServerPlayerStorage getServerPlayerStorage() {
        return serverplayerstorage;
    }
}
