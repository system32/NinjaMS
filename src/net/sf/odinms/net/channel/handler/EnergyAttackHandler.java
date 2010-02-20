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
package net.sf.odinms.net.channel.handler;

import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Danny
 */
public class EnergyAttackHandler extends AbstractDealDamageHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readByte();
        AttackInfo attack = new AttackInfo();
        attack.numAttackedAndDamage = slea.readByte();
        attack.numAttacked = (attack.numAttackedAndDamage >>> 4) & 0xF;
        attack.numDamage = attack.numAttackedAndDamage & 0xF;
        attack.skill = slea.readInt();
        slea.readInt();
        attack.stance = slea.readByte();
        attack.direction = slea.readByte();
        slea.readByte();
        attack.speed = slea.readByte();
        slea.readInt();
        slea.readInt();
        attack.allDamage = new ArrayList<Pair<Integer, List<Integer>>>();
        for (int i = 0; i < attack.numAttacked; i++) {
            int oid = slea.readInt();
            slea.readByte();
            slea.skip(13);
            List<Integer> allDamageNumbers = new ArrayList<Integer>();
            for (int j = 0; j < attack.numDamage; j++) {
                allDamageNumbers.add(slea.readInt());
            }
            attack.allDamage.add(new Pair<Integer, List<Integer>>(oid, allDamageNumbers));
        }
        int maxdamage = c.getPlayer().getCurrentMaxBaseDamage();
        MapleStatEffect effect = attack.getAttackEffect(c.getPlayer());
        if (effect != null) {
            maxdamage *= effect.getDamage() / 100.0;
        }

        if (!c.getPlayer().isChunin()) {
            if (SpecialStuff.getInstance().isSkillBlocked(c.getPlayer().getMapId())) {
                c.showMessage(5, "[KrytleCruz]you cannot use skills in this map");
                return;
            }
        } else {
            applyAttack(attack, c.getPlayer(), maxdamage, 1);
        }
    }
}
