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

import java.util.concurrent.ScheduledFuture;
import net.sf.odinms.client.Clones;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.Enums.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacter.CancelCooldownAction;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.Inventory.MapleInventory;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Enums.MapleJob;
import net.sf.odinms.client.Inventory.MapleWeaponType;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.server.constants.Skills;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class RangedAttackHandler extends AbstractDealDamageHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        AttackInfo attack = parseRanged(c.getPlayer(), slea);

        MapleCharacter player = c.getPlayer();
        if (c.getPlayer().isJounin()) {
            c.getPlayer().heal();
        }

        MapleInventory equip = player.getInventory(MapleInventoryType.EQUIPPED);
        IItem weapon = equip.getItem((byte) -11);
        MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
        MapleWeaponType type = mii.getWeaponType(weapon.getItemId());
        if (type == MapleWeaponType.NOT_A_WEAPON) {
            throw new RuntimeException("[h4x] Player " + player.getName() + " is attacking with something that's not a weapon");
        }
        if (!c.getPlayer().canUseSkill(attack.skill)) {
            c.showMessage(5, "It appears you are unable to use this skill!");
            return;
        }
        boolean soulBlade = attack.skill == Skills.DawnWarrior2.SoulBlade;
        boolean sharkWave = attack.skill == Skills.ThunderBreaker3.SharkWave;
        if (soulBlade || sharkWave) {
            player.getMap().broadcastMessage(player, MaplePacketCreator.rangedAttack(player, attack, 0), false, true);
            for (Clones clone : c.getPlayer().getClones()) {
                c.getPlayer().getMap().broadcastMessage(clone.getClone(), MaplePacketCreator.rangedAttack(clone.getClone(), attack, 0), false, true);
            }
            ISkill skill = SkillFactory.getSkill(attack.skill);
            int skillLevel = c.getPlayer().getSkillLevel(skill);
            MapleStatEffect effect_ = skill.getEffect(skillLevel);
            if (effect_.getCooldown() > 0) {
                c.getSession().write(MaplePacketCreator.skillCooldown(attack.skill, effect_.getCooldown()));
                ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(c.getPlayer(), attack.skill), effect_.getCooldown() * 1000);
                c.getPlayer().addCooldown(attack.skill, System.currentTimeMillis(), effect_.getCooldown() * 1000, timer);
            }
            int maxdamage = c.getPlayer().getCurrentMaxBaseDamage();
            MapleStatEffect effect = attack.getAttackEffect(player);
            if (effect != null) {
                maxdamage *= effect.getDamage() / 100.0;
            }
            Integer comboBuff = player.getBuffedValue(MapleBuffStat.COMBO);
            if (attack.numAttacked > 0 && comboBuff != null) {
                player.handleOrbGain();
            }
            applyAttack(attack, player, maxdamage, 1);
            return;
        }

        MapleInventory use = player.getInventory(MapleInventoryType.USE);
        int projectile = 0;
        int bulletCount = 1;
        MapleStatEffect effect = null;
        if (attack.skill != 0) {
            effect = attack.getAttackEffect(player);
            bulletCount = effect.getBulletCount();
            if (effect.getCooldown() > 0) {
                c.getSession().write(MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
            }
        }

        boolean hasShadowPartner = player.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null;
        int damageBulletCount = bulletCount;
        if (hasShadowPartner) {
            bulletCount *= 2;
        }
        for (int i = 0; i < 255; i++) { // impose order...
            IItem item = use.getItem((byte) i);
            if (item != null) {
                boolean clawCondition = type == MapleWeaponType.CLAW && mii.isThrowingStar(item.getItemId()) && weapon.getItemId() != 1472063;
                boolean bowCondition = type == MapleWeaponType.BOW && mii.isArrowForBow(item.getItemId());
                boolean crossbowCondition = type == MapleWeaponType.CROSSBOW && mii.isArrowForCrossBow(item.getItemId());
                boolean gunCondition = type == MapleWeaponType.GUN && mii.isBullet(item.getItemId());
                boolean mittenCondition = weapon.getItemId() == 1472063 && (mii.isArrowForBow(item.getItemId()) || mii.isArrowForCrossBow(item.getItemId()));
                if ((clawCondition || bowCondition || crossbowCondition || mittenCondition || gunCondition) && item.getQuantity() >= bulletCount) {
                    projectile = item.getItemId();
                    break;
                }
            }
        }
        boolean soulArrow = player.getBuffedValue(MapleBuffStat.SOULARROW) != null;
        boolean shadowClaw = player.getBuffedValue(MapleBuffStat.SHADOW_CLAW) != null;
        if (!soulArrow && !shadowClaw) {
            int bulletConsume = bulletCount;
            if (effect != null && effect.getBulletConsume() != 0) {
                bulletConsume = effect.getBulletConsume() * (hasShadowPartner ? 2 : 1);
            }
            MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile, bulletConsume, false, true);
        }
        if (projectile != 0 || soulArrow) {
            int visProjectile = projectile;
            if (mii.isThrowingStar(projectile)) {
                MapleInventory cash = player.getInventory(MapleInventoryType.CASH);
                for (int i = 0; i < 255; i++) {
                    IItem item = cash.getItem((byte) i);
                    if (item != null) {
                        if (item.getItemId() / 1000 == 5021) {
                            visProjectile = item.getItemId();
                            break;
                        }
                    }
                }
            } else {
                if (soulArrow || attack.skill == Skills.Ranger.ArrowRain || attack.skill == Skills.Sniper.ArrowEruption) {
                    visProjectile = 0;
                }
            }

            player.getMap().broadcastMessage(player, MaplePacketCreator.rangedAttack(player, attack, visProjectile), false, true);

            int basedamage;
            int projectileWatk = 0;
            if (projectile != 0) {
                projectileWatk = mii.getWatkForProjectile(projectile);
            }
            if (attack.skill != Skills.Rogue.LuckySeven) { // not lucky 7
                if (projectileWatk != 0) {
                    basedamage = c.getPlayer().calculateMaxBaseDamage(c.getPlayer().getTotalWatk() + projectileWatk);
                } else {
                    basedamage = c.getPlayer().getCurrentMaxBaseDamage();
                }
            } else { // l7 has a different formula :>
                basedamage = (int) (((c.getPlayer().getTotalLuk() * 5.0) / 100.0) * (c.getPlayer().getTotalWatk() + projectileWatk));
            }
            if (attack.skill == Skills.Hunter.ArrowBomb) { //arrowbomb is hardcore like that �.o
                basedamage *= effect.getX() / 100.0;
            }
            int maxdamage = basedamage;
            double critdamagerate = 0.0;
            if (player.getJob().isA(MapleJob.ASSASSIN)) {
                ISkill criticalthrow = SkillFactory.getSkill(Skills.Assassin.CriticalThrow);
                int critlevel = player.getSkillLevel(criticalthrow);
                if (critlevel > 0) {
                    critdamagerate = (criticalthrow.getEffect(critlevel).getDamage() / 100.0);
                }
            } else if (player.getJob().isA(MapleJob.BOWMAN)) {
                ISkill criticalshot = SkillFactory.getSkill(Skills.Archer.CriticalShot);
                int critlevel = player.getSkillLevel(criticalshot);
                if (critlevel > 0) {
                    critdamagerate = (criticalshot.getEffect(critlevel).getDamage() / 100.0) - 1.0;
                }
            } else if (player.getJob().isA(MapleJob.NIGHTWALKER1)) {
                ISkill criticalthrow = SkillFactory.getSkill(Skills.NightWalker2.CriticalThrow);
                int critlevel = player.getSkillLevel(criticalthrow);
                if (critlevel > 0) {
                    critdamagerate = (criticalthrow.getEffect(critlevel).getDamage() / 100.0);
                }
            } else if (player.getJob().isA(MapleJob.WINDARCHER1)) {
                ISkill criticalshot = SkillFactory.getSkill(Skills.WindArcher1.CriticalShot);
                int critlevel = player.getSkillLevel(criticalshot);
                if (critlevel > 0) {
                    critdamagerate = (criticalshot.getEffect(critlevel).getDamage() / 100.0) - 1.0;
                }
            } else if (player.getJob().isA(MapleJob.THUNDERBREAKER3)) {
                ISkill criticalpunch = SkillFactory.getSkill(Skills.ThunderBreaker3.CriticalPunch);
                int critlevel = player.getSkillLevel(criticalpunch);
                if (critlevel > 0) {
                    critdamagerate = (criticalpunch.getEffect(critlevel).getDamage() / 100.0) - 1.0;
                }
            }
            int critdamage = (int) (basedamage * critdamagerate);
            if (effect != null) {
                maxdamage *= effect.getDamage() / 100.0;
            }
            maxdamage += critdamage;
            maxdamage *= damageBulletCount;
            if (hasShadowPartner) {
                ISkill shadowPartner = SkillFactory.getSkill(Skills.Hermit.ShadowPartner);
                int shadowPartnerLevel = player.getSkillLevel(shadowPartner);
                if (shadowPartnerLevel == 0) {
                    shadowPartner = SkillFactory.getSkill(Skills.NightWalker3.ShadowPartner);
                    shadowPartnerLevel = player.getSkillLevel(shadowPartner);
                }
                MapleStatEffect shadowPartnerEffect = shadowPartner.getEffect(shadowPartnerLevel);
                if (attack.skill != 0) {
                    maxdamage *= (1.0 + shadowPartnerEffect.getY() / 100.0);
                } else {
                    maxdamage *= (1.0 + shadowPartnerEffect.getX() / 100.0);
                }
            }

            if (attack.skill == Skills.Hermit.ShadowMeso) {
                maxdamage = 35000;
            }

            if (effect != null) {
                int money = effect.getMoneyCon();
                if (money != 0) {
                    double moneyMod = money * 0.5;
                    money = (int) (money + Math.random() * moneyMod);
                    if (money > player.getMeso()) {
                        money = player.getMeso();
                    }
                    player.gainMeso(-money, false);
                }
            }

            if (attack.skill != 0) {
                ISkill skill = SkillFactory.getSkill(attack.skill);
                int skillLevel = c.getPlayer().getSkillLevel(skill);
                MapleStatEffect effect_ = skill.getEffect(skillLevel);
                if (effect_.getCooldown() > 0) {
                    c.getSession().write(MaplePacketCreator.skillCooldown(attack.skill, effect_.getCooldown()));
                    ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(c.getPlayer(), attack.skill), effect_.getCooldown() * 1000);
                    c.getPlayer().addCooldown(attack.skill, System.currentTimeMillis(), effect_.getCooldown() * 1000, timer);
                }
            }
            applyAttack(attack, player, maxdamage, bulletCount);
        }
    }
}
