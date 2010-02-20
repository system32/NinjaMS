/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.donator;

import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.messages.DonatorCommand;
import net.sf.odinms.client.messages.DonatorCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.MapleInventoryManipulator;

/**
 *
 * @author Admin
 */
public class InventoryManipulationCommands implements DonatorCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("removeeqrow")) {
            int lol = 4;
            if (splitted.length == 2) {
                lol = (Integer.parseInt(splitted[1]) * 4);
            }
            for (int i = 0; i < lol + 1; i++) {
                IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) i);
                if (tempItem == null) {
                    continue;
                }
                MapleInventoryManipulator.removeFromSlot1337(c, MapleInventoryType.EQUIP, (byte) i, tempItem.getQuantity(), false, true);
            }
            mc.dropMessage("should be done");
        } else if (splitted[0].equalsIgnoreCase("removecashrow")) {
            int lol = 4;
            if (splitted.length == 2) {
                lol = (Integer.parseInt(splitted[1]) * 4);
            }
            for (int i = 0; i < lol + 1; i++) {
                IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) i);
                if (tempItem == null) {
                    continue;
                }
                MapleInventoryManipulator.removeFromSlot1337(c, MapleInventoryType.CASH, (byte) i, tempItem.getQuantity(), false, true);
            }
            mc.dropMessage("should be done");
        }
    }

    public DonatorCommandDefinition[] getDefinition() {
        return new DonatorCommandDefinition[]{
                    new DonatorCommandDefinition("removeeqrow", "<number of rows>", "removes the items from the rows you specify on your Eq inventory"),
                    new DonatorCommandDefinition("removecashrow", "<number of rows>", "removes the items from the rows you specify on your Cash inventory")
                };
    }
}
