package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.FileManager;
import org.rs2.io.Packet;
import org.rs2.model.content.DialogueOption;
import org.rs2.model.content.DialogueOption.DialogueButton;
import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;
import org.rs2.model.items.types.Food;
import org.rs2.model.players.Player;
import org.rs2.model.players.Player.MagicBooks;
import org.rs2.model.players.skills.Prayer.Prayers;
import org.rs2.util.Skills;
import org.rs2.util.TextUtils;
import org.rs2.world.Shop;

/**
 * Player commands. These can be accessed by typing "::" followed by one of the
 * commands below (for example "::char").
 */
public class PlayerCommands implements Packet {

	@Override
	public void packet(final Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		String command = p.stream.readString();
		String[] cmd = command.toLowerCase().split(" ");

		if (p.rights < 2) {
			return;
		}

		if (cmd[0].equals("sound")) {
			try {
				p.frames.sendSound(Integer.parseInt(cmd[1]), 0, 10);
				p.frames.sendMessage("Playing sound: @red@" + cmd[1]);
			} catch (NumberFormatException e) {
				p.frames.sendMessage("Cannot play sound, invalid integer.");
			}
		}

		if (cmd[0].equals("ded")) {
			Item[] items = new Item[3];

			if (p.prayer.activePrayers.contains(Prayers.PROTECT_ITEMS)) {
				items = new Item[4];
			}
			for (int i = 0; i < items.length; i++) {
				int highestVal = 0;

				for (int j = 0; j < p.inventory.capacity(); j++) {
					if (ItemDef.getValue(p.inventory.getId(j)) > highestVal) {
						items[i] = p.inventory.copy(j).setAmt(1);
						p.inventory.remove(j, 1);
						highestVal = ItemDef.getValue(p.inventory.getId(j));
					}
				}
			}
			p.inventory.clear();
			p.inventory.add(items);
		}

		if (cmd[0].equals("unequipall")) {
			p.equipment.transferAll(p.inventory);
		}

		if (cmd[0].equals("inspect")) {
			String name = command.substring(command.indexOf(" ") + 1);

			if (cmd.length < 2) {
				p.frames.sendMessage("Invalid syntax.");
				p.frames.sendMessage("Use '::" + cmd[0] + " playername' instead.");
				return;
			}
			for (Player p2 : Engine.players) {
				if (p2 == null) {
					continue;
				}
				if (p2.playerName.equalsIgnoreCase(name)) {
					if (p2 == p) {
						p.frames.sendMessage("Why are you trying to inspect yourself?");
						break;
					}
					p.frames.setItems(5382, p2.bank);
					p.frames.sendInterface(5292);
					return;
				}
			}
			p.frames.sendMessage("User @dre@" + Character.toUpperCase(name.charAt(0))
				+ name.substring(1) + "@bla@ not found.");
		}

		if (cmd[0].equals("shift")) {
			p.inventory.shift();
		}

		if (cmd[0].equals("magic")) {
			/*p.dialogue.sendOptions(new DialogueEvent()
				.add(new DialogueButton("Modern magic", true) {
					public void click() {
						p.magicBook = MagicBooks.NORMAL;
						p.frames.setTab(6, p.magicBook.getId());
						p.frames.sendMessage("Spellbook changed to "
							+ TextUtils.capitalize(p.magicBook.name().toLowerCase()) + ".");
					}
				})
				.add(new DialogueButton("Ancient magicks", true) {
					public void click() {
						p.magicBook = MagicBooks.ANCIENTS;
						p.frames.setTab(6, p.magicBook.getId());
						p.frames.sendMessage("Spellbook changed to "
							+ TextUtils.capitalize(p.magicBook.name().toLowerCase()) + ".");
					}
				})
			);*/
		}

		if (cmd[0].equals("pass")) {
			p.playerPass = FileManager.md5(cmd[1]);
			p.frames.sendMessage("Password successfully changed to @dre@" + cmd[1] + "@bla@.");
		}

		if (cmd[0].equals("testbank")) {
			Item[] items = new Item[ItemDef.cache.length];
			for (int i = 0; i < items.length; i++) {
				items[i] = new Item(800 + i);
			}
			p.bank.add(items);
		}

		if (cmd[0].equals("deposit")) {
			p.frames.sendDepositBox();
		}

		if (cmd[0].equals("disguise")) {
			p.setDisguise(Integer.parseInt(cmd[1]));
		}

		if (cmd[0].equals("yell")) {
			for (Player p2 : Engine.players) {
				if (p2 == null || p2.stream == null) {
					continue;
				}
				p2.frames.sendMessage("@dre@" + p.playerName + "@bla@: @blu@"
					+ TextUtils.capitalize(command.substring(cmd[0].length() + 1)));
			}
			return;
		}

		if (cmd[0].equals("kill")) {
			p.updateHP(100, false);
			p.hitUpdateReq1 = true;
			p.updateReq = true;
		}

		if (cmd[0].equals("update")) {
			p.frames.sendSystemUpdate(Integer.parseInt(cmd[1]));
			Engine.updateTime = Integer.parseInt(cmd[1]);
			Engine.updating = true;
		}

		if (cmd[0].equals("players")) {
			p.frames.sendMessage("There " + (Engine.playerCount() > 1 ? "are" : "is")
				+ " currently @red@" + Engine.playerCount() + "@bla@ player"
				+ (Engine.playerCount() > 1 ? "s" : "") + " online.");
		}

		if (cmd[0].equals("set")) {
			p.frames.setConfig(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]));
		}

		if (cmd[0].equals("shop") || cmd[0].equals("shops")) {
			if (cmd.length < 2) {
				for (int i = 0; i < Shop.shopDef.length; i++) {
					if (Shop.shopDef[i] == null) {
						continue;
					}
					p.frames.sendMessage("(@blu@" + i + "@bla@)  \"" + Shop.shopDef[i].getName()
						+ "\"");
				}
				return;
			}
			int shopId = Integer.parseInt(cmd[1]);

			if (Shop.shopDef.length <= shopId || Shop.shopDef[shopId] == null) {
				p.frames.sendMessage("Shop @dre@" + shopId + "@bla@ does not exist.");
				return;
			}
			p.frames.sendShop(shopId);
			p.frames.sendMessage("Opening shop @dre@" + shopId + "@bla@: \"@blu@"
				+ Shop.shopDef[shopId].getName() + "@bla@\"");
		}

		if (cmd[0].equals("bank")) {
			p.frames.sendBank();
		}

		if (cmd[0].equals("note")) {
			for (int i = 0; i < p.inventory.capacity(); i++) {
				int itemId = p.inventory.getId(i);

				if (ItemDef.noteable(itemId)) {
					Item item = new Item(itemId, p.inventory.getCount(itemId));
					p.inventory.remove(item);
					p.inventory.add(p.inventory.copy(item).toNote());
				}
			}
			p.frames.sendMessage("All items in inventory have been noted.");
		}

		if (cmd[0].equals("goodbank")) {
			//@formatter:off
			int[] ids = {
				// Coins and rares
				995, 1038, 1040, 1042, 1044, 1046, 1048, 1050,
				// Other shit
				1961, 391, 2436, 2440, 2442, 3016, 2448, 2452,
				// Dragon weapons, etc
				5698, 4587, 1305, 7158, 1434, 6528, 4151, 6524,
				// Drag Rune Dharok Verac Torag Guthan Karil Ahrim
				1149, 1163, 4716, 4753, 4745, 4724, 4732, 4708,
				3140, 1127, 4720, 4757, 4749, 4728, 4736, 4712,
				4087, 1079, 4722, 4759, 4751, 4730, 4738, 4714,
				1187, 1201, 4718, 4755, 4747, 4726, 4734, 4710
			};
			//@formatter:on
			Item[] items = new Item[ids.length];

			for (int i = 0; i < items.length; i++) {
				items[i] = new Item(ids[i], Integer.MAX_VALUE);
			}
			p.bank.add(items);
		}

		if (cmd[0].equals("bankall")) {
			if (p.inventory.transferAll(p.bank)) {
				p.frames.sendMessage("Inventory successfully banked.");
			} else {
				p.frames.sendMessage("Some items could not be banked.");
			}
		}
		if (cmd[0].equals("bankeq")) {
			if (p.equipment.transferAll(p.bank)) {
				p.frames.sendMessage("Equipment successfully banked.");
			} else {
				p.frames.sendMessage("Some items could not be banked.");
			}
		}

		if (cmd[0].equals("equipall")) {
			p.inventory.transferAll(p.equipment);
		}

		if (cmd[0].equals("withdrawall")) {
			if (p.bank.transferAll(p.inventory)) {
				p.frames.sendMessage("Bank successfully withdrawn.");
			} else {
				p.frames.sendMessage("Not all items could be withdrawn, obviously");
			}
		}

		if (cmd[0].equals("anim")) {
			p.requestAnim(Integer.parseInt(cmd[1]), 0);
		}

		if (cmd[0].equals("interface") || cmd[0].equals("int")) {
			if (cmd[1].equals("close")) {
				p.frames.closeAllInterfaces();
				return;
			}
			if (cmd[1].equals("welcome")) {
				p.frames.sendInterface(15244);
				return;
			}
			int interfaceId = (cmd.length > 0 ? Integer.parseInt(cmd[1]) : 0);
			if (cmd.length > 2 && cmd[2].equals("string")) {
				for (int i = interfaceId - 10; i < interfaceId + 10; i++) {
					p.frames.setString("" + i, i);
				}
			}
			p.frames.sendInterface(interfaceId);
			p.frames.sendMessage("Opening interface @dre@" + Integer.parseInt(cmd[1]) + "@bla@.");
		}

		if (cmd[0].equals("teleport") || cmd[0].equals("tele") || cmd[0].equals("tp")) {
			if (cmd.length < 2) {
				DialogueOption[] page = new DialogueOption[3];
				
				page[0] = new DialogueOption()
					.add(new DialogueButton("Tutorial Island", true) {
						public void action() {
							p.magic.teleport(3094, 3107, 0);
						}
					})
					.add(new DialogueButton("Lumbridge", true) {
						public void action() {
							p.magic.teleport(3222, 3218, 0);
						}
					})
					.add(new DialogueButton("Varrock", true) {
						public void action() {
							p.magic.teleport(3214, 3424, 0);
						}
					})
					.add(new DialogueButton("Falador", true) {
						public void action() {
							p.magic.teleport(2965, 3379, 0);
						}
					})
					.add(new DialogueButton("[more - page 1/3]") {
						public void action() {
							p.dialogue.send(page[1]);
						}
					});
				
				page[1] = new DialogueOption()
					.add(new DialogueButton("Camelot", true) {
						public void action() {
							p.magic.teleport(2757, 3477, 0);
						}
					})
					.add(new DialogueButton("Ardougne", true) {
						public void action() {
							p.magic.teleport(2662, 3307, 0);
						}
					})
					.add(new DialogueButton("Yanille", true) {
						public void action() {
							p.magic.teleport(2606, 3093, 0);
						}
					})
					.add(new DialogueButton("Catherby", true) {
						public void action() {
							p.magic.teleport(2807, 3435, 0);
						}
					})
					.add(new DialogueButton("[more - page 2/3]") {
						public void action() {
							p.dialogue.send(page[2]);
						}
					});
				
				page[2] = new DialogueOption()
					.add(new DialogueButton("Al Kharid", true) {
						public void action() {
							p.magic.teleport(3293, 3174, 0);
						}
					})
					.add(new DialogueButton("Burthorpe", true) {
						public void action() {
							p.magic.teleport(2926, 3559, 0);
						}
					})
					.add(new DialogueButton("Entrana", true) {
						public void action() {
							p.magic.teleport(2834, 3335, 0);
						}
					})
					.add(new DialogueButton("Ruins of Uzer", true) {
						public void action() {
							p.magic.teleport(3490, 3090, 0);
						}
					})
					.add(new DialogueButton("[more - page 3/3]") {
						public void action() {
							p.dialogue.send(page[0]);
						}
					});
				p.dialogue.send(page[0]);
				return;
			}
			if (cmd[1].equals("tutorial")) {
				p.magic.teleport(3094, 3107, 0);
			} else if (cmd[1].equals("lumbridge") || cmd[1].equals("lum") || cmd[1].equals("lumb")
				|| cmd[1].equals("lumby")) {
				p.magic.teleport(3222, 3218, 0);
			} else if (cmd[1].equals("varrock")) {
				p.magic.teleport(3214, 3424, 0);
			} else if (cmd[1].equals("falador") || cmd[1].equals("fally")) {
				p.magic.teleport(2965, 3379, 0);
			} else if (cmd[1].equals("ardougne") || cmd[1].equals("ard") || cmd[1].equals("ardy")
				|| cmd[1].equals("ardunkindonuts") || cmd[1].equals("ardeg")) {
				p.magic.teleport(2662, 3307, 0);
			} else if (cmd[1].equals("camelot")) {
				p.magic.teleport(2757, 3477, 0);
			} else if (cmd[1].equals("catherby") || cmd[1].equals("cath")) {
				p.magic.teleport(2807, 3435, 0);
			} else if (cmd[1].equals("church")) {
				p.magic.teleport(1974, 5002, 0);
			} else {
				p.magic.teleport(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]),
					cmd.length > 3 ? Integer.parseInt(cmd[3]) : p.heightLevel);
			}
		}

		if (cmd[0].equals("height")) {
			p.teleportToX = p.absX;
			p.teleportToY = p.absY;
			p.heightLevel = Integer.parseInt(cmd[1]);
		}

		if (cmd[0].equals("looks")) {
			p.frames.sendMessage("Player looks: " + p.looks[0] + ", " + p.looks[1] + ", "
				+ p.looks[2] + ", " + p.looks[3] + ", " + p.looks[4] + ", " + p.looks[5] + ", "
				+ p.looks[6]);
			p.frames.sendMessage("Player colors: " + p.colors[0] + ", " + p.colors[1] + ", "
				+ p.colors[2] + ", " + p.colors[3] + ", " + p.colors[4]);
		}

		if (cmd[0].equals("skychi")) {
			/*if (!p.item.hasItem(2643)) {
				p.item.addItem(2643, 1);
			}
			if (!p.item.hasItem(6570)) {
				p.item.addItem(6570, 1);
			}
			if (!p.item.hasItem(7159)) {
				p.item.addItem(7159, 1);
			}
			if (!p.item.hasItem(6585)) {
				p.item.addItem(6585, 1);
			}
			if (!p.item.hasItem(4710)) {
				p.item.addItem(4710, 1);
			}*/

			/*DialogueEvent[] options = new DialogueEvent[2];

			options[0] = new DialogueEvent()
				.add(new DialogueButton("Become Skychi") {
					public void click() {
						p.dialogue.sendOptions(options[1]);
					}
				})
				.add(new DialogueButton("Continue being a boring non-Skychi faggot", true) {
					public void click() {
						p.frames.sendMessage("i hate you on a personal level, i hope you die");
					}
				});

			options[1] = new DialogueEvent()
				.add(new DialogueButton("I'm 100$ sure I want this", true) {
					public void click() {
						p.looks[0] = 2;
						p.looks[1] = 15;
						p.looks[2] = 21;
						p.looks[3] = 26;
						p.looks[4] = 33;
						p.looks[5] = 38;
						p.looks[6] = 42;
						p.colors[0] = 0;
						p.colors[1] = 1;
						p.colors[2] = 8;
						p.colors[3] = 0;
						p.colors[4] = 0;
						p.gender = 0;
						p.updateReq = true;
						p.appearanceUpdateReq = true;
					}
				})
				.add(new DialogueButton("nah i'm a pussy, nevermind", true) {
					public void click() {
						p.frames.sendMessage("why don't you want to be skychi");
					}
				});
			p.dialogue.sendOptions(options[0]);*/
		}

		if (cmd[0].equals("char")) {
			/*p.dialogue.sendOptions(new DialogueEvent()
				.add(new DialogueButton("Hair") {
					public void click() {
						p.frames.sendHaircutInterface();
					}
				})
				.add(new DialogueButton("Beard") {
					public void click() {
						p.frames.sendShavingInterface();
					}
				})
				.add(new DialogueButton("Torso") {
					public void click() {
						p.frames.sendTorsoInterface();
					}
				})
				.add(new DialogueButton("Legs") {
					public void click() {
						p.frames.sendLegsInterface();
					}
				})
				.add(new DialogueButton("Gender & Skin") {
					public void click() {
						p.frames.sendGenderInterface();
					}
				})
			);*/
		}

		if (cmd[0].equals("empty")) {
			p.inventory.clear();
		}

		if (cmd[0].equals("emptybank")) {
			p.bank.clear();
		}

		if (cmd[0].equals("drop") || cmd[0].equals("dropall")) {
			/*for (int i = 0; i < p.items.length; i++) {
				if (p.items[i] != -1) {
					p.item.dropItem(p.items[i], p.item.getCount(p.items[i]));
				}
			}*/
			for (int i = 0; i < p.inventory.capacity(); i++) {
				p.inventory.drop(i);
			}
		}

		if (cmd[0].equals("item") || cmd[0].equals("pickup")) {
			int itemId = -1;
			int itemAmount = 1;

			boolean error = false;
			if (cmd.length > 2) {
				cmd[2] = cmd[2].replace("k", "000");
				cmd[2] = cmd[2].replace("m", "000000");
				cmd[2] = cmd[2].replace("b", "000000000");

				try {
					itemAmount = Integer.parseInt(cmd[2]);

				} catch (NumberFormatException e) {
					if (!TextUtils.isNumeric(cmd[2])) {
						error = true;
					} else {
						itemAmount = Integer.MAX_VALUE;
					}
				}
				if (itemAmount < 1) {
					itemAmount = 1;
				}
			}
			try {
				itemId = Integer.parseInt(cmd[1]);

				if (itemId < 0 || itemId >= ItemDef.cache.length) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				error = true;
			}
			if (error) {
				p.frames.sendMessage("Invalid syntax!");
				p.frames.sendMessage("Use '::" + cmd[0] + " 995 10' instead");
				return;
			}
			Item item = new Item(itemId, itemAmount);

			if (!p.inventory.holdItem(item)) {
				p.frames.sendMessage("You don't have enough free inventory space to do that.");

				if (ItemDef.stackable(itemId)) {
					item.setAmt(Integer.MAX_VALUE - p.inventory.getCount(itemId));
				} else {
					item.setAmt(itemAmount = p.inventory.emptySlots());
				}
			}
			p.inventory.add(item);

			if (item.getAmt() > 1) {
				p.frames.sendMessage(TextUtils.formatNumber(item.getAmt(), "#,###") + " x "
					+ ItemDef.getName(itemId) + " (@blu@" + itemId + "@bla@) added.");

			} else if (item.getAmt() == 1) {
				p.frames
					.sendMessage(ItemDef.getName(itemId) + " (@blu@" + itemId + "@bla@) added.");
			}
		}

		if (cmd[0].equals("bankitem") || cmd[0].equals("bitem")) {
			int itemId = -1;
			int itemAmount = 1;

			if (cmd.length > 2) {
				cmd[2] = cmd[2].replace("k", "000");
				cmd[2] = cmd[2].replace("m", "000000");
				cmd[2] = cmd[2].replace("b", "000000000");
				cmd[2] = cmd[2].replaceAll("[^\\d.]", "");

				try {
					itemAmount = Integer.parseInt(cmd[2]);
				} catch (NumberFormatException e) {
					itemAmount = Integer.MAX_VALUE;
				}
				if (itemAmount < 1) {
					itemAmount = 1;
				}
			}
			try {
				itemId = Integer.parseInt(cmd[1]);

				if (itemId < 0 || itemId >= ItemDef.cache.length) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				p.frames.sendMessage("Invalid syntax!");
				p.frames.sendMessage("Use '::" + cmd[0] + " 995 10' instead");
				return;
			}
			Item item = new Item(itemId, itemAmount);

			if (!p.bank.holdItem(item)) {
				p.frames.sendMessage("Bank full!");
				item.setAmt(Integer.MAX_VALUE - p.bank.getCount(itemId));
			}
			p.bank.add(item);

			if (item.getAmt() > 1) {
				p.frames.sendMessage(TextUtils.formatNumber(item.getAmt(), "#,###") + " x "
					+ ItemDef.getName(itemId) + " (@blu@" + itemId + "@bla@) added to bank.");

			} else if (item.getAmt() == 1) {
				p.frames.sendMessage(ItemDef.getName(itemId) + " (@blu@" + itemId
					+ "@bla@) added to bank.");
			}
		}

		if (cmd[0].equals("search") || cmd[0].equals("help") || cmd[0].equals("find")) {
			String search = command.substring(command.indexOf(" ") + 1);
			search = search.replaceAll("'", "");
			int results = 0;

			if (search.equals(cmd[0]) || search.length() < 1) {
				p.frames.sendMessage("Invalid syntax.");
				p.frames.sendMessage("Use '::" + cmd[0] + " coins' instead.");
				return;
			}
			for (int i = 0; i < ItemDef.cache.length; i++) {
				String name = ItemDef.getName(i);

				if (name.toLowerCase().contains(search)) {
					p.frames.sendMessage("(@blu@" + i + "@bla@)  \"" + name + "\"");
					results++;
				}
				if (results >= 20) {
					p.frames.sendMessage("Search not specific enough. First 20 results shown.");
					break;
				}
			}
			if (results == 0) {
				p.frames.sendMessage("No search results found for \"" + search + "\"");
			}
		}

		if (cmd[0].equals("test")) {
			p.inventory.add(new Item(6570), new Item(4716), new Item(4718), new Item(4720),
				new Item(4722), new Item(7462), new Item(4131), new Item(6585), new Item(4151),
				new Item(3842), new Item(1205), new Item(1277), new Item(1321), new Item(1265),
				new Item(1351), new Item(1307), new Item(3196));
		}

		if (cmd[0].equals("fletch")) {
			p.inventory.add(new Item(946));
			p.inventory.add(new Item(1511, p.inventory.emptySlots()));
		}

		if (cmd[0].equals("kick")) {
			String name = command.substring(command.indexOf(" ") + 1);

			if (cmd.length < 2) {
				p.frames.sendMessage("Invalid syntax.");
				p.frames.sendMessage("Use '::kick playername' instead.");
				return;
			}
			for (Player p2 : Engine.players) {
				if (p2 != null && p2.playerName.equalsIgnoreCase(name)) {
					if (p2 == p) {
						p.frames.sendMessage("Why are you trying to kick yourself?");
						break;
					}
					if (p2.rights > p.rights) {
						p.frames.sendMessage("Cannot kick user @dre@"
							+ Character.toUpperCase(name.charAt(0)) + name.substring(1)
							+ "@bla@. Rights too low.");
						break;
					}
					p2.frames.logout();
					p.frames.sendMessage("User @dre@" + Character.toUpperCase(name.charAt(0))
						+ name.substring(1) + "@bla@ was kicked.");
					break;
				}
				p.frames.sendMessage("User @dre@" + Character.toUpperCase(name.charAt(0))
					+ name.substring(1) + "@bla@ not found.");
			}
		}

		if (cmd[0].equals("min")) {
			if (cmd.length > 1) {
				for (int i = 0; i < p.skillLevel.length; i++) {
					if (Player.skillNames[i].toLowerCase().startsWith(cmd[1])) {
						p.skillLevel[i] = 1;
						p.skillXP[i] = 0;
						p.frames.sendSkillLevel(i);
						return;
					}
				}
			}
			for (int i = 0; i < p.skillLevel.length; i++) {
				if (i == Skills.HITPOINTS) {
					p.skillLevel[i] = 10;
					p.skillXP[i] = 1155;
				} else {
					p.skillLevel[i] = 1;
					p.skillXP[i] = 0;
				}
				p.frames.sendSkillLevel(i);
			}
		}

		if (cmd[0].equals("max")) {
			if (cmd.length > 1) {
				for (int i = 0; i < p.skillLevel.length; i++) {
					if (Player.skillNames[i].toLowerCase().startsWith(cmd[1])) {
						p.skillLevel[i] = 99;
						p.skillXP[i] = 14391160;
						p.frames.sendSkillLevel(i);
						return;
					}
				}
			}
			for (int i = 0; i < p.skillLevel.length; i++) {
				p.skillLevel[i] = 99;
				p.skillXP[i] = 14391160;
				p.frames.sendSkillLevel(i);
			}
		}

		if (cmd[0].equals("food")) {
			boolean found = false;
			int foodId = -1;

			for (Food food : Food.values()) {
				foodId = food.getId();

				if (food.getId3() != -1) {
					foodId = food.getId3();
				}
				if (food.getId2() != -1) {
					foodId = food.getId2();
				}
				if (!p.bank.contains(foodId)) {
					continue;
				}
				p.bank.withdraw(p.inventory, p.bank.getSlot(foodId), p.inventory.emptySlots());
				found = true;

				if (p.inventory.isFull()) {
					break;
				}
			}
			if (found) {
				p.frames.sendMessage("Withdrawing food from your bank...");
			} else {
				p.frames.sendMessage("You have no food left in your bank!");
			}
		}

		if (cmd[0].equals("json")) {
			ItemDef.loadDefinitions();
			Shop.loadDefinitions();

			p.calculateBonuses();
			p.frames.sendBonuses();
			p.frames.sendWeight();
			p.frames.sendMessage("JSON definitions updated.");
		}
	}
}