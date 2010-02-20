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
function start() {
    status = -1;
    action(1, 0, 0);
}

function scam(){
	cm.sendOk("You have been Scammed");
}

function levelUp(){
	chance1 = Math.random() * 25;
	chance2 = Math.random() * 25;
	chance3 = chance1 + chance2 + 1;
	for (i = 0; i < chance3; i++)
		if (cm.getPlayer().getLevel() < 255) {
			cm.getPlayer().levelUp();
		}
	cm.sendOk("You have Gained levels");
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
		cm.sendNext("Hey #h #! I'm the level up Gacha pon on NinjaMS . Wanna Try your luck?\r\n #rYou need to have a Gachapon ticket. You can get them from idolly in the other end of henesys#k");
    } else if (status == 1) {
		if(!cm.haveItem(gachatix, 1)) {
			cm.sendOk("Bring me a Gacha ticket I'll let you check your Luck");
			cm.dispose();
		} else {
			cm.gainItem(gachatix, -1);
			chance1 = Math.random() * 900;
			if (chance1 < 500){			
				levelUp();
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