/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import net.sf.odinms.client.Enums.MapleStat;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Admin
 */
public class InventoryManipulationCommands implements PlayerCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {

        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("removeitem")) {
            if (splitted.length < 2) {
                mc.dropMessage("Learn to Read @commands");
                return;
            }
            int id = Integer.parseInt(splitted[1]);
            MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(id);
            MapleInventory iv = player.getInventory(type);
            int possessed = iv.countById(id);
            if (possessed > 0) {
                MapleInventoryManipulator.removeById(c, MapleItemInformationProvider.getInstance().getInventoryType(id), id, possessed, true, false);
                c.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) -possessed, true));
            }
            mc.dropMessage("Complete.");
        } else if (splitted[0].equalsIgnoreCase("removeeqrow")) {
            for (int i = 0; i < 5; i++) {
                IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) i);
                if (tempItem == null) {
                    continue;
                }
                MapleInventoryManipulator.removeFromSlot1337(c, MapleInventoryType.EQUIP, (byte) i, tempItem.getQuantity(), false, true);
            }
            mc.dropMessage("should be done");
        } else if (splitted[0].equalsIgnoreCase("removecashrow")) {
            for (int i = 0; i < 5; i++) {
                IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) i);
                if (tempItem == null) {
                    continue;
                }
                MapleInventoryManipulator.removeFromSlot1337(c, MapleInventoryType.CASH, (byte) i, tempItem.getQuantity(), false, true);
            }
            mc.dropMessage("should be done");
            mc.dropMessage("should be done");
        } else if (splitted[0].equalsIgnoreCase("storage")) {
            if (SpecialStuff.getInstance().isPQMap(player.getMapId())) {
                mc.dropMessage("You cannot do it from these maps :)");
            }
            if (player.getEventInstance() == null) {
                c.getPlayer().getStorage().sendStorage(c, 2080005);
            } else {
                mc.dropMessage("[System] Smuggle Much? Try relog if this is a mistake :p");
            }
        } else if (splitted[0].equalsIgnoreCase("rechargestars")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            for (IItem stars : c.getPlayer().getInventory(MapleInventoryType.USE).list()) {
                if (ii.isThrowingStar(stars.getItemId())) {
                    stars.setQuantity(ii.getSlotMax(stars.getItemId()));
                    c.getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, stars));
                }
            }
            mc.dropMessage("[System] Recharged stars :)!");
        } else if (splitted[0].equalsIgnoreCase("rechargebullets")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            for (IItem bullets : c.getPlayer().getInventory(MapleInventoryType.USE).list()) {
                if (ii.isBullet(bullets.getItemId())) {
                    bullets.setQuantity(ii.getSlotMax(bullets.getItemId()));
                    c.getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, bullets));
                }
            }
        } else if (splitted[0].equalsIgnoreCase("buytao")) {
            if (player.isAlive()) {
                if (player.getMeso() >= 2000000000) {
                    player.gainMeso(-2000000000, true);
                    player.gainItem(4032016, 1);
                    player.setHp(1);
                    player.setMp(1);
                    player.updateSingleStat(MapleStat.HP, 1);
                    player.updateSingleStat(MapleStat.MP, 1);
                } else {
                    mc.dropMessage("Trying to scam the system?");
                    player.kill();
                }
            } else {
                mc.dropMessage("not when you are dead bitch");
            }
        } else if (splitted[0].equalsIgnoreCase("mesarz")) {
            if (player.haveSight(1)) {
                player.gainItem(Items.currencyType.Sight, -1);
                player.setMeso(2000000000);
                player.updateSingleStat(MapleStat.MESO, 2000000000);
            }
        }
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("removeitem", "<itemid>", "Removes all the items with that item id from your inventory"),
                    new PlayerCommandDefinition("removeeqrow", "", "removes the items from the first 4 slots on your Eq inventory"),
                    new PlayerCommandDefinition("removecashrow", "", "removes the items from the first 4 slots on your Cash inventory"),
                    new PlayerCommandDefinition("storage", "", "opens storage"),
                    new PlayerCommandDefinition("rechargestars", "", "recharge all your stars for free"),
                    new PlayerCommandDefinition("rechargebullets", "", "recharge all your bullets for free"),
                    new PlayerCommandDefinition("buytao", "", "buys Tao"),
                    new PlayerCommandDefinition("mesarz", "", "gives 2 bil mesos ")
                };
    }
}
