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

package net.sf.odinms.net.login;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;

public class LoginWorker {
    private static LoginWorker instance = new LoginWorker();

    public static LoginWorker getInstance() {
	return instance;
    }

    public void registerClient(final MapleClient c) {
	try {
	    if (c.finishLogin(true) == 0) {
                c.getSession().write(MaplePacketCreator.getAuthSuccessRequestPin(c.getAccountName(), c.isJounin()));
                c.setIdleTask(TimerManager.getInstance().schedule(new Runnable() {

                    public void run() {
                        c.getSession().close();
                    }
                }, 10 * 60 * 10000));
            } else {
                c.getSession().write(MaplePacketCreator.getLoginFailed(7));
            }

	    LoginServer LS = LoginServer.getInstance();
            Map<Integer, Integer> load = LS.getWorldInterface().getChannelLoad();
            double loadFactor = 1200 / ((double) LS.getUserLimit() / load.size());
            for (Entry<Integer, Integer> entry : load.entrySet()) {
                load.put(entry.getKey(), Math.min(1200, (int) (entry.getValue() * loadFactor)));
            }
            LS.setLoad(load);
        } catch (RemoteException ex) {
            LoginServer.getInstance().reconnectWorld();
        }
    }
}
