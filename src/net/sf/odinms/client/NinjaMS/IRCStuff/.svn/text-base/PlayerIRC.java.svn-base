/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.IRCStuff;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.tools.MaplePacketCreator;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 *
 * @author Owner
 */
public class PlayerIRC extends PircBot {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    private String channel = "#ninjas";
    private MapleCharacter player;
    private static Map<String, PlayerIRC> instances = new HashMap<String, PlayerIRC>();

    public static PlayerIRC getInstance(MapleCharacter player) {
        if (!instances.containsKey(player.getName())) {
            instances.put(player.getName(), new PlayerIRC(player));
        }
        return instances.get(player.getName());
    }

    public static boolean hasInstance(MapleCharacter player) {
        return instances.containsKey(player.getName());
    }

    public static void cancelInstance(MapleCharacter player) {
        if (instances.containsKey(player.getName())) {
             try {
                getInstance(player).disconnect();
            } catch (Exception e) {
            }
            player.setIrcmsg(false);
            instances.remove(player.getName());
        }
    }

    public PlayerIRC(MapleCharacter player) {
        try {
            this.player = player;
            this.setName(player.getName() + "[G]");
            this.setVerbose(false);
            this.setAutoNickChange(true);
            this.connect("irc.vbirc.com");
            this.joinChannel(channel);
            this.checkIRCBot();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (IrcException ex) {
            ex.printStackTrace();
        }
    }

    public void checkIRCBot() {
        TimerManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (player == null || !hasInstance(player)) {
                    disconnect();
                } else {
                    checkIRCBot();
                }
            }
        }, 5 * 60 * 3000); // every 5 minutes
    }

    @Override
    public void onDisconnect() {
        player.getClient().getSession().write(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.ITEMMEGAPHONE, 69, "IRC Chat : ~~~~~~~~You have been disconnected from IRC~~~~~~~~", null, false));
    }

    @Override
    protected void onConnect() {
        player.getClient().getSession().write(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.ITEMMEGAPHONE, 69, "IRC Chat : ~~~~~~~~You have successfully entered the IRC and joining #chat~~~~~~~~", null, false));
    }

    @Override
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        player.getClient().getSession().write(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.ITEMMEGAPHONE, 69, "IRC Chat :  '" + oldNick + "' has changed his/her nickname on IRC to '" + newNick + "'.", null, false));
    }

    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        player.getClient().getSession().write(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.ITEMMEGAPHONE, 69, "IRC Chat :  '" + kickerNick + "' has kicked '" + recipientNick + "' out of the " + channel + "!", null, false));
        if (recipientNick.equalsIgnoreCase("[G]" + player.getName())) {
            disconnect();
        }
    }

    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        player.getClient().getSession().write(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.ITEMMEGAPHONE, 69, "IRC Chat :  " + sender + " has entered " + channel + "", null, false));
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        player.getClient().getSession().write(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.MEGAPHONE, 69, "IRC Chat :  <" + sender + " via. PM> " + message, null, false));

    }

    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        player.getClient().getSession().write(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.ITEMMEGAPHONE, 69, "IRC Chat :  " + sender + " " + action, null, false));
    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        player.getClient().getSession().write(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.ITEMMEGAPHONE, 69, "IRC Chat :  <" + sourceNick + "> " + notice, null, false));
    }

    public void sendIRCMessage(String message) {
        player.getClient().getSession().write(MaplePacketCreator.getMegaphone(Items.MegaPhoneType.SUPERMEGAPHONE, 69, "#chat <" + player.getName() + "[G]> : " + message, null, false));
        this.sendMessage(channel, message);
    }
}
