package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

public class NPCDialogue implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
	}

}
