/* 
 * @npc Branch Snow Man - 2001001
 * @author Sunny
 * @location : Happy ville
 * @function : Warp to Happy ville
 */

var status = 0;
function start(){
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection){
    if (mode == -1) {
        cm.sendNext("Merry Christmas!!! Don't forget to vote for us");
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.sendNext("Merry Christmas!!!!");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var text = "Hello #b#h #!#k";
            text += "I'm Rudi the XMas Ornamant Seller of NinjaMS\r\n";
            text += "#rChoose What you want :#b\r\n";
            text += "\r\n#L1# Ornamants #l";
			text += "\r\n#L2# Green Letters #l";
			text += "\r\n#L3# Red Letters #l";
			cm.sendSimple(text);
        } else if (status == 1){
            cm.openShop(selection + 304);
            cm.dispose();            
        }
    }
}

