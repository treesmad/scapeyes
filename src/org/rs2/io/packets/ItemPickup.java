package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.items.Item;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.util.Sounds;

public class ItemPickup implements Packet {

	@Override
	public void packet(final Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		final int itemY = p.stream.readSignedWordBigEndian();
		final int itemId = p.stream.readUnsignedWord();
		final int itemX = p.stream.readSignedWordBigEndian();
		final int itemAmount = Engine.ground.getItemAmount(itemId, itemX, itemY, p.heightLevel);

		if (!Engine.ground.itemExists(itemId, itemX, itemY, p.heightLevel)) {
			return;
		}
		if (p.isBusy()) {
			return;
		}
		Engine.task.schedule(p.walkingToTask = new Task(true) {

			@Override
			protected void execute() {
				if (p == null || !p.online || p.disconnected) {
					stop();
					return;
				}
				if (!Engine.ground.itemExists(itemId, itemX, itemY, p.heightLevel)) {
					p.requestWalk(0, 0);
					stop();
					return;
				}
				if (p.destinationReached()) {
					if (Engine.ground.itemExists(itemId, itemX, itemY, p.heightLevel)) {
						if (p.inventory.add(new Item(itemId, itemAmount))) {
							Engine.ground.removeItem(itemId, itemX, itemY, p.heightLevel);
							Sounds.play(p, Sounds.PICKUP, 0, 10);
						} else {
							p.frames.sendMessage("You don't have enough inventory space to hold that item.");
						}
					} else {
						p.requestWalk(0, 0);
					}
					stop();
				}
			}
		});
	}

}
