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
package net.sf.odinms.client;

import net.sf.odinms.client.Inventory.InventoryContainer;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.IEquip;
import net.sf.odinms.client.Inventory.Equip;
import net.sf.odinms.client.Inventory.MapleWeaponType;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.Enums.MapleStat;
import net.sf.odinms.client.Enums.MapleBuffStat;
import net.sf.odinms.server.constants.Skills;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import net.sf.odinms.client.Enums.Clans;
import net.sf.odinms.client.Enums.Status;
import net.sf.odinms.client.Enums.MapleDisease;
import net.sf.odinms.client.Enums.MapleJob;
import net.sf.odinms.client.Enums.MapleSkinColor;
import net.sf.odinms.client.Inventory.MapleRing;

import net.sf.odinms.client.anticheat.CheatTracker;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.database.DatabaseException;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleMessenger;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.PlayerBuffValueHolder;
import net.sf.odinms.net.world.PlayerCoolDownValueHolder;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.scripting.event.EventInstanceManager;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MaplePlayerShop;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.MapleStorage;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.AbstractAnimatedMapleMapObject;
import net.sf.odinms.server.maps.MapleDoor;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleSummon;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.server.quest.MapleCustomQuest;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.net.world.guild.*;

import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MiniGame;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.life.MobSkillFactory;
import net.sf.odinms.server.maps.HiredMerchant;
import net.sf.odinms.server.maps.MapleMapEffect;
import net.sf.odinms.server.maps.SavedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleCharacter extends AbstractAnimatedMapleMapObject implements InventoryContainer {

    private static Logger log = LoggerFactory.getLogger(PacketProcessor.class);
    public static final double MAX_VIEW_RANGE_SQ = 850 * 850;
    private int world, accountid, level, str, dex, luk, int_, hp, mp, hair, face, fame, remainingAp, storageAp;
    private String name, createdate;
    private AtomicInteger exp = new AtomicInteger();
    private AtomicInteger meso = new AtomicInteger();
    private SavedLocation savedLocations[];
    private long lastfametime;
    private List<Integer> lastmonthfameids;
    // local stats represent current stats of the player to avoid expensive operations
    private transient int localmaxhp, localmaxmp, localstr, localdex, localluk, localint_, magic, localmaxbasedamage, watk;
    private transient double speedMod, jumpMod;
    private int id, parentId, childId;
    private MapleClient client;
    private MapleMap map;
    // mapid is only used when calling getMapId() with map == null, it is not updated when running in channelserver mode
    private int mapid, initialSpawnPoint;
    private MapleShop shop = null;
    private MaplePlayerShop playerShop = null;
    private MapleStorage storage = null;
    // pets - Oliver 
    private MaplePet pet = null;
    private MaplePet[] pets = new MaplePet[3];
    // FunStuff Oliver
    // torture status and penalties
    private boolean inflicted;
    private SkillMacro[] skillMacros = new SkillMacro[5];
    private MapleTrade trade = null;
    private MapleSkinColor skinColor = MapleSkinColor.NORMAL;
    private MapleJob job = MapleJob.BEGINNER;
    private Status gmLevel;
    private boolean hidden, canDoor = true;
    private int chair, itemEffect;
    private MapleParty party;
    private EventInstanceManager eventInstance = null;
    private MapleInventory[] inventory;
    private Map<MapleQuest, MapleQuestStatus> quests;
    private Set<MapleMonster> controlled = new LinkedHashSet<MapleMonster>();
    private Set<MapleMapObject> visibleMapObjects = new LinkedHashSet<MapleMapObject>();
    private Map<ISkill, SkillEntry> skills = new LinkedHashMap<ISkill, SkillEntry>();
    private Map<MapleBuffStat, MapleBuffStatValueHolder> effects = new LinkedHashMap<MapleBuffStat, MapleBuffStatValueHolder>();
    private Map<Integer, MapleKeyBinding> keymap = new LinkedHashMap<Integer, MapleKeyBinding>();
    private List<MapleDoor> doors = new ArrayList<MapleDoor>();
    private Map<Integer, MapleSummon> summons = new LinkedHashMap<Integer, MapleSummon>();
    private BuddyList buddylist;
    private Map<Integer, MapleCoolDownValueHolder> coolDowns = new LinkedHashMap<Integer, MapleCoolDownValueHolder>();
    // anticheat related information
    private CheatTracker anticheat;
    private ScheduledFuture<?> dragonBloodSchedule, mapTimeLimitTask = null;
    //guild related information
    private int guildid, guildrank, allianceRank;
    private MapleGuildCharacter mgc = null;
    // cash shop related information
    private int cardNX, maplePoints, paypalNX;
    private boolean incs;
    private MapleMessenger messenger = null;
    int messengerposition = 4;
    private List<MapleDisease> diseases = new ArrayList<MapleDisease>();
    private int markedMonster = 0, npcId = -1, battleshipHp = 0, energybar;
    private Byte hammerSlot = null;
    // ninja special
    private int reborn, mobkilled, ninjatensu, bosskilled, mutality, clonelimit, rank, rankmove, jobrank, jobrankmove, taorank, clantaorank;
    private Clans clan = Clans.UNDECIDED;
    private boolean leet = false;
    private byte rasengan;
    /**
     * Title shit contols the display of legend and title.
     * 0 = show none
     * 1 = show title only
     * 2 = show both.
     * 3 = show legend only
     */
    private byte prefixshit, smega;
    private String chalktext, legend;
    private boolean ircmsg;
    // fakes!- Oliver
    private List<Clones> fakes = new ArrayList<Clones>();
    public boolean isfake = false;
    //Rb Glitch Check
    private boolean rebirthing;
    //maxtstat counter
    private byte maxstatitem = 0;
    //donator
    private short dpoints, damount;
    //JQ
    private String jqStart;
    private String lastJQFinish = "In 1947";
    // PVP
    private int pvpkills, pvpdeaths;
    // limitations credits: Gayliver
    private boolean cannotdrop = false;
    //AutoAp credits: GayLiver
    private byte autoap;
    // Rate boost
    private int expBoost, mesoBoost, dropBoost, bdropBoost;
    private boolean rateChange, noHide;
    // Checks
    private String previousnames;
    private int taocheck;
    private boolean keymapchange, macrochange;
    //GMS Mode
    private byte GMSMode = 0;
    // dojo?????
    private int dojoPoints, lastDojoStage, dojoEnergy;
    private Map<Integer, String> entered = new LinkedHashMap<Integer, String>();
    //autobuff
    private List<Integer> autobuffs = new LinkedList<Integer>(); // 12
    private boolean autobuffchange = false;
    //BossQuest
    private int bossPoints;
    //MiniGames
    private int omokwins, omokties, omoklosses, matchcardwins, matchcardties, matchcardlosses;
    private MiniGame miniGame;
    //HiredMerchant
    private HiredMerchant hiredMerchant = null;
    private boolean hasMerchant = false;
    private int slots = 0;
    //  Ring - Oliver
    private List<MapleRing> crushRings = new LinkedList<MapleRing>();
    private List<MapleRing> friendshipRings = new LinkedList<MapleRing>();
    private List<MapleRing> marriageRings = new LinkedList<MapleRing>();

    private MapleCharacter() {
        setStance(0);
        inventory = new MapleInventory[MapleInventoryType.values().length];
        for (MapleInventoryType type : MapleInventoryType.values()) {
            inventory[type.ordinal()] = new MapleInventory(type, (byte) 96);
        }
        savedLocations = new SavedLocation[SavedLocationType.values().length];
        for (int i = 0; i < SavedLocationType.values().length; i++) {
            savedLocations[i] = null;
        }
        quests = new LinkedHashMap<MapleQuest, MapleQuestStatus>();
        anticheat = new CheatTracker(this);
        setPosition(new Point(0, 0));
    }

    public MapleCharacter getThis() {
        return this;
    }

    public static void doLoginMapCheck(MapleCharacter ret) {
        MapleMapFactory mapFactory = ChannelServer.getInstance(ret.client.getChannel()).getMapFactory();
        ret.map = mapFactory.getMap(ret.mapid);
        if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
            ret.map = mapFactory.getMap(100000000);
        }
        if (ret.inJail()) {
            ret.scheduleUnJail();
        }
        MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
        if (portal == null) {
            portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
            ret.initialSpawnPoint = 0;
        }
        ret.setPosition(portal.getPosition());
    }

    private void setClient(MapleClient c) {
        client = c;
    }

    private void setMGC(MapleGuildCharacter mgc) {
        this.mgc = mgc;
    }

    public static MapleCharacter loadCharFromDB(int charid, MapleClient client, boolean channelserver) throws SQLException {
        if (channelserver && ChannelServer.getServerPlayerStorage().getCharacters().containsKey(charid)) {
            client.setAccountName(ChannelServer.getServerPlayerStorage().getCharacters().get(charid).getClient().getAccountName());
            ChannelServer.getServerPlayerStorage().getCharacters().get(charid).setClient(client);
            if (ChannelServer.getServerPlayerStorage().getCharacters().get(charid).getGuildId() > 0) {
                ChannelServer.getServerPlayerStorage().getCharacters().get(charid).setMGC(new MapleGuildCharacter(ChannelServer.getServerPlayerStorage().getCharacters().get(charid)));
            }
            doLoginMapCheck(ChannelServer.getServerPlayerStorage().getCharacters().get(charid));
            return ChannelServer.getServerPlayerStorage().getCharacters().get(charid);
        }
        MapleCharacter ret = new MapleCharacter();
        ret.client = client;
        ret.id = charid;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
        ps.setInt(1, charid);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            rs.close();
            ps.close();
            throw new RuntimeException("Loading the Char Failed (char not found)");
        }
        ret.name = rs.getString("name");
        ret.level = rs.getInt("level");
        if (ret.level >= 255) {
            ret.level = 255;
        }
        if (ret.level < 1) {
            ret.level = 1;
        }
        ret.fame = rs.getInt("fame");
        ret.str = rs.getInt("str");
        ret.dex = rs.getInt("dex");
        ret.int_ = rs.getInt("int");
        ret.luk = rs.getInt("luk");
        ret.exp.set(rs.getInt("exp"));
        ret.hp = rs.getInt("hp");
        ret.mp = rs.getInt("mp");
        ret.storageAp = rs.getInt("storageap");
        ret.remainingAp = rs.getInt("ap");
        ret.meso.set(rs.getInt("meso"));
        ret.gmLevel = Status.getByLevel(rs.getInt("gm"));
        ret.skinColor = MapleSkinColor.getById(rs.getInt("skincolor"));
        int jobId = rs.getInt("job");
        ret.job = MapleJob.getById(jobId);
        ret.hair = rs.getInt("hair");
        ret.face = rs.getInt("face");
        ret.accountid = rs.getInt("accountid");
        ret.mapid = rs.getInt("map");
        ret.initialSpawnPoint = rs.getInt("spawnpoint");
        ret.world = rs.getInt("world");
        ret.reborn = rs.getInt("reborns");
        ret.mobkilled = rs.getInt("mobkilled");
        ret.bosskilled = rs.getInt("bosskilled");
        ret.mutality = rs.getInt("mutality");
        ret.guildid = rs.getInt("guildid");
        ret.guildrank = rs.getInt("guildrank");
        ret.allianceRank = rs.getInt("allianceRank");
        ret.clonelimit = rs.getInt("clonelimit");
        ret.rasengan = rs.getByte("rasengan");
        ret.legend = rs.getString("legend");
        ret.maxstatitem = rs.getByte("msi");
        ret.pvpdeaths = rs.getInt("pvpdeaths");
        ret.pvpkills = rs.getInt("pvpkills");
        ret.prefixshit = rs.getByte("prefixshit");
        ret.autoap = rs.getByte("autoap");
        ret.jobrank = rs.getInt("jobrank");
        ret.jobrankmove = rs.getInt("jobrankmove");
        ret.rank = rs.getInt("rank");
        ret.rankmove = rs.getInt("rankmove");
        ret.buddylist = new BuddyList(255);
        if (ret.guildid > 0) {
            ret.mgc = new MapleGuildCharacter(ret);
        }
        if (channelserver) {
            ret.maxQuests();
            ret.createdate = rs.getString("createdate");
            ret.previousnames = rs.getString("previousnames");
            if (ret.previousnames == null || ret.previousnames.length() < 2) {
                ret.previousnames = "||";
            }
            ret.taocheck = rs.getInt("taocheck");
            ret.GMSMode = rs.getByte("gmsmode");
            ret.lastDojoStage = rs.getInt("lastdojostage");
            ret.dojoPoints = rs.getInt("dojopoints");
            ret.taorank = rs.getInt("taorank");
            ret.clantaorank = rs.getInt("clantaorank");
            ret.smega = rs.getByte("smega");
            ret.bossPoints = rs.getInt("bosspoints");
            doLoginMapCheck(ret);
            int partyid = rs.getInt("party");
            if (partyid >= 0) {
                try {
                    MapleParty party = client.getChannelServer().getWorldInterface().getParty(partyid);
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                } catch (RemoteException e) {
                    client.getChannelServer().reconnectWorld();
                }
            }
        }
        rs.close();
        ps.close();
        ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
        ps.setInt(1, ret.accountid);
        rs = ps.executeQuery();
        if (rs.next()) {
            ret.getClient().setAccountName(rs.getString("name"));
            ret.paypalNX = rs.getInt("paypalNX");
            ret.maplePoints = rs.getInt("maplePoints");
            ret.cardNX = rs.getInt("cardNX");
            ret.clan = Clans.getById(rs.getInt("clan"));
            ret.gmLevel = Status.getByLevel(rs.getInt("gm"));
            ret.ninjatensu = rs.getInt("ninjatensu");
            ret.dpoints = rs.getShort("dpoints");
            ret.damount = rs.getShort("damount");
        }
        rs.close();
        ps.close();
        ps = con.prepareStatement("SELECT skillid FROM autobuffs WHERE characterid = ?");
        ps.setInt(1, charid);
        rs = ps.executeQuery();
        while (rs.next()) {
            ret.autobuffs.add(rs.getInt("skillid"));
        }
        rs.close();
        ps.close();
        ps = con.prepareStatement("SELECT * FROM rate_boost WHERE cid = ?");
        ps.setInt(1, ret.id);
        rs = ps.executeQuery();
        if (rs.next()) {
            ret.expBoost = rs.getInt("exp");
            ret.mesoBoost = rs.getInt("meso");
            ret.dropBoost = rs.getInt("drop");
            ret.bdropBoost = rs.getInt("boss");
        }
        rs.close();
        ps.close();
        String sql = "SELECT * FROM inventoryitems " + "LEFT JOIN inventoryequipment USING (inventoryitemid) " + "WHERE characterid = ?";
        if (!channelserver) {
            sql += " AND inventorytype = " + MapleInventoryType.EQUIPPED.getType();
        }
        ps = con.prepareStatement(sql);
        ps.setInt(1, charid);
        rs = ps.executeQuery();
        while (rs.next()) {
            MapleInventoryType type = MapleInventoryType.getByType((byte) rs.getInt("inventorytype"));
            if (type.equals(MapleInventoryType.EQUIP) || type.equals(MapleInventoryType.EQUIPPED)) {
                int itemid = rs.getInt("itemid");
                Equip equip = new Equip(itemid, (byte) rs.getInt("position"), rs.getInt("ringid"));
                equip.setOwner(rs.getString("owner"));
                equip.setQuantity((short) rs.getInt("quantity"));
                equip.setAcc((short) rs.getInt("acc"));
                equip.setAvoid((short) rs.getInt("avoid"));
                equip.setDex((short) rs.getInt("dex"));
                equip.setHands((short) rs.getInt("hands"));
                equip.setHp((short) rs.getInt("hp"));
                equip.setInt((short) rs.getInt("int"));
                equip.setJump((short) rs.getInt("jump"));
                equip.setLuk((short) rs.getInt("luk"));
                equip.setMatk((short) rs.getInt("matk"));
                equip.setMdef((short) rs.getInt("mdef"));
                equip.setMp((short) rs.getInt("mp"));
                equip.setSpeed((short) rs.getInt("speed"));
                equip.setStr((short) rs.getInt("str"));
                equip.setWatk((short) rs.getInt("watk"));
                equip.setWdef((short) rs.getInt("wdef"));
                equip.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
                equip.setLevel((byte) rs.getInt("level"));
                equip.setHammers((byte) rs.getInt("hammers"));
                equip.setExpiration(rs.getLong("expiredate"));
                ret.getInventory(type).addFromDB(equip);
                if (equip.getRingId() > -1) {
                    ret.addRingToCache(equip.getRingId());
                }
            } else {
                byte ppos = rs.getByte("position");
                Item item = new Item(rs.getInt("itemid"), ppos, (short) rs.getInt("quantity"));
                item.setOwner(rs.getString("owner"));
                byte petindex = rs.getByte("petindex");
                int peee = (rs.getInt("uniqueid"));
                item.setUniqueId(peee);
                item.setExpiration(rs.getLong("expiredate"));
                ret.getInventory(type).addFromDB(item);
                if (peee > 0 && petindex > -1) {
                    MaplePet lololol;
                    lololol = MaplePet.loadFromDb(item.getItemId(), petindex, peee);
                    if (lololol != null) {
                        Point pos = ret.getPosition();
                        pos.y -= 12;
                        lololol.setPos(pos);
                        lololol.setFh(0);
                        lololol.setStance(ret.getStance());
                        ret.pets[petindex] = lololol;
                    }
                }
            }
        }
        rs.close();
        ps.close();
        if (channelserver) {
            ret.maxSkills(false);
            ret.maxQuests();
            ret.loadMacrosFromDB(con);
            ret.loadKeyMapFromDB(con);
            ps = con.prepareStatement("SELECT `locationtype`,`map`,`portal` FROM savedlocations WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            while (rs.next()) {
                ret.savedLocations[SavedLocationType.valueOf(rs.getString("locationtype")).ordinal()] = new SavedLocation(rs.getInt("map"), rs.getInt("portal"));
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            ret.lastfametime = 0;
            ret.lastmonthfameids = new ArrayList<Integer>(31);
            while (rs.next()) {
                ret.lastfametime = Math.max(ret.lastfametime, rs.getTimestamp("when").getTime());
                ret.lastmonthfameids.add(rs.getInt("characterid_to"));
            }
            rs.close();
            ps.close();

            ret.buddylist.loadFromDb(charid);
            ret.storage = MapleStorage.loadOrCreateFromDB(ret.accountid);

        }
        ret.maxQuests();
        ret.recalcLocalStats();
        ret.silentEnforceMaxHpMp();
        ret.resetBattleshipHp();
        if (channelserver) {
            ChannelServer.getServerPlayerStorage().addPlayerToWorldStorage(ret);
            //ret.spawnPets();
        }
        return ret;
    }

    public static MapleCharacter getDefault(MapleClient client, int chrid) {
        MapleCharacter ret = getDefault(client);
        ret.id = chrid;
        return ret;
    }

    public static MapleCharacter getDefault(MapleClient client) {
        MapleCharacter ret = new MapleCharacter();
        ret.client = client;
        ret.hp = 50;
        ret.mp = 50;
        ret.map = null;
        ret.exp.set(0);
        ret.job = MapleJob.BEGINNER;
        ret.meso.set(0);
        ret.level = 1;
        ret.accountid = client.getAccID();
        ret.buddylist = new BuddyList(25);
        ret.cardNX = 0;
        ret.maplePoints = 0;
        ret.paypalNX = 0;
        ret.incs = false;
        ret.childId = 0;
        ret.parentId = 0;
        ret.clonelimit = 0;
        ret.rasengan = 0;
        ret.legend = "";
        ret.pvpdeaths = 0;
        ret.pvpkills = 0;
        ret.storageAp = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, ret.accountid);
            ResultSet rs;
            rs = ps.executeQuery();
            if (rs.next()) {
                ret.getClient().setAccountName(rs.getString("name"));
                ret.paypalNX = rs.getInt("paypalNX");
                ret.maplePoints = rs.getInt("maplePoints");
                ret.cardNX = rs.getInt("cardNX");
                ret.clan = Clans.getById(rs.getInt("clan"));
                ret.gmLevel = Status.getByLevel(rs.getInt("gm"));
                ret.ninjatensu = rs.getInt("ninjatensu");
                ret.dpoints = rs.getShort("dpoints");
                ret.damount = rs.getShort("damount");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.error("ERROR", e);
        }
        // keymap :D
        int[] key = {18, 65, 2, 23, 3, 4, 5, 6, 16, 17, 19, 25, 26, 27, 31, 34, 35, 37, 38, 40, 43, 44, 45, 46, 50, 56, 59, 60, 61, 62, 63, 64, 57, 48, 29, 7, 24, 33, 41};
        int[] type = {4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 5, 6, 6, 6, 6, 6, 6, 5, 4, 5, 4, 4, 4, 4};
        int[] action = {0, 106, 10, 1, 12, 13, 18, 24, 8, 5, 4, 19, 14, 15, 2, 17, 11, 3, 20, 16, 9, 50, 51, 6, 7, 53, 100, 101, 102, 103, 104, 105, 54, 22, 52, 21, 25, 26, 23};
        for (int i = 0; i < key.length; i++) {
            ret.keymap.put(key[i], new MapleKeyBinding(type[i], action[i]));
        }
        // pets
        //       ret.petid1 = -1;
        //     ret.petid2 = -1;
        //   ret.petid3 = -1;
        ret.recalcLocalStats();
        ret.maxQuests();
        return ret;
    }

    public void maxQuests() {
        try {
            for (Entry<Integer, MapleQuest> quest : MapleQuest.getAllQuests().entrySet()) {
                MapleQuest noob = MapleQuest.getInstance2(quest.getKey());
                MapleQuestStatus s = new MapleQuestStatus(noob, MapleQuestStatus.Status.getById(2));
                s.setCompletionTime(System.currentTimeMillis());
                s.setForfeited(0);
                quests.put(noob, s);
            }
        } catch (Exception e) {
            log.error("ERROR ", e);
        }
    }

    public void saveNewToDB() {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // clients should not be able to log back before their old state is saved (see MapleClient#getLoginState) so we are safe to switch to a very low isolation level here
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            // connections are thread local now, no need to
            // synchronize anymore =)
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO characters " + "(`hair`, `face`, `accountid`, `name`, `defaultaccid`)" + //6
                    "VALUES (?, ?, ?, ?, ?);"); //6
            ps.setInt(1, hair);
            ps.setInt(2, face);
            ps.setInt(3, accountid);
            ps.setString(4, name);
            ps.setInt(5, accountid);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            } else {
                rs.close();
                ps.close();
                throw new DatabaseException("Inserting char failed.");
            }
            rs.close();
            ps.close();
            // Pets not saved for new char
            // Skill Macros not saved for new Char
            // Deleting inventory is not needed for new char. so removed.
            ps = con.prepareStatement("INSERT INTO inventoryitems (characterid, itemid, inventorytype, position, quantity, owner, uniqueid, expiredate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement pse = con.prepareStatement("INSERT INTO inventoryequipment VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (MapleInventory iv : inventory) {
                ps.setInt(3, iv.getType().getType());
                for (IItem item : iv.list()) {
                    ps.setInt(1, id);
                    ps.setInt(2, item.getItemId());
                    ps.setInt(4, item.getPosition());
                    ps.setInt(5, item.getQuantity());
                    ps.setString(6, item.getOwner());
                    ps.setInt(7, item.getUniqueId());
                    ps.setLong(8, item.getExpiration());
                    ps.executeUpdate();
                    rs = ps.getGeneratedKeys();
                    int itemid;
                    if (rs.next()) {
                        itemid = rs.getInt(1);
                    } else {
                        rs.close();
                        ps.close();
                        throw new DatabaseException("Inserting char failed.");
                    }
                    if (iv.getType().equals(MapleInventoryType.EQUIP) || iv.getType().equals(MapleInventoryType.EQUIPPED)) {
                        pse.setInt(1, itemid);
                        IEquip equip = (IEquip) item;
                        pse.setInt(2, equip.getUpgradeSlots());
                        pse.setInt(3, equip.getLevel());
                        pse.setInt(4, equip.getStr());
                        pse.setInt(5, equip.getDex());
                        pse.setInt(6, equip.getInt());
                        pse.setInt(7, equip.getLuk());
                        pse.setInt(8, equip.getHp());
                        pse.setInt(9, equip.getMp());
                        pse.setInt(10, equip.getWatk());
                        pse.setInt(11, equip.getMatk());
                        pse.setInt(12, equip.getWdef());
                        pse.setInt(13, equip.getMdef());
                        pse.setInt(14, equip.getAcc());
                        pse.setInt(15, equip.getAvoid());
                        pse.setInt(16, equip.getHands());
                        pse.setInt(17, equip.getSpeed());
                        pse.setInt(18, equip.getJump());
                        pse.setInt(19, equip.getRingId());
                        pse.setInt(20, equip.getHammers());
                        pse.executeUpdate();
                    }
                }
            }
            ps.close();
            pse.close();
            //Deleting Keyboard is not needed for new characters
            ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, id);
            for (Entry<Integer, MapleKeyBinding> keybinding : keymap.entrySet()) {
                ps.setInt(2, keybinding.getKey());
                ps.setInt(3, keybinding.getValue().getType());
                ps.setInt(4, keybinding.getValue().getAction());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            // no need for saved locations for new char
            // no buddies for new char
            //storage and account details doesnt have to be saved for new character
            con.commit();
        } catch (Exception e) {
            log.error(MapleClient.getLogMessage(this, "[charsave] Error saving new character data"), e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                log.error(MapleClient.getLogMessage(this, "[charsave] Error Rolling Back"), e);
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                log.error(MapleClient.getLogMessage(this, "[charsave] Error saving new character going back to autocommit mode"), e);
            }
        }
    }

    public void saveToDB() {
        saveToDB(-1, false, false);
    }

    public void forceSave(boolean force, boolean removestorage) {
        saveToDB(-1, force, removestorage);
    }

    public void saveToDB(int spawnpointasdf, boolean force, boolean removestorage) {
        // first we alter it so even if it's not saving to db, it works.
        if (map != null) {
            if (spawnpointasdf > -1) {
                initialSpawnPoint = spawnpointasdf;
            } else {
                MaplePortal closest = map.findClosestSpawnpoint(getPosition());
                if (closest != null) {
                    initialSpawnPoint = closest.getId();
                } else {
                    initialSpawnPoint = 0;
                }
            }

            mapid = map.getId();
        }
        // then we check if character is taken
        if (!force && ChannelServer.getServerPlayerStorage().getCharacters().containsKey(id)) {
            return; // hehe :)
        }
        // else, begin the hell!
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // clients should not be able to log back before their old state is saved (see MapleClient#getLoginState) so we are save to switch to a very low isolation level here
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            // connections are thread local now, no need to
            // synchronize anymore =)
            con.setAutoCommit(false);
            ps = con.prepareStatement("UPDATE characters SET" + " `level` = ?, `fame` = ?, `str` = ?, `dex` = ?, `luk` = ?," + //5
                    " `int` = ?, `exp` = ?, `hp` = ?, `mp` = ?, `storageap` = ?," + //10
                    " `ap` = ?, `skincolor` = ?, `job` = ?, `hair` = ?," + //14
                    " `face` = ?, `map` = ?, `meso` = ?, `spawnpoint` = ?, `party` = ?," + //19
                    " `reborns` = ?, `mobkilled` = ?, `bosskilled` = ?, `mutality` =?, `clonelimit` = ?," +//24
                    " `legend` = ?, `msi` = ?, `name` = ?, `pvpdeaths` = ?," + //28
                    " `pvpkills` = ?, `prefixshit` = ?, `autoap` = ?, `irc` = ?, `taocheck` = ?, `gmsmode` = ?," + //34
                    " `lastdojostage` = ? , `dojopoints` = ?, `smega` = ?, `bosspoints` = ?" + //38
                    //      " `matchcardwins` = ?, `matchcardlosses` = ?, `matchcardties` = ?, `omokwins` = ?, `omoklosses` = ?, `omokties` = ?" + //46
                    " WHERE id = ?"); //40
            ps.setInt(1, level);
            ps.setInt(2, fame);
            ps.setInt(3, str);
            ps.setInt(4, dex);
            ps.setInt(5, luk);
            ps.setInt(6, int_);
            ps.setInt(7, exp.get()); // exp
            ps.setInt(8, hp);
            ps.setInt(9, mp);
            ps.setInt(10, storageAp);
            ps.setInt(11, remainingAp);
            ps.setInt(12, skinColor.getId());
            ps.setInt(13, job.getId());
            ps.setInt(14, hair);
            ps.setInt(15, face);
            ps.setInt(16, map.getId()); // map
            ps.setInt(17, meso.get()); // meso
            ps.setInt(18, initialSpawnPoint);
            if (party != null) {
                ps.setInt(19, party.getId());
            } else {
                ps.setInt(19, -1);
            }
            ps.setInt(20, reborn);
            ps.setInt(21, mobkilled);
            ps.setInt(22, bosskilled);
            ps.setInt(23, mutality);
            ps.setInt(24, clonelimit);
            ps.setString(25, legend);
            ps.setByte(26, maxstatitem);
            ps.setString(27, name);
            ps.setInt(28, pvpdeaths);
            ps.setInt(29, pvpkills);
            ps.setByte(30, prefixshit);
            ps.setByte(31, autoap);
            ps.setBoolean(32, ircmsg);
            int itemcount = getTaoOfSight();
            int difference = itemcount - taocheck;
            if ((difference > 100 && reborn < 5) || (difference > 1000 && reborn < 25) || (difference > 10000 && reborn < 100)) {
                if (isJounin()) {
                    client.getChannelServer().broadcastStaffPacket(MaplePacketCreator.serverNotice(1, "[Report] " + name + " has received " + difference + " more chickens more than he originated since his last save."));
                }
            }
            ps.setInt(33, itemcount);
            ps.setByte(34, GMSMode);
            ps.setInt(35, lastDojoStage);
            ps.setInt(36, dojoPoints);
            ps.setByte(37, smega);
            ps.setInt(38, bossPoints);
            ps.setInt(39, id);
            int updateRows = ps.executeUpdate();
            if (updateRows < 1) {
                throw new DatabaseException("Character not in database (" + id + ")");
            }
            for (MaplePet pett : pets) {
                if (pett != null) {
                    pett.saveToDb();
                }
            }
            if (macrochange) {
                ps = con.prepareStatement("DELETE FROM skillmacros WHERE characterid = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps = con.prepareStatement("INSERT INTO skillmacros" + " (characterid, skill1, skill2, skill3, name, shout, position) " + "VALUES (?, ?, ?, ?, ?, ?, ?)");
                for (int i = 0; i < 5; i++) {
                    SkillMacro macro = skillMacros[i];
                    ps.setInt(1, id);
                    if (macro != null) {
                        ps.setInt(2, macro.getSkill1());
                        ps.setInt(3, macro.getSkill2());
                        ps.setInt(4, macro.getSkill3());
                        ps.setString(5, macro.getName());
                        ps.setInt(6, macro.getShout());
                        ps.setInt(7, i);
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }

            deleteWhereCharacterId(con, "DELETE FROM inventoryitems WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO inventoryitems (characterid, itemid, inventorytype, position, quantity, owner, uniqueid, expiredate, petindex) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement pse = con.prepareStatement("INSERT INTO inventoryequipment VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (MapleInventory iv : inventory) {
                ps.setInt(3, iv.getType().getType());
                for (IItem item : iv.list()) {
                    ps.setInt(1, id);
                    ps.setInt(2, item.getItemId());
                    ps.setInt(4, item.getPosition());
                    ps.setInt(5, item.getQuantity());
                    ps.setString(6, item.getOwner());
                    ps.setInt(7, item.getUniqueId());
                    ps.setLong(8, item.getExpiration());
                    byte xx = -1;
                    if (item.getUniqueId() > 0) {
                        for (byte i = 0; i < pets.length; i++) {
                            if (pets[i] != null) {
                                if (item.getUniqueId() == pets[i].getUniqueId()) {
                                    xx = i;
                                }
                            }
                        }
                    }
                    ps.setByte(9, xx);
                    ps.executeUpdate();
                    rs = ps.getGeneratedKeys();
                    int itemid;
                    if (rs.next()) {
                        itemid = rs.getInt(1);
                    } else {
                        rs.close();
                        throw new DatabaseException("Inserting char failed.");
                    }
                    if (iv.getType().equals(MapleInventoryType.EQUIP) || iv.getType().equals(MapleInventoryType.EQUIPPED)) {
                        pse.setInt(1, itemid);
                        IEquip equip = (IEquip) item;
                        pse.setInt(2, equip.getUpgradeSlots());
                        pse.setInt(3, equip.getLevel());
                        pse.setInt(4, equip.getStr());
                        pse.setInt(5, equip.getDex());
                        pse.setInt(6, equip.getInt());
                        pse.setInt(7, equip.getLuk());
                        pse.setInt(8, equip.getHp());
                        pse.setInt(9, equip.getMp());
                        pse.setInt(10, equip.getWatk());
                        pse.setInt(11, equip.getMatk());
                        pse.setInt(12, equip.getWdef());
                        pse.setInt(13, equip.getMdef());
                        pse.setInt(14, equip.getAcc());
                        pse.setInt(15, equip.getAvoid());
                        pse.setInt(16, equip.getHands());
                        pse.setInt(17, equip.getSpeed());
                        pse.setInt(18, equip.getJump());
                        pse.setInt(19, equip.getRingId());
                        pse.setInt(20, equip.getHammers());
                        pse.executeUpdate();
                    }
                    rs.close();
                }
            }
            pse.close();
            if (keymapchange) {
                deleteWhereCharacterId(con, "DELETE FROM keymap WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)");
                ps.setInt(1, id);
                for (Entry<Integer, MapleKeyBinding> keybinding : keymap.entrySet()) {
                    ps.setInt(2, keybinding.getKey());
                    ps.setInt(3, keybinding.getValue().getType());
                    ps.setInt(4, keybinding.getValue().getAction());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO savedlocations (characterid, `locationtype`, `map`, `portal`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, id);
            for (SavedLocationType savedLocationType : SavedLocationType.values()) {
                if (savedLocations[savedLocationType.ordinal()] != null) {
                    ps.setString(2, savedLocationType.name());
                    ps.setInt(3, savedLocations[savedLocationType.ordinal()].getMapId());
                    ps.setInt(4, savedLocations[savedLocationType.ordinal()].getPortal());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ? AND pending = 0");
            ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `group`, `pending`) VALUES (?, ?, ?, 0)");
            ps.setInt(1, id);
            for (BuddylistEntry entry : buddylist.getBuddies()) {
                if (entry.isVisible()) {
                    ps.setInt(2, entry.getCharacterId());
                    ps.setString(3, entry.getGroup());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            // Custom Rates
            if (rateChange) {
                deleteWhereCharacterId(con, "DELETE FROM rate_boost WHERE cid = ?");
                ps = con.prepareStatement("INSERT INTO rate_boost (`cid`, `exp`, `meso`, `drop`, `boss`) VALUES (?, ?, ?, ?, ?)");
                ps.setInt(1, id);
                ps.setInt(2, expBoost);
                ps.setInt(3, mesoBoost);
                ps.setInt(4, dropBoost);
                ps.setInt(5, bdropBoost);
                ps.execute();
                ps.close();
            }
            if (autobuffchange) {
                deleteWhereCharacterId(con, "DELETE FROM autobuffs WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO autobuffs (characterid, skillid) VALUES (?, ?)");
                ps.setInt(1, id);
                for (int buff : autobuffs) {
                    ps.setInt(2, buff);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }
            ps = con.prepareStatement("UPDATE accounts SET `cardNX` = ?, `maplePoints` = ?, `paypalNX` = ?, `clan` = ?, `ninjatensu` = ?, `dpoints` = ?, `damount` = ?  WHERE id = ?");
            ps.setInt(1, cardNX);
            ps.setInt(2, maplePoints);
            ps.setInt(3, paypalNX);
            ps.setInt(4, clan.getId());
            ps.setInt(5, ninjatensu);
            ps.setShort(6, dpoints);
            ps.setShort(7, damount);
            ps.setInt(8, client.getAccID());
            ps.executeUpdate();
            if (storage != null) {
                storage.saveToDB();
            }
            con.commit();
        } catch (Exception e) {
            log.error(MapleClient.getLogMessage(this, "[charsave] Error saving character data"), e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                log.error(MapleClient.getLogMessage(this, "[charsave] Error Rolling Back"), e);
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                log.error(MapleClient.getLogMessage(this, "[charsave] Error going back to autocommit mode"), e);
            }
        }

        if (removestorage && ChannelServer.getServerPlayerStorage().getCharacters().containsKey(id)) {
            ChannelServer.getServerPlayerStorage().removePlayerFromWorldStorage(id);
        }
    }

    private void deleteWhereCharacterId(Connection con, String sql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    public MapleQuestStatus getQuest(
            MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
        }

        return quests.get(quest);
    }

    public void updateQuest(MapleQuestStatus quest) {
        quests.put(quest.getQuest(), quest);
        client.getSession().write(MaplePacketCreator.completeQuest(this, (short) quest.getQuest().getId()));
    }

    public static int getIdByName(String name, int world) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;

        try {
            ps = con.prepareStatement("SELECT id FROM characters WHERE name = ? AND world = ?");
            ps.setString(1, name);
            ps.setInt(2, world);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }

            int id = rs.getInt("id");
            rs.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            log.error("ERROR", e);
        }

        return -1;
    }

    public Integer getBuffedValue(
            MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }

        return mbsvh.value;
    }

    public boolean isBuffFrom(MapleBuffStat stat, ISkill skill) {
        MapleBuffStatValueHolder mbsvh = effects.get(stat);
        return mbsvh != null && mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skill.getId();
    }

    public int getBuffSource(MapleBuffStat stat) {
        MapleBuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null) {
            return -1;
        }

        return mbsvh.effect.getSourceId();
    }

    public void setBuffedValue(MapleBuffStat effect, int value) {
        MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }

    public Long getBuffedStarttime(
            MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }

        return mbsvh.startTime;
    }

    public MapleStatEffect getStatForBuff(
            MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }

        return mbsvh.effect;
    }

    private void prepareDragonBlood(final MapleStatEffect bloodEffect) {
        if (dragonBloodSchedule != null) {
            dragonBloodSchedule.cancel(false);
        }
        dragonBloodSchedule = TimerManager.getInstance().register(new Runnable() {

            public void run() {
                addHP(-bloodEffect.getX());
                getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(bloodEffect.getSourceId(), 5));
                getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(getId(), bloodEffect.getSourceId(), 5), false);
            }
        }, 4000, 4000);
    }

    public void startMapTimeLimitTask(final MapleMap from,
            final MapleMap to) {
        if (to.getTimeLimit() > 0 && from != null) {
            final MapleCharacter chr = this;
            mapTimeLimitTask =
                    TimerManager.getInstance().register(new Runnable() {

                public void run() {
                    MaplePortal pfrom;
                    if (MapleItemInformationProvider.getInstance().isMiniDungeonMap(from.getId())) {
                        pfrom = from.getPortal("MD00");
                    } else {
                        pfrom = from.getPortal(0);
                    }

                    if (pfrom != null) {
                        chr.changeMap(from, pfrom);
                    }

                }
            }, from.getTimeLimit() * 1000, from.getTimeLimit() * 1000);
        }

    }

    public void cancelMapTimeLimitTask() {
        if (mapTimeLimitTask != null) {
            mapTimeLimitTask.cancel(false);
        }
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule) {
        if (effect.isHide()) {
            this.hidden = true;
            getClient().getSession().write(MaplePacketCreator.sendGMOperation(16, 1));
            getMap().broadcastMessage(this, MaplePacketCreator.removePlayerFromMap(getId()), false);
        } else if (effect.isDragonBlood()) {
            prepareDragonBlood(effect);
        }

        for (Pair<MapleBuffStat, Integer> statup : effect.getStatups()) {
            effects.put(statup.getLeft(), new MapleBuffStatValueHolder(effect, starttime, schedule, statup.getRight()));
        }

        recalcLocalStats();
    }

    private List<MapleBuffStat> getBuffStats(MapleStatEffect effect, long sTime) {
        List<MapleBuffStat> stats = new ArrayList<MapleBuffStat>();
        long startTime = sTime;
        Map<MapleBuffStat, MapleBuffStatValueHolder> lol = effects;
        for (Entry<MapleBuffStat, MapleBuffStatValueHolder> stateffectz : lol.entrySet()) {
            Entry<MapleBuffStat, MapleBuffStatValueHolder> stateffect = stateffectz;
            MapleBuffStatValueHolder mbsvh = stateffect.getValue();
            if (mbsvh.effect.sameSource(effect) && (startTime == -1 || startTime == mbsvh.startTime)) {
                stats.add(stateffect.getKey());
            }
        }
        return stats;
    }

    private void deregisterBuffStats(List<MapleBuffStat> stats) {
        List<MapleBuffStatValueHolder> effectsToCancel = new ArrayList<MapleBuffStatValueHolder>(stats.size());
        for (MapleBuffStat stat : stats) {
            MapleBuffStatValueHolder mbsvh = effects.get(stat);
            if (mbsvh != null) {
                effects.remove(stat);
                boolean addMbsvh = true;
                for (MapleBuffStatValueHolder contained : effectsToCancel) {
                    if (mbsvh.startTime == contained.startTime && contained.effect == mbsvh.effect) {
                        addMbsvh = false;
                    }

                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if (stat == MapleBuffStat.SUMMON || stat == MapleBuffStat.PUPPET) {
                    int summonId = mbsvh.effect.getSourceId();
                    MapleSummon summon = summons.get(summonId);
                    if (summon != null) {
                        getMap().broadcastMessage(MaplePacketCreator.removeSpecialMapObject(summon, true), summon.getPosition());
                        getMap().removeMapObject(summon);
                        removeVisibleMapObject(summon);
                        summons.remove(summonId);
                    }

                } else if (stat == MapleBuffStat.DRAGONBLOOD) {
                    dragonBloodSchedule.cancel(false);
                    dragonBloodSchedule =
                            null;
                }

            }
        }
        for (MapleBuffStatValueHolder cancelEffectCancelTasks : effectsToCancel) {
            if (getBuffStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).size() == 0) {
                cancelEffectCancelTasks.schedule.cancel(false);
            }

        }
    }

    /**
     * cancel effect
     * @param effect
     * @param overwrite when overwrite is set no data is sent and all the Buffstats in the StatEffect are deregistered
     * @param startTime
     */
    public void cancelEffect(MapleStatEffect lol, boolean overwrite, long stime) {
        List<MapleBuffStat> buffstats;
        MapleStatEffect effect = lol;
        long startTime = stime;
        if (!overwrite) {
            buffstats = getBuffStats(effect, startTime);
        } else {
            List<Pair<MapleBuffStat, Integer>> statups = effect.getStatups();
            buffstats = new ArrayList<MapleBuffStat>(statups.size());
            for (Pair<MapleBuffStat, Integer> statup : statups) {
                buffstats.add(statup.getLeft());
            }
        }
        deregisterBuffStats(buffstats);
        if (effect.isMagicDoor()) {
            // remove for all on maps
            if (!getDoors().isEmpty()) {
                MapleDoor door = getDoors().iterator().next();
                for (MapleCharacter chr : door.getTarget().getCharacters()) {
                    door.sendDestroyData(chr.getClient());
                }
                for (MapleCharacter chr : door.getTown().getCharacters()) {
                    door.sendDestroyData(chr.getClient());
                }
                for (MapleDoor destroyDoor : getDoors()) {
                    door.getTarget().removeMapObject(destroyDoor);
                    door.getTown().removeMapObject(destroyDoor);
                }
                clearDoors();
                silentPartyUpdate();
            }
        }

        if (!overwrite) {
            if (this != null) {
                cancelPlayerBuffs(buffstats);
                if (effect.isHide() && getMap().getMapObject(getObjectId()) != null) {
                    this.hidden = false;
                    getClient().getSession().write(MaplePacketCreator.sendGMOperation(16, 0));
                    getMap().broadcastMessage(this, MaplePacketCreator.spawnPlayerMapobject(this), false);
                    getMap().broadcastMessage(this, MaplePacketCreator.updateCharLook(this), false);
                    for (int i = 0; i < 3; i++) {
                        if (pets[i] != null) {
                            getMap().broadcastMessage(this, MaplePacketCreator.showPet(this, pets[i], false, false), false);
                        }
                    }
                    if (getChalkboard() != null) {
                        this.getMap().broadcastMessage(this, (MaplePacketCreator.useChalkboard(this, false)), false);
                    }
                }
            }
        }
    }

    public void cancelBuffStats(MapleBuffStat... stat) {
        List<MapleBuffStat> buffStatList = Arrays.asList(stat);
        deregisterBuffStats(buffStatList);
        cancelPlayerBuffs(buffStatList);
    }

    public void cancelEffectFromBuffStat(MapleBuffStat stat) {
        cancelEffect(effects.get(stat).effect, false, -1);
    }

    private void cancelPlayerBuffs(List<MapleBuffStat> buffstats) {
        if (getClient().getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) {
            recalcLocalStats();
            enforceMaxHpMp();

            getClient().getSession().write(MaplePacketCreator.cancelBuff(buffstats));
            getMap().broadcastMessage(this, MaplePacketCreator.cancelForeignBuff(getId(), buffstats), false);
        }

    }

    public void dispel() {
        if (isJounin() || isHidden()) {
            return;
        }

        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isSkill()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }

        }
    }

    public void cancelAllBuffs() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }

        cancelBuffStats(MapleBuffStat.SUMMON);
    }

    public void silentGiveBuffs(List<PlayerBuffValueHolder> buffs) {
        for (PlayerBuffValueHolder mbsvh : buffs) {
            mbsvh.effect.silentApplyBuff(this, mbsvh.startTime);
        }

    }

    public List<PlayerBuffValueHolder> getAllBuffs() {
        List<PlayerBuffValueHolder> ret = new ArrayList<PlayerBuffValueHolder>();
        for (MapleBuffStatValueHolder mbsvh : effects.values()) {
            ret.add(new PlayerBuffValueHolder(mbsvh.startTime, mbsvh.effect));
        }

        return ret;
    }

    public List<PlayerBuffValueHolder> getAllFakeBuffs() {
        List<PlayerBuffValueHolder> ret = new ArrayList<PlayerBuffValueHolder>();
        for (MapleBuffStatValueHolder mbsvh : effects.values()) {
            if (mbsvh.effect.isMonsterRiding() || mbsvh.effect.isMorph()) {
                ret.add(new PlayerBuffValueHolder(mbsvh.startTime, mbsvh.effect));
            }

        }
        return ret;
    }

    public void cancelMagicDoor() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMagicDoor()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }

        }
    }

    public void setEnergyBar(int set) {
        energybar = set;
    }

    public void handleEnergyChargeGain() {
        ISkill energycharge = getJobId() >= 1000 ? SkillFactory.getSkill(Skills.ThunderBreaker2.EnergyCharge) : SkillFactory.getSkill(Skills.Marauder.EnergyCharge);
        int energyChargeSkillLevel = getSkillLevel(energycharge);
        MapleStatEffect ceffect = null;
        ceffect = energycharge.getEffect(energyChargeSkillLevel);
        TimerManager tMan = TimerManager.getInstance();
        if (energyChargeSkillLevel > 0) {
            if (energybar < 10000) {
                energybar += 102;
                if (energybar > 10000) {
                    energybar = 10000;
                }
                client.getSession().write(MaplePacketCreator.giveEnergyCharge(energybar));
                client.getSession().write(MaplePacketCreator.showOwnBuffEffect(energycharge.getId(), 2));
                getMap().broadcastMessage(MaplePacketCreator.showBuffeffect(id, energycharge.getId(), 2));
                if (energybar == 10000) {
                    getMap().broadcastMessage(MaplePacketCreator.giveForeignEnergyCharge(id, energybar));
                }
            }
            if (energybar >= 10000 && energybar < 11000) {
                energybar = 15000;
                tMan.schedule(new Runnable() {

                    @Override
                    public void run() {
                        client.getSession().write(MaplePacketCreator.giveEnergyCharge(0));
                        getMap().broadcastMessage(MaplePacketCreator.giveForeignEnergyCharge(id, energybar));
                        energybar = 0;
                    }
                }, ceffect.getDuration());
            }
        }
    }

    public void handleOrbGain() {
        int orbcount = getBuffedValue(MapleBuffStat.COMBO);
        ISkill combo = SkillFactory.getSkill(Skills.Crusader.ComboAttack);
        ISkill advcombo = SkillFactory.getSkill(Skills.Hero.AdvancedComboAttack);
        // if (getSkillLevel(combo) == 0) {
        //   combo = SkillFactory.getSkill(Skills.DawnWarrior3.ComboAttack);
        // advcombo = SkillFactory.getSkill(Skills.DawnWarrior3.Advancedcombo);
        //}
        MapleStatEffect ceffect = advcombo.getEffect(30);
        if (orbcount < ceffect.getX() + 1) {
            int neworbcount = orbcount + 1;
            if (ceffect.makeChanceResult()) {
                if (neworbcount < ceffect.getX() + 1) {
                    neworbcount++;
                }

            }
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, neworbcount));
            setBuffedValue(MapleBuffStat.COMBO, neworbcount);
            int duration = ceffect.getDuration();
            duration +=
                    (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));
            getClient().getSession().write(MaplePacketCreator.giveBuff(combo.getId(), duration, stat));
            getMap().broadcastMessage(this, MaplePacketCreator.giveForeignBuff(getId(), stat, false), false);
        }

    }

    public void handleOrbconsume() {
        ISkill combo = SkillFactory.getSkill(Skills.Crusader.ComboAttack);
        if (getSkillLevel(combo) == 0) {
            combo = SkillFactory.getSkill(Skills.DawnWarrior3.ComboAttack);
        }

        MapleStatEffect ceffect = combo.getEffect(getSkillLevel(combo));
        List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, 1));
        setBuffedValue(MapleBuffStat.COMBO, 1);
        int duration = ceffect.getDuration();
        duration +=
                (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));
        getClient().getSession().write(MaplePacketCreator.giveBuff(combo.getId(), duration, stat));
        getMap().broadcastMessage(this, MaplePacketCreator.giveForeignBuff(getId(), stat, false), false);
    }

    private void silentEnforceMaxHpMp() {
        setMp(getMp());
        setHp(getHp(), true);
    }

    private void enforceMaxHpMp() {
        List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>(2);
        if (getMp() > getCurrentMaxMp()) {
            setMp(getMp());
            stats.add(new Pair<MapleStat, Integer>(MapleStat.MP, getMp()));
        }

        if (getHp() > getCurrentMaxHp()) {
            setHp(getHp());
            stats.add(new Pair<MapleStat, Integer>(MapleStat.HP, getHp()));
        }

        if (stats.size() > 0) {
            getClient().getSession().write(MaplePacketCreator.updatePlayerStats(stats));
        }

    }

    public MapleMap getMap() {
        return map;
    }

    public void setMap(MapleMap newmap) {
        this.map = newmap;
    }

    public int getMapId() {
        if (map != null) {
            return map.getId();
        }
        return mapid;
    }

    public int getInitialSpawnpoint() {
        return initialSpawnPoint;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreateDate() {
        return createdate;
    }

    public String getPreviousNames() {
        return previousnames;
    }

    public void addPreviousNames(String namez) {
        previousnames += " || ";
        previousnames += namez;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE `characters` SET `previousnames` = ? WHERE `id` = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, previousnames);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public int getLevel() {
        return level;
    }

    public int getFame() {
        return fame;
    }

    public int getStr() {
        return str;
    }

    public int getDex() {
        return dex;
    }

    public int getLuk() {
        return luk;
    }

    public int getInt() {
        return int_;
    }

    public MapleClient getClient() {
        return client;
    }

    public int getExp() {
        return exp.get();
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        if (GMSMode == 1) {
            return 5000;
        } else if (GMSMode == 2) {
            return 7500;
        } else if (GMSMode == 3) {
            return 10000;
        } else if (GMSMode == 4) {
            return 15000;
        } else {
            return 30000;
        }
    }

    public int getMp() {
        return mp;
    }

    public int getMaxMp() {
        if (GMSMode == 1) {
            return 5000;
        } else if (GMSMode == 2) {
            return 7500;
        } else if (GMSMode == 3) {
            return 10000;
        } else if (GMSMode == 4) {
            return 15000;
        } else {
            return 30000;
        }
    }

    public int getRemainingAp() {
        return remainingAp;
    }

    public int getRemainingSp() {
        return 32767;
    }

    public boolean isHidden() {
        return hidden;
    }

    public MapleSkinColor getSkinColor() {
        return skinColor;
    }

    public MapleJob getJob() {
        return job;
    }

    public int getGender() {
        return 2;
    }

    public int getHair() {
        return hair;
    }

    public int getFace() {
        return face;
    }

    public void setJob(MapleJob job) {
        this.job = job;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStr(int str) {
        this.str = str;
        recalcLocalStats();

    }

    public void setDex(int dex) {
        this.dex = dex;
        recalcLocalStats();

    }

    public void setLuk(int luk) {
        this.luk = luk;
        recalcLocalStats();
    }

    public void setInt(int int_) {
        this.int_ = int_;
        recalcLocalStats();

    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public void setRemainingAp(int remainingAp) {
        this.remainingAp = remainingAp;
    }

    public void setSkinColor(MapleSkinColor skinColor) {
        this.skinColor = skinColor;
    }

    public CheatTracker getCheatTracker() {
        return anticheat;
    }

    public BuddyList getBuddylist() {
        return buddylist;
    }

    public void addFame(int famechange) {
        this.fame += famechange;
        if (fame > 1337 && !haveItem(1142003, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 11420043, name, (short) 1337, (short) 50, (short) 50);
            dropMessage("[The Elite ninja Gang] You have gained a Celebrity for reaching 1337 Fame");
        }
        if (fame > 13337 && !haveItem(1142006, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 11420006, name, (short) 13337, (short) 100, (short) 100);
            dropMessage("[The Elite ninja Gang] You have gained a MapleIdol for reaching 13337 Fame");
        }
    }

    public void setFame(int fuck) {
        this.fame += fuck;
    }

    public void changeMap(final MapleMap to,
            final Point pos) {
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, 0x80, this);
        changeMapInternal(to, pos, warpPacket);
    }

    public void changeMap(final MapleMap to,
            final MaplePortal pto) {
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, pto.getId(), this);
        changeMapInternal(to, pto.getPosition(), warpPacket);
    }

    private void changeMapInternal(final MapleMap to, final Point pos, MaplePacket warpPacket) {
        if (NPCScriptManager.getInstance().getCM(client) != null) {
            NPCScriptManager.getInstance().dispose(client);
        }
        removeClones();
        if (isChunin() && !noHide) {
            this.hide();
        }
        if (isHidden() && !noHide) {
            getClient().getSession().write(MaplePacketCreator.sendGMOperation(16, 1));
        }
        warpPacket.setOnSend(new Runnable() {

            @Override
            public void run() {
                map.removePlayer(MapleCharacter.this);
                if (getClient().getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) {
                    map = to;
                    setPosition(pos);
                    to.addPlayer(MapleCharacter.this);
                    if (party != null) {
                        silentPartyUpdate();
                        getClient().getSession().write(MaplePacketCreator.updateParty(getClient().getChannel(), party, PartyOperation.SILENT_UPDATE, null));
                        updatePartyMemberHP();
                    }
                }
            }
        });
        getClient().getSession().write(warpPacket);
        /*
        for (int i = 0; i < 3; i++) {
        if (pets[i] != null) {
        client.getSession().write(MaplePacketCreator.showPet(this, pets[i], false, false));
        }
        }*/
    }

    public void leaveMap() {
        controlled.clear();
        visibleMapObjects.clear();
        if (chair != 0) {
            chair = 0;
        }

    }

    public void changeJob(MapleJob newJob) {
        int jid = this.job.getId();
        this.job = newJob;
        updateSingleStat(MapleStat.JOB, newJob.getId());
        getMap().broadcastMessage(this, MaplePacketCreator.showJobChange(getId()), false);
        this.maxSkills(true);
        silentPartyUpdate();
        guildUpdate();
    }

    public void gainAp(int ap) {
        if (this.remainingAp + ap > 32767) {
            dropMessage("You tried to get more than 32767 AP so you got scammed");
            ap = 32767 - this.remainingAp;
        }

        this.remainingAp += ap;
        updateSingleStat(MapleStat.AVAILABLEAP, this.remainingAp);
    }

    public void changeSkillLevel(ISkill skill, int newLevel, int newMasterlevel) {
        skills.put(skill, new SkillEntry(newLevel, newMasterlevel));
        this.getClient().getSession().write(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel));
    }

    public void setHp(int newhp) {
        setHp(newhp, false);
    }

    public void setHp(int newhp, boolean silent) {
        int oldHp = hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > localmaxhp) {
            thp = localmaxhp;
        }
        this.hp = thp;
        if (!silent) {
            updatePartyMemberHP();
        }
        if (oldHp > hp && !isAlive()) {
            playerDead();
        }
    }

    private void playerDead() {
        if (getEventInstance() != null) {
            getEventInstance().playerKilled(this);
        }

        cancelAllBuffs();
        getClient().getSession().write(MaplePacketCreator.enableActions());
    }

    public void updatePartyMemberHP() {
        if (party != null) {
            int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        other.getClient().getSession().write(
                                MaplePacketCreator.updatePartyMemberHP(getId(), this.hp, localmaxhp));
                    }

                }
            }
        }
    }

    public void receivePartyMemberHP() {
        if (party != null) {
            int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        getClient().getSession().write(
                                MaplePacketCreator.updatePartyMemberHP(other.getId(), other.getHp(), other.getCurrentMaxHp()));
                    }

                }
            }
        }
    }

    public void setMp(int newmp) {
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }

        if (tmp > localmaxmp) {
            tmp = localmaxmp;
        }

        this.mp = tmp;
    }

    /**
     * Convenience function which adds the supplied parameter to the current hp then directly does a updateSingleStat.
     *
     * @see MapleCharacter#setHp(int)
     * @param delta : int
     */
    public void addHP(int delta) {
        setHp(hp + delta);
        updateSingleStat(MapleStat.HP, hp);
    }

    /**
     * Convenience function which adds the supplied parameter to the current mp then directly does a updateSingleStat.
     *
     * @see MapleCharacter#setMp(int)
     * @param delta
     */
    public void addMP(int delta) {
        setMp(mp + delta);
        updateSingleStat(MapleStat.MP, mp);
    }

    public void addMPHP(int hpDiff, int mpDiff) {
        setHp(hp + hpDiff);
        setMp(mp + mpDiff);
        List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>();
        stats.add(new Pair<MapleStat, Integer>(MapleStat.HP, hp));
        stats.add(new Pair<MapleStat, Integer>(MapleStat.MP, mp));
        MaplePacket updatePacket = MaplePacketCreator.updatePlayerStats(stats);
        client.getSession().write(updatePacket);
    }

    /**
     * Updates a single stat of this MapleCharacter for the client. This method only creates and sends an update packet,
     * it does not update the stat stored in this MapleCharacter instance.
     *
     * @param stat
     * @param newval
     * @param itemReaction
     */
    public void updateSingleStat(MapleStat stat, int newval, boolean itemReaction) {
        Pair<MapleStat, Integer> statpair = new Pair<MapleStat, Integer>(stat, newval);
        MaplePacket updatePacket = MaplePacketCreator.updatePlayerStats(Collections.singletonList(statpair), itemReaction);
        client.getSession().write(updatePacket);
    }

    public void updateSingleStat(MapleStat stat, int newval) {
        updateSingleStat(stat, newval, false);
    }

    public void gainExp(int gain, boolean show, boolean inChat, boolean white, short partymembers) {
        if (rebirthing) {
            return;
        }
        int maxlevel = 255;
        if (level == maxlevel) {
            setExp(0);
            updateSingleStat(MapleStat.EXP, 0);
            return;
        }
        if (getExp() < 0) {
            client.showMessage(5, "You had negative EXP and now its Fixed by Janet the Pervie ninja.");
            levelUp();
            updateSingleStat(MapleStat.LEVEL, level);
            setExp(0);
            updateSingleStat(MapleStat.EXP, 0);
        }
        if (level < maxlevel) {
            if ((long) this.exp.get() + (long) gain > (long) Integer.MAX_VALUE) {
                int gainFirst = ExpTable.getExpNeededForLevel(level) - this.exp.get();
                gain -=
                        gainFirst + 1;
                this.gainExp(gainFirst + 1, false, inChat, white, partymembers);
            }
            updateSingleStat(MapleStat.EXP, this.exp.addAndGet(gain));
        } else {
            return;
        }
        if (show && gain != 0) {
            client.getSession().write(MaplePacketCreator.getShowExpGain(gain, inChat, white, partymembers));
        }

        if (exp.get() >= ExpTable.getExpNeededForLevel(level) && level < maxlevel) {
            while (level < maxlevel && exp.get() >= ExpTable.getExpNeededForLevel(level)) {
                levelUp();
            }

        }
    }

    public int getMaxLevel() {
        int reborns = this.getReborns();
        if (reborns < 10) {
            return 200;
        } else if (reborns < 50) {
            return 205;
        } else if (reborns < 100) {
            return 210;
        } else if (reborns < 200) {
            return 215;
        } else if (reborns < 400) {
            return 220;
        } else if (reborns < 800) {
            return 225;
        } else if (reborns < 1200) {
            return 230;
        } else if (reborns < 1600) {
            return 235;
        } else if (reborns < 2000) {
            return 240;
        } else if (reborns < 2500) {
            return 250;
        } else {
            return 255;
        }

    }

    public void silentPartyUpdate() {
        if (party != null) {
            try {
                getClient().getChannelServer().getWorldInterface().updateParty(party.getId(),
                        PartyOperation.SILENT_UPDATE, new MaplePartyCharacter(MapleCharacter.this));
            } catch (RemoteException e) {
                log.error("REMOTE THROW", e);
                getClient().getChannelServer().reconnectWorld();
            }

        }
    }

    public void gainExp(int gain, boolean show, boolean inChat) {
        gainExp(gain, show, inChat, true);
    }

    public void gainExp(int gain, boolean show, boolean inChat, boolean white) {
        gainExp(gain, show, inChat, white, (short) 0);
    }

    public MapleInventory getInventory(
            MapleInventoryType type) {
        return inventory[type.ordinal()];
    }

    public MapleShop getShop() {
        return shop;
    }

    public void setShop(MapleShop shop) {
        this.shop = shop;
    }

    public int getMeso() {
        return meso.get();
    }

    public int getSavedLocation(String type) {
        int m = savedLocations[SavedLocationType.fromString(type).ordinal()].getMapId();
        clearSavedLocation(SavedLocationType.fromString(type));
        return m;
    }

    public void saveLocation(String type) {
        MaplePortal closest = map.findClosestSpawnpoint(getPosition());
        savedLocations[SavedLocationType.fromString(type).ordinal()] = new SavedLocation(getMapId(), closest != null ? closest.getId() : 0);
    }

    public void clearSavedLocation(SavedLocationType type) {
        savedLocations[type.ordinal()] = null;
    }

    public void gainMeso(int gain, boolean show) {
        gainMeso(gain, show, false, false);
    }

    public void setMeso(int fuck) {
        meso.set(fuck);
    }

    public void gainMeso(int gain, boolean show, boolean enableActions) {
        gainMeso(gain, show, enableActions, false);
    }

    public void gainMeso(int gain, boolean show, boolean enableActions, boolean inChat) {
        if (meso.get() + gain < 0) {
            client.getSession().write(MaplePacketCreator.enableActions());
            return;

        }
        int newVal = meso.addAndGet(gain);
        updateSingleStat(MapleStat.MESO, newVal, enableActions);
        if (show) {
            client.getSession().write(MaplePacketCreator.getShowMesoGain(gain, inChat));
        }
    }

    /**
     * Adds this monster to the controlled list. The monster must exist on the Map.
     *
     * @param monster
     * @param aggro
     */
    public void controlMonster(MapleMonster monster, boolean aggro) {
        monster.setController(this);
        controlled.add(monster);
        client.getSession().write(MaplePacketCreator.controlMonster(monster, false, aggro));
    }

    public void stopControllingMonster(MapleMonster monster) {
        controlled.remove(monster);
    }

    public Collection<MapleMonster> getControlledMonsters() {
        return Collections.unmodifiableCollection(controlled);
    }

    public int getNumControlledMonsters() {
        return controlled.size();
    }

    @Override
    public String toString() {
        return "Character: " + this.name;
    }

    public int getAccountID() {
        return accountid;
    }

    public void mobKilled() {
        this.mobkilled++;
        if (mobkilled > 50000 && !haveItem(1142004, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142004, name, (short) 1337, (short) 25, (short) 25);
            dropMessage("[The Elite ninja Gang] You have gained a veteran hunter medal for killing 50000 monsters");
        }
    }

    public void bossKilled() {
        this.bosskilled++;
        if (mobkilled > 50000 && !haveItem(1142005, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142005, name, (short) 13337, (short) 50, (short) 50);
            dropMessage("[The Elite ninja Gang] You have gained a Legendary hunter for killing 50000 boss monsters");
        }
    }

    public int getMobKilled() {
        return this.mobkilled;
    }

    public int getBossKilled() {
        return this.bosskilled;
    }

    public final List<MapleQuestStatus> getStartedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<MapleQuestStatus>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus().equals(MapleQuestStatus.Status.STARTED) && !(q.getQuest() instanceof MapleCustomQuest)) {
                ret.add(q);
            }

        }
        return Collections.unmodifiableList(ret);
    }

    public final List<MapleQuestStatus> getCompletedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<MapleQuestStatus>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus().equals(MapleQuestStatus.Status.COMPLETED) && !(q.getQuest() instanceof MapleCustomQuest)) {
                ret.add(q);
            }

        }
        return Collections.unmodifiableList(ret);
    }

    public MaplePlayerShop getPlayerShop() {
        return playerShop;
    }

    public void setPlayerShop(MaplePlayerShop playerShop) {
        this.playerShop = playerShop;
    }

    public Map<ISkill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    public int getSkillLevel(ISkill skill) {
        SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }

        return ret.skillevel;
    }

    public int getMasterLevel(ISkill skill) {
        SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }

        return ret.masterlevel;
    }

