/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.Sannin;

import net.sf.odinms.client.Clones;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.SanninCommand;
import net.sf.odinms.client.messages.SanninCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;

/**
 *
 * @author Sannin
 */
public class CloneCommands implements SanninCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("setclonelimit")) {
            MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            int number = Integer.parseInt(splitted[2]);
            if (noob == null) {
                mc.dropMessage("player does not exist or not in your channel");
                return;
            }
            noob.setCloneLimit(number);
            noob.saveToDB();
            mc.dropMessage("haxxed his clone limit");
        } else if (splitted[0].equalsIgnoreCase("kagebunshin")) {
            if (c.getPlayer().hasClones()) {
                c.getPlayer().removeClones();
            }
            for (int i = 0; i < 69; i++) {
                Clones clone = new Clones(c.getPlayer(), ((c.getPlayer().getId() * 100) + c.getPlayer().getClones().size() + 1));
                c.getPlayer().addClone(clone);
            }
            mc.dropMessage("Please move around for it to take effect. Clone Limit for you is : " + c.getPlayer().getAllowedClones());
        } else if (splitted[0].equalsIgnoreCase("fkagebunshin")) {
            MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            int numbers = Integer.parseInt(splitted[2]);
            if (noob == null) {
                mc.dropMessage("player does not exist or not in your channel");
                return;
            }
            if (noob.hasClones()) {
                noob.removeClones();
            }
            if (numbers > 69) {
                numbers = 10;
            }
            for (int i = 0; i < numbers; i++) {
                Clones clone = new Clones(noob, ((noob.getId() * 100) + noob.getClones().size() + 1));
                noob.addClone(clone);
            }
            mc.dropMessage("You have forced clone spawn on the nub");
        }
    }

    public SanninCommandDefinition[] getDefinition() {
        return new SanninCommandDefinition[]{
                    new SanninCommandDefinition("setclonelimit", "ign number", "sets clone limits"),
                    new SanninCommandDefinition("kagebunshin", "number", "adds clones"),
                    new SanninCommandDefinition("fkagebunshin", "ign number", "adds clone to victim"),};
    }
}
