var status = 0;
function start(){
    status = -1;
    action(1, 0);
}

function action(mode, selection){
    if (mode == -1) {
        cm.sendNext("Merry Christmas!!! Don't forget to vote for us");
        cm.dispose();
    } else {
        if (mode == 0) {
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
            text += "I'm rooney the assistant of Santa\r\n";
            text += "I'm only supposed to give gifts to good kids.";
            text += "Good kids are the kids who voted for NinjaMS\r\n";
            text += "I can take you to happyville if you want that\r\n";
            text += "#rChoose What you need#b";
            text += "#L1# Give me a Gift#l\r\n";
            text += "#L2# Take me to Happyville#l";
            cm.sendSimple(text);          
        } else if (status == 1){
            if(selection == 1){
                cm.gainStatItem(1002717, 1337, 137, 137);
                cm.sendOk("Have fun. This hat wil last for a week");
                cm.dispose();
            } else {
                cm.warp(209000000);
                cm.dispose()
            }
        }
    }
}
