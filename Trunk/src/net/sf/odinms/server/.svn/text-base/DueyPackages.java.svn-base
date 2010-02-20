/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.server;

import java.util.Calendar;
import net.sf.odinms.client.Inventory.IItem;

/**
 *
 * @author Owner
 */
public class DueyPackages {
    private String sender = null;
    private IItem item = null;
    private int mesos = 0;
    private int day;
    private int month;
    private int year;
    private int packageId = 0;

    public DueyPackages(int pId, IItem item) {
        this.item = item;
        packageId = pId;
    }

    public DueyPackages(int pId) { // Meso only package.
        this.packageId = pId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String name) {
        sender = name;
    }

    public IItem getItem() {
        return item;
    }

    public int getMesos() {
        return mesos;
    }

    public void setMesos(int set) {
        mesos = set;
    }

    public int getPackageId() {
        return packageId;
    }

    public long sentTimeInMilliseconds() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTimeInMillis();
    }

    public void setSentTime(String sentTime) {
        day = Integer.parseInt(sentTime.substring(0, 2));
        month = Integer.parseInt(sentTime.substring(3, 5));
        year = Integer.parseInt(sentTime.substring(6, 10));
    }
}

