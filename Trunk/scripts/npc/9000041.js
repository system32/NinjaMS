// default message
var status = 0;

var names = new Array("Boosters", "Stat Buffs", "Defense Buffs", "Element Charges", "Misc");
var boosters = new Array(1101004, 1101005, 1201005, 1301004, 1301005, 3101002, 3201002, 4101003, 4201002, 2111005, 5101006, 5201003);
var statups = new Array(1005, 1101006, 1111002, 1221000, 1311008, 2201001, 2311003, 3001003, 3121002, 3121008, 4201003, 4111001, 11101003);
var defencebuffs = new Array(1001003, 1101007, 1121002, 1301006, 1301007, 2001002, 2001003, 2301003, 2301004, 2321005, 4211005);
var charges = new Array(1211003, 1211004, 1211005, 1211006, 1211007, 1211008, 1221003, 1221004, 11111007, 15101006);
var souls = new Array(3101004, 4121006, 13101003);
var summons = new Array(2121005, 2221005, 2311006, 2321003, 3111005, 3121006, 3211005, 3221005, 5211001, 5211002, 11001004, 12001004, 13001004, 14001004, 15001004);

var buffs = new Array();
var toadd = 0;
var fuck = 0;
var gay = 0;

var selected = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.sendDefaultMessage();
    } else {
        if (mode == 1) {
            status++;
        } else { // determine below - if it is send default message, it will dispose upon clicking no.
            // if it is status--, it goes back upon no. sophistication includes if (status >= 2 && mode == 0). DIY.
            cm.voteMSG(); cm.dispose();return; // return only if using senddefaultmess
        //status--;
        }

        if (status == 0) {
                if (!cm.p().isGenin()) { cm.dispose(); cm.p().goHome(); return};
                    var selStr = "";
            selStr += "Hello there, #b#h ##k, I am the #rCharity Box#k,";
			selStr += "and I am in charge of the distribution of #eDonator Autobuffs#n.";
			selStr += "Buffs selected here will automatically be activated when yo$";
            selStr += "#r#ePlease choose an appropriate category to add an autobuff to.#k#n\r\n#b";
            for (var i = 0; i < names.length; i++) {
                selStr += "\r\n#L" + i + "#" + names[i] + "#l";
            }
            selStr += "\r\n\r\n#e#rOr alternatively, if you wish to remove an autobuff...#k#n";
            selStr += "\r\n#L100##bRemove an autobuff#k.";
            cm.sendSimple(selStr);
        } else if (status == 1) {
            if (selection == 100) {
                selected = 1;
                buffs = cm.getAutobuffArray();
                var selStr = "#e#bPlease select the buff you wish to remove from below:#k#n";
                for (var i = 0; i < buffs.length; i++) {
                        selStr += "\r\n\r\n#L" + i + "##s" + buffs[i] + "# - #b#e" + cm.getSkillName(buffs[i]) + "#k#n";
                        selStr += "\r\n#eJob of Origin:#n " + cm.getJobName(Math.floor(buffs[i] / 10000));
                        selStr += "#n#l";
                    
                }
                cm.sendSimple(selStr);
            } else {

                if (cm.p().getAutobuffs().size() >= 12) {
                    cm.dispose();
                    cm.sendOk("You are allowed a maximum of #e#r12 Autobuffs.#k#n\r\n\r\nPlease remove some before trying again!");
                    return;
                }

                switch (selection) {
                    case 0: buffs = boosters; break;
                    case 1: buffs = statups; break;
                    case 2: buffs = defencebuffs; break;
                    case 3: buffs = charges; break;
                    case 4: buffs = souls; break;
                    default: cm.dispose(); cm.sendOk("Invalid buff choice: " + selection); return;
                }

                var selStr = "#r#ePlease choose an approriate buff from below.#n#k";
                for (var i = 0; i < buffs.length; i++) {
                    if (!cm.p().getAutobuffs().contains(buffs[i])) {
                        selStr += "\r\n\r\n#L" + i + "##s" + buffs[i] + "# - #b#e" + cm.getSkillName(buffs[i]) + "#k#n";
                        selStr += "\r\n#eJob of Origin:#n " + cm.getJobName(Math.floor(buffs[i] / 10000));
                        selStr += "#n#l";
                    }
                }
                cm.sendSimple(selStr);
            }
        } else if (status == 2) {
            if (selected == 1) {
                fuck = buffs[selection];
				gay = selection;
				cm.sendYesNo("Are you sure you wish to remove #e#b#s" + fuck + "# " + cm.getSkillName(fuck) + "#k#n belonging to the #r#e" + cm.getJobName(Math.floor(fuck / 10000)) + "#n#k class?");
            } else {
                toadd = buffs[selection];
                cm.sendYesNo("Are you sure you wish to add #e#b#s" + toadd + "# " + cm.getSkillName(toadd) + "#k#n belonging to the #r#e" + cm.getJobName(Math.floor(toadd / 10000)) + "#n#k class?");
            }
        } else if (status == 3) {
            if (selected == 1) {
                cm.sendYesNo("You have successfully removed #e#b#s" + fuck + "# " + cm.getSkillName(fuck) + "#k#n belonging to the #r#e" + cm.getJobName(Math.floor(fuck / 10000)) + "#n#k class.\r\n\r\n#eWould you like to use me again?#n");
                cm.removeAutobuff(gay);
                status = -1;
            } else {
                status = -1;
                cm.addAutobuff(toadd);
                cm.p().rebuff();
                cm.sendYesNo("You have successfully added #e#b#s" + toadd + "# " + cm.getSkillName(toadd) + "#k#n belonging to the #r#e" + cm.getJobName(Math.floor(toadd / 10000)) + "#n#k class.\r\n\r\n#eWould you like to add another buff?#n");
            }
        } else {
            cm.sendDefaultMessage();
        }
    }
}
