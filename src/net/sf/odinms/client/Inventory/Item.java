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
package net.sf.odinms.client.Inventory;

public class Item implements IItem {

    private int id;
    private byte position;
    private short quantity;
    private int petid = -1;
    private long expiration = -1;
    private String owner = "";
        private byte flag = 0;

   public Item(int id, byte position, short quantity) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.petid = -1;
    }

    public Item(int id, byte position, short quantity, int petid) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.petid = petid;
    }

    public Item(int id, byte position, short quantity, int petid, long expiration) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.petid = petid;
        this.expiration = expiration;
    }

    public IItem copy() {
        Item ret = new Item(id, position, quantity, petid, expiration);
        ret.owner = owner;
        ret.flag = flag;
        return ret;
    }

    // gets
    @Override
    public int getItemId() {
        return id;
    }

    @Override
    public byte getPosition() {
        return position;
    }

    @Override
    public short getQuantity() {
        return quantity;
    }

    @Override
    public byte getType() {
        return IItem.ITEM;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public int getUniqueId() {
        return petid;
    }

     @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public byte getFlag() {
        return flag;
    }

    // void
    @Override
    public void setPosition(byte position) {
        this.position = position;
    }

    @Override
    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public void setExpiration(long expire) {
        this.expiration = expire;
    }

    @Override
    public void setUniqueId(int id){
        this.petid = id;
    }
    
    @Override
    public void setFlag(byte fff) {
        this.flag = fff;
    }

    // lol scam!
    @Override
    public int compareTo(IItem other) {
        if (Math.abs(position) < Math.abs(other.getPosition())) {
            return -1;
        } else if (Math.abs(position) == Math.abs(other.getPosition())) {
            return 0;
        } else {
            return 1;
        }
    }
}

