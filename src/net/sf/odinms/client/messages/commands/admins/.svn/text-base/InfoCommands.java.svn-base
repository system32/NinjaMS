/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.admins;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.CharInfoProcessor;
import net.sf.odinms.client.messages.AdminCommand;
import net.sf.odinms.client.messages.AdminCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;

/**
 *
 * @author Admin
 */
public class InfoCommands implements AdminCommand {

    public AdminCommandDefinition[] getDefinition() {
        return new AdminCommandDefinition[]{
                    new AdminCommandDefinition("onlineall", "", "shows online characters in all channel. Dont use this often thanks."),
                    new AdminCommandDefinition("ninjaglare", "charname", "Shows info about the character with the given name"),
                    new AdminCommandDefinition("getcharinfo", "charname", "shows stats offline. Use with care. too expensive"),
                    new AdminCommandDefinition("seereason", "charname", "shows ban reason. dont use it with out a reason. too expensive"),};
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].toLowerCase().equalsIgnoreCase("onlineall")) {
            StringBuilder sb = new StringBuilder("Characters online: ");
            mc.dropMessage(sb.toString());
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                sb = new StringBuilder();
                sb.append("Channel " + cs.getChannel() +" : ");
                for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
                    if (sb.length() > 150) {
                        sb.setLength(sb.length() - 2);
                        mc.dropMessage(sb.toString());
                        sb = new StringBuilder();
                    }
                    if (!chr.isJounin()) {
                        sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                        sb.append(", ");
                    }
                }
                if (sb.length() >= 2) {
                    sb.setLength(sb.length() - 2);
                    mc.dropMessage(sb.toString());
                }
            }
        } else if (splitted[0].equalsIgnoreCase("ninjaglare")) {
            MapleCharacter other = c.getPlayer();
            if (splitted.length == 2) {
                try {
                    WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                    if (loc != null) {
                        other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    } else {
                        mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                        return;
                    }
                } catch (Exception e) {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                    return;
                }
            }
            CharInfoProcessor.getNinjaGlare(mc, other);
        } else if (splitted[0].equalsIgnoreCase("getcharinfo")) {
            if (splitted.length == 2) {
                CharInfoProcessor.loadAccountDetails(c, splitted[1]);
            } else {
                mc.dropMessage("fail GM . Syntax: !getcharinfo <ign>");
            }
        } else if (splitted[0].equalsIgnoreCase("seereason")) {
            if (splitted.length == 2) {
                CharInfoProcessor.seeReason(c, splitted[1]);
            } else {
                mc.dropMessage("fail GM . Syntax: !seereason <ign>");
            }
        }
    }
}
