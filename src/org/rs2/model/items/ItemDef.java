package org.rs2.model.items;

import java.io.FileReader;
import java.util.Random;
import com.google.gson.stream.JsonReader;

public class ItemDef {
	/**
	 * Cache of all the item definitions.
	 */
	public static ItemDef[] cache = new ItemDef[7956];

	private String name = "null";
	private int value;
	private double weight;
	private double weightEq;

	private boolean noteable;
	private boolean stackable;
	private boolean tradeable;

	private int[] bonuses = new int[12];

	private int childId = -1;
	private int parentId = -1;

	private int dropItem = -1;

	private int disguise = -1;
	private int[] disguises = new int[6];

	private String[] destroy = { "", "" };

	/**
	 * Items with matching names will be equipped to the weapon slot.
	 */
	private static String[] weapons = { "dagger", "sword", "scimitar", "axe", "warhammer", "mace",
			"spear", "halberd", "claws", "bow", "dart", "staff", "abyssal whip", "veracs flail",
			"rubber chicken", "maul", "tzhaar ket-om", "excalibur", "knife", "crossbow", "torch",
			"rapier", "net", "wand", "cleaver", "flag", "sabre", "banner", "basket" };
	/**
	 * Items with matching names will be equipped to the shield slot.
	 */
	private static String[] shields = { "sq shield", "kiteshield", "shield", "book",
			"toktz-ket-xil", "defender", "ward", "lantern" };
	/**
	 * Items with matching names will be equipped to the hat slot.
	 */
	private static String[] helmets = { "helm", "helmet", "hat", "partyhat", "cavalier", "hood",
			"mask", "bunny ears", "head", "coif", "tiara", "cowl", "afro", "mitre", "beret",
			"eye patch", "bandana", "crown", "coronet", "snelm", "boater", "antlers", "headdress" };
	/**
	 * Items with matching names will be equipped to the torso slot.
	 */
	private static String[] bodies = { "platebody", "chainbody", "veracs brassard", "robetop",
			"top", "jacket", "apron", "shirt", "body", "gown", "blouse", "mail", "coat", "tunic" };
	/**
	 * Items with matching names will be equipped to the leg slot.
	 */
	private static String[] legs = { "platelegs", "plateskirt", "trousers", "robe", "chaps",
			"legs", "skirt", "leggings", "tassets" };
	/**
	 * Items with matching names will be equipped to the amulet slot.
	 */
	private static String[] amulets = { "amulet", "ammy", "necklace", "pendant", "symbol", "logo",
			"sigil", "medallion", "trinkets", "stole", "scarf", "hiss", "whisper", "murmur",
			"charm" };

	/**
	 * Returns the name of an item.
	 * @param id Id of the item to get the name for.
	 */
	public static String getName(int id) {
		if (id < 0 || cache[id] == null) {
			return "null";
		}
		return cache[id].name;
	}

	/**
	 * Returns the value of an item. This value is used as the default shop
	 * price, unless overridden in the shop definition. This is also split 60/40
	 * to determine high and low alch values respectively.
	 * @param id Id of the item to get the value for.
	 */
	public static int getValue(int id) {
		if (id < 0 || cache[id] == null) {
			return -1;
		}
		if (noted(id)) {
			return cache[getParent(id)].value;
		}
		return cache[id].value;
	}

	/**
	 * Returns the weight of an item.
	 * @param id Id of the item to get the weight for.
	 */
	public static double getWeight(int id) {
		if (id < 0 || cache[id] == null) {
			return 0;
		}
		if (stackable(id)) {
			return 0;
		}
		return cache[id].weight;
	}

	/**
	 * Returns the equipped weight of an item.
	 * @param id Id of the item to get the equipped weight for.
	 */
	public static double getWeightEq(int id) {
		if (id < 0 || cache[id] == null) {
			return 0;
		}
		if (stackable(id)) {
			return 0;
		}
		return cache[id].weightEq;
	}

