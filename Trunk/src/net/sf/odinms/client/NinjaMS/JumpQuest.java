/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS;

import net.sf.odinms.client.*;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Angy
 */
public class JumpQuest {

    public static long lastjqcomplete = 0;
    public static long lastjqcompare = 0;
    public static long startjqtime = 0;

    public static void checkTimes(MapleClient c, int currentjqmap) {
        int[] jqmaps = {280020000, 280020001, 109040001, 109040002, 109040003, 109040004, 103000906, 103000907, 103000908, 101000100, 101000101, 101000102, 101000103, 101000104, 105040310, 105040311, 105040312, 105040313, 105040314, 105040315, 105040316};
        int[] completiontimes = {80, 85, 70, 60, 145, 85, 70, 30, 60, 60, 80, 100, 50, 60, 30, 80, 90, 70, 70, 50, 90};
        for (int i = 0; i < jqmaps.length; i++) {
            if (currentjqmap == jqmaps[i]) {
                lastjqcompare = System.currentTimeMillis() - lastjqcomplete;
                if (lastjqcompare < completiontimes[i]) {
                    //AutobanManager.getInstance().autoban(c, "Abnormal Completion of JQ. Finished Jq map " + jqmaps[i] + "in " + lastjqcompare);
                    WorldChannelInterface wci = c.getChannelServer().getWorldInterface();
                    try {
                        wci.broadcastGMMessage(null, MaplePacketCreator.serverNotice(5, "Abnormal Completion Of Jq map " + jqmaps[i] + " in " + lastjqcompare).getBytes());
                    } catch (Exception ex) {
                    }
                } else {
                    lastjqcomplete = System.currentTimeMillis();
                }
                break;
            } else {
                continue;
            }
        }
    }

  

    public static void warpOut(MapleCharacter player) {
        player.changeMap(100000000);
    }

    public static void startJq(MapleClient c, int wtf) {
        int[] target = {105040310, 105040312, 105040314, 103000900, 103000903, 103000906, 101000100, 101000102, 109040001, 280020000};
        c.getPlayer().changeMap(target[wtf - 1]);
        c.getPlayer().dropMessage("Warping to map : " + target[wtf - 1] + " to start JQ");
        c.getPlayer().dropMessage("Please Vote for us at http://vote.ninjams.info. and DON'T hack :)");
        startjqtime = System.currentTimeMillis();
    }

    public static long startJQTime() {
        return startjqtime;
    }

    public static void bonusReward(MapleClient c) {
        MapleCharacter pl = c.getPlayer();
        int[] pages = {4001064, 4001065, 4001066, 4001067, 4001068, 4001069, 4001070, 4001071, 4001072, 4001073};
        int i = 0;
        for (i = 0; i < pages.length; i++) {
            if (!pl.haveItem(pages[i], 1, false, true)) {
                c.showMessage("You don't seem to have all the items :). Get them first ");
                break;
            }
        }
        i = 0;
        for (i = 0; i < pages.length; i++) {
            MapleInventoryManipulator.removeById(c, MapleItemInformationProvider.getInstance().getInventoryType(pages[i]), pages[i], 1, true, false);
        }
        int type = (int) Math.floor(Math.random() * 5200 + 1);
        if (type < 500) {
            c.showMessage("how does it feel to be scammed? xD");
        } else if (type >= 500 && type < 1000) {
            c.showMessage("You have gained 1 bil mesos");
            pl.gainMeso(1000000000, true);
        } else if (type >= 1000 && type < 1500) {
            int nxxx = (int) Math.floor(Math.random() * type + 20000);
            pl.modifyCSPoints(1, nxxx);
            c.showMessage("You have gained " + nxxx + " NX");
        } else if (type >= 1500 && type < 2500) {
            int[] scrolls = {2040603, 2044503, 2041024, 2041025, 2044703, 2044603, 2043303, 2040807, 2040806, 2040006, 2040007, 2043103, 2043203, 2043003, 2040506, 2044403, 2040903, 2040709, 2040710, 2040711, 2044303, 2043803, 2040403, 2044103, 2044203, 2044003, 2043703, 2041200, 2049100, 2049000, 2049001, 2049002, 2049003};
            i = (int) Math.floor(Math.random() * scrolls.length);
            MapleInventoryManipulator.addById(c, scrolls[i], (short) 1);
            c.showMessage("You have gained a scroll (Gm scroll / Chaos scroll / Clean slate scroll)");
        } else if (type >= 2500 && type < 3000) {
            int[] rareness = {1302081, 1312037, 1322060, 1402046, 1412033, 1422037, 1442063, 1482023, 1372035, 1372036, 1372037, 1372038, 1372039, 1372040, 1372041, 1372042, 1382045, 1382046, 1382047, 1382048, 1382049, 1382050, 1382051, 1382052, 1382060, 1442068, 1452060};
            c.showMessage("You have gained a super Rare Weapon");
            i = (int) Math.floor(Math.random() * rareness.length);
            MapleInventoryManipulator.addById(c, rareness[i], (short) 1);
        } else if (type >= 3000 && type < 3500) {
            c.showMessage("You have gained 200 power elixirs");
            MapleInventoryManipulator.addById(c, 2000005, (short) 200);
        } else if (type >= 3500 && type < 4000) {
            c.showMessage("You have gained 200 elixirs");
            MapleInventoryManipulator.addById(c, 2000004, (short) 200);
        } else if (type >= 4000 && type < 4250) {
            c.showMessage("You have gained 20 heart stoppers");
            MapleInventoryManipulator.addById(c, 2022245, (short) 20);
        } else if (type >= 4250 && type < 4500) {
            c.showMessage("You have gained 10 Onyx Apples");
            MapleInventoryManipulator.addById(c, 2022179, (short) 10);
        } else if (type >= 4500 && type < 4750) {
            c.showMessage("You have gained 5 demon elixirs");
            MapleInventoryManipulator.addById(c, 2022282, (short) 5);
        } else if (type >= 4750 && type < 5000) {
            pl.gainAp(2500);
            c.showMessage("You have gained 2500 Ap");
        } else if (type > 5200) {
            c.showMessage("You have gained a max stat nub item :)");
            int[] items = {1052081, 1002562, 1072005, 1012011, 1122007};
            i = (int) Math.floor(Math.random() * items.length);
            MapleInventoryManipulator.addStatItemById(c, items[i], c.getPlayer().getName(),(short) 32763,(short) 1,(short) 1);
            c.getPlayer().addMaxStatItem();
            c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[The Elite NinjaGang] Congratulations " + c.getPlayer().getName()+ " On getting a max stat item from JQ bonus"));
        }
    }
}
