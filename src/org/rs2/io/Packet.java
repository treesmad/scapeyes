package org.rs2.io;

import org.rs2.model.players.Player;

public interface Packet {

	/**
	 * The template used for all packets.
	 * @param p Player to call the packet for.
	 * @param id Packet Id/type.
	 * @param size Packet size.
	 */
	public void packet(Player p, int id, int size);

}
