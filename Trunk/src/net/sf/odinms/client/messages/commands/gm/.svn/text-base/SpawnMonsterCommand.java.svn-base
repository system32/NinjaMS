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

import java.util.HashMap;
import static net.sf.odinms.client.messages.CommandProcessor.getNamedDoubleArg;
import static net.sf.odinms.client.messages.CommandProcessor.getNamedIntArg;
import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.GMCommand;
import net.sf.odinms.client.messages.GMCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleMonsterStats;

public class SpawnMonsterCommand implements GMCommand {
    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception{
        if (splitted[0].equals("mob")) {
            HashMap<String, Integer> mobs = new HashMap<String, Integer>();
            mobs.put("anego", 9400121);
            mobs.put("balrog", 9400536);
            mobs.put("mushmom", 6130101);
            mobs.put("mushma", 6300005);
            mobs.put("mushdad", 9400205);
            mobs.put("pap", 8500001);
            mobs.put("pianus", 8510000);
            mobs.put("theboss", 9400300);
            mobs.put("manon", 8180000);
            mobs.put("griffey", 8180001);
            mobs.put("bigfoot", 9400575);
            mobs.put("ergoth", 9300028);
            mobs.put("crow ", 9400014);
            mobs.put("phoenix", 9300089);
            mobs.put("freezer", 9300090);
            mobs.put("kyrin", 9300158);
            mobs.put("ikyrin", 9300159);
            mobs.put("leviathan", 8220003);
            mobs.put("franken", 9500335);
            mobs.put("pirate", 9500334);
            mobs.put("centipede", 9600010);
            mobs.put("alishar", 9500330);
            mobs.put("papa", 9500329);
            mobs.put("snowman", 9500319);
            mobs.put("skiman", 8220001);
            mobs.put("elliza", 8220000);
            mobs.put("priestcat", 7220002);
            mobs.put("taeroon", 7220000);
            mobs.put("ninetails", 7220001);
            mobs.put("dyle", 6220000);
            mobs.put("zeno", 6220001);
            mobs.put("timer", 5220003);
            mobs.put("faust", 5220002);
            mobs.put("stumpy", 3220000);
            mobs.put("mano", 2220000);
            mobs.put("latanica", 9420513);
            mobs.put("snackbar", 9410014);
            mobs.put("biker", 9410004);
            mobs.put("bigpuff", 9400569);
            mobs.put("loki", 9400566);
            mobs.put("bob", 9400551);
            mobs.put("kimera", 8220002);
            if (splitted.length != 2) {
                StringBuilder builder = new StringBuilder("Syntax: !mob [monstername]:");
                for (String allMobs : mobs.keySet()) {
                    if (1 % 10 == 0) {
                        mc.dropMessage(builder.toString());
                    } else {
                        builder.append(allMobs).append(", ");
                    }
                }
                mc.dropMessage(builder.toString());
            } else if (mobs.containsKey(splitted[1])) {
                c.getPlayer().getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(mobs.get(splitted[1])), c.getPlayer().getPosition());
            } else {
                mc.dropMessage("Invalid mob, try something else.");
            }
        } else if (splitted[0].equalsIgnoreCase("spawn")) {
            int mid = Integer.parseInt(splitted[1]);
            if(SpecialStuff.getInstance().isGMSpawnBlocked(c.getPlayer(), mid)){
                mc.dropMessage("You cannot spawn this");
                return;
            }
            int num = Math.min(getOptionalIntArg(splitted, 2, 1), 500);
            Integer hp = getNamedIntArg(splitted, 1, "hp");
            Integer exp = getNamedIntArg(splitted, 1, "exp");
            Double php = getNamedDoubleArg(splitted, 1, "php");
            Double pexp = getNamedDoubleArg(splitted, 1, "pexp");
            MapleMonster onemob = MapleLifeFactory.getMonster(mid);
            int newhp;
            int newexp;
            double oldExpRatio = ((double) onemob.getHp() / onemob.getExp());

            if (hp != null) {
                newhp = hp;
            } else if (php != null) {
                newhp = (int) (onemob.getMaxHp() * (php / 100));
            } else {
                newhp = onemob.getMaxHp();
            }
            if (exp != null) {
                newexp = exp;
            } else if (pexp != null) {
                newexp = (int) (onemob.getExp() * (pexp / 100));
            } else {
                newexp = onemob.getExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }
            double newExpRatio = ((double) newhp / newexp);
            if (newExpRatio < oldExpRatio && newexp > 0) {
                mc.dropMessage("The new hp/exp ratio is better than the old one. (" + newExpRatio + " < " +
                        oldExpRatio + ") Please don't do this");
                return;
            }
            MapleMonsterStats overrideStats = new MapleMonsterStats();
            overrideStats.setHp(newhp);
            overrideStats.setExp(newexp);
            overrideStats.setMp(onemob.getMaxMp());
            overrideStats.setGMSpawn();
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                c.getPlayer().getMap().spawnMonsterOnGroudBelow(mob, c.getPlayer().getPosition());
            }
        }
    }

    @Override
    public GMCommandDefinition[] getDefinition() {
        return new GMCommandDefinition[]{
                    new GMCommandDefinition("spawn", "[hp newHp] [exp newExp] [php procentual Hp] [pexp procentual Exp] monsterid", "Spawns the monster with the given id"),
                    new GMCommandDefinition("mob", "monstername", "Spawns some bosses"),};
    }
}
