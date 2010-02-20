/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.constants.SpecialStuff;

/**
 *
 * @author Admin
 */
public class WarpCommands implements PlayerCommand {

    public void execute(final MapleClient c, final MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("home")) {
            if (!SpecialStuff.getInstance().canWarpFrom(player)) {
                mc.dropMessage("[Anbu] We say you suck for trying to warp from this map. now please die. no warp for you.");
            } else {
                player.goHome();
                mc.dropMessage("Euu are a retard for warping home");
            }
        } else if (splitted[0].equalsIgnoreCase("cc")) {
            if (((!c.getPlayer().isAlive() || SpecialStuff.getInstance().canCCFrom(c.getPlayer().getMapId())) && !player.isJounin()) || c.getPlayer().isInflicted()) {
                mc.dropMessage("[Anbu] You cannot CC in this place or situation.");
                return;
            }
            final int channel = Integer.parseInt(splitted[1]);
            if (channel < 1 || channel > ChannelServer.getAllInstances().size()) {
                c.showMessage(1, "This channel does not exist!");
            } else {
                mc.dropMessage("[Anbu] Please wait 3 seconds for cooldown (breaks down mass CCing)");
                mc.dropMessage("[Anbu] Please wait....3");
                TimerManager tMan = TimerManager.getInstance();
                TimerManager.getInstance().schedule(new Runnable() {
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
                        mc.dropMessage("[Anbu] You are warping to channel " + channel + ". This may take a few seconds.");
                        c.getPlayer().changeChannel(channel);
                    }
                }, 3000);
            }
        }
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("home", "", "Warps you Home"),
                    new PlayerCommandDefinition("cc", "channel number", "changes channel"),};
    }
}


