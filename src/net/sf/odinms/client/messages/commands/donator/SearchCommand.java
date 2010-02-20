package net.sf.odinms.client.messages.commands.donator;

import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.client.messages.DonatorCommand;
import net.sf.odinms.client.messages.DonatorCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.MapleClient;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;


public class SearchCommand implements DonatorCommand {
    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {

if (splitted[0].equalsIgnoreCase("search")) {
            if (splitted.length == 1) {
                mc.dropMessage(splitted[0] + ": <NPC/MOB/ITEM/MAP/SKILL>  <search for>");
            } else if (splitted.length == 2) {
                mc.dropMessage(splitted[0] + ": <NPC/MOB/ITEM/MAP/SKILL>  <search for>");
            } else {
                String type = splitted[1];
                String search = StringUtil.joinStringFrom(splitted, 2);
                MapleData data;
                MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));
                mc.dropMessage("<<Type: " + type + " | Search: " + search + ">>");
                if (type.equalsIgnoreCase("NPC") || type.equalsIgnoreCase("NPCS")) {
                    List<String> retNpcs = new ArrayList<String>();
                    data =
                            dataProvider.getData("Npc.img");
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
                    if (retNpcs != null && retNpcs.size() > 0) {
                        for (String singleRetNpc : retNpcs) {
                            mc.dropMessage(singleRetNpc);
                        }

                    } else {
                        mc.dropMessage("No NPC's Found");
                    }

                } else if (type.equalsIgnoreCase("MAP") || type.equalsIgnoreCase("MAPS")) {
                    List<String> retMaps = new ArrayList<String>();
                    data =
                            dataProvider.getData("Map.img");
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

                } else if (type.equalsIgnoreCase("MOB") || type.equalsIgnoreCase("MOBS") || type.equalsIgnoreCase("MONSTER") || type.equalsIgnoreCase("MONSTERS")) {
                    List<String> retMobs = new ArrayList<String>();
                    data =
                            dataProvider.getData("Mob.img");
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

                } else if (type.equalsIgnoreCase("REACTOR") || type.equalsIgnoreCase("REACTORS")) {
                    mc.dropMessage("NOT ADDED YET");

                } else if (type.equalsIgnoreCase("ITEM") || type.equalsIgnoreCase("ITEMS")) {
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

                } else if (type.equalsIgnoreCase("SKILL") || type.equalsIgnoreCase("SKILLS")) {
                    List<String> retSkills = new ArrayList<String>();
                    data =
                            dataProvider.getData("Skill.img");
                    List<Pair<Integer, String>> skillPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData skillIdData : data.getChildren()) {
                        int skillIdFromData = Integer.parseInt(skillIdData.getName());
                        String skillNameFromData = MapleDataTool.getString(skillIdData.getChildByPath("name"), "NO-NAME");
                        skillPairList.add(new Pair<Integer, String>(skillIdFromData, skillNameFromData));
                    }

                    for (Pair<Integer, String> skillPair : skillPairList) {
                        if (skillPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retSkills.add(skillPair.getLeft() + " - " + skillPair.getRight());
                        }

                    }
                    if (retSkills != null && retSkills.size() > 0) {
                        for (String singleRetSkill : retSkills) {
                            mc.dropMessage(singleRetSkill);
                        }

                    } else {
                        mc.dropMessage("No Skills Found");
                    }

                } else {
                    mc.dropMessage("Sorry, that search call is unavailable");
                    mc.dropMessage(splitted[0] + ": <NPC/MOB/ITEM/MAP/SKILL>  <search for>");
                }
            }
        }
    }

    @Override
    public DonatorCommandDefinition[] getDefinition() {
        return new DonatorCommandDefinition[]{
            new DonatorCommandDefinition("search", "<item/mob/map/skill/npc> <searchfor>", "search command")
        };
    }


}
