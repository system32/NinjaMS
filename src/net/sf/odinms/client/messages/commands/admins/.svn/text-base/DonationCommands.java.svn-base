/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.admins;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.AdminCommand;
import net.sf.odinms.client.messages.AdminCommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;

/**
 *
 * @author Admin
 */
public class DonationCommands implements AdminCommand {

    public AdminCommandDefinition[] getDefinition() {
        return new AdminCommandDefinition[]{
                    new AdminCommandDefinition("setdpoint", "ign amount", "sets the persons donator points"),
                    new AdminCommandDefinition("setdamount", "ign amount", "sets the persons donated amount"),};
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        if (splitted[0].equalsIgnoreCase("setdpoint")) {
            if (splitted.length != 3) {
                mc.dropMessage("Drunk?? read the commands. Syntax : /setdpoint ign amount");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    short x = victim.getDPoints();
                    try {
                        x = Short.parseShort(splitted[2]);
                    } catch (NumberFormatException numberFormatException) {
                        mc.dropMessage("Drunk?? read the commands. Syntax : /setdpoint ign amount");
                    }
                    victim.modifyDPoints(x);
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }

        } else if (splitted[0].equalsIgnoreCase("setdamount")) {
            if (splitted.length != 3) {
                mc.dropMessage("Drunk?? read the commands. Syntax : /setdamount ign amount");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    short x = victim.getDAmount();
                    try {
                        x = Short.parseShort(splitted[2]);
                    } catch (NumberFormatException numberFormatException) {
                        mc.dropMessage("Drunk?? read the commands. Syntax : /setdamount ign amount");
                    }
                    victim.modifyDAmount(x);
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }

        }
    }
}
