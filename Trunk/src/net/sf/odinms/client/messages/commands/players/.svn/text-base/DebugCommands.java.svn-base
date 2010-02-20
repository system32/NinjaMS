/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import java.rmi.RemoteException;
import net.sf.odinms.client.Enums.MapleStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Owner
 */
public class DebugCommands implements PlayerCommand {

    public void execute(final MapleClient c, final MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("fixexp")) {
            player.setExp(0);
            player.updateSingleStat(MapleStat.EXP, 0);
        } else if (splitted[0].equalsIgnoreCase("save")) {
            mc.dropMessage("[Anbu] Please wait 3 seconds for cooldown ");
            mc.dropMessage("[Anbu] Please wait....3");
            TimerManager tMan = TimerManager.getInstance();
            tMan.schedule(new Runnable() {

                @Override
                public void run() {
                    mc.dropMessage("[Anbu] Please wait....2");
                    c.getPlayer().setUndroppable();
                }
            }, 1000);
            tMan.schedule(new Runnable() {

                @Override
                public void run() {
                    mc.dropMessage("[Anbu] Please wait....1");
                }
            }, 2000);
            tMan.schedule(new Runnable() {

                @Override
                public void run() {
                    mc.dropMessage("[Anbu] Saving your character. This may take a few seconds.");
                    c.getPlayer().forceSave(true, true);
                }
            }, 3000);
        } else if (splitted[0].equalsIgnoreCase("unstuck")) {
            if (player.getCheatTracker().spam(60000, 9)) {
                mc.dropMessage("not so often bitch ;P");
            } else {
                if (splitted.length < 3 && !player.isJounin()) {
                    mc.dropMessage("Syntax : @unstuck <ign> <loginid>");
                } else if (player.isJounin()) {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);

                    if (victim == null) {
                        try {
                            WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                            mc.dropMessage("tried to determine location from world");
                            if (loc != null) {
                                victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                                mc.dropMessage("location determined and assigned the character as victim");
                            } else {
                                player.dropMessage("Player does not exist or is offline");
                                return;
                            }
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (victim != null) {
                        mc.dropMessage("trying to unstuck the victim");
                        victim.unstuck();
                        mc.dropMessage("victim unstuck");
                    }
                } else {
                    MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                    if (victim == null) {
                        try {
                            WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                            if (loc != null) {
                                victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                            } else {
                                player.dropMessage("Player does not exist or is offline");
                                return;
                            }
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (victim != null) {
                        if (splitted[2].equalsIgnoreCase(victim.getClient().getAccountName())) {
                            victim.unstuck();
                        } else {
                            mc.dropMessage("You have entered a wrong login id");
                        }
                    }
                }
            }
        } else if (splitted[0].equalsIgnoreCase("quit")) {
            player.forceSave(true, true);
            TimerManager tMan = TimerManager.getInstance();
            tMan.schedule(new Runnable() {

                @Override
                public void run() {
                    mc.dropMessage("[Anbu] Please wait....2");
                    c.getPlayer().setUndroppable();
                }
            }, 1000);
            tMan.schedule(new Runnable() {

                @Override
                public void run() {
                    mc.dropMessage("[Anbu] Please wait....1");
                }
            }, 2000);
            tMan.schedule(new Runnable() {

                @Override
                public void run() {
                    mc.dropMessage("[Anbu] Saving your character This may take a few seconds.");

                }
            }, 3000);
            c.getSession().write(MaplePacketCreator.dcPacket());
        } else if (splitted[0].equalsIgnoreCase("loadkb")) {
            player.sendKeymap();
            player.sendMacros();
        }
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("fixexp", "", "Fixes negative Exp and sets you to 0 exp"),
                    new PlayerCommandDefinition("save", "", " saves your character and keyboard"),
                    new PlayerCommandDefinition("unstuck", "<ign> <loginId>", "unstuck the noob"),
                    new PlayerCommandDefinition("quit", "", "exits game after saving your char"),
                    new PlayerCommandDefinition("loadkb", "", "loads saved keyboard"),};
    }
}
