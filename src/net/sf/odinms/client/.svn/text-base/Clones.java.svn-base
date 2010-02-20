/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client;

import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.client.Inventory.IItem;
import net.sf.odinms.client.Enums.MapleBuffStat;
import net.sf.odinms.net.world.guild.MapleGuildSummary;

/**
 *
 * @author Admin
 */
public class Clones{

    private MapleCharacter owner;
    private MapleGuildSummary gs;
    private MapleCharacter fake;

    public Clones(MapleCharacter player, int id) {
        owner = player;
        MapleCharacter fakechr = MapleCharacter.getDefault(player.getClient(), id + 1000000);
        fakechr.setHair(player.getHair());
        fakechr.setFace(player.getFace());
        fakechr.setSkinColor(player.getSkinColor());
        fakechr.setName(player.getName());
        fakechr.setID(id + 1000000);
        fakechr.setLevel(player.getLevel() + 1);
        fakechr.setJob(player.getJob());
        fakechr.setMap(player.getMap());
        fakechr.setPosition(player.getPosition());
        fakechr.setStance(player.getStance());   // Oliver Says this looks better :p
        if (player.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
            SkillFactory.getSkill(1004).getEffect(1).applyTo(fakechr); // does this even work. 100% . oo the morph? not sure.
            // morph works anyway
        }
        for (IItem equip : player.getInventory(MapleInventoryType.EQUIPPED)) {
            fakechr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip);
        }
        fakechr.isfake = true;
        player.getMap().addClone(fakechr);
        fake = fakechr;
    }

    public MapleGuildSummary getGuildS() {
        return this.gs;
    }

    public MapleCharacter getClone() {
        return fake;
    }

    public MapleCharacter getOwner() {
        return this.owner;
    }
}
