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
package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.Inventory.Equip;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleStorage;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Matze
 */
public class StorageHandler extends AbstractMaplePacketHandler {

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte mode = slea.readByte();
        final MapleStorage storage = c.getPlayer().getStorage();
        if(c.getPlayer().getReborns() < 5){
            c.showMessage("You cannot use thie feature until you have 5 Rebirths or Above");
            return;
        }
        if (mode == 4) { // take out
            byte type = slea.readByte();
            byte slot = slea.readByte();
            slot = storage.getSlot(MapleInventoryType.getByType(type), slot);
            IItem item = storage.takeOut(slot);
            if (item != null) {
                if (MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    StringBuilder logInfo = new StringBuilder("Taken out from storage by ");
                    logInfo.append(c.getPlayer().getName());
                    MapleInventoryManipulator.addFromDrop(c, item);
                } else {
                    storage.store(item);
                    c.getSession().write(MaplePacketCreator.serverNotice(1, "Your inventory is full"));
                }
                storage.sendTakenOut(c, ii.getInventoryType(item.getItemId()));
            } else {
                AutobanManager.getInstance().autoban(c, "Trying to take out item from storage which does not exist.");
            }
        } else if (mode == 5) { // store
            byte slot = (byte) slea.readShort();
            int itemId = slea.readInt();
            short quantity = slea.readShort();
            if (quantity < 1) {
                AutobanManager.getInstance().autoban(c, "Trying to store " + quantity + " of " + itemId);
                return;
            }
            if (storage.isFull()) {
                c.getSession().write(MaplePacketCreator.getStorageFull());
                return;
            }
            if (c.getPlayer().getMeso() < 100) {
                c.getSession().write(MaplePacketCreator.serverNotice(1, "You don't have enough mesos to store the item"));
            } else {
                MapleInventoryType type = ii.getInventoryType(itemId);
                IItem item = c.getPlayer().getInventory(type).getItem(slot).copy();
                if (type == MapleInventoryType.EQUIPPED || type == MapleInventoryType.EQUIP) {
                    Equip equip = (Equip) item;
                    if (equip.getLevel() > 12) {
                        c.showMessage("You cannot store a Stat Item.");
                        return;
                    }
                }
                if (item.getItemId() == itemId && (item.getQuantity() >= quantity || ii.isThrowingStar(itemId) || ii.isBullet(itemId))) {
                    if (ii.isThrowingStar(itemId) || ii.isBullet(itemId)) {
                        quantity = item.getQuantity();
                    }                    
                    c.getPlayer().gainMeso(-100, true, true, true);
                    MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
                    item.setQuantity(quantity);
                    storage.store(item);
                } else {
                    AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to store non-matching itemid (" + itemId + "/" + item.getItemId() + ") or quantity not in posession (" + quantity + "/" + item.getQuantity() + ")");
                }
            }
            storage.sendStored(c, ii.getInventoryType(itemId));
        } else if (mode == 7) { // meso
            int meso = slea.readInt();
            int storageMesos = storage.getMeso();
            int playerMesos = c.getPlayer().getMeso();
            if ((meso > 0 && storageMesos >= meso) || (meso < 0 && playerMesos >= -meso)) {
                if (meso < 0 && (storageMesos - meso) < 0) { // storing with overflow
                    meso = -(Integer.MAX_VALUE - storageMesos);
                    if ((-meso) > playerMesos) { // should never happen just a failsafe
                        throw new RuntimeException("everything sucks");
                    }
                } else if (meso > 0 && (playerMesos + meso) < 0) { // taking out with overflow
                    meso = (Integer.MAX_VALUE - playerMesos);
                    if ((meso) > storageMesos) { // should never happen just a failsafe
                        throw new RuntimeException("everything sucks");
                    }
                }
                storage.setMeso(storageMesos - meso);
                c.getPlayer().gainMeso(meso, true, true, true);
            } else {
                AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to store or take out unavailable amount of mesos (" + meso + "/" + storage.getMeso() + "/" + c.getPlayer().getMeso() + ")");
            }
            storage.sendMeso(c);
        } else if (mode == 8) { // close
            storage.close();
        }
    }
}
