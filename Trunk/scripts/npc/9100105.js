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
var scrolls1 = new Array(2040103, // face accessory hp
			2040108, // face accessory avoid
			2040203, // eye accessory acc
			2040208, // eye accessory int
            2040013, // helmet int
            2040015, // helmet acc
            2040307, // earring dex
            2044705, // claw att
            2044505, // bow att
            2043305, // dagger att
            2044605, // crossbow att
            2040407, // topwear str
            2040411, // topwear luk
            2040907, // shield luk
            2041035, // cape str
            2041037, // cape int
            2041039, // cape dex
            2041041, // cape luk
            2043105, // one-handed axe att
            2044105, // two-handed axe att
            2043205, // one-handed bw att
            2044205, // two-handed bw att
            2043005, // one-handed sword att
            2044005, // two-handed sword att
            2044405, // pole arm att
            2044305, // spear att
            2043805, // staff matt
            2043705, // wand matt
            2040715, // shoes jump
            2040509, // overall dex
            2040519, // overall int
            2040521, // overall luk
            2040811, // gloves att
            2040815, // gloves matt
            2040305, // earring int
            2040917, // shield att
            2040922, // shield matt
            2043007 // sword matt
			);		

// 40% scrolls

var scrolls2 = new Array(
            2040315, // earring int
            2040912, // shield def
            2040313, // one-handed sword att
            2043108, // one-handed axe att
            2043208, // one-handed bw att
            2043308, // dagger att
            2043708, // wand matt
            2043808, // staff matt
            2044008, // two-handed sword att
            2044108, // two-handed axe att
            2044208, // two-handed bw att
            2044308, // spear att
            2044408, // pole arm att
            2044508, // bow att
            2044608, // crossbow att
            2044708 // claw att
            );			
			
// special scrolls
var scrolls3 = new Array(2049000,2049001,2049002,2049003,2340000,2049100);
// GM scrolls
var scrolls4 = new Array(2044503, 2044703, 2044603, 2043303, 2044303, 2044403, 2043803, 2043703, 2043003, 2044003, 2043203, 2044203, 2043103, 2044103, 2040506, 2040709, 2040710, 2040711, 2040303, 2040807, 2040806);


function start() {
    status = -1;
    action(1, 0, 0);
}

function scam(){
	cm.sendOk("You have been Scammed.");
}

function gainScrolls(){
	chance1 = Math.floor(Math.random() * 5);
	var scroll;
	switch(chance1){
		case 0:
			scroll = scrolls1;
			break;
		case 1:
			scroll = scrolls2;
			break;
		case 2:
			scroll = scrolls3;
			break;			
		case 3:
			scroll = scrolls4;
			break;
		default :
			scroll = scrolls1;
	}
	chance2 = Math.floor(Math.random() * scroll.length);
	cm.gainItem(scroll[chance2], 1);
	cm.sendOk("You have Gained Scrolls");
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
	var text = "Hey #h #! I'm the Scrolls Gachapon on NinjaMS .Wanna Try your luck?";
		text += "\r\n #rYou need to have 1 Gachapon ticket. #v"+gachatix+"# ";
		text += "You can get them from idolly in the other end of henesys\r\n";
		text += "#e#bYou can also get scammed. So be careful.#n"
		cm.sendNext(text);
    } else if (status == 1) {
		if(!cm.haveItem(gachatix, 1)) {
			cm.sendOk("Bring me 1 Gacha ticket I'll let you check your Luck");
			cm.dispose();
		} else if (!cm.checkSpace(2210000, 1) || !cm.checkSpace(tao, 1)) {
			cm.sendOk("Seems like you dont have Space in inventory");
			cm.dispose();
		} else {
			cm.gainItem(gachatix, -1);
			chance1 = Math.random() * 900;
			if (chance1 < 400){			
				gainScrolls();
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