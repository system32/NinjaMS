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
package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * 
 * @author Matze
 */
public class WhisperHandler extends AbstractMaplePacketHandler {

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte mode = slea.readByte();
        if (mode == 0x44) { // CREDITS OLIVER. NOT TO BE RELEASED TO PUBLIC
            String person = slea.readMapleAsciiString();
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(person);
                MapleCharacter who = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(person);
                if ((c.getChannel() != who.getClient().getChannel() || who.getGMLevel() > c.getPlayer().getGMLevel()) && !c.getPlayer().isAdmin()) {
                    c.getSession().write(MaplePacketCreator.getFindBuddyReplyInCS(who.getName()));
                } else if (who.inCS()) {
                    c.getSession().write(MaplePacketCreator.getFindBuddyReplyInCS(who.getName()));
                } else {
                    c.getSession().write(MaplePacketCreator.getFindBuddyReplyInChannel(who.getName(), who.getMapId()));
                }
            } catch (Exception e) {
                c.showMessage(5, "Error finding channel...");
            }
        } else if (mode == 6) { // whisper
            // System.out.println("in whisper handler");
            String recipient = slea.readMapleAsciiString();
            String text = slea.readMapleAsciiString();
            if (!CommandProcessor.getInstance().processCommand(c, text)) {
                if (c.getPlayer().getMute() <= 2) {
                    MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                    if (player != null) {
                        player.getClient().getSession().write(MaplePacketCreator.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                        c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 1));
                    } else { // not found
                        try {
                            if (ChannelServer.getInstance(c.getChannel()).getWorldInterface().isConnected(recipient)) {

                                ChannelServer.getInstance(c.getChannel()).getWorldInterface().whisper(
                                        c.getPlayer().getName(), recipient, c.getChannel(), text);
                                c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 1));
                            } else {
                                c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                            }
                        } catch (RemoteException e) {
                            c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                            c.getChannelServer().reconnectWorld();
                        }
                    }
                } else {
                    c.showMessage(1, "You have been muted permanently. You will be unmuted next server check :].");
                }
            }
        } else if (mode == 5) { // - /find
            String recipient = slea.readMapleAsciiString();            
            try {
                WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(recipient);
                if (loc != null) {
                    MapleCharacter player = ChannelServer.getInstance(loc.channel).getPlayerStorage().getCharacterByName(recipient);
                    if (player != null) {
                        if (player.getGMLevel() > c.getPlayer().getGMLevel()) {
                            c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                            return;
                        }
                        if (c.getChannel() == loc.channel) {
                            c.showMessage(5, "'" + player.getName() + "' is on your channel.");
                        } else {
                            c.showMessage(5, "'" + player.getName() + "' is on Channel " + loc.channel + ".");
                        }

                        if (player.inCS()) {
                            c.getSession().write(MaplePacketCreator.getFindReplyWithCSorMTS(player.getName(), false));
                        } else {
                            c.getSession().write(MaplePacketCreator.getFindReplyWithMap(player.getName(), player.getMap().getId()));
                        }
                    } else { // not found
                        c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                    }

                } else {
                    c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
                }
            } catch (Exception e) {
                c.getSession().write(MaplePacketCreator.getWhisperReply(recipient, (byte) 0));
            }
        }
    }
}
