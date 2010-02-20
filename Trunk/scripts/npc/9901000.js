/* Author: Xterminator
	NPC Name: 		Shinobi
	Map(s): 		Map 180000000.
	Description: 		First NPC
*/

function start() {
	var text = "Hey there rookie ninja #b#h #!#k I am the great Shinobi and welcome to #e#rNinjaMs#n#k a unique ninja themed server with a fun & exciting gaming experience. We believe in maturity, innovation, love, stability, passion. We hope you like and enjoy our Server.\r\n";
text += "\r\n\r\nI am sure you are wondering why you look so damn #r'Pretty'#k . I have dressed you to what i see fit for a Ninja.";
text += "\r\n\r\n You can change your skin color by talking to ICutHair or ICutHairToo in Henesys"
text += "\r\n\r\nWe have everything to your Needs. Custom FM bosses,Nice GM'S,Events,ULFJ, ETC!. Most of the things you need can be obtained our @shop.";
text += "\r\n\r\n#g#eThere are some things that money cannot buy. For every fucking thing else, there is NLC MegaMall. Talk to Spinel to reach there:)#k#n";
text += "\r\n\r\nIf you need help have no fear just type @help/@commands before asking anything. That can help with anything referring to the game that you need help with.";
text += "For more information Please visit #bhttp://Ninjams.org#k for updates and information concerning the game. \r\n #rHave Fun #k ";
	cm.sendOk(text)
}
function action() {
	cm.sendOk("Bye now. Take Care");
	while (cm.getPlayer().getLevel() < 30){
		cm.getPlayer().levelUp();
	}
	cm.getPlayer().setExp(0);
	cm.warp(100000000);
	cm.dispose();
}

