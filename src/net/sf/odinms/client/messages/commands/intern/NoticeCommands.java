/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.intern;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.NoticeProcessor;
import net.sf.odinms.client.NinjaMS.Processors.NubHelpProcessor;
import net.sf.odinms.client.messages.InternCommand;
import net.sf.odinms.client.messages.InternCommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.tools.StringUtil;

/**
 *
 * @author Admin
 */
public class NoticeCommands implements InternCommand {
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        if (splitted[0].equalsIgnoreCase("say")) {
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendSay(c.getPlayer(), msg);
        } else if (splitted[0].equalsIgnoreCase("me")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendMe(c.getPlayer(), msg);
        } else if (splitted[0].equalsIgnoreCase("notice")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendBlueNoticeWithNotice(msg);
        } else if (splitted[0].equalsIgnoreCase("blue")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendBlueNotice(msg);
        } else if (splitted[0].equalsIgnoreCase("pink")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendPinkNotice(msg);
        } else if (splitted[0].equalsIgnoreCase("pop")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendPopup(msg);
        } else if (splitted[0].equalsIgnoreCase("yellow")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendYellowNotice(msg);
        } else if (splitted[0].equalsIgnoreCase("white")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendWhiteNotice(name, msg);
        } else if (splitted[0].equalsIgnoreCase("cblue")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendCBlueNotice(c, msg);
        } else if (splitted[0].equalsIgnoreCase("cpink")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendCPinkNotice(c, msg);
        } else if (splitted[0].equalsIgnoreCase("cpop")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendCPopup(c, msg);
        } else if (splitted[0].equalsIgnoreCase("cyellow")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendCYellowNotice(c, msg);
        } else if (splitted[0].equalsIgnoreCase("cwhite")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendCWhiteNotice(c, name, msg);
        } else if (splitted[0].equalsIgnoreCase("mblue")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendMBlueNotice(c, msg);
        } else if (splitted[0].equalsIgnoreCase("mpink")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendMPinkNotice(c, msg);
        } else if (splitted[0].equalsIgnoreCase("mpop")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendMPopup(c, msg);
        } else if (splitted[0].equalsIgnoreCase("myellow")){
            String msg = StringUtil.joinStringFrom(splitted, 1);
            NoticeProcessor.sendMYellowNotice(c, msg);
        } else if (splitted[0].equalsIgnoreCase("mwhite")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendMWhiteNotice(c, name, msg);
        } else if (splitted[0].equalsIgnoreCase("pblue")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendPBlueNotice(c.getPlayer(), name, msg);
        } else if (splitted[0].equalsIgnoreCase("ppink")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendPPinkNotice(c.getPlayer(), name, msg);
        } else if (splitted[0].equalsIgnoreCase("ppop")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendPPopup(c.getPlayer(), name, msg);
        } else if (splitted[0].equalsIgnoreCase("pyellow")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendPYellowNotice(c.getPlayer(), name, msg);
        } else if (splitted[0].equalsIgnoreCase("pwhite")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendPWhiteNotice(c.getPlayer(), name, msg);
        } else if (splitted[0].equalsIgnoreCase("saymc")){
            NoticeProcessor.sayMindControl(c.getPlayer(), splitted);
        } else if (splitted[0].equalsIgnoreCase("memc")) {
             NoticeProcessor.meMindControl(c.getPlayer(), splitted);
        } else if (splitted[0].equalsIgnoreCase("custom")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendBlueNotice("["+ name + "] "+msg);
        } else if (splitted[0].equalsIgnoreCase("custompink")){
            String name = splitted[1];
            String msg = StringUtil.joinStringFrom(splitted, 2);
            NoticeProcessor.sendPinkNotice("["+ name + "] "+msg);
        } else if (splitted[0].equalsIgnoreCase("faq")){                      
            if(splitted.length < 3){
                NubHelpProcessor.getInstance().chooseFAQ(c.getPlayer(), splitted[1]);
            } else {
                NubHelpProcessor.getInstance().chooseFAQ(c.getPlayer(), splitted[2], splitted[1]);
            }
        }
    }

    public InternCommandDefinition[] getDefinition() {
        return new InternCommandDefinition[]{
                    new InternCommandDefinition("say", "message", "Sends notice with tag"),
                    new InternCommandDefinition("me", "message", "Sends notice with tag"),
                    new InternCommandDefinition("notice", "message", "Sends blue notice with \"notice\" as prefix"),
                    new InternCommandDefinition("blue", "message", "Sends blue notice with no prefix"),
                    new InternCommandDefinition("pink", "message", "Sends pink notice with no prefix"),
                    new InternCommandDefinition("popup", "message", "Sends popup notice with no prefix"),
                    new InternCommandDefinition("yellow", "message", "Sends yellow notice with no prefix"),
                    new InternCommandDefinition("white", "name, message", "Sends white notice with the name you put"),
                    new InternCommandDefinition("cblue", "message", "Sends blue notice with no prefix to your channel"),
                    new InternCommandDefinition("cpink", "message", "Sends pink notice with no prefix to your channel"),
                    new InternCommandDefinition("cpopup", "message", "Sends popup notice with no prefix to your channel"),
                    new InternCommandDefinition("cyellow", "message", "Sends yellow notice with no prefix to your channel"),
                    new InternCommandDefinition("cwhite", "name, message", "Sends white notice with the name you put to your channel"),
                    new InternCommandDefinition("mblue", "message", "Sends blue notice with no prefix to your map"),
                    new InternCommandDefinition("mpink", "message", "Sends pink notice with no prefix to your map"),
                    new InternCommandDefinition("mpopup", "message", "Sends popup notice with no prefix to your map"),
                    new InternCommandDefinition("myellow", "message", "Sends yellow notice with no prefix to your map"),
                    new InternCommandDefinition("mwhite", "name, message", "Sends white notice with the name you put to your map"),
                    new InternCommandDefinition("pblue", "name, message", "Sends blue notice with no prefix to the person"),
                    new InternCommandDefinition("ppink", "name, message", "Sends blue notice with no prefix to the person"),
                    new InternCommandDefinition("ppopup", "name, message", "Sends blue notice with no prefix to the person"),
                    new InternCommandDefinition("pyellow", "name message", "Sends yellow notice with no prefix to the person"),
                    new InternCommandDefinition("pwhite", "name, message", "Sends blue notice with the name you put to the person"),
                    new InternCommandDefinition("saymc", "name, message", " !say mind control"),
                    new InternCommandDefinition("memc", "nme, message", " !me mind control"),
                    new InternCommandDefinition("custom", "name, message", "blue notice with custom prefix(prefix cannot have space)"),
                    new InternCommandDefinition("custompink", "name, message", "pink notice with custom prefix(prefix cannot have space)"),
                    new InternCommandDefinition("faq", "<tao,rasengan,kagebunshin,koc,training,meso,kpq,shiken,status,morph,chairs,joko,ninja,tensu,medal,jq,donation,gmsmode,modes> [IGN (optional)]"," shows FAQ to the person or sends notice"),

        };

    }
}
