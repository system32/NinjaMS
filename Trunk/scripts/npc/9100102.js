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

var pages = new Array (4001064, 4001065, 4001066, 4001067, 4001068, 4001069, 4001070, 4001071, 4001072, 4001073);

function start() {
    status = -1;
    action(1, 0, 0);
}

function scam(){
	cm.sendOk("You have been Scammed.");
}

function gainPages(){
	chance1 = Math.floor(Math.random() * pages.length);
	cm.gainItem(pages[chance1], 1);
	cm.sendOk("You have gained a diary page");

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
	var text = "Hey #h #! I'm the Diary Pages Gachapon on NinjaMS .Wanna Try your luck?";
		text += "\r\n #rYou need to have 10 Gachapon ticket. #v"+gachatix+"# ";
		text += "You can get them from idolly in the other end of henesys\r\n";
		text += " You also should have 25 Tao of Sight. & 25 Thanatos Strap . :p \r\n";
		text += "\r\n #dYou can get Thanatos Strap from Thanatos in Ludibrium\r\n";
		text += "#e#bYou can also get scammed. So be careful.#n"
		cm.sendNext(text);
    } else if (status == 1) {
		if(!cm.haveItem(gachatix, 10) || !cm.haveItem(tao, 25) || !cm.haveItem(4000152, 25)) {
			cm.sendOk("Bring me 10 Gacha ticket and 25 tao, I'll let you check your Luck");
			cm.dispose();
		} else if (!cm.checkSpace(4006000, 1) || !cm.checkSpace(tao, 1)) {
			cm.sendOk("Seems like you dont have Space in inventory");
			cm.dispose();
		} else {
			cm.gainItem(gachatix, -1);
			cm.gainItem(tao, -25);
			cm.gainItem(4000152, -25);
			chance1 = Math.random() * 1000;
			if (chance1 < 200){			
				gainPages();
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