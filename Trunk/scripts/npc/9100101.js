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
	var ap = cm.getPlayer().getRemainingAp();
	chance1 = Math.random() * ap;
	chance3 = Math.random() * ap;	
	var app = Math.floor((chance1 + chance3) / 2);
	cm.gainAP(-app);
	cm.sendOk("You have been Scammed. you lost "+app+"Ap");
}

function gainAp(){
	var ap = cm.getPlayer().getRemainingAp();	
	chance1 = Math.random() * ap;
	chance3 = Math.random() * ap;	
	var app = (chance1 + chance3) / 2;
	var appp = (32767 - ap);
	var newap = Math.floor(Math.min(ap, appp));
	if(cm.getPlayer().getGMSMode() != 0){
		cm.sendOk("You have been scammed " +newap+ " AP because you are in GMS mode");
	} else {
		cm.gainAP(newap);
		cm.sendOk("You have Gained "+newap+" ap");
	}
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
	var text = "Hey #h #! I'm the Ap Gachapon on NinjaMS .Wanna Try your luck?";
		text += "\r\n #rYou need to have a Gachapon ticket. #v"+gachatix+"# ";
		text += "You can get them from idolly in the other end of henesys\r\n";
		text += "#e#bYou can also get scammed. So be careful.#n"
		text += "\r\n#dNote The more Ap you have the more Ap you may win or lose :p"
		cm.sendNext(text);
    } else if (status == 1) {
		if(!cm.haveItem(gachatix, 1)) {
			cm.sendOk("Bring me a Gacha ticket I'll let you check your Luck");
			cm.dispose();
		} else {
			cm.gainItem(gachatix, -1);
			chance1 = Math.random() * 900;
			if (chance1 < 300){			
				gainAp();
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