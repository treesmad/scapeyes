package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.model.players.skills.Firemaking.Logs;
import org.rs2.task.Task;
import org.rs2.world.Ground;

/**
 * Handles using an item from your inventory with an item on the ground.
 */
public class ItemOnGroundItem implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int interfaceId = p.stream.readSignedWordBigEndian();
		int itemId = p.stream.readSignedWordA();
		int groundId = p.stream.readUnsignedWord();
		int groundY = p.stream.readSignedWordA();
		int itemSlot = p.stream.readSignedWordBigEndianA();
		int groundX = p.stream.readUnsignedWord();
		//int groundAmount = Ground.getItemAmount(groundId, groundX, groundY);

		if (p.inventory.getId(itemSlot) != itemId) {
			return;
		}
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
				if (p.distanceToPoint(groundX, groundY) != 0 || p.walkDir != -1) {
					return;
				}
				if (itemId == 590 && Logs.isLog(groundId)) {
					p.firemaking.start(groundId);
				}
				stop();
			}
		});*/
	}

}
