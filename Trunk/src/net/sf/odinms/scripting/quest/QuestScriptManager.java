/*
 * This file is part of the OdinMS Maple Story Server
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
package net.sf.odinms.scripting.quest;

import java.util.HashMap;
import java.util.Map;
import javax.script.Invocable;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.scripting.AbstractScriptManager;
import net.sf.odinms.server.quest.MapleQuest;

/**
 *
 * @author RMZero213
 */
public class QuestScriptManager extends AbstractScriptManager {

    private Map<MapleClient, QuestActionManager> qms = new HashMap<MapleClient, QuestActionManager>();
    private Map<MapleClient, QuestScript> scripts = new HashMap<MapleClient, QuestScript>();
    private static QuestScriptManager instance = new QuestScriptManager();

    public synchronized static QuestScriptManager getInstance() {
        return instance;
    }

    public void start(MapleClient c, int npc, int quest) {
        begin(c, npc, quest, true);
    }

    public void end(MapleClient c, int npc, int quest) {
        begin(c, npc, quest, false);
    }

    public void begin(MapleClient c, int npc, int quest, boolean start) {
        try {
            synchronized (this) {
                if (checkQuestStatus(c, quest, start) || qms.containsKey(c) || scripts.containsKey(c)) {
                    return;
                }
                Invocable iv = getInvocable("quest/" + quest + ".js", c);
                if (iv == null) {
                    System.err.println("Uncoded Quest, ID : " + quest);
                    return;
                }
                QuestActionManager qm = new QuestActionManager(c, npc, quest, start);
                qms.put(c, qm);
                engine.put("qm", qm);
                QuestScript qs = iv.getInterface(QuestScript.class);
                scripts.put(c, qs);
                if (start) {
                    qs.start((byte) 1, (byte) 0, 0); // start it off as something
                } else {
                    qs.end((byte) 1, (byte) 0, 0);
                }
            }
        } catch (Exception e) {
            log.error("Error executing Quest script. (" + quest + ")", e);
            dispose(c);
        }
    }

    public void action(MapleClient c, byte mode, byte type, int selection) {
        QuestScript qs = scripts.get(c);
        if (qs != null) {
            try {
                if (getQM(c).isStart()) {
                    qs.start(mode, type, selection);
                } else {
                    qs.end(mode, type, selection);
                }
            } catch (Exception e) {
                log.error("Error executing Quest script. (" + getQM(c).getQuest() + ")", e);
                dispose(c);
            }
        }
    }

    public void dispose(QuestActionManager qm, MapleClient c) {
        qms.remove(c);
        scripts.remove(c);
        resetContext("quest/" + qm.getQuest() + ".js", c);
    }

    public void dispose(MapleClient c) {
        QuestActionManager qm = qms.get(c);
        if (qm != null) {
            dispose(qm, c);
        }
    }

    public QuestActionManager getQM(MapleClient c) {
        return qms.get(c);
    }

    private boolean checkQuestStatus(MapleClient c, int questid, boolean start) {
        if (start) {
            return !c.getPlayer().getQuest(MapleQuest.getInstance(questid)).getStatus().equals(MapleQuestStatus.Status.NOT_STARTED);
        } else { // Complete
            return c.getPlayer().getQuest(MapleQuest.getInstance(questid)).getStatus().equals(MapleQuestStatus.Status.COMPLETED);
        }
    }
}