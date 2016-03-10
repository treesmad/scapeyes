package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

public class ObjectOption3 implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		if (p.isBusy() || p.disguiseId != -1) {
			return;
		}
		if (!p.destinationReached()) {
			return;
		}
	}

}
