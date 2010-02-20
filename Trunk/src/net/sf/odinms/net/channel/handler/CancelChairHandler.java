package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.Clones;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CancelChairHandler extends AbstractMaplePacketHandler {

    public CancelChairHandler() {
    }

    public void handlePacket(SeekableLittleEndianAccessor slea, final MapleClient c) {
        c.getPlayer().setChair(0);
        c.getSession().write(MaplePacketCreator.cancelChair());
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
        if (c.getPlayer().hasClones()) {
            int i = 1;
            for (final Clones clone : c.getPlayer().getClones()) {
                TimerManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.getPlayer().getMap().broadcastMessage(clone.getClone(), MaplePacketCreator.showChair(clone.getClone().getId(), 0), false);
                    }
                }, i * 250);
                i++;
            }
        }
    }
}

