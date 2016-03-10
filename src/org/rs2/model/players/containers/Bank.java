package org.rs2.model.players.containers;

import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;

public class Bank extends Container {
	/**
	 * Player this container belongs to.
	 */
	private Player p;

	/**
	 * Construct a new Bank container.
	 * @param p Player to assign this container to.
	 */
	public Bank(Player p) {
		super(352, Type.STACK_ONLY);
		this.p = p;
	}

	/**
	 * Deposits an item from the specified container to the bank.
	 * @param from Container to deposit item from.
	 * @param slot Slot to deposit from.
	 * @param amount Amount to deposit.
	 */
	public void deposit(Container from, int slot, int amount) {
		Item item = from.get(slot);

		if (item == null || amount < 1) {
			return;
		}
		if (!from.transfer(item, amount, this)) {
			p.frames.sendMessage("Bank full!");
		}
	}

	/**
	 * Withdraws an item from the bank to a container.
	 * @param to Container to withdraw to.
	 * @param slot Slot to withdraw from.
	 * @param amount Amount to withdraw.
	 */
	public void withdraw(Container to, int slot, int amount) {
		Item item = get(slot);

		if (item == null || amount < 1) {
			return;
		}
		if (!transfer(item, amount, to)) {
			if (to == p.inventory) {
				p.frames
					.sendMessage("You don't have enough inventory space to withdraw that many.");
			}
		}
	}

	@Override
	public boolean transfer(Item item, int amount, Container to) {
		if (item == null || to == null || amount < 1) {
			return false;
		}
		boolean success = true;

		if (amount > getCount(item.getId())) {
			amount = getCount(item.getId());
		}
		item = copy(item).setAmt(amount);

		if (p.withdrawNoted) {
			if (ItemDef.noteable(item.getId())) {
				item = item.toNote();
			} else {
				p.frames.sendMessage("This item cannot be withdrawn as a note.");
			}
		}
		if (!to.holdItem(item)) {
			if ((ItemDef.stackable(item.getId()) || to.type == Type.STACK_ONLY)
				|| to.getCount(item.getId()) + item.getAmt() < 0) {
				item.setAmt(Integer.MAX_VALUE - to.getCount(item.getId()));
			} else if (!ItemDef.stackable(item.getId()) && to.type != Type.STACK_ONLY) {
				item.setAmt(to.emptySlots());
			}
			success = false;
		}
		if (to.add(copy(item))) {
			remove(item.toItem());
		}
		return success;
	}

	/**
	 * Moves an item from one slot and inserts it into another, shifting items
	 * left or right.
	 * @param slotFrom Slot to move the item from.
	 * @param slotTo Slot to insert the item at.
	 */
	public void insert(int slotFrom, int slotTo) {
		if (!validSlot(slotFrom) || !validSlot(slotTo)) {
			return;
		}
		if (get(slotTo) == null) {
			swap(slotFrom, slotTo);
		}
		int tempFrom = slotFrom;

		for (int tempTo = slotTo; tempFrom != tempTo;) {
			if (tempFrom > tempTo) {
				Item from = get(tempFrom);
				set(get(tempFrom - 1), tempFrom);
				set(from, tempFrom - 1);
				tempFrom--;
			} else if (tempFrom < tempTo) {
				Item from = get(tempFrom);
				set(get(tempFrom + 1), tempFrom);
				set(from, tempFrom + 1);
				tempFrom++;
			}
		}
		updateItems();
	}

	@Override
	public boolean holdItem(Item item) {
		if (item == null) {
			return false;
		}
		item = copy(item).toItem();
		return super.holdItem(item);
	}

	@Override
	public int getCount(int id) {
		if (ItemDef.noted(id)) {
			id = ItemDef.getParent(id);
		}
		return super.getCount(id);
	}

	@Override
	public boolean add(Item item) {
		if (item == null) {
			return false;
		}
		item = copy(item).toItem();
		return super.add(item);
	}

	@Override
	void updateItem(int slot) {
		p.frames.setItem(5382, slot, this);
	}

	@Override
	void updateItems() {
		p.frames.setItems(5382, this);
	}

}
