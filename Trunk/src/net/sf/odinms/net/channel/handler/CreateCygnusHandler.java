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

import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Enums.MapleJob;
import net.sf.odinms.client.Enums.MapleSkinColor;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CreateCygnusHandler extends AbstractMaplePacketHandler {
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

        int str = 12;
        int dex = 5;
        int _int = 4;
        int luk = 4;

        boolean charok = true;

        if (!MapleCharacterUtil.canCreateChar(name, c.getWorld())) {
            c.getSession().write(MaplePacketCreator.sendCygnusMessage(1));
            return;
        } else if (c.loadCharacters(c.getWorld()).size() >= 6) {
            c.getSession().write(MaplePacketCreator.sendCygnusMessage(2));
            return;
		} else if (c.getPlayer().getChildId() != 0) {
			return;
        } else if (c.getPlayer().getJob().getId() >= 1000) {
            return;
        }

        if (gender == 0) {
            if (face != 20000 && face != 20001 && face != 20002) {
                charok = false;
            }
            if (hair != 30000 && hair != 30020 && hair != 30030) {
                charok = false;
            }
            if (top != 1040002 && top != 1040006 && top != 1040010) {
                charok = false;
            }
            if (bottom != 1060006 && bottom != 1060002) {
                charok = false;
            }
        } else if (gender == 1) {
            if (face != 21000 && face != 21001 && face != 21002) {
                charok = false;
            }
            if (hair != 31000 && hair != 31040 && hair != 31050) {
                charok = false;
            }
            if (top != 1041002 && top != 1041006 && top != 1041010 && top != 1041011) {
                charok = false;
            }
            if (bottom != 1061002 && bottom != 1061008) {
                charok = false;
            }
        } else {
            charok = false;
        }
        if (skinColor < 0 || skinColor > 3) {
            charok = false;
        }
        if (weapon != 1302000 && weapon != 1322005 && weapon != 1312004) {
            charok = false;
        }
        if (shoes != 1072001 && shoes != 1072005 && shoes != 1072037 && shoes != 1072038) {
            charok = false;
        }
        if (hairColor != 0 && hairColor != 2 && hairColor != 3 && hairColor != 7) {
            charok = false;
        }

        MapleCharacter newchar = MapleCharacter.getDefault(c);

        if (charok) {
            newchar.setWorld(c.getWorld());
            newchar.setFace(face);
            newchar.setHair(hair + hairColor);
            newchar.setStr(str);
            newchar.setDex(dex);
            newchar.setInt(_int);
            newchar.setLuk(luk);
            newchar.setName(name);
            newchar.setSkinColor(MapleSkinColor.getById(skinColor));
            newchar.setMap(c.getChannelServer().getMapFactory().getMap(130000000));
            newchar.setJob(MapleJob.NOBLESSE);
			newchar.setParentId(c.getPlayer().getId());

            MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
            IItem eq_top = MapleItemInformationProvider.getInstance().getEquipById(top);
            eq_top.setPosition((byte) -5);
            equip.addFromDB(eq_top);
            IItem eq_bottom = MapleItemInformationProvider.getInstance().getEquipById(bottom);
            eq_bottom.setPosition((byte) -6);
            equip.addFromDB(eq_bottom);
            IItem eq_shoes = MapleItemInformationProvider.getInstance().getEquipById(shoes);
            eq_shoes.setPosition((byte) -7);
            equip.addFromDB(eq_shoes);
            IItem eq_weapon = MapleItemInformationProvider.getInstance().getEquipById(weapon);
            eq_weapon.setPosition((byte) -11);
            equip.addFromDB(eq_weapon);

            MapleInventory etc = newchar.getInventory(MapleInventoryType.ETC);
            etc.addItem(new Item(4161001, (byte) 0, (short) 1));

            newchar.saveNewToDB();
			c.getPlayer().setChildId(MapleCharacter.getIdByName(newchar.getName(), c.getWorld()));
            c.getSession().write(MaplePacketCreator.sendCygnusMessage(0));
        }
	}
}
