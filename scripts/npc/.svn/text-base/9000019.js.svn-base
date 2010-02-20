/* [NPC]
	JobAdvancer Rock, Paper, Scissor Admin
 */
var status = 0;
var job = 0;
var newJob = 0;
var jobs = Array(0, 112, 122, 132, 212, 222, 232, 312, 322, 412, 422, 512, 522, 1111, 1211, 1311, 1411, 1511);
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
			if (cm.getPlayer().getPath() == 1 && cm.getPlayer().getReborns() < 3 && cm.getPlayer().getJobId() == 0){
				var fuck1 = "Hello #h #. I'm Rock, Paper, Scissors Admin  - The Job Manager of the Ninja World. You can choose Any job you want. You can choose to change your path to Cygnus Knight after 3 Rebirths by talking to Shadrion the Clan and path manager of NinjaMS. \r\n#rNow choose your Job: #b\r\n#L1# Hero#l\r\n#L2# Paladin#l\r\n#L3# Dark Knight#l\r\n#L4# Fire ArchMage#l\r\n#L5# Ice ArchMage#l\r\n#L6# Bishop#l\r\n#L7# BowMaster#l\r\n#L8# MarksMan#l\r\n#L9# NightLord#l\r\n#L10# Shadower#l\r\n#L11# Buccaneer#l\r\n#L12# Corsair#l";
				cm.sendSimple(fuck1);
				status = 99;
			} else if (cm.getPlayer().getPath() == 1 && cm.getPlayer().getReborns() >= 3){
				var fuck1 = "Hello #h #. I'm Rock, Paper, Scissors Admin  - The Job Manager of the Ninja World. You can choose Any job you want. You can choose to change your path to Cygnus Knight after 3 Rebirths by talking to Shadrion the Clan and path manager of NinjaMS. \r\n#rNow choose your Job: #b\r\n#L1# Hero#l\r\n#L2# Paladin#l\r\n#L3# Dark Knight#l\r\n#L4# Fire ArchMage#l\r\n#L5# Ice ArchMage#l\r\n#L6# Bishop#l\r\n#L7# BowMaster#l\r\n#L8# MarksMan#l\r\n#L9# NightLord#l\r\n#L10# Shadower#l\r\n#L11# Buccaneer#l\r\n#L12# Corsair#l";
				cm.sendSimple(fuck1);
				status = 99;
			} else if (cm.getPlayer().getPath() == 2){
				var fuck2 = "Hello #h h. I'm Rock, Paper, Scissors Admin  - The Job Manager of the Ninja World. You can choose Any job you want. If You want to change your path talk to Shadrion the Clan and path manager of NinjaMS.#r Now please choose your Job : #b\r\n#L13# Dawn Warrior 3#l\r\n#L14# Blaze Wizard 3#l\r\n#L15# Wind Archer 3 #l\r\n#L16# Night Walker 3#l\r\n#L17# Thunder Breaker 3#l";
				cm.sendSimple(fuck2);
				status = 99;
			} else {
				cm.sendOk("Hi there :)! How you doing? ");
				cm.dispose();
			}
		} else if (status == 100) {
			cm.getPlayer().cancelAllBuffs();			
			cm.changeJobById(jobs[selection]);
			cm.getPlayer().maxSkills(true);
			cm.voteMSG();
			cm.dispose();
		}
	}
}
				
				
		/*		
			}
			job = cm.getPlayer().getJobId();
			if (job > 910){
				if(job % 100 == 0){
					newjob = job + 11;
				} else if (job % 10 == 0) {
					newjob = job + 1;
				}
				fuck1 = " I have changed your job Have fun now :)";
				cm.sendOk(fuck1);
				cm.dispose();
			} else if (job > 0 && job < 900){
				if (job % 10 == 0){
					newjob = job + 2;
				} else if (job % 10 == 1) {
					newjob = job + 1;
				}
				fuck1 = " I have changed your job Have fun now :)";
				cm.sendOk(fuck1);
				cm.dispose();
			} else {
				fuck1 = "The job advancement in NinjaMS is designed specially for you. You can be an adventurer or Cygnus Knight. Choice is Yours. Please choose what you want to be : \r\n #b#L0# Adventurer#l\r\n#L1# CygnusKnight#l";
				cm.sendSimple(fuck1);
			}
			
} else if (status == 1){
			cm.getPlayer().cancelAllBuffs();			
			cm.changeJobById(jobs[selection]);
			cm.getPlayer().maxSkills(true);
			cm.voteMSG();
			cm.dispose();
		} else {
			cm.voteMSG();
			cm.dispose();
		}
	}
}*/
