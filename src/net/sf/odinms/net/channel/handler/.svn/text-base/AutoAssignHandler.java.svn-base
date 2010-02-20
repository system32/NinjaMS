/*
MyMaple Maplestory Source Coded in Java
Copyright (C) 2008-2009 MyMaple
(johnlth93) Johnny Lee <johnlth93@live.com>

This program is free software. You may not however, redistribute it and/or
modify it without the sole, written consent of MyMaple Team.

This program is distributed in the hope that it will be useful to those of
the MyMaple Community, and those who have consent to redistribute this.

Upon reading this, you agree to follow and maintain the mutual balance
between the Author and the Community at hand.
 */
package net.sf.odinms.net.channel.handler;

import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.client.Enums.MapleStat;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Anujan
 */
public class AutoAssignHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        List<Pair<MapleStat, Integer>> statupdate = new ArrayList<Pair<MapleStat, Integer>>();
        slea.readInt();
        slea.readInt();
        if (c.getPlayer().getRemainingAp() > 0) {
            while (slea.available() > 0) {
                int update = slea.readInt();
                int add = slea.readInt();
                if (c.getPlayer().getRemainingAp() < add) {
                    return;
                }
                switch (update) {
                    case 64: // Str

                        if (c.getPlayer().getStr() + add >= 32767) {
                            c.showMessage("You cannot add more than 32767 stats");
                            return;
                        }
                        c.getPlayer().setStr(c.getPlayer().getStr() + add);
                        statupdate.add(new Pair<MapleStat, Integer>(MapleStat.STR, c.getPlayer().getStr()));
                        break;
                    case 128: // Dex

                        if (c.getPlayer().getDex() + add >= 32767) {
                            c.showMessage("You cannot add more than 32767 stats");
                            return;
                        }
                        c.getPlayer().setDex(c.getPlayer().getDex() + add);
                        statupdate.add(new Pair<MapleStat, Integer>(MapleStat.DEX, c.getPlayer().getDex()));
                        break;
                    case 256: // Int

                        if (c.getPlayer().getInt() + add >= 32767) {
                            c.showMessage("You cannot add more than 32767 stats");
                            return;
                        }
                        c.getPlayer().setInt(c.getPlayer().getInt() + add);
                        statupdate.add(new Pair<MapleStat, Integer>(MapleStat.INT, c.getPlayer().getInt()));
                        break;
                    case 512: // Luk

                        if (c.getPlayer().getLuk() + add >= 32767) {
                            c.showMessage("You cannot add more than 32767 stats");
                            return;
                        }
                        c.getPlayer().setLuk(c.getPlayer().getLuk() + add);
                        statupdate.add(new Pair<MapleStat, Integer>(MapleStat.LUK, c.getPlayer().getLuk()));
                        break;
                    default:
                        c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true));
                        return;
                }
                c.getPlayer().setRemainingAp(c.getPlayer().getRemainingAp() - add);
            }
            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp()));
            c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true));
        } else {
            System.out.printf("[h4x] Player %s is distributing AP with no AP", c.getPlayer().getName());
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }
}
