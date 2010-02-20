/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.guild.MapleAlliance;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Owner
 */
public final class AllianceHandler extends AbstractMaplePacketHandler {
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleAlliance alliance = null;
        if (c.getPlayer().getGuild() != null && c.getPlayer().getGuild().getAllianceId() > 0) {
            try {
                alliance = c.getChannelServer().getWorldInterface().getAlliance(c.getPlayer().getGuild().getAllianceId());
            } catch (RemoteException re) {
                c.getChannelServer().reconnectWorld();
            }
        }
        if (alliance == null) {
            c.getPlayer().dropMessage("System error!");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        } else if (c.getPlayer().getMGC().getAllianceRank() > 2 || !alliance.getGuilds().contains(c.getPlayer().getGuildId())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        try {
            switch (slea.readByte()) {
                case 0x01:
                    c.getChannelServer().getWorldInterface().allianceMessage(alliance.getId(), sendShowInfo(c.getPlayer().getGuild().getAllianceId(), c.getPlayer().getId()), -1, -1);
                    break;
                case 0x02: { // Leave Alliance
                    if (c.getPlayer().getGuild().getAllianceId() == 0 || c.getPlayer().getGuildId() < 1 || c.getPlayer().getGuildRank() != 1) {
                        return;
                    }
                    c.getChannelServer().getWorldInterface().allianceMessage(alliance.getId(), sendChangeGuild(c.getPlayer().getGuildId(), c.getPlayer().getId(), c.getPlayer().getGuildId(), 2), -1, -1);
                    break;
                }
                case 0x03: // send alliance invite
                    String charName = slea.readMapleAsciiString();
                    int channel = c.getChannelServer().getWorldInterface().find(charName);
                    if (channel == -1) {
                        c.getPlayer().dropMessage("The c.getPlayer() is not online");
                    } else {
                        MapleCharacter victim = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(charName);
                        if (victim.getGuildId() == 0) {
                            c.getPlayer().dropMessage("c.getPlayer() does not have a guild");
                        } else if (victim.getGuildRank() != 1) {
                            c.getPlayer().dropMessage("c.getPlayer() is not the leader of his/her guild.");
                        } else {
                            c.getChannelServer().getWorldInterface().allianceMessage(alliance.getId(), sendInvitation(c.getPlayer().getGuild().getAllianceId(), c.getPlayer().getId(), slea.readMapleAsciiString()), -1, -1);
                        }
                    }
                    break;
                case 0x04: {
                    int guildid = slea.readInt();
                    slea.readMapleAsciiString();//guild name
                    if (c.getPlayer().getGuild().getAllianceId() != 0 || c.getPlayer().getGuildRank() != 1 || c.getPlayer().getGuildId() < 1) {
                        return;
                    }
                    c.getChannelServer().getWorldInterface().allianceMessage(alliance.getId(), sendChangeGuild(guildid, c.getPlayer().getId(), c.getPlayer().getGuildId(), 0), -1, -1);
                    break;
                }
                case 0x06: { // Expel Guild
                    int guildid = slea.readInt();
                    int allianceid = slea.readInt();
                    if (c.getPlayer().getGuild().getAllianceId() == 0 || c.getPlayer().getGuild().getAllianceId() != allianceid) {
                        return;
                    }
                    c.getChannelServer().getWorldInterface().allianceMessage(alliance.getId(), sendChangeGuild(allianceid, c.getPlayer().getId(), guildid, 1), -1, -1);
                    break;
                }
                case 0x07: { // Change Alliance Leader
                    if (c.getPlayer().getGuild().getAllianceId() == 0 || c.getPlayer().getGuildId() < 1) {
                        return;
                    }
                    c.getChannelServer().getWorldInterface().allianceMessage(alliance.getId(), sendChangeLeader(c.getPlayer().getGuild().getAllianceId(), c.getPlayer().getId(), slea.readInt()), -1, -1);
                    break;
                }
                case 0x08:
                    String ranks[] = new String[5];
                    for (int i = 0; i < 5; i++) {
                        ranks[i] = slea.readMapleAsciiString();
                    }
                    c.getChannelServer().getWorldInterface().setAllianceRanks(alliance.getId(), ranks);
                    c.getChannelServer().getWorldInterface().allianceMessage(alliance.getId(), MaplePacketCreator.changeAllianceRankTitle(alliance.getId(), ranks), -1, -1);
                    break;
                case 0x09: {
                    int int1 = slea.readInt();
                    byte byte1 = slea.readByte();
                    c.getChannelServer().getWorldInterface().allianceMessage(alliance.getId(), sendChangeRank(c.getPlayer().getGuild().getAllianceId(), c.getPlayer().getId(), int1, byte1), -1, -1);
                    break;
                }
                case 0x0A:
                    String notice = slea.readMapleAsciiString();
                    c.getChannelServer().getWorldInterface().setAllianceNotice(alliance.getId(), notice);
                    c.getChannelServer().getWorldInterface().allianceMessage(alliance.getId(), MaplePacketCreator.allianceNotice(alliance.getId(), notice), -1, -1);
                    break;
                default:
                    c.getPlayer().dropMessage("Feature not available");
            }
            alliance.saveToDB();
        } catch (RemoteException rawr) {
            c.getChannelServer().reconnectWorld();
        }
    }

    private static MaplePacket sendShowInfo(int allianceid, int playerid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x02);
        mplew.writeInt(allianceid);
        mplew.writeInt(playerid);
        return mplew.getPacket();
    }

    private static MaplePacket sendInvitation(int allianceid, int playerid, final String guildname) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x05);
        mplew.writeInt(allianceid);
        mplew.writeInt(playerid);
        mplew.writeMapleAsciiString(guildname);
        return mplew.getPacket();
    }

    private static MaplePacket sendChangeGuild(int allianceid, int playerid, int guildid, int option) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x07);
        mplew.writeInt(allianceid);
        mplew.writeInt(guildid);
        mplew.writeInt(playerid);
        mplew.write(option);
        return mplew.getPacket();
    }

    private static MaplePacket sendChangeLeader(int allianceid, int playerid, int victim) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x08);
        mplew.writeInt(allianceid);
        mplew.writeInt(playerid);
        mplew.writeInt(victim);
        return mplew.getPacket();
    }

    private static MaplePacket sendChangeRank(int allianceid, int playerid, int int1, byte byte1) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(0x09);
        mplew.writeInt(allianceid);
        mplew.writeInt(playerid);
        mplew.writeInt(int1);
        mplew.writeInt(byte1);
        return mplew.getPacket();
    }
}