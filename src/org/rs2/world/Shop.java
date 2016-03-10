package org.rs2.world;

import java.io.FileReader;
import org.rs2.Engine;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import com.google.gson.stream.JsonReader;

public class Shop {

	/**
	 * Array containing all shops.
	 */
	public static Shop[] shopDef = new Shop[100];

	private static int lastRestock, lastDestock;

	private String name = "null";
	public int[] items = new int[40];
	public int[] itemsN = new int[40];
	public int[] baseItems = new int[40];
	public int[] baseItemsN = new int[40];
	public int[] price = new int[40];
	private int currency = 995;

	public String getName() {
		return name;
	}

	public int getCurrency() {
		return currency;
	}

	public String getCurrencyName() {
		String currency = ItemDef.getName(getCurrency()).toLowerCase();

		if (!currency.endsWith("s")) {
			currency = currency + "s";
		}
		return currency;
	}

	public boolean isGeneral() {
		return name.toLowerCase().contains("general");
	}

	/**
	 * Runs every 600ms.
	 */
	public static void tick() {
		if (Engine.currentTicks() - lastRestock >= 5) {
			for (int i = 0; i < shopDef.length; i++) {
				if (shopDef[i] == null) {
					continue;
				}
				for (int j = 0; j < shopDef[i].items.length; j++) {
					if (shopDef[i].items[j] == -1) {
						continue;
					}
					if (shopDef[i].itemsN[j] < shopDef[i].baseItemsN[j]) {
						addItem(i, shopDef[i].baseItems[j], 1);
					}
				}
			}
			lastRestock = Engine.currentTicks();
		}
		if (Engine.currentTicks() - lastDestock >= 30) {
			for (int i = 0; i < shopDef.length; i++) {
				if (shopDef[i] == null) {
					continue;
				}
				for (int j = 0; j < shopDef[i].items.length; j++) {
					if (shopDef[i].items[j] == -1) {
						continue;
					}
					if (shopDef[i].itemsN[j] > shopDef[i].baseItemsN[j]) {
						deleteItem(i, j, 1);
					}
				}
			}
			lastDestock = Engine.currentTicks();
		}
	}

	public static boolean canSell(Player p, int item) {
		if (hasItem(p.shopId, item) && ItemDef.noted(item)) {
			return true;
		}
		if (item == 995 || !ItemDef.tradeable(item) || ItemDef.getValue(item) == -1) {
			p.frames.sendMessage("You can't sell this item to a shop.");
			return false;
		}
		if (!shopDef[p.shopId].isGeneral()
			&& !hasItem(p.shopId, (ItemDef.noted(item)) ? ItemDef.getParent(item) : item)) {
			p.frames.sendMessage("You cannot sell that item to this shop.");
			return false;
		}
		return true;
	}

	/**
	 * Buys an item from the currently active shop.
	 * @param p Player to buy item for.
	 * @param item Item to buy.
	 * @param slot Slot to buy from.
	 * @param amount Amount of item to buy.
	 */
	public static void buyItem(Player p, int item, int slot, int amount) {
		if (p == null || item == -1 || slot == -1) {
			return;
		}
		for (int i = 0; i < amount; i++) {
			if (shopDef[p.shopId].itemsN[slot] < 1) {
				p.frames.sendMessage("The shop has run out of stock.");
				break;
			}
			/*if (p.item.getCount(shopDef[p.shopId].getCurrency()) < getBuyPrice(p.shopId, slot)) {
				p.frames.sendMessage("You don't have enough " + shopDef[p.shopId].getCurrencyName() + ".");
				break;
			}
			if (p.item.holdItem(item, 1)) {
				p.item.deleteItem(shopDef[p.shopId].getCurrency(), getBuyPrice(p.shopId, slot));
				p.item.addItem(item, 1);
			} else {
				if (p.item.getCount(shopDef[p.shopId].getCurrency()) == getBuyPrice(p.shopId, slot)) {
					p.item.replaceItem(item, amount, p.item.getSlot(shopDef[p.shopId].getCurrency()));

					if (amount == 1) {
						deleteItem(p.shopId, slot, 1);
						//p.frames.setItems(3823, p.items, p.itemsN);
						return;
					}
				}
				p.frames.sendMessage("You do not have enough inventory space.");
				break;
			}*/
			shopDef[p.shopId].itemsN[slot] -= 1;

			if (shopDef[p.shopId].itemsN[slot] < 1) {
				if (shopDef[p.shopId].items[slot] != shopDef[p.shopId].baseItems[slot]) {
					shopDef[p.shopId].items[slot] = -1;
				}
				shopDef[p.shopId].itemsN[slot] = 0;
			}
		}
		for (Player p2 : Engine.players) {
			if (p2 == null || !p2.online) {
				continue;
			}
			//p2.frames.setItems(3900, shopDef[p.shopId].items, shopDef[p.shopId].itemsN);
		}
		//p.frames.setItems(3823, p.items, p.itemsN);
	}

