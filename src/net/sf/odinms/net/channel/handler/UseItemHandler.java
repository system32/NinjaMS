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

/*
 * UseItemHandler.java
 *
 * Created on 27. November 2007, 16:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

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

/**
 *
 * @author Matze
 */
public class UseItemHandler extends AbstractMaplePacketHandler {
	private static Logger log = LoggerFactory.getLogger(UseItemHandler.class);
	
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		if (!c.getPlayer().isAlive() || (!c.getPlayer().isJounin() && c.getPlayer().getMapId() == 910000001)) {
			c.getSession().write(MaplePacketCreator.enableActions());
			return;
		}
		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		slea.readInt(); // i have no idea :) (o.o)
		byte slot = (byte)slea.readShort();
		int itemId = slea.readInt(); //as if we cared... ;)
		IItem toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
		if (toUse != null && toUse.getQuantity() > 0) {
			if (toUse.getItemId() != itemId) {
				log.info("[h4x] Player {} is using an item not in the slot: {}", c.getPlayer().getName(), Integer.valueOf(itemId));
			}
			if (itemId == 2022178 || itemId == 2050004) {
				c.getPlayer().dispelDebuffs();
				MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
				c.getSession().write(MaplePacketCreator.enableActions());
				return;
			}
			MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
			ii.getItemEffect(toUse.getItemId()).applyTo(c.getPlayer());
		} else {
			log.info("[h4x] Player {} is using an item he does not have: {}", c.getPlayer().getName(), Integer.valueOf(itemId));
		}
	}
}