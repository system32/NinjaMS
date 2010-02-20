/* Author: Xterminator
	NPC Name: 		Sera
	Map(s): 		Maple Road : Entrance - Mushroom Town Training Camp (0), Maple Road: Upper level of the Training Camp (1)
	Description: 		First NPC
*/
importPackage(net.sf.odinms.client);

var status = 0;
var yes = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (cm.getChar().getMapId() == 0 ) {
		if (mode == -1) {
			cm.dispose();
		} else {
		if (status == -1 && mode == 0) {
			cm.sendNext("Please talk to me again when you finally made your decision.");
			cm.dispose();
			return;
		} else if (status >= 0 && mode == 0) {
			yes = 1;
			cm.sendYesNo("Do you really want to start your journey right away?");
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			if (!cm.getJob().equals(MapleJob.BEGINNER)) {
				status = 3;
				cm.sendNext("You don't belong here.");
			} else {
				if (yes == 1) {
					status = 2;
					cm.sendNext("It seems like you want to start your journey without taking the training program. Then, I will let you move on to the training ground. Be careful~");
				} else {
					cm.sendYesNo("Welcome to the world of MapleStory. The purpose of this training camp is to help beginners. Would you like to enter this training camp? Some people start their journey without taking the training program. But I strongly recommend you take the training program first.");
				}
			}
		} else if (status == 1) {
				cm.sendNext("Ok then, I will let you enter the training camp. Please follow your instructor's lead.");
		} else if (status == 2) {
			var statup = new java.util.ArrayList();
			var p = cm.c.getPlayer();
			var totAp = p.getRemainingAp() + p.getStr() + p.getDex() + p.getInt() + p.getLuk();		
			p.setStr(4);
			p.setDex(4);
			p.setInt(4);
			p.setLuk(4);
			p.setRemainingAp (totAp - 16);
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(4)));
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
			p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
			cm.warp(1, 0);
			cm.dispose();
		} else if (status == 3) {
			var statup = new java.util.ArrayList();
			var p = cm.c.getPlayer();
			var totAp = p.getRemainingAp() + p.getStr() + p.getDex() + p.getInt() + p.getLuk();		
			p.setStr(4);
			p.setDex(4);
			p.setInt(4);
			p.setLuk(4);
			p.setRemainingAp (totAp - 16);
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(4)));
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
			statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
			p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
			cm.warp(40000, 0);
			cm.dispose();
		} else if (status == 4) {
			cm.warp(100000000, 0);
			cm.dispose();
			}
		}
	} else {
		if (mode == -1) {
			cm.dispose();
		} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("This is the image room where your first training program begins. In this room, you will have an advance look into the job of your choice.");
		} else if (status == 1) {
			cm.sendPrev("Once you train hard enough, you will be entitled to occupy a job. You can become a Bowman in Henesys, a Magician in Ellinia, a Warrior in Perion, and a Thief in Kerning City...");
		} else if (status == 2) {
			cm.dispose();
			}
		}
	}
}