	/**
	 * Sell an item to the currently active shop.
	 * @param p Player to sell item from.
	 * @param item Item to sell.
	 * @param amount Amount of item to sell.
	 */
	public static void sellItem(Player p, int item, int amount) {
		if (p == null || item == -1) {
			return;
		}
		int itemId = (ItemDef.noted(item)) && !hasItem(p.shopId, item) ? ItemDef.getParent(item) : item;

		if (!canSell(p, item)) {
			return;
		}
		for (int i = 0; i < amount; i++) {
			/*if (p.item.getCount(item) < 1) {
				break;
			}
			if (!hasItem(p.shopId, item) && emptySlots(p.shopId) == 0) {
				p.frames.sendMessage("Shop full.");
				return;
			}
			p.item.deleteItem(item, 1);
			p.item.addItem(shopDef[p.shopId].getCurrency(), getSellPrice(p.shopId, itemId));

			if (hasItem(p.shopId, itemId)) {
				int slot = getSlot(p.shopId, itemId);

				if (shopDef[p.shopId].itemsN[slot] + 1 < Integer.MAX_VALUE && shopDef[p.shopId].itemsN[slot] + 1 > 0) {
					shopDef[p.shopId].itemsN[slot] += 1;
				} else {
					shopDef[p.shopId].itemsN[slot] = Integer.MAX_VALUE;

				}
			} else {
				int slot = getFreeSlot(p.shopId);

				shopDef[p.shopId].items[slot] = itemId;

				if (shopDef[p.shopId].itemsN[slot] < Integer.MAX_VALUE) {
					shopDef[p.shopId].itemsN[slot] = 1;
				} else {
					shopDef[p.shopId].itemsN[slot] = Integer.MAX_VALUE;
				}
			}*/
		}
		for (Player p2 : Engine.players) {
			if (p2 == null || !p2.online) {
				continue;
			}
			//p2.frames.setItems(3900, shopDef[p.shopId].items, shopDef[p.shopId].itemsN);
		}
		//p.frames.setItems(3823, p.items, p.itemsN);
	}

	private static boolean addItem(int shopId, int item, int amount) {
		if (emptySlots(shopId) == 0 || amount < 1) {
			return false;
		}
		Shop shop = shopDef[shopId];
		int slot = hasItem(shopId, item) ? getSlot(shopId, item) : getFreeSlot(shopId);

		if (shop.items[slot] + amount > 0) {
			shop.itemsN[slot] = shop.itemsN[slot] + amount;
		} else {
			shop.itemsN[slot] = Integer.MAX_VALUE;
		}

		for (Player p : Engine.players) {
			if (p == null || !p.online) {
				continue;
			}
			if (p.shopId == shopId) {
				//p.frames.setItems(3900, shop.items, shop.itemsN);
			}
		}
		return true;
	}

	private static void deleteItem(int shopId, int slot, int amount) {
		if (amount < 1) {
			return;
		}
		shopDef[shopId].itemsN[slot] -= amount;

		if (shopDef[shopId].itemsN[slot] < 1) {
			if (shopDef[shopId].items[slot] != shopDef[shopId].baseItems[slot]) {
				shopDef[shopId].items[slot] = -1;
			}
			shopDef[shopId].itemsN[slot] = 0;
		}
		for (Player p : Engine.players) {
			if (p == null || !p.online) {
				continue;
			}
			if (p.shopId == shopId) {
				//p.frames.setItems(3900, shopDef[shopId].items, shopDef[shopId].itemsN);
			}
		}
	}

	private static boolean hasItem(int shopId, int item) {
		for (int i = 0; i < shopDef[shopId].items.length; i++) {
			if (shopDef[shopId].items[i] == item) {
				return true;
			}
		}
		return false;
	}

	private static int getSlot(int shopId, int item) {
		for (int i = 0; i < shopDef[shopId].items.length; i++) {
			if (shopDef[shopId].items[i] == item) {
				return i;
			}
		}
		return -1;
	}

	private static int getFreeSlot(int shopId) {
		for (int i = 0; i < shopDef[shopId].items.length; i++) {
			if (shopDef[shopId].items[i] == -1) {
				return i;
			}
		}
		return -1;
	}

