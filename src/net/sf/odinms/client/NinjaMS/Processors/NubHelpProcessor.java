/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.Processors;

import net.sf.odinms.client.MapleCharacter;

/**
 *
 * @author Owner
 */
public class NubHelpProcessor {

    private static NubHelpProcessor instance;

    /**
     * Instance to call from other places.
     * @return
     */
    public static NubHelpProcessor getInstance() {
        if (instance == null) {
            instance = new NubHelpProcessor();
        }
        return instance;
    }

    public void chooseFAQ(MapleCharacter pl, String msg){
        chooseFAQ(pl, null, msg);
    }

    public void chooseFAQ(MapleCharacter pl, String nub, String msg) {
        String message = "Error";
        if (msg.equalsIgnoreCase("tao")){
            message = taoInfo();
        } else if (msg.equalsIgnoreCase("rasengan")){
            message = rasenganInfo();
        } else if (msg.equalsIgnoreCase("kagebunshin")){
            message = kagebunshinInfo();
        } else if (msg.equalsIgnoreCase("koc")){
            message = kocInfo();
        } else if (msg.equalsIgnoreCase("training")){
            message = trainingInfo();
        } else if (msg.equalsIgnoreCase("meso")){
            message = mesoInfo();
        } else if (msg.equalsIgnoreCase("kpq")){
            message = kpqInfo();
        } else if (msg.equalsIgnoreCase("shiken")){
            message = shikenInfo();
        } else if (msg.equalsIgnoreCase("status")){
            message = statusInfo();
        } else if (msg.equalsIgnoreCase("morph")){
            message = morphInfo();
        } else if (msg.equalsIgnoreCase("chairs")){
            message = chairInfo();
        } else if (msg.equalsIgnoreCase("joko")){
            message = jokoInfo();
        } else if (msg.equalsIgnoreCase("ninja")){
            message = ninjaInfo();
        } else if (msg.equalsIgnoreCase("tensu")){
            message = tensuInfo();
        } else if (msg.equalsIgnoreCase("medal")){
            message = medalInfo();
        } else if (msg.equalsIgnoreCase("jq")){
            message = jqInfo();
        } else if (msg.equalsIgnoreCase("donation")){
            message = donationInfo();
        } else if (msg.equalsIgnoreCase("gmsmode")){
            message = gmsModeInfo();
        } else if (msg.equalsIgnoreCase("modes")){
            message = modeInfo();
        } else if (msg.equalsIgnoreCase("look")){
            message = lookInfo();
        } else if (msg.equalsIgnoreCase("nlc")){
            message = nlcInfo();
        } else if (msg.equalsIgnoreCase("keyboard")){
            message = kbInfo();
        }
        if(message.equalsIgnoreCase("error")){
            String[] omg = {"tao","rasengan","kagebunshin","koc","training","meso",
            "kpq","shiken","status","morph","chairs","joko","ninja","tensu","medal",
            "jq","donation","gmsmode","modes","look","nlc","keyboard"};
            StringBuilder sb = new StringBuilder();
            sb.append(" The option you want is not available at this time. These are the options : ");
            for(String omgla : omg){
                sb.append(omgla);
                sb.append(" / ");
            }
            pl.dropMessage(sb.toString());
            return;
        }
        if(nub != null){
            NoticeProcessor.sendPPinkNotice(pl, nub, message);
            pl.dropMessage("message sent to the Noob");
        } else {
            NoticeProcessor.sendSay(pl, message);
        }
    }

    private String taoInfo() {
        String msg = " Tao Of Sight is the currency in NinjaMS.";
        msg += " 1 tao is worth 2 billion mesos.";
        msg += " You can buy tao by using command @buytao when you have 2 billion mesos using command @buytao.";
        msg += " You can sell tao and get 2 billion mesos using Command @mesarz";
        return msg;
    }

