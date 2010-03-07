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
package net.sf.odinms.tools;

import java.awt.Point;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.Enums.MapleBuffStat;
import net.sf.odinms.client.Enums.MapleDisease;
import net.sf.odinms.client.Enums.MapleStat;
import net.sf.odinms.client.Inventory.IEquip;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.MapleKeyBinding;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.MapleQuestStatus;

import net.sf.odinms.client.Inventory.IEquip.ScrollResult;
import net.sf.odinms.client.Inventory.MapleRing;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.SkillMacro;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.ByteArrayMaplePacket;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.net.channel.handler.AbstractDealDamageHandler.AttackInfo;
import net.sf.odinms.net.channel.handler.SummonDamageHandler.SummonAttackEntry;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MaplePlayerShop;
import net.sf.odinms.server.MaplePlayerShopItem;
import net.sf.odinms.server.MapleShopItem;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.data.output.LittleEndianWriter;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;
import net.sf.odinms.net.world.guild.*;
import net.sf.odinms.server.CashItemInfo;
import net.sf.odinms.server.DueyPackages;
import net.sf.odinms.server.MiniGame;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.life.PlayerNPCs;
import net.sf.odinms.server.maps.HiredMerchant;
import net.sf.odinms.server.maps.MapleMist;
import net.sf.odinms.server.maps.MapleSummon;

/**
 * Provides all MapleStory packets needed in one place.
 * 
 * @author Frz
 * @since Revision 259
 * @version 1.0
 */
public class MaplePacketCreator {

    private static Logger log = LoggerFactory.getLogger(MaplePacketCreator.class);
    private final static byte[] CHAR_INFO_MAGIC = new byte[]{(byte) 0xff, (byte) 0xc9, (byte) 0x9a, 0x3b};
    private final static byte[] ITEM_MAGIC = new byte[]{(byte) 0x80, 5};
    public static final List<Pair<MapleStat, Integer>> EMPTY_STATUPDATE = Collections.emptyList();
    private final static long FT_UT_OFFSET = 116444592000000000L;
    private final static long NO_EXPIRATION = 150842304000000000L;

    private static long getKoreanTimestamp(long realTimestamp) {
        long time = (realTimestamp / 1000 / 60); //convert to minutes

        return ((time * 600000000) + FT_UT_OFFSET);
    }

    private static int getQuestTimestamp(long realTimestamp) {
        return (int) (((int) (realTimestamp / 1000 / 60)) * 0.1396987) + 27111908;
    }

    private static long getTime(long realTimestamp) {
        long time = (realTimestamp / 1000); // convert to seconds

        return ((time * 10000000) + FT_UT_OFFSET);
    }