	/**
	 * Returns true if its possible to note the item specified.
	 * @param id Id of the item to check for.
	 */
	public static boolean noteable(int id) {
		if (id < 0 || cache[id] == null) {
			return false;
		}
		if (stackable(id)) {
			return false;
		}
		return cache[id].noteable;
	}

	/**
	 * Returns true if the item specified is tradeable.
	 * @param id Id of the item to check for.
	 */
	public static boolean stackable(int id) {
		if (id < 0 || cache[id] == null) {
			return false;
		}
		if (noted(id)) {
			return true;
		}
		return cache[id].stackable;
	}

	/**
	 * Returns true if the item specified is stackable.
	 * @param id Id of the item to check for.
	 */
	public static boolean tradeable(int id) {
		if (id < 0 || cache[id] == null) {
			return false;
		}
		if (noted(id)) {
			id = getParent(id);
		}
		return cache[id].tradeable;
	}

	/**
	 * Returns the bonus at the specified index.
	 * @param id Id of the item to find the bonus for.
	 * @param bonus Bonus to get.
	 */
	public static int getBonus(int id, int bonus) {
		if (id < 0 || cache[id] == null) {
			return 0;
		}
		return cache[id].bonuses[bonus];
	}

	/**
	 * Returns true of the item is noted.
	 * @param id
	 * @return
	 */
	public static boolean noted(int id) {
		if (id < 0 || cache[id] == null) {
			return false;
		}
		int itemId = getParent(id);

		if (getName(id).equals(getName(itemId))) {
			return cache[itemId].noteable;
		}
		return false;
	}

	/**
	 * Returns the noted version of an item.
	 * @param id Id of the item to check for.
	 */
	public static int getChild(int id) {
		if (id < 0 || cache[id] == null) {
			return -1;
		}
		if (cache[id].childId != -1) {
			return cache[id].childId;
		}
		return id + 1;
	}

	/**
	 * Returns the un-noted version of a noted item.
	 * @param id Id of the note to check for.
	 */
	public static int getParent(int id) {
		if (id < 0 || cache[id] == null) {
			return -1;
		}
		if (cache[id].parentId != -1) {
			return cache[id].parentId;
		}
		return id - 1;
	}

	/**
	 * Returns the 'drop item' for the specified item. This item replaces the
	 * original when dropped.
	 * @param id Id of the item to find dropItem for.
	 */
	public static int getDropItem(int id) {
		if (id < 0 || cache[id] == null) {
			return -1;
		}
		return cache[id].dropItem;
	}

	/**
	 * Returns true if the item is destroyable.
	 * @param id Id of the item to check.
	 */
	public static boolean destroyable(int id) {
		if (id < 0 || cache[id] == null) {
			return false;
		}
		return cache[id].destroy[0] != "" || cache[id].destroy[1] != "";
	}

	/**
	 * Returns the destroy message for the item.
	 * @param id Id of the item to check.
	 */
	public static String[] getDestroyMsg(int id) {
		if (id < 0 || cache[id] == null) {
			return null;
		}
		return cache[id].destroy;
	}

