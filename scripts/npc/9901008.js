/**
 * @NPC: 9901008 - Angy
 * @Location: Henesys
 * @Function: Gacha
 * @author: System
 * @credits : iDolly, Oliver
**/

var status = 0;

var megaphones = new Array(5072000, 5076000, 5390000, 5390001, 5390002, 5090000);
		
var scrolls1 = new Array(2040103, // face accessory hp
			2040108, // face accessory avoid
			2040203, // eye accessory acc
			2040208, // eye accessory int
            2040013, // helmet int
            2040015, // helmet acc
            2040307, // earring dex
            2044705, // claw att
            2044505, // bow att
            2043305, // dagger att
            2044605, // crossbow att
            2040407, // topwear str
            2040411, // topwear luk
            2040907, // shield luk
            2041035, // cape str
            2041037, // cape int
            2041039, // cape dex
            2041041, // cape luk
            2043105, // one-handed axe att
            2044105, // two-handed axe att
            2043205, // one-handed bw att
            2044205, // two-handed bw att
            2043005, // one-handed sword att
            2044005, // two-handed sword att
            2044405, // pole arm att
            2044305, // spear att
            2043805, // staff matt
            2043705, // wand matt
            2040715, // shoes jump
            2040509, // overall dex
            2040519, // overall int
            2040521, // overall luk
            2040811, // gloves att
            2040815, // gloves matt
            2040305, // earring int
            2040917, // shield att
            2040922, // shield matt
            2043007 // sword matt
			);
			
// 60% scrolls			
var scrolls2 = new Array(2044701, // claw att
            2044501, // bow att
            2043301, // dagger att
            2044601, // crossbow att
            2043101, // one-handed axe att
            2044101, // two-handed axe att
            2043201, // one-handed bw att
            2044201, // two-handed bw att
            2043001, // one-handed sword att
            2044001, // two-handed sword att
            2044401, // pole arm att
            2044301, // spear att
            2043801, // staff matt
            2043701, // wand matt
            2040704, // shoes jump
            2040501, // overall dex
            2040513, // overall int
            2040516, // overall luk
            2040804, // gloves att
            2040801, // gloves dex
            2040301 // earring int
            );
			
// 40% scrolls
var scrolls3 = new Array(
            2040315, // earring int
            2040912, // shield def
            2040313, // one-handed sword att
            2043108, // one-handed axe att
            2043208, // one-handed bw att
            2043308, // dagger att
            2043708, // wand matt
            2043808, // staff matt
            2044008, // two-handed sword att
            2044108, // two-handed axe att
            2044208, // two-handed bw att
            2044308, // spear att
            2044408, // pole arm att
            2044508, // bow att
            2044608, // crossbow att
            2044708 // claw att
            );			
			
// special scrolls
var scrolls4 = new Array(2049000,2049001,2049002,2049003,2340000,2049100);

// GM scrolls
var scrolls5 = new Array(2044503, 2044703, 2044603, 2043303, 2044303, 2044403, 2043803, 2043703, 2043003, 2044003, 2043203, 2044203, 2043103, 2044103, 2040506, 2040709, 2040710, 2040711, 2040303, 2040807, 2040806);

 
var chance1;
var chance2;
var chance3;
var chance4;
var tao = 4032016;
var snowrose = 4031695;

// morphs
var items1 = new Array(2210000, 2210001, 2210002, 2210003, 2210005, 2210006, 2210007, 2210008, 2210010, 2210011, 2210012, 2210016, 2210017, 2210018, 2210021, 2210032, 2210033);
// mounts
var items2  = new Array(1902000, 1902001, 1902002, 1902005, 1902006, 1902008, 1902009, 1902011, 1902012);
// saddles
var items3 = new Array(1912000, 1912003, 1912004, 1912005, 1912008, 1912007);
// Timeless and Reverse
var items4 = new Array(1452019,
            1472053, 1462015, 1332051, 1312030, 1322045, 1302056, 1442044, 1432030,
            1382035, 1412021, 1422027, 1402037, 1372010, 1302026, 1102041, 1302081,
            1312037,1322060,1402046,1412033,1422037,1442063,1472023,1332073,1332074,
            1372044,1382057,1432047,1462050,1472068,1492023,1302086,1312038,1322061,
            1332075,1332076,1372045,1372059,1402047,1412034,1422038,1432049,1442067,
            1452059,1462051,1472071,1482024,1492025); 