    private String rasenganInfo() {
        String msg = " Rasengan is GM Roar.";
        msg += " There is a 13 step quest to obtain Rasengan in NinjaMS.";
        msg += " Rasengan does not use any HP or MP to use and does 1000% damage.";
        msg += " It can be used with any job and any level and any weapon.";
        msg += " Rasengan is a AOE skill, that is it attack your whole screen.";
        return msg;
    }

    private String kagebunshinInfo() {
        String msg = " Kage bunshin = Shadow clones.";
        msg += " You have to complete a 1 step mini quest from NPC Joko to obtain this.";
        msg += " The Shadow clones does not help you in training. They are only for the cool looks.";
        msg += " They can only be used in Henesys or Freemarket.";
        msg += " The limite for Kagebunshin for rookie is 10 and genin is 15.";
        msg += " The command to activate Kagebunshin is : @kagebunshin <amount>.";
        return msg;
    }

    private String kocInfo() {
        String msg = " Knight Of Cygnus is Available after 3 rebirths. ";
        msg += " Talk to Shadrion in henesys or use command @clan and";
        msg += " choose path management to change your path from adventurer to cygnus or vice versa.";
        msg += " When you change paths, you will lose your skills and you will have to obtain";
        msg += " skills of your path by changing jobs.";
        msg += " After 3 RB you can change job anytime by talking to the Job Advacer.";
        msg += " You cannot have Adventurer and KOC skills at the same time unless you are Jounin.";
        msg += " You will have a different Keyboard for KOC, Which will be saved and used again when you change paths to KOC";
        msg += " When you change paths to KOC, your adventurer Keyboard will be saved and can be reused when you change back to Adventurer";
        return msg;
    }

    private String trainingInfo() {
        String msg = " Training and gaining Rebirths are very easy in NinjaMS.";
        msg += " For beginners below 10 rebirths, FM rooms 2, 3, 13 and 14 are the best places to train and earn mesos.";
        msg += " For those between 20 ~ 50 Rb FM rooms 2, 3, 10, 11, 12 and 5, are the best places to Train and earn mesos.";
        msg += " For those between 50 ~ 100 Rb FM rooms 8, 7, 10, 11, and 12 are the best places train.";
        msg += " For those above 100 Rebirths FM rooms 10, 6, 8 and 9 are the best places Train.";
        msg += " PQ in GMS mode is also a good way to Gain rebirths and items if you are above 5 Rebirths.";
        msg += " If you are good at Jumping, Our jump quests can be a good way to earn mesos. ";
        return msg;
    }

    private String mesoInfo() {
        String msg = " Mesos can be easily earned as you train.";
        msg += " The meso rates are very high and it is very easy to earn mesos";
        msg += " You can also gain some easy mesos by hunting etc items like Ores and Selling them to other players";
        msg += " You can gain more than 10 Tao of sight by Voting. Vote rewards can be claimed from NPC charles in Henesys";
        msg += " You can also gain mesos by selling diary pages which you can get from Jump Quests.";
        msg += " Easiest way to gain mesos will be donating money to the server to keep it up :p.";
        msg += " Party quest and Ninja Shiken are also reckoned to be a good way to earn $_$_$_$_$_$__$";
        return msg;
    }

    private String kpqInfo() {
        String msg = " KPQ is KErning Party Quest. This can be started by Talking to NPC Lakelis in Henesys.";
        msg += " You need to be in GMS mode 1 to be able to start this PQ.";
        msg += " You can turn on GMS mode by talking to NPC duru in Henesys.";
        msg += " You need a party of 3 or 4 players";
        msg += " The rewards are a lot better than what it is in GMS";
        msg += " As you clear all the stages, you will gain 4 Rebirths and the Ap accordingly.";
        msg += " While you are in KPQ all your ap gained will be added to storage AP and stored until you leave GMS mode.";
        msg += " Ap can be restored from storage AP when you are not in GMS mode by using command : @restoreap <amount>.";
        msg += " The rewards of KPQ are customised in our server and includes Timesless equipments and GM scrolls.";
        return msg;
    }

