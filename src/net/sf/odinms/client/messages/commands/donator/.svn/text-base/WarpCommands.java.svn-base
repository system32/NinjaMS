/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.donator;

import java.util.HashMap;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.DonatorCommand;
import net.sf.odinms.client.messages.DonatorCommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;

import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.maps.SavedLocationType;

/**
 *
 * @author Admin
 */
public class WarpCommands implements DonatorCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equals("map")) {
            int mapId = Integer.parseInt(splitted[1]);
            if (SpecialStuff.getInstance().canWarpTo(player, mapId)) {
                player.changeMap(mapId);
            } else {
                mc.dropMessage("you cannot warp to that map from your map");
            }
        } else if (splitted[0].equalsIgnoreCase("goto")) {
            if (SpecialStuff.getInstance().canWarpFrom(player)) {
                HashMap<String, Integer> maps = new HashMap<String, Integer>();
                maps.put("henesys", 100000000);
                maps.put("perion", 102000000);
                maps.put("kerning", 103000000);
                maps.put("lith", 104000000);
                maps.put("sleepywood", 105040300);
                maps.put("florina", 110000000);
                maps.put("orbis", 200000000);
                maps.put("happy", 209000000);
                maps.put("elnath", 211000000);
                maps.put("ludi", 220000000);
                maps.put("aqua", 230000000);
                maps.put("leafre", 240000000);
                maps.put("mulung", 250000000);
                maps.put("herb", 251000000);
                maps.put("omega", 221000000);
                maps.put("korean", 222000000);
                maps.put("nlc", 600000000);
                maps.put("excavation", 990000000);
                maps.put("mushmom", 100000005);
                maps.put("griffey", 240020101);
                maps.put("manon", 240020401);
                maps.put("horseman", 682000001);
                maps.put("balrog", 105090900);
                maps.put("showa", 801000000);
                maps.put("guild", 200000301);
                maps.put("shrine", 800000000);
                maps.put("skelegon", 240040511);
                maps.put("squids", 230040200);
                maps.put("vikings", 220060300);
                maps.put("wolfspiders", 600020300);
                if (splitted.length != 2) {
                    StringBuilder builder = new StringBuilder("Syntax: @go <mapname>");
                    for (String mapss : maps.keySet()) {
                        if (1 % 10 == 0) {// 10 maps per line
                            mc.dropMessage(builder.toString());
                        } else {
                            builder.append(mapss + ", ");
                        }
                    }
                    mc.dropMessage(builder.toString());
                } else if (maps.containsKey(splitted[1])) {
                    int map = maps.get(splitted[1]);
                    if (map == 910000000) {
                        player.saveLocation("FREE_MARKET");
                    }
                    player.changeMap(map, 0);
                    mc.dropMessage("Please feel free to suggest any more locations");
                } else {
                    mc.dropMessage("I could not find the map that you requested.");
                }
                maps.clear();
            } else {
            }
        } else if (splitted[0].equalsIgnoreCase("island")) {
            if (SpecialStuff.getInstance().cannotWarpTo(player, 925100700)) {
                mc.dropMessage("You cannot warp to island from this map");
                return;
            }
            player.changeMap(925100700);
            mc.dropMessage("What a retard!!!");
        } else if (splitted[0].equalsIgnoreCase("warp")) {
            player.cancelAllBuffs();
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                if (loc != null) {
                    MapleCharacter noob = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    if (player.canFuck(noob)) {
                        if (noob.inCS()) {
                            mc.dropMessage("Please Note: " + noob.getName() + " is in the Cash Shop. You are warping to where he/she will respawn.");
                        }
                        if (loc.channel == c.getChannel()) {
                            if (SpecialStuff.getInstance().canWarpTo(player, loc.map)) {
                                c.getPlayer().changeMap(noob.getMapId());
                            } else {
                                mc.dropMessage("[Anbu] You cannot warp to this map.");
                            }
                        } else {
                            if (SpecialStuff.getInstance().canWarpTo(player, loc.map)) {
                                mc.dropMessage("[Anbu] You are CCing to " + noob.getName() + ". Please wait while you lag.");
                                player.setMap( c.getChannelServer().getMapFactory().getMap(loc.map));
                                player.changeChannel(loc.channel);
                            } else {
                                mc.dropMessage("[Anbu] You cannot warp to this map.");
                            }
                        }
                    } else {
                        mc.dropMessage("[Anbu] You cannot warp to that ninja. He is too cool to be warped to!");
                    }
                } else {
                    int map = Integer.parseInt(splitted[1]);
                    if (SpecialStuff.getInstance().canWarpTo(player, map)) {
                        player.changeMap(map);
                        mc.dropMessage("[Anbu] Warping to " + c.getChannelServer().getMapFactory().getMap(map).getMapName() + " (" + map + ").");
                    } else {
                        mc.dropMessage("[Anbu] You cannot warp to this map.");
                    }
                }
            } catch (Exception e) {
                mc.dropMessage("[Anbu] The Ninja your are stalking - '" + splitted[1] + "' fails to exist, is CCing, or is offline.");
            }
        }
    }

    public DonatorCommandDefinition[] getDefinition() {
        return new DonatorCommandDefinition[]{
                    new DonatorCommandDefinition("map", "<mapid>", "warps you to map"),
                    new DonatorCommandDefinition("goto", "<town name>", "warps you to towns"),
                    new DonatorCommandDefinition("warp", "[mapid or person]", "Warps to the particular [mapid or person]. Limits apply."),
                    new DonatorCommandDefinition("island", "", "warps you to donator Island")
                };
    }
}
