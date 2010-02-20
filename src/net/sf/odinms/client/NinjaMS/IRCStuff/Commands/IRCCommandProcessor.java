/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.NinjaMS.IRCStuff.Commands;

/**
 *
 * @author Owner
 */
public class IRCCommandProcessor {


    public static boolean processCommand(String command, String sender, int power, String channel){
        if (command.charAt(0) == '!' && power > 1) {
            String[] splitted = command.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            IRCOpCommands.execute(sender, splitted, channel);
        } else if (command.charAt(0) == '!') {
            String[] splitted = command.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            IRCCommands.execute(sender, splitted, channel);
        }
        return false;
    }
}
