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

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import net.sf.odinms.client.Clones;
import net.sf.odinms.client.ExpTable;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.NinjaMS.Processors.SmegaProcessor;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.constants.Items;

public class UseCashItemHandler extends AbstractMaplePacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UseCashItemHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        c.getSession().write(MaplePacketCreator.enableActions());
        //@SuppressWarnings("unused")
        //byte mode = slea.readByte();
        slea.readByte();
        slea.readByte();
        int itemId = slea.readInt();
        IItem item = c.getPlayer().getInventory(MapleInventoryType.CASH).findById(itemId);
        if (item == null) {
            c.disconnect();
            return;
        }
        try {
            if (Items.Cash.isSPReset(itemId)) {
                c.showMessage("Why???? The skills are automaxxed. What chu need this for?????");
            } else if (itemId == Items.Cash.APReset) {
                c.showMessage("Why???? use @reset <str/dex/int/luk> instead");
            } else if (itemId == Items.Cash.Megaphone) {
                String message = slea.readMapleAsciiString();
                c.getChannelServer().broadcastPacket(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.MEGAPHONE, c.getChannel(), message, null, true));
            } else if (itemId == Items.Cash.SuperMegaphone) {
                String message = slea.readMapleAsciiString();
                boolean showEar = slea.readByte() > 0;
                SmegaProcessor.smegaProcessor(Items.MegaPhoneType.SUPERMEGAPHONE, c, message, null, showEar);
            } else if (itemId == Items.Cash.TripleMegaphone) {
                byte numLines = slea.readByte();
                String[] message = {"", "", ""};
                for (int i = 0; i < numLines; i++) {
                    String msg = slea.readMapleAsciiString();
                    message[i] = c.getPlayer().getName() + " : " + msg;
                }
                slea.readByte();
                SmegaProcessor.tripleMegaProcessor(c, message, numLines);
            } else if (itemId == Items.Cash.ItemMegaphone) {
                String append = slea.readMapleAsciiString();
                if (append.length() > 100) {
                    append = "I suck Cock";
                }
                String message = append;
                boolean showEar = slea.readByte() == 1;
                IItem megaitem = null;
                if (slea.readByte() == 1) {
                    int invtype = slea.readInt();
                    int slotno = slea.readInt();
                    megaitem = c.getPlayer().getInventory(MapleInventoryType.getByType((byte) invtype)).getItem((byte) slotno);
                }
                SmegaProcessor.smegaProcessor(Items.MegaPhoneType.ITEMMEGAPHONE, c, message, megaitem, showEar);
            } else if (Items.Cash.isAvatarMega(itemId)) {
                List<String> lines = new LinkedList<String>();
                for (int i = 0; i < 4; i++) {
                    lines.add(slea.readMapleAsciiString());
                }
                SmegaProcessor.asmegaProcessor(c, itemId, lines);
            } else if (itemId / 1000 == 512) {
                c.getPlayer().getMap().startMapEffect(ii.getMsg(itemId).replaceFirst("%s", c.getPlayer().getName()).replaceFirst("%s", slea.readMapleAsciiString()), itemId);
            } else if (Items.Cash.isPetFood(itemId)) {
                for (int i = 0; i < 3; i++) {
                    MaplePet pet = c.getPlayer().getPet(i);
                    if (pet == null) {
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                    if (pet.canConsume(itemId)) {
                       // pet.setFullness(100);
                        int closeGain = 100 * c.getChannelServer().getPetExpRate();
                        if (pet.getCloseness() < 30000) {
                            if (pet.getCloseness() + closeGain > 30000) {
                                pet.setCloseness(30000);
                            } else {
                                pet.setCloseness(pet.getCloseness() + closeGain);
                            }
                            while (pet.getCloseness() >= ExpTable.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                                pet.setLevel(pet.getLevel() + 1);
                                c.getSession().write(MaplePacketCreator.showOwnPetLevelUp(c.getPlayer().getPetIndex(pet)));
                                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showPetLevelUp(c.getPlayer(), c.getPlayer().getPetIndex(pet)));
                            }
                        }
                        c.getSession().write(MaplePacketCreator.updatePet(pet));
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);
                        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.commandResponse(c.getPlayer().getId(), (byte) 1, 0, true, true), true);
                        break;
                    }
                }
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            } else if (itemId == Items.Cash.PetNameTag) {
                MaplePet pet = c.getPlayer().getPet(0);
                if (pet == null) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                String newName = slea.readMapleAsciiString();
                pet.setName(newName);
                c.getSession().write(MaplePacketCreator.updatePet(pet));
                c.getSession().write(MaplePacketCreator.enableActions());
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.changePetName(c.getPlayer(), newName, 1), true);
            } else if (itemId == Items.Cash.Chalkboard1 || itemId == Items.Cash.Chalkboard2) {
                if (c.getPlayer().getMute() == 0) {
                    String text = slea.readMapleAsciiString();
                    c.getPlayer().setChalkboard(text);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.useChalkboard(c.getPlayer(), false));
                    for (Clones clone : c.getPlayer().getClones()) {
                        clone.getClone().setChalkboard(text);
                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.useChalkboard(clone.getClone(), false));
                    }
                } else {
                    c.showMessage("You cannot use this while mute, or muted in some form!");
                }
            } else if (itemId == Items.Cash.Note) {
                String sendTo = slea.readMapleAsciiString();
                String msg = slea.readMapleAsciiString();
                c.getPlayer().sendNote(sendTo, msg);
            } else if (itemId == Items.Cash.ViciousHammer) {
                /*byte inventory = */ slea.readInt();
                byte slot = (byte) slea.readInt();
                IItem toHammer = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot);
                ii.hammerEquip(toHammer);
                c.getSession().write(MaplePacketCreator.sendHammerSlot(slot));
                c.getPlayer().setHammerSlot(Byte.valueOf(slot));

            } else if ((itemId / 10000) == 512) {
                if (ii.getStateChangeItem(itemId) != 0) {
                    for (MapleCharacter mChar : c.getPlayer().getMap().getCharacters()) {
                        ii.getItemEffect(ii.getStateChangeItem(itemId)).applyTo(mChar);
                    }
                }
                c.getPlayer().getMap().startMapEffect(ii.getMsg(itemId).replaceFirst("%s", c.getPlayer().getName()).replaceFirst("%s", slea.readMapleAsciiString()), itemId);
            } else if ((itemId / 10000) == 510) {
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange("Jukebox/Congratulation"));
            } else {
                return;
            }
            MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);
        } catch (SQLException e) {
            log.error("Error saving note", e);
        }
    }

    private static int rand(int lbound, int ubound) {
        return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
    }
}
