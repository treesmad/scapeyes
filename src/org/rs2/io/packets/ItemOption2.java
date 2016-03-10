package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.content.DialogueOption;
import org.rs2.model.players.Player;
import org.rs2.task.Task;

public class ItemOption2 implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int itemId = p.stream.readSignedWordA();
		int itemSlot = p.stream.readSignedWordBigEndianA();
		int interfaceId = p.stream.readSignedWordBigEndianA();

		if (p.inventory.getId(itemSlot) != itemId) {
			return;
		}
		if (p.isBusy()) {
			return;
		}
		if (itemId == 3842) {
			/*p.dialogue.sendOptions(new DialogueEvent()
				.add(new DialogueButton("Wedding rights", true) {
					public void click() {
						if (!p.drainSpecial(25)) {
							p.frames.sendMessage("You do not have enough Special Energy to do that.");
							p.frames.closeAllInterfaces();
							return;
						}
						Engine.task.schedule(p.preachingTask = new Task(2, true) {
							int ticks;

							@Override
							protected void execute() {
								if (ticks == 0) {
									p.requestForcedChat("Two great warriors, joined by hand");
								}
								if (ticks == 2) {
									p.requestForcedChat("to spread destruction across the land.");
								}
								if (ticks == 4) {
									p.requestForcedChat("In Zamorak's name, now two are one.");
								}
								if (ticks == 6) {
									stop();
									p.preachingTask = null;
								}
								p.requestAnim(1336);
								ticks++;
							}
						});
					}
				})
				.add(new DialogueButton("Last rights", true) {
					public void click() {
						if (!p.drainSpecial(25)) {
							p.frames.sendMessage("You do not have enough Special Energy to do that.");
							p.frames.closeAllInterfaces();
							return;
						}
						Engine.task.schedule(p.preachingTask = new Task(2, true) {
							int ticks;

							@Override
							protected void execute() {

								if (ticks == 0) {
									p.requestForcedChat("The weak deserve to die,");
								}
								if (ticks == 2) {
									p.requestForcedChat("so the strong may flourish.");
								}
								if (ticks == 4) {
									p.requestForcedChat("This is the will of Zamorak.");
								}
								if (ticks == 6) {
									stop();
									p.preachingTask = null;
								}
								p.requestAnim(1336);
								ticks++;
							}
						});
					}
				})
				.add(new DialogueButton("Blessing", true) {
					public void click() {
						if (!p.drainSpecial(25)) {
							p.frames.sendMessage("You do not have enough Special Energy to do that.");
							p.frames.closeAllInterfaces();
							return;
						}
						Engine.task.schedule(p.preachingTask = new Task(2, true) {
							int ticks;

							@Override
							protected void execute() {

								if (ticks == 0) {
									p.requestForcedChat("May your bloodthirst never be sated,");
								}
								if (ticks == 2) {
									p.requestForcedChat("and may all your battles be glorious.");
								}
								if (ticks == 4) {
									p.requestForcedChat("Zamorak bring you strength.");
								}
								if (ticks == 6) {
									stop();
									p.preachingTask = null;
								}
								p.requestAnim(1336);
								ticks++;
							}
						});
					}
				})
				.add(new DialogueButton("Preach", true) {
					public void click() {
						if (!p.drainSpecial(25)) {
							p.frames.sendMessage("You do not have enough Special Energy to do that.");
							p.frames.closeAllInterfaces();
							return;
						}
						Engine.task.schedule(p.preachingTask = new Task(2, true) {
							int ticks;

							@Override
							protected void execute() {
								if (ticks == 0) {
									p.requestForcedChat("Battles are not lost and won;");
								}
								if (ticks == 2) {
									p.requestForcedChat("They simply remove the weak from the equation.");
								}
								if (ticks == 4) {
									p.requestForcedChat("Zamorak give me strength!");
								}

								if (ticks == 7) {
									p.requestForcedChat("Those who fight, then run away,");
								}
								if (ticks == 9) {
									p.requestForcedChat("shame Zamorak with their cowardice.");
								}
								if (ticks == 11) {
									p.requestForcedChat("Zamorak give me strength!");
								}

								if (ticks == 14) {
									p.requestForcedChat("Battle is by those who choose to disagree with it.");
								}
								if (ticks == 16) {
									p.requestForcedChat("Zamorak give me strength!");
								}

								if (ticks == 19) {
									p.requestForcedChat("Strike fast, strike hard, strike true:");
								}
								if (ticks == 21) {
									p.requestForcedChat("The strength of Zamorak will be with you.");
								}
								if (ticks == 23) {
									p.requestForcedChat("Zamorak give me strength!");
								}
								if (ticks == 25) {
									stop();
									p.preachingTask = null;
								}
								p.requestAnim(1336);
								ticks++;
							}
						});
					}
				})
			);*/
			return;
		}
	}

}
