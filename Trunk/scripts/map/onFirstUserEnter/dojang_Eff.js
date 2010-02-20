/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 *@Author:     Moogra, Traitor
 *@Map(s):     All Dojo fighting maps
 *@Function:   Spawns dojo monsters and handles time
*/
/*
importPackage(Packages.server.life);
importPackage(Packages.tools);

function start(ms) {
    try {
        ms.getPlayer().resetEnteredScript();
        var stage = (ms.getPlayer().getMap().getId() / 100) % 100;
        if (stage % 6 == 1)
            ms.getPlayer().setDojoStart();
        if (ms.getPlayer().getMap().getCharacters().size() == 1)
            ms.getPlayer().showDojoClock();
        if (stage % 6 > 0) {
            var realstage = stage - ((stage / 6) | 0);
            ms.getClient().getSession().write(MaplePacketCreator.playSound("Dojang/start"));
            ms.getClient().getSession().write(MaplePacketCreator.showEffect("dojang/start/stage"));
            ms.getClient().getSession().write(MaplePacketCreator.showEffect("dojang/start/number/" + realstage));
            ms.getClient().getSession().write(MaplePacketCreator.getEnergy(ms.getPlayer().getDojoEnergy()));
            var mob = MapleLifeFactory.getMonster(9300183 + realstage);
            if (mob != null && ms.getPlayer().getMap().getMonsterById(9300183 + realstage) == null) {
                mob.setBoss(false);
                ms.getPlayer().getMap().spawnDojoMonster(mob);
            }
        }
    } catch(err) {
        ms.getPlayer().dropMessage(err);
    }
}
*/
function start(ms) {
    ms.getPlayer().resetEnteredScript();
    ms.getPlayer().setDojoStage(0);
    var stage = Math.floor(ms.getPlayer().getMap().getId() / 100) % 100;
    if (stage % 6 > 0) {
        var realstage = stage - Math.floor((stage / 6));
        ms.dojang_Eff(realstage);
    }
}
