package org.rs2.world;

import org.rs2.Engine;
import org.rs2.model.items.Item;

public class GroundItem {
	/**
	 * Item object.
	 */
	private Item item;
	/**
	 * Item coordinates.
	 */
	private int x, y;
	/**
	 * Item height.
	 */
	private int height;
	/**
	 * Time when item was created.
	 */
	private int time = Engine.currentTicks();
	/**
	 * Owner of this item.
	 */
	private String owner = null;

	/**
	 * Types of ground items.
	 */
	public enum Type {
		/**
		 * Item was dropped by a player, or dropped for a player.
		 */
		DROPPED,
		/**
		 * Item respawns on a timer if it doesn't exist.
		 */
		RESPAWNING
	}
	/**
	 * Type of this item.
	 */
	public Type type = Type.DROPPED;

	/**
	 * Set to true if the item is visible to all players in the region.
	 */
	private boolean pub;

	/**
	 * Construct a new GroundItem.
	 * @param item Item to add to the ground.
	 * @param x X coordinate of this item.
	 * @param y Y coordinate of this item.
	 */
	public GroundItem(Item item, int x, int y) {
		this.item = item;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Construct a new GroundItem.
	 * @param item Item to add to the ground.
	 * @param x X coordinate of this item.
	 * @param y Y coordinate of this item.
	 * @param height Height level of this item.
	 */
	public GroundItem(Item item, int x, int y, int height) {
		this.item = item;
		this.x = x;
		this.y = y;
		this.height = height;
	}
	
	/**
	 * Construct a new GroundItem.
	 * @param item Item to add to the ground.
	 * @param x X coordinate of this item.
	 * @param y Y coordinate of this item.
	 * @param height Height level of this item.
	 * @param owner Name of owner.
	 */
	public GroundItem(Item item, int x, int y, int height, String owner) {
		this.item = item;
		this.x = x;
		this.y = y;
		this.height = height;
		this.owner = owner;
	}

	/**
	 * Construct a new GroundItem.
	 * @param item Item to add to the ground.
	 * @param x X coordinate of this item.
	 * @param y Y coordinate of this item.
	 * @param type Type of this item.
	 */
	public GroundItem(Item item, int x, int y, Type type) {
		this.item = item;
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	/**
	 * Construct a new GroundItem.
	 * @param item Item to add to the ground.
	 * @param x X coordinate of this item.
	 * @param y Y coordinate of this item.
	 * @param height Height level of this item.
	 * @param type Type of this item.
	 */
	public GroundItem(Item item, int x, int y, int height, Type type) {
		this.item = item;
		this.x = x;
		this.y = y;
		this.height = height;
		this.type = type;
	}

	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getTime() {
		return time;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setTime(int time) {
		this.time = time;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public boolean isPublic() {
		return pub;
	}

	public void setPublic(boolean pub) {
		this.pub = pub;
	}

}
