package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.task.Task;

/**
 * Sent when answering a chatbox trade request.
 */
public class TradeAnswer implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int playerId = p.stream.readSignedWordBigEndian();
		Player op = Engine.players[playerId];

		if (p.isBusy()) {
			return;
		}
		/*if (p.tradeId != -1) {
			return;
		}
		if (p.distanceToPoint(op.absX, op.absY) == 1) {
			p.requestFaceDir(op.absX, op.absY);
		} else {
			p.requestFacePlayer(playerId);
		}
		Engine.task.schedule(p.walkingToTask = new Task(true) {
			@Override
			protected void execute() {
				if (p.distanceToPoint(op.absX, op.absY) > 1) {
					return;
				}
				op.requestFaceDir(p.absX, p.absY);
				p.requestFacePlayer(-1);

				if (op.interfaceId != -1 && op.interfaceId != 3323) {
					p.frames.sendMessage("Player is busy at the moment.");
					stop();
					return;
				}
				p.tradeId = playerId;

				if (op.tradeId == p.playerId) {
					p.trade.start();
				} else {
					p.frames.sendMessage("Sending trade request...");
					op.frames.sendMessage(p.playerName + ":tradereq:");
				}
				stop();
			}
		});*/
	}

}
