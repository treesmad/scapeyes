package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

/**
 * Sent when the player changes their privacy options.
 */
public class PlayerPrivacyOptions implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		p.chatPublic = p.stream.readUnsignedByte();
		p.chatPrivate = p.stream.readUnsignedByte();
		p.chatTrade = p.stream.readUnsignedByte();
		p.updateFriends();
	}

}
