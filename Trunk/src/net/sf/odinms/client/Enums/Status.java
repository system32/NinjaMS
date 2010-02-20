/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.Enums;

/**
 *
 * @author Owner
 */
public enum Status {
    ROOKIE(0),
    GENIN(1),
    CHUNIN(2),
    JOUNIN(3),
    SANNIN(4),
    HOKAGE(5),
    ;

    private final int level;
    private Status(int lvl){
        this.level = lvl;
    }

    public int getLevel() {
        return level;
    }

    public static Status getByLevel(int id) {
        for (Status l : Status.values()) {
            if (l.getLevel() == id) {
                return l;
            }
        }
        return null;
    }

    public String getTitle() {
        switch(level){
            case 0:
                return "Rookie";
            case 1:
                return "Genin";
            case 2:
                return "Chunin";
            case 3:
                return "Jounin";
            case 4:
                return "Sannin";
            case 5:
                return "Hokage";
            default :
                return "SystemError";

        }
    }
}
