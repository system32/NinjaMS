// default message
var status = 0;

var selected = 0;
var checknumber = 0;

var arrayofitems = new Array(1132000, 1132001, 1132002, 1132003, 1132004);
var beltpoints = Array(2000, 18000, 40000, 92000, 170000);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (cm.getPlayer().getMapId() == 925020001) {
            if (mode == 1) {
                status++;
            } else { // determine below - if it is send default message, it will dispose upon clicking no.
                if (selected == 1 && cm.getPlayer().getDojoStage() > 0 && status == 1) {
                    cm.dispose();
                    cm.getPlayer().setDojoStage(0);
                    cm.sendOk("Alright then, you will be able start from the beginning next time you click on me.");
                    return;
                }
                cm.dispose();
                return;
            }

            if (status == 0) {
                cm.sendSimple("#eMy master is the strongest person in Mu Lung#n, and you want to challenge him? Fine, but you'll regret it later, rookie.\r\n\r\n#b"
                    + "#L0#I shall battle to him alone!#l\r\n"
                    + "#L1#We wish to party against your boss!#l\r\n"
                    + "#L2#I want to receive a belt#l\r\n"
                    + "#L3#How many Dojo Points to I have?#l\r\n"
                    + "#L4#What is the Mu Lung Dojo?#l\r\n"
					+ "#L5#I want to go home#l\r\n\r\n");
            } else {
                if (status == 1) {
                    selected = selection;
                }
                if (selected == 0) {
					if(cm.getPlayer().getGMSMode() < 1){
						cm.sendOk("Please turn on GMS Mode");
						cm.dispose();
					} else if (cm.getPlayer().getDojoStage() > 0) {
                        if (status == 1) {
                            cm.sendYesNo("#eThe last time you took the challenge by yourself, you went up to level " + (cm.getPlayer().getDojoStage() / 100 % 100) + "#n.\r\n\r\n#bI can take you there right now. Do you want to go there?#k\r\n\r\n#e#rNote:#k#n Clicking 'No' will make you restart from the beginning.");
                        } else {
                            cm.doDojoMapCheck(0, selected);
                        }
                    } else {
                        if (status == 1) {
                            cm.sendYesNo("So, #b#h ##k, are you ready to enter the Dojo?");
                        } else {
                            cm.doDojoMapCheck(0, selected);
                        }
                    }
                } else if (selected == 1) {
                    if (status == 1) {
                        cm.sendYesNo("So, #b#h ##k, are you #band your party#k ready to embark the Dojo and #rface a horrible, twisted death#k?");
                    } else {
                        var party = cm.getParty().getMembers();						
						if(!cm.checkPartyGMSMode(5, party)){
							cm.sendOk("Please get "+membersNotQualified(5, party)+" to turn on GMS Mode 5");
							cm.dispose();
						}  else if (party == null || party.getLeader().getId() != cm.getPlayer().getId()) {
                            cm.sendNext("Where do you think you're going? #eYou're not even the party leader#n! Go tell your party leader to talk to me.");
                            cm.dispose();
                        } else if (party.getMembers().size() == 1) {
                            cm.dispose();
                            cm.sendNext("You're going to take on the challenge as a one-man party?");
                        } else {
                            cm.doDojoMapCheck(0, selected);
                        }
                    }
                } else if (selected == 2) { // Receive Belt
                    if (status == 1) {
                        cm.sendSimple("#eThe #bDojo Belts#k are given to you automatically when you have reached the required amount of points.#n However, can show you how the belts look like and how many #rDojo Points#k you'll need to acquire the belts.\r\n\r\n#b"
                            +"#L20#I'd like to see the belts#l\r\n"
                            +"#L21#Oh okay, I'm done thanks#l\r\n");
                    } else if (status == 2) {
                        if (selection == 20) {
                            var selStr = "#eHere are the belts of #rMu Lung Dojo#k!#n";
                            for (var i = 0; i < arrayofitems.length; i++) {
                                selStr += "\r\n\r\n#v" + arrayofitems[i] + "# - #b#t" + arrayofitems[i] + "##k #e(" + beltpoints[i] + " Dojo Points)";
								selStr += "They all have Stats added to them :p";
                            }
                            cm.sendOk(selStr);
                        } else {
                            cm.dispose();
                            cm.sendOk("Remember to talk to me when you want to #rMu Lung Dojo#k!");
                        }
                    }
                } else if (selected == 3) { // View Points
                    if (status == 1) {
                        cm.dispose();
                        cm.sendOk("You currently have #e#r" + cm.getPlayer().getDojoPoints() + "#b Dojo Point(s).");
                    }
                } else if (selected == 4) { // Background Info
                    if (status == 1) {
                        cm.dispose();
                        cm.sendOk("Our master is the strongest person in Mu Lung. The place he built is called Mu Lung Dojo, a building that is 38 stories tall! You can train yourself as you can go up each level. Of course, it'll be hard for someone at your level to reach the top.");
                    }
                } else if (selected == 5) {
                    if (status == 1) {
                        cm.dispose();
                        cm.getPlayer().goHome();
                    }
                } else {
                    cm.sendDefaultMessage();
                }
            }
        } else if (cm.isRestingSpot(cm.getPlayer().getMapId())) {
            if (mode == 1) {
                status++;
            } else { // determine below - if it is send default message, it will dispose upon clicking no.
                cm.dispose();
                cm.sendOk("Oh, okay then.");
                return;
            }

            if (status == 0) {
                cm.sendSimple("I'm surprised you made it this far! But it won't be easy from here on out. You still want the challenge?\r\n\r\n#b#L0#I want to continue!#l\r\n#L1#I want to #v4000252# out!#l\r\n#L2#I want to record my score up to this point!#l");
            } else if (status == 1) {
                if (selection == 0) {
                    cm.dispose();
                    cm.warp(cm.getPlayer().getMap().getId() + 100, 0);
                } else if (selection == 1) {
                    status = 3;
                    cm.sendYesNo("So, you're giving up? #eYou're really going to leave#n?");
                } else if (selection == 2) {
                    status = 5;
                    cm.sendYesNo("If you record your score, #byou can start where you left off the next time#k. Isn't that convenient? #eDo you want to record your current score?#n");
                }
            } else if (status == 4) {
                cm.dispose();
                cm.warp(240000005);
            } else if (status == 6) {
                cm.dispose();
                cm.sendOk("#eI recorded your score, rookie .#n If you tell me the click me next time you go up, I'll take you here!");
                cm.getPlayer().setDojoStage(cm.getPlayer().getMap().getId());
                cm.warp(925020001);
            } else {
                cm.sendDefaultMessage();
            }
        } else {
            if (mode == 1) {
                status++;
            } else { // determine below - if it is send default message, it will dispose upon clicking no.
                cm.dispose();
                cm.sendOk("Stop changing your mind! Soon, #eyou'll be crying cause of the Xero amount of friends you have#n, #bbegging me to go back#k.");
                return;
            }
            if (status == 0) {
                cm.sendYesNo("What, you're giving up? #eYou just need to get to the next level#n! Do you really want to quit and leave?");
            } else {
                cm.dispose();
                //cm.warp(925020001, 0);
                cm.warp(925020002, 1);
            }
        }
    }
}