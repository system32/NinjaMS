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
package net.sf.odinms.client.messages.commands.gm;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.CharInfoProcessor;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;

public class InfoCommand implements GMCommand {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
       if (splitted[0].equalsIgnoreCase("ninjaglare")) {
            MapleCharacter other = c.getPlayer();
            if (splitted.length == 2) {
                try {
                    WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                    if (loc != null) {
                        other = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(splitted[1]);
                    } else {
                        mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                        return;
                    }
                } catch (Exception e) {
                    mc.dropMessage("[Anbu] '" + splitted[1] + "' does not exist, is CCing, or is offline.");
                    return;
                }
            }
           CharInfoProcessor.getNinjaGlare(mc, other);
        } else if (splitted[0].equalsIgnoreCase("getcharinfo")) {
            if (splitted.length == 2) {
                CharInfoProcessor.loadAccountDetails(c, splitted[1]);
            } else {
                mc.dropMessage("fail GM . Syntax: !getcharinfo <ign>");
            }
        } else if (splitted[0].equalsIgnoreCase("seereason")) {
            if (splitted.length == 2) {
                CharInfoProcessor.seeReason(c, splitted[1]);
            } else {
                mc.dropMessage("fail GM . Syntax: !seereason <ign>");
            }
        } else if (splitted[0].equalsIgnoreCase("connected")) {
            try {
                Map<Integer, Integer> connected = c.getChannelServer().getWorldInterface().getConnected();
                StringBuilder conStr = new StringBuilder("Connected Clients: ");
                boolean first = true;
                for (int i : connected.keySet()) {
                    if (!first) {
                        conStr.append(", ");
                    } else {
                        first = false;
                    }
                    if (i == 0) {
                        conStr.append("Total: ");
                        conStr.append(connected.get(i));
                    } else {
                        conStr.append("Ch");
                        conStr.append(i);
                        conStr.append(": ");
                        conStr.append(connected.get(i));
                    }
                }
                new ServernoticeMapleClientMessageCallback(c).dropMessage(conStr.toString());
            } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
        } else if (splitted[0].equalsIgnoreCase("online")) {
            mc.dropMessage("Characters connected to channel " + c.getChannel() + ":");
            Collection<MapleCharacter> chrs = ChannelServer.getInstance(c.getChannel()).getPlayerStorage().getAllCharacters();
            for (MapleCharacter chr : chrs) {
                mc.dropMessage(chr.getName() + " at map ID: " + chr.getMapId());
            }
            mc.dropMessage("Total characters on channel " + c.getChannel() + ": " + chrs.size());
        } else if (splitted[0].toLowerCase().equalsIgnoreCase("onlineall")) {
            StringBuilder sb = new StringBuilder("Characters online: ");
            mc.dropMessage(sb.toString());
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                sb = new StringBuilder();
                sb.append("Channel " + cs.getChannel() +" : ");
                for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
                    if (sb.length() > 150) {
                        sb.setLength(sb.length() - 2);
                        mc.dropMessage(sb.toString());
                        sb = new StringBuilder();
                    }
                    if (!chr.isJounin()) {
                        sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                        sb.append(", ");
                    }
                }
                if (sb.length() >= 2) {
                    sb.setLength(sb.length() - 2);
                    mc.dropMessage(sb.toString());
                }
            }
        }
    }

    @Override
    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("onlineall", "", "shows characters online in all channels"),
                    new GMCommandDefinition("ninjaglare", "charname", "Shows info about the character with the given name"),
                    new GMCommandDefinition("getcharinfo", "charname", "shows stats offline. Use with care. too expensive"),
                    new GMCommandDefinition("seereason", "charname", "shows ban reason. dont use it with out a reason. too expensive"),
                    new GMCommandDefinition("connected", "", "Shows how many players are connected on each channel"),
                    new GMCommandDefinition("online", "", "Shows the players connected to that channel"),
        };
    }

    
}
