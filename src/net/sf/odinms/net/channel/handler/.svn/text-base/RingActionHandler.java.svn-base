/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Owner
 */
public class RingActionHandler extends AbstractMaplePacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RingActionHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte mode = slea.readByte();
        switch (mode) {
            case 0: //Send
                String partnerName = slea.readMapleAsciiString();
                if (partnerName.equalsIgnoreCase(c.getPlayer().getName())) {
                    c.getSession().write(MaplePacketCreator.serverNotice(1, "You cannot put your own name in it."));
                    return;
                }
                MapleCharacter partner = c.getChannelServer().getPlayerStorage().getCharacterByName(partnerName);
                if (partner == null) {
                    c.getSession().write(MaplePacketCreator.serverNotice(1, partnerName + " was not found on this channel. If you are both logged in, please make sure you are in the same channel."));
                    return;
                } else if (partner.getGender() == c.getPlayer().getGender()) {
                    c.getSession().write(MaplePacketCreator.serverNotice(1, "Your partner is the same gender as you are."));
                    return;
                } else {
                    NPCScriptManager.getInstance().start(partner.getClient(), 9201002);
                }
                break;
            case 1: //Cancel send
                c.getSession().write(MaplePacketCreator.serverNotice(1, "You have cancelled the request."));
                break;
            case 3: //Drop Ring
                try {
                    //MapleCharacterUtil.divorceEngagement(c.getPlayer());
                    c.getSession().write(MaplePacketCreator.serverNotice(1, "Your engagement has been broken up."));
                } catch (Exception exc) {
                    log.error("Error divorcing engagement", exc);
                }
                break;
        }
    }
}

