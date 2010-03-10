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

/*  Legor
	Bowman 4th job advancement
	Leafre : Forest of the Priest (240010501)
*/

importPackage(net.sf.odinms.client);

var status = 0;
var job;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.sendOk("Make up your mind and visit me again.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			if (!(cm.getJob().equals(MapleJob.RANGER) || cm.getJob().equals(MapleJob.SNIPER))) {
				cm.sendOk("...");
				cm.dispose();
				return;
			}
			if ((cm.getJob().equals(MapleJob.RANGER) || cm.getJob().equals(MapleJob.SNIPER)) && cm.getLevel() >= 120 &&  cm.getChar().getRemainingSp() <= (cm.getLevel() - 120) * 3) {
				cm.sendYesNo("You have come so far, my child.\r\n\r\nAre you ready to access untold power?");
			} else {
				cm.sendOk("Your time has yet to come...");
				cm.dispose();
			}
		} else if (status == 1) {
			if (cm.getJob().equals(MapleJob.RANGER)) {
				cm.changeJob(MapleJob.BOWMASTER);
				cm.teachSkill(3121003,0,30);
				cm.teachSkill(3121004,0,30);
				cm.teachSkill(3121006,0,30);
				cm.teachSkill(3121008,0,30);
				cm.teachSkill(3121009,0,1);
				cm.teachSkill(3120005,0,30);
				cm.teachSkill(3121007,0,30);
				cm.teachSkill(3121000,0,30);
				cm.teachSkill(3121002,0,30);
				cm.getChar().gainAp(5);
				cm.sendOk("You are now a #bBowmaster#k.\r\n\r\nNow go, with pride!");
				cm.dispose();
			} else if (cm.getJob().equals(MapleJob.SNIPER)) {
				cm.changeJob(MapleJob.CROSSBOWMASTER);
				cm.teachSkill(3221001,0,30);
				cm.teachSkill(3221003,0,30);
				cm.teachSkill(3221005,0,30);
				cm.teachSkill(3221007,0,30);
				cm.teachSkill(3221002,0,30);
				cm.teachSkill(3221008,0,1);
				cm.teachSkill(3221006,0,30);
				cm.teachSkill(3220004,0,30);
				cm.teachSkill(3221000,0,30);
				cm.getChar().gainAp(5);
				cm.sendOk("You are now a #bMarksman#k.\r\n\r\nNow go, with pride!");
				cm.dispose();
			}
		}
	}
}	
