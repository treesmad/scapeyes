package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.util.Skills;
import org.rs2.world.cache.region.Region;

public class ObjectOption2 implements Packet {

	@Override
	public void packet(final Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		final int objectId = p.stream.readUnsignedWordBigEndianA();
		final int objectY = p.stream.readSignedWordBigEndian();
		final int objectX = p.stream.readUnsignedWordA();
		final int objectRot = Region.getRotation(objectX, objectY, p.heightLevel);

		if (!Region.objectExists(objectId, objectX, objectY, p.heightLevel)) {
			return;
		}
		if (p.isBusy() || p.disguiseId != -1) {
			return;
		}
		p.frames.closeAllInterfaces();

		if (p.destinationReached()) {
			performAction(p, objectId, objectX, objectY, objectRot);
		} else {
			Engine.task.schedule(p.walkingToTask = new Task(true) {
				protected void execute() {
					if (p.destinationReached()) {
						performAction(p, objectId, objectX, objectY, objectRot);
						stop();
						p.walkingToTask = null;
					}
				}
			});
		}
	}
	
	public void performAction(Player p, int objectId, int objectX, int objectY, int objectRot) {
		p.requestFaceDir(objectX, objectY);
		
		if (!Region.withinRange(objectId, objectX, objectY, p.absX, p.absY, p.heightLevel)) {
			p.frames.sendMessage("You cannot reach that.");
			return;
		}
		switch (objectId) {
				/**
				 * Dummies
				 */
				case 823:
					if (p.lastDummyHit == 0 || Engine.currentTicks() - p.lastDummyHit >= 3) {
						p.addSkillXP(Skills.ATTACK, 120 * Math.pow(p.getLevelForXP(Skills.ATTACK), 0.6));
						p.requestAnim(422, 0);
						p.frames.sendMessage("You hit the dummy!");
						p.lastDummyHit = Engine.currentTicks();
					}
					break;

				/**
				 * Bank booths
				 */
				case 2213:
				case 11758:
					p.frames.sendBank();
					break;

				/**
				 * Fish stall.
				 */
				case 4277: {
					/*int[][] items = { { 391, 5 },		// Manta ray
						{ 397, 5 },		// Sea turtle
						{ 385, 15 },	// Shark
						{ 7946, 20 },	// Monkfish
						{ 379, 30 }		// Lobster
					};
					int item = -1;

					for (int i = 0; i < items.length; i++) {
						if (Misc.random(100) < items[i][1]) {
							item = items[i][0];
							break;
						} else {
							item = 333;
						}
					}
					if (p.item.addItem(item, 1)) {
						p.requestAnim(881);
						p.addSkillXP(Skills.THIEVING, 2400);
						p.frames.sendMessage("You managed to steal a " + ItemDef.getName(item).toLowerCase() + ".");
					} else {
						p.frames.sendMessage("Your inventory is too full to steal any more.");
					}*/
					break;
				}
			}
	}

}
