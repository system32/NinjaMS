/* 
 *@author: System
 *@npc : me0w
 *@npcid : 9901007
 *@location : Henesys
 *@function : Title and Legend Management
 *@credits: Oliver of FarmerStory for some Ideas
 */

var status = 0;

function start(){
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.voteMSG();
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var text = "Hello#b #h ##k";
            text += "\r\n I\'m #rMe0w#k. I manage Legend and Title stuff for ninjams";
            text += "\r\n\r\n#e#r Choose What you want me to help you with#n#k";
            text += "#b\r\n#L1# Title Info#l";
            text += "\r\n#L2# Legend Info#l";
            text += "\r\n#L3# Change Title and Legend Display#l";
            text += "\r\n#L4# Change your legend#l";
            cm.sendSimple(text);
        } else if (status == 1){
            var text1 = " Bleh";
            if(selection == 1){
                text1 = "\r\n #d#eLegend#k#n and #b#eTitle#n#k are the things you can see in a Smega";
                text1 += "\r\n Title is essentially a ninja status given by the Hokage";
                text1 += "\r\n Ninja status are not easy to obtain.";
                text1 += "\r\n 100 ninjatensu, a donation of $20 or above, winning some events";
                text1 += " can get you the Genin Status.";
                text1 += "\r\n Any thing above Genin can only obtained by ";
                text1 += "#rdedication to this server, commitment, being active in the community";
                text1 += ", behaving sensibly, not being a retard or jerk, not begging for it#k";
                text1 += "\r\n\r\n While changing your title may not be a easy task and is";
                text1 += " some thing I cannot help you with, "
                text1 += " you certainly can opt not to let other see it,";
                text1 += " if you are ashamed of your status title; and I can help you with it";
                cm.sendOk(text1);
                cm.dispose();
            } else if (selection == 2){
                text1 = "\r\n #d#eLegend#k#n and #b#eTitle#n#k are the things you can see in a Smega";
                text1 += "\r\n Legend is some thing you can set for yourself";
                text1 += "\r\n It can be anything as long as it is not disturbing";
                text1 += "or misleading.";
                text1 += " The obviously misleading stuff is already blocked from being used.";
                text1 += " The character limit for legend for now is 15 characters";
                text1 += " Changing legend will cost you merely 250 million, if you are a rookie";
                text1 += " Genin and above gets to change their legends for free";
                text1 += " \r\n By default the legend is set to off. But Since I'm too pro,";
                text1 += " I can help you turn it on";
                cm.sendOk(text1);
                cm.dispose();
            } else if(selection == 3){
                text1 = " I hope you have read the Information of Title and Legend";
                text1 += "\r\n\r\n#rChoose what you really want :#k";
                text1 += "\r\n Any change will cost you 50 million mesos";
                text1 += "#b\r\n#L1#Show only Title#l";
                text1 += "#b\r\n#L2#Show only legend#l";
                text1 += "#b\r\n#L3#Show both Title and Legend#l";
                text1 += "#b\r\n#L4#Show nothing#l";
                text1 += "\r\n";
                cm.sendSimple(text1);
            } else if (selection == 4){
                if (cm.getPlayer().haveSight(20)){
                    cm.sendGetText("Enter your Desired legend in the box : ");
                    status = 9;
                } else {
                    cm.sendOk("You are too poor. Bring me 20 TaoOfSight before I can help you with this");
                    cm.dispose();
                }
            } else {
                cm.dispose()
            }
        } else if (status == 2){
            if(cm.getPlayer().haveSight(20)){
                var textt = "";
                switch(selection){
                    case 1:
                        textt = "title only";
                        cm.getPlayer().setPrefixShit(1);
                        break;
                    case 2:
                        textt = "legend only";
                        cm.getPlayer().setPrefixShit(3);
                        break;
                    case 3:
                        textt = "title & legend";
                        cm.getPlayer().setPrefixShit(2);
                        break;
                    case 4:
                        textt = "nothing";
                        cm.getPlayer().setPrefixShit(0);
                        break;
                    default:
                        text = "title only";
                        cm.getPlayer().setPrefixShit(1);
                        break;
                }
                cm.gainItem(4032016, -20);
                cm.sendOk("Enjoy NinjaMS Your prefix status has been set to show : " + textt);
                cm.dispose();
            } else {
                cm.sendOk("You are too poor. Bring me 250 Tao Of Sight before I can help you with this");
                cm.dispose();
            }
        } else if (status == 10){
            var newlegend = cm.getText();
            if(cm.getPlayer().legendBlocked(newlegend) || newlegend.length() > 15){
                cm.sendOk("This legend is not allowed to be used in ninjams");
                cm.dispose();
            } else {
                cm.getPlayer().gainItem(4032016, -20);
                cm.getPlayer().setLegend(newlegend);
                cm.sendOk("Have fun with your New Legend <3. Your legend now is : " + newlegend);
                cm.dispose();
            }
        }
    }
}




