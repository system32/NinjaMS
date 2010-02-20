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

var ores1 = new Array(4010000, //bronze ore
4010001, //steel
4010002, //mithril
4010003, //adamantium
4010004, //silver
4010005, //orihalcon
4010006, //gold
4010007 //lidium
);

var ores2 = new Array(4020000, //garnet
4020001, //amethyst
4020002, //aquamarine
4020003, //emerald
4020004, //opal
4020005, //sapphire
4020006, //topaz
4020007, //diamond
4020008 //black cryst
);

var ores3 = new Array (4004000, // power
4004001, //wisdom
4004002, //dex
4004003, //luk
4004004 //dark
);

function start() {
    status = -1;
    action(1, 0, 0);
}

function scam(){
	cm.sendOk("You have been Scammed.");
}

function gainOres(){
	chance1 = Math.floor(Math.random() * 3);
	var ores;
	switch(chance1){
		case 0:
			ores = ores1;
			break;
		case 1:
			ores = ores2;
			break;
		case 2:
			ores = ores3;
			break;
		default:
			ores = ores1;
	}
	chance2 = Math.floor(Math.random() * 10);
	chance3 = Math.floor(Math.random() * 10);
	var lol = ((chance2 + chance3 + 2)/2);
	var chance4 = Math.floor(Math.random() * ores.length);
	cm.gainItem(ores[chance4], lol);
	cm.sendOk("You have gained : " +lol+ " #v"+ores[chance4]+"#");

}

function giveTao(){
	chance1 = Math.random() * 5;
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
	var text = "Hey #h #! I'm the Ores Gachapon on NinjaMS .Wanna Try your luck?";
		text += "\r\n #rYou need to have 10 Gachapon ticket. #v"+gachatix+"# ";
		text += "You can get them from idolly in the other end of henesys\r\n";
		text += "#e#bYou can also get scammed. So be careful.#n"
		cm.sendNext(text);
    } else if (status == 1) {
		if(!cm.haveItem(gachatix, 10)) {
			cm.sendOk("Bring me 10 Gacha ticket I'll let you check your Luck");
			cm.dispose();
		} else if (!cm.checkSpace(4006000, 1) || !cm.checkSpace(tao, 1)) {
			cm.sendOk("Seems like you dont have Space in inventory");
			cm.dispose();
		} else {
			cm.gainItem(gachatix, -10);
			chance1 = Math.random() * 900;
			if (chance1 < 400){			
				gainOres();
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