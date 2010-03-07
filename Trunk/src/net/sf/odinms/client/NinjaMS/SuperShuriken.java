/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.constants.Items;

/**
 *
 * @author Admin
 */
public class SuperShuriken {

    static int[] capreq = {200, 4000017, //Pig Head
        1500, 4000077, //Dark Cloud Foxtail
        69, 4031241, //- Swallow's Lost Seed ( Quest Item )
        1500, 4000150 // Ice Piece
    };

    static int[] capereq = {200, 4031309,// - Cloud Piece ( Quest Item )
        2000, 4000205,// Dirty Bandage
        1000, 4000228 //Anesthetic Powder
    };
    static int[] coatreq = {250, 4000125, //- Chief Gray's Sign
        2500, 4000137, //- Subordinate D Fingernail
        300, 4031458 //- Thanatos's Black Tornado ( Quest Item )
    };
    static int[] glovereq = {1000, 4000061, // - Luster Pixie's Sunpiece
        1669, 4000075, //- Triangular Bandana of the Nightghost
        250, 4031098 //- All-purpose Clock Spring ( Quest Item )
    };
    static int[] longcoatreq = {1337, 4000437, // - Black Mushroom Spore
        250, 4001006,// - Flaming Feather
        500, 4001075, // - Cornian's Marrow ( Quest Item )
        1337, 4000041 // - Malady's Experimental Frog
    };
    static int[] pantsreq = {2000, 4000153, //- Snorkle
        150, 4031215, // - Taurospear's Spirit Rock ( Quest Item )
        2000, 4000128 // - Buffy Hat
    };
    static int[] peteqreq = {250, 4031460,// - Cold Heart of a Wolf ( Quest Item )
        250, 4000415,// - Ice Tear
        250, 4001000,// - Arwen's Glass Shoes
        1500, 4032005 // - Typhon Feather
    };
    static int[] shoesreq = {300, 4031253, //- Pianus's Scream ( Quest Item )
        2000, 4000066,// - Cloud Foxtail
        2000, 4032010 // - Elder Ashes
    };
    static int[] tamingreq = {350, 4031195, //- Aurora Marble ( Quest Item )
        350, 4000082, //- Zombie's Lost Gold Tooth
        350, 4000124, //- Rombot's Memory Card
        700, 4000336 //- Bible of the Corrupt
    };
    static int[] accessoryreq = {350, 4000240, //- Small Flaming Feather
        250, 4000224, //- Sabots
        350, 4031674, //- Elpam Magnet ( Quest Item )
        2500, 4000325 //- Carrot
    };

    public static int getItemType(int itemId) {
        int cat = 0;
        if (itemId >= 1010000 && itemId < 1040000) {
            cat = 1; //"Accessory";
        } else if (itemId >= 1000000 && itemId < 1010000) {
            cat = 2; // "Cap";
        } else if (itemId >= 1102000 && itemId < 1103000) {
            cat = 3; // "Cape";
        } else if (itemId >= 1040000 && itemId < 1050000) {
            cat = 4; //"Coat";
        } else if (itemId >= 1080000 && itemId < 1090000) {
            cat = 5;// "Glove";
        } else if (itemId >= 1050000 && itemId < 1060000) {
            cat = 6; //"Longcoat";
        } else if (itemId >= 1060000 && itemId < 1070000) {
            cat = 7; //"Pants";
        } else if (itemId >= 1802000 && itemId < 1810000) {
            cat = 8; //"PetEquip";
        } else if (itemId >= 1070000 && itemId < 1080000) {
            cat = 9; //"Shoes";
        } else if (itemId >= 1900000 && itemId < 2000000) {
            cat = 10; //"Taming";
        }
        int[] blockedItems = {1812006,// Magic scales
            1002140, // - Wizet Invincible Hat
            1042003,// - Wizet Plain Suit
            1062007,// - Wizet Plain Suit Pants
            1322013,// - Wizet Secret Agent Suitcase
            1002959 //- Junior GM Cap
        };
        for (int i = 0; i < blockedItems.length; i++) {
            if (blockedItems[1] == itemId) {
                cat = 1337;
                break;
            }
        }
        if (MapleItemInformationProvider.getInstance().isCashEquip(itemId)) {
            cat += 100;
        }
        return cat;
    }

