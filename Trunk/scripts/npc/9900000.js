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

/* KIN
	Male Hair for GM.
*/
var status = 0;
var beauty = 0;
var haircolor = Array();
var skin = Array(0, 1, 2, 3, 4, 5, 9, 10);
var hair = Array(30000, 30020, 30030, 30040, 30050, 30060, 30100, 30110, 30120, 30130, 30140, 30150, 30160, 30170, 30180, 30190, 30200, 30210, 30220, 30230, 30240, 30250, 30260, 30270, 30280, 30290, 30300, 30310, 30320, 30330, 30340, 30350, 30360, 30370, 30400, 30410, 30420, 30430, 30440, 30450, 30460, 30470, 30480, 30490, 30510, 30520, 30530, 30540, 30550, 30560, 30570, 30580, 30590, 30600, 30610, 30620, 30630, 30640, 30650, 30660, 30670, 30680, 30690, 30700, 30710, 30720, 30730, 30740, 30750, 30760, 30770, 30780, 30790, 30800, 30810, 30820, 30830, 30840, 30850, 30860, 30870, 30880, 30890, 30900, 30910, 30920);
var hairnew = Array();
var face = Array(20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009, 20010, 20011, 20012, 20013, 20014, 20015, 20016, 20017, 20018, 20019, 20020, 20021, 20022, 20023, 20024, 20025, 20026, 20027, 20028);
var facenew = Array();
var colors = Array();

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
				if(cm.getPlayer().getGender() != 1 || cm.getPlayer().isGenin()) {
					cm.sendSimple("Hey there! I could change the way you look! What would you like to change?\r\n#L0#Skin#l\r\n#L1#Hair#l\r\n#L2#Hair Color#l\r\n#L3#Eye#l\r\n#L4#Eye Color#l");									
				} else {
					cm.sendOk("Currently I am only serving males, but you should try NimaKIN!");
					cm.dispose();	
				}
			//}
		} else if (status == 1) {
			if (selection == 0) {
				beauty = 1;
				cm.sendStyle("Pick one?", skin);
			} else if (selection == 1) {
				beauty = 2;
				hairnew = Array();
				for(var i = 0; i < hair.length; i++) {
					hairnew.push(hair[i] + parseInt(cm.getChar().getHair() % 10));
				}
				cm.sendStyle("Pick one?", hairnew);
			} else if (selection == 2) {
				beauty = 3;
				haircolor = Array();
				var current = parseInt(cm.getChar().getHair()/10)*10;
				for(var i = 0; i < 8; i++) {
					haircolor.push(current + i);
				}
				cm.sendStyle("Pick one?", haircolor);
			} else if (selection == 3) {
				beauty = 4;
				facenew = Array();
				for(var i = 0; i < face.length; i++) {
					facenew.push(face[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
				}
				cm.sendStyle("Pick one?", facenew);
			} else if (selection == 4) {
				beauty = 5;
				var current = cm.getChar().getFace() % 100 + 20000;
				colors = Array();
				colors = Array(current , current + 100, current + 200, current + 300, current +400, current + 500, current + 600, current + 700);
				cm.sendStyle("Pick one?", colors);
			}
		}
		else if (status == 2){
			cm.dispose();
			if (beauty == 1){
				cm.setSkin(skin[selection]);
			}
			if (beauty == 2){
				cm.setHair(hairnew[selection]);
			}
			if (beauty == 3){
				cm.setHair(haircolor[selection]);
			}
			if (beauty == 4){
				cm.setFace(facenew[selection]);
			}
			if (beauty == 5){
				cm.setFace(colors[selection]);
			}
		}
	}
}
