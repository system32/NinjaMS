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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.scripting.npc;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.odinms.client.Enums.MapleJob;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;

import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.scripting.AbstractPlayerInteraction;
import net.sf.odinms.scripting.event.EventManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.client.Enums.MapleStat;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.guild.MapleAlliance;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.server.MapleSquad;
import net.sf.odinms.server.MapleSquadType;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.quest.MapleQuest;

/**
 *
 * @author Matze
 */
public class NPCConversationManager extends AbstractPlayerInteraction {

    private MapleClient c;
    private int npc;
    private String getText;
    private ChannelServer cserv;
    private MapleCharacter chr;

    public NPCConversationManager(MapleClient c, int npc) {
        super(c);
        this.c = c;
        this.npc = npc;
    }

    public NPCConversationManager(MapleClient c, int npc, MapleCharacter chr) {
        super(c);
        this.c = c;
        this.npc = npc;
        this.chr = chr;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(this);
    }

    public void sendNext(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01"));
    }

    public void sendPrev(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00"));
    }

    public void sendNextPrev(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01"));
    }

    public void sendOk(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00"));
    }

    public void voteMSG() {
        sendOk("Don't forget to vote for us #b http://ninjams.org/vote");
    }

    public void sendYesNo(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 1, text, ""));
    }

    public void sendAcceptDecline(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0C, text, ""));
    }

    public void sendSimple(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 4, text, ""));
    }

    public void sendStyle(String text, int styles[]) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalkStyle(npc, text, styles));
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalkNum(npc, text, def, min, max));
    }

    public void sendGetText(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalkText(npc, text));
    }

    public void setGetText(String text) {
        this.getText = text;
    }

    public int getNumber() {
        int fuck = 0;
        try {
            fuck = Integer.parseInt(getText);
        } catch (NumberFormatException numberFormatException) {
            fuck = 0;
        }
        return fuck;
    }

    public String getText() {
        return this.getText;
    }

    public void openShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(getClient());
    }

    public void changeJob(MapleJob job) {
        getPlayer().changeJob(job);
    }

    public void changeJobById(int fuck) {
        getPlayer().changeJobById(fuck);
    }

    public MapleJob getJob() {
        return getPlayer().getJob();
    }

    public int getJobId() {
        return getPlayer().getJob().getId();
    }

    public void completeQuest(int id) {
        MapleQuest.getInstance(id).complete(getPlayer(), npc);
    }

    public void forfeitQuest(int id) {
        MapleQuest.getInstance(id).forfeit(getPlayer());
    }

    /**
     * use getPlayer().getMeso() instead
     * @return
     */
    @Deprecated
    public int getMeso() {
        return getPlayer().getMeso();
    }

    public void gainMeso(int gain) {
        getPlayer().gainMeso(gain, true, false, true);
    }

    public void gainExp(int gain) {
        getPlayer().gainExp(gain, true, true);
    }

    public int getNpc() {
        return npc;
    }

    /**
     * use getPlayer().getLevel() instead
     * @return
     */
    @Deprecated
    public int getLevel() {
        return getPlayer().getLevel();
    }

    public void unequipEverything() {
        MapleInventory equipped = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        List<Byte> ids = new LinkedList<Byte>();
        for (IItem item : equipped.list()) {
            ids.add(item.getPosition());
        }
        for (byte id : ids) {
            MapleInventoryManipulator.unequip(getC(), id, equip.getNextFreeSlot());
        }
    }

    public void teachSkill(int id, int level, int masterlevel) {
        getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }

    /**
     * Use getPlayer() instead (for consistency with MapleClient)
     * @return
     */
    @Deprecated
    public MapleCharacter getChar() {
        return getPlayer();
    }

    public MapleClient getC() {
        return getClient();
    }

    public void rechargeStars() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        IItem stars = getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) 1);
        if (ii.isThrowingStar(stars.getItemId()) || ii.isBullet(stars.getItemId())) {
            stars.setQuantity(ii.getSlotMax(stars.getItemId()));
            getC().getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, (Item) stars));
        }
    }

    public EventManager getEventManager(String event) {
        return getClient().getChannelServer().getEventSM().getEventManager(event);
    }

    public void showEffect(String effect) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(effect));
    }

    public void playSound(String sound) {
        getClient().getPlayer().getMap().broadcastMessage(MaplePacketCreator.playSound(sound));
    }

    @Override
    public String toString() {
        return "Conversation with NPC: " + npc;
    }

    public void updateBuddyCapacity(int capacity) {
        getPlayer().setBuddyCapacity(capacity);
    }

    public int getBuddyCapacity() {
        return getPlayer().getBuddyCapacity();
    }

    public void setHair(int hair) {
        c.getPlayer().setHair(hair);
        c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        c.getPlayer().equipChanged();
    }

    public void setFace(int face) {
        c.getPlayer().setFace(face);
        c.getPlayer().updateSingleStat(MapleStat.FACE, face);
        c.getPlayer().equipChanged();
    }

    @SuppressWarnings("static-access")
    public void setSkin(int color) {
        c.getPlayer().setSkinColor(c.getPlayer().getSkinColor().getById(color));
        c.getPlayer().updateSingleStat(MapleStat.SKIN, color);
        c.getPlayer().equipChanged();
    }

    public MapleSquad createMapleSquad(MapleSquadType type) {
        MapleSquad squad = new MapleSquad(c.getChannel(), getPlayer());
        if (getSquadState(type) == 0) {
            c.getChannelServer().addMapleSquad(squad, type);
        } else {
            return null;
        }
        return squad;
    }

    public MapleCharacter getSquadMember(MapleSquadType type, int index) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        MapleCharacter ret = null;
        if (squad != null) {
            ret = squad.getMembers().get(index);
        }
        return ret;
    }

    public int getSquadState(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            return squad.getStatus();
        } else {
            return 0;
        }
    }

    public void setSquadState(MapleSquadType type, int state) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.setStatus(state);
        }
    }

    public boolean checkSquadLeader(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            if (squad.getLeader().getId() == getPlayer().getId()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void removeMapleSquad(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            if (squad.getLeader().getId() == getPlayer().getId()) {
                squad.clear();
                c.getChannelServer().removeMapleSquad(squad, type);
            }
        }
    }

    public int numSquadMembers(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        int ret = 0;
        if (squad != null) {
            ret = squad.getSquadSize();
        }
        return ret;
    }

    public boolean isSquadMember(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        boolean ret = false;
        if (squad.containsMember(getPlayer())) {
            ret = true;
        }
        return ret;
    }

    public void addSquadMember(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.addMember(getPlayer());
        }
    }

    public void removeSquadMember(MapleSquadType type, MapleCharacter chr, boolean ban) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(chr, ban);
        }
    }

    public void removeSquadMember(MapleSquadType type, int index, boolean ban) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            MapleCharacter chrs = squad.getMembers().get(index);
            squad.banMember(chrs, ban);
        }
    }

    public boolean canAddSquadMember(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            if (squad.isBanned(getPlayer())) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void warpSquadMembers(MapleSquadType type, int mapId) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
        if (squad != null) {
            if (checkSquadLeader(type)) {
                for (MapleCharacter chaar : squad.getMembers()) {
                    chaar.changeMap(map, map.getPortal(0));
                }
            }
        }
    }

    public void resetReactors(int mapid) {
        MapleMap shit = c.getChannelServer().getMapFactory().getMap(mapid);
        if (shit != null) {
            shit.resetReactors();
        }
    }

    public void killMobsInMap(int mapid) {
        MapleMap shit = c.getChannelServer().getMapFactory().getMap(mapid);
        if (shit != null) {
            shit.killAllMonsters(false);
        }
    }

    public void resetReactors() {
        c.getPlayer().getMap().resetReactors();
    }

    public void displayGuildRanks() {
        MapleGuild.displayGuildRanks(getClient(), npc);
    }

    public void setClan(int fuck) {
        getPlayer().setClan(fuck);
    }

    public int getClan() {
        return getPlayer().getClan().getId();
    }

    public String showClan() {
        return " You belong in " + getPlayer().getClan().getName();
    }

    public void claimVoteRewards() {
        if (getPlayer().getCheatTracker().spam(600000, 7)) {
            c.showMessage(5, "You are trying too often So The system did not check if you have any rewards left. try after 15 minutes");
            return;
        } else {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM voterewards WHERE name = ?");
                ps.setString(1, c.getAccountName());
                ResultSet rs = ps.executeQuery();
                int i = 0;
                if (!rs.next()) {
                    c.showMessage(5, "You dont seem to have any claim left");
                } else {
                    do {
                        getPlayer().addNinjaTensu();
                        c.showMessage(5, "You have gained 1 NinjaTensuu");
                    }while (rs.next());
                    PreparedStatement pse = con.prepareStatement("DELETE FROM voterewards WHERE name = ?");
                    pse.setString(1, c.getAccountName());
                    pse.executeUpdate();
                    pse.close();
                }
                rs.close();
                ps.close();
                getPlayer().saveToDB();
            } catch (SQLException ex) {
                Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void gainAP(int fuck) {
        getPlayer().gainAp(fuck);
    }

    public void gainNX(int fuck) {
        getPlayer().addCSPoints(2, fuck);
    }

    public void jqComplete() {
        int[] jqmap = {105040311, 105040313, 105040316, 103000902, 103000905, 103000909, 101000101, 101000104, 109040004, 280020001};
        int i = 0;
        boolean fuck = false;
        for (i = 0; i < jqmap.length; i++) {
            if (getPlayer().getMapId() == jqmap[i]) {
                fuck = true;
            }
        }
        if (fuck && (getChannel() == 3)) {
            getPlayer().giveJQReward();
            getPlayer().finishAlert();
        }
    }

    private int getChannel() {
        return c.getChannel();
    }

    public void startJQ(int fuck) {
        getPlayer().startJq(fuck);
        getPlayer().startAlert(fuck);
    }

    public void dropMessage(String fuck) {
        getPlayer().dropMessage(fuck);
    }

    public void jqBonus() {
        if (checkPages()) {
            getPlayer().bonusReward();
        } else {
            dropMessage("you don't have the pages. you can go fap now");
        }
    }

    public boolean checkPages() {
        int[] pages = {4001064, 4001065, 4001066, 4001067, 4001068, 4001069, 4001070, 4001071, 4001072, 4001073};
        int i = 0;
        for (i = 0; i < pages.length; i++) {
            if (!getPlayer().haveItem(pages[i], 1)) {
                return false;
            }
        }
        return true;
    }

    public void apBonus() {
        if (checkPages()) {
            if (getPlayer().getRemainingAp() < (32767 - 1500)) {
                getPlayer().gainAp(1500);
                removePages();
                dropMessage("you have gained 1500 Ap");
            } else {
                dropMessage("Too bad you have too much remaining AP");
                return;
            }
        } else {
            dropMessage("you don't have the pages. you can go fap now");
        }
    }

    public boolean checkPagesTence() {
        int[] pages = {4001064, 4001065, 4001066, 4001067, 4001068, 4001069, 4001070, 4001071, 4001072, 4001073};
        int i = 0;
        for (i = 0; i < pages.length; i++) {
            if (!getPlayer().haveItem(pages[i], 10)) {
                return false;
            }
        }
        return true;
    }

    public void superYellowSnowShoes() {
        if (checkPagesTence()) {
            if (checkSpace(1072239, 1)) {
                int i = 0;
                for (i = 0; i < 10; i++) {
                    removePages();
                }
                gainStatItem(1072239, (short) 13337, (short) 10, (short) 69);
            } else {
                dropMessage("You don't have space in your bag.");
            }
        } else {
            dropMessage("You no have enuff chakra lor. go fap fap ;P");
        }
    }

    public void removePages() {
        int[] pages = {4001064, 4001065, 4001066, 4001067, 4001068, 4001069, 4001070, 4001071, 4001072, 4001073};
        int i = 0;
        for (i = 0; i < pages.length; i++) {
            gainItem(pages[i], (short) -1);
        }
    }

    public boolean checkSpace(int itemid, int fuck) {
        return getPlayer().checkSpace(itemid, fuck);
    }

    public boolean checkSpace(int itemid) {
        return checkSpace(itemid, 1);
    }

   public void disbandAlliance(MapleClient c, int allianceId) {
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("DELETE FROM `alliance` WHERE id = ?");
            ps.setInt(1, allianceId);
            ps.executeUpdate();
            ps.close();
            c.getChannelServer().getWorldInterface().allianceMessage(c.getPlayer().getGuild().getAllianceId(), MaplePacketCreator.disbandAlliance(allianceId), -1, -1);
            c.getChannelServer().getWorldInterface().disbandAlliance(allianceId);
        } catch (RemoteException r) {
            c.getChannelServer().reconnectWorld();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public boolean canBeUsedAllianceName(String name) {
        if (name.contains(" ") || name.length() > 12) {
            return false;
        }
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT name FROM alliance WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                rs.close();
                return false;
            }
            ps.close();
            rs.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static MapleAlliance createAlliance(MapleCharacter chr1, MapleCharacter chr2, String name) {
        int id = 0;
        int guild1 = chr1.getGuildId();
        int guild2 = chr2.getGuildId();
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `alliance` (`name`, `guild1`, `guild2`) VALUES (?, ?, ?)");
            ps.setString(1, name);
            ps.setInt(2, guild1);
            ps.setInt(3, guild2);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            id = rs.getInt(1);
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        MapleAlliance alliance = new MapleAlliance(name, id, guild1, guild2);
        try {
            WorldChannelInterface wci = chr1.getClient().getChannelServer().getWorldInterface();
            wci.setGuildAllianceId(guild1, id);
            wci.setGuildAllianceId(guild2, id);
            chr1.setAllianceRank(1);
            chr1.saveGuildStatus();
            chr2.setAllianceRank(2);
            chr2.saveGuildStatus();
            wci.addAlliance(id, alliance);
            wci.allianceMessage(id, MaplePacketCreator.makeNewAlliance(alliance, chr1.getClient()), -1, -1);
        } catch (RemoteException e) {
            chr1.getClient().getChannelServer().reconnectWorld();
            chr2.getClient().getChannelServer().reconnectWorld();
            return null;
        }
        return alliance;
    }
   

    public void sendServerNotice(int type, String message) {
        for (ChannelServer vvvv : ChannelServer.getAllInstances()) {
            vvvv.broadcastPacket(MaplePacketCreator.serverNotice(type, message));
        }
    }

    public int partyMembersInMap() {
        int inMap = 0;
        for (MapleCharacter char2 : getPlayer().getMap().getCharacters()) {
            if (char2.getParty() == getPlayer().getParty()) {
                inMap++;
            }
        }
        return inMap;
    }

    public void doDojoMapCheck(int checknumber, int type) {
        for (int i = 1; i <= 38; i++) { //only 32 stages, but 38 maps
            if (getPlayerQuantity(925020000 + (100 * i) + checknumber) > 0) {
                if (checknumber != 4) {
                    doDojoMapCheck(checknumber + 1, type);
                    return;
                } else {
                    sendOk("#eThis channel already has 5 parties inside the dojo!#n Please embark on another channel.");
                    dispose();
                    return;
                }
            } else {
                clearDojoMap(925020000 + (100 * i) + checknumber);
            }
        }
        dispose();
        if (getPlayer().getDojoStage() == 0) {
            if (type == 1) {
                warpParty(925020100 + checknumber);
            } else {
                warp(925020100 + checknumber);
            }
        } else {
            warp(925020000 + ((getPlayer().getDojoStage() / 100) % 100 * 100) + checknumber, 0);
            getPlayer().setDojoStage(0);
        }
        sendOk("Have fun, #v4000252#.");
    }

    public void addAutobuff(int id) {
        p().addAutobuff(id);
    }

    public void removeAutobuff(String id) {
        p().removeAutobuff(Integer.parseInt(id));
    }

    public void removeAutobuff(int id) {
        p().removeAutobuff((int) id);
    }

    public void removeAutobuff(long id) {
        p().removeAutobuff((int) id);
    }

    public String showAutobuffs() {
        String ret = "";
        for (int i : p().getAutobuffs()) {
            ret += "\r\n\r\n#L" + ((int) i) + "##s" + i + "# #e#b" + getSkillName(i) + "#k#n";
            ret += "\r\n#eJob of Origin#n: " + getJobName(i / 10000) + "#l";
        }
        return ret;
    }

    public String getSkillName(int i) {
        return SkillFactory.getSkillName(i);
    }

    public String getJobName(int id) {
        return GameConstants.getJobName(id);
    }

    public int[] getAutobuffArray() {
        int[] array = new int[p().getAutobuffs().size()];
        int z = 0;
        for (int i : p().getAutobuffs()) {
            array[z] = i;
            z++;
        }
        return array;
    }
}
