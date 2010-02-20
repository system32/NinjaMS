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

import java.util.List;

import net.sf.odinms.client.Clones;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovePlayerHandler extends AbstractMovementPacketHandler {

    private static Logger log = LoggerFactory.getLogger(MovePlayerHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readByte();
        slea.readInt();
        slea.readInt();
        // log.trace("Movement command received: unk1 {} unk2 {}", new Object[] { unk1, unk2 });
        final List<LifeMovementFragment> res = parseMovement(slea);
        // TODO more validation of input data
        if (res != null) {
            if (slea.available() != 18) {
             //   log.warn("slea.available != 18 (movement parsing error)");
                return;
            }
            MapleCharacter player = c.getPlayer();
            
            if(player.getJobId() == 900 || player.getJobId() == 910 ){
                if(!player.isJounin() && player.getMapId() != 100000000){                    
                    player.changeJobById(0);
                    player.goHome();
                }
                
            }

            if(!player.isChunin() && SpecialStuff.getInstance().isDojoMap(player.getMapId()) && (player.getGMSMode() < 1)){
                player.goHome();
                player.dropMessage("You can only be in Dojo if you are in GMS mode");
            }
            
            if (!player.isHidden()) {
                MaplePacket packet = MaplePacketCreator.movePlayer(player.getId(), res);
                c.getPlayer().getMap().broadcastMessage(player, packet, false);
            }
            updatePosition(res, c.getPlayer(), 0);
            c.getPlayer().getMap().movePlayer(c.getPlayer(), c.getPlayer().getPosition());
            if (c.getPlayer().hasClones()) {
                int i = 1;
                for (final Clones clone : c.getPlayer().getClones()) {
                    TimerManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            MaplePacket packet = MaplePacketCreator.movePlayer(clone.getClone().getId(), res);
                            clone.getClone().getMap().broadcastMessage(clone.getClone(), packet, false);
                            updatePosition(res, clone.getClone(), 0);
                            clone.getClone().getMap().movePlayer(clone.getClone(), clone.getClone().getPosition());
                        }
                    }, i * 250);
                    i++;
                }
            }
        }
    }
    
}
