package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

/**
 * Sent when a player types into the chatbox and presses enter.
 */
public class PlayerChat implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		p.chatTextEffects = p.stream.readUnsignedByteS();
		p.chatTextColor = p.stream.readUnsignedByteS();
		p.chatTextSize = (byte) (size - 2);
		p.stream.readBytes_reverseA(p.chatText, p.chatTextSize, 0);
		p.chatTextUpdateReq = true;
		p.updateReq = true;
	}

}
