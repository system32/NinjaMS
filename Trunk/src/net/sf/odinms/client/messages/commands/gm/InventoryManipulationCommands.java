/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.gm;

import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.constants.SpecialStuff;
import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;

/**
 *
 * @author Owner
 */
public class InventoryManipulationCommands implements GMCommand {

    private MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equals("shop")) {
            MapleShopFactory sfact = MapleShopFactory.getInstance();
            MapleShop shop = sfact.getShop(getOptionalIntArg(splitted, 1, 1));
            shop.sendShop(c);
        } else if (splitted[0].equals("item")) {
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            int itemId = Integer.parseInt(splitted[1]);
            if (ii.getSlotMax(itemId) > 0) {
                if (itemId >= 5000000 && itemId <= 5000100) {
                    if (quantity > 1) {
                        quantity = 1;
                    }
                    int petId = MaplePet.createPet(itemId);
                    MapleInventoryManipulator.addById(c, itemId, quantity, c.getPlayer().getName() + "used !item with quantity " + quantity, player.getName(), petId);
                    return;
                }
                MapleInventoryManipulator.addById(c, itemId, quantity, c.getPlayer().getName() + "used !item with quantity " + quantity, player.getName());
            } else {
                mc.dropMessage("Item " + itemId + " not found.");
            }
        } else if (splitted[0].equals("drop")) {
            int itemId = Integer.parseInt(splitted[1]);
            if (ii.getSlotMax(itemId) > 0) {
                if (SpecialStuff.getInstance().isGMBlocked(itemId) && !player.isHokage()) {
                    mc.dropMessage("Sunny says you cannot drop this item");
                    return;
                }
                short quantity = (short) (short) getOptionalIntArg(splitted, 2, 1);
                IItem toDrop;
                if (ii.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    toDrop = ii.getEquipById(itemId);
                } else {
                    toDrop = new Item(itemId, (byte) 0, (short) quantity);
                }
                toDrop.setOwner(player.getName());
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            } else {
                mc.dropMessage("The Item " + itemId + " does not exist");
            }
        } else if (splitted[0].equalsIgnoreCase("clearslot")) {
            if (splitted[1].equalsIgnoreCase("all")) {
                clearslot(c, 1);
                clearslot(c, 2);
                clearslot(c, 3);
                clearslot(c, 4);
                clearslot(c, 5);
                mc.dropMessage("All inventory slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("eq")) {
                clearslot(c, 1);
                mc.dropMessage("Eq inventory slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("use")) {
               clearslot(c, 2);
                mc.dropMessage("Use inventory slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("etc")) {
                clearslot(c, 4);
                mc.dropMessage("Etc inventory slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("setup")) {
                clearslot(c, 3);
                mc.dropMessage("Setup inventory slots cleared.");
            } else if (splitted[1].equalsIgnoreCase("cash")) {
                clearslot(c, 5);
                mc.dropMessage("Cash inventory slots cleared.");
            } else {
                mc.dropMessage("!clearslot " + splitted[1] + " does not exist!");
            }
        } else if (splitted[0].equalsIgnoreCase("omghax")) {
            short stat = 1;
            short wa = 1;
            int itemid = 1302000;
            if (splitted.length == 4) {
                try {
                    itemid = Integer.parseInt(splitted[1]);
                    stat = Short.parseShort(splitted[2]);
                    wa = Short.parseShort(splitted[3]);
                } catch (NumberFormatException numberFormatException) {
                }
            } else if (splitted.length == 3) {
                try {
                    itemid = Integer.parseInt(splitted[1]);
                    stat = Short.parseShort(splitted[2]);
                } catch (NumberFormatException numberFormatException) {
                }
            } else if (splitted.length == 2) {
                try {
                    itemid = Integer.parseInt(splitted[1]);
                } catch (NumberFormatException numberFormatException) {
                }
                stat = 32767;
            }
            if (itemid > 1000000 && itemid > 2000000 || ii.getSlotMax(itemid) > 0) {
                MapleInventoryManipulator.addStatItemById(c, itemid, c.getPlayer().getName(), stat, wa, wa);
            } else {
                mc.dropMessage("You have entered invalid Item ID : " + itemid);
            }
        }
    }

    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("shop", "", ""),
                    new GMCommandDefinition("item", "itemid <quantity(optional)>", "make item"),
                    new GMCommandDefinition("drop", "itemid <quantity(optional)>", "drops Items"),
                    new GMCommandDefinition("clearslot", "<all/eq/use/setup/etc/cash>", "clears inventory items"),
                    new GMCommandDefinition("omghax", "itemid stat wa", "Makes max stat item la. Stat and wa or optional arguments"),};
    }

    public void clearslot(MapleClient c, int x) {
        MapleInventoryType type = MapleInventoryType.getByType((byte) x);
        for (byte i = 0; i < 101; i++) {
            IItem tempItem = c.getPlayer().getInventory(type).getItem(i);
            if (tempItem == null) {
                continue;
            }
            MapleInventoryManipulator.removeFromSlot(c, type, i);
        }
    }
}
