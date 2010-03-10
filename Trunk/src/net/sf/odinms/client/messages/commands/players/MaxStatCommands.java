/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Admin
 */
public class MaxStatCommands implements PlayerCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        mc.dropMessage("Talk to NPC Angy in Henesys to get MSI");
    }




    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("shuriken", "", "Gives you Max stat item"),
                };
    }
     /*private boolean checkEtcItems(MapleCharacter player, byte msicount) {
    switch(msicount){
    case 0:
    return true;
    case 1:
    case 2:
    return (player.haveItem(4000020, 500)) && player.haveItem(4000069, 750); // Wild Boar Tooth and Zombie tooth
    case 3:
    case 4:
    return (player.haveItem(4000002, 750)) && player.haveItem(4000168, 1500); // Pig's Ribbon and sun flower Seed
    case 5:
    case 6:
    return (player.haveItem(4000001, 1000)) && player.haveItem(4000078, 1500); // orange mushy cap and jr cerebres tooth
    case 7:
    return (player.haveItem(4000051, 1500)) && player.haveItem(4000184, 2500); // Hector Tails and Butter Toasted Squid
    case 8:

    return (player.haveItem(4000040, 50)) && player.haveItem(4000176, 50) &&
    player.haveItem(4000074, 2500) && player.haveItem(4000014, 3000); // mushroom spore, poisonous mushroom,
    // lucida tail & drake skull.
    default:
    return false;
    }
    }

    private void removeItems(MapleCharacter player, byte x) {
    switch(x){
    case 0:
    break;
    case 1:
    case 2:
    player.gainItem(4000020, -500);
    player.gainItem(4000069, -750); // Wild Boar Tooth and Zombie tooth
    break;
    case 3:
    case 4:
    player.gainItem(4000002, -750);
    player.gainItem(4000168, -1500); // Pig's Ribbon and sun flower Seed
    break;
    case 5:
    case 6:
    player.gainItem(4000001, -1000);
    player.gainItem(4000078, -1500); // orange mushy cap and jr cerebres tooth
    break;
    case 7:
    player.gainItem(4000051, -1500);
    player.gainItem(4000184, -2500); // Hector Tails and Butter Toasted Squid
    break;
    case 8:
    player.gainItem(4000040, -50);// mushroom spore
    player.gainItem(4000176, -50);// poisonous mushroom,
    player.gainItem(4000074, -2500);// lucida tail
    player.gainItem(4000014, -3000); //drake skull.
    break;

    default:
    break;
    }
    }

    private String require (byte x){
    switch(x){
    case 0:
    return "Some thing wrong. Report to a GM";
    case 1:
    case 2:
    return "You need to have 500 Wild Boar tooth and 750 Zombie Tooth";
    case 3:
    case 4:
    return "You need to have 750 Pig Ribbon and 1500 Sun Flower Seed.";
    case 5:
    case 6:
    return "You need to have 1000 Orange mushroom caps and 1500 Jr CereBres Tooth";
    case 7:
    return "You need to have 1500 Hector Tails and 2500 Butter Toasted Squids";
    case 8:
    return "You need to have 50 Mushmom Spore, 50 Poisonous mushroom, 2500 lucida Tail, 3000 Drake Skull";
    default:
    return "Some thing wrong. Report to a GM";
    }
    }*/
}
