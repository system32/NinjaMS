package net.sf.odinms.net.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.client.Enums.Clans;

import net.sf.odinms.database.DatabaseConnection;

public class RankingWorker implements Runnable {

    private Connection con;
    private long lastUpdate = System.currentTimeMillis();

    public void run() {
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);
            for (int i = -1; i < Clans.values().length; i++) {
                updateRanking(i);
                updateTaoRanking(i);
            }
            con.commit();
            con.setAutoCommit(true);
            lastUpdate = System.currentTimeMillis();
        } catch (SQLException ex) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                System.err.println("Could not update rankings" + ex);
            } catch (SQLException ex2) {
                System.err.println("Could not rollback unfinished ranking transaction" + ex2);
            }
        }
    }

    private void updateRanking(int clan) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c.id, ");
        sb.append(clan != -1 ? "c.jobRank, c.jobRankMove" : "c.rank, c.rankMove");
        sb.append(", a.lastlogin AS lastlogin, a.loggedin FROM characters AS c LEFT JOIN accounts AS a ON c.accountid = a.id WHERE a.gm < 3 AND a.banned < 1 AND c.reborns > 10 ");
        if (clan != -1) {
            sb.append("AND a.clan = ? ");
        }
        sb.append("ORDER BY c.reborns DESC , c.level DESC , c.exp DESC ");
        PreparedStatement charSelect = con.prepareStatement(sb.toString());
        if (clan != -1) {
            charSelect.setInt(1, clan);
        }
        ResultSet rs = charSelect.executeQuery();
        PreparedStatement ps = con.prepareStatement("UPDATE characters SET " + (clan != -1 ? "jobRank = ?, jobRankMove = ? " : "rank = ?, rankMove = ? ") + "WHERE id = ?");
        int rank = 0;
        while (rs.next()) {
            int rankMove = 0;
            rank++;
            if (rs.getLong("lastlogin") < lastUpdate || rs.getInt("loggedin") > 0) {
                rankMove = rs.getInt((clan != -1 ? "jobRankMove" : "rankMove"));
            }
            rankMove += rs.getInt((clan != -1 ? "jobRank" : "rank")) - rank;
            ps.setInt(1, rank);
            ps.setInt(2, rankMove);
            ps.setInt(3, rs.getInt("id"));
            ps.addBatch();
        }
        ps.executeBatch();
        rs.close();
        charSelect.close();
        ps.close();
    }

    private void updateTaoRanking(int clan) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c.id FROM characters AS c LEFT JOIN accounts AS a ON c.accountid = a.id WHERE a.gm < 3 AND a.banned < 1 AND c.taocheck > 10 ");
        if (clan != -1) {
            sb.append("AND a.clan = ? ");
        }
        sb.append("ORDER BY c.taocheck DESC , c.meso DESC");
        PreparedStatement charSelect = con.prepareStatement(sb.toString());
        if (clan != -1) {
            charSelect.setInt(1, clan);
        }
        ResultSet rs = charSelect.executeQuery();
        PreparedStatement ps = con.prepareStatement("UPDATE characters SET " + (clan != -1 ? "clanTaoRank = ?" : "taorank = ?") + " WHERE id = ?");
        int taorank = 0;
        while (rs.next()) {
            taorank++;
            ps.setInt(1, taorank);
            ps.setInt(2, rs.getInt("id"));
            ps.addBatch();
        }
        ps.executeBatch();
        rs.close();
        charSelect.close();
        ps.close();
    }
}
