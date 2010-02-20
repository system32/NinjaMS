var status = 0;



function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
  	cm.dispose();
   	cm.voteMSG();
  	cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			cm.voteMSG();
			cm.dispose();
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
		var simple = "Hello #h #. I'm fiona the nifty ninja of the NinjaMS. Welcome to our ninja World. I manage the Jump Quest for NinjaMS. So what you want to do? #b\r\n#L1#JumpQuest#l\r\n#L2#Duck out#l#k";
			if (cm.getPlayer().getMapId() == 100000000){
				cm.sendSimple(simple);
			} else {
				cm.sendYesNo("Grats on finishing the JQ. Do you want to get your reward?");
				status = 100;
			}
		} else if (status == 1){
			if (selection == 1){
				if (cm.getPlayer().getReborns() < 10){
					cm.sendOk("Jq is available only for those who have passed 10 Rebirths. Head to Room 13/ 14 to RB faster. It takes barely 15 minutes");
					cm.dispose();
				} else {
					cm.sendSimple("So you are interested in JQ? We have Autobans and GM alerts in JQ. So don't bother Hacking. Jq is only available for those above 10 Rebirths and only in channel 3. Select what you want to do #b\r\n#L0#Start a JQ#l\r\n#L1#Claim rewards#l\r\n#k");
					status = 200;
				}
			} else if (selection == 2) {
				cm.sendOk("Ok bye. I shall kill you for disturbing me");
				cm.getPlayer().kill();
				cm.dispose();				
			}
		} else if (status == 101){
			if (cm.getC().getChannel() == 3) {
				cm.jqComplete();
				cm.dispose();  
			} else {
				cm.sendOk("trying to cheat eh? ");
				cm.warp(100000000);
				cm.dispose();
			}
		} else if (status == 201) {
			if (selection == 0) {
				if (cm.getC().getChannel() == 3){
					var fuck = "Choose which Jq you want to start";
					for (i = 0; i < 10; i++){
						fuck += "#b\r\n#L" + i + "# Jump Quest Number : #r" + (i + 1) + ". #k";
					}
					cm.sendSimple(fuck);
					status = 210;
				} else {
					cm.sendOk("JumpQuest is only available in channel 3");
				}
			} else if (selection == 1) {
				cm.sendSimple("Here are the bonus Rewards: #b\r\n#L0# Gamble with 10 Dairy Pages for a bonus prize #r[prizes include Max stat items]#l#b\r\n#L1#Get Stat Bonus for 10 Dairy Pages #r[1500 stats for 10 pages]#l#b\r\n#L2#Super Yellow Snow Shoes 13337 stats each #r[ you need 10 full sets of dairy pages]#l#k");
				status = 220;
			}
		} else if (status == 211){
			if (cm.getC().getChannel() == 3) {
				var omg = (selection + 1);
				cm.startJQ(omg);
				cm.dispose();
			} else {
				cm.sendOk("Only in channel 3");
				cm.dispose();
			}
		} else if (status == 221) {
			if (selection == 0) {
				cm.jqBonus();
				cm.dispose();
			} else if (selection == 1) {
				cm.apBonus();
				cm.dipose();
			} else if (selection == 2) {				
				cm.superYellowSnowShoes();
				cm.dispose();
			}
		} else {
			cm.dispose();
		}
	}
}
