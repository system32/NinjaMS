/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.Processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.client.Enums.Status;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.OliveroMatic.RBRankingInfo;
import net.sf.odinms.net.channel.OliveroMatic.RankingWorker;
import net.sf.odinms.net.channel.OliveroMatic.TaoRankingInfo;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.constants.Rates;

/**
 *
 * @author Owner
 */
public class CharInfoProcessor {

    public static String getNinjaGlare(MapleCharacter other) {
        if (other != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("--------------- Player Name : " + other.getName()
                    + " (CharacterId :" + other.getId() + ")"
                    + " Created on : " + other.getCreateDate());
            if (!other.isJounin()) {
                sb.append("Rebirth Ranking: "
                        + "Overall Rank : #" + other.getRank()
                        + GameConstants.getCardinal(other.getRank())
                        + " Rebirths : " + other.getReborns());
                sb.append("Wealth Ranking: #"
                        + "Overall Rank : #" + other.getTaorank()
                        + GameConstants.getCardinal(other.getTaorank())
                        + " Tao Amount : " + other.getTaoOfSight());
            }
            sb.append("MapleStory Job: "
                    + GameConstants.getJobName(other.getJob().getId())
                    + " ... Clan: " + other.getClan().getName());
            if (other.getPreviousNames() != null) {
                if (other.getPreviousNames().length() < 3) {
                    sb.append(other.getName() + " has also been known as..." + other.getPreviousNames());
                }
            }
            sb.append(" Str : " + other.getStr()
                    + " Dex : " + other.getDex()
                    + " Int : " + other.getInt()
                    + " Luk : " + other.getLuk()
                    + " Remaining AP : "
                    + other.getRemainingAp() + " StorageAp : " + other.getStorageAp());
            sb.append(" TStr : " + other.getStr()
                    + " TDex : " + other.getDex()
                    + " TInt : " + other.getInt()
                    + " TLuk : " + other.getLuk()
                    + " WA : " + other.getTotalWatk());
            sb.append(" MobKilled : " + other.getMobKilled()
                    + " BossKilled : " + other.getBossKilled()
                    + " level : " + other.getLevel()
                    + " Mesos : " + other.getMeso()
                    + " ShurikenItems : " + other.getMaxStatItems());
            sb.append(" PvP Kills : " + other.getPvpKills()
                    + " PvP Deaths : " + other.getPvpDeaths()
                    + " NinjaTensu : " + other.getNinjaTensu());
            sb.append("Rasengan Quest Level : " + other.getRasengan()
                    + "Exp/meso/drop/bossdrop boost :" + other.getExpBoost()
                    + " / " + other.getMesoBoost()
                    + " / " + other.getDropBoost()
                    + " / " + other.getBossDropBoost()
                    + " Total Exp/meso/drop/bossdrop rates : "
                    + + Rates.getExpRate(other)
                    + " / " + Rates.getMesoRate(other)
                    + " / " + Rates.getDropRate(other)
                    + " / " + Rates.getBossDropRate(other));
            sb.append(" This Player is : " + other.getGMStatus().getTitle()
                    + " Player's Legend : " + other.getLegend() + " Player's GMS mode : " + other.getGMSMode());
            if (other.getDAmount() > 0) {
                sb.append("Donated Amount : " + other.getDAmount() + " Dpoints : " + other.getDPoints());
            }
            return sb.toString();
        }
        return "ERROR";
    }