var items5 = new Array(4031679, //Dragon Jewel
1442012, //SKy Snowboard
1442013, //Aqua Snowboard
1442014, //Silver Snowboard
1442015, //Golden Snowboard
1442016, //Dark Snowboard
1442030, //Maple Snowboard
1442046, //Super Snowboard
1442011, //Surfboard
1442026, //Red Surfboard
1442027, //Green Surfboard
1442028, //Black Surfboard
1442029, //Gold Surfboard
1442054, //Red Surfboard
1442055, //Green Surfboard
1442056, //Sky Blue Surfboard
1442057, //Purple Surfboard
1442023, //Maroon Mop
1472032, //Maple Kandayo
1302036, //Maple Flag : 1000 days
1082252, //Maple Gage
1302020, //Maple Sword
1382009, //Maple Staff
1452016, //Maple Bow
1462014, //Maple Crow
1472030, //Maple Claw
1482020, //Maple Knuckle
1492020, //Maple Gun
1402030, //Maple Soul Singer
1332025, //Maple Wagner
1382012, //Maple Lama Staff
1412011, //Maple Dragon Axe
1422014, //Maple Doom Singer
1432012, //Maple Impaler
1442024, //Maple Scorpio
1452022, //Maple Soul Searcher
1462019, //Maple Crossbow
1482021, //Maple Storm Finger
1492021  //Maple Storm Pistol
);
// pet items and chairs
var special = new Array (3010000, 3010001, 3010002, 3010003, 3010004, 3010005, 3010006, 3010007, 3010008, 
3010009, 3010010, 3010011, 3010012, 3010013, 3010014, 3010015, 3010016, 3010017, 3010018, 3010019, 
3010022, 3010023, 3010024, 3010025, 3010026, 3010028, 3011000, 3010040, 3010041, 3010045, 
3010046, 3010047, 3010072, 3010058, 3010057, 1812006, 1812000, 1812001, 1812004, 1812005);
function start() {
    status = -1;
    action(1, 0, 0);
}

function giveMega(){
	chance1 = Math.floor(Math.random() * megaphones.length);
	chance2 = Math.random() * 5;
	chance3 = Math.random() * 5;
	chance4 = Math.floor(chance2 + chance3 + 1);
	cm.gainItem(megaphones[chance1], chance4);
	cm.sendOk("You have Gained Megaphone(s)/Note(s)");
}

function giveScrolls(){
	chance1 = Math.floor(Math.random() * 5);
	var scroll;
	switch(chance1){
		case 0:
			scroll = scrolls1;
			break;
		case 1:
			scroll = scrolls2;
			break;
		case 2:
			scroll = scrolls3;
			break;			
		case 3:
			scroll = scrolls4;
			break;
		case 4:		
			scroll = scrolls5;
			break;
		default :
			scroll = scrolls1;
	}
	chance2 = Math.floor(Math.random() * scroll.length);
	cm.gainItem(scroll[chance2], 1);
	cm.sendOk("You have Gained Scrolls");
}

function giveTao(){
	chance1 = Math.random() * 5;
	chance2 = Math.random() * 5;
	chance4 = Math.floor(chance1 + chance2 + 1);
	cm.gainItem(tao, chance4);
	cm.sendOk("You have Gained Tao");
}

function giveNx(){
	chance1 = Math.random() * 500;
	chance2 = Math.random() * 1000;
	chance3 = Math.random() * 500;
	chance4 = Math.floor(chance2 + chance3 + chance1);
	cm.gainNX(chance4);
	cm.sendOk("You have Gained NX");
}

function giveSpecial(){
	chance1 = Math.floor(Math.random() * special.length);
	cm.gainExpiringItem(special[chance1], 720);
	cm.sendOk("You have gained a special item "+ special[chance1]+" which will last for 12 hrs");
}

function scam(){
	cm.sendOk("You have been Scammed");
}

function giveItem(){
	chance1 = Math.random() * 5;
	var item;
	switch(chance1){
		case 0:
			item = items1;
			break;
		case 1:
			item = items2;
			break;
		case 2:
			item = items3;
			break;			
		case 3:
			item = items4;
			break;
		case 4:		
			item = items5;
			break;
		default :
			item = items1;
	}
	chance2 = Math.floor(Math.random() * item.length);
	cm.gainItem(item[chance2], 1);
	cm.sendOk("You have Gained an Item");
}

function levelUp(){
	chance1 = Math.random() * 5;
	chance2 = Math.random() * 20;
	chance3 = chance1 + chance2 + 1;
	for (i = 0; i < chance3; i++)
		cm.getPlayer().levelUp();
	
	cm.sendOk("You have Gained levels");
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
	if (status == 0) {
		cm.sendNext("Hey #h #! Wanna try your luck??????");
    } else if (status == 1) {
		if(cm.getPlayer().isJounin()){
			
		}
		if(!cm.haveItem(snowrose, 1) || !cm.haveItem(4000019, 1)) {
			cm.sendOk("Bring me a snow Rose and a snail shell & I'll let you check your Luck");
			cm.dispose();
		} else {
			cm.gainItem(4000019, -1);
			cm.gainItem(snowrose, -1);
			chance1 = Math.random() * 1000;
			if (chance1 < 200){
				giveItem();
			} else if (chance1 < 350) {
				giveScrolls();
			} else if (chance1 < 550) {
				giveMega();
			} else if (chance1 < 600){
				giveTao();
			} else if (chance1 < 700) {
				scam();
			} else if (chance1 < 800){
				levelUp();
			} else if(chance1 < 900) {
				giveNx();
			} else if (chance1 < 960){
				giveSpecial();
			} else {
				giveTao();
			}		
			cm.dispose();
		}
	} else {
		cm.voteMSG();
		cm.dispose();
	}
}