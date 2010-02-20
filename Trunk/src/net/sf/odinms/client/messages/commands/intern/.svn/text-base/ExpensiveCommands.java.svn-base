/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.intern;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.maps.MapleMap;

/**
 *
 * @author Admin
 */
public class ExpensiveCommands implements InternCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("reloadmap")) {
            if (splitted.length < 2) {
                mc.dropMessage("If you don't know how to use it. You should't be using it.");
                return;
            }
            int mapid = Integer.parseInt(splitted[1]);
            MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
            map.deleteAndReloadMap();
        } else if (splitted[0].equalsIgnoreCase("reloaddropspawn")) {
            try {
                TimerManager.getInstance().stop();
            } catch (Exception e) {
                mc.dropMessage("Error : " + e);
                e.printStackTrace();
            } finally {
                try {
                    TimerManager tMan = TimerManager.getInstance();
                    tMan.start();
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.restarttimers();
                    }
                    mc.dropMessage("Success");
                } catch (Exception e) {
                    mc.dropMessage("Error : " + e);
                    e.printStackTrace();
                }
            }
        } else if (splitted[0].equalsIgnoreCase("resetreactors")){
            c.getPlayer().getMap().resetReactors();
        }
    }

    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[]{
                    new InternCommandDefinition("reloadmap", "mapid", "deletes and reloads map"),
                    new InternCommandDefinition("reloaddropspawn", "", "ONLY USE IF THERE IS NO DROP AND RESPAWN"),
                    new InternCommandDefinition("resetReactors", "", "Resets reacots. Don't use it if you don't know what it is"),
        };
    }
}
