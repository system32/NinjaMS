/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.messages.commands.Sannin;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.SanninCommand;
import net.sf.odinms.client.messages.SanninCommandDefinition;

/**
 *
 * @author Admin
 */
public class HelpSanninCommand implements SanninCommand {

    public SanninCommandDefinition[] getDefinition() {
        return new SanninCommandDefinition[]{
                    new SanninCommandDefinition("commands", "<page number>", "Shows list of Available commands")};
    }


    public void execute(MapleClient c, MessageCallback mc, String[] splittedLine) throws Exception, IllegalCommandSyntaxException {
        int page = CommandProcessor.getOptionalIntArg(splittedLine, 1, 1);
        CommandProcessor.getInstance().dropSanninHelp(c.getPlayer(), mc, page);
    }
}
