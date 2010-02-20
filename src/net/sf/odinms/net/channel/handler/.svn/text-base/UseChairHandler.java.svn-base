package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.Clones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class UseChairHandler extends AbstractMaplePacketHandler {
	private static Logger log = LoggerFactory.getLogger(UseItemHandler.class);

	public UseChairHandler() {
	}

	    public void handlePacket(SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int itemId = slea.readInt();
        if (c.getPlayer().getInventory(MapleInventoryType.SETUP).findById(itemId) == null) {
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
            return;
        }
        c.getPlayer().setChair(itemId);
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.showChair(c.getPlayer().getId(), itemId), false);
        c.getSession().write(MaplePacketCreator.enableActions());

        if (c.getPlayer().hasClones()) {
            int i = 1;
            for (final Clones clone : c.getPlayer().getClones()) {
                TimerManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.getPlayer().getMap().broadcastMessage(clone.getClone(), MaplePacketCreator.showChair(clone.getClone().getId(), itemId), false);
                    }
                }, i * 250);
                i++;
            }
        }
    }
}