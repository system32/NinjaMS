/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.donator;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.client.messages.DonatorCommand;
import net.sf.odinms.client.messages.DonatorCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;

/**
 *
 * @author Admin
 */
public class HelpDonatorCommand implements DonatorCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("commands") || splitted[0].equalsIgnoreCase("help")) {
            int page = CommandProcessor.getOptionalIntArg(splitted, 1, 1);
            CommandProcessor.getInstance().dropDonatorHelp(c.getPlayer(), mc, page);

        } else {
            mc.dropMessage(splitted[0] + " is not a valid command");
        }
    }

    public DonatorCommandDefinition[] getDefinition() {
        return new DonatorCommandDefinition[]{
                    new DonatorCommandDefinition("help", "", "shows a list of available commands"),};
    }
}
