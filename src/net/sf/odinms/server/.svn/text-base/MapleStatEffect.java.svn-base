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

/*
 * ItemEndEffect.java
 * 
 * Created on 29. November 2007, 01:34
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */
package net.sf.odinms.server;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import net.sf.odinms.client.Enums.MapleBuffStat;
import net.sf.odinms.client.Enums.MapleJob;
import net.sf.odinms.client.Enums.MapleStat;

import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;

import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.server.constants.Skills;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleDoor;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleMist;
import net.sf.odinms.server.maps.MapleSummon;
import net.sf.odinms.server.maps.SummonMovementType;
import net.sf.odinms.tools.ArrayMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;

/**
 * @author Matze
 * @author Frz
 */
public class MapleStatEffect implements Serializable {

    static final long serialVersionUID = 9179541993413738569L;
    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MapleStatEffect.class);
    private short watk, matk, wdef, mdef, acc, avoid, hands, speed, jump;
    private short hp, mp;
    private double hpR, mpR;
    private short mpCon, hpCon;
    private int duration;
    private boolean overTime;
    private int sourceid;
    private int moveTo;
    private boolean skill;
    private List<Pair<MapleBuffStat, Integer>> statups;
    private Map<MonsterStatus, Integer> monsterStatus;
    private int x, y, z;
    private double prop;
    private int itemCon, itemConNo;
    private int damage, attackCount, bulletCount, bulletConsume;
    private Point lt, rb;
    private int mobCount;
    private int moneyCon;
    private int cooldown;
    private int fixDamage;
    private int morphId = 0;

    private MapleStatEffect() {
    }

    public static MapleStatEffect loadSkillEffectFromData(MapleData source, int skillid, boolean overtime) {
        return loadFromData(source, skillid, true, overtime);
    }

    public static MapleStatEffect loadItemEffectFromData(MapleData source, int itemid) {
        return loadFromData(source, itemid, false, false);
    }

    private static void addBuffStatPairToListIfNotZero(List<Pair<MapleBuffStat, Integer>> list, MapleBuffStat buffstat, Integer val) {
        if (val.intValue() != 0) {
            list.add(new Pair<MapleBuffStat, Integer>(buffstat, val));
        }
    }

    private static MapleStatEffect loadFromData(MapleData source, int sourceid, boolean skill, boolean overTime) {
        MapleStatEffect ret = new MapleStatEffect();
        ret.duration = MapleDataTool.getInt("time", source, -1);
        ret.hp = (short) MapleDataTool.getInt("hp", source, 0);
        ret.hpR = MapleDataTool.getInt("hpR", source, 0) / 100.0;
        ret.mp = (short) MapleDataTool.getInt("mp", source, 0);
        ret.mpR = MapleDataTool.getInt("mpR", source, 0) / 100.0;
        ret.mpCon = (short) MapleDataTool.getInt("mpCon", source, 0);
        ret.hpCon = (short) MapleDataTool.getInt("hpCon", source, 0);
        int iprop = MapleDataTool.getInt("prop", source, 100);
        ret.prop = iprop / 100.0;
        ret.mobCount = MapleDataTool.getInt("mobCount", source, 1);
        ret.cooldown = MapleDataTool.getInt("cooltime", source, 0);
        ret.fixDamage = MapleDataTool.getInt("fixdamage", source, 0);
        ret.morphId = MapleDataTool.getInt("morph", source, 0);

        ret.sourceid = sourceid;
        ret.skill = skill;

        if (!ret.skill && ret.duration > -1) {
            ret.overTime = true;
        } else {
            ret.duration *= 1000;
            ret.overTime = overTime;
        }
        ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<Pair<MapleBuffStat, Integer>>();

        ret.watk = (short) MapleDataTool.getInt("pad", source, 0);
        ret.wdef = (short) MapleDataTool.getInt("pdd", source, 0);
        ret.matk = (short) MapleDataTool.getInt("mad", source, 0);
        ret.mdef = (short) MapleDataTool.getInt("mdd", source, 0);
        ret.acc = (short) MapleDataTool.getIntConvert("acc", source, 0);
        ret.avoid = (short) MapleDataTool.getInt("eva", source, 0);
        ret.speed = (short) MapleDataTool.getInt("speed", source, 0);
        ret.jump = (short) MapleDataTool.getInt("jump", source, 0);
        if (ret.overTime && ret.getSummonMovementType() == null) {
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WATK, Integer.valueOf(ret.watk));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WDEF, Integer.valueOf(ret.wdef));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MATK, Integer.valueOf(ret.matk));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MDEF, Integer.valueOf(ret.mdef));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ACC, Integer.valueOf(ret.acc));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.AVOID, Integer.valueOf(ret.avoid));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.SPEED, Integer.valueOf(ret.speed));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.JUMP, Integer.valueOf(ret.jump));
        }

        MapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = (Point) ltd.getData();
            ret.rb = (Point) source.getChildByPath("rb").getData();
        }

        int x = MapleDataTool.getInt("x", source, 0);
        ret.x = x;
        ret.y = MapleDataTool.getInt("y", source, 0);
        ret.z = MapleDataTool.getInt("z", source, 0);
        ret.damage = MapleDataTool.getIntConvert("damage", source, 100);
        ret.attackCount = MapleDataTool.getIntConvert("attackCount", source, 1);
        ret.bulletCount = MapleDataTool.getIntConvert("bulletCount", source, 1);
        ret.bulletConsume = MapleDataTool.getIntConvert("bulletConsume", source, 0);
        ret.moneyCon = MapleDataTool.getIntConvert("moneyCon", source, 0);

        ret.itemCon = MapleDataTool.getInt("itemCon", source, 0);
        ret.itemConNo = MapleDataTool.getInt("itemConNo", source, 0);
        ret.moveTo = MapleDataTool.getInt("moveTo", source, -1);

        Map<MonsterStatus, Integer> monsterStatus = new ArrayMap<MonsterStatus, Integer>();

        if (skill) {
            switch (sourceid) {
                case Skills.Magician.MagicGuard:
                case Skills.BlazeWizard1.MagicGuard:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAGIC_GUARD, Integer.valueOf(x)));
                    break;
                case Skills.Cleric.Invincible:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.INVINCIBLE, Integer.valueOf(x)));
                    break;
                case Skills.SuperGM.Hide:
                    ret.duration = 2100000000;
                    ret.overTime = true;
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.GM_HIDE, Integer.valueOf(0)));
                    break;
                case Skills.Rogue.DarkSight:
                case Skills.NightWalker1.DarkSight:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKSIGHT, Integer.valueOf(x)));
                    break;
                case Skills.ChiefBandit.Pickpocket:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.PICKPOCKET, Integer.valueOf(x)));
                    break;
                case Skills.ChiefBandit.MesoGuard:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MESOGUARD, Integer.valueOf(x)));
                    break;
                case Skills.Hermit.MesoUp:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MESOUP, Integer.valueOf(x)));
                    break;
                case Skills.Hermit.ShadowPartner:
                case Skills.NightWalker3.ShadowPartner:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHADOWPARTNER, Integer.valueOf(x)));
                    break;
                case Skills.Hunter.SoulArrow:
                case Skills.CrossBowMan.SoulArrow:
                case Skills.WindArcher2.SoulArrow:
                case Skills.Priest.MysticDoor:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOULARROW, Integer.valueOf(x)));
                    break;
                case Skills.WhiteKnight.BlizzardChargeBW:
                case Skills.WhiteKnight.IceChargeSword:
                case Skills.WhiteKnight.FlameChargeBW:
                case Skills.WhiteKnight.FireChargeSword:
                case Skills.WhiteKnight.LightningChargeBW:
                case Skills.WhiteKnight.ThunderChargeSword:
                case Skills.Paladin.DivineChargeBW:
                case Skills.Paladin.HolyChargeSword:
                case Skills.ThunderBreaker2.LightningCharge:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.WK_CHARGE, Integer.valueOf(x)));
                    break;
                case Skills.Fighter.AxeBooster:
                case Skills.Fighter.SwordBooster:
                case Skills.Page.BWBooster:
                case Skills.Page.SwordBooster:
                case Skills.Spearman.PoleArmBooster:
                case Skills.Spearman.SpearBooster:
                case Skills.Hunter.BowBooster:
                case Skills.CrossBowMan.CrossbowBooster:
                case Skills.Assassin.ClawBooster:
                case Skills.Bandit.DaggerBooster:
                case Skills.FPMage.SpellBooster:
                case Skills.ILMage.SpellBooster:
                case Skills.Brawler.KnucklerBooster:
                case Skills.Gunslinger.GunBooster:
                case Skills.DawnWarrior2.SwordBooster:
                case Skills.BlazeWizard2.SpellBooster:
                case Skills.WindArcher2.BowBooster:
                case Skills.NightWalker2.ClawBooster:
                case Skills.ThunderBreaker2.KnuckleBooster:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BOOSTER, Integer.valueOf(x)));
                    break;
                case Skills.Fighter.PowerGuard:
                case Skills.Page.PowerGuard:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.POWERGUARD, Integer.valueOf(x)));
                    break;
                case Skills.Spearman.HyperBody:
                case Skills.SuperGM.HyperBody:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HYPERBODYHP, Integer.valueOf(x)));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HYPERBODYMP, Integer.valueOf(ret.y)));
                    break;
                case Skills.Beginner.Recovery:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.RECOVERY, Integer.valueOf(x)));
                    break;
                case Skills.Crusader.ComboAttack:
                case Skills.DawnWarrior3.ComboAttack:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, Integer.valueOf(1)));
                    break;
                case Skills.Beginner.MonsterRider:
                case Skills.Noblesse.MonsterRider:
                case Skills.Corsair.Battleship:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MONSTER_RIDING, Integer.valueOf(sourceid)));
                    break;
                case Skills.DragonKnight.DragonRoar:
                    ret.hpR = -x / 100.0;
                    break;
                case Skills.DragonKnight.DragonBlood:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DRAGONBLOOD, Integer.valueOf(ret.x)));
                    break;
                case Skills.Hero.MapleWarrior:
                case Skills.Paladin.MapleWarrior:
                case Skills.DarkKnight.MapleWarrior:
                case Skills.FPArchMage.MapleWarrior:
                case Skills.ILArchMage.MapleWarrior:
                case Skills.Bishop.MapleWarrior:
                case Skills.Bowmaster.MapleWarrior:
                case Skills.Marksman.MapleWarrior:
                case Skills.NightLord.MapleWarrior:
                case Skills.Shadower.MapleWarrior:
                case Skills.Buccaneer.MapleWarrior:
                case Skills.Corsair.MapleWarrior:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAPLE_WARRIOR, Integer.valueOf(ret.x)));
                    break;
                case Skills.Bowmaster.SharpEyes:
                case Skills.Marksman.SharpEyes:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHARP_EYES, Integer.valueOf(ret.x << 8 | ret.y)));
                    break;
                case Skills.Rogue.Disorder:
                    monsterStatus.put(MonsterStatus.WATK, Integer.valueOf(ret.x));
                    monsterStatus.put(MonsterStatus.WDEF, Integer.valueOf(ret.y));
                    break;
                case Skills.Page.Threaten:
                    monsterStatus.put(MonsterStatus.WATK, Integer.valueOf(ret.x));
                    monsterStatus.put(MonsterStatus.WDEF, Integer.valueOf(ret.y));
                    break;
                case Skills.Crusader.Shout:
                case Skills.Crusader.SwordComa:
                case Skills.Crusader.AxeComa:
                case Skills.WhiteKnight.ChargedBlow:
                case Skills.Hunter.ArrowBomb:
                case Skills.ChiefBandit.Assaulter:
                case Skills.Shadower.BoomerangStep:
                case Skills.Brawler.BackspinBlow:
                case Skills.Brawler.DoubleUppercut:
                case Skills.Buccaneer.Demolition:
                case Skills.Buccaneer.Snatch:
                case Skills.Buccaneer.Barrage:
                case Skills.Gunslinger.BlankShot:
                case Skills.DawnWarrior3.Coma:
                    monsterStatus.put(MonsterStatus.STUN, Integer.valueOf(1));
                    break;
                case Skills.ILMagician.ColdBeam:
                case Skills.ILMage.IceStrike:
                case Skills.ILMage.ElementComposition:
                case Skills.ILArchMage.Blizzard:
                case Skills.Sniper.Blizzard:
                case Skills.Outlaw.IceSplitter:
                    monsterStatus.put(MonsterStatus.FREEZE, Integer.valueOf(1));
                    ret.duration *= 2;
                    break;
                case Skills.FPMagician.Slow:
                case Skills.ILMagician.Slow:
                case Skills.BlazeWizard2.Slow:
                    monsterStatus.put(MonsterStatus.SPEED, Integer.valueOf(ret.x));
                    break;
                case Skills.FPMagician.PoisonBreath:
                case Skills.FPMage.ElementComposition:
                    monsterStatus.put(MonsterStatus.POISON, Integer.valueOf(1));
                    break;
                case Skills.Priest.Doom:
                    monsterStatus.put(MonsterStatus.DOOM, Integer.valueOf(1));
                    break;
                case Skills.Ranger.Puppet:
                case Skills.Sniper.Puppet:
                case Skills.WindArcher3.Puppet:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.PUPPET, Integer.valueOf(1)));
                    break;
                case Skills.Ranger.SilverHawk:
                case Skills.Sniper.GoldenEagle:
                    monsterStatus.put(MonsterStatus.STUN, Integer.valueOf(1));
                case Skills.FPArchMage.Elquines:
                case Skills.Marksman.Frostprey:
                    monsterStatus.put(MonsterStatus.FREEZE, Integer.valueOf(1));
                case Skills.ILArchMage.Ifrit:
                case Skills.Priest.SummonDragon:
                case Skills.Bishop.Bahamut:
                case Skills.Bowmaster.Phoenix:
                case Skills.Outlaw.Octopus:
                case Skills.Outlaw.Gaviota:
                case Skills.Corsair.WrathOfTheOctopi:
                case Skills.DawnWarrior1.Soul:
                case Skills.BlazeWizard1.Flame:
                case Skills.BlazeWizard3.Ifrit:
                case Skills.WindArcher1.Storm:
                case Skills.NightWalker1.Darkness:
                case Skills.ThunderBreaker1.Lightning:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SUMMON, Integer.valueOf(1)));
                    break;
                case Skills.Priest.HolySymbol:
                case Skills.SuperGM.HolySymbol:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOLY_SYMBOL, Integer.valueOf(x)));
                    break;
                case Skills.FPMage.Seal:
                case Skills.ILMage.Seal:
                case Skills.BlazeWizard3.Seal:
                    monsterStatus.put(MonsterStatus.SEAL, 1);
                    break;
                case Skills.Hermit.ShadowWeb:
                case Skills.NightWalker3.ShadowWeb:
                    monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                    break;
                case Skills.NightLord.ShadowStars:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHADOW_CLAW, Integer.valueOf(0)));
                    break;
                case Skills.FPArchMage.Infinity:
                case Skills.ILArchMage.Infinity:
                case Skills.Bishop.Infinity:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.INFINITY, Integer.valueOf(x)));
                    break;
                case Skills.Hero.PowerStance:
                case Skills.Paladin.PowerStance:
                case Skills.DarkKnight.PowerStance:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.STANCE, Integer.valueOf(iprop)));
                    break;
                case Skills.Beginner.EchoOfHero:
                case Skills.Noblesse.EchoOfHero:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ECHO_OF_HERO, Integer.valueOf(ret.x)));
                    break;
                case Skills.Bishop.HolyShield:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOLY_SHIELD, Integer.valueOf(x)));
                    break;
                case Skills.Bowmaster.Hamstring:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HAMSTRING, Integer.valueOf(x)));
                    monsterStatus.put(MonsterStatus.SPEED, x);
                    break;
                case Skills.Marksman.Blind:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.BLIND, Integer.valueOf(x)));
                    monsterStatus.put(MonsterStatus.ACC, x);
                    break;
                case Skills.Brawler.OakBarrel:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MORPH, Integer.valueOf(ret.morphId)));
                    break;
                case Skills.Marauder.Transformation:
                case Skills.Buccaneer.SuperTransformation:
                case Skills.WindArcher3.EagleEye:
                case Skills.ThunderBreaker3.Transformation:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MORPH, Integer.valueOf(ret.morphId)));
                    break;
                case Skills.Pirate.Dash:
                case Skills.ThunderBreaker1.Dash:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH, Integer.valueOf(x)));
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH2, Integer.valueOf(ret.y)));
                    break;
                case Skills.Buccaneer.SpeedInfusion:
                case Skills.ThunderBreaker3.SpeedInfusion:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SPEED_INFUSION, Integer.valueOf(x)));
                    break;
                case Skills.Marauder.EnergyCharge:
                case Skills.ThunderBreaker2.EnergyCharge:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ENERGY_CHARGE, Integer.valueOf(0)));
                case Skills.Outlaw.HomingBeacon:
                case Skills.Corsair.Bullseye:
                    statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.HOMING_BEACON, Integer.valueOf(0)));
                    break;
                default:
                // nothing needs to be added, that's ok
            }
        }
        if (ret.isMorph()) {
            statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MORPH, Integer.valueOf(ret.getMorph())));
        }
        ret.monsterStatus = monsterStatus;

        statups.trimToSize();
        ret.statups = statups;

        return ret;
    }

    /**
     * @param applyto
     * @param obj
     * @param attack damage done by the skill
     */
    public void applyPassive(MapleCharacter applyto, MapleMapObject obj, int attack) {
        if (makeChanceResult()) {
            switch (sourceid) {
                // MP eater
                case Skills.FPMagician.MPEater:
                case Skills.ILMagician.MPEater:
                case Skills.Cleric.MPEater:
                    if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                        return;
                    }
                    MapleMonster mob = (MapleMonster) obj;
                    // x is absorb percentage
                    if (!mob.isBoss()) {
                        int absorbMp = Math.min((int) (mob.getMaxMp() * (getX() / 100.0)), mob.getMp());
                        if (absorbMp > 0) {
                            mob.setMp(mob.getMp() - absorbMp);
                            applyto.addMP(absorbMp);
                            applyto.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(sourceid, 1));
                            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), sourceid, 1), false);
                        }
                    }
                    break;
            }
        }
    }

    public boolean applyTo(MapleCharacter chr) {
        return applyTo(chr, chr, true, null, 0);
    }

    public boolean applyTo(MapleCharacter chr, Point pos) {
        return applyTo(chr, chr, true, pos, 0);
    }

    public boolean applyTo(MapleCharacter chr, Point pos, int addedInfo) {
        return applyTo(chr, chr, true, pos, addedInfo);
    }

    private boolean applyTo(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, Point pos, int addedInfo) {
        int hpchange = calcHPChange(applyfrom, primary);
        int mpchange = calcMPChange(applyfrom, primary);

        if (primary) {
            if (itemConNo != 0) {
                MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemCon);
                MapleInventoryManipulator.removeById(applyto.getClient(), type, itemCon, itemConNo, false, true);
            }
        }
        List<Pair<MapleStat, Integer>> hpmpupdate = new ArrayList<Pair<MapleStat, Integer>>(2);
        if (!primary && isResurrection()) {
            hpchange = applyto.getMaxHp();
            applyto.setStance(4); // credits to me for finding the right standing up stance :P ()
            final MapleCharacter applytoo = applyto;
            TimerManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    applytoo.getMap().broadcastMessage(applytoo, MaplePacketCreator.removePlayerFromMap(applytoo.getObjectId()), false);
                    applytoo.getMap().broadcastMessage(applytoo, MaplePacketCreator.spawnPlayerMapobject(applytoo), false);
                    MapleCharacter mc = applytoo;
                    if (mc.getChalkboard() != null) {
                        mc.getMap().broadcastMessage(mc, (MaplePacketCreator.useChalkboard(mc, false)), false);
                    }
                } // credits destinyms for giving me the crappy inspiration to do it like this.
            }, 1000); // allow one second so the ressurection effect shows
        }
        if (isDispel() && makeChanceResult() && !isGmBuff()) {
            applyto.dispelDebuffs();
        }
        if (isDispel() && makeChanceResult() && isGmBuff()) {
            applyto.dispelAllDebuffs();
        }
        if (applyto.getMapId() != 910000001 || applyto.isJounin() || isGmBuff()) {
            if (hpchange != 0) {
                if (hpchange < 0 && (-hpchange) > applyto.getHp()) {
                    return false;
                }
                int newHp = applyto.getHp() + hpchange;
                if (newHp < 1) {
                    newHp = 1;
                }
                applyto.setHp(newHp);
                hpmpupdate.add(new Pair<MapleStat, Integer>(MapleStat.HP, Integer.valueOf(applyto.getHp())));
            }
        } else {
            applyto.dropMessage("You cannot pot in this Map");
        }
        if (mpchange != 0) {
            if (mpchange < 0 && (-mpchange) > applyto.getMp()) {
                return false;
            }
            applyto.setMp(applyto.getMp() + mpchange);
            hpmpupdate.add(new Pair<MapleStat, Integer>(MapleStat.MP, Integer.valueOf(applyto.getMp())));
        }
        applyto.getClient().getSession().write(MaplePacketCreator.updatePlayerStats(hpmpupdate, true));

        if (moveTo != -1) {
            MapleMap target;
            if (moveTo == 999999999) {
                target = applyto.getMap().getReturnMap();
            } else {
                target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                if (target.getId() / 10000000 != 21 && applyto.getMapId() / 10000000 != 20) {
                    if (target.getId() / 10000000 != applyto.getMapId() / 10000000) {
                        log.info("Player {} is trying to use a return scroll to an illegal location ({}->{})", new Object[]{applyto.getName(), applyto.getMapId(), target.getId()});
                        return false;
                    }
                }
            }
            applyto.changeMap(target, target.getPortal(0));
        }
        if (isShadowClaw()) {
            MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
            MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
            int projectile = 0;
            for (int i = 0; i < 255; i++) { // impose order...
                IItem item = use.getItem((byte) i);
                if (item != null) {
                    boolean isStar = mii.isThrowingStar(item.getItemId());
                    if (isStar && item.getQuantity() >= 200) {
                        projectile = item.getItemId();
                        break;
                    }
                }
            }
            if (projectile == 0) {
                return false;
            } else {
                MapleInventoryManipulator.removeById(applyto.getClient(), MapleInventoryType.USE, projectile, 200, false, true);
            }
        }
        if (overTime) {
            applyBuffEffect(applyfrom, applyto, primary, addedInfo);
        }
        if (primary && (overTime || isHeal())) {
            applyBuff(applyfrom, addedInfo);
        }
        if (primary && isMonsterBuff()) {
            applyMonsterBuff(applyfrom);
        }

        SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null && pos != null) {
            final MapleSummon tosummon = new MapleSummon(applyfrom, sourceid, pos, summonMovementType);
            if (!tosummon.isPuppet()) {
                applyfrom.getCheatTracker().resetSummonAttack();
            }
            applyfrom.getMap().spawnSummon(tosummon);
            applyfrom.getSummons().put(sourceid, tosummon);
            tosummon.addHP(x);
        }

        // Magic Door
        if (isMagicDoor()) {
            //applyto.cancelMagicDoor();
            Point doorPosition = new Point(applyto.getPosition());
            //doorPosition.y -= 280;
            MapleDoor door = new MapleDoor(applyto, doorPosition);
            applyto.getMap().spawnDoor(door);
            applyto.addDoor(door);
            door = new MapleDoor(door);
            applyto.addDoor(door);
            door.getTown().spawnDoor(door);
            if (applyto.getParty() != null) {
                // update town doors
                applyto.silentPartyUpdate();
            }
            applyto.disableDoor();
        } else if (isMist()) {
            Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
            MapleMist mist = new MapleMist(bounds, applyfrom, this);
            applyfrom.getMap().spawnMist(mist, getDuration(), sourceid == Skills.FPMage.PoisonMist, false);
        }
        return true;
    }

    private void applyBuff(MapleCharacter applyfrom, int addedInfo) {
        if (isPartyBuff() && (applyfrom.getParty() != null || isGmBuff())) {
            Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
            List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInBox(bounds, Arrays.asList(MapleMapObjectType.PLAYER));
            List<MapleCharacter> affectedp = new ArrayList<MapleCharacter>(affecteds.size());
            for (MapleMapObject affectedmo : affecteds) {
                MapleCharacter affected = (MapleCharacter) affectedmo;
                if (affected != applyfrom && (isGmBuff() || applyfrom.getParty().equals(affected.getParty()))) {
                    boolean isRessurection = isResurrection();
                    if ((isRessurection && !affected.isAlive()) || (!isRessurection && affected.isAlive())) {
                        affectedp.add(affected);
                    }
                }
            }
            for (MapleCharacter affected : affectedp) {
                // TODO actually heal (and others) shouldn't recalculate everything
                // for heal this is an actual bug since heal hp is decreased with the number
                // of affected players
                applyTo(applyfrom, affected, false, null, addedInfo);
                affected.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(sourceid, 2));
                affected.getMap().broadcastMessage(affected, MaplePacketCreator.showBuffeffect(affected.getId(), sourceid, 2), false);
            }
        }
    }

    private void applyMonsterBuff(MapleCharacter applyfrom) {
        Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInBox(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        ISkill skill_ = SkillFactory.getSkill(sourceid);
        int i = 0;
        for (MapleMapObject mo : affected) {
            MapleMonster monster = (MapleMonster) mo;
            if (makeChanceResult()) {
                monster.applyStatus(applyfrom, new MonsterStatusEffect(getMonsterStati(), skill_, false), isPoison(), getDuration());
            }
            i++;
            if (i >= mobCount) {
                break;
            }
        }
    }

    private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        Point mylt;
        Point myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        return bounds;
    }

    public void silentApplyBuff(MapleCharacter chr, long starttime) {
        int localDuration = duration;
        localDuration = alchemistModifyVal(chr, localDuration, false);
        CancelEffectAction cancelAction = new CancelEffectAction(chr, this, starttime);
        ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, ((starttime + localDuration) - System.currentTimeMillis()));
        chr.registerEffect(this, starttime, schedule);

        SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            final MapleSummon tosummon = new MapleSummon(chr, sourceid, chr.getPosition(), summonMovementType);
            if (!tosummon.isPuppet()) {
                chr.getCheatTracker().resetSummonAttack();
                chr.getSummons().put(sourceid, tosummon);
                tosummon.addHP(x);
            }
        }
    }

    private void applyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, int addedInfo) {
        applyto.cancelEffect(this, true, -1);
        List<Pair<MapleBuffStat, Integer>> localstatups = statups;
        int localDuration = duration;
        if (isMonsterRiding() || isBattleship()) {
            int mountId = 0;
            IItem mount = applyfrom.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
            if (mount != null) {
                mountId = mount.getItemId();
            }
            if (isBattleship()) {
                mountId = 1932000;
            }
            applyto.getClient().getSession().write(MaplePacketCreator.giveMountBuff(sourceid, mountId));
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showMountBuff(applyto.getId(), sourceid, mountId), false);
            long starttime = System.currentTimeMillis();
            CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
            ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, localDuration);
            applyto.registerEffect(this, starttime, schedule);
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), sourceid, 1), false);
            return;
        }
        if (isSkillMorph()) {
            localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MORPH, getMorph(applyto)));
        }
        if (primary) {
            localDuration = alchemistModifyVal(applyfrom, localDuration, false);
        }
        if (localstatups.size() > 0) {
            MaplePacket buff = MaplePacketCreator.giveBuff((skill ? sourceid : -sourceid), localDuration, localstatups);
            if (isDash()) {
                buff = MaplePacketCreator.givePirateBuff(sourceid, localDuration / 1000, localstatups);
            }
            if (isEnergyCharge()) {
                localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ENERGY_CHARGE, 10000));
                buff = MaplePacketCreator.givePirateBuff(sourceid, localDuration / 1000, localstatups);
            }
            if (isSpeedInfusion()) {
                buff = MaplePacketCreator.giveSpeedInfusion(sourceid, localDuration / 1000, localstatups, addedInfo);
            }
            applyto.getClient().getSession().write(buff);
        } else {
           // log.warn(MapleClient.getLogMessage(applyto, "Applying empty statups (skill {}, id {})", skill, sourceid));
        }
        if (isDash() || isEnergyCharge()) {
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showPirateBuff(applyto.getId(), sourceid, localDuration, localstatups), false);
        }
        if (isSpeedInfusion()) {
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showSpeedInfusion(applyto.getId(), sourceid, localDuration, localstatups), false);
        }
        if (isDs()) {
            List<Pair<MapleBuffStat, Integer>> dsstat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DARKSIGHT, 0));
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), dsstat, false), false);
        }
        if (isCombo()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.COMBO, 1));
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, false), false);
        }
        if (isShadowPartner()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SHADOWPARTNER, 0));
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, false), false);
        }
        if (isSoulArrow()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.SOULARROW, 0));
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, false), false);
        }
        if (isEnrage()) {
            applyto.handleOrbconsume();
        }
        if (isMorph()) {
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MORPH, Integer.valueOf(getMorph(applyto))));
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, true), false);
        }
        if (localstatups.size() > 0) {
            long starttime = System.currentTimeMillis();
            CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
            ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, localDuration);
            applyto.registerEffect(this, starttime, schedule);
        }

        if (primary) {
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), sourceid, 1, (byte) 3, isMorph()), false);
        }
    }

    private int calcHPChange(MapleCharacter applyfrom, boolean primary) {
        int hpchange = 0;
        if (hp != 0) {
            if (!skill) {
                if (primary) {
                    hpchange += alchemistModifyVal(applyfrom, hp, true);
                } else {
                    hpchange += hp;
                }
            } else { // assumption: this is heal
                hpchange += makeHealHP(hp / 100.0, applyfrom.getTotalMagic(), 3, 5);
            }
        }
        if (hpR != 0) {
            hpchange += (int) (applyfrom.getCurrentMaxHp() * hpR);
        }
        // actually receivers probably never get any hp when it's not heal but whatever
        if (primary) {
            if (hpCon != 0) {
                hpchange -= hpCon;
            }
        }
        if (isChakra()) {
            hpchange += makeHealHP(getY() / 100.0, applyfrom.getTotalLuk(), 2.3, 3.5);
        }
        return hpchange;
    }

    private int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        int maxHeal = (int) (stat * upperfactor * rate);
        int minHeal = (int) (stat * lowerfactor * rate);
        return (int) ((Math.random() * (maxHeal - minHeal + 1)) + minHeal);
    }

    private int calcMPChange(MapleCharacter applyfrom, boolean primary) {
        int mpchange = 0;
        if (mp != 0) {
            if (primary) {
                mpchange += alchemistModifyVal(applyfrom, mp, true);
            } else {
                mpchange += mp;
            }
        }
        if (mpR != 0) {
            mpchange += (int) (applyfrom.getCurrentMaxMp() * mpR);
        }
        if (primary) {
            if (mpCon != 0) {
                double mod = 1.0;
                boolean isAFpMage = applyfrom.getJob().isA(MapleJob.FP_MAGE);
                if (isAFpMage || applyfrom.getJob().isA(MapleJob.IL_MAGE)) {
                    ISkill amp;
                    if (isAFpMage) {
                        amp = SkillFactory.getSkill(Skills.FPMage.ElementAmplification);
                    } else {
                        amp = SkillFactory.getSkill(Skills.ILMage.ElementAmplification);
                    }
                    int ampLevel = applyfrom.getSkillLevel(amp);
                    if (ampLevel > 0) {
                        MapleStatEffect ampStat = amp.getEffect(ampLevel);
                        mod = ampStat.getX() / 100.0;
                    }
                }
                mpchange -= mpCon * mod;
                if (applyfrom.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                    mpchange = 0;
                }
            }
        }
        return mpchange;
    }

    private int alchemistModifyVal(MapleCharacter chr, int val, boolean withX) {
        if (!skill && (chr.getJob().isA(MapleJob.HERMIT) || chr.getJob().isA(MapleJob.NIGHTWALKER3))) {
            MapleStatEffect alchemistEffect = getAlchemistEffect(chr, chr.getJob().isA(MapleJob.NIGHTWALKER3));
            if (alchemistEffect != null) {
                return (int) (val * ((withX ? alchemistEffect.getX() : alchemistEffect.getY()) / 100.0));
            }
        }
        return val;
    }

    private MapleStatEffect getAlchemistEffect(MapleCharacter chr, boolean cygnus) {
        ISkill alchemist = SkillFactory.getSkill(cygnus == true ? Skills.NightWalker3.Alchemist : Skills.Hermit.Alchemist);
        int alchemistLevel = chr.getSkillLevel(alchemist);
        if (alchemistLevel == 0) {
            return null;
        }
        return alchemist.getEffect(alchemistLevel);
    }

    private boolean isGmBuff() {
        switch (sourceid) {
            case Skills.Beginner.EchoOfHero:
            case Skills.Noblesse.EchoOfHero:
            case Skills.SuperGM.Bless:
            case Skills.SuperGM.Haste:
            case Skills.SuperGM.HealDispel:
            case Skills.SuperGM.HolySymbol:
            case Skills.SuperGM.HyperBody:
            case Skills.SuperGM.Resurrection:
                return true;
            default:
                return false;
        }
    }

    private boolean isMonsterBuff() {
        if (!skill) {
            return false;
        }
        switch (sourceid) {
            case Skills.Page.Threaten:
            case Skills.FPMagician.Slow:
            case Skills.ILMagician.Slow:
            case Skills.FPMage.Seal:
            case Skills.ILMage.Seal:
            case Skills.Priest.Doom:
            case Skills.Hermit.ShadowWeb:
            case Skills.NightWalker3.ShadowWeb:
                return true;
            default:
                return false;
        }
    }

    private boolean isPartyBuff() {
        if (lt == null || rb == null) {
            return false;
        }
        switch (sourceid) {
            case Skills.WhiteKnight.BlizzardChargeBW:
            case Skills.WhiteKnight.IceChargeSword:
            case Skills.WhiteKnight.FlameChargeBW:
            case Skills.WhiteKnight.FireChargeSword:
            case Skills.WhiteKnight.LightningChargeBW:
            case Skills.WhiteKnight.ThunderChargeSword:
            case Skills.Paladin.DivineChargeBW:
            case Skills.Paladin.HolyChargeSword:
            case Skills.DawnWarrior3.SoulCharge:
                return false;
        }
        return true;
    }

    public boolean isHeal() {
        return skill && (sourceid == Skills.Cleric.Heal || sourceid == Skills.SuperGM.HealDispel || sourceid == Skills.SuperGM.Resurrection);
    }

    public boolean isResurrection() {
        return skill && (sourceid == Skills.SuperGM.Resurrection || sourceid == Skills.Bishop.Resurrection || sourceid == Skills.SuperGM.HealDispel);
    }

    public boolean isDash() {
        return skill && (sourceid == Skills.Pirate.Dash || sourceid == Skills.ThunderBreaker1.Dash);
    }

    public boolean isEnergyCharge() {
        return skill && (sourceid == Skills.Marauder.EnergyCharge || sourceid == Skills.ThunderBreaker2.EnergyCharge);
    }

    public boolean isSpeedInfusion() {
        return skill && (sourceid == Skills.Buccaneer.SpeedInfusion || sourceid == Skills.ThunderBreaker3.SpeedInfusion);
    }

    public boolean isBattleship() {
        return skill && (sourceid == Skills.Corsair.Battleship);
    }

    public boolean isHide() {
        return skill && sourceid == Skills.SuperGM.Hide;
    }

    public boolean isDragonBlood() {
        return skill && sourceid == Skills.DragonKnight.DragonBlood;
    }

    private boolean isDs() {
        return skill && (sourceid == Skills.Rogue.DarkSight || sourceid == Skills.NightWalker1.DarkSight);
    }

    private boolean isCombo() {
        return skill && sourceid == Skills.Crusader.ComboAttack;
    }

    private boolean isEnrage() {
        return skill && sourceid == Skills.Hero.Enrage;
    }

    private boolean isShadowPartner() {
        return skill && (sourceid == Skills.Hermit.ShadowPartner || sourceid == Skills.NightWalker3.ShadowPartner);
    }

    private boolean isChakra() {
        return skill && sourceid == Skills.ChiefBandit.Chakra;
    }

    public boolean isMonsterRiding() {
        return skill && (sourceid == Skills.Beginner.MonsterRider || sourceid == Skills.Noblesse.MonsterRider);
    }

    public boolean isMagicDoor() {
        return skill && sourceid == Skills.Priest.MysticDoor;
    }

    public boolean isMesoGuard() {
        return skill && sourceid == Skills.ChiefBandit.MesoGuard;
    }

    public boolean isCharge() {
        if (skill) {
            switch (sourceid) {
                case Skills.WhiteKnight.BlizzardChargeBW:
                case Skills.WhiteKnight.IceChargeSword:
                case Skills.WhiteKnight.FlameChargeBW:
                case Skills.WhiteKnight.FireChargeSword:
                case Skills.WhiteKnight.LightningChargeBW:
                case Skills.WhiteKnight.ThunderChargeSword:
                case Skills.Paladin.DivineChargeBW:
                case Skills.Paladin.HolyChargeSword:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public boolean isPoison() {
        return skill && (sourceid == Skills.FPMagician.PoisonBreath || sourceid == Skills.FPMage.ElementComposition || sourceid == Skills.FPMage.PoisonMist);
    }

    private boolean isMist() {
        return skill && (sourceid == Skills.FPMage.PoisonMist || sourceid == Skills.Shadower.Smokescreen); // poison mist and smokescreen
    }

    private boolean isSoulArrow() {
        return skill && (sourceid == Skills.Hunter.SoulArrow || sourceid == Skills.CrossBowMan.SoulArrow || sourceid == Skills.WindArcher2.SoulArrow); // bow and crossbow
    }

    private boolean isShadowClaw() {
        return skill && sourceid == Skills.NightLord.ShadowStars;
    }

    private boolean isDispel() {
        return skill && (sourceid == Skills.Priest.Dispel || sourceid == Skills.SuperGM.HealDispel);
    }

    private boolean isSkillMorph() {
        return skill && (sourceid == Skills.Marauder.Transformation || sourceid == Skills.Buccaneer.SuperTransformation || sourceid == Skills.ThunderBreaker3.Transformation || sourceid == Skills.WindArcher3.EagleEye);
    }

    public boolean isMorph() {
        return morphId > 0;
    }

    public short getWatk() {
        return watk;
    }

    public short getMatk() {
        return matk;
    }

    public short getWdef() {
        return wdef;
    }

    public short getMdef() {
        return mdef;
    }

    public short getAcc() {
        return acc;
    }

    public short getAvoid() {
        return avoid;
    }

    public short getHands() {
        return hands;
    }

    public short getSpeed() {
        return speed;
    }

    public short getJump() {
        return jump;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isOverTime() {
        return overTime;
    }

    public List<Pair<MapleBuffStat, Integer>> getStatups() {
        return statups;
    }

    public boolean sameSource(MapleStatEffect effect) {
        return this.sourceid == effect.sourceid && this.skill == effect.skill;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDamage() {
        return damage;
    }

    public int getAttackCount() {
        return attackCount;
    }

    public int getBulletCount() {
        return bulletCount;
    }

    public int getBulletConsume() {
        return bulletConsume;
    }

    public int getMoneyCon() {
        return moneyCon;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getFixedDamage() {
        return fixDamage;
    }

    public Map<MonsterStatus, Integer> getMonsterStati() {
        return monsterStatus;
    }

    public int getMorph(MapleCharacter chr) {
        return skill ? (morphId + (chr.getGender() * 100)) : morphId;
    }

    public SummonMovementType getSummonMovementType() {
        if (!skill) {
            return null;
        }
        switch (sourceid) {
            case Skills.Ranger.Puppet:
            case Skills.Sniper.Puppet:
            case Skills.Outlaw.Octopus:
            case Skills.Corsair.WrathOfTheOctopi:
            case Skills.WindArcher3.Puppet:
                return SummonMovementType.STATIONARY;
            case Skills.Priest.SummonDragon:
            case Skills.Ranger.SilverHawk:
            case Skills.Sniper.GoldenEagle:
            case Skills.Bowmaster.Phoenix:
            case Skills.Marksman.Frostprey:
            case Skills.Outlaw.Gaviota:
                return SummonMovementType.CIRCLE_FOLLOW;
            case Skills.FPArchMage.Elquines:
            case Skills.ILArchMage.Ifrit:
            case Skills.Bishop.Bahamut:
            case Skills.DawnWarrior1.Soul:
            case Skills.BlazeWizard1.Flame:
            case Skills.BlazeWizard3.Ifrit:
            case Skills.WindArcher1.Storm:
            case Skills.NightWalker1.Darkness:
            case Skills.ThunderBreaker1.Lightning:
                return SummonMovementType.FOLLOW;
        }
        return null;
    }

    public boolean isSkill() {
        return skill;
    }

    public int getSourceId() {
        return sourceid;
    }

    /**
     *
     * @return true if the effect should happen based on it's probablity, false otherwise
     */
    public boolean makeChanceResult() {
        return prop == 1.0 || Math.random() < prop;
    }

    public static class CancelEffectAction implements Runnable {

        private MapleStatEffect effect;
        private WeakReference<MapleCharacter> target;
        private long startTime;

        public CancelEffectAction(MapleCharacter target, MapleStatEffect effect, long startTime) {
            this.effect = effect;
            this.target = new WeakReference<MapleCharacter>(target);
            this.startTime = startTime;
        }

        @Override
        public void run() {
            MapleCharacter realTarget = target.get();
            if (realTarget != null) {
                realTarget.cancelEffect(effect, false, startTime);
            }
        }
    }

    public int getMorph() {
        return morphId;
    }
}
