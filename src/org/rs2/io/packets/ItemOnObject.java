package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.world.cache.region.Region;

public class ItemOnObject implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int interfaceId = p.stream.readUnsignedWord();
		int objectId = p.stream.readSignedWordBigEndian();
		int objectY = p.stream.readSignedWordBigEndianA();
		int itemSlot = p.stream.readSignedWordBigEndian();
		int objectX = p.stream.readSignedWordBigEndianA();
		int itemId = p.stream.readUnsignedWord();
		
		if (!Region.objectExists(objectId, objectX, objectY, p.heightLevel)) {
			return;
		}
		if (p.inventory.getId(itemSlot) != itemId) {
			return;
		}
		/*Engine.task.schedule(p.walkingToTask = new Task(true) {
			@Override
			protected void execute() {
				if (p.distanceToPoint(objectX, objectY) > 1) {
					return;
				}
				p.requestFaceDir(objectX, objectY);
				stop();
				
				if (objectId == 2213 || objectId == 9398) {

					p.requestAnim(881);
					p.bank.deposit(p.inventory, itemSlot, p.inventory.getAmt(itemSlot));
					return;
				}
				p.frames.sendMessage("Nothing interesting happens.");
			}
			
		});*/
	}

}
