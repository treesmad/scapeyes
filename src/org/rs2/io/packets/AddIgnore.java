package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

/**
 * Add person to the ignore list.
 */
public class AddIgnore implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		long ignore = p.stream.readQWord();
		p.ignores.add(ignore);
	}

}
