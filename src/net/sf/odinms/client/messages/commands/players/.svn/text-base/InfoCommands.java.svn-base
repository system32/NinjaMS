/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import java.rmi.RemoteException;
import java.util.Map;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.CharInfoProcessor;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.channel.ChannelServer;

import net.sf.odinms.net.world.remote.WorldLocation;

/**
 *
 * @author Admin
 */
public class InfoCommands implements PlayerCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("ninjaglare")) {
            MapleCharacter other = c.getPlayer();
            if (splitted.length == 2) {
                try {
                    WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                    if (loc != null) {
                        other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    } else {
                        mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                        return;
                    }
                } catch (Exception e) {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                    return;
                }
            }
            CharInfoProcessor.getNinjaGlare(mc, other);
        } else if (splitted[0].equalsIgnoreCase("connected")) {
            try {
                Map<Integer, Integer> connected = c.getChannelServer().getWorldInterface().getConnected();
                StringBuilder conStr = new StringBuilder("Connected Clients: ");
                boolean first = true;
                for (int i : connected.keySet()) {
                    if (!first) {
                        conStr.append(", ");
                    } else {
                        first = false;
                    }
                    if (i == 0) {
                        conStr.append("Total: ");
                        conStr.append(connected.get(i));
                    } else {
                        conStr.append("Ch");
                        conStr.append(i);
                        conStr.append(": ");
                        conStr.append(connected.get(i));
                    }
                }
                new ServernoticeMapleClientMessageCallback(c).dropMessage(conStr.toString());
            } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
        } else if (splitted[0].equalsIgnoreCase("ninjatop10")) {
            int tocheck = 1;
            if (splitted.length > 1) {
                try {
                    tocheck = Integer.parseInt(splitted[1]);
                } catch (NumberFormatException e) {
                    CharInfoProcessor.getNinjaTop10(mc, splitted[1]);
                    return;
                }
            }
            CharInfoProcessor.getNinjaTop10(mc, tocheck);
        } else if (splitted[0].equalsIgnoreCase("taotop10")) {
            int tocheck = 1;
            if (splitted.length > 1) {
                try {
                    tocheck = Integer.parseInt(splitted[1]);
                } catch (NumberFormatException e) {
                    CharInfoProcessor.getTaoTop10(mc, splitted[1]);
                    return;
                }
            }
            CharInfoProcessor.getTaoTop10(mc, tocheck);
        } 
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("ninjaglare", "<ign>", "Shows Stats of the person"),
                    new PlayerCommandDefinition("connected", "", "Shows the number of players online"),
                    new PlayerCommandDefinition("ninjatop10", "<start from rank / person name>", "shows rb ranking"),   
                    new PlayerCommandDefinition("taotop10", "<start from rank / person name>", "shows tao ranking"),
                };


    }
}