    public static void getNinjaTop10(MessageCallback mc, String name) {
        mc.dropMessage("--------------------------------------Searching Rank for '" + name + "'--------------------------------------");
        RBRankingInfo rank = RankingWorker.getInstance().getRBRanksByName().get(name);
        if (rank != null) {
            int tocheck = rank.getRank();
            for (int i = tocheck; i < (tocheck + 20); i++) {
                rank = RankingWorker.getInstance().getRBRanks().get(i);
                if (rank == null) {
                    mc.dropMessage("The Ranking for this person is not available. The Rank you entered is invalid");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append(" Rank #" + rank.getRank());
                sb.append(" Name : " + rank.getName());
                sb.append(" Rb : " + rank.getRB());
                sb.append(" Clan : " + rank.getClan());
                sb.append(" MSI : " + rank.getMSI());
                mc.dropMessage(sb.toString());
                mc.dropMessage("--Rankings Last Updated: " + RankingWorker.getInstance().getLastRBRankingUpdateTime() + " (Updates Every 30 Minutes)--");
            }
        } else {
            mc.dropMessage("The Ranking for this person is not available.");
        }
        return;
    }

    public static void getNinjaTop10(MessageCallback mc, int tocheck) {
        mc.dropMessage("--------------------------------------Searching Ranks " + tocheck + " to " + (tocheck + 19) + "--------------------------------------");
        for (int i = tocheck; i < (tocheck + 20); i++) {
            RBRankingInfo rank = RankingWorker.getInstance().getRBRanks().get(i);
            if (rank == null) {
                mc.dropMessage("The Ranking for this person is not available. The Rank you entered is invalid");
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(" Rank #" + rank.getRank());
            sb.append(" Name : " + rank.getName());
            sb.append(" Rb : " + rank.getRB());
            sb.append(" Clan : " + rank.getClan());
            sb.append(" MSI : " + rank.getMSI());
            mc.dropMessage(sb.toString());
        }
        mc.dropMessage("--Rankings Last Updated: " + RankingWorker.getInstance().getLastRBRankingUpdateTime() + " (Updates Every 30 Minutes)--");
        mc.dropMessage("For individual rankings, type @ninjatop10 <player>.");
        mc.dropMessage("For other rankings (shows 20), type @ninjatop10 <number> (i.e. @ninjatop10 23 shows 23-42nd)");
        mc.dropMessage("As a default, just type @ninjatop10 for top 20.");
    }

    public static void getTaoTop10(MessageCallback mc, String name) {
        mc.dropMessage("--------------------------------------Searching Tao Rank for '" + name + "'--------------------------------------");
        TaoRankingInfo rank = RankingWorker.getInstance().getTaoRanksByName().get(name);
        if (rank != null) {
            int tocheck = rank.getRank();
            for (int i = tocheck; i < (tocheck + 20); i++) {
                rank = RankingWorker.getInstance().getTaoRanks().get(i);
                if (rank == null) {
                    mc.dropMessage("The Tao Ranking for this person is not available. The Rank you entered is invalid");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append(" Tao Rank #" + rank.getRank());
                sb.append(" Name : " + rank.getName());
                sb.append(" Taos : " + rank.getTaoCheck());
                sb.append(" Clan : " + rank.getClan());
                sb.append(" RB : " + rank.getRB());
                mc.dropMessage(sb.toString());
                mc.dropMessage("--Rankings Last Updated: " + RankingWorker.getInstance().getLastTaoRankingUpdateTime() + " (Updates Every 30 Minutes)--");
            }
        } else {
            mc.dropMessage("The Ranking for this person is not available.");
        }
        return;
    }

    public static void getTaoTop10(MessageCallback mc, int tocheck) {
        mc.dropMessage("--------------------------------------Searching Tao Ranks " + tocheck + " to " + (tocheck + 19) + "--------------------------------------");
        for (int i = tocheck; i < (tocheck + 20); i++) {
            TaoRankingInfo rank = RankingWorker.getInstance().getTaoRanks().get(i);
            if (rank == null) {
                mc.dropMessage("The Ranking for this person is not available. The Rank you entered is invalid");
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(" Tao Rank #" + rank.getRank());
            sb.append(" Name : " + rank.getName());
            sb.append(" Taos : " + rank.getTaoCheck());
            sb.append(" Clan : " + rank.getClan());
            sb.append(" RB : " + rank.getRB());
            mc.dropMessage(sb.toString());
        }
        mc.dropMessage("--Rankings Last Updated: " + RankingWorker.getInstance().getLastTaoRankingUpdateTime() + " (Updates Every 30 Minutes)--");
        mc.dropMessage("For individual rankings, type @ninjatop10 <player>.");
        mc.dropMessage("For other rankings (shows 20), type @taotop10 <number> (i.e. @taotop10 23 shows 23-42nd)");
        mc.dropMessage("As a default, just type @ninjatop10 for top 20.");
    }

    public static void getNinjaGlare(MessageCallback mc, MapleCharacter other) {
        if (other != null) {
            int clan = other.getClan().getId();
            mc.dropMessage("--------------- Player Name : " + other.getName()
                    + " (CharacterId :" + other.getId() + ")"
                    + " Created on : " + other.getCreateDate());
            if (!other.isJounin()) {
                mc.dropMessage("Rebirth Ranking: "
                        + "Overall Rank : #" + other.getRank()
                        + GameConstants.getCardinal(other.getRank())
                        + " Rebirths : " + other.getReborns());
                mc.dropMessage("Wealth Ranking: #"
                        + "Overall Rank : #" + other.getTaorank()
                        + GameConstants.getCardinal(other.getTaorank())
                        + " Tao Amount : " + other.getTaoOfSight());
            }
            mc.dropMessage("MapleStory Job: "
                    + GameConstants.getJobName(other.getJob().getId())
                    + " ... Clan: " + other.getClan().getName());
            mc.dropMessage(other.getPreviousNames().length() < 3 ? "This player has no alias." : other.getName()
                    + " has also been known as..." + other.getPreviousNames());
            mc.dropMessage(" Str : " + other.getStr()
                    + " Dex : " + other.getDex()
                    + " Int : " + other.getInt()
                    + " Luk : " + other.getLuk()
                    + " Remaining AP : "
                    + other.getRemainingAp() + " StorageAp : " + other.getStorageAp());
            mc.dropMessage(" TStr : " + other.getStr()
                    + " TDex : " + other.getDex()
                    + " TInt : " + other.getInt()
                    + " TLuk : " + other.getLuk()
                    + " WA : " + other.getTotalWatk());
            mc.dropMessage(" MobKilled : " + other.getMobKilled()
                    + " BossKilled : " + other.getBossKilled()
                    + " level : " + other.getLevel()
                    + " Mesos : " + other.getMeso()
                    + " ShurikenItems : " + other.getMaxStatItems());
            mc.dropMessage(" PvP Kills : " + other.getPvpKills()
                    + " PvP Deaths : " + other.getPvpDeaths()
                    + " NinjaTensu : " + other.getNinjaTensu());
            mc.dropMessage("Rasengan Quest Level : " + other.getRasengan()
                    + "Exp/meso/drop/bossdrop boost :" + other.getExpBoost()
                    + " / " + other.getMesoBoost()
                    + " / " + other.getDropBoost()
                    + " / " + other.getBossDropBoost()
                    + " Total Exp/meso/drop/bossdrop rates : "
                    + + Rates.getExpRate(other)
                    + " / " + Rates.getMesoRate(other)
                    + " / " + Rates.getDropRate(other)
                    + " / " + Rates.getBossDropRate(other));
            mc.dropMessage(" This Player is : " + other.getGMStatus().getTitle()
                    + " Player's Legend : " + other.getLegend() + " Player's GMS mode : " + other.getGMSMode());
            if (other.getDAmount() > 0) {
                mc.dropMessage("Donated Amount : " + other.getDAmount() + " Dpoints : " + other.getDPoints());
            }
        }
    }

    public static void loadAccountDetails(MapleClient c, String IGN) { // !getcharinfo command credits to Oliver the Slut
        c.showMessage(6, "Processing...");
        Connection con = DatabaseConnection.getConnection();
        try {
            int str = 0, dex = 0, int_ = 0, luk = 0, ap = 0, store = 0,
                    damount = 0, dpoint = 0, level = 0, rasengan = 0, tensu = 0;
            String previousnames = "", macs = "", accountcreatedate = "", accountname = "", gm = "";
            int checkmacs = 0;
            c.showMessage(5, "[Step 1/3] Loading Character Information...");
            String sql = "SELECT c.`str`, c.`dex`, c.`luk`, c.`int`,"
                    + " c.`ap`, c.`storageap`, c.`rasengan`, c.`previousnames`,"
                    + " a.`macs`, a.`name`, a.createdat,"
                    + " a.`gm`, a.`damount`, a.`dpoints`,"
                    + " a.`ninjatensu` FROM characters AS c"
                    + " LEFT JOIN accounts AS a ON"
                    + " c.accountid = a.id WHERE c.name = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, IGN);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                str = rs.getInt("str");
                dex = rs.getInt("dex");
                luk = rs.getInt("luk");
                int_ = rs.getInt("int");
                ap = rs.getInt("ap");
                store = rs.getInt("storageap");
                rasengan = rs.getByte("rasengan");
                previousnames = rs.getString("previousnames");
                if (previousnames == null) {
                    previousnames = "N/A";
                }
                macs = rs.getString("macs");
                accountname = rs.getString("name");
                accountcreatedate = rs.getString("createdat");
                gm = Status.getByLevel(rs.getInt("gm")).getTitle();
                damount = rs.getInt("damount");
                dpoint = rs.getInt("dpoints");
                tensu = rs.getInt("ninjatensu");
            }
            rs.close();
            ps.close();
            c.showMessage(5, "[Step 2/3] Loading mac Information...");
            ps = con.prepareStatement("SELECT COUNT(*) FROM accounts WHERE macs = ? LIMIT 10");
            ps.setString(1, macs);
            rs = ps.executeQuery();
            while (rs.next()) {
                checkmacs = rs.getInt(1);
            }
            rs.close();
            ps.close();
            c.showMessage(5, "[Step 3/3] Printing Results...");
            c.showMessage(6, "Information of: " + IGN + ":" + "(Acc Creation Date: " + accountcreatedate + ")");

            c.showMessage(6, previousnames.length() < 4 ? "This player has no alias." : IGN + " has also been known as..." + previousnames);
            c.showMessage(6, "Level : " + level + "; Str : " + str + "; Dex : " + dex + "; Int : " + int_ + "; Luk : " + luk);
            c.showMessage(6, "RemainingAp : " + ap + " StorageAp : " + store + "; rasengan : " + rasengan);
            c.showMessage(6, "Account Name: " + accountname + "; Mac Address(es): " + macs + "; Account Created: " + accountcreatedate);
            RBRankingInfo rbrank = RankingWorker.getInstance().getRBRanksByName().get(IGN);
            TaoRankingInfo taorank = RankingWorker.getInstance().getTaoRanksByName().get(IGN);
            if (rbrank != null) {
                c.showMessage(6, "Rebirth Ranking: "
                        + "Overall Rank : #" + rbrank.getRank()
                        + GameConstants.getCardinal(rbrank.getRank())
                        + " Rebirths : " + rbrank.getRB() + " MSI : " + rbrank.getMSI());
            }
            if (taorank != null) {
                c.showMessage(6, "Wealth Ranking: #"
                        + "Overall Rank : #" + taorank.getRank()
                        + GameConstants.getCardinal(taorank.getRank())
                        + " Tao Amount : " + taorank.getTaoCheck());
            }
            c.showMessage(6, "dPoint : " + dpoint + "; damount : " + damount + "; Tensu : " + tensu + "; GM status : " + gm);
            if (checkmacs > 0 && !macs.equalsIgnoreCase("")) {
                ps = con.prepareStatement("SELECT name FROM characters WHERE accountid in (SELECT id FROM accounts WHERE macs = ?) LIMIT 20");
                ps.setString(1, macs);
                rs = ps.executeQuery();
                while (rs.next()) {
                    c.showMessage(5, "This player has a MAC same to another user(s). The IGN is: " + rs.getString(1));
                }
                rs.close();
                ps.close();
            }
            c.showMessage(5, "-----------------------------Command Complete------------------------------");
        } catch (SQLException e) {
            c.showMessage(5, "Error: " + e);
        }
        // search similarities
    }

    public static void seeReason(MapleClient c, String name) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        int accountid = 0;
        try {
            ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
            }
            accountid = rs.getInt("accountid");
            ps.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e);
            c.showMessage(5, "[SQL Checker] Character not found");
        }
        String banString;
        try {
            ps = con.prepareStatement("SELECT banreason FROM accounts WHERE id = " + accountid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
            }
            banString = rs.getString("banreason");
            c.showMessage(5, "[SQL Checker] " + banString);
            ps.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e);
            c.showMessage(5, "[SQL Checker] Character not found");
        }
    }
}
