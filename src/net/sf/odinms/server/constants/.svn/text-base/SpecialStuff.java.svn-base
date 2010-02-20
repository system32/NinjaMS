package net.sf.odinms.server.constants;

import net.sf.odinms.client.Inventory.Equip;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.net.channel.ChannelServer;

/**
 * This file is a server constant to give us list of special maps when we need it. 
 * @author System
 */
public class SpecialStuff {

    private static SpecialStuff instance;

    /**
     * This is here so that we can call it from any where xD
     */
    public SpecialStuff() {
    }

    /**
     * Instance to call from other places.
     * @return
     */
    public static SpecialStuff getInstance() {
        if (instance == null) {
            instance = new SpecialStuff();
        }
        return instance;
    }

    /*
     * Special Maps Starts here
     *
     *
     */
    /**
     * boolean to check if its a JQ map. input is map id
     * @author system
     * @param mapId
     * @return
     */
    public boolean isJQMap(int mapId) {
        switch (mapId) {
            case 103000900:// construction site b1 area 1
            case 103000901:// construction site b1 area 2
            case 103000902:// construction site b1 subway depot*
            case 103000903:// construction site b2 area 1
            case 103000904:// construction site b2 area 2
            case 103000905:// construction site b2 subway depot*
            case 103000906:// construction site b3 area 1
            case 103000907:// construction site b3 area 2
            case 103000908:// construction site b3 area 3
            case 103000909:// construction site b3 subway depot*
            case 280020001://adobis mission 1 breath of lava level 1
            case 280020000:// adobis mission breath of lava level 2*
            case 101000100:// FOP step 1
            case 101000101:// FOP step 2*
            case 101000102:// FOP step 3
            case 101000103:// FOP step 4
            case 101000104:// FOP step 5*
            case 105040310://DFOP 1
            case 105040311://DFOP 2*
            case 105040312://DFOP 3
            case 105040313://DFOP 4*
            case 105040314://DFOP 5
            case 105040315://DFOP 6
            case 105040316://DFOP 7*
            case 109040000:// Fitness Test waiting room
            case 109040001://Fitness level 1
            case 109040002://fitness level 2
            case 109040003://fitness level 3
            case 109040004://fitness level 4*
                return true;
            default:
                return false;
        }
    }

    /**
     * This returns a number which will be used to check whether the
     * map is a Jq finisher map and will also determine which
     * JQ page should be given as reward
     * @author System
     * @param mapId
     * @return
     */
    public byte finishJQMap(int mapId) {
        switch (mapId) {
            case 103000902:// construction site b1 subway depot*
                return 1;
            case 103000905:// construction site b2 subway depot*
                return 2;
            case 103000909:// construction site b3 subway depot*
                return 3;
            case 280020000:// adobis mission breath of lava level 2*
                return 4;
            case 101000101:// FOP step 2*
                return 5;
            case 101000104:// FOP step 5*
                return 6;
            case 105040311://DFOP 2*
                return 7;
            case 105040313://DFOP 4*
                return 8;
            case 105040316://DFOP 7*
                return 9;
            case 109040004://fitness level 4*
                return 10;
            default:
                return 0;
        }
    }

    /**
     * Boolean to check if its a PQ Map input is map id.
     * @param MapId
     * @return
     */
    public boolean isPQMap(int mapId) {
        if (isDojoMap(mapId)) {
            return true;
        }
        switch (mapId) {
            case 103000800:     //kpq stage 1
            case 103000801:
            case 103000802:
            case 103000803:
            case 103000804:
            case 103000805:     //kpq stage 5
                return true;
            default:
                return false;
        }
    }

    /**
     * check whether it is a donator map so we could resrict people from getting in it
     * and boot out nubs from it
     * @author System
     * @param mapId
     * @return
     */
    public boolean isDonatorMap(int mapId) {
        return mapId == 925100700;
    }

    /**
     * check whether it is a dojo map so we could resrict people from getting in it
     * and boot out nubs from it when they login.
     * @author System
     * @param mid
     * @return
     */
    public boolean isDojoMap(int mid) {
        if (mid >= 925020000 && mid <= 925033804) {
            return true;
        }
        return false;
    }

