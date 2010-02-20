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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.server;

import net.sf.odinms.client.Inventory.IItem;

/**
 *
 * @author Matze
 */
public class MaplePlayerShopItem {

    private IItem item;
    private short bundles;
    private int price;
    private boolean doesExist;

    public MaplePlayerShopItem(IItem item, short bundles, int price) {

        this.item = item;
        this.bundles = bundles;
        this.price = price;
        this.doesExist = true;
    }

    public IItem getItem() {
        return item;
    }

    public boolean isExist() {
        return doesExist;
    }

    public void setDoesExist(boolean setTo){
        doesExist = setTo;
    }

    public short getBundles() {
        return bundles;
    }

    public int getPrice() {
        return price;
    }

    public void setBundles(short bundles) {
        this.bundles = bundles;
    }
}
