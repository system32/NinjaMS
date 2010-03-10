/* 
 * @npc Scarf Snow Man - 2001004
 * @author Sunny
 * @location : Hill of Happy ville 12 maps
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
			cm.warp(209000000);
            cm.dispose();
        }
    }
}

