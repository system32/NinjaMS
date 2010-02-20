/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.Processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.database.DatabaseConnection;

/**
 *
 * @author Admin
 */
public class UnbanProcessor {

    public static List<String> unban(String playerName) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        int done = 0;
        int accountid = 0;
        List<String> ret = new LinkedList<String>();
        try {
            ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                ret.add("There are two many of these characters or the Character does not exist.");
            }
            accountid = rs.getInt("accountid");
            ret.add("[Account ID Exists] Initating Unban Request");
            done = 0;
            ps.close();
        } catch (SQLException e) {
            ret.add("Account ID or Character fails to exist. Error : " + e);
        }
        try {
            ps = con.prepareStatement("update accounts set greason = null, banned = 0, banreason = null, tempban = '0000-00-00 00:00:00' where id = ? and tempban <> '0000-00-00 00:00:00'");
            ps.setInt(1, accountid);
            int results = ps.executeUpdate();
            if (results > 0) {
                ret.add("[Untempbanned] Account has been untempbanned. Command execution is over. If still banned, repeat command.");
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
                ret.add("[Account Unbanned] ID: " + accountid);
                ret.add("Macs and Ips will be unbanned when the person logs in");
            } catch (SQLException e) {
                ret.add("Account failed to be unbanned or banreason is unfound. Error : " + e);
            }
        }
        if (done == 1) {
            ret.add("Unban command has been completed successfully.");
        } else if (done == 0) {
            ret.add("The unban has epic failed.");
        }
        return ret;
    }

}
