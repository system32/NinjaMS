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

importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.client.messages);
importPackage(net.sf.odinms.net.channel);
importPackage(net.sf.odinms.server);
importPackage(net.sf.odinms.tools);

function getDefinition () {
	var ret = java.lang.reflect.Array.newInstance(CommandDefinition, 1);
	ret[0] = new CommandDefinition("exprate", "rate", "Sets the experience rate.", "100"); 
	return ret;
}

function execute (c, mc, splitted) {
	if (splitted.length != 2) {
		mc.dropMessage("Syntax: !exprate <rate>");
	} else {
		var exp = splitted[1];
		var packet = MaplePacketCreator.serverNotice(6, "Exprate has been changed to " + exp + "x");
		ChannelServer.getInstance(1).setExpRate(exp);
		ChannelServer.getInstance(2).setExpRate(exp);
		ChannelServer.getInstance(3).setExpRate(exp);
		ChannelServer.getInstance(4).setExpRate(exp);
		ChannelServer.getInstance(1).broadcastPacket(packet);
		ChannelServer.getInstance(2).broadcastPacket(packet);
		ChannelServer.getInstance(3).broadcastPacket(packet);
		ChannelServer.getInstance(4).broadcastPacket(packet);
	}
}