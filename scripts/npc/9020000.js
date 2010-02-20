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

/**
-- Odin JavaScript --------------------------------------------------------------------------------
	Lakelis - Victoria Road: Kerning City (103000000)
-- By ---------------------------------------------------------------------------------------------
	Stereo
-- Version Info -----------------------------------------------------------------------------------
	1.0 - First Version by Stereo
---------------------------------------------------------------------------------------------------
**/

var status;

function start() {
    var txt = "Hello Welcome to the Customized KPQ Of NinjaMS";
	txt += "You need to be in GMS mode 1 to be able to participate in KPQ";
	txt += "";
	status = -1;
	action(1, 0, 0);
}


function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        if (cm.getParty() == null) { // No Party
            cm.sendOk("How about you and your party members collectively beating a quest? Here you'll find obstacles and problems where you won't be able to beat it without great teamwork.  If you want to try it, please tell the #bleader of your party#k to talk to me.");
            cm.dispose();
        } else if (!cm.isLeader()) { // Not Party Leader
            cm.sendOk("If you want to try the quest, please tell the #bleader of your party#k to talk to me.");
            cm.dispose();
        } else {			            
			var party = cm.getParty().getMembers();	
			var modecheck = cm.checkPartyGMSMode(1, party);	
			var mnq = cm.membersNotQualified(1, party);
            if (!modecheck) {
				cm.sendOk("Please all your members are present and are in GMS Mode 1 to participate in this quest. Members not qualified are :" + mnq);
                cm.dispose();
			} else if (party.size() < 3 || party.size() > 4) {
                cm.sendOk("Your party is not a party of "+3 +"or "+ 4 +". Please make sure all your members are present");
                cm.dispose();
            } else {
                var em = cm.getEventManager("KerningPQ");
                if (em == null) {
                    cm.sendOk("This PQ is not currently available.");
                } else if (em.getProperty("KPQOpen").equals("true")) {
                    em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    em.setProperty("KPQOpen" , "false");
                } else {
                    cm.sendNext("There is already another party inside. Please wait !");
					if (cm.getPlayerCount(103000800) == 0 && cm.getPlayerCount(103000801) == 0  && cm.getPlayerCount(103000802) == 0  && cm.getPlayerCount(103000803) == 0  && cm.getPlayerCount(103000804) == 0  && cm.getPlayerCount(103000805) == 0) {
						em.setProperty("KPQOpen", "true");
					}
                }
                cm.dispose();
            }
        }
    }
}