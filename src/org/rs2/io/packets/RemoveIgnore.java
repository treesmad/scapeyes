package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

/**
 * Remove person from the ignore list.
 */
public class RemoveIgnore implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		long ignore = p.stream.readQWord();
		p.ignores.remove(ignore);
	}

}