// the equipped inventory only contains equip... I hope
    public int getTotalDex() {
        return localdex;
    }

    public int getTotalInt() {
        return localint_;
    }

    public int getTotalStr() {
        return localstr;
    }

    public int getTotalLuk() {
        return localluk;
    }

    public int getTotalMagic() {
        return magic;
    }

    public double getSpeedMod() {
        return speedMod;
    }

    public double getJumpMod() {
        return jumpMod;
    }

    public int getTotalWatk() {
        return watk;
    }

    public void levelUp() {
        if (this.level >= 255) {
            this.level = 255;
            exp.set(0);
            return;
        }
        if (level < 0) {
            this.level = 1;
            exp.set(0);
            return;
        }
        if (this.level < this.getMaxLevel()) {
            byte gain = getApPerLevel();
            doAutoAp(gain);
        } else {
            if (level % 5 == 0) {
                dropMessage("You are over the required Level for your Rebirth. So you will not gain Any AP");
            }
        }
        exp.addAndGet(-ExpTable.getExpNeededForLevel(level + 1));
        level++;
        if (level == getMaxLevel()) {
            MaplePacket packet = MaplePacketCreator.serverNotice(0, "Congratulations to you for reaching max level! You can now Rebirth by typing @rebirth");
            getClient().getSession().write(packet);
        }
        hp = getMaxHp();
        mp = getMaxMp();
        List<Pair<MapleStat, Integer>> statup = new ArrayList<Pair<MapleStat, Integer>>(8);
        statup.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, remainingAp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.EXP, exp.get()));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.LEVEL, level));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.HP, hp));
        statup.add(new Pair<MapleStat, Integer>(MapleStat.MP, mp));
        getClient().getSession().write(MaplePacketCreator.updatePlayerStats(statup));
        getMap().broadcastMessage(this, MaplePacketCreator.showLevelup(getId()), false);
        recalcLocalStats();
        silentPartyUpdate();
        if (level == 200) {
            levelUp();
            dropMessage(5, "[The Elite Ninja Gang]Free 1 level for you as you reached a landmark level of 200");
        }
    }

    public void changeKeybinding(int key, MapleKeyBinding keybinding) {
        if (keybinding.getType() != 0) {
            keymap.put(key, keybinding);
        } else {
            keymap.remove(key);
        }
        keymapchange = true;
    }

    public void setKeyMap(Map<Integer, MapleKeyBinding> keys) {
        keymap.clear();
        keymap = keys;
        sendKeymap();
    }

    public void sendKeymap() {
        getClient().getSession().write(MaplePacketCreator.getKeymap(keymap));
    }

    public void loadKeyMapFromDB(Connection con) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int key = rs.getInt("key");
                int type = rs.getInt("type");
                int action = rs.getInt("action");
                this.keymap.put(key, new MapleKeyBinding(type, action));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadMacrosFromDB(Connection con) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM skillmacros WHERE characterid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int skill1 = rs.getInt("skill1");
                int skill2 = rs.getInt("skill2");
                int skill3 = rs.getInt("skill3");
                String name = rs.getString("name");
                int shout = rs.getInt("shout");
                int position = rs.getInt("position");
                SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, position);
                this.skillMacros[position] = macro;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Map<Integer, MapleKeyBinding> getKeymap() {
        return keymap;
    }

    public void sendMacros() { // Credits : Oliver        
        getClient().getSession().write(MaplePacketCreator.getMacros(skillMacros));
    }

    public SkillMacro[] getSkillMacros() {
        return skillMacros;
    }

    public void setMacros(SkillMacro[] fuck) {
        skillMacros = null;
        skillMacros = fuck;
        sendMacros();
    }

    public void updateMacros(int position, SkillMacro updateMacro) {
        skillMacros[position] = updateMacro;
        macrochange = true;
    }

    public void tempban(String reason, Calendar duration, int greason) {
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }

        tempban(reason, duration, greason, client.getAccID());
        client.getSession().close();
    }

    public static boolean tempban(String reason, Calendar duration, int greason, int accountid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?");
            Timestamp TS = new Timestamp(duration.getTimeInMillis());
            ps.setTimestamp(1, TS);
            ps.setString(2, reason);
            ps.setInt(3, greason);
            ps.setInt(4, accountid);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException ex) {
            log.error("Error while tempbanning", ex);
        }

        return false;
    }

    public void ban(String reason) {
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }

        try {
            getClient().banMacs();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ?");
            ps.setInt(1, 1);
            ps.setString(2, reason);
            ps.setInt(3, accountid);
            ps.executeUpdate();
            ps.close();
            ps =
                    con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
            String[] ipSplit = client.getSession().getRemoteAddress().toString().split(":");
            ps.setString(1, ipSplit[0]);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            log.error("Error while banning", ex);
        }

        client.getSession().close();
    }

    public static boolean ban(String id, String reason, boolean accountId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;

            if (id.matches("/[0-9]{1,3}\\..*")) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, id);
                ps.executeUpdate();
                ps.close();
                return true;
            }

            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            } else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            }

            boolean ret = false;
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PreparedStatement psb = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?");
                psb.setString(1, reason);
                psb.setInt(2, rs.getInt(1));
                psb.executeUpdate();
                psb.close();
                ret =
                        true;
            }

            rs.close();
            ps.close();
            return ret;
        } catch (SQLException ex) {
            log.error("Error while banning", ex);
        }

        return false;
    }

    /**
     * Oid of players is always = the cid
     */
    @Override
    public int getObjectId() {
        return getId();
    }

    /**
     * Throws unsupported operation exception, oid of players is read only
     */
    @Override
    public void setObjectId(int id) {
        throw new UnsupportedOperationException();
    }

    public MapleStorage getStorage() {
        return storage;
    }

    public int getCurrentMaxHp() {
        return localmaxhp;
    }

    public int getCurrentMaxMp() {
        return localmaxmp;
    }

    public int getCurrentMaxBaseDamage() {
        return localmaxbasedamage;
    }

    public int calculateMaxBaseDamage(int watk) {
        int maxbasedamage;
        if (watk == 0) {
            maxbasedamage = 1;
        } else {
            IItem weapon_item = getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
            if (weapon_item != null) {
                MapleWeaponType weapon = MapleItemInformationProvider.getInstance().getWeaponType(weapon_item.getItemId());
                int mainstat;
                int secondarystat;
                if (weapon == MapleWeaponType.BOW || weapon == MapleWeaponType.CROSSBOW) {
                    mainstat = localdex;
                    secondarystat =
                            localstr;
                } else if ((getJob().isA(MapleJob.THIEF) || getJob().isA(MapleJob.NIGHTWALKER1)) && (weapon == MapleWeaponType.CLAW || weapon == MapleWeaponType.DAGGER)) {
                    mainstat = localluk;
                    secondarystat =
                            localdex + localstr;
                } else {
                    mainstat = localstr;
                    secondarystat =
                            localdex;
                }

                maxbasedamage = (int) (((weapon.getMaxDamageMultiplier() * mainstat + secondarystat) / 100.0) * watk);
                //just some saveguard against rounding errors, we want to a/b for this
                maxbasedamage +=
                        10;
            } else {
                maxbasedamage = 0;
            }

        }
        return maxbasedamage;
    }

    public void addVisibleMapObject(MapleMapObject mo) {
        visibleMapObjects.add(mo);
    }

    public void removeVisibleMapObject(MapleMapObject mo) {
        visibleMapObjects.remove(mo);
    }

    public boolean isMapObjectVisible(MapleMapObject mo) {
        return visibleMapObjects.contains(mo);
    }

    public Collection<MapleMapObject> getVisibleMapObjects() {
        return Collections.unmodifiableCollection(visibleMapObjects);
    }

    public boolean isAlive() {
        return this.hp > 0;
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.removePlayerFromMap(this.getObjectId()));
    }

    public void sendSpawnData(MapleClient client) {
        if (!this.isHidden()) {
            client.getSession().write(MaplePacketCreator.spawnPlayerMapobject(this));
            for (int i = 0; i < 3; i++) {
                if (pets[i] != null) {
                    client.getSession().write(MaplePacketCreator.showPet(this, pets[i], false, false));
                } else {
                    break;
                }
            }
            if (getChalkboard() != null) {
                client.getSession().write(MaplePacketCreator.useChalkboard(this, false));
            }
        }
    }

    public void recalcLocalStats() {
        int oldmaxhp = localmaxhp;
        localmaxhp = getMaxHp();
        localmaxmp = getMaxMp();
        localdex = getDex();
        localint_ = getInt();
        localstr = getStr();
        localluk = getLuk();
        int speed = 100;
        int jump = 100;
        magic = localint_;
        watk = 0;
        for (IItem item : getInventory(MapleInventoryType.EQUIPPED)) {
            IEquip equip = (IEquip) item;
            localmaxhp += equip.getHp();
            localmaxmp += equip.getMp();
            localdex += equip.getDex();
            localint_ += equip.getInt();
            localstr += equip.getStr();
            localluk += equip.getLuk();
            magic += equip.getMatk() + equip.getInt();
            watk += equip.getWatk();
            speed += equip.getSpeed();
            jump += equip.getJump();
        }
        magic = Math.min(magic, 2000);
        Integer hbhp = getBuffedValue(MapleBuffStat.HYPERBODYHP);
        if (hbhp != null) {
            localmaxhp += (hbhp.doubleValue() / 100) * localmaxhp;
        }
        Integer hbmp = getBuffedValue(MapleBuffStat.HYPERBODYMP);
        if (hbmp != null) {
            localmaxmp += (hbmp.doubleValue() / 100) * localmaxmp;
        }
        localmaxhp = Math.min(30000, localmaxhp);
        localmaxmp = Math.min(30000, localmaxmp);
        Integer watkbuff = getBuffedValue(MapleBuffStat.WATK);
        if (watkbuff != null) {
            watk += watkbuff;
        }
        if (job.isA(MapleJob.BOWMAN)) {
            ISkill expert = null;
            if (job.isA(MapleJob.CROSSBOWMASTER)) {
                expert = SkillFactory.getSkill(Skills.Marksman.MarksmanBoost);
            } else if (job.isA(MapleJob.BOWMASTER)) {
                expert = SkillFactory.getSkill(Skills.Bowmaster.BowExpert);
            }

            if (expert != null) {
                int boostLevel = getSkillLevel(expert);
                if (boostLevel > 0) {
                    watk += expert.getEffect(boostLevel).getX();
                }
            }
        }
        Integer matkbuff = getBuffedValue(MapleBuffStat.MATK);
        if (matkbuff != null) {
            magic += matkbuff;
        }
        Integer speedbuff = getBuffedValue(MapleBuffStat.SPEED);
        if (speedbuff != null) {
            speed += speedbuff;
        }
        Integer jumpbuff = getBuffedValue(MapleBuffStat.JUMP);
        if (jumpbuff != null) {
            jump += jumpbuff;
        }
        if (speed > 140) {
            speed = 140;
        }
        if (jump > 123) {
            jump = 123;
        }
        speedMod = speed / 100.0;
        jumpMod =
                jump / 100.0;
        Integer mount = getBuffedValue(MapleBuffStat.MONSTER_RIDING);
        if (mount != null) {
            switch (mount) {
                case 1004:
                    int mountId = getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18).getItemId();
                    switch (mountId) {
                        case 1902000:
                            speedMod = 1.5;
                            break;
                        case 1902001:
                            speedMod = 1.7;
                            break;
                        case 1902002:
                            speedMod = 1.8;
                            break;
                    }
                    jumpMod = 1.23;
                    break;
                case 5221006:
                    // AFAIK Battleship doesn't give any speed/jump bonuses
                    break;
                default:
                    log.warn("Unhandeled monster riding level");
            }
        }
        localmaxbasedamage = calculateMaxBaseDamage(watk);
        if (oldmaxhp != 0 && oldmaxhp != localmaxhp) {
            updatePartyMemberHP();
        }
    }

    public void equipChanged() {
        getMap().broadcastMessage(this, MaplePacketCreator.updateCharLook(this), false);
        recalcLocalStats();

        enforceMaxHpMp();

        if (getClient().getPlayer().getMessenger() != null) {
            WorldChannelInterface wci = ChannelServer.getInstance(getClient().getChannel()).getWorldInterface();
            try {
                wci.updateMessenger(getClient().getPlayer().getMessenger().getId(), getClient().getPlayer().getName(), getClient().getChannel());
            } catch (RemoteException e) {
                getClient().getChannelServer().reconnectWorld();
            }

        }
    }

    public MaplePet getPet(int index) {
        return pets[index];
    }

    public void addPet(MaplePet pet) {
        for (int i = 0; i < 3; i++) {
            if (pets[i] == null) {
                pets[i] = pet;
                return;
            }
        }
    }

    public void removePet(MaplePet pet, boolean shift_left) {
        byte slot = -1;
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                if (pets[i].getUniqueId() == pet.getUniqueId()) {
                    pets[i] = null;
                    slot = (byte) i;
                    break;
                }
            }
        }
        if (shift_left) {
            if (slot > -1) {
                for (int i = slot; i < 3; i++) {
                    if (i != 2) {
                        pets[i] = pets[i + 1];
                    } else {
                        pets[i] = null;
                    }
                }
            }
        }
    }

    public int getNoPets() {
        int ret = 0;
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                ret++;
            }
        }
        return ret;
    }

    public int getPetIndex(MaplePet pet) {
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                if (pets[i].getUniqueId() == pet.getUniqueId()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getPetIndex(int petId) {
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                if (pets[i].getUniqueId() == petId) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getNextEmptyPetIndex() {
        if (pets[0] == null) {
            return 0;
        }
        if (pets[1] == null) {
            return 1;
        }
        if (pets[2] == null) {
            return 2;
        }
        return 3;
    }

    public MaplePet[] getPets() {
        return pets;
    }

    public void unequipAllPets() {
        while (getNoPets() > 0) {
            for (int i = 0; i < 3; i++) {
                if (pets[i] != null) {
                    unequipPet(pets[i], true);
                }
            }
        }
    }

    public void removePets() {
        unequipAllPets();
    }

    public void unequipPet(MaplePet pet, boolean shift_left) {
        unequipPet(pet, shift_left, false);
    }

    public void unequipPet(MaplePet pet, boolean shift_left, boolean hunger) {
        pet.saveToDb();
        // Broadcast the packet to the map - with null instead of MaplePet
        getMap().broadcastMessage(this, MaplePacketCreator.showPet(this, pet, true, hunger), true);
        // Make a new list for the stat updates
        // Write the stat update to the player...
        getClient().getSession().write(MaplePacketCreator.petStatUpdate(this));
        getClient().getSession().write(MaplePacketCreator.enableActions());
        // Un-assign the pet set to the player
        removePet(pet, shift_left);
    }

    public void spawnLoadedPet(MaplePet petzor, boolean lead) {
        if (lead) {
            shiftPetsRight();
        }
        Point pos = getPosition();
        pos.y -= 12;
        petzor.setPos(pos);
        petzor.setFh(getMap().getFootholds().findBelow(petzor.getPos()).getId());
        petzor.setStance(0);
        addPet(petzor);
        getMap().broadcastMessage(this, MaplePacketCreator.showPet(this, petzor, false), true);
        int uniqueid = petzor.getUniqueId();
        List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>();
        stats.add(new Pair<MapleStat, Integer>(MapleStat.PET, Integer.valueOf(uniqueid)));
        getClient().getSession().write(MaplePacketCreator.petStatUpdate(this));
        getClient().getSession().write(MaplePacketCreator.enableActions());
    }

    public void shiftPetsRight() {
        if (pets[2] == null) {
            pets[2] = pets[1];
            pets[1] = pets[0];
            pets[0] = null;
        }

    }

    public FameStatus canGiveFame(
            MapleCharacter from) {
        if (lastfametime >= System.currentTimeMillis() - 60 * 60 * 24 * 1000) {
            return FameStatus.NOT_TODAY;
        } else if (lastmonthfameids.contains(Integer.valueOf(from.getId()))) {
            return FameStatus.NOT_THIS_MONTH;
        } else {
            return FameStatus.OK;
        }

    }

    public void hasGivenFame(MapleCharacter to) {
        lastfametime = System.currentTimeMillis();
        lastmonthfameids.add(to.getId());
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)");
            ps.setInt(1, getId());
            ps.setInt(2, to.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("ERROR writing famelog for char " + getName() + " to " + to.getName(), e);
        }

    }

    public MapleParty getParty() {
        return party;
    }

    public int getPartyId() {
        return (party != null ? party.getId() : -1);
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public void setParty(MapleParty party) {
        this.party = party;
    }

    public MapleTrade getTrade() {
        return trade;
    }

    public void setTrade(MapleTrade trade) {
        this.trade = trade;
    }

    public EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public void addDoor(MapleDoor door) {
        doors.add(door);
    }

    public void clearDoors() {
        doors.clear();
    }

    public List<MapleDoor> getDoors() {
        return new ArrayList<MapleDoor>(doors);
    }

    public boolean canDoor() {
        return canDoor;
    }

    public void disableDoor() {
        canDoor = false;
        TimerManager tMan = TimerManager.getInstance();
        tMan.schedule(new Runnable() {

            @Override
            public void run() {
                canDoor = true;
            }
        }, 5000);
    }

    public Map<Integer, MapleSummon> getSummons() {
        return summons;
    }

    public int getChair() {
        return chair;
    }

    public int getItemEffect() {
        return itemEffect;
    }

    public void setChair(int chair) {
        this.chair = chair;
    }

    public void setItemEffect(int itemEffect) {
        this.itemEffect = itemEffect;
    }

    @Override
    public Collection<MapleInventory> allInventories() {
        return Arrays.asList(inventory);
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    public MapleGuild getGuild() {
        try {
            return getClient().getChannelServer().getWorldInterface().getGuild(getGuildId(), null);
        } catch (RemoteException ex) {
            client.getChannelServer().reconnectWorld();
        }
        return null;
    }

    public int getGuildId() {
        return guildid;
    }

    public int getGuildRank() {
        return guildrank;
    }

    public void setGuildId(int _id) {
        if (isfake) {
            return;
        }
        guildid = _id;
        if (guildid > 0) {
            if (mgc == null) {
                mgc = new MapleGuildCharacter(this);
            } else {
                mgc.setGuildId(guildid);
            }

        } else {
            mgc = null;
        }
    }

    public void deleteGuild(int guildId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = 0, guildrank = 5 WHERE guildid = ?");
            ps.setInt(1, guildId);
            ps.execute();
            ps.close();
            ps = con.prepareStatement("DELETE FROM guilds WHERE guildid = ?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
            System.out.print("Error deleting guild: " + ex);
        }
    }

    public void setAllianceRank(int rank) {
        allianceRank = rank;
        if (mgc != null) {
            mgc.setAllianceRank(rank);
        }
    }

    public int getAllianceRank() {
        return this.allianceRank;
    }

    public void setGuildRank(int _rank) {
        guildrank = _rank;
        if (mgc != null) {
            mgc.setGuildRank(_rank);
        }
    }

    public MapleGuildCharacter getMGC() {
        return mgc;
    }

    public void guildUpdate() {
        if (this.guildid <= 0) {
            return;
        }
        mgc.setReborns(reborn);
        mgc.setJobId(job.getId());
        try {
            this.client.getChannelServer().getWorldInterface().memberLevelJobUpdate(this.mgc);
            int allianceId = getGuild().getAllianceId();
            if (allianceId > 0) {
                client.getChannelServer().getWorldInterface().allianceMessage(allianceId, MaplePacketCreator.updateAllianceJobLevel(this), getId(), -1);
            }
        } catch (RemoteException re) {
            log.error("RemoteExcept while trying to update level/job in guild.", re);
        }
    }
    private NumberFormat nf = new DecimalFormat("#,###,###,###");

    public String guildCost() {
        return nf.format(MapleGuild.CREATE_GUILD_COST);
    }

    public String emblemCost() {
        return nf.format(MapleGuild.CHANGE_EMBLEM_COST);
    }

    public String capacityCost() {
        return nf.format(MapleGuild.CREATE_GUILD_COST);
    }

    public void genericGuildMessage(int code) {
        this.client.getSession().write(MaplePacketCreator.genericGuildMessage((byte) code));
    }

    public void disbandGuild() {
        if (guildid <= 0 || guildrank != 1) {
            log.warn(this.name + " tried to disband and s/he is either not in a guild or not leader.");
            return;
        }
        try {
            client.getChannelServer().getWorldInterface().disbandGuild(this.guildid);
        } catch (Exception e) {
            log.error("Error while disbanding guild.", e);
        }
    }

    public void increaseGuildCapacity() {
        if (this.getMeso() < MapleGuild.CREATE_GUILD_COST) {
            client.getSession().write(MaplePacketCreator.serverNotice(1, "You do not have enough mesos."));
            return;
        }
        if (this.guildid <= 0) {
            log.info(this.name + " is trying to increase guild capacity without being in the guild.");
            return;
        }
        try {
            client.getChannelServer().getWorldInterface().increaseGuildCapacity(this.guildid);
        } catch (Exception e) {
            log.error("Error while increasing capacity.", e);
            return;
        }
        this.gainMeso(-MapleGuild.CREATE_GUILD_COST, true, false, true);
    }

    public void saveGuildStatus() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = ?, guildrank = ? WHERE id = ?");
            ps.setInt(1, this.guildid);
            ps.setInt(2, this.guildrank);
            ps.setInt(3, this.id);
            ps.execute();
            ps.close();
        } catch (SQLException se) {
            log.error("SQL error: " + se.getLocalizedMessage(), se);
        }
    }

    /**
     * Allows you to change someone's NXCash, Maple Points, and Gift Tokens!
     *
     * Created by Acrylic/Penguins
     *
     * @param type: 0 = NX, 1 = MP, 2 = GT
     * @param quantity: how much to modify it by. Negatives subtract points, Positives add points.
     */
    public void addCSPoints(int type, int quantity) {
        modifyCSPoints(type, quantity);
    }

    public void modifyCSPoints(int type, int quantity) {
        if (type == 0) {
            this.cardNX += quantity;
        } else if (type == 1) {
            this.maplePoints += quantity;
        } else if (type == 2) {
            this.paypalNX += quantity;
        }
    }

    public int getCSPoints(int type) {
        if (type == 0) {
            return this.cardNX;
        } else if (type == 1) {
            return this.maplePoints;
        } else if (type == 2) {
            return this.paypalNX;
        } else {
            return 0;
        }
    }

    public boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    public boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemid);
        MapleInventory iv = inventory[type.ordinal()];
        int possesed = iv.countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        } else {
            return possesed == quantity;
        }
    }

    public void setLeetness(boolean b) {
        this.leet = b;
    }

    public boolean getLeetness() {
        return leet;
    }

    public void goHome() {
        if (getMute() == 1) {
            unMute();
        }
        if (this.getEventInstance() != null) {
            this.getEventInstance().unregisterPlayer(this);
        }
        changeMap(100000000, 0);
    }

    public byte getMaxStatItems() {
        return maxstatitem;
    }

    public void setMaxStatItem(byte fuck) {
        this.maxstatitem = fuck;
    }

    public void addMaxStatItem() {
        this.maxstatitem++;
    }

    public void wipeStats() {
        setStr(4);
        updateSingleStat(MapleStat.STR, 4);
        setDex(4);
        updateSingleStat(MapleStat.DEX, 4);
        setInt(4);
        updateSingleStat(MapleStat.INT, 4);
        setLuk(4);
        updateSingleStat(MapleStat.LUK, 4);
    }

    public boolean hasAllStatMax() {
        if (getStr() != 32767) {
            dropMessage("Little birdie told me that you don't have have Your Str");
            return false;
        } else if (getDex() != 32767) {
            dropMessage("Little birdie told me that you don't have have Your Dex");
            return false;
        } else if (getInt() != 32767) {
            dropMessage("Little birdie told me that you don't have have Your Int");
            return false;
        } else if (getLuk() != 32767) {
            dropMessage("Little birdie told me that you don't have have Your Luk");
            return false;
        }
        return true;
    }

    public boolean checkSpace(int fuck) {
        return checkSpace(fuck, 1);
    }

    public boolean checkSpace(int itemid, int quantity) {
        return MapleInventoryManipulator.checkSpace(getClient(), itemid, quantity, "");
    }

    private static class MapleBuffStatValueHolder {

        public MapleStatEffect effect;
        public long startTime;
        public int value;
        public ScheduledFuture<?> schedule;

        public MapleBuffStatValueHolder(MapleStatEffect effect, long startTime, ScheduledFuture<?> schedule, int value) {
            super();
            this.effect = effect;
            this.startTime = startTime;
            this.schedule = schedule;
            this.value = value;
        }
    }

    public static class MapleCoolDownValueHolder {

        public int skillId;
        public long startTime;
        public long length;
        public ScheduledFuture<?> timer;

        public MapleCoolDownValueHolder(int skillId, long startTime, long length, ScheduledFuture<?> timer) {
            super();
            this.skillId = skillId;
            this.startTime = startTime;
            this.length = length;
            this.timer = timer;
        }
    }

    public static class SkillEntry {

        public int skillevel;
        public int masterlevel;

        public SkillEntry(int skillevel, int masterlevel) {
            this.skillevel = skillevel;
            this.masterlevel = masterlevel;
        }

        @Override
        public String toString() {
            return skillevel + ":" + masterlevel;
        }
    }

    public enum FameStatus {

        OK, NOT_TODAY, NOT_THIS_MONTH
    }

    public int getBuddyCapacity() {
        return buddylist.getCapacity();
    }

    public void setBuddyCapacity(int capacity) {
        buddylist.setCapacity(capacity);
        client.getSession().write(MaplePacketCreator.updateBuddyCapacity(capacity));
    }

    public MapleMessenger getMessenger() {
        return messenger;
    }

    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    public void checkMessenger() {
        if (messenger != null && messengerposition < 4 && messengerposition > -1) {
            try {
                WorldChannelInterface wci = ChannelServer.getInstance(client.getChannel()).getWorldInterface();
                MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(client.getPlayer(), messengerposition);
                wci.silentJoinMessenger(messenger.getId(), messengerplayer, messengerposition);
                wci.updateMessenger(getClient().getPlayer().getMessenger().getId(), getClient().getPlayer().getName(), getClient().getChannel());
            } catch (RemoteException e) {
                client.getChannelServer().reconnectWorld();
            }
        }
    }

    public int getMessengerPosition() {
        return messengerposition;
    }

    public void setMessengerPosition(int position) {
        this.messengerposition = position;
    }

    public int hasEXPCard() {
        return 1;
    }

    public boolean getNXCodeValid(String code, boolean validcode) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `valid` FROM nxcode WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            validcode = rs.getInt("valid") != 0;
        }

        rs.close();
        ps.close();
        return validcode;
    }

    public int getNXCodeType(String code) throws SQLException {
        int type = -1;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `type` FROM nxcode WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            type = rs.getInt("type");
        }
        rs.close();
        ps.close();
        return type;
    }

    public int getNXCodeItem(String code) throws SQLException {
        int item = -1;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT `item` FROM nxcode WHERE code = ?");
        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            item = rs.getInt("item");
        }
        rs.close();
        ps.close();
        return item;
    }

    public void setNXCodeUsed(String code) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE nxcode SET `valid` = 0 WHERE code = ?");
        ps.setString(1, code);
        ps.executeUpdate();
        ps.close();
        ps = con.prepareStatement("UPDATE nxcode SET `user` = ? WHERE code = ?");
        ps.setString(1, this.getName());
        ps.setString(2, code);
        ps.executeUpdate();
        ps.close();
    }

    public void setInCS(boolean yesno) {
        this.incs = yesno;
    }

    public boolean inCS() {
        return this.incs;
    }

    public void addCooldown(int skillId, long startTime, long length, ScheduledFuture<?> timer) {
        if (this.coolDowns.containsKey(skillId)) {
            this.coolDowns.remove(skillId);
        }
        this.coolDowns.put(skillId, new MapleCoolDownValueHolder(skillId, startTime, length, timer));
    }

    public void removeCooldown(int skillId) {
        if (this.coolDowns.containsKey(skillId)) {
            this.coolDowns.remove(skillId);
        }
    }

    public void giveCoolDowns(final List<PlayerCoolDownValueHolder> cooldowns) {
        for (PlayerCoolDownValueHolder cooldown : cooldowns) {
            int time = (int) ((cooldown.length + cooldown.startTime) - System.currentTimeMillis());
            ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(this, cooldown.skillId), time);
            addCooldown(cooldown.skillId, System.currentTimeMillis(), time, timer);
        }
    }

    public List<PlayerCoolDownValueHolder> getAllCoolDowns() {
        List<PlayerCoolDownValueHolder> ret = new ArrayList<PlayerCoolDownValueHolder>();
        for (MapleCoolDownValueHolder mcdvh : coolDowns.values()) {
            ret.add(new PlayerCoolDownValueHolder(mcdvh.skillId, mcdvh.startTime, mcdvh.length));
        }
        return ret;
    }

    public static class CancelCooldownAction implements Runnable {

        private int skillId;
        private WeakReference<MapleCharacter> target;

        public CancelCooldownAction(MapleCharacter target, int skillId) {
            this.target = new WeakReference<MapleCharacter>(target);
            this.skillId = skillId;
        }

        @Override
        public void run() {
            MapleCharacter realTarget = target.get();
            if (realTarget != null) {
                realTarget.removeCooldown(skillId);
                realTarget.getClient().getSession().write(MaplePacketCreator.skillCooldown(skillId, 0));
            }
        }
    }

    public void addDisease(MapleDisease disease) {
        this.diseases.add(disease);
    }

    public List<MapleDisease> getDiseases() {
        synchronized (diseases) {
            return Collections.unmodifiableList(diseases);
        }
    }

    public void removeDisease(MapleDisease disease) {
        synchronized (diseases) {
            if (diseases.contains(disease)) {
                diseases.remove(disease);
            }
        }
    }

    public void removeDiseases() {
        diseases.clear();
    }

    public void giveDebuff(MapleDisease disease, MobSkill skill) {
        List<Pair<MapleDisease, Integer>> disease_ = new ArrayList<Pair<MapleDisease, Integer>>();
        disease_.add(new Pair<MapleDisease, Integer>(disease, skill.getX()));
        this.diseases.add(disease);
        getClient().getSession().write(MaplePacketCreator.giveDebuff(disease_, skill));
        getMap().broadcastMessage(this, MaplePacketCreator.giveForeignDebuff(this.id, disease_, skill), false);
    }

    public void dispelDebuffs() {
        List<MapleDisease> toDispel = new ArrayList<MapleDisease>();
        for (MapleDisease disease : diseases) {
            if (disease != MapleDisease.SEDUCE && disease != MapleDisease.SLOW) {
                toDispel.add(disease);
            }
        }
        getClient().getSession().write(MaplePacketCreator.cancelDebuff(toDispel));
        getMap().broadcastMessage(this, MaplePacketCreator.cancelForeignDebuff(this.id, toDispel), false);
        toDispel.clear();
        this.diseases.clear();
    }

    public void dispelAllDebuffs() {
        getClient().getSession().write(MaplePacketCreator.cancelDebuff(diseases));
        getMap().broadcastMessage(this, MaplePacketCreator.cancelForeignDebuff(this.id, diseases), false);
        this.diseases.clear();
    }

    public void setLevel(int level) {
        this.level = level - 1;
    }

