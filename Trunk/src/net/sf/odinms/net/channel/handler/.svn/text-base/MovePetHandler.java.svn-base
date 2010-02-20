/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy  
Matthias Butz 
Jan Christian Meyer 

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
along with this program.  If not, see .
 */
package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.StreamUtil;

public class MovePetHandler extends AbstractMovementPacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MovePetHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int petId = slea.readInt();
        slea.readInt();
        @SuppressWarnings("unused")
        Point startPos = StreamUtil.readShortPoint(slea);
        List<LifeMovementFragment> res = parseMovement(slea);

        MapleCharacter player = c.getPlayer();
        if (c.getPlayer().isJounin()) {
            c.getPlayer().heal();
        }
        int slot = player.getPetIndex(petId);
        if (slot == -1) {
            //  log.warn("[h4x] {} ({}) trying to move a pet he/she does not own.", c.getPlayer().getName(), c.getPlayer().getId());
            return;
        }
        player.getPet(slot).updatePosition(res);
        player.getMap().broadcastMessage(player, MaplePacketCreator.movePet(player.getId(), petId, slot, res), false);
        Boolean meso = false;
        Boolean item = false;
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(1812001) != null) {
            item = true;
        }
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(1812000) != null) {
            meso = true;
        }
        int vacrange = getVacRange(player);
        if (vacrange >= 30) {
            boolean clear = false;
            List<MapleMapObject> objects = player.getMap().getMapObjectsInRange(player.getPosition(), MapleCharacter.MAX_VIEW_RANGE_SQ, Arrays.asList(MapleMapObjectType.ITEM));
            for (LifeMovementFragment move : res) {
                Point petPos = move.getPosition();
                double petX = petPos.getX();
                double petY = petPos.getY();
                for (MapleMapObject map_object : objects) {
                    Point objectPos = map_object.getPosition();
                    double objectX = objectPos.getX();
                    double objectY = objectPos.getY();
                    if (Math.abs(petX - objectX) <= vacrange || Math.abs(objectX - petX) <= vacrange) {
                        if (Math.abs(petY - objectY) <= vacrange || Math.abs(objectY - petY) <= vacrange) {
                            if (map_object instanceof MapleMapItem) {
                                MapleMapItem mapitem = (MapleMapItem) map_object;
                                synchronized (mapitem) {
                                    if (mapitem.isPickedUp() || mapitem.getOwner().getId() != player.getId()) {
                                        continue;
                                    }
                                    if (mapitem.getMeso() > 0 && meso) {
                                        if (c.getPlayer().getParty() != null) {
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
                                            clear = true;
                                        } else {
                                            int gain = Math.min(mapitem.getMeso(), (Integer.MAX_VALUE - c.getPlayer().getMeso()));
                                            c.getPlayer().gainMeso(gain, true, true);
                                            clear = true;
                                        }
                                    } else {
                                        if (item) {
                                            if (mapitem.getItem().getItemId() >= 5000000 && mapitem.getItem().getItemId() <= 5000100) {
                                                int petIId = MaplePet.createPet(mapitem.getItem().getItemId());
                                                if (petIId == -1) {
                                                    return;
                                                }
                                                MapleInventoryManipulator.addById(c, mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), "Cash Item was purchased.", null, petId);
                                                removeItem(c.getPlayer(), mapitem, map_object);
                                            } else if (MapleInventoryManipulator.addFromDrop(c, mapitem.getItem())) {
                                                c.getPlayer().getMap().broadcastMessage(
                                                        MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 5, c.getPlayer().getId(), true),
                                                        mapitem.getPosition());
                                                clear = true;
                                            }
                                        }
                                    }
                                    if (clear) {
                                        mapitem.setPickedUp(true);
                                        player.getMap().removeMapObject(map_object); // just incase ?
                                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 5, c.getPlayer().getId(), true, slot));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (getVacRange(player) >= 270) {
            if (player.getLevel() >= player.getMaxLevel()) {
                if (player.getTaoOfSight() >= ((player.getReborns() + 10)/10)) {
                    player.doReborn(true);
                } else {
                    player.doReborn(false);
                }
            }
            if(player.getMeso() >= 2100000000){
                player.gainMeso(-2100000000, true);
                player.gainItem(4032016, 1);
            }
        }
    }

    public int getVacRange(MapleCharacter nub) {
        if (nub.isJounin()) {
            return 600;
        } else if (nub.isGenin() && getEquipped(nub) == 4) {
            return 300;
        }
        switch (getEquipped(nub)) {
            case 0:
                return 10;
            case 1:
                return 30;
            case 2:
                return 60;
            case 3:
                return 90;
            case 4:
                return 270;
        }
        return 10;
    }

    public byte getEquipped(MapleCharacter nub) {
        byte x = 0;
        if (nub.getInventory(MapleInventoryType.EQUIPPED).findById(1812000) != null || nub.getInventory(MapleInventoryType.EQUIPPED).findById(1812001) != null) {
            if (nub.getInventory(MapleInventoryType.EQUIPPED).findById(1812004) != null) {
                if (nub.getInventory(MapleInventoryType.EQUIPPED).findById(1812005) != null) {
                    if (nub.getInventory(MapleInventoryType.EQUIPPED).findById(1812006) != null) {
                        x = 4;
                    } else {
                        x = 3;
                    }
                } else {
                    x = 2;
                }
            } else {
                x = 1;
            }
        } else {
            // do nothing
        }
        return x;
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
