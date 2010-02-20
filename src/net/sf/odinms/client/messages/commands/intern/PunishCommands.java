/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.intern;

import static net.sf.odinms.client.messages.CommandProcessor.getNamedIntArg;

import java.text.DateFormat;
import java.util.Calendar;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class PunishCommands implements InternCommand {

    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[]{
                    new InternCommandDefinition("unmute", "ign", "unmutes the person"),
                    new InternCommandDefinition("pmute", "ign", "perma mutes a person"),
                    new InternCommandDefinition("tban", "ign", "Temp ban a person for an Hr.")
        };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception{
        if (splitted[0].equalsIgnoreCase("unmute")) {
            if (splitted.length != 2) {
                mc.dropMessage("learn to read $commands");
                return;
            }
            MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (noob == null) {
                mc.dropMessage("this noob does not exist or in a diff channel");
            } else {
                noob.unMute();
                mc.dropMessage("unmuted");
            }
        } else if (splitted[0].equalsIgnoreCase("pmute")) {
            if (splitted.length != 2) {
                mc.dropMessage("learn to read $commands");
                return;
            }
            MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (noob == null) {
                mc.dropMessage("this noob does not exist or in a diff channel");
            } else {
                noob.mute(2);
                mc.dropMessage("unmuted");
            }
        } else if (splitted[0].equalsIgnoreCase("tban")){
            Calendar tempB = Calendar.getInstance();
            String originalReason = StringUtil.joinStringFrom(splitted, 2);
            if (splitted.length < 3 || originalReason == null) {
                 mc.dropMessage("Syntax helper: !tempban <name> <reason>");
                 return;
            }
            int gReason = 7;
            String reason = c.getPlayer().getName() + " tempbanned " + splitted[1] + ": " + originalReason;
            DateFormat df = DateFormat.getInstance();
            tempB.set(tempB.get(Calendar.YEAR), tempB.get(Calendar.MONTH), tempB.get(Calendar.DATE), tempB.get(Calendar.HOUR_OF_DAY) + 2, tempB.get(Calendar.MINUTE));
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                int accId = MapleClient.findAccIdForCharacterName(splitted[1]);
                if (accId >= 0 && MapleCharacter.tempban(reason, tempB, gReason, accId)) {
                    mc.dropMessage("The character " + splitted[1] + " has been successfully offline-tempbanned till " +
                            df.format(tempB.getTime()) + ".");
                } else {
                    mc.dropMessage("There was a problem offline banning character " + splitted[1] + ".");
                }
            } else {
                victim.tempban(reason, tempB, gReason);
                mc.dropMessage("The character " + splitted[1] + " has been successfully tempbanned till " +
                        df.format(tempB.getTime()));
            }
        }
    }
}
