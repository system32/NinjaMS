/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.client.messages;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.commands.donator.HelpDonatorCommand;
import net.sf.odinms.client.messages.commands.admins.HelpAdminCommand;
import net.sf.odinms.client.messages.commands.gm.HelpGMCommand;
import net.sf.odinms.client.messages.commands.intern.HelpInternCommand;
import net.sf.odinms.client.messages.commands.players.HelpPlayerCommand;
import net.sf.odinms.client.messages.commands.Sannin.HelpSanninCommand;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.channel.handler.GeneralChatHandler;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.ClassFinder;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.MockIOSession;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandProcessor implements CommandProcessorMBean {

    private static final Logger log = LoggerFactory.getLogger(GeneralChatHandler.class);
    private static List<Pair<MapleCharacter, String>> gmlog = new LinkedList<Pair<MapleCharacter, String>>();
    private Map<String, DefinitionPlayerCommandPair> playercommands = new LinkedHashMap<String, DefinitionPlayerCommandPair>();
    private Map<String, DefinitionDonatorCommandPair> donatorcommands = new LinkedHashMap<String, DefinitionDonatorCommandPair>();
    private Map<String, DefinitionGMCommandPair> gmcommands = new LinkedHashMap<String, DefinitionGMCommandPair>();
    private Map<String, DefinitionAdminCommandPair> admincommands = new LinkedHashMap<String, DefinitionAdminCommandPair>();
    private Map<String, DefinitionInternCommandPair> interncommands = new LinkedHashMap<String, DefinitionInternCommandPair>();
    private Map<String, DefinitionSanninCommandPair> sannincommands = new LinkedHashMap<String, DefinitionSanninCommandPair>();
    private static CommandProcessor instance = new CommandProcessor();
    private static Runnable persister;

    static {
        persister = new PersistingTask();
        TimerManager.getInstance().register(persister, 62000);
    }

    private CommandProcessor() {
        instance = this; // hackydihack
        reloadAllCommands();
    }

    public static class PersistingTask implements Runnable {

        @Override
        public void run() {
            synchronized (gmlog) {
                Connection con = DatabaseConnection.getConnection();
                try {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO gmlog (accname, charname, command) VALUES (?, ?, ?)");
                    for (Pair<MapleCharacter, String> logentry : gmlog) {
                        ps.setString(1, logentry.getLeft().getClient().getAccountName());
                        ps.setString(2, logentry.getLeft().getName());
                        ps.setString(3, logentry.getRight().toString());
                        ps.executeUpdate();
                    }
                    ps.close();
                } catch (SQLException e) {
                    log.error("error persisting gmlog", e);
                }
                gmlog.clear();
            }
        }
    }

    public static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.registerMBean(instance, new ObjectName("net.sf.odinms.client.messages:name=CommandProcessor"));
        } catch (Exception e) {
            log.error("Error registering CommandProcessor MBean");
        }
    }

    public static String joinAfterString(String splitted[], String str) {
        for (int i = 1; i < splitted.length; i++) {
            if (splitted[i].equalsIgnoreCase(str) && i + 1 < splitted.length) {
                return StringUtil.joinStringFrom(splitted, i + 1);
            }
        }
        return null;
    }

    public static int getOptionalIntArg(String splitted[], int position, int def) {
        if (splitted.length > position) {
            try {
                return Integer.parseInt(splitted[position]);
            } catch (NumberFormatException nfe) {
                return def;
            }
        }
        return def;
    }

    public static String getNamedArg(String splitted[], int startpos, String name) {
        for (int i = startpos; i < splitted.length; i++) {
            if (splitted[i].equalsIgnoreCase(name) && i + 1 < splitted.length) {
                return splitted[i + 1];
            }
        }
        return null;
    }

    public static Integer getNamedIntArg(String splitted[], int startpos, String name) {
        String arg = getNamedArg(splitted, startpos, name);
        if (arg != null) {
            try {
                return Integer.parseInt(arg);
            } catch (NumberFormatException nfe) {
                // swallow - we don't really care
            }
        }
        return null;
    }

    public static int getNamedIntArg(String splitted[], int startpos, String name, int def) {
        Integer ret = getNamedIntArg(splitted, startpos, name);
        if (ret == null) {
            return def;
        }
        return ret.intValue();
    }

    public static Double getNamedDoubleArg(String splitted[], int startpos, String name) {
        String arg = getNamedArg(splitted, startpos, name);
        if (arg != null) {
            try {
                return Double.parseDouble(arg);
            } catch (NumberFormatException nfe) {
                // swallow - we don't really care
            }
        }
        return null;
    }

    public boolean processCommand(MapleClient c, String line) {
        return instance.processCommandInternal(c, new ServernoticeMapleClientMessageCallback(c), line);
    }

    /* (non-Javadoc)
     * @see net.sf.odinms.client.messages.CommandProcessorMBean#processCommandJMX(int, int, java.lang.String)
     */
    public String processCommandJMX(int cserver, int mapid, String command) {
        ChannelServer cserv = ChannelServer.getInstance(cserver);
        if (cserv == null) {
            return "The specified channel Server does not exist in this serverprocess";
        }
        MapleClient c = new MapleClient(null, null, new MockIOSession());
        MapleCharacter chr = MapleCharacter.getDefault(c, 26023);
        c.setPlayer(chr);
        chr.setName("/---------jmxuser-------------\\"); // (name longer than maxmimum length)
        MapleMap map = cserv.getMapFactory().getMap(mapid);
        if (map != null) {
            chr.setMap(map);
            SkillFactory.getSkill(5101004).getEffect(1).applyTo(chr);
            map.addPlayer(chr);
        }
        cserv.addPlayer(chr);
        MessageCallback mc = new StringMessageCallback();
        try {
            processCommandInternal(c, mc, command);
        } finally {
            if (map != null) {
                map.removePlayer(chr);
            }
            cserv.removePlayer(chr);
        }
        return mc.toString();
    }

    public static void forcePersisting() {
        persister.run();
    }

    public static CommandProcessor getInstance() {
        return instance;
    }

    public void reloadAllCommands() {
        reloadAdminCommands();
        reloadGMCommands();
        reloadInternCommands();
        reloadSanninCommands();
        reloadDonatorCommands();
        reloadPlayerCommands();
    }

    public void reloadAdminCommands() {
        admincommands.clear();
        try {
            ClassFinder classFinder = new ClassFinder();
            String[] classes = classFinder.listClasses("net.sf.odinms.client.messages.commands.admin", true);
            registerAdminCommand(new HelpAdminCommand()); // register the helpcommand first so it appears first in the list (LinkedHashMap)
            for (String clazz : classes) {
                Class<?> clasz = Class.forName(clazz);
                if (AdminCommand.class.isAssignableFrom(clasz)) {
                    try {
                        AdminCommand newInstance = (AdminCommand) clasz.newInstance();
                        registerAdminCommand(newInstance);
                    } catch (Exception e) {
                        log.error("ERROR INSTANCIATING COMMAND CLASS", e);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("THROW", e);
        }
    }

    public void reloadGMCommands() {
        gmcommands.clear();
        try {
            ClassFinder classFinder = new ClassFinder();
            String[] classes = classFinder.listClasses("net.sf.odinms.client.messages.commands.gm", true);
            registerGMCommand(new HelpGMCommand()); // register the helpcommand first so it appears first in the list (LinkedHashMap)
            for (String clazz : classes) {
                Class<?> clasz = Class.forName(clazz);
                if (GMCommand.class.isAssignableFrom(clasz)) {
                    try {
                        GMCommand newInstance = (GMCommand) clasz.newInstance();
                        registerGMCommand(newInstance);
                    } catch (Exception e) {
                        log.error("ERROR INSTANCIATING COMMAND CLASS", e);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("THROW", e);
        }
    }

    public void reloadInternCommands() {
        interncommands.clear();
        try {
            ClassFinder classFinder = new ClassFinder();
            String[] classes = classFinder.listClasses("net.sf.odinms.client.messages.commands.intern", true);
            registerInternCommand(new HelpInternCommand()); // register the helpcommand first so it appears first in the list (LinkedHashMap)
            for (String clazz : classes) {
                Class<?> clasz = Class.forName(clazz);
                if (InternCommand.class.isAssignableFrom(clasz)) {
                    try {
                        InternCommand newInstance = (InternCommand) clasz.newInstance();
                        registerInternCommand(newInstance);
                    } catch (Exception e) {
                        log.error("ERROR INSTANCIATING COMMAND CLASS", e);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("THROW", e);
        }
    }

    public void reloadSanninCommands() {
        sannincommands.clear();
        try {
            ClassFinder classFinder = new ClassFinder();
            String[] classes = classFinder.listClasses("net.sf.odinms.client.messages.commands.Sannin", true);
            registerSanninCommand(new HelpSanninCommand()); // register the helpcommand first so it appears first in the list (LinkedHashMap)
            for (String clazz : classes) {
                Class<?> clasz = Class.forName(clazz);
                if (SanninCommand.class.isAssignableFrom(clasz)) {
                    try {
                        SanninCommand newInstance = (SanninCommand) clasz.newInstance();
                        registerSanninCommand(newInstance);
                    } catch (Exception e) {
                        log.error("ERROR INSTANCIATING COMMAND CLASS", e);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("THROW", e);
        }

    }

    public void reloadDonatorCommands() {
        donatorcommands.clear();
        try {
            ClassFinder classFinder = new ClassFinder();
            String[] classes = classFinder.listClasses("net.sf.odinms.client.messages.commands.donator", true);
            registerDonatorCommand(new HelpDonatorCommand()); // register the helpcommand first so it appears first in the list (LinkedHashMap)
            for (String clazz : classes) {
                Class<?> clasz = Class.forName(clazz);
                if (DonatorCommand.class.isAssignableFrom(clasz)) {
                    try {
                        DonatorCommand newInstance = (DonatorCommand) clasz.newInstance();
                        registerDonatorCommand(newInstance);
                    } catch (Exception e) {
                        log.error("ERROR INSTANCIATING COMMAND CLASS", e);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("THROW", e);
        }
    }

    public void reloadPlayerCommands() {
        playercommands.clear();
        try {
            ClassFinder classFinder = new ClassFinder();
            String[] classes = classFinder.listClasses("net.sf.odinms.client.messages.commands.player", true);
            registerPlayerCommand(new HelpPlayerCommand()); // register the helpcommand first so it appears first in the list (LinkedHashMap)
            for (String clazz : classes) {
                Class<?> clasz = Class.forName(clazz);
                if (PlayerCommand.class.isAssignableFrom(clasz)) {
                    try {
                        PlayerCommand newInstance = (PlayerCommand) clasz.newInstance();
                        registerPlayerCommand(newInstance);
                    } catch (Exception e) {
                        log.error("ERROR INSTANCIATING COMMAND CLASS", e);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("THROW", e);
        }
    }

    private void registerAdminCommand(AdminCommand command) {
        AdminCommandDefinition[] definition = command.getDefinition();
        for (AdminCommandDefinition def : definition) {
            admincommands.put(def.getCommand(), new DefinitionAdminCommandPair(command, def));
        }
    }

    private void registerGMCommand(GMCommand command) {
        GMCommandDefinition[] definition = command.getDefinition();
        for (GMCommandDefinition def : definition) {
            gmcommands.put(def.getCommand(), new DefinitionGMCommandPair(command, def));
        }
    }

    private void registerPlayerCommand(PlayerCommand command) {
        PlayerCommandDefinition[] definition = command.getDefinition();
        for (PlayerCommandDefinition def : definition) {
            playercommands.put(def.getCommand(), new DefinitionPlayerCommandPair(command, def));
        }
    }

    private void registerDonatorCommand(DonatorCommand command) {
        DonatorCommandDefinition[] definition = command.getDefinition();
        for (DonatorCommandDefinition def : definition) {
            donatorcommands.put(def.getCommand(), new DefinitionDonatorCommandPair(command, def));
        }
    }

    private void registerInternCommand(InternCommand command) {
        InternCommandDefinition[] definition = command.getDefinition();
        for (InternCommandDefinition def : definition) {
            interncommands.put(def.getCommand(), new DefinitionInternCommandPair(command, def));
        }
    }

    private void registerSanninCommand(SanninCommand command) {
        SanninCommandDefinition[] definition = command.getDefinition();
        for (SanninCommandDefinition def : definition) {
            sannincommands.put(def.getCommand(), new DefinitionSanninCommandPair(command, def));
        }
    }

    public void dropAdminHelp(MapleCharacter chr, MessageCallback mc, int page) {
        List<DefinitionAdminCommandPair> allCommands = new ArrayList<DefinitionAdminCommandPair>(admincommands.values());
        int startEntry = (page - 1) * 20;
        mc.dropMessage("Admin Command list page : --------" + page + "---------");
        for (int i = startEntry; i < startEntry + 20 && i < allCommands.size(); i++) {
            AdminCommandDefinition commandDefinition = allCommands.get(i).getDefinition();
            if (chr.isAdmin()) {
                dropHelpForAdminDefinition(mc, commandDefinition);
            }
        }
    }

    public void dropGMHelp(MapleCharacter chr, MessageCallback mc, int page) {
        List<DefinitionGMCommandPair> allCommands = new ArrayList<DefinitionGMCommandPair>(gmcommands.values());
        int startEntry = (page - 1) * 20;
        mc.dropMessage("GM Command List Page: --------" + page + "---------");
        for (int i = startEntry; i < startEntry + 20 && i < allCommands.size(); i++) {
            GMCommandDefinition commandDefinition = allCommands.get(i).getDefinition();
            if (chr.isJounin()) {
                dropHelpForGMDefinition(mc, commandDefinition);
            }
        }
    }

    public void dropInternHelp(MapleCharacter chr, MessageCallback mc, int page) {
        List<DefinitionInternCommandPair> allCommands = new ArrayList<DefinitionInternCommandPair>(interncommands.values());
        int startEntry = (page - 1) * 20;
        mc.dropMessage("Intern Command List Page: --------" + page + "---------");
        for (int i = startEntry; i < startEntry + 20 && i < allCommands.size(); i++) {
            InternCommandDefinition commandDefinition = allCommands.get(i).getDefinition();
            if (chr.isChunin()) {
                dropHelpForInternDefinition(mc, commandDefinition);
            }
        }
    }

    public void dropSanninHelp(MapleCharacter chr, MessageCallback mc, int page) {
        List<DefinitionSanninCommandPair> allCommands = new ArrayList<DefinitionSanninCommandPair>(sannincommands.values());
        int startEntry = (page - 1) * 20;
        mc.dropMessage("Sannin Command List Page: --------" + page + "---------");
        for (int i = startEntry; i < startEntry + 20 && i < allCommands.size(); i++) {
            SanninCommandDefinition commandDefinition = allCommands.get(i).getDefinition();
            if (chr.isSannin()) {
                dropHelpForSanninDefinition(mc, commandDefinition);
            }
        }
    }

    public void dropDonatorHelp(MapleCharacter chr, MessageCallback mc, int page) {
        List<DefinitionDonatorCommandPair> allCommands = new ArrayList<DefinitionDonatorCommandPair>(donatorcommands.values());
        int startEntry = (page - 1) * 20;
        mc.dropMessage("Donator Command List Page: --------" + page + "---------");
        for (int i = startEntry; i < startEntry + 20 && i < allCommands.size(); i++) {
            DonatorCommandDefinition commandDefinition = allCommands.get(i).getDefinition();
            if (chr.isGenin()) {
                dropHelpForDonatorDefinition(mc, commandDefinition);
            }
        }
    }

    public void dropPlayerHelp(MapleCharacter chr, MessageCallback mc, int page) {
        List<DefinitionPlayerCommandPair> allCommands = new ArrayList<DefinitionPlayerCommandPair>(playercommands.values());
        int startEntry = (page - 1) * 20;
        mc.dropMessage("Player Command List Page: --------" + page + "---------");
        for (int i = startEntry; i < startEntry + 20 && i < allCommands.size(); i++) {
            PlayerCommandDefinition commandDefinition = allCommands.get(i).getDefinition();
            dropHelpForPlayerDefinition(mc, commandDefinition);
        }
    }

    private void dropHelpForAdminDefinition(MessageCallback mc, AdminCommandDefinition commandDefinition) {
        mc.dropMessage("/" + commandDefinition.getCommand() + " . Syntax: /" + commandDefinition.getCommand() + " " + commandDefinition.getParameterDescription() + "  use : " + commandDefinition.getHelp());
    }

    private void dropHelpForGMDefinition(MessageCallback mc, GMCommandDefinition commandDefinition) {
        mc.dropMessage("!" + commandDefinition.getCommand() + " . Syntax: !" + commandDefinition.getCommand() + " " + commandDefinition.getParameterDescription() + "  use : " + commandDefinition.getHelp());
    }

    private void dropHelpForInternDefinition(MessageCallback mc, InternCommandDefinition commandDefinition) {
        mc.dropMessage("$" + commandDefinition.getCommand() + " . Syntax: $" + commandDefinition.getCommand() + " " + commandDefinition.getParameterDescription() + "  use : " + commandDefinition.getHelp());
    }

    private void dropHelpForSanninDefinition(MessageCallback mc, SanninCommandDefinition commandDefinition) {
        mc.dropMessage("%" + commandDefinition.getCommand() + " . Syntax: %" + commandDefinition.getCommand() + " " + commandDefinition.getParameterDescription() + "  use : " + commandDefinition.getHelp());
    }

    private void dropHelpForDonatorDefinition(MessageCallback mc, DonatorCommandDefinition commandDefinition) {
        mc.dropMessage("#" + commandDefinition.getCommand() + " . Syntax: #" + commandDefinition.getCommand() + " " + commandDefinition.getParameterDescription() + "  use : " + commandDefinition.getHelp());
    }

    private void dropHelpForPlayerDefinition(MessageCallback mc, PlayerCommandDefinition commandDefinition) {
        mc.dropMessage("@" + commandDefinition.getCommand() + " . Syntax: @" + commandDefinition.getCommand() + " " + commandDefinition.getParameterDescription() + "  use : " + commandDefinition.getHelp());
    }

    /* (non-Javadoc)
     * @see net.sf.odinms.client.messages.CommandProcessorMBean#processCommandInstance(net.sf.odinms.client.MapleClient, java.lang.String)
     */
    private boolean processAdminCommand(MapleClient c, MessageCallback mc, String[] splitted) {
        DefinitionAdminCommandPair definitionCommandPair = admincommands.get(splitted[0]);
        if (definitionCommandPair != null) {
            try {
                definitionCommandPair.getCommand().execute(c, mc, splitted);
            } catch (IllegalCommandSyntaxException ex) {
                mc.dropMessage(ex.getMessage());
                dropHelpForAdminDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NumberFormatException nfe) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForAdminDefinition(mc, definitionCommandPair.getDefinition());
            } catch (ArrayIndexOutOfBoundsException ex) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForAdminDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NullPointerException exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            } catch (Exception exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            }
            return true;
        } else {
            processGMCommand(c, mc, splitted);
        }
        return false;
    }

    private boolean processGMCommand(MapleClient c, MessageCallback mc, String[] splitted) {
        DefinitionGMCommandPair definitionCommandPair = gmcommands.get(splitted[0]);
        MapleCharacter player = c.getPlayer();
        if (definitionCommandPair != null) {
            try {
                definitionCommandPair.getCommand().execute(c, mc, splitted);
            } catch (IllegalCommandSyntaxException ex) {
                mc.dropMessage(ex.getMessage());
                dropHelpForGMDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NumberFormatException nfe) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForGMDefinition(mc, definitionCommandPair.getDefinition());
            } catch (ArrayIndexOutOfBoundsException ex) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForGMDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NullPointerException exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            } catch (Exception exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            }
            return true;
        } else {
            processInternCommand(c, mc, splitted);
        }
        return true;
    }

    private boolean processInternCommand(MapleClient c, MessageCallback mc, String[] splitted) {
        DefinitionInternCommandPair definitionCommandPair = interncommands.get(splitted[0]);
        if (definitionCommandPair != null) {
            try {
                definitionCommandPair.getCommand().execute(c, mc, splitted);
            } catch (IllegalCommandSyntaxException ex) {
                mc.dropMessage(ex.getMessage());
                dropHelpForInternDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NumberFormatException nfe) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForInternDefinition(mc, definitionCommandPair.getDefinition());
            } catch (ArrayIndexOutOfBoundsException ex) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForInternDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NullPointerException exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            } catch (Exception exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            }
            return true;
        } else {
            processDonatorCommand(c, mc, splitted);
        }
        return true;
    }

    private boolean processSanninCommand(MapleClient c, MessageCallback mc, String[] splitted) {
        DefinitionSanninCommandPair definitionCommandPair = sannincommands.get(splitted[0]);
        if (definitionCommandPair != null) {
            try {
                definitionCommandPair.getCommand().execute(c, mc, splitted);
            } catch (IllegalCommandSyntaxException ex) {
                mc.dropMessage(ex.getMessage());
                dropHelpForSanninDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NumberFormatException nfe) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForSanninDefinition(mc, definitionCommandPair.getDefinition());
            } catch (ArrayIndexOutOfBoundsException ex) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForSanninDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NullPointerException exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            } catch (Exception exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            }
            return true;
        } else {
            processGMCommand(c, mc, splitted);
        }
        return true;
    }

    private boolean processDonatorCommand(MapleClient c, MessageCallback mc, String[] splitted) {
        DefinitionDonatorCommandPair definitionCommandPair = donatorcommands.get(splitted[0]);
        if (definitionCommandPair != null) {
            try {
                definitionCommandPair.getCommand().execute(c, mc, splitted);
            } catch (IllegalCommandSyntaxException ex) {
                mc.dropMessage(ex.getMessage());
                dropHelpForDonatorDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NumberFormatException nfe) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForDonatorDefinition(mc, definitionCommandPair.getDefinition());
            } catch (ArrayIndexOutOfBoundsException ex) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForDonatorDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NullPointerException exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            } catch (Exception exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            }
            return true;
        } else {
            processPlayerCommand(c, mc, splitted);
        }
        return true;
    }

    private boolean processPlayerCommand(MapleClient c, MessageCallback mc, String[] splitted) {
        DefinitionPlayerCommandPair definitionCommandPair = playercommands.get(splitted[0]);
        if (definitionCommandPair != null) {
            try {
                definitionCommandPair.getCommand().execute(c, mc, splitted);
            } catch (IllegalCommandSyntaxException ex) {
                mc.dropMessage(ex.getMessage());
                dropHelpForPlayerDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NumberFormatException nfe) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForPlayerDefinition(mc, definitionCommandPair.getDefinition());
            } catch (ArrayIndexOutOfBoundsException ex) {
                mc.dropMessage("[The Elite Ninja Gang] The Syntax to your command appears to be wrong. The correct syntax is given below.");
                dropHelpForPlayerDefinition(mc, definitionCommandPair.getDefinition());
            } catch (NullPointerException exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            } catch (Exception exx) {
                mc.dropMessage("An error occured: " + exx.getClass().getName() + " " + exx.getMessage());
                log.error("COMMAND ERROR", exx);
            }
            return true;
        } else {
            mc.dropMessage("[The Elite Ninja Gang] Such command does not exist.");
        }
        return true;
    }

    private boolean processCommandInternal(MapleClient c, MessageCallback mc, String line) {
        MapleCharacter player = c.getPlayer();
        if (line.charAt(0) == '`' && player.isChunin()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].substring(1);
            String lol = StringUtil.joinStringFrom(splitted, 0);
            if (lol.length() > 1) {
                try {
                    c.getChannelServer().getWorldInterface().broadcastStaffMessage(null, MaplePacketCreator.multiChat(player.getName() + " [GMChat] ", lol, 3).getBytes());
                } catch (Exception ex) {
                    mc.dropMessage("GM Chat Error: Unable to broadcast message.");
                }
            }
            return true;
        } else if (line.charAt(0) == '/' && player.isAdmin()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            if (splitted.length > 0 && splitted[0].length() > 1) {
                processAdminCommand(c, mc, splitted);
                return true;
            }
        } else if (line.charAt(0) == '%' && (player.isSannin()|| player.getName().equalsIgnoreCase("system"))) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            if (splitted.length > 0 && splitted[0].length() > 1) {
                processSanninCommand(c, mc, splitted);
                if (!c.getPlayer().isAdmin()) {
                    synchronized (gmlog) {
                        gmlog.add(new Pair<MapleCharacter, String>(player, line));
                    }
                }
                return true;
            }
        } else if (line.charAt(0) == '!' && (player.isJounin() || player.getName().equalsIgnoreCase("system"))) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            if (splitted.length > 0 && splitted[0].length() > 1) {
                processGMCommand(c, mc, splitted);
                if (!c.getPlayer().isAdmin()) {
                    synchronized (gmlog) {
                        gmlog.add(new Pair<MapleCharacter, String>(c.getPlayer(), line));
                    }
                }
                return true;
            }
        } else if (line.charAt(0) == '$' && player.isChunin() && !player.inJail()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            if (splitted.length > 0 && splitted[0].length() > 1) {
                processInternCommand(c, mc, splitted);
                if (!c.getPlayer().isAdmin()) {
                    synchronized (gmlog) {
                        gmlog.add(new Pair<MapleCharacter, String>(c.getPlayer(), line));
                    }
                }
                return true;
            }
        } else if (line.charAt(0) == '#' && player.isGenin() && !player.inJail()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            if (splitted.length > 0 && splitted[0].length() > 1) {
                processDonatorCommand(c, mc, splitted);
                return true;
            }
        } else if (line.charAt(0) == '@' && !player.inJail()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase().substring(1);
            if (splitted.length > 0 && splitted[0].length() > 1) {
                processPlayerCommand(c, mc, splitted);
                return true;
            }
        }
        return false;
    }
}

class DefinitionGMCommandPair {

    private GMCommand gmcommand;
    private GMCommandDefinition definition;

    public DefinitionGMCommandPair(GMCommand command, GMCommandDefinition definition) {
        super();
        this.gmcommand = command;
        this.definition = definition;
    }

    public GMCommand getCommand() {
        return gmcommand;
    }

    public GMCommandDefinition getDefinition() {
        return definition;
    }
}

class DefinitionAdminCommandPair {

    private AdminCommand admincommand;
    private AdminCommandDefinition definition;

    public DefinitionAdminCommandPair(AdminCommand command, AdminCommandDefinition definition) {
        super();
        this.admincommand = command;
        this.definition = definition;
    }

    public AdminCommand getCommand() {
        return admincommand;
    }

    public AdminCommandDefinition getDefinition() {
        return definition;
    }
}

class DefinitionInternCommandPair {

    private InternCommand interncommand;
    private InternCommandDefinition definition;

    public DefinitionInternCommandPair(InternCommand command, InternCommandDefinition definition) {
        super();
        this.interncommand = command;
        this.definition = definition;
    }

    public InternCommand getCommand() {
        return interncommand;
    }

    public InternCommandDefinition getDefinition() {
        return definition;
    }
}

class DefinitionSanninCommandPair {

    private SanninCommand Sannincommand;
    private SanninCommandDefinition definition;

    public DefinitionSanninCommandPair(SanninCommand command, SanninCommandDefinition definition) {
        super();
        this.Sannincommand = command;
        this.definition = definition;
    }

    public SanninCommand getCommand() {
        return Sannincommand;
    }

    public SanninCommandDefinition getDefinition() {
        return definition;
    }
}

class DefinitionDonatorCommandPair {

    private DonatorCommand donatorcommand;
    private DonatorCommandDefinition definition;

    public DefinitionDonatorCommandPair(DonatorCommand command, DonatorCommandDefinition definition) {
        super();
        this.donatorcommand = command;
        this.definition = definition;
    }

    public DonatorCommand getCommand() {
        return donatorcommand;
    }

    public DonatorCommandDefinition getDefinition() {
        return definition;
    }
}

class DefinitionPlayerCommandPair {

    private PlayerCommand playercommand;
    private PlayerCommandDefinition definition;

    public DefinitionPlayerCommandPair(PlayerCommand command, PlayerCommandDefinition definition) {
        super();
        this.playercommand = command;
        this.definition = definition;
    }

    public PlayerCommand getCommand() {
        return playercommand;
    }

    public PlayerCommandDefinition getDefinition() {
        return definition;
    }
}
