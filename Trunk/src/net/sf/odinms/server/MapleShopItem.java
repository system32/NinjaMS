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
 * MapleShopItem.java
 *
 * Created on 25. November 2007, 20:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.odinms.server;

/**
 *
 * @author Matze
 */
public class MapleShopItem {

		private int maxSlot;
        private int itemId;
        private int price;
        
        /** Creates a new instance of MapleShopItem */
        public MapleShopItem(int itemId, int price, int maxSlot) {
                this.itemId = itemId;
                this.price = price;
				this.maxSlot = maxSlot;
        }

        public int getMaxSlot() {
                return maxSlot;
        }

        public int getItemId() {
                return itemId;
        }

        public int getPrice() {
                return price;
        }
        
}