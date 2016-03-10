package org.rs2.model.players.containers;

import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;

public abstract class Container {
	/**
	 * Capacity of this container.
	 */
	private int capacity;
	/**
	 * This container's items.
	 */
	private Item[] items;

	/**
	 * Types of containers.
	 */
	public enum Type {
		STANDARD,
		STACK_ONLY
	}

	/**
	 * This container's type.
	 */
	public Type type = Type.STANDARD;

	/**
	 * If set to false, itemChanged() and itemsChanged() will not trigger.
	 */
	private boolean updating = true;

	/**
	 * Construct a new Container.
	 * @param capacity Capacity of this container.
	 */
	public Container(int capacity) {
		this.capacity = capacity;
		items = new Item[capacity];
	}

	/**
	 * Construct a new Container.
	 * @param capacity Capacity of this container.
	 * @param type Type of this container.
	 */
	public Container(int capacity, Type type) {
		this.capacity = capacity;
		this.type = type;
		items = new Item[capacity];
	}

	/**
	 * Returns true if the item id is valid.
	 * @param id Id to check.
	 */
	public static boolean validId(int id) {
		if (id < 0 || id >= ItemDef.cache.length) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if the specified slot is valid.
	 * @param slot Slot to check.
	 */
	protected boolean validSlot(int slot) {
		if (slot < 0 || slot >= capacity) {
			return false;
		}
		return true;
	}

	/**
	 * Returns container's items.
	 * @return
	 */
	public Item[] getItems() {
		return items;
	}

	/**
	 * Returns the item at the specified slot.
	 * @param slot Slot to get item from.
	 */
	public Item get(int slot) {
		if (!validSlot(slot)) {
			return null;
		}
		return items[slot];
	}

	/**
	 * Sets the item at the specified slot.
	 * @param item Item to set.
	 * @param slot Slot to set item to.
	 */
	public void set(Item item, int slot) {
		if (!validSlot(slot) || (item != null && !validId(item.getId()))) {
			return;
		}
		items[slot] = item;
	}

	/**
	 * Returns a copy of the item at the specified slot.
	 * @param slot Slot to copy item from.
	 */
	public Item copy(int slot) {
		if (items[slot] == null || !validSlot(slot)) {
			return null;
		}
		return new Item(items[slot].getId(), items[slot].getAmt(), items[slot].getHealth());
	}

	/**
	 * Returns a copy of the item specified.
	 * @param item Item to copy.
	 */
	public Item copy(Item item) {
		if (item == null || !validId(item.getId())) {
			return null;
		}
		return new Item(item.getId(), item.getAmt(), item.getHealth());
	}

	/**
	 * Copies all items from this container to the specified array.
	 * @param toArray Array to copy items to.
	 */
	public void copyAll(Item[] toArray) {
		for (int i = 0; i < capacity; i++) {
			if (get(i) == null) {
				continue;
			}
			if (toArray[i] == null) {
				toArray[i] = copy(i);
			}
		}
	}

	/**
	 * Returns the capacity of this container.
	 */
	public int capacity() {
		return capacity;
	}

	/**
	 * Returns the Id of the item at the specified slot, or -1 if it doesn't
	 * exist.
	 * @param slot Slot to get item Id for.
	 */
	public int getId(int slot) {
		if (items[slot] == null) {
			return -1;
		}
		return items[slot].getId();
	}

	/**
	 * Returns the amount of the item at the specified slot, or 0 if it doesn't
	 * exist.
	 * @param slot Slot to get item amount for.
	 */
	public int getAmt(int slot) {
		if (items[slot] == null) {
			return 0;
		}
		return items[slot].getAmt();
	}

	/**
	 * Returns the health of the item at the specified slot, or -1 if it doesn't
	 * exist.
	 * @param slot Slot to get item amount for.
	 */
	public int getHealth(int slot) {
		if (items[slot] == null) {
			return -1;
		}
		return items[slot].getHealth();
	}

	/**
	 * Sets the stack size at the specified slot.
	 * @param amount Amount to set the stack size to.
	 * @param slot Slot to set amount for.
	 */
	public void setAmt(int amount, int slot) {
		items[slot].setAmt(amount);
	}

	/**
	 * Returns true if the container has space for the item.
	 * @param guideId Id of item to check.
	 * @param amount Amount of item.
	 */
	public boolean holdItem(Item item) {
		if (ItemDef.stackable(item.getId()) || type == Type.STACK_ONLY) {
			if (getCount(item.getId()) + item.getAmt() < 0) {
				return false;
			}
			if (!contains(item.getId()) && isFull()) {
				return false;
			}
		} else {
			if (emptySlots() < item.getAmt()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if the container contains the specified item.
	 * @param id Id of the item to look for.
	 */
	public boolean contains(int id) {
		return getSlot(id) != -1;
	}

	/**
	 * Returns the amount of the specified item in the container. Not to be
	 * confused with getAmount(), which returns the amount in a stack.
	 * @param id Id of item to count.
	 */
	public int getCount(int id) {
		if (!validId(id)) {
			return 0;
		}
		int amount = 0;

		for (int i = 0; i < capacity; i++) {
			if (getId(i) == id) {
				amount += getAmt(i);
			}
		}
		return amount;
	}

	/**
	 * Returns the first slot the item can be found at in the container.
	 * @param id Id of the item to check for.
	 */
	public int getSlot(int id) {
		if (!validId(id)) {
			return -1;
		}
		for (int i = 0; i < capacity; i++) {
			if (getId(i) == id) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the last slot the item found at in the trade container.
	 * @param id Id of the item to check for.
	 */
	public int getLastSlot(int id) {
		if (!validId(id)) {
			return -1;
		}
		int slot = -1;

		for (int i = 0; i < capacity; i++) {
			if (getId(i) == id) {
				slot = i;
			}
		}
		return slot;
	}

	/**
	 * Returns the first free slot available.
	 */
	public int getFreeSlot() {
		for (int i = 0; i < capacity; i++) {
			if (items[i] == null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the amount of used slots in the container.
	 */
	public int size() {
		int size = 0;

		for (int i = 0; i < capacity; i++) {
			if (items[i] != null) {
				size++;
			}
		}
		return size;
	}

	/**
	 * Returns the amount of available slots in the container.
	 */
	public int emptySlots() {
		return capacity - size();
	}

	/**
	 * Returns true if the container is empty.
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns true if the container has reached its capacity.
	 */
	public boolean isFull() {
		return size() == capacity();
	}

	/**
	 * Adds an item to the container.
	 * @param item Item to add.
	 */
	public boolean add(Item item) {
		if (item == null || !validId(item.getId()) || item.getAmt() < 1) {
			return false;
		}
		if (!holdItem(item)) {
			return false;
		}
		int amount = item.getAmt();

		if (!ItemDef.stackable(item.getId()) && amount > 1 && type != Type.STACK_ONLY
			&& item.getHealth() == -1) {
			item.setAmt(1);

			for (int i = 0; i < amount; i++) {
				set(item, getFreeSlot());
			}
			if (updating) {
				updateItems();
			}
		} else {
			add(item, getFreeSlot());
		}
		return true;
	}

	/**
	 * Adds a variable number of items to the container.
	 * @param items Items to add.
	 */
	public boolean add(Item... items) {
		updating = false;
		for (int i = 0; i < items.length; i++) {
			if (!add(items[i])) {
				updating = true;
				updateItems();
				return false;
			}
		}
		updating = true;
		updateItems();
		return true;
	}

	/**
	 * Adds an item to the container at the specified slot.
	 * @param item Item to add.
	 * @param slot Slot to add item to.
	 */
	public boolean add(Item item, int slot) {
		if (item == null || !validId(item.getId()) || item.getAmt() < 1) {
			return false;
		}
		if (!ItemDef.stackable(item.getId()) && type != Type.STACK_ONLY) {
			item.setAmt(1);
		}
		if (!holdItem(item)) {
			return false;
		}
		if (get(slot) != null && getId(slot) != item.getId()) {
			slot = getFreeSlot();
		}
		if ((ItemDef.stackable(item.getId()) || (type == Type.STACK_ONLY && item.getHealth() == -1))
			&& contains(item.getId())) {
			slot = getSlot(item.getId());

			if (getAmt(slot) + item.getAmt() > 0) {
				setAmt(items[slot].getAmt() + item.getAmt(), slot);
			} else {
				setAmt(Integer.MAX_VALUE, slot);
			}
		} else {
			set(item, slot);
		}
		if (updating) {
			updateItem(slot);
		}
		return true;
		/*if (!ItemDef.stackable(item.getId()) && type != Type.STACK_ONLY) {
			item.setAmt(1);
		}
		if (!holdItem(item)) {
			return;
		}
		if (get(slot) != null && getId(slot) != item.getId()) {
			slot = getFreeSlot();
		}
		if ((ItemDef.stackable(item.getId()) || type == Type.STACK_ONLY) && contains(item.getId()) && item.getHealth() == -1) {
			slot = getSlot(item.getId());

			if (getAmt(slot) + item.getAmt() > 0) {
				setAmt(items[slot].getAmt() + item.getAmt(), slot);
			} else {
				setAmt(Integer.MAX_VALUE, slot);
			}
		} else {
			set(item, slot);
		}
		if (update) {
			itemChanged(slot);
		}*/
	}

	/**
	 * Removes the first instance of the specified item.
	 * @param guideId Id of item to remove.
	 * @param amount Amount of item to remove.
	 */
	public boolean remove(Item item) {
		if (item == null || !validId(item.getId()) || item.getAmt() < 1) {
			return false;
		}
		if (getCount(item.getId()) < item.getAmt()) {
			return false;
		}
		if (ItemDef.stackable(item.getId()) || type == Type.STACK_ONLY) {
			remove(getSlot(item.getId()), item.getAmt());
		} else {
			int itemAmount = item.getAmt();

			if (itemAmount > 1) {
				for (int i = 0; i < itemAmount; i++) {
					set(null, getSlot(item.getId()));
				}
				if (updating) {
					updateItems();
				}
			} else {
				remove(getSlot(item.getId()), 1);
			}
		}
		return true;
	}

	/**
	 * Removes the item amount at the specified slot.
	 * @param slot Slot of item to remove.
	 * @param amount Amount of item to remove.
	 */
	public void remove(int slot, int amount) {
		if (items[slot] == null || !validSlot(slot) || amount < 1) {
			return;
		}
		if (getAmt(slot) > amount) {
			setAmt(getAmt(slot) - amount, slot);
		} else {
			set(null, slot);
		}
		if (updating) {
			updateItem(slot);
		}
	}

	/**
	 * Removes the item at the specified slot.
	 * @param guideId Id of item to remove.
	 * @param slot Slot of item to remove.
	 */
	public void remove(int slot) {
		if (items[slot] == null || !validSlot(slot)) {
			return;
		}
		set(null, slot);

		if (updating) {
			updateItem(slot);
		}
	}

	/**
	 * Replaces the item at the specified slot.
	 * @param id Id to replace item with.
	 * @param slot Slot to replace.
	 */
	public void replace(int id, int slot) {
		if (!validId(id) || !validSlot(slot)) {
			return;
		}
		if (ItemDef.stackable(id) && contains(id)) {
			return;
		}
		set(get(slot).setId(id), slot);
		updateItem(slot);
	}

	/**
	 * Clears all items from the container;
	 */
	public void clear() {
		items = new Item[capacity];
		updateItems();
	}

	/**
	 * Shifts all items to the left, eliminating any empty slots.
	 */
	public void shift() {
		boolean required = false;

		for (int i = 0; i < capacity; i++) {
			if (get(i) == null && get(i + 1) != null) {
				required = true;
				break;
			}
		}
		if (!required) {
			return;
		}
		for (int i = 0; i < capacity; i++) {
			if (i > 0 && get(i - 1) == null) {
				int newSlot = getFreeSlot();
				set(items[i], newSlot);
				set(null, i);
			}
		}
		updateItems();
	}

	/**
	 * Swaps the items between two slots.
	 * @param slotFrom Slot to swap from.
	 * @param slotTo Slot to swap to.
	 */
	public void swap(int slotFrom, int slotTo) {
		if (!validSlot(slotFrom) || !validSlot(slotTo)) {
			return;
		}
		Item from = items[slotFrom];
		items[slotFrom] = items[slotTo];
		items[slotTo] = from;
		updateItems();
	}

	/**
	 * @param slotFrom
	 * @param slotTo
	 * @param to
	 */
	public void swap(int slotFrom, int slotTo, Container to) {
		if (!validSlot(slotFrom) || !to.validSlot(slotTo)) {
			return;
		}
		Item from = items[slotFrom];
		items[slotFrom] = to.items[slotTo];
		to.items[slotTo] = from;
		updateItems();
		to.updateItems();
	}

	/**
	 * Transfers an item from this container to the specified container.
	 * @param item Item to transfer.
	 * @param to Container to transfer item to.
	 */
	public boolean transfer(Item item, Container to) {
		if (item == null || to == null) {
			return false;
		}
		item = copy(item);
		boolean success = true;

		if (!to.holdItem(item)) {
			if ((ItemDef.stackable(item.getId()) || to.type == Type.STACK_ONLY
				&& item.getHealth() == -1)
				|| to.getCount(item.getId()) + item.getAmt() < 0) {
				item.setAmt(Integer.MAX_VALUE - to.getCount(item.getId()));

			} else if (!ItemDef.stackable(item.getId()) && to.type != Type.STACK_ONLY) {
				item.setAmt(to.emptySlots());
			}
			success = false;
		}
		if (to.add(copy(item))) {
			remove(item);
		}
		return success;
	}

	/**
	 * Transfers an item from this container to the specified container.
	 * @param item Item to transfer.
	 * @param amount Amount to transfer.
	 * @param to Container to transfer item to.
	 */
	public boolean transfer(Item item, int amount, Container to) {
		if (item == null || to == null || amount < 1) {
			return false;
		}
		if (amount > getCount(item.getId())) {
			amount = getCount(item.getId());
		}
		return transfer(copy(item).setAmt(amount), to);
	}

	/**
	 * Transfers an item from this container to the specific container.
	 * @param slot Slot to get item from.
	 * @param to Container to transfer to.
	 */
	public boolean transfer(int slot, Container to) {
		return transfer(get(slot), to);
	}

	/**
	 * Transfers all items from this container to the specified container.
	 * @param to Container to transfer to.
	 */
	public boolean transferAll(Container to) {
		boolean success = true;
		updating = false;

		for (int i = 0; i < capacity; i++) {
			if (get(i) == null) {
				continue;
			}
			if (!transfer(get(i), getCount(getId(i)), to)) {
				success = false;
			}
		}
		updating = true;
		updateItems();
		to.updateItems();
		return success;
	}

	/**
	 * Called when a single item is changed.
	 * @param slot Slot to update for.
	 */
	abstract void updateItem(int slot);

	/**
	 * Called when multiple items have been changed.
	 */
	abstract void updateItems();

}
