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
package net.sf.odinms.server.life;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.odinms.client.Enums.MapleDisease;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleMist;

/**
 *
 * @author Danny (Leifde)
 */
public class MobSkill {

    private int skillId;
    private int skillLevel;
    private int mpCon;
    private List<Integer> toSummon = new ArrayList<Integer>();
    private int spawnEffect;
    private int hp;
    private int x;
    private int y;
    private long duration;
    private long cooltime;
    private float prop;
    private Point lt, rb;
    private int limit;

    public MobSkill(int skillId, int level) {
        this.skillId = skillId;
        this.skillLevel = level;
    }

    public void setMpCon(int mpCon) {
        this.mpCon = mpCon;
    }

    public void addSummons(List<Integer> toSummon) {
        for (Integer summon : toSummon) {
            this.toSummon.add(summon);
        }
    }

    public void setSpawnEffect(int spawnEffect) {
        this.spawnEffect = spawnEffect;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCoolTime(long cooltime) {
        this.cooltime = cooltime;
    }

    public void setProp(float prop) {
        this.prop = prop;
    }

    public void setLtRb(Point lt, Point rb) {
        this.lt = lt;
        this.rb = rb;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void applyEffect(MapleCharacter player, MapleMonster monster, boolean skill) {
        MonsterStatus monStat = null;
        MapleDisease disease = null;
        boolean heal = false;
        boolean dispel = false;
        switch (skillId) {
            case 100:
            case 110:
                monStat = MonsterStatus.WEAPON_ATTACK_UP;
                break;
            case 101:
            case 111:
                monStat = MonsterStatus.MAGIC_ATTACK_UP;
                break;
            case 102:
            case 112:
                monStat = MonsterStatus.WEAPON_DEFENSE_UP;
                break;
            case 103:
            case 113:
                monStat = MonsterStatus.MAGIC_DEFENSE_UP;
                break;
            case 114: // Heal
                heal = true;
                break;
            case 120:
                disease = MapleDisease.SEAL;
                break;
            case 121:
                disease = MapleDisease.DARKNESS;
                break;
            case 122:
                disease = MapleDisease.WEAKEN;
                break;
            case 123:
                disease = MapleDisease.STUN;
                break;
            case 124: // Curse

                break;
            case 125:
                disease = MapleDisease.POISON;
                break;
            case 126: // Slow
                disease = MapleDisease.SLOW;
                break;
            case 127:
                dispel = true;
                break;
            case 128: // Seduce
                disease = MapleDisease.SEDUCE;
                break;
            case 129: // Banish?
                // TODO

                break;
            case 131: // Mist
                Rectangle bounds = calculateBoundingBox(monster.getPosition(), true);
                MapleMist mist = new MapleMist(bounds, monster, this);
                monster.getMap().spawnMist(mist, x * 10, false, false);
                break;
            case 132:
                disease = MapleDisease.CRAZY_SKULL;
                break;
            case 133:
                disease = MapleDisease.ZOMBIFY;
                break;
            case 140:
                monStat = MonsterStatus.WEAPON_IMMUNITY;
                break;
            case 141:
                monStat = MonsterStatus.MAGIC_IMMUNITY;
                break;
            case 200:
                for (Integer mobId : getSummons()) {
                    MapleMonster toSpawn = MapleLifeFactory.getMonster(mobId);
                    monster.getMap().spawnMonsterWithEffect(toSpawn, getSpawnEffect(), monster.getPosition());
                }
                break;
        }

        if (monStat != null || heal) {
            if (lt != null && rb != null && skill) {
                List<MapleMapObject> objects = getObjectsInRange(monster, MapleMapObjectType.MONSTER);
                if (heal) {
                    for (MapleMapObject mons : objects) {
                        ((MapleMonster) mons).heal(getX(), getY());
                    }
                } else {
                    for (MapleMapObject mons : objects) {
                        ((MapleMonster) mons).applyMonsterBuff(monStat, getX(), getSkillId(), getDuration(), this);
                    }
                }
            } else {
                if (heal) {
                    monster.heal(getX(), getY());
                } else {
                    monster.applyMonsterBuff(monStat, getX(), getSkillId(), getDuration(), this);
                }
            }
        }

        if (disease != null || dispel) {
            if (lt != null && rb != null && skill) {
                List<MapleMapObject> objects = getObjectsInRange(monster, MapleMapObjectType.PLAYER);
                if (dispel) {
                    for (MapleMapObject chr : objects) {
                        ((MapleCharacter) chr).dispel();
                    }
                } else {
                    for (MapleMapObject chr : objects) {
                        ((MapleCharacter) chr).giveDebuff(disease, this);
                    }
                }
            } else {
                if (dispel) {
                    player.dispel();
                } else {
                    player.giveDebuff(disease, this);
                }
            }
        }

        monster.usedSkill(skillId, skillLevel, cooltime);
        monster.setMp(monster.getMp() - getMpCon());
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getMpCon() {
        return mpCon;
    }

    public List<Integer> getSummons() {
        return Collections.unmodifiableList(toSummon);
    }

    public int getSpawnEffect() {
        return spawnEffect;
    }

    public int getHP() {
        return hp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getDuration() {
        return duration;
    }

    public long getCoolTime() {
        return cooltime;
    }

    public Point getLt() {
        return lt;
    }

    public Point getRb() {
        return rb;
    }

    public int getLimit() {
        return limit;
    }

    public boolean makeChanceResult() {
        return prop == 1.0 || Math.random() < prop;
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

    private List<MapleMapObject> getObjectsInRange(MapleMonster monster, MapleMapObjectType objectType) {
        Rectangle bounds = calculateBoundingBox(monster.getPosition(), monster.isFacingLeft());
        List<MapleMapObjectType> objectTypes = new ArrayList<MapleMapObjectType>();
        objectTypes.add(objectType);
        return monster.getMap().getMapObjectsInBox(bounds, objectTypes);
    }
}
