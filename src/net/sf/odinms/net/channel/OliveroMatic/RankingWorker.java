/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.OliveroMatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.TimerManager;

/**
 *
 * @author Oliver
 */
public class RankingWorker {

    private static Map<Integer, RBRankingInfo> rbRankings = new LinkedHashMap<Integer, RBRankingInfo>();
    private static Map<String, RBRankingInfo> rbRankingsByName = new LinkedHashMap<String, RBRankingInfo>();
    private static Map<Integer, TaoRankingInfo> taoRankings = new LinkedHashMap<Integer, TaoRankingInfo>();
    private static Map<String, TaoRankingInfo> taoRankingsByName = new LinkedHashMap<String, TaoRankingInfo>();
    private static long lastRBRanking = -1;
    private static long lastTaoRanking = -1;
    private static RankingWorker instance = new RankingWorker();

    public static RankingWorker getInstance() {
        if (instance == null) {
            instance = new RankingWorker();
        }
        return instance;
    }

    public void start() {
        updateRankings();
        updateTaoRankings();
        schedule();
    }

    public void schedule() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                start();
            }
        }, 15 * 60 * 1000); // 15 min once
    }

    private void updateTaoRankings() {
        if (lastRBRanking > 0) {
            if (taoRankings != null) {
                taoRankings.clear();
            }
            if (taoRankingsByName != null) {
                taoRankingsByName.clear();
            }
        }
        Connection con = DatabaseConnection.getConnection();
        lastTaoRanking = System.currentTimeMillis();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT c.name, c.taorank, c.clantaorank, c.reborns, c.taocheck, a.clan AS clan FROM characters AS c LEFT JOIN accounts AS a ON c.accountid = a.id WHERE c.reborns > 10 AND c.taocheck > 10 AND a.gm < 3 AND a.banned < 1 ORDER BY c.taorank ASC");
            rs = ps.executeQuery();
            System.out.println("Tao ranking Query executed");
            while (rs.next()) {
                int clan = rs.getByte("clan");
                int rank = rs.getInt("taorank");
                int clanrank = rs.getInt("clantaorank");
                TaoRankingInfo taorankk = new TaoRankingInfo(
                        rs.getString("name"),
                        clan,
                        rs.getInt("reborns"),
                        rs.getInt("taocheck"),
                        rank,
                        clanrank);
                taoRankings.put(rs.getRow(), taorankk);
                taoRankingsByName.put(rs.getString("name"), taorankk);               
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateRankings() {
        if (lastRBRanking > 0) {
            if (rbRankings != null) {
                rbRankings.clear();
            }
            if (rbRankingsByName != null) {
                rbRankingsByName.clear();
            }
        }
        Connection con = DatabaseConnection.getConnection();
        lastRBRanking = System.currentTimeMillis();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT c.name, c.msi, c.level, c.reborns, c.jobrank, c.rank, a.clan AS clan FROM characters AS c LEFT JOIN accounts AS a ON c.accountid = a.id WHERE c.reborns > 10 AND a.gm < 3 AND a.banned < 1 ORDER BY c.rank ASC");
            rs = ps.executeQuery();
            while (rs.next()) {
                int clan = rs.getByte("clan");
                int rank = rs.getInt("rank");
                int clanrank = rs.getInt("jobrank");
                RBRankingInfo rbrankk = new RBRankingInfo(
                        rs.getString("name"),
                        rs.getByte("clan"),
                        rs.getInt("reborns"),
                        rs.getInt("level"),
                        rs.getInt("msi"),
                        rank,
                        clanrank);
                rbRankings.put(rs.getRow(), rbrankk);
                rbRankingsByName.put(rs.getString("name"), rbrankk);               
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public long getLastRBRankingUpdateTime() {
        return lastRBRanking;
    }

    public long getLastTaoRankingUpdateTime() {
        return lastTaoRanking;
    }

    public Map<Integer, RBRankingInfo> getRBRanks() {
        return rbRankings;
    }

    public Map<String, RBRankingInfo> getRBRanksByName() {
        return rbRankingsByName;
    }

    public Map<Integer, TaoRankingInfo> getTaoRanks() {
        return taoRankings;
    }

    public Map<String, TaoRankingInfo> getTaoRanksByName() {
        return taoRankingsByName;
    }
}
