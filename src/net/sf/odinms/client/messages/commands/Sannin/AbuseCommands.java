/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.Sannin;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.SmegaProcessor;
import net.sf.odinms.client.messages.SanninCommand;
import net.sf.odinms.client.messages.SanninCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.WorldServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.constants.Items.MegaPhoneType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class AbuseCommands implements SanninCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("smegamc")) {
            if (splitted.length != 3) {
                mc.dropMessage("Hey nub read %commands. Syntax: %smegamc ign text");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    SmegaProcessor.smegaProcessor(MegaPhoneType.SUPERMEGAPHONE, victim.getClient(), StringUtil.joinStringFrom(splitted, 2), null, true);
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        } else if (splitted[0].equalsIgnoreCase("ismegamc")) {
            if (splitted.length != 3) {
                mc.dropMessage("Hey nub read %commands. Syntax: %ismegamc ign ");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    SmegaProcessor.smegaProcessor(MegaPhoneType.ITEMMEGAPHONE, victim.getClient(), StringUtil.joinStringFrom(splitted, 2), null, true);
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        } else if (splitted[0].equalsIgnoreCase("omghax")) {
            short stat = 1;
            short wa = 1;
            int itemid = 0;
            if (splitted.length == 4) {
                itemid = Integer.parseInt(splitted[1]);
                stat = Short.parseShort(splitted[2]);
                wa = Short.parseShort(splitted[3]);
            } else if (splitted.length == 3) {
                itemid = Integer.parseInt(splitted[1]);
                stat = Short.parseShort(splitted[2]);
            } else if (splitted.length == 2) {
                itemid = Integer.parseInt(splitted[1]);
                stat = 32767;
            } else {
                mc.dropMessage("Read %commands. Syntax : %omghax <itemid> <stats> <wa>");
                return;
            }
            MapleInventoryManipulator.addStatItemById(c, itemid, player.getName(), stat, wa, wa);
        } else if (splitted[0].equalsIgnoreCase("alias")) {
            if (splitted.length != 3) {
                mc.dropMessage("Hey nub read %commands. Syntax: %alias ign newname");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    String newname = StringUtil.joinStringFrom(splitted, 2);
                    if (newname.length() > 14) {
                        mc.dropMessage("[Anbu] Name is too long to hold.");
                        return;
                    }
                    c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Anbu] " + victim.getName() + " - The retard is now known as " + newname + "!"));
                    victim.setName(newname);
                    victim.getClient().getSession().write(MaplePacketCreator.getCharInfo(victim));
                    victim.getMap().removePlayer(victim);
                    victim.getMap().addPlayer(victim);
                    victim.saveToDB();
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        } else if (splitted[0].equalsIgnoreCase("mapmc")) {
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                if (mch != null && !mch.isHokage() && mch != c.getPlayer() && !mch.isfake) {
                    String text = StringUtil.joinStringFrom(splitted, 1);
                    mch.getMap().broadcastMessage(MaplePacketCreator.getChatText(mch.getId(), text, mch.isJounin(), 1));
                }
            }
        } else if (splitted[0].equalsIgnoreCase("kill")) {
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    victim.kill();
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        } else if (splitted[0].equalsIgnoreCase("haxrb")) {
            if (splitted.length < 2) {
                mc.dropMessage("What a nub. Read !commands lar idiot. Syntax : !haxrb <amount>");
            }
            short fuck;
            try {
                fuck = Short.parseShort(splitted[1]);
            } catch (NumberFormatException nigger) {
                mc.dropMessage("only 32767 or below. Now eff off");
                return;
            }
            player.setReborns(fuck);
            mc.dropMessage("Your rebirths now is : " + fuck);
        } else if (splitted[0].equalsIgnoreCase("smega")) {
            String fuck = StringUtil.joinStringFrom(splitted, 1);
            SmegaProcessor.smegaProcessor(MegaPhoneType.ITEMMEGAPHONE, c, fuck, null, true);
        } else if (splitted[0].equalsIgnoreCase("torture")) {
            if (splitted.length < 3) {
                mc.dropMessage("Fail Jounin is Fail. Read !commands la nub. It is : !commands <ign> <reason>");
                return;
            }
            WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
            if (loc != null) {
                MapleCharacter noob = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                if (noob != null) {
                    if (c.getPlayer().getGMLevel() >= noob.getGMLevel() || noob.isHokage()) {
                        noob.torture(StringUtil.joinStringFrom(splitted, 2));
                    } else {
                        c.getPlayer().torture("trying to torture " + noob.getName());
                    }
                } else {
                    mc.dropMessage(splitted[1] + " does not exist.");
                }
            } else {
                mc.dropMessage(splitted[1] + " does not exist.");
            }
        } else if (splitted[0].equalsIgnoreCase("addtao")) {
            if (splitted.length != 3) {
                mc.dropMessage("Correct Syntax : %addtao noob'sIGN amount");
            } else {
                try {
                    WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                    if (loc != null) {
                        MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                        int amount;
                        try {
                            amount = Integer.parseInt(splitted[2]);
                        } catch (NumberFormatException numberFormatException) {
                            mc.dropMessage("Drunk asshole. read the syntax. %addtao ign number");
                            return;
                        }
                        noob.gainItem(4032016, amount);
                    } else {
                        mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                    }
                } catch (Exception e) {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
                }
            }
        } else if (splitted[0].equalsIgnoreCase("mc")) {
            if (splitted.length < 3) {
                mc.dropMessage("Read %commands la nub. Syntax : %mc <ign> <text>");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter noobs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                    if (player.getGMLevel() < noobs.getGMLevel()) {
                        player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), "Hey everybody. Fuck me. You all fucking suck. BAN ME GM. BAHAHAHA.", player.isJounin(), 0));
                    } else {
                        String text = StringUtil.joinStringFrom(splitted, 2);
                        noobs.getMap().broadcastMessage(MaplePacketCreator.getChatText(noobs.getId(), text, noobs.isJounin(), 0));
                    }
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        } else if (splitted[0].equalsIgnoreCase("gmwannabe")) {
            if (splitted.length < 3) {
                mc.dropMessage("Read %commands la nub. Syntax : %gmwannabe <ign> <text>");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter noobs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                    if (player.getGMLevel() < noobs.getGMLevel()) {
                        player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), "Hey everybody. Fuck me. You all fucking suck. BAN ME GM. BAHAHAHA.", player.isJounin(), 0));
                    } else {
                        String text = StringUtil.joinStringFrom(splitted, 2);
                        noobs.getMap().broadcastMessage(MaplePacketCreator.getChatText(noobs.getId(), text, true, 0));
                    }
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        } else if (splitted[0].equalsIgnoreCase("world")){
            WorldServer.getInstance().sendIRCMsg("testing la");
        } 
    }

    public SanninCommandDefinition[] getDefinition() {
        return new SanninCommandDefinition[]{
                    new SanninCommandDefinition("omghax", "itemid stat wa", "makes stat items :p"),
                    new SanninCommandDefinition("alias", "ign newname", ""),
                    new SanninCommandDefinition("smegamc", "name message", "smega mind control a player"),
                    new SanninCommandDefinition("ismegamc", "name message", "ismega mind control a player"),
                    new SanninCommandDefinition("mapmc", "text", "map mind control"),
                    new SanninCommandDefinition("kill", "ign", "kills the person la"),
                    new SanninCommandDefinition("haxrb", "amount", "set your RB"),
                    new SanninCommandDefinition("smega", "msg", "send smega with your name . "),
                    new SanninCommandDefinition("torture", "<ign> <reason>", "world tour command. Dont abuse this bishes"),
                    new SanninCommandDefinition("addgold", "ign amount", "adds amount of gold. negative value possible. Dont abuse"),
                    new SanninCommandDefinition("mc", "ign text", "mind control"),
                    new SanninCommandDefinition("world", "", ""),
                    new SanninCommandDefinition("gmwannabe", "ign text", "white chat mind control"),};


    }
}
