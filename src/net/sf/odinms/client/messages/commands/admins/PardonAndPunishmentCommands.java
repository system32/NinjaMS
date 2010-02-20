/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.admins;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.AdminCommand;
import net.sf.odinms.client.messages.AdminCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;

/**
 *
 * @author Admin
 */
public class PardonAndPunishmentCommands implements AdminCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("-1")) {
            String playerName = splitted[1];
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            int done = 0;
            int accountid = 0;
            try {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
                ps.setString(1, playerName);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    ps.close();
                    mc.dropMessage("There are two many of these characters or the Character does not exist.");
                }
                accountid = rs.getInt("accountid");
                mc.dropMessage("[Account ID Exists] Initating Unban Request");
                done = 0;
                ps.close();
            } catch (SQLException e) {
                mc.dropMessage("Account ID or Character fails to exist. Error : " + e);
            }
            try {
                ps = con.prepareStatement("update accounts set greason = null, banned = 0, banreason = null, tempban = '0000-00-00 00:00:00' where id = ? and tempban <> '0000-00-00 00:00:00'");
                ps.setInt(1, accountid);
                int results = ps.executeUpdate();
                if (results > 0) {
                    mc.dropMessage("[Untempbanned] Account has been untempbanned. Command execution is over. If still banned, repeat command.");
                    done = 1;
                }
                ps.close();
            } catch (SQLException e) {
                // not a tempba, moved on.
            }
            if (done != 1) {
                try {
                    ps = con.prepareStatement("UPDATE accounts SET banned = -1, banreason = null WHERE id = " + accountid);
                    ps.executeUpdate();
                    ps.close();
                    done = 1;
                    mc.dropMessage("[Account Unbanned] ID: " + accountid);
                    mc.dropMessage("Macs and Ips will be unbanned when the person logs in");
                } catch (SQLException e) {
                    mc.dropMessage("Account failed to be unbanned or banreason is unfound. Error : " + e);
                }
            }
            if (done == 1) {
                mc.dropMessage("Unban command has been completed successfully.");
            } else if (done == 0) {
                mc.dropMessage("The unban has epic failed.");
            }
        }
    }

    public AdminCommandDefinition[] getDefinition() {
        return new AdminCommandDefinition[]{
                    new AdminCommandDefinition("-1", "ign", "unban command"),};
    }
}
