/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.gm;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.SmegaProcessor;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.constants.Items.MegaPhoneType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class AbuseCommands implements GMCommand {

    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("kill", "ign", "kills the person la"),
                    new GMCommandDefinition("haxrb", "amount", "set your RB"),
                    new GMCommandDefinition("smega", "msg", "send smega with your name . "),
                    new GMCommandDefinition("torture", "<ign> <reason>", "world tour command. Dont abuse this bishes"),
                    new GMCommandDefinition("addtao", "ign amount", "adds amount of tao. negative value possible. Dont abuse"),
                    new GMCommandDefinition("mc", "ign text", "mind control"),
                    new GMCommandDefinition("gmwannabe", "ign text", "white chat mind control"),
        };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("kill")) {
            if (splitted.length != 2) {
                mc.dropMessage("read commands la nub");
            } else {
                MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (noob == null) {
                    mc.dropMessage("the noob is not online/ is in different channel / does not exist la");
                } else {
                    noob.kill();
                    mc.dropMessage("MURDERER LA EUU. EUU HAVE MURDERED DA JEWZ");
                }
            }
        } else if (splitted[0].equalsIgnoreCase("haxrb")){
            if (splitted.length < 2){
                mc.dropMessage("What a nub. Read !commands lar idiot. Syntax : !haxrb <amount>");
            }
            short fuck;
            try {
                fuck = Short.parseShort(splitted[1]);
            } catch (NumberFormatException nigger) {
                mc.dropMessage("only 32767 or below. Now eff off");
                return;
            }
            c.getPlayer().setReborns(fuck);
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
                    if (c.getPlayer().getGMLevel() >= noob.getGMLevel() || noob.isAdmin()) {
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
        } else if (splitted[0].equalsIgnoreCase("addtao")){
            if(splitted.length != 3){
                mc.dropMessage("Correct Syntax : !addtao IGN amount");
            } else {
                MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if(noob != null){
                    int amount;
                    try {
                        amount = Integer.parseInt(splitted[2]);
                    } catch (NumberFormatException numberFormatException) {
                        mc.dropMessage("Drunk asshole. read the syntax. !addtao ign number");
                        return;
                    }
                    noob.gainItem(4032016, amount);
                } else {
                    mc.dropMessage("player not in your channel");
                }
            }
        } else if (splitted[0].equalsIgnoreCase("mc")){
            MapleCharacter noobs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (player.getGMLevel() < noobs.getGMLevel()) {
                player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), "Hey everybody. Fuck me. You all fucking suck. BAN ME GM. BAHAHAHA.", player.isJounin(), 1));
            } else {
                String text = StringUtil.joinStringFrom(splitted, 2);
                noobs.getMap().broadcastMessage(MaplePacketCreator.getChatText(noobs.getId(), text, noobs.isJounin(), 0));
            }
        } else if (splitted[0].equalsIgnoreCase("gmwannabe")) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (player.getGMLevel() > victim.getGMLevel()) {
                String text = StringUtil.joinStringFrom(splitted, 2);
                player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), text, true, 0));
            } else {
                player.getMap().broadcastMessage(MaplePacketCreator.getChatText(player.getId(), "Hey everybody. Fuck me. You all fucking suck. BAN ME GM. BAHAHAHA.", true, 0));
            }
        }
    }
}
