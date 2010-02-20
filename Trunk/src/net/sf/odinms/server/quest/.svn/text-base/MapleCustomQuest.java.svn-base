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
package net.sf.odinms.server.quest;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.database.DatabaseConnection;

/**
 *
 * @author Matze
 */
public class MapleCustomQuest extends MapleQuest {

    public MapleCustomQuest(int id) {
        try {
            this.id = id;
            startActs = new LinkedList<MapleQuestAction>();
            completeActs = new LinkedList<MapleQuestAction>();
            startReqs = new LinkedList<MapleQuestRequirement>();
            completeReqs = new LinkedList<MapleQuestRequirement>();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM questrequirements WHERE " +
                    "questid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            MapleQuestRequirement req;
            MapleCustomQuestData data;
            while (rs.next()) {
                Blob blob = rs.getBlob("data");
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                        blob.getBytes(1, (int) blob.length())));
                data = (MapleCustomQuestData) ois.readObject();
                req = new MapleQuestRequirement(this,
                        MapleQuestRequirementType.getByWZName(data.getName()), data);
                MapleQuestStatus.Status status = MapleQuestStatus.Status.getById(
                        rs.getInt("status"));
                if (status.equals(MapleQuestStatus.Status.NOT_STARTED)) {
                    startReqs.add(req);
                } else if (status.equals(MapleQuestStatus.Status.STARTED)) {
                    completeReqs.add(req);
                }
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM questactions WHERE questid = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            MapleQuestAction act;
            while (rs.next()) {
                Blob blob = rs.getBlob("data");
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                        blob.getBytes(1, (int) blob.length())));
                data = (MapleCustomQuestData) ois.readObject();
                act = new MapleQuestAction(MapleQuestActionType.getByWZName(data.getName()), data, this);
                MapleQuestStatus.Status status = MapleQuestStatus.Status.getById(
                        rs.getInt("status"));
                if (status.equals(MapleQuestStatus.Status.NOT_STARTED)) {
                    startActs.add(act);
                } else if (status.equals(MapleQuestStatus.Status.STARTED)) {
                    completeActs.add(act);
                }
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            log.error("Error loading custom quest.", ex);
        }
    }
}
