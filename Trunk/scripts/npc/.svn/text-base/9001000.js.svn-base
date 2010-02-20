/*
 * @NPC : Maple Admin - FreeMarket
 * @Author : System
 * @function: Info NPC*
 */

var status = 0;
var npcs = Array("Job Advancer",
    "ICutHair/ICutHairToo",
    "Shadrion",
    "Charles",
    "Miki",
    "Ria",
    "Spinel",
    "Joko",
    "Duru",
    "Doofus",
    "Cloy",
    "Francois",
    "NLC MALL NPCS");
var npcfunction = Array("Job Advancer",
    "Style Npc",
    "Clan and Path Manager",
    "Gold / votereward manager",
    "Pot/smega/ summonrock Seller",
    "Ninja Shiken Manager",
    "Tour Tour Assistant",
    "Mounts/ Morphs/ Chairs/ Kagebunshin/ Rasengan NPC",
    "Max stat item and Legend Management NPC",
    "Pet Seller",
    "Special Pet Eq Seller for Pet vac",
    "All in one shop",
    "Everything item want can be found here");

//PlayerCommands
var apcommands = Array ("str", "<amount>", "Adds Str if you have AP",
    "dex", "<amount>", "Adds Dex if you have AP",
    "int", "<amount>", "Adds Int if you have AP",
    "luk", "<amount>", "Adds luk if you have AP",
    "reducestr", "<amount>", "reduces AP from your STR and adds to remaining AP",
    "reducedex", "<amount>", "reduces AP from your DEX and adds to remaining AP",
    "reduceint", "<amount>", "reduces AP from your INT and adds to remaining AP",
    "reduceluk", "<amount>", "reduces AP from your LUK and adds to remaining AP",
    "resetstr", " ", "resets AP from your STR and adds to remaining AP",
    "resetdex", " ", "resets AP from your DEX and adds to remaining AP",
    "resetint", " ", "resets AP from your INT and adds to remaining AP",
    "resetluk", " ", "resets AP from your LUK and adds to remaining AP",
	"storeap", "amount", "adds ap to apstorage",
	"restoreap", "amount", "retrieves ap from storage",
	"autoapon", "", "turns on auto ap",
	"autoapoff", "", "turns off auto ap");

var gameplaycommands = Array ("rebirth", " ", "rebirth command",
    "unstuck", "\<ign\> \<loginId\>", "unstuck the noob",
    "quit", " ", "exits game after saving your char",
    "buynx", " ", "buys 5k nx for 10 mil mesos",
    "shuriken", " ", " Get a Shuriken aka max stat item",
    "autospawn", "on/off", "turns pet autospawn on and off",
    "savepets", "", "saves pets for autospawn ");

var inventorycommands = Array("removeitem", "<itemid>", "Removes all the items with that item id from your inventory",
    "removeeqrow", "", "removes the items from the first 4 slots on your Eq inventory",
    "removecashrow", "", "removes the items from the first 4 slots on your Cash inventory",
    "storage", "", "opens storage",
    "rechargestars", "", "recharge all your stars for free",
    "rechargebullets", "", "recharge all your bullets for free");

var npctalkcommands = Array("clan", "", "Opens NPC MIA the clan manager of NinjaMS",
    "shop", "", "Opens the All in one shop NPC",
    "guide", "", "Opens guide NPC",
    "dispose", "", "Use this if the NPCs wont talk to you");
var searchcommands = Array("itemid", "search for", "searches for item id",
	"mobid","search for","search for mob id",
	"mapid", "search for", "search for mapid");
var warpcommands = Array("home", "", "Warps you to your Home");
var infocommands = Array("ninjaglare", "<ign>", "Shows Stats of the person",
    "connected", "", "Shows the number of players online",
    "ninjatop10", "<start from rank>", "shows ranking");
var funcommands = Array("smega", "message", "smega for you 10k mesos",
    "ismega", "message", "yellow Smega for 20k mesos",
    "emo", "", "try it for the lulz",
    "leet", "on\/off", "leet talk",
    "kagebunshin", "number", "Shadow Clone Jutsu specially from Naruto",
    "cancelkagebunshin", "", "removes all clones",
    "retardcure", "ign", " sures Retardness. hopefully",
    "title", "on\/off", " turns on and off title",
    "legend", "on\/off", " turns on and off legend",
    "namechange", "newign", "Changes your nick name for a fee");
    
