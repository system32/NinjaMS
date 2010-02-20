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
package net.sf.odinms.net;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public enum SendPacketOpcode implements WritableIntValueHolder {
	// GENERAL
	PING,
	// LOGIN
	LOGIN_STATUS,
	PIN_OPERATION,
	SERVERLIST,
	SERVERSTATUS,
	SERVER_IP,
	CHARLIST,
	CHAR_NAME_RESPONSE,
	RELOG_RESPONSE,
	ADD_NEW_CHAR_ENTRY,
	DELETE_CHAR_RESPONSE,
	CHANNEL_SELECTED,
	// CHANNEL
	CHANGE_CHANNEL,
	UPDATE_STATS,
	FAME_RESPONSE,
	UPDATE_SKILLS,
	WARP_TO_MAP,
	SERVERMESSAGE,
	AVATAR_MEGA,
	SPAWN_NPC,
	SPAWN_NPC_REQUEST_CONTROLLER,
	SPAWN_MONSTER,
	SPAWN_MONSTER_CONTROL,
	MOVE_MONSTER_RESPONSE,
	CHATTEXT,
	SHOW_STATUS_INFO,
	SHOW_MESO_GAIN,
	SHOW_QUEST_COMPLETION,
	WHISPER,
	SPAWN_PLAYER,
	//ANNOUNCE_PLAYER_SHOP,
	SHOW_SCROLL_EFFECT,
	SHOW_ITEM_GAIN_INCHAT,
        SHOW_EQUIP_EFFECT,
	KILL_MONSTER,
	DROP_ITEM_FROM_MAPOBJECT,
	FACIAL_EXPRESSION,
	MOVE_PLAYER,
	MOVE_MONSTER,
	CLOSE_RANGE_ATTACK,
	RANGED_ATTACK,
	MAGIC_ATTACK,
	OPEN_NPC_SHOP,
	CONFIRM_SHOP_TRANSACTION,
	OPEN_STORAGE,
	MODIFY_INVENTORY_ITEM,
	REMOVE_PLAYER_FROM_MAP,
	REMOVE_ITEM_FROM_MAP,
	UPDATE_CHAR_LOOK,
	SHOW_FOREIGN_EFFECT,
	GIVE_FOREIGN_BUFF,
	CANCEL_FOREIGN_BUFF,
	DAMAGE_PLAYER,
	CHAR_INFO,
	UPDATE_QUEST_INFO,
	GIVE_BUFF,
	CANCEL_BUFF,
	PLAYER_INTERACTION,
	UPDATE_CHAR_BOX,
	NPC_TALK,
	KEYMAP,
	SHOW_MONSTER_HP,
	PARTY_OPERATION,
	UPDATE_PARTYMEMBER_HP,
	MULTICHAT,
	APPLY_MONSTER_STATUS,
	CANCEL_MONSTER_STATUS,
	CLOCK,
	SPAWN_PORTAL,
	SPAWN_DOOR,
	REMOVE_DOOR,
	SPAWN_SPECIAL_MAPOBJECT,
	REMOVE_SPECIAL_MAPOBJECT,
	SUMMON_ATTACK,
	MOVE_SUMMON,
	SPAWN_MIST,
	REMOVE_MIST,
	DAMAGE_SUMMON,
	DAMAGE_MONSTER,
	BUDDYLIST,
	SHOW_ITEM_EFFECT,
	SHOW_CHAIR,
	CANCEL_CHAIR,
	SKILL_EFFECT,
	CANCEL_SKILL_EFFECT,
	BOSS_ENV,
	REACTOR_SPAWN,
	REACTOR_HIT,
	REACTOR_DESTROY,
	MAP_EFFECT,
	GUILD_OPERATION,
	BBS_OPERATION,
	SHOW_MAGNET,
	MESSENGER,
	NPC_ACTION,
	SPAWN_PET,
	MOVE_PET,
	PET_CHAT,
	PET_COMMAND,
	PET_NAMECHANGE,
	COOLDOWN,
	PLAYER_HINT,
	USE_SKILL_BOOK,
	SHOW_FORCED_EQUIP,
	SKILL_MACRO,
	CS_OPEN,
	CS_UPDATE,
	CS_OPERATION,
	PLAYER_NPC,
	SHOW_NOTES,
	GM_OPERATION,
	VICIOUS_HAMMER,
        YELLOW_TIP,
	CREATE_CYGNUS,
	CYGNUS_RESPONSE,
        REMOVE_NPC,
        SPOUSE_CHAT,
        CHALKBOARD,
        OX_QUIZ,
        ALLIANCE_OPERATION,
        DOJO_WARP_UP,
        ENERGY,
        CYGNUS_INTRO_LOCK,
        CYGNUS_INTRO_DISABLE_UI,
        FAMILY,
        SPAWN_HIRED_MERCHANT,
        DESTROY_HIRED_MERCHANT,
        DUEY;
	private int code = -2;

	public void setValue(int code) {
		this.code = code;
	}

	@Override
	public int getValue() {
		return code;
	}

	public static Properties getDefaultProperties() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		FileInputStream fileInputStream = new FileInputStream(System.getProperty("net.sf.odinms.sendops"));
		props.load(fileInputStream);
		fileInputStream.close();
		return props;
	}
	

	static {
		try {
			ExternalCodeTableGetter.populateValues(getDefaultProperties(), values());
		} catch (IOException e) {
			throw new RuntimeException("Failed to load recvops", e);
		}
	}
}
