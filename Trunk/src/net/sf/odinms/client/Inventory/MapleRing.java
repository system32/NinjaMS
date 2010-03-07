/*
KryptoDEV Maplestory Source Coded in Java Part of KryptoDEV Community

Copyright (C) 2009 KryptoDEV

(xcheater3161) Robert Carpenter <rjctlc3@hotmail.com>

This program is free software. You may not however, redistribute it and/or
modify it without the sole, written consent of KryptoDEV Team.

This program is distributed in the hope that it will be useful to those of
the KryptoDEV Community, and those who have consent to redistribute this.

Upon reading this, you agree to follow and maintain the mutual balance
between the Author and the Community at hand.


 */
package net.sf.odinms.client.Inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.database.DatabaseConnection;

import net.sf.odinms.server.MapleInventoryManipulator;

public class MapleRing implements Comparable<MapleRing> {

    private int ringId;
    private int ringId2;
    private int partnerId;
    private int itemId;
    private String partnerName;
    private boolean equipped;

    public MapleRing(int id, int id2, int partnerId, int itemid, String partnername) {
        this.ringId = id;
        this.ringId2 = id2;
        this.partnerId = partnerId;
        this.itemId = itemid;
        this.partnerName = partnername;
    }

    public static MapleRing loadFromDb(int ringId) {
        try {
            Connection con = DatabaseConnection.getConnection(); // Get a connection to the database
            PreparedStatement ps = con.prepareStatement("SELECT * FROM rings WHERE id = ?"); // Get ring details..
            ps.setInt(1, ringId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            MapleRing ret = new MapleRing(ringId, rs.getInt("partnerRingId"), rs.getInt("partnerChrId"), rs.getInt("itemid"), rs.getString("partnerName"));
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static int createRing(int itemid, final MapleCharacter partner1, final MapleCharacter partner2, String message) {

        if (partner1 == null) {
            System.err.println("Partner Number 1 is not on the same channel.");
            return -2; //
        } else if (partner2 == null) {
            System.err.println("Partner Number 2 is not on the same channel.");
            return -1; // Partner Number 2 is not on the same channel.
        }
        int[] ringID = new int[2];
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("INSERT INTO rings (itemid, partnerChrId, partnername) VALUES (?, ?, ?)");
            ps.setInt(1, itemid);
            ps.setInt(2, partner2.getId());
            ps.setString(3, partner2.getName());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            ringID[0] = rs.getInt(1);
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        try {
            ps = con.prepareStatement("INSERT INTO rings (itemid, partnerRingId, partnerChrId, partnername) VALUES (?, ?, ?, ?)");
            ps.setInt(1, itemid);
            ps.setInt(2, ringID[0]);
            ps.setInt(3, partner1.getId());
            ps.setString(4, partner1.getName());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            ringID[1] = rs.getInt(1);
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        try {
            ps = con.prepareStatement("UPDATE rings SET partnerRingId = ? WHERE id = ?");
            ps.setInt(1, ringID[1]);
            ps.setInt(2, ringID[0]);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        MapleInventoryManipulator.addRing(partner1, itemid, ringID[0]);
        MapleInventoryManipulator.addRing(partner2, itemid, ringID[1]);
        partner1.addRingToCache(ringID[0]);
        partner2.addRingToCache(ringID[1]);
        partner2.relog();
        partner1.relog();
        return 1;
    }

    public int getRingId() {
        return ringId;
    }

    public int getPartnerRingId() {
        return ringId2;
    }

    public int getPartnerChrId() {
        return partnerId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MapleRing) {
            if (((MapleRing) o).getRingId() == getRingId()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.ringId;
        return hash;
    }

    @Override
    public int compareTo(MapleRing other) {
        if (ringId < other.getRingId()) {
            return -1;
        } else if (ringId == other.getRingId()) {
            return 0;
        } else {
            return 1;
        }
    }
}
