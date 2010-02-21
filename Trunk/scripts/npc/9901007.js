/** * @NPC: 9901007 - iDOlly * @Location: Henesys * @Function: Tao Shop * @author: System * @credits : iDolly, Oliver**/

var status = 0;
var chairs = Array (3010000,3010001,3010002,3010003,3010004,3010005,3010006,3010009,3010011,3010012,3010015);
var noob = new Array(1050018,1051017,1072344,1050127,1051140,1050100,1051098,1012070,1012071,1012073,1102084,1102041,1102086,1102042,1082149,1002357,1122000);
var tao = 4032016;
var megaphones = new Array(5072000, 5076000, 5390000, 5390001, 5390002, 5090000);
var megaphonesprice = new Array(1, 2, 5, 5, 5, 1);
var gachatix = 5220000;
var seltype = 1;
function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == 1)        status++;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var sel1 = "#r Hello #h #! I'm iDolly the Tao shop manager of NinjaMS.";
        sel1 += "These are the shops I manage. Choose which one you want : #b";
        sel1 += "\r\n #L1#Smega/note Shop #r - (Pink - 1 tao ea and Yellow - 2) #l#b ";
        sel1 += "\r\n #L2#New-bie shop - #r - (1 tao each) #l#b ";
        sel1 += "\r\n #L3#Gacha Ticket Shop #r - (1 tao and 1 snail shell each) #l#b ";
        sel1 += "\r\n #L4#Chair Shop #r - (5 tao each) #l#b ";
        sel1 += "\r\n #L5#Some Special Weapons Shop #r - (5 Tao Each) #l";
        sel1 += "\r\n\r\n\r\n";
        cm.sendSimple(sel1);
    } else if (status == 1) {
        var sel2= " ";
        if(selection == 1) {
            sel2 += " #dChoose what you need : #r(If you annoy others with spam you will be muted or Jailed)\r\n";
            for (i = 0; i < megaphones.length; i++)				sel2 += "#b #L"+i+"##t"+megaphones[i]+ "# - #r " + megaphonesprice[i] + " tao each#l\r\n";
            cm.sendSimple(sel2);
            status = 9;
        } else if (selection == 2) {
            sel2 += " #dChoose what you need :  #rThey all cost 1 Tao Each\r\n";
            for (i = 0; i < noob.length; i++)				sel2 += "#b #L"+i+"##t"+noob[i]+ "# - #v" + noob[i]+ "##l\r\n";
            cm.sendSimple(sel2);
            status = 14;
        } else if (selection == 3){
            var prompt = "So, you want me to make some Gacha ticket #v"+gachatix+"#for 1 tao each? You will also need bring 1 snail shell each #v4000019#. In that case, how many do you want me to make?";
            cm.sendGetNumber(prompt,1,1,10);
            status = 29;
        } else if (selection == 4) {
            sel2 += " #dChoose what you need : #r(If you annoy others with spam you will be muted or Jailed)\r\n";
            for (i = 0; i < chairs.length; i++)				sel2 += "#b #L"+i+"##t" +chairs[i]+ "# - #r 1 tao each#l\r\n";
            cm.sendSimple(sel2);
            status = 34;
        } else {
            cm.sendOk("Under Construction. Please be patient");
            cm.dispose();
        }
    } else if (status == 10) {
        seltype = selection;
        var prompt = "So, you want me to make some #t" + megaphones[selection] + "#s  for " + megaphonesprice[selection] +" each ?In that case, how many do you want me to make?";
        cm.sendGetNumber(prompt,1,1,100);
    } else if (status == 11){
        var qty = selection;
        if (cm.getPlayer().haveSight(qty * megaphonesprice[seltype])){
            cm.gainItem(tao, - (qty * megaphonesprice[seltype]));
            cm.gainItem(megaphones[seltype], qty);
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else if (status == 15){
        if (cm.getPlayer().haveSight(1)){
            cm.gainItem(tao, - 1);
            cm.gainItem(noob[selection], 1);
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else if (status == 30) {
        var qty = selection;
        if (cm.getPlayer().haveItem(gachatix, 10)){
            cm.sendOk("You already have 10 Gacha tix. Spend them before trying to buy more");
        } else if (cm.getPlayer().haveSight(qty) && cm.getPlayer().haveItem(4000019, qty)){
            cm.gainItem(tao, -qty);
            cm.gainItem(4000019, -qty);
            cm.gainItem(gachatix, qty);
            cm.sendOk("You have gained "+qty+" gacha tix #v"+gachatix+"# and lost "+qty+" tao of sight and "+qty+ "snail shells");
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else if (status == 35){
        if (cm.getPlayer().haveSight(1)){
            cm.gainItem(tao, - 1);
            cm.gainItem(chairs[selection], 1);
        } else {
            cm.sendOk("You do not have enough Tao");
        }
        cm.dispose();
    } else {
        cm.voteMSG();
        cm.dispose();
    }
}