    private String shikenInfo() {
        String msg = " Ninja Shiken is commonly known as Feild of Judgement in many other servers";
        msg += " You need to be in GMS mode 3 to be able to Enter Ninja Shiken";
        msg += " You can enter Ninja Shiken 1 minute before the event starts by talking to NPC Ria In Henesys.";
        msg += " Ninja Shiken is a automated event and is open once every 4 hrs.";
        msg += " The rewards of Ninja shiken are customised in our server and includes Timesless equipments and GM scrolls";
        return msg;
    }

    private String statusInfo() {
        String msg = " The GM status in game is shown by the User title which you can see in Smega. ";
        msg += " Hokage : It's a title for NinjaMS Administrator. The owner of NinjaMS.";
        msg += " Sannin : It's a title for NinjaMS Co-Administrator.";
        msg += " Jounin : It's a title for NinjaMS Game Master. Have more powers than Chunins.";
        msg += " Chunin : It's a title for NinjaMS Junior Game Master aka Intern.";
        msg += " Genin : It's a title for NinjaMS Donators. Has more features than Rookie. It can also be obtained in other ways";
        msg += " Rookie : It's a title for NinjaMS Normal Players....aka nubshit.";
        return msg;
    }

    private String morphInfo(){
        String msg = " Morphs and Transformation potions can be obtained from Joko the Fat ninja NPC in Henesys";
        msg += " They will cost you 100 Jr sentinel shell pieces for 10.";
        msg += " Jr sentinel shell pieces can be hunt from Jr Sentinels in Orbis Tower";
        return msg;
    }

    private String chairInfo(){
        String msg = " Chairs can be obtained from Joko the Fat ninja NPC in Henesys";
        msg += " They will cost you 500 cactus for 1.";
        msg += " Jr sentinel shell pieces can be hunt from Jr Sentinels in Orbis Tower";
        return msg;
    }

    private String jokoInfo(){
        String msg = " Joko is the Fat ninja NPC in Henesys";
        msg += " He manages Chairs, Mounts, Morphs, Rasengan quest and Kage bunshin Quest";
        msg += " Talk to him to know more about him";
        return msg;
    }

    private String ninjaInfo(){
        String msg = " NinjaMS - The Awesome server you are currently playing. It was previously Known as Wagga";
        msg += " People to be remembered when you mention ninjaMS are : ";
        msg += " CupOfTeA, Peter, Oliver, TheBass, FairyGaia, BeerBaron, Hope, Rando, WildnSmexy, Kimmchii,";
        msg += " Kasper, Cocolatte, xDutchGunn, Elektra, Janey, ThiefOfRoses and most importantly Kelly";
        msg += " and all the players who ever played this server and supported us financially and morally.";
        return msg;
    }

    private String tensuInfo(){
        String msg = " Tensu - You gain these by Winning events or voting for the server.";
        msg += " You can claim the tensu for voting by talking to NPC Charles in Henesys.";
        msg += " You can gain genin status in this server by saving up 100 NinjaTensu";
        msg += " NinjaTensu is also used in Quests like Magic scales quest and Rasengan Quest";
        return msg;
    }

    private String medalInfo(){
        String msg = " You will gain medals as you rebirth. on 10, 100, 500 and 1000 RB. They have special Stats.";
        msg += " There are many special medals which you can only gain from special events or PQ or voting";
        msg += " All donor medals are only available for donators. ";
        msg += " Nine-Tailed Vanquisher medals are only Staff special and cannot be obtained.";
        msg += " DO NOT BEG FOR MEDALS!";
        return msg;
    }

    private String jqInfo(){
        String msg = " JQ - JumpQuests are customized in our server.";
        msg += " JQ are available in Channel 3 for those who have more than 10 rebirths.";
        msg += " Fiona npc in Extreme right side of the Henesys town helps you start the JQ";
        msg += " and waits for you at the end to give you rewards.";
        msg += " For every JQ you finish you will gain a Diary Page.";
        msg += " There are 10 different Diary pages ergo 10 different JQs.";
        msg += " You need these diary pages to finish Quests like Rasengan Quest.";
        msg += " There are many other rewards for Diary pages. You can find out about the rewards by talking to NPC Fiona";
        return msg;
    }

