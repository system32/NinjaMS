var chairs = Array (3010000,3010001,3010002,3010003,3010004,3010005,3010006,3010007,3010008,3010009,3010010,3010011, 
    3010012,3010013,3010014,3010015,3010016,3010017,3010018,3010019,3010022,3010023,3010024,3010025,3010026, 
    3010028,3010040,3010041,3010045,3010046,3010047,3010057,3010058,3010072,3011000); 


var mounts = Array(1902000,1902001,1902002,1912000,1902008,1902009,1902011,1902012,1912003,1912004,1912007,1912008,1902005,1902006,1912005); 


var morphs = Array(2210000, 2210001, 2210002, 2210003, 2210005, 2210006, 2210007, 2210008, 2210010, 2210011, 2210012, 2210016, 2210017, 2210018, 2210021, 
    2210032, 2210033); 

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
            var test = " Hello #b#h #,#k. I r so lonely.";
            test += " I was waiting for a ninja to come find me. I love ninjas. ";
            test += " I wish I could be one too, but as euu can see, me is too fat to be a ninja.";
            test += " Its all because I have too many chairs. I sit on them all day and get Fat.";
            test += " I also have to many mounts and morphs. I r now too fat to sit on one.";
            test += " So I want to sell them off too. I can also teach your naruto specials Kage Bunshin or Rasengan jutsu.";
            test += " Worry Not. I r a good teacher. I was considered as the only match to the Pervert Sannin Jiraya.";
            test += " Well that was a long time ago when I was not this fat and old \r\n #rChoose What you want: #k ";
            test += " \r\n\r\n#L1# Learn the Awesome Kage Bunshin#l ";
            test += "\r\n#L2# Learn the Legendary Rasengan#l#k";
            cm.sendSimple(test);			
        } else if (status == 1) {
            if (selection == 1){
                cm.sendNextPrev("Kage Bunshin is a secret Jutsu which Naruto learned when he was a kid. It takes a lot of effort to learn it. I cannot teach it to every other retard. The Genins and above get to learn it from the Hokage himself. But for the Rookie ninjas, I'm the only way. I'll only teach you if you prove yourself worth of having such an awesome power. Are you ready to take my tests and prove yourself? ");
                status = 39;
            } else if (selection == 2){
                cm.sendNextPrev("Rasengan is a secret Jutsu which Naruto learned from jiraya. It takes a lot of effort and concentration and patience to learn it. I cannot teach it to every other retard. I'll only teach you if you prove yourself worth of having such an awesome power. Are you ready to take my tests and prove yourself?");
                status = 49;
            } else {
                cm.voteMSG();
                cm.dispose();
            }
        } else if (status == 40){
            if(cm.haveItem(5010044, 1) && cm.haveItem(4000008, 5000) && (cm.getPlayer().haveSight(50)) && (cm.getPlayer().getAllowedClones() < 10)) {
                cm.gainItem(5010044, -1);
                cm.gainItem(4000008, -5000);
                cm.gainItem(4032016, -50);
                cm.getPlayer().setCloneLimit(10);
                cm.getPlayer().saveToDb(true);
                cm.sendOk("You now have the ability to use #b@kagebunshin#. Your clone limit is now 10. I might in future teach you how to increase the limit and use your chakra more efficiently. Have fun :)");
                cm.dispose();
            } else {
                cm.sendOk("You have to bring me a Shadowstyle effect which you can get for 8k nx each from the Cash shop and 5000 undead charms which looks like this #v4000008# \r\n #rThe Charms can be hunted from the Zombie mushrooms. For a strong ninja like yourself. It should be a piece of pie#k\r\n Last but not least 50 Tao Of Sight. find me when you have all of it. ");
                cm.dispose();
            }
        } else if (status == 50){
            if(cm.getPlayer().hasRasengan()){
                cm.getPlayer().changeJobById(900);
                cm.getPlayer().maxGMSkills(true);
                cm.sendOk("If you roam around with GM job you will get banned");
                cm.dispose();
            } else if(cm.getPlayer().getRasengan() == 0){ 
				if (cm.getPlayer().getAllowedClones() >= 10){
					cm.getPlayer().setRasengan(1);
					cm.sendOk("Congratulations. You have passed on to stage 2 of this quest. Talk to me when you are ready to proceed");					
					status = 99;
				} else {
					cm.sendOk("You should finish the Kage bunshin quest in order to proceed. Talk to me when you have it done");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 1){
				if (cm.checkPages()){
					cm.removePages();
					cm.getPlayer().setRasengan(2);
					cm.sendOk("Congratulations. You have passed on to stage 3 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me a set of diary pages to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 2){
				if (cm.getPlayer().haveSight(1337)){
					var i = 0;
					cm.gainItem(4032016, -1337);
					cm.getPlayer().setRasengan(3);
					cm.sendOk("Congratulations. You have passed on to stage 4 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 1337 Tao Of Sight to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 3){
				var tens = cm.getPlayer().getNinjaTensu();
				if (tens >= 15){
					cm.getPlayer().setNinjaTensu(tens - 15);
					cm.getPlayer().setRasengan(4);
					cm.sendOk("Congratulations. You have passed on to stage 5 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 15 ninja tensu to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 4){
				if (cm.haveItem(4000000, 1337)){
					cm.gainItem(4000000, -1337);
					cm.getPlayer().setRasengan(5);
					cm.sendOk("Congratulations. You have passed on to stage 6 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 1337 #t4000000# - #v4000000# to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 5){
				if (cm.haveItem(4000019, 1337)){
					cm.gainItem(4000019, -1337);
					cm.getPlayer().setRasengan(6);
					cm.sendOk("Congratulations. You have passed on to stage 7 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 1337 #t4000019# - #v4000019# to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 6){
				if (cm.haveItem(4000016, 1337)){
					cm.gainItem(4000016, -1337);
					cm.getPlayer().setRasengan(7);
					cm.sendOk("Congratulations. You have passed on to stage 8 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 1337 #t4000016# - #v4000016# to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 7){
				if (cm.haveItem(4031449, 500)){
					cm.gainItem(4031449, -500);
					cm.getPlayer().setRasengan(8);
					cm.sendOk("Congratulations. You have passed on to stage 9 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 500 #t4031449# - #v4031449# to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 8){
				if (cm.haveItem(4005000, 5)){
					cm.gainItem(4005000, -5);
					cm.getPlayer().setRasengan(9);
					cm.sendOk("Congratulations. You have passed on to stage 10 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 5 #t4005000# - #v4005000# to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 9){
				if (cm.haveItem(4005001, 5)){
					cm.gainItem(4005001, -5);
					cm.getPlayer().setRasengan(10);
					cm.sendOk("Congratulations. You have passed on to stage 11 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 5 #t4005001# - #v4005001# to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 10){
				if (cm.haveItem(4005002, 5)){
					cm.gainItem(4005002, -5);
					cm.getPlayer().setRasengan(11);
					cm.sendOk("Congratulations. You have passed on to stage 12 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 5 #t4005002# - #v4005002# to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 11){
				if (cm.haveItem(4005003, 5)){
					cm.gainItem(4005003, -5);
					cm.getPlayer().setRasengan(12);
					cm.sendOk("Congratulations. You have passed on to stage 13 of this quest. Talk to me when you are ready to proceed");
					status = 99;
				} else {
					cm.sendNext("Please bring me 5 #t4005003# - #v4005003# to advance to the next stage");
					cm.dispose();
				}
			} else if(cm.getPlayer().getRasengan() == 12){
				if (cm.haveItem(4005004, 5)){
					cm.gainItem(4005004, -5);
					cm.getPlayer().setRasengan(69);
					cm.sendOk("Congratulations. You now have the ability to use #bRasengan#. You can change your Job to Gm to get the Rasengan when ever you talk to me in the future.\r\n #rYou are not allowed to have GM job anytime other than for taking the skill off it to add in your Keyboard.#k\r\n)");
					status = 99;
					cm.getPlayer().getClient().getChannelServer().getWorldInterface().broadcastMessage("system", net.sf.odinms.tools.MaplePacketCreator.serverNotice(6, "[KrystleCruz] Congratulations " + cm.getPlayer().getName() + " on successful completion of Rasengan Quest" ).getBytes());
					cm.getPlayer().maxGMSkills(true);					
					cm.getPlayer().changeJobById(900);
					
				} else {
					cm.sendNext("Please bring me 5 #t4005004# - #v4005004# to advance to the next stage");
					cm.dispose();
				}
            }
        } else {
            cm.voteMSG();
            cm.dispose();
        } 
    }
}

