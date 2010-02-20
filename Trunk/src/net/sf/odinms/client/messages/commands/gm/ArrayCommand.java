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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.tools.StringUtil;

public class ArrayCommand implements GMCommand {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted.length >= 2) {
            if (splitted[1].equalsIgnoreCase("*CLEAR")) {
                c.getChannelServer().getWorldInterface().setArrayString("");
                mc.dropMessage("Array Sucessfully Flushed");
            } else if (splitted[1].equalsIgnoreCase("*UPDATE")){
                c.getChannelServer().getWorldInterface().updateArrayString();
            } else {
                c.getChannelServer().getWorldInterface().setArrayString(c.getChannelServer().getWorldInterface().getArrayString() + StringUtil.joinStringFrom(splitted, 1));
                mc.dropMessage("Added " + StringUtil.joinStringFrom(splitted, 1) + " to the array. Use !array to check.");
            }
        } else {
            mc.dropMessage("Array: " + c.getChannelServer().getWorldInterface().getArrayString());
        }
    }

    @Override
    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("array", "", "Shows array"),};
    }
}
