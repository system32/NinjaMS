/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.CashItemFactory;
import net.sf.odinms.server.CashItemInfo;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class BuyCSItemHandler extends AbstractMaplePacketHandler {

    public BuyCSItemHandler() {
    }

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int action = slea.readByte();
        if (action == 3) {
            slea.skip(1);
            int useNX = slea.readInt();
            int snCS = slea.readInt();
            CashItemInfo item = CashItemFactory.getItem(snCS);
            if (item.getBlock() == 1) {
                c.getPlayer().dropMessage(1, "This Item is Blocked from being bought. too bad so sad");
                c.getSession().write(MaplePacketCreator.enableCSorMTS());
                c.getSession().write(MaplePacketCreator.enableCSUse4());
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if (!MapleInventoryManipulator.checkSpace(c, item.getId(), item.getCount(), "")) {
                c.getPlayer().dropMessage(1, "You do not have enough inventory space!");
            } else {
                if (c.getPlayer().getCSPoints(useNX) >= item.getPrice()) {
                    c.getPlayer().addCSPoints(useNX, -item.getPrice());
                } else {
                    c.getPlayer().dropMessage(1, "You do not have enough Nx!");
                    c.getSession().write(MaplePacketCreator.enableCSorMTS());
                    c.getSession().write(MaplePacketCreator.enableCSUse4());
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                MapleInventoryManipulator.addById(c, item.getId(), (short) item.getCount(), "");
                c.getSession().write(MaplePacketCreator.showBoughtCSItem(c, item));
            }

            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableCSUse4());
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (action == 5) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("DELETE FROM wishlist WHERE charid = ?");
                ps.setInt(1, c.getPlayer().getId());
                ps.executeUpdate();
                ps.close();
                int i = 10;
                while (i > 0) {
                    int sn = slea.readInt();
                    if (sn != 0) {
                        ps = con.prepareStatement("INSERT INTO wishlist(charid, sn) VALUES(?, ?) ");
                        ps.setInt(1, c.getPlayer().getId());
                        ps.setInt(2, sn);
                        ps.executeUpdate();
                        ps.close();
                    }
                    i--;
                }
            } catch (SQLException se) {
            }
            c.getSession().write(MaplePacketCreator.updateWishList(c.getPlayer().getId()));
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.enableCSUse4());
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (action == 12) {
            c.getPlayer().dropMessage(1, "This item is already in your inventory!");
            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableCSUse4());
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (action == 31) { // credits pilsueng of Kdev
            int snCS = slea.readInt();
            CashItemInfo item = CashItemFactory.getItem(snCS);
            if (c.getPlayer().getMeso() >= item.getPrice()) {
                c.getPlayer().gainMeso(-item.getPrice(), false);
                MapleInventoryManipulator.addById(c, item.getId(), (short) item.getCount(), "Quest Item was purchased.");
                MapleInventory etcInventory = c.getPlayer().getInventory(MapleInventoryType.ETC);
                byte slot = etcInventory.findById(item.getId()).getPosition();
                c.getSession().write(MaplePacketCreator.showBoughtCSQuestItem(slot, item.getId()));
            } else {
                c.disconnect(); // The fuck? quest items are only like 1 meso HAX. poor hobo?
                return;
            }
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.enableCSUse3());
            return;
        }
        c.getPlayer().saveToDB();
    }
}

