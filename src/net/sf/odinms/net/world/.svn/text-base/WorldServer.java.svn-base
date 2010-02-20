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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.world;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.Properties;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.client.NinjaMS.IRCStuff.PlayerIRC;

import net.sf.odinms.database.DatabaseConnection;
import org.jibble.pircbot.Colors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matze
 */
public class WorldServer {

    private static WorldServer instance = null;
    private static Logger log = LoggerFactory.getLogger(WorldServer.class);
    private int worldId;
    private Properties dbProp = new Properties();
    private Properties worldProp = new Properties();
    private boolean doubleRate;
    private MainIRC bot = null;
    private PlayerIRC bot1 = null;

    private WorldServer() {
        try {            
            InputStreamReader is = new FileReader("db.properties");
            dbProp.load(is);
            is.close();
            DatabaseConnection.setProps(dbProp);
            DatabaseConnection.getConnection();            
            is = new FileReader("world.properties");
            worldProp.load(is);
            is.close();
        } catch (Exception e) {
            log.error("Could not configuration", e);
        }
    }

    public synchronized static WorldServer getInstance() {
        if (instance == null) {
            instance = new WorldServer();
        }
        return instance;
    }

    public int getWorldId() {
        return worldId;
    }

    public Properties getDbProp() {
        return dbProp;
    }

    public Properties getWorldProp() {
        return worldProp;
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT,
                    new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
            registry.rebind("WorldRegistry", WorldRegistryImpl.getInstance());
            log.info("World server has initated successfully.");
        } catch (RemoteException ex) {
            log.error("Could not initialize RMI system", ex);
        }
    }

    public void setDoubleRate() {
        doubleRate = true;
    }

    public boolean isDoubleRate() {
        if (Calendar.DAY_OF_WEEK == Calendar.SUNDAY) {
            if (Calendar.HOUR > 7 && Calendar.HOUR < 9) {
                return true;
            }
        } else if (Calendar.DAY_OF_WEEK == Calendar.SATURDAY) {
            if (Calendar.HOUR > 7 && Calendar.HOUR < 9) {
                return true;
            }
        } else if (doubleRate) {
            return true;
        }
        return false;
    }

    public void turnIRCBot() {
        bot = MainIRC.getInstance();
    }

    public MainIRC getBot() {
        return bot;
    }

    public void sendIRCMsg(String msg) {
        if(bot == null){
            turnIRCBot();
        }
        bot.sendIrcMessage("World " + msg);
    }   
}
