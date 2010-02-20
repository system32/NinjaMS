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
package net.sf.odinms.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.odinms.client.Inventory.IItem;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.maps.AbstractMapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Matze
 */
public class MaplePlayerShop extends AbstractMapleMapObject {
    private MapleCharacter owner;
    private MapleCharacter[] visitors = new MapleCharacter[3];
    private List<MaplePlayerShopItem> items = new ArrayList<MaplePlayerShopItem>();
    private MapleCharacter[] slot = {null, null, null};
    private String description;
    private int boughtnumber = 0;
    private List<String> bannedList = new ArrayList<String>();

    public MaplePlayerShop(MapleCharacter owner, String description) {
        this.owner = owner;
        this.description = description;
    }

    public boolean hasFreeSlot() {
        return visitors[0] == null || visitors[1] == null || visitors[2] == null;
    }

    public boolean isOwner(MapleCharacter c) {
        return owner.equals(c);
    }

    public void addVisitor(MapleCharacter visitor) {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] == null) {
                visitors[i] = visitor;
                if (this.getSlot(1) == null) {
                    this.setSlot(visitor, 1);
                    this.broadcast(MaplePacketCreator.getPlayerShopNewVisitor(visitor, 1));
                } else if (this.getSlot(2) == null) {
                    this.setSlot(visitor, 2);
                    this.broadcast(MaplePacketCreator.getPlayerShopNewVisitor(visitor, 2));
                } else if (this.getSlot(3) == null) {
                    this.setSlot(visitor, 3);
                    this.broadcast(MaplePacketCreator.getPlayerShopNewVisitor(visitor, 3));
                    visitor.getMap().broadcastMessage(MaplePacketCreator.addCharBox(this.getOwner(), 1));
                }
                break;
            }
        }
    }

    public void removeVisitor(MapleCharacter visitor) {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] == visitor) {
                visitors[i] = null;
                int slot_ = visitor.getSlot();
                this.setSlot(null, slot_);
                visitor.setSlot(0);
                if (slot_ == 1) {
                    this.broadcastToVisitors(MaplePacketCreator.getPlayerShopRemoveVisitor(slot_));
                    break;
                } else if (slot_ > 1) {
                    this.broadcast(MaplePacketCreator.getPlayerShopRemoveVisitor(slot_));
                    break;
                }
                if (i == 3) {
                    visitor.getMap().broadcastMessage(MaplePacketCreator.addCharBox(this.getOwner(), 4));
                }
                break;
            }
        }
    }

    public boolean isVisitor(MapleCharacter visitor) {
        return visitors[0] == visitor || visitors[1] == visitor || visitors[2] == visitor;
    }

    public void addItem(MaplePlayerShopItem item) {
        items.add(item);
    }

    public void removeItem(int item) {
        items.remove(item);
    }

    /**
     * no warnings for now o.op
     * @param c
     * @param item
     * @param quantity
     */
    public void buy(MapleClient c, int item, short quantity) {
        if (isVisitor(c.getPlayer())) {
            MaplePlayerShopItem pItem = items.get(item);
            IItem newItem = pItem.getItem().copy();
            newItem.setQuantity((short) (newItem.getQuantity() * quantity));
            if (quantity < 1 && pItem.getBundles() < quantity && pItem.getBundles() < 1 || newItem.getQuantity() > pItem.getBundles()) {
                return;
            } else if (newItem.getType() == 1 && newItem.getQuantity() > 1) {
                return;
            }
            owner = this.getOwner();
            synchronized (c.getPlayer()) {
                if (c.getPlayer().getMeso() >= pItem.getPrice() * quantity) {
                    if (MapleInventoryManipulator.addFromDrop(c, newItem)) {
                        c.getPlayer().gainMeso(-pItem.getPrice() * quantity, true);
                        owner.gainMeso(pItem.getPrice() * quantity, true);
                        pItem.setBundles((short) (pItem.getBundles() - quantity));
                        boughtnumber++;
                        if (boughtnumber == items.size()) {
                            owner.setPlayerShop(null);
                            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeCharBox(c.getPlayer()));
                            this.removeVisitors();
                            owner.dropMessage(1, "Your items are sold out, and therefore your shop is closed.");
                        }
                    } else {
                        c.getPlayer().dropMessage(1, "Your inventory is full. Please clean a slot before buying this item.");
                    }
                }
            }
        }
    }

    public void broadcastToVisitors(MaplePacket packet) {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] != null) {
                visitors[i].getClient().getSession().write(packet);
            }
        }
    }

    public void removeVisitors() {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] != null) {
                removeVisitor(visitors[i]);
            }
            if (this.getOwner() != null) {
                removeVisitor(getOwner());
            }
        }
    }

    public void broadcast(MaplePacket packet) {
        if (owner.getClient() != null && owner.getClient().getSession() != null) {
            owner.getClient().getSession().write(packet);
        }
        broadcastToVisitors(packet);
    }

    public void chat(MapleClient c, String chat) {
        byte slot = 0;
        for (MapleCharacter mc : getVisitors()) {
            slot++;
            if (mc != null) {
                if (mc.getName().equalsIgnoreCase(c.getPlayer().getName())) {
                    break;
                }
            } else if (slot == 3) {
                slot = 0;
            }
        }
        broadcast(MaplePacketCreator.getPlayerShopChat(c.getPlayer(), chat, slot));
    }

    public void sendShop(MapleClient c) {
        c.getSession().write(MaplePacketCreator.getPlayerShop(c, this, isOwner(c.getPlayer())));
    }

    public MapleCharacter getOwner() {
        return owner;
    }

    public MapleCharacter[] getVisitors() {
        return visitors;
    }

    public MapleCharacter getSlot(int s) {
        return slot[s];
    }

    private void setSlot(MapleCharacter person, int s) {
        slot[s] = person;
        if (person != null) {
            person.setSlot(s);
        }
    }

    public List<MaplePlayerShopItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void banPlayer(String name) {
        if (!bannedList.contains(name)) {
            bannedList.add(name);
        }
        for (int i = 0; i < 3; i++) {
            if (visitors[i].getName().equals(name)) {
                visitors[i].getClient().getSession().write(MaplePacketCreator.shopErrorMessage(5, 1));
//                visitors[i].setInteraction(null);
                removeVisitor(visitors[i]);
            }
        }
    }

    public boolean isBanned(String name) {
        return bannedList.contains(name);
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SHOP;
    }
}