//public boolean canWear(IEquip equip) {
//	if (equip.)
//}
    public void sendNote(String to, String msg) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO notes (`to`, `from`, `message`, `timestamp`) VALUES (?, ?, ?, ?)");
        ps.setString(1, to);
        ps.setString(2, this.getName());
        ps.setString(3, msg);
        ps.setLong(4, System.currentTimeMillis());
        ps.executeUpdate();
        ps.close();
    }

    public void showNote() throws SQLException {
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ps.setString(1, this.getName());
        ResultSet rs = ps.executeQuery();
        rs.last();
        int count = rs.getRow();
        rs.first();
        client.getSession().write(MaplePacketCreator.showNotes(rs, count));
        rs.close();
        ps.close();
    }

    public void deleteNote(int id) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM notes WHERE `id`=?");
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    public int getCashSlots() {
        return 100;
    }

    public int getEquipSlots() {
        return 100;
    }

    public int getEtcSlots() {
        return 100;
    }

    public int getSetupSlots() {
        return 100;
    }

    public int getUseSlots() {
        return 100;
    }

    public int getMarkedMonster() {
        return markedMonster;
    }

    public void setMarkedMonster(int markedMonster) {
        this.markedMonster = markedMonster;
    }

    public Byte getHammerSlot() {
        return hammerSlot;
    }

    public void setHammerSlot(Byte hammerSlot) {
        this.hammerSlot = hammerSlot;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int id) {
        this.parentId = id;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int id) {
        this.childId = id;
    }

    public void resetBattleshipHp() {
        ISkill skill = SkillFactory.getSkill(Skills.Corsair.Battleship);
        this.battleshipHp = (4000 * getSkillLevel(skill)) + ((getLevel() - 120) * 2000);
    }

    public int getBattleshipHp() {
        return battleshipHp;
    }

    public void setBattleshipHp(int battleshipHp) {
        this.battleshipHp = battleshipHp;
    }

    public void decreaseBattleshipHp(int decrease) {
        this.battleshipHp -= decrease;
        if (battleshipHp <= 0) {
            this.battleshipHp = 0;
            ISkill battleship = SkillFactory.getSkill(Skills.Corsair.Battleship);
            int cooldown = battleship.getEffect(getSkillLevel(battleship)).getCooldown();
            getClient().getSession().write(MaplePacketCreator.skillCooldown(Skills.Corsair.Battleship, cooldown));
            ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(this, Skills.Corsair.Battleship), cooldown * 1000);
            addCooldown(Skills.Corsair.Battleship, System.currentTimeMillis(), cooldown * 1000, timer);
            cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            resetBattleshipHp();
        }
    }

    public int getJobId() {
        return this.job.getId();
    }

    private void maxAdventurerSkills(boolean shit) {
        maxHeroSkills(shit);
        maxPaladinSkills(shit);
        maxDarkKnightSkills(shit);
        maxFPArchMageSkills(shit);
        maxILArchMageSkills(shit);
        maxBishopSkills(shit);
        maxBowMasterSkills(shit);
        maxCrossBowMasterSkills(shit);
        maxNightLordSkills(shit);
        maxShadowerSkills(shit);
        maxBuccaneerSkills(shit);
        maxCorsairSkills(shit);
    }

    public void maxKOCSkills(boolean shit) {
        maxDawnWarrior3Skills(shit);
        maxBlazeWizard3Skills(shit);
        maxWindArcher3Skills(shit);
        maxNightWalker3Skills(shit);
        maxThunderBreaker3Skills(shit);
    }

    public void maxSkills(boolean shit) {
        if (GMSMode > 0 && !isJounin()) {
            maxJobSkills(shit);
            return;
        }
        if (this.getPath() < 2 || isJounin()) {
            maxAdventurerSkills(shit);
        }
        if (this.getPath() == 2 || isJounin()) {
            maxKOCSkills(shit);
        }
        if (this.hasRasengan()) {
            maxGMSkills(shit);
        }
        if (isJounin()) {
            maxSuperGMSkills(shit);
        }
    }

    public void maxSingleSkill(boolean shit, int skillid) {
        ISkill ski = SkillFactory.getSkill(skillid);
        int maxi = ski.getMaxLevel();
        if (shit) {
            changeSkillLevel(ski, maxi, maxi);
        } else {
            skills.put(ski, new SkillEntry(maxi, maxi));
        }
    }

    public void maxBeginnerSkills(boolean shit) {
        for (int ski : Skills.BeginnerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxSwordsManSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.SwordsmanSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxFighterSkills(boolean shit) {
        maxSwordsManSkills(shit);
        for (int ski : Skills.FighterSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxCrusaderSkills(boolean shit) {
        maxFighterSkills(shit);
        for (int ski : Skills.CrusaderSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxHeroSkills(boolean shit) {
        maxCrusaderSkills(shit);
        for (int ski : Skills.HeroSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxPageSkills(boolean shit) {
        maxSwordsManSkills(shit);
        for (int ski : Skills.PageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxWhiteKnightSkills(boolean shit) {
        maxPageSkills(shit);
        for (int ski : Skills.WhiteKnightSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxPaladinSkills(boolean shit) {
        maxWhiteKnightSkills(shit);
        for (int ski : Skills.PaladinSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxSpearManSkills(boolean shit) {
        maxSwordsManSkills(shit);
        for (int ski : Skills.SpearManSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDragonKnightSkills(boolean shit) {
        maxSpearManSkills(shit);
        for (int ski : Skills.DragonKnightSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDarkKnightSkills(boolean shit) {
        maxDragonKnightSkills(shit);
        for (int ski : Skills.DarkKnightSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxMagicianSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.MagicianSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxFPMagicianSkills(boolean shit) {
        maxMagicianSkills(shit);
        for (int ski : Skills.FPMagicianSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxFPMageSkills(boolean shit) {
        maxFPMagicianSkills(shit);
        for (int ski : Skills.FPMageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxFPArchMageSkills(boolean shit) {
        maxFPMageSkills(shit);
        for (int ski : Skills.FPArchMageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxILMagicianSkills(boolean shit) {
        maxMagicianSkills(shit);
        for (int ski : Skills.ILMagicianSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxILMageSkills(boolean shit) {
        maxILMagicianSkills(shit);
        for (int ski : Skills.ILMageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxILArchMageSkills(boolean shit) {
        maxILMageSkills(shit);
        for (int ski : Skills.ILArchMageSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxClericSkills(boolean shit) {
        maxMagicianSkills(shit);
        for (int ski : Skills.ClericSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxPriestSkills(boolean shit) {
        maxClericSkills(shit);
        for (int ski : Skills.Priestskills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBishopSkills(boolean shit) {
        maxPriestSkills(shit);
        for (int ski : Skills.BishopSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxArcherSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.ArcherSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBowManSkills(boolean shit) {
        maxArcherSkills(shit);
        for (int ski : Skills.HunterSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxRangerSkills(boolean shit) {
        maxBowManSkills(shit);
        for (int ski : Skills.RangerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBowMasterSkills(boolean shit) {
        maxRangerSkills(shit);
        for (int ski : Skills.BowmasterSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxCrossBowManSkills(boolean shit) {
        maxArcherSkills(shit);
        for (int ski : Skills.CrossBowMankills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxSniperSkills(boolean shit) {
        maxCrossBowManSkills(shit);
        for (int ski : Skills.SniperSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxCrossBowMasterSkills(boolean shit) {
        maxSniperSkills(shit);
        for (int ski : Skills.MarksmanSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxRogueSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.RogueSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxAssasinSkills(boolean shit) {
        maxRogueSkills(shit);
        for (int ski : Skills.AssassinSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxHermitSkills(boolean shit) {
        maxAssasinSkills(shit);
        for (int ski : Skills.HermitSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNightLordSkills(boolean shit) {
        maxHermitSkills(shit);
        for (int ski : Skills.NightLordSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBanditSkills(boolean shit) {
        maxRogueSkills(shit);
        for (int ski : Skills.BanditSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxChiefBanditSkills(boolean shit) {
        maxBanditSkills(shit);
        for (int ski : Skills.ChiefBanditSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxShadowerSkills(boolean shit) {
        maxChiefBanditSkills(shit);
        for (int ski : Skills.ShadowerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxPirateSkills(boolean shit) {
        maxBeginnerSkills(shit);
        for (int ski : Skills.PirateSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBrawlerSkills(boolean shit) {
        maxPirateSkills(shit);
        for (int ski : Skills.BrawlerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxMarauderSkills(boolean shit) {
        maxBrawlerSkills(shit);
        for (int ski : Skills.MarauderSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBuccaneerSkills(boolean shit) {
        maxMarauderSkills(shit);
        for (int ski : Skills.BuccaneerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxGunSlingerSkills(boolean shit) {
        maxPirateSkills(shit);
        for (int ski : Skills.GunslingerSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxOutLawSkills(boolean shit) {
        maxGunSlingerSkills(shit);
        for (int ski : Skills.OutlawSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxCorsairSkills(boolean shit) {
        maxOutLawSkills(shit);
        for (int ski : Skills.CorsairSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNoblesseSkills(boolean shit) {
        for (int ski : Skills.NoblesseSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDawnWarrior1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.DawnWarrior1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDawnWarrior2Skills(boolean shit) {
        maxDawnWarrior1Skills(shit);
        for (int ski : Skills.DawnWarrior2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxDawnWarrior3Skills(boolean shit) {
        maxDawnWarrior2Skills(shit);
        for (int ski : Skills.DawnWarrior3skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBlazeWizard1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.BlazeWizard1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBlazeWizard2Skills(boolean shit) {
        maxBlazeWizard1Skills(shit);
        for (int ski : Skills.BlazeWizard2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxBlazeWizard3Skills(boolean shit) {
        maxBlazeWizard2Skills(shit);
        for (int ski : Skills.BlazeWizard3Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxWindArcher1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.WindArcher1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxWindArcher2Skills(boolean shit) {
        maxWindArcher1Skills(shit);
        for (int ski : Skills.WindArcher2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxWindArcher3Skills(boolean shit) {
        maxWindArcher2Skills(shit);
        for (int ski : Skills.WindArcher3Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNightWalker1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.NightWalker1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNightWalker2Skills(boolean shit) {
        maxNightWalker1Skills(shit);
        for (int ski : Skills.NightWalker2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxNightWalker3Skills(boolean shit) {
        maxNightWalker2Skills(shit);
        for (int ski : Skills.NightWalker3Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxThunderBreaker1Skills(boolean shit) {
        maxNoblesseSkills(shit);
        for (int ski : Skills.ThunderBreaker1Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxThunderBreaker2Skills(boolean shit) {
        maxThunderBreaker1Skills(shit);
        for (int ski : Skills.ThunderBreaker2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxThunderBreaker3Skills(boolean shit) {
        maxThunderBreaker2Skills(shit);
        for (int ski : Skills.ThunderBreaker2Skills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxGMSkills(boolean shit) {
        for (int ski : Skills.GMSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void maxSuperGMSkills(boolean shit) {
        maxGMSkills(shit);
        for (int ski : Skills.SuperGMSkills) {
            maxSingleSkill(shit, ski);
        }
    }

    public void clearSkills() {
        this.skills.clear();
    }

    public void wipeKB() {
        keymap.clear();
    }

    public void changeMap(int mapid, int portal) {
        MapleMap to = client.getChannelServer().getMapFactory().getMap(mapid);
        MaplePortal pto = to.getPortal(portal);
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, pto.getId(), this);
        changeMapInternal(to, pto.getPosition(), warpPacket);
    }

    public void changeMap(MapleMap to) {
        MaplePortal pto = to.getPortal(0);
        MaplePacket warpPacket = MaplePacketCreator.getWarpToMap(to, pto.getId(), this);
        changeMapInternal(to, pto.getPosition(), warpPacket);
    }

    public void unJail() {
        changeMap(100000000, 0);
        dropMessage("Be a good girl now or you might be banned ;P");
        unMute();

    }

    public void dropMessage(String msg) {
        dropMessage(6, msg);
    }

    public void dropMessage(int type, String message) {
        getClient().getSession().write(MaplePacketCreator.serverNotice(type, message));
    }

    public void showMessage(String msg) {
        showMessage(6, msg);
    }

    public void showMessage(int type, String message) {
        getClient().getSession().write(MaplePacketCreator.serverNotice(type, message));
    }

    /*
     * GM Status
     */
    public boolean canFuck(MapleCharacter noob) {
        if (getGMLevel() >= noob.getGMLevel()) {
            return true;
        }
        return false;
    }

    public int getGMLevel() {
        return gmLevel.getLevel();
    }

    public boolean hasGmLevel(int level) {
        return gmLevel.getLevel() >= level;
    }

    public boolean isAdmin() {
        return getAccountID() == 1 || gmLevel == Status.HOKAGE;
    }

    public boolean isHokage() {
        return isAdmin();
    }

    public boolean isSannin() {
        return isHokage() || gmLevel == Status.SANNIN;
    }

    public boolean isJounin() {
        return isSannin() || gmLevel == Status.JOUNIN;
    }

    public boolean isChunin() {
        return isJounin() || gmLevel == Status.CHUNIN;
    }

    public boolean isGenin() {
        return isChunin() || gmLevel == Status.GENIN;
    }

    public Status getGMStatus() {
        return this.gmLevel;
    }

    public void setGMStatus(int lvl) {
        this.gmLevel = Status.getByLevel(lvl);
        if (lvl >= 3) {
            clan = Clans.LIGHTNING;
        } else if (lvl == 2) {
            clan = Clans.FIRE;
        }
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE `accounts` SET `gm` = ? WHERE `id` = ?");
            ps.setByte(1, (byte) lvl);
            ps.setInt(2, accountid);
            ps.execute();
            ps.close();
        } catch (SQLException sQLException) {
        }
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.broadcastPacket(MaplePacketCreator.sendYellowTip("[The Elite ninjaGang] We are glad to announce that " + getName() + " is now a " + gmLevel.getTitle()));
        }
    }

    public boolean inJail() {
        return getMapId() == 200090300;
    }

    public void jail() {
        mute(1);
        changeMap(200090300, 0);
        dropMessage("fuck you asshole. Rot in jail. Auto unjail in 15 minutes. If you relog counter will reset :)");
        scheduleUnJail();

    }

    public void scheduleUnJail() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                unJail();
            }
        }, 15 * 60 * 1000); // 15 minute
    }

    public void mute(int fuck) {
        this.mutality = fuck;
    }

    public void unMute() {
        this.mutality = 0;
    }

    public int getMute() {
        return this.mutality;
    }

    public void changeJobById(int fuck) {
        skills.clear();        
        this.changeJob(MapleJob.getById(fuck));
        if (fuck > 910 && !haveItem(1142065, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 11420043, name, (short) 100, (short) 1, (short) 1);
            dropMessage("[The Elite ninja Gang] You have gained a Nobless medal for gaining Cygnus Job");
        }
    }

    public void kill() {
        setHp(0);
        setMp(0);
        updateSingleStat(MapleStat.HP, 0);
        updateSingleStat(MapleStat.MP, 0);
    }

    public void heal() {
        int thehp = getHp(); //  getnow
        setHp(getMaxHp());
        setMp(getMaxMp());
        updateSingleStat(MapleStat.HP, getHp());
        updateSingleStat(MapleStat.MP, getMp());
        if (thehp == 0) {
            setStance(4); //TODO fix death bug, player doesnt spawn on other screen
            getMap().broadcastMessage(this, MaplePacketCreator.removePlayerFromMap(this.getObjectId()), false);
            getMap().broadcastMessage(this, MaplePacketCreator.spawnPlayerMapobject(this), false);
            if (getChalkboard() != null) {
                this.getMap().broadcastMessage(this, (MaplePacketCreator.useChalkboard(this, false)), false);
            }
        }
    }

    public void setExp(int newExp) {
        exp.set(newExp);
        this.updateSingleStat(MapleStat.EXP, newExp);
    }

    public int getPath() {
        return getJobId() > 911 ? 2 : 1;
    }

    public void setClan(int fuck) {
        if (isJounin()) {
            clan = Clans.LIGHTNING;
        } else {
            clan = Clans.getById(fuck);
        }
    }

    public void setClanz(int fuck) {
        clan = Clans.getById(fuck);
    }

    public Clans getClan() {
        return clan;
    }

    public boolean isRebirthing() {
        return rebirthing;
    }

    public void setIsRebirthing(boolean b) {
        this.rebirthing = b;
    }

    public void doReborn(boolean changejob) {
        this.reborn++;
        this.level = 1;
        this.setExp(0);
        this.setHp(1);
        this.setMp(1);
        if (changejob) {
            if (getPath() != 2) {
                this.changeJob(MapleJob.BEGINNER);
            } else {
                this.changeJob(MapleJob.NOBLESSE);
            }
        }
        this.updateSingleStat(MapleStat.LEVEL, 1);
        this.updateSingleStat(MapleStat.EXP, 0);
        this.updateSingleStat(MapleStat.HP, 1);
        this.updateSingleStat(MapleStat.MP, 1);
        this.cancelAllBuffs();
        if ((reborn % 10) == 0) {
            try {
                this.getClient().getChannelServer().getWorldInterface().broadcastMessage(getName(), MaplePacketCreator.serverNotice(6, "[Notice] Congratulations " + getName() + " on his/her " + getReborns() + " rebirth!").getBytes());
            } catch (RemoteException ex) {
                this.getClient().getChannelServer().reconnectWorld();
            }
        }
        guildUpdate();
        if (reborn > 1000 && !haveItem(1142069, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142069, name, (short) 1337, (short) 50, (short) 50);
            dropMessage("[The Elite ninja Gang] You have gained a Captain Knight Medal for reaching 1000 Rebirths");
        } else if (reborn >= 500 && !haveItem(1142068, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142068, name, (short) 500, (short) 25, (short) 25);
            dropMessage("[The Elite ninja Gang] You have gained an Advanced Knight Medal for reaching 500 Rebirths");
        } else if (reborn >= 100 && !haveItem(1142067, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142067, name, (short) 250, (short) 10, (short) 10);
            dropMessage("[The Elite ninja Gang] You have gained an Official Knight Medal for reaching 100 Rebirths");
        } else if (reborn >= 10 && !haveItem(1142066, 1, true, true)) {
            MapleInventoryManipulator.addStatItemById(client, 1142066, name, (short) 50, (short) 5, (short) 5);
            dropMessage("[The Elite ninja Gang] You have gained an Training Knight Medal for reaching 10 Rebirths");
        }
        if (reborn == 10 && (getMapId() == 910000013 || getMapId() == 910000014)) {
            this.goHome();
        }
    }

    public void setReborns(int fuck) {
        this.reborn = fuck;
    }

    public int getReborns() {
        return reborn;
    }

    public void torture() {
        if (!isAdmin()) {
            client.getSession().write(MaplePacketCreator.serverNotice(1, "You have lost your E-Penis"));
            final int originalmap = getMapId();
            TimerManager.getInstance().schedule(new Runnable() {

                public void run() {
                    for (int i = 0; i < 50; i++) {
                        int[] mapids = {0, 2, 100000000, 101000000, 102000000, 103000000, 104000000, 106000000, 107000000, 200000000, 220000000, 230000000, 240000000, 250000000, 600000000, 800000000, 910000001, 910000007, 910000015, 910000021, 280030000, 240060200, originalmap};
                        for (int mapid1 : mapids) {
                            changeMap(mapid1, 0);
                        }

                    }
                }
            }, 1000);
        }

    }

    public void torture(String reason) {
        String[] reasonslol = {"a Jinchuriki", "a legendary Sannin", "Oruchimaru", "a Shinigami", "a Hollow", "a kage"};
        try {
            this.getClient().getChannelServer().getWorldInterface().broadcastMessage(getName(), MaplePacketCreator.serverNotice(6, "[Akatsuki] " + name + " has been butt-raped by " + reasonslol[(int) Math.round(Math.random() * reasonslol.length)] + " and is now taking a ride on a big black ahem! Reason : " + reason).getBytes());
        } catch (RemoteException ex) {
            this.getClient().getChannelServer().reconnectWorld();
        }
        torture();
    }

    public void hide() {
        if (noHide) {
            // do nothing
        } else {
            // SkillFactory.getSkill(9101004).getEffect(1).applyTo(this);
            getClient().getSession().write(MaplePacketCreator.sendGMOperation(16, 1));
        }
    }

    public void deHide() {
        if (this.getBuffedValue(MapleBuffStat.GM_HIDE) != null) {
            this.cancelBuffStats(MapleBuffStat.GM_HIDE);
        }
    }

    public void setChalkboard(String text) {
        this.chalktext = text;
        if (chalktext == null) {
            getMap().broadcastMessage(MaplePacketCreator.useChalkboard(this, true));
        } else {
            getMap().broadcastMessage(MaplePacketCreator.useChalkboard(this, false));
        }

    }

    public String getChalkboard() {
        return chalktext;
    }

    public void setID(int i) {
        this.id = i;
    }

    public void loadAllSavedPets() {
        if (SpecialStuff.getInstance().isEventMap(mapid) || getMute() != 0) {
            //  showMessage(1, "You cannot Spawn pets in a Event or while muted!");
            return;
        }
        MaplePet lol0 = pets[0];
        MaplePet lol1 = pets[1];
        MaplePet lol2 = pets[2];
        this.unequipAllPets();
        if (lol0 != null) {
            spawnLoadedPet(lol0, true);
            if (lol1 != null) {
                spawnLoadedPet(lol1, true);
                if (lol2 != null) {
                    spawnLoadedPet(lol2, true);
                } else {
                }
            } else {
            }
        } else {
        }
    }

    public void removeClones() {
        if (hasClones()) {
            for (Clones clone : getClones()) {
                clone.getClone().getMap().removePlayer(clone.getClone());
            }
            fakes.clear();
        }
    }

    public boolean hasClones() {
        for (Clones clone : fakes) {
            if (clone != null) {
                return true;
            }
        }
        return false;
    }

    public List<Clones> getClones() {
        return fakes;
    }

    public void addClone(Clones f) {
        this.fakes.add(f);
    }

    public boolean canHasClone() {
        return clonelimit > 0;
    }

    public int getAllowedClones() {
        if (isJounin() && clonelimit < 25) {
            this.clonelimit = 25;
        }
        if (isGenin() && clonelimit < 15) {
            this.clonelimit = 15;
        }
        return clonelimit;
    }

    public void setCloneLimit(int fuck) {
        this.clonelimit = fuck;
    }

    public boolean hasRasengan() {
        return rasengan == 69;
    }

    public byte getRasengan() {
        return rasengan;
    }

    public void setRasengan(byte b) {
        rasengan = b;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE `characters` SET `rasengan` = ? where `id` =?");
            ps.setByte(1, this.rasengan);
            ps.setInt(2, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException sQLException) {
            sQLException.printStackTrace();
        }
        dropMessage("Your Rasengan Quest level is now " + b);
    }

    public void sendGMMsg(String msg) {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.broadcastStaffPacket(MaplePacketCreator.serverNotice(5, msg));
        }
    }

    public void unstuck() {
        sendGMMsg("Unstucking " + getName());
        try {
            getMap().broadcastMessage(MaplePacketCreator.removePlayerFromMap(id));
            try {
                map.removePlayer(this);
                sendGMMsg("tried to remove player from map");
            } catch (Exception e) {
                getMap().broadcastMessage(MaplePacketCreator.removePlayerFromMap(id));
                sendGMMsg("Tried to broadcast remove player packet as removing player failed");
            }
            try {
                if (getEventInstance() != null) {
                    getEventInstance().unregisterPlayer(this);
                    sendGMMsg("Event stuck tried to fix by unregistering the character in the event");
                }
            } catch (Exception e) {
                log.error("Unstuck ERROR : " + e);
            }
            try {
                if (NPCScriptManager.getInstance().getCM(client) != null) {
                    NPCScriptManager.getInstance().dispose(client);
                }
            } catch (Exception e) {
                log.error("Unstuck ERROR : " + e);
            }
            try {
                if (client.getChannelServer().getPlayerStorage().getAllCharacters().contains(this)) { // check, null pointer no thanks
                    client.getChannelServer().removePlayer(this); // this is all you need.
                    sendGMMsg("player was in the player storage so tried to remove him from that");
                }
            } catch (Exception e) {
                log.error("Unstuck error ", e);
            }
            try {
                client.disconnect();
                sendGMMsg("Tried to disconnect his client");
            } catch (Exception e) {
                log.error("Unstuck error ", e);
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ? WHERE id = ?");
                ps.setInt(1, 0);
                ps.setInt(2, accountid);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ignored) {
            }
        } catch (Exception e) {
            log.error("Unstuck error ", e);
        }

    }

    public int getNinjaTensu() {
        return this.ninjatensu;
    }

    public void setNinjaTensu(int fuck) {
        this.ninjatensu = fuck;
    }

    public void addNinjaTensu() {
        this.ninjatensu++;
    }

    public void reduceNinjaTensu() {
        this.ninjatensu--;
    }

    public boolean legendBlocked(String legend) {
        return GameConstants.isBlockedName(legend) && !isChunin() || legend.length() > 15;
    }

    public void setLegend(String legend) {
        if (legendBlocked(legend)) {
            dropMessage("This cannot be used as a legend in NinjaMS (" + legend + ")");
            return;
        }
        this.legend = legend;
        dropMessage("Your legend set to" + legend);
    }

    public String getLegend() {
        return this.legend;
    }

    public void runAutoSave() {
        expirationTask();
        forceSave(true, false);
    }

    public void expirationTask() {
        long expiration;
        long currenttime = System.currentTimeMillis();
        List<IItem> toberemove = new ArrayList<IItem>(); // This is here to prevent deadlock.
        for (MapleInventory inv : inventory) {
            for (IItem item : inv.list()) {
                expiration = item.getExpiration();
                if (currenttime > expiration && expiration != -1) {
                    client.getSession().write(MaplePacketCreator.itemExpired(item.getItemId()));
                    toberemove.add(item);
                }
            }
            for (IItem item : toberemove) {
                MapleInventoryManipulator.removeFromSlot(client, inv.getType(), item.getPosition(), item.getQuantity(), true);
            }
            toberemove.clear();
        }
    }

    public void doRebornn(final boolean changejob) {
        if (isRebirthing()) {
            return;
        }
        if (getReborns() >= 10 && getClan().equals(Clans.UNDECIDED)) {
            dropMessage("You need to be in a clan to rebirth. Talk to Shadrion in Henesys");
            return;
        }
        if (getLevel() < getMaxLevel()) {
            dropMessage("You need atleast " + getMaxLevel() + " to rebirth");
            return;
        }
        setIsRebirthing(true);
        dropMessage("You are now in Queue. Please Wait 5 seconds");
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                doReborn(changejob);
                dropMessage("Please Wait another 10 seconds");
            }
        }, 5 * 1000);

        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                setIsRebirthing(false);
                dropMessage("You can continue your Training now. Have Fun :)");
            }
        }, 10 * 1000);
    }

    public MaplePet getPet() {
        return pet;
    }

    public void setPet(MaplePet pet) {
        this.pet = pet;
    }

//Donation stuff
    public short getDPoints() {
        return this.dpoints;
    }

    public short getDAmount() {
        return this.damount;
    }

    public void modifyDPoints(short fff) {
        dpoints += fff;
        saveToDB();
        if (fff < 0) {
            dropMessage("You have lost " + (fff * -1) + " donator Points.");
        } else {
            dropMessage("You have Gained " + fff + "donator Points");
        }

    }

    public void modifyDAmount(short fff) {
        damount += fff;
        saveToDB();
    }

    public void finishAlert() {
        StringBuilder sb = new StringBuilder();
        String readableTargetName = MapleCharacterUtil.makeMapleReadable(getName());
        sb.append(getName());
        sb.append(" (").append(readableTargetName).append(")");
        sb.append(" has just finished the JQ: ");
        sb.append(getMapId());
        sb.append(". Last Jq Finished at : ").append(lastJQFinish);
        lastJQFinish =
                new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(System.currentTimeMillis()));
        sb.append(" Started at System time : ").append(jqStart).append(". finished at : ").append(lastJQFinish).append(". ");
        try {
            getClient().getChannelServer().broadcastStaffPacket(MaplePacketCreator.serverNotice(5, sb.toString()));
        } catch (Exception ignored) {
        }

    }

    public void giveJQReward() {
        if (jqStart == null) {
            dropMessage("You did not start the JQ properly. either you warped in or CCed . so no rewards for you");
            return;

        }


        int type = (int) Math.floor(Math.random() * 3200 + 1);
        int[] specialitem = {4001064, 4001065, 4001066, 4001067, 4001068, 4001069, 4001070, 4001071, 4001072, 4001073};
        int[] jqmap = {105040311, 105040313, 105040316, 103000902, 103000905, 103000909, 101000101, 101000104, 109040004, 280020001};
        int i;
        for (i = 0; i < jqmap.length; i++) {
            if (this.getMapId() == jqmap[i]) {
                dropMessage("diarypage gain");
                gainItem(specialitem[i], 1);
                dropMessage("diarypage gained2");
            } else {
                // Do nothing
            }
        }
        if (type < 250) {
            this.dropMessage("How sweet! You have been scammed! :)");
        } else if (type >= 250 && type < 500) {
            int nxx = (int) Math.floor(Math.random() * 2000 + 1);
            this.modifyCSPoints(1, nxx);
            this.dropMessage("You have gained " + nxx + " NX.");
        } else if (type >= 500 && type < 1000) {
            this.gainMeso((type * 10000), true);
            this.dropMessage("You have gained " + (type * 10000) + " mesos");
        } else if (type >= 1000 && type < 1500) {
            this.levelUp();
            this.levelUp();
            this.levelUp();
        } else if (type >= 1500 && type < 2000) {
            int aap = (int) Math.floor(Math.random() * 15);
            this.gainAp(aap);
            this.dropMessage("You have gained" + aap + "Ap");
        } else if (type >= 2000 && type < 2500) {
            int[] scrolls = {2040011, 2040012, 2040013, 2040014, 2040015, 2040028, 2040100, 2040101, 2040102, 2040103, 2040104, 2040200, 2040201, 2040203, 2040204,
                2040205, 2040206, 2040207, 2040208, 2040209, 2040300, 2040301, 2040302, 2040303, 2040304, 2040305, 2040306,
                2040307, 2040308, 2040309, 2040310, 2040311, 2040312, 2040314, 2040317, 2040318, 2040319, 2040320, 2040321, 2040322, 2040323, 2040327,
                2040328, 2040412, 2040413, 2040414, 2040417, 2040418, 2040419, 2040530, 2040531, 2040532, 2040533, 2040623, 2040624, 2040625,
                2040626, 2040627, 2040803, 2040804, 2040805, 2040806, 2040807, 2040810, 2040811, 2040814, 2040815, 2040914, 2040915, 2040916, 2040917, 2040918, 2040919, 2040920, 2040921, 2040922, 2049100, 2049000, 2049001, 2049002, 2049003};
            int[] dragonweapon = {1302059, 1312031, 1322052, 1332049, 1332050, 1372032, 1382036, 1402036, 1412026, 1422028, 1432038, 1442045, 1452044, 1462039, 1472051, 1472052, 1482012, 1492013};
            if (type < 2250) {
                i = (int) Math.floor(Math.random() * scrolls.length);
                MapleInventoryManipulator.addById(this.getClient(), scrolls[i], (short) 1, null);
                this.dropMessage("You have gained a scroll");
            } else {
                i = (int) Math.floor(Math.random() * dragonweapon.length);
                MapleInventoryManipulator.addById(this.getClient(), dragonweapon[i], (short) 1, null);
                this.dropMessage("you have gained dragon weapon");
            }
        } else if (type >= 2500 && type < 3000) {
            MapleInventoryManipulator.addById(this.getClient(), 2000005, (short) 30, null);
            this.dropMessage("you have gained 30 power elixirs");
        } else {
            MapleInventoryManipulator.addById(this.getClient(), 2000004, (short) 50, null);
            this.dropMessage("you have gained 50 elixirs");
        }

        changeMap(100000000);
    }

    public String jqStartTime() {
        return this.jqStart;
    }

    public void startJq(int wtf) {
        int[] target = {105040310, 105040312, 105040314, 103000900, 103000903, 103000906, 101000100, 101000102, 109040001, 280020000};
        changeMap(target[wtf - 1]);
        dropMessage("Warping to map : " + target[wtf - 1] + " to start JQ");
        dropMessage("Please Vote for us at http://ninjams.org/vote. and DON'T hack :)");
        noHide = true;
        cancelAllBuffs();
    }

    public void startAlert(int jq) {
        StringBuilder sb = new StringBuilder();
        String readableTargetName = MapleCharacterUtil.makeMapleReadable(getName());
        sb.append(getName());
        sb.append(" (").append(readableTargetName).append(")");
        sb.append(" has just started the JQ: ");
        sb.append(jq);
        sb.append(".");
        jqStart = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(System.currentTimeMillis()));
        sb.append(" Started at System time : ").append(jqStart).append(" . ");
        try {
            getClient().getChannelServer().broadcastStaffPacket(MaplePacketCreator.serverNotice(5, sb.toString()));
        } catch (Exception ignored) {
        }
    }

    public void bonusReward() {
        int[] pages = {4001064, 4001065, 4001066, 4001067, 4001068, 4001069, 4001070, 4001071, 4001072, 4001073};
        int i;
        for (i = 0; i < pages.length; i++) {
            if (haveItem(pages[i], 1, false, true)) {
                showMessage("You don't seem to have all the items :). Get them first ");
                break;
            }
        }
        for (i = 0; i < pages.length; i++) {
            MapleInventoryManipulator.removeById(getClient(), MapleItemInformationProvider.getInstance().getInventoryType(pages[i]), pages[i], 1, true, false);
        }
        int type = (int) Math.floor(Math.random() * 5200 + 1);
        if (type < 500) {
            showMessage("how does it feel to be scammed? xD");
        } else if (type >= 500 && type < 1000) {
            showMessage("You have gained 1 bil mesos");
            gainMeso(1000000000, true);
        } else if (type >= 1000 && type < 1500) {
            int nxxx = (int) Math.floor(Math.random() * type + 20000);
            modifyCSPoints(1, nxxx);
            showMessage("You have gained " + nxxx + " NX");
        } else if (type >= 1500 && type < 2500) {
            int[] scrolls = {2040603, 2044503, 2041024, 2041025, 2044703, 2044603, 2043303, 2040807, 2040806, 2040006, 2040007, 2043103, 2043203, 2043003, 2040506, 2044403, 2040903, 2040709, 2040710, 2040711, 2044303, 2043803, 2040403, 2044103, 2044203, 2044003, 2043703, 2041200, 2049100, 2049000, 2049001, 2049002, 2049003};
            i =
                    (int) Math.floor(Math.random() * scrolls.length);
            MapleInventoryManipulator.addById(getClient(), scrolls[i], (short) 1);
            showMessage("You have gained a scroll (Gm scroll / Chaos scroll / Clean slate scroll)");
        } else if (type >= 2500 && type < 3000) {
            int[] rareness = {1302081, 1312037, 1322060, 1402046, 1412033, 1422037, 1442063, 1482023, 1372035, 1372036, 1372037, 1372038, 1372039, 1372040, 1372041, 1372042, 1382045, 1382046, 1382047, 1382048, 1382049, 1382050, 1382051, 1382052, 1382060, 1442068, 1452060};
            showMessage("You have gained a super Rare Weapon");
            i =
                    (int) Math.floor(Math.random() * rareness.length);
            MapleInventoryManipulator.addById(getClient(), rareness[i], (short) 1);
        } else if (type >= 3000 && type < 3500) {
            showMessage("You have gained 200 power elixirs");
            MapleInventoryManipulator.addById(getClient(), 2000005, (short) 200);
        } else if (type >= 3500 && type < 4000) {
            showMessage("You have gained 200 elixirs");
            MapleInventoryManipulator.addById(getClient(), 2000004, (short) 200);
        } else if (type >= 4000 && type < 4250) {
            showMessage("You have gained 20 heart stoppers");
            MapleInventoryManipulator.addById(getClient(), 2022245, (short) 20);
        } else if (type >= 4250 && type < 4500) {
            showMessage("You have gained 10 Onyx Apples");
            MapleInventoryManipulator.addById(getClient(), 2022179, (short) 10);
        } else if (type >= 4500 && type < 4750) {
            showMessage("You have gained 5 demon elixirs");
            MapleInventoryManipulator.addById(getClient(), 2022282, (short) 5);
        } else if (type >= 4750 && type < 5000) {
            gainAp(2500);
            showMessage("You have gained 2500 Ap");
        } else {
            showMessage("All your stats has been Maxxed");
            maxAllStats();

            getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[The Elite NinjaGang] Congratulations " + getName() + " On getting all his stats maxxed from JQ bonus"));
        }

    }

    public void donatorGacha() {
        if (this.getDPoints() < 1) {
            dropMessage("You dont have any donator Point");
            return;
        }
        int i = 0;
        int type = (int) Math.floor(Math.random() * 20 + 1);
        int chance = (int) Math.floor(Math.random() * 10 + 1);
        int chance1 = (int) Math.floor(Math.random() * 10 + 1);
        int chance2 = (int) Math.floor(Math.random() * 10 + 1);
        int[] pages = {4001064, 4001065, 4001066, 4001067, 4001068, 4001069, 4001070, 4001071, 4001072, 4001073};
        switch (type) {
            case 1:
            case 2:
            case 3:
                int mesolar = type * 1000000;
                mesolar +=
                        chance * 1000000;
                mesolar +=
                        chance1 * 1000000;
                mesolar +=
                        chance2 * 1000000;
                mesolar *=
                        20;
                if (mesolar < 100000000) {
                    mesolar += 100000000;
                }

                if (getMeso() + mesolar >= Integer.MAX_VALUE) {
                    gainMeso((Integer.MAX_VALUE - getMeso() - 1), true);
                } else {
                    gainMeso(mesolar, true);
                }

                dropMessage("You have gained Mesos");
                break;
            case 4:
            case 5:
            case 6:
                modifyCSPoints(1, 15000);
                showMessage("You have gained 15000 NX");
            case 7:
                int[] scrolls = {2040603, 2044503, 2041024, 2041025, 2044703, 2044603, 2043303, 2040807, 2040806, 2040006, 2040007, 2043103, 2043203, 2043003, 2040506, 2044403, 2040903, 2040709, 2040710, 2040711, 2044303, 2043803, 2040403, 2044103, 2044203, 2044003, 2043703, 2041200, 2049100, 2049000, 2049001, 2049002, 2049003};
                i =
                        (int) Math.floor(Math.random() * scrolls.length);
                MapleInventoryManipulator.addById(getClient(), scrolls[i], (short) 5);
                showMessage("You have gained a scroll (Gm scroll / Chaos scroll / Clean slate scroll)");
                break;
            case 8:
                int[] rareness = {1302081, 1312037, 1322060, 1402046, 1412033, 1422037, 1442063, 1482023, 1372035, 1372036, 1372037, 1372038, 1372039, 1372040, 1372041, 1372042, 1382045, 1382046, 1382047, 1382048, 1382049, 1382050, 1382051, 1382052, 1382060, 1442068, 1452060};
                showMessage("You have gained a super Rare Weapon");
                i = (int) Math.floor(Math.random() * rareness.length);
                MapleInventoryManipulator.addById(getClient(), rareness[i], (short) 1);
                int fucks = chance + chance1 + chance2;
                showMessage("You have gained " + fucks + " attack post of each kind");
                MapleInventoryManipulator.addById(getClient(), 2022245, (short) fucks);
                MapleInventoryManipulator.addById(getClient(), 2022179, (short) fucks);
                MapleInventoryManipulator.addById(getClient(), 2022282, (short) fucks);
                break;
            case 9:
                gainAp(2500);
                showMessage("You have gained 2500 AP");
                break;
            case 10:
                gainAp(1000);
                showMessage("you have gained 1000 AP");
                break;
            case 11:
                gainAp(5000);
                showMessage("you have gained 5000 Ap");
                break;
            case 12:
                gainAp(500);
                showMessage("you have gained 500 Ap");
                break;
            case 13:
                addFame(100);
                showMessage("You have gained 100 Fame");
                break;
            case 14:
                for (i = 0; i < pages.length; i++) {
                    gainItem(pages[i], 1);
                }
                showMessage("you have gained a set of DiaryPages");
                break;
            case 15:
                int p = chance > 5 ? 1 : 2;
                for (i = 0; i < pages.length; i++) {
                    gainItem(pages[i], p);
                }
                showMessage("you have gained " + p + "set of DiaryPages");
                break;
            case 16:
                int q = chance1 > 5 ? 1 : 2;
                for (i = 0; i < pages.length; i++) {
                    gainItem(pages[i], q);
                }
                showMessage("you have gained " + q + " set of DiaryPages");
                break;
            case 17:
            case 18:
            case 19:
                int[] chairs1 = {3010000, 3010001, 3010002, 3010003, 3010004, 3010005};
                int[] chairs2 = {3010006, 3010007, 3010008, 3010009, 3010010, 3010011};
                int[] chairs3 = {3010012, 3010013, 3010014, 3010015, 3010016, 3010017};
                int[] chairs4 = {3010018, 3010019, 3010022, 3010023, 3010024, 3010025};
                int[] chairs5 = {3010026, 3010028, 3010040, 3010041, 3010045, 3010046};
                int[] chairs6 = {3010000, 3010047, 3010057, 3010058, 3010072, 3011000};
                int z;
                switch (chance2) {
                    case 1:
                    case 2:
                        for (z = 0; z < 6; z++) {
                            gainItem(chairs1[i], 1);
                        }
                        break;
                    case 3:
                    case 4:
                        for (z = 0; z < 6; z++) {
                            gainItem(chairs2[i], 1);
                        }
                        break;
                    case 5:
                    case 6:
                        for (z = 0; z < 6; z++) {
                            gainItem(chairs3[i], 1);
                        }
                        break;
                    case 7:
                        for (z = 0; z < 6; z++) {
                            gainItem(chairs4[i], 1);
                        }
                        break;
                    case 8:
                        for (z = 0; z < 6; z++) {
                            gainItem(chairs5[i], 1);
                        }
                        break;
                    default:
                        for (z = 0; z < 6; z++) {
                            gainItem(chairs6[i], 1);
                        }
                }
                showMessage("You have Gained Chairs");
                break;
            case 20:
                showMessage("All your stats has been Maxxed");
                maxAllStats();
                getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[The Elite NinjaGang] Congratulations " + getName() + " On getting all his stats maxxed from Donator Gacha"));
                break;
            default:
                modifyCSPoints(1, 15000);
                showMessage("You have gained 15000 NX");
                break;
        }
        modifyDPoints((short) -1);
    }

    public void maxAllStats() {
        this.setStr(32767);
        this.setDex(32767);
        this.setLuk(32767);
        this.setInt(32767);
    }

    public void changeMap(int fuck) {
        changeMap(fuck, 0);
    }

    public boolean hasPetVac() {
        return this.getNoPets() >= 1 && getInventory(MapleInventoryType.EQUIPPED).findById(1812004) != null && getInventory(MapleInventoryType.EQUIPPED).findById(1812005) != null && getInventory(MapleInventoryType.EQUIPPED).findById(1812001) != null && getInventory(MapleInventoryType.EQUIPPED).findById(1812000) != null;
    }

    public void gainItem(int id, int quantity) {
        if (quantity >= 0) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            IItem item = ii.getEquipById(id);
            MapleInventoryType type = ii.getInventoryType(id);
            if (!MapleInventoryManipulator.checkSpace(client, id, quantity, "")) {
                client.getSession().write(MaplePacketCreator.serverNotice(1, "Your inventory is full."));
                return;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !ii.isThrowingStar(item.getItemId()) && !ii.isBullet(item.getItemId())) {
                MapleInventoryManipulator.addFromDrop(client, item);
            } else {
                MapleInventoryManipulator.addById(client, id, (short) quantity, "");
            }
        } else {
            MapleInventoryManipulator.removeById(client, MapleItemInformationProvider.getInstance().getInventoryType(id), id, -quantity, true, false);
        }

        client.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) quantity, true));
    }

    public int getPvpKills() {
        return this.pvpkills;
    }

    public int getPvpDeaths() {
        return this.pvpdeaths;
    }

    public void gainPvpKill() {
        this.pvpkills++;
    }

    public void gainPvpDeath() {
        this.pvpdeaths++;
    }

    public void setPvpDeaths(int amount) {
        this.pvpdeaths = amount;
    }

    public void setPvpKills(int amount) {
        this.pvpkills = amount;
    }

// Fun stuff from Oliver For the GMs
    public void registerGayBombs() {
        inflicted = true;
        doGayBombs();

    }

    public void inflict() {
        inflicted = true;
    }

    public boolean isInflicted() {
        return inflicted;
    }

    private void doGayBombs() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (inflicted) {
                    heal();
                    getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9300166), getPosition());
                    doGayBombs();

                }
            }
        }, 500);
    }

    public void registerJump() {
        inflicted = true;
        removeChair();
//dispelSeduce(); // LOL
        dispelDebuffs();
        giveDebuff(MapleDisease.getType(128), MobSkillFactory.getMobSkill(128, 6));
        doJump();
    }

    public void removeChair() {
        setChair(0);
        getClient().getSession().write(MaplePacketCreator.cancelChair());
        getMap().broadcastMessage(this, MaplePacketCreator.showChair(id, 0), false);
    }

    private void doJump() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (inflicted) {
                    removeChair();
                    //dispelSeduce(); // LOL
                    dispelDebuffs();
                    heal();
                    giveDebuff(MapleDisease.getType(128), MobSkillFactory.getMobSkill(128, 6));
                    doJump();
                }
            }
        }, 2000);
    }

    public void registerDance() {
        inflicted = true;
        doDance(1);
    }

    private void doDance(final int dir) {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (inflicted) {
                    removeChair();
                    dispelDebuffs();
                    heal();
                    giveDebuff(MapleDisease.getType(128), MobSkillFactory.getMobSkill(128, dir));
                    if (dir == 1) {
                        doDance(2); // loop
                    } else {
                        doDance(1);
                    }
                }
            }
        }, 1500);
    }

    public void deInflict() {
        inflicted = false;
    }

    public boolean unequipEverything() {
        MapleInventory equipped = this.getInventory(MapleInventoryType.EQUIPPED);
        List<Byte> position = new ArrayList<Byte>();
        for (IItem item : equipped.list()) {
            position.add(item.getPosition());
        }

        for (byte pos : position) {
            if (getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1) {
                MapleInventoryManipulator.unequip(client, pos, getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
            } else {
                showMessage(1, "You do not have enough space to strip all your clothes.");
                return false;
            }

        }
        client.showMessage(5, "[System] All items have been successfully unequiped!");
        return true;
    }

// limitations
    public void setUndroppable() {
        cannotdrop = true;
    }

    public boolean cannotDrop() {
        return cannotdrop;
    }

// Smega Stuff
    public void setPrefixShit(int lol) {
        this.prefixshit = (byte) lol;
    }

    public byte getPrefixShit() {
        return prefixshit;
    }

    // Ap storage
    public void setStorageAp(int fuck) {
        storageAp = fuck;
    }

    public void addStorageAp(int fuck) {
        storageAp += fuck;
    }

    public void reduceStorageAp(int fuck) {
        storageAp -= fuck;
    }

    public int getStorageAp() {
        return storageAp;
    }

    // ap per level
    public byte getApPerLevel() {
        if (reborn > 2000) {
            return 1;
        } else if (reborn > 1000) {
            return 2;
        } else if (reborn > 500) {
            return 3;
        } else if (reborn > 250) {
            return 4;
        } else {
            return 5;
        }
    }

    // AutoAp Shit
    public byte getAutoAp() {
        return autoap;
    }

    public void autoAp(byte type) {
        autoap = type;
    }

    public void doAutoAp(byte apla) {
        switch (autoap) {
            case 0:
                if (remainingAp + apla <= Short.MAX_VALUE) {
                    remainingAp += apla;
                    updateSingleStat(MapleStat.AVAILABLEAP, remainingAp);
                } else {
                    byte togain = apla;
                    byte gain = (byte) (Short.MAX_VALUE - remainingAp);
                    togain -= gain;
                    dropMessage("Your remaining AP has been maxed! Your AP will now be added to Storage AP");
                    autoap = 5;
                    remainingAp += gain;
                    storageAp += togain;
                    updateSingleStat(MapleStat.AVAILABLEAP, remainingAp);
                }
                break;
            case 1:
                if (str + apla <= Short.MAX_VALUE) {
                    str += apla;
                    updateSingleStat(MapleStat.STR, str);
                } else {
                    byte togain = apla;
                    byte gain = (byte) (Short.MAX_VALUE - str);
                    togain -= gain;
                    dropMessage("Your STR has been maxed! Your AutoAP has been turned off.");
                    autoap = 0;
                    str += gain;
                    remainingAp += togain;
                    updateSingleStat(MapleStat.STR, str);
                }
                break;
            case 2:
                if (dex + apla <= Short.MAX_VALUE) {
                    dex += apla;
                    updateSingleStat(MapleStat.DEX, dex);
                } else {
                    byte togain = apla;
                    byte gain = (byte) (Short.MAX_VALUE - dex);
                    togain -= gain;
                    dropMessage("Your DEXTERITY stat has been maxed! Your AutoAP has been turned off.");
                    autoap = 0;
                    dex += gain;
                    remainingAp += togain;
                    updateSingleStat(MapleStat.DEX, dex);
                }
                break;
            case 3:
                if (int_ + apla <= Short.MAX_VALUE) {
                    int_ += apla;
                    updateSingleStat(MapleStat.INT, int_);
                } else {
                    byte togain = apla;
                    byte gain = (byte) (Short.MAX_VALUE - int_);
                    togain -= gain;
                    dropMessage("Your INT has been maxed! Your AutoAP has been turned off.");
                    autoap = 0;
                    int_ += gain;
                    remainingAp += togain;
                    updateSingleStat(MapleStat.INT, int_);
                }
                break;
            case 4:
                if (luk + apla <= Short.MAX_VALUE) {
                    luk += apla;
                    updateSingleStat(MapleStat.LUK, luk);
                } else {
                    int togain = apla;
                    int gain = (byte) (Short.MAX_VALUE - luk);
                    togain -= gain;
                    dropMessage("Your LUCK stat has been maxed! Your AutoAP has been turned off.");
                    autoap = 0;
                    luk += gain;
                    remainingAp += togain;
                    updateSingleStat(MapleStat.LUK, luk);
                }
                break;
            case 5:
                if (storageAp + apla < Integer.MAX_VALUE) {
                    storageAp += apla;
                } else {
                    byte togain = apla;
                    byte gain = (byte) (Integer.MAX_VALUE - storageAp);
                    client.showMessage((byte) 5, "Your STORAGE AP has been maxed! You will not gain AP any more.");
                }
                break;
        }
    }

    public boolean isMarried() {
        return false;
    }

    public boolean getNoHide() {
        return noHide;
    }

    public void setNoHide(boolean setTo) {
        noHide = setTo;
    }

    // skills
    public boolean canUseSkill(int skillid) {
        if (skillid < 1000000) {
            return true;
        }
        if (isJounin()) {
            return true;
        }
        if (this.getBuffedValue(MapleBuffStat.MORPH) != null) {
            if (!SkillFactory.getSkill(skillid).canBeLearnedBy(MapleJob.getById(this.getBuffSource(MapleBuffStat.MORPH) / 10000)) && SkillFactory.getSkill(this.getBuffSource(MapleBuffStat.MORPH)) != null) {
                cancelAllBuffs();
                changeSkillLevel(SkillFactory.getSkill(skillid), 0, 0); // false :D!
                return false;
            }
        }
        if (!SpecialStuff.getInstance().isSkillBlocked(mapid)) {
            return true;
        }
        if (skillid < 10000000 && getPath() == 1) {
            return true;
        }
        if (skillid >= 10000000 && getPath() == 2) {
            changeSkillLevel(SkillFactory.getSkill(skillid), 0, 0); // false :D!
        }
        return false;
    }

    // Rates Stuff
    public int getExpBoost() {
        return (expBoost);
    }

    public int getMesoBoost() {
        return (mesoBoost);
    }

    public int getDropBoost() {
        return (dropBoost);
    }

    public int getBossDropBoost() {
        return (bdropBoost);
    }

    public void addExpBoost() {
        expBoost++;
        rateChange = true;
    }

    public void addMesoBoost() {
        mesoBoost++;
        rateChange = true;
    }

    public void addDropBoost() {
        dropBoost++;
        rateChange = true;
    }

    public void addBossDropBoost() {
        bdropBoost++;
        rateChange = true;
    }

    // Changing Channel Stuff cred: Olivia
    public void endTradeIfExists() {
        if (getTrade() != null) {
            MapleTrade.cancelTrade(this);
        }
    }

    public void endEventIfExists() {
        if (getEventInstance() != null) {
            getEventInstance().unregisterPlayer(this);
        }
    }

    public void destroyCheatTrackerStuff() {
        if (getCheatTracker() != null) {
            getCheatTracker().dispose();
        }
    }

    public void addBuffsToWorldStorage() {
        try {
            WorldChannelInterface wci = ChannelServer.getInstance(client.getChannel()).getWorldInterface();
            wci.addBuffsToStorage(getId(), getAllBuffs());
            if (!isJounin()) {
                wci.addCooldownsToStorage(getId(), getAllCoolDowns());
            }
        } catch (RemoteException e) {
            log.error("RemoteException: {}", e);
            client.getChannelServer().reconnectWorld();
        }
    }

    public void deleteBuffsForCC() {
        if (getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
            cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
        }
        if (getBuffedValue(MapleBuffStat.PUPPET) != null) {
            cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
        }
    }

    public void messengerCC() {
        if (messenger != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(this);
            try {
                WorldChannelInterface wci = ChannelServer.getInstance(client.getChannel()).getWorldInterface();
                wci.silentLeaveMessenger(messenger.getId(), messengerplayer);
            } catch (RemoteException e) {
                client.getChannelServer().reconnectWorld();
            }
        }
    }

    public void changeChannel(int channel) {
        changeChannel(getMapId(), -1, channel);
    }

    public void changeChannel(int mapid, int setspawnpoint, int channel) {
        cancelBuffStats(MapleBuffStat.SUMMON);
        deleteBuffsForCC();
        dispelDebuffs();
        cancelMagicDoor();
        endTradeIfExists();
        messengerCC();
        removeClones();
        addBuffsToWorldStorage();
        String ip = client.getChannelServer().getIP(channel);
        String[] socket = ip.split(":");
        getMap().removePlayer(this);
        destroyCheatTrackerStuff();
        setMap(ChannelServer.getInstance(channel).getMapFactory().getMap(mapid));
        ChannelServer.getInstance(client.getChannel()).removePlayer(this);
        client.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
        saveToDB(setspawnpoint, false, false);
        try {
            MaplePacket packet = MaplePacketCreator.getChannelChange(
                    InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]));
            client.getSession().write(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Smega limitations
     * @return
     */
    public boolean isAsmega() {
        return smega > 1;
    }

    public void setAllMega() {
        smega = 0;
    }

    public void setAsmega() {
        smega = 1;
    }

    public boolean isIrcmsg() {
        return ircmsg;
    }

    public void setIrcmsg(boolean ircmsg) {
        this.ircmsg = ircmsg;
    }

    public boolean isPsmega() {
        return smega >= 2;
    }

    public void setPsmega() {
        smega = 2;
    }

    // Tao management
    public int itemCount(int itemid) {
        MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemid);
        MapleInventory iv = inventory[type.ordinal()];
        int possesed = iv.countById(itemid);
        if (storage != null) {
            for (IItem noob : storage.getItems()) {
                if (noob.getItemId() == itemid) {
                    possesed += noob.getQuantity();
                }
            }
        }
        return possesed;
    }

    public int getItemQuantity(int itemid, boolean checkEquipped) {
        int possesed = inventory[MapleItemInformationProvider.getInstance().getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }

    public int getTaoOfSight() {
        return itemCount(Items.currencyType.Sight);
    }

    public boolean haveSight(int amount) {
        return haveItem(Items.currencyType.Sight, amount);
    }

    public int getTaoOfHarmony() {
        return itemCount(Items.currencyType.Harmony);
    }

    public int getTaoOfShadow() {
        return itemCount(Items.currencyType.Shadow);
    }

    /**
     * GMS Mode my version. Oliver's Idea
     */
    public void cancelGMSMode() {
        GMSMode = 0;
        autoap = 0;
        skills.clear();
        maxSkills(true);
        changeChannel(client.getChannel());
        dropMessage("You are no longer in GMS mode. You have " + storageAp + " AP in Storage");
    }

    public void setGMSMode(byte type) {
        if (reborn < 5) {
            dropMessage("You need to be above 5 RB to be able to turn on GMS mode");
            return;
        }
        if (!unequipEverything()) {
            dropMessage("You don't seem to have enough inventory Slots");
            return;
        }
        if (!addAllApToStorage(type)) {
            return;
        }
        autoap = 5;
        GMSMode = type;
        setExp(0);
        skills.clear();
        this.maxSkills(false);
        changeChannel(client.getChannel());
        dropMessage("[The Elite ninja Gang] You are now in GMS mode. You will not gain Exp by killing mobs. You will how ever gain stats and elevel ups from PQs.");
        dropMessage("[The Elite ninja Gang] You cannot stalk skills from other Jobs when you are in GMS mode. Your skills will be wiped when you change jobs");
        dropMessage("[The Elite Ninja Gang] Your level will not change when you level in GMS mode. Your AP will automagically be added to your storage Ap.");
    }

    public boolean addAllApToStorage(byte type) {
        int ap = getStr() + getDex() + getLuk() + getInt() + getRemainingAp();
        int[] apAllowed = {0, 169, 269, 369, 769, 1019};
        if (ap < apAllowed[type]) {
            dropMessage("You cannot scam me :p. Get " + apAllowed[type] + " before you can try again ");
            return false;
        }
        this.wipeStats();
        this.storageAp += (ap - (apAllowed[type] - 16));
        this.remainingAp = apAllowed[type] - 16;
        dropMessage("Your AP has been successfully added to Storage AP. You now have " + storageAp + " AP in storage");
        return true;
    }

    public byte getGMSMode() {
        return GMSMode;
    }

    public void maxJobSkills(boolean shit) {
        switch (job.getId()) {
            case 112:
                maxHeroSkills(shit);
                break;
            case 122:
                maxPaladinSkills(shit);
                break;
            case 132:
                maxDarkKnightSkills(shit);
                break;
            case 212:
                maxFPArchMageSkills(shit);
                break;
            case 222:
                maxILArchMageSkills(shit);
                break;
            case 232:
                maxBishopSkills(shit);
                break;
            case 312:
                maxBowMasterSkills(shit);
                break;
            case 322:
                maxCrossBowMasterSkills(shit);
                break;
            case 412:
                maxNightLordSkills(shit);
                break;
            case 422:
                maxShadowerSkills(shit);
                break;
            case 512:
                maxBuccaneerSkills(shit);
                break;
            case 522:
                maxCorsairSkills(shit);
                break;
            case 1111:
                maxDawnWarrior3Skills(shit);
                break;
            case 1211:
                maxBlazeWizard3Skills(shit);
                break;
            case 1311:
                maxWindArcher3Skills(shit);
                break;
            case 1411:
                maxNightWalker3Skills(shit);
                break;
            case 1511:
                maxThunderBreaker3Skills(shit);
                break;
        }
    }

    public void wipeSkills() {
        skills.clear();
    }

    // .....dojo!
    public int getDojoEnergy() {
        return dojoEnergy;
    }

    public int getDojoPoints() {
        return dojoPoints;
    }

    public int getDojoStage() {
        return lastDojoStage;
    }

    public void setDojoEnergy(int x) {
        this.dojoEnergy = x;
    }

    public void setDojoPoints(int x) {
        this.dojoPoints = x;
    }

    public void setDojoStage(int x) {
        this.lastDojoStage = x;
    }

    public void resetEnteredScript() {
        if (entered.containsKey(map.getId())) {
            entered.remove(map.getId());
        }
    }

    public void resetEnteredScript(int mapId) {
        if (entered.containsKey(mapId)) {
            entered.remove(mapId);
        }
    }

    public void resetEnteredScript(String script) {
        for (int mapId : entered.keySet()) {
            if (entered.get(mapId).equals(script)) {
                entered.remove(mapId);
            }
        }
    }

    public void enteredScript(String script, int mapid) {
        if (!entered.containsKey(mapid)) {
            entered.put(mapid, script);
        }
    }

    public boolean hasEntered(String script) {
        for (int mapId : entered.keySet()) {
            if (entered.get(mapId).equals(script)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEntered(String script, int mapId) {
        if (entered.containsKey(mapId)) {
            if (entered.get(mapId).equals(script)) {
                return true;
            }
        }
        return false;
    }

    public int addDojoPointsByMap() {
        int pts = 0;
        int oldpts = dojoPoints;
        pts = 2 + (int) Math.floor(((getMap().getId() - 1) / 100 % 100) / 6);
        if (party != null) {
            pts--;
        }
        if (dojoPoints < 170000) {
            this.dojoPoints += pts;
            checkForBelts(oldpts);
        }
        return pts;
    }

    private void checkForBelts(int oldpts) {
        int[] beltpoints = {2000, 18000, 40000, 92000, 170000};
        int[] belts = {1132000, 1132001, 1132002, 1132003, 1132004};
        int[] stats = {250, 500, 1337, 3337, 13337};
        for (int i = 0; i < beltpoints.length; i++) {
            if (beltpoints[i] < oldpts && dojoPoints >= beltpoints[i] && !haveItem(belts[i], 1)) {
                MapleInventoryManipulator.addStatItemById(client, belts[i], name, (short) stats[i], (short) 1, (short) 1);
                showMessage(1, "Congratulations on acheiving a belt!");
            }
        }
    }

    public void startMapEffect(String msg, int itemId) {
        startMapEffect(msg, itemId, 30000);
    }

    public void startMapEffect(String msg, int itemId, int duration) {
        final MapleMapEffect mapEffect = new MapleMapEffect(msg, itemId);
        getClient().getSession().write(mapEffect.makeStartData());
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                getClient().getSession().write(mapEffect.makeDestroyData());
            }
        }, duration);
    }

    public void startCygnusIntro() {
        client.getSession().write(MaplePacketCreator.CygnusIntroDisableUI(true));
        client.getSession().write(MaplePacketCreator.CygnusIntroLock(true));
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                client.getSession().write(MaplePacketCreator.CygnusIntroDisableUI(false));
                client.getSession().write(MaplePacketCreator.CygnusIntroLock(false));
                changeMap(913040006);
            }
        }, 54 * 1000); // 52 second = intro with "Please Help, Be my Knight..." 40 sec = without
    }

    public int getJobRank() {
        return jobrank;
    }

    public int getJobRankMove() {
        return jobrankmove;
    }

    public int getRank() {
        return rank;
    }

    public int getRankMove() {
        return rankmove;
    }

    public int getClantaorank() {
        return clantaorank;
    }

    public int getTaorank() {
        return taorank;
    }

    // autobuffs
    public void rebuff() {
        if (GMSMode > 0 && !isJounin()) {
            dropMessage(5, "[Meow]You could have got rebuff, but you are in GMS mode. So bleh ");
            return;
        }

        if (autobuffs != null) {
            for (int i : autobuffs) {
                SkillFactory.getSkill(i).getEffect(SkillFactory.getSkill(i).getMaxLevel()).applyTo(this);
            }
        }
    }

    public List<Integer> getAutobuffs() {
        return autobuffs;
    }

    public void addAutobuff(int buff) {
        autobuffs.add(buff);
        autobuffchange = true;
    }

    public void removeAutobuff(int id) {
        autobuffs.remove(id);
        autobuffchange = true;
    }

    // Boss Quest
    public int getBossPoints() {
        return bossPoints;
    }

    public void setBossPoints(int bossPoints) {
        this.bossPoints = bossPoints;
    }

    // MiniGames
    public MiniGame getMiniGame() {
        return miniGame;
    }

    public int getMiniGamePoints(String type, boolean omok) {
        if (omok) {
            if (type.equals("wins")) {
                return omokwins;
            } else if (type.equals("losses")) {
                return omoklosses;
            } else {
                return omokties;
            }
        } else {
            if (type.equals("wins")) {
                return matchcardwins;
            } else if (type.equals("losses")) {
                return matchcardlosses;
            } else {
                return matchcardties;
            }
        }
    }

    public void setMiniGame(MiniGame miniGame) {
        this.miniGame = miniGame;
    }

    public void setMiniGamePoints(boolean win, boolean loss, boolean omok) {
        if (omok) {
            if (win) {
                this.omokwins++;
            } else if (loss) {
                this.omoklosses++;
            } else {
                this.omokties++;
            }
        } else {
            if (win) {
                this.matchcardwins++;
            } else if (loss) {
                this.matchcardwins++;
            } else {
                this.matchcardties++;
            }
        }
    }

    public HiredMerchant getHiredMerchant() {
        return hiredMerchant;
    }

    public void setHasMerchant(boolean set) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET HasMerchant = ? WHERE id = ?");
            ps.setInt(1, set ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            return;
        }
        hasMerchant = set;
    }

    public void setHiredMerchant(HiredMerchant merchant) {
        this.hiredMerchant = merchant;
    }

    public boolean hasMerchant() {
        return hasMerchant;
    }

    public int getSlot() {
        return slots;
    }

    public void setSlot(int slots) {
        this.slots = slots;
    }

    // Rings
    // fff
    public int getEquippedRing(int type) {
        for (IItem item : getInventory(MapleInventoryType.EQUIPPED)) {
            IEquip equip = (IEquip) item;
            if (equip.getRingId() > 0) {
                int itemId = equip.getItemId();
                if ((itemId >= 1112001 && itemId <= 1112007 || itemId == 1112012) && type == 0) {
                    return equip.getRingId();
                }
                if (itemId >= 1112800 && itemId <= 1112802 && type == 1) {
                    return equip.getRingId();
                }
                if ((itemId >= 1112803 && itemId <= 1112807 || itemId == 1112809) && type == 2) {
                    return equip.getRingId();
                }
            }
        }
        return 0;
    }

    public boolean isRingEquipped(int ringId) {
        for (IItem item : getInventory(MapleInventoryType.EQUIPPED)) {
            IEquip equip = (IEquip) item;
            if (equip.getRingId() == ringId) {
                return equip.getPosition() <= (byte) -1;
            }
        }
        return false;
    }

    public List<MapleRing> getCrushRings() {
        Collections.sort(crushRings);
        return crushRings;
    }

    public List<MapleRing> getFriendshipRings() {
        Collections.sort(friendshipRings);
        return friendshipRings;
    }

    public List<MapleRing> getMarriageRings() {
        Collections.sort(marriageRings);
        return marriageRings;
    }

    public void addRingToCache(int ringId) {
        MapleRing ring = MapleRing.loadFromDb(ringId);
        if (ring.getItemId() >= 1112001 && ring.getItemId() <= 1112007 || ring.getItemId() == 1112012) {
            if (ring != null) {
                crushRings.add(ring);
            }
        } else if (ring.getItemId() >= 1112800 && ring.getItemId() <= 1112802) {
            if (ring != null) {
                friendshipRings.add(ring);
            }
        } else if (ring.getItemId() >= 1112803 && ring.getItemId() <= 1112807 || ring.getItemId() == 1112809) {
            if (ring != null) {
                marriageRings.add(ring);
            }
        }
    }

    public void relog() {
        client.getSession().write(MaplePacketCreator.getCharInfo(this));
        getMap().removePlayer(this);
        getMap().addPlayer(this);
    }
}