    /**
     * Sends a hello packet.
     *
     * @param mapleVersion The maple client version.
     * @param sendIv the IV used by the server for sending
     * @param recvIv the IV used by the server for receiving
     */
    public static MaplePacket getHello(short mapleVersion, byte[] sendIv, byte[] recvIv, boolean testServer) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(0x0d);
        mplew.writeShort(mapleVersion);
        mplew.write(new byte[]{0, 0});
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(testServer ? 5 : 8);
        return mplew.getPacket();
    }

    /**
     * Sends a ping packet.
     *
     * @return The packet.
     */
    public static MaplePacket getPing() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.PING.getValue());
        return mplew.getPacket();
    }

    /**
     * Gets a login failed packet.
     *
     * Possible values for <code>reason</code>:<br>
     * 3: ID deleted or blocked<br>
     * 4: Incorrect password<br>
     * 5: Not a registered id<br>
     * 6: System error<br>
     * 7: Already logged in<br>
     * 8: System error<br>
     * 9: System error<br>
     * 10: Cannot process so many connections<br>
     * 11: Only users older than 20 can use this channel
     *
     * @param reason The reason logging in failed.
     * @return The login failed packet.
     */
    public static MaplePacket getLoginFailed(int reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeInt(reason);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket getPermBan(byte reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        // Response.WriteHexString("00 00 02 00 01 01 01 01 01 00");
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(0x02); // Account is banned

        mplew.write(0x0);
        mplew.write(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));
        return mplew.getPacket();
    }

    public static MaplePacket getTempBan(long timestampTill, byte reason) {

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(0x02);
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00")); // Account is banned

        mplew.write(reason);
        mplew.writeLong(timestampTill); // Tempban date is handled as a 64-bit long, number of 100NS intervals since
        // 1/1/1601. Lulz.

        return mplew.getPacket();
    }

    /**
     * Gets a successful authentication and PIN Request packet.
     *
     * @param account The account name.
     * @return The PIN request packet.
     */
    public static MaplePacket getAuthSuccessRequestPin(String account, boolean gm) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeInt(0); //user id
        mplew.write(0); //gender (0x0a == gender select, 0x0b == pin select)
        mplew.write(gm ? 1 : 0);
        mplew.write(0); //admin, doesn't let them chat
        mplew.write(0);
        mplew.writeMapleAsciiString(account);
        mplew.write(0);
        mplew.write(0);
        mplew.writeLong(0);
        mplew.writeLong(0); //creation time

        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     * Gets a packet detailing a PIN operation.
     *
     * Possible values for <code>mode</code>:<br>
     * 0 - PIN was accepted<br>
     * 1 - Register a new PIN<br>
     * 2 - Invalid pin / Reenter<br>
     * 3 - Connection failed due to system error<br>
     * 4 - Enter the pin
     *
     * @param mode The mode.
     */
    public static MaplePacket pinOperation(byte mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        mplew.writeShort(SendPacketOpcode.PIN_OPERATION.getValue());
        mplew.write(mode);
        return mplew.getPacket();
    }

    /**
     * Gets a packet requesting the client enter a PIN.
     *
     * @return The request PIN packet.
     */
    public static MaplePacket requestPin() {
        return pinOperation((byte) 4);
    }

    /**
     * Gets a packet requesting the PIN after a failed attempt.
     *
     * @return The failed PIN packet.
     */
    public static MaplePacket requestPinAfterFailure() {
        return pinOperation((byte) 2);
    }

    /**
     * Gets a packet saying the PIN has been accepted.
     *
     * @return The PIN accepted packet.
     */
    public static MaplePacket pinAccepted() {
        return pinOperation((byte) 0);
    }

    /**
     * Gets a packet detailing a server and its channels.
     *
     * @param serverIndex The index of the server to create information about.
     * @param serverName The name of the server.
     * @param channelLoad Load of the channel - 1200 seems to be max.
     * @return The server info packet.
     */
    public static MaplePacket getServerList(int serverIndex, String serverName, Map<Integer, Integer> channelLoad) {
        /*
         * 0B 00 00 06 00 53 63 61 6E 69 61 00 00 00 64 00 64 00 00 13 08 00 53 63 61 6E 69 61 2D 31 5E 04 00 00 00 00
         * 00 08 00 53 63 61 6E 69 61 2D 32 25 01 00 00 00 01 00 08 00 53 63 61 6E 69 61 2D 33 F6 00 00 00 00 02 00 08
         * 00 53 63 61 6E 69 61 2D 34 BC 00 00 00 00 03 00 08 00 53 63 61 6E 69 61 2D 35 E7 00 00 00 00 04 00 08 00 53
         * 63 61 6E 69 61 2D 36 BC 00 00 00 00 05 00 08 00 53 63 61 6E 69 61 2D 37 C2 00 00 00 00 06 00 08 00 53 63 61
         * 6E 69 61 2D 38 BB 00 00 00 00 07 00 08 00 53 63 61 6E 69 61 2D 39 C0 00 00 00 00 08 00 09 00 53 63 61 6E 69
         * 61 2D 31 30 C3 00 00 00 00 09 00 09 00 53 63 61 6E 69 61 2D 31 31 BB 00 00 00 00 0A 00 09 00 53 63 61 6E 69
         * 61 2D 31 32 AB 00 00 00 00 0B 00 09 00 53 63 61 6E 69 61 2D 31 33 C7 00 00 00 00 0C 00 09 00 53 63 61 6E 69
         * 61 2D 31 34 B9 00 00 00 00 0D 00 09 00 53 63 61 6E 69 61 2D 31 35 AE 00 00 00 00 0E 00 09 00 53 63 61 6E 69
         * 61 2D 31 36 B6 00 00 00 00 0F 00 09 00 53 63 61 6E 69 61 2D 31 37 DB 00 00 00 00 10 00 09 00 53 63 61 6E 69
         * 61 2D 31 38 C7 00 00 00 00 11 00 09 00 53 63 61 6E 69 61 2D 31 39 EF 00 00 00 00 12 00
         */

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(serverIndex);
        mplew.writeMapleAsciiString(serverName);
        mplew.write(3); // 1: E 2: N 3: H
        // mplew.writeShort(0);

        mplew.writeMapleAsciiString("");
        mplew.write(0x64); // rate modifier, don't ask O.O!

        mplew.write(0x0); // event xp * 2.6 O.O!

        mplew.write(0x64); // rate modifier, don't ask O.O!

        mplew.write(0x0); // drop rate * 2.6

        mplew.write(0x0);
        int lastChannel = 1;
        Set<Integer> channels = channelLoad.keySet();
        for (int i = 30; i > 0; i--) {
            if (channels.contains(i)) {
                lastChannel = i;
                break;
            }
        }
        mplew.write(lastChannel);

        int load;
        for (int i = 1; i <= lastChannel; i++) {
            if (channels.contains(i)) {
                load = channelLoad.get(i);
            } else {
                load = 1200;
            }
            mplew.writeMapleAsciiString(serverName + "-" + i);
            mplew.writeInt(load);
            mplew.write(serverIndex);
            mplew.writeShort(i - 1);
        }
        mplew.writeShort(0); // ver 0.56

        return mplew.getPacket();
    }

    /**
     * Gets a packet saying that the server list is over.
     *
     * @return The end of server list packet.
     */
    public static MaplePacket getEndOfServerList() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(0xFF);
        return mplew.getPacket();
    }

    /**
     * Gets a packet detailing a server status message.
     *
     * Possible values for <code>status</code>:<br>
     * 0 - Normal<br>
     * 1 - Highly populated<br>
     * 2 - Full
     *
     * @param status The server status.
     * @return The server status packet.
     */
    public static MaplePacket getServerStatus(int status) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        mplew.writeShort(status);
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client the IP of the channel server.
     *
     * @param inetAddr The InetAddress of the requested channel server.
     * @param port The port the channel is on.
     * @param clientId The ID of the client.
     * @return The server IP packet.
     */
    public static MaplePacket getServerIP(InetAddress inetAddr, int port, int clientId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVER_IP.getValue());
        mplew.writeShort(0);
        byte[] addr = inetAddr.getAddress();
        mplew.write(addr);
        mplew.writeShort(port);
        // 0x13 = numchannels?
        mplew.writeInt(clientId); // this gets repeated to the channel server
        // leos.write(new byte[] { (byte) 0x13, (byte) 0x37, 0x42, 1, 0, 0, 0,
        // 0, 0 });

        mplew.write(new byte[]{0, 0, 0, 0, 0});
        // 0D 00 00 00 3F FB D9 0D 8A 21 CB A8 13 00 00 00 00 00 00
        // ....?....!.........
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client the IP of the new channel.
     *
     * @param inetAddr The InetAddress of the requested channel server.
     * @param port The port the channel is on.
     * @return The server IP packet.
     */
    public static MaplePacket getChannelChange(InetAddress inetAddr, int port) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHANGE_CHANNEL.getValue());
        mplew.write(1);
        byte[] addr = inetAddr.getAddress();
        mplew.write(addr);
        mplew.writeShort(port);
        return mplew.getPacket();
    }

    /**
     * Gets a packet with a list of characters.
     *
     * @param c The MapleClient to load characters of.
     * @param serverId The ID of the server requested.
     * @return The character list packet.
     */
    public static MaplePacket getCharList(MapleClient c, int serverId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        mplew.write(0);
        List<MapleCharacter> chars = c.loadCharacters(serverId);
        mplew.write((byte) chars.size());

        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr);
        }

        mplew.writeInt(6); // Char limit

        return mplew.getPacket();
    }

    /**
     * Adds character stats to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add the stats of.
     */
    private static void addCharStats(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getId()); // character id
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(chr.getName(), '\0', 13));
        mplew.write(chr.getGender()); // gender (0 = male, 1 = female)
        mplew.write(chr.getSkinColor().getId()); // skin color
        mplew.writeInt(chr.getFace()); // face
        mplew.writeInt(chr.getHair()); // hair
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.write(chr.getLevel()); // level
        mplew.writeShort(chr.getJob().getId()); // job
        mplew.writeShort(chr.getStr()); // str
        mplew.writeShort(chr.getDex()); // dex
        mplew.writeShort(chr.getInt()); // int
        mplew.writeShort(chr.getLuk()); // luk
        mplew.writeShort(chr.getHp()); // hp (?)
        mplew.writeShort(chr.getMaxHp()); // maxhp
        mplew.writeShort(chr.getMp()); // mp (?)
        mplew.writeShort(chr.getMaxMp()); // maxmp
        mplew.writeShort(chr.getRemainingAp()); // remaining ap
        mplew.writeShort(chr.getRemainingSp()); // remaining sp
        mplew.writeInt(chr.getExp()); // current exp
        mplew.writeShort(chr.getFame()); // fame
        mplew.writeInt(0); // Gachapon EXP (Vana says this... WTF?)
        mplew.writeInt(chr.getMapId()); // current map id
        mplew.write(chr.getInitialSpawnpoint()); // spawnpoint
        mplew.writeInt(0);
    }

    /**
     * Adds the aesthetic aspects of a character to an existing
     * MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add the looks of.
     * @param mega Unknown
     */
    private static void addCharLook(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean mega) {
        mplew.write(2);
        mplew.write(chr.getSkinColor().getId());
        mplew.writeInt(chr.getFace());
        mplew.write(mega ? 0 : 1);
        mplew.writeInt(chr.getHair());
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
        // Map<Integer, Integer> equipped = new LinkedHashMap<Integer,
        // Integer>();
        Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        for (IItem item : equip.list()) {
            byte pos = (byte) (item.getPosition() * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, item.getItemId());
            } else if ((pos > 100 || pos == -128) && pos != 111) {
                pos -= 100;
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, item.getItemId());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, item.getItemId());
            }
        }
        for (Entry<Byte, Integer> entry : myEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF); // end of visible itens
        // masked itens

        for (Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        /*
         * for (IItem item : equip.list()) { byte pos = (byte)(item.getPosition() * -1); if (pos > 100) {
         * mplew.write(pos - 100); mplew.writeInt(item.getItemId()); } }
         */
        // ending markers
        mplew.write(0xFF);
        IItem cWeapon = equip.getItem((byte) -111);
        mplew.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
        for (int i = 0; i < 3; i++) {
            if (chr.getPet(i) != null) {
                mplew.writeInt(chr.getPet(i).getItemId());
            } else {
                mplew.writeInt(0);
            }
        }
    }

    /**
     * Adds an entry for a character to an existing
     * MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add.
     */
    private static void addCharEntry(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        addCharStats(mplew, chr);
        addCharLook(mplew, chr, false);
        if (chr.isJounin()) {
            mplew.write(0);
            return;
        }
        mplew.write(1); // world rank enabled (next 4 ints are not sent if disabled)
        mplew.writeInt(chr.getRank()); // world rank
        mplew.writeInt(chr.getRankMove()); // move (negative is downwards)
        mplew.writeInt(chr.getJobRank()); // job rank
        mplew.writeInt(chr.getJobRankMove()); // move (negative is downwards)

    }

    /**
     * Adds a quest info entry for a character to an existing
     * MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     *            to.
     * @param chr The character to add quest info about.
     */
    private static void addQuestInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        List<MapleQuestStatus> started = chr.getStartedQuests();
        mplew.writeShort(started.size());
        for (MapleQuestStatus q : started) {
            mplew.writeShort(q.getQuest().getId());
            String killStr = "";
            for (int kills : q.getMobKills().values()) {
                killStr += StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3);
            }
            mplew.writeMapleAsciiString(killStr);
        }
        List<MapleQuestStatus> completed = chr.getCompletedQuests();
        mplew.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            mplew.writeShort(q.getQuest().getId());
            mplew.writeLong(q.getCompletionTime());
        }
    }

    /**
     * Gets character info for a character.
     *
     * @param chr The character to get info about.
     * @return The character info packet.
     */
    public static MaplePacket getCharInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue()); // 0x49
        mplew.writeInt(chr.getClient().getChannel() - 1);
        mplew.write(1);
        mplew.write(1);
        mplew.writeShort(0);
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(Randomizer.getInstance().nextInt());
        }
        mplew.writeLong(-1);
        addCharStats(mplew, chr);
        mplew.write(chr.getBuddylist().getCapacity()); // buddylist capacity
        addInventoryInfo(mplew, chr);
        addSkillInfo(mplew, chr);
        addQuestInfo(mplew, chr);
        addRingInfo(mplew, chr);
        for (int x = 0; x < 15; x++) {
            mplew.write(CHAR_INFO_MAGIC);
        }
        addMonsterBookInfo(mplew, chr);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeLong(getTime((long) System.currentTimeMillis()));
        return mplew.getPacket();
    }

    public static void addRingInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeShort(0);
        mplew.writeShort(chr.getCrushRings().size());
        for (MapleRing ring : chr.getCrushRings()) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(ring.getPartnerName(), '\0', 13));
            mplew.writeInt(ring.getRingId());
            mplew.writeInt(0);
            mplew.writeInt(ring.getPartnerRingId());
            mplew.writeInt(0);
        }
        mplew.writeShort(chr.getFriendshipRings().size());
        for (MapleRing ring : chr.getFriendshipRings()) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(ring.getPartnerName(), '\0', 13));
            mplew.writeInt(ring.getRingId());
            mplew.writeInt(0);
            mplew.writeInt(ring.getPartnerRingId());
            mplew.writeInt(0);
            mplew.writeInt(ring.getItemId());
        }
        mplew.writeShort(chr.getMarriageRings().size());
        int marriageId = 30000;
        for (MapleRing ring : chr.getMarriageRings()) {
            mplew.writeInt(marriageId);
            mplew.writeInt(chr.getGender() == 0 ? chr.getId() : ring.getPartnerChrId());
            mplew.writeInt(chr.getGender() == 0 ? ring.getPartnerChrId() : chr.getId());
            mplew.writeShort(3);
            mplew.writeInt(ring.getItemId());
            mplew.writeInt(ring.getItemId());
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(chr.getGender() == 0 ? chr.getName() : ring.getPartnerName(), '\0', 13));
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(chr.getGender() == 0 ? ring.getPartnerName() : chr.getName(), '\0', 13));
            marriageId++;
        }

    }

    private static void addMonsterBookInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        //  mplew.writeInt(chr.getMonsterBookCover()); // cover
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeShort(0);
        // Map<Integer, Integer> cards = chr.getMonsterBook().getCards();
        // mplew.writeShort(cards.size());
        // for (Entry<Integer, Integer> all : cards.entrySet()) {
        //   mplew.writeShort(all.getKey() % 10000); // Id
        // mplew.write(all.getValue()); // Level
        // }
    }

    /**
     * Gets an empty stat update.
     *
     * @return The empy stat update packet.
     */
    public static MaplePacket enableActions() {
        return updatePlayerStats(EMPTY_STATUPDATE, true);
    }

    /**
     * Gets an update for specified stats.
     *
     * @param stats The stats to update.
     * @return The stat update packet.
     */
    public static MaplePacket updatePlayerStats(List<Pair<MapleStat, Integer>> stats) {
        return updatePlayerStats(stats, false);
    }

    /**
     * Gets an update for specified stats.
     *
     * @param stats The list of stats to update.
     * @param itemReaction Result of an item reaction(?)
     * @param pet Result of spawning a pet(?)
     * @return The stat update packet.
     */
    public static MaplePacket updatePlayerStats(List<Pair<MapleStat, Integer>> stats, boolean itemReaction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        if (itemReaction) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        int updateMask = 0;
        for (Pair<MapleStat, Integer> statupdate : stats) {
            updateMask |= statupdate.getLeft().getValue();
        }
        List<Pair<MapleStat, Integer>> mystats = stats;
        if (mystats.size() > 1) {
            Collections.sort(mystats, new Comparator<Pair<MapleStat, Integer>>() {

                @Override
                public int compare(Pair<MapleStat, Integer> o1, Pair<MapleStat, Integer> o2) {
                    int val1 = o1.getLeft().getValue();
                    int val2 = o2.getLeft().getValue();
                    return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
                }
            });
        }
        mplew.writeInt(updateMask);
        for (Pair<MapleStat, Integer> statupdate : mystats) {
            if (statupdate.getLeft().getValue() >= 1) {
                if (statupdate.getLeft().getValue() == 0x1) {
                    mplew.writeShort(statupdate.getRight().shortValue());
                } else if (statupdate.getLeft().getValue() <= 0x4) {
                    mplew.writeInt(statupdate.getRight());
                } else if (statupdate.getLeft().getValue() < 0x20) {
                    mplew.write(statupdate.getRight().shortValue());
                } else if (statupdate.getLeft().getValue() < 0xFFFF) {
                    mplew.writeShort(statupdate.getRight().shortValue());
                } else {
                    mplew.writeInt(statupdate.getRight().intValue());
                }
            }
        }

        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to change maps.
     *
     * @param to The <code>MapleMap</code> to warp to.
     * @param spawnPoint The spawn portal number to spawn at.
     * @param chr The character warping to <code>to</code>
     * @return The map change packet.
     */
    public static MaplePacket getWarpToMap(MapleMap to, int spawnPoint, MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue()); // 0x49

        mplew.writeInt(chr.getClient().getChannel() - 1);
        mplew.writeShort(0x2);
        mplew.writeShort(0);
        mplew.writeInt(to.getId());
        mplew.write(spawnPoint);
        mplew.writeShort(chr.getHp()); // hp (???)

        mplew.write(0);
        long questMask = 0x1ffffffffffffffL;
        mplew.writeLong(questMask);

        return mplew.getPacket();
    }

    /**
     * Gets a packet to spawn a portal.
     *
     * @param townId The ID of the town the portal goes to.
     * @param targetId The ID of the target.
     * @param pos Where to put the portal.
     * @return The portal spawn packet.
     */
    public static MaplePacket spawnPortal(int townId, int targetId, Point pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_PORTAL.getValue());
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        if (pos != null) {
            mplew.writeShort(pos.x);
            mplew.writeShort(pos.y);
        }
        return mplew.getPacket();
    }

    /**
     * Gets a packet to spawn a door.
     *
     * @param oid The door's object ID.
     * @param pos The position of the door.
     * @param town
     * @return The remove door packet.
     */
    public static MaplePacket spawnDoor(int oid, Point pos, boolean town) {
        // [D3 00] [01] [93 AC 00 00] [6B 05] [37 03]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_DOOR.getValue());

        mplew.write(town ? 1 : 0);
        mplew.writeInt(oid);
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y);

        return mplew.getPacket();
    }

    /**
     * Gets a packet to remove a door.
     *
     * @param oid The door's ID.
     * @param town
     * @return The remove door packet.
     */
    public static MaplePacket removeDoor(int oid, boolean town) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (town) {
            mplew.writeShort(SendPacketOpcode.SPAWN_PORTAL.getValue());
            mplew.writeInt(999999999);
            mplew.writeInt(999999999);
        } else {
            mplew.writeShort(SendPacketOpcode.REMOVE_DOOR.getValue());
            mplew.write(/*town ? 1 : */0);
            mplew.writeInt(oid);
        }
        return mplew.getPacket();
    }

    /**
     * Gets a packet to spawn a special map object.
     *
     * @param chr The MapleCharacter who spawned the object.
     * @param skill The skill used.
     * @param skillLevel The level of the skill used.
     * @param pos Where the object was spawned.
     * @param movementType Movement type of the object.
     * @param animated Animated spawn?
     * @return The spawn packet for the map object.
     */
    public static MaplePacket spawnSpecialMapObject(MapleSummon summon, int skillLevel, boolean animated) {
        // 72 00 29 1D 02 00 FD FE 30 00 19 7D FF BA 00 04 01 00 03 01 00
        // 85 00 [6A 4D 27 00] [35 1F 00 00] [2D 5D 20 00] [0C] [8C 16] [CA 01] [03] [00] [00] [01] [01] [00]

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_SPECIAL_MAPOBJECT.getValue());

        mplew.writeInt(summon.getOwner().getId());
        mplew.writeInt(summon.getObjectId()); // Supposed to be Object ID, but this works too! <3

        mplew.writeInt(summon.getSkill());
        mplew.write(skillLevel);
        mplew.writeShort(summon.getPosition().x);
        mplew.writeShort(summon.getPosition().y);
        mplew.write(3); // test

        mplew.write(0); // test

        mplew.write(0); // test

        mplew.write(summon.getMovementType().getValue()); // 0 = don't move, 1 = follow
        // (4th mage summons?), 2/4 =
        // only tele follow, 3 = bird
        // follow

        mplew.write(1); // 0 and the summon can't attack - but puppets don't
        // attack with 1 either ^.-

        mplew.write(animated ? 0 : 1);

        return mplew.getPacket();
    }

    /**
     * Gets a packet to remove a special map object.
     *
     * @param chr The MapleCharacter who removed the object.
     * @param skill The skill used to create the object.
     * @param animated Animated removal?
     * @return The packet removing the object.
     */
    public static MaplePacket removeSpecialMapObject(MapleSummon summon, boolean animated) {
        // [86 00] [6A 4D 27 00] 33 1F 00 00 02
        // 92 00 36 1F 00 00 0F 65 85 01 84 02 06 46 28 00 06 81 02 01 D9 00 BD FB D9 00 BD FB 38 04 2F 21 00 00 10 C1 2A 00 06 00 06 01 00 01 BD FB FC 00 BD FB 6A 04 88 1D 00 00 7D 01 AF FB
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REMOVE_SPECIAL_MAPOBJECT.getValue());

        mplew.writeInt(summon.getOwner().getId());
        mplew.writeInt(summon.getObjectId());

        mplew.write(animated ? 4 : 1); // ?

        return mplew.getPacket();
    }

    private static void addSkillInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        Map<ISkill, MapleCharacter.SkillEntry> skills = chr.getSkills();
        mplew.writeShort(skills.size());
        for (Entry<ISkill, MapleCharacter.SkillEntry> skill : skills.entrySet()) {
            mplew.writeInt(skill.getKey().getId());
            mplew.writeInt(skill.getValue().skillevel);
            if (skill.getKey().isFourthJob()) {
                mplew.writeInt(skill.getValue().masterlevel);
            }
        }
        mplew.writeShort(0);
    }

    private static void addInventoryInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getMeso());
        mplew.write(96);
        mplew.write(96);
        mplew.write(96);
        mplew.write(96);
        mplew.write(96);

        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);

        Collection<IItem> equippedC = iv.list();
        List<Item> equipped = new ArrayList<Item>(equippedC.size());
        List<Item> equippedCash = new ArrayList<Item>(equippedC.size());
        for (IItem item : equippedC) {
            if (item.getPosition() <= -100) {
                equippedCash.add((Item) item);
            } else {
                equipped.add((Item) item);
            }
        }
        Collections.sort(equipped);
        for (Item item : equipped) {
            addItemInfo(mplew, item);
        }
        mplew.write(0); // start of equip cash
        for (Item item : equippedCash) {
            addItemInfo(mplew, item);
        }

        mplew.write(0);

        iv = chr.getInventory(MapleInventoryType.EQUIP);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0);

        iv = chr.getInventory(MapleInventoryType.USE);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0);

        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0);

        iv = chr.getInventory(MapleInventoryType.ETC);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0);

        iv = chr.getInventory(MapleInventoryType.CASH);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0);
    }

    /**
     * Adds info about an item to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param item The item to write info about.
     */
    protected static void addItemInfo(MaplePacketLittleEndianWriter mplew, IItem item) {
        addItemInfo(mplew, item, false, false);
    }

    /**
     * Adds expiration time info to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param time The expiration time.
     * @param showexpirationtime Show the expiration time?
     */
    private static void addExpirationTime(MaplePacketLittleEndianWriter mplew, long time, boolean showexpirationtime) {
        mplew.writeInt(KoreanDateUtil.getItemTimestamp(time));
        mplew.write(showexpirationtime ? 1 : 2);
    }

    private static void addItemHasExpiryTime(MaplePacketLittleEndianWriter mplew, long expire) {
        boolean showexpiration = false;
        if (expire != -1) {
            mplew.writeInt(KoreanDateUtil.getItemTimestamp(expire));
            showexpiration = true;
        } else {
            mplew.writeInt(400967355);
        }
        mplew.write(showexpiration ? 1 : 2);
    }

    /**
     * Adds item info to existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param item The item to add info about.
     * @param zeroPosition Is the position zero?
     * @param leaveOut Leave out the item if position is zero?
     */
    public static void addItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, boolean zeroPosition, boolean leaveOut) {
        boolean ring = false;
        IEquip equip = null;
        if (item.getType() == IItem.EQUIP) {
            equip = (IEquip) item;
            if (equip.getRingId() > -1) {
                ring = true;
            }
        }
        byte pos = item.getPosition();
        boolean masking = false;
        if (zeroPosition) {
            if (!leaveOut) {
                mplew.write(0);
            }
        } else if (pos <= (byte) -1) {
            pos *= -1;
            if (pos > 100 || pos == -128 || ring) {
                masking = true;
                mplew.write(pos - 100);
            } else {
                mplew.write(pos);
            }
        } else {
            mplew.write(item.getPosition());
        }
        if (item.getUniqueId() > -1) {
            mplew.write(3);
        } else {
            mplew.write(item.getType());
        }
        mplew.writeInt(item.getItemId());
        if (ring) {
            mplew.write(1);
            mplew.writeInt(equip.getRingId());
            mplew.writeInt(0);
        }
        if (item.getUniqueId() > 0) {
            addPetInfo(mplew, MaplePet.loadFromDb(item.getItemId(), item.getPosition(), item.getUniqueId()));
            return;
        }
        if (masking && !ring) {
            mplew.write(HexTool.getByteArrayFromHexString("01 BE 50 17 00 00 00 00 00 B0 CE EB"));
            if (item.getExpiration() > -1) {
                addItemHasExpiryTime(mplew, item.getExpiration());
            } else {
                addExpirationTime(mplew, 0, false);
            }
        } else if (ring) {
            mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.2)));
        } else {
            mplew.writeShort(0);
            mplew.write(ITEM_MAGIC);
            if (item.getExpiration() > -1) {
                addItemHasExpiryTime(mplew, item.getExpiration());
            } else {
                addExpirationTime(mplew, 0, false);
            }
        }
        if (item.getType() == IItem.EQUIP) {
            mplew.write(equip.getUpgradeSlots());
            mplew.write(equip.getLevel());
            mplew.writeShort(equip.getStr()); // str
            mplew.writeShort(equip.getDex()); // dex
            mplew.writeShort(equip.getInt()); // int
            mplew.writeShort(equip.getLuk()); // luk
            mplew.writeShort(equip.getHp()); // hp
            mplew.writeShort(equip.getMp()); // mp
            mplew.writeShort(equip.getWatk()); // watk
            mplew.writeShort(equip.getMatk()); // matk
            mplew.writeShort(equip.getWdef()); // wdef
            mplew.writeShort(equip.getMdef()); // mdef
            mplew.writeShort(equip.getAcc()); // accuracy
            mplew.writeShort(equip.getAvoid()); // avoid
            mplew.writeShort(equip.getHands()); // hands
            mplew.writeShort(equip.getSpeed()); // speed
            mplew.writeShort(equip.getJump()); // jump
            mplew.writeMapleAsciiString(equip.getOwner());
            mplew.writeShort(0); //Item Flags
            if (!masking && !ring) {
                mplew.write(0);
                mplew.write(0); // item level
                mplew.writeShort(0);
                mplew.writeShort(1); // item exp
                mplew.writeShort(equip.getHammers());
                mplew.writeShort(0);
                mplew.writeLong(0);
            } else {
                mplew.write(HexTool.getByteArrayFromHexString("65 0A 28 27 F4 00 00 00 00 00"));
            }
            mplew.write(HexTool.getByteArrayFromHexString("00 40 E0 FD 3B 37 4F 01"));
            mplew.writeInt(-1);
        } else {
            mplew.writeShort(item.getQuantity());
            mplew.writeMapleAsciiString(item.getOwner());
            mplew.writeShort(0);
            if (MapleItemInformationProvider.getInstance().isThrowingStar(item.getItemId()) || MapleItemInformationProvider.getInstance().isBullet(item.getItemId())) {
                mplew.write(HexTool.getByteArrayFromHexString("02 00 00 00 54 00 00 34"));
            }
        }
    }

    private static void addPetInfo(MaplePacketLittleEndianWriter mplew, MaplePet pet) {
        addPetInfo(mplew, pet, true);
    }

    private static void addPetInfo(MaplePacketLittleEndianWriter mplew, MaplePet pet, boolean alive) {        
        mplew.write(1);
        mplew.writeInt(pet.getUniqueId());
        mplew.writeInt(0);
        mplew.writeLong(NO_EXPIRATION);
        String petname = pet.getName();
        if (petname.length() > 13) {
            petname = petname.substring(0, 13);
        }
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(petname, '\0', 13));
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        if (alive) {
            mplew.writeLong(getKoreanTimestamp((long) ((double) System.currentTimeMillis() * 1.2D)));
            mplew.writeInt(0);
            mplew.writeInt(0);
        } else {
            mplew.write(HexTool.getByteArrayFromHexString("00 80 05 bb 46 e6 17 02 00 00 00 00"));
            mplew.writeInt(0);
        }
    }

    /**
     * Gets the response to a relog request.
     *
     * @return The relog response packet.
     */
    public static MaplePacket getRelogResponse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        mplew.writeShort(SendPacketOpcode.RELOG_RESPONSE.getValue());
        mplew.write(1);
        return mplew.getPacket();
    }

    /**
     * Gets a server message packet.
     *
     * @param message The message to convey.
     * @return The server message packet.
     */
    public static MaplePacket serverMessage(String message) {
        return serverMessage(4, 0, message, true);
    }

    /**
     * Gets a server notice packet.
     *
     * Possible values for <code>type</code>:<br>
     * 0: [Notice]<br>
     * 1: Popup<br>
     * 2: Light blue background and lolwhut<br>
     * 4: Scrolling message at top<br>
     * 5: Pink Text<br>
     * 6: Lightblue Text
     *
     * @param type The type of the notice.
     * @param message The message to convey.
     * @return The server notice packet.
     */
    public static MaplePacket serverNotice(int type, String message) {
        return serverMessage(type, 0, message, false);
    }

    /**
     * Gets a server notice packet.
     *
     * Possible values for <code>type</code>:<br>
     * 0: [Notice]<br>
     * 1: Popup<br>
     * 2: Light blue background and lolwhut<br>
     * 4: Scrolling message at top<br>
     * 5: Pink Text<br>
     * 6: Lightblue Text
     *
     * @param type The type of the notice.
     * @param channel The channel this notice was sent on.
     * @param message The message to convey.
     * @return The server notice packet.
     */
    public static MaplePacket serverNotice(int type, int channel, String message) {
        return serverMessage(type, channel, message, false);
    }

    /**
     * Gets a server message packet.
     *
     * Possible values for <code>type</code>:<br>
     * 0: [Notice]<br>
     * 1: Popup<br>
     * 2: Light blue background and lolwhut<br>
     * 4: Scrolling message at top<br>
     * 5: Pink Text<br>
     * 6: Lightblue Text
     * 8: Item Megaphone
     *
     * @param type The type of the notice.
     * @param channel The channel this notice was sent on.
     * @param message The message to convey.
     * @param servermessage Is this a scrolling ticker?
     * @return The server notice packet.
     */
    private static MaplePacket serverMessage(int type, int channel, String message, boolean servermessage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 41 00 //
        // 08 //
        // 48 00 // 70 6F 6F 70 6F 6F 31 32 33 20 3A 20 3C 3C 53 65 6C 6C 69 6E 67 20 74 68 65 73 65 20 57 68 69 73 70 20 6F 66 66 65 72 73 20 31 38 30 6D 20 48 2F 4F 20 53 68 65 72 20 69 73 20 6C 61 7A 79 21 21 20 46 61 74 61 6C 3C 33 //
        // 11 //
        // 01 //

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue()); // 0.47:
        // 0x37,
        // unchanged

        mplew.write(type);
        if (servermessage) {
            mplew.write(1);
        }
        mplew.writeMapleAsciiString(message);

        if (type == 3) {
            mplew.write(channel - 1);
            mplew.write(0);
        } else if (type == 6) {
            mplew.writeInt(0);
        } else if (type == 8) {
            mplew.write(channel - 1);
            mplew.write(0);
        }

        return mplew.getPacket();
    }

    /**
     * Gets a server message packet.
     *
     * Possible values for <code>type</code>:<br />
     * 0: Megaphone<br />
     * 1: Supermegaphone<br />
     *
     *
     * @param type The type of the notice.
     * @param channel The channel this notice was sent on.
     * @param message The message to convey.
     * @param servermessage Is this a scrolling ticker?
     * @return The server notice packet.
     */
    public static MaplePacket getMegaphone(Items.MegaPhoneType type, int channel, String message, IItem item, boolean showEar) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(type.getValue());
        mplew.writeMapleAsciiString(message);
        if (type == Items.MegaPhoneType.SUPERMEGAPHONE) {
            mplew.write(channel - 1);
            mplew.write(showEar ? 1 : 0);
        } else if (type == Items.MegaPhoneType.ITEMMEGAPHONE) {
            mplew.write(channel - 1);
            mplew.write(showEar ? 1 : 0);
            if (item != null) {
                addItemInfo(mplew, item);
            } else {
                mplew.write(0);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket getTripleMegaphone(int channel, String[] messages, byte numlines, boolean showEar) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(10);
        if (messages[0] != null) {
            mplew.writeMapleAsciiString(messages[0]);
        }
        mplew.write(numlines);
        for (int i = 1; i < numlines; i++) {
            if (messages[i] != null) {
                mplew.writeMapleAsciiString(messages[i]);
            }
        }
        for (int i = 0; i < 10; i++) {
            mplew.write(channel - 1);
        }
        mplew.write(showEar ? 1 : 0);
        mplew.write(1);
        return mplew.getPacket();
    }

    /**
     * Gets an avatar megaphone packet.
     *
     * @param chr The character using the avatar megaphone.
     * @param channel The channel the character is on.
     * @param itemId The ID of the avatar-mega.
     * @param message The message that is sent.
     * @return The avatar mega packet.
     */
    public static MaplePacket getAvatarMega(MapleCharacter chr, int channel, int itemId, List<String> message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.AVATAR_MEGA.getValue());
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        for (String s : message) {
            mplew.writeMapleAsciiString(s);
        }
        mplew.writeInt(channel - 1); // channel

        mplew.write(0);
        addCharLook(mplew, chr, true);

        return mplew.getPacket();
    }

    /**
     * Gets a NPC spawn packet.
     *
     * @param life The NPC to spawn.
     * @param requestController Does the NPC want a controller?
     * @return The NPC spawn packet.
     */
    public static MaplePacket spawnNPC(MapleNPC life, boolean requestController) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // Request
        // E5 00 [01] [7D 01 00 00] [24 71 0F 00] [2B 0D] [8C FF] [01] [3F 00] [F9 0C] [5D 0D] [01]
        // Spawn
        // E3 00 [7E 01 00 00] [44 DB 8A 00] [00 02] [9A 00] [01] [77 00] [CE 01] [32 02] [01]
        if (requestController) {
            mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
            mplew.write(1); // ?

        } else {
            mplew.writeShort(SendPacketOpcode.SPAWN_NPC.getValue());
        }
        mplew.writeInt(life.getObjectId());
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        mplew.write(life.getF() == 1 ? 0 : 1); //Facing Left

        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());

        mplew.write(1);

        return mplew.getPacket();
    }

    /**
     * Gets a spawn monster packet.
     *
     * @param life The monster to spawn.
     * @param newSpawn Is it a new spawn?
     * @return The spawn monster packet.
     */
    public static MaplePacket spawnMonster(MapleMonster life, boolean newSpawn) {
        return spawnMonsterInternal(life, false, newSpawn, false, 0);
    }

    /**
     * Gets a spawn monster packet.
     *
     * @param life The monster to spawn.
     * @param newSpawn Is it a new spawn?
     * @param effect The spawn effect.
     * @return The spawn monster packet.
     */
    public static MaplePacket spawnMonster(MapleMonster life, boolean newSpawn, int effect) {
        return spawnMonsterInternal(life, false, newSpawn, false, effect);
    }

    /**
     * Gets a control monster packet.
     *
     * @param life The monster to give control to.
     * @param newSpawn Is it a new spawn?
     * @param aggro Aggressive monster?
     * @return The monster control packet.
     */
    public static MaplePacket controlMonster(MapleMonster life, boolean newSpawn, boolean aggro) {
        return spawnMonsterInternal(life, true, newSpawn, aggro, 0);
    }

    /**
     * Internal function to handler monster spawning and controlling.
     *
     * @param life The mob to perform operations with.
     * @param requestController Requesting control of mob?
     * @param newSpawn New spawn (fade in?)
     * @param aggro Aggressive mob?
     * @param effect The spawn effect to use.
     * @return The spawn/control packet.
     */
    private static MaplePacket spawnMonsterInternal(MapleMonster life, boolean requestController, boolean newSpawn,
            boolean aggro, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (requestController) {
            mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
            if (aggro) {
                mplew.write(2);
            } else {
                mplew.write(1);
            }
        } else {
            mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        }
        mplew.writeInt(life.getObjectId());
        mplew.write(5);

        mplew.writeInt(life.getId());
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(8);
        mplew.writeInt(0);

        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance()); // or 5? o.O"

        mplew.writeShort(0); // seems to be left and right
        // restriction...

        mplew.writeShort(life.getFh());

        if (effect > 0) {
            mplew.write(effect);
            mplew.write(0);
            mplew.writeShort(0);
            if (effect == 15) {//15 seems to add a byte... (Dojo spawn effect)
                mplew.write(0);
            }
        }

        if (newSpawn) {
            mplew.writeShort(-2);
        } else {
            mplew.writeShort(-1);
        }

        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     * Handles spawning monsters that spawn after another is killed
     * @param life The mob to spawn
     * @param parent The OID of the parent mob
     * @return The packet to spawn the mob
     */
    public static MaplePacket spawnRevives(MapleMonster life, int parent) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());
        mplew.write(0); // Status

        mplew.writeShort(0);
        mplew.write(8);
        mplew.writeInt(0);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getStartFh());
        mplew.write(0xFD); // FD

        mplew.writeInt(parent); // oid of the mob that spawned it

        mplew.writeShort(-1);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     * Handles monsters not being targettable, such as Zakum's first body.
     * @param life The mob to spawn as non-targettable.
     * @param effect The effect to show when spawning.
     * @return The packet to spawn the mob as non-targettable.
     */
    public static MaplePacket spawnFakeMonster(MapleMonster life, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());

        mplew.write(1);
        mplew.writeInt(life.getObjectId());
        mplew.write(5);
        mplew.writeInt(life.getId());
        mplew.writeInt(0);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(life.getStartFh());
        mplew.writeShort(life.getFh());

        if (effect > 0) {
            mplew.write(effect);
            mplew.write(0);
            mplew.writeShort(0);
        }

        mplew.writeShort(-2);

        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     * Makes a monster previously spawned as non-targettable, targettable.
     * @param life The mob to make targettable.
     * @return The packet to make the mob targettable.
     */
    public static MaplePacket makeMonsterReal(MapleMonster life) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());

        mplew.writeInt(life.getObjectId());
        mplew.write(5);
        mplew.writeInt(life.getId());
        mplew.writeInt(0);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(life.getStartFh());
        mplew.writeShort(life.getFh());
        mplew.writeShort(-1);

        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     * Gets a stop control monster packet.
     *
     * @param oid The ObjectID of the monster to stop controlling.
     * @return The stop control monster packet.
     */
    public static MaplePacket stopControllingMonster(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(0);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    /**
     * Gets a response to a move monster packet.
     *
     * @param objectid The ObjectID of the monster being moved.
     * @param moveid The movement ID.
     * @param currentMp The current MP of the monster.
     * @param useSkills Can the monster use skills?
     * @return The move response packet.
     */
    public static MaplePacket moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills) {
        return moveMonsterResponse(objectid, moveid, currentMp, useSkills, 0, 0);
    }

    /**
     * Gets a response to a move monster packet.
     *
     * @param objectid The ObjectID of the monster being moved.
     * @param moveid The movement ID.
     * @param currentMp The current MP of the monster.
     * @param useSkills Can the monster use skills?
     * @param skillId The skill ID for the monster to use.
     * @param skillLevel The level of the skill to use.
     * @return The move response packet.
     */
    public static MaplePacket moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skillId, int skillLevel) {
        // A1 00 18 DC 41 00 01 00 00 1E 00 00 00
        // A1 00 22 22 22 22 01 00 00 00 00 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER_RESPONSE.getValue());
        mplew.writeInt(objectid);
        mplew.writeShort(moveid);
        mplew.write(useSkills ? 1 : 0);
        mplew.writeShort(currentMp);
        mplew.write(skillId);
        mplew.write(skillLevel);

        return mplew.getPacket();
    }

    /**
     * Gets a general chat packet.
     *
     * @param cidfrom The character ID who sent the chat.
     * @param text The text of the chat.
     * @return The general chat packet.
     */
    public static MaplePacket getChatText(int cidfrom, String text, boolean whiteBG, int show) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHATTEXT.getValue());
        mplew.writeInt(cidfrom);
        mplew.write(whiteBG ? 1 : 0);
        mplew.writeMapleAsciiString(text);
        mplew.write(show);
        return mplew.getPacket();
    }

    /**
     * For testing only! Gets a packet from a hexadecimal string.
     *
     * @param hex The hexadecimal packet to create.
     * @return The MaplePacket representing the hex string.
     */
    public static MaplePacket getPacketFromHexString(String hex) {
        byte[] b = HexTool.getByteArrayFromHexString(hex);
        return new ByteArrayMaplePacket(b);
    }

    /**
     * Gets a packet telling the client to show an EXP increase.
     *
     * @param gain The amount of EXP gained.
     * @param inChat In the chat box?
     * @param white White text or yellow?
     * @return The exp gained packet.
     */
    public static MaplePacket getShowExpGain(int gain, boolean inChat, boolean white, short party) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(3); // 3 = exp, 4 = fame, 5 = mesos, 6 = guildpoints
        mplew.write(white ? 1 : 0);
        mplew.writeInt(gain);
        mplew.write(inChat ? 1 : 0);
        mplew.writeInt(0); // monster book bonus
        mplew.write(0);
        mplew.write(((short) party) * 10); // Party bonus
        mplew.writeShort(0);
        mplew.writeLong(0);
        mplew.writeInt(0);
        if (inChat) {
            mplew.writeInt(0); // some ring bonus/ party exp ??
        } else { // some ring bonus/ party exp
            mplew.writeShort(0);
            mplew.write(0);
        }
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show a meso gain.
     *
     * @param gain How many mesos gained.
     * @return The meso gain packet.
     */
    public static MaplePacket getShowMesoGain(int gain) {
        return getShowMesoGain(gain, false);
    }

    /**
     * Gets a packet telling the client to show a meso gain.
     *
     * @param gain How many mesos gained.
     * @param inChat Show in the chat window?
     * @return The meso gain packet.
     */
    public static MaplePacket getShowMesoGain(int gain, boolean inChat) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        if (!inChat) {
            mplew.write(0);
            mplew.write(1);
        } else {
            mplew.write(5);
        }
        mplew.writeInt(gain);
        mplew.writeShort(0); // inet cafe meso gain ?.o

        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show a item gain.
     *
     * @param itemId The ID of the item gained.
     * @param quantity How many items gained.
     * @return The item gain packet.
     */
    public static MaplePacket getShowItemGain(int itemId, short quantity) {
        return getShowItemGain(itemId, quantity, false);
    }

    /**
     * Gets a packet telling the client to show an item gain.
     *
     * @param itemId The ID of the item gained.
     * @param quantity The number of items gained.
     * @param inChat Show in the chat window?
     * @return The item gain packet.
     */
    public static MaplePacket getShowItemGain(int itemId, short quantity, boolean inChat) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (inChat) {
            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(3);
            mplew.write(1);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
        } else {
            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.writeShort(0);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client that a monster was killed.
     *
     * @param oid The objectID of the killed monster.
     * @param animation Show killed animation?
     * @return The kill monster packet.
     */
    public static MaplePacket killMonster(
            int oid, boolean animation) {
        return killMonster(oid, animation ? 1 : 0);
    }

    public static MaplePacket killMonster(int oid, int animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(animation); // Not a boolean, really an int type
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show mesos coming out of a map
     * object.
     *
     * @param amount The amount of mesos.
     * @param itemoid The ObjectID of the dropped mesos.
     * @param dropperoid The OID of the dropper.
     * @param ownerid The ID of the drop owner.
     * @param dropfrom Where to drop from.
     * @param dropto Where the drop lands.
     * @param mod ?
     * @return The drop mesos packet.
     */
    public static MaplePacket dropMesoFromMapObject(
            int amount, int itemoid, int dropperoid, int ownerid, Point dropfrom, Point dropto, byte mod) {
        return dropItemFromMapObjectInternal(amount, itemoid, dropperoid, ownerid, ownerid == 0 ? 2 : 0, dropfrom, dropto, mod, true);
    }

    public static MaplePacket dropMesoFromMapObject(
            int amount, int itemoid, int dropperoid, int ownerid, int typadrop, Point dropfrom, Point dropto, byte mod) {
        return dropItemFromMapObjectInternal(amount, itemoid, dropperoid, ownerid, typadrop, dropfrom, dropto, mod, true);
    }

    /**
     * Gets a packet telling the client to show an item coming out of a map
     * object.
     *
     * @param itemid The ID of the dropped item.
     * @param itemoid The ObjectID of the dropped item.
     * @param dropperoid The OID of the dropper.
     * @param ownerid The ID of the drop owner.
     * @param dropfrom Where to drop from.
     * @param dropto Where the drop lands.
     * @param mod ?
     * @return The drop mesos packet.
     */
    public static MaplePacket dropItemFromMapObject(int itemid, int itemoid, int dropperoid, int ownerid, Point dropfrom, Point dropto, byte mod) {
        return dropItemFromMapObjectInternal(itemid, itemoid, dropperoid, ownerid, ownerid == 0 ? 2 : 0, dropfrom, dropto, mod, false);
    }

    public static MaplePacket dropItemFromMapObject(int itemid, int itemoid, int dropperoid, int ownerid, int typadrop, Point dropfrom, Point dropto, byte mod) {
        return dropItemFromMapObjectInternal(itemid, itemoid, dropperoid, ownerid, typadrop, dropfrom, dropto, mod, false);
    }

    /**
     * Internal function to get a packet to tell the client to drop an item onto
     * the map.
     *
     * @param itemid The ID of the item to drop.
     * @param itemoid The ObjectID of the dropped item.
     * @param dropperoid The OID of the dropper.
     * @param ownerid The ID of the drop owner.
     * @param dropfrom Where to drop from.
     * @param dropto Where the drop lands.
     * @param mod ?
     * @param mesos Is the drop mesos?
     * @return The item drop packet.
     */
    public static MaplePacket dropItemFromMapObjectInternal(int itemid, int itemoid, int dropperoid, int ownerid, int mode, Point dropfrom, Point dropto, byte mod, boolean mesos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
        mplew.write(mod);
        mplew.writeInt(itemoid);
        mplew.write(mesos ? 1 : 0);
        mplew.writeInt(itemid);
        mplew.writeInt(ownerid);
        mplew.write(mode); // DROP OWNERSHIP (0 = owner, 1 = party, 2 = FFA, 3 = explosive/FFA)
        mplew.writeShort(dropto.x);
        mplew.writeShort(dropto.y);
        if (mod != 2) {
            mplew.writeInt(ownerid);
            mplew.writeShort(dropfrom.x);
            mplew.writeShort(dropfrom.y);
        } else {
            mplew.writeInt(dropperoid);
        }
        mplew.write(0);
        if (mod != 2) {
            mplew.write(1);
            mplew.write(mesos ? 1 : 0);
        }
        if (!mesos) {
            mplew.write(ITEM_MAGIC);
            addExpirationTime(mplew, System.currentTimeMillis(), false);
            mplew.write(0);
        }

        return mplew.getPacket();
    }

    /**
     * Gets a packet spawning a player as a mapobject to other clients.
     *
     * @param chr The character to spawn to other clients.
     * @return The spawn player packet.
     */
    public static MaplePacket spawnPlayerMapobject(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_PLAYER.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeMapleAsciiString(chr.getName());
        if (chr.getGuildId() < 1) {
            mplew.writeMapleAsciiString("");
            mplew.write(new byte[6]);
        } else {
            MapleGuildSummary gs = chr.getClient().getChannelServer().getGuildSummary(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeMapleAsciiString("");
                mplew.write(new byte[6]);
            }
        }
        mplew.writeInt(0); //Not sure anymore this should be an int.
        mplew.write(0xf8);
        mplew.write(3);
        mplew.writeShort(0);
        if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
            mplew.writeInt(2);
        } else {
            mplew.writeInt(0);
        }
        long buffmask = 0;
        Integer buffvalue = null;
        if (chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null && !chr.isHidden()) {
            buffmask |= MapleBuffStat.DARKSIGHT.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.COMBO) != null) {
            buffmask |= MapleBuffStat.COMBO.getValue();
            buffvalue = Integer.valueOf(chr.getBuffedValue(MapleBuffStat.COMBO).intValue());
        }
        if (chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) {
            buffmask |= MapleBuffStat.SHADOWPARTNER.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
            buffmask |= MapleBuffStat.SOULARROW.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
            buffvalue = Integer.valueOf(chr.getBuffedValue(MapleBuffStat.MORPH).intValue());
        }
        mplew.writeInt((int) ((buffmask >> 32) & 0xffffffffL));
        if (buffvalue != null) {
            if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
                mplew.writeShort(buffvalue);
            } else {
                mplew.write(buffvalue.byteValue());
            }
        }
        mplew.writeInt((int) (buffmask & 0xffffffffL));
        int CHAR_MAGIC_SPAWN = Randomizer.getInstance().nextInt();
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0); //v74
        mplew.write(0); //v74
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);//v74
        mplew.writeShort(0);
        mplew.write(0); //v74
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        mplew.write(0);
        IItem mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
        if (chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
            if (chr.getBuffSource(MapleBuffStat.MONSTER_RIDING) != 5221006) {
                mplew.writeInt(mount.getItemId());
                mplew.writeInt(chr.getBuffSource(MapleBuffStat.MONSTER_RIDING));
                mplew.writeInt(CHAR_MAGIC_SPAWN);
            } else {
                mplew.writeInt(1932000);
                mplew.writeInt(5221006);
                mplew.writeInt(CHAR_MAGIC_SPAWN);
            }
        } else {
            mplew.writeLong(0); // 2 int
            mplew.writeInt(CHAR_MAGIC_SPAWN);
        }
        mplew.write(0);
        mplew.writeLong(0); // 2 int
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeLong(0); // 2 int
        mplew.writeLong(0); // 2 int
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.write(0);
        mplew.writeLong(0); // 2 int
        mplew.writeInt(0);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        mplew.write(0);
        if (chr.getJob().getId() < 910 && !chr.isJounin()) {
            mplew.writeShort(412);
        } else {
            mplew.writeShort(1411);
        }
        addCharLook(mplew, chr, false);

        mplew.writeInt(0);
        mplew.writeInt(chr.getItemEffect());
        mplew.writeInt(chr.getChair());
        mplew.writeShort(chr.getPosition().x);
        mplew.writeShort(chr.getPosition().y);
        mplew.write(chr.getStance());
        mplew.writeShort(0); // FH
        // pets apparently.
        mplew.writeShort(0); // announce box.
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        addRingLooks(mplew, chr);
        mplew.write(0);
        return mplew.getPacket();
    }

    private static void addRingLooks(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.write(chr.getEquippedRing(0) != 0 ? 1 : 0);
        for (MapleRing ring : chr.getCrushRings()) {
            if (ring.getRingId() == chr.getEquippedRing(0)) {
                mplew.writeInt(ring.getRingId());
                mplew.writeInt(0);
                mplew.writeInt(ring.getPartnerRingId());
                mplew.writeInt(0);
                mplew.writeInt(ring.getItemId());
            }
        }
        mplew.write(chr.getEquippedRing(1) != 0 ? 1 : 0);
        for (MapleRing ring : chr.getFriendshipRings()) {
            if (ring.getRingId() == chr.getEquippedRing(1)) {
                mplew.writeInt(ring.getRingId());
                mplew.writeInt(0);
                mplew.writeInt(ring.getPartnerRingId());
                mplew.writeInt(0);
                mplew.writeInt(ring.getItemId());
            }
        }
        mplew.write(chr.getEquippedRing(2) != 0 ? 1 : 0);
        for (MapleRing ring : chr.getMarriageRings()) {
            if (ring.getRingId() == chr.getEquippedRing(2)) {
                mplew.writeInt(ring.getPartnerChrId());
                mplew.writeInt(chr.getId());
                mplew.writeInt(ring.getItemId());
            }
        }
        mplew.write(0);
    }


    /* public static MaplePacket spawnPlayerMapobject(MapleCharacter chr) {
    MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
    mplew.writeShort(SendPacketOpcode.SPAWN_PLAYER.getValue());
    mplew.writeInt(chr.getId());
    mplew.writeMapleAsciiString(chr.getName());
    if (chr.getGuildId() < 1) {
    mplew.writeMapleAsciiString("");
    mplew.write(new byte[6]);
    } else {
    MapleGuildSummary gs = chr.getClient().getChannelServer().getGuildSummary(chr.getGuildId());
    if (gs != null) {
    mplew.writeMapleAsciiString(gs.getName());
    mplew.writeShort(gs.getLogoBG());
    mplew.write(gs.getLogoBGColor());
    mplew.writeShort(gs.getLogo());
    mplew.write(gs.getLogoColor());
    } else {
    mplew.writeMapleAsciiString("");
    mplew.write(new byte[6]);
    }
    }
    mplew.writeInt(0); //Not sure anymore this should be an int.
    mplew.write(0xf8);
    mplew.write(3);
    mplew.writeShort(0);
    if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
    mplew.writeInt(2);
    } else {
    mplew.writeInt(0);
    }
    long buffmask = 0;
    Integer buffvalue = null;
    if (chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null && !chr.isHidden()) {
    buffmask |= MapleBuffStat.DARKSIGHT.getValue();
    }
    if (chr.getBuffedValue(MapleBuffStat.COMBO) != null) {
    buffmask |= MapleBuffStat.COMBO.getValue();
    buffvalue = Integer.valueOf(chr.getBuffedValue(MapleBuffStat.COMBO).intValue());
    }
    if (chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) {
    buffmask |= MapleBuffStat.SHADOWPARTNER.getValue();
    }
    if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
    buffmask |= MapleBuffStat.SOULARROW.getValue();
    }
    if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
    buffvalue = Integer.valueOf(chr.getBuffedValue(MapleBuffStat.MORPH).intValue());
    }
    mplew.writeInt((int) ((buffmask >> 32) & 0xffffffffL));
    if (buffvalue != null) {
    if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
    mplew.writeShort(buffvalue);
    } else {
    mplew.write(buffvalue.byteValue());
    }
    }
    mplew.writeInt((int) (buffmask & 0xffffffffL));
    int CHAR_MAGIC_SPAWN = Randomizer.getInstance().nextInt();
    mplew.writeInt(0);
    mplew.writeShort(0);
    mplew.writeInt(CHAR_MAGIC_SPAWN);
    mplew.writeShort(0); //v74
    mplew.write(0); //v74
    mplew.writeInt(0);
    mplew.writeInt(0);
    mplew.writeInt(CHAR_MAGIC_SPAWN);//v74
    mplew.writeShort(0);
    mplew.write(0); //v74
    mplew.writeInt(0);
    mplew.writeInt(0);
    mplew.writeInt(CHAR_MAGIC_SPAWN);
    mplew.writeShort(0);
    mplew.write(0);
    IItem mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
    if (chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
    if (chr.getBuffSource(MapleBuffStat.MONSTER_RIDING) != 5221006) {
    mplew.writeInt(mount.getItemId());
    mplew.writeInt(chr.getBuffSource(MapleBuffStat.MONSTER_RIDING));
    mplew.writeInt(CHAR_MAGIC_SPAWN);
    } else {
    mplew.writeInt(1932000);
    mplew.writeInt(5221006);
    mplew.writeInt(CHAR_MAGIC_SPAWN);
    }
    } else {
    mplew.writeLong(0); // 2 int
    mplew.writeInt(CHAR_MAGIC_SPAWN);
    }
    mplew.write(0);
    mplew.writeLong(0); // 2 int
    mplew.writeInt(CHAR_MAGIC_SPAWN);
    mplew.writeLong(0); // 2 int
    mplew.writeLong(0); // 2 int
    mplew.writeInt(CHAR_MAGIC_SPAWN);
    mplew.write(0);
    mplew.writeLong(0); // 2 int
    mplew.writeInt(0);
    mplew.writeInt(CHAR_MAGIC_SPAWN);
    mplew.writeShort(0);
    mplew.write(0);
    if (chr.getJob().getId() < 910 && !chr.isJounin()) {
    mplew.writeShort(412);
    } else {
    mplew.writeShort(1411);
    }
    addCharLook(mplew, chr, false);
    mplew.writeInt(0);
    mplew.writeInt(chr.getItemEffect());
    mplew.writeInt(chr.getChair());
    mplew.writeShort(chr.getPosition().x);
    mplew.writeShort(chr.getPosition().y);
    mplew.write(chr.getStance());
    mplew.writeShort(0); // FH
    mplew.write(0);
    for (byte i = 0; i < 3; i++) {
    MaplePet pet = chr.getPet(1);
    if (pet != null) {
    mplew.write(1);
    mplew.writeInt(pet.getItemId());
    mplew.writeMapleAsciiString(pet.getName());
    mplew.writeInt(pet.getUniqueId());
    mplew.writeInt(0);
    mplew.writeShort(chr.getPosition().x);
    mplew.writeShort(chr.getPosition().y);
    mplew.write(pet.getStance());
    mplew.writeInt(0);
    }
    }
    mplew.writeLong(0); // 2 int
    mplew.writeLong(0); // 2 int
    mplew.writeShort(0); // actually 2 byte
    String omg = chr.getChalkboard();
    if (omg != null) {
    mplew.write(1);
    mplew.writeMapleAsciiString(omg);
    } else {
    mplew.write(0);
    }
    //mplew.writeInt(0); // ring
    mplew.writeShort(0);
    mplew.write(0);
    return mplew.getPacket();

    } */
    /**
     * Adds a announcement box to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to add an announcement box
     *            to.
     * @param shop The shop to announce.
     */
    private static void addAnnounceBox(MaplePacketLittleEndianWriter mplew, MaplePlayerShop shop) {
        // 00: no game
        // 01: omok game
        // 02: card game
        // 04: shop
        mplew.write(4);
        mplew.writeInt(shop.getObjectId()); // gameid/shopid

        mplew.writeMapleAsciiString(shop.getDescription()); // desc
        // 00: public
        // 01: private
        mplew.write(0);
        // 00: red 4x3
        // 01: green 5x4
        // 02: blue 6x5
        // omok:
        // 00: normal
        mplew.write(0);
        // first slot: 1/2/3/4
        // second slot: 1/2/3/4
        mplew.write(1);
        mplew.write(4);
        // 0: open
        // 1: in progress
        mplew.write(0);
    }

    public static MaplePacket facialExpression(MapleCharacter from, int expression) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FACIAL_EXPRESSION.getValue());

        mplew.writeInt(from.getId());
        mplew.writeInt(expression);

        return mplew.getPacket();
    }

    private static void serializeMovementList(LittleEndianWriter lew, List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }

    public static MaplePacket movePlayer(int cid, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MOVE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(0);
        serializeMovementList(mplew, moves);
        return mplew.getPacket();
    }

    public static MaplePacket moveSummon(int cid, int oid, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_SUMMON.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.writeShort(startPos.x);
        mplew.writeShort(startPos.y);
        serializeMovementList(mplew, moves);
        return mplew.getPacket();
    }

    public static MaplePacket moveMonster(int useskill, int skill, int skill_1, int skill_2, int skill_3, int oid, Point startPos, List<LifeMovementFragment> moves) {
        /*
         * A0 00 C8 00 00 00 00 FF 00 00 00 00 48 02 7D FE 02 00 1C 02 7D FE 9C FF 00 00 2A 00 03 BD 01 00 DC 01 7D FE
         * 9C FF 00 00 2B 00 03 7B 02
         */
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
        // mplew.writeShort(0xA2); // 47 a0
        mplew.writeInt(oid);
        mplew.write(useskill);
        mplew.write(skill);
        mplew.write(skill_1);
        mplew.write(skill_2);
        mplew.write(skill_3);
        mplew.write(0);
        mplew.writeShort(startPos.x);
        mplew.writeShort(startPos.y);
        serializeMovementList(mplew, moves);
        return mplew.getPacket();
    }

    public static MaplePacket summonAttack(int cid, int summonSkillId, int newStance, List<SummonAttackEntry> allDamage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SUMMON_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(newStance);
        mplew.write(allDamage.size());
        for (SummonAttackEntry attackEntry : allDamage) {
            mplew.writeInt(attackEntry.getMonsterOid()); // oid
            mplew.write(6); // who knows
            mplew.writeInt(attackEntry.getDamage()); // damage
        }
        return mplew.getPacket();
    }

    public static MaplePacket closeRangeAttack(MapleCharacter player, AttackInfo attack) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOSE_RANGE_ATTACK.getValue());
        int skillLevel = player.getSkillLevel(SkillFactory.getSkill(attack.skill));
        if (attack.skill == 4211006) {
            addMesoExplosion(mplew, player.getId(), attack.skill, skillLevel, attack.stance, attack.numAttackedAndDamage, 0, attack.allDamage, attack.speed);
        } else {
            if (player.isfake) {
                addFakeAttackBody(mplew, player.getId(), attack, skillLevel, 0);
            } else {
                addAttackBody(mplew, player.getId(), attack, skillLevel, 0);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket rangedAttack(MapleCharacter player, AttackInfo attack, int projectile) {
        // 7E 00 30 75 00 00 01 00 97 04 0A CB 72 1F 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.RANGED_ATTACK.getValue());
        // mplew.writeShort(0x80); // 47 7E
        int skillLevel = player.getSkillLevel(SkillFactory.getSkill(attack.skill));
        if (player.isfake) {
            addFakeAttackBody(mplew, player.getId(), attack, skillLevel, 0);
        } else {
            addAttackBody(mplew, player.getId(), attack, skillLevel, 0);
        }
        mplew.writeLong(0); // This is 8 bytes, read from the received packet
        //  e.g. (88 05 00 80 EB 02 8F FF)
        return mplew.getPacket();
    }

    public static MaplePacket magicAttack(MapleCharacter player, AttackInfo attack) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MAGIC_ATTACK.getValue());
        int skillLevel = player.getSkillLevel(SkillFactory.getSkill(attack.skill));
        if (player.isfake) {
            addFakeAttackBody(mplew, player.getId(), attack, skillLevel, 0);
        } else {
            addAttackBody(mplew, player.getId(), attack, skillLevel, 0);
        }
        if (attack.charge != -1) {
            mplew.writeInt(attack.charge);
        }
        return mplew.getPacket();
    }

    private static void addAttackBody(LittleEndianWriter lew, int cid, AttackInfo attack, int skillLevel, int projectile) {
        lew.writeInt(cid);
        lew.write(attack.numAttackedAndDamage);
        if (attack.skill > 0) {
            lew.write(skillLevel); // too low and some skills don't work (?)
            lew.writeInt(attack.skill);
        } else {
            lew.write(0);
        }
        lew.write(0);
        lew.write(attack.stance);
        lew.write(attack.speed);
        lew.write(3);
        lew.writeInt(projectile);
        for (Pair<Integer, List<Integer>> oned : attack.allDamage) {
            if (oned.getRight() != null) {
                lew.writeInt(oned.getLeft().intValue());
                lew.write(6);
                for (Integer eachd : oned.getRight()) {
                    lew.writeInt(eachd.intValue());
                }
            }
        }
        lew.writeInt(attack.pos);
    }

    private static void addMesoExplosion(LittleEndianWriter lew, int cid, int skill, int skillLevel, int stance,
            int numAttackedAndDamage, int projectile,
            List<Pair<Integer, List<Integer>>> damage, int speed) {
        // 7A 00 6B F4 0C 00 22 1E 3E 41 40 00 38 04 0A 00 00 00 00 44 B0 04 00
        // 06 02 E6 00 00 00 D0 00 00 00 F2 46 0E 00 06 02 D3 00 00 00 3B 01 00
        // 00
        // 7A 00 6B F4 0C 00 00 1E 3E 41 40 00 38 04 0A 00 00 00 00
        lew.writeInt(cid);
        lew.write(numAttackedAndDamage);
        lew.write(skillLevel);
        lew.writeInt(skill);
        lew.write(0);
        lew.write(stance);
        lew.write(speed);
        lew.write(0x0A);
        lew.writeInt(projectile);
        for (Pair<Integer, List<Integer>> oned : damage) {
            if (oned.getRight() != null) {
                lew.writeInt(oned.getLeft().intValue());
                lew.write(0xFF);
                lew.write(oned.getRight().size());
                for (Integer eachd : oned.getRight()) {
                    lew.writeInt(eachd.intValue());
                }
            }
        }
    }

    public static MaplePacket getNPCShop(int sid, List<MapleShopItem> items) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OPEN_NPC_SHOP.getValue());
        mplew.writeInt(sid);
        mplew.writeShort(items.size());
        for (MapleShopItem item : items) {
            mplew.writeInt(item.getItemId());
            mplew.writeInt(item.getPrice());
            if (!ii.isThrowingStar(item.getItemId()) && !ii.isBullet(item.getItemId())) {
                mplew.writeShort(1);
                mplew.writeShort(item.getMaxSlot());
            } else {
                mplew.writeLong(Double.doubleToLongBits(ii.getPrice(item.getItemId())));
                mplew.writeShort(item.getMaxSlot());
            }
        }
        return mplew.getPacket();
    }

    /**
     * code (8 = sell, 0 = buy, 0x20 = due to an error the trade did not happen
     * o.o)
     *
     * @param code
     * @return
     */
    public static MaplePacket confirmShopTransaction(byte code) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CONFIRM_SHOP_TRANSACTION.getValue());
        mplew.write(code);
        return mplew.getPacket();
    }

    /*
     * 19 reference 00 01 00 = new while adding 01 01 00 = add from drop 00 01 01 = update count 00 01 03 = clear slot
     * 01 01 02 = move to empty slot 01 02 03 = move and merge 01 02 01 = move and merge with rest
     */
    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item) {
        return addInventorySlot(type, item, false);
    }

    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        // mplew.writeShort(0x19);
        if (fromDrop) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        mplew.write(HexTool.getByteArrayFromHexString("01 00")); // add mode
        mplew.write(type.getType()); // iv type
        mplew.write(item.getPosition()); // slot id
        if (item.getUniqueId() != -1 && item.getItemId() > 5000000) {
            addPetInfo(mplew, MaplePet.loadFromDb(item.getItemId(), item.getPosition(), item.getUniqueId()), true);
        } else {
            addItemInfo(mplew, item, true, false);
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item) {
        return updateInventorySlot(type, item, false);
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        if (fromDrop) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        mplew.write(HexTool.getByteArrayFromHexString("01 01"));
        mplew.write(type.getType());
        mplew.write(item.getPosition());
        mplew.write(0);
        mplew.writeShort(item.getQuantity());
        return mplew.getPacket();
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, byte src, byte dst) {
        return moveInventoryItem(type, src, dst, (byte) -1);
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, byte src, byte dst, byte equipIndicator) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(1);
        mplew.write(1);
        mplew.write(2);
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(dst);
        if (equipIndicator != -1) {
            mplew.write(equipIndicator);
        }
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeInventoryItem(MapleInventoryType type, byte src, byte dst, short total) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.write(1); // merge mode?
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(total);
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeWithRestInventoryItem(MapleInventoryType type, byte src, byte dst,
            short srcQ, short dstQ) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 01"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(srcQ);
        mplew.write(HexTool.getByteArrayFromHexString("01"));
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(dstQ);
        return mplew.getPacket();
    }

    public static MaplePacket clearInventoryItem(MapleInventoryType type, byte slot, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(fromDrop ? 1 : 0);
        mplew.write(HexTool.getByteArrayFromHexString("01 03"));
        mplew.write(type.getType());
        mplew.writeShort(slot);
        return mplew.getPacket();
    }

    public static MaplePacket scrolledItem(IItem scroll, IItem item, boolean destroyed) {
        // 18 00 01 02 03 02 08 00 03 01 F7 FF 01
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(1); // fromdrop always true
        mplew.write(destroyed ? 2 : 3);
        mplew.write(scroll.getQuantity() > 0 ? 1 : 3);
        mplew.write(MapleInventoryType.USE.getType());
        mplew.writeShort(scroll.getPosition());
        if (scroll.getQuantity() > 0) {
            mplew.writeShort(scroll.getQuantity());
        }
        mplew.write(3);
        if (!destroyed) {
            mplew.write(MapleInventoryType.EQUIP.getType());
            mplew.writeShort(item.getPosition());
            mplew.write(0);
        }
        mplew.write(MapleInventoryType.EQUIP.getType());
        mplew.writeShort(item.getPosition());

        if (!destroyed) {
            addItemInfo(mplew, item, true, true);
        }
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket getScrollEffect(int chr, ScrollResult scrollSuccess, boolean legendarySpirit) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_SCROLL_EFFECT.getValue());
        mplew.writeInt(chr);
        switch (scrollSuccess) {
            case SUCCESS:
                mplew.writeShort(1);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            case FAIL:
                mplew.writeShort(0);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            case CURSE:
                mplew.write(0);
                mplew.write(1);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            default:
                throw new IllegalArgumentException("effect in illegal range");
        }
        return mplew.getPacket();
    }

    public static MaplePacket removePlayerFromMap(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP.getValue());
        // mplew.writeShort(0x65); // 47 63
        mplew.writeInt(cid);
        return mplew.getPacket();
    }

    /**
     * animation: 0 - expire<br/> 1 - without animation<br/> 2 - pickup<br/>
     * 4 - explode<br/> cid is ignored for 0 and 1
     *
     * @param oid
     * @param animation
     * @param cid
     * @return
     */
    public static MaplePacket removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, false, 0);
    }

    /**
     * animation: 0 - expire<br/> 1 - without animation<br/> 2 - pickup<br/>
     * 4 - explode<br/> cid is ignored for 0 and 1.<br /><br />Flagging pet
     * as true will make a pet pick up the item.
     *
     * @param oid
     * @param animation
     * @param cid
     * @param pet
     * @param slot
     * @return
     */
    public static MaplePacket removeItemFromMap(int oid, int animation, int cid, boolean pet) {
        return removeItemFromMap(oid, animation, cid, pet, 0);
    }

    public static MaplePacket removeItemFromMap(int oid, int animation, int cid, boolean pet, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
        mplew.write(animation); // expire
        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(cid);
            if (pet) {
                mplew.write(slot);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateCharLook(MapleCharacter chr) {
        // 88 00 80 74 03 00 01 00 00 19 50 00 00 00 67 75 00 00 02 34 71 0F 00
        // 04 59 BF 0F 00 05 AB 05 10 00 07 8C 5B
        // 10 00 08 F4 82 10 00 09 E7 D0 10 00 0A BE A9 10 00 0B 0C 05 14 00 FF
        // FF 00 00 00 00 00 00 00 00 00 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_LOOK.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        addCharLook(mplew, chr, false);
        mplew.write(0);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket dropInventoryItem(MapleInventoryType type, short src) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        // mplew.writeShort(0x19);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);






        if (src < 0) {
            mplew.write(1);






        }
        return mplew.getPacket();






    }

    public static MaplePacket dropInventoryItemUpdate(MapleInventoryType type, IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01"));
        mplew.write(type.getType());
        mplew.writeShort(item.getPosition());
        mplew.writeShort(item.getQuantity());






        return mplew.getPacket();






    }

    public static MaplePacket damagePlayer(int skill, int monsteridfrom, int cid, int damage, int fake, int direction, boolean pgmr, int pgmr_1, boolean is_pg, int oid, int pos_x, int pos_y) {
        // 82 00 30 C0 23 00 FF 00 00 00 00 B4 34 03 00 01 00 00 00 00 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DAMAGE_PLAYER.getValue());
        // mplew.writeShort(0x84); // 47 82
        mplew.writeInt(cid);
        mplew.write(skill);
        mplew.writeInt(damage);
        mplew.writeInt(monsteridfrom);
        mplew.write(direction);






        if (pgmr) {
            mplew.write(pgmr_1);
            mplew.write(is_pg ? 1 : 0);
            mplew.writeInt(oid);
            mplew.write(6);
            mplew.writeShort(pos_x);
            mplew.writeShort(pos_y);
            mplew.write(0);






        } else {
            mplew.writeShort(0);






        }

        mplew.writeInt(damage);







        if (fake > 0) {
            mplew.writeInt(fake);






        }

        return mplew.getPacket();






    }

    public static MaplePacket charNameResponse(String charname, boolean nameUsed) {
        // 0D 00 0C 00 42 6C 61 62 6C 75 62 62 31 32 33 34 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);







        return mplew.getPacket();






    }

    public static MaplePacket addNewCharEntry(MapleCharacter chr, boolean worked) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        mplew.write(worked ? 0 : 1);
        addCharEntry(
                mplew, chr);






        return mplew.getPacket();






    }

    /**
     *
     * @param c
     * @param quest
     * @return
     */
    public static MaplePacket startQuest(MapleCharacter c, short quest) {
        // [24 00] [01] [69 08] [01 00] [00]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // mplew.writeShort(0x21);
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.writeShort(1);
        mplew.write(0);






        return mplew.getPacket();






    }

    /**
     * state 0 = del ok state 12 = invalid bday
     *
     * @param cid
     * @param state
     * @return
     */
    public static MaplePacket deleteCharResponse(int cid, int state) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        mplew.writeInt(cid);
        mplew.write(state);






        return mplew.getPacket();






    }

    public static MaplePacket charInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHAR_INFO.getValue());
        // mplew.writeShort(0x31);
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob().getId());
        mplew.writeShort(chr.getFame());
        mplew.write(1); // heart red or gray
        String allianceName = "";






        if (chr.getGuildId() <= 0) {
            mplew.writeMapleAsciiString("-"); // guild






        } else {
            MapleGuildSummary gs = null;
            gs = chr.getClient().getChannelServer().getGuildSummary(chr.getGuildId());






            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());






                try {
                    MapleAlliance alliance = chr.getClient().getChannelServer().getWorldInterface().getAlliance(gs.getAllianceId());






                    if (alliance != null) {
                        allianceName = alliance.getName();






                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                    chr.getClient().getChannelServer().reconnectWorld();






                }
            } else {
                mplew.writeMapleAsciiString("-"); // Guild






            }
        }
        mplew.writeMapleAsciiString(allianceName); // Alliance
        mplew.write(0);


        MaplePet[] pets = chr.getPets();






        for (int i = 0; i
                < 3; i++) {
            if (pets[i] != null) {
                mplew.write(pets[i].getUniqueId());
                mplew.writeInt(pets[i].getItemId()); // petid
                mplew.writeMapleAsciiString(pets[i].getName());
                mplew.write(pets[i].getLevel()); // pet level
                mplew.writeShort(pets[i].getCloseness()); // pet closeness
                mplew.write(pets[i].getFullness()); // pet fullness
                mplew.writeShort(0); // ??
                mplew.writeInt(0);






            }
        }

        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(1);
        mplew.writeLong(0);
        mplew.writeLong(0);






        return mplew.getPacket();






    }

    /**
     *
     * @param c
     * @param quest
     * @return
     */
    public static MaplePacket forfeitQuest(MapleCharacter c, short quest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);






        return mplew.getPacket();






    }

    /**
     *
     * @param c
     * @param quest
     * @return
     */
    public static MaplePacket completeQuest(MapleCharacter c, short quest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        /*  mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.write(HexTool.getByteArrayFromHexString("02 A0 67 B9 DA 69 3A C8 01"));
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();*/
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.write(2);
        mplew.writeLong(getTime(System.currentTimeMillis()));






        return mplew.getPacket();






    }

    /**
     *
     * @param c
     * @param quest
     * @param npc
     * @param progress
     * @return
     */
    // frz note, 0.52 transition: this is only used when starting a quest and
    // seems to have no effect, is it needed?
    public static MaplePacket updateQuestInfo(MapleCharacter c, short quest, int npc, byte progress) {
        // [A5 00] [08] [69 08] [86 71 0F 00] [00 00 00 00]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
        mplew.write(progress);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(0);






        return mplew.getPacket();






    }

    private static long getLongMask(List<Pair<MapleBuffStat, Integer>> statups) {
        long mask = 0;






        for (Pair<MapleBuffStat, Integer> statup : statups) {
            mask |= statup.getLeft().getValue();






        }
        return mask;






    }

    private static long getLongMaskFromList(List<MapleBuffStat> statups) {
        long mask = 0;






        for (MapleBuffStat statup : statups) {
            mask |= statup.getValue();






        }
        return mask;






    }

    private static long getLongMaskD(List<Pair<MapleDisease, Integer>> statups) {
        long mask = 0;






        for (Pair<MapleDisease, Integer> statup : statups) {
            mask |= statup.getLeft().getValue();






        }
        return mask;






    }

    private static long getLongMaskFromListD(List<MapleDisease> statups) {
        long mask = 0;






        for (MapleDisease statup : statups) {
            mask |= statup.getValue();






        }
        return mask;






    }

    /**
     * It is important that statups is in the correct order (see decleration
     * order in MapleBuffStat) since this method doesn't do automagical
     * reordering.
     *
     * @param buffid
     * @param bufflength
     * @param statups
     * @param morph
     * @param firstLong
     * @return
     */
    public static MaplePacket giveBuff(int buffid, int bufflength, List<Pair<MapleBuffStat, Integer>> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());







        long mask = getLongMask(statups);

        mplew.writeLong(0);
        mplew.writeLong(mask);






        for (Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeInt(buffid);
            mplew.writeInt(bufflength);






        }
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);







        return mplew.getPacket();






    }

    /**
     * @param buffid
     * @param bufflength
     * @param statups
     * @param morph
     * @param firstLong
     * @return
     */
    public static MaplePacket giveSpeedInfusion(int buffid, int bufflength, List<Pair<MapleBuffStat, Integer>> statups, int addedInfo) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());







        long mask = getLongMask(statups);

        mplew.writeLong(mask);
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeInt(statups.get(0).getRight().intValue());
        mplew.writeInt(buffid);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeShort(bufflength);
        mplew.writeShort(addedInfo);






        return mplew.getPacket();






    }

    /**
     * @param buffid
     * @param bufflength
     * @param statups
     * @param morph
     * @param firstLong
     * @return
     */
    public static MaplePacket givePirateBuff(int buffid, int bufflength, List<Pair<MapleBuffStat, Integer>> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());







        long mask = getLongMask(statups);

        mplew.writeLong(mask);
        mplew.writeLong(0);
        mplew.writeShort(0);






        for (Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeShort(0);
            mplew.writeInt(buffid);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeShort(bufflength);






        }
        mplew.writeShort(0);
        mplew.write(0);







        return mplew.getPacket();






    }

    public static MaplePacket giveMountBuff(int skillid, int mountid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());

        mplew.writeLong(MapleBuffStat.MONSTER_RIDING.getValue());
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeInt(mountid);
        mplew.writeInt(skillid);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0);







        return mplew.getPacket();






    }

    public static MaplePacket giveDebuff(List<Pair<MapleDisease, Integer>> statups, MobSkill skill) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());






        long mask = getLongMaskD(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);






        for (Pair<MapleDisease, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeShort(skill.getSkillId());
            mplew.writeShort(skill.getSkillLevel());
            mplew.writeInt((int) skill.getDuration());






        }
        mplew.writeShort(0); // ??? wk charges have 600 here o.o
        mplew.writeShort(900);//Delay
        mplew.write(1);






        return mplew.getPacket();






    }

    public static MaplePacket giveEnergyCharge(
            int barammount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        mplew.writeLong(0);
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue()); //energy charge buffstat
        mplew.writeShort(0);
        mplew.writeShort(barammount); // 0=no bar, 10000=full bar
        mplew.writeShort(0);
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeShort(0);






        return mplew.getPacket();






    }

    public static MaplePacket giveForeignEnergyCharge(
            int cid, int barammount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.writeLong(0);
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue()); //energy charge buffstat
        mplew.writeShort(0);
        mplew.writeShort(barammount); // 0=no bar, 10000=full bar
        mplew.writeShort(0);
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeShort(0);






        return mplew.getPacket();






    }

    public static MaplePacket giveForeignDebuff(int cid, List<Pair<MapleDisease, Integer>> statups, MobSkill skill) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);






        long mask = getLongMaskD(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);






        for (@SuppressWarnings("unused") Pair<MapleDisease, Integer> statup : statups) {
            //mplew.writeShort(statup.getRight().byteValue());
            mplew.writeShort(skill.getSkillId());
            mplew.writeShort(skill.getSkillLevel());






        }
        mplew.writeShort(0); // same as give_buff
        mplew.writeShort(900);//Delay






        return mplew.getPacket();






    }

    public static MaplePacket cancelForeignDebuff(int cid, List<MapleDisease> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());

        mplew.writeInt(cid);






        long mask = getLongMaskFromListD(statups);

        mplew.writeLong(0);
        mplew.writeLong(mask);







        return mplew.getPacket();






    }

    public static MaplePacket showMountBuff(int cid, int skillid, int mountid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.writeLong(MapleBuffStat.MONSTER_RIDING.getValue());
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeInt(mountid);
        mplew.writeInt(skillid);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0);







        return mplew.getPacket();






    }

    public static MaplePacket showPirateBuff(int cid, int skillid, int time, List<Pair<MapleBuffStat, Integer>> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);






        long mask = getLongMask(statups);
        mplew.writeLong(mask);
        mplew.writeLong(0);
        mplew.writeShort(0);






        for (Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight());
            mplew.writeShort(0);
            mplew.writeInt(skillid);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeShort(time);






        }
        mplew.writeShort(0);







        return mplew.getPacket();






    }

    public static MaplePacket showSpeedInfusion(int cid, int skillid, int time, List<Pair<MapleBuffStat, Integer>> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);






        long mask = getLongMask(statups);
        mplew.writeLong(mask);
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeInt(statups.get(0).getRight());
        mplew.writeInt(skillid);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeShort(time);
        mplew.writeShort(0);







        return mplew.getPacket();






    }

    public static MaplePacket giveForeignBuff(int cid, List<Pair<MapleBuffStat, Integer>> statups, boolean morph) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);






        long mask = getLongMask(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);







        for (Pair<MapleBuffStat, Integer> statup : statups) {
            if (morph) {
                mplew.writeInt(statup.getRight());






            } else {
                mplew.writeShort(statup.getRight());






            }
        }

        mplew.writeShort(0);
        mplew.write(0);







        return mplew.getPacket();






    }

    public static MaplePacket cancelForeignBuff(int cid, List<MapleBuffStat> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);






        long mask = getLongMaskFromList(statups);
        mplew.writeLong(isFirstLong(statups) ? mask : 0);
        mplew.writeLong(isFirstLong(statups) ? 0 : mask);







        return mplew.getPacket();






    }

    public static MaplePacket cancelBuff(List<MapleBuffStat> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());







        long mask = getLongMaskFromList(statups);

        mplew.writeLong(isFirstLong(statups) ? mask : 0);
        mplew.writeLong(isFirstLong(statups) ? 0 : mask);
        mplew.write(3); // wtf?







        return mplew.getPacket();






    }

    private static boolean isFirstLong(List<MapleBuffStat> statups) {
        for (MapleBuffStat stat : statups) {
            if (stat.equals(MapleBuffStat.DASH)
                    || stat.equals(MapleBuffStat.DASH2)
                    || stat.equals(MapleBuffStat.SPEED_INFUSION)
                    || stat.equals(MapleBuffStat.MONSTER_RIDING)
                    || stat.equals(MapleBuffStat.ENERGY_CHARGE)) {
                return true;






            }
        }
        return false;






    }

    public static MaplePacket cancelDebuff(List<MapleDisease> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());






        long mask = getLongMaskFromListD(statups);

        mplew.writeLong(0);
        mplew.writeLong(mask);

        mplew.write(0);






        return mplew.getPacket();






    }

    public static MaplePacket getPlayerShopChat(MapleCharacter c, String chat, boolean owner) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("06 08"));
        mplew.write(owner ? 0 : 1);
        mplew.writeMapleAsciiString(c.getName() + " : " + chat);






        return mplew.getPacket();






    }

    public static MaplePacket getPlayerShopNewVisitor(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("04 02"));
        addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        return mplew.getPacket();
    }

    public static MaplePacket getTradePartnerAdd(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("04 01"));
        addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        return mplew.getPacket();
    }

    public static MaplePacket getTradeInvite(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("02 03"));
        mplew.writeMapleAsciiString(c.getName());
        mplew.write(HexTool.getByteArrayFromHexString("B7 50 00 00"));
        return mplew.getPacket();
    }

    public static MaplePacket getTradeMesoSet(byte number, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0xF);
        mplew.write(number);
        mplew.writeInt(meso);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeItemAdd(byte number, IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0xE);
        mplew.write(number);
        addItemInfo(mplew, item);
        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopItemUpdate(MaplePlayerShop shop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x16);
        mplew.write(shop.getItems().size());
        for (MaplePlayerShopItem item : shop.getItems()) {
            mplew.writeShort(item.getBundles());
            mplew.writeShort(item.getItem().getQuantity());
            mplew.writeInt(item.getPrice());
            addItemInfo(mplew, item.getItem(), true, true);
        }
        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param shop
     * @param owner
     * @return
     */
    public static MaplePacket getPlayerShop(MapleClient c, MaplePlayerShop shop, boolean owner) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("05 04 04"));
        mplew.write(owner ? 0 : 1);
        mplew.write(0);
        addCharLook(mplew, shop.getOwner(), false);
        mplew.writeMapleAsciiString(shop.getOwner().getName());
        MapleCharacter[] visitors = shop.getVisitors();
        for (int i = 0; i
                < visitors.length; i++) {
            if (visitors[i] != null) {
                mplew.write(i + 1);
                addCharLook(mplew, visitors[i], false);
                mplew.writeMapleAsciiString(visitors[i].getName());
            }
        }
        mplew.write(0xFF);
        mplew.writeMapleAsciiString(shop.getDescription());
        List<MaplePlayerShopItem> items = shop.getItems();
        mplew.write(0x10);
        mplew.write(items.size());
        for (MaplePlayerShopItem item : items) {
            mplew.writeShort(item.getBundles());
            mplew.writeShort(item.getItem().getQuantity());
            mplew.writeInt(item.getPrice());
            addItemInfo(mplew, item.getItem(), true, true);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getTradeStart(MapleClient c, MapleTrade trade, byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("05 03 02"));
        mplew.write(number);
        if (number == 1) {
            mplew.write(0);
            addCharLook(mplew, trade.getPartner().getChr(), false);
            mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
        }
        mplew.write(number);
        /*if (number == 1) {
        mplew.write(0);
        mplew.writeInt(c.getPlayer().getId());
        }*/
        addCharLook(mplew, c.getPlayer(), false);
        mplew.writeMapleAsciiString(c.getPlayer().getName());
        mplew.write(0xFF);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeConfirmation() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x10);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeCompletion(byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0xA);
        mplew.write(number);
        mplew.write(6);
        return mplew.getPacket();
    }

    public static MaplePacket getTradeCancel(byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0xA);
        mplew.write(number);
        mplew.write(2);
        return mplew.getPacket();
    }

    public static MaplePacket updateCharBox(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        if (c.getPlayerShop() != null) {
            addAnnounceBox(mplew, c.getPlayerShop());
        } else {
            mplew.write(0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getNPCTalk(int npc, byte msgType, String talk, String endBytes) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4); // ?
        mplew.writeInt(npc);
        mplew.write(msgType);
        mplew.writeMapleAsciiString(talk);
        mplew.write(HexTool.getByteArrayFromHexString(endBytes));
        return mplew.getPacket();
    }

    public static MaplePacket getNPCTalkStyle(int npc, String talk, int styles[]) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4); // ?
        mplew.writeInt(npc);
        mplew.write(7);
        mplew.writeMapleAsciiString(talk);
        mplew.write(styles.length);
        for (int i = 0; i                < styles.length; i++) {
            mplew.writeInt(styles[i]);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getNPCTalkNum(int npc, String talk, int def, int min, int max) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4); // ?
        mplew.writeInt(npc);
        mplew.write(3);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(def);
        mplew.writeInt(min);
        mplew.writeInt(max);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket getNPCTalkText(int npc, String talk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4); // ?
        mplew.writeInt(npc);
        mplew.write(2);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket showLevelup(int cid) {
        return showForeignEffect(cid, 0);
    }

    public static MaplePacket showJobChange(int cid) {
        return showForeignEffect(cid, 8);
    }

    public static MaplePacket showForeignEffect(int cid, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid); // ?
        mplew.write(effect);
        return mplew.getPacket();
    }

    public static MaplePacket showBuffeffect(int cid, int skillid, int effectid) {
        return showBuffeffect(cid, skillid, effectid, (byte) 3, false);
    }

    public static MaplePacket showBuffeffect(int cid, int skillid, int effectid, byte direction, boolean morph) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        if (morph) {
            mplew.write(1);
            mplew.writeInt(skillid);
            mplew.write(direction);
        }
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(1); // probably buff level but we don't know it and it
        // doesn't really matter
        if (direction != (byte) 3) {
            mplew.write(direction);
        }

        return mplew.getPacket();
    }

    public static MaplePacket showOwnBuffEffect(int skillid, int effectid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(1); // probably buff level but we don't know it and it
        // doesn't really matter
        return mplew.getPacket();
    }

    public static MaplePacket updateSkill(int skillid, int level, int masterlevel) {
        // 1E 00 01 01 00 E9 03 00 00 01 00 00 00 00 00 00 00 01
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_SKILLS.getValue());
        mplew.write(1);
        mplew.writeShort(1);
        mplew.writeInt(skillid);
        mplew.writeInt(level);
        mplew.writeInt(masterlevel);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket updateQuestMobKills(MapleQuestStatus status) {
        // 21 00 01 FB 03 01 03 00 30 30 31
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(status.getQuest().getId());
        mplew.write(1);
        String killStr = "";
        for (int kills : status.getMobKills().values()) {
            killStr += StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3);
        }
        mplew.writeMapleAsciiString(killStr);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket getShowQuestCompletion(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_QUEST_COMPLETION.getValue());
        mplew.writeShort(id);
        return mplew.getPacket();
    }

    public static MaplePacket getKeymap(Map<Integer, MapleKeyBinding> keybindings) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.KEYMAP.getValue());
        mplew.write(0);
        for (int x = 0; x < 90; x++) {
            MapleKeyBinding binding = keybindings.get(Integer.valueOf(x));
            if (binding != null) {
                mplew.write(binding.getType());
                mplew.writeInt(binding.getAction());
            } else {
                mplew.write(0);
                mplew.writeInt(0);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket getWhisper(String sender, int channel, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(0x12);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1); // I guess this is the channel
        mplew.writeMapleAsciiString(text);
        return mplew.getPacket();
    }

    /**
     *
     * @param target name of the target character
     * @param reply error code: 0x0 = cannot find char, 0x1 = success
     * @return the MaplePacket
     */
    public static MaplePacket getWhisperReply(String target, byte reply) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(0x0A); // whisper?

        mplew.writeMapleAsciiString(target);
        mplew.write(reply);
        // System.out.println(HexTool.toString(mplew.getPacket().getBytes()));
        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithMap(String target, int mapid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(1);
        mplew.writeInt(mapid);
        // ?? official doesn't send zeros here but whatever
        mplew.write(new byte[8]);
        return mplew.getPacket();
    }

    public static MaplePacket getFindReply(String target, int channel) {
        // Received UNKNOWN (1205941596.79689): (25)
        // 54 00 09 07 00 64 61 76 74 73 61 69 01 86 7F 3D 36 D5 02 00 00 22 00
        // 00 00
        // T....davtsai..=6...."...
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);
        return mplew.getPacket();
    }

    public static MaplePacket getInventoryFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(1);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket getShowInventoryFull() {
        return getShowInventoryStatus(0xff);
    }

    public static MaplePacket showItemUnavailable() {
        return getShowInventoryStatus(0xfe);
    }

    public static MaplePacket getShowInventoryStatus(int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(0);
        mplew.write(mode);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket getStorage(int npcId, byte slots, Collection<IItem> items, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(0x16);
        mplew.writeInt(npcId);
        mplew.write(slots);
        mplew.writeShort(0x7E);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(meso);
        mplew.writeShort(0);
        mplew.write((byte) items.size());
        for (IItem item : items) {
            addItemInfo(mplew, item, true, true);
        }
        mplew.writeShort(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket getStorageFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(0x11);
        return mplew.getPacket();
    }

    public static MaplePacket mesoStorage(byte slots, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(0x13);
        mplew.write(slots);
        mplew.writeShort(2);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(meso);
        return mplew.getPacket();
    }

    public static MaplePacket storeStorage(byte slots, MapleInventoryType type, Collection<IItem> items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(0xD);
        mplew.write(slots);
        mplew.writeShort(type.getBitfieldEncoding());
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.write(items.size());
        for (IItem item : items) {
            addItemInfo(mplew, item, true, true);
        }
        return mplew.getPacket();
    }

    public static MaplePacket takeOutStorage(byte slots, MapleInventoryType type, Collection<IItem> items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(0x9);
        mplew.write(slots);
        mplew.writeShort(type.getBitfieldEncoding());
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.write(items.size());
        for (IItem item : items) {
            addItemInfo(mplew, item, true, true);
        }
        return mplew.getPacket();
    }

    /**
     *
     * @param oid
     * @param remhp in %
     * @return
     */
    public static MaplePacket showMonsterHP(int oid, int remhppercentage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_MONSTER_HP.getValue());
        mplew.writeInt(oid);
        mplew.write(remhppercentage);
        return mplew.getPacket();
    }

    public static MaplePacket showBossHP(int oid, int currHP, int maxHP, byte tagColor, byte tagBgColor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        //53 00 05 21 B3 81 00 46 F2 5E 01 C0 F3 5E 01 04 01
        //00 81 B3 21 = 8500001 = Pap monster ID
        //01 5E F3 C0 = 23,000,000 = Pap max HP
        //04, 01 - boss bar color/background color as provided in WZ

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(oid);
        mplew.writeInt(currHP);
        mplew.writeInt(maxHP);
        mplew.write(tagColor);
        mplew.write(tagBgColor);
        return mplew.getPacket();
    }

    public static MaplePacket giveFameResponse(int mode, String charname, int newfame) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(charname);
        mplew.write(mode);
        mplew.writeShort(newfame);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    /**
     * status can be: <br>
     * 0: ok, use giveFameResponse<br>
     * 1: the username is incorrectly entered<br>
     * 2: users under level 15 are unable to toggle with fame.<br>
     * 3: can't raise or drop fame anymore today.<br>
     * 4: can't raise or drop fame for this character for this month anymore.<br>
     * 5: received fame, use receiveFame()<br>
     * 6: level of fame neither has been raised nor dropped due to an unexpected
     * error
     *
     * @param status
     * @param mode
     * @param charname
     * @param newfame
     * @return
     */
    public static MaplePacket giveFameErrorResponse(int status) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(status);
        return mplew.getPacket();
    }

    public static MaplePacket receiveFame(int mode, String charnameFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(5);
        mplew.writeMapleAsciiString(charnameFrom);
        mplew.write(mode);
        return mplew.getPacket();
    }

    public static MaplePacket partyCreated() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(8);
        mplew.writeShort(0x8b);
        mplew.writeShort(2);
        mplew.write(CHAR_INFO_MAGIC);
        mplew.write(CHAR_INFO_MAGIC);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket partyInvite(MapleCharacter from) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(4);
        mplew.writeInt(from.getParty().getId());
        mplew.writeMapleAsciiString(from.getName());
        mplew.write(0);
        return mplew.getPacket();
    }

    /**
     * 10: a beginner can't create a party<br>
     * 11/14/19: your request for a party didn't work due to an unexpected error<br>
     * 13: you have yet to join a party<br>
     * 16: already have joined a party<br>
     * 17: the party you are trying to join is already at full capacity<br>
     * 18: unable to find the requested character in this channel<br>
     *
     * @param message
     * @return
     */
    public static MaplePacket partyStatusMessage(int message) {
        // 32 00 08 DA 14 00 00 FF C9 9A 3B FF C9 9A 3B 22 03 6E 67
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(message);
        return mplew.getPacket();
    }

    /**
     * 22: has denied the invitation<br>
     *
     * @param message
     * @param charname
     * @return
     */
    public static MaplePacket partyStatusMessage(int message, String charname) {
        // 32 00 08 DA 14 00 00 FF C9 9A 3B FF C9 9A 3B 22 03 6E 67
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(message);
        mplew.writeMapleAsciiString(charname);
        return mplew.getPacket();
    }

    private static void addPartyStatus(int forchannel, MapleParty party, LittleEndianWriter lew, boolean leaving) {
        List<MaplePartyCharacter> partymembers = new ArrayList<MaplePartyCharacter>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeAsciiString(StringUtil.getRightPaddedStr(partychar.getName(), '\0', 13));
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getJobId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getLevel());
       }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.isOnline()) {
                lew.writeInt(partychar.getChannel() - 1);
            } else {
                lew.writeInt(-2);
            }
        }
        lew.writeInt(party.getLeader().getId());
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel) {
                lew.writeInt(partychar.getMapid());
            } else {
                lew.writeInt(0);
            }
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel && !leaving) {
                lew.writeInt(partychar.getDoorTown());
                lew.writeInt(partychar.getDoorTarget());
                lew.writeInt(partychar.getDoorPosition().x);
                lew.writeInt(partychar.getDoorPosition().y);
            } else {
                lew.writeInt(0);
                lew.writeInt(0);
                lew.writeInt(0);
                lew.writeInt(0);
            }
        }
    }

    public static MaplePacket updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                mplew.write(0xC);
                mplew.writeInt(40546);
                mplew.writeInt(target.getId());
                if (op == PartyOperation.DISBAND) {
                    mplew.write(0);
                    mplew.writeInt(party.getId());
                } else {
                    mplew.write(1);
                    if (op == PartyOperation.EXPEL) {
                        mplew.write(1);
                    } else {
                        mplew.write(0);
                    }
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(                            forChannel, party, mplew, false);
                    // addLeavePartyTail(mplew);
                }
                break;
            case JOIN:
                mplew.write(0xF);
                mplew.writeInt(40546);
                mplew.writeMapleAsciiString(target.getName());
                addPartyStatus(                        forChannel, party, mplew, false);
                // addJoinPartyTail(mplew);
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                mplew.write(0x7);
                mplew.writeInt(party.getId());
                addPartyStatus(                        forChannel, party, mplew, false);
                break;
        }
        return mplew.getPacket();
    }

    public static MaplePacket partyPortal(int townId, int targetId, Point position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.writeShort(0x23);
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        mplew.writeShort(position.x);
        mplew.writeShort(position.y);
        return mplew.getPacket();
    }

    public static MaplePacket updatePartyMemberHP(int cid, int curhp, int maxhp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_PARTYMEMBER_HP.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);
        return mplew.getPacket();
    }

    /**
     * mode: 0 buddychat; 1 partychat; 2 guildchat
     *
     * @param name
     * @param chattext
     * @param mode
     * @return
     */
    public static MaplePacket multiChat(String name, String chattext, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MULTICHAT.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);
        return mplew.getPacket();
    }

    public static MaplePacket applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stats, int skill,
            boolean monsterSkill, int delay) {
        return applyMonsterStatus(oid, stats, skill, monsterSkill, delay, null);
    }

    public static MaplePacket applyMonsterStatusTest(int oid, int mask, int delay, MobSkill mobskill, int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 9B 00 67 40 6F 00 80 00 00 00 01 00 FD FE 30 00 08 00 64 00 01
        // 1D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 01 00 79 00 01 00 B4 78 00 00 00 00 84 03
        // B4 00 A8 90 03 00 00 00 04 00 01 00 8C 00 03 00 14 00 4C 04 02
        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(mask);
        mplew.writeShort(1);
        mplew.writeShort(mobskill.getSkillId());
        mplew.writeShort(mobskill.getSkillLevel());
        mplew.writeShort(0); // as this looks similar to giveBuff this
        // might actually be the buffTime but it's
        // not displayed anywhere
        mplew.writeShort(delay); // delay in ms
        mplew.write(1); // ?
        return mplew.getPacket();
    }

    public static MaplePacket applyMonsterStatusTest2(int oid, int mask, int skill, int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 9B 00 67 40 6F 00 80 00 00 00 01 00 FD FE 30 00 08 00 64 00 01
        // 1D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 01 00 79 00 01 00 B4 78 00 00 00 00 84 03
        // B4 00 A8 90 03 00 00 00 04 00 01 00 8C 00 03 00 14 00 4C 04 02
        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(mask);
        mplew.writeShort(value);
        mplew.writeInt(skill);
        mplew.writeShort(0); // as this looks similar to giveBuff this
        // might actually be the buffTime but it's
        // not displayed anywhere
        mplew.writeShort(0); // delay in ms
        mplew.write(1); // ?
        return mplew.getPacket();
    }

    public static MaplePacket applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stats, int skill,
            boolean monsterSkill, int delay, MobSkill mobskill) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 9B 00 67 40 6F 00 80 00 00 00 01 00 FD FE 30 00 08 00 64 00 01
        // 1D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 01 00 79 00 01 00 B4 78 00 00 00 00 84 03
        // B4 00 A8 90 03 00 00 00 04 00 01 00 8C 00 03 00 14 00 4C 04 02
        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        int mask = 0;
        for (MonsterStatus stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        mplew.writeInt(mask);
        for (Integer val : stats.values()) {
            mplew.writeShort(val);
            if (monsterSkill) {
                mplew.writeShort(mobskill.getSkillId());
                mplew.writeShort(mobskill.getSkillLevel());
            } else {
                mplew.writeInt(skill);
            }
            mplew.writeShort(0); // as this looks similar to giveBuff this
            // might actually be the buffTime but it's
            // not displayed anywhere
        }
        mplew.writeShort(delay); // delay in ms
        mplew.write(1); // ?
        return mplew.getPacket();
    }

    public static MaplePacket cancelMonsterStatus(int oid, Map<MonsterStatus, Integer> stats) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        int mask = 0;
        for (MonsterStatus stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        mplew.writeInt(mask);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket getClock(int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(2);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket getClockTime(int hour, int min, int sec) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(1);
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);
        return mplew.getPacket();
    }

    public static MaplePacket spawnMist(int oid, int ownerCid, int skill, int level, MapleMist mist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_MIST.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(mist.isMobMist() ? 0 : mist.isPoisonMist() ? 1 : 2);
        mplew.writeInt(ownerCid);
        mplew.writeInt(skill);
        mplew.write(level);
        mplew.writeShort(mist.getSkillDelay());
        mplew.writeInt(mist.getBox().x);
        mplew.writeInt(mist.getBox().y);
        mplew.writeInt(mist.getBox().x + mist.getBox().width);
        mplew.writeInt(mist.getBox().y + mist.getBox().height);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket removeMist(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REMOVE_MIST.getValue());
        mplew.writeInt(oid);
        return mplew.getPacket();
    }

    public static MaplePacket damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DAMAGE_SUMMON.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(unkByte);
        mplew.writeInt(damage);
        mplew.writeInt(monsterIdFrom);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket damageMonster(int oid, int damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(damage);
        return mplew.getPacket();
    }

    public static MaplePacket healMonster(int oid, int heal) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(-heal);
        return mplew.getPacket();
    }

    public static MaplePacket updateBuddylist(Collection<BuddylistEntry> buddylist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(7);
        mplew.write(buddylist.size());
        for (BuddylistEntry buddy : buddylist) {
            if (buddy.isVisible()) {
                mplew.writeInt(buddy.getCharacterId());
                mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getName(), '\0', 13));
                mplew.write(0);
                mplew.writeInt(buddy.getChannel() - 1);
                mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getGroup(), '\0', 17));
            }
        }
        for (int x = 0; x                < buddylist.size(); x++) {
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket requestBuddylistAdd(int cidFrom, String nameFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(9);
        mplew.writeInt(cidFrom);
        mplew.writeMapleAsciiString(nameFrom);
        mplew.writeInt(cidFrom);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(nameFrom, '\0', 13));
        mplew.write(1);
        mplew.write(31);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr("Default Group", '\0', 17));
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket updateBuddyChannel(int characterid, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        // 2B 00 14 30 C0 23 00 00 11 00 00 00
        mplew.write(0x14);
        mplew.writeInt(characterid);
        mplew.write(0);
        mplew.writeInt(channel);
        // 2B 00 14 30 C0 23 00 00 0D 00 00 00
        // 2B 00 14 30 75 00 00 00 11 00 00 00
        return mplew.getPacket();
    }

    public static MaplePacket itemEffect(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_EFFECT.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static MaplePacket updateBuddyCapacity(int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(0x15);
        mplew.write(capacity);
        return mplew.getPacket();
    }

    public static MaplePacket showChair(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_CHAIR.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static MaplePacket cancelChair() {
        return cancelChair(-1);
    }

    public static MaplePacket cancelChair(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CANCEL_CHAIR.getValue());
        if (id == -1) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeShort(id);
        }
        return mplew.getPacket();
    }

    // is there a way to spawn reactors non-animated?
    public static MaplePacket spawnReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        Point pos = reactor.getPosition();
        mplew.writeShort(SendPacketOpcode.REACTOR_SPAWN.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.writeInt(reactor.getId());
        mplew.write(reactor.getState());
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket triggerReactor(MapleReactor reactor, int stance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        Point pos = reactor.getPosition();
        mplew.writeShort(SendPacketOpcode.REACTOR_HIT.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y);
        mplew.writeShort(stance);
        mplew.write(0);
        //frame delay, set to 5 since there doesn't appear to be a fixed formula for it
        mplew.write(5);
        return mplew.getPacket();
    }

    public static MaplePacket destroyReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        Point pos = reactor.getPosition();
        mplew.writeShort(SendPacketOpcode.REACTOR_DESTROY.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y);
        return mplew.getPacket();
    }

    public static MaplePacket musicChange(String song) {
        return environmentChange(song, 6);
    }

    public static MaplePacket showEffect(String effect) {
        return environmentChange(effect, 3);
    }

    public static MaplePacket playSound(String sound) {
        return environmentChange(sound, 4);
    }

    public static MaplePacket environmentChange(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(env);
        return mplew.getPacket();
    }

    public static MaplePacket startMapEffect(String msg, int itemid, boolean active) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MAP_EFFECT.getValue());
        mplew.write(active ? 0 : 1);
        mplew.writeInt(itemid);
        if (active) {
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket removeMapEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MAP_EFFECT.getValue());
        mplew.write(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket showGuildInfo(MapleCharacter c) {
        //whatever functions calling this better make sure
        //that the character actually HAS a guild
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x1A); //signature for showing guild info
        if (c == null) { //show empty guild (used for leaving, expelled)
            mplew.write(0);
            return mplew.getPacket();
        }
        MapleGuild g = c.getClient().getChannelServer().getGuild(c.getMGC());
        if (g == null) { //failed to read from DB - don't show a guild
            mplew.write(0);
            log.warn(MapleClient.getLogMessage(c, "Couldn't load a guild"));
            return mplew.getPacket();
        }
        mplew.write(1); //bInGuild
        mplew.writeInt(g.getId());
        mplew.writeMapleAsciiString(g.getName());
        for (int i = 1; i                <= 5; i++) {
            mplew.writeMapleAsciiString(g.getRankTitle(i));
        }
        Collection<MapleGuildCharacter> members = g.getMembers();
        mplew.write(members.size());
        //then it is the size of all the members
        for (MapleGuildCharacter mgc : members) //and each of their character ids o_O
        {
            mplew.writeInt(mgc.getId());
        }
        for (MapleGuildCharacter mgc : members) {
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
            mplew.writeInt(mgc.getJobId());
            mplew.writeInt(mgc.getReborns());
            mplew.writeInt(mgc.getGuildRank());
            mplew.writeInt(mgc.isOnline() ? 1 : 0);
            mplew.writeInt(g.getSignature());
            mplew.writeInt(mgc.getAllianceRank());
        }
        mplew.writeInt(g.getCapacity());
        mplew.writeShort(g.getLogoBG());
        mplew.write(g.getLogoBGColor());
        mplew.writeShort(g.getLogo());
        mplew.write(g.getLogoColor());
        mplew.writeMapleAsciiString(g.getNotice());
        mplew.writeInt(g.getGP());
        mplew.writeInt(g.getAllianceId());
        return mplew.getPacket();
    }

    public static MaplePacket guildMemberOnline(int gid, int cid, boolean bOnline) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x3d);
        mplew.writeInt(gid);
        mplew.writeInt(cid);
        mplew.write(bOnline ? 1 : 0);        return mplew.getPacket();
    }

    public static MaplePacket guildInvite(int gid, String charName) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x05);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(charName);
        return mplew.getPacket();
    }

    public static MaplePacket genericGuildMessage(byte code) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(code);
        return mplew.getPacket();
    }

    public static MaplePacket newGuildMember(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x27);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
        mplew.writeInt(mgc.getJobId());
        mplew.writeInt(mgc.getReborns());
        mplew.writeInt(mgc.getGuildRank()); //should be always 5 but whatevs
        mplew.writeInt(mgc.isOnline() ? 1 : 0); //should always be 1 too
        mplew.writeInt(1); //? could be guild signature, but doesn't seem to matter
        mplew.writeInt(3);
        return mplew.getPacket();
    }

    //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    public static MaplePacket memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(bExpelled ? 0x2f : 0x2c);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeMapleAsciiString(mgc.getName());
        return mplew.getPacket();
    }
    //rank change
    public static MaplePacket changeRank(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x40);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.write(mgc.getGuildRank());
        return mplew.getPacket();
    }

    public static MaplePacket guildNotice(int gid, String notice) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x44);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(notice);
        return mplew.getPacket();
    }

    public static MaplePacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x3C);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getReborns());
        mplew.writeInt(mgc.getJobId());
        return mplew.getPacket();
    }

    public static MaplePacket rankTitleChange(int gid, String[] ranks) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x3e);
        mplew.writeInt(gid);
        for (int i = 0; i                < 5; i++) {
            mplew.writeMapleAsciiString(ranks[i]);
        }
        return mplew.getPacket();
    }

    public static MaplePacket guildDisband(int gid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x32);
        mplew.writeInt(gid);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x42);
        mplew.writeInt(gid);
        mplew.writeShort(bg);
        mplew.write(bgcolor);
        mplew.writeShort(logo);
        mplew.write(logocolor);
        return mplew.getPacket();
    }

    public static MaplePacket guildCapacityChange(int gid, int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x3a);
        mplew.writeInt(gid);
        mplew.write(capacity);
        return mplew.getPacket();
    }

    public static void addThread(MaplePacketLittleEndianWriter mplew, ResultSet rs) throws SQLException {
        mplew.writeInt(rs.getInt("localthreadid"));
        mplew.writeInt(rs.getInt("postercid"));
        mplew.writeMapleAsciiString(rs.getString("name"));
        mplew.writeLong(MaplePacketCreator.getKoreanTimestamp(rs.getLong("timestamp")));
        mplew.writeInt(rs.getInt("icon"));
        mplew.writeInt(rs.getInt("replycount"));
    }

    public static MaplePacket BBSThreadList(ResultSet rs, int start) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
        mplew.write(0x06);
        if (!rs.last()) //no result at all
        {
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        int threadCount = rs.getRow();
        if (rs.getInt("localthreadid") == 0) //has a notice
        {
            mplew.write(1);
            addThread(                    mplew, rs);
            threadCount--; //one thread didn't count (because it's a notice)

        } else {
            mplew.write(0);
        }
        if (!rs.absolute(start + 1)) //seek to the thread before where we start
        {
            rs.first(); //uh, we're trying to start at a place past possible
            start = 0;
            // System.out.println("Attempting to start past threadCount");
        }
        mplew.writeInt(threadCount);
        mplew.writeInt(Math.min(10, threadCount - start));
        for (int i = 0; i          < Math.min(10, threadCount - start); i++) {
            addThread(mplew, rs);
            rs.next();
        }
        return mplew.getPacket();
    }

    public static MaplePacket showThread(int localthreadid, ResultSet threadRS, ResultSet repliesRS)
            throws SQLException, RuntimeException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
        mplew.write(0x07);
        mplew.writeInt(localthreadid);
        mplew.writeInt(threadRS.getInt("postercid"));
        mplew.writeLong(getKoreanTimestamp(threadRS.getLong("timestamp")));
        mplew.writeMapleAsciiString(threadRS.getString("name"));
        mplew.writeMapleAsciiString(threadRS.getString("startpost"));
        mplew.writeInt(threadRS.getInt("icon"));
        if (repliesRS != null) {
            int replyCount = threadRS.getInt("replycount");
            mplew.writeInt(replyCount);
            int i;
            for (i = 0; i
                    < replyCount && repliesRS.next(); i++) {
                mplew.writeInt(repliesRS.getInt("replyid"));
                mplew.writeInt(repliesRS.getInt("postercid"));
                mplew.writeLong(getKoreanTimestamp(repliesRS.getLong("timestamp")));
                mplew.writeMapleAsciiString(repliesRS.getString("content"));
            }
            if (i != replyCount || repliesRS.next()) {
                //in the unlikely event that we lost count of replyid
                throw new RuntimeException(String.valueOf(threadRS.getInt("threadid")));
                //we need to fix the database and stop the packet sending
                //or else it'll probably error 38 whoever tries to read it
                //there is ONE case not checked, and that's when the thread
                //has a replycount of 0 and there is one or more replies to the
                //thread in bbs_replies
            }
        } else {
            mplew.writeInt(0); //0 replies
        }
        return mplew.getPacket();
    }

    public static MaplePacket showGuildRanks(int npcid, ResultSet rs) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x49);
        mplew.writeInt(npcid);
        if (!rs.last()) //no guilds o.o
        {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow());		//number of entries
        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("GP"));
            mplew.writeInt(rs.getInt("logo"));
            mplew.writeInt(rs.getInt("logoColor"));
            mplew.writeInt(rs.getInt("logoBG"));
            mplew.writeInt(rs.getInt("logoBGColor"));
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateGP(int gid, int GP) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x48);
        mplew.writeInt(gid);
        mplew.writeInt(GP);
        return mplew.getPacket();
    }

    public static MaplePacket skillEffect(MapleCharacter from, int skillId, byte flags) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(0x01); // unknown at this point
        mplew.write(flags);
        mplew.write(0x04); // unknown at this point
        return mplew.getPacket();
    }

    public static MaplePacket skillCancel(MapleCharacter from, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CANCEL_SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        return mplew.getPacket();
    }

    public static MaplePacket showMagnet(int mobid, byte success) {  // Monster Magnet
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_MAGNET.getValue());
        mplew.writeInt(mobid);
        mplew.write(success);
        return mplew.getPacket();
    }

    /**
     * Sends a Player Hint, something that pops up above your character!
     *
     * @return The packet.
     */
    public static MaplePacket sendHint(String hint) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.PLAYER_HINT.getValue());
        mplew.writeMapleAsciiString(hint);
        mplew.write(HexTool.getByteArrayFromHexString("FA 00 05 00 01"));
        return mplew.getPacket();
    }

    /**
     * Sends a player hint.
     *
     * @param hint The hint it's going to send.
     * @param width How tall the box is going to be.
     * @param height How long the box is going to be.
     * @return The player hint packet.
     */
    public static MaplePacket sendHint(String hint, int width, int height) {
        if (width < 1) {
            width = hint.length() * 10;
            if (width < 40) {
                width = 40;
            }
        }
        if (height < 5) {
            height = 5;
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_HINT.getValue());
        mplew.writeMapleAsciiString(hint);
        mplew.writeShort(width);
        mplew.writeShort(height);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket messengerInvite(String from, int messengerid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0x03);
        mplew.writeMapleAsciiString(from);
        mplew.write(0x00);
        mplew.writeInt(messengerid);
        mplew.write(0x00);
        return mplew.getPacket();
    }

    public static MaplePacket addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0x00);
        mplew.write(position);
        addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(0x00);
        return mplew.getPacket();
    }

    public static MaplePacket removeMessengerPlayer(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0x02);
        mplew.write(position);
        return mplew.getPacket();
    }

    public static MaplePacket updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0x07);
        mplew.write(position);
        addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(0x00);
        return mplew.getPacket();
    }

    public static MaplePacket joinMessenger(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0x01);
        mplew.write(position);
        return mplew.getPacket();
    }

    public static MaplePacket messengerChat(String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0x06);
        mplew.writeMapleAsciiString(text);
        return mplew.getPacket();
    }

    public static MaplePacket messengerNote(String text, int mode, int mode2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(text);
        mplew.write(mode2);
        return mplew.getPacket();
    }

    public static MaplePacket warpCS(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        MapleCharacter chr = c.getPlayer();
        mplew.writeShort(SendPacketOpcode.CS_OPEN.getValue());
        mplew.writeLong(-1);
        addCharStats( mplew, chr);
        mplew.write(20); // ???
        mplew.writeInt(chr.getMeso()); // mesos
        mplew.write(96); // equip slots
        mplew.write(96); // use slots
        mplew.write(96); // set-up slots
        mplew.write(96); // etc slots
        mplew.write(96); // storage slots
        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        Collection<IItem> equippedC = iv.list();
        List<Item> equipped = new ArrayList<Item>(equippedC.size());
        for (IItem item : equippedC) {
            equipped.add((Item) item);
        }
        Collections.sort(equipped);
        for (Item item : equipped) {
            addItemInfo(mplew, item);
        }
        mplew.writeShort(0); // start of equip inventory
        iv = chr.getInventory(MapleInventoryType.EQUIP);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0); // start of use inventory
        iv = chr.getInventory(MapleInventoryType.USE);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0); // start of set-up inventory
        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }
        mplew.write(0); // start of etc inventory
        iv = chr.getInventory(MapleInventoryType.ETC);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
       }
        mplew.write(0); // start of cash inventory
        iv = chr.getInventory(MapleInventoryType.CASH);
        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);
        }        mplew.write(0);
        Map<ISkill, MapleCharacter.SkillEntry> skills = chr.getSkills();
        mplew.writeShort(skills.size());
        for (Entry<ISkill, MapleCharacter.SkillEntry> skill : skills.entrySet()) {
            mplew.writeInt(skill.getKey().getId());
            mplew.writeInt(skill.getValue().skillevel);
            if (skill.getKey().isFourthJob()) {
                mplew.writeInt(skill.getValue().masterlevel);
            }
        }
        mplew.writeShort(0);
        mplew.writeShort(1);
        mplew.writeInt(662990);
        mplew.write(HexTool.getByteArrayFromHexString("5A 42 43 44 45 46 47 48 49 4A 00 00"));
        //mplew.writeLong(0);
        //mplew.writeLong(0);
        mplew.writeLong(0);
        for (int i = 0; i                < 15; i++) {
            mplew.write(CHAR_INFO_MAGIC);
        }        mplew.writeInt(0);
        mplew.write(1);
        mplew.writeMapleAsciiString(chr.getClient().getAccountName());
        mplew.writeInt(40);//Event Items
        mplew.write(HexTool.getByteArrayFromHexString("F2 20 9A 00 F3 20 9A 00 F4 20 9A 00 F5 20 9A 00 F6 20 9A 00 F7 20 9A 00 F8 20 9A 00 F9 20 9A 00 FA 20 9A 00 FB 20 9A 00 FC 20 9A 00 FD 20 9A 00 FE 20 9A 00 FF 20 9A 00 00 21 9A 00 01 21 9A 00 02 21 9A 00 03 21 9A 00 04 21 9A 00 05 21 9A 00 06 21 9A 00 07 21 9A 00 08 21 9A 00 09 21 9A 00 0A 21 9A 00 0B 21 9A 00 0C 21 9A 00 0D 21 9A 00 0E 21 9A 00 0F 21 9A 00 10 21 9A 00 11 21 9A 00 12 21 9A 00 13 21 9A 00 14 21 9A 00 15 21 9A 00 16 21 9A 00 17 21 9A 00 18 21 9A 00 19 21 9A 00"));
        mplew.writeShort(0xD8);//New Items
        mplew.write(HexTool.getByteArrayFromHexString("46 9D 98 00 08 02 07 00 47 9D 98 00 08 02 07 00 48 9D 98 00 08 02 07 00 49 9D 98 00 08 02 07 00 4A 9D 98 00 08 02 07 00 4B 9D 98 00 00 02 00 4C 9D 98 00 00 02 00 4D 9D 98 00 00 02 00 4E 9D 98 00 00 02 00 4F 9D 98 00 00 02 00 50 9D 98 00 00 02 00 51 9D 98 00 00 02 00 52 9D 98 00 00 02 00 53 9D 98 00 00 02 00 54 9D 98 00 00 02 00 55 9D 98 00 00 02 00 56 9D 98 00 00 02 00 57 9D 98 00 00 02 00 58 9D 98 00 00 02 00 59 9D 98 00 00 02 00 5A 9D 98 00 00 02 00 5B 9D 98 00 00 02 00 5C 9D 98 00 00 02 00 5D 9D 98 00 00 02 00 5E 9D 98 00 00 02 00 5F 9D 98 00 00 02 00 60 9D 98 00 00 02 00 61 9D 98 00 00 02 00 62 9D 98 00 00 02 00 63 9D 98 00 00 02 00 64 9D 98 00 00 02 00 65 9D 98 00 00 02 00 66 9D 98 00 00 02 00 67 9D 98 00 00 02 00 68 9D 98 00 00 02 00 69 9D 98 00 00 02 00 6A 9D 98 00 00 02 00 6B 9D 98 00 00 02 00 6C 9D 98 00 00 02 00 6D 9D 98 00 00 02 00 6E 9D 98 00 00 02 00 7C 9D 98 00 00 02 00 7D 9D 98 00 00 02 00 7E 9D 98 00 00 02 00 7F 9D 98 00 00 02 00 80 9D 98 00 00 02 00 81 9D 98 00 00 02 00 82 9D 98 00 00 02 00 83 9D 98 00 00 02 00 84 9D 98 00 00 02 00 85 9D 98 00 00 02 00 86 9D 98 00 00 02 00 87 9D 98 00 00 02 00 88 9D 98 00 00 02 00 89 9D 98 00 00 02 00 8A 9D 98 00 00 02 00 F2 20 9A 00 00 02 01 F3 20 9A 00 00 02 01 F4 20 9A 00 00 02 01 F5 20 9A 00 00 02 01 F6 20 9A 00 00 02 01 F7 20 9A 00 00 02 01 F8 20 9A 00 00 02 01 F9 20 9A 00 00 02 01 FA 20 9A 00 00 02 01 FB 20 9A 00 00 02 01 FC 20 9A 00 00 02 01 FD 20 9A 00 00 02 01 FE 20 9A 00 00 02 01 FF 20 9A 00 00 02 01 00 21 9A 00 00 02 01 01 21 9A 00 00 02 01 02 21 9A 00 00 02 01 03 21 9A 00 00 02 01 04 21 9A 00 00 02 01 05 21 9A 00 00 02 01 06 21 9A 00 00 02 01 07 21 9A 00 00 02 01 08 21 9A 00 00 02 01 09 21 9A 00 00 02 01 0A 21 9A 00 00 02 01 0B 21 9A 00 00 02 01 0C 21 9A 00 00 02 01 0D 21 9A 00 00 02 01 0E 21 9A 00 00 02 01 0F 21 9A 00 00 02 01 10 21 9A 00 00 02 01 11 21 9A 00 00 02 01 12 21 9A 00 00 02 01 13 21 9A 00 00 02 01 14 21 9A 00 00 02 01 15 21 9A 00 00 02 01 16 21 9A 00 00 02 01 17 21 9A 00 00 02 01 18 21 9A 00 00 02 01 19 21 9A 00 00 02 01 1A 21 9A 00 00 02 00 1B 21 9A 00 00 02 00 1C 21 9A 00 00 02 00 1D 21 9A 00 00 02 00 1E 21 9A 00 00 02 00 1F 21 9A 00 00 02 00 20 21 9A 00 00 02 00 21 21 9A 00 00 02 00 22 21 9A 00 00 02 00 23 21 9A 00 00 02 00 24 21 9A 00 00 02 00 15 2D 31 01 08 00 09 2B 2D 31 01 08 00 09 4C 2D 31 01 08 00 09 EC 2D 31 01 08 00 09 D3 2E 31 01 08 00 07 D4 2E 31 01 08 00 07 D5 2E 31 01 08 00 07 D6 2E 31 01 08 04 08 02 D7 2E 31 01 08 04 07 FF D8 2E 31 01 08 04 07 FF D9 2E 31 01 08 04 07 FF DA 2E 31 01 00 02 00 DB 2E 31 01 00 02 00 DC 2E 31 01 00 02 00 A4 B3 32 01 08 00 09 B5 B3 32 01 08 00 09 CE B3 32 01 08 00 09 D1 B3 32 01 08 00 09 E4 B3 32 01 08 04 07 FF E9 B3 32 01 08 04 08 02 EC B3 32 01 08 04 07 FF ED B3 32 01 08 04 07 FF EE B3 32 01 08 04 07 FF EF B3 32 01 08 04 07 FF F0 B3 32 01 08 04 07 FF F1 B3 32 01 08 04 07 FF F2 B3 32 01 08 04 07 FF F3 B3 32 01 08 04 07 FF F4 B3 32 01 08 04 07 FF F5 B3 32 01 08 04 07 FF F6 B3 32 01 08 04 07 FF F7 B3 32 01 08 04 07 FF F8 B3 32 01 08 04 07 FF F9 B3 32 01 08 04 07 FF FA B3 32 01 08 04 07 FF FB B3 32 01 08 04 07 FF FC B3 32 01 08 04 07 FF FD B3 32 01 08 04 07 FF FE B3 32 01 08 04 07 FF FF B3 32 01 08 04 07 FF 00 B4 32 01 08 04 07 FF E9 C0 35 01 08 00 08 85 C1 35 01 08 00 08 C5 C1 35 01 08 00 08 10 C2 35 01 08 04 07 FF 12 C2 35 01 00 02 00 13 C2 35 01 00 02 00 9C 47 37 01 08 00 08 1A 48 37 01 08 00 08 2B 48 37 01 08 00 08 5E 48 37 01 08 00 08 65 48 37 01 08 04 07 FF 6A 48 37 01 00 02 00 3B CE 38 01 08 00 08 67 CE 38 01 08 00 08 9F CE 38 01 08 00 08 D4 CE 38 01 00 02 00 EE 54 3A 01 08 00 09 90 55 3A 01 00 02 00 C5 62 3D 01 08 04 07 FF D7 62 3D 01 08 04 07 FF D8 62 3D 01 08 04 07 FF D9 62 3D 01 00 06 00 FF DA 62 3D 01 00 06 00 FF DB 62 3D 01 08 04 07 FF DC 62 3D 01 08 04 07 FF DF 62 3D 01 00 02 00 E0 62 3D 01 00 02 00 E1 62 3D 01 00 02 00 E2 62 3D 01 00 02 00 E3 62 3D 01 00 02 00 BC E8 3E 01 08 00 08 DA E8 3E 01 08 00 08 DC E8 3E 01 08 00 08 DD E8 3E 01 08 00 08 E2 E8 3E 01 08 04 08 02 E4 E8 3E 01 00 02 00 E6 E8 3E 01 00 02 00 E8 E8 3E 01 00 02 00 EA E8 3E 01 00 02 00 EC E8 3E 01 00 02 00 EE E8 3E 01 00 02 00 E2 F5 41 01 08 00 08 F8 F5 41 01 08 00 08 F9 F5 41 01 08 00 08 FC F5 41 01 08 04 08 02 09 F6 41 01 08 00 08 0E F6 41 01 08 04 07 FF C5 D0 CC 01 08 04 07 FF C9 D0 CC 01 08 04 08 02 CB D0 CC 01 08 00 08 CD D0 CC 01 08 04 07 FF DE D0 CC 01 00 04 02 A0 91 02 03 08 04 07 FF A1 91 02 03 08 00 08 B8 91 02 03 08 00 08 BA 91 02 03 08 04 07 FF BB 91 02 03 08 00 08 CD 91 02 03 08 04 08 02 CE 91 02 03 08 04 08 02 CF 91 02 03 08 04 07 FF D1 91 02 03 00 02 00 D2 91 02 03 00 02 00 A0 0D 95 03 08 00 09 A4 0D 95 03 08 00 09 C9 0D 95 03 08 00 09 CA 0D 95 03 08 00 09 D3 0D 95 03 08 04 07 FF E0 0D 95 03 08 04 08 02 00 2E 00 35 00 38 00 00 00 03 00 0D 00 65 01 0E 05 04 00 00 00 34 00 39 00 00 00 00 00 31 00 35 00 07 00 10 00 66 00 0C 05 B0 01 14 00 F8 C2 51 02 67 00 69 00 6E 00 49 00 70 00 00 00 00 00 70 00 03 00 04 00 62 00 08 05 90 01 14 00 D8 27 F1 01 64 00 49 00 70 00 00 00 03 00 17 00 6F 01 0E 05 04 00 00 00 34 00 39 00 00 00 00 00 6D 00 61 00 05 00 1A 00 68 01 08 05 01 00 00 00 00 00 00 00 C3 FD FD 02 01 00 00 00 00 00 00 00 34 77 FC 02 01 00 00 00 00 00 00 00 39 4A CB 01 01 00 00 00 00 00 00 00 E0 0D 95 03 01 00 00 00 00 00 00 00 89 C3 C9 01 01 00 00 00 01 00 00 00 C3 FD FD 02 01 00 00 00 01 00 00 00 34 77 FC 02 01 00 00 00 01 00 00 00 39 4A CB 01 01 00 00 00 01 00 00 00 E0 0D 95 03 01 00 00 00 01 00 00 00 89 C3 C9 01 02 00 00 00 00 00 00 00 C3 FD FD 02 02 00 00 00 00 00 00 00 34 77 FC 02 02 00 00 00 00 00 00 00 39 4A CB 01 02 00 00 00 00 00 00 00 E0 0D 95 03 02 00 00 00 00 00 00 00 89 C3 C9 01 02 00 00 00 01 00 00 00 C3 FD FD 02 02 00 00 00 01 00 00 00 34 77 FC 02 02 00 00 00 01 00 00 00 39 4A CB 01 02 00 00 00 01 00 00 00 E0 0D 95 03 02 00 00 00 01 00 00 00 89 C3 C9 01 03 00 00 00 00 00 00 00 C3 FD FD 02 03 00 00 00 00 00 00 00 34 77 FC 02 03 00 00 00 00 00 00 00 39 4A CB 01 03 00 00 00 00 00 00 00 E0 0D 95 03 03 00 00 00 00 00 00 00 89 C3 C9 01 03 00 00 00 01 00 00 00 C3 FD FD 02 03 00 00 00 01 00 00 00 34 77 FC 02 03 00 00 00 01 00 00 00 39 4A CB 01 03 00 00 00 01 00 00 00 E0 0D 95 03 03 00 00 00 01 00 00 00 89 C3 C9 01 04 00 00 00 00 00 00 00 C3 FD FD 02 04 00 00 00 00 00 00 00 34 77 FC 02 04 00 00 00 00 00 00 00 39 4A CB 01 04 00 00 00 00 00 00 00 E0 0D 95 03 04 00 00 00 00 00 00 00 89 C3 C9 01 04 00 00 00 01 00 00 00 C3 FD FD 02 04 00 00 00 01 00 00 00 34 77 FC 02 04 00 00 00 01 00 00 00 39 4A CB 01 04 00 00 00 01 00 00 00 E0 0D 95 03 04 00 00 00 01 00 00 00 89 C3 C9 01 05 00 00 00 00 00 00 00 C3 FD FD 02 05 00 00 00 00 00 00 00 34 77 FC 02 05 00 00 00 00 00 00 00 39 4A CB 01 05 00 00 00 00 00 00 00 E0 0D 95 03 05 00 00 00 00 00 00 00 89 C3 C9 01 05 00 00 00 01 00 00 00 C3 FD FD 02 05 00 00 00 01 00 00 00 34 77 FC 02 05 00 00 00 01 00 00 00 39 4A CB 01 05 00 00 00 01 00 00 00 E0 0D 95 03 05 00 00 00 01 00 00 00 89 C3 C9 01 06 00 00 00 00 00 00 00 C3 FD FD 02 06 00 00 00 00 00 00 00 34 77 FC 02 06 00 00 00 00 00 00 00 39 4A CB 01 06 00 00 00 00 00 00 00 E0 0D 95 03 06 00 00 00 00 00 00 00 89 C3 C9 01 06 00 00 00 01 00 00 00 C3 FD FD 02 06 00 00 00 01 00 00 00 34 77 FC 02 06 00 00 00 01 00 00 00 39 4A CB 01 06 00 00 00 01 00 00 00 E0 0D 95 03 06 00 00 00 01 00 00 00 89 C3 C9 01 07 00 00 00 00 00 00 00 C3 FD FD 02 07 00 00 00 00 00 00 00 34 77 FC 02 07 00 00 00 00 00 00 00 39 4A CB 01 07 00 00 00 00 00 00 00 E0 0D 95 03 07 00 00 00 00 00 00 00 89 C3 C9 01 07 00 00 00 01 00 00 00 C3 FD FD 02 07 00 00 00 01 00 00 00 34 77 FC 02 07 00 00 00 01 00 00 00 39 4A CB 01 07 00 00 00 01 00 00 00 E0 0D 95 03 07 00 00 00 01 00 00 00 89 C3 C9 01 08 00 00 00 00 00 00 00 C3 FD FD 02 08 00 00 00 00 00 00 00 34 77 FC 02 08 00 00 00 00 00 00 00 39 4A CB 01 08 00 00 00 00 00 00 00 E0 0D 95 03 08 00 00 00 00 00 00 00 89 C3 C9 01 08 00 00 00 01 00 00 00 C3 FD FD 02 08 00 00 00 01 00 00 00 34 77 FC 02 08 00 00 00 01 00 00 00 39 4A CB 01 08 00 00 00 01 00 00 00 E0 0D 95 03 08 00 00 00 01 00 00 00 89 C3 C9 01 00 00 01 00 49 4B 4C 00 02 21 9A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 0F 00 00 00 00 00 00 00 0F 00 00 00 BE 68 32 01 BE 68 32 01 0B 00 00 00 0C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01"));
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static void toCashItem(MaplePacketLittleEndianWriter mplew, int sn, int type1, int type2) {
        // E1 9C 98 00 00 06 00 00 00 - Globe Cap
        mplew.writeInt(sn);
        mplew.write(0);
        mplew.write(type1);
        mplew.writeShort(0);
        mplew.write(type2);
    }

    public static void toCashItem(MaplePacketLittleEndianWriter mplew, int sn, int type0, int type1, int type2) {
        mplew.writeInt(sn);
        mplew.write(type0);
        mplew.write(type1);
        mplew.writeShort(0);
        mplew.write(type2);
    }

    public static MaplePacket showNXMapleTokens(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_UPDATE.getValue());
        mplew.writeInt(chr.getCSPoints(1)); // Paypal/PayByCash NX
        mplew.writeInt(chr.getCSPoints(2)); // Maple Points
        mplew.writeInt(chr.getCSPoints(4)); // Game Card NX
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSItem(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // FE 00
        // 3B 32 F0 00
        // 00 00 00 00
        // 00
        // 80 B0 41 00
        // 00 00 00 00
        // 05 20 4E 00 - item ID
        // CD D0 CC 01 01 00 00 04 F1 4B 40 00 80 00 00 00 01 00 00 E0 CE 0D 5F BB 53 C9 01 00 00 00 00 00 00 00 00

        // FE 00
        // 3B 41 4F 32
        // 00 00 00 00
        // 00
        // 80 B0 41 00
        // 00 00 00 00
        // 05 20 4E 00 - item ID
        // CD D0 CC 01 01 00 00 0A F1 4B 40 00 80 00 00 00 01 00 00 10 CB E9 4A BD 53 C9 01
        // 00 00 00 00 00 00 00 00
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.writeInt(15741499);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeInt(4305024);
        mplew.writeInt(0);
        mplew.writeInt(itemid);
        mplew.write(HexTool.getByteArrayFromHexString("CD D0 CC 01 01 00 00 0A F1 4B 40 00 80 00 00 00 01 00 00 10 CB E9 4A BD 53 C9 01"));
        mplew.writeLong(0);
        return mplew.getPacket();
    }

    public static MaplePacket showCouponRedeemedItem(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.writeShort(0x3A);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeShort(0x1A);
        mplew.writeInt(itemid);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket enableCSUse0() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(HexTool.getByteArrayFromHexString("12 00 00 00 00 00 00"));
        return mplew.getPacket();
    }

    public static MaplePacket enableCSUse1() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // FE 00 2F 00 00 04 00 03 00
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.writeShort(0x2F);
        mplew.write(0);
        mplew.writeShort(4);
        mplew.writeShort(3);
        return mplew.getPacket();
    }

    public static MaplePacket enableCSUse2() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x31);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket enableCSUse3() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x33);
        mplew.write(new byte[40]);
        return mplew.getPacket();
    }

    // Decoding : Raz (Snow) | Author : Penguins (Acrylic) | Finishing: Bassoe
    public static MaplePacket sendWishList(int characterid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x33);
        Connection con = DatabaseConnection.getConnection();
        int i = 10;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT sn FROM wishlist WHERE charid = ? LIMIT 10");
            ps.setInt(1, characterid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mplew.writeInt(rs.getInt("sn"));
                i--;

            }
            rs.close();
            ps.close();
        } catch (SQLException se) {
            log.info("Error getting wishlist data:", se);
        }
        while (i > 0) {
            mplew.writeInt(0);
            i--;
        }
        return mplew.getPacket();}
    // Decoding : Raz (Snow) | Author : Penguins (Acrylic)

    public static MaplePacket wrongCouponCode() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // FE 00 40 87
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x40);
        mplew.write(0x87);
        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithCS(String target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);
        return mplew.getPacket();
    }

    public static MaplePacket updatePet(MaplePet pet) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.write(2);
        mplew.write(3);
        mplew.write(5);
        mplew.write(pet.getPosition());
        mplew.writeShort(0);
        mplew.write(5);
        mplew.write(pet.getPosition());
        mplew.write(0);
        mplew.write(3);
        mplew.writeInt(pet.getItemId());
        mplew.write(1);
        mplew.writeInt(pet.getUniqueId());
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(ITEM_MAGIC);
        addExpirationTime(mplew, 0, false);
        String petname = pet.getName();
        if (petname.length() > 13) {
            petname = petname.substring(0, 13);
        }
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(petname, '\0', 13));
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket showPet(MapleCharacter chr, MaplePet pet, boolean remove) {
        return showPet(chr, pet, remove, false);
    }

    public static MaplePacket showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_PET.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getPetIndex(pet));
        if (remove) {
            mplew.write(0);
            mplew.write(hunger ? 1 : 0);
        } else {
            mplew.write(1);
            mplew.write(0);
            mplew.writeInt(pet.getItemId());
            mplew.writeMapleAsciiString(pet.getName());
            mplew.writeInt(pet.getUniqueId());
            mplew.writeInt(0);
            mplew.writeShort(pet.getPos().x);
            mplew.writeShort(pet.getPos().y);
            mplew.write(pet.getStance());
            mplew.writeInt(pet.getFh());
        }
        return mplew.getPacket();
    }

    public static MaplePacket movePet(int cid, int pid, int slot, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MOVE_PET.getValue());
        mplew.writeInt(cid);
        mplew.write(slot);
        mplew.writeInt(pid);
        serializeMovementList(mplew, moves);
        return mplew.getPacket();
    }

    public static MaplePacket petChat(int cid, int un, String text, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PET_CHAT.getValue());
        mplew.writeInt(cid);
        mplew.write(slot);
        mplew.writeShort(un);
        mplew.writeMapleAsciiString(text);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket commandResponse(int cid, byte command, int slot, boolean success, boolean food) {
        // 84 00 09 03 2C 00 00 00 19 00 00
        // 84 00 E6 DC 17 00 00 01 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PET_COMMAND.getValue());
        mplew.writeInt(cid);
        mplew.write(slot);
        if (!food) {
            mplew.write(0);
        }
        mplew.write(command);
        if (success) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket showOwnPetLevelUp(int index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(4);
        mplew.write(0);
        mplew.write(index); // Pet Index
        return mplew.getPacket();
    }

    public static MaplePacket showPetLevelUp(MapleCharacter chr, int index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(4);
        mplew.write(0);
        mplew.write(index);
        return mplew.getPacket();
    }

    public static MaplePacket changePetName(MapleCharacter chr, String newname, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PET_NAMECHANGE.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(slot);
        mplew.writeMapleAsciiString(newname);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket petStatUpdate(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        int mask = 0;
        mask |= MapleStat.PET.getValue();
        mplew.write(0);
        mplew.writeInt(mask);
        MaplePet[] pets = chr.getPets();
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                mplew.writeInt(pets[i].getUniqueId());
            } else {
                mplew.writeInt(0);
            }
            mplew.writeInt(0);
        }
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket skillCooldown(int sid, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.COOLDOWN.getValue());
        mplew.writeInt(sid);
        mplew.writeShort(time);
        return mplew.getPacket();
    }

    public static MaplePacket skillBookSuccess(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.USE_SKILL_BOOK.getValue());
        mplew.writeInt(chr.getId()); // character id
        mplew.write(1);
        mplew.writeInt(skillid);
        mplew.writeInt(maxlevel);
        mplew.write(canuse ? 1 : 0);
        mplew.write(success ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket getMacros(SkillMacro[] macros) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SKILL_MACRO.getValue());
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (macros[i] != null) {
                count++;
            }
        }
        mplew.write(count); // number of macros
        for (int i = 0; i < 5; i++) {
            SkillMacro macro = macros[i];
            if (macro != null) {
                mplew.writeMapleAsciiString(macro.getName());
                mplew.write(macro.getShout());
                mplew.writeInt(macro.getSkill1());
                mplew.writeInt(macro.getSkill2());
                mplew.writeInt(macro.getSkill3());
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket showNotes(ResultSet notes, int count) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
        mplew.write(2);
        mplew.write(count);
        for (int i = 0; i < count; i++) {
            mplew.writeInt(notes.getInt("id"));
            mplew.writeMapleAsciiString(notes.getString("from"));
            mplew.writeMapleAsciiString(notes.getString("message"));
            mplew.writeLong(getKoreanTimestamp(notes.getLong("timestamp")));
            mplew.write(0);
            notes.next();
        }
        return mplew.getPacket();
    }

    public static void sendUnkwnNote(String to, String msg, String from) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO notes (`to`, `from`, `message`, `timestamp`) VALUES (?, ?, ?, ?)");
        ps.setString(1, to);
        ps.setString(2, from);
        ps.setString(3, msg);
        ps.setLong(4, System.currentTimeMillis());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Gets a gm effect packet (ie. hide, banned, etc.)
     *
     * Possible values for <code>type</code>:<br>
     * 4: You have successfully blocked access.<br>
     * 5: The unblocking has been successful.<br>
     * 6 with Mode 0: You have successfully removed the name from the ranks.<br>
     * 6 with Mode 1: You have entered an invalid character name.<br>
     * 16: GM Hide, mode determines whether or not it is on.<br>
     * 26: Enables minimap<br>
     * 27: Disables minimap<br>
     * 29 with Mode 0: Unable to send the message. Please enter the user's name before warning.<br>
     * 29 with Mode 1: Your warning has been successfully sent.<br>
     *
     * @param type The type
     * @param mode The mode
     * @return The gm effect packet
     */
    public static MaplePacket sendGMOperation(int type, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GM_OPERATION.getValue());
        mplew.write(type);
        mplew.write(mode);
        return mplew.getPacket();
    }

    public static MaplePacket sendHammerSlot(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.VICIOUS_HAMMER.getValue());
        mplew.write(0x34);
        mplew.writeInt(0);
        mplew.writeInt(slot);
        return mplew.getPacket();
    }

    public static MaplePacket sendHammerEnd() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.VICIOUS_HAMMER.getValue());
        mplew.write(0x38);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket updateHammerItem(IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.write(2);
        mplew.write(3);
        mplew.write(item.getType());
        mplew.writeShort(item.getPosition());
        mplew.write(0);
        mplew.write(1);
        addItemInfo(mplew, item, false, false);
        return mplew.getPacket();
    }

    public static MaplePacket sendCygnusCreation() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CREATE_CYGNUS.getValue());
        return mplew.getPacket();
    }

    /**
     *
     * @param type Success = 0 \r\n Name in Use = 1 \r\n All Character Slots Full = 2 \r\n This Name Cannot be Used = 3 \r\n
     * @return
     */
    public static MaplePacket sendCygnusMessage(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CYGNUS_RESPONSE.getValue());
        mplew.writeInt(type);
        return mplew.getPacket();
    }

    public static MaplePacket sendYellowTip(String s) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.YELLOW_TIP.getValue());
        mplew.write(255);
        mplew.writeMapleAsciiString(s);
        return mplew.getPacket();
    }

    public static MaplePacket dcPacket() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(7);
        mplew.write(1);
        mplew.writeInt(1337);
        mplew.writeAsciiString("sucker");
        return mplew.getPacket();
    }

    public static MaplePacket warpCS(MapleClient c, boolean mts) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        MapleCharacter chr = c.getPlayer();
        if (!mts) {
            mplew.writeShort(SendPacketOpcode.CS_OPEN.getValue());
        } else {
            mplew.writeShort(SendPacketOpcode.CS_OPEN.getValue()); //TODO : add MTS WArp
        }
        mplew.writeLong(-1);
        addCharStats(
                mplew, chr);
        mplew.write(20); // ???
        mplew.writeInt(chr.getMeso()); // mesos
        mplew.write(100); // equip slots
        mplew.write(100); // use slots
        mplew.write(100); // set-up slots
        mplew.write(100); // etc slots
        mplew.write(48); // storage slots
        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        Collection<IItem> equippedC = iv.list();
        List<Item> equipped = new ArrayList<Item>(equippedC.size());






        for (IItem item : equippedC) {
            equipped.add((Item) item);






        }
        Collections.sort(equipped);






        for (Item item : equipped) {
            addItemInfo(mplew, item);






        }
        mplew.writeShort(0); // start of equip inventory
        iv =
                chr.getInventory(MapleInventoryType.EQUIP);






        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);






        }
        mplew.write(0); // start of use inventory
        iv = chr.getInventory(MapleInventoryType.USE);






        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);






        }
        mplew.write(0); // start of set-up inventory
        iv = chr.getInventory(MapleInventoryType.SETUP);






        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);






        }
        mplew.write(0); // start of etc inventory
        iv = chr.getInventory(MapleInventoryType.ETC);






        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);






        }
        mplew.write(0); // start of cash inventory
        iv = chr.getInventory(MapleInventoryType.CASH);






        for (IItem item : iv.list()) {
            addItemInfo(mplew, item);






        }
        mplew.write(0);
        Map<ISkill, MapleCharacter.SkillEntry> skills = chr.getSkills();
        mplew.writeShort(skills.size());






        for (Entry<ISkill, MapleCharacter.SkillEntry> skill : skills.entrySet()) {
            mplew.writeInt(skill.getKey().getId());
            mplew.writeInt(skill.getValue().skillevel);






            if (skill.getKey().isFourthJob()) {
                mplew.writeInt(skill.getValue().masterlevel);






            }
        }
        mplew.write(HexTool.getByteArrayFromHexString("00 00 03 00 E1 50 00 00 DE 6D 01 00 31 2C 27 03 00 69 6E 67 1A 00 21 4E 80 0E 84 D9 67 1C CA 01 DE 50 80 F2 43 F2 6E 1C CA 01 89 4E 00 6E B9 15 6C 1C CA 01 29 27 80 B7 A3 1C 24 1D CA 01 22 4E 80 89 54 5F 69 1C CA 01 DF 50 80 CC 3C 8D 6F 1C CA 01 2A 27 80 B7 A3 1C 24 1D CA 01 E0 50 00 61 CB 66 70 1C CA 01 23 4E 80 37 A1 8C 69 1C CA 01 2B 27 80 B7 A3 1C 24 1D CA 01 24 4E 00 06 D6 A9 69 1C CA 01 84 4E 00 71 75 01 6C 1C CA 01 2C 27 80 B7 A3 1C 24 1D CA 01 25 4E 00 0F 37 9E 6A 1C CA 01 2D 27 80 B7 A3 1C 24 1D CA 01 26 4E 80 85 40 11 6B 1C CA 01 DD 27 80 01 1D BE 6F 1C CA 01 1E 27 80 B7 A3 1C 24 1D CA 01 DC 50 00 9C 9C DB 6C 1C CA 01 27 4E 80 3E F8 59 6B 1C CA 01 1F 27 80 B7 A3 1C 24 1D CA 01 20 4E 00 F5 6B 23 66 1C CA 01 DD 50 00 59 C2 D4 6D 1C CA 01 28 4E 80 7E 43 A6 6B 1C CA 01 10 27 00 67 BC AA 6C 1C CA 01 28 27 80 B7 A3 1C 24 1D CA 01"));
        mplew.writeLong(0);






        for (int i = 0; i
                < 15; i++) {
            mplew.write(CHAR_INFO_MAGIC);






        }
        mplew.writeInt(0);
        mplew.writeInt(0);//v72
        mplew.writeInt(0);//v72
        mplew.write(0);






        if (!mts) {
            mplew.write(1);






        }
        mplew.writeMapleAsciiString(chr.getClient().getAccountName());
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 38 00 01 9F 98 00 00 04 00 00 00 02 9F 98 00 00 04 00 00 00 03 9F 98 00 00 04 00 00 00 04 9F 98 00 00 04 00 00 00 05 9F 98 00 00 04 00 00 00 06 9F 98 00 00 04 00 00 00 07 9F 98 00 00 04 00 00 00 08 9F 98 00 00 04 00 00 00 09 9F 98 00 00 04 00 00 00 0A 9F 98 00 00 04 00 00 00 0B 9F 98 00 00 04 00 00 00 0C 9F 98 00 00 04 00 00 00 0D 9F 98 00 00 04 00 00 00 0E 9F 98 00 00 04 00 00 00 0F 9F 98 00 00 04 00 00 00 6F 24 9A 00 00 04 00 00 00 70 24 9A 00 00 04 00 00 00 71 24 9A 00 00 04 00 00 00 72 24 9A 00 00 04 00 00 00 73 24 9A 00 00 08 00 00 FF 74 24 9A 00 00 08 00 00 FF 78 24 9A 00 00 04 00 00 00 79 24 9A 00 00 04 00 00 00 53 2F 31 01 10 00 00 00 0D 54 2F 31 01 00 04 00 00 00 9B 3A 34 01 00 04 00 00 00 9C 3A 34 01 00 04 00 00 00 11 C2 35 01 14 08 00 00 0C 92 09 00 00 01 42 C2 35 01 10 00 00 00 0B 44 C2 35 01 00 04 00 00 00 45 C2 35 01 00 0C 00 00 00 03 46 C2 35 01 00 0C 00 00 00 03 69 48 37 01 14 08 00 00 0C 92 09 00 00 01 8A 48 37 01 00 04 00 00 00 D3 CE 38 01 14 08 00 00 0C 04 06 00 00 01 07 CF 38 01 00 04 00 00 00 AD 55 3A 01 00 04 00 00 00 AE 55 3A 01 00 04 00 00 00 AF 55 3A 01 00 04 00 00 00 98 62 3D 01 06 08 00 00 0C 00 4E 0C 00 00 01 1B 63 3D 01 10 08 00 00 0B 02 1E 63 3D 01 00 04 00 00 00 1F 63 3D 01 00 04 00 00 00 20 63 3D 01 00 04 00 00 00 FF F5 41 01 14 08 00 00 0C 92 09 00 00 01 3A F6 41 01 00 04 00 00 00 3B F6 41 01 00 0C 00 00 00 03 AE C3 C9 01 00 04 00 00 00 47 77 FC 02 00 08 00 00 FF 48 77 FC 02 00 08 00 00 FF 3C FE FD 02 00 0C 00 00 00 FF 40 FE FD 02 00 04 00 00 00 E7 91 02 03 00 0C 00 00 00 03 4E 87 93 03 00 04 00 00 01 DB 1E 2C 04 00 04 00 00 00 DC 1E 2C 04 00 04 00 00 00 00 73 00 65 00 63 00 2E 00 00 00 0D 00 DF 01 0E 04 0E 00 0E 00 DE 01 0E 04 5C 00 00 00 48 00 50 00 20 00 2D 00 32 00 34 00 2C 00 20 00 4D 00 50 00 20 00 2D 00 32 00 34 00 3B 00 20 00 49 00 6D 00 70 00 72 00 6F 00 76 00 65 00 73 00 20 00 67 00 75 00 6E 00 20 00 73 00 70 00 65 00 65 00 64 00 20 00 66 00 6F 00 72 00 20 00 36 00 30 00 20 00 73 00 65 00 63 00 2E 00 01 00 00 00 00 00 00 00 E6 91 02 03 01 00 00 00 00 00 00 00 FB E8 3E 01 01 00 00 00 00 00 00 00 4F 2F 31 01 01 00 00 00 00 00 00 00 75 24 9A 00 01 00 00 00 00 00 00 00 76 24 9A 00 01 00 00 00 01 00 00 00 E6 91 02 03 01 00 00 00 01 00 00 00 FB E8 3E 01 01 00 00 00 01 00 00 00 4F 2F 31 01 01 00 00 00 01 00 00 00 75 24 9A 00 01 00 00 00 01 00 00 00 76 24 9A 00 02 00 00 00 00 00 00 00 E6 91 02 03 02 00 00 00 00 00 00 00 FB E8 3E 01 02 00 00 00 00 00 00 00 4F 2F 31 01 02 00 00 00 00 00 00 00 75 24 9A 00 02 00 00 00 00 00 00 00 76 24 9A 00 02 00 00 00 01 00 00 00 E6 91 02 03 02 00 00 00 01 00 00 00 FB E8 3E 01 02 00 00 00 01 00 00 00 4F 2F 31 01 02 00 00 00 01 00 00 00 75 24 9A 00 02 00 00 00 01 00 00 00 76 24 9A 00 03 00 00 00 00 00 00 00 E6 91 02 03 03 00 00 00 00 00 00 00 FB E8 3E 01 03 00 00 00 00 00 00 00 4F 2F 31 01 03 00 00 00 00 00 00 00 75 24 9A 00 03 00 00 00 00 00 00 00 76 24 9A 00 03 00 00 00 01 00 00 00 E6 91 02 03 03 00 00 00 01 00 00 00 FB E8 3E 01 03 00 00 00 01 00 00 00 4F 2F 31 01 03 00 00 00 01 00 00 00 75 24 9A 00 03 00 00 00 01 00 00 00 76 24 9A 00 04 00 00 00 00 00 00 00 E6 91 02 03 04 00 00 00 00 00 00 00 FB E8 3E 01 04 00 00 00 00 00 00 00 4F 2F 31 01 04 00 00 00 00 00 00 00 75 24 9A 00 04 00 00 00 00 00 00 00 76 24 9A 00 04 00 00 00 01 00 00 00 E6 91 02 03 04 00 00 00 01 00 00 00 FB E8 3E 01 04 00 00 00 01 00 00 00 4F 2F 31 01 04 00 00 00 01 00 00 00 75 24 9A 00 04 00 00 00 01 00 00 00 76 24 9A 00 05 00 00 00 00 00 00 00 E6 91 02 03 05 00 00 00 00 00 00 00 FB E8 3E 01 05 00 00 00 00 00 00 00 4F 2F 31 01 05 00 00 00 00 00 00 00 75 24 9A 00 05 00 00 00 00 00 00 00 76 24 9A 00 05 00 00 00 01 00 00 00 E6 91 02 03 05 00 00 00 01 00 00 00 FB E8 3E 01 05 00 00 00 01 00 00 00 4F 2F 31 01 05 00 00 00 01 00 00 00 75 24 9A 00 05 00 00 00 01 00 00 00 76 24 9A 00 06 00 00 00 00 00 00 00 E6 91 02 03 06 00 00 00 00 00 00 00 FB E8 3E 01 06 00 00 00 00 00 00 00 4F 2F 31 01 06 00 00 00 00 00 00 00 75 24 9A 00 06 00 00 00 00 00 00 00 76 24 9A 00 06 00 00 00 01 00 00 00 E6 91 02 03 06 00 00 00 01 00 00 00 FB E8 3E 01 06 00 00 00 01 00 00 00 4F 2F 31 01 06 00 00 00 01 00 00 00 75 24 9A 00 06 00 00 00 01 00 00 00 76 24 9A 00 07 00 00 00 00 00 00 00 E6 91 02 03 07 00 00 00 00 00 00 00 FB E8 3E 01 07 00 00 00 00 00 00 00 4F 2F 31 01 07 00 00 00 00 00 00 00 75 24 9A 00 07 00 00 00 00 00 00 00 76 24 9A 00 07 00 00 00 01 00 00 00 E6 91 02 03 07 00 00 00 01 00 00 00 FB E8 3E 01 07 00 00 00 01 00 00 00 4F 2F 31 01 07 00 00 00 01 00 00 00 75 24 9A 00 07 00 00 00 01 00 00 00 76 24 9A 00 08 00 00 00 00 00 00 00 E6 91 02 03 08 00 00 00 00 00 00 00 FB E8 3E 01 08 00 00 00 00 00 00 00 4F 2F 31 01 08 00 00 00 00 00 00 00 75 24 9A 00 08 00 00 00 00 00 00 00 76 24 9A 00 08 00 00 00 01 00 00 00 E6 91 02 03 08 00 00 00 01 00 00 00 FB E8 3E 01 08 00 00 00 01 00 00 00 4F 2F 31 01 08 00 00 00 01 00 00 00 75 24 9A 00 08 00 00 00 01 00 00 00 76 24 9A 00 00 00 00 00 00 00 00 41 00 00 00"));






        return mplew.getPacket();






    }

    //TODO: ADD CS Ring
