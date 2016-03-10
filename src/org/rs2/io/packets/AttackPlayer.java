package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;

public class AttackPlayer implements Packet {

	@Override
	public void packet(final Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int playerId = p.stream.readUnsignedWord();
		Player p2 = Engine.players[playerId];
		
		if (p.isBusy()) {
			return;
		}
		if (playerId < 1 || playerId >= Engine.players.length || Engine.players[playerId] == null) {
			return;
		}
		p.requestFaceNPC(32768 + p2.playerId);
	}

}
