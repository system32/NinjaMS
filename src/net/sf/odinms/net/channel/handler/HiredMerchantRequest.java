/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import java.util.Arrays;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Owner
 */
public final class HiredMerchantRequest extends AbstractMaplePacketHandler {
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 23000, Arrays.asList(MapleMapObjectType.MERCHANT)).size() == 0 && c.getPlayer().getMapId() > 910000000 && c.getPlayer().getMapId() < 910000023) {
            if (!c.getPlayer().hasMerchant()) {
                c.getSession().write(MaplePacketCreator.hiredMerchantBox());
            } else {
                c.getPlayer().dropMessage(1, "You already have a store open.");
            }
        } else {
            c.getPlayer().dropMessage(1, "You cannot open your hired merchant here.");
        }
    }
}
