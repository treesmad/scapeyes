package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.util.TextUtils;

public class PrivateMessage implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		long friend = p.stream.readQWord();
		byte chatText[] = new byte[100];
		int chatTextSize = (byte) (size - 8);
		p.stream.readBytes(chatText, chatTextSize, 0);

		if (p.getWorld(friend) == 0) {
			p.frames.sendMessage("That player is currently offline.");
			return;
		}
		for (Player op : Engine.players) {
			if (op == null || !op.online) {
				continue;
			}
			if (friend == TextUtils.stringToLong(op.playerName)) {
				op.stream.createFrameVarSize(196);
				op.stream.writeQWord(TextUtils.stringToLong(p.playerName));
				op.stream.writeDWord(op.privateMessageId++);
				op.stream.writeByte(p.rights);
				op.stream.writeBytes(chatText, chatTextSize, 0);
				op.stream.endFrameVarSize();
				break;
			}
		}
	}

}
