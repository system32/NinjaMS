/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Owner
 */
public class PassiveEnergyHandler extends AbstractMaplePacketHandler {
    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (slea.available() != 0) {
            slea.readByte(); // 02
            slea.readInt(); // 11 F1 F8 4D
            slea.readShort(); // 00 00
            slea.readByte(); // 00
            slea.readShort(); // 08 00
            slea.readInt(); // D7 8A 11 00
            int oid = slea.readInt(); // 6B 00 00 00
            slea.readInt(); // 06 80 05 05
            slea.readShort(); // D4 00
            slea.readShort(); // D7 00
            slea.readShort(); // D6 00
            slea.readInt(); // D7 00 00 00
            int damage = slea.readInt(); // 9F 86 01 00
            slea.readInt(); // CA 98 39 DA
            slea.readShort(); // C5 00
            slea.readShort(); // D7 00

            MapleMonster attacker = (MapleMonster) c.getPlayer().getMap().getMapObject(oid);
            c.getPlayer().getMap().damageMonster(c.getPlayer(), attacker, damage);
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.damageMonster(oid, damage), false, true);
        }
    }
}  