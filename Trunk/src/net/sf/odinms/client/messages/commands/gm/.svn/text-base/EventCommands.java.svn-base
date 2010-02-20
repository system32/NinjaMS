/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.gm;

import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Admin
 */
public class EventCommands implements GMCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {

        if (splitted[0].equalsIgnoreCase("warpallhere")) {
            int players = 0;
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch != null && !mch.isJounin()) {
                    mch.dropMessage(5, "You are being warped to " + c.getPlayer().getName());
                    mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                    players++;
                }
            }
            mc.dropMessage("[Anbu] A total of " + players + " people have been warped to you");
        } else if (splitted[0].equalsIgnoreCase("killmap")) {
            int players = 0;
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                if (mch != null && !mch.isChunin()) {
                    mch.dropMessage(5, "You have been killed by" + c.getPlayer().getName());
                    mch.kill();
                    players++;
                }
            }
            mc.dropMessage("[Anbu] A total of " + players + " people have been assasinated by you");
        } else if (splitted[0].equalsIgnoreCase("healmap")) {
            int players = 0;
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                if (mch != null) {
                    mch.dropMessage(5, "You have been healed by" + c.getPlayer().getName());
                    mch.heal();
                    players++;
                }
            }
            mc.dropMessage("[Anbu] A total of " + players + " people have been healed by you");
        } else if (splitted[0].equalsIgnoreCase("killleft")) {
            int xpos = c.getPlayer().getPosition().x;
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                if (mch != null && mch.getPosition().x < xpos) {
                    mch.kill();
                }
            }
        } else if (splitted[0].equalsIgnoreCase("killright")) {
            int xpos = c.getPlayer().getPosition().x;
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                if (mch != null && mch.getPosition().x > xpos) {
                    mch.kill();
                }
            }
        } else if (splitted[0].equalsIgnoreCase("healleft")) {
            int xpos = c.getPlayer().getPosition().x;
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                if (mch != null && mch.getPosition().x < xpos) {
                    mch.heal();
                }
            }
        } else if (splitted[0].equalsIgnoreCase("healright")) {
            int xpos = c.getPlayer().getPosition().x;
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                if (mch != null && mch.getPosition().x < xpos) {
                    mch.heal();
                }
            }
        } else if (splitted[0].equalsIgnoreCase("tf")) {
            mc.dropMessage("[System] You are a retard for warping to True or False Map.");
            int map = 109020001;
            MapleMap target = c.getChannelServer().getMapFactory().getMap(map);
            c.getPlayer().changeMap(target, target.getPortal(17));
        } else if (splitted[0].equalsIgnoreCase("tensu")) {
            if (splitted.length < 3) {
                mc.dropMessage("fail GM is Fail. Syntax : !tensu <ign>");
            }
            MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (noob != null) {
                noob.addNinjaTensu();
            } else {
                mc.dropMessage(" Player not in your channel . Fail!! ");
            }
            mc.dropMessage("Abusing this command Will get you banned");
        } else if (splitted[0].equalsIgnoreCase("youlose")) {
            c.showMessage(5, "Please spam this command until the success message pops up.");
            for (MapleCharacter mch : c.getPlayer().getMap().getCharacters()) {
                if (!mch.isAlive() && !mch.isChunin() && mch != null) {
                    mch.unMute();
                    c.showMessage(5, "[Event]You are being warped out and unmuted.");
                    mch.changeMap(100000000, 0);
                    mch.heal();
                }
            }
            mc.dropMessage("[Mute] Success!");
        } else if (splitted[0].equalsIgnoreCase("true")) {
            for (MapleCharacter noob : c.getPlayer().getMap().getCharacters()) {
                if (!noob.isJounin() && (noob.getPosition().getX() < -308) || noob.getPosition().getY() < 300) {
                    noob.unMute();
                    noob.changeMap(100000000, 0);
                }
            }
        } else if (splitted[0].equalsIgnoreCase("false")) {
            for (MapleCharacter noob : c.getPlayer().getMap().getCharacters()) {
                if (!noob.isJounin() && (noob.getPosition().getX() > -308) || noob.getPosition().getY() < 300) {
                    noob.unMute();
                    noob.changeMap(100000000, 0);
                }
            }
        } else if (splitted[0].equalsIgnoreCase("prolevel")) {
            MapleCharacter noob = c.getPlayer();
            if (splitted.length < 2) {
                while (noob.getLevel() < 200) {
                    noob.levelUp();
                }
            }
            noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (noob != null) {
                while (noob.getLevel() < 200) {
                    noob.levelUp();
                }
                mc.dropMessage("Why would you do that??????");
            } else {
                mc.dropMessage("The Player is not in your channel or does not exist.");
            }
        } else if (splitted[0].equals("clock")) {
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(getOptionalIntArg(splitted, 1, 60)));
        } else if (splitted[0].equalsIgnoreCase("mutemap")){
            for (MapleCharacter noob : c.getPlayer().getMap().getCharacters()) {
                if (!noob.isChunin()) {
                    noob.mute(1);
                }
            }
        } else if (splitted[0].equalsIgnoreCase("unmutemap")){
            for (MapleCharacter noob : c.getPlayer().getMap().getCharacters()) {
                if (!noob.isChunin()) {
                    noob.unMute();
                }
            }
        }
    }

    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("warpallhere", "", " Warps whole channel to you."),
                    new GMCommandDefinition("killmap", "", " Kills every one in map"),
                    new GMCommandDefinition("killleft", "", " Kills every one in map left to you"),
                    new GMCommandDefinition("killright", "", " Kills every one in map right to you"),
                    new GMCommandDefinition("healmap", "", " heals every one in map"),
                    new GMCommandDefinition("healleft", "", " heals every one in map left to you"),
                    new GMCommandDefinition("healright", "", " heals every one in map right to you"),
                    new GMCommandDefinition("tf", "", "warps you to t/f map"),
                    new GMCommandDefinition("tensu", "ign", "gives ninja tensu to player. Abuse this and you will be banned"),
                    new GMCommandDefinition("true", "", "the answer is true so warp out all False"),
                    new GMCommandDefinition("false", "", "the answer is false so warp out all True"),
                    new GMCommandDefinition("prolevel", "ign", "levels a person to 200"),
                    new GMCommandDefinition("clock", "seconds", "shows a clock"),
                    new GMCommandDefinition("mutemap", "", "mutes the map for event"),
                    new GMCommandDefinition("unmutemap", "", "umutes event muted Map"),
        };
    }
}
