/*
This file is part of the OdinMS Maple Story Server
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
package net.sf.odinms.server.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Calendar;
import net.sf.odinms.client.Enums.MapleBuffStat;

import net.sf.odinms.client.Inventory.Equip;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.channel.pvp.MaplePvp;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.scripting.Map.MapScriptManager;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.constants.Rates;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.life.PlayerNPCs;
import net.sf.odinms.server.life.SpawnPoint;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Randomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleMap {

    private static final int MAX_OID = 20000;
    private static final List<MapleMapObjectType> rangedMapobjectTypes = Arrays.asList(MapleMapObjectType.ITEM,
            MapleMapObjectType.MONSTER, MapleMapObjectType.DOOR, MapleMapObjectType.SUMMON, MapleMapObjectType.REACTOR);
    /**
     * Holds a mapping of all oid -> MapleMapObject on this map. mapobjects is NOT a synchronized collection since it
     * has to be synchronized together with runningOid that's why all access to mapobjects have to be done trough an
     * explicit synchronized block
     */
    private Map<Integer, MapleMapObject> mapobjects = new LinkedHashMap<Integer, MapleMapObject>();
    private Collection<SpawnPoint> monsterSpawn = new LinkedList<SpawnPoint>();
    private AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private Collection<MapleCharacter> characters = new LinkedHashSet<MapleCharacter>();
    private Map<Integer, MaplePortal> portals = new HashMap<Integer, MaplePortal>();
    private List<Rectangle> areas = new ArrayList<Rectangle>();
    private MapleFootholdTree footholds = null;
    private int mapid, runningOid = 100, returnMapId, channel;
    private float monsterRate;
    private boolean dropsDisabled = false, clock;
    private String mapName, streetName;
    private MapleMapEffect mapEffect = null;
    private boolean everlast = false;
    private int forcedReturnMap = 999999999;
    private int timeLimit, fieldType, timeMobId;
    private boolean maprespawn = true;
    private static Logger log = LoggerFactory.getLogger(MapleMap.class);
    private int dojowarpout;
    private String timeMobMessage = "", onFirstUserEnter, onUserEnter;
    // map clock
    private long timetoendclock;

    public MapleMap(int mapid, int channel, int returnMapId, float monsterRate) {
        this.mapid = mapid;
        this.channel = channel;
        this.returnMapId = returnMapId;
        this.monsterRate = monsterRate;
    }

    public void toggleDrops() {
        dropsDisabled = !dropsDisabled;
    }

    public int getId() {
        return mapid;
    }

    public MapleMap getReturnMap() {
        return ChannelServer.getInstance(channel).getMapFactory().getMap(returnMapId);
    }

    public int getForcedReturnId() {
        return forcedReturnMap;
    }

    public MapleMap getForcedReturnMap() {
        return ChannelServer.getInstance(channel).getMapFactory().getMap(forcedReturnMap);
    }

    public void setForcedReturnMap(int map) {
        this.forcedReturnMap = map;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public int getCurrentPartyId() {
        for (MapleCharacter chr : this.getCharacters()) {
            if (chr.getPartyId() != -1) {
                return chr.getPartyId();
            }
        }
        return -1;
    }

    public void addMapObject(MapleMapObject mapobject) {
        synchronized (this.mapobjects) {
            mapobject.setObjectId(runningOid);
            this.mapobjects.put(Integer.valueOf(runningOid), mapobject);
            incrementRunningOid();
        }
    }

    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, DelayedPacketCreation packetbakery) {
        spawnAndAddRangedMapObject(mapobject, packetbakery, null);
    }

    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, DelayedPacketCreation packetbakery, SpawnCondition condition) {
        synchronized (this.mapobjects) {
            mapobject.setObjectId(runningOid);

            synchronized (characters) {
                for (MapleCharacter chr : characters) {
                    if (condition == null || condition.canSpawn(chr)) {
                        if (chr.getPosition().distanceSq(mapobject.getPosition()) <= MapleCharacter.MAX_VIEW_RANGE_SQ && !chr.isfake) {
                            packetbakery.sendPackets(chr.getClient());
                            chr.addVisibleMapObject(mapobject);
                        }
                    }
                }
            }

            this.mapobjects.put(Integer.valueOf(runningOid), mapobject);
            incrementRunningOid();
        }
    }

    private void incrementRunningOid() {
        runningOid++;
        for (int numIncrements = 1; numIncrements < MAX_OID; numIncrements++) {
            if (runningOid > MAX_OID) {
                runningOid = 100;
            }
            if (this.mapobjects.containsKey(Integer.valueOf(runningOid))) {
                runningOid++;
            } else {
                return;
            }
        }
        throw new RuntimeException("Out of OIDs on map " + mapid + " (channel: " + channel + ")");
    }

    public void removeMapObject(int num) {
        synchronized (this.mapobjects) {
            this.mapobjects.remove(Integer.valueOf(num));
        }
    }

    public void removeMapObject(MapleMapObject obj) {
        if (obj != null) {
            removeMapObject(obj.getObjectId());
        }
    }

    private Point calcPointBelow(Point initial) {
        MapleFoothold fh = footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            double s4 = Math.abs(initial.x - fh.getX1());
            double alpha = Math.atan(s2 / s1);
            double beta = Math.atan(s1 / s2);
            double s5 = Math.cos(alpha) * (s4 / Math.cos(beta));
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) s5;
            } else {
                dropY = fh.getY1() + (int) s5;
            }
        }
        return new Point(initial.x, dropY);
    }

    private Point calcDropPos(Point initial, Point fallback) {
        Point ret = calcPointBelow(new Point(initial.x, initial.y - 99));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    private void dropFromMonster(MapleCharacter dropOwner, MapleMonster monster) {
        ChannelServer cserv = dropOwner.getClient().getChannelServer();
        if (monster.dropsDisabled() || cserv.getDropRate() < 1 || cserv.getBossDropRate() < 1 || monster.isGMSpawn()) {
            return;
        }
        /*
         * drop logic: decide based on monster what the max drop count is get drops (not allowed: multiple mesos,
         * multiple items of same type exception: event drops) calculate positions
         */
        int maxDrops;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final boolean isBoss = monster.isBoss();
        if (isBoss) {
            maxDrops = Rates.getBossDropRate(dropOwner); // boss droprate
        } else {
            maxDrops = Rates.getDropRate(dropOwner); // mob droprate
        }
        List<Integer> toDrop = new ArrayList<Integer>();
        for (int i = 0; i < maxDrops; i++) {
            toDrop.add(monster.getDrop());
        }
        Set<Integer> alreadyDropped = new HashSet<Integer>();
        for (int i = 0; i < toDrop.size(); i++) {
            if (toDrop.get(i) == -1) {
                if (alreadyDropped.contains(-1) && !isBoss) {
                    toDrop.remove(i);
                    i--;
                } else {
                    alreadyDropped.add(-1);
                }
            } else {
                MapleInventoryType type = ii.getInventoryType(toDrop.get(i));
                if (alreadyDropped.contains((int) type.getType()) && !isBoss) {
                    toDrop.remove(i);
                    i--;
                } else {
                    alreadyDropped.add((int) type.getType());
                }
            }
        }

        if (toDrop.size() > maxDrops) {
            toDrop = toDrop.subList(0, maxDrops);
        }

        /*
         * To make sure it drops Dragon Jewel for Magic scales Quest
         */
        if ((Math.random() * 100000) < 5 && !dropOwner.haveItem(1812006, 1, true, true) && !dropOwner.haveItem(4031679, 1)) {
            toDrop.add(4031679);
            broadcastMessage(MaplePacketCreator.serverNotice(5,
                    "[The Elite Ninja Gang] Huong has used her magical powers and rewarded you with a mysterious drop in your map."
                    + "Talk to NPC Cloy to find out more about the item"));
        }

        if (SpecialStuff.getInstance().isDojoMap(getId())) {
            int fuck = (int) Math.floor(Math.random() * 3);
            int[] pots = {2022430, 2022431, 2022432};
            toDrop.add(pots[fuck]);
        }
        Point[] toPoint = new Point[toDrop.size()];
        int shiftDirection = 0;
        int shiftCount = 0;

        int curX = Math.min(Math.max(monster.getPosition().x - 25 * (toDrop.size() / 2), footholds.getMinDropX() + 25),
                footholds.getMaxDropX() - toDrop.size() * 25);
        int curY = Math.max(monster.getPosition().y, footholds.getY1());
        //int monsterShift = curX -
        while (shiftDirection < 3 && shiftCount < 1000) {
            // TODO for real center drop the monster width is needed o.o"
            if (shiftDirection == 1) {
                curX += 25;
            } else if (shiftDirection == 2) {
                curX -= 25;
            }
            // now do it
            for (int i = 0; i < toDrop.size(); i++) {
                MapleFoothold wall = footholds.findWall(new Point(curX, curY), new Point(curX + toDrop.size() * 25, curY));
                if (wall != null) {
                    //System.out.println("found a wall. wallX " + wall.getX1() + " curX " + curX);
                    if (wall.getX1() < curX) {
                        shiftDirection = 1;
                        shiftCount++;
                        break;
                    } else if (wall.getX1() == curX) {
                        if (shiftDirection == 0) {
                            shiftDirection = 1;
                        }
                        shiftCount++;
                        break;
                    } else {
                        shiftDirection = 2;
                        shiftCount++;
                        break;
                    }
                } else if (i == toDrop.size() - 1) {
                    //System.out.println("ok " + curX);
                    shiftDirection = 3;
                }
                final Point dropPos = calcDropPos(new Point(curX + i * 25, curY), new Point(monster.getPosition()));
                toPoint[i] = new Point(curX + i * 25, curY);
                final int drop = toDrop.get(i);

                if (drop == -1) { // meso
                    final int mesoRate = Rates.getMesoRate(dropOwner);
                    Random r = new Random();
                    double mesoDecrease = Math.pow(0.93, monster.getExp() / 300.0);
                    if (mesoDecrease > 1.0) {
                        mesoDecrease = 1.0;
                    } else if (mesoDecrease < 0.001) {
                        mesoDecrease = 0.005;
                    }
                    int tempmeso = Math.min(30000, (int) (mesoDecrease * (monster.getExp())
                            * (1.0 + r.nextInt(20)) / 10.0));
                    if (dropOwner.getBuffedValue(MapleBuffStat.MESOUP) != null) {
                        tempmeso = (int) (tempmeso * dropOwner.getBuffedValue(MapleBuffStat.MESOUP).doubleValue() / 100.0);
                    }
                    if (this.getId() == 910000013 || this.getId() == 910000014) {
                        tempmeso /= 3;
                    }
                    final int meso = tempmeso;

                    if (meso > 0) {
                        final MapleMonster dropMonster = monster;
                        final MapleCharacter dropChar = dropOwner;
                        TimerManager.getInstance().schedule(new Runnable() {

                            public void run() {
                                spawnMesoDrop(meso * mesoRate, meso, dropPos, dropMonster, dropChar, isBoss);
                            }
                        }, monster.getAnimationTime("die1"));
                    }
                } else {
                    IItem idrop;
                    MapleInventoryType type = ii.getInventoryType(drop);
                    if (type.equals(MapleInventoryType.EQUIP)) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(drop));
                    } else {
                        idrop = new Item(drop, (byte) 0, (short) 1);
                        // Randomize quantity for certain items
                        if (ii.isArrowForBow(drop) || ii.isArrowForCrossBow(drop)) {
                            idrop.setQuantity((short) (1 + 100 * Math.random()));
                        } else if (ii.isThrowingStar(drop) || ii.isBullet(drop)) {
                            idrop.setQuantity((short) (1));
                        }
                    }

                    final MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, monster, dropOwner);
                    final MapleMapObject dropMonster = monster;
                    final MapleCharacter dropChar = dropOwner;
                    final TimerManager tMan = TimerManager.getInstance();
                    activateItemReactors(mdrop);
                    tMan.schedule(new Runnable() {

                        public void run() {
                            spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

                                public void sendPackets(MapleClient c) {
                                    c.getSession().write(MaplePacketCreator.dropItemFromMapObject(drop, mdrop.getObjectId(), dropMonster.getObjectId(), isBoss ? 0 : dropChar.getId(), dropMonster.getPosition(), dropPos, (byte) 1));
                                }
                            }, null);
                            tMan.schedule(new ExpireMapItemJob(mdrop), 60000);
                        }
                    }, monster.getAnimationTime("die1"));
                }
            }
        }
    }

    public boolean damageMonster(MapleCharacter chr, MapleMonster monster, int damage) {
        // double checking to potentially avoid synchronisation overhead
        if (monster.isAlive()) {
            synchronized (monster) {
                if (!monster.isAlive()) {
                    return false;
                }
                if (damage > 0) {
                    int monsterhp = monster.getHp();
                    monster.damage(chr, damage, true);
                    if (!monster.isAlive()) { // monster just died
                        killMonster(monster, chr, true);
                        if (monster.getId() >= 8810002 && monster.getId() <= 8810009) {
                            Collection<MapleMapObject> objects = chr.getMap().getMapObjects();
                            for (MapleMapObject object : objects) {
                                MapleMonster mons = chr.getMap().getMonsterByOid(object.getObjectId());
                                if (mons != null) {
                                    if (mons.getId() == 8810018) {
                                        damageMonster(chr, mons, monsterhp);
                                    }
                                }
                            }
                        }
                    } else {
                        if (monster.getId() >= 8810002 && monster.getId() <= 8810009) {
                            Collection<MapleMapObject> objects = chr.getMap().getMapObjects();
                            for (MapleMapObject object : objects) {
                                MapleMonster mons = chr.getMap().getMonsterByOid(object.getObjectId());
                                if (mons != null) {
                                    if (mons.getId() == 8810018) {
                                        damageMonster(chr, mons, damage);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops) {
        killMonster(monster, chr, withDrops, 1);
    }

    public void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, int animation) {
        spawnedMonstersOnMap.decrementAndGet(); // safe?
        monster.setHp(0);
        broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), animation), monster.getPosition());
        removeMapObject(monster);
        if (monster.getId() >= 8800003 || monster.getId() <= 8800010) {
            boolean makeZakReal = false;
            synchronized (this) {
                Collection<MapleMapObject> objects = this.getMapObjects();
                synchronized (objects) {
                    for (MapleMapObject object : objects) {
                        MapleMonster mons = getMonsterByOid(object.getObjectId());
                        if (mons != null) {
                            switch (mons.getId()) {
                                case 8800003:
                                case 8800004:
                                case 8800005:
                                case 8800006:
                                case 8800007:
                                case 8800008:
                                case 8800009:
                                case 8800010:
                                    makeZakReal = true;
                                    break;
                            }
                        }
                    }
                }
                if (makeZakReal) {
                    for (MapleMapObject object : objects) {
                        MapleMonster mons = chr.getMap().getMonsterByOid(object.getObjectId());
                        if (mons != null) {
                            if (mons.getId() == 8800000) {
                                broadcastMessage(MaplePacketCreator.makeMonsterReal(mons));
                                updateMonsterController(mons);
                            }
                        }
                    }
                }
            }
        }
        if (monster.getId() == 8810018) {
            String lol = "Characters in map : ";
            for (MapleCharacter chrs : this.getCharacters()) {
                if (!chrs.isChunin()) {
                    lol += chrs.getName() + ", ";
                }
            }
            MaplePacket packet = MaplePacketCreator.serverNotice(6, "To the crew who have defeated Horntail, we salute you! You are the true losers of NinjaMS! " + lol + " Hatake Kakashi.");
            try {
                ChannelServer.getInstance(chr.getClient().getChannel()).getWorldInterface().broadcastMessage(chr.getName(), packet.getBytes());
            } catch (RemoteException e) {
                chr.getClient().getChannelServer().reconnectWorld();
            }
        }
        MapleCharacter dropOwner = monster.killBy(chr);
        if (withDrops) {
            if (dropOwner == null) {
                dropOwner = chr;
            }
            dropFromMonster(dropOwner, monster);
        }
    }
    /*
    public void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean secondTime) {
    if (monster.getId() == 8810018 && !secondTime) {
    TimerManager.getInstance().schedule(new Runnable() {

    @Override
    public void run() {
    killMonster(monster, chr, withDrops, true);
    killAllMonsters(false);
    }
    }, 1000);
    return;
    }
    if (monster.getBuffToGive() > -1) {
    MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
    MapleStatEffect statEffect = mii.getItemEffect(monster.getBuffToGive());
    for (MapleMapObject mmo : this.getAllPlayer()) {
    MapleCharacter character = (MapleCharacter) mmo;
    statEffect.applyTo(character);
    }
    }
    if (monster.getId() == 8810018) {
    String lol = "Characters in map : ";
    for (MapleCharacter chrs : this.getCharacters()) {
    chrs.htKilled();
    lol += chrs.getName() + ", ";
    }
    MaplePacket packet = MaplePacketCreator.serverNotice(6, "To the crew who have defeated Horntail, we salute you! You are the true losers of NinjaMS! " + lol + " Hatake Kakashi.");
    try {
    ChannelServer.getInstance(chr.getClient().getChannel()).getWorldInterface().broadcastMessage(chr.getName(), packet.getBytes());
    } catch (RemoteException e) {
    chr.getClient().getChannelServer().reconnectWorld();
    }
    }
    spawnedMonstersOnMap.decrementAndGet();
    monster.setHp(0);
    MapleCharacter dropOwner = monster.killBy(chr);
    broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), true), monster.getPosition());
    removeMapObject(monster);
    if (withDrops && !monster.dropsDisabled()) {
    if (dropOwner == null) {
    dropOwner = chr;
    }
    dropFromMonster(dropOwner, monster);
    }
    }

    /*
    @SuppressWarnings("static-access")
    public void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean secondTime) {
    if (monster.getId() == 8810018 && !secondTime) {
    TimerManager.getInstance().schedule(new Runnable() {

    @Override
    public void run() {
    killMonster(monster, chr, withDrops, true);
    killAllMonsters(false);
    }
    }, 1000);
    return;
    }
    if (monster.getBuffToGive() > -1) {
    MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
    MapleStatEffect statEffect = mii.getItemEffect(monster.getBuffToGive());
    for (MapleMapObject mmo : this.getAllPlayer()) {
    MapleCharacter character = (MapleCharacter) mmo;
    statEffect.applyTo(character);
    }
    }
    if (monster.getId() == 8810018) {
    String lol = "Characters in map : ";
    for (MapleCharacter chrs : this.getCharacters()) {
    chrs.htKilled();
    lol += chrs.getName() + ", ";
    }
    MaplePacket packet = MaplePacketCreator.serverNotice(6, "To the crew who have defeated Horntail, we salute you! You are the true losers of NinjaMS! " + lol + " Hatake Kakashi.");
    try {
    chr.getClient().getChannelServer().getInstance(chr.getClient().getChannel()).getWorldInterface().broadcastMessage(chr.getName(), packet.getBytes());
    } catch (RemoteException e) {
    chr.getClient().getChannelServer().reconnectWorld();
    }
    }
    spawnedMonstersOnMap.decrementAndGet();
    monster.setHp(0);
    MapleCharacter dropOwner = monster.killBy(chr);
    broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), true), monster.getPosition());
    removeMapObject(monster);
    if (monster.getId() >= 8800003 || monster.getId() <= 8800010) {
    boolean makeZakReal = false;
    Collection<MapleMapObject> objects = getMapObjects();
    for (MapleMapObject object : objects) {
    MapleMonster mons = getMonsterByOid(object.getObjectId());
    if (mons != null) {
    switch (mons.getId()) {
    case 8800003:
    case 8800004:
    case 8800005:
    case 8800006:
    case 8800007:
    case 8800008:
    case 8800009:
    case 8800010:
    makeZakReal = true;
    break;
    }
    }
    }
    if (makeZakReal) {
    for (MapleMapObject object : objects) {
    MapleMonster mons = chr.getMap().getMonsterByOid(object.getObjectId());
    if (mons != null) {
    if (mons.getId() == 8800000) {
    broadcastMessage(MaplePacketCreator.makeMonsterReal(mons));
    updateMonsterController(mons);
    }
    }
    }
    }

    }
    if (withDrops && !monster.dropsDisabled()) {
    if (dropOwner == null) {
    dropOwner = chr;
    }
    dropFromMonster(dropOwner, monster);
    }
    }
     */

    public void killAllMonsters() {
        killAllMonsters(false);
    }

    public void killAllMonsters(boolean drop) {
        List<MapleMapObject> players = null;
        if (drop) {
            players = getAllPlayer();
        }
        List<MapleMapObject> monsters = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
        for (MapleMapObject monstermo : monsters) {
            MapleMonster monster = (MapleMonster) monstermo;
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), true), monster.getPosition());
            removeMapObject(monster);
            if (drop) {
                int random = (int) Math.random() * (players.size());
                dropFromMonster((MapleCharacter) players.get(random), monster);
            }
        }
    }

    public void killMonster(int monsId) {
        for (MapleMapObject mmo : getMapObjects()) {
            if (mmo instanceof MapleMonster) {
                if (((MapleMonster) mmo).getId() == monsId) {
                    this.killMonster((MapleMonster) mmo, (MapleCharacter) getAllPlayer().get(0), false);
                }
            }
        }
    }

    private List<MapleMapObject> getAllPlayer() {
        return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.PLAYER));
    }

    public void destroyReactor(int oid) {
        final MapleReactor reactor = getReactorByOid(oid);
        TimerManager tMan = TimerManager.getInstance();
        broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
        reactor.setAlive(false);
        removeMapObject(reactor);
        if (reactor.getDelay() > 0) {
            tMan.schedule(new Runnable() {

                @Override
                public void run() {
                    respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    /*
     * command to reset all item-reactors in a map to state 0 for GM/NPC use - not tested (broken reactors get removed
     * from mapobjects when destroyed) Should create instances for multiple copies of non-respawning reactors...
     */
    public void resetReactors() {
        synchronized (this.mapobjects) {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    ((MapleReactor) o).setState((byte) 0);
                    ((MapleReactor) o).setTimerActive(false);
                    broadcastMessage(MaplePacketCreator.triggerReactor((MapleReactor) o, 0));
                }
            }
        }
    }

    public void setReactorState() {
        synchronized (this.mapobjects) {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    ((MapleReactor) o).setState((byte) 1);
                    broadcastMessage(MaplePacketCreator.triggerReactor((MapleReactor) o, 1));
                }
            }
        }
    }

    /*
     * command to shuffle the positions of all reactors in a map for PQ purposes (such as ZPQ/LMPQ)
     */
    public void shuffleReactors() {
        List<Point> points = new ArrayList<Point>();
        synchronized (this.mapobjects) {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    points.add(((MapleReactor) o).getPosition());
                }
            }

            Collections.shuffle(points);

            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    ((MapleReactor) o).setPosition(points.remove(points.size() - 1));
                }
            }
        }
    }

    /**
     * Automagically finds a new controller for the given monster from the chars on the map...
     *
     * @param monster
     */
    public void updateMonsterController(MapleMonster monster) {
        synchronized (monster) {
            if (!monster.isAlive()) {
                return;
            }
            if (monster.getController() != null) {
                // monster has a controller already, check if he's still on this map
                if (monster.getController().getMap() != this) {
                    log.warn("Monstercontroller wasn't on same map");
                    monster.getController().stopControllingMonster(monster);
                } else {
                    // controller is on the map, monster has an controller, everything is fine
                    return;
                }
            }
            int mincontrolled = -1;
            MapleCharacter newController = null;
            synchronized (characters) {
                for (MapleCharacter chr : characters) {
                    if (!chr.isHidden() && (chr.getControlledMonsters().size() < mincontrolled || mincontrolled == -1) && !chr.isfake) {
                        mincontrolled = chr.getControlledMonsters().size();
                        newController = chr;
                    }
                }
            }
            if (newController != null) { // was a new controller found? (if not no one is on the map)

                if (monster.isFirstAttack()) {
                    newController.controlMonster(monster, true);
                    monster.setControllerHasAggro(true);
                    monster.setControllerKnowsAboutAggro(true);
                } else {
                    newController.controlMonster(monster, false);
                }
            }
        }
    }

    public Collection<MapleMapObject> getMapObjects() {
        return Collections.unmodifiableCollection(mapobjects.values());
    }

    public boolean containsNPC(int npcid) {
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.NPC) {
                    if (((MapleNPC) obj).getId() == npcid) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public MapleMapObject getMapObject(int oid) {
        return mapobjects.get(oid);
    }

    /**
     * returns a monster with the given oid, if no such monster exists returns null
     *
     * @param oid
     * @return
     */
    public MapleMonster getMonsterByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid);
        if (mmo == null) {
            return null;
        }
        if (mmo.getType() == MapleMapObjectType.MONSTER) {
            return (MapleMonster) mmo;
        }
        return null;
    }

    public MapleReactor getReactorByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid);
        if (mmo == null) {
            return null;
        }
        if (mmo.getType() == MapleMapObjectType.REACTOR) {
            return (MapleReactor) mmo;
        }
        return null;
    }

    public MapleReactor getReactorByName(String name) {
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) obj).getName().equals(name)) {
                        return (MapleReactor) obj;
                    }
                }
            }
        }
        return null;
    }

    //backwards compatible
    public void spawnMonsterOnGroudBelow(MapleMonster mob, Point pos) {
        spawnMonsterOnGroundBelow(mob, pos);
    }

    public void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y -= 1;
        mob.setPosition(spos);
        spawnMonster(mob);
    }

    public void spawnFakeMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y -= 1;
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    public void spawnMonster(final MapleMonster monster) {
        spawnMonster(monster, -1);
    }

    public void spawnMonster(final MapleMonster monster, final int parent) {
        monster.setMap(this);
        synchronized (this.mapobjects) {
            spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    if (parent >= 0) {
                        c.getSession().write(MaplePacketCreator.spawnRevives(monster, parent));
                    } else {
                        c.getSession().write(MaplePacketCreator.spawnMonster(monster, true));
                    }
                }
            });
            if (monster.hasBossHPBar()) {
                broadcastMessage(monster.makeBossHPBarPacket(), monster.getPosition());
            }
            updateMonsterController(monster);
        }
        spawnedMonstersOnMap.incrementAndGet();
    }

    public void spawnMonsterWithEffect(final MapleMonster monster, final int effect, Point pos) {
        monster.setMap(this);
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y -= 1;
        monster.setPosition(spos);
        if (!SpecialStuff.getInstance().isDojoMap(mapid)) {
            monster.disableDrops();
        }
        synchronized (this.mapobjects) {
            spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    c.getSession().write(MaplePacketCreator.spawnMonster(monster, true, effect));
                }
            });
            if (monster.hasBossHPBar()) {
                broadcastMessage(monster.makeBossHPBarPacket(), monster.getPosition());
            }
            updateMonsterController(monster);
        }
        spawnedMonstersOnMap.incrementAndGet();
    }

    public void spawnFakeMonster(final MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        synchronized (this.mapobjects) {
            spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    c.getSession().write(MaplePacketCreator.spawnFakeMonster(monster, 0));
                }
            });
        }
        spawnedMonstersOnMap.incrementAndGet();
    }

    public void makeMonsterReal(final MapleMonster monster) {
        monster.setFake(false);
        broadcastMessage(MaplePacketCreator.makeMonsterReal(monster));
        if (monster.hasBossHPBar()) {
            broadcastMessage(monster.makeBossHPBarPacket(), monster.getPosition());
        }
        updateMonsterController(monster);
    }

    public void spawnReactor(final MapleReactor reactor) {
        reactor.setMap(this);
        synchronized (this.mapobjects) {
            spawnAndAddRangedMapObject(reactor, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    c.getSession().write(reactor.makeSpawnData());
                }
            });
            //broadcastMessage(reactor.makeSpawnData());
        }
    }

    private void respawnReactor(final MapleReactor reactor) {
        reactor.setState((byte) 0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public void spawnDoor(final MapleDoor door) {
        synchronized (this.mapobjects) {
            spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    c.getSession().write(MaplePacketCreator.spawnDoor(door.getOwner().getId(), door.getTargetPosition(), false));
                    if (door.getOwner().getParty() != null && (door.getOwner() == c.getPlayer() || door.getOwner().getParty().containsMembers(new MaplePartyCharacter(c.getPlayer())))) {
                        c.getSession().write(MaplePacketCreator.partyPortal(door.getTown().getId(), door.getTarget().getId(), door.getTargetPosition()));
                    }
                    c.getSession().write(MaplePacketCreator.spawnPortal(door.getTown().getId(), door.getTarget().getId(), door.getTargetPosition()));
                    c.getSession().write(MaplePacketCreator.enableActions());
                }
            }, new SpawnCondition() {

                public boolean canSpawn(MapleCharacter chr) {
                    return chr.getMapId() == door.getTarget().getId()
                            || chr == door.getOwner() && chr.getParty() == null;
                }
            });
        }
    }

    public void spawnSummon(final MapleSummon summon) {
        spawnAndAddRangedMapObject(summon, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                int skilLlevel = summon.getOwner().getSkillLevel(SkillFactory.getSkill(summon.getSkill()));
                c.getSession().write(MaplePacketCreator.spawnSpecialMapObject(summon, skilLlevel, true));
            }
        });
    }

    public void spawnMist(final MapleMist mist, final int duration, boolean poison, boolean fake) {
        if (SpecialStuff.getInstance().isMistBlocked(this.getId())) {
            mist.getOwner().dropMessage("You cannot do this in this map. It annoys people");
            return;
        }
        addMapObject(mist);
        broadcastMessage(fake ? mist.makeFakeSpawnData(30) : mist.makeSpawnData());
        TimerManager tMan = TimerManager.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        if (poison) {
            Runnable poisonTask = new Runnable() {

                @Override
                public void run() {
                    List<MapleMapObject> affectedMonsters = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER));
                    for (MapleMapObject mo : affectedMonsters) {
                        if (mist.makeChanceResult()) {
                            MonsterStatusEffect poisonEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), mist.getSourceSkill(), false);
                            ((MapleMonster) mo).applyStatus(mist.getOwner(), poisonEffect, true, duration, false);
                        }
                    }
                }
            };
            poisonSchedule = tMan.register(poisonTask, 2000, 2500);
        } else {
            poisonSchedule = null;
        }
        tMan.schedule(new Runnable() {

            @Override
            public void run() {
                removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
                broadcastMessage(mist.makeDestroyData());
            }
        }, duration);
    }

    public void disappearingItemDrop(final MapleMapObject dropper,
            final MapleCharacter owner, final IItem item, Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner);
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(item.getItemId(), drop.getObjectId(), 0, 0, dropper.getPosition(), droppos, (byte) 3), drop.getPosition());
    }

    public void spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final IItem item, Point pos, final boolean ffaDrop, final boolean expire) {
        TimerManager tMan = TimerManager.getInstance();
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner);
        spawnAndAddRangedMapObject(drop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.dropItemFromMapObject(item.getItemId(), drop.getObjectId(), 0, ffaDrop ? 0 : owner.getId(),
                        dropper.getPosition(), droppos, (byte) 1));
            }
        });
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(item.getItemId(), drop.getObjectId(), 0, ffaDrop ? 0
                : owner.getId(), dropper.getPosition(), droppos, (byte) 0), drop.getPosition());

        if (expire) {
            tMan.schedule(new ExpireMapItemJob(drop), 60000);
        }

        activateItemReactors(drop);
    }

    private void activateItemReactors(MapleMapItem drop) {

        IItem item = drop.getItem();
        final TimerManager tMan = TimerManager.getInstance();
        //check for reactors on map that might use this item
        synchronized (this) {
            for (MapleMapObject o : mapobjects.values()) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) o).getReactorType() == 100) {
                        if (((MapleReactor) o).getReactItem().getLeft() == item.getItemId() && ((MapleReactor) o).getReactItem().getRight() <= item.getQuantity()) {
                            Rectangle area = ((MapleReactor) o).getArea();

                            if (area.contains(drop.getPosition())) {
                                MapleClient ownerClient = null;
                                if (drop.getOwner() != null) {
                                    ownerClient = drop.getOwner().getClient();
                                }
                                tMan.schedule(new ActivateItemReactor(drop, ((MapleReactor) o), ownerClient), 5000);
                            }
                        }
                    }
                }
            }
        }
    }

    public void spawnMesoDrop(final int meso, final int displayMeso, Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean ffaLoot) {
        TimerManager tMan = TimerManager.getInstance();
        final Point droppos = calcDropPos(position, position);
        final MapleMapItem mdrop = new MapleMapItem(meso, displayMeso, droppos, dropper, owner);
        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.dropMesoFromMapObject(displayMeso, mdrop.getObjectId(), dropper.getObjectId(),
                        owner.getId(), dropper.getPosition(), droppos, (byte) 1));
            }
        });
        tMan.schedule(new ExpireMapItemJob(mdrop), 60000);
    }

    public void startMapEffect(String msg, int itemId) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new MapleMapEffect(msg, itemId);
        broadcastMessage(mapEffect.makeStartData());
        TimerManager tMan = TimerManager.getInstance();
        /*tMan.schedule(new Runnable() {
        @Override
        public void run() {
        mapEffect.setActive(false);
        broadcastMessage(mapEffect.makeStartData());
        }
        }, 20000);*/
        tMan.schedule(new Runnable() {

            @Override
            public void run() {
                broadcastMessage(mapEffect.makeDestroyData());
                mapEffect = null;
            }
        }, 30000);
    }

    /**
     * Adds a player to this map and sends nescessary data
     *
     * @param chr
     */
    public void addPlayer(MapleCharacter chr) {
        //log.warn("[dc] [level2] Player {} enters map {}", new Object[] { chr.getName(), mapid });
        synchronized (characters) {
            this.characters.add(chr);
        }
        synchronized (this.mapobjects) {
            if (!chr.isfake) {
                if (!chr.isHidden()) {
                    broadcastMessage(chr, MaplePacketCreator.spawnPlayerMapobject(chr), false);
                    if (chr.getChalkboard() != null) {
                        broadcastMessage(chr, MaplePacketCreator.useChalkboard(chr, false), false);
                    }
                    MaplePet[] pets = chr.getPets();
                    for (int i = 0; i < 3; i++) {
                        if (pets[i] != null) {
                            //pets[i].setPos(getGroundBelow(chr.getPosition()));
                            broadcastMessage(chr, MaplePacketCreator.showPet(chr, pets[i], false, false), false);
                        } else {
                            break;
                        }
                    }
                }
                sendObjectPlacement(chr.getClient());
                if (!onFirstUserEnter.equals("") && !chr.hasEntered(onFirstUserEnter, mapid) && MapScriptManager.getInstance().scriptExists(onFirstUserEnter, true)) {
                    chr.enteredScript(onFirstUserEnter, mapid);
                    MapScriptManager.getInstance().getMapScript(chr.getClient(), onFirstUserEnter, true);
                }
                if (!onUserEnter.equals("") && MapScriptManager.getInstance().scriptExists(onUserEnter, false)) {
                    MapScriptManager.getInstance().getMapScript(chr.getClient(), onUserEnter, false);
                }
                switch (mapid) {
                    case 1:
                    case 2:
                    case 809000101:
                    case 809000201:
                        chr.getClient().getSession().write(MaplePacketCreator.showEquipEffect());
                        break;
                    case 913040000:
                        chr.startCygnusIntro(); // fall through
                        chr.getClient().getSession().write(MaplePacketCreator.showCygnusIntro(mapid - 913040000));
                        break;
                    case 913040001:
                    case 913040002:
                    case 913040003:
                    case 913040004:
                    case 913040005:
                        chr.getClient().getSession().write(MaplePacketCreator.showCygnusIntro(mapid - 913040000));
                        break;
                    case 913040006:
                        NPCScriptManager.getInstance().start(chr.getClient(), 1103005);
                        break;
                }
                // spawn self
                chr.getClient().getSession().write(MaplePacketCreator.spawnPlayerMapobject(chr));
                if (chr.getChalkboard() != null) {
                    chr.getClient().getSession().write(MaplePacketCreator.useChalkboard(chr, false));
                }
                for (MaplePet pet : chr.getPets()) {
                    if (pet != null) {
                        chr.getClient().getSession().write(MaplePacketCreator.showPet(chr, pet, false, false));
                    }
                }
            }
            this.mapobjects.put(Integer.valueOf(chr.getObjectId()), chr);
        }
        if (chr.getPlayerShop() != null) {
            addMapObject(chr.getPlayerShop());
        }
        MapleStatEffect summonStat = chr.getStatForBuff(MapleBuffStat.SUMMON);
        if (summonStat != null) {
            MapleSummon summon = chr.getSummons().get(summonStat.getSourceId());
            summon.setPosition(chr.getPosition());
            chr.getMap().spawnSummon(summon);
            updateMapObjectVisibility(chr, summon);
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(chr.getClient());
        }
        if (getTimeLimit() > 0 && getForcedReturnMap() != null) {
            chr.getClient().getSession().write(MaplePacketCreator.getClock(getTimeLimit()));
            chr.startMapTimeLimitTask(this, this.getForcedReturnMap());
        }
        if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted()) {
            chr.getClient().getSession().write(MaplePacketCreator.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
        }
        if (hasClock()) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            chr.getClient().getSession().write((MaplePacketCreator.getClockTime(hour, min, second)));
        }
        chr.receivePartyMemberHP();
    }

    public void removePlayer(MapleCharacter chr) {
        //log.warn("[dc] [level2] Player {} leaves map {}", new Object[] { chr.getName(), mapid });
        synchronized (characters) {
            characters.remove(chr);
        }
        removeMapObject(Integer.valueOf(chr.getObjectId()));
        broadcastMessage(MaplePacketCreator.removePlayerFromMap(chr.getId()));
        for (MapleMonster monster : chr.getControlledMonsters()) {
            monster.setController(null);
            monster.setControllerHasAggro(false);
            monster.setControllerKnowsAboutAggro(false);
            updateMonsterController(monster);
        }
        chr.leaveMap();
        chr.cancelMapTimeLimitTask();
        for (MapleSummon summon : chr.getSummons().values()) {
            if (summon.isPuppet()) {
                chr.cancelBuffStats(MapleBuffStat.PUPPET);
            } else {
                removeMapObject(summon);
            }
        }
    }

    /**
     * Broadcasts the given packet to everyone on the map but the source. source = null Broadcasts to everyone
     *
     * @param source
     * @param packet
     */
    // public void broadcastMessage(MapleCharacter source, MaplePacket packet) {
    // synchronized (characters) {
    // for (MapleCharacter chr : characters) {
    // if (chr != source) {
    // chr.getClient().getSession().write(packet);
    // }
    // }
    // }
    // }
    /**
     * Broadcast a message to everyone in the map
     *
     * @param packet
     */
    public void broadcastMessage(MaplePacket packet) {
        broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Nonranged. Repeat to source according to parameter.
     *
     * @param source
     * @param packet
     * @param repeatToSource
     */
    public void broadcastMessage(MapleCharacter source, MaplePacket packet, boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * Ranged and repeat according to parameters.
     *
     * @param source
     * @param packet
     * @param repeatToSource
     * @param ranged
     */
    public void broadcastMessage(MapleCharacter source, MaplePacket packet, boolean repeatToSource, boolean ranged) {
        broadcastMessage(repeatToSource ? null : source, packet, ranged ? MapleCharacter.MAX_VIEW_RANGE_SQ : Double.POSITIVE_INFINITY, source.getPosition());
    }

    /**
     * Always ranged from Point.
     *
     * @param packet
     * @param rangedFrom
     */
    public void broadcastMessage(MaplePacket packet, Point rangedFrom) {
        broadcastMessage(null, packet, MapleCharacter.MAX_VIEW_RANGE_SQ, rangedFrom);
    }

    /**
     * Always ranged from point. Does not repeat to source.
     *
     * @param source
     * @param packet
     * @param rangedFrom
     */
    public void broadcastMessage(MapleCharacter source, MaplePacket packet, Point rangedFrom) {
        broadcastMessage(source, packet, MapleCharacter.MAX_VIEW_RANGE_SQ, rangedFrom);
    }

    private void broadcastMessage(MapleCharacter source, MaplePacket packet, double rangeSq, Point rangedFrom) {
        synchronized (characters) {
            for (MapleCharacter chr : characters) {
                if (chr != source && !chr.isfake) {
                    chr.getClient().getSession().write(packet);
                }
            }
        }
    }

    public List<MapleCharacter> getPlayersInRect(Rectangle box, List<MapleCharacter> chr) {
        List<MapleCharacter> character = new LinkedList<MapleCharacter>();
        synchronized (characters) {
            for (MapleCharacter a : characters) {
                if (chr.contains(a.getClient().getPlayer())) {
                    if (box.contains(a.getPosition())) {
                        character.add(a);
                    }
                }
            }
        }
        return character;
    }

    private boolean isNonRangedType(MapleMapObjectType type) {
        switch (type) {
            case NPC:
            case PLAYER:
            case MIST:
            case PLAYER_NPC:
            case MERCHANT:
                //case REACTOR:
                return true;
        }
        return false;
    }

    private void sendObjectPlacement(MapleClient mapleClient) {
        for (MapleMapObject o : mapobjects.values()) {
            if (isNonRangedType(o.getType())) {
                o.sendSpawnData(mapleClient);
            } else if (o.getType() == MapleMapObjectType.MONSTER) {
                updateMonsterController((MapleMonster) o);
            }
        }
        MapleCharacter chr = mapleClient.getPlayer();
        if (chr != null) {
            for (MapleMapObject o : getMapObjectsInRange(chr.getPosition(), MapleCharacter.MAX_VIEW_RANGE_SQ, rangedMapobjectTypes)) {
                if (o.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) o).isAlive()) {
                        o.sendSpawnData(chr.getClient());
                        chr.addVisibleMapObject(o);
                    }
                } else {
                    o.sendSpawnData(chr.getClient());
                    chr.addVisibleMapObject(o);
                }
            }
        } else {
            log.info("sendObjectPlacement invoked with null char");
        }
    }

    public List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapleMapObjectType> types) {
        List<MapleMapObject> ret = new LinkedList<MapleMapObject>();
        for (MapleMapObject l : mapobjects.values()) {
            if (types.contains(l.getType())) {
                if (from.distanceSq(l.getPosition()) <= rangeSq) {
                    ret.add(l);
                }
            }
        }
        return ret;
    }

    public List<MapleMapObject> getMapObjectsInBox(Rectangle box, List<MapleMapObjectType> types) {
        List<MapleMapObject> ret = new LinkedList<MapleMapObject>();
        synchronized (mapobjects) {
            for (MapleMapObject l : mapobjects.values()) {
                if (types.contains(l.getType())) {
                    if (box.contains(l.getPosition())) {
                        ret.add(l);
                    }
                }
            }
        }
        return ret;
    }

    public List<MapleMapObject> getItemsInRange(Point from, double rangeSq) {
        List<MapleMapObject> ret = new LinkedList<MapleMapObject>();
        synchronized (mapobjects) {
            for (MapleMapObject l : mapobjects.values()) {
                if (l.getType() == MapleMapObjectType.ITEM) {
                    if (from.distanceSq(l.getPosition()) <= rangeSq) {
                        ret.add(l);
                    }
                }
            }
        }
        return ret;
    }

    public void addPortal(MaplePortal myPortal) {
        portals.put(myPortal.getId(), myPortal);
    }

    public MaplePortal getPortal(String portalname) {
        for (MaplePortal port : portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public MaplePortal getPortal(int portalid) {
        return portals.get(portalid);
    }

    public void addMapleArea(Rectangle rec) {
        areas.add(rec);
    }

    public List<Rectangle> getAreas() {
        return new ArrayList<Rectangle>(areas);
    }

    public Rectangle getArea(int index) {
        return areas.get(index);
    }

    public void setFootholds(MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public MapleFootholdTree getFootholds() {
        return footholds;
    }

    /**
     * not threadsafe, please synchronize yourself
     *
     * @param monster
     */
    public void addMonsterSpawn(MapleMonster monster, int mobTime) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime);

        monsterSpawn.add(sp);
        if (sp.shouldSpawn() || mobTime == -1) { // -1 does not respawn and should not either but force ONE spawn

            sp.spawnMonster(this);
        }
    }

    public float getMonsterRate() {
        return monsterRate;
    }

    public Collection<MapleCharacter> getCharacters() {
        return Collections.unmodifiableCollection(this.characters);
    }

    public MapleCharacter getCharacterById(int id) {
        for (MapleCharacter c : this.characters) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    private void updateMapObjectVisibility(MapleCharacter chr, MapleMapObject mo) {
        if (chr.isfake) {
            return;
        }
        if (!chr.isMapObjectVisible(mo)) { // monster entered view range

            if (mo.getPosition().distanceSq(chr.getPosition()) <= MapleCharacter.MAX_VIEW_RANGE_SQ && !chr.isfake) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else { // monster left view range
            if (mo.getPosition().distanceSq(chr.getPosition()) > MapleCharacter.MAX_VIEW_RANGE_SQ) {
                chr.removeVisibleMapObject(mo);
                mo.sendDestroyData(chr.getClient());
            }
        }
    }

    public void moveMonster(MapleMonster monster, Point reportedPos) {
        monster.setPosition(reportedPos);
        synchronized (characters) {
            for (MapleCharacter chr : characters) {
                updateMapObjectVisibility(chr, monster);
            }
        }
    }

    public void movePlayer(MapleCharacter player, Point newPosition) {

        player.setPosition(newPosition);
        if (player.isfake) {
            return;
        }
        Collection<MapleMapObject> visibleObjects = player.getVisibleMapObjects();
        MapleMapObject[] visibleObjectsNow = visibleObjects.toArray(new MapleMapObject[visibleObjects.size()]);
        for (MapleMapObject mo : visibleObjectsNow) {
            if (mapobjects.get(mo.getObjectId()) == mo) {
                updateMapObjectVisibility(player, mo);
            } else {
                player.removeVisibleMapObject(mo);
            }
        }
        for (MapleMapObject mo : getMapObjectsInRange(player.getPosition(), MapleCharacter.MAX_VIEW_RANGE_SQ,
                rangedMapobjectTypes)) {
            if (!player.isMapObjectVisible(mo)) {
                mo.sendSpawnData(player.getClient());
                player.addVisibleMapObject(mo);
            }
        }
    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public void spawnDebug(MessageCallback mc) {
        mc.dropMessage("Spawndebug...");
        synchronized (mapobjects) {
            mc.dropMessage("Mapobjects in map: " + mapobjects.size() + " \"spawnedMonstersOnMap\": "
                    + spawnedMonstersOnMap + " spawnpoints: " + monsterSpawn.size()
                    + " maxRegularSpawn: " + getMaxRegularSpawn());
            int numMonsters = 0;
            for (MapleMapObject mo : mapobjects.values()) {
                if (mo instanceof MapleMonster) {
                    numMonsters++;
                }
            }
            mc.dropMessage("actual monsters: " + numMonsters);
        }
    }

    private int getMaxRegularSpawn() {
        return (int) (monsterSpawn.size() / monsterRate);
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    public boolean hasClock() {
        return clock;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setEverlast(boolean everlast) {
        this.everlast = everlast;
    }

    public boolean getEverlast() {
        return everlast;
    }

    public boolean hasForcedEquip() {
        return fieldType == 81 || fieldType == 82;
    }

    private class ExpireMapItemJob implements Runnable {

        private MapleMapItem mapitem;

        public ExpireMapItemJob(MapleMapItem mapitem) {
            this.mapitem = mapitem;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
                synchronized (mapitem) {
                    if (mapid >= 209000001 && mapid <= 209000015) {
                        return;
                    }
                    if (mapitem.isPickedUp()) {
                        return;
                    }
                    MapleMap.this.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0),
                            mapitem.getPosition());
                    MapleMap.this.removeMapObject(mapitem);
                    mapitem.setPickedUp(true);
                }
            }
        }
    }

    private class ActivateItemReactor implements Runnable {

        private MapleMapItem mapitem;
        private MapleReactor reactor;
        private MapleClient c;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
                synchronized (mapitem) {
                    TimerManager tMan = TimerManager.getInstance();
                    if (mapitem.isPickedUp()) {
                        return;
                    }
                    MapleMap.this.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0),
                            mapitem.getPosition());
                    MapleMap.this.removeMapObject(mapitem);
                    reactor.hitReactor(c);
                    if (reactor.getDelay() > 0) {
                        tMan.schedule(new Runnable() {

                            @Override
                            public void run() {
                                reactor.setState((byte) 0);
                                broadcastMessage(MaplePacketCreator.triggerReactor(reactor, 0));
                            }
                        }, reactor.getDelay());
                    }
                }
            }
        }
    }

    private static interface DelayedPacketCreation {

        void sendPackets(MapleClient c);
    }

    private static interface SpawnCondition {

        boolean canSpawn(MapleCharacter chr);
    }

    public MapleReactor getReactorById(int Id) {
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.REACTOR) {
                    if (((MapleReactor) obj).getId() == Id) {
                        return (MapleReactor) obj;
                    }
                }
            }
        }
        return null;
    }

    public boolean isMonsterPresent(int Id) {
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.MONSTER) {
                    if (((MapleMonster) obj).getId() == Id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addClone(MapleCharacter chr) {
        synchronized (characters) {
            this.characters.add(chr);
        }
        synchronized (this.mapobjects) {
            if (!chr.isHidden()) {
                broadcastMessage(chr, (MaplePacketCreator.spawnPlayerMapobject(chr)), false);
            }
            this.mapobjects.put(chr.getObjectId(), chr);
        }
    }

    public void deleteAndReloadMap() {
        if (this.getId() == 100000000) {
            for (MapleCharacter chrs : this.getCharacters()) {
                chrs.dropMessage(1, "Due to a bug, you are now being warped to the free market.");
                chrs.changeMap(910000000, 0);
            }
        } else {
            for (MapleCharacter chrs1 : this.getCharacters()) {
                chrs1.dropMessage(1, "Due to a bug, you are now being warped home.");
                chrs1.changeMap(100000000, 0);
            }
        }
        ChannelServer.getInstance(this.channel).getMapFactory().getMaps().remove(this.getId());
    }

    public void respawn() {
        if (maprespawn) {
            if (channel == 3 && mapid == 910000001) {
                return;
            }
            if (characters.size() == 0) {
                return;
            }
            int numShouldSpawn = getMaxRegularSpawn() - spawnedMonstersOnMap.get();
            if (spawnedMonstersOnMap.get() > 50) {
                return;
            }
            if (numShouldSpawn > 0) { // k find that many monsters that need respawning and respawn them O.o
                List<SpawnPoint> randomSpawn = new ArrayList<SpawnPoint>(monsterSpawn);
                Collections.shuffle(randomSpawn);
                int spawned = 0;
                for (SpawnPoint spawnPoint : randomSpawn) {
                    if (spawnPoint.shouldSpawn()) {
                        spawnPoint.spawnMonster(MapleMap.this);
                        spawned++;
                    }
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    // timestamp
    public void setTimeStampClock(int seconds) {
        timetoendclock = System.currentTimeMillis() + (seconds * 1000);
    }

    // respawn Oliver
    public void mapSpawnOn() {
        maprespawn = true;
    }

    public void mapSpawnOff() {
        maprespawn = false;
    }

    public boolean getRespawnOn() {
        return maprespawn;
    }

    public boolean withinObjectRange(MapleCharacter who, int objectid) {
        List<MapleMapObject> npc = getMapObjectsInRange(who.getPosition(), MapleCharacter.MAX_VIEW_RANGE_SQ, Arrays.asList(MapleMapObjectType.NPC));
        for (MapleMapObject pen : npc) {
            MapleNPC npcla = (MapleNPC) pen;
            if (npcla.getObjectId() == objectid) {
                return true;
            }
        }

        npc = getMapObjectsInRange(who.getPosition(), MapleCharacter.MAX_VIEW_RANGE_SQ, Arrays.asList(MapleMapObjectType.PLAYER_NPC));
        for (MapleMapObject pen : npc) {
            PlayerNPCs npcla = (PlayerNPCs) pen;
            if (npcla.getObjectId() == objectid) {
                return true;
            }
        }
        return false;
    }

    public Collection<MapleCharacter> getNearestPvpChar(Point attacker, double maxRange, double maxHeight, Collection<MapleCharacter> chr) {
        Collection<MapleCharacter> character = new LinkedList<MapleCharacter>();
        for (MapleCharacter a : characters) {
            if (chr.contains(a.getClient().getPlayer())) {
                Point attackedPlayer = a.getPosition();
                MaplePortal Port = a.getMap().findClosestSpawnpoint(a.getPosition());
                Point nearestPort = Port.getPosition();
                double safeDis = attackedPlayer.distance(nearestPort);
                double distanceX = attacker.distance(attackedPlayer.getX(), attackedPlayer.getY());
                if (MaplePvp.isLeft) {
                    if (attacker.x > attackedPlayer.x && distanceX < maxRange && distanceX > 2
                            && attackedPlayer.y >= attacker.y - maxHeight && attackedPlayer.y <= attacker.y + maxHeight && safeDis > 2) {
                        character.add(a);
                    }
                }
                if (MaplePvp.isRight) {
                    if (attacker.x < attackedPlayer.x && distanceX < maxRange && distanceX > 2
                            && attackedPlayer.y >= attacker.y - maxHeight && attackedPlayer.y <= attacker.y + maxHeight && safeDis > 2) {
                        character.add(a);
                    }
                }
            }
        }
        return character;
    }

    public void spawnDojoMonster(final MapleMonster monster) {
        Point[] pts = {new Point(140, 0), new Point(190, 7), new Point(187, 7)};
        spawnMonsterWithEffect(monster, 15, pts[Randomizer.getInstance().nextInt(3)]);
    }

    public void clearDrops() {
        List<MapleMapObject> items = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM));
        for (MapleMapObject i : items) {
            removeMapObject(i);
            if (characters.size() >= 1) {
                broadcastMessage(MaplePacketCreator.removeItemFromMap(i.getObjectId(), 0, 0));
            }
        }
    }

    public void clearDrops(MapleCharacter player, boolean command) {
        List<MapleMapObject> items = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM));
        for (MapleMapObject i : items) {
            player.getMap().removeMapObject(i);
            player.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(i.getObjectId(), 0, player.getId()));
        }
        if (command) {
            player.dropMessage("Items Destroyed: " + items.size());
        }
    }

    public void startDojoClock() {
        final int byebye = dojowarpout;
        dojowarpout++;

        int stage = (getId() / 100) % 100;
        int time;

        if (stage % 6 == 0) {
            time = ((stage > 36 ? 15 : (int) Math.floor(stage / 6) + 5)) * 60;
        } else {
            time = (int) (((System.currentTimeMillis() + ((stage > 36 ? 15 : (int) Math.floor(stage / 6) + 5)) * 60000) - System.currentTimeMillis()) / 1000);
        }

        setTimeStampClock(time);
        broadcastMessage(MaplePacketCreator.getClock((int) time));

        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (dojowarpout == byebye) {
                    Collection<MapleCharacter> as = Collections.unmodifiableCollection(getCharacters());
                    for (MapleCharacter mch : as) {
                        if (mch != null) {
                            mch.showMessage(1, "Time's up!");
                            mch.changeMap(925020002);
                        }
                    }
                }
            }
        }, time * 1000 + 2500);
    }

    public MapleMonster getMonsterById(int id) {
        synchronized (mapobjects) {
            for (MapleMapObject obj : mapobjects.values()) {
                if (obj.getType() == MapleMapObjectType.MONSTER) {
                    if (((MapleMonster) obj).getId() == id) {
                        return (MapleMonster) obj;
                    }
                }
            }
        }
        return null;
    }

    public void setOnUserEnter(String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public String getOnUserEnter() {
        return onUserEnter;
    }

    public void setOnFirstUserEnter(String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public String getOnFirstUserEnter() {
        return onFirstUserEnter;
    }

    public void setTimeMobId(int id) {
        this.timeMobId = id;
    }

    public void setTimeMobMessage(String message) {
        this.timeMobMessage = message;
    }

    public int getTimeMobId() {
        return timeMobId;
    }

    public String getTimeMobMessage() {
        return timeMobMessage;
    }

    /* public void showPets(MapleCharacter player) {
    for (byte i = 0; i < player.getNoPets(); i++) {
    MaplePet pet = player.getPet(i);
    if (pet != null) {
    if(!player.isHidden()){
    broadcastMessage(player, MaplePacketCreator.showPet(player, pet, false, false), false);
    }
    player.getClient().getSession().write(MaplePacketCreator.showPet(player, pet, false));
    }
    }
    }*/
}

