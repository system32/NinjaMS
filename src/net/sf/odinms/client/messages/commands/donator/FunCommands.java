/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.donator;

import net.sf.odinms.client.Clones;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.SmegaProcessor;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.DonatorCommand;
import net.sf.odinms.client.messages.DonatorCommandDefinition;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.constants.Items.MegaPhoneType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class FunCommands implements DonatorCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("smega") || splitted[0].equalsIgnoreCase("ismega")) {
            String fuck = StringUtil.joinStringFrom(splitted, 1);
            if (fuck.length() < 3 && !player.isJounin()) {
                mc.dropMessage("Message needs to be more than 3 characters in length");
                return;
            }
            if (fuck.length() > 100 && !player.isJounin()) {
                fuck = fuck.substring(0, 100);
            }
            if (splitted[0].equalsIgnoreCase("ismega")) {
                SmegaProcessor.smegaProcessor(MegaPhoneType.ITEMMEGAPHONE, c, fuck, null, true);
            } else {
                SmegaProcessor.smegaProcessor(MegaPhoneType.SUPERMEGAPHONE, c, fuck, null, true);
            }
        } else if (splitted[0].equalsIgnoreCase("emo")) {
            player.kill();
        } else if (splitted[0].equalsIgnoreCase("leet")) {
            if (splitted[1].equalsIgnoreCase("on")) {
                player.setLeetness(true);
                mc.dropMessage("[System] Successfully leeted self.");
            } else {
                player.setLeetness(false);
                mc.dropMessage("[System] Successfully unleeted self.");
            }
        } else if (splitted[0].equalsIgnoreCase("kagebunshin")) {
            if (player.getMapId() != 910000000 && player.getMapId() != 100000000) {
                mc.dropMessage("due to some bugs. Kage bunshin is available only in FM entrance or Henesys");
                return;
            }
            if (!player.canHasClone() && !player.isGenin()) {
                mc.dropMessage("hehe you cannot do this");
                return;
            }

            int numbers = 1;
            if (player.hasClones()) {
                player.removeClones();
            }
            if (splitted.length == 2) {
                numbers = Integer.parseInt(splitted[1]);
            }
            if (numbers > player.getAllowedClones()) {
                numbers = player.getAllowedClones();
            }
            if (player.haveItem(4006001, numbers * 10, false, true)) {
                for (int i = 0; i < numbers; i++) {
                    Clones clone = new Clones(c.getPlayer(), ((c.getPlayer().getId() * 100) + c.getPlayer().getClones().size() + 1));
                    c.getPlayer().addClone(clone);
                }
                mc.dropMessage("Please move around for it to take effect. Clone Limit for You is : " + player.getAllowedClones());
            } else {
                mc.dropMessage("You do not have enough summon rocks. You need number of clones * 10 summon rocks");
            }
        } else if (splitted[0].equalsIgnoreCase("cancelkagebunshin")) {
            if (player.hasClones()) {
                player.removeClones();
            } else {
                mc.dropMessage("You do not have any clones");
            }
        } else if (splitted[0].equalsIgnoreCase("retardcure")) {
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    if (victim.getReborns() < 10) {
                        victim.dropMessage(1, c.getPlayer().getName() + " thinks you are a retard for not knowing to use @commands. Henesys is our home town which you can reach by command @home. Everything you need can be found there.");
                        mc.dropMessage("If you abuse This Command You will be punished. May be banned");
                    } else {
                        mc.dropMessage("That ninja has more than 10 RB. So you cannot Annoy him.");
                    }
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
            }
        } else if (splitted[0].equalsIgnoreCase("namechange")) {
            if (splitted.length != 2) {
                mc.dropMessage("Learn to read @commands, retard! Syntax : @namechnage <new ign>");
                return;
            }
            if(player.isChunin()){
                mc.dropMessage("You cannot change name. nub");
                return;
            }
            String newname = splitted[1];
            if (newname.length() > 14) {
                mc.dropMessage("[Anbu] Name is too long to hold.");
                return;
            }
            if(GameConstants.isBlockedName(newname)){
                mc.dropMessage("[Anbu] Name is not permitted.");
                return;
            }
            if (player.getTaoOfSight() >= 500) {
                player.gainItem(4032016, -500);
                player.removeClones();
                player.cancelAllBuffs();
                c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Anbu] " + player.getName() + " - The retard is now known as " + newname + "!"));
                player.setName(newname);
                player.getClient().getSession().write(MaplePacketCreator.getCharInfo(player));
                player.getMap().removePlayer(player);
                player.getMap().addPlayer(player);
                player.saveToDB();
            } else {
                mc.dropMessage("You need atleast 500 Tao Of Sight to change name");
            }
        } else if (splitted[0].equalsIgnoreCase("rebuff")){
            player.rebuff();
        }
    }

    public DonatorCommandDefinition[] getDefinition() {
        return new DonatorCommandDefinition[]{
                    new DonatorCommandDefinition("smega", "message", "smega for you 10k mesos"),
                    new DonatorCommandDefinition("ismega", "message", "yellow Smega for 20k mesos"),
                    new DonatorCommandDefinition("emo", "", "try it for the lulz"),
                    new DonatorCommandDefinition("leet", "on/off", "leet talk"),
                    new DonatorCommandDefinition("kagebunshin", "number", "Shadow Clone Jutsu specially from Naruto"),
                    new DonatorCommandDefinition("cancelkagebunshin", "", "removes all clones"),
                    new DonatorCommandDefinition("retardcure", "ign", " sures Retardness. hopefully"),
                    new DonatorCommandDefinition("rebuff", "", " Rebuffs you"),
                   // new DonatorCommandDefinition("legend", "on/off", " turns on and off legend"),
                    new DonatorCommandDefinition("namechange", "newign", "Changes your nick name for a fee"),};
    }
}