/*
     *
     *
    public static MaplePacket getAddCSRing(MapleClient c, IItem item) {
    MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
    mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
    mplew.write(0x67); //71 in other? find what makes this
    mplew.writeInt(item.getUniqueId());
    mplew.writeInt(0);
    mplew.writeInt(c.getAccID());
    mplew.writeInt(0);
    mplew.writeInt(item.getItemId());
    mplew.writeInt(item.getSn());
    mplew.write(1);
    mplew.writeLong(0);
    mplew.writeInt(0);
    mplew.writeShort(0);
    mplew.writeLong(NO_EXPIRATION);
    mplew.writeLong(0);
    mplew.writeMapleAsciiString(item.getSender());
    mplew.writeInt(item.getItemId());
    mplew.writeShort(1);
    return mplew.getPacket();
    }*/
//03 01 6D 01 00 00 00 01 00 15 00 D8 82 3D 00
    public static MaplePacket transferFromCSToInv(IItem item, int position) {
        //23 01 4D 01 00 01 CA 4A 0F 00 01 A7 C0 62 00 00 00 00 00 00 CD 77 3C AB 25 CA 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 65 69 6E 6E 08 00 00 40 E0 FD 3B 37 4F 01 FF FF FF FF
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x4A);
        mplew.write(position);//in csinventory
        addItemInfo(mplew, item, true, false);
        return mplew.getPacket();
    }

    public static MaplePacket getCSGifts(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x31);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSItem(MapleClient mapleclient, CashItemInfo cashiteminfo) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(74);
        mplew.write(HexTool.getByteArrayFromHexString("FA 96 C1 00"));
        mplew.writeInt(0);
        mplew.writeInt(mapleclient.getAccID());
        mplew.writeInt(0);
        mplew.writeInt(cashiteminfo.getId());
        mplew.write(HexTool.getByteArrayFromHexString("15 2D 31 01"));
        mplew.writeShort(cashiteminfo.getCount());
        mplew.write(HexTool.getByteArrayFromHexString("00 00 50 4C 40 00 B4 F9 78"));
        mplew.writeInt(0);
        mplew.write(HexTool.getByteArrayFromHexString("C0 1E CC C5 A4 73 CA 01"));
        mplew.writeLong(0);
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSQuestItem(byte position, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.writeInt(382);//7E 01 00 00
        mplew.write(0);//00
        mplew.writeShort(1);//01 00
        mplew.write(position);//19
        mplew.write(0);//00
        mplew.writeInt(itemid);//D8 82 3D 00
        return mplew.getPacket();
    }

    public static MaplePacket enableCSorMTS() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(HexTool.getByteArrayFromHexString("12 00 00 00 00 00 00"));
        return mplew.getPacket();
    }

    public static MaplePacket getCSInventory(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x2F);
        mplew.writeShort(0);
        mplew.writeShort(4);
        mplew.writeShort(3);
        return mplew.getPacket();
    }

    public static MaplePacket enableCSUse4() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 02 01 00 00 00 00 00 00 00 00 00 00 00 00
        mplew.write(HexTool.getByteArrayFromHexString("9F 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        return mplew.getPacket();
    }

    // Decoding : Raz (Snow) | Author : Penguins (Acrylic) | Finishing: Bassoe
    public static MaplePacket updateWishList(
            int characterid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x39);
        Connection con = DatabaseConnection.getConnection();
        int i = 10;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT sn FROM wishlist WHERE charid = ? LIMIT 10");
            ps.setInt(1, characterid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mplew.writeInt(rs.getInt("sn"));
                i--;
            }
            rs.close();
            ps.close();
        } catch (SQLException se) {
            log.info("Error getting wishlist data:", se);
        }
        while (i > 0) {
            mplew.writeInt(0);
            i--;
        }
        return mplew.getPacket();
    }

    public static MaplePacket spawnPlayerNPC(PlayerNPCs npc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
        mplew.write(1);
        mplew.writeInt(npc.getObjectId());
        mplew.writeInt(npc.getId());
        mplew.writeShort(npc.getPosition().x);
        mplew.writeShort(npc.getCY());
        mplew.write(npc.getDir());
        mplew.writeShort(npc.getFH());
        mplew.writeShort(npc.getRX0());
        mplew.writeShort(npc.getRX1());
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket getPlayerNPC(PlayerNPCs npc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_NPC.getValue());
        mplew.write(0x01);
        mplew.writeInt(npc.getId());
        mplew.writeMapleAsciiString(npc.getName());
        mplew.write(npc.getGender());
        mplew.write(npc.getSkin());
        mplew.writeInt(npc.getFace());
        mplew.write(npc.getDir());
        mplew.writeInt(npc.getHair());
        Map<Byte, Integer> equip = npc.getEquips();
        Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        for (byte position : equip.keySet()) {
            byte pos = (byte) (position * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, equip.get(position));
            } else if ((pos > 100 || pos == -128) && pos != 111) { // don't ask. o.o
                pos -= 100;
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, equip.get(position));
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, equip.get(position));
            }

        }
        for (Entry<Byte, Integer> entry : myEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF);
        for (Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF);
        Integer cWeapon = equip.get((byte) -111);
        if (cWeapon != null) {
            mplew.writeInt(cWeapon);
        } else {
            mplew.writeInt(0);
        }
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket removeNPC(int npcid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REMOVE_NPC.getValue());
        mplew.writeInt(npcid);
        return mplew.getPacket();
    }

    public static MaplePacket spouseChat(String from, String message, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPOUSE_CHAT.getValue());
        mplew.write(type);
        if (type == 4) {
            mplew.write(1);
        } else {
            mplew.writeMapleAsciiString(from);
            mplew.write(5);
        }
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }

    public static MaplePacket useChalkboard(MapleCharacter chr, boolean close) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHALKBOARD.getValue());
        mplew.writeInt(chr.getId());
        if (close) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getChalkboard());
        }
        return mplew.getPacket();
    }

    private static void addFakeAttackBody(LittleEndianWriter lew, int cid, AttackInfo attack, int skillLevel, int projectile) {
        lew.writeInt(cid);
        lew.write(attack.numAttackedAndDamage);
        if (attack.skill > 0) {
            lew.write(0xFF);
            lew.writeInt(attack.skill);
        } else {
            lew.write(0);
        }
        lew.write(0);
        lew.write(attack.stance);
        lew.write(attack.speed);
        lew.write(3);
        lew.writeInt(projectile);
        for (Pair<Integer, List<Integer>> oned : attack.allDamage) {
            if (oned.getRight() != null) {
                lew.writeInt(oned.getLeft().intValue());
                lew.write(0xFF);
                for (Integer eachd : oned.getRight()) {
                    lew.writeInt(eachd.intValue());
                }
            }
        }
    }

    public static MaplePacket showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OX_QUIZ.getValue());
        mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);
        return mplew.getPacket();
    }

    public static MaplePacket itemExpired(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 1E 00 02 83 C9 51 00 [2x drop coupon, 6pm~12am]
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(2);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static MaplePacket getFindBuddyReplyNotInChannel(String stringah, int channel) {  // CREDITS OLIVER. NOT TO BE RELEASED TO PUBLIC
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(0x48);
        mplew.writeMapleAsciiString(stringah);
        mplew.write(3);
        mplew.writeInt(channel - 1);
        return mplew.getPacket();
    }

    public static MaplePacket getFindBuddyReplyInChannel(String stringah, int mapid) { // CREDITS OLIVER. NOT TO BE RELEASED TO PUBLIC
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(0x48);
        mplew.writeMapleAsciiString(stringah);
        mplew.write(1);
        mplew.writeInt(mapid);
        mplew.write(new byte[8]); // ?? official doesn't send zeros here but whatever
        return mplew.getPacket();
    }

    public static MaplePacket getFindBuddyReplyInCS(String stringah) { // CREDITS OLIVER. NOT TO BE RELEASED TO PUBLIC
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(0x48);
        mplew.writeMapleAsciiString(stringah);
        mplew.write(2);
        mplew.writeInt(-1);
        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithCSorMTS(String target, boolean mts) { // Oliver
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(mts ? 0 : 2);
        mplew.writeInt(-1);
        return mplew.getPacket();
    }

    public static MaplePacket showAllianceInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x0C);
        MapleAlliance alliance = chr.getGuild().getAlliance(chr.getClient());
        if (alliance == null) { //show empty alliance (used for leaving, expelled)
            mplew.write(0);
            return mplew.getPacket();
        }
        mplew.write(1); //Only happens if you are in an alliance
        mplew.writeInt(alliance.getId());
        mplew.writeMapleAsciiString(alliance.getName()); // alliance name
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(alliance.getRankTitle(i));
        }
        int size = alliance.getGuilds().size();
        mplew.write(size);
        for (Integer guild : alliance.getGuilds()) {
            mplew.writeInt(guild);
        }
        mplew.writeInt((5 - size)); // probably capacity
        mplew.writeMapleAsciiString(alliance.getNotice());
        return mplew.getPacket();
    }

    public static MaplePacket makeNewAlliance(MapleAlliance alliance, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x0F);
        mplew.writeInt(alliance.getId());
        mplew.writeMapleAsciiString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(alliance.getRankTitle(i));
        }
        mplew.write(alliance.getGuilds().size());
        for (Integer guild : alliance.getGuilds()) {
            mplew.writeInt(guild);

        }
        mplew.writeInt(2); // probably capacity
        mplew.writeShort(0);
        for (Integer guildd : alliance.getGuilds()) {
            try {
                getGuildInfo(mplew, c.getChannelServer().getWorldInterface().getGuild(guildd, null));
            } catch (RemoteException re) {
                c.getChannelServer().reconnectWorld();
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket showAllianceMembers(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x0D);
        MapleAlliance az = chr.getGuild().getAlliance(chr.getClient());
        int e = 0;
        for (int u = 0; u < 5; u++) {
            if (az.getGuildlist().get(u) != null) {
                e++;
            }
        }
        mplew.writeInt(e);//ammount of guilds joined
        chr.setGuildRank(chr.getGuild().getMGC(chr.getId()).getGuildRank());
        for (int i = 0; i < 5; i++) {
            MapleGuild g = az.getGuildlist().get(i);
            if (g != null) {
                mplew.writeInt(g.getId());
                mplew.writeMapleAsciiString(g.getName());
                for (int a = 1; a
                        < 6; a++) {
                    mplew.writeMapleAsciiString(g.getRankTitle(a));
                }
                Collection<MapleGuildCharacter> members = g.getMembers();
                mplew.write(members.size());
                for (MapleGuildCharacter mgc : members) {
                    mplew.writeInt(mgc.getId());
                }
                for (MapleGuildCharacter mgc : members) {
                    mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 12));
                    mplew.write(0x00);
                    mplew.writeInt(mgc.getJobId());
                    mplew.writeInt(mgc.getReborns());
                    mplew.writeInt(mgc.getGuildRank());
                    mplew.writeInt(mgc.isOnline() ? 1 : 0);
                    mplew.writeInt(g.getSignature());
                    mplew.writeInt(mgc.getGuildRank());
                }
                mplew.writeInt(g.getCapacity());
                mplew.writeShort(g.getLogoBG());
                mplew.write(g.getLogoBGColor());
                mplew.writeShort(g.getLogo());
                mplew.write(g.getLogoColor());
                mplew.writeMapleAsciiString(g.getNotice());
                mplew.writeInt(g.getGP());
                mplew.write(HexTool.getByteArrayFromHexString("0F 03 00 00"));
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket getGuildAlliances(MapleAlliance alliance, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x0D);
        mplew.writeInt(alliance.getGuilds().size());
        for (Integer guild : alliance.getGuilds()) {
            try {
                getGuildInfo(mplew, c.getChannelServer().getWorldInterface().getGuild(guild, null));
            } catch (RemoteException re) {
                c.getChannelServer().reconnectWorld();
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket addGuildToAlliance(MapleAlliance alliance, int newGuild, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x12);
        mplew.writeInt(alliance.getId());
        mplew.writeMapleAsciiString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(alliance.getRankTitle(i));
        }
        mplew.write(alliance.getGuilds().size());
        for (Integer guild : alliance.getGuilds()) {
            mplew.writeInt(guild);
        }
        mplew.writeInt(2);
        mplew.writeMapleAsciiString(alliance.getNotice());
        mplew.writeInt(newGuild);
        try {
            getGuildInfo(mplew, c.getChannelServer().getWorldInterface().getGuild(newGuild, null));
        } catch (RemoteException re) {
            c.getChannelServer().reconnectWorld();
        }
        return mplew.getPacket();
    }

    public static MaplePacket allianceMemberOnline(MapleCharacter mc, boolean online) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x0E);
        mplew.writeInt(mc.getGuild().getAllianceId());
        mplew.writeInt(mc.getGuildId());
        mplew.writeInt(mc.getId());
        mplew.write(online ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket allianceNotice(int id, String notice) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x1C);
        mplew.writeInt(id);
        mplew.writeMapleAsciiString(notice);
        return mplew.getPacket();
    }

    public static MaplePacket changeAllianceRankTitle(int alliance, String[] ranks) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x1A);
        mplew.writeInt(alliance);
        for (int i = 0; i < 5; i++) {
            mplew.writeMapleAsciiString(ranks[i]);
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateAllianceJobLevel(MapleCharacter mc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x18);
        mplew.writeInt(mc.getGuild().getAllianceId());
        mplew.writeInt(mc.getGuildId());
        mplew.writeInt(mc.getId());
        mplew.writeInt(mc.getReborns());
        mplew.writeInt(mc.getJob().getId());
        return mplew.getPacket();
    }

    public static MaplePacket removeGuildFromAlliance(MapleAlliance alliance, int expelledGuild, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // PLEASE NOTE THAT WE MUST REMOVE THE GUILD BEFORE SENDING THIS PACKET. <3
        // ALSO ANOTHER NOTE, WE MUST REMOVE ALLIANCEID FROM GUILD BEFORE SENDING ASWELL <3
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x10);
        mplew.writeInt(alliance.getId());
        mplew.writeMapleAsciiString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(alliance.getRankTitle(i));
        }
        mplew.write(alliance.getGuilds().size());
        for (Integer guild : alliance.getGuilds()) {
            mplew.writeInt(guild);
        }
        mplew.write(HexTool.getByteArrayFromHexString("02 00 00 00"));
        mplew.writeMapleAsciiString(alliance.getNotice());
        mplew.writeInt(expelledGuild);
        try {
            getGuildInfo(mplew, c.getChannelServer().getWorldInterface().getGuild(expelledGuild, null));
        } catch (RemoteException re) {
            c.getChannelServer().reconnectWorld();
        }
        mplew.write(0x01);
        return mplew.getPacket();
    }

    public static MaplePacket disbandAlliance(int alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x1D);
        mplew.writeInt(alliance);
        return mplew.getPacket();
    }

    private static void getGuildInfo(MaplePacketLittleEndianWriter mplew, MapleGuild guild) {
        mplew.writeInt(guild.getId());
        mplew.writeMapleAsciiString(guild.getName());
        for (byte i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
        }
        Collection<MapleGuildCharacter> members = guild.getMembers();
        mplew.write(members.size());
        //then it is the size of all the members
        for (MapleGuildCharacter mgc : members) { //and each of their character ids o_O
            mplew.writeInt(mgc.getId());
        }
        for (MapleGuildCharacter mgc : members) {
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
            mplew.writeInt(mgc.getJobId());
            mplew.writeInt(mgc.getReborns());
            mplew.writeInt(mgc.getGuildRank());
            mplew.writeInt(mgc.isOnline() ? 1 : 0);
            mplew.writeInt(guild.getSignature());
            mplew.writeInt(mgc.getAllianceRank());
        }
        mplew.writeInt(guild.getCapacity());
        mplew.writeShort(guild.getLogoBG());
        mplew.write(guild.getLogoBGColor());
        mplew.writeShort(guild.getLogo());
        mplew.write(guild.getLogoColor());
        mplew.writeMapleAsciiString(guild.getNotice());
        mplew.writeInt(guild.getGP());
        mplew.writeInt(guild.getAllianceId());
    }

    public static MaplePacket sendDojoAnimation(byte firstByte, String animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(firstByte);
        mplew.writeMapleAsciiString(animation);
        return mplew.getPacket();
    }

    public static MaplePacket getDojoInfo(String info) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(10);
        mplew.write(HexTool.getByteArrayFromHexString("B7 04"));
        mplew.writeMapleAsciiString(info);
        return mplew.getPacket();
    }

    public static MaplePacket getDojoInfoMessage(String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }

    public static MaplePacket updateDojoStats(MapleCharacter chr, int belt) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(0x0a);
        mplew.write(HexTool.getByteArrayFromHexString("B7 04")); //?
        mplew.writeMapleAsciiString("pt=" + chr.getDojoPoints() + ";belt=" + belt + ";tuto=1");
        return mplew.getPacket();
    }

    public static MaplePacket trembleEffect(int type, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(1);
        mplew.write(type);
        mplew.writeInt(delay);
        return mplew.getPacket();
    }

    public static MaplePacket getEnergy(int level) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ENERGY.getValue());
        mplew.writeMapleAsciiString("energy");
        mplew.writeMapleAsciiString(Integer.toString(level));
        return mplew.getPacket();
    }

    public static MaplePacket dojoWarpUp() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DOJO_WARP_UP.getValue());
        mplew.write(0);
        mplew.write(6);
        return mplew.getPacket();
    }

    public static MaplePacket updateItemInSlot(IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0); // could be from drop
        mplew.write(2); // always 2
        mplew.write(3); // quantity > 0 (?)
        mplew.write(item.getType()); // inventory type
        mplew.write(item.getPosition()); // item slot
        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(item.getPosition()); // wtf repeat
        addItemInfo(mplew, item);
        return mplew.getPacket();

    }

    public static MaplePacket showCygnusIntro(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // For globalMS v.74, Credits to Traitor
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(0x12);
        mplew.writeMapleAsciiString("Effect/Direction.img/cygnus/Scene" + id);
        return mplew.getPacket();
    }

    public static MaplePacket CygnusIntroLock(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CYGNUS_INTRO_LOCK.getValue());
        mplew.write(enable ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket CygnusIntroDisableUI(boolean enable) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CYGNUS_INTRO_DISABLE_UI.getValue());
        mplew.write(enable ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket showEquipEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());
        return mplew.getPacket();
    }

    public static MaplePacket getFamilyData() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FAMILY.getValue());
        mplew.writeInt(11); // Number of events
        mplew.write(0);
        mplew.writeInt(300); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("Transfer to Family Member");
        mplew.writeMapleAsciiString("[Target] Myself\n[Effect] Will be transfered directly to the Map where the family member is located in.");
        mplew.write(1);
        mplew.writeInt(500); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("Summon family member");
        mplew.writeMapleAsciiString("[Target] 1 Family member\n[Effect] Summons one of the family member to the map you are located in.");
        mplew.write(2);
        mplew.writeInt(700); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("1.5 X Drop Rate for Me(15min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 15 min\n[Effect]  Drop rate will be #cincreased by 50%#.\nThe effect will be disregarded if overlapped with other drop rate event.");
        mplew.write(3);
        mplew.writeInt(800); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("1.5 X EXP for me(15min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 15min\n[Effect] EXP gained from monsters  will be #cincreased by 50%.#\nThe effect will be disregarded if overlapped with other EXP event.");
        mplew.write(4);
        mplew.writeInt(1000); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("Unity of Family(30min)");
        mplew.writeMapleAsciiString("[Condition] 6 juniors online from pedigree\n[Duration] 30min\n[Effect] Drop Rate and EXP gained will be #cincreased by 100%#.\nThe effect will be disregarded if overlapped with other Drop Rate and EXP event.");
        mplew.write(2);
        mplew.writeInt(1200); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X Drop Rate for Me(15min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 15min\n[Effect]  Drop rate will be #cincreased by 100%.# \nThe effect will be disregarded if overlapped with other Drop Rate event.");
        mplew.write(3);
        mplew.writeInt(1500); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X EXP event for Me(15min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 15min\n[Effect] EXP gained from monsters  will be #cincreased by 100%.#\nThe effect will be disregarded if overlapped with other EXP event.");
        mplew.write(2);
        mplew.writeInt(2000); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X Drop Rate for Me(30min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 30min\n[Effect]  drop rate will be #cincreased by 100%.# \nThe effect will be disregarded if overlapped with other Drop Rate event");
        mplew.write(3);
        mplew.writeInt(2500); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X EXP event for Me(30min)");
        mplew.writeMapleAsciiString("[Target] Myself\n[Duration] 30min\n[Effect] EXP gained from monsters  will be #cincreased by 100%.#\nThe effect will be disregarded if overlapped with other EXP event.");
        mplew.write(2);
        mplew.writeInt(4000); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X Drop Rate for Party(30min)");
        mplew.writeMapleAsciiString("[Target] My Party\n[Duration] 30min\n[Effect]  drop rate will be #cincreased by 100%.# \nThe effect will be disregarded if overlapped with other Drop Rate event.");
        mplew.write(3);
        mplew.writeInt(5000); // REP needed
        mplew.writeInt(1); // Number of times allowed per day
        mplew.writeMapleAsciiString("2 X EXP event for Party(30min)");
        mplew.writeMapleAsciiString("[Target] My Party\n[Duration] 30min\n[Effect] EXP gained from monsters  will be #cincreased by 100%.#\nThe effect will be disregarded if overlapped with other EXP event.");
        return mplew.getPacket();
    }

    //Mini Games - Cred : Shoot
    public static MaplePacket getMiniGame(MapleClient c, MiniGame minigame, boolean owner, int piece) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("05 01 02"));
        mplew.write(owner ? 0 : 1);
        mplew.write(0);
        addCharLook(mplew, minigame.getOwner(), false);
        mplew.writeMapleAsciiString(minigame.getOwner().getName());
        if (minigame.getVisitor() != null) {
            MapleCharacter visitor = minigame.getVisitor();
            mplew.write(1);
            addCharLook(mplew, visitor, false);
            mplew.writeMapleAsciiString(visitor.getName());
        }
        mplew.write(0xFF);
        mplew.write(0);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(2000);
        if (minigame.getVisitor() != null) {
            MapleCharacter visitor = minigame.getVisitor();
            mplew.write(1);
            mplew.writeInt(1);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(2000);
        }
        mplew.write(0xFF);
        mplew.writeMapleAsciiString(minigame.getDescription());
        mplew.write(piece);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameReady(MiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x34);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameUnReady(MiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x35);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameStart(MiniGame game, int loser) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("37 0" + loser));
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameSkipOwner(MiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("39 01"));
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameRequestTie(MiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x2C);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameDenyTie(MiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x2D);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("05 00"));
        mplew.write(2);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameSkipVisitor(MiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("39 00"));
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameMoveOmok(MiniGame game, int move1, int move2, int move3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("3A"));
        mplew.writeInt(move1);
        mplew.writeInt(move2);
        mplew.write(move3);






        return mplew.getPacket();






    }

    public static MaplePacket getMiniGameNewVisitor(MapleCharacter c, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("04 0" + slot));
        addCharLook(
                mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(2000);






        return mplew.getPacket();






    }

    public static MaplePacket getMiniGameRemoveVisitor() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("0A 01"));






        return mplew.getPacket();






    }

    private static MaplePacket getMiniGameResult(MiniGame game, boolean win, boolean lose, boolean tie, boolean forfeit, boolean omok) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x38);






        if (!tie && !forfeit) {
            mplew.write(0);






        } else if (tie) {
            mplew.write(1);






        } else if (forfeit) {
            mplew.write(2);






        }
        mplew.write(0); // owner
        mplew.writeInt(1); // unknown
        mplew.writeInt(game.getOwner().getMiniGamePoints("wins", omok) + (win ? 1 : 0)); // wins
        mplew.writeInt(game.getOwner().getMiniGamePoints("ties", omok) + (tie ? 1 : 0)); // ties
        mplew.writeInt(game.getOwner().getMiniGamePoints("losses", omok) + (lose ? 1 : 0)); // losses
        mplew.writeInt(2000); // points
        mplew.writeInt(1); // start of visitor; unknown
        mplew.writeInt(game.getVisitor().getMiniGamePoints("wins", omok) + (lose ? 1 : 0)); // wins
        mplew.writeInt(game.getVisitor().getMiniGamePoints("ties", omok) + (tie ? 1 : 0)); // ties
        mplew.writeInt(game.getVisitor().getMiniGamePoints("losses", omok) + (win ? 1 : 0)); // losses
        mplew.writeInt(2000); // points
        game.getOwner().setMiniGamePoints(win, lose, omok);
        game.getVisitor().setMiniGamePoints(lose, win, omok);






        return mplew.getPacket();






    }

    public static MaplePacket getMiniGameOwnerWin(MiniGame game) {
        return getMiniGameResult(game, true, false, false, false, true);






    }

    public static MaplePacket getMiniGameVisitorWin(MiniGame game) {
        return getMiniGameResult(game, false, true, false, false, true);






    }

    public static MaplePacket getMiniGameTie(MiniGame game) {
        return getMiniGameResult(game, false, false, true, false, true);






    }

    public static MaplePacket getMiniGameOwnerForfeit(MiniGame game) {
        return getMiniGameResult(game, false, true, false, true, true);






    }

    public static MaplePacket getMiniGameVisitorForfeit(MiniGame game) {
        return getMiniGameResult(game, true, false, false, true, true);






    }

    public static MaplePacket getMatchCardOwnerWin(MiniGame game) {
        return getMiniGameResult(game, true, false, false, false, false);






    }

    public static MaplePacket getMatchCardVisitorWin(MiniGame game) {
        return getMiniGameResult(game, false, true, false, false, false);






    }

    public static MaplePacket getMatchCardTie(MiniGame game) {
        return getMiniGameResult(game, false, false, true, false, false);






    }

    public static MaplePacket getMiniGameClose(byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(5);
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0xA);
        mplew.write(1);
        mplew.write(3);






        return mplew.getPacket();






    }

    public static MaplePacket getMatchCard(MapleClient c, MiniGame minigame, boolean owner, int piece) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("05 02 02"));
        mplew.write(owner ? 0 : 1);
        mplew.write(0);
        addCharLook(
                mplew, minigame.getOwner(), false);
        mplew.writeMapleAsciiString(minigame.getOwner().getName());






        if (minigame.getVisitor() != null) {
            MapleCharacter visitor = minigame.getVisitor();
            mplew.write(1);
            addCharLook(
                    mplew, visitor, false);
            mplew.writeMapleAsciiString(visitor.getName());






        }
        mplew.write(0xFF);
        mplew.write(0);
        mplew.writeInt(2);
        mplew.writeInt(minigame.getOwner().getMiniGamePoints("wins", false));
        mplew.writeInt(minigame.getOwner().getMiniGamePoints("ties", false));
        mplew.writeInt(minigame.getOwner().getMiniGamePoints("losses", false));
        mplew.writeInt(2000);






        if (minigame.getVisitor() != null) {
            MapleCharacter visitor = minigame.getVisitor();
            mplew.write(1);
            mplew.writeInt(2);
            mplew.writeInt(visitor.getMiniGamePoints("wins", false));
            mplew.writeInt(visitor.getMiniGamePoints("ties", false));
            mplew.writeInt(visitor.getMiniGamePoints("losses", false));
            mplew.writeInt(2000);






        }
        mplew.write(0xFF);
        mplew.writeMapleAsciiString(minigame.getDescription());
        mplew.write(piece);
        mplew.write(0);






        return mplew.getPacket();






    }

    public static MaplePacket getMatchCardStart(MiniGame game, int loser) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("37 0" + loser));
        mplew.write(HexTool.getByteArrayFromHexString("0C"));






        int last = 13;






        if (game.getMatchesToWin() > 10) {
            last = 31;






        } else if (game.getMatchesToWin() > 6) {
            last = 21;






        }
        for (int i = 1; i
                < last; i++) {
            mplew.writeInt(game.getCardId(i));






        }
        return mplew.getPacket();






    }

    public static MaplePacket getMatchCardNewVisitor(MapleCharacter c, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("04 0" + slot));
        addCharLook(
                mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeInt(1);
        mplew.writeInt(c.getMiniGamePoints("wins", false));
        mplew.writeInt(c.getMiniGamePoints("ties", false));
        mplew.writeInt(c.getMiniGamePoints("losses", false));
        mplew.writeInt(2000);






        return mplew.getPacket();






    }

    public static MaplePacket getMatchCardSelect(MiniGame game, int turn, int slot, int firstslot, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("3E 0" + turn));






        if (turn == 1) {
            mplew.write(slot);






        } else if (turn == 0) {
            mplew.write(slot);
            mplew.write(firstslot);
            mplew.write(type);






        }
        return mplew.getPacket();






    }

    public static MaplePacket getHiredMerchant(MapleClient c, MiniGame minigame, boolean owner, int piece) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("05 05 04 00 00 71 C0 4C 00"));
        mplew.writeMapleAsciiString("Hired Merchant");
        mplew.write(0xFF);
        mplew.write(0);
        mplew.write(0);
        mplew.writeMapleAsciiString(c.getPlayer().getName());
        mplew.write(HexTool.getByteArrayFromHexString("1F 7E 00 00 00 00 00 00 00 00 03 00 31 32 33 10 00 00 00 00 01 01 00 01 00 7B 00 00 00 02 52 8C 1E 00 00 00 80 05 BB 46 E6 17 02 01 00 00 00 00 00"));






        return mplew.getPacket();






    }

    public static MaplePacket addOmokBox(MapleCharacter c, int ammount, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        addAnnounceBox(
                mplew, c.getMiniGame(), 1, 0, ammount, type);






        return mplew.getPacket();






    }

    public static MaplePacket removeOmokBox(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        mplew.write(0);






        return mplew.getPacket();






    }

    public static MaplePacket addMatchCardBox(MapleCharacter c, int ammount, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        addAnnounceBox(
                mplew, c.getMiniGame(), 2, 0, ammount, type);






        return mplew.getPacket();






    }

    public static MaplePacket removeMatchcardBox(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        mplew.write(0);






        return mplew.getPacket();






    }

    public static MaplePacket enableReport() { // by snow
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(0x2C);
        mplew.write(1);






        return mplew.getPacket();






    }

    public static MaplePacket addCharBox(MapleCharacter c, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        addAnnounceBox(
                mplew, c.getPlayerShop(), type);






        return mplew.getPacket();






    }

    public static MaplePacket removeCharBox(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        mplew.write(0);






        return mplew.getPacket();






    }

    private static void addAnnounceBox(MaplePacketLittleEndianWriter mplew, MaplePlayerShop shop, int availability) {
        // 00: no game        // 01: omok game        // 02: card game        // 04: shop
        mplew.write(4);
        mplew.writeInt(shop.getObjectId()); // gameid/shopid
        mplew.writeMapleAsciiString(shop.getDescription()); // desc
        // 00: public        // 01: private
        mplew.write(0);
        // 00: red 4x3        // 01: green 5x4        // 02: blue 6x5        // omok:        // 00: normal
        mplew.write(0);
        // first slot: 1/2/3/4        // second slot: 1/2/3/4
        mplew.write(1);
        mplew.write(availability);
        // 0: open        // 1: in progress
        mplew.write(0);






    }

    private static void addAnnounceBox(MaplePacketLittleEndianWriter mplew, MiniGame game, int gametype, int type, int ammount, int joinable) {
        mplew.write(gametype);
        mplew.writeInt(game.getObjectId()); // gameid/shopid
        mplew.writeMapleAsciiString(game.getDescription()); // desc
        mplew.write(0);
        mplew.write(type);
        mplew.write(ammount);
        mplew.write(2);
        mplew.write(joinable);






    }

    public static MaplePacket getPlayerShopChat(MapleCharacter c, String chat, byte slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("06 08"));
        mplew.write(slot);
        mplew.writeMapleAsciiString(c.getName() + " : " + chat);






        return mplew.getPacket();






    }

    public static MaplePacket getTradeChat(MapleCharacter c, String chat, boolean owner) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("06 08"));
        mplew.write(owner ? 0 : 1);
        mplew.writeMapleAsciiString(c.getName() + " : " + chat);






        return mplew.getPacket();






    }

    public static MaplePacket hiredMerchantBox() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(0x2F); // header.
        mplew.write(0x07);






        return mplew.getPacket();






    }

    public static MaplePacket getHiredMerchant(MapleCharacter chr, HiredMerchant hm, boolean firstTime) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue()); // header.
        mplew.write(HexTool.getByteArrayFromHexString("05 05 04"));
        mplew.write(hm.isOwner(chr) ? 0 : 1);
        mplew.write(0);
        mplew.writeInt(hm.getItemId());
        mplew.writeMapleAsciiString("Hired Merchant");






        for (int i = 0; i
                < 3; i++) {
            if (hm.getVisitors()[i] != null) {
                mplew.write(i + 1);
                addCharLook(
                        mplew, hm.getVisitors()[i], false);
                mplew.writeMapleAsciiString(hm.getVisitors()[i].getName());






            }
        }
        mplew.write(0xFF);
        mplew.writeShort(0);
        mplew.writeMapleAsciiString(hm.getOwner());






        if (hm.isOwner(chr)) {
            mplew.writeInt(Integer.MAX_VALUE);
            mplew.write(firstTime ? 1 : 0);
            mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00"));






        }
        mplew.writeMapleAsciiString(hm.getDescription());
        mplew.write(0x10);
        mplew.writeInt(0);
        mplew.write(hm.getItems().size());






        if (hm.getItems().size() == 0) {
            mplew.write(0);






        } else {
            for (MaplePlayerShopItem item : hm.getItems()) {
                mplew.writeShort(item.getBundles());
                mplew.writeShort(item.getItem().getQuantity());
                mplew.writeInt(item.getPrice());
                addItemInfo(
                        mplew, item.getItem(), true, true);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket hiredMerchantUpdate(HiredMerchant hm) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x17);
        mplew.writeInt(0);
        mplew.write(hm.getItems().size());
        for (MaplePlayerShopItem item : hm.getItems()) {
            mplew.writeShort(item.getBundles());
            mplew.writeShort(item.getItem().getQuantity());
            mplew.writeInt(item.getPrice());
            addItemInfo(mplew, item.getItem(), true, true);
        }
        return mplew.getPacket();
    }

    public static MaplePacket hiredMerchantChat(String message, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("06 08"));
        mplew.write(slot);
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }

    public static MaplePacket hiredMerchantVisitorLeave(int slot, boolean owner) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x0A);
        if (!owner) {
            mplew.write(slot);
        }
        return mplew.getPacket();
    }

    public static MaplePacket hiredMerchantForceLeave2() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x0A);
        mplew.write(HexTool.getByteArrayFromHexString("00 10"));
        return mplew.getPacket();
    }

    public static MaplePacket hiredMerchantForceLeave1() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x0A);
        mplew.write(HexTool.getByteArrayFromHexString("01 0D"));
        return mplew.getPacket();
    }

    public static MaplePacket hiredMerchantVisitorAdd(MapleCharacter chr, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x04);
        mplew.write(slot);
        addCharLook(mplew, chr, false);
        mplew.writeMapleAsciiString(chr.getName());
        return mplew.getPacket();
    }

    public static MaplePacket spawnHiredMerchant(HiredMerchant hm) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_HIRED_MERCHANT.getValue());
        mplew.writeInt(hm.getOwnerId());
        mplew.writeInt(hm.getItemId());
        mplew.writeShort((short) hm.getPosition().getX());
        mplew.writeShort((short) hm.getPosition().getY());
        mplew.writeShort(0);
        mplew.writeMapleAsciiString(hm.getOwner());
        mplew.write(0x05);
        mplew.writeInt(hm.getObjectId());
        mplew.writeMapleAsciiString(hm.getDescription());
        mplew.write(hm.getItemId() % 10);
        mplew.write(HexTool.getByteArrayFromHexString("01 04"));
        return mplew.getPacket();
    }

    public static MaplePacket destroyHiredMerchant(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DESTROY_HIRED_MERCHANT.getValue());
        mplew.writeInt(id);
        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopNewVisitor(MapleCharacter c, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x04);
        mplew.write(slot);
        addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopRemoveVisitor(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x0A);
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static MaplePacket shopErrorMessage(int error, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x0A);
        mplew.write(type);
        mplew.write(error);
        return mplew.getPacket();
    }

    public static MaplePacket removeItemFromDuey(boolean remove, int Package) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(0x17);
        mplew.writeInt(Package);
        mplew.write(remove ? 3 : 4);
        return mplew.getPacket();
    }

    public static MaplePacket sendDueyMSG(byte operation) {
        return sendDuey(operation, null);
    }

    public static MaplePacket sendDuey(byte operation, List<DueyPackages> packages) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(operation);
        if (operation == 8) {
            mplew.write(0);
            mplew.write(packages.size());
            for (DueyPackages dp : packages) {
                mplew.writeInt(dp.getPackageId());
                mplew.writeAsciiString(dp.getSender());
                for (int i = dp.getSender().length(); i < 13; i++) {
                    mplew.write(0);
                }
                mplew.writeInt(dp.getMesos());
                mplew.writeLong(getQuestTimestamp(dp.sentTimeInMilliseconds()));
                mplew.writeLong(0); // Contains message o____o.
                for (int i = 0; i < 48; i++) {
                    mplew.writeInt(Randomizer.getInstance().nextInt(Integer.MAX_VALUE));
                }
                mplew.writeInt(0);
                mplew.write(0);
                if (dp.getItem() != null) {
                    mplew.write(1);
                    addItemInfo(
                            mplew, dp.getItem(), true, true);
                } else {
                    mplew.write(0);
                }
            }
            mplew.write(0);

        }
        return mplew.getPacket();
    }
}
