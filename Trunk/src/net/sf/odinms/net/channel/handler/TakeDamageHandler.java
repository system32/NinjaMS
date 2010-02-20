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

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.Enums.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.constants.Skills;
import net.sf.odinms.server.constants.SpecialStuff;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MobAttackInfo;
import net.sf.odinms.server.life.MobAttackInfoFactory;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.life.MobSkillFactory;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class TakeDamageHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        // damage from map object
        // 26 00 EB F2 2B 01 FE 25 00 00 00 00 00
        // damage from monster
        // 26 00 0F 60 4C 00 FF 48 01 00 00 B5 89 5D 00 CC CC CC CC 00 00 00 00
        MapleCharacter player = c.getPlayer();

        slea.readInt();
        int damagefrom = slea.readByte();
        slea.readByte();
        int damage = slea.readInt();
        int oid = 0;
        int monsteridfrom = 0;
        int pgmr = 0;
        int direction = 0;
        int pos_x = 0;
        int pos_y = 0;
        int fake = 0;
        boolean is_pgmr = false;
        boolean is_pg = true;
        int mpattack = 0;

        MapleMonster attacker = null;

        if (damagefrom != -2) {
            monsteridfrom = slea.readInt();
            oid = slea.readInt();
            if (monsteridfrom != 0 && damage != -1) {
                attacker = (MapleMonster) player.getMap().getMapObject(monsteridfrom);
            } else {
                attacker = (MapleMonster) player.getMap().getMapObject(oid);
            }
            direction = slea.readByte();
        }

        if (damagefrom != -1 && damagefrom != -2 && attacker != null) {
            MobAttackInfo attackInfo = MobAttackInfoFactory.getMobAttackInfo(attacker, damagefrom);
            if (attackInfo != null) {
                if (attackInfo.isDeadlyAttack()) {
                    mpattack = player.getMp() - 1;
                }
                mpattack += attackInfo.getMpBurn();

                MobSkill skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
                if (skill != null && damage > 0) {
                    skill.applyEffect(player, attacker, false);
                }
                if (attacker != null) {
                    attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
                    if (player.getBuffedValue(MapleBuffStat.MANA_REFLECTION) != null && damage > 0 && !attacker.isBoss()) {
                        int[] manaReflectSkillId = {2121002, 2221002, 2321002};
                        for (int manaReflect : manaReflectSkillId) {
                            ISkill manaReflectSkill = SkillFactory.getSkill(manaReflect);
                            if (player.isBuffFrom(MapleBuffStat.MANA_REFLECTION, manaReflectSkill) && player.getSkillLevel(manaReflectSkill) > 0 && manaReflectSkill.getEffect(player.getSkillLevel(manaReflectSkill)).makeChanceResult()) {
                                int bouncedamage = (int) (damage * (manaReflectSkill.getEffect(player.getSkillLevel(manaReflectSkill)).getX() / 100));
                                if (bouncedamage > attacker.getMaxHp() * .2) {
                                    bouncedamage = (int) (attacker.getMaxHp() * .2);
                                }
                                player.getMap().damageMonster(player, attacker, bouncedamage);
                                player.getMap().broadcastMessage(player, MaplePacketCreator.damageMonster(oid, bouncedamage), true);
                                player.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(manaReflect, 5));
                                player.getMap().broadcastMessage(player, MaplePacketCreator.showBuffeffect(player.getId(), manaReflect, 5), false);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (damage == -1) {
            int job = (int) (player.getPath() == 2 ? 1411 : 412 / 10 - 40);
            fake = 4020002 + (job * 100000);
        }

        if (damage < -1 || damage > 60000) {
            return; // dont a/b
        }
        player.getCheatTracker().checkTakeDamage();

        if (damage > 0) {
            player.getCheatTracker().setAttacksWithoutHit(0);
            player.getCheatTracker().resetHPRegen();
        }

        if (damage > 0 && !player.isHidden()) {
            if (damagefrom == -1) {
                Integer pguard = player.getBuffedValue(MapleBuffStat.POWERGUARD);
                if (pguard != null) {
                    // why do we have to do this? -.- the client shows the damage...
                    attacker = (MapleMonster) player.getMap().getMapObject(oid);
                    if (attacker != null && !attacker.isBoss()) {
                        int bouncedamage = (int) (damage * (pguard.doubleValue() / 100));
                        bouncedamage = Math.min(bouncedamage, attacker.getMaxHp() / 10);
                        player.getMap().damageMonster(player, attacker, bouncedamage);
                        damage -= bouncedamage;
                        player.getMap().broadcastMessage(player, MaplePacketCreator.damageMonster(oid, bouncedamage), false, true);
                    }
                }
            }
            if (damagefrom != -2) {
                Integer achilles = 0;
                ISkill achilles1 = null;
                switch (player.getJob().getId()) {
                    case 112:
                        achilles = player.getSkillLevel(SkillFactory.getSkill(1120004));
                        achilles1 = SkillFactory.getSkill(1120004);
                        break;
                    case 122:
                        achilles = player.getSkillLevel(SkillFactory.getSkill(1220005));
                        achilles1 = SkillFactory.getSkill(1220005);
                        break;
                    case 132:
                        achilles = player.getSkillLevel(SkillFactory.getSkill(1320005));
                        achilles1 = SkillFactory.getSkill(1320005);
                        break;
                }
                if (achilles != 0) {
                    int x = achilles1.getEffect(achilles).getX();
                    double multiplier = x / 1000.0;
                    int newdamage = (int) (multiplier * damage);
                    damage = newdamage;
                }
            }
            Integer mguard = player.getBuffedValue(MapleBuffStat.MAGIC_GUARD);
            Integer mesoguard = player.getBuffedValue(MapleBuffStat.MESOGUARD);
            if (mguard != null && mpattack == 0) {
                int mploss = (int) (damage * (mguard.doubleValue() / 100.0));
                int hploss = damage - mploss;
                if (mploss > player.getMp()) {
                    hploss += mploss - player.getMp();
                    mploss = player.getMp();
                }
                player.addMPHP(-hploss, -mploss);
            } else if (mesoguard != null) {
                damage = (damage % 2 == 0) ? damage / 2 : (damage / 2) + 1;
                int mesoloss = (int) (damage * (mesoguard.doubleValue() / 100.0));
                if (player.getMeso() < mesoloss) {
                    player.gainMeso(-player.getMeso(), false);
                    player.cancelBuffStats(MapleBuffStat.MESOGUARD);
                } else {
                    player.gainMeso(-mesoloss, false);
                }
                player.addMPHP(-damage, -mpattack);
            } else {
                player.addMPHP(-damage, -mpattack);
            }
            Integer battleship = player.getBuffedValue(MapleBuffStat.MONSTER_RIDING);
            if (battleship != null) {
                if (battleship.intValue() == Skills.Corsair.Battleship) {
                    player.decreaseBattleshipHp(damage);
                }
            }
        }
        if (!player.isHidden()) {
            if (monsteridfrom == 00000000 || MapleLifeFactory.getMonster(monsteridfrom) != null) {
                player.getMap().broadcastMessage(player, MaplePacketCreator.damagePlayer(damagefrom, monsteridfrom, player.getId(), damage, fake, direction, is_pgmr, pgmr, is_pg, oid, pos_x, pos_y), false);
            } else {
                AutobanManager.getInstance().autoban(c, "'Attacked' by a monster that doesn't exist. Also known as map crashing.");
            }
        }

         if (SpecialStuff.getInstance().isDojoMap(player.getMapId())) {
            player.setDojoEnergy(player.isJounin() ? 300 : player.getDojoEnergy() < 300 ? player.getDojoEnergy() + 1 : 300);
            player.getClient().getSession().write(MaplePacketCreator.getEnergy(player.getDojoEnergy()));
        }
    }
}
