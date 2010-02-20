/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.messages.commands.players;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.NinjaMS.Processors.SearchProcessor;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.messages.PlayerCommand;
import net.sf.odinms.client.messages.PlayerCommandDefinition;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.server.constants.Items;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Owner
 */
public class ConvinienceCommands implements PlayerCommand {

    public void execute(final MapleClient c, final MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equalsIgnoreCase("avatarblock")) {
            if (player.haveSight(5)) {
                if (splitted[1].equalsIgnoreCase("on")) {
                    mc.dropMessage("You will not see Avatar Smegas");
                    player.gainItem(Items.currencyType.Sight, -5);
                    player.setAsmega();
                } else {
                    mc.dropMessage("You should now be able to see Avatar Smegas");
                    player.gainItem(Items.currencyType.Sight, -5);
                    player.setAllMega();
                }
            } else {
                mc.dropMessage("You need atleast 5 Tao of Sight to do this.");
            }
        } else if (splitted[0].equalsIgnoreCase("smegablock")) {
            if (player.haveSight(5)) {
                if (splitted[1].equalsIgnoreCase("on")) {
                    mc.dropMessage("You will not see Pink & yellow Smegas");
                    player.gainItem(Items.currencyType.Sight, -5);
                    player.setPsmega();
                } else {
                    mc.dropMessage("You should now be able to see pink and Tellow Smegas");
                    player.gainItem(Items.currencyType.Sight, -5);
                    player.setAsmega();
                }
            } else {
                mc.dropMessage("You need atleast 5 Tao of Sight to do this.");
            }
        } else if (splitted[0].equalsIgnoreCase("allsmegablock")) {
            if (player.haveSight(5)) {
                if (splitted[1].equalsIgnoreCase("on")) {
                    mc.dropMessage("You will not see Pink & yellow and Avatar Smegas");
                    player.gainItem(Items.currencyType.Sight, -5);
                    player.setPsmega();
                } else {
                    mc.dropMessage("You should now be able to see pink, Yellow and Avatar Smegas");
                    player.gainItem(Items.currencyType.Sight, -5);
                    player.setAllMega();
                }
            } else {
                mc.dropMessage("You need atleast 5 Tao of Sight to do this.");
            }
        } else if (splitted[0].equalsIgnoreCase("whodrops")) {
            if (splitted.length < 2) {
                mc.dropMessage("@whodrops <itemid>");
            } else {
                int itemid = Integer.parseInt(splitted[1]);
                List<String> retMobs = SearchProcessor.whoDrops(itemid);
                if (retMobs != null && retMobs.size() > 1) {
                    mc.dropMessage("---------------------List---------------------");
                    for (String singleRetMob : retMobs) {
                        c.showMessage(5, singleRetMob);
                    }
                    mc.dropMessage("'Per drop' is determined by your drop rate. A monster can drop a maximum of your drop rate items each time. Each drop is individually calculated by this chance. Same principle applies to boss drop.");
                } else {
                    mc.dropMessage("No mobs drop this item. ( Item id : " + itemid+" )");
                }
            }
        }
    }

    public PlayerCommandDefinition[] getDefinition() {
        return new PlayerCommandDefinition[]{
                    new PlayerCommandDefinition("avatarblock", "on/off", "Turn on and off avatar mega"),
                    new PlayerCommandDefinition("smegablock", "on/off", "Turn on and off pink and yellow mega"),
                    new PlayerCommandDefinition("allsmegablock", "on/off", "Turn on and off all Smega"),
                    new PlayerCommandDefinition("whodrops", "itemid", "shows who drops a certain item in our server"),};
    }
}
