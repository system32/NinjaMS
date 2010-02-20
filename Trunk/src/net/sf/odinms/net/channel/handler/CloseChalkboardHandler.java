/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.Clones;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class CloseChalkboardHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getMute() != 4) {
            c.getPlayer().setChalkboard(null);
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.useChalkboard(c.getPlayer(), true));

            for (Clones clone : c.getPlayer().getClones()) {
                clone.getClone().setChalkboard(null);
                clone.getClone().getMap().broadcastMessage(MaplePacketCreator.useChalkboard(clone.getClone(), true));
            }
        } else {
            c.showMessage("You cannot remove your chalkboard :D!");
        }
    }
}
