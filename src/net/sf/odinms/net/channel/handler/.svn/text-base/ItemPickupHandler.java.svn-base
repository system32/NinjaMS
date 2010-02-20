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
 * ItemPickupHandler.java
 *
 * Created on 29. November 2007, 13:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Matze
 */
public class ItemPickupHandler extends AbstractMaplePacketHandler {

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        @SuppressWarnings("unused")
        byte mode = slea.readByte(); // or something like that...but better ignore it if you want
        // mapchange to work! o.o!
        slea.readInt(); //?
        slea.readInt(); // position, but we dont need it o.o
        int oid = slea.readInt();
        MapleMapObject ob = c.getPlayer().getMap().getMapObject(oid);
        if (ob == null) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            c.getSession().write(MaplePacketCreator.getShowInventoryFull());
            return;
        }
        if (ob instanceof MapleMapItem) {
            MapleMapItem mapitem = (MapleMapItem) ob;
            synchronized (mapitem) {
                if (c.getPlayer().getMap().getEverlast()) {
                    if (mapitem.getDropper() == c.getPlayer()) {
                        if (MapleInventoryManipulator.checkSpace(c, mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), "")) {
                            if (MapleInventoryManipulator.addFromDrop(c, mapitem.getItem())) {
                                c.getPlayer().getMap().broadcastMessage(
                                        MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 2, c.getPlayer().getId()),
                                        mapitem.getPosition());
                                c.getPlayer().getCheatTracker().pickupComplete();
                                c.getPlayer().getMap().removeMapObject(ob);
                                mapitem.setPickedUp(true);
                            } else {
                                c.getPlayer().getCheatTracker().pickupComplete();
                                return;
                            }
                        } else {
                            c.getSession().write(MaplePacketCreator.getInventoryFull());
                            c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                            return;
                        }
                    } else {
                        c.getSession().write(MaplePacketCreator.getInventoryFull());
                        c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    return;
                }
                if (mapitem.isPickedUp()) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                double distance = c.getPlayer().getPosition().distanceSq(mapitem.getPosition());
                c.getPlayer().getCheatTracker().checkPickupAgain();
                if (distance > 90000.0) { // 300^2, 550 is approximatly the range of ultis
                    c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.ITEMVAC);
                } else if (distance > 22500.0) {
                    c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.SHORT_ITEMVAC);
                }
                if (mapitem.getMeso() > 0) {
                    int mesar = mapitem.getMeso();
                    if (c.getPlayer().getParty() != null && c.getPlayer().getMapId() != 30000) {
                        ChannelServer cserv = c.getChannelServer();
                        int mesosamm = mapitem.getMeso();
                        int partynum = 0;
                        for (MaplePartyCharacter partymem : c.getPlayer().getParty().getMembers()) {
                            if (partymem.isOnline() && partymem.getMapid() == c.getPlayer().getMap().getId() && partymem.getChannel() == c.getChannel()) {
                                partynum++;
                            }
                        }
                        int mesosgain = mesosamm / partynum;
                        for (MaplePartyCharacter partymem : c.getPlayer().getParty().getMembers()) {
                            if (partymem.isOnline() && partymem.getMapid() == c.getPlayer().getMap().getId()) {
                                MapleCharacter somecharacter = cserv.getPlayerStorage().getCharacterById(partymem.getId());
                                if (somecharacter != null) {
                                    int gain = Math.min(mesosgain, (Integer.MAX_VALUE - c.getPlayer().getMeso()));
                                    somecharacter.gainMeso(gain, true, true);
                                }
                            }
                        }
                    } else {
                        int gain = Math.min(mesar, (Integer.MAX_VALUE - c.getPlayer().getMeso()));
                        c.getPlayer().gainMeso(gain, true, true);
                    }
                    c.getPlayer().getMap().broadcastMessage(
                            MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 2, c.getPlayer().getId()),
                            mapitem.getPosition());
                    c.getPlayer().getCheatTracker().pickupComplete();
                    c.getPlayer().getMap().removeMapObject(ob);
                } else {
                    if (useItem(c, mapitem.getItem().getItemId())) {
                        removeItem(c.getPlayer(), mapitem, ob);
                    } else {
                        if (MapleInventoryManipulator.checkSpace(c, mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), "")) {
                            if (mapitem.getItem().getItemId() >= 5000000 && mapitem.getItem().getItemId() <= 5000100) {
                                int petId = MaplePet.createPet(mapitem.getItem().getItemId());
                                if (petId == -1) {
                                    return;
                                }
                                MapleInventoryManipulator.addById(c, mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), null, petId);
                                removeItem(c.getPlayer(), mapitem, ob);
                            } else if (MapleInventoryManipulator.addFromDrop(c, mapitem.getItem())) {
                                removeItem(c.getPlayer(), mapitem, ob);
                            } else {
                                c.getPlayer().getCheatTracker().pickupComplete();
                            }
                        } else {
                            c.getSession().write(MaplePacketCreator.getInventoryFull());
                            c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                            return;
                        }
                    }
                }
            }
        }
    }

    public boolean useItem(MapleClient c, int id) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (Items.isUse(id)) { // TO prevent caching of everything, waste of mem
            if (ii.isConsumeOnPickup(id)) {
                ii.getItemEffect(id).applyTo(c.getPlayer());
                return true;
            }

        }
        return false;
    }

    private void removeItem(MapleCharacter chr, MapleMapItem mapitem, MapleMapObject ob) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 2, chr.getId()), mapitem.getPosition());
        chr.getCheatTracker().pickupComplete();
        chr.getMap().removeMapObject(ob);
    }
}
