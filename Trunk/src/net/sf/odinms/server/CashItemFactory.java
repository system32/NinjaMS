/*
 * This file is part of the OdinMS Maple Story Server
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.sf.odinms.database.DatabaseConnection;

/**
 *
 * @author Lerk
 */
public class CashItemFactory {
	private static Map<Integer, CashItemInfo> itemStats = new HashMap<Integer, CashItemInfo>();

    public static CashItemInfo getItem(int sn) {
        CashItemInfo CashItem = itemStats.get(sn);
        if (CashItem != null) {
            return CashItem;
        } else {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * from `ninjams`.`cash_commodity_data` WHERE serial_number = ?");
                ps.setInt(1, sn);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int itemId = rs.getInt("itemid");
                    int count = rs.getInt("quantity");
                    int price = rs.getInt("price");
                    int block = rs.getInt("ninjablock");
                    CashItem = new CashItemInfo(itemId, count, price, block);
                    itemStats.put(sn, CashItem);
                }
                ps.close();
                rs.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return CashItem;
    }
}