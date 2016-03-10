package org.rs2.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.rs2.model.players.Player;

public class PlayerSocket {
	/**
	 * Player this socket belongs to.
	 */
	public Player player;
	/**
	 * Socket used for receiving and sending bytes.
	 */
	public Socket socket;
	/**
	 * InputStream for receiving.
	 */
	public InputStream input;
	/**
	 * OutputStream for sending.
	 */
	public OutputStream output;

	/**
	 * Constructs a new PlayerSocket
	 * @param p Player to create for
	 * @param s Socket this PlayerSocket belongs to
	 */
	public PlayerSocket(Player p, Socket s) {
		player = p;
		socket = s;
		try {
			input = socket.getInputStream();
			output = socket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close socket and input and output streams.
	 */
	public void destruct(Player p) {
		if (socket == null) {
			return;
		}
		p.disconnected = true;

		try {
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.close();
			}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		socket = null;
		p.stream = null;
		p.online = false;

		synchronized (this) {
			notify();
		}
	}

	public void fillStream(Player p, int read) {
		if (p == null) {
			return;
		}
		p.stream.inOffset = 0;

		try {
			input.read(p.stream.inBuffer, 0, read);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(byte[] b, int start, int end) throws Exception {
		if (socket == null) {
			return;
		}
		output.write(b, start, end);
	}

	public void flush() throws Exception {
		if (socket == null) {
			return;
		}
		output.flush();
	}

}
