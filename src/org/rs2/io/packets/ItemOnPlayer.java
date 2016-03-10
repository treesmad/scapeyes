package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.util.Misc;

public class ItemOnPlayer implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int interfaceId = p.stream.readSignedWordA();
		int playerId = p.stream.readUnsignedWord();
		int itemId = p.stream.readUnsignedWord();
		int itemSlot = p.stream.readSignedWordBigEndian();
		Player p2 = Engine.players[playerId];

		if (p.isBusy() || p2.dead()) {
			return;
		}
		p.resetActionTask();
		p.requestFacePlayer(p2.playerId);

		/*Engine.task.schedule(p.walkingToTask = new Task(true) {
			@Override
			protected void execute() {
				if (p == null || !p.online || p.disconnected || p2 == null || !p2.online
					|| p2.disconnected) {
					stop();
					return;
				}
				if (p.distanceToPoint(p2.absX, p2.absY) > 1) {
					return;
				}
				if (itemId == 962) {
					int[] items = { 1038, 1040, 1042, 1044, 1046, 1048 };
					int[][] bonuses = { { 1973, 1 }, { 2355, 1 }, { 1969, 1 }, { 441, 5 },
						{ 1897, 1 }, { 1718, 1 }, { 950, 1 }, { 1635, 1 }, { 1217, 1 }, { 6434, 1 } };

					int item = items[Misc.random(items.length - 1)];
					int bonusItem = Misc.random(bonuses.length - 1);

					p2.requestFacePlayer(p.playerId);

					p.frames.sendMessage("You pull a cracker...");
					Engine.task.schedule(new Task(true) {
						int ticks;

						@Override
						protected void execute() {
							ticks++;

							if (ticks < 2) {
								return;
							}
							p.item.deleteItem(itemId, 1);

							Player winner = (Misc.random(100) < 50) ? p : p2;
							winner.requestForcedChat("Hey! I got the cracker!");
							winner.item.addItemDrop(item, 1);
							winner.item.addItemDrop(bonuses[bonusItem][0], bonuses[bonusItem][1]);
							
							if (winner == p2) {
								p.frames
									.sendMessage("The person you pulled the cracker with gets the prize.");
							}
							
							p.requestFacePlayer(-1);
							p2.requestFacePlayer(-1);
							this.stop();
						}
					});
				} else {
					p.frames.sendMessage("Nothing interesting happens.");
				}
				stop();
			}
		});*/
	}

}