	/**
	 * Returns true if the item has any set disguises.
	 * @param id Id of item to check.
	 */
	public static boolean hasDisguise(int id) {
		if (id < 0 || cache[id] == null) {
			return false;
		}
		if (cache[id].disguise != -1) {
			return true;
		}
		for (int i = 0; i < cache[id].disguises.length; i++) {
			if (cache[id].disguises[i] != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a random disguise from the array.
	 * @param id Id of item to check.
	 */
	public static int getDisguise(int id) {
		if (id < 0 || cache[id] == null) {
			return -1;
		}
		if (cache[id].disguise != -1) {
			return cache[id].disguise;
		}
		int[] totalDisguises = new int[cache[id].disguises.length];

		for (int i = 0; i < cache[id].disguises.length; i++) {
			if (cache[id].disguises[i] != 0) {
				totalDisguises[i] = cache[id].disguises[i];
			}
		}
		return totalDisguises[new Random().nextInt(totalDisguises.length)];
	}

	/**
	 * Returns the slot where a piece of equipment should be placed.
	 * @param id Id of the item to check.
	 */
	public static int getEquipSlot(int id) {
		if (id < 0) {
			return -1;
		}
		String name = ItemDef.getName(id).toLowerCase();

		for (String i : helmets) {
			if (name.contains(i)) {
				return 0;
			}
		}
		if (name.contains("cape") || name.contains("cloak")) {
			return 1;
		}

		for (String i : amulets) {
			if (name.contains(i)) {
				return 2;
			}
		}
		for (String i : weapons) {
			if (name.contains(i)) {
				return 3;
			}
		}
		if (id == 6107 || id == 426 || id == 4069 || id == 1035) {
			return 4;
		}
		for (String i : bodies) {
			if (name.contains(i)) {
				return 4;
			}
		}
		for (String i : shields) {
			if (name.contains(i)) {
				return 5;
			}
		}
		if (id == 6108 || id == 428 || id == 4070 || id == 1033) {
			return 7;
		}
		for (String i : legs) {
			if (name.contains(i)) {
				return 7;
			}
		}
		if (name.contains("gloves") || name.contains("vamb")) {
			return 9;
		}

		if (name.contains("boots")) {
			return 10;
		}

		if (name.contains("ring")) {
			return 12;
		}

		if (name.contains("arrow")) {
			return 13;
		}

		return -1;
	}

	/**
	 * Returns true if worn torso item is a platebody (item that covers your
	 * arms).
	 */
	public static boolean twoHanded(int id) {
		if (id < 0) {
			return false;
		}
		String name = ItemDef.getName(id).toLowerCase();
		String[] twoHanded = { "2h sword", "spear", "halberd", "claws", "shortbow", "longbow",
				"dharok's greataxe", "veracs flail", "ahrims staff", "granite maul", "tzhaar ket-om" };

		for (String i : twoHanded) {
			if (name.contains(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if worn torso item is a platebody (item that covers your
	 * arms).
	 */
	public static boolean fullBody(int id) {
		if (id < 0) {
			return false;
		}
		String name = ItemDef.getName(id).toLowerCase();
		String[] fullBody = { "platebody", "chainbody", "veracs brassard", "robe", "top", "jacket",
				"ham shirt", "body", "shirt" };

		if (id == 4069) {
			return true;
		}
		if (id == 426) {
			return true;
		}
		if (name.contains("d'hide body")) {
			return false;
		}
		for (String i : fullBody) {
			if (name.contains(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if worn torso item cannot be worn with gloves.
	 */
	public static boolean fullTorso(int id) {
		if (id < 0) {
			return false;
		}
		String name = ItemDef.getName(id).toLowerCase();

		if (name.equals("plague jacket")) {
			return true;
		}

		return false;
	}

	/**
	 * Returns true if worn legs cannot be worn with boots.
	 */
	public static boolean fullLegs(int id) {
		if (id < 0) {
			return false;
		}
		String name = ItemDef.getName(id).toLowerCase();

		if (name.equals("plague trousers")) {
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the specified helmet covers your face but not your beard.
	 */
	public static boolean fullHelmet(int id) {
		if (id < 0) {
			return false;
		}
		String name = ItemDef.getName(id).toLowerCase();
		String[] fullHelmet = { "med helm", "hood", "coif", "dharoks helm", "hood" };

		for (String i : fullHelmet) {
			if (name.contains(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the specified helmet covers face and beard.
	 */
	public static boolean fullMask(int id) {
		if (id < 0) {
			return false;
		}
		String name = ItemDef.getName(id).toLowerCase();
		String[] fullMask = { "full helm", "h'ween", "veracs helm", "mask" };

		for (String i : fullMask) {
			if (name.contains(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Loads item definitions from the json file.
	 */
	public static void loadDefinitions() {
		JsonReader reader;

		for (int i = 0; i < ItemDef.cache.length; i++) {
			cache[i] = null;
		}

		try {
			reader = new JsonReader(new FileReader("./data/items.json"));
			reader.beginObject();

			while (reader.hasNext()) {
				String name = reader.nextName();

				if (name.equals("items")) {
					reader.beginArray();

					while (reader.hasNext()) {
						reader.beginObject();

						int itemId = -1;

						while (reader.hasNext()) {
							name = reader.nextName();

							if (name.equals("id")) {
								itemId = reader.nextInt();
								cache[itemId] = new ItemDef();
							}
							if (itemId != -1) {
								if (name.equals("name")) {
									cache[itemId].name = reader.nextString();

								} else if (name.equals("weight")) {
									cache[itemId].weight = reader.nextDouble();
									cache[itemId].weightEq = cache[itemId].weight;

								} else if (name.equals("weightEq")) {
									cache[itemId].weightEq = reader.nextDouble();

								} else if (name.equals("value")) {
									cache[itemId].value = reader.nextInt();

								} else if (name.equals("flags")) {
									reader.beginArray();
									cache[itemId].noteable = reader.nextBoolean();
									cache[itemId].stackable = reader.nextBoolean();
									cache[itemId].tradeable = reader.nextBoolean();
									reader.endArray();

								} else if (name.equals("bonuses")) {
									reader.beginArray();
									for (int i = 0; i < 12; i++) {
										cache[itemId].bonuses[i] = reader.nextInt();
									}
									reader.endArray();

								} else if (name.equals("dropItem")) {
									cache[itemId].dropItem = reader.nextInt();

								} else if (name.equals("childId")) {
									cache[itemId].childId = reader.nextInt();

								} else if (name.equals("parentId")) {
									cache[itemId].parentId = reader.nextInt();

								} else if (name.equals("destroy")) {
									reader.beginArray();
									cache[itemId].destroy[0] = reader.nextString();

									if (reader.hasNext()) {
										cache[itemId].destroy[1] = reader.nextString();
									}
									reader.endArray();
								} else if (name.equals("disguise")) {
									cache[itemId].disguise = reader.nextInt();
								} else if (name.equals("disguises")) {
									reader.beginArray();
									for (int i = 0; i < cache[itemId].disguises.length; i++) {
										if (reader.hasNext()) {
											cache[itemId].disguises[i] = reader.nextInt();
										}
									}
									reader.endArray();
								}

							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					}
					reader.endArray();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();

			reader = new JsonReader(new FileReader("./data/items_override.json"));
			reader.beginObject();

			while (reader.hasNext()) {
				String name = reader.nextName();

				if (name.equals("items")) {
					reader.beginArray();

					while (reader.hasNext()) {
						reader.beginObject();
						int itemId = -1;

						while (reader.hasNext()) {
							name = reader.nextName();

							if (name.equals("id")) {
								itemId = reader.nextInt();
							}
							if (itemId != -1) {
								if (name.equals("name")) {
									cache[itemId].name = reader.nextString();
								
								} else if (name.equals("weight")) {
									cache[itemId].weight = reader.nextDouble();

								} else if (name.equals("weightEq")) {
									cache[itemId].weightEq = reader.nextDouble();

								} else if (name.equals("value")) {
									cache[itemId].value = reader.nextInt();

								} else if (name.equals("bonuses")) {
									reader.beginArray();
									for (int i = 0; i < 12; i++) {
										cache[itemId].bonuses[i] = reader.nextInt();
									}
									reader.endArray();

								} else if (name.equals("dropItem")) {
									cache[itemId].dropItem = reader.nextInt();

								} else if (name.equals("destroy")) {
									reader.beginArray();
									cache[itemId].destroy[0] = reader.nextString();

									if (reader.hasNext()) {
										cache[itemId].destroy[1] = reader.nextString();
									}
									reader.endArray();
								} else if (name.equals("disguise")) {
									cache[itemId].disguise = reader.nextInt();
								} else if (name.equals("disguises")) {
									reader.beginArray();
									for (int i = 0; i < cache[itemId].disguises.length; i++) {
										if (reader.hasNext()) {
											cache[itemId].disguises[i] = reader.nextInt();
										}
									}
									reader.endArray();
								}
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					}
					reader.endArray();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
