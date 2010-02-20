/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS.IRCStuff;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.odinms.client.NinjaMS.IRCStuff.Commands.IRCCommandProcessor;
import net.sf.odinms.client.NinjaMS.Processors.SmegaProcessor;
import net.sf.odinms.server.TimerManager;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

/**
 *
 * @author Owner
 */
public class MainIRC extends PircBot {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    private String channel = "#ninjahelp";
    private String channel1 = "#ninjas";
    private String channel2 = "#ninjastaff";
    private static MainIRC instance = new MainIRC();
    private Map<String, Long> lastlogs = new LinkedHashMap<String, Long>();

    public static MainIRC getInstance() {
        return instance;
    }

    public MainIRC() {
        try {
            this.setName("ninjabot");
            this.setAutoNickChange(true);
            this.connect("irc.vbirc.com");
            this.joinChannel(channel);
            this.joinChannel(channel1);
            this.joinChannel(channel2);
            this.identify("{Janet143<3}");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (IrcException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        super.onAction(sender, login, hostname, target, action);
    }


    public User getUser(String sender){
        User[] onlineusers = getUsers(channel);
        User senderObj = null;
        boolean isOp = false;
        boolean isHop = false;
        for (int i = 0; i < onlineusers.length; i++) {
            User xxx = onlineusers[i];
            if(xxx.getNick().equalsIgnoreCase(sender)){
                senderObj = xxx;
            }
        }
        return senderObj;
    }

    public int getPower(String nub){
        User noob = getUser(nub);
        int x = 0;
        if(noob.getPrefix().indexOf('~') >= 0){
            x = 5;
        } else if(noob.getPrefix().indexOf('&') >= 0){
            x = 4;
        } else if(noob.getPrefix().indexOf('@') >= 0){
            x = 3;
        }else if(noob.getPrefix().indexOf('%') >= 0){
            x = 2;
        } else if(noob.getPrefix().indexOf('+') >= 0){
            x = 1;
        }
        return x;
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (!sender.equalsIgnoreCase(this.getNick()) && !sender.contains("[G]")) {
            if (IRCCommandProcessor.processCommand(message, sender, getPower(sender))) {
                return;
            } else if (message.contains("#") && getPower(sender) >= 1) {
                SmegaProcessor.broadcastIRCSmega(sender, message);
            }
        }
    }

    public void sendIrcMessage(String Message) {
        this.sendMessage(channel, Message);
    }
    public void sendGlobalMessage(String Message) {
        this.sendMessage(channel, Message);
        this.sendMessage(channel1, Message);
        this.sendMessage(channel2, Message);
    }

    @Override
    public void onKick(String channel, String kickerNick, String kickerLogin,
            String kickerHostname, String recipientNick, String reason) {
        //Auto Rejoin if Kicked
        if (recipientNick.equalsIgnoreCase(getNick())) {
            joinChannel(channel);
        }
    }

    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        super.onJoin(channel, sender, login, hostname);
    }

    @Override
    public void onDisconnect() {
        //reconnect
        this.reconnectIRC();
    }


    public void reconnectIRC(){
        if(!isConnected()){
            try {
                reconnect();
            } catch (Exception e){
                e.printStackTrace();
                TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    reconnectIRC();
                }
            }, 5000);
            }
        }
    }
}
