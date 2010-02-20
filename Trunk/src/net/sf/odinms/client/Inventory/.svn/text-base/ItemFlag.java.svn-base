/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.Inventory;

import net.sf.odinms.net.IntValueHolder;

/**
 *
 * @author Owner
 */
public enum ItemFlag implements IntValueHolder {

    LOCK(0x01),
    SPIKES(0x02),
    COLD(0x04),
    UNTRADEABLE(0x08),
    KARMA(0x10);
    private final int i;

    private ItemFlag(int i) {
        this.i = i;
    }

    @Override
    public int getValue() {
        return i;
    }
}

