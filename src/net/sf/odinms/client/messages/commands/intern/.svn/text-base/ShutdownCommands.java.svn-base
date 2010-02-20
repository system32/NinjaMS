/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.client.messages.commands.intern;

import java.util.Collection;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;

public class ShutdownCommands implements InternCommand {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
            Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
            int time = 60000;
            if (splitted.length > 1) {
                time = Integer.parseInt(splitted[1]) * 60000;
            }
            for (ChannelServer cserv : cservs) {
                cserv.broadcastPacket(MaplePacketCreator.serverNotice(1, "The Server is Shutting Down For a Reboot in "+ time / 60000 +" minutes. Please log off safely."));
            }
            mc.dropMessage("All characters saved.");            
            CommandProcessor.forcePersisting();
            c.getChannelServer().shutdownWorld(time);
            // shutdown        
    }

    @Override
    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[]{
                    new InternCommandDefinition("shutdownworld", "[when in Minutes]", "Cleanly shuts down all channels and the loginserver of this world"),};
    }
}
