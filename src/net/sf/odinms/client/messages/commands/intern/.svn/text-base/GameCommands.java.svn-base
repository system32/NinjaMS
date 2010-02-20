/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.intern;

import java.util.Arrays;
import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.server.constants.*;

/**
 *
 * @author Admin
 */
public class GameCommands implements InternCommand {

    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[]{
                    new InternCommandDefinition("cleardrops", "", "clears all drops in ground"),
                    new InternCommandDefinition("hide", "", "hides you"),
                    new InternCommandDefinition("unstuckspecial", "<ign>", "unstuck the noob"),
                    new InternCommandDefinition("hideoff","","turns hide off temporarily until you cc/ relog")
        };
    }

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("cleardrops")) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            List<MapleMapObject> items = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.ITEM));
            for (MapleMapObject itemmo : items) {
                map.removeMapObject(itemmo);
                map.broadcastMessage(MaplePacketCreator.removeItemFromMap(itemmo.getObjectId(), 0, c.getPlayer().getId()));
            }
            mc.dropMessage("You have destroyed " + items.size() + " items on the ground.");
        } else if (splitted[0].equalsIgnoreCase("hide")){
            if(Modes.getInstance(c.getPlayer()).hasModeOn() && !SpecialStuff.getInstance().isJQMap(c.getPlayer().getMapId())){
                SkillFactory.getSkill(Skills.SuperGM.Hide).getEffect(1).applyTo(c.getPlayer());
            }
        } else if (splitted[0].equalsIgnoreCase("hideoff")){
            c.getPlayer().cancelAllBuffs();
            c.getPlayer().setNoHide(true);
        } else if (splitted[0].equalsIgnoreCase("unstuckspecial")) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                MapleCharacter vic = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
                if (vic != null) {
                    if(vic.getEventInstance() != null){
                        vic.getEventInstance().dispose();
                    }
                    cserv.removePlayer(vic);
                    vic.getClient().disconnect();
                }
            }

        }

    }
}
