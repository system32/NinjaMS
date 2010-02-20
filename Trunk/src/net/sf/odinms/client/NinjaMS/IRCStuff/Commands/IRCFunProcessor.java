/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.IRCStuff.Commands;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class IRCFunProcessor {

    public static void process(String message, String sender, String channel) {
        message = message.toLowerCase();
        String[] splitted = message.split(" ");
        Map<Integer, List<String>> lol = new LinkedHashMap<Integer, List<String>>();

        String[] lols = {"hi", "hello"};
        if (splitted[0].startsWith("hi") || splitted[0].startsWith("hello")) {
            if (splitted[1].equals("ninjabot")) {
                sendGreeting(sender, channel);
            }
        } else if (message.startsWith("ninjabot:")) {
            String process = StringUtil.joinStringFrom(splitted, 2);
            if (processGreeting(sender, channel, process)) {
                return;
            } else if (processGay(sender, channel, process)) {
                //ircMsg(channel, sender + ": you are gay and so is your face. Now get lost");
                return;
            } else if (message.contains("is gay?")) {
                ircMsg(channel, sender + ": I do not have that information yet. ");
            } else if (processLove(sender, channel, process)) {
                return;
            } else if (processDoYouLoveMe(sender, channel, process)){
                return;
            } else if (processHowdy(sender, channel, process)) {
                return;
            } else {
                sendUncoded(sender, channel);
            }
        }
    }

    private static void sendUncoded(String sender, String channel) {
        ircMsg(channel, sender + ": I do not know how to respond to that yet. I'm rying my best to learn more. Gimme a break!");
    }

    private static boolean processGreeting(String sender, String channel, String process) {
        String[] preset = {"hi", "hello", "hey", "ohaiyo", "hiya", "arlo", "arlos", "allo"};
        for (int i = 0; i < preset.length; i++) {
            if (process.equals(preset[i])) {
                sendGreeting(sender, channel);
                return true;
            }
            if (process.equals(preset[i] + " ninjabot")) {
                sendGreeting(sender, channel);
                return true;
            }
        }
        return false;
    }

    private static void sendGreeting(String sender, String channel) {
        String[] msg = {" I don't want to greet you today",
            " Hi ", " Ohaiyo ", " Hello ", " Hi there",
            "  Hello thar ", " arlos ", " Hey hey bro",
            " Hey tharbish ", " Omg its ", " Long time no see ",
            " Nice to see you again ", " Yay! we meet again ",
            " YAY! nice to see you again bro "};
        int i = (int) Math.floor(Math.random() * msg.length);
        ircMsg(channel, sender + ": " + msg[i]);
    }

    private static boolean processGay(String sender, String channel, String process) {
        String[] preset = {"ninjabot is gay?", "are you gay?",
            "is ninjabot gay?", "is sunny gay?", "is sundar gay?",
            "is sundaranathan gay?", "is sundaranathan sampath gay?",
            "is sampath gay?", "is ninjams gay?", "is hokage gay?",
            "is admin gay?", "is sunburn gay?", "sunny is gay?"};

        for (int i = 0; i < preset.length; i++) {
            if (process.equals(preset[i])) {
                kick(channel, sender, "You are gay and so is your face. Now get lost");
                return true;
            }
        }
        return false;
    }

    private static boolean processHowdy(String sender, String channel, String process) {
        String[] preset = {"how are you?", "howdy?", "doing good?",
            "are you doing good?", "how do you do?",
            "how r u?", "how u?", "how you doin?", "how you doing?", "how are you doing?"};
        for (int i = 0; i < preset.length; i++) {
            if (process.equals(preset[i])) {
                sendHowdyReply(sender, channel);
                return true;
            }
            if (process.equals(preset[i] + " ninjabot")) {
                sendHowdyReply(sender, channel);
                return true;
            }
        }
        return false;
    }

    private static void sendHowdyReply(String sender, String channel) {
        String[] msg = {" I'm fine thank you :) ",
            " I'm feeling horny how about you? ",
            " So nice of you to ask. Lately no one cares about me :'( ",
            " I'm not talking to you. I'm angry with you cuz you are gay ",
            " Go away noob. Don't disturb me. I'm meditating ",
            " I'm sad. Sammi did not want to make bewbies with me :'( ",
            " I'm feeling constipated. Want to buy me some laxative? ",
            " I'm fine. Just was watching porn and you came to disturb me "};
        int i = (int) Math.floor(Math.random() * msg.length);
        ircMsg(channel, sender + ": " + msg[i]);
    }

    private static boolean processLove(String sender, String channel, String process) {
        String[] preset = {"i love you", "i love you so much", "fuck me",
            "fuck you", "you suck",
            "suck a dick", "go suck sango\'s", "you are my bf", "you are my gf", "suck my dick"};
        for (int i = 0; i < preset.length; i++) {
            if (process.equals(preset[i])) {
                sendILoveYouToo(sender, channel);
                return true;
            }
            if (process.equals(preset[i] + " ninjabot")) {
                sendILoveYouToo(sender, channel);
                return true;
            }
        }
        return false;
    }

    private static void sendILoveYouToo(String sender, String channel) {
        String[] msg = {" May be I love you too :) ",
            " I think I love you too (L)",
            " I love you sooooo much :3",
            " I only love (#) (L)",
            " You are too fugly to be loved by me. :p",
            " I love only myself",
            " I'd love you too, if you were not such a pathetic loser",
            " Oh well, I'm too awesome and loved by many people around. pft fans"};
        int i = (int) Math.floor(Math.random() * msg.length);
        ircMsg(channel, sender + ": " + msg[i]);
    }

    private static boolean processDoYouLoveMe(String sender, String channel, String process) {
        String[] preset = {"do you love me?", "do you lurv me?", "are you my gf?",
        "are you my bf?", "u r my gf?", "u r my bf?", "do you (l) me?",
        "do you <3 me?", "do you :3 me?"};
        for (int i = 0; i < preset.length; i++) {
            if (process.equals(preset[i])) {
                sendILoveYou(sender, channel);
                return true;
            }
            if (process.equals(preset[i] + " ninjabot")) {
                sendILoveYou(sender, channel);
                return true;
            }
        }
        return false;
    }

    private static void sendILoveYou(String sender, String channel) {
        String msg[] = {" May be I do :) ",
            " I think I love you (L)",
            " I love you sooooo much :3",
            " I only love (#) (L)",
            " You are too fugly to be loved by me. :p",
            " I love only myself"};
        int i = (int) Math.floor(Math.random() * msg.length);
        ircMsg(channel, sender + ": " + msg[i]);
    }

    private static boolean has(String msg, String[] message) {
        for (String lol : message) {
            if (msg.contains(lol)) {
                return true;
            }
        }
        return false;
    }

    private static void ircMsg(String message) {
        MainIRC.getInstance().sendIrcMessage(message);
    }

    private static void ircMsg(String target, String message) {
        MainIRC.getInstance().sendMessage(target, message);
    }

    private static void kick(String channel, String sender, String reason) {
        MainIRC.getInstance().kick(channel, sender, reason);
    }
}
