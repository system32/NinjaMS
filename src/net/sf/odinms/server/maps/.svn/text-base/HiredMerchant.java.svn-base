/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server.maps;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import net.sf.odinms.client.Inventory.Equip;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MaplePlayerShopItem;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Owner
 */
public class HiredMerchant extends AbstractMapleMapObject {
    private int ownerId;
    private int itemId;
    private String ownerName = "";
    private String description = "";
    private MapleCharacter[] visitors = new MapleCharacter[3];
    private List<MaplePlayerShopItem> items = new LinkedList<MaplePlayerShopItem>();
    private boolean open;
    public ScheduledFuture<?> schedule = null;
    private MapleMap map;

    public HiredMerchant(final MapleCharacter owner, int itemId, String desc) {
        this.setPosition(owner.getPosition());
        this.ownerId = owner.getId();
        this.itemId = itemId;
        this.ownerName = owner.getName();
        this.description = desc;
        this.map = owner.getMap();
        this.schedule = TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                HiredMerchant.this.closeShop(owner.getClient());
            }
        }, 1000 * 60 * 60 * 24);
    }

    public void broadcastToVisitors(MaplePacket packet) {
        for (MapleCharacter visitor : visitors) {
            if (visitor != null) {
                visitor.getClient().getSession().write(packet);
            }
        }
    }

    public void addVisitor(MapleCharacter visitor) {
        int i = this.getFreeSlot();
        if (i > -1) {
            visitors[i] = visitor;
            broadcastToVisitors(MaplePacketCreator.hiredMerchantVisitorAdd(visitor, i + 1));
        }
    }

    public void removeVisitor(MapleCharacter visitor) {
        int slot = getVisitorSlot(visitor);
        if (visitors[slot] == visitor) {
            visitors[slot] = null;
            broadcastToVisitors(MaplePacketCreator.hiredMerchantVisitorLeave(slot + 1, false));
        }
    }

    public int getVisitorSlot(MapleCharacter visitor) {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] == visitor) {
                return i;
            }
        }
        return 1;
    }

    public void removeAllVisitors(String message) {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] != null) {
                visitors[i].getClient().getSession().write(MaplePacketCreator.hiredMerchantForceLeave1());
                visitors[i].getClient().getSession().write(MaplePacketCreator.hiredMerchantForceLeave2());
                if (message.length() > 0) {
                    visitors[i].dropMessage(1, message);
                }
                visitors[i] = null;
            }
        }
    }

    public void buy(MapleClient c, int item, short quantity) {
        MaplePlayerShopItem pItem = items.get(item);
        synchronized (items) {
            IItem newItem = pItem.getItem().copy();
            newItem.setQuantity((short) (newItem.getQuantity() * quantity));
            if (quantity < 1 && pItem.getBundles() < quantity && pItem.getBundles() < 1 || newItem.getQuantity() > pItem.getBundles()) {
                return;
            } else if (newItem.getType() == 1 && newItem.getQuantity() > 1) {
                return;
            }
            if (c.getPlayer().getMeso() >= pItem.getPrice() * quantity) {
                if (MapleInventoryManipulator.addFromDrop(c, newItem)) {
                    c.getPlayer().gainMeso(-pItem.getPrice() * quantity, false);
                    try {
                        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET MerchantMesos = MerchantMesos + " + pItem.getPrice() * quantity + " WHERE id = ?");
                        ps.setInt(1, ownerId);
                        ps.executeUpdate();
                        ps.close();
                    } catch (Exception e) {
                    }
                    pItem.setBundles((short) (pItem.getBundles() - quantity));
                    if (pItem.getBundles() < 1) {
                        pItem.setDoesExist(false);
                    }
                } else {
                    c.getPlayer().dropMessage(1, "Your inventory is full. Please clean a slot before buying this item.");
                }
            } else {
                c.getPlayer().dropMessage(1, "You do not have enough mesos.");
            }
        }
    }

    public void closeShop(MapleClient c) {
        map.removeMapObject(this);
        map.broadcastMessage(MaplePacketCreator.destroyHiredMerchant(ownerId));
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET HasMerchant = 0 WHERE id = ?");
            ps.setInt(1, ownerId);
            ps.executeUpdate();
            ps.close();
            for (MaplePlayerShopItem mpsi : getItems()) {
                if (mpsi.getBundles() > 2) {
                    MapleInventoryManipulator.addById(c, mpsi.getItem().getItemId(), (short) mpsi.getBundles(), null, -1);
                } else if (mpsi.isExist()) {
                    MapleInventoryManipulator.addFromDrop(c, mpsi.getItem());
                }
            }
        } catch (Exception e) {
        }
        schedule.cancel(false);
    }

    public String getOwner() {
        return ownerName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getDescription() {
        return description;
    }

    public MapleCharacter[] getVisitors() {
        return visitors;
    }

    public List<MaplePlayerShopItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(MaplePlayerShopItem item) {
        items.add(item);
    }

    public void removeFromSlot(int slot) {
        items.remove(slot);
    }

    public int getFreeSlot() {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean set) {
        this.open = set;
    }

    public int getItemId() {
        return itemId;
    }

    public boolean isOwner(MapleCharacter chr) {
        return chr.getId() == ownerId;
    }

    public void saveItems() throws SQLException {
        PreparedStatement ps;
        for (MaplePlayerShopItem pItems : items) {
            if (pItems.getBundles() > 0) {
                if (pItems.getItem().getType() == 1) {
                    ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO hiredmerchant " +
                            "(ownerid, itemid, quantity, upgradeslots, level," +
                            " str, dex, `int`, luk, hp," +
                            " mp, watk, matk, wdef, mdef," +
                            " acc, avoid, hands, speed, jump," +
                            " ring, hammers, owner, type) VALUES " +
                            "(?, ?, ?, ?, ?," +
                            " ?, ?, ?, ?, ?," +
                            " ?, ?, ?, ?, ?," +
                            " ?, ?, ?, ?, ?," +
                            " ?, ?, ?, 1)");
                    Equip eq = (Equip) pItems.getItem();
                    ps.setInt(2, eq.getItemId());
                    ps.setInt(3, 1);
                    ps.setInt(4, eq.getUpgradeSlots());
                    ps.setInt(5, eq.getLevel());
                    ps.setInt(6, eq.getStr());
                    ps.setInt(7, eq.getDex());
                    ps.setInt(8, eq.getInt());
                    ps.setInt(9, eq.getLuk());
                    ps.setInt(10, eq.getHp());
                    ps.setInt(11, eq.getMp());
                    ps.setInt(12, eq.getWatk());
                    ps.setInt(13, eq.getMatk());
                    ps.setInt(14, eq.getWdef());
                    ps.setInt(15, eq.getMdef());
                    ps.setInt(16, eq.getAcc());
                    ps.setInt(17, eq.getAvoid());
                    ps.setInt(18, eq.getHands());
                    ps.setInt(19, eq.getSpeed());
                    ps.setInt(20, eq.getJump());                    
                    ps.setInt(21, eq.getRingId());
                    ps.setInt(22, eq.getHammers());
                    ps.setString(23, eq.getOwner());
                } else {
                    ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO hiredmerchant (ownerid, itemid, quantity, owner, type) VALUES (?, ?, ?, ?, 0)");
                    ps.setInt(2, pItems.getItem().getItemId());
                    ps.setInt(3, pItems.getBundles());
                    ps.setString(4, pItems.getItem().getOwner());
                }
                ps.setInt(1, getOwnerId());
                ps.executeUpdate();
                ps.close();
            }
        }
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        return;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.MERCHANT;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.spawnHiredMerchant(this));
    }
}

