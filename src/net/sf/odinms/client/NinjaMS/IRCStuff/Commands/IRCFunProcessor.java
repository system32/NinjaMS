/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.IRCStuff.Commands;

import net.sf.odinms.client.NinjaMS.IRCStuff.MainIRC;

/**
 *
 * @author Admin
 */
public class IRCFunProcessor {

    public static void process(String message, String sender, String channel) {
        message = message.toLowerCase();
        String[] splitted = message.split(" ");
        if (splitted[0].startsWith("hi") || splitted[0].startsWith("hello")) {
            if (splitted[1].equals("ninjabot") || splitted[1].contains("ninja")) {
                String[] msg = {" I don't want to greet you today",
                                " Hi ", " Ohaiyo ", " Hello ", " Hi there" ,
                                "  Hello thar ", " arlos ", " Hey hey bro",
                                " Hey tharbish ", " Omg its ", " Long time no see ",
                                " Nice to see you again ", " Yay! we meet again ",
                                " YAY! nice to see you again bro "};
                int i = (int)Math.floor(Math.random() * msg.length);
                ircMsg(channel, msg[i] + sender);
            }
        } else if (message.startsWith("ninjabot:")){
            if(message.contains("ninjabot is gay?")
                    || message.contains("are you gay?")
                    || message.contains("is ninjabot gay?")
                    || message.contains("is sunny gay?")
                    || message.contains("is sundar gay?")
                    || message.contains("is sundaranathan gay?")
                    || message.contains("is sundaranathan sampath gay?")
                    || message.contains("is sampath gay?")
                    || message.contains("is ninjams gay?")
                    || message.contains("is hokage gay?")
                    || message.contains("is admin gay?")
                    || message.contains("is sunburn gay?")
                    || message.contains("sunny is gay?")){
                //ircMsg(channel, sender + ": you are gay and so is your face. Now get lost");
                kick(channel, sender, "You are gay and so is your face. Now get lost");
            } else if (message.contains ("is gay?")){
                ircMsg(channel, sender + ": I do not have that information yet. but I think you are right :p");
            } else if (message.contains("do you love me?")) {
                String[] msg = {" May be I do :) ",
                                " I think I love you (L)",
                                " I love you sooooo much :3",
                                " I only love (#) (L)",
                                " You are too fugly to be loved by me. :p",
                                " I love only myself"};
                int i = (int)Math.floor(Math.random() * msg.length);
                ircMsg(channel, msg[i] + sender);
            }
        }
    }


    private static void ircMsg(String message) {
        MainIRC.getInstance().sendIrcMessage(message);
    }

    private static void ircMsg(String target, String message) {
        MainIRC.getInstance().sendMessage(target, message);
    }

    private static void kick(String channel, String sender, String reason){
        MainIRC.getInstance().kick(channel, sender, reason);
    }
}
