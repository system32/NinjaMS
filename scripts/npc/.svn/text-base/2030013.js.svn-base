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
	Adobis
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
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            if (cm.getParty() == null){
                cm.sendOk("Sorry but you are not in a party. To take down a boss like Zakum, you should be in a party.");
                cm.dispose();
            } else if (cm.getPlayerCount(280030000) == 0) {
                cm.sendSimple("Would you like to assemble a team to take on the mighty Zakum?\r\n#b#L1#Lets get this going!#l\r\n\#L2#No, I think I'll wait a bit...#l");
                cm.killMobsInMap(280030000);
                cm.resetReactors(280030000);
            } else {
                cm.sendOk("Sorry but The Boss fight seems to have already started. Try again next time.");
                cm.dispose();
            }
        } else if (status == 1) {
            var party = cm.getParty().getMembers();
            var mapId = cm.getChar().getMapId();
            var it = party.iterator();
            var inMap = 0;
            var next = true;
            while (it.hasNext()) {
                var cPlayer = it.next();
                if (cPlayer.getMapid() == mapId) {
                    inMap += 1;
                } else {
                    next = false;
                }
            }
			
            if (inMap < party.size()){
                next = false;
            }
            if (next){
                cm.warpParty(280030000);
                cm.dispose();
            } else {
                cm.sendOk("Your party does not have 2 memebers or they are not in this map. I see " + inMap + " members in this map.");
                cm.dispose();
            }
        }
    }
}
