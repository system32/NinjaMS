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
 * NPCBuyHandler.java
 *
 * Created on 26. November 2007, 00:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * 
 * @author Matze
 */
public class NPCShopHandler extends AbstractMaplePacketHandler {

	private int BUY = 0;
	private int SELL = 1;
	private int RECHARGE = 2;
	private int CLOSE = 3;

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		if (c.getPlayer().getShop() == null) {
			c.disconnect();
			return;
		}
		byte bmode = slea.readByte();
		if (bmode == BUY) {
			short index = slea.readShort();
			int itemId = slea.readInt();
			short quantity = slea.readShort();
			int price = slea.readInt();
			c.getPlayer().getShop().buy(c, index, itemId, quantity, price);
		} else if (bmode == SELL) {
			byte slot = (byte) slea.readShort();
			int itemId = slea.readInt();
			MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemId);
			short quantity = slea.readShort();
			c.getPlayer().getShop().sell(c, type, slot, quantity);
		} else if (bmode == RECHARGE) {
			byte slot = (byte) slea.readShort();
			c.getPlayer().getShop().recharge(c, slot);
		} else if (bmode == CLOSE) {
			c.getPlayer().setNpcId(-1);
			c.getPlayer().setShop(null);
		}
	}
}
