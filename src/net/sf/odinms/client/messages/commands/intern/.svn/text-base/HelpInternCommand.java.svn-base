/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.messages.commands.intern;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;

/**
 *
 * @author Admin
 */
public class HelpInternCommand implements InternCommand {



    public void execute(MapleClient c, MessageCallback mc, String[] splittedLine) throws Exception {
       int page = CommandProcessor.getOptionalIntArg(splittedLine, 1, 1);
        CommandProcessor.getInstance().dropInternHelp(c.getPlayer(), mc, page);
    }

    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[] {
            new InternCommandDefinition("commands", "<page number>", "shows the list of Available commands"),
            new InternCommandDefinition("help", "<page number>", "shows the list of Available commands"),
        };
    }
}
