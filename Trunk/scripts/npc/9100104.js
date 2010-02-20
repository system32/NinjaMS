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

var special = new Array (3010000, 3010001, 3010002, 3010003, 3010004, 3010005, 3010006, 
3010007, 3010008, 3010009, 3010010, 3010011, 3010012, 3010013, 3010014, 3010015,
 3010016, 3010017, 3010018, 3010019, 3010022, 3010023, 3010024, 3010025, 3010026, 
 3010028, 3011000, 3010040, 3010041, 3010045, 3010046, 3010047, 3010072, 3010058, 
 3010057);

function start() {
    status = -1;
    action(1, 0, 0);
}

function scam(){
	cm.sendOk("You have been Scammed.");
}

function gainChair(){
	chance1 = Math.floor(Math.random() * special.length);
	cm.gainExpiringItem(special[chance1], 10080);
	cm.sendOk("You have gained a Chair "+ special[chance1]+" which will last for a week");

}

function giveTao(){
	chance1 = Math.random() * 3;
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
	var text = "Hey #h #! I'm the Chairs Gachapon on NinjaMS .Wanna Try your luck?";
		text += "\r\n #rYou need to have 1 Gachapon ticket. #v"+gachatix+"# ";
		text += "You can get them from idolly in the other end of henesys\r\n";
		text += "#e#bYou can also get scammed. So be careful.#n"
		cm.sendNext(text);
    } else if (status == 1) {
		if(!cm.haveItem(gachatix, 1)) {
			cm.sendOk("Bring me 1 Gacha ticket I'll let you check your Luck");
			cm.dispose();
		} else if (!cm.checkSpace(3010041, 1) || !cm.checkSpace(tao, 1)) {
			cm.sendOk("Seems like you dont have Space in inventory");
			cm.dispose();
		} else {
			cm.gainItem(gachatix, -1);
			chance1 = Math.random() * 900;
			if (chance1 < 400){			
				gainChair();
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