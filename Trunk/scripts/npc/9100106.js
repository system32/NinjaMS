/**

 * @NPC: 9100105 - Level Gacha

 * @Location: Henesys

 * @Function: Gacha

 * @author: System

 * @credits : iDolly, Oliver

**/



var status = 0;
var chance1;
var chance2;
var chance3;
var chance4;
var tao = 4032016;
var gachatix = 5220000;
// morphs
var items1 = new Array(2210000, 2210001, 2210002, 2210003, 2210005, 2210006, 2210007, 2210008, 2210010, 2210011, 2210012, 2210016, 2210017, 2210018, 2210021, 2210032, 2210033);
// mounts
var items2  = new Array(1902000, 1902001, 1902002, 1902005, 1902006, 1902008, 1902009, 1902011, 1902012);
// saddles
var items3 = new Array(1912000, 1912003, 1912004, 1912005, 1912008, 1912007);
// Timeless and Reverse
var items4 = new Array(1452019,1472053, 1462015, 1332051, 1312030, 1322045, 1302056, 1442044, 1432030,
            1382035, 1412021, 1422027, 1402037, 1372010, 1302026, 1102041, 1302081,
            1312037,1322060,1402046,1412033,1422037,1442063,1472023,1332073,1332074,
            1372044,1382057,1432047,1462050,1472068,1492023,1302086,1312038,1322061,
            1332075,1332076,1372045,1372059,1402047,1412034,1422038,1432049,1442067,
            1452059,1462051,1472071,1482024,1492025); 
function start() {
    status = -1;
    action(1, 0, 0);
}

function scam(){
	cm.sendOk("You have been Scammed.");
}

function gainItem(){
	chance1 = Math.floor(Math.random() * 4);
	var item;
	switch(chance1){
		case 0:
			item = items1;
			break;
		case 1:
			item = items2;
			break;
		case 2:
			item = items3;
			break;			
		case 3:
			item = items4;
			break;
		default :
			item = items1;
	}
	chance2 = Math.floor(Math.random() * item.length);
	cm.gainItem(item[chance2], 1);
	cm.sendOk("You have Gained an Item : #v"+item[chance2]+"#");

}
function giveTao(){
	chance1 = Math.random() * 10;
	chance4 = Math.floor(chance1 + 1);
	cm.gainItem(tao, chance4);
	cm.sendOk("You have Gained Tao");
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
	if (status == 0) {
	var text = "Hey #h #! I'm the Super special item Gachapon on NinjaMS .Wanna Try your luck?";
		text += "\r\n #rYou need to have 3 Gachapon ticket. #v"+gachatix+"# ";
		text += "You can get them from idolly in the other end of henesys\r\n";
		text += "#e#bYou can also get scammed. So be careful.#n"
		cm.sendNext(text);
    } else if (status == 1) {
		if(!cm.haveItem(gachatix, 3)) {
			cm.sendOk("Bring me 3 Gacha ticket I'll let you check your Luck");
			cm.dispose();
		} else if (!cm.checkSpace(1302000, 1) || !cm.checkSpace(2210000, 1) || !cm.checkSpace(tao, 1)) {
			cm.sendOk("Seems like you dont have Space in inventory");
			cm.dispose();
		} else {
			cm.gainItem(gachatix, -3);
			chance1 = Math.random() * 900;
			if (chance1 < 400){			
				gainItem();
			} else if (chance1 < 800){
				scam();
			} else {
				giveTao();
			}		
			cm.dispose();
		}
	} else {
		cm.voteMSG();
		cm.dispose();
	}

}