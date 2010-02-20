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

package net.sf.odinms.server.maps;

import java.util.concurrent.ScheduledFuture;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Danny
 */
public class MapMonitor {
	private ScheduledFuture<?> monitorSchedule;
	private MapleMap map;
	private MaplePortal portal;
	private int ch;
	private MapleReactor reactor;

	public MapMonitor (final MapleMap map, MaplePortal portal, final int ch, MapleReactor reactor) {
		this.map = map;
        this.portal = portal;
		this.ch = ch;
		this.reactor = reactor;
		this.monitorSchedule = TimerManager.getInstance().register(
			new Runnable() {
				@Override
				public void run() {
                    switch (map.getId()) {
                        case 280030000:
                        case 220080001:
                        case 240060200:
                            if (map.getCharacters().size() <= 0) {
                                cancelAction();
                            }
                            break;
                        case 801040100:
              //              if (map.getCharacters().size() <= 0 && !map.isMonsterPresent(9400112) && !map.isMonsterPresent(9400113) && !map.isMonsterPresent(9400300)) {
                //                thebossAction();
                  //          }
//
                            break;
                    }
				}
			}, 2000);
	}

	public void cancelAction() {
		monitorSchedule.cancel(false);
		map.killAllMonsters();
		if (portal != null) {
			portal.setPortalStatus(MaplePortal.OPEN);
		}
		if (reactor != null) {
			reactor.setState((byte) 0);
			reactor.getMap().broadcastMessage(MaplePacketCreator.triggerReactor(reactor, 0));
		}
		map.resetReactors();
		ChannelServer.getInstance(ch).removeMapMonitor(map.getId());
	}

    public void thebossAction() {
		monitorSchedule.cancel(false);
		map.resetReactors();
		ChannelServer.getInstance(ch).removeMapMonitor(map.getId());
	}
}