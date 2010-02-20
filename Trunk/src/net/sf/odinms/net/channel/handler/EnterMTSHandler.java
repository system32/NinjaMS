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
package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class EnterMTSHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (!(c.getPlayer().isAlive())) {
            new ServernoticeMapleClientMessageCallback(5, c).dropMessage("You cannot warp to the Free Market when you're dead!");
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (!SpecialStuff.getInstance().canWarpFrom(c.getPlayer())) {
            new ServernoticeMapleClientMessageCallback(5, c).dropMessage("You cannot warp to the Free Market from this map!");
            c.getSession().write(MaplePacketCreator.enableActions());
        } else {
            if (c.getPlayer().getMapId() != 910000000) {
                new ServernoticeMapleClientMessageCallback(5, c).dropMessage("Euu are being warped to the Free Market.");
                c.getSession().write(MaplePacketCreator.enableActions());
                c.getPlayer().saveLocation("FREE_MARKET");
                c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(910000000), c.getChannelServer().getMapFactory().getMap(910000000).getPortal("out00"));
            } else {
                new ServernoticeMapleClientMessageCallback(5, c).dropMessage("You're already in the Free Market!");
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        }
    }
}
