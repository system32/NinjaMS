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

import java.rmi.RemoteException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Acrylic (Terry Han)
 */
public class EnterCashShopHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (!player.isAlive()) {
            c.showMessage(5, "[KrystleCruz]You cannot Enter CashShop when you're dead!");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        } else if (!SpecialStuff.getInstance().canWarpFrom(player)) {
            c.showMessage(5, "[KrystleCruz]You cannot Enter CashShop from this map!");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        player.unequipAllPets();
        player.removeClones();
        try {
            WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
            wci.addBuffsToStorage(player.getId(), player.getAllBuffs());
            wci.addCooldownsToStorage(player.getId(), player.getAllCoolDowns());
        } catch (RemoteException e) {
            c.getChannelServer().reconnectWorld();
        }
        player.cancelAllBuffs();
        player.getMap().removePlayer(c.getPlayer());
        c.getSession().write(MaplePacketCreator.warpCS(c, false));
        player.setInCS(true);
        c.getSession().write(MaplePacketCreator.enableCSorMTS());
        c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
        c.getSession().write(MaplePacketCreator.sendWishList(player.getId()));
        c.getSession().write(MaplePacketCreator.getCSInventory(c.getPlayer()));
        c.getSession().write(MaplePacketCreator.getCSGifts(c.getPlayer()));
        c.getSession().write(MaplePacketCreator.enableCSUse3());
        player.saveToDB();
    }
}
