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

/* 
	Machine Apparatus
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple("010010000110010101101100011011000110111100101110 010010000110111101110111 011000110110000101101110 01001001 0110100001100101011011000111000000111111\r\n#L1##bCrack#k please! (1,000,000 mesos)#l\r\n\#L2#Take me to #bLudibrium#k!#l\r\n\#L3#Take me to #bDeep Inside The Clocktower#k!#l");
		} else if (status == 1) {
			if (selection == 1) {
				if(cm.getMeso() >= 1000000) {
					cm.gainMeso(-1000000);
					cm.gainItem(4031179, 1);
					cm.sendOk("010001010110111001101010011011110111100100100001");
				} else {
					cm.sendOk("010011100110111101110100 011001010110111001101111011101010110011101101000 011011010110010101110011011011110111001100100001");
				}
				cm.dispose();
			} else if (selection == 2) {
				cm.resetReactors();
				cm.warp(220000000, 0);
				cm.dispose();
			} else if (selection == 3) {
				cm.resetReactors();
				cm.warp(220080000, 0);
				cm.dispose();
			}
		}
	}
}
