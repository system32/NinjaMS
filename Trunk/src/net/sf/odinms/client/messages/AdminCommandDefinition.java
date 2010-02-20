/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.messages;

/**
 *
 * @author Admin
 */
public class AdminCommandDefinition {
private String admincommand;
    private String parameterDescription;
    private String help;

    public AdminCommandDefinition(String command, String parameterDescription, String help) {
        this.admincommand = command;
        this.help = help;
        this.parameterDescription = parameterDescription;
    }


    public String getCommand() {
        return admincommand;
    }

    public String getHelp() {
        return help;
    }

    public String getParameterDescription() {
        return parameterDescription;
    }
}
