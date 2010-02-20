/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.donator;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.DonatorCommand;
import net.sf.odinms.client.messages.DonatorCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class DonatorCommands implements DonatorCommand {

    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("setlegend")) {
            if (splitted.length != 2) {
                mc.dropMessage("Learn to read #commands. Syntax: #setlegend <newlegend> . Note: the legend cannot have Spaces :)");
                return;
            }
            String legend = splitted[1];            
            c.getPlayer().setLegend(legend);
        } else if (splitted[0].equalsIgnoreCase("changejob")) {
            MapleCharacter player = c.getPlayer();
            if (player.getCheatTracker().spam(60000, 1)) {
                mc.dropMessage("not so often bitch ;P");
                return;
            }
            if (player.getMapId() != 100000000) {
                mc.dropMessage("only in henesys babe");
                return;
            }
            if (player.getPath() == 1) {
                if (splitted.length < 2) {
                    mc.dropMessage("Syntax : #changejob <job name> [hero, paladin, dk, bishop, fire, ice, bow, xbow, nl, dit, gun, brawler]");
                } else if (splitted.length == 2) {
                    String[] jobs = {"hero", "paladin", "dk", "bishop", "fire", "ice", "bow", "xbow", "nl", "dit", "gun", "brawler"};
                    int[] jobid = {112, 122, 132, 232, 212, 222, 312, 322, 412, 422, 522, 512};
                    for (int i = 0; i < jobid.length; i++) {
                        if (splitted[1].equalsIgnoreCase(jobs[i])) {
                            player.changeJobById(jobid[i]);
                        }
                    }
                }
            } else {
                if (splitted.length < 2) {
                    mc.dropMessage("Syntax : #changejob <job name> [dw, bw, wa, nw, tb]");
                } else if (splitted.length == 2) {
                    String[] jobs = {"dw", "bw","wa", "nw", "tb"};
                    int[] jobid = {1111, 1211, 1311, 1411, 1511};
                    for (int i = 0; i < jobid.length; i++) {
                        if (splitted[1].equalsIgnoreCase(jobs[i])) {
                            player.changeJobById(jobid[i]);
                        }
                    }
                }
            }
        } else if (splitted[0].equalsIgnoreCase("callgm")) {
            if(!c.getPlayer().getCheatTracker().spam(60000, 8)){
                StringBuilder sb = new StringBuilder();
                sb.append("Player ");
                sb.append(c.getPlayer().getName());
                sb.append(" needs your assistance! His problem is: ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                WorldChannelInterface wci = c.getChannelServer().getWorldInterface();
                try {
                    wci.broadcastGMMessage(null, MaplePacketCreator.serverNotice(5, sb.toString()).getBytes());
                } catch (Exception ex) {
                    mc.dropMessage("Failed to call gm... " + ex + "");
                }
                mc.dropMessage("You have called a Head Farmer for assistance regarding: " + StringUtil.joinStringFrom(splitted, 1));
            } else {
                mc.dropMessage("You have to wait at least 60 seconds before using this command again!");
            }
        }
    }

    public DonatorCommandDefinition[] getDefinition() {
        return new DonatorCommandDefinition[]{
                    new DonatorCommandDefinition("setlegend", "<new legend>", "changes your legend"),
                    new DonatorCommandDefinition("changejob", "<jobname>", "changes your job"),
                    new DonatorCommandDefinition("callgm", "<message>", "sends a GM message. Dont use it unless necessary")
                    
        };

    }
}
