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
package net.sf.odinms.client.Enums;

import net.sf.odinms.net.LongValueHolder;

public enum MapleDisease implements LongValueHolder {

    NULL(0x0),
    SLOW(0x1), // 01 00 00 00 00 00 00 00 0x1
    SEDUCE(0x80),
    ZOMBIFY(0x4000), // Oliver
    FISHABLE(0x100), // Oliver
    CRAZY_SKULL(0x80000), // Oliver
    STUN(0x2000000000000L), // 00 00 00 00 00 00 02 00 0x2000000000000L
    POISON(0x4000000000000L), // 00 00 00 00 00 00 04 00 0x4000000000000L
    SEAL(0x8000000000000L), // 00 00 00 00 00 00 08 00 0x8000000000000L
    DARKNESS(0x10000000000000L), // 00 00 00 00 00 00 10 00 0x10000000000000L
    WEAKEN(0x4000000000000000L), // 00 00 00 00 00 00 00 40 0x4000000000000000L
    CURSE(0x8000000000000000L);
    private long i;

    private MapleDisease(long i) {
        this.i = i;
    }

    @Override
    public long getValue() {
        return i;
    }

    public static MapleDisease getType(int skill) {
        switch (skill) {
            case 120:
                return MapleDisease.SEAL;
            case 121:
                return MapleDisease.DARKNESS;
            case 122:
                return MapleDisease.WEAKEN;
            case 123:
                return MapleDisease.STUN;
            case 125:
                return MapleDisease.POISON;
            case 128:
                return MapleDisease.SEDUCE;
            case 133:
                return MapleDisease.CRAZY_SKULL;
            case 134:
                return MapleDisease.ZOMBIFY;
            default:
                return null;
        }
    }
}
