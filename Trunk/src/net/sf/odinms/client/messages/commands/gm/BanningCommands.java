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

import static net.sf.odinms.client.messages.CommandProcessor.getNamedIntArg;
import static net.sf.odinms.client.messages.CommandProcessor.joinAfterString;

import java.text.DateFormat;
import java.util.Calendar;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

public class BanningCommands implements GMCommand {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        ChannelServer cserv = c.getChannelServer();
        if (splitted[0].equals("ban") || splitted[0].equals("assasinate")) {
            if (splitted.length < 3) {
                mc.dropMessage("why are you even a GM when you don't know how to use ban command?");
            }
            String originalReason = StringUtil.joinStringFrom(splitted, 2);
            String reason = c.getPlayer().getName() + " banned " + splitted[1] + ": " + originalReason;
            MapleCharacter target = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                String readableTargetName = MapleCharacterUtil.makeMapleReadable(target.getName());
                String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                reason += " (IP: " + ip + ")";
                target.ban(reason);
                mc.dropMessage("Banned " + readableTargetName + " ipban for " + ip + " reason: " + originalReason);
                c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Akatsuki] The noob retarded rookie and international criminal " + readableTargetName + "has been assasinated by " + c.getPlayer().getName() + " in a 1337 Ninja Way For " + originalReason));
            } else {
                if (MapleCharacter.ban(splitted[1], reason, false)) {
                    mc.dropMessage("Offline Banned " + splitted[1]);
                    c.getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, "[Akatsuki] The noob retarded rookie and international criminal " + splitted[1] + "has been assasinated offline by " + c.getPlayer().getName() + " in a 1337 Ninja Way For " + originalReason));
                } else {
                    mc.dropMessage("Failed to ban " + splitted[1]);
                }
            }
        } else if (splitted[0].equals("tempban")) {
            Calendar tempB = Calendar.getInstance();
            String originalReason = joinAfterString(splitted, ":");
            if (splitted.length < 4 || originalReason == null) {
                 mc.dropMessage("Syntax helper: !tempban <name> [i / m / w / d / h] <amount> [r [reason id] : Text Reason");
                 mc.dropMessage("2 - macro and auto keyboard, 3 - promotion and advertising, 4 - harrassment, 5 - profane language, 6 - scamming, 7 - misconduct, 10 - Temporary request, 11 - impersonating GM, 12 - violating game policy ,13 - one of curse, scamming or illegal transaction via megaphones");
                 mc.dropMessage("Example : !rempban system h 1 r 2 : for botting shit");
                 mc.dropMessage("Example : !tempban system h 1 r : for test");
                 return;
            }
            int yChange = getNamedIntArg(splitted, 1, "y", 0);
            int mChange = getNamedIntArg(splitted, 1, "m", 0);
            int wChange = getNamedIntArg(splitted, 1, "w", 0);
            int dChange = getNamedIntArg(splitted, 1, "d", 0);
            int hChange = getNamedIntArg(splitted, 1, "h", 0);
            int iChange = getNamedIntArg(splitted, 1, "i", 0);
            int gReason = getNamedIntArg(splitted, 1, "r", 7);
            String reason = c.getPlayer().getName() + " tempbanned " + splitted[1] + ": " + originalReason;
            if (gReason > 14) {
                mc.dropMessage("You have entered an incorrect ban reason ID, please try again.");
                return;
            }
            DateFormat df = DateFormat.getInstance();
            tempB.set(tempB.get(Calendar.YEAR) + yChange, tempB.get(Calendar.MONTH) + mChange, tempB.get(Calendar.DATE) +
                    (wChange * 7) + dChange, tempB.get(Calendar.HOUR_OF_DAY) + hChange, tempB.get(Calendar.MINUTE) +
                    iChange);

            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);

            if (victim == null) {
                int accId = MapleClient.findAccIdForCharacterName(splitted[1]);
                if (accId >= 0 && MapleCharacter.tempban(reason, tempB, gReason, accId)) {
                    mc.dropMessage("The character " + splitted[1] + " has been successfully offline-tempbanned till " +
                            df.format(tempB.getTime()) + ".");
                } else {
                    mc.dropMessage("There was a problem offline banning character " + splitted[1] + ".");
                }
            } else {
                victim.tempban(reason, tempB, gReason);
                mc.dropMessage("The character " + splitted[1] + " has been successfully tempbanned till " +
                        df.format(tempB.getTime()));
            }
        } else if (splitted[0].equals("dc")) {
            int level = 0;
            MapleCharacter victim;
            if (splitted[1].charAt(0) == '-') {
                level = StringUtil.countCharacters(splitted[1], 'f');
                victim = cserv.getPlayerStorage().getCharacterByName(splitted[2]);
            } else {
                victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            }
            victim.getClient().getSession().close();
            if (level >= 1) {
                victim.getClient().disconnect();
            }
            if (level >= 2) {
                victim.saveToDB();
                cserv.removePlayer(victim);
            }
        }
    }

    @Override
    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("ban", "charname reason", "Permanently ip, mac and accountbans the given character"),
                    new GMCommandDefinition("assasinate", "charname reason", "Permanently ip, mac and accountbans the given character"),
                    new GMCommandDefinition("tempban", "<name> [i / m / w / d / h] <amount> [r  [reason id] : Text Reason", "Tempbans the given account"),
                    new GMCommandDefinition("dc", "name", "Disconnectes the player with the given name"),};
    }
}
