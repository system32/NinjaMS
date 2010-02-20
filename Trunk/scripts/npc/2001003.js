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
            text += "I'm Branch Snow Man The manager of First 5 of the Happy ville hills\r\n";
            text += "#rChoose Where you want to go#b\r\n";
            for (i = 11; i <= 15; i++){
				text += "\r\n#L" + i + "# Hill " + i + " #l";
			}
            cm.sendSimple(text);
        } else if (status == 1){
            cm.warp(209000000 + selection );
            cm.dispose();            
        }
    }
}