    private String donationInfo(){
        String msg = " As we run by donations, you are most welcome to donate to keep the server up";
        msg += " Donation Rewards are listed in NPC The bass";
        msg += " We only accept donation through Paypal";
        msg += " Your donations will only be used to cover server costs.";
        msg += " Genin status can be obtained by donating $20 total. (it may be 1 donation of 20$ or splitted donation)";
        msg += " Genin status will allow you to have special commands special training maps and higher rates than regular players.";
        return msg;
    }

    private String gmsModeInfo() {
        String msg = " GMS mode is originally Oliver's Idea. ";
        msg += " With his permission, We have implemented it in our server with many variations.";
        msg += " You need to be in GMS mode to start Party Quests and do some boss runs.";
        msg += " There are 5 different GMS modes, which corresponds to the level 29, 49, 69, 149 and 199.";
        msg += " While you are in GMS mode, your Level will not change. How ever, you will be gaining Ap to your StorageAP.";
        msg += " You will not be able to equip any Stat equipments when you are in GMS modes.";
        msg += " You can only use Items that can be obtained in GMS legitly. You can use Scrolled items.";
        msg += " You will be restricted to one job. You cannot mix jobs when you are in GMS mode.";
        msg += " You can change jobs anytime when you are in GMS mode by talking to JobAdvancer NPC in Henesys.";
        msg += " You will have a different keyboard and macro settings for GMS mode. You can save it and use it again when you go back to GMS mode";
        msg += " The PQs will auto rebirth you as they give you level up rewards for completed stages.";
        msg += " You will not gain EXP from monsters while you are in GMS mode.";
        msg += " You can turn off GMS mode by Talking to NPC DURU in Henesys.";
        return msg;
    }

    private String modeInfo(){
        String msg = " Modes - There are 4 different modes in NinjaMS -";
        msg += " Kyubi, sage, Hachibi and Shakaku modes ";
        msg += " All these modes will add a 25% extra damage which will be shown when you attack a monster.";
        msg += " Kyubi mode increases EXP rate, Sage - meso rate, Hachibi - drop, shakaku - bossdrop.";
        msg += " These modes can be turned on by talking to NPC Shadrion (using Command : @clan)";
        msg += " They will cost you some Taos and Dragon hearts.";
        msg += " Dragon hearts can be hunt from Wyverns in Leafre";
        msg += " These modes will only last for 15 minutes. ";
        return msg;
    }

    private String lookInfo(){
        String msg = " How to change your looks from the starting ninja character:";
        msg += " Talk to ICutHair and ICutHairToo NPCs to change your hair, hair color, eyes, eye color and skin color for free.";
        msg += " If you are a male character, talk to ICutHairToo NPC to change your looks.";
        msg += " If you are a female character, talk to ICutHair NPC to change your looks.";
        return msg;
    }
    private String nlcInfo(){
        String msg = " To buy equips, go and take a look at the NLC mall.";
        msg += " Each item costs 20 Tao and will have your name tagged on it.";
        msg += " If you are running low on funds, then use @shop or talk to Francois NPC in henesys for equips and weapons.";
        msg += " For nx items use @buynx and purchase NX items through the Cash Shop if you are running low on money.";
        return msg;
    }

    private String kbInfo(){
        String msg = " The Keyboards & macros will be wiped when you change paths or go into GMS Mode.";
        msg += " However, you can save a keyboard for each path and a keyboard for GMSMode.";
        msg += " When ever you need it, you can reload the keyboard.";
        msg += " Use commands : \"@saveadvkb\", \"@savekockb\", \"@savegmskb\", to save them.";
        msg += " Use commands : \"@loadadvkb\", \"@loadkockb\", \"@loadgmskb\", to load them.";   
        return msg;
    }
}
