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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.odinms.client.Enums.MapleBuffStat;
import net.sf.odinms.client.Enums.MapleJob;

import net.sf.odinms.client.ISkill;

import net.sf.odinms.client.MapleCharacter;

import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.server.constants.Skills;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.pvp.MaplePvp;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.constants.GameConstants;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.constants.Modes;
import net.sf.odinms.server.life.Element;
import net.sf.odinms.server.life.ElementalEffectiveness;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.LittleEndianAccessor;

public abstract class AbstractDealDamageHandler extends AbstractMaplePacketHandler {

    private int PVP_MAP = 910000001;

    private byte damageNerf(MapleCharacter player) {
        if (player.getGMSMode() > 0) {
            return 1;
        }
        byte fuck = 2;
        byte msi = player.getMaxStatItems();
        if (msi >= 1) {
            fuck = (byte) ((msi / 2) + 1);
        }
        if (player.getMapId() == 280030000) {
        }
        return fuck;
    }
    // private static Logger log = LoggerFactory.getLogger(AbstractDealDamageHandler.class);

    public class AttackInfo {

        public int numAttacked, numDamage, numAttackedAndDamage;
        public int skill, stance, direction, charge, pos;
        public List<Pair<Integer, List<Integer>>> allDamage;
        public boolean isHH = false;
        public int speed = 4;

        private MapleStatEffect getAttackEffect(MapleCharacter chr, ISkill theSkill) {
            ISkill mySkill = theSkill;
            if (mySkill == null) {
                mySkill = SkillFactory.getSkill(skill);
            }
            int skillLevel = chr.getSkillLevel(mySkill);
            if (mySkill.getId() == 1009 || mySkill.getId() == 10001009) {
                skillLevel = 1;
            }
            if (skillLevel == 0) {
                return null;
            }
            return mySkill.getEffect(skillLevel);
        }

        public MapleStatEffect getAttackEffect(MapleCharacter chr) {
            return getAttackEffect(chr, null);
        }
    }

    protected void applyAttack(AttackInfo attack, MapleCharacter player, int maxDamagePerMonster, int attackCount) {
        player.getCheatTracker().resetHPRegen();
        if (player.isJounin()) {
            player.heal();
        }
        ISkill theSkill = null;
        MapleStatEffect attackEffect = null;
        if (attack.skill != 0) {
            theSkill = SkillFactory.getSkill(attack.skill);
            attackEffect = attack.getAttackEffect(player, theSkill);
            if (attackEffect == null) {
                //AutobanManager.getInstance().autoban(player.getClient(), "Using a skill he doesn't have (" + attack.skill + ")");
            }
            if (attack.skill != Skills.Cleric.Heal && attack.skill != Skills.Marauder.EnergyCharge && attack.skill != Skills.ThunderBreaker2.EnergyCharge) {
                if (player.isAlive()) {
                    attackEffect.applyTo(player);
                } else {
                    player.getClient().getSession().write(MaplePacketCreator.enableActions());
                }
            }
        }
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }

