package org.rs2.model.items;

public class Item {
	/**
	 * Id of the item.
	 */
	private int id = -1;
	/**
	 * Amount of the item stack.
	 */
	private int amount = 1;
	/**
	 * Health of the item.
	 */
	private int health = -1;

	/**
	 * Construct a new Item.
	 * @param id Id of this item.
	 */
	public Item(int id) {
		this.id = id;
	}

	/**
	 * Construct a new Item.
	 * @param id Id of this item.
	 * @param amount Amount of this item.
	 */
	public Item(int id, int amount) {
		this.id = id;
		this.amount = amount;
	}

	/**
	 * Construct a new Item.
	 * @param id Id of this item.
	 * @param amount Amount of this item.
	 * @param health Health of this item.
	 */
	public Item(int id, int amount, int health) {
		this.id = id;
		this.amount = amount;
		this.health = health;
	}

	public int getId() {
		return id;
	}

	public Item setId(int id) {
		this.id = id;
		return this;
	}

	public int getAmt() {
		return amount;
	}

	public Item setAmt(int amount) {
		this.amount = amount;
		return this;
	}

	public int getHealth() {
		return health;
	}

	public Item setHealth(int health) {
		this.health = health;
		return this;
	}

	/**
	 * Converts this item into its noted format if possible.
	 */
	public Item toNote() {
		if (!ItemDef.noteable(id)) {
			return this;
		}
		return setId(ItemDef.getChild(id));
	}

	/**
	 * Converts item into its parent if it is noted.
	 */
	public Item toItem() {
		if (!ItemDef.noted(id)) {
			return this;
		}
		return setId(ItemDef.getParent(id));
	}

	/**
	 * Returns whether or not the two items are the same.
	 * @param item Item to compare with.
	 */
	public boolean equals(Item item) {
		return getId() == item.getId() && getAmt() == item.getAmt();
	}

}
