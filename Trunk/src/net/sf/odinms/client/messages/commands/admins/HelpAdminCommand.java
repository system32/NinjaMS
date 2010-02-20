/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.admins;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.AdminCommand;
import net.sf.odinms.client.messages.AdminCommandDefinition;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.client.messages.MessageCallback;

/**
 *
 * @author Admin
 */
public class HelpAdminCommand implements AdminCommand {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splittedLine) throws Exception{
        int page = CommandProcessor.getOptionalIntArg(splittedLine, 1, 1);
        CommandProcessor.getInstance().dropAdminHelp(c.getPlayer(), mc, page);
    }

    @Override
    public AdminCommandDefinition[] getDefinition() {
        return new AdminCommandDefinition[]{
                    new AdminCommandDefinition("help", "[page - defaults to 1]", "Shows the help"),
                    new AdminCommandDefinition("command", "[page - defaults to 1]", "Shows the help"),};
    }
}
