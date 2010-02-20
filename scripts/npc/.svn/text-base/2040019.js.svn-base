var status = 0;

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
				cm.sendSimple("Hey there #h #. I'm the SexChanger Of NinjaMS. Dont be scared by my Machine looks. Things dont go wrong any more. It was a long back :). I do this for a small fee though. Just 2.5 mil mesos :) #b\r\n#L0# Give me Boobs#l\r\n#L1# Give me Penis#l\r\n#L2# Make me a Trans#l\r\n#L3# Nah I pass#l");
		} else if (status == 1) {
			if (selection == 0) {
				if (cm.getPlayer().getMeso() >= 2500000){				
					if(cm.getPlayer().getGender() == 0) {
						cm.gainMeso(-2500000);		
						cm.getPlayer().setGender(1);
						cm.sendOk("Boobs it is :)");
						cm.dispose();
					} else {					
						cm.sendOk("You already have saggy boobs");
						cm.dispose();
					}
				} else {
					cm.sendOk("haha you hobo. find me when you have the money :p");
					cm.dispose();
				}
			} else if (selection == 1) {
				if (cm.getPlayer().getMeso() >= 2500000){				
					if(cm.getPlayer().getGender() == 1) {
						cm.gainMeso(-2500000);		
						cm.getPlayer().setGender(0);
						cm.sendOk("Penis it is :)");
						cm.dispose();
					} else {					
						cm.sendOk("You already have tiny tictac");
						cm.dispose();
					}
				} else {
					cm.sendOk("haha you hobo. find me when you have the money :p");
					cm.dispose();
				}
			} else if (selection == 2) {
				if (cm.getPlayer().isGenin()){				
					if(cm.getPlayer().getGender() != 2) {
						cm.gainMeso(-2500000);		
						cm.getPlayer().setGender(2);
						cm.sendOk("Haha you are now a happy Shemale :)");
						cm.dispose();
					} else {					
						cm.sendOk("You already have boobs and penis");
						cm.dispose();
					}
				} else {
					cm.sendOk("haha you hobo. find me when are a donator. Transexual is exclusive Gender:p");
					cm.dispose();
				}
			} else {
				cm.sendOk("haha ok you nib shit. Fucking Grats for wasting my time. bye now");
				cm.dispose();
			}
		} else {		
			cm.voteMSG();
			cm.dispose();
		}
	}
}