// End Player Commands
//Training Grounds
var training = Array ("FM1", "Silver Slimes that spawn at random intervals.",
    "FM2", "Full of Skelegons.",
    "FM3", "Skelegons and Nest Golems.",
    "FM4", "Female Boss and Male Bosses.",
    "FM5", "Fake Papulatus. (Easy to kill)",
    "FM6", "Bigfoots and The Boss.",
    "FM7", "Female Boss and Male Boss.",
    "FM8", "A few Black Crows.",
    "FM9", "Bodyguards galore!",
    "FM10", "Tons of Black Crows.",
    "FM11", "Qualm Monk Trainees.",
    "FM12", "Qualm Monk Trainees and Chief Oblivion Guardians.",
    "FM13", "Beginer Only Map! Tons of Mushmoms.",
    "FM14", "Beginer Only Map! Jr. Balrog and Silver Slimes.",
    "FM20", "Beginer Only Map! Mr. Anchor and Black Kentaurus",
    "FM21", "Beginer Only Map! Horny Mushrooms, Coke Pigs, Sakura Cellions and Pac Pinkys.",
    "FM22", "Beginer Only Map! Tutorial Sentinals, Red Snails and Ribbon Pigs.");
//End Training Grounds Stuff

function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
		cm.voteMSG();
        cm.dispose();
    } else {
		if(mode == 0 && status == 0){
			cm.voteMSG();
			cm.dispose();
		}
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var text = "Hello #h #. Welcome to NinjaMS. I'm the All in one guide Of NinjaMS. \r\n";
            text += "#e I'm here to help the Lazy fags who forgot to read what Fiona said in the beginning#n\r\n";
            text += "#r Choose What you want to know about : #b";
            text += "\r\n#L1# General Info#l";
            text += "\r\n#L2# NPC Info#l";
            text += "\r\n#L3# Commands Info#b"
            text += "\r\n#L4# Rebirth Info#l";
            text += "\r\n#L5# Cygnus Info#l";
            text += "\r\n#L6# Training ground Info#l\r\n";
            text += "\r\n#e#r Note : Please don't forget to vote for us :#b http://ninjams.org/vote";
            cm.sendSimple(text);
        } else if (status == 1){
            var guide = " ";
            var simple = false;
            switch (selection){
                case 1:
                    guide += "NinjaMS is a unique Friendly Community. We are always glad for New People.\r\n";
                    guide += "We will be glad to help you with all the difficulties you face in game.";
                    guide += "All you have to do is ask for it. You can smega for help using @smega <message here>";
                    guide += "Most of the things/ NPC you need can be found in Ellinia or FM.\r\n";
                    guide += "You can reach henesys by using command #e#b@home#k#n ";
                    guide += "You can reach the FM by pressing the #b#eTrade#n#k button\r\n";
                    guide += "#rPlease check my other options before you ask for help.";
                    guide += "Afterall, its always a pleasure to browse around and find things :)";
                    break;
                case 2:
                    guide += "#rThese are the NPCs you should Know About#k#e";
                    for (i = 0; i < npcs.length; i++){
                        guide += "\r\n\r\n#n#bName : #e"+ npcs[i];
                        guide += "\r\n#n#rFunction : #e" + npcfunction[i];
                    }
                    guide += "\r\n\r\n note : All the NPC can be found in Henesys which you can reach by using command : @home.\r\n";
                    break;
                case 3:
                    simple = true;
                    guide += " These are the Player Commands Available now. Choose What you need : \r\n";
                    guide += "#b\r\n#L1# AP/Stat Commands #l";
                    guide += "\r\n#L2# GamePlay Commands #l";
                    guide += "\r\n#L3# Inventory Manipulation Commands#l";
                    guide += "\r\n#L4# Convinient Commands#l";
                    guide += "\r\n#L5# Fun Commands #l\r\n";
                    break;
                case 4 :
                    guide += " Rebirth in NinjaMS is really a easy thing to do.";
                    guide += " Rebirthing makes you a lot stronger than you could have ever imagined";
                    guide += " \r\n level required for rebirthing varies according to the number of rebirths you have.";
                    guide += " \r\n #rupto 10 rb - lvl 200; upto 50rb - lvl 205 and so on.";
                    guide += " #b\r\n Rebirthing can be done by using command #d#e @rebirth#n#k";
                    guide += " \r\n #e Note: When you rebirth for 15 secods you cannot gain any EXP.#n";
                case 5 :
                    guide += " Oh the Cygnus. They suck to be honest. But ya I know you love it";
                    guide += " \r\n I wont blame you for being #rGAY#k";
                    guide += " \r\n For getting Cygnus Jobs, you should be atleast 3 rebirths.";
                    guide += " \r\n Type @clan to open the path manager. Change your path to toggle adventurer and cygnus path";
                    guide += " \r\n #dYour keys will be wiped when you change paths. You can job chnage anytime after 3 RB by talking to Rock,paper,Scissor Admin.#k ";
                    guide += " \r\n You will be warped to map 0 when you change paths. use #b@home# to reach Ellinia back.";
                    break;
                case 6 :
                    guide += "These are all the training grounds in NinjaMS";
                    guide += " \r\n If you weren't so lazy, then you could just walk around for 5 minutes and find out for your self.";
                    for (i = 0; i < training.length; i++){
                        guide += "\r\n #n#bPlace : #e#r " + training[i];
                        guide += "\r\n #n#bMonsters : #e#d " + training [i+1];
                        i++;
                    }
                    guide += "\r\n "
                    break;
                default:
                    guide = "undercontruction";
                    break;
            }
            if (simple){
                cm.sendSimple(guide);
            } else {
                cm.sendOk(guide);
                cm.dispose();
            }            
        } else if (status == 2){
            var guides = " ";
            switch(selection){
                case 1:
                    guides += "\r\nThese are the AP Manipulation Commands available now : ";
                    for (i = 0; i < apcommands.length; i++){
                        guides += "\r\n #n#bCommand : #e \@" + apcommands[i];
                        guides += "\r\n #n#dSyntax : #e \@" + apcommands[i] + " " + apcommands[i+1];
                        guides += "\r\n #n#kFunction : #e " + apcommands[i+2] + "\r\n";
                        i++;
                        i++;
                    }
                    guides += " ";
                    break;
                case 2:
                    guides += "\r\nThese are the GamePlay Commands available now :  ";
                    for (i = 0; i < gameplaycommands.length; i++){
                        guides += "\r\n #n#bCommand : #e \@" + gameplaycommands[i];
                        guides += "\r\n #n#dSyntax : #e \@" + gameplaycommands[i] + " " + gameplaycommands[i+1];
                        guides += "\r\n #n#kFunction : #e" + gameplaycommands[i+2] + "\r\n";
                        i++;
                        i++;
                    }
                    guides += " ";
                    break;
                case 3:
                    guides += "\r\nThese are the Inventory Manipulation Commands available now: ";
                    for (i = 0; i < inventorycommands.length; i++){
                        guides += "\r\n #n#bCommand : #e \@" + inventorycommands[i];
                        guides += "\r\n #n#dSyntax : #e \@" + inventorycommands[i] + " " + inventorycommands[i+1];
                        guides += "\r\n #n#kFunction : #e" + inventorycommands[i+2] + "\r\n";
                        i++;
                        i++;
                    }
                    guides += " ";
                    break;
                case 4:
                    guides += "\r\nThese are the Convinient Commands available now: ";
                    guides += "\r\n\r\n#r#e NPC Commands : ";
                    for (i = 0; i < npctalkcommands.length; i++){
                        guides += "\r\n #n#bCommand : #e \@" + npctalkcommands[i];
                        guides += "\r\n #n#dSyntax : #e \@" + npctalkcommands[i] + " " + npctalkcommands[i+1];
                        guides += "\r\n #n#kFunction : #e" + npctalkcommands[i+2] + "\r\n";
                        i++;
                        i++;
                    }
                    guides += "\r\n\r\n#r#e Search Command : ";
                    for (i = 0; i < searchcommands.length; i++){
                        guides += "\r\n #n#bCommand : #e \@" + searchcommands[i];
                        guides += "\r\n #n#dSyntax : #e \@" + searchcommands[i] + " " + searchcommands[i+1];
                        guides += "\r\n #n#kFunction : #e" + searchcommands[i+2] + "\r\n";
                        i++;
                        i++;
                    }
                    guides += "\r\n\r\n#r#e Warp command : ";
                    for (i = 0; i < warpcommands.length; i++){
                        guides += "\r\n #n#bCommand : #e \@" + warpcommands[i];
                        guides += "\r\n #n#dSyntax : #e \@" + warpcommands[i] + " " + warpcommands[i+1];
                        guides += "\r\n #n#kFunction : #e" + warpcommands[i+2] + "\r\n";
                        i++;
                        i++;
                    }
                    guides += "\r\n\r\n#r#e Info Commands : ";
                    for (i = 0; i < infocommands.length; i++){
                        guides += "\r\n #n#bCommand : #e \@" + infocommands[i];
                        guides += "\r\n #n#dSyntax : #e \@" + infocommands[i] + " " + infocommands[i+1];
                        guides += "\r\n #n#kFunction : #e" + infocommands[i+2] + "\r\n";
                        i++;
                        i++;
                    }
                    guides += " ";
                    break;
                case 5 :
                    guides += "\r\nThese are the fun Commands available now : ";
                    for (i = 0; i < (funcommands.length); i++){
                        guides += "\r\n #n#bCommand : #e \@" + funcommands[i];
                        guides += "\r\n #n#dSyntax : #e \@" + funcommands[i] + " " + funcommands[i+1];
                        guides += "\r\n #n#kFunction : #e" + funcommands[i+2] + "\r\n";
                        i++;
                        i++;
                    }
                    guides += " ";
                    break;
                default:
                    guides += " You retard lar";
                    break;
            }
            cm.sendOk(guides);
            cm.dispose();
        }
    }
}

