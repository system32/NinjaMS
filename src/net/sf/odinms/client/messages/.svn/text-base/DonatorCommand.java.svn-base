/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages;

import net.sf.odinms.client.MapleClient;

/**
 *
 * @author Admin
 */
public interface DonatorCommand {

    DonatorCommandDefinition[] getDefinition();

    void execute(MapleClient c, MessageCallback mc, String[] splittedLine) throws Exception, IllegalCommandSyntaxException;
}
