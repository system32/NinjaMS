/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import java.util.List;
import net.sf.odinms.client.Clones;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.BashBot;
import net.sf.odinms.client.NinjaMS.IRCStuff.PlayerIRC;
import net.sf.odinms.client.NinjaMS.MLIABot;
import net.sf.odinms.client.NinjaMS.MapleFML;
import net.sf.odinms.client.NinjaMS.Processors.SmegaProcessor;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.server.constants.Items.MegaPhoneType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class FunCommands implements PlayerCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("smega") || splitted[0].equalsIgnoreCase("ismega")) {
            int fee = 0;
            if (splitted[0].equalsIgnoreCase("ismega")) {
                fee = 3;
            } else {
                fee = 5;
            }
            if(player.isGenin()){
                fee -= 2;
            }
            if (!player.haveItem(Items.currencyType.Sight, fee) && !player.isGenin()) {
                mc.dropMessage("need "+fee+" Tao of sight to so this");
                return;
            }
            if (player.getCheatTracker().spam(10000, 3) && !player.isJounin()) {
                mc.dropMessage("Not so often babe");
                return;
            }
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
            if(!player.isJounin()) {
                player.gainItem(4032016, -fee);
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
        } else if (splitted[0].equalsIgnoreCase("namechange")) {
            if (splitted.length != 2) {
                mc.dropMessage("Learn to read @commands, retard! Syntax : @namechnage <new ign>");
                return;
            }
            if (player.isChunin()) {
                mc.dropMessage("You cannot change name. nub");
                return;
            }
            String newname = splitted[1];
            if (newname.length() > 14) {
                mc.dropMessage("[Anbu] Name is too long to hold.");
                return;
            }
            if (GameConstants.isBlockedName(newname)) {
                mc.dropMessage("[Anbu] Name is not permitted.");
                return;
            }
            if (player.haveItem(Items.currencyType.Sight, 1337)) {
                player.gainItem(4032016, -1337);
                player.removeClones();
                player.cancelAllBuffs();
                c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Anbu] " + player.getName() + " - The retard is now known as " + newname + "!"));                
                player.addPreviousNames(player.getName());
                player.setName(newname);
                player.getClient().getSession().write(MaplePacketCreator.getCharInfo(player));
                player.getMap().removePlayer(player);
                player.getMap().addPlayer(player);
                player.saveToDB();
            } else {
                mc.dropMessage("You need 1337 Tao Of Sight to Change your name");
            }
        } else if (splitted[0].equalsIgnoreCase("irc")) {
            if (splitted[1].equalsIgnoreCase("on")) {
                player.setIrcmsg(true);
                PlayerIRC.getInstance(player);
                mc.dropMessage("You will not See IRC messages now");
            } else if (splitted[1].equalsIgnoreCase("off")) {
                PlayerIRC.cancelInstance(player);
                mc.dropMessage("You will see IRC messages now");
            }
        } else if (splitted[0].equalsIgnoreCase("nudity")) {
            mc.dropMessage("Striping...");
            player.cancelAllBuffs();
            player.unequipEverything();
        } else if (splitted[0].equalsIgnoreCase("bash")) {
            List<String> bash = BashBot.getQuotes();
            for(String msg : bash){
                mc.dropMessage(msg.toString());
            }
        } else if (splitted[0].equalsIgnoreCase("fml")) {
            mc.dropMessage(MapleFML.getFML());
        } else if (splitted[0].equalsIgnoreCase("mlia")) {
            mc.dropMessage(MLIABot.findMLIA());
        }
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("smega", "message", "smega for you 10k mesos"),
                    new PlayerCommandDefinition("ismega", "message", "yellow Smega for 20k mesos"),
                    new PlayerCommandDefinition("emo", "", "try it for the lulz"),
                    new PlayerCommandDefinition("leet", "on/off", "leet talk"),
                    new PlayerCommandDefinition("kagebunshin", "number", "Shadow Clone Jutsu specially from Naruto"),
                    new PlayerCommandDefinition("cancelkagebunshin", "", "removes all clones"),
                    new PlayerCommandDefinition("namechange", "newign", "Changes your nick name for a fee"),
                    new PlayerCommandDefinition("irc", "on/off", "turns on and off IRC"),
                    new PlayerCommandDefinition("nudity", "", "strips you nude"),
                    new PlayerCommandDefinition("bash", "", "Shows a random bash.org message"),
                    new PlayerCommandDefinition("fml", "", "shows a random fml message"),
                    new PlayerCommandDefinition("mlia", "", "shows a random MLIA message"),
        };


    }
}
