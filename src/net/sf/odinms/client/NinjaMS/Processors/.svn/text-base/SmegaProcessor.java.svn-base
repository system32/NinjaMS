/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.Processors;

import java.rmi.RemoteException;
import java.util.List;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.tools.MaplePacketCreator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.constants.Items;

/**
 *
 * @author Admin
 */
public class SmegaProcessor {

    public static void smegaProcessor(Items.MegaPhoneType type, MapleClient c, String msg, IItem item, boolean ears) {
        String message = msg.toLowerCase();
        if (msg.length() > 100) {
            c.showMessage("toobad so sad. not over 100 characters. :P");
            return;
        }
        if (isIllegalWords(c.getPlayer(), message)) {
            msg = "<NoobFuckShitProstitute>" + c.getPlayer().getName() + " : I'm a Cock Sucker. Ban me GM bahahaha. I tried to advertise";
        } else {
            String tag = "";
            String legend = "";
            msg = c.getPlayer().getName() + " : " + msg;
            if (c.getPlayer().getPrefixShit() >= 2) {
                legend = "[" + c.getPlayer().getLegend() + "]";
                msg = legend + msg;
            }
            if (c.getPlayer().getPrefixShit() == 1 || c.getPlayer().getPrefixShit() == 2) {
                tag = "<" + c.getPlayer().getGMStatus().getTitle() + ">";
                msg = tag + msg;
            }
        }
        broadcastSmega(MaplePacketCreator.getMegaphone(type, c.getChannel(), msg, item, ears));
        MainIRC.getInstance().sendIrcMessage("#smega ~ " + msg);
        if (message.contains(".faillist") || message.contains(".gay?") || message.contains(".hit") || message.contains("fuck") || message.contains("sex")) {
            smegaReplies(c, message, type);
        }
    }

