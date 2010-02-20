/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.messages.commands.intern;

import java.rmi.RemoteException;
import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.world.remote.CheaterData;
import net.sf.odinms.server.constants.Skills;

/**
 *
 * @author Owner
 */
public class CheaterHuntingCommands implements InternCommand{

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        MapleCharacter pl = c.getPlayer();
        if (splitted[0].equals("whosthere")) {
            MessageCallback callback = new ServernoticeMapleClientMessageCallback(c);
            StringBuilder builder = new StringBuilder("Players on Map: ");
            for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
                if (pl.getGMLevel() >= chr.getGMLevel()) {
                    if (builder.length() > 150) { // wild guess :o
                        builder.setLength(builder.length() - 2);
                        callback.dropMessage(builder.toString());
                        builder = new StringBuilder();
                    }
                    builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                    builder.append(", ");
                }
            }
            builder.setLength(builder.length() - 2);
            mc.dropMessage(builder.toString());
        } else if (splitted[0].equals("cheaters")) {
            try {
                List<CheaterData> cheaters = c.getChannelServer().getWorldInterface().getCheaters();
                for (int x = cheaters.size() - 1; x >= 0; x--) {
                    CheaterData cheater = cheaters.get(x);
                    mc.dropMessage(cheater.getInfo());
                    mc.dropMessage("This doesnt do shit");
                }
            } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
        } else if (splitted[0].equalsIgnoreCase("dehide")) {
            if (splitted.length < 2) {
                mc.dropMessage("Syntax: $dehide ign");
            } else {
                MapleCharacter noob = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (noob != null) {
                    if (pl.getGMLevel() >= noob.getGMLevel()) {
                        noob.deHide();
                        SkillFactory.getSkill(Skills.Rogue.DarkSight).getEffect(20).applyTo(noob);
                        mc.dropMessage("done player position : " + noob.getMapId() + "; x: " + noob.getPosition().x + "; y: " + noob.getPosition().y);
                    } else {
                        mc.dropMessage("The ninja you are trying to dehide is too elite");
                    }
                }
            }
        }

    }

    @Override
    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[]{
                    new InternCommandDefinition("whosthere", "", ""),
                    new InternCommandDefinition("cheaters", "", ""),
                    new InternCommandDefinition("dehide", "ign", " stalk lar"),};
    }
}
