/*
BossQuest NPC Starter - 9201102
*/

var status = 0;
var minLevel = 50;
var maxLevel = 255;
var minPlayers = 1;
var maxPlayers = 6;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            if (cm.getPlayer().getMapId() == 240050000) {
                if (cm.getParty() == null) { // no party
                    cm.sendOk("Welcome,#g #h ##k, to the waiting room for the Boss Quest. I hope you have trained well.\r\n\\r\n\You may do this solo, or form a party, and tell the leader to talk to me.\r\n\\r\n\#rMake sure you're between level 50 and 250. GMs accompanying you may be level 255.#k");
                    cm.dispose();
                    return;
                }
                if (!cm.isLeader()) { // not party leader
                    cm.sendOk("If you want to try the quest, tell your leader to talk to me.");
                    cm.dispose();
                }
                else {
                    // check if all party members are within 50-200 range, etc.
                    var party = cm.getParty().getMembers();
                    var mapId = cm.getPlayer().getMapId();
                    var next = true;
                    var levelValid = 0;
                    var inMap = 0;
                    // Temp removal for testing
                    if (party.size() < minPlayers || party.size() > maxPlayers)
                        next = false;
                    else {
                        for (var i = 0; i < party.size() && next; i++) {
                            if (party.get(i).getMapid() == mapId)
                                inMap += 1;
                        }
                        if(!cm.checkPartyGMSMode(5, party)){
                            cm.sendOk("The following Members are not in GMS mode : " +  cm.membersNotQualified(5, party) +" Please instruct them to get GMS mode from Duru in Henesys");
                            next = false;
							cm.dispose();
                            return;
                        }
                        if (inMap < minPlayers) {
                            next = false;
						}
                    }
                    if (next) {
                        var em = cm.getEventManager("BossQuest");
                        if (em == null) {
                            cm.sendOk("This PQ is currently disabled. Please be patient.");
                        } else {
                            em.startInstance(cm.getParty(),cm.getChar().getMap());
                            party = cm.getChar().getEventInstance().getPlayers();							
							cm.sendOk("Okay, good luck.");
                        }
                        cm.dispose();
                    } else {
                        cm.sendOk("You don't have a party of at least 1. Please make sure all your members are present and qualified to participate in this quest.  If this seems wrong, log out and log back in, or reform the party.");
                        cm.dispose();
                    }
                }
            } else {
                cm.warp(240050000);
                cm.dispose();
            }
        } else {
            cm.sendOk("I'm broken.");
            cm.dispose();
        }
    }
}