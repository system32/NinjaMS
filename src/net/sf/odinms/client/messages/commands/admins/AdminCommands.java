/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.admins;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.AdminCommand;
import net.sf.odinms.client.messages.AdminCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class AdminCommands implements AdminCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {

        if (splitted[0].equalsIgnoreCase("sql")) {
            try {
                DatabaseConnection.getConnection().prepareStatement(StringUtil.joinStringFrom(splitted, 1)).executeUpdate();
                mc.dropMessage("Sucess");
            } catch (SQLException ex) {
                mc.dropMessage("Something went wrong.");
            }
        } else if (splitted[0].equalsIgnoreCase("banlist")) {
            mc.dropMessage("works");
        } else if (splitted[0].equalsIgnoreCase("newdrop")) {
            int monster = Integer.parseInt(splitted[1]);
            int item = Integer.parseInt(splitted[2]);
            int chance = Integer.parseInt(splitted[3]);
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("INSERT INTO monsterdrops (`monsterid`, `itemid`, `chance`) VALUES (?, ?, ?)");
                ps.setInt(1, monster);
                ps.setInt(2, item);
                ps.setInt(3, chance);
                ps.executeUpdate();
                ps.close();
                mc.dropMessage("Drop added.");
            } catch (Exception ex) {
                mc.dropMessage("Drop failed");
            }
        } else if (splitted[0].equalsIgnoreCase("reloadevents")) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.reloadEvents();
            }
            mc.dropMessage("Done reloading all events");
        } else if (splitted[0].equalsIgnoreCase("setsannin")){
            if(splitted.length != 2){
                mc.dropMessage("Hey nub read /commands. Syntax: /setsannin ign");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    victim.setGMStatus(4);
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        }else if (splitted[0].equalsIgnoreCase("setjounin")){
            if(splitted.length != 2){
                mc.dropMessage("Hey nub read /commands. Syntax: /setjounin ign");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    victim.setGMStatus(3);
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        }else if (splitted[0].equalsIgnoreCase("setchunin")){
            if(splitted.length != 2){
                mc.dropMessage("Hey nub read /commands. Syntax: /setchunin ign");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    victim.setGMStatus(2);
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        }else if (splitted[0].equalsIgnoreCase("setgenin")){
            if(splitted.length != 2){
                mc.dropMessage("Hey nub read /commands. Syntax: /setgenin ign");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    victim.setGMStatus(1);
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        }else if (splitted[0].equalsIgnoreCase("setrookie")){
            if(splitted.length != 2){
                mc.dropMessage("Hey nub read /commands. Syntax: /setrookie ign");
                return;
            }
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter victim = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    victim.setGMStatus(0);
                } else {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline. Error :" + e.toString());
            }
        }
    }

    public AdminCommandDefinition[] getDefinition() {
        return new AdminCommandDefinition[]{
                    new AdminCommandDefinition("sql", "Query String", "runs SQL update Queries from game"),
                    new AdminCommandDefinition("newdrop", "monster itemid chance", "adds new drop "),
                    new AdminCommandDefinition("setsannin", "ign", "Sets the persons gm level to sannin"),
                    new AdminCommandDefinition("setjounin", "ign", "Sets the persons gm level to jounin"),
                    new AdminCommandDefinition("setchunin", "ign", "Sets the persons gm level to chunin"),
                    new AdminCommandDefinition("setgenin", "ign", "Sets the persons gm level to genin"),
                    new AdminCommandDefinition("setrookie", "ign", "Sets the persons gm level to rookie"),
        };
    }
}
