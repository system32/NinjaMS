/* [NPC]
	The Bass - Donation NPC
	Location - Henesys;
 */

var status = 0; 
var dpoint; 
var damount;
var itemid; 
var newname;
var chairs = Array (3010000,3010001,3010002,3010003,3010004,3010005,3010006,3010007,3010008,3010009,3010010,3010011, 
    3010012,3010013,3010014,3010015,3010016,3010017,3010018,3010019,3010022,3010023,3010024,3010025,3010026, 
    3010028,3010040,3010041,3010045,3010046,3010047,3010057,3010058,3010072,3011000); 

function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
		cm.voteMSG();
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
			dpoint = cm.getPlayer().getDPoints();
			damount = cm.getPlayer().getDAmount();
			var fuck1 = "Hello #b#h ##k. I'm #rThe Bass#k. I donated over9000$ to this server back in the days";
				fuck1 += "Now I'm a npc to help other donators who support this awesome server.";
				fuck1 += "These are the stuff I can do for you.";
				fuck1 += "\r\n#dYour total Donated Amount :#e#r";
				fuck1 += " " + damount + " ";
				fuck1 += " #n \r\n #dYour Current DPoints : #e#r";
				fuck1 += " " + dpoint + " ";
				fuck1 += "#b\r\n#L1# Change Name #r(5 Dpoints)#l#b";
				fuck1 += "#b\r\n#L2# Get all Pet loot EQ #r(5 Dpoints)#l#b";
				fuck1 += "\r\n#L3# Get 100k MaplePoints #r(1 DPoint)#l#b";
				fuck1 += "\r\n#L4# Get 1337 stat 100 WA Item#r(5 Dpoints)#l#b";
				fuck1 += "\r\n#L5# Get a chair #r(2 Dpoints)#l#b"; 
				fuck1 += "\r\n#L6# Get 200 Tao #r(2 DPoints)#l#b";
				fuck1 += "\r\n#L7# Get MaxStatItem #r(50 DPoints)#l#b";
				fuck1 += "\r\n#L8# Get 1000 Ap #r(2 DPoints)#l#b";
				fuck1 += "\r\n#L9# Try your luck with Donator Gacha #r(1 DPoints)#l#b";
				fuck1 += "\r\n#L10# Get Magic Scales #r(50 DPoints)#l#b";
				fuck1 += "\r\n#L11# Warp to Donator Island#l";
				cm.sendSimple(fuck1);
		} else if (status == 1) {
			if (selection == 1){
				cm.sendOk("Please use command #namechange to change name.");
				cm.dispose();
			} else if (selection == 2){
				if (dpoint >= 5 || cm.getPlayer().isJounin()){
					if(cm.checkSpace(1302000, 4)){
						cm.getPlayer().modifyDPoints(-5);
						cm.gainTaggedItem(1812000, 1);
						cm.gainTaggedItem(1812001, 1);
						cm.gainTaggedItem(1812004, 1);
						cm.gainTaggedItem(1812005, 1);
						cm.sendOk("There you go. Have Fun");
						cm.dispose();
					} else {
						cm.sendOk("You do not have enough space in inventory");
						cm.dispose();
					}
				} else {
					cm.sendOk("You do not have enough dPoints.");
					cm.dispose();
				}
			} else if (selection == 3){
				if (dpoint >= 1 || cm.getPlayer().isJounin()){
					cm.getPlayer().modifyDPoints(-1);
					cm.gainNX(20000);
					cm.getPlayer().saveToDB(true);
					cm.sendOk("You have gained Maple Points. Check Cash Shop :)");
				} else {
					cm.sendOk("You do not have enough dPoints.");
					cm.dispose();
				}
			} else if (selection == 4){
				if (dpoint >= 5 || cm.getPlayer().isJounin()) {
					var fuckla = "Enter the ItemId of what you want. If you try to get GM items you will get Scammed :";
					cm.sendGetText(fuckla);
					status = 9;
				} else {
					cm.sendOk("You do not have the herbs and Spices");
					cm.dispose();
				}
			} else if (selection == 5){
				if (dpoint >= 2 || cm.getPlayer().isJounin()){					
					var ok = "Choose Which Chair you want. Every one needs to sit and rest at times #b";
					for (i = 0; i < chairs.length; i++){
						ok += "\r\n#L" + i + "##v" + chairs[i] + "# ~ #t" + chairs[i] + "##l\r\n";
					}
					cm.sendSimple(ok);
					status = 199;
				} else {
					cm.sendOk("You do not have the herbs and Spices");
					cm.dispose();
				}
			} else if (selection == 6){
				if (dpoint >= 2 || cm.getPlayer().isJounin()){
					cm.getPlayer().modifyDPoints(-2);
					cm.gainItem(4032016, 200);
					cm.sendOk("You have gained a gold ");
				} else {
					cm.sendOk("You do not have enough dPoints.");
					cm.dispose();
				}				
			} else if (selection == 7){
				if (dpoint >= 50) {
					var fuckla = "Enter the ItemId of what you want. If you try to get GM items you will get Scammed :";
					cm.sendGetText(fuckla);
					status = 99;
				} else {
					cm.sendOk("You do not have enough dPoints.");
					cm.dispose();
				}	
			} else if (selection == 8) {
				if (dpoint >= 2 || cm.getPlayer().isJounin()) {
					if(cm.getPlayer().getRemainingAp() < 31767){
						cm.getPlayer().modifyDPoints(-2);
						cm.gainAP(1000);
						cm.sendOk("You have gained 1000 AP");
						cm.dispose();
					} else {
						cm.sendOk("You cannot have more than 32767 Stats");
						cm.dispose();
					}
				} else {
					cm.sendOk("You do not have enough dPoints.");
					cm.dispose();
				}	
			} else if (selection == 9) {
				if(dpoint >= 1 || cm.getPlayer().isJounin()){					
					cm.getPlayer().donatorGacha();
					cm.dispose();
				} else {
					cm.sendOk("You dont have enough dPoints");
					cm.dispose();
				}
			} else if (selection == 10){
				if(dpoint >= 50){
					if(cm.getPlayer().checkSpace(1812006)){
						cm.getPlayer().modifyDPoints(-50);
						cm.gainItem(1812006, 1);
						cm.sendOk("Have fun with your magic scales");
						cm.dispose();
					} else {
						cm.sendNext("You don't seem to have enough inventory slots");
						cm.dispose();
					}
				}
			} else if (selection == 11){
				if (cm.getPlayer().isGenin()){
					cm.warp(925100700);
				} else {
					cm.sendOk("You are not Genin");
					cm.dispose();
				}
			}
		} else if (status == 10){
			itemid = cm.getText();
			itemid = Math.floor(itemid);
            itemid = Math.round(itemid);
			var fucklar1 = "Please confirm if you want this Item to be 1337 by pressing Next.\r\n The Item You entered is";
			cm.sendNext(fucklar1 + " #v" +itemid+ "# - #t" +itemid+ "#");
		} else if (status == 11) {
			if (dpoint >= 5 || cm.getPlayer().isJounin()){
				cm.getPlayer().modifyDPoints(-5);
				cm.gainStatItem(itemid, 1337, 100, 100);
				cm.sendOk("There you go. Have Fun");
			} else {
				cm.sendOk("You do not have enough dPoints.");
				cm.dispose();
			}
		}else if (status == 100){
			itemid = cm.getText();
			itemid = Math.floor(itemid);
            itemid = Math.round(itemid);
			var fucklar1 = "Please confirm if you want this Item to be max stat by pressing Next.\r\n The Item You entered is";
			cm.sendNext(fucklar1 + " #v" +itemid+ "# - #t" +itemid+ "#");
		} else if (status == 101) {
			if (dpoint >= 50 || cm.getPlayer().isJounin()){
				cm.getPlayer().modifyDPoints(-50);
				cm.gainStatItem(itemid, 32767, 100, 100);
				cm.sendOk("There you go. Have Fun");
			} else {
				cm.sendOk("You do not have enough dPoints.");
				cm.dispose();
			}
		} else if (status == 200){
			cm.getPlayer().modifyDPoints(-2);
			cm.gainItem(chairs[selection], 1);
            cm.sendOk("Have fun with your new chair :)");
            cm.dispose();
		} else {
			cm.voteMSG();
			cm.dispose();
		}
	}
}
				
				
		