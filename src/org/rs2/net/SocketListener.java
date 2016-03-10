package org.rs2.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.rs2.Server;
import org.rs2.model.players.Player;

public class SocketListener extends ConnectionManager {
	/**
	 * Server socket.
	 */
	private ServerSocket serverSocket;
	/**
	 * Set to true if the listener should run.
	 */
	private boolean listening = true;
	/**
	 * List of currently connected IP addresses.
	 */
	public ArrayList<String> connectedIPs = new ArrayList<String>();

	/**
	 * Constructs a new SocketListener.
	 */
	public SocketListener(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Server initialized on port " + port);
	}

	/**
	 * Listen for incoming connections and accept them.
	 */
	public void run() {
		Socket socket;

		while (listening) {
			try {
				socket = serverSocket.accept();
				socket.setTcpNoDelay(true);

				int id = getFreeId();
				sockets[id] = socket;
				Server.engine.addConnection(socket, id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
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
