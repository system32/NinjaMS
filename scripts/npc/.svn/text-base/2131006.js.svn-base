var status;

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
			var txt = "Hey #h #! I'm Duru. I manage the GMS modes required of PQs";
			if(cm.getPlayer().getGMSMode() < 1){
				txt += "\r\n#rChoose what you want :#b";
				txt += "\r\n#L1# GMS Mode 1 #r(for KPQ)#l#b";
				txt += "\r\n#L2# GMS Mode 2 #r(for LPQ - Unavailable)#l#b";
				txt += "\r\n#L3# GMS Mode 3 #r(for Orbis PQ - Unavailable)#l#b";
				txt += "\r\n#L4# GMS Mode 4 #r(for Zakum)#l#b";
				txt += "\r\n#L5# GMS Mode 5 #r(for HT and Mu Lung Dojo)#l#b";
			} else {
				txt += "\r\n#L10#Cancel GMS Mode#l";
			}			
			cm.sendSimple(txt);
		} else if (status == 1){
			if(selection > 0 && selection < 6) {
				cm.getPlayer().setGMSMode(selection);
				cm.sendOk("You should now be in GMS mode. If you did get a error notice, please try again");
				cm.dispose();
			} else if (selection == 10){
				cm.getPlayer().cancelGMSMode();
			} else {
				cm.voteMSG();
				cm.dispose();
			}
		} else {
			cm.voteMSG();
			cm.dispose();
		}
	}
}
	