	private static int emptySlots(int shopId) {
		int slots = 0;
		for (int i = 0; i < shopDef[shopId].items.length; i++) {
			if (shopDef[shopId].items[i] == -1) {
				slots++;
			}
		}
		return slots;
	}

	/**
	 * Returns the buy price (from shop) for the specified slot.
	 * @param shopId Shop to check.
	 * @param slot Slot to get buy price for.
	 */
	public static int getBuyPrice(int shopId, int slot) {
		if (shopId == -1 || shopDef[shopId] == null) {
			return 0;
		}
		Shop shop = shopDef[shopId];

		double baseValue = ItemDef.getValue(shop.items[slot]);
		/**
		 * Override base value if assigned in shop definition.
		 */
		if (shop.baseItems[slot] != -1 && shop.price[slot] != -1) {
			baseValue = shop.price[slot];
		}
		double priceMult = shop.isGeneral() && shop.baseItems[slot] == -1 ? 1.125 : 1;
		double finalPrice = Math.ceil(baseValue * priceMult) - Math.ceil(baseValue * priceMult * 0.0075)
			* (shop.itemsN[slot] - shop.baseItemsN[slot]);

		if (finalPrice < baseValue * .7) {
			finalPrice = baseValue * .7;
		}
		return (int) finalPrice;
	}

	/**
	 * Returns the sell price (to shop) for the specified item.
	 * @param shopId Shop to check.
	 * @param item Item to get sell price for.
	 */
	public static int getSellPrice(int shopId, int item) {
		if (shopId == -1 || shopDef[shopId] == null) {
			return 0;
		}
		Shop shop = shopDef[shopId];

		if (ItemDef.noted(item) && !hasItem(shopId, item)) {
			item = ItemDef.getParent(item);
		}
		double baseValue = ItemDef.getValue(item);
		if (hasItem(shopId, item) && shop.price[getSlot(shopId, item)] != -1) {
			baseValue = shop.price[getSlot(shopId, item)];
		}
		int quantity = hasItem(shopId, item) ? shop.itemsN[getSlot(shopId, item)] : 0;
		int baseQuantity = hasItem(shopId, item) ? shop.baseItemsN[getSlot(shopId, item)] : 0;
		double valueMult = shop.isGeneral() ? 0.4 : 0.6;
		double finalValue = Math.ceil(baseValue * valueMult) - Math.ceil(baseValue * valueMult) * 0.0127
			* (quantity - baseQuantity);

		if (finalValue < baseValue * 0.05) {
			finalValue = baseValue * 0.05;
		}
		return (int) finalValue;
	}

	/**
	 * Load shop definitions.
	 */
	public static void loadDefinitions() {
		JsonReader reader;

		for (int i = 0; i < shopDef.length; i++) {
			shopDef[i] = null;
		}
		try {
			reader = new JsonReader(new FileReader("./data/shops.json"));
			reader.beginObject();

			int shopId = 0;

			while (reader.hasNext()) {
				String name = reader.nextName();

				if (name.equals("shops")) {
					reader.beginArray();

					while (reader.hasNext()) {
						reader.beginObject();

						while (reader.hasNext()) {
							name = reader.nextName();

							if (shopDef[shopId] == null) {
								shopDef[shopId] = new Shop();

								for (int i = 0; i < shopDef[shopId].items.length; i++) {
									shopDef[shopId].items[i] = -1;
									shopDef[shopId].itemsN[i] = 0;

									shopDef[shopId].baseItems[i] = -1;
									shopDef[shopId].baseItemsN[i] = 0;

									shopDef[shopId].price[i] = -1;
								}
							}
							if (name.equals("name")) {
								shopDef[shopId].name = reader.nextString();
							}
							if (name.equals("items")) {
								reader.beginArray();

								int slot = 0;
								while (reader.hasNext()) {
									reader.beginArray();

									while (reader.hasNext()) {
										int itemId = reader.nextInt();
										shopDef[shopId].items[slot] = itemId;
										shopDef[shopId].baseItems[slot] = itemId;

										int itemAmount = reader.nextInt();
										shopDef[shopId].itemsN[slot] = itemAmount;
										shopDef[shopId].baseItemsN[slot] = itemAmount;

										if (reader.hasNext()) {
											shopDef[shopId].price[slot] = reader.nextInt();
										}
										slot++;
									}
									reader.endArray();
								}
								reader.endArray();
							}
							if (name.equals("currency")) {
								int currency = reader.nextInt();

								if (ItemDef.stackable(currency)) {
									shopDef[shopId].currency = currency;
								}
							}
						}
						reader.endObject();
						shopId++;
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
