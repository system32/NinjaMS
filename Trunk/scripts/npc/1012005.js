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
		if (status == 0){
			var text = "I'm cloy the pet loot manager of NinjnaMS";
			text += "\r\n I can help you obtain the items which you need for pet vac\r\n";
			text += "#rChoose What you want : #b";
			text += "\r\n#L1# Binoculars and Wing Boots#l";
			text += "\r\n#L2# Magic Scales#l";
			cm.sendSimple(text);
        } else if (status == 1) {
			if (selection == 1){
				cm.sendNext("I can sell you Wing Boots and Binoculars. They make the Pets Vac Items and mesos. Of course nothing is free in this world. You will have to do a small favour for me for both these items. You have to get me 1000 Pet food, 50 tao of sight, 300 Cursed dolls and 500 Teddy's cotton. What you think? good deal aint it?");
			} else {
				var text = " ";
				if(cm.haveItem(4031679, 1)){
					text += "I see you have the Dragon Jewel.\r\n";
					text += "Now all you have to do is bring me ";
					text += "#e#r10#d different diary page,\r\n\r\n #r1337#d Tao Of Sight,\r\n\r\n #r5#d ninjaTensu \r\n\r\n";
					text += "#r1337#d bain collars - #v4000080#\r\n\r\n";
					text += "#r5#d refined diamonds - #v4021007#\r\n\r\n";
					text += "#r5#d refined opals - #v4021004# \r\n\r\n";
					text += "#r5#d mithril plates - #v4011002# \r\n\r\n";
					text += "#r5#d black crystal - #v4021008#\r\n\r\n";
					text += "#r1337#d strange eggs - #v4000265#\r\n\r\n";
					text += "#r1337#d viking sail - #v4000134#\r\n\r\n";
					text += "#r1337#d Broken piece of Pot - #v4000291#";
					cm.sendNext(text);
					status = 9;
				} else {
					cm.sendOk("You do not have the etc item required to start the quest. Keep hunting and you might as well find it ");
					cm.dispose();
				}
			}
		} else if (status == 2) {
			if(cm.getPlayer().haveSight(50)){
				if(cm.haveItem(2120000, 1000) || cm.getPlayer().isJounin()){
					if (cm.haveItem(4000031, 300) || cm.getPlayer().isJounin()){
						if(cm.haveItem(4000106, 500) || cm.getPlayer().isJounin()) {
							if(cm.checkSpace(1302000, 2)){
								if(!cm.getPlayer().isJounin()) {
									cm.gainItem(4000106, -500);
									cm.gainItem(4000031, -300);
									cm.gainItem(2120000, -1000);
									cm.gainItem(4032016, -50);
								}
								cm.gainTaggedItem(1812004, 1);
								cm.gainTaggedItem(1812005, 1);
								cm.dispose();
							} else {
								cm.sendOk("You do not have enough space. I scam 100 mil mesos for wasting my time");
								cm.gainMeso(-100000000);
								cm.dispose();
							}
						} else { 
							cm.sendOk("You dont seem to have the Teddy's Cotton. You can hunt them from the Teddies in LudiBrium");
							cm.dispose();
						}
					} else {
						cm.sendOk("You dont seem to have the cursed Zombie Dolls. You can hunt them in the swamps near Kerning City from Zombie Lupins");
						cm.dispose();
					}
				} else {
					cm.sendOk("You dont seem to have the Pet food. You can buy them from Miki in Ellinia");
					cm.dispose();
				}
			} else {
				cm.sendOk("You dont seem to have the Mesos now. Good luck Getting them");
				cm.dispose();
			}
		} else if (status == 10){
			if(cm.checkSpace(1812006, 1)){
				if (cm.haveItem(4031679, 1)
					&& cm.checkPages()
					&& (cm.getPlayer().getTaoOfSight() >= 1337)
					&& (cm.getPlayer().getNinjaTensu() >= 5)
					&& cm.haveItem(4000080, 1337)
					&& cm.haveItem(4021007, 5)
					&& cm.haveItem(4021004, 5)
					&& cm.haveItem(4011002, 5)
					&& cm.haveItem(4021008, 5)
					&& cm.haveItem(4000265, 1337)
					&& cm.haveItem(4000134, 1337)
					&& cm.haveItem(4000291, 1337)){			
					cm.gainItem(4031679, -1);
					cm.removePages();
					cm.gainItem(4032016, -1337);
					var tens = cm.getPlayer().getNinjaTensu();
					cm.getPlayer().setNinjaTensu(tens - 5);
					cm.gainItem(4000080, -1337)
					cm.gainItem(4021007, -5);
					cm.gainItem(4021004, -5);
					cm.gainItem(4011002, -5);
					cm.gainItem(4021008, -5);
					cm.gainItem(4000265, -1337);
					cm.gainItem(4000134, -1337);
					cm.gainItem(4000291, -1337);
					cm.gainStatItem(1812006, 1337, 100, 100);		
					cm.sendOk("Have fun with your magic scales");
					cm.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastMessage("system", net.sf.odinms.tools.MaplePacketCreator.serverNotice(6, "[KrystleCruz] Congratulations " + cm.getPlayer().getName() + " on aquiring the legendary magic scales" ).getBytes());					
				} else {
					cm.sendOk("You do not have the herbs and spices.");
					cm.dispose();
				}
			} else {
				cm.sendOk("You do not have enough space. I scam 1 gold for wasting my time");
				cm.getPlayer().reduceGold();
				cm.dispose();
			}
		} else {
			cm.dispose();
		}
	}
}

