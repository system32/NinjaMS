/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.intern;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class BanCommands implements InternCommand {

    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[] {
            new InternCommandDefinition("ban", "ign reason", "bans a person")
        };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equals("ban")) {
            if (splitted.length < 3) {
                mc.dropMessage("You should be sacked for not knowing the ban command syntax");
                return;
            }
            String originalReason = StringUtil.joinStringFrom(splitted, 2);
            String reason = c.getPlayer().getName() + " banned " + splitted[1] + ": " + originalReason;
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                String readableTargetName = MapleCharacterUtil.makeMapleReadable(target.getName());
                String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                reason += " (IP: " + ip + ")";
                target.ban(reason);
                mc.dropMessage("Banned " + readableTargetName + " ipban for " + ip + " reason: " + originalReason);
            } else {
                if (MapleCharacter.ban(splitted[1], reason, false)) {
                    mc.dropMessage("Offline Banned " + splitted[1]);
                } else {
                    mc.dropMessage("Failed to ban " + splitted[1]);
                }
            }
        }
    }
}
