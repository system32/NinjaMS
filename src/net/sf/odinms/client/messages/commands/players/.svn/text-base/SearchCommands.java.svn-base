/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class SearchCommands implements PlayerCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted.length < 2) {
            mc.dropMessage("You cannot search for empty string");
        } else if (splitted[0].equalsIgnoreCase("itemid")) {
            String search = StringUtil.joinStringFrom(splitted, 1);
            mc.dropMessage("<<ItemId Search: " + search + ">>");
            List<String> retItems = new ArrayList<String>();
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            for (Pair<Integer, String> itemPair : ii.getAllItems()) {
                if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                    retItems.add(itemPair.getLeft() + " - " + itemPair.getRight());
                }
            }
            if (retItems != null && retItems.size() > 0) {
                for (String singleRetItem : retItems) {
                    mc.dropMessage(singleRetItem);
                }
            } else {
                mc.dropMessage("No Item's Found");
            }
        } else if (splitted[0].equalsIgnoreCase("mapid")) {
            String search = StringUtil.joinStringFrom(splitted, 1);
            mc.dropMessage("<<MapId Search: " + search + ">>");
            MapleData data;
            MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));
            List<String> retMaps = new ArrayList<String>();
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
                    retMaps.add(mapPair.getLeft() + " - " + mapPair.getRight());
                }
            }
            if (retMaps != null && retMaps.size() > 0) {
                for (String singleRetMap : retMaps) {
                    mc.dropMessage(singleRetMap);
                }
            } else {
                mc.dropMessage("No Maps Found");
            }
        } else if (splitted[0].equalsIgnoreCase("mobid")) {
            String search = StringUtil.joinStringFrom(splitted, 1);
            mc.dropMessage("<<Mobid  Search: " + search + ">>");
            MapleData data;
            MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));
            List<String> retMobs = new ArrayList<String>();
            data = dataProvider.getData("Mob.img");
            List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
            for (MapleData mobIdData : data.getChildren()) {
                int mobIdFromData = Integer.parseInt(mobIdData.getName());
                String mobNameFromData = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
                mobPairList.add(new Pair<Integer, String>(mobIdFromData, mobNameFromData));
            }
            for (Pair<Integer, String> mobPair : mobPairList) {
                if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                    retMobs.add(mobPair.getLeft() + " - " + mobPair.getRight());
                }
            }
            if (retMobs != null && retMobs.size() > 0) {
                for (String singleRetMob : retMobs) {
                    mc.dropMessage(singleRetMob);
                }
            } else {
                mc.dropMessage("No Mob's Found");
            }
        }
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("itemid", "<search for>", "searches for item id"),
                    new PlayerCommandDefinition("mobid", "<search for>", "searches for mob id"),
                    new PlayerCommandDefinition("mapid", "<search for>", "searches for map id"),};
    }
}
