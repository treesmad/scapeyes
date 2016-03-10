package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

/**
 * Called when player accepts the character design screen and applies player
 * looks.
 */
public class PlayerDesign implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		p.gender = p.stream.readSignedByte();

		for (int i = 0; i < p.looks.length; i++) {
			p.looks[i] = p.stream.readSignedByte();
		}
		for (int i = 0; i < p.colors.length; i++) {
			p.colors[i] = p.stream.readSignedByte();
		}
		p.updateReq = true;
		p.appearanceUpdateReq = true;
	}

}