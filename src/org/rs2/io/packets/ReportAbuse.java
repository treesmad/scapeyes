package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.util.TextUtils;

public class ReportAbuse implements Packet {

	private String[] ruleNames = { "Offensive language", "Item scamming", "Password scamming", "Bug abuse",
		"Jagex staff impersonation", "Account sharing/trading", "Macroing", "Multiple logging in",
		"Encouraging others to break rules", "Misuse of customer support", "Advertising / website",
		"Real world item trading" };

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		String name = TextUtils.longToPlayerName(p.stream.readQWord()).replaceAll("_", " ");
		int rule = p.stream.readUnsignedByte();
		boolean mute = p.stream.readUnsignedByte() == 1;

		if (name.equalsIgnoreCase(p.playerName)) {
			p.frames.sendMessage("You can't report yourself!");
			return;
		}
		p.frames.sendMessage(TextUtils.capitalize(name) + " reported for " + ruleNames[rule] + " - Mute for 48 hours: "
			+ mute);
	}

}
