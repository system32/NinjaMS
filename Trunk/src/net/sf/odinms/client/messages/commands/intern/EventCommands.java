/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.messages.commands.intern;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;

/**
 *
 * @author Admin
 */
public class EventCommands implements InternCommand{

    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[]{
          new InternCommandDefinition("forceremove", "ign", "removed clones and pets from a person"),
        };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("forceremove")){
            MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (noob != null){
                noob.removeClones();
                noob.removePets();
            }
        }
    }

}
