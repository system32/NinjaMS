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

import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.client.Enums.MapleStat;
import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Inventory.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.MaplePet;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShop;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.constants.Skills;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;

public class CharCommands implements GMCommand {

    private MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
            IllegalCommandSyntaxException {

        MapleCharacter player = c.getPlayer();
        if (splitted[0].equals("lowhp")) {
            player.setHp(1);
            player.setMp(500);
            player.updateSingleStat(MapleStat.HP, 1);
            player.updateSingleStat(MapleStat.MP, 500);
        } else if (splitted[0].equals("heal")) {
            player.addMPHP(player.getMaxHp() - player.getHp(), player.getMaxMp() - player.getMp());
        } else if (splitted[0].equals("skill")) {
            int skill = Integer.parseInt(splitted[1]);
            int level = getOptionalIntArg(splitted, 2, 1);
            if (skill == Skills.Hermit.FlashJump) {
                level = 0;
            }
            int masterlevel = getOptionalIntArg(splitted, 3, 1);
            c.getPlayer().changeSkillLevel(SkillFactory.getSkill(skill), level, masterlevel);
        } else if (splitted[0].equals("ap")) {
            player.setRemainingAp(getOptionalIntArg(splitted, 1, 1));
            player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
        } else if (splitted[0].equals("whereami")) {
            new ServernoticeMapleClientMessageCallback(c).dropMessage("You are on map "
                    + c.getPlayer().getMap().getId() + " - " + c.getPlayer().getMap().getMapName());
        } else if (splitted[0].equals("levelup")) {
            c.getPlayer().levelUp();
            c.getPlayer().setExp(0);
        } else if (splitted[0].equals("level")) {
            if (splitted.length == 3) {
                MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (noob == null) {
                    mc.dropMessage("player not in your channel");
                    return;
                }
                noob.setLevel(Integer.parseInt(splitted[2]));
                noob.levelUp();
                int newexp = noob.getExp();
                if (newexp < 0) {
                    noob.gainExp(-newexp, false, false);
                }
            } else if (splitted.length == 2) {
                int quantity = Integer.parseInt(splitted[1]);
                c.getPlayer().setLevel(quantity);
                c.getPlayer().levelUp();
                int newexp = c.getPlayer().getExp();
                if (newexp < 0) {
                    c.getPlayer().gainExp(-newexp, false, false);
                }
            } else {
                mc.dropMessage("Read @commands lar euu nub");
            }
        } else if (splitted[0].equals("statreset")) {
            int str = c.getPlayer().getStr();
            int dex = c.getPlayer().getDex();
            int int_ = c.getPlayer().getInt();
            int luk = c.getPlayer().getLuk();
            int newap = c.getPlayer().getRemainingAp() + (str - 4) + (dex - 4) + (int_ - 4) + (luk - 4);
            c.getPlayer().setStr(4);
            c.getPlayer().setDex(4);
            c.getPlayer().setInt(4);
            c.getPlayer().setLuk(4);
            c.getPlayer().setRemainingAp(newap);
            List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>();
            stats.add(new Pair<MapleStat, Integer>(MapleStat.STR, Integer.valueOf(4)));
            stats.add(new Pair<MapleStat, Integer>(MapleStat.DEX, Integer.valueOf(4)));
            stats.add(new Pair<MapleStat, Integer>(MapleStat.INT, Integer.valueOf(4)));
            stats.add(new Pair<MapleStat, Integer>(MapleStat.LUK, Integer.valueOf(4)));
            stats.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, Integer.valueOf(newap)));
            c.getSession().write(MaplePacketCreator.updatePlayerStats(stats));
        } else if (splitted[0].equals("maxstat")) {
            c.getPlayer().setStr(Short.MAX_VALUE);
            c.getPlayer().setDex(Short.MAX_VALUE);
            c.getPlayer().setInt(Short.MAX_VALUE);
            c.getPlayer().setLuk(Short.MAX_VALUE);
            c.getPlayer().setRemainingAp(Short.MAX_VALUE);
            List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>();
            stats.add(new Pair<MapleStat, Integer>(MapleStat.STR, Integer.valueOf(Short.MAX_VALUE)));
            stats.add(new Pair<MapleStat, Integer>(MapleStat.DEX, Integer.valueOf(Short.MAX_VALUE)));
            stats.add(new Pair<MapleStat, Integer>(MapleStat.INT, Integer.valueOf(Short.MAX_VALUE)));
            stats.add(new Pair<MapleStat, Integer>(MapleStat.LUK, Integer.valueOf(Short.MAX_VALUE)));
            stats.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, Integer.valueOf(Short.MAX_VALUE)));
            c.getSession().write(MaplePacketCreator.updatePlayerStats(stats));
        } else if (splitted[0].equalsIgnoreCase("job")) {
            if (splitted.length == 2) {
                int fuck = Integer.parseInt(splitted[1]);
                player.changeJobById(fuck);
            } else if (splitted.length == 3) {
                int fuck = Integer.parseInt(splitted[2]);
                MapleCharacter noob = ChannelServer.getInstance(c.getChannel()).getPlayerStorage().getCharacterByName(splitted[1]);
                noob.changeJobById(fuck);
            } else {
                mc.dropMessage("Syntax : !job <id> or !job <name> <id>");
            }
        } else if (splitted[0].equalsIgnoreCase("mesos")) {
            player.gainMeso(Integer.MAX_VALUE - player.getMeso(), true);
        } else if (splitted[0].equalsIgnoreCase("setstat")) {
            if (splitted.length < 4) {
                mc.dropMessage("Learn to read !commands");
                return;
            }
            MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[2]);
            if (noob == null) {
                mc.dropMessage("The Player is not in your channel");
                return;
            }
            short x;
            try {
                x = Short.parseShort(splitted[3]);
            } catch (NumberFormatException numberFormatException) {
                mc.dropMessage("You have entered a invalid number and so the stat will be set as 4");
                x = (short) 4;
            }
            if (splitted[1].equalsIgnoreCase("str")) {
                noob.setStr(x);
                noob.updateSingleStat(MapleStat.STR, x);
            } else if (splitted[1].equalsIgnoreCase("dex")) {
                noob.setDex(x);
                noob.updateSingleStat(MapleStat.DEX, x);
            } else if (splitted[1].equalsIgnoreCase("luk")) {
                noob.setLuk(x);
                noob.updateSingleStat(MapleStat.LUK, x);
            } else if (splitted[1].equalsIgnoreCase("int")) {
                noob.setInt(x);
                noob.updateSingleStat(MapleStat.INT, x);
            } else if (splitted[1].equalsIgnoreCase("ap")) {
                noob.setRemainingAp(x);
                noob.updateSingleStat(MapleStat.AVAILABLEAP, x);
            } else {
                mc.dropMessage("Learn to read !commands");
                return;
            }
            mc.dropMessage("Command Executed");
        } else if (splitted[0].equalsIgnoreCase("setclan")) {
            if (splitted.length < 3) {
                mc.dropMessage("Read !commands please. Syntax : !setclan ign clanname");
            } else {
                MapleCharacter other = c.getPlayer();
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
                if (splitted[2].equalsIgnoreCase("undecided")) {
                    other.setClanz(0);
                } else if (splitted[2].equalsIgnoreCase("earth")) {
                    other.setClanz(1);
                } else if (splitted[2].equalsIgnoreCase("wind")) {
                    other.setClanz(2);
                } else if (splitted[2].equalsIgnoreCase("naruto")) {
                    other.setClanz(3);
                } else if (splitted[2].equalsIgnoreCase("fire")) {
                    other.setClanz(4);
                } else if (splitted[1].equalsIgnoreCase("lightning")) {
                    other.setClanz(5);
                } else {
                     mc.dropMessage("Read !commands please. Syntax : !setclan ign clanname");
                     return;
                }
                mc.dropMessage(splitted[1] + " has been set to" + splitted[2] + " clan");
            }
        }
    }

    @Override
    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("lowhp", "", ""),
                    new GMCommandDefinition("heal", "", "heals you"),
                    //  new GMCommandDefinition("skill", "", ""),
                    new GMCommandDefinition("ap", "", ""),
                    new GMCommandDefinition("job", "", ""),
                    new GMCommandDefinition("whereami", "", ""),
                    new GMCommandDefinition("levelup", "", ""),
                    new GMCommandDefinition("level", "<ign> <level>", " ign is optional. if you don't know what it does, go bang a wall"),
                    new GMCommandDefinition("statreset", "", ""),
                    new GMCommandDefinition("mesos", "", ""),
                    new GMCommandDefinition("setstat", "<str/dex/luk/int/ap> <ign> <amount>", "sets "),
                    new GMCommandDefinition("maxstat", "", " maxxes all your stats"),
                    new GMCommandDefinition("setclan", "undecided/earth/wind/naruto/fire/lightning", " sets Clan")
                };
    }
}
