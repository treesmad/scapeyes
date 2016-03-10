package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

/**
 * Remove a friend from the friends list.
 */
public class RemoveFriend implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		long friend = p.stream.readQWord();
		p.friends.remove(friend);
		p.updateFriends();
	}

}
