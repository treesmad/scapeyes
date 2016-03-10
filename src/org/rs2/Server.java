package org.rs2;

import org.rs2.net.SocketListener;

public class Server {

	/**
	 * Engine for player and NPC updating.
	 */
	public static Engine engine;

	/**
	 * Socket listener to listen for incoming connections.
	 */
	public static SocketListener socketListener;

	/**
	 * Main method for running the server.
	 * @param args Port to run the server on.
	 */
	public static void main(String[] args) {
		try {
			socketListener = new SocketListener(Integer.parseInt(args[0]));
		} catch (Exception e) {
			return;
		}
		engine = new Engine();
		socketListener.run();
	}

}
