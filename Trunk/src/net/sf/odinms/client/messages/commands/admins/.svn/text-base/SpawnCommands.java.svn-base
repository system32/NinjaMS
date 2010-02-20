/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.admins;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.messages.AdminCommand;
import net.sf.odinms.client.messages.AdminCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.life.PlayerNPCs;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class SpawnCommands implements AdminCommand {

    public AdminCommandDefinition[] getDefinition() {
        return new AdminCommandDefinition[]{
                    new AdminCommandDefinition("newmob", "mobid respawntime", "spawns a permanent mob"),
                    new AdminCommandDefinition("newnpc", "npcid", "spawns a permanent npc"),
                    new AdminCommandDefinition("playernpc", "name scriptid", "player npc spawning command")
        };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter player = c.getPlayer();
        ChannelServer cserv = c.getChannelServer();
        if (splitted[0].equalsIgnoreCase("newmob")) {
        int npcId = Integer.parseInt(splitted[1]);
            int mobTime = Integer.parseInt(splitted[2]);
            MapleMonster mob = MapleLifeFactory.getMonster(npcId);
            int xpos = player.getPosition().x;
            int ypos = player.getPosition().y;
            if (mob != null && !mob.getName().equals("MISSINGNO")) {
                mob.setPosition(c.getPlayer().getPosition());
                mob.setCy(ypos);
                mob.setRx0(xpos + 50);
                mob.setRx1(xpos - 50);
                mob.setFh(player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                try {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid, mobtime ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                    ps.setInt(1, npcId);
                    ps.setInt(2, 0);
                    ps.setInt(3, player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                    ps.setInt(4, ypos);
                    ps.setInt(5, xpos + 50);
                    ps.setInt(6, xpos - 50);
                    ps.setString(7, "m");
                    ps.setInt(8, xpos);
                    ps.setInt(9, ypos);
                    ps.setInt(10, player.getMapId());
                    ps.setInt(11, mobTime);
                    ps.executeUpdate();
                    ps.close();
                    mc.dropMessage("Mob Placed.");
                } catch (Exception ex) {
                }
                player.getMap().addMonsterSpawn(mob, mobTime);
            } else {
                mc.dropMessage("You have entered an invalid mob-ID.");
            }
        } else if (splitted[0].equalsIgnoreCase("newnpc")) {
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                npc.setCustom(false);
                try {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                    ps.setInt(1, npcId);
                    ps.setInt(2, 0);
                    ps.setInt(3, c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                    ps.setInt(4, c.getPlayer().getPosition().y);
                    ps.setInt(5, c.getPlayer().getPosition().x + 50);
                    ps.setInt(6, c.getPlayer().getPosition().x - 50);
                    ps.setString(7, "n");
                    ps.setInt(8, c.getPlayer().getPosition().x);
                    ps.setInt(9, c.getPlayer().getPosition().y);
                    ps.setInt(10, c.getPlayer().getMapId());
                    ps.executeUpdate();
                    ps.close();
                    mc.dropMessage("NPC Placed.");
                } catch (Exception ex) {
                }
                for (ChannelServer channel : ChannelServer.getAllInstances()) {
                    channel.getMapFactory().getMap(player.getMapId()).addMapObject(npc);
                    channel.getMapFactory().getMap(player.getMapId()).broadcastMessage(MaplePacketCreator.spawnNPC(npc, false));
                }
            } else {
                mc.dropMessage("NPC fails to exist.");
            }
        }else if (splitted[0].equalsIgnoreCase("playernpc")) {
            if (splitted.length < 3) {
                mc.dropMessage("Please use the correct syntax. !playernpc <char name> <script name>. NOTE: scriptId < 9901000 || scriptId > 9901319");
                return;
            }

            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            int scriptId = Integer.parseInt(splitted[2]);

            int npcId;
            if (victim == null) {
                mc.dropMessage("The character is not in this channel");
            } else {
                try {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("INSERT INTO playernpcs (name, hair, face, skin, x, cy, map, ScriptId, Foothold, rx0, rx1, gender, dir) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    MapleNPC npc = MapleLifeFactory.getNPC(scriptId);
                    if (npc == null) {
                        c.showMessage("Invalid NPCId.");
                        return;
                    }
                    if (splitted.length > 3) {
                        ps.setString(1, StringUtil.joinStringFrom(splitted, 3));
                    } else {
                        ps.setString(1, victim.getName());
                    }
                    ps.setInt(2, victim.getHair());
                    ps.setInt(3, victim.getFace());
                    ps.setInt(4, victim.getSkinColor().getId());
                    ps.setInt(5, player.getPosition().x);
                    ps.setInt(6, player.getPosition().y);
                    ps.setInt(7, player.getMapId());
                    ps.setInt(8, scriptId);
                    ps.setInt(9, player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                    ps.setInt(10, player.getPosition().x + 50); // I should really remove rx1 rx0. Useless piece of douche
                    ps.setInt(11, player.getPosition().x - 50);
                    ps.setInt(12, victim.getGender());
                    ps.setInt(13, player.isFacingLeft() ? 1 : 0);
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    rs.next();
                    npcId = rs.getInt(1);
                    ps.close();
                    ps = con.prepareStatement("INSERT INTO playernpcs_equip (NpcId, equipid, equippos) VALUES (?, ?, ?)");
                    ps.setInt(1, npcId);
                    for (IItem equip : victim.getInventory(MapleInventoryType.EQUIPPED)) {
                        ps.setInt(2, equip.getItemId());
                        ps.setInt(3, equip.getPosition());
                        ps.executeUpdate();
                    }
                    ps.close();
                    rs.close();

                    ps = con.prepareStatement("SELECT * FROM playernpcs WHERE ScriptId = ?");
                    ps.setInt(1, scriptId);
                    rs = ps.executeQuery();
                    rs.next();
                    PlayerNPCs pn = new PlayerNPCs(rs);

                    for (ChannelServer channel : ChannelServer.getAllInstances()) {
                        MapleMap map = channel.getMapFactory().getMap(player.getMapId());
                        map.broadcastMessage(MaplePacketCreator.spawnPlayerNPC(pn));
                        map.broadcastMessage(MaplePacketCreator.getPlayerNPC(pn));
                        map.addMapObject(pn);
                    }

                    ps.close();
                    rs.close();

                    mc.dropMessage("Successfully executed!");
                } catch (SQLException e) {
                    mc.dropMessage("" + e + e.getErrorCode() + e.getSQLState() + e.getNextException());
                }
            }
        }
    }
}
