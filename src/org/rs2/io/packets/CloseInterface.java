package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;

public class CloseInterface implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		if (p.interfaceId == 3323 || p.interfaceId == 3443) {
			p.trade.decline();
		}
		if (p.interfaceId == 7424) {
			//p.bankPin.cancel();
		}
		p.frames.closeAllInterfaces();
	}

}
