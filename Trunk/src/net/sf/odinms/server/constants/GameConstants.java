/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.server.constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.server.AutobanManager;

/**
 *
 * @author Owner
 */
public class GameConstants {

    public static String getJobName(int id) {
        String name = "Beginner";
        switch (id) {
            case 0:
                name = "Beginner";
                break;
            case 200:
                name = "Magician";
                break;
            case 300:
                name = "Bowman";
                break;
            case 400:
                name = "Thief";
                break;
            case 500:
                name = "Pirate";
                break;
            case 100:
                name = "Swordman";
                break;
            case 110:
                name = "Fighter";
                break;
            case 120:
                name = "Page";
                break;
            case 130:
                name = "Spearman";
                break;
            case 210:

                name = "F/P Wizard";
                break;
            case 220:
                name = "I/L Wizard";
                break;
            case 230:
                name = "Cleric";
                break;
            case 310:
                name = "Hunter";
                break;
            case 320:
                name = "Crossbowman";
                break;
            case 410:
                name = "Assassin";
                break;
            case 420:
                name = "Bandit";
                break;
            case 510:
                name = "Brawler";
                break;
            case 520:
                name = "Gunslinger";
                break;
            case 111:
                name = "Crusader";
                break;
            case 121:
                name = "White Knight";
                break;
            case 131:
                name = "Dragon Knight";
                break;
            case 211:
                name = "F/P Mage";
                break;
            case 221:
                name = "I/L Mage";
                break;
            case 231:
                name = "Priest";
                break;
            case 311:
                name = "Ranger";
                break;
            case 321:
                name = "Sniper";
                break;
            case 411:
                name = "Hermit";
                break;
            case 421:
                name = "Chief Bandit";
                break;
            case 511:
                name = "Marauder";
                break;
            case 521:
                name = "Outlaw";
                break;
            case 112:
                name = "Hero";
                break;
            case 122:
                name = "Paladin";
                break;
            case 132:
                name = "Dark Knight";
                break;
            case 212:
                name = "F/P Arch Mage";
                break;
            case 222:
                name = "I/L Arch Mage";
                break;
            case 232:
                name = "Bishop";
                break;
            case 312:
                name = "Bowmaster";
                break;
            case 322:
                name = "Marksman";
                break;
            case 412:
                name = "Night Lord";
                break;
            case 422:
                name = "Shadower";
                break;
            case 512:
                name = "Buccaneer";
                break;
            case 522:
                name = "Corsair";
                break;
            case 900:
                name = "GM";
                break;
            case 910:
                name = "Epic GM";
                break;
            case 1000:
                name = "Noblesse";
                break;
            case 1100:
            case 1110:
            case 1111:
                name = "Dawn Warrior";
                break;
            case 1200:
            case 1210:
            case 1211:
                name = "Blaze Wizard";
                break;
            case 1300:
            case 1310:
            case 1311:
                name = "Wind Archer";
                break;
            case 1400:
            case 1410:
            case 1411:
                name = "Night Walker";
                break;
            case 1500:
            case 1510:
            case 1511:
                name = "Thunder Breaker";
                break;
            default:
                name = "Noobass";
                break;
        }
        return name;
    }

