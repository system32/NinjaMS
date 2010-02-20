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
var setupTask;

function init() {
    scheduleNew();
}

function scheduleNew() {
    var cal = java.util.Calendar.getInstance();
    cal.set(java.util.Calendar.HOUR, 0);
    cal.set(java.util.Calendar.MINUTE, 0);
    cal.set(java.util.Calendar.SECOND, 0);
    var nextTime = cal.getTimeInMillis();
    while (nextTime <= java.lang.System.currentTimeMillis())
        nextTime += 300 * 1000;
    setupTask = em.scheduleAtTimestamp("start", nextTime);
}

function cancelSchedule() {
    setupTask.cancel(true);
}

function start() {
    scheduleNew();
    var Message = new Array("Please refrain from using foul language in this game.", 
	"Verbal and other forms of abuse will NOT be tolerated. Abusers will be blocked from the game.", 
	"FM rooms are a great place to train for beginners, go ahead and take a look!",
"Taos are the new currency of NinjaMS, buy a tao today for 2bil mesos with @buytao.",
"If you need any help, do not hesitate to smega your questions. We are here to help.",
"Many features of regular GMS are open, such as dojo, KPQ, and OPQ and LPQ.",
"To rebirth type @rebirth, but you may need some requirements before you do.",
"To try to get a max stat item, type @shuriken.",
"If you want to contribute to the server, donations are always appreciated.",
"Want to compare yourself to the greatest ninjas? @ninjatop10.",
"To get item vac, you need all four pet equips along with magic scales.",
"Kelly Rox Lar",
"Sunny Sucks lar",
"You can deny it but you are gay",
"You will be banned",
"You are a noob ninja",
"You did not see this",
"Thanks to Danner / Danny for Odinteh",
"Thanks to Odin Team for our awesome source",
"Thanks to Oliver for many non-negligible stuff ",
"Welcome to GMS like Server ./end Sarcasm",
"Type @guide to know more about our Game",
"You wasted 5.39898989898 seconds by reading this",
"To get Rasengan (GM roar), talk to Joko in Henesys.",
"Give a man a fish and he will eat for a day. Teach him how to fish, and he will sit in a boat and drink beer all day",
"Take my advice. I don't use it anyway");
    em.getChannelServer().yellowWorldMessage("[NinjaTip] " + Message[Math.floor(Math.random() * Message.length)]);
}