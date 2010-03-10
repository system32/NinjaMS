/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.Clans;

import java.util.Arrays;
import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Admin
 */
public class Ninja {

    public static void checkClones(MapleCharacter noob) {
        if (noob.getMatchingOccupationLevel(Clan.NINJA) < 1) {
            return;
        }
       
        if (!noob.hasClones()) {
            return;
        }

        int others = 0;

        List<MapleMapObject> objects = noob.getMap().getMapObjectsInRange(noob.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.PLAYER));
        for (MapleMapObject mobster : objects) {
            MapleCharacter mmm = (MapleCharacter) mobster;
            if (!mmm.isfake) {
                if (mmm.getParty() == null) {
                    others++;
                } else {
                    if (mmm.getParty() != noob.getParty()) {
                        others++;
                    }
                }
            }
        }

        if (others > 3) {
            if (noob.getFakeChars().size() > 0) {
                noob.removeClones();
                noob.showMessage(5, "There are too many people not in your party in your map so your clones have been withdrawn (3 people).");
            }
        }
    }

    public static void checkLoot(MapleCharacter player) {
        if (player.getMatchingOccupationLevel(Occupations.NINJA) >= 10) {
            if (player.getMap().getMaxRegularSpawn() > 0) {
                if (player.getFakeChars().size() > 0) {
                    for (FakeCharacter ch : player.getFakeChars()) {
                        List<MapleMapObject> items = ch.getFakeChar().getMap().getMapObjectsInRange(ch.getFakeChar().getPosition(), 50 * 50, Arrays.asList(MapleMapObjectType.ITEM));
                        for (MapleMapObject item : items) {
                            MapleMapItem mapItem = (MapleMapItem) item;

                            boolean clear = false;

                            if (mapItem.isHuman()) {
                                continue;
                            }

                            boolean returns = false;
                            if (player.getParty() != null) {
                                for (MaplePartyCharacter partymem : player.getParty().getMembers()) {
                                    if (mapItem.getOwner().getId() == partymem.getId() && !mapItem.isPickedUp()) {
                                        returns = true;
                                    }
                                }
                            } else {
                                if (mapItem.getOwner().getId() == player.getId() && !mapItem.isPickedUp()) {
                                    returns = true;
                                }
                            }

                            if (!returns) {
                                continue;
                            }

                            if (mapItem.getMeso() > 0) {
                                if (player.getMapId() == 30000) {
                                    if (player.haveItem(4000252, 5)) {
                                        player.showMessage(1, "Type @henesys to exit!");
                                        return;
                                    }
                                }

                                ChannelServer cserv = player.getClient().getChannelServer();

                                if (player.getParty() == null) {
                                    int gain = Math.min(mapItem.getMeso(), (Integer.MAX_VALUE - player.getMeso()));
                                    player.gainMeso(gain, true, true);
                                } else {
                                    int mesosamm = mapItem.getMeso();

                                    int partynum = 0;
                                    for (MaplePartyCharacter partymem : player.getParty().getMembers()) {
                                        if (partymem.isOnline() && partymem.getMapid() == player.getMap().getId() && partymem.getChannel() == player.getClient().getChannel()) {
                                            partynum++;
                                        }
                                    }

                                    int mesosgain = mesosamm / partynum;

                                    for (MaplePartyCharacter partymem : player.getParty().getMembers()) {
                                        if (partymem.isOnline() && partymem.getMapid() == player.getMap().getId()) {
                                            MapleCharacter somecharacter = cserv.getPlayerStorage().getCharacterById(partymem.getId());
                                            if (somecharacter != null) {
                                                int gain = Math.min(mesosgain, (Integer.MAX_VALUE - player.getMeso()));
                                                somecharacter.gainMeso(gain, true, true);
                                            }
                                        }
                                    }
                                }
                                clear = true;
                            } else {
                                if (player.getDragonRoar() != null && mapItem.isForDRoar() && DragonRoar.tryAddItem(player.getClient(), mapItem.getItem().getItemId())) {
                                    clear = true;
                                } else if (!MapleInventoryManipulator.checkSpace(player.getClient(), mapItem.getItem().getItemId(), mapItem.getItem().getQuantity(), "")) {
                                    player.getClient().getSession().write(MaplePacketCreator.getInventoryFull());
                                    player.getClient().getSession().write(MaplePacketCreator.getShowInventoryFull());
                                    return;
                                } else {
                                    MapleInventoryManipulator.addFromDrop(player.getClient(), mapItem.getItem(), true);
                                    clear = true;
                                }
                            }
                            if (clear) {
                                mapItem.setPickedUp(true);
                                player.getMap().removeMapObject(item); // just incase ?
                                player.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapItem.getObjectId(), 2, player.getId()));
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
