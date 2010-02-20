var status = 0;
var n1 = Array(1702000,1702001,1702002,1702003,1702004,1702005,1702006,1702007,1702008,1702009,
1702010,1702011,1702012,1702013,1702014,1702015,1702016,1702017,1702018,1702019,
1702020,1702021,1702022,1702023,1702024,1702025,1702026,1702027,1702028,1702029,
1702030,1702031,1702032,1702033,1702034,1702035,1702036,1702037,1702038,1702039,
1702040,1702041,1702042,1702043,1702044,1702045,1702046,1702047,1702048,1702049);
var n2 = Array(1702050,1702051,1702052,1702053,1702054,1702055,1702056,1702057,1702058,1702059,
1702060,1702061,1702062,1702063,1702064,1702065,1702066,1702067,1702068,1702069,
1702081,1702082,1702083,1702084,1702085,1702086,1702087,1702088,1702089,1702090,
1702091,1702092,1702093,1702094,1702095,1702096,1702098,1702099,1702100,1702102,
1702103,1702104,1702105,1702106,1702107,1702108,1702112,1702114,1702115,1702118);
var n3 = Array(1702119,1702120,1702121,1702122,1702123,1702124,1702125,1702126,1702127,1702128,
1702129,1702130,1702131,1702132,1702133,1702134,1702135,1702136,1702138,1702140,
1702141,1702142,1702143,1702144,1702145,1702146,1702147,1702148,1702149,1702150,
1702151,1702152,1702153,1702154,1702155,1702156,1702157,1702158,1702159,1702160,
1702161,1702162,1702163,1702164,1702165,1702166,1702167,1702168,1702169,1702170);
var n4 = Array(1702171,1702172,1702173,1702174,1702175,1702177,1702179,1702180,1702181,1702182,
1702183,1702184,1702185,1702187,1702188,1702189,1702190,1702191,1702193,1702195,
1702196,1702201,1702202,1702203,1702204,1702207,1702209,1702210,1702211,1702213,
1702217,1702218,1702219,1702220,1702226,1702228,1702229,1702230,1702233);
var x = 20;			
    
function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendOk("Hello there#b #h #. #kI'm Weapons Seller of #eNinjaMS#n who sell every available Weapons in NinjaMS For you. Of course for a small fee. Are you ready to go shopping with me?\r\n #r note : All items will cost you #b"+x+"#r tao each and every item will be tagged with your name :)");
		} else if (status == 1){
			if (!cm.getPlayer().haveSight(x)) {
				cm.sendOk("Seems like you are a bit low on funds . Sorry, I dont do charity");
				cm.dispose();
			} else {
				var selstr = "choose what you want #b";		
				for (i = 0; i < 4; i++){
					selstr += " \r\n#L"+i+"# Nx Weapons Shop - " + (i + 1)+" #l";
				}
				cm.sendSimple(selstr);
			}
		} else if (status == 2){
			var showItem = " Choose What you want : #b";
			switch(selection) {
				case 0:
					for (i = 0; i < n1.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + n1[i] + "# - #t" + n1[i] + "# "; 
					}
					status = 100;
					break;
				case 1:
					for (i = 0; i < n2.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + n2[i] + "# - #t" + n2[i] + "# "; 
					}
					status = 110;
					break;
				case 2:
					for (i = 0; i < n3.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + n3[i] + "# - #t" + n3[i] + "# "; 
					}
					status = 120;
					break;
				case 3:
					for (i = 0; i < n4.length; i++) {
						showItem += "\r\n#L" + i + "# #v" + n4[i] + "# - #t" + n4[i] + "# "; 
					}
					status = 130;
					break;
			}
			cm.sendSimple(showItem);				
		} else if (status == 101) {
			cm.gainItem(4032016, -x)
			cm.gainTaggedItem(n1[selection], 1);
			cm.dispose();
		}else if (status == 111) {
			cm.gainItem(4032016, -x)
			cm.gainTaggedItem(n2[selection], 1);
			cm.dispose();
		}else if (status == 121) {
			cm.gainItem(4032016, -x)
			cm.gainTaggedItem(n3[selection], 1);
			cm.dispose();
		}else if (status == 131) {
			cm.gainItem(4032016, -x)
			cm.gainTaggedItem(n4[selection], 1);
			cm.dispose();
		}
	}
}
