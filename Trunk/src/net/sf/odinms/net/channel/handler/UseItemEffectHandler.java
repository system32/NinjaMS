package net.sf.odinms.net.channel.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class UseItemEffectHandler extends AbstractMaplePacketHandler {

    private static Logger log = LoggerFactory.getLogger(UseItemHandler.class);

    public UseItemEffectHandler() {
    }

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int itemId = slea.readInt();
        boolean mayUse = true;
        if (itemId >= 5000000 && itemId <= 5000045) {
            log.warn(slea.toString());
        }
        if (itemId != 0) {
            IItem toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(itemId);
            if (toUse == null) {
                mayUse = false;
                log.info("[h4x] Player {} is using an item he does not have: {}", c.getPlayer().getName(), Integer.valueOf(itemId));
                c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
            }
        }
        if (mayUse) {
            c.getPlayer().setItemEffect(itemId);
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.itemEffect(c.getPlayer().getId(), itemId), false);
        }
    }
}
