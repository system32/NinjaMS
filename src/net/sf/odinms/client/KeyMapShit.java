/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.odinms.database.DatabaseConnection;

/**
 *
 * @author Owner
 */
public class KeyMapShit {

    public static void loadKeymap(MapleCharacter chr, int type) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM saved_skillmacros WHERE characterid = ? AND type = ?");
            ps.setInt(1, chr.getId());
            ps.setInt(2, type);
            ResultSet rs = ps.executeQuery();
            SkillMacro[] skillMacros = new SkillMacro[5];
            if (!rs.next()) {
                chr.dropMessage("You do not have Saved Macro for this mode");
                rs.close();
                ps.close();
            } else {
                while (rs.next()) {
                    int skill1 = rs.getInt("skill1");
                    int skill2 = rs.getInt("skill2");
                    int skill3 = rs.getInt("skill3");
                    String name = rs.getString("name");
                    int shout = rs.getInt("shout");
                    int position = rs.getInt("position");
                    SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, position);
                    skillMacros[position] = macro;
                }
                chr.setMacros(skillMacros);
                chr.dropMessage("Macros Loaded");
                rs.close();
                ps.close();
            }
            ps = con.prepareStatement("SELECT * FROM saved_keymap WHERE characterid = ? AND kbtype = ?");
            ps.setInt(1, chr.getId());
            ps.setInt(2, type);
            rs = ps.executeQuery();
            if (!rs.next()) {
                chr.dropMessage("You do not have Saved KeyBoard for this ");
                rs.close();
                ps.close();
                return;
            }
            Map<Integer, MapleKeyBinding> keymap = new LinkedHashMap<Integer, MapleKeyBinding>();
            while (rs.next()) {
                keymap.put(Integer.valueOf(rs.getInt("key")), new MapleKeyBinding(rs.getInt("type"), rs.getInt("action")));
            }
            chr.setKeyMap(keymap);
            chr.dropMessage("Keyboard Loaded");
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveKeymap(MapleCharacter chr, int type) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM saved_skillmacros WHERE characterid = ? AND type = ?");
            ps.setInt(1, chr.getId());
            ps.setInt(2, type);
            ps.executeUpdate();
            ps = con.prepareStatement("INSERT INTO saved_skillmacros" + " (characterid, skill1, skill2, skill3, name, shout, position, type) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            SkillMacro[] skillMacro = chr.getSkillMacros();
            for (int i = 0; i < 5; i++) {
                SkillMacro macro = skillMacro[i];
                ps.setInt(1, chr.getId());
                ps.setInt(8, type);
                if (macro != null) {
                    ps.setInt(2, macro.getSkill1());
                    ps.setInt(3, macro.getSkill2());
                    ps.setInt(4, macro.getSkill3());
                    ps.setString(5, macro.getName());
                    ps.setInt(6, macro.getShout());
                    ps.setInt(7, i);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            chr.dropMessage("Macros Saved");
            ps = con.prepareStatement("DELETE FROM saved_keymap WHERE characterid = ? AND type = ?");
            ps.setInt(1, chr.getId());
            ps.setInt(2, type);
            ps = con.prepareStatement("INSERT INTO saved_keymap (`characterid`, `key`, `type`, `action`, `kbtype`) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, chr.getId());
            ps.setInt(5, type);
            for (Entry<Integer, MapleKeyBinding> keybinding : chr.getKeymap().entrySet()) {
                if (keybinding != null) {
                    ps.setInt(2, keybinding.getKey().intValue());
                    ps.setInt(3, keybinding.getValue().getType());
                    ps.setInt(4, keybinding.getValue().getAction());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            chr.dropMessage("Key Board Saved");
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
