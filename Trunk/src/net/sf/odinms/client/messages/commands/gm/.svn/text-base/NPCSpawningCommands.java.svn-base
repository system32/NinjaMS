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

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;

public class NPCSpawningCommands implements GMCommand {
	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
		if (splitted[0].equals("npc")) {
			int npcId = Integer.parseInt(splitted[1]);
			MapleNPC npc = MapleLifeFactory.getNPC(npcId);
			if (npc != null && !npc.getName().equals("MISSINGNO")) {
				npc.setPosition(c.getPlayer().getPosition());
				npc.setCy(c.getPlayer().getPosition().y);
				npc.setRx0(c.getPlayer().getPosition().x + 50);
				npc.setRx1(c.getPlayer().getPosition().x - 50);
				npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
				npc.setCustom(true);
				c.getPlayer().getMap().addMapObject(npc);
				c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc, false));
				// c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
			} else {
				mc.dropMessage("You have entered an invalid Npc-Id");
			}
		} else if (splitted[0].equals("removenpcs")) {
			MapleCharacter player = c.getPlayer();
			List<MapleMapObject> npcs = player.getMap().getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.NPC));
			for (MapleMapObject npcmo : npcs) {
				MapleNPC npc = (MapleNPC) npcmo;
				if (npc.isCustom()) {
					player.getMap().removeMapObject(npc.getObjectId());
				}
			}
		} else if (splitted[0].equals("mynpcpos")) {
		    Point pos = c.getPlayer().getPosition();
		    mc.dropMessage("CY: " + pos.y +" | X : " + pos.x +" | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getMap().getFootholds().findBelow(pos).getId());
		}
	}

	@Override
	public GMCommandDefinition[] getDefinition() {
		return new GMCommandDefinition[] {
			new GMCommandDefinition("npc", "npcid", "Spawns the npc with the given id at the player position"),
			new GMCommandDefinition("removenpcs", "", "Removes all custom spawned npcs from the map - requires reentering the map"),
			new GMCommandDefinition("mynpcpos", "", "Gets the info for making an npc"),
		};
	}

}
