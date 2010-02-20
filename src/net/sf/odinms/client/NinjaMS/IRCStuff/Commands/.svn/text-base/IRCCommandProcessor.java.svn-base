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


    public static boolean processCommand(String command, String sender, int power){
        if (command.charAt(0) == '\\' && power > 0) {
            String[] splitted = command.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            VoicedUserCommands.execute(sender, splitted);
        } else if(command.charAt(0) == '*' && power > 1){
            String[] splitted = command.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            OPUserCommands.execute(sender, splitted);
        }
        return false;
    }
}