    public static boolean isIllegialWords(MapleCharacter player, String text) {
        if (player.getReborns() > 3) {
            return false;
        }

        String origitext = text;

        text = text.replaceAll(" ", "").toLowerCase();

        // server sucks
        String[] bannedcomments = {"isserversux", "farmermssux", "farmerstorysux", "farmermssuc", "farmerstorysuc", "farmermssuk", "farmerstorysuk"};
        for (int i = 0; i < bannedcomments.length; i++) {
            if (text.contains(bannedcomments[i])) {
                AutobanManager.getInstance().autoban(player.getClient(), "Trying to comment on the server sucking (Text: " + origitext + ")");
                return true;
            }
        }

        // advertisements of maplestory servers...
        String[] advertisingarray = {".com", ".net", ".info", ".org", ".tk", ".weebly", ".freewebs"};
        String[] forgivencontents = {"story.org", "sydneyms", "pokemonms", "farmerstory", ".org/vote"};
        for (int i = 0; i < advertisingarray.length; i++) {
            String[] subarray = {"story", "ms"};
            for (int p = 0; p < subarray.length; p++) {
                if (text.toLowerCase().contains(subarray[p] + advertisingarray[i])) {
                    boolean banhammer = true;

                    for (int z = 0; z < forgivencontents.length; z++) {
                        if (text.contains(forgivencontents[z])) {
                            return false;
                        }
                    }

                    if (banhammer) {
                        AutobanManager.getInstance().autoban(player.getClient(), "Advertisement of other server (Text: " + origitext + ")");
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isBlockedLegend(String newname) {
        String[] blockedWords = { // all curse's right away from WZ! [Credits to Super_Sonic]
            ",", ".", "?", "-", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "[", "]", "{", "}", "|", ":", ";", "/", "<", ">", "/", "?",
            //numbers
            "1esb1an", "455h0Ie", "455h0le", "455m45ter", "455much", "455munch", "4n4I5ex", "4n4Isex", "4n4l5ex", "4n4lsex", "4ssh0Ie", "4ssh0le", "4ssm4ster", "4ssmuch", "4ssmunch", "5chI0ng", "5chl0ng", "5h1b4I", "5h1b4l", "5h1baI", "5h1bal", "5h1t", "5h1z", "5hibaI", "5hibal", "5hit", "5hiz", "5hlbal", "5hlt", "5hlz",
            // letter a
            "a55h0Ie", "a55h0le", "a55ma5ter", "a55much", "a55munch", "anaI5ex", "anaIsex", "anal", "anal5ex", "analsex", "assh0Ie", "assh0le", "asshole", "asslover", "ass-lover", "assmaster", "assmuch", "assmunch",
            // letter b
            "b14tch", "b1atch", "b1tch", "b1tch455", "b1tcha55", "b1tchass", "b1y0tch", "b1z4tch", "b1zatch", "b1zn4tch", "b1znatch", "b45t4rd", "b4IIz", "b4llz", "b4st4rd", "balls", "ballz", "bastard", "beeeech", "beeotch", "beeyotch", "beyotch", "bI0wj0b", "bI0wme", "bi4tch", "biaatch", "biatch", "biiiiitch", "biiiitch", "biiitch", "biitch", "biotch", "bitch", "bitch4ss", "bitcha55", "bitchass", "bittch", "biy0tch", "biyaaatch", "biyatch", "biyotch", "biz4tch", "bizatch", "bizn4tch", "biznatch", "bl0wj0b", "bl0wme", "bl4tch", "blatch", "bllltch", "blotch", "blowjob", "blowme", "bltch", "bltch4ss", "bltcha55", "bltchass", "bly0tch", "blyotch", "blz4tch", "blzatch", "blzn4tch", "blznatch", "buttmunch", "bytch",
            // letter c
            "chunin", "c8", "ch00ch1e", "ch00chie", "ch00chle", "ch1nk", "chink", "choochie", "cI1t0r1s", "cIit", "cIit0ri5", "cIit0ris", "cl1t0r1s", "clit", "clit0ri5", "clit0ris", "clitoris", "cllt", "cllt0rl5", "cllt0rls", "cock", "condom", "cottonpick", "cum", "cunnt", "cunt",
            // letter d
            "d0ggy5tyIe", "d0ggy-5tyIe", "d0ggy5tyle", "d0ggy-5tyle", "d0ggystyIe", "d0ggystyle", "d1ck", "damn", "deepthr04t", "deepthroat", "dick", "dildo", "dilhole", "dlldo", "doggystyle", "doggy-style", "dumbfuck", "dyke",
            // letter e
            "eatme",
            // letter f
            "fag", "fagg0t", "faggot", "fetish", "fucker", "fuck", "fuc", "fuker", "fukkin", "fuk", "fuuk",
            // letter g
            "genin", "g00k", "g4y", "gaaay", "gaay", "gay", "gizay", "goddamn", "goddmamn", "gook",
            // letter h
            "havesex", "homo", "hong", "hoochie", "hooters", "hokage",
            // letter i
            "Iesb0",
            // letter j
            "Jounin", "j4ck0ff", "jackoff", "jack-off", "jap5", "japs", "jerkme", "jerk-off", "jiz", "jizm", "jew",
            // letter k
            "kike",
            // letter l
            "lesb0", "lesbian", "lesbo", "lezbo",
            // letter m
            "m1ss10nary", "mastabate", "mastarbate", "masterbate", "masturbate", "missi0n4ry", "missi0nary", "missionary", "mlssl0n4ry", "mlssl0nary", "mofucc", "mothafuc", "mutha", "mytit",
            // letter n
            "n1gger", "negro", "niga", "nigar", "niger", "nigga", "niggar", "nigger", "nipple", "nlgger", "nutsack",
            // letter o
            "orgasm", "orgy", "p0rn", "pen15", "penis", "phuck", "porn", "porno", "pr0n", "pussie", "pussy",
            // letter r
            "retard", "rubmy",
            // letter s
            "sannin", "schlong", "sexfreak", "sexmachine", "sexual", "sexwith", "sh1t", "shibal", "shiiiiiit", "shit", "shiz", "shlt", "spank", "sperm", "spum", "ssh1t", "sshit", "sshlt", "suckme", "suckmy", "sexy",
            // letter t
            "titty", "tltty", "tw4t", "twat",
            // letter v
            "v4g1n4", "vagina",
            // letter w
            "w4nker", "wackoff", "wack-off", "wanker", "whatthefukk", "whore",
            // letter y
            "y0urt1t", "y0urtit", "y0urtlt", "yourtit",
            // others ;D
            "coon", "Poontang", "Poon", "Coon", "Koon", "Porchmonkey", "Porch", "Spic", "Yellowboy", "Slant", "nazzi", "Nazi", "Adolph", "fuck'", "Gestapo", "Fuhrer", "F?rer", "Himmler", "Goebbles", "Crip", "Pedobear", "Pedo", "Slut", "fuker", "Skank", "Skeezer", "Whore", "drivebitch", "fuckgm", "fuckugm", "fukugm", "fucugm", "motherfuckin'", "move", "movebitch", "asshat", "hitler", "splooge", "spooge",
            "skeet", "blow", "asshole'", "dick'", "titties", "assface", "douche", "doosh", "dooshe", "kraut", "kyke", "wop", "fuckjoo", "pollock", "jigaboo", "pecker", "peckerwood", "fck'", "ragheaD", "wetback", "masturbate", "fck", "sht", "rape", "brogamecom", "freewebscomnxking", "gamekoocom", "lolsalecom", "maplestorygoldscom", "mesorichcom", "mmogresourcecom", "mmovpcom", "msvgoldscom", "msgodmodecom", "msgoldscom", "myigskycom", "nexonmesospiczocom",
            "power4gamecom", "rpgbuckscom", "togexcom", "zedgamecom", "maplegold", "msgold", "c0m", "maplestorygoldscom", "lolsalecom", "mapleaidcom", "power4gamecom", "zedgamenet", "thsalecom", "msmesoseu", "brosalecom", "thvendcom", "igscom", "gamekoocom", "gosalecom", "gameimcom", "sellmscom", "mmogamesalecom", "igamegardencom", "swagvaultcom", "thepowerlevelcom", "gamekoo", "gamek00", "gamek0o", "gameko0", "power4game", "p0wer4game", "xxcity", "mmogamesale", "mm0gamesale", "mmogamesaie", "guygame", "pkxman", "igs",
            "lgs", "oosale", "00sale", "o0sale", "0osale", "lolsale", "l0lsale", "ioisale", "ioisaie", "i0isale", "i0isaie", "zedgame", "brosale", "br0sale", "msmesos", "msmes0s", "thepowerlevel", "thep0werlevel", "thepowerievei", "thep0werievei", "mapleaid", "maplestorygolds", "atmgame", "yangyuqiang", "ugamegold", "ugamegoid", "ugameg0ld", "ugameg0id", "swagvault", "igamegarden", "lgamegarden", "sellms", "gameim", "thvend",
            "mysupersales", "igecom", "peons4hire", "gmworker", "xxc1ty", "mmogamesa1e", "mm0gamesa1e", "1gs", "oosa1e", "00sa1e", "o0sa1e", "0osa1e", "1o1sa1e", "101sa1e", "brosa1e", "br0sa1e", "thepower1eve1", "thep0wer1eve1", "map1ea1d", "map1estorygo1ds", "yangyuq1ang", "ugamego1d", "ugameg01d", "swagvau1t", "1gamegard", "se11ms", "game1m", "mysupersa1es", "1ge", "peons4h1re", "lgecom", "1gecom1", "ig3com", "1g3com", "nexon3xwus", "n3xon3xwus", "n3x0n3xwus", "nex0n3xwus", "prizerebelnettc",
            "prizerebe1nettc", "prizerebeinettc", "pr1zerebelnettc", "pr1zerebe1nettc", "pr1zerebeinettc", "prlzerebelnettc", "prlzerebeinettc", "prlzerebe1nettc", "priz3r3b3ln3ttc", "priz3r3b3in3ttc", "priz3r3b31n3ttc", "pr1z3r3b3ln3ttc", "pr1z3r3b3in3ttc", "pr1z3r3b31n3ttc", "prlz3r3b3ln3ttc", "prlz3r3b3in3ttc", "prlz3r3b31n3ttc", "mesodealcom", "mes0dealcom", "mesodealco0m", "m3sodealcom", "friskgamecom", "fr1skgamecom",
            "frlskgamecom", "m3so", "mes0", "m3s0", "n3xon", "nex0n", "n3x0n", "gam3", "n3t", "gamecorn", "garnecom", "lolgame", "1olgame", "10lgame", "1oigame", "10igame", "1o1game", "101game", "lo1game", "l01game", "loigame", "l0igame", "iolgame", "i0lgame", "io1game", "i01game", "ioigame", "i0igame", "g4me", "g4m3", "itemratecom", "ratecom", "r4tecom", "rat3com", "r4t3com", "gamecom", "salecom", "s4lecom", "gamekoo",
            "guygamecom", "gamegoldcom", "goldcom", "38dugameus", "garnecorn", "xxclty", "xxc1ty", "Thsalecom", "fvck", "thefirststorycom", "wow4scom", "mapleftptk", "mapieftptk", "thsaiecom", "fuuck", "Shiit", "saiecom", "sa1ecom", "5alecom", "Garnekoocom", "Garnekoo", "Oosalecom", "Oosaiecom", "wow4snet", "mmodocom", "gm963com",
            "vi4scom", "maplestoryio", "maplestorysh", "itemshopscom", "itemshopsnet", "game8thcom", "mmobe", "maplestorymesos", "fastmesos", "goldceo", "mygamebuy", "sale2k", "mapleftp", "sellaccount", "ogpal", "msogpal", "gamecuu", "thepowerlevel", "thepowerIeveI", "thepowerleveI", "thepowerIevel"
        };
        for (int w = 0; w < blockedWords.length; w++) {
            if (newname.replaceAll(" ", "").toLowerCase().contains(blockedWords[w])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlockedGuild(String newname) {
        String[] blockedWords = { // all curse's right away from WZ! [Credits to Super_Sonic]
            "-", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "{", "}", "|", ":", ";", "/", "/",
            //numbers
            "1esb1an", "455h0Ie", "455h0le", "455m45ter", "455much", "455munch", "4n4I5ex", "4n4Isex", "4n4l5ex", "4n4lsex", "4ssh0Ie", "4ssh0le", "4ssm4ster", "4ssmuch", "4ssmunch", "5chI0ng", "5chl0ng", "5h1b4I", "5h1b4l", "5h1baI", "5h1bal", "5h1t", "5h1z", "5hibaI", "5hibal", "5hit", "5hiz", "5hlbal", "5hlt", "5hlz",
            // letter a
            "a55h0Ie", "a55h0le", "a55ma5ter", "a55much", "a55munch", "anaI5ex", "anaIsex", "anal", "anal5ex", "analsex", "assh0Ie", "assh0le", "asshole", "asslover", "ass-lover", "assmaster", "assmuch", "assmunch",
            // letter b
            "b14tch", "b1atch", "b1tch", "b1tch455", "b1tcha55", "b1tchass", "b1y0tch", "b1z4tch", "b1zatch", "b1zn4tch", "b1znatch", "b45t4rd", "b4IIz", "b4llz", "b4st4rd", "balls", "ballz", "bastard", "beeeech", "beeotch", "beeyotch", "beyotch", "bI0wj0b", "bI0wme", "bi4tch", "biaatch", "biatch", "biiiiitch", "biiiitch", "biiitch", "biitch", "biotch", "bitch", "bitch4ss", "bitcha55", "bitchass", "bittch", "biy0tch", "biyaaatch", "biyatch", "biyotch", "biz4tch", "bizatch", "bizn4tch", "biznatch", "bl0wj0b", "bl0wme", "bl4tch", "blatch", "bllltch", "blotch", "blowjob", "blowme", "bltch", "bltch4ss", "bltcha55", "bltchass", "bly0tch", "blyotch", "blz4tch", "blzatch", "blzn4tch", "blznatch", "buttmunch", "bytch",
            // letter c
            "c8", "ch00ch1e", "ch00chie", "ch00chle", "ch1nk", "chink", "choochie", "cI1t0r1s", "cIit", "cIit0ri5", "cIit0ris", "cl1t0r1s", "clit", "clit0ri5", "clit0ris", "clitoris", "cllt", "cllt0rl5", "cllt0rls", "cock", "condom", "cottonpick", "cum", "cunnt", "cunt",
            // letter d
            "d0ggy5tyIe", "d0ggy-5tyIe", "d0ggy5tyle", "d0ggy-5tyle", "d0ggystyIe", "d0ggystyle", "d1ck", "damn", "deepthr04t", "deepthroat", "dick", "dildo", "dilhole", "dlldo", "doggystyle", "doggy-style", "dumbfuck", "dyke",
            // letter e
            "eatme",
            // letter f
            "fag", "fagg0t", "faggot", "fetish", "fucker", "fuck", "fuc", "fuker", "fukkin", "fuk", "fuuk",
            // letter g
            "g00k", "g4y", "gaaay", "gaay", "gay", "gizay", "goddamn", "goddmamn", "gook",
            // letter h
            "havesex", "homo", "hong", "hoochie", "hooters",
            // letter i
            "Iesb0",
            // letter j
            "j4ck0ff", "jackoff", "jack-off", "jap5", "japs", "jerkme", "jerk-off", "jiz", "jizm", "jew",
            // letter k
            "kike",
            // letter l
            "lesb0", "lesbian", "lesbo", "lezbo",
            // letter m
            "m1ss10nary", "mastabate", "mastarbate", "masterbate", "masturbate", "missi0n4ry", "missi0nary", "missionary", "mlssl0n4ry", "mlssl0nary", "mofucc", "mothafuc", "mutha", "mytit",
            // letter n
            "n1gger", "negro", "niga", "nigar", "niger", "nigga", "niggar", "nigger", "nipple", "nlgger", "nutsack",
            // letter o
            "orgasm", "orgy", "p0rn", "pen15", "penis", "phuck", "porn", "porno", "pr0n", "pussie", "pussy",
            // letter r
            "retard", "rubmy",
            // letter s
            "schlong", "sexfreak", "sexmachine", "sexual", "sexwith", "sh1t", "shibal", "shiiiiiit", "shit", "shiz", "shlt", "spank", "sperm", "spum", "ssh1t", "sshit", "sshlt", "suckme", "suckmy", "sexy",
            // letter t
            "titty", "tltty", "tw4t", "twat",
            // letter v
            "v4g1n4", "vagina",
            // letter w
            "w4nker", "wackoff", "wack-off", "wanker", "whatthefukk", "whore",
            // letter y
            "y0urt1t", "y0urtit", "y0urtlt", "yourtit",
            // others ;D
            "coon", "Poontang", "Poon", "Coon", "Koon", "Porchmonkey", "Porch", "Spic", "Yellowboy", "Slant", "nazzi", "Nazi", "Adolph", "fuck'", "Gestapo", "Fuhrer", "F?rer", "Himmler", "Goebbles", "Crip", "Pedobear", "Pedo", "Slut", "fuker", "Skank", "Skeezer", "Whore", "drivebitch", "fuckgm", "fuckugm", "fukugm", "fucugm", "motherfuckin'", "move", "movebitch", "asshat", "hitler", "splooge", "spooge",
            "skeet", "blow", "asshole'", "dick'", "titties", "assface", "douche", "doosh", "dooshe", "kraut", "kyke", "wop", "fuckjoo", "pollock", "jigaboo", "pecker", "peckerwood", "fck'", "ragheaD", "wetback", "masturbate", "fck", "sht", "rape", "brogamecom", "freewebscomnxking", "gamekoocom", "lolsalecom", "maplestorygoldscom", "mesorichcom", "mmogresourcecom", "mmovpcom", "msvgoldscom", "msgodmodecom", "msgoldscom", "myigskycom", "nexonmesospiczocom",
            "power4gamecom", "rpgbuckscom", "togexcom", "zedgamecom", "maplegold", "msgold", "c0m", "maplestorygoldscom", "lolsalecom", "mapleaidcom", "power4gamecom", "zedgamenet", "thsalecom", "msmesoseu", "brosalecom", "thvendcom", "igscom", "gamekoocom", "gosalecom", "gameimcom", "sellmscom", "mmogamesalecom", "igamegardencom", "swagvaultcom", "thepowerlevelcom", "gamekoo", "gamek00", "gamek0o", "gameko0", "power4game", "p0wer4game", "xxcity", "mmogamesale", "mm0gamesale", "mmogamesaie", "guygame", "pkxman", "igs",
            "lgs", "oosale", "00sale", "o0sale", "0osale", "lolsale", "l0lsale", "ioisale", "ioisaie", "i0isale", "i0isaie", "zedgame", "brosale", "br0sale", "msmesos", "msmes0s", "thepowerlevel", "thep0werlevel", "thepowerievei", "thep0werievei", "mapleaid", "maplestorygolds", "atmgame", "yangyuqiang", "ugamegold", "ugamegoid", "ugameg0ld", "ugameg0id", "swagvault", "igamegarden", "lgamegarden", "sellms", "gameim", "thvend",
            "mysupersales", "igecom", "peons4hire", "gmworker", "xxc1ty", "mmogamesa1e", "mm0gamesa1e", "1gs", "oosa1e", "00sa1e", "o0sa1e", "0osa1e", "1o1sa1e", "101sa1e", "brosa1e", "br0sa1e", "thepower1eve1", "thep0wer1eve1", "map1ea1d", "map1estorygo1ds", "yangyuq1ang", "ugamego1d", "ugameg01d", "swagvau1t", "1gamegard", "se11ms", "game1m", "mysupersa1es", "1ge", "peons4h1re", "lgecom", "1gecom1", "ig3com", "1g3com", "nexon3xwus", "n3xon3xwus", "n3x0n3xwus", "nex0n3xwus", "prizerebelnettc",
            "prizerebe1nettc", "prizerebeinettc", "pr1zerebelnettc", "pr1zerebe1nettc", "pr1zerebeinettc", "prlzerebelnettc", "prlzerebeinettc", "prlzerebe1nettc", "priz3r3b3ln3ttc", "priz3r3b3in3ttc", "priz3r3b31n3ttc", "pr1z3r3b3ln3ttc", "pr1z3r3b3in3ttc", "pr1z3r3b31n3ttc", "prlz3r3b3ln3ttc", "prlz3r3b3in3ttc", "prlz3r3b31n3ttc", "mesodealcom", "mes0dealcom", "mesodealco0m", "m3sodealcom", "friskgamecom", "fr1skgamecom",
            "frlskgamecom", "m3so", "mes0", "m3s0", "n3xon", "nex0n", "n3x0n", "gam3", "n3t", "gamecorn", "garnecom", "lolgame", "1olgame", "10lgame", "1oigame", "10igame", "1o1game", "101game", "lo1game", "l01game", "loigame", "l0igame", "iolgame", "i0lgame", "io1game", "i01game", "ioigame", "i0igame", "g4me", "g4m3", "itemratecom", "ratecom", "r4tecom", "rat3com", "r4t3com", "gamecom", "salecom", "s4lecom", "gamekoo",
            "guygamecom", "gamegoldcom", "goldcom", "38dugameus", "garnecorn", "xxclty", "xxc1ty", "Thsalecom", "fvck", "thefirststorycom", "wow4scom", "mapleftptk", "mapieftptk", "thsaiecom", "fuuck", "Shiit", "saiecom", "sa1ecom", "5alecom", "Garnekoocom", "Garnekoo", "Oosalecom", "Oosaiecom", "wow4snet", "mmodocom", "gm963com",
            "vi4scom", "maplestoryio", "maplestorysh", "itemshopscom", "itemshopsnet", "game8thcom", "mmobe", "maplestorymesos", "fastmesos", "goldceo", "mygamebuy", "sale2k", "mapleftp", "sellaccount", "ogpal", "msogpal", "gamecuu", "thepowerlevel", "thepowerIeveI", "thepowerleveI", "thepowerIevel"
        };
        for (int w = 0; w < blockedWords.length; w++) {
            if (newname.replaceAll(" ", "").toLowerCase().contains(blockedWords[w])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlockedName(String newname) {
        String[] blockedWords = { // all curse's right away from WZ! [Credits to Super_Sonic]
            " ", ",", ".", "?", "-", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "[", "]", "{", "}", "|", ":", ";", "/", "<", ">", "/", "?",
            //numbers
            "1esb1an", "455h0Ie", "455h0le", "455m45ter", "455much", "455munch", "4n4I5ex", "4n4Isex", "4n4l5ex", "4n4lsex", "4ssh0Ie", "4ssh0le", "4ssm4ster", "4ssmuch", "4ssmunch", "5chI0ng", "5chl0ng", "5h1b4I", "5h1b4l", "5h1baI", "5h1bal", "5h1t", "5h1z", "5hibaI", "5hibal", "5hit", "5hiz", "5hlbal", "5hlt", "5hlz",
            // letter a
            "a55h0Ie", "a55h0le", "a55ma5ter", "a55much", "a55munch", "anaI5ex", "anaIsex", "anal", "anal5ex", "analsex", "assh0Ie", "assh0le", "asshole", "asslover", "ass-lover", "assmaster", "assmuch", "assmunch",
            // letter b
            "b14tch", "b1atch", "b1tch", "b1tch455", "b1tcha55", "b1tchass", "b1y0tch", "b1z4tch", "b1zatch", "b1zn4tch", "b1znatch", "b45t4rd", "b4IIz", "b4llz", "b4st4rd", "balls", "ballz", "bastard", "beeeech", "beeotch", "beeyotch", "beyotch", "bI0wj0b", "bI0wme", "bi4tch", "biaatch", "biatch", "biiiiitch", "biiiitch", "biiitch", "biitch", "biotch", "bitch", "bitch4ss", "bitcha55", "bitchass", "bittch", "biy0tch", "biyaaatch", "biyatch", "biyotch", "biz4tch", "bizatch", "bizn4tch", "biznatch", "bl0wj0b", "bl0wme", "bl4tch", "blatch", "bllltch", "blotch", "blowjob", "blowme", "bltch", "bltch4ss", "bltcha55", "bltchass", "bly0tch", "blyotch", "blz4tch", "blzatch", "blzn4tch", "blznatch", "buttmunch", "bytch",
            // letter c
            "c8", "ch00ch1e", "ch00chie", "ch00chle", "ch1nk", "chink", "choochie", "cI1t0r1s", "cIit", "cIit0ri5", "cIit0ris", "cl1t0r1s", "clit", "clit0ri5", "clit0ris", "clitoris", "cllt", "cllt0rl5", "cllt0rls", "cock", "condom", "cottonpick", "cum", "cunnt", "cunt", "chunin",
            // letter d
            "d0ggy5tyIe", "d0ggy-5tyIe", "d0ggy5tyle", "d0ggy-5tyle", "d0ggystyIe", "d0ggystyle", "d1ck", "damn", "deepthr04t", "deepthroat", "dick", "dildo", "dilhole", "dlldo", "doggystyle", "doggy-style", "dumbfuck", "dyke",
            // letter e
            "eatme",
            // letter f
            "fag", "fagg0t", "faggot", "fetish", "fucker", "fuck", "fuc", "fuker", "fukkin", "fuk", "fuuk",
            // letter g
            "g00k", "g4y", "gaaay", "gaay", "gay", "gizay", "goddamn", "goddmamn", "gook", "genin",
            // letter h
            "havesex", "homo", "hong", "hoochie", "hooters", "hokage",
            // letter i
            "Iesb0",
            // letter j
            "j4ck0ff", "jackoff", "jack-off", "jap5", "japs", "jerkme", "jerk-off", "jiz", "jizm", "jew", "jounin",
            // letter k
            "kike",
            // letter l
            "lesb0", "lesbian", "lesbo", "lezbo",
            // letter m
            "m1ss10nary", "mastabate", "mastarbate", "masterbate", "masturbate", "missi0n4ry", "missi0nary", "missionary", "mlssl0n4ry", "mlssl0nary", "mofucc", "mothafuc", "mutha", "mytit",
            // letter n
            "n1gger", "negro", "niga", "nigar", "niger", "nigga", "niggar", "nigger", "nipple", "nlgger", "nutsack",
            // letter o
            "orgasm", "orgy", "p0rn", "pen15", "penis", "phuck", "porn", "porno", "pr0n", "pussie", "pussy",
            // letter r
            "retard", "rubmy",
            // letter s
            "schlong", "sexfreak", "sexmachine", "sexual", "sexwith", "sh1t", "shibal", "shiiiiiit", "shit", "shiz", "shlt", "spank", "sperm", "spum", "ssh1t", "sshit", "sshlt", "suckme", "suckmy", "sexy", "sannin", "system",
            // letter t
            "titty", "tltty", "tw4t", "twat",
            // letter v
            "v4g1n4", "vagina",
            // letter w
            "w4nker", "wackoff", "wack-off", "wanker", "whatthefukk", "whore",
            // letter y
            "y0urt1t", "y0urtit", "y0urtlt", "yourtit",
            // others ;D
            "coon", "Poontang", "Poon", "Coon", "Koon", "Porchmonkey", "Porch", "Spic", "Yellowboy", "Slant", "nazzi", "Nazi", "Adolph", "fuck'", "Gestapo", "Fuhrer", "F?rer", "Himmler", "Goebbles", "Crip", "Pedobear", "Pedo", "Slut", "fuker", "Skank", "Skeezer", "Whore", "drivebitch", "fuckgm", "fuckugm", "fukugm", "fucugm", "motherfuckin'", "move", "movebitch", "asshat", "hitler", "splooge", "spooge",
            "skeet", "blow", "asshole'", "dick'", "titties", "assface", "douche", "doosh", "dooshe", "kraut", "kyke", "wop", "fuckjoo", "pollock", "jigaboo", "pecker", "peckerwood", "fck'", "ragheaD", "wetback", "masturbate", "fck", "sht", "rape", "brogamecom", "freewebscomnxking", "gamekoocom", "lolsalecom", "maplestorygoldscom", "mesorichcom", "mmogresourcecom", "mmovpcom", "msvgoldscom", "msgodmodecom", "msgoldscom", "myigskycom", "nexonmesospiczocom",
            "power4gamecom", "rpgbuckscom", "togexcom", "zedgamecom", "maplegold", "msgold", "c0m", "maplestorygoldscom", "lolsalecom", "mapleaidcom", "power4gamecom", "zedgamenet", "thsalecom", "msmesoseu", "brosalecom", "thvendcom", "igscom", "gamekoocom", "gosalecom", "gameimcom", "sellmscom", "mmogamesalecom", "igamegardencom", "swagvaultcom", "thepowerlevelcom", "gamekoo", "gamek00", "gamek0o", "gameko0", "power4game", "p0wer4game", "xxcity", "mmogamesale", "mm0gamesale", "mmogamesaie", "guygame", "pkxman", "igs",
            "lgs", "oosale", "00sale", "o0sale", "0osale", "lolsale", "l0lsale", "ioisale", "ioisaie", "i0isale", "i0isaie", "zedgame", "brosale", "br0sale", "msmesos", "msmes0s", "thepowerlevel", "thep0werlevel", "thepowerievei", "thep0werievei", "mapleaid", "maplestorygolds", "atmgame", "yangyuqiang", "ugamegold", "ugamegoid", "ugameg0ld", "ugameg0id", "swagvault", "igamegarden", "lgamegarden", "sellms", "gameim", "thvend",
            "mysupersales", "igecom", "peons4hire", "gmworker", "xxc1ty", "mmogamesa1e", "mm0gamesa1e", "1gs", "oosa1e", "00sa1e", "o0sa1e", "0osa1e", "1o1sa1e", "101sa1e", "brosa1e", "br0sa1e", "thepower1eve1", "thep0wer1eve1", "map1ea1d", "map1estorygo1ds", "yangyuq1ang", "ugamego1d", "ugameg01d", "swagvau1t", "1gamegard", "se11ms", "game1m", "mysupersa1es", "1ge", "peons4h1re", "lgecom", "1gecom1", "ig3com", "1g3com", "nexon3xwus", "n3xon3xwus", "n3x0n3xwus", "nex0n3xwus", "prizerebelnettc",
            "prizerebe1nettc", "prizerebeinettc", "pr1zerebelnettc", "pr1zerebe1nettc", "pr1zerebeinettc", "prlzerebelnettc", "prlzerebeinettc", "prlzerebe1nettc", "priz3r3b3ln3ttc", "priz3r3b3in3ttc", "priz3r3b31n3ttc", "pr1z3r3b3ln3ttc", "pr1z3r3b3in3ttc", "pr1z3r3b31n3ttc", "prlz3r3b3ln3ttc", "prlz3r3b3in3ttc", "prlz3r3b31n3ttc", "mesodealcom", "mes0dealcom", "mesodealco0m", "m3sodealcom", "friskgamecom", "fr1skgamecom",
            "frlskgamecom", "m3so", "mes0", "m3s0", "n3xon", "nex0n", "n3x0n", "gam3", "n3t", "gamecorn", "garnecom", "lolgame", "1olgame", "10lgame", "1oigame", "10igame", "1o1game", "101game", "lo1game", "l01game", "loigame", "l0igame", "iolgame", "i0lgame", "io1game", "i01game", "ioigame", "i0igame", "g4me", "g4m3", "itemratecom", "ratecom", "r4tecom", "rat3com", "r4t3com", "gamecom", "salecom", "s4lecom", "gamekoo",
            "guygamecom", "gamegoldcom", "goldcom", "38dugameus", "garnecorn", "xxclty", "xxc1ty", "Thsalecom", "fvck", "thefirststorycom", "wow4scom", "mapleftptk", "mapieftptk", "thsaiecom", "fuuck", "Shiit", "saiecom", "sa1ecom", "5alecom", "Garnekoocom", "Garnekoo", "Oosalecom", "Oosaiecom", "wow4snet", "mmodocom", "gm963com",
            "vi4scom", "maplestoryio", "maplestorysh", "itemshopscom", "itemshopsnet", "game8thcom", "mmobe", "maplestorymesos", "fastmesos", "goldceo", "mygamebuy", "sale2k", "mapleftp", "sellaccount", "ogpal", "msogpal", "gamecuu", "thepowerlevel", "thepowerIeveI", "thepowerleveI", "thepowerIevel"
        };
        for (int w = 0; w < blockedWords.length; w++) {
            if (newname.replaceAll(" ", "").toLowerCase().contains(blockedWords[w])) {
                return true;
            }
        }
        return false;
    }

    public static String getDateTime(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sd = new SimpleDateFormat("MMMMMMMMMMM dd, yyyy 'at' hh:mm:ss a");
        return sd.format(date);
    }

    public static String getCardinal(int number) {
        while (number > 100) {
            number -= 100;
        }
        if ((number - 1) % 10 == 0 && number != 11) {
            return "st";
        }
        if ((number - 2) % 10 == 0 && number != 12) {
            return "nd";
        }
        if ((number - 3) % 10 == 0 && number != 13) {
            return "rd";
        }
        return "th";
    }
}