    public static int checkRequirements(MapleCharacter player, int itemId) {
        int cat = getItemType(itemId);
        if (cat > 1000) {
            return 1;
        } else if (MapleItemInformationProvider.getInstance().getSlotMax(itemId) < 1) {
            return 2;
        } else {
            int taocount = player.getMaxStatItems() * 200;
            if (cat > 100) {
                taocount *= 2;
            }
            if (!player.hasAllStatMax()){
                return 3;
            } else if (!player.haveSight(taocount)) {
                return 4;
            } else if (!checkEtcItems(player, cat)) {
                return 5;
            } else if (player.getBossPoints() < (taocount * 1000)) {
                return 6;
            } else {
                return 7;
            }
        }
    }

    public static void removeItems(MapleCharacter player, int itemId) {
        int cat = getItemType(itemId);
        int taocount = player.getMaxStatItems() * 200;
        if (cat > 100) {
            taocount *= 2;
        }
        player.gainItem(Items.currencyType.Sight, -taocount);
        player.setBossPoints(player.getBossPoints() - (taocount * 1000));
        removeEtcItems(player, itemId);
    }


    public static int[] getEtcArray(int cat) {
        int[] etcarray;
        if (cat > 100) {
            cat -= 100;
        }
        switch (cat) {
            case 1:
                etcarray = accessoryreq;
                break;
            case 2:
                etcarray = capreq;
                break;
            case 3:
                etcarray = capereq;
                break;
            case 4:
                etcarray = coatreq;
                break;
            case 5:
                etcarray = glovereq;
                break;
            case 6:
                etcarray = longcoatreq;
                break;
            case 7:
                etcarray = pantsreq;
                break;
            case 8:
                etcarray = peteqreq;
                break;
            case 9:
                etcarray = shoesreq;
                break;
            default:
                etcarray = tamingreq;
                break;
        }
        return etcarray;
    }

    public static boolean checkEtcItems(MapleCharacter player, int itemid) {
        int cat = getItemType(itemid);
        int[] etcarray = getEtcArray(cat);
        for (int i = 0; i < etcarray.length; i++) {
            int amt = etcarray[i];
            if (cat > 100) {
                amt *= 2;
            }
            if (player.haveItem(etcarray[i + 1], amt)) {
                return false;
            }
            i++;
        }
        return true;
    }

    public static String getReQItems(MapleCharacter player, int itemid) {
        int cat = getItemType(itemid);
        int[] etcarray = getEtcArray(cat);
        StringBuilder lol = new StringBuilder();
        lol.append(" There are the items you need for the selected Item");
        for (int i = 0; i < etcarray.length; i++) {
            int amt = etcarray[i];
            if (cat > 100) {
                amt *= 2;
            }
            lol.append("\r\n ");
            lol.append(amt);
            lol.append(" of #v");
            lol.append(etcarray[i + 1]);
            lol.append("# - #t");
            lol.append(etcarray[i + 1]);
            lol.append("# . \r\n");
            i++;
        }
        lol.append("\r\n You will also need ");
        int taocount = player.getMaxStatItems() * 200;
        if (cat > 100) {
            taocount *= 2;
        }
        lol.append(taocount);
        lol.append(" amount of Tao of Sights and ");
        lol.append(taocount * 1000);
        lol.append(" boss points. ");
        return lol.toString();
    }

    private static void removeEtcItems(MapleCharacter player, int itemId) {
        int cat = getItemType(itemId);
        int[] etcarray = getEtcArray(cat);
        for (int i = 0; i < etcarray.length; i++) {
            int amt = etcarray[i];
            if (cat > 100) {
                amt *= 2;
            }
            player.gainItem(etcarray[i+1], amt);
            i++;
        }
    }
}