    /**
     * this is to check the warping to map id to
     * disallow retards from entering noob maps
     * @author System
     * @param player
     * @param mapId
     * @return boolean
     */
    public boolean cannotWarpTo(MapleCharacter player, int mapId) {
        if (player.isChunin()) {
            return false;
        }
        if ((mapId == 910000014 || mapId == 910000013) && player.getReborns() > 10) {
            return true;
        }
        if ((mapId > 910000002 && mapId < 910000006) && player.getReborns() > 100) {
            return true;
        }
        if ((mapId >= 910000006 && mapId < 910000010) && player.getReborns() < 50) {
            return true;
        }
        if (mapId == 910000006 && player.getReborns() >= 1000) {
            return true;
        }
        return false;
    }

    public boolean cannotWarpToPortal(MapleCharacter player, int mapId) {
        if (player.isChunin()) {
            return false;
        }
        if ((mapId == 910000014 || mapId == 910000013) && player.getReborns() > 10) {
            return true;
        }

        if ((mapId > 910000002 && mapId < 910000006) && player.getReborns() > 100) {
            return true;
        }
        if ((mapId >= 910000006 && mapId < 910000010) && player.getReborns() < 50) {
            return true;
        }
        return false;
    }

    public boolean isJail(int mapId) {
        return mapId == 200090300;
    }

    // specific variables!
    public boolean canWarpTo(MapleCharacter player, int mapid) {
        if (player.isChunin()) {
            return true;
        }
        if (cannotWarpTo(player, mapid)) {
            return false;
        }
        if (!canWarpFrom(player)) {
            return false;
        }
        if (isJQMap(mapid) || isPQMap(mapid)) {
            return false;
        }
        return true;
    }

    public boolean canWarpFrom(MapleCharacter player) {
        if (player.isChunin()) {
            return true;
        }
        if (isJail(player.getMapId())) {
            return false;
        }
        if (isPQMap(player.getMapId())) {
            return false;
        }
        return true;
    }

    public boolean isLoginAccessible(int mapid) {
        return !(isJQMap(mapid) || isPQMap(mapid));
    }

    public boolean canCCFrom(int mapid) {
        return !(isJail(mapid) || isPQMap(mapid));
    }

    public boolean isMistBlocked(int mapId) {
        return isEventMap(mapId) || isInert(mapId);
    }

    public boolean isSkillBlocked(int mapId) {
        return isJQMap(mapId) || isEventMap(mapId) || isJail(mapId);
    }

    private boolean isInert(int mapId) {
        return mapId == 910000000 || mapId == 100000000;
    }

    public boolean isEventMap(int mapId) {
        return mapId == 109020001;
    }

    public boolean isGMBlocked(int itemid) {
        return itemid == 4031679;
    }

    public boolean isGMSpawnBlocked(MapleCharacter player, int mid) {
        if (player.isHokage()) {
            return false;
        }
        return mid == 9400203;
    }

    public boolean canStoreTradeDrop(IItem item, MapleCharacter player) {
        if (item == null) {
            return false;
        }
        // early dec.
        if (player.isHokage()) {
            return true;
        }

        if (MapleItemInformationProvider.getInstance().isArrowForBow(item.getItemId())
                || MapleItemInformationProvider.getInstance().isArrowForCrossBow(item.getItemId())
                || MapleItemInformationProvider.getInstance().isThrowingStar(item.getItemId())
                || MapleItemInformationProvider.getInstance().isBullet(item.getItemId())) {
            return true;
        }

        if (ChannelServer.isShuttingDown() || player.cannotDrop()) {
            player.showMessage(1, "You cannot do this operation during this time.");
            return false;
        }
        if (player.getMute() > 0) {
            player.showMessage(1, "You cannot do this operation while you are muted!");
            return false;
        }
        MapleInventoryType type = MapleInventoryType.getByType(item.getType());
        if (type == MapleInventoryType.EQUIPPED || type == MapleInventoryType.EQUIP) {
            Equip equip = (Equip) item;
            if (equip.getLevel() > 68) {
                player.showMessage("You cannot drop a Stat Item. If you want to remove it any way, use @removeitem or @removeeqrow");
                return false;
            }
        }
        int itemid = item.getItemId();
        MapleInventoryType ivType = MapleItemInformationProvider.getInstance().getInventoryType(itemid);
        if (ivType != MapleItemInformationProvider.getInstance().getInventoryType(item.getItemId())) {
            AutobanManager.getInstance().autoban(player.getClient(), "Mismatching ivType.");
            return false;
        }
        if (isEventMap(player.getMapId())) {
            player.showMessage(1, "You cannot drop items here!");
            return false;
        }
        return true;
    }
}
