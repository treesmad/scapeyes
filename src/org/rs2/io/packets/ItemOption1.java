package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;
import org.rs2.model.items.types.Bones;
import org.rs2.model.items.types.Food;
import org.rs2.model.items.types.Potions;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.util.Skills;
import org.rs2.util.Sounds;

/**
 * Handles the first item option (eat, drink, etc).
 */
public class ItemOption1 implements Packet {
	@Override
	public void packet(final Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int interfaceId = p.stream.readSignedWordBigEndianA();
		int itemSlot = p.stream.readUnsignedWordA();
		int itemId = p.stream.readUnsignedWordBigEndian();

		if (p.inventory.getId(itemSlot) != itemId) {
			return;
		}
		if (p.isBusy()) {
			return;
		}
		for (Food food : Food.values()) {
			if (Food.isFood(food, itemId)) {
				if (Engine.currentTicks() - p.lastTickFood >= 3) {
					p.updateHP(food.getHeal(), true);
					p.requestAnim(829, 0);
					Sounds.play(p, Sounds.EAT_FOOD, 0, 10);
					p.inventory.remove(itemSlot, 1);

					if (itemId == food.getId() && food.getId2() != -1) {
						p.inventory.add(new Item(food.getId2()));
					}
					if (itemId == food.getId2() && food.getId3() != -1) {
						p.inventory.add(new Item(food.getId3()));
					}
					if (food.getEffect() != null) {
						food.getEffect().execute(p);
					}
					String name = ItemDef.getName(itemId).toLowerCase();

					if (food.name().toLowerCase().contains("cake")) {
						if (name.contains("2/3")) {
							p.frames.sendMessage("You eat some more of the "
								+ food.name().toLowerCase().replaceAll("_", " ") + ".");
						} else if (name.contains("slice")) {
							p.frames.sendMessage("You eat the slice of "
								+ food.name().toLowerCase().replaceAll("_", " ") + ".");
						} else {
							p.frames.sendMessage("You eat part of the "
								+ food.name().toLowerCase().replaceAll("_", " ") + ".");
						}
					} else if (food.name().toLowerCase().contains("pie")
						|| food.name().toLowerCase().contains("pizza")) {
						if (name.toLowerCase().contains("1/2") || name.contains("half")) {
							p.frames.sendMessage("You eat the remaining "
								+ name.replace("1/2 ", "").replace("half a ", "") + ".");
						} else {
							p.frames.sendMessage("You eat half of the " + name + ".");
						}
					} else {
						p.frames.sendMessage("You eat the " + name + ".");
					}
					p.frames.sendMessage("It heals some health.");

					p.lastTickFood = Engine.currentTicks();
					break;
				}
				break;
			}
		}

		if (Potions.isPotion(itemId)) {
			if (Engine.currentTicks() - p.lastTickFood >= 3) {
				Potions potion = Potions.forId(itemId);

				if (itemId == potion.getId(3)) {
					p.inventory.replace(potion.getId(2), itemSlot);
				} else if (itemId == potion.getId(2)) {
					p.inventory.replace(potion.getId(1), itemSlot);
				} else if (itemId == potion.getId(1)) {
					p.inventory.replace(potion.getId(0), itemSlot);
				} else if (itemId == potion.getId(0)) {
					p.inventory.replace(229, itemSlot);
				}
				p.requestAnim(829, 0);
				Sounds.play(p, Sounds.DRINK_POTION, 0, 10);
				potion.getEffect().execute(p);
				
				String itemName = ItemDef.getName(itemId);

				p.frames.sendMessage("You drink some of your " + itemName.substring(0, itemName.indexOf("(")).toLowerCase() + ".");
				
				int dose = Integer.parseInt(itemName.substring(itemName.indexOf("(") + 1, itemName.indexOf(")"))) - 1;
				p.frames.sendMessage((dose > 0) ? "You have " + dose + " doses of potion left." : "You have finished your potion.");

				p.lastTickFood = Engine.currentTicks();
			}
			return;
		}

		for (final Bones bone : Bones.values()) {
			if (itemId == bone.getId()) {
				if (Engine.currentTicks() - p.lastTickBones >= 3) {
					p.frames.sendMessage("You dig a hole in the ground...");
					p.requestAnim(827);
					p.requestWalk(0, 0);
					p.inventory.remove(itemSlot);

					Engine.task.schedule(p.boneBuryTask = new Task(true) {
						int ticks;

						@Override
						public void execute() {
							if (p == null || !p.online || p.disconnected) {
								stop();
								p.boneBuryTask = null;
								return;
							}
							if (ticks == 1) {
								Sounds.play(p, Sounds.BURY_BONES, 0, 10);
								p.frames.sendMessage("You bury the bones.");
								p.addSkillXP(Skills.PRAYER, bone.getXP());
								stop();
								p.boneBuryTask = null;
							}
							ticks++;
						}
					});
					p.lastTickBones = Engine.currentTicks();
					break;
				}
				return;
			}
		}

		if (itemId == 550) {		// Newcomer map.
			int config = (p.absX / 64 - 46) + (p.absY / 64 - 49) * 6;
			System.out.println(config);
			if (config > 20 && config < 36) {
				p.frames.setString("Northwest Runescape", 5394);
			} else if (config > 0 && config < 23) {
				p.frames.setString("Southwest Runescape", 5394);
			}
			p.frames.setConfig(106, config);
			p.frames.sendInterface(5392);
			return;
		}

		if (itemId == 33) {			// Extinguish candle.
			p.inventory.replace(36, itemSlot);
		}

		if (itemId == 3801) {		// Keg
			if (Engine.currentTicks() - p.lastTickFood >= 3) {
				if (p.kegTask != null && p.kegTask.isRunning()) {
					return;
				}
				p.inventory.remove(itemSlot);
				p.requestAnim(1329, 0);
				p.requestWalk(0, 0);

				Engine.task.schedule(p.kegTask = new Task(true) {
					int count;

					@Override
					public void execute() {
						if (p == null || !p.online || p.disconnected) {
							stop();
						}
						count++;
						p.resetRun();

						if (count == 2) {
							p.frames.sendMessage("You feel reinvigorated...");
						}
						if (count == 5) {
							p.frames.sendMessage("...but extremely drunk, too.");
							p.walkAnim = 2769;
							p.standAnim = 3040;
							p.appearanceUpdateReq = true;
							p.updateReq = true;
						}
						if (count == 60) {
							p.equipment.sendStances();
							stop();
							p.kegTask = null;
						}
					}
				});
				p.lastTickFood = Engine.currentTicks();

				/*p.item.deleteItem(itemId, 1, itemSlot);
				p.requestAnim(1329, 0);
				p.requestWalk(0, 0);

				Engine.task.schedule(p.kegTask = new Task(true) {
					int count;

					@Override
					public void execute() {
						if (p == null || !p.online || p.disconnected) {
							stop();
						}
						count++;
						p.resetRun();

						if (count == 2) {
							p.frames.sendMessage("You feel reinvigorated...");
						}
						if (count == 5) {
							p.frames.sendMessage("...but extremely drunk, too.");
							p.walkAnim = 2769;
							p.standAnim = 3040;
							p.appearanceUpdateReq = true;
							p.updateReq = true;
						}
						if (count == 60) {
							p.equip.getStances(p.equipment[3]);
							stop();
							p.kegTask = null;
						}
					}
				});
				p.lastTickFood = Engine.currentTicks();*/
			}
			return;
		}
	}
}
