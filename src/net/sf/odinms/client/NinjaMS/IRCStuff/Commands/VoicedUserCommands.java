/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.IRCStuff.Commands;

import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.client.NinjaMS.Processors.CharInfoProcessor;
import net.sf.odinms.client.NinjaMS.Processors.ConnectedProcessor;
import net.sf.odinms.client.NinjaMS.Processors.SearchProcessor;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Owner
 */
class VoicedUserCommands {

    static void execute(String sender, String[] splitted) {
        String command = splitted[0];
        if (command.equalsIgnoreCase("commands")) {
            ircMsg(sender, "You should bang a wall and get Raped by AJ");
        } else if (command.equalsIgnoreCase("connected")) {
            ircMsg(ConnectedProcessor.getConnected());
        } else if (command.equalsIgnoreCase("onlineall")) {
            int i = 0;
            while (i < ChannelServer.getAllInstances().size()) {
                i++;
                ircMsg(sender, ConnectedProcessor.getOnline(i));
            }
        } else if (command.equalsIgnoreCase("online")) {
            int channel = Integer.parseInt(splitted[1]);
            ircMsg(sender, ConnectedProcessor.getOnline(channel));
        } else if (command.equalsIgnoreCase("ninjaglare")) {
            MapleCharacter other = null;
            try {
                WorldLocation loc = ChannelServer.getInstance(1).getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                } else {
                    ircMsg(sender, splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                ircMsg(sender, splitted[1] + "' does not exist, is CCing, or is offline.");
            }
            CharInfoProcessor.getNinjaGlare(other);
        } else if (command.equalsIgnoreCase("playercommands")) {
            ircMsg(sender, "Under Construction");
        } else if (command.equalsIgnoreCase("itemid")){
            List<String> ids = SearchProcessor.getItemId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for(String id : ids){
                    ircMsg(sender, id);
                }
            }
        } else if (command.equalsIgnoreCase("mapid")){
            List<String> ids = SearchProcessor.getItemId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for(String id : ids){
                    ircMsg(sender, id);
                }
            }
        } else if (command.equalsIgnoreCase("mobid")){
            List<String> ids = SearchProcessor.getItemId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for(String id : ids){
                    ircMsg(sender, id);
                }
            }
        }
    }

    private static void ircMsg(String message){
         MainIRC.getInstance().sendIrcMessage(message);
    }
    private static void ircMsg(String target, String message){
         MainIRC.getInstance().sendMessage(target, message);
    }

}
