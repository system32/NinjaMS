/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.messages;

/**
 *
 * @author Admin
 */
import net.sf.odinms.client.MapleClient;

public interface AdminCommand {
   AdminCommandDefinition[] getDefinition();
   void execute (MapleClient c, MessageCallback mc, String []splittedLine) throws Exception, IllegalCommandSyntaxException;
}