/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.IRCStuff.Commands;

import java.rmi.RemoteException;
import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.client.NinjaMS.Processors.CharInfoProcessor;
import net.sf.odinms.client.NinjaMS.Processors.ConnectedProcessor;
import net.sf.odinms.client.NinjaMS.Processors.NoticeProcessor;
import net.sf.odinms.client.NinjaMS.Processors.SearchProcessor;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Owner
 */
class OPUserCommands {

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
            ircMsg(sender,CharInfoProcessor.getNinjaGlare(other));
        } else if (command.equalsIgnoreCase("playercommands")) {
            ircMsg(sender, "Under Construction");
        } else if (command.equalsIgnoreCase("itemid")) {
            List<String> ids = SearchProcessor.getItemId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for (String id : ids) {
                    ircMsg(sender, id);
                }
            }
        } else if (command.equalsIgnoreCase("mapid")) {
            List<String> ids = SearchProcessor.getItemId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for (String id : ids) {
                    ircMsg(sender, id);
                }
            }
        } else if (command.equalsIgnoreCase("mobid")) {
            List<String> ids = SearchProcessor.getItemId(StringUtil.joinStringFrom(splitted, 1));
            if (ids != null && ids.size() > 0) {
                for (String id : ids) {
                    ircMsg(sender, id);
                }
            }
        } else if (command.equalsIgnoreCase("ban")) {
            if (splitted.length < 3) {
                ircMsg(sender, "You should be sacked for not knowing the ban command syntax");
            } else {
                String originalReason = StringUtil.joinStringFrom(splitted, 2);
                String reason = sender + " banned " + splitted[1] + ": " + originalReason + " (FROM IRC)";
                MapleCharacter target = null;
                try {
                    WorldLocation loc = ChannelServer.getInstance(1).getWorldInterface().getLocation(splitted[1]);
                    if (loc != null) {
                        target = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    } else {
                        ircMsg(sender, splitted[1] + "'Is not Online. Lets Try Offline ban :p.");
                    }
                } catch (RemoteException ex) {
                    ircMsg(sender, splitted[1] + "'Is not Online. Lets Try Offline ban :p.");
                }
                if (target != null) {
                    String readableTargetName = MapleCharacterUtil.makeMapleReadable(target.getName());
                    String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    reason += " (IP: " + ip + ")";
                    target.ban(reason);
                    ircMsg(sender + " Banned " + readableTargetName + " ipban for " + ip + " reason: " + originalReason);
                } else {
                    if (MapleCharacter.ban(splitted[1], reason, false)) {
                        ircMsg(sender + " Offline Banned " + splitted[1]);
                    } else {
                        ircMsg(sender, "Failed to ban " + splitted[1]);
                    }
                }
            }
        } else if (command.equalsIgnoreCase("unstuck")) {
            MapleCharacter target = null;
            try {
                WorldLocation loc = ChannelServer.getInstance(1).getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    target = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                } else {
                    ircMsg(sender, splitted[1] + "'Is not Online or does not exist.");
                    return;
                }
            } catch (RemoteException ex) {
                ircMsg(sender, splitted[1] + "'Is not Online or does not exist.");
                return;
            }
            if(target != null){
                target.unstuck();
                ircMsg(sender +" has performed a Magical spell to unstuck " + splitted[1]);
            } else {
                ircMsg(sender, splitted[1] + "'Is not Online or does not exist.");
            }
        } else if (command.equalsIgnoreCase("shutdownworld")){
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.broadcastPacket(MaplePacketCreator.serverNotice(1, "The Server is Shutting Down For a Reboot in 5 minutes. Please log off safely."));
            }
            CommandProcessor.forcePersisting();
            ChannelServer.getInstance(1).shutdownWorld(5 * 60000);
            ircMsg("The Game Server is Shutting Down For a Reboot in 5 minutes.");
        } else if (command.equalsIgnoreCase("notice")){
            NoticeProcessor.sendBlueNoticeWithNotice(StringUtil.joinStringFrom(splitted, 1));
            ircMsg(sender, "Your notice has been broadcasted in game");
        }
    }

    private static void ircMsg(String message) {
        MainIRC.getInstance().sendIrcMessage(message);
    }

    private static void ircMsg(String target, String message) {
        MainIRC.getInstance().sendMessage(target, message);
    }
}
