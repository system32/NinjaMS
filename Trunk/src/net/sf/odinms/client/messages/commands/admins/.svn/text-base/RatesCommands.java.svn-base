/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.messages.commands.admins;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.AdminCommand;
import net.sf.odinms.client.messages.AdminCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Admin
 */
public class RatesCommands implements AdminCommand{

    public AdminCommandDefinition[] getDefinition() {
        return new AdminCommandDefinition[]{
            new AdminCommandDefinition("exprate", "<rate>", "duh? "),
        };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {

        if (splitted[0].equals("exprate")) { // by Redline/2azn4u
            if (splitted.length > 1) {
                int exp = Integer.parseInt(splitted[1]);
                c.getChannelServer().setExpRate(exp);
                MaplePacket packet = MaplePacketCreator.serverNotice(6, "Exprate has been changed to " + exp + "x");
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else {
                mc.dropMessage("Syntax: !exprate <number>");
            }
        } else if (splitted[0].equals("droprate")) { // by doncare aka voice123
            if (splitted.length > 1) {
                int drop = Integer.parseInt(splitted[1]);
                c.getChannelServer().setDropRate(drop);
                MaplePacket packet = MaplePacketCreator.serverNotice(6, "Drop Rate has been changed to " + drop + "x");
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else {
                mc.dropMessage("Syntax: !droprate <number>");
            }
        } else if (splitted[0].equals("bossdroprate")) { // by doncare aka voice123
            if (splitted.length > 1) {
                int bossdrop = Integer.parseInt(splitted[1]);
                c.getChannelServer().setBossDropRate(bossdrop);
                MaplePacket packet = MaplePacketCreator.serverNotice(6, "Boss Drop Rate has been changed to " + bossdrop + "x");
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else {
                mc.dropMessage("Syntax: !bossdroprate <number>");
            }
        }
    }

}
