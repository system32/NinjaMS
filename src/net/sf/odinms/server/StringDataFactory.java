/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.sf.odinms.database.DatabaseConnection;

/**
 *
 * @author Admin
 */
public class StringDataFactory {

    private static Map<Integer, StringData> strings = new HashMap<Integer, StringData>();
    public static StringData getData(int objectId) {
        StringData stringdata = strings.get(objectId);
        if (stringdata != null) {
            return stringdata;
        } else {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * from `mcdb`.`strings`");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String type = rs.getString("object_type");
                    String name = rs.getString("name");
                    int objid = rs.getInt("objectid");
                    stringdata = new StringData(type, name);
                    strings.put(objid, stringdata);
                }
                ps.close();
                rs.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return strings.get(objectId);
    }   
}


