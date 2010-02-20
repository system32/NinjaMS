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
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package net.sf.odinms.net.channel.handler;

import java.util.List;

import net.sf.odinms.client.Inventory.IEquip;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.InventoryException;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.IEquip.ScrollResult;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matze
 * @author Frz
 */
public class ScrollHandler extends AbstractMaplePacketHandler {

    private static Logger log = LoggerFactory.getLogger(ScrollHandler.class);

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readInt(); // whatever...
        byte slot = (byte) slea.readShort();
        byte dst = (byte) slea.readShort();
        byte ws = (byte) slea.readShort();
        boolean whiteScroll = false; // white scroll being used?
        boolean legendarySpirit = false; // legendary spirit skill
        if ((ws & 2) == 2) {
            whiteScroll = true;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        IEquip toScroll;
        if (dst < 0) {
            toScroll = (IEquip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        } else {
            // legendary spirit
            legendarySpirit = true;
            toScroll = (IEquip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
        }
        byte oldLevel = toScroll.getLevel();
        if (((IEquip) toScroll).getUpgradeSlots() < 1) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            return;
        }
        if (((IEquip) toScroll).getUpgradeSlots() > 69) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            return;
        }

        MapleInventory useInventory = c.getPlayer().getInventory(MapleInventoryType.USE);
        IItem scroll = useInventory.getItem(slot);
        IItem wscroll = null;

        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            return;
        }
        if (whiteScroll) {
            wscroll = useInventory.findById(2340000);
            if (wscroll == null || wscroll.getItemId() != 2340000) {
                whiteScroll = false;
                log.info("[h4x] Player {} is trying to scroll with non existant white scroll", new Object[]{c.getPlayer().getName()});
            }
        }
        if (scroll.getItemId() != 2049100 && !ii.isCleanSlate(scroll.getItemId())) {
            if (!ii.canScroll(scroll.getItemId(), toScroll.getItemId())) {
                log.info("[h4x] Player {} is trying to scroll {} with {} which should not work", new Object[]{
                            c.getPlayer().getName(), toScroll.getItemId(), scroll.getItemId()});
                return;
            }
        }
        if (scroll.getQuantity() <= 0) {
            throw new InventoryException("<= 0 quantity when scrolling");
        }
        boolean isJounin = c.getPlayer().isJounin();
        boolean isGenin = false;
        int[] scrollItems = {2040603, 2044503, 2041024, 2041025, 2044703, 2044603, 2043303,
            2040806, 2040006, 2040007, 2043103, 2043203, 2043003, 2040506, 2044403, 2040903,
            2040709, 2040710, 2040711, 2044303, 2043803, 2040403, 2044103, 2044203, 2044003,
            2043703, 2041200};
        for (int i = 0; i < scrollItems.length; i++) {
            if (c.getPlayer().isGenin() && (scroll.getItemId() == scrollItems[i])) {
                isGenin = true;
            }
        }
        int damount = c.getPlayer().getDAmount();
        IEquip scrolled = (IEquip) ii.scrollEquipWithId(toScroll, scroll.getItemId(), whiteScroll, isJounin, isGenin, damount);
        ScrollResult scrollSuccess = IEquip.ScrollResult.FAIL; // fail
        if (scrolled == null) {
            scrollSuccess = IEquip.ScrollResult.CURSE;
        } else if (scrolled.getLevel() > oldLevel || (ii.isCleanSlate(scroll.getItemId()) && scrolled.getLevel() == oldLevel + 1)) {
            scrollSuccess = IEquip.ScrollResult.SUCCESS;
        }
        useInventory.removeItem(scroll.getPosition(), (short) 1, false);
        if (whiteScroll) {
            useInventory.removeItem(wscroll.getPosition(), (short) 1, false);
            if (wscroll.getQuantity() < 1) {
                c.getSession().write(MaplePacketCreator.clearInventoryItem(MapleInventoryType.USE, wscroll.getPosition(), false));
            } else {
                c.getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, (Item) wscroll));
            }
        }
        if (scrollSuccess == IEquip.ScrollResult.CURSE) {
            c.getSession().write(MaplePacketCreator.scrolledItem(scroll, toScroll, true));

            if (dst < 0) {
                c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else {
            c.getSession().write(MaplePacketCreator.scrolledItem(scroll, scrolled, false));
        }
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getScrollEffect(c.getPlayer().getId(), scrollSuccess, legendarySpirit));

        // equipped item was scrolled and changed
        if (dst < 0 && (scrollSuccess == IEquip.ScrollResult.SUCCESS || scrollSuccess == IEquip.ScrollResult.CURSE)) {
            c.getPlayer().equipChanged();
        }
    }
}
