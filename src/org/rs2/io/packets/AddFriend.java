package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

/**
 * Sent when attempting to add a person to the player's friends list.
 */
public class AddFriend implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		long friend = p.stream.readQWord();
		p.friends.add(friend);
		p.frames.sendFriend(friend, p.getWorld(friend));
		p.updateFriends();
	}

}
