/*
This file was written by "StellarAshes" <stellar_dust@hotmail.com> 
as a part of the Guild package for
the OdinMS Maple Story Server
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
package net.sf.odinms.net.world.guild;

import net.sf.odinms.client.MapleCharacter;

public class MapleGuildCharacter implements java.io.Serializable // alias for a character
{

    public static final long serialVersionUID = 2058609046116597760L;
    private int reborns, id, channel, jobid, guildrank, guildid, allianceRank;
    private boolean online;
    private String name;

    // either read from active character...
    // if it's online
    public MapleGuildCharacter(MapleCharacter c) {
        name = c.getName();
        reborns = c.getReborns();
        id = c.getId();
        channel = c.getClient().getChannel();
        jobid = c.getJob().getId();
        guildrank = c.getGuildRank();
        guildid = c.getGuildId();
        online = true;
        allianceRank = c.getAllianceRank();
    }

    // or we could just read from the database
    public MapleGuildCharacter(int _id, int _rb, String _name, int _channel, int _job, int _rank, int _gid, boolean _on, int aRank) {
        reborns = _rb;
        id = _id;
        name = _name;
        if (_on) {
            channel = _channel;
        }

        jobid = _job;
        online = _on;
        guildrank = _rank;
        guildid = _gid;
        allianceRank = aRank;
    }

    public int getReborns() {
        return reborns;
    }

    public void setReborns(int l) {
        reborns = l;
    }

    public int getId() {
        return id;
    }

    public void setChannel(int ch) {
        channel = ch;
    }

    public int getChannel() {
        return channel;
    }

    public int getJobId() {
        return jobid;
    }

    public void setJobId(int job) {
        jobid = job;
    }

    public int getGuildId() {
        return guildid;
    }

    public void setGuildId(int gid) {
        guildid = gid;
    }

    public void setGuildRank(int rank) {
        guildrank = rank;
    }

    public int getGuildRank() {
        return guildrank;
    }

    public boolean isOnline() {
        return online;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MapleGuildCharacter)) {
            return false;
        }

        MapleGuildCharacter o = (MapleGuildCharacter) other;
        return (o.getId() == id && o.getName().equals(name));
    }

    public void setOnline(boolean f) {
        online = f;
    }

    public void setAllianceRank(int rank) {
        allianceRank = rank;
    }

    public int getAllianceRank() {
        return allianceRank;
    }
}
