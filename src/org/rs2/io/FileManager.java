package org.rs2.io;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import org.rs2.model.items.Item;
import org.rs2.model.players.Player;
import org.rs2.model.players.Player.MagicBooks;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class FileManager {

	private static MessageDigest md;

	public static void profileSave(Player p) {
		JsonWriter writer;

		String path = "./data/char/" + p.playerName + ".json";
		String backup = path + ".bak";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			writer = new JsonWriter(new FileWriter(path));
			writer.setIndent("	");
			writer.beginObject();

			// Account
			writer.name("user").value(p.playerName);
			writer.name("pass").value(p.playerPass);
			writer.name("lastLogin").value(sdf.format(p.lastLogin));
			writer.name("lastIP").value(p.lastIP);
			writer.name("rights").value(p.rights);
			writer.name("posX").value(p.absX);
			writer.name("posY").value(p.absY);
			writer.name("height").value(p.heightLevel);

			// Character
			writer.name("character").beginObject();
			writer.name("questPoints").value(p.questPoints);
			writer.name("running").value(p.running);
			writer.name("runEnergy").value(p.runEnergy);
			writer.name("specialEnergy").value(p.specialEnergy);
			writer.name("autoRetaliate").value(p.autoRetaliate);
			writer.name("combatStyle").value(p.combatStyle);
			writer.name("magicBook").value(p.magicBook.name());

			writer.name("looks").beginArray();
			writer.setIndent("");
			for (int i = 0; i < p.looks.length; i++) {
				writer.value(p.looks[i]);
			}
			writer.endArray();
			writer.setIndent("	");

			writer.name("colors").beginArray();
			writer.setIndent("");
			for (int i = 0; i < p.colors.length; i++) {
				writer.value(p.colors[i]);
			}
			writer.endArray();
			writer.setIndent("	");

			writer.name("gender").value((p.gender == 0) ? "male" : "female");
			// writer.name("skullIcon").value(p.skullIcon);
			// writer.name("prayIcon").value(p.prayIcon);

			/*writer.name("bankPin").beginObject();
			writer.name("pin").beginArray();
			writer.setIndent("");

			for (int i = 0; i < p.bankPin.setPin.size(); i++) {
				writer.value(p.bankPin.setPin.get(i));
			}
			writer.endArray();

			if (!p.bankPin.newPin.isEmpty()) {
				writer.setIndent("	");
				writer.name("newPin").beginArray();
				writer.setIndent("");
				for (int i = 0; i < p.bankPin.newPin.size(); i++) {
					writer.value(p.bankPin.newPin.get(i));
				}
				writer.endArray();
			}
			writer.setIndent("	");
			writer.name("state").value(p.bankPin.state.name());
			writer.name("delay").value(p.bankPin.recoveryDelay);

			if (p.bankPin.date != null) {
				writer.name("date").value(p.bankPin.date != null ? sdf.format(p.bankPin.date) : "");
			}
			if (p.bankPin.attempts > 0) {
				writer.name("attempts").value(p.bankPin.attempts);
			}
			if (p.bankPin.attemptCooldown > 0) {
				writer.name("cooldown").value(p.bankPin.attemptCooldown);
			}
			writer.endObject();*/
			writer.endObject();

			// Settings
			writer.name("settings").beginObject();
			writer.name("brightness").value(p.brightness);
			writer.name("singleMouseButton").value(p.singleMouseButton);
			writer.name("chatEffects").value(p.chatEffects);

			writer.name("chatPrivacy").beginArray();
			writer.setIndent("");
			writer.value(p.chatPublic);
			writer.value(p.chatPrivate);
			writer.value(p.chatTrade);
			writer.endArray();
			writer.setIndent("	");

			writer.name("splitPrivateChat").value(p.splitPrivateChat);
			writer.name("acceptAid").value(p.acceptAid);
			writer.name("volumeMusic").value(p.volumeMusic);
			writer.name("volumeEffects").value(p.volumeEffects);
			writer.name("musicPlayer").beginObject();
			writer.name("mode").value((p.musicPlayerMode == 0) ? "auto" : "manual");
			writer.name("loop").value(p.musicPlayerLoop);
			writer.endObject();
			writer.endObject();

			// Skills
			writer.name("skills").beginArray();
			for (int i = 0; i < p.skillLevel.length; i++) {
				writer.beginArray();
				writer.setIndent("");
				writer.value(p.skillLevel[i]);
				writer.value(p.skillXP[i]);
				writer.endArray();
				writer.setIndent("	");
			}
			writer.endArray();

			// Equipment
			writer.name("equip").beginArray();
			for (int i = 0; i < p.equipment.capacity(); i++) {
				if (p.equipment.get(i) == null) {
					continue;
				}
				writer.beginArray();
				writer.setIndent("");
				writer.value(i);
				writer.value(p.equipment.getId(i));
				writer.value(p.equipment.getAmt(i));
				if (p.equipment.getHealth(i) != -1) {
					writer.value(p.equipment.getHealth(i));
				}
				writer.endArray();
				writer.setIndent("	");
			}
			writer.endArray();

			// Inventory
			writer.name("inv").beginArray();
			for (int i = 0; i < p.inventory.capacity(); i++) {
				if (p.inventory.get(i) == null) {
					continue;
				}
				writer.beginArray();
				writer.setIndent("");
				writer.value(i);
				writer.value(p.inventory.getId(i));
				writer.value(p.inventory.getAmt(i));
				if (p.inventory.getHealth(i) != -1) {
					writer.value(p.inventory.getHealth(i));
				}
				writer.endArray();
				writer.setIndent("	");
			}
			writer.endArray();

			// Bank
			writer.name("bank").beginArray();
			for (int i = 0; i < p.bank.capacity(); i++) {
				if (p.bank.get(i) == null) {
					continue;
				}
				writer.beginArray();
				writer.setIndent("");
				writer.value(i);
				writer.value(p.bank.getId(i));
				writer.value(p.bank.getAmt(i));
				if (p.bank.getHealth(i) != -1) {
					writer.value(p.bank.getHealth(i));
				}
				writer.endArray();
				writer.setIndent("	");
			}
			writer.endArray();

			// Friends
			writer.name("friends").beginArray();
			for (Long l : p.friends) {
				writer.value(l);
			}
			writer.endArray();

			// Ignores
			writer.name("ignores").beginArray();
			for (Long l : p.ignores) {
				writer.value(l);
			}
			writer.endArray();

			writer.endObject();
			writer.close();

			/**
			 * Save the 3 most recent backups.
			 */
			if (Files.exists(Paths.get(backup))) {
				if (Files.exists(Paths.get(backup + "1"))) {
					if (Files.exists(Paths.get(backup + "2"))) {
						Files.delete(Paths.get(backup + "2"));
					}
					Files.copy(Paths.get(backup + "1"), Paths.get(backup + "2"));
					Files.delete(Paths.get(backup + "1"));
				}
				Files.copy(Paths.get(backup), Paths.get(backup + "1"));
				Files.delete(Paths.get(backup));
			}
			Files.copy(Paths.get(path), Paths.get(backup));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void profileLoad(Player p) {
		JsonReader reader;
		String path = "./data/char/" + p.playerName + ".json";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			reader = new JsonReader(new FileReader(path));
			reader.beginObject();

			while (reader.hasNext()) {
				String name = reader.nextName();

				// Account
				if (name.equals("user")) {
					p.playerName = reader.nextString();

				} else if (name.equals("pass")) {
					p.playerPass = reader.nextString();

				} else if (name.equals("lastLogin")) {
					try {
						p.lastLogin = sdf.parse(reader.nextString());
					} catch (Exception e) {
					}
				} else if (name.equals("lastIP")) {
					p.lastIP = reader.nextString();

				} else if (name.equals("rights")) {
					p.rights = reader.nextInt();

				} else if (name.equals("posX")) {
					p.teleportToX = reader.nextInt();

				} else if (name.equals("posY")) {
					p.teleportToY = reader.nextInt();

				} else if (name.equals("height")) {
					p.heightLevel = reader.nextInt();

					// Character
				} else if (name.equals("character")) {
					reader.beginObject();
					while (reader.hasNext()) {
						name = reader.nextName();

						if (name.equals("questPoints")) {
							p.questPoints = reader.nextInt();

						} else if (name.equals("running")) {
							p.running = reader.nextBoolean();

						} else if (name.equals("runEnergy")) {
							p.runEnergy = reader.nextInt();

						} else if (name.equals("specialEnergy")) {
							p.specialEnergy = reader.nextInt();

						} else if (name.equals("autoRetaliate")) {
							p.autoRetaliate = reader.nextBoolean();

						} else if (name.equals("combatStyle")) {
							p.combatStyle = reader.nextInt();

						} else if (name.equals("magicBook")) {
							p.magicBook = MagicBooks.valueOf(reader.nextString());

						} else if (name.equals("looks")) {
							reader.beginArray();
							for (int i = 0; i < p.looks.length; i++) {
								p.looks[i] = reader.nextInt();
							}
							reader.endArray();

						} else if (name.equals("colors")) {
							reader.beginArray();
							for (int i = 0; i < p.colors.length; i++) {
								p.colors[i] = reader.nextInt();
							}
							reader.endArray();

						} else if (name.equals("gender")) {
							if (reader.nextString().equals("male")) {
								p.gender = 0;
							} else {
								p.gender = 1;
							}

						} else if (name.equals("bankPin")) {
							reader.beginObject();
							while (reader.hasNext()) {
								name = reader.nextName();

								/*if (name.equals("pin")) {
									reader.beginArray();
									while (reader.hasNext()) {
										p.bankPin.setPin.add(reader.nextInt());
									}
									reader.endArray();
								} else if (name.equals("newPin")) {
									reader.beginArray();
									while (reader.hasNext()) {
										p.bankPin.newPin.add(reader.nextInt());
									}
									reader.endArray();
								} else if (name.equals("state")) {
									p.bankPin.state = States.valueOf(reader.nextString());

								} else if (name.equals("delay")) {
									p.bankPin.recoveryDelay = reader.nextInt();

								} else if (name.equals("date")) {
									try {
										p.bankPin.date = sdf.parse(reader.nextString());
									} catch (Exception e) {
									}
								} else if (name.equals("attempts")) {
									p.bankPin.attempts = reader.nextInt();
								} else if (name.equals("cooldown")) {
									p.bankPin.attemptCooldown = reader.nextLong();
								} else {
									reader.skipValue();
								}*/
							}
							reader.endObject();

						} else {
							reader.skipValue();
						}
					}
					reader.endObject();

					// Settings
				} else if (name.equals("settings")) {
					reader.beginObject();
					while (reader.hasNext()) {
						name = reader.nextName();

						if (name.equals("brightness")) {
							p.brightness = reader.nextInt();

						} else if (name.equals("singleMouseButton")) {
							p.singleMouseButton = reader.nextBoolean();

						} else if (name.equals("chatEffects")) {
							p.chatEffects = reader.nextBoolean();

						} else if (name.equals("chatPrivacy")) {
							reader.beginArray();
							p.chatPublic = reader.nextInt();
							p.chatPrivate = reader.nextInt();
							p.chatTrade = reader.nextInt();
							reader.endArray();

						} else if (name.equals("splitPrivateChat")) {
							p.splitPrivateChat = reader.nextBoolean();

						} else if (name.equals("acceptAid")) {
							p.acceptAid = reader.nextBoolean();

						} else if (name.equals("volumeMusic")) {
							p.volumeMusic = reader.nextInt();

						} else if (name.equals("volumeEffects")) {
							p.volumeEffects = reader.nextInt();

						} else if (name.equals("musicPlayer")) {
							reader.beginObject();
							while (reader.hasNext()) {
								name = reader.nextName();

								if (name.equals("mode")) {
									if (reader.nextString().equals("auto")) {
										p.musicPlayerMode = 0;
									} else {
										p.musicPlayerMode = 1;
									}
								} else if (name.equals("loop")) {
									p.musicPlayerLoop = reader.nextBoolean();
								} else {
									reader.skipValue();
								}
							}
							reader.endObject();

						} else {
							reader.skipValue();
						}

					}
					reader.endObject();

					// Skills
				} else if (name.equals("skills")) {
					reader.beginArray();
					for (int i = 0; i < p.skillLevel.length; i++) {
						reader.beginArray();
						p.skillLevel[i] = reader.nextInt();
						p.skillXP[i] = reader.nextDouble();
						reader.endArray();
					}
					reader.endArray();

					// Equipment
				} else if (name.equals("equip")) {
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginArray();
						while (reader.hasNext()) {
							int slot = reader.nextInt();
							int id = reader.nextInt();
							int amount = reader.nextInt();
							int health = reader.hasNext() ? reader.nextInt() : -1;
							p.equipment.set(new Item(id, amount, health), slot);
						}
						reader.endArray();
					}
					reader.endArray();

					// Inventory
				} else if (name.equals("inv")) {
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginArray();
						while (reader.hasNext()) {
							int slot = reader.nextInt();
							int id = reader.nextInt();
							int amount = reader.nextInt();
							int health = reader.hasNext() ? reader.nextInt() : -1;
							p.inventory.set(new Item(id, amount, health), slot);
						}
						reader.endArray();
					}
					reader.endArray();

					// Bank
				} else if (name.equals("bank")) {
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginArray();
						while (reader.hasNext()) {
							int slot = reader.nextInt();
							int id = reader.nextInt();
							int amount = reader.nextInt();
							int health = reader.hasNext() ? reader.nextInt() : -1;
							p.bank.set(new Item(id, amount, health), slot);
						}
						reader.endArray();
					}
					reader.endArray();

					// Friends
				} else if (name.equals("friends")) {
					reader.beginArray();
					while (reader.hasNext()) {
						p.friends.add(reader.nextLong());
					}
					reader.endArray();

					// Ignores
				} else if (name.equals("ignores")) {
					reader.beginArray();
					while (reader.hasNext()) {
						p.ignores.add(reader.nextLong());
					}
					reader.endArray();

				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (Exception e) {
		}
	}

	public static String md5(String s) {
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(s.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);

			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
