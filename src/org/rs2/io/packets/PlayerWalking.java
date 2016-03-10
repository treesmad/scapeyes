package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.model.players.containers.Trade;
import org.rs2.util.Misc;

/**
 * Player walking. Sent when walking via the map, regular walking, and walking
 * on command.
 */
public class PlayerWalking implements Packet {

	private static final int WALK_MAP = 248;

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		if (p.isBusy()) {
			if (p.walkingToTask != null && p.walkingToTask.isRunning()) {
				p.walkingToTask.stop();
			}
			p.frames.resetDestination();
			return;
		}
		if (p.disguiseId != -1) {
			p.resetDisguise();
		}
		if (p.trade.state != Trade.State.NOT_TRADING) {
			//p.trade.decline(Engine.players[p.tradeId]);
		}
		if (p.interfaceId == 7424 || p.interfaceId == 14924) {
			p.bankPin.cancel();
		}
		p.resetActionTask();
		p.frames.closeAllInterfaces();
		p.frames.sendWalkableInterface(-1);
		p.requestFaceNPC(-1);

		if (id == WALK_MAP) {
			size -= 14;
		}
		p.newWalkCmdSteps = size - 5;

		if (p.newWalkCmdSteps % 2 != 0) {
			System.out.println("Warning: walkTo(" + id + ") command malformed: "
				+ Misc.hex(p.stream.outBuffer, 0, size));
		}
		p.newWalkCmdSteps /= 2;

		if (++p.newWalkCmdSteps > Player.WALKING_QUEUE_SIZE) {
			System.out.println("Warning: walkTo(" + id + ") command contains too many steps ("
				+ p.newWalkCmdSteps + ").");
			p.newWalkCmdSteps = 0;
			return;
		}
		int firstStepX = p.stream.readSignedWordBigEndianA();
		firstStepX -= p.mapRegionX * 8;

		for (int i = 1; i < p.newWalkCmdSteps; i++) {
			p.newWalkCmdX[i] = p.stream.readSignedByte();
			p.newWalkCmdY[i] = p.stream.readSignedByte();
		}
		p.newWalkCmdX[0] = 0;
		p.newWalkCmdY[0] = 0;

		int firstStepY = p.stream.readSignedWordBigEndian();
		firstStepY -= p.mapRegionY * 8;

		p.newWalkCmdRunning = p.stream.readSignedByteC() == 1;

		for (int i = 0; i < p.newWalkCmdSteps; i++) {
			p.newWalkCmdX[i] += firstStepX;
			p.newWalkCmdY[i] += firstStepY;
		}
		p.poimiX = firstStepX;
		p.poimiY = firstStepY;

		if (p.newWalkCmdSteps > 0) {
			p.newWalkDestX = p.newWalkCmdX[p.newWalkCmdSteps - 1];
			p.newWalkDestY = p.newWalkCmdY[p.newWalkCmdSteps - 1];
		}
	}

}
