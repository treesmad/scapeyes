package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.model.players.skills.Firemaking.Logs;
import org.rs2.task.Task;
import org.rs2.world.Ground;

public class ItemLight implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int itemX = p.stream.readSignedWordBigEndian();
		int itemY = p.stream.readSignedWordBigEndianA();
		int itemId = p.stream.readUnsignedWordA();

		if (p.isBusy()) {
			return;
		}
		p.resetActionTask();

		/*Engine.task.schedule(p.walkingToTask = new Task(true) {

			@Override
			protected void execute() {
				if (p == null || !p.online || p.disconnected) {
					stop();
					return;
				}
				if (p.distanceToPoint(itemX, itemY) != 0 || p.walkDir != -1) {
					return;
				}
				for (Logs l : Logs.values()) {
					if (p.inventory.contains(590)) {
						if (itemId == l.getId()) {
							p.firemaking.start(itemId);
							break;
						}
					} else {
						p.frames.sendMessage("You need a tinderbox to light a fire.");
						break;
					}
				}
				stop();
				p.walkingToTask = null;
			}
		});*/
	}

}