        if (attackCount != attack.numDamage && attack.skill != Skills.ChiefBandit.MesoExplosion && attack.numDamage != attackCount * 2) {
            player.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT, attack.numDamage + "/" + attackCount);
        }
        int totDamage = 0;
        MapleMap map = player.getMap();
        //pvp monster bombscase
        if (attack.skill != 2301002 && attack.skill != 4201004 && attack.skill != 1111008 && player.getMapId() == PVP_MAP && player.getClient().getChannel() == 3) {//checks
            MaplePvp.doPvP(player, map, attack);
        }
        //end pvp monster bombs
        synchronized (map) {
            if (attack.skill == Skills.ChiefBandit.MesoExplosion) {
                for (Pair<Integer, List<Integer>> oned : attack.allDamage) {
                    MapleMapObject mapobject = map.getMapObject(oned.getLeft().intValue());
                    if (mapobject != null && mapobject.getType() == MapleMapObjectType.ITEM) {
                        MapleMapItem mapitem = (MapleMapItem) mapobject;
                        if (mapitem.getMeso() > 0) {
                            synchronized (mapitem) {
                                if (mapitem.isPickedUp()) {
                                    return;
                                }
                                map.removeMapObject(mapitem);
                                map.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 4, 0), mapitem.getPosition());
                                mapitem.setPickedUp(true);
                            }
                        } else if (mapitem.getMeso() == 0) {
                            player.getCheatTracker().registerOffense(CheatingOffense.ETC_EXPLOSION);
                            return;
                        }
                    } else if (mapobject != null && mapobject.getType() != MapleMapObjectType.MONSTER) {
                        player.getCheatTracker().registerOffense(CheatingOffense.EXPLODING_NONEXISTANT);
                        return; // etc explosion, exploding nonexistant things, etc.

                    }
                }
            }

            for (Pair<Integer, List<Integer>> oned : attack.allDamage) {
                MapleMonster monster = map.getMonsterByOid(oned.getLeft().intValue());

                if (monster != null) {
                    int totDamageToOneMonster = 0;
                    for (Integer eachd : oned.getRight()) {
                        totDamageToOneMonster += eachd.intValue();
                    }
                    totDamage += totDamageToOneMonster;

                    Point playerPos = player.getPosition();
                    if (totDamageToOneMonster > attack.numDamage + 1) {
                        int dmgCheck = player.getCheatTracker().checkDamage(totDamageToOneMonster);
                        if (dmgCheck > 5 && totDamageToOneMonster > 90000 && player.getMaxStatItems() == 0) {
                            AutobanManager.getInstance().autoban(player.getClient(), "Same damage over 5 times. Damage: " + totDamageToOneMonster + " with Rebirths/Level: " + player.getReborns() + "/" + player.getLevel());
                        }
                    }
                    checkHighDamage(player, monster, attack, theSkill, attackEffect, totDamageToOneMonster, maxDamagePerMonster);
                    double distance = playerPos.distanceSq(monster.getPosition());
                    if (distance > 400000.0) {
                        player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, Double.toString(Math.sqrt(distance)));
                    }
                    if (!monster.isControllerHasAggro()) {
                        if (monster.getController() == player) {
                            monster.setControllerHasAggro(true);
                        } else {
                            monster.switchController(player, true);
                        }
                    }
                    // only ds, sb, assaulter, normal (does it work for thieves, bs, or assasinate?)
                    if ((attack.skill == Skills.Rogue.DoubleStab || attack.skill == Skills.Bandit.SavageBlow || attack.skill == 0 || attack.skill == Skills.ChiefBandit.Assaulter || attack.skill == Skills.Shadower.BoomerangStep) && player.getBuffedValue(MapleBuffStat.PICKPOCKET) != null) {
                        handlePickPocket(player, monster, oned);
                    }

                    if (attack.skill == Skills.Assassin.Drain) { // drain
                        ISkill drain = SkillFactory.getSkill(4101005);
                        int gainhp = (int) ((double) totDamageToOneMonster * (double) drain.getEffect(player.getSkillLevel(drain)).getX() / 100.0);
                        gainhp = Math.min(monster.getMaxHp(), Math.min(gainhp, player.getMaxHp() / 2));
                        player.addHP(gainhp);
                    }

                    if (attack.skill == 4201004) {
                        handleSteal(player, monster);
                    }
                    if (attack.skill == 5111004) { // energy drain
                        int gainhpE = (int) ((double) totDamage * (double) SkillFactory.getSkill(5111004).getEffect(player.getSkillLevel(SkillFactory.getSkill(5111004))).getX() / 100.0);
                        gainhpE = Math.min(monster.getMaxHp(), Math.min(gainhpE, player.getMaxHp() / 2));
                        player.addHP(gainhpE);
                    }
                    if (player.getBuffedValue(MapleBuffStat.HAMSTRING) != null) {
                        ISkill hamstring = SkillFactory.getSkill(Skills.Bowmaster.Hamstring);
                        if (hamstring.getEffect(player.getSkillLevel(hamstring)).makeChanceResult()) {
                            MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.SPEED, hamstring.getEffect(player.getSkillLevel(hamstring)).getX()), hamstring, false);
                            monster.applyStatus(player, monsterStatusEffect, false, hamstring.getEffect(player.getSkillLevel(hamstring)).getY() * 1000);
                        }
                    }

                    if (player.getBuffedValue(MapleBuffStat.BLIND) != null) {
                        ISkill blind = SkillFactory.getSkill(Skills.Marksman.Blind);
                        if (blind.getEffect(player.getSkillLevel(blind)).makeChanceResult()) {
                            MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.ACC, blind.getEffect(player.getSkillLevel(blind)).getX()), blind, false);
                            monster.applyStatus(player, monsterStatusEffect, false, blind.getEffect(player.getSkillLevel(blind)).getY() * 1000);
                        }
                    }

                    if (player.getJob().isA(MapleJob.WHITEKNIGHT)) {
                        int[] charges = new int[]{Skills.WhiteKnight.BlizzardChargeBW, Skills.WhiteKnight.IceChargeSword};
                        for (int charge : charges) {
                            ISkill chargeSkill = SkillFactory.getSkill(charge);

                            if (player.isBuffFrom(MapleBuffStat.WK_CHARGE, chargeSkill)) {
                                final ElementalEffectiveness iceEffectiveness = monster.getEffectiveness(Element.ICE);
                                if (totDamageToOneMonster > 0 && iceEffectiveness == ElementalEffectiveness.NORMAL || iceEffectiveness == ElementalEffectiveness.WEAK) {
                                    MapleStatEffect chargeEffect = chargeSkill.getEffect(player.getSkillLevel(chargeSkill));
                                    MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.FREEZE, 1), chargeSkill, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, chargeEffect.getY() * 2000);
                                }
                                break;
                            }
                        }
                    }

                    ISkill venomNL = SkillFactory.getSkill(Skills.NightLord.VenomousStar);
                    ISkill venomShadower = SkillFactory.getSkill(Skills.Shadower.VenomousStab);
                    if (player.getSkillLevel(venomNL) > 0) {
                        MapleStatEffect venomEffect = venomNL.getEffect(player.getSkillLevel(venomNL));
                        for (int i = 0; i < attackCount; i++) {
                            if (venomEffect.makeChanceResult() == true) {
                                if (monster.getVenomMulti() < 3) {
                                    monster.setVenomMulti((monster.getVenomMulti() + 1));
                                    MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), venomNL, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration(), true);
                                }
                            }
                        }
                    } else if (player.getSkillLevel(venomShadower) > 0) {
                        MapleStatEffect venomEffect = venomShadower.getEffect(player.getSkillLevel(venomShadower));
                        for (int i = 0; i < attackCount; i++) {
                            if (venomEffect.makeChanceResult() == true) {
                                if (monster.getVenomMulti() < 3) {
                                    monster.setVenomMulti((monster.getVenomMulti() + 1));
                                    MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), venomShadower, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration(), true);
                                }
                            }
                        }
                    }

                    if (totDamageToOneMonster > 0 && attackEffect != null && attackEffect.getMonsterStati().size() > 0) {
                        if (attackEffect.makeChanceResult()) {
                            MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(attackEffect.getMonsterStati(), theSkill, false);
                            monster.applyStatus(player, monsterStatusEffect, attackEffect.isPoison(), attackEffect.getDuration());
                        }
                    }

                    if (attack.isHH && !monster.isBoss()) {
                        int fuck = Math.min(199999, monster.getHp() - 1);
                        map.damageMonster(player, monster, fuck);
                    } else if (attack.isHH && monster.isBoss()) {
                        map.damageMonster(player, monster, 199999);
                    } else if (monster.isBoss()) {
                        if (attack.skill != Skills.Shadower.Assassinate && attack.skill != Skills.Marksman.Snipe) {
                            map.damageMonster(player, monster, totDamageToOneMonster / damageNerf(player));
                        } else {
                            int fuck = Math.min(19999999, monster.getHp() / 5);
                            fuck = Math.min(fuck, totDamageToOneMonster);
                            map.damageMonster(player, monster, fuck);
                        }
                    } else {
                        map.damageMonster(player, monster, totDamageToOneMonster);
                    }
                    if (Modes.getInstance(player).hasModeOn()) {
                        int modeDamage = totDamageToOneMonster / 4;
                        if (attack.isHH && !monster.isBoss()) {
                            int fuck = Math.min(199999, monster.getHp() - 1);
                            map.damageMonster(player, monster, fuck);
                            modeDamage = fuck;
                        } else if (attack.isHH && monster.isBoss()) {
                            map.damageMonster(player, monster, 199999);
                            modeDamage = 199999;
                        } else if (monster.isBoss()) {
                            if (attack.skill != Skills.Shadower.Assassinate && attack.skill != Skills.Marksman.Snipe) {
                                map.damageMonster(player, monster, totDamageToOneMonster / damageNerf(player));
                            } else {
                                int fuck = Math.min(19999999, monster.getHp() / 5);
                                fuck = Math.min(fuck, modeDamage);
                                map.damageMonster(player, monster, fuck);
                            }
                        } else {
                            map.damageMonster(player, monster, modeDamage);
                        }
                        map.broadcastMessage(MaplePacketCreator.damageMonster(monster.getObjectId(), modeDamage));
                    }
                }
            }
        }
    }

    private void handlePickPocket(MapleCharacter player, MapleMonster monster, Pair<Integer, List<Integer>> oned) {
        ISkill pickpocket = SkillFactory.getSkill(Skills.ChiefBandit.Pickpocket);
        int delay = 0;
        int maxmeso = player.getBuffedValue(MapleBuffStat.PICKPOCKET).intValue();
        int reqdamage = 20000;
        Point monsterPosition = monster.getPosition();

        for (Integer eachd : oned.getRight()) {
            if (pickpocket.getEffect(player.getSkillLevel(pickpocket)).makeChanceResult()) {
                double perc = (double) eachd / (double) reqdamage;

                final int todrop = Math.min((int) Math.max(perc * (double) maxmeso, (double) 1), maxmeso);
                final MapleMap tdmap = player.getMap();
                final Point tdpos = new Point((int) (monsterPosition.getX() + (Math.random() * 100) - 50), (int) (monsterPosition.getY()));
                final MapleMonster tdmob = monster;
                final MapleCharacter tdchar = player;

                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        tdmap.spawnMesoDrop(todrop, todrop, tdpos, tdmob, tdchar, false);
                    }
                }, delay);

                delay += 200;
            }
        }
    }

    private void checkHighDamage(MapleCharacter player, MapleMonster monster, AttackInfo attack, ISkill theSkill, MapleStatEffect attackEffect, int damageToMonster, int maximumDamageToMonster) {
        if ((attack.skill > 2000000 && attack.skill < 3000000) || (attack.skill > 12000000 && attack.skill < 13000000)) {
            if (attack.skill != Skills.Cleric.Heal) {
                if (damageToMonster > 90000 && player.getTotalInt() < 500 && player.getReborns() < 3) {
                    AutobanManager.getInstance().autoban(player.getClient(), damageToMonster
                            + " damage to monster: " + monster.getName() + " with " + player.getReborns() + " rebirths at level " + player.getLevel() + " (Job: " + GameConstants.getJobName(player.getJob().getId()) + " Skill: " + SkillFactory.getSkillName(attack.skill) + ")");
                } else if (damageToMonster > 50000000) {
                    AutobanManager.getInstance().autoban(player.getClient(), damageToMonster
                            + " damage to monster: " + monster.getName() + " with " + player.getReborns() + " rebirths (Job: " + GameConstants.getJobName(player.getJob().getId()) + " Skill: " + SkillFactory.getSkillName(attack.skill) + ")");
                }
            }
        } else {
            if (attack.skill != Skills.ChiefBandit.MesoExplosion && attack.skill != Skills.Marksman.Snipe && attack.skill != Skills.Shadower.Assassinate) {
                int str = player.getTotalStr();
                int dex = player.getTotalDex();
                int luk = player.getTotalLuk();
                int wa = player.getTotalWatk();
                int rb = player.getReborns();
                int damount = player.getDAmount();
                if (damageToMonster > 90000 && wa < 150 && str < 500 && dex < 500 && luk < 500) {
                    AutobanManager.getInstance().autoban(player.getClient(), damageToMonster
                            + " damage to monster: " + monster.getName() + " with no rebirths at level " + player.getLevel() + " (Job: " + GameConstants.getJobName(player.getJob().getId()) + " Skill: " + SkillFactory.getSkillName(attack.skill) + ")");
                } else if (damageToMonster > 900000 && rb == 0 && damount == 0) {
                    AutobanManager.getInstance().autoban(player.getClient(), damageToMonster
                            + " damage to monster: " + monster.getName() + " with no rebirths at level " + player.getLevel() + " (Job: " + GameConstants.getJobName(player.getJob().getId()) + " Skill: " + SkillFactory.getSkillName(attack.skill) + ")");
                } else if (damageToMonster > 75000000 && rb < 20 && damount == 0 && str < 30000 && dex < 30000 && luk < 30000) {
                    AutobanManager.getInstance().autoban(player.getClient(), damageToMonster
                            + " damage to monster: " + monster.getName() + " with " + rb + " rebirths (Job: " + GameConstants.getJobName(player.getJob().getId()) + " Skill: " + SkillFactory.getSkillName(attack.skill) + ")");
                } else if (damageToMonster > 120000000 && rb < 100 && damount == 0 && str < 50000 && dex < 50000 && luk < 50000) {
                    AutobanManager.getInstance().autoban(player.getClient(), damageToMonster
                            + " damage to monster: " + monster.getName() + " with " + rb + " rebirths (Job: " + GameConstants.getJobName(player.getJob().getId()) + " Skill: " + SkillFactory.getSkillName(attack.skill) + ")");
                } else if (damageToMonster > 250000000 && rb < 150 && damount == 0 && str < 75000 && dex < 75000 && luk < 75000) {
                    AutobanManager.getInstance().autoban(player.getClient(), damageToMonster
                            + " damage to monster: " + monster.getName() + " with " + rb + " rebirths (Job: " + GameConstants.getJobName(player.getJob().getId()) + " Skill: " + SkillFactory.getSkillName(attack.skill) + ")");
                }
            }
        }
    }

    public AttackInfo parseRanged(MapleCharacter chr, LittleEndianAccessor lea) {
        AttackInfo ret = new AttackInfo();
        lea.readByte();
        ret.numAttackedAndDamage = lea.readByte();
        ret.numAttacked = (ret.numAttackedAndDamage >>> 4) & 0xF;
        ret.numDamage = ret.numAttackedAndDamage & 0xF;
        ret.allDamage = new ArrayList<Pair<Integer, List<Integer>>>();
        ret.skill = lea.readInt();
        lea.readInt(); // Mob's .img size
        lea.readInt();
        switch (ret.skill) {
            case Skills.Bowmaster.Hurricane:
            case Skills.Marksman.PiercingArrow:
            case Skills.Corsair.RapidFire:
            case Skills.WindArcher3.Hurricane:
                lea.readInt();
                break;
        }
        lea.readByte(); // Projectile that is thrown
        ret.stance = lea.readByte();
        lea.readByte(); // Weapon subclass
        ret.speed = lea.readByte();
        lea.readInt();
        lea.readShort(); // Slot
        lea.readShort(); // CS Star
        lea.readByte();
        for (int i = 0; i < ret.numAttacked; i++) {
            int mobId = lea.readInt();
            lea.skip(14);
            List<Integer> allDamageNumbers = new ArrayList<Integer>();
            for (int j = 0; j < ret.numDamage; j++) {
                allDamageNumbers.add(Integer.valueOf(lea.readInt()));
            }
            ret.allDamage.add(new Pair<Integer, List<Integer>>(Integer.valueOf(mobId), allDamageNumbers));
            lea.readInt();
        }
        lea.readInt();
        ret.pos = lea.readInt();
        return ret;
    }

    public AttackInfo parseDamage(MapleCharacter chr, LittleEndianAccessor lea, boolean ranged) {
        AttackInfo ret = new AttackInfo();

        lea.readByte();
        ret.numAttackedAndDamage = lea.readByte();
        ret.numAttacked = (ret.numAttackedAndDamage >>> 4) & 0xF;
        ret.numDamage = ret.numAttackedAndDamage & 0xF;
        ret.allDamage = new ArrayList<Pair<Integer, List<Integer>>>();
        ret.skill = lea.readInt();
        lea.readInt();
        lea.readInt();
        lea.readByte();
        ret.stance = lea.readByte();

        switch (ret.skill) {
            case Skills.FPArchMage.BigBang:
            case Skills.ILArchMage.BigBang:
            case Skills.Bishop.BigBang:
            case Skills.Gunslinger.Grenade:
            case Skills.Brawler.CorkscrewBlow:
            case Skills.NightWalker3.PoisonBomb:
            case Skills.ThunderBreaker2.CorkscrewBlow:
                ret.charge = lea.readInt();
                break;
            case Skills.Bowmaster.Hurricane:
            case Skills.Marksman.PiercingArrow:
            case Skills.Corsair.RapidFire:
            case Skills.WindArcher3.Hurricane:
                lea.readInt();
            default:
                ret.charge = 0;
                break;
        }

        if (ret.skill == Skills.Paladin.HeavensHammer) {
            ret.isHH = true;
        }

        if (ret.skill == Skills.ChiefBandit.MesoExplosion) {
            return parseMesoExplosion(lea, ret);
        }

        if (ranged && ret.skill != Skills.DawnWarrior2.SoulBlade && ret.skill != Skills.ThunderBreaker3.SharkWave) {
            lea.readByte();
            ret.speed = lea.readByte();
            lea.readByte();
            ret.direction = lea.readByte();
            lea.skip(7);
            if (ret.skill == Skills.Bowmaster.Hurricane || ret.skill == Skills.Marksman.PiercingArrow || ret.skill == Skills.Corsair.RapidFire || ret.skill == Skills.WindArcher3.Hurricane) {
                lea.skip(4);
            }
        } else if (ret.skill == Skills.DawnWarrior2.SoulBlade || ret.skill == Skills.ThunderBreaker3.SharkWave) {
            lea.readByte();
            ret.speed = lea.readByte();
            lea.skip(3);
            ret.direction = lea.readByte();
            lea.skip(5);
        } else {
            lea.readByte();
            ret.speed = lea.readByte();
            lea.skip(3);
            lea.readByte();
        }

        for (int i = 0; i < ret.numAttacked; i++) {
            int oid = lea.readInt();
            lea.readByte();
            lea.skip(13);
            List<Integer> allDamageNumbers = new ArrayList<Integer>();
            for (int j = 0; j < ret.numDamage; j++) {
                int damage = lea.readInt();
                MapleStatEffect effect = null;
                if (ret.skill != 0) {
                    effect = SkillFactory.getSkill(ret.skill).getEffect(chr.getSkillLevel(SkillFactory.getSkill(ret.skill)));
                }
                if (damage != 0 && effect != null && effect.getFixedDamage() != 0) {
                    damage = effect.getFixedDamage();
                }
                allDamageNumbers.add(Integer.valueOf(damage));
            }
            lea.readInt();
            ret.allDamage.add(new Pair<Integer, List<Integer>>(Integer.valueOf(oid), allDamageNumbers));
        }
        if (ranged) {
            lea.readInt();
        }
        ret.pos = lea.readInt();

        return ret;
    }

    public AttackInfo parseMesoExplosion(LittleEndianAccessor lea, AttackInfo ret) {
        if (ret.numAttackedAndDamage == 0) {
            lea.skip(10);
            int bullets = lea.readByte();
            for (int j = 0; j < bullets; j++) {
                int mesoid = lea.readInt();
                lea.skip(1);
                ret.allDamage.add(new Pair<Integer, List<Integer>>(Integer.valueOf(mesoid), null));
            }
            return ret;
        } else {
            lea.skip(6);
        }
        for (int i = 0; i < ret.numAttacked; i++) {
            int oid = lea.readInt();
            lea.skip(12);
            int bullets = lea.readByte();
            List<Integer> allDamageNumbers = new ArrayList<Integer>();
            for (int j = 0; j < bullets; j++) {
                int damage = lea.readInt();
                allDamageNumbers.add(Integer.valueOf(damage));
            }
            ret.allDamage.add(new Pair<Integer, List<Integer>>(Integer.valueOf(oid), allDamageNumbers));
            lea.skip(4);
        }
        lea.skip(4);
        int bullets = lea.readByte();
        for (int j = 0; j < bullets; j++) {
            int mesoid = lea.readInt();
            lea.skip(1); // 0 = not hit, 1 = hit 1, 3 = hit 2, 3F = hit 6 ????
            ret.allDamage.add(new Pair<Integer, List<Integer>>(Integer.valueOf(mesoid), null));
        }
        return ret;
    }

     private void handleSteal(MapleCharacter chr, MapleMonster mob) {
        ISkill steal = SkillFactory.getSkill(4201004);
        int level = chr.getSkillLevel(steal);
        if (steal.getEffect(level).makeChanceResult()) {
            int toSteal = mob.getDrop();
            MapleInventoryManipulator.addById(chr.getClient(), toSteal, (short) 1);
            mob.addStolen(toSteal);
        }
    }
}
