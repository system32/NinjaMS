/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Owner
 */
public final class ItemSortHandler extends AbstractMaplePacketHandler {
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readInt();
        byte mode = slea.readByte();
        boolean sorted = false;
        MapleInventoryType pInvType = MapleInventoryType.getByType(mode);
        MapleInventory pInv = c.getPlayer().getInventory(pInvType);
        while (!sorted) {
            byte freeSlot = pInv.getNextFreeSlot();
            if (freeSlot != -1) {
                byte itemSlot = -1;
                for (int i = freeSlot + 1; i <= 100; i++) {
                    if (pInv.getItem((byte) i) != null) {
                        itemSlot = (byte) i;
                        break;
                    }
                }
                if (itemSlot <= 100 && itemSlot > 0) {
                    MapleInventoryManipulator.move(c, pInvType, itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            }
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }
}
