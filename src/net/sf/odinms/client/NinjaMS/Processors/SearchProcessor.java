/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.Processors;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Owner
 */
public class SearchProcessor {

    public static List<String> getItemId(String search) {
        List<String> ret = new ArrayList<String>();
        ret.add("<<ItemId Search: " + search + ">>");
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (Pair<Integer, String> itemPair : ii.getAllItems()) {
            if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                ret.add(itemPair.getLeft() + " - " + itemPair.getRight());
            }
        }
        return ret;
    }

    public static List<String> getMapId(String search) {
        List<String> ret = new ArrayList<String>();
        ret.add("<<MapId Search: " + search + ">>");
        MapleData data;
        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));
        data = dataProvider.getData("Map.img");
        List<Pair<Integer, String>> mapPairList = new LinkedList<Pair<Integer, String>>();
        for (MapleData mapAreaData : data.getChildren()) {
            for (MapleData mapIdData : mapAreaData.getChildren()) {
                int mapIdFromData = Integer.parseInt(mapIdData.getName());
                String mapNameFromData = MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME");
                mapPairList.add(new Pair<Integer, String>(mapIdFromData, mapNameFromData));
            }
        }
        for (Pair<Integer, String> mapPair : mapPairList) {
            if (mapPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                ret.add(mapPair.getLeft() + " - " + mapPair.getRight());
            }
        }
        return ret;
    }

    public static List<String> getMobId(String search) {
        List<String> ret = new ArrayList<String>();
        ret.add("<<Mobid  Search: " + search + ">>");
        MapleData data;
        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));

        data = dataProvider.getData("Mob.img");
        List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
        for (MapleData mobIdData : data.getChildren()) {
            int mobIdFromData = Integer.parseInt(mobIdData.getName());
            String mobNameFromData = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
            mobPairList.add(new Pair<Integer, String>(mobIdFromData, mobNameFromData));
        }
        for (Pair<Integer, String> mobPair : mobPairList) {
            if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                ret.add(mobPair.getLeft() + " - " + mobPair.getRight());
            }
        }
        return ret;
    }

    public static List<String> getNPCId(String search) {
        List<String> retNpcs = new ArrayList<String>();
        retNpcs.add("<<Mobid  Search: " + search + ">>");
        MapleData data;
        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));
        data = dataProvider.getData("Npc.img");
        List<Pair<Integer, String>> npcPairList = new LinkedList<Pair<Integer, String>>();
        for (MapleData npcIdData : data.getChildren()) {
            int npcIdFromData = Integer.parseInt(npcIdData.getName());
            String npcNameFromData = MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME");
            npcPairList.add(new Pair<Integer, String>(npcIdFromData, npcNameFromData));
        }

        for (Pair<Integer, String> npcPair : npcPairList) {
            if (npcPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                retNpcs.add(npcPair.getLeft() + " - " + npcPair.getRight());
            }
        }
        return retNpcs;

    }

    public static List<String> whoDrops(int itemid) {
        int searchid = itemid;
        List<String> retMobs = new ArrayList<String>();
        MapleData data = null;
        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("server.wzpath") + "/" + "String.wz"));
        data = dataProvider.getData("Mob.img");
        List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
        int chance = 0;
        Connection con = DatabaseConnection.getConnection();
        ResultSet rs = null;
        PreparedStatement ps = null;
        retMobs.add("[The Elite Ninja Gang] Item " + searchid + " is dropped by the following mobs:");
        try {
            ps = con.prepareStatement("SELECT monsterid, chance FROM monsterdrops WHERE itemid = ?");
            ps.setInt(1, searchid);
            rs = ps.executeQuery();
            while (rs.next()) {
                chance = rs.getInt("chance");
                int mobn = rs.getInt("monsterid");
                for (MapleData mobIdData : data.getChildren()) {
                    int mobIdFromData = Integer.parseInt(mobIdData.getName());
                    String mobNameFromData = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
                    mobPairList.add(new Pair<Integer, String>(mobIdFromData, mobNameFromData));
                }
                for (Pair<Integer, String> mobPair : mobPairList) {
                    if (mobPair.getLeft() == (mobn) && !retMobs.contains(mobPair.getRight())) {
                        retMobs.add(mobPair.getRight() + " - 1 in " + chance + " chance per drop.");
                    }
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException sQLException) {
        } catch (NumberFormatException numberFormatException) {
        }
        return retMobs;
    }
}
