package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.task.Task;

/**
 * Sent when the player clicks on the "Follow" context menu option for another
 * player.
 */
public class PlayerFollow implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int playerId = p.stream.readSignedWordBigEndian();
		Player op = Engine.players[playerId];

		/**
		 * This is really fucking bad, fix it soon
		 */
		/*Engine.task.schedule(p.walkingToTask = new Task() {
			@Override
			protected void execute() {
				Engine.playerMovement.findRoute(p, op.absX, op.absY, false, 1, 1);
			}
		});*/
	}

}
