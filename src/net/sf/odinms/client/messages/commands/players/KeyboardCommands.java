/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import net.sf.odinms.client.KeyMapShit;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;

/**
 *
 * @author Owner
 */
public class KeyboardCommands implements PlayerCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        MapleCharacter pl = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("savegmskb")) {
            if(!pl.getCheatTracker().spam(60000, 4)){
                if(pl.getGMSMode() < 1){
                    mc.dropMessage("You cannot save GMS keyboard when you are not in GMS mode");
                    return;
                }
                KeyMapShit.saveKeymap(pl, 3);
            } else {
                mc.dropMessage("Not so often dear");
            }
        } else if (splitted[0].equalsIgnoreCase("savekockb")) {
            if(!pl.getCheatTracker().spam(60000, 4)){
                if(pl.getJobId() < 900){
                    mc.dropMessage("You cannot save KOC keyboard when you are not in KOC path");
                    return;
                }
                KeyMapShit.saveKeymap(pl, 2);
            } else {
                mc.dropMessage("Not so often dear");
            }
        } else if (splitted[0].equalsIgnoreCase("saveadvkb")) {
            if(!pl.getCheatTracker().spam(60000, 4)){
                if(pl.getJobId() > 900){
                    mc.dropMessage("You cannot save Adventurer keyboard when you are not in Adventurer path");
                    return;
                }
                KeyMapShit.saveKeymap(pl, 1);
            } else {
                mc.dropMessage("Not so often dear");
            }
        } else if (splitted[0].equalsIgnoreCase("loadgmskb")) {
            if(!pl.getCheatTracker().spam(60000, 4)){
                if(pl.getGMSMode() < 1){
                    mc.dropMessage("You cannot load GMS keyboard when you are not in GMS mode");
                    return;
                }
                KeyMapShit.loadKeymap(pl, 3);
            } else {
                mc.dropMessage("Not so often dear");
            }
        } else if (splitted[0].equalsIgnoreCase("loadkockb")) {
            if(!pl.getCheatTracker().spam(60000, 4)){
                if(pl.getJobId() < 900){
                    mc.dropMessage("You cannot load KOC keyboard when you are not in KOC path");
                    return;
                }
                KeyMapShit.loadKeymap(pl, 2);
            } else {
                mc.dropMessage("Not so often dear");
            }
        } else if (splitted[0].equalsIgnoreCase("loadadvkb")) {
            if(!pl.getCheatTracker().spam(60000, 4)){
                if(pl.getJobId() > 900){
                    mc.dropMessage("You cannot load adventurer keyboard when you are not in adventurer path");
                    return;
                }
                KeyMapShit.loadKeymap(pl, 1);
            } else {
                mc.dropMessage("Not so often dear");
            }
        }
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("savegmskb", "", "Saves your GMS mode Keyboard"),
                    new PlayerCommandDefinition("savekockb", "", "Saves your koc keyboard"),
                    new PlayerCommandDefinition("saveadvkb", "", "Saves your Adventurer keyboard"),
                    new PlayerCommandDefinition("loadgmskb", "", "Loads your GMS mode Keyboard"),
                    new PlayerCommandDefinition("loadkockb", "", "Loads your koc keyboard"),
                    new PlayerCommandDefinition("loadadvkb", "", "Loads your Adventurer keyboard"),};
    }
}
