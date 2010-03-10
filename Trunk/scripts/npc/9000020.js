/**
 * @Name:  Spinel -- Not GMS-like
 * @NPC:   9000020
 * @Author System
 * @credits : Got it from ThePack and changed the map ids. ]
 */

var bossmaps = Array(100000005, 105070002, 105090900, 230040420, 211042300, 220080001, 240020402, 240020101, 801040100, 240060200, 610010005, 610010012, 610010013, 610010100, 610010101, 610010102, 610010103, 610010104);
var monstermaps = Array(683000110, 100040001, 101010100, 104040000, 103000101, 103000105, 101030110, 106000002, 101030103, 101040001, 101040003, 101030001, 104010001, 105070001, 105090300, 105040306, 230020000, 230010400, 211041400, 222010000, 220080000, 220070301, 220070201, 220050300, 220010500, 250020000, 251010000, 200040000, 200010301, 240020100, 240040500, 240040000, 600020300, 801040004, 800020130, 800020400);
var townmaps = Array(300000000, 261000000, 130000000, 610030010, 211042400, 1010000, 680000000, 230000000, 101000000, 211000000, 100000000, 251000000, 103000000, 222000000, 104000000, 240000000, 220000000, 250000000, 800000000, 600000000, 221000000, 200000000, 102000000, 801000000, 105040300, 610010004, 260000000, 540010000, 120000000, 270000000, 600000001);
var chosenMap = -1;
var monsters = 0;
var towns = 0;
var bosses = 0;
var status = 0;

importPackage(net.sf.odinms.client);

function start() {
    cm.sendSimple("#fUI/UIWindow.img/QuestIcon/3/0#\r\n#L0#World Tour#l\r\n#L1#pass#l");
}

function action(mode, type, selection) {
    if (mode < 1)
        cm.dispose();
    else {
        status++;
        if (status == 1) {
            if (selection == 0)
                cm.sendSimple("#fUI/UIWindow.img/QuestIcon/3/0#\r\n#L0#Towns#l\r\n#L1#MonsterMaps#l\r\n#L2#BossMaps#l\r\n#L3#Ninja Leaf City Mega Mall#l\r\n");
            else {
                cm.dispose();
            }
        } else if (status == 2) {
            if (selection == 0) {
                var selStr = "Select your destination.#b";
                for (var i = 0; i < townmaps.length; i++)
                    selStr += "\r\n#L" + i + "##m" + townmaps[i] + "#";
                cm.sendSimple(selStr);
                towns++;
            } else if (selection == 1) {
                var selStr2 = "Select your destination.#b";
                for (var  j = 0; j < monstermaps.length; j++)
                    selStr2 += "\r\n#L" + j + "##m" + monstermaps[j] + "#";
                cm.sendSimple(selStr2);
                monsters++;
            } else if (selection == 2) {
                var selStr3 = "Select your destination.#b";
                for (var k = 0; k < bossmaps.length; k++)
                    selStr3 += "\r\n#L" + k + "##m" + bossmaps[k] + "#";
                cm.sendSimple(selStr3);
                bosses++;
            } else if (selection == 3){
		cm.warp(600000001);
		cm.dispose();
	    }
        } else if (status == 3) {
            if (towns == 1) {
                cm.sendYesNo("Do you want to go to #m" + townmaps[selection] + "#?");
                chosenMap = selection;
                towns = 2;
            } else if (monsters == 1) {
                cm.sendYesNo("Do you want to go to #m" + monstermaps[selection] + "#?");
                chosenMap = selection;
                monsters = 2;
            } else if (bosses == 1) {
                cm.sendYesNo("Do you want to go to #m" + bossmaps[selection] + "#?");
                chosenMap = selection;
                bosses = 2;
            }
        } else if (status == 4) {
            if (towns == 2)
                cm.warp(townmaps[chosenMap], 0);
            else if (monsters == 2)
                cm.warp(monstermaps[chosenMap], 0);
            else if (bosses == 2)
                cm.warp(bossmaps[chosenMap], 0);
            cm.dispose();
        }

    }
}
