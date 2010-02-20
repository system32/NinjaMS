var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0){
            cm.sendSimple("Hey #h #! I use my diplomatic looks to smuggle some stuff out of the Elite Ninja Association to make some bucks for myself. Support me by buying my stuff . Here is What I got \r\n #L0#Weapons#l\r\n#L1#Armours#l\r\n#L2#NxItems#l");
        } else if (status == 1){
            if (selection == 0){
                var weaponShops = Array("Level 1 Weapons", "1h Sword Shop", "1h Axe Shop", "1h Mace shop","2h Sword Shop","2h axe"," 2h mace","Spear", "Pole Arm","bow", "xbow", "wand", "staff", "claw", "Dagger", "guns", "knuckles", "Maple Weapons 1", "Maple Weapons 2", "stars", "arrows", "bullets");
                var shit1 = "Choose What you Want : #b";
                for (i = 0; i < weaponShops.length; i++){
                    shit1 += "\r\n#L"+i+"#" + weaponShops[i]+ "#l";
                }
                cm.sendSimple(shit1);
                status = 9;
            } else if (selection == 1) {
                var shit2 = "Choose What You want : ";
                var armourShops = Array("Warrior Armour", "Mage Armour", "Thief Armour", "BowMan Armour");
                for (i = 0; i < armourShops.length; i++){
                    shit2 += "\r\n#L"+i+"#" + armourShops[i]+ "#l";
                }
                cm.sendSimple(shit2);
            } else if (selection == 3){
                cm.sendOk("Its Still under construction");
                cm.dispose();
            }
        } else if (status == 2){
            if(selection == 0){ // warrior
                var shit3 = "Choose what you want : \r\n #L0#Shield#l \r\n #L1#Glove#l \r\n #L2#Hat#l \r\n #L3#Shoes#l \r\n #L4#Overall#l \r\n #L5#Top#l  \r\n #L6#Bottom#l";
                cm.sendSimple(shit3);
                status = 19;
            } else if(selection == 1){ // mage
                var shit4 = "Choose what you want : \r\n #L0#Shield#l \r\n #L1#Glove#l \r\n #L2#Hat#l \r\n #L3#Shoes#l \r\n #L4#Overall#l \r\n #L5#Top#l  \r\n #L6#Bottom#l";
                cm.sendSimple(shit4);
                status = 29;
            }if(selection == 2){ // thief
                var shit5 = "Choose what you want : \r\n #L0#Shield#l \r\n #L1#Glove#l \r\n #L2#Hat#l \r\n #L3#Shoes#l \r\n #L4#Overall#l \r\n #L5#Top#l  \r\n #L6#Bottom#l";
                cm.sendSimple(shit5);
                status = 39;
            }if(selection == 3){ // bowman
                var shit6 = "Choose what you want : \r\n #L1#Glove#l \r\n #L2#Hat#l \r\n #L3#Shoes#l \r\n #L4#Overall#l \r\n #L5#Top#l  \r\n #L6#Bottom#l";
                cm.sendSimple(shit6);
                status = 49;
            }
        } else if (status == 10){ // Weapons
            cm.dispose();
            cm.openShop(selection + 100);
        } else if (status == 20){ // Warrior Armour
            cm.dispose();
            cm.openShop(selection + 201);
        } else if (status == 30){ // Mage Armour
            cm.dispose();
            cm.openShop(selection + 211);
        } else if (status == 40){ //thief
            cm.dispose();
            cm.openShop(selection + 221);
        } else if (status == 50){ //bowman
            cm.dispose();
            cm.openShop(selection + 230);
        } else {
            cm.dispose();
        }
    }
}
		