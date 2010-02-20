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

package net.sf.odinms.net.channel.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.SkillFactory;
	
public class SkillBookHandler extends AbstractMaplePacketHandler {	
	private static Logger log = LoggerFactory.getLogger(ScrollHandler.class);
	
	// Create a new instance
	public SkillBookHandler() {
	}
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c){
		if (!c.getPlayer().isAlive()) {
			c.getSession().write(MaplePacketCreator.enableActions());
			return;
		}
		slea.readInt(); // teh ignored.
		byte slot = (byte)slea.readShort();
		int itemId = slea.readInt();
		MapleCharacter player = c.getPlayer();
		//List<IItem> existing = c.getPlayer().getInventory(MapleInventoryType.USE).listById(itemId);
		IItem toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
			
		if (toUse != null && toUse.getQuantity() == 1) {
			if (toUse.getItemId() != itemId) {
				log.info("[h4x] Player {} is using a SkillBook not in the slot: {}", c.getPlayer().getName(), Integer.valueOf(itemId));
			}
			MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
			MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
			Map<String, Integer> skilldata = ii.getSkillStats(toUse.getItemId(), c.getPlayer().getJob().getId());
			
			// Initialize defaults
			boolean canuse = false;
			boolean success = false;
			int skill = 0;
			int maxlevel = 0;
			
			if(skilldata == null) {
				// Hacking
				// Used an unknown item
				return;				
			}
			
			if(skilldata.get("skillid") == 0) {
				// Wrong Job
				canuse = false;
			} else if (player.getMasterLevel(SkillFactory.getSkill(skilldata.get("skillid"))) >= skilldata.get("reqSkillLevel") || skilldata.get("reqSkillLevel") == 0) {
				canuse = true;
				int random = (int) Math.floor(Math.random() * 100) + 1;				
				if(random <= skilldata.get("success") && skilldata.get("success") != 0) {
					success = true;
					ISkill skill2 = SkillFactory.getSkill(skilldata.get("skillid"));
					int curlevel = player.getSkillLevel(skill2);
					player.changeSkillLevel(skill2, curlevel, skilldata.get("masterLevel"));
				} else {
					success = false;
				}				
			} else {
				// Failed to meet skill requirements.
				canuse = false;					
			}
			
			if(canuse == false)
				MapleInventoryManipulator.addById(c, toUse.getItemId(), (short)1, "Skillbook cannot be used.");
			
			player.getClient().getSession().write(MaplePacketCreator.skillBookSuccess(player, skill, maxlevel, canuse, success));
		} else {
			log.info("[h4x] Player {} is using a SkillBook they does not have: {}", c.getPlayer().getName(), Integer.valueOf(itemId));
		}
	}
}