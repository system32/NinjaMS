/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;

/**
 *
 * @author Admin
 */
public class PetCommands implements PlayerCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
    /*    if (splitted[0].equalsIgnoreCase("savepets")) {
            c.getPlayer().savePetLoc();
        } else if (splitted[0].equalsIgnoreCase("autospawn")) {
            if (c.getPlayer().getMeso() >= 20) {
                if (splitted.length < 2) {
                    mc.dropMessage("nub shit la euu. syntax is : @autospawn on/off");
                    return;
                }
                if (splitted[1].equalsIgnoreCase("on")) {
                    c.getPlayer().setAutoSpawn(true);
                    c.getPlayer().gainItem(4032016, -20);
                    mc.dropMessage("Autospawn is on");
                } else {
                    c.getPlayer().setAutoSpawn(false);
                    c.getPlayer().gainItem(4032016, -20);
                    mc.dropMessage("Autospawn is off");
                }
                c.getPlayer().gainMeso(-10000000, true);

            } else {
                mc.dropMessage("Sorry br0 we dont run charity. get 10 mil mesos then use this command");
            }
        }*/
        mc.dropMessage("Sorry, disabled temporarily");
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("savepets", "", "save pets for Auto spawn"),
                    new PlayerCommandDefinition("autospawn", "on/off", "turn on and off pet auto spawn"),};
    }
}
