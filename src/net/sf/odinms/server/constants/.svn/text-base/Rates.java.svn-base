/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server.constants;

import net.sf.odinms.client.Enums.Clans;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.Inventory.MapleInventoryType;
import net.sf.odinms.net.world.WorldServer;

/**
 *
 * @author Owner
 */
public class Rates {  
        
    public static int getExpRate(MapleCharacter noob){
        if(noob.getGMSMode() > 0){
            return 0;
        }
        double expRate = noob.getClan().getExpRate();
        if(Modes.getInstance(noob).hasKyubi()){
            if(noob.getClan() != Clans.NARUTO) {
                expRate *= 2;
            } else {
                expRate *= 1.3;
            }
        }
        expRate += (noob.getExpBoost() * expRate / 100);
        if(noob.isGenin()){
            expRate += (expRate * 0.25);
        } 
        if(WorldServer.getInstance().isDoubleRate()){
            expRate *= 2;
        }
        if(noob.hasRasengan()){
            expRate *= 0.70;
        }
        double reduce = Math.min((noob.getMaxStatItems() * 100), noob.getReborns());
        reduce = Math.min(reduce, (expRate /2));
        expRate -= reduce;
        return (int) Math.floor(expRate);
    }

    public static int getMesoRate(MapleCharacter noob){
        double mesoRate = noob.getClan().getMesoRate();
        if(Modes.getInstance(noob).hasSage()){
           if(noob.getClan() != Clans.NARUTO) {
                mesoRate *= 2;
            } else {
                mesoRate *= 1.3;
            }
        }
        mesoRate += (noob.getMesoBoost() * mesoRate / 100);
        if(noob.isGenin()){            
            mesoRate += (mesoRate * 0.25);
        }

        if(WorldServer.getInstance().isDoubleRate()){
            mesoRate *= 2;
        }
        if(noob.getInventory(MapleInventoryType.EQUIPPED).findById(1812006) != null){
            mesoRate *= 0.75;
        }
        double reduce = Math.min((noob.getMaxStatItems() * 100), noob.getReborns());
        reduce = Math.min(reduce, (mesoRate /2));
        mesoRate -= reduce;
        return (int) Math.floor(mesoRate);
    }

    public static int getDropRate(MapleCharacter noob){
        double dropRate = noob.getClan().getDropRate();
        if(Modes.getInstance(noob).hasHachibi()){
            if(noob.getClan() != Clans.NARUTO) {
                dropRate *= 2;
            } else {
                dropRate *= 1.3;
            }
        }
        dropRate += (noob.getDropBoost() * dropRate / 100);
        if(noob.isGenin()){
            dropRate += (dropRate * 0.25);
        }
        if(WorldServer.getInstance().isDoubleRate()){
            dropRate *= 2;
        }
        double reduce = Math.min((noob.getMaxStatItems()), (noob.getReborns()/100));
        reduce = Math.min(reduce, (dropRate /2));
        dropRate -= reduce;
        return (int) Math.floor(dropRate);
    }

    public static int getBossDropRate(MapleCharacter noob){
        double bdropRate = noob.getClan().getBossRate();
        if(Modes.getInstance(noob).hasShakaku()){
            if(noob.getClan() != Clans.NARUTO) {
                bdropRate *= 2;
            } else {
                bdropRate *= 1.3;
            }
        }
        bdropRate += (noob.getBossDropBoost() * bdropRate / 100);
        if(noob.isGenin()){
            bdropRate += (bdropRate * 0.25);
        }
        if(WorldServer.getInstance().isDoubleRate()){
            bdropRate *= 2;
        }
        if(noob.hasRasengan()){
            bdropRate *= 0.80;
        }
        if(noob.getInventory(MapleInventoryType.EQUIPPED).findById(1812006) != null){
            bdropRate *= 0.80;
        }
        double reduce = Math.min((noob.getMaxStatItems()), (noob.getReborns()/100));
        reduce = Math.min(reduce, (bdropRate /2));
        bdropRate -= reduce;
        return (int) Math.floor(bdropRate);
    }
}
