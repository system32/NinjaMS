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

/* Dark Lord
	Thief Job Advancement
	Victoria Road : Thieves' Hideout (103000003)
*/

var status = 0;
var job;

importPackage(net.sf.odinms.client);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if ((mode == 0 && status == 2) || (mode == 0 && status == 13)) {
			cm.sendOk("Come back once you have thought about it some more.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
				if (cm.getLevel() >= 10)
					cm.sendNext("So you decided to become a #rThief#k?");
				else {
					cm.sendOk("Train a bit more and I can show you the way of the #rThief#k.")
					cm.dispose();
				}
			} else {
				if (cm.getLevel() >= 30 && cm.getJob().equals(net.sf.odinms.client.MapleJob.THIEF)) {
					status = 10;
					cm.sendNext("The progress you have made is astonishing.");
				} else if (cm.getLevel() >= 70 && (cm.getJob().equals(net.sf.odinms.client.MapleJob.BANDIT) || cm.getJob().equals(net.sf.odinms.client.MapleJob.ASSASSIN))) {
					cm.sendOk("Please go visit #bArec#k. He resides in #bEl Nath#k.");
					cm.dispose();
				} else if (cm.getLevel() < 30 && cm.getJob().equals(net.sf.odinms.client.MapleJob.THIEF)) {
					cm.sendOk("Please come back to see me once you have trained more.");
					cm.dispose();
				} else if (cm.getLevel() >= 120 && (cm.getJob().equals(net.sf.odinms.client.MapleJob.ASSASSIN) || cm.getJob().equals(net.sf.odinms.client.MapleJob.CHIEFBANDIT))) {
					cm.sendOk("Please go visit #bHellin#k. She resides in #bLeafre#k.");
					cm.dispose();
				} else {
					cm.sendOk("You are not a #rThief#k, I have no business with you.");
					cm.dispose();
				}
			}
		} else if (status == 1) {
			cm.sendNextPrev("It is an important and final choice. You will not be able to turn back.");
		} else if (status == 2) {
			cm.sendYesNo("Do you want to become a #rThief#k?");
		} else if (status == 3) {
			if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
				cm.getPlayer().updateSingleStat(MapleStat.STR, 4, false);
				cm.getPlayer().updateSingleStat(MapleStat.DEX, 25, false);
				cm.getPlayer().updateSingleStat(MapleStat.INT, 4, false);
				cm.getPlayer().updateSingleStat(MapleStat.LUK, 4, false);
				cm.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, 33, false);
				cm.changeJob(net.sf.odinms.client.MapleJob.THIEF);
			}
			cm.sendOk("So be it! Now go, and go with pride.");
			cm.dispose();
		} else if (status == 11) {
			cm.sendNextPrev("You are now ready to take the next step as a #rAssassin#k or #rBandit#k.");
		} else if (status == 12) {
			cm.sendSimple("What do you want to become?#b\r\n#L0#Assassin#l\r\n#L1#Bandit#l#k");
		} else if (status == 13) {
			var jobName;
			if (selection == 0) {
				jobName = "Assassin";
				job = net.sf.odinms.client.MapleJob.ASSASSIN;
			} else {
				jobName = "Bandit";
				job = net.sf.odinms.client.MapleJob.BANDIT;
			}
			cm.sendYesNo("Do you want to become a #r" + jobName + "#k?");
		} else if (status == 14) {
			cm.changeJob(job);
			cm.sendOk("So be it! Now go, my servant.");
			cm.dispose();
		}
	}
}	