    public static void asmegaProcessor(MapleClient c, int itemId, List<String> lines) {
        String msg = "";
        for (String line : lines) {
            msg += " " + line.toLowerCase();
        }
        if (isIllegalWords(c.getPlayer(), msg)) {
            // already banned so do nothing
        } else {
            MaplePacket pkt = MaplePacketCreator.getAvatarMega(c.getPlayer(), c.getChannel(), itemId, lines);
            MainIRC.getInstance().sendIrcMessage("#Asmega ~ " +c.getPlayer().getName()+ " : " + msg);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    if (!chr.inCS() && !chr.isAsmega() && !chr.isfake) {
                        chr.getClient().getSession().write(pkt);
                    }
                }
            }
        }
    }

    public static void tripleMegaProcessor(MapleClient c, String[] message, byte numlines) {
        String tag = "";
        String legend = "";
        if (c.getPlayer().getPrefixShit() >= 2) {
            legend = c.getPlayer().getLegend();
            for (int i = 0; i < numlines; i++) {
                message[i] = "[" + legend + "]" + message[i];
            }
        }
        if (c.getPlayer().getPrefixShit() == 1 || c.getPlayer().getPrefixShit() == 2) {
            tag = c.getPlayer().getGMStatus().getTitle();
            for (int i = 0; i < numlines; i++) {
                message[i] = "<" + tag + ">" + message[i];
            }
        }
        for (int i = 0; i < numlines; i++) {
            String msg = message[i].toLowerCase();
            if (isIllegalWords(c.getPlayer(), msg)) {
                message[i] = "<NoobFuckShitProstitute> [I Suck Dick]" + c.getPlayer().getName() + " : I'm a Cock Sucker. Ban me GM bahahaha. I tried to advertise";
            }
        }
        MaplePacket pkt = MaplePacketCreator.getTripleMegaphone(c.getChannel(), message, numlines, true);
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (!chr.inCS() && !chr.isPsmega() && !chr.isfake) {
                    chr.getClient().getSession().write(pkt);
                }
            }
        }
    }

    public static void smegaReplies(MapleClient c, String msg, Items.MegaPhoneType type) {
        String[] splitted = msg.split(" ");
        String packet = "You Suck Sango's";
        String failures = "NoobMS, DrakoMS, ToyStory, Sango, lance";
        if (splitted[2].equalsIgnoreCase(".faillist")) {
            packet = "These are the Worst Failures I know : " + failures;
        } else if (msg.contains("fuck")) {
            packet = " Tell that Eff word to your mom. Kids play here. Watch your mouth bitch";
        } else if (msg.contains("sex")) {
            packet = " You so ugly no one want to smex you la. They would rather be with a bot like me";
        } else if (splitted[2].equalsIgnoreCase(".gay?")) {
            boolean fuckhim = false;
            String[] names = {"sundar", "hokage", "sundaranathan", "sampath", "system", "sunny", "sungy", "ninjams", "huong", "kelly", "kitkat", "angy"};
            for (int i = 0; i < names.length; i++) {
                if (splitted[3].equalsIgnoreCase(names[i])) {
                    fuckhim = true;
                }
            }
            boolean nexon = false;
            String[] nxnames = {"toystory", "nexon", "wei", "thiefofroses", "wizet", "gms", "globalms", "beerbaron", "mike", "msea", "lance", "noobms", "oliver"};
            for (int i = 0; i < nxnames.length; i++) {
                if (splitted[3].equalsIgnoreCase(nxnames[i])) {
                    nexon = true;
                }
            }
            if (fuckhim || splitted.length != 4) {
                packet = "you are 100% gay and 100% retarded";
            } else if (nexon) {
                packet = splitted[3] + "is 100% gay";
            } else {
                int gayness = (int) (Math.random() * 100);
                packet = splitted[3] + " is " + gayness + "% Gay";
            }
        } else if (splitted[2].equalsIgnoreCase(".hit")) {
            String[] slapwith = {"a big fish", "a sake bottle", "a torn slipper", "my e-penis", "Oliver's tiny thingo ", "a bag filled with shit", "with a used condom", "with a packet full of pee"};
            int in = (int) (Math.random() * slapwith.length);
            boolean fuckhim = false;
            String[] names = {"sundar", "sundaranathan", "sampath", "system", "sunny", "sungy", "ninjams", "janet", "cupoftea", "cupofpee", "angy"};
            for (int i = 0; i < names.length; i++) {
                if (splitted[3].equalsIgnoreCase(names[i])) {
                    fuckhim = true;
                }
            }
            if (fuckhim) {
                packet = "I just gave " + c.getPlayer().getName() + " a surprise buttsecks and he/she/it liked it";
            } else {
                packet = "I just hit " + splitted[3] + " with " + slapwith[in];
            }
        }
        broadcastSmega(MaplePacketCreator.getMegaphone(type, c.getChannel(), "SexBot : " + packet, null, true));
    }

    public static boolean isIllegalWords(MapleCharacter player, String text) {
        String origitext = text;
        text = text.replaceAll(" ", "").toLowerCase();
        // server sucks
        String[] bannedcomments = {"isserversu", "ninjamssu", "ninjastorysu", "isserverisgay", "fuckthisserv", "ispssu"};
        for (int i = 0; i < bannedcomments.length; i++) {
            if (text.contains(bannedcomments[i])) {
                if (player.getReborns() < 10) {
                    AutobanManager.getInstance().autoban(player.getClient(), "Trying to comment on the server sucking (Text: " + origitext + ")");
                }
                return true;
            }
        }
        // advertisements of maplestory servers...
        String[] advertisingarray = {".com", ".net", ".info", ".org", ".tk", ".weebly", ".freewebs", ".co.cc"};
        String[] forgivencontents = {"story.org", "sydneyms", "ninjams", "wagga", ".org/vote", "google", "tinypic", "mapletip"};
        for (int i = 0; i < advertisingarray.length; i++) {
            String[] subarray = {"story", "ms"};
            for (int p = 0; p < subarray.length; p++) {
                if (text.toLowerCase().contains(subarray[p] + advertisingarray[i])) {
                    boolean banhammer = true;
                    for (int z = 0; z < forgivencontents.length; z++) {
                        if (text.contains(forgivencontents[z])) {
                            banhammer = false;
                            return false;
                        }
                    }
                    if (banhammer) {
                        if (player.getReborns() < 10) {
                            AutobanManager.getInstance().autoban(player.getClient(), "Advertisement of other server (Text: " + origitext + ")");
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static void broadcastSmega(MaplePacket pkt) {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (!chr.inCS() && !chr.isPsmega() && !chr.isfake) {
                    chr.getClient().getSession().write(pkt);
                }
            }
        }
    }

    public static void broadcastIRCSmega(String sender, String msg) {
        MaplePacket pkt = MaplePacketCreator.getMegaphone(Items.MegaPhoneType.SUPERMEGAPHONE, 69, sender + " : " + msg, null, false);
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                if (!chr.inCS() && chr.isIrcmsg() && !chr.isfake) {
                    chr.getClient().getSession().write(pkt);
                }
            }
        }
    }
}
