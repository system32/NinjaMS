function start() {
	cm.sendOk("Hey there #h #. If you give me 25 Leathers, I can let you access the Pet Shop :p");
}
function action() {
if (cm.haveItem(4000021, 25)){
	cm.gainItem(4000021, -25);
	cm.openShop(237);
	
	cm.dispose();
	} else {
		cm.sendOk("haha you hobo");
		cm.dispose();
	}
}
