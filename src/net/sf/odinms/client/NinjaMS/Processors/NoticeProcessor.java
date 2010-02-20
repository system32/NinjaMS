/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.Processors;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;
import org.jibble.pircbot.Colors;

/**
 *
 * @author Owner
 */
public class NoticeProcessor {

    public static void sendSay(MapleCharacter player, String msg) {
        MaplePacket packet;
        switch (player.getGMLevel()) {
            case 0:
                packet = MaplePacketCreator.serverNotice(5, "[Rookie ~ " + player.getName() + "] " + msg);
                break;
            case 1:
                packet = MaplePacketCreator.serverNotice(5, "[Genin ~ " + player.getName() + "] " + msg);
                break;
            case 2:
                packet = MaplePacketCreator.serverNotice(5, "[Chunin ~ " + player.getName() + "] " + msg);
                break;
            case 3:
                packet = MaplePacketCreator.serverNotice(6, "[Jounin ~ " + player.getName() + "] " + msg);
                break;
            case 4:
                packet = MaplePacketCreator.serverNotice(6, "[Sannin ~ " + player.getName() + "] " + msg);
                break;
            default:
                packet = MaplePacketCreator.sendYellowTip("[Hokage] " + msg);
        }
        try {
            player.getClient().getChannelServer().getWorldInterface().broadcastMessage(player.getName(), packet.getBytes());
            MainIRC.getInstance().sendIrcMessage(Colors.BOLD + "#SAY : " + player.getName() + " - "+ Colors.RED + msg);
        } catch (RemoteException ex) {
            Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendMe(MapleCharacter player, String msg) {
        MaplePacket packet;
        switch (player.getGMLevel()) {
            case 0:
                packet = MaplePacketCreator.serverNotice(6, "[Rookie ~ " + player.getName() + "] " + msg);
                break;
            case 1:
                packet = MaplePacketCreator.serverNotice(6, "[Genin ~ " + player.getName() + "] " + msg);
                break;
            case 2:
                packet = MaplePacketCreator.serverNotice(6, "[Chunin ~ " + player.getName() + "] " + msg);
                break;
            case 3:
                packet = MaplePacketCreator.serverNotice(6, "[Jounin ~ " + player.getName() + "] " + msg);
                break;
            case 4:
                packet = MaplePacketCreator.serverNotice(6, "[Sannin ~ " + player.getName() + "] " + msg);
                break;
            default:
                packet = MaplePacketCreator.serverNotice(6, "[Hokage] " + msg);
        }
        try {
            player.getClient().getChannelServer().getWorldInterface().broadcastMessage(player.getName(), packet.getBytes());
            MainIRC.getInstance().sendIrcMessage(Colors.BOLD + "#ME : " + player.getName() + " - " + Colors.RED + msg);
        } catch (RemoteException ex) {
            Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sayMindControl(MapleCharacter player, String[] splitted) {
        MapleCharacter other;
        try {
            WorldLocation loc = player.getClient().getChannelServer().getWorldInterface().getLocation(splitted[1]);
            if (loc != null) {
                other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
            } else {
                player.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                return;
            }
        } catch (Exception e) {
            player.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
            return;
        }
        if (other.getGMLevel() > player.getGMLevel()) {
            sendMe(player, "I Suck Dick for living");
        } else {
            sendSay(other, StringUtil.joinStringFrom(splitted, 2));
        }
    }

    public static void meMindControl(MapleCharacter player, String[] splitted) {
        MapleCharacter other;
        try {
            WorldLocation loc = player.getClient().getChannelServer().getWorldInterface().getLocation(splitted[1]);
            if (loc != null) {
                other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
            } else {
                player.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                return;
            }
        } catch (Exception e) {
            player.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
            return;
        }
        if (other.getGMLevel() > player.getGMLevel()) {
            sendSay(player, "I Suck Dick for living");
        } else {
            sendMe(other, StringUtil.joinStringFrom(splitted, 2));
        }
    }

    public static void sendBlueNoticeWithNotice(String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(6, "[Notice]" + msg);
        try {
            ChannelServer.getInstance(1).getWorldInterface().broadcastMessage(null, packet.getBytes());
            MainIRC.getInstance().sendIrcMessage(Colors.BOLD + "#Notice : " + Colors.RED + msg);
        } catch (RemoteException ex) {
            Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendBlueNotice(String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(6, msg);
        try {
            ChannelServer.getInstance(1).getWorldInterface().broadcastMessage(null, packet.getBytes());
            MainIRC.getInstance().sendIrcMessage(Colors.BOLD + "#blue : " + Colors.RED + msg);
        } catch (RemoteException ex) {
            Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendPinkNotice(String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(5, msg);
        try {
            ChannelServer.getInstance(1).getWorldInterface().broadcastMessage(null, packet.getBytes());
            MainIRC.getInstance().sendIrcMessage(Colors.BOLD + "#pink : " + Colors.RED + msg);
        } catch (RemoteException ex) {
            Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendPopup(String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(1, msg);
        try {
            ChannelServer.getInstance(1).getWorldInterface().broadcastMessage(null, packet.getBytes());
            MainIRC.getInstance().sendIrcMessage(Colors.BOLD + "#popup : " + Colors.RED + msg);
        } catch (RemoteException ex) {
            Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendWhiteNotice(String name, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = "[notice] : " + ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(2, name + " : " + msg);
        try {
            ChannelServer.getInstance(1).getWorldInterface().broadcastMessage(null, packet.getBytes());
            MainIRC.getInstance().sendIrcMessage(Colors.BOLD + "#white : " + Colors.RED + msg);
        } catch (RemoteException ex) {
            Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendYellowNotice(String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.sendYellowTip(msg);
        try {
            ChannelServer.getInstance(1).getWorldInterface().broadcastMessage(null, packet.getBytes());
        } catch (RemoteException ex) {
            Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendCBlueNotice(MapleClient c, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(6, msg);
        c.getChannelServer().broadcastPacket(packet);

    }

    public static void sendCPinkNotice(MapleClient c, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(5, msg);
        c.getChannelServer().broadcastPacket(packet);
    }

    public static void sendCPopup(MapleClient c, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(1, msg);
        c.getChannelServer().broadcastPacket(packet);
    }

    public static void sendCWhiteNotice(MapleClient c, String name, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(2, name + " : " + msg);
        c.getChannelServer().broadcastPacket(packet);
    }

    public static void sendCYellowNotice(MapleClient c, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.sendYellowTip(msg);
        c.getChannelServer().broadcastPacket(packet);
    }

    public static void sendMBlueNotice(MapleClient c, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(6, msg);
        c.getPlayer().getMap().broadcastMessage(packet);
    }

    public static void sendMPinkNotice(MapleClient c, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(5, msg);
        c.getPlayer().getMap().broadcastMessage(packet);
    }

    public static void sendMPopup(MapleClient c, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(1, msg);
        c.getPlayer().getMap().broadcastMessage(packet);
    }

    public static void sendMWhiteNotice(MapleClient c, String name, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.serverNotice(2, name + " : " + msg);
        c.getPlayer().getMap().broadcastMessage(packet);
    }

    public static void sendMYellowNotice(MapleClient c, String msg) {
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MaplePacket packet = MaplePacketCreator.sendYellowTip(msg);
        c.getPlayer().getMap().broadcastMessage(packet);
    }

    public static void sendPBlueNotice(MapleCharacter player, String name, String msg) {
        MapleCharacter other;
        try {
            WorldLocation loc = player.getClient().getChannelServer().getWorldInterface().getLocation(name);
            if (loc != null) {
                other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(name);
            } else {
                player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
                return;
            }
        } catch (Exception e) {
            player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
            return;
        }
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        other.dropMessage(6, msg);
    }

    public static void sendPPinkNotice(MapleCharacter player, String name, String msg) {
        MapleCharacter other;
        try {
            WorldLocation loc = player.getClient().getChannelServer().getWorldInterface().getLocation(name);
            if (loc != null) {
                other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(name);
            } else {
                player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
                return;
            }
        } catch (Exception e) {
            player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
            return;
        }
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        other.dropMessage(5, msg);
    }

    public static void sendPPopup(MapleCharacter player, String name, String msg) {
        MapleCharacter other;
        try {
            WorldLocation loc = player.getClient().getChannelServer().getWorldInterface().getLocation(name);
            if (loc != null) {
                other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(name);
            } else {
                player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
                return;
            }
        } catch (Exception e) {
            player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
            return;
        }
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        other.dropMessage(1, msg);
    }

    public static void sendPWhiteNotice(MapleCharacter player, String name, String msg) {
        MapleCharacter other;
        try {
            WorldLocation loc = player.getClient().getChannelServer().getWorldInterface().getLocation(name);
            if (loc != null) {
                other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(name);
            } else {
                player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
                return;
            }
        } catch (Exception e) {
            player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
            return;
        }
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        other.dropMessage(2, player.getName() + " : " + msg);
    }

    public static void sendPYellowNotice(MapleCharacter player, String name, String msg) {
        MapleCharacter other;
        try {
            WorldLocation loc = player.getClient().getChannelServer().getWorldInterface().getLocation(name);
            if (loc != null) {
                other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(name);
            } else {
                player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
                return;
            }
        } catch (Exception e) {
            player.dropMessage("[Anbu] '" + name + "' does not exist, is CCing, or is offline.");
            return;
        }
        if (msg.equalsIgnoreCase("!array")) {
            try {
                msg = ChannelServer.getInstance(1).getWorldInterface().getArrayString();
            } catch (RemoteException ex) {
                Logger.getLogger(NoticeProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        other.getClient().getSession().write(MaplePacketCreator.sendYellowTip(msg));
    }
}
