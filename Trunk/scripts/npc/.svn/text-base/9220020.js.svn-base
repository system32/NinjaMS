var status = 0;
var taoReward = 10;

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
			cm.sendNext("Hello #b#h #! #k I give rewards for people who vote for this server. You can claim rewards for voting from me .");
		} else if (status == 1){
			taoReward = Math.floor(((cm.getPlayer().getReborns() + 10) /10) + 10);
			var text = "You happen to have #b" + cm.getPlayer().getNinjaTensu();
			text += " Ninja Tensu #k Choose what you want to do with it.";
			text += "Keep in mind that 100 ninjapoints can get You donator status :).";
			text += "Yes you are correct. \r\n\r\n#r";
			text += "Our Hokage decided not only $ should decide the previledges in the server.";
			text += "So if you are a dedicated player and have 100 ninjatensu, "
			text += "you can talk to Hokage to get Donator Status #k.";
			text += "Now please choose what you want: ";
			text += "#b\r\n\r\n#L1# Get " + taoReward + " Tao of Sight - cost : 1 ninjatensu#l";
			text += "\r\n#L2# Get 25 onyx Apples - cost 1 ninjatensu#l";
			text += "\r\n#L3# Get a BrownPuppy, meso magnet and item pouch  - cost : 1 ninjatensu #l";
			text += "\r\n#L4# Get 50k Nx - cost 1 NinjaTensu #l";
			text += "\r\n#L5# Get 500 Ap - Cost 1 NinjaTensu#l"; 
			text += "\r\n#L6# Get a patriotMedal - 5 NinjaTensu#l";
			text += " \r\n";
			cm.claimVoteRewards();
			cm.sendSimple(text);
			status = 9;
		} else if (status == 10){
			if (cm.getPlayer().getNinjaTensu() >= 1){
				if(selection == 1){
					cm.getPlayer().reduceNinjaTensu();
					cm.gainItem(4032016, taoReward);
					cm.sendOk("Here is your Tao. Have fun and Keep Voting :)");
					cm.dispose();
				} else if (selection == 2){
					cm.getPlayer().reduceNinjaTensu();
					cm.gainItem(2022179, 25);
					cm.sendOk("Here is your Onyx Apples. Have fun and Keep Voting :)");
					cm.dispose();
				} else if (selection == 3){
					cm.getPlayer().reduceNinjaTensu();
					cm.gainItem(5000001, 1);
					cm.gainItem(1812000, 1);
					cm.gainItem(1812001, 1);
					cm.sendOk("Here is your Pet. Have fun and Keep Voting :)");
					cm.dispose();
				} else if (selection == 4){
					cm.getPlayer().reduceNinjaTensu();
					cm.gainNX(50000);
					cm.sendOk("You have gaines 50000 nx. Have fun and Keep Voting :)");
					cm.dispose();
				} else if (selection == 5){
					cm.getPlayer().reduceNinjaTensu();
					cm.gainAP(500);
					cm.sendOk("You have Gained 500 AP Have fun and Keep Voting :)");
					cm.dispose();
				} else if (selection == 6){
					if (cm.getPlayer().getNinjaTensu() >= 5){
						cm.getPlayer().reduceNinjaTensu();
						cm.getPlayer().reduceNinjaTensu();
						cm.getPlayer().reduceNinjaTensu();
						cm.getPlayer().reduceNinjaTensu();
						cm.getPlayer().reduceNinjaTensu();
						cm.gainStatItem(1142075, 250, 1, 1); // patriot medal
						cm.sendOk("Have fun and Keep Voting :)");
						cm.dispose();
					} else {
						cm.sendOk("I should scam you for tring to cheat me. but nvm. I'm not that mean");
						cm.dispose();
					}
				}				

			} else {
				cm.sendOk("I should scam you for tring to cheat me. but nvm. I'm not that mean");
				cm.dispose();
			}
		} else {
			cm.voteMSG();
			cm.dispose(); 
		}
	}
}

