package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.world.cache.region.Region;

public class MagicOnGroundItem implements Packet {

	/**
	 * Called when player casts a spell on a ground item (telekinetic grab).
	 */
	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		final int itemY = p.stream.readSignedWordBigEndian();
		final int itemId = p.stream.readUnsignedWord();
		final int itemX = p.stream.readSignedWordBigEndian();
		final int spellId = p.stream.readUnsignedWordA();
		//final int itemAmount = Ground.getItemAmount(itemId, itemX, itemY);
		
		if (p.isBusy()) {
			return;
		}
		/*if (!DroppedItem.exists(itemId, itemAmount, itemX, itemY, p.heightLevel)
			&& !SpawnedItem.exists(itemId, itemAmount, itemX, itemY, p.heightLevel)) {
			return;
		}*/
		System.out.println(spellId);
		
		/*Engine.task.schedule(p.walkingToTask = new Task(true) {
			@Override
			protected void execute() {
				if (p == null || !p.online || p.disconnected) {
					stop();
					return;
				}
				if (Region.blockedShot(itemX, itemY, p.heightLevel, p.absX, p.absY)) {
					return;
				}
				if (spellId == 1168) {
					if (p.distanceToPoint(itemX, itemY) > 6) {
						return;
					}
					//DroppedItem.remove(itemId, itemAmount, itemX, itemY, p.heightLevel);
					//p.inventory.add(new Item(itemId, itemAmount));
					p.frames.sendMessage("Nice dick");
					p.requestFaceDir(itemX, itemY);
					p.requestWalk(0, 0);
					stop();
				}
			};
		});*/
	}

}
