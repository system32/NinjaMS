/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.messages.commands.gm;
import java.util.List;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.UnbanProcessor;

/**
 *
 * @author Owner
 */
public class PardonCommand implements GMCommand{
 public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("-1")) {
            if(splitted.length != 2){
                mc.dropMessage("Wrong syntax. Too bad so sad");
                return;
            }
            List<String> ret = UnbanProcessor.unban(splitted[1]);
            for(String lol : ret){
                mc.dropMessage(lol);
            }
        }
    }

    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("-1", "ign", "unban command"),};
    }
}
