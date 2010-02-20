/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.messages.commands.intern;

import java.net.InetAddress;
import java.rmi.RemoteException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Admin
 */
public class WarpCommands implements InternCommand {

    public InternCommandDefinition[] getDefinition() {
       return new InternCommandDefinition[] {
           new InternCommandDefinition("warp", "", "warp command duh?")
       };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception{
        if (splitted[0].equals("warp")) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
               MapleMap target = victim.getMap();
               c.getPlayer().changeMap(target, target.findClosestSpawnpoint(victim.getPosition()));
            } else {
                try {
                    victim = c.getPlayer();
                    WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                    if (loc != null) {
                        mc.dropMessage("You will be cross-channel warped. This may take a few seconds.");
                        // WorldLocation loc = new WorldLocation(40000, 2);
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(loc.map);
                        String ip = c.getChannelServer().getIP(loc.channel);
                        c.getPlayer().getMap().removePlayer(c.getPlayer());
                        victim.setMap(target);
                        victim.changeChannel(loc.channel);
                    } else {
                        int map = Integer.parseInt(splitted[1]);
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(map);
                        c.getPlayer().changeMap(target, target.getPortal(0));
                    }
                } catch (Exception e) {
                    mc.dropMessage("Something went wrong " + e.getMessage());
                }
            }
        }
    }

}
