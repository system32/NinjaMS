/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.server.constants;

import java.util.HashMap;
import java.util.Map;
import net.sf.odinms.client.Enums.Clans;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.server.TimerManager;

/**
 *
 * @author Owner
 */
public class Modes {

    private boolean kyubi = false, sage = false, shakaku = false, hachibi = false;
    private int kyubiTimer = 0, sageTimer = 0, shakakuTimer = 0, hachibiTimer = 0;
    private MapleCharacter chr;
    private static Map<MapleCharacter, Modes> instances = new HashMap<MapleCharacter, Modes>();

    public Modes(MapleCharacter chr) {
        this.chr = chr;
    }

    public static Modes getInstance(MapleCharacter player) {
        if (!instances.containsKey(player)) {
            instances.put(player, new Modes(player));
        }
        return instances.get(player);
    }

    // Set Modes on
    public void setKyubi() {
        if (chr.getReborns() <= 10 || (chr.getClan() != Clans.EARTH && chr.getClan() != Clans.NARUTO)) {
            chr.dropMessage("You need to have atleast 10 Rebirths before you can unleash the Kyubi inside you");
            return;
        }
        chr.setNoHide(true);
        cancelKyubi();
        chr.cancelAllBuffs();
        kyubi = true;
        kyubiTimer();
        chr.dropMessage(5, "[Sarutobi]You are now in Kyubi Mode");
    }

    public void setSage() {
        if (chr.getReborns() <= 10 || (chr.getClan() != Clans.WIND && chr.getClan() != Clans.NARUTO)) {
            chr.dropMessage("[Jiraya]You need to have atleast 10 Rebirths before you can use the Chakra of the environment to go in Sage mode");
            return;
        }
        chr.setNoHide(true);
        cancelSage();
        chr.cancelAllBuffs();
        sage = true;
        sageTimer();
        chr.dropMessage(5, "[Jiraya]You are now in Sage mode");
    }

    public void setHachibi() {
        if (chr.getReborns() <= 10 || (chr.getClan() != Clans.WATER && chr.getClan() != Clans.NARUTO)) {
            chr.dropMessage("[KillerBee]You need to have atleast 10 Rebirths before you can unleash the power of Hachibi");
            return;
        }
        chr.setNoHide(true);
        cancelHachibi();
        chr.cancelAllBuffs();
        hachibi = true;
        hachibiTimer();
        chr.dropMessage(5, "[KillerBee]You are now in Hachibi Mode");
    }

    public void setShakaku() {
        if (chr.getReborns() <= 10 || (chr.getClan() != Clans.FIRE && chr.getClan() != Clans.NARUTO)) {
            chr.dropMessage("[Gaara]You need to have atleast 10 Rebirths before you can use the Chakra of the Shakaku");
            return;
        }
        chr.setNoHide(true);
        cancelShakaku();
        chr.cancelAllBuffs();
        shakaku = true;
        shakakuTimer();
        chr.dropMessage(5, "[Gaara]You are now in Shakaku mode");
    }

    public void setAllModeOn() {
        setKyubi();
        setSage();
        setHachibi();
        setShakaku();
    }

//Cancel modes
    public void cancelKyubi() {
        kyubi = false;
        kyubiTimer = 0;
    }

    public void cancelSage() {
        sage = false;
        sageTimer = 0;
    }

    public void cancelHachibi() {
        hachibi = false;
        hachibiTimer = 0;
    }

    public void cancelShakaku() {
        shakaku = false;
        shakakuTimer = 0;
    }

    // booleans to check if mode is on
    public boolean hasKyubi() {
        return kyubi;
    }

    public boolean hasSage() {
        return sage;
    }

    public boolean hasHachibi() {
        return hachibi;
    }

    public boolean hasShakaku() {
        return shakaku;
    }

    public boolean hasModeOn() {
        return kyubi || sage || hachibi || shakaku;
    }

    // Timers count and run shit
    public void kyubiTimer() {
        if (kyubi) {
            addKyubiTimer();
            final int modeused = getKyubiTimer();
            if (modeused < 15) {
                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        chr.dropMessage(5, "[Naruto] You have used " + modeused + " minutes of your Kyubi mode");
                        kyubiTimer();
                    }
                }, 60 * 1000);
            } else {
                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        cancelKyubi();
                        chr.dropMessage(5, "[Sarutobi] You have ran out of Kyubi chakra.");
                    }
                }, 60 * 1000);
            }
        }
    }

    public void sageTimer() {
        if (sage) {
            addSageTimer();
            final int modeused = getSageTimer();
            if (modeused < 15) {
                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        chr.dropMessage(5, "[Jiraya] You have used " + modeused + " minutes of your Sage mode");
                        sageTimer();
                    }
                }, 60 * 1000);
            } else {
                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        cancelSage();
                        chr.dropMessage(5, "[Jiraya] You have ran out of Sage chakra.");
                    }
                }, 60 * 1000);
            }
        }
    }

    public void hachibiTimer() {
        if (hachibi) {
            addHachibiTimer();
            final int modeused = getHachibiTimer();
            if (modeused < 15) {
                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        chr.dropMessage(5, "[KillerBee] You have used " + modeused + " minutes of your Hachibi mode");
                        hachibiTimer();
                    }
                }, 60 * 1000);
            } else {
                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        cancelHachibi();
                        chr.dropMessage(5, "[KillerBee] You have ran out of Hachibi chakra.");
                    }
                }, 60 * 1000);
            }
        }
    }

    public void shakakuTimer() {
        if (shakaku) {
            addShakakuTimer();
            final int modeused = getShakakuTimer();
            if (modeused < 15) {
                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        chr.dropMessage(5, "[Gaara] You have used " + modeused + " minutes of your Shakaku mode");
                        shakakuTimer();
                    }
                }, 60 * 1000);
            } else {
                TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        cancelShakaku();
                        chr.dropMessage(5, "[Gaara] You have ran out of Shakaku chakra.");
                    }
                }, 60 * 1000);
            }
        }
    }

    //adding timer varibles and such
    public int getKyubiTimer() {
        return kyubiTimer;
    }

    public void addKyubiTimer() {
        kyubiTimer++;
    }

    public int getSageTimer() {
        return sageTimer;
    }

    public void addSageTimer() {
        sageTimer++;
    }

    public int getHachibiTimer() {
        return hachibiTimer;
    }

    public void addHachibiTimer() {
        hachibiTimer++;
    }

    public int getShakakuTimer() {
        return shakakuTimer;
    }

    public void addShakakuTimer() {
        shakakuTimer++;
    }
}
