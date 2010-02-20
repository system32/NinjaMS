/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.IRCStuff.Commands;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.client.NinjaMS.Processors.NoticeProcessor;
import net.sf.odinms.client.NinjaMS.Processors.UnbanProcessor;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleMonsterInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Owner
 */
class IRCOpCommands {

    static void execute(String sender, String[] splitted, String channel) {
        String command = splitted[0];
        if (command.equalsIgnoreCase("commands")) {
            ircMsg(sender, "You should bang a wall and get Raped by AJ");
        } else if (command.equalsIgnoreCase("gameban")) {
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
            if (target != null) {
                target.unstuck();
                ircMsg(sender + " has performed a Magical spell to unstuck " + splitted[1]);
            } else {
                ircMsg(sender, splitted[1] + "'Is not Online or does not exist.");
            }
        } else if (command.equalsIgnoreCase("shutdownworld")) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.broadcastPacket(MaplePacketCreator.serverNotice(1, "The Server is Shutting Down For a Reboot in 5 minutes. Please log off safely."));
            }
            CommandProcessor.forcePersisting();
            ChannelServer.getInstance(1).shutdownWorld(5 * 60000);
            ircMsg("The Game Server is Shutting Down For a Reboot in 5 minutes.");
        } else if (command.equalsIgnoreCase("notice")) {
            NoticeProcessor.sendBlueNoticeWithNotice(StringUtil.joinStringFrom(splitted, 1));
            ircMsg(channel, "Your notice has been broadcasted in game");
        } else {
            processSpecial(sender, splitted, channel);
        }
    }

    private static void ircMsg(String message) {
        MainIRC.getInstance().sendIrcMessage(message);
    }

    private static void ircMsg(String target, String message) {
        MainIRC.getInstance().sendMessage(target, message);
    }

    private static void kick(String channel, String sender, String reason) {
        MainIRC.getInstance().kick(channel, sender, reason);
    }

    private static void processSpecial(String sender, String[] splitted, String channel) {
        if (channel.equalsIgnoreCase("#ninjastaff")) {

            if (splitted[0].equalsIgnoreCase("insertdrop")) {
                try {
                    if (splitted.length < 3) {
                        ircMsg(channel, "Syntax : !insertdrop mobid itemid chance");
                        return;
                    }
                    int mid = Integer.parseInt(splitted[1]);
                    int iid = Integer.parseInt(splitted[2]);
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    if (ii.getSlotMax(iid) < 1) {
                        ircMsg(channel, "Seems like you entered wrong itemid");
                        return;
                    }
                    int chance = Integer.parseInt(splitted[3]);
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("INSERT INTO `monsterdrops` (`monsterid`,`itemid`,`chance`) VALUES (?, ?, ?);");
                    ps.setInt(1, mid);
                    ps.setInt(2, iid);
                    ps.setInt(3, chance);
                    try {
                        ps.execute();
                        ircMsg(channel, " Success ");
                    } catch (SQLException ex) {
                        ircMsg(channel, " Failed ");
                    }
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(IRCOpCommands.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (splitted[0].equalsIgnoreCase("removedrop")) {
                int mid = Integer.parseInt(splitted[1]);
                int iid = Integer.parseInt(splitted[2]);
                try {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("DELETE FROM `monsterdrops` where `monsterid` = ? AND `itemid` = ?");
                    ps.setInt(1, mid);
                    ps.setInt(2, iid);
                    if (ps.execute()) {
                        ircMsg(channel, " Success ");
                    } else {
                        ircMsg(channel, " Failed ");
                    }
                    ps.close();
                } catch (SQLException sQLException) {
                }
            } else if (splitted[0].equalsIgnoreCase("relaoddrops")) {
                MapleMonsterInformationProvider.getInstance().clearDrops();
                ircMsg(channel, "Reloaded drops");
            } else if (splitted[0].equalsIgnoreCase("-1")) {
                if (splitted.length != 2) {
                    ircMsg(channel, "Wrong syntax. Too bad so sad");
                    return;
                }
                List<String> ret = UnbanProcessor.unban(splitted[1]);
                for (String lol : ret) {
                    ircMsg(channel, lol);
                }
            }
        }
    }
}
