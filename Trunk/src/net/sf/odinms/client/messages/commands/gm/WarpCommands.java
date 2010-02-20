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
package net.sf.odinms.client.messages.commands.gm;

import java.net.InetAddress;
import java.rmi.RemoteException;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;

public class WarpCommands implements GMCommand {

    @Override
    public void execute(final MapleClient c, final MessageCallback mc, String[] splitted) throws Exception {
        ChannelServer cserv = c.getChannelServer();
        if (splitted[0].equals("warp")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                if (splitted.length == 2) {
                    MapleMap target = victim.getMap();
                    c.getPlayer().changeMap(target, target.findClosestSpawnpoint(victim.getPosition()));
                } else {
                    int mapid = Integer.parseInt(splitted[2]);
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                    victim.changeMap(target, target.getPortal(0));
                }
            } else {
                try {
                    victim = c.getPlayer();
                    WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                    if (loc != null) {
                        mc.dropMessage("You will be cross-channel warped. This may take a few seconds.");
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(loc.map);
                        String ip = c.getChannelServer().getIP(loc.channel);
                        c.getPlayer().getMap().removePlayer(c.getPlayer());
                        victim.setMap(target);
                        String[] socket = ip.split(":");
                        if (c.getPlayer().getTrade() != null) {
                            MapleTrade.cancelTrade(c.getPlayer());
                        }
                        try {
                            WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
                            wci.addBuffsToStorage(c.getPlayer().getId(), c.getPlayer().getAllBuffs());
                            wci.addCooldownsToStorage(c.getPlayer().getId(), c.getPlayer().getAllCoolDowns());
                        } catch (RemoteException e) {
                            c.getChannelServer().reconnectWorld();
                        }
                        c.getPlayer().saveToDB();
                        if (c.getPlayer().getCheatTracker() != null) {
                            c.getPlayer().getCheatTracker().dispose();
                        }
                        ChannelServer.getInstance(c.getChannel()).removePlayer(c.getPlayer());
                        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
                        try {
                            MaplePacket packet = MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]));
                            c.getSession().write(packet);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        int map = Integer.parseInt(splitted[1]);
                        MapleMap target = cserv.getMapFactory().getMap(map);
                        c.getPlayer().changeMap(target, target.getPortal(0));
                    }
                } catch (Exception e) {
                    mc.dropMessage("Something went wrong " + e.getMessage());
                }
            }
        } else if (splitted[0].equalsIgnoreCase("warphere")) {

            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    final MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    if (victim.getGMLevel() > c.getPlayer().getGMLevel()) {
                            victim.dropMessage(1, c.getPlayer().getName() + " needs you!");
                    } else if (victim != null && loc.channel == c.getChannel()) {
                        mc.dropMessage(victim.getName() + " is being warped to you.");
                        victim.cancelAllBuffs();
                        victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(
                                c.getPlayer().getPosition()));
                    } else if (victim != null) {
                        mc.dropMessage(victim.getName() + " is being CCed to you.");
                        victim.changeChannel(c.getChannel());
                        TimerManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(victim.getPosition()));
                            }
                        }, 1000);
                    } else {
                        mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                    }
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                if (c.getPlayer().isAdmin()) {
                    throw new Exception(e);
                }
            }
        } else if (splitted[0].equals("lolcastle")) {            
            MapleMap target = c.getChannelServer().getEventSM().getEventManager("lolcastle").getInstance("lolcastle").getMapFactory().getMap(990000300, false, false);
            c.getPlayer().changeMap(target, target.getPortal(0));
        } else if (splitted[0].equals("jail")) {
             WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
            if (loc != null) {
                MapleCharacter noob = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                if (noob != null) {
                    if (c.getPlayer().getGMLevel() >= noob.getGMLevel() || noob.isAdmin()) {
                        noob.jail();
                    } else {
                        c.getPlayer().jail();
                        mc.dropMessage("The ninja you tried to Jail is too 1337 for you to jail him");
                    }
                } else {
                    mc.dropMessage(splitted[1] + " does not exist.");
                }
            } else {
                mc.dropMessage(splitted[1] + " does not exist.");
            }
        } else if (splitted[0].equals("map")) {
            int mapid = Integer.parseInt(splitted[1]);
            MapleMap target = cserv.getMapFactory().getMap(mapid);
            MaplePortal targetPortal = null;
            if (splitted.length > 2) {
                try {
                    targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                } catch (IndexOutOfBoundsException ioobe) {
                    // noop, assume the gm didn't know how many portals there are
                } catch (NumberFormatException nfe) {
                    // noop, assume that the gm is drunk
                }
            }
            if (targetPortal == null) {
                targetPortal = target.getPortal(0);
            }
            c.getPlayer().changeMap(target, targetPortal);
        } else {
            mc.dropMessage("GM Command " + splitted[0] + " does not exist");
        }
    }

    @Override
    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("warp", "playername [targetid]", "Warps yourself to the player with the given name. When targetid is specified warps the player to the given mapid"),
                    new GMCommandDefinition("warphere", "playername", "Warps the player with the given name to yourself"),
                    new GMCommandDefinition("lolcastle", "[1-5]", "Warps you into Field of Judgement with the given level"),
                    new GMCommandDefinition("jail", "[2] playername", "Warps the player to a map that he can't leave"),
                    new GMCommandDefinition("map", "mapid", "Warps you to the given mapid (use /m instead)"),};
    }
}
