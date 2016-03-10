package org.rs2.net;

import java.net.Socket;
import org.rs2.model.players.Player;

public class ConnectionManager {
	/**
	 * Array of sockets
	 */
	public static Socket[] sockets = new Socket[2000];

	/**
	 * Close and remove a socket
	 * @param id Socket ID to close
	 */
	public void removeSocket(int id) {
		if (sockets[id] == null) {
			return;
		}
		try {
			sockets[id].close();
		} catch (Exception e) {
		}
		sockets[id] = null;
	}

	/**
	 * Returns a free socket
	 */
	public int getFreeId() {

		for (int i = 1; i < sockets.length; i++) {
			if (sockets[i] != null) {
				continue;
			}
			return i;
		}
		return 0;
	}

	public void writeBuffer(Player p) {
		if (p == null || !p.online || p.disconnected) {
			return;
		}
		try {
			if (p.stream.outOffset > 0) {
				p.socket.write(p.stream.outBuffer, 0, p.stream.outOffset);
			}
			p.stream.outOffset = 0;
			p.socket.flush();
		} catch (Exception e) {
			p.disconnected = true;
		}
	}

}
