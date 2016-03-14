package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.content.DialogueOption;
import org.rs2.model.content.DialogueOption.DialogueButton;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.util.Skills;
import org.rs2.world.cache.region.Region;

public class ObjectOption1 implements Packet {

	@Override
	public void packet(final Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		final int objectX = p.stream.readSignedWordBigEndianA();
		final int objectId = p.stream.readUnsignedWord();
		final int objectY = p.stream.readUnsignedWordA();

		if (!Region.objectExists(objectId, objectX, objectY, p.heightLevel)) {
			return;
		}
		if (p.isBusy()) {
			return;
		}
		p.frames.closeAllInterfaces();

		if (p.destinationReached()) {
			performAction(p, objectId, objectX, objectY);
		} else {
			Engine.task.schedule(p.walkingToTask = new Task(true) {
				protected void execute() {
					if (p.destinationReached()) {
						performAction(p, objectId, objectX, objectY);
						stop();
						p.walkingToTask = null;
					}
				}
			});
		}

	}

	private void performAction(Player p, int objectId, int objectX, int objectY) {
		int objectRot = Region.getRotation(objectX, objectY, p.heightLevel);
		System.out.println("object rot: " + objectRot);

		p.requestFaceDir(objectX, objectY);

		if (!Region.withinRange(objectId, objectX, objectY, p.absX, p.absY, p.heightLevel)) {
			p.frames.sendMessage("You cannot reach that.");
			return;
		}
		switch (objectId) {
			case 299:	// Dummies
				if (p.lastDummyHit == 0 || Engine.currentTicks() - p.lastDummyHit >= 6) {
					p.addSkillXP(Skills.DEFENCE,
						240 * Math.pow(p.getLevelForXP(Skills.DEFENCE), 0.6));
					p.requestAnim(645, 0);
					p.frames.sendMessage("You search the bail of hay...");
					p.lastDummyHit = Engine.currentTicks();
				}
				break;

			case 354:	// Crates
			case 355:
			case 356:
			case 357:
			case 358:
			case 360:
			case 361:
			case 3685:
			case 3686:
				p.frames.sendMessage("You search the crate but find nothing.");
				break;

			case 1276:	// Trees
			case 1278:
			case 1286:
			case 1281:
			case 1308:
			case 1309:
				//p.woodcutting.start(objectId, objectX, objectY, objectRot);
				break;

			case 2090:	// Rocks
			case 2094:
				//p.mining.start(objectId, objectX, objectY, objectRot);
				break;

			case 2213:	// Bank booths
			case 3192:	// Bank chests
			case 3193:
			case 11758:
				p.dialogue.send(new DialogueOption()
					.add(new DialogueButton("I'd like to access my bank account, please.") {
						public void action() {
							p.frames.sendBank();
						}
					})
					.add(new DialogueButton("I'd like to check my PIN settings.", true) {
						public void action() {
							p.frames.sendMessage("Coming soon (tm)");
						}
					})
				);
				
					
				break;

			case 4483:
				p.frames.sendBank();
				break;

			case 9398:
				p.frames.sendDepositBox();
				break;

			/**
			 * Training objects
			 */
			case 1531:
				if (p.lastDummyHit == 0 || Engine.currentTicks() - p.lastDummyHit >= 3) {
					p.addSkillXP(Skills.STRENGTH,
						120 * Math.pow(p.getLevelForXP(Skills.STRENGTH), 0.6));
					p.requestAnim(422, 0);
					p.frames.sendMessage("You slam the door!");
					p.lastDummyHit = Engine.currentTicks();
				}
				break;

			case 1738:
				if (objectRot == 1) {
					p.teleport(objectX, objectY - 1, p.heightLevel + 1);
				}
				break;

			case 1740:
				if (objectRot == 1) {
					p.teleport(objectX - 1, objectY, p.heightLevel - 1);
				}
				break;
				
			/**
			 * Stairs
			 */
			case 1726:
				p.teleport(p.absX - 4, p.absY, p.heightLevel - 1);
				break;

			case 1725:
				p.teleport(p.absX + 4, p.absY, p.heightLevel + 1);
				break;
				

			/**
			 * Ladders
			 */
			case 1746:
				p.requestAnim(827);
				p.busy = true;

				Engine.task.schedule(new Task(2) {
					@Override
					protected void execute() {
						p.teleport(p.absX, p.absY, p.heightLevel - 1);
						stop();
						p.busy = false;
					}
				});
				break;

			case 1747:
				p.requestAnim(828);
				p.busy = true;

				Engine.task.schedule(new Task(2) {
					@Override
					protected void execute() {
						p.teleport(objectX - 1, objectY, p.heightLevel + 1);
						stop();
						p.busy = false;
					}
				});
				break;
				
			case 1749:
				p.requestAnim(827);
				p.busy = true;

				Engine.task.schedule(new Task(2) {
					@Override
					protected void execute() {
						p.teleport(p.absX, p.absY, p.heightLevel - 1);
						stop();
						p.busy = false;
					}
				});
				break;
				
			case 1750:
				p.requestAnim(828);
				p.busy = true;

				Engine.task.schedule(new Task(2) {
					@Override
					protected void execute() {
						p.teleport(objectX, objectY + 1, p.heightLevel + 1);
						stop();
						p.busy = false;
					}
				});
				break;
		}
	}

}
