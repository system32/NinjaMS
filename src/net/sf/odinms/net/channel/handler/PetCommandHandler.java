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

import java.util.Random;

import net.sf.odinms.client.ExpTable;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.PetCommand;
import net.sf.odinms.client.PetDataFactory;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class PetCommandHandler extends AbstractMaplePacketHandler {

	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PetCommandHandler.class);
	
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int petId = slea.readInt();
		int petIndex = c.getPlayer().getPetIndex(petId);
		MaplePet pet = null;
		if (petIndex == -1) {
			return;
		} else {
			 pet = c.getPlayer().getPet(petIndex);
		}
		slea.readInt();
		slea.readByte();
		
		byte command = slea.readByte();
		
		PetCommand petCommand = PetDataFactory.getPetCommand(pet.getItemId(), (int) command);
		
		boolean success = false;
			
		Random rand = new Random();
		int random = rand.nextInt(101);
		if (random <= petCommand.getProbability()) {
			success = true;
			if (pet.getCloseness() < 30000) {
				int newCloseness = pet.getCloseness() + (petCommand.getIncrease() * c.getChannelServer().getPetExpRate());
				if (newCloseness > 30000) {
					newCloseness = 30000;
				}
				pet.setCloseness(newCloseness);
				if (newCloseness >= ExpTable.getClosenessNeededForLevel(pet.getLevel() + 1)) {
					pet.setLevel(pet.getLevel() + 1);
					c.getSession().write(MaplePacketCreator.showOwnPetLevelUp(c.getPlayer().getPetIndex(pet)));
					c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showPetLevelUp(c.getPlayer(), c.getPlayer().getPetIndex(pet)));
				}
				c.getSession().write(MaplePacketCreator.updatePet(pet));
			}
		}
		
		MapleCharacter player = c.getPlayer();
		player.getMap().broadcastMessage(player, MaplePacketCreator.commandResponse(player.getId(), (byte)petIndex, command, success, false), true);
	}
}
