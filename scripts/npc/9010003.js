var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (cm.getChar().getMapId() != 100000000) {
			cm.sendOk("You can enter NinjaShiken Only From henesys");
			cm.dispose();
			return;
		}
		if (mode == 0) {
			cm.sendOk("Alright, see you next time.");
			cm.dispose();
			return;
		}
		status++;
		if (status == 0) {
			cm.sendNext("I am Ria. I can send you to the #rNinja Shiken#k.");
		} else if (status == 1) {
			cm.sendYesNo("Do you wish to enter #rNinja Shiken#k now?");
		} else if (status == 2) {
			var em = cm.getEventManager("lolcastle");
			if (em == null || !em.getProperty("entryPossible").equals("true")) {
				cm.sendOk("Sorry, but #rNinja Shiken#k is currently closed.");
			} else if (cm.getPlayer().getGMSMode() != 4){
				cm.sendOk("You need to be in GMS mode 4 to be able to enter Ninja Shiken");
			} else {
				em.getInstance("lolcastle5").registerPlayer(cm.getChar());
			}
			cm.dispose();
		}
	}
}
