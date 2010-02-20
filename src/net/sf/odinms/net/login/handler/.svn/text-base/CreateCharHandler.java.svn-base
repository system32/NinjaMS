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
package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.Inventory.Equip;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CreateCharHandler extends AbstractMaplePacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreateCharHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        String name = slea.readMapleAsciiString();
        int face = slea.readInt();
        int hair = slea.readInt();
        int hairColor = slea.readInt();
        int skinColor = slea.readInt();
        int top = slea.readInt();
        int bottom = slea.readInt();
        int shoes = slea.readInt();
        int weapon = slea.readInt();
        int gender = slea.readByte();
        //ninjahax
        MapleCharacter newchar = MapleCharacter.getDefault(c);
        newchar.setName(name);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (gender == 1) {
            newchar.setFace(21000);
            newchar.setHair(31875);
            MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
            IItem eq_top = ii.getEquipById(1041002);
            eq_top.setPosition((byte) -5);
            equip.addFromDB(eq_top);
            IItem eq_bottom =ii.getEquipById(1061002);
            eq_bottom.setPosition((byte) -6);
            equip.addFromDB(eq_bottom);
            IItem eq_shoes =ii.getEquipById(1072001);
            eq_shoes.setPosition((byte) -7);
            equip.addFromDB(eq_shoes);
            IItem eq_weapon =ii.getEquipById(1302000);
            eq_weapon.setPosition((byte) -11);
            equip.addFromDB(eq_weapon);
            IItem eq_glove =ii.getEquipById(1082244);
            eq_glove.setPosition((byte) -8);
            equip.addFromDB(eq_glove);
            //ninjahax lar
            IItem eq_nxoverall =ii.getEquipById(1051061);
            eq_nxoverall.setPosition((byte) -105);
            equip.addFromDB(eq_nxoverall);
            IItem eq_nxshoes =ii.getEquipById(1072175);
            eq_nxshoes.setPosition((byte) -107);
            equip.addFromDB(eq_nxshoes);
            IItem eq_nxhat =ii.getEquipById(1001005);
            eq_nxhat.setPosition((byte) -101);
            equip.addFromDB(eq_nxhat);
            IItem eq_nxglove =ii.getEquipById(1081000);
            eq_nxglove.setPosition((byte) -108);
            equip.addFromDB(eq_nxglove);
            IItem eq_nxmask =ii.getEquipById(1011000);
            eq_nxmask.setPosition((byte) -102);
            equip.addFromDB(eq_nxmask);
            IItem eq_nxweapon =ii.makeEquipWithStats((Equip) ii.getEquipById(1702153), (short)100, (short)10, (short)10); // common
            eq_nxweapon.setPosition((byte) -111);
            eq_nxweapon.setExpiration(System.currentTimeMillis() + (120));
            equip.addFromDB(eq_nxweapon);
        } else {
            newchar.setFace(20209);
            newchar.setHair(30755);
            MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
            IItem eq_top =ii.getEquipById(1040002);
            eq_top.setPosition((byte) -5);
            equip.addFromDB(eq_top);
            IItem eq_bottom =ii.getEquipById(1060006);
            eq_bottom.setPosition((byte) -6);
            equip.addFromDB(eq_bottom);
            IItem eq_shoes =ii.getEquipById(1072001);
            eq_shoes.setPosition((byte) -7);
            equip.addFromDB(eq_shoes);
            IItem eq_weapon =ii.getEquipById(1302000);
            eq_weapon.setPosition((byte) -11);
            equip.addFromDB(eq_weapon);
            IItem eq_glove =ii.getEquipById(1082244);
            eq_glove.setPosition((byte) -8);
            equip.addFromDB(eq_glove);
            //ninjahax lar
            IItem eq_nxoverall =ii.getEquipById(1050071); // male
            eq_nxoverall.setPosition((byte) -105);
            equip.addFromDB(eq_nxoverall);
            IItem eq_nxshoes =ii.getEquipById(1072175); // common
            eq_nxshoes.setPosition((byte) -107);
            equip.addFromDB(eq_nxshoes);
            IItem eq_nxhat =ii.getEquipById(1000005); // male
            eq_nxhat.setPosition((byte) -101);
            equip.addFromDB(eq_nxhat);
            IItem eq_nxglove =ii.getEquipById(1080000); //male
            eq_nxglove.setPosition((byte) -108);
            equip.addFromDB(eq_nxglove);
            IItem eq_nxmask =ii.getEquipById(1010002); // male
            eq_nxmask.setPosition((byte) -102);
            equip.addFromDB(eq_nxmask);
            IItem eq_nxweapon =ii.makeEquipWithStats((Equip) ii.getEquipById(1702153), (short)100, (short)10, (short)10); // common
            eq_nxweapon.setPosition((byte) -111);
            eq_nxweapon.setExpiration(System.currentTimeMillis() + (120));
            equip.addFromDB(eq_nxweapon);
        }
        MapleInventory etc = newchar.getInventory(MapleInventoryType.ETC);
        etc.addItem(new Item(Items.currencyType.Sight, (byte) 0, (short) 25)); // Tao :P
        MapleInventory use = newchar.getInventory(MapleInventoryType.USE);
        use.addItem(new Item(2000015, (byte) 0, (short) 200)); // PE
        use.addItem(new Item(2022179, (byte) 1, (short) 10)); // onyx
        MapleInventory setup = newchar.getInventory(MapleInventoryType.SETUP);
        setup.addItem(new Item(3010000, (byte) 0, (short) 1));
        MapleInventory cash = newchar.getInventory(MapleInventoryType.CASH);
        cash.addItem(new Item(5072000, (byte) 0, (short) 10));
        if (MapleCharacterUtil.canCreateChar(name, c.getWorld())) {
            newchar.saveNewToDB();
            c.getSession().write(MaplePacketCreator.addNewCharEntry(newchar, true));
        } else {
            log.warn(MapleClient.getLogMessage(c, "Trying to create a character with a name: {}", name));
        }
    }
}
