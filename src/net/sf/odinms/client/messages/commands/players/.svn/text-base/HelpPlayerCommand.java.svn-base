/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;

/**
 *
 * @author Admin
 */
public class HelpPlayerCommand implements PlayerCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splittedLine) throws Exception {
        int page = CommandProcessor.getOptionalIntArg(splittedLine, 1, 1);
        CommandProcessor.getInstance().dropPlayerHelp(c.getPlayer(), mc, page);
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("commands", "<page number>", "Shows a list of available commands"),
                    new PlayerCommandDefinition("help", "<page number>", "Shows a list of available commands"),
                    new PlayerCommandDefinition("command", "<page number>", "Shows a list of available commands")
                };
    }
}
