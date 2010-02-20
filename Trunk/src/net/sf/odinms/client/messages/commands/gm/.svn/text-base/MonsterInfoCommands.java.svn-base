/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.client.messages.commands.gm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;

public class MonsterInfoCommands implements GMCommand {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        Connection con = DatabaseConnection.getConnection();
        if (splitted[0].equals("killall") || splitted[0].equals("monsterdebug")) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            if (splitted.length > 1) {
                int irange = Integer.parseInt(splitted[1]);
                range = irange * irange;
            }
            List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            boolean kill = splitted[0].equals("killall");
            for (MapleMapObject monstermo : monsters) {
                MapleMonster monster = (MapleMonster) monstermo;
                if (kill) {
                    map.killMonster(monster, c.getPlayer(), false);
                } else {
                    mc.dropMessage("Monster " + monster.toString());
                }
            }
            if (kill) {
                mc.dropMessage("Killed " + monsters.size() + " monsters <3");
            }
        } else if (splitted[0].equalsIgnoreCase("insertdrop")) {
            if (splitted.length != 3) {
                mc.dropMessage("Syntax : !insertdrop mobid itemid");
                return;
            }
            int mid = Integer.parseInt(splitted[1]);
            int iid = Integer.parseInt(splitted[2]);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (ii.getSlotMax(iid) < 1) {
                mc.dropMessage("Seems like you entered wrong itemid");
                return;
            }
            int chance = 0;
            if (iid < 2000000) {
                chance = 10000;
            } else if (iid < 3000000) {
                chance = 5000;
            } else if (iid < 4000000) {
                mc.dropMessage("Seems like you entered wrong itemid");
                return;
            } else {
                chance = 500;
            }
            PreparedStatement ps = con.prepareStatement("INSERT INTO `monsterdrops` (`monsterid`,`itemid`,`chance`) VALUES (?, ?, ?);");
            ps.setInt(1, mid);
            ps.setInt(2, iid);
            ps.setInt(3, chance);
            if (ps.execute()) {
                mc.dropMessage(" Success ");
            } else {
                mc.dropMessage(" Failed ");
            }
            ps.close();
        } else if (splitted[0].equalsIgnoreCase("removedrop")) {
            int mid = Integer.parseInt(splitted[1]);
            int iid = Integer.parseInt(splitted[2]);
            PreparedStatement ps = con.prepareStatement("DELETE FROM `monsterdrops` where `monsterid` = ? AND `itemid` = ?");
            ps.setInt(1, mid);
            ps.setInt(2, iid);
            if (ps.execute()) {
                mc.dropMessage(" Success ");
            } else {
                mc.dropMessage(" Failed ");
            }
            ps.close();
        }
    }

    @Override
    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("killall", "[range]", ""),
                    new GMCommandDefinition("monsterdebug", "[range]", ""),
                    new GMCommandDefinition("insertdrop", "mobid itemid", " Inserts items to the drop data base. Will work after server check"),
                    new GMCommandDefinition("removedrop", "mobid itemid", " Deletes an item from dropping"),};
    }
}
