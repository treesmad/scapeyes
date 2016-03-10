package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

/**
 * Logs out player if they have been idle for a period of time set by the
 * client. Player will not log out if they taken damage within the last 10
 * seconds.
 */
public class PlayerIdleLogout implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		p.disconnected = true;
	}

}
