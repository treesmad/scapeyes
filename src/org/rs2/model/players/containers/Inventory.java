package org.rs2.model.players.containers;

import org.rs2.Engine;
import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.world.GroundItem;

public class Inventory extends Container {
	/**
	 * Player this container belongs to.
	 */
	private Player p;

	/**
	 * Construct a new Inventory container.
	 * @param p Player to assign this container to.
	 */
	public Inventory(Player p) {
		super(28);
		this.p = p;
	}

	/**
	 * Drops an item from the inventory onto the ground.
	 * @param slot Slot to drop item from.
	 */
	public void drop(int slot) {
		Item item = copy(slot);

		if (ItemDef.destroyable(item.getId())) {
			return;
		}
		if (ItemDef.getDropItem(item.getId()) != -1) {
			item.setId(ItemDef.getDropItem(item.getId()));
		}
		remove(slot);

		if (item.getId() == 2412 || item.getId() == 2413 || item.getId() == 2414) {
			p.frames.sendMessage(ItemDef.getName(item.getId()).split(" ")[0]
				+ " reclaims the cape as it touches the ground.");
			return;
		}
		Engine.ground.addItem(new GroundItem(item, p.absX, p.absY, p.heightLevel, p.playerName));
	}

	@Override
	void updateItem(int slot) {
		if (p.interfaceChildId == 197) {
			p.frames.setItem(7423, slot, this);
		}
		if (p.interfaceChildId == 5063) {
			p.frames.setItem(5064, slot, this);
		}
		if (p.interfaceChildId == 3321) {
			p.frames.setItem(3322, slot, this);
		}
		p.frames.setItem(3214, slot, this);

		if (!ItemDef.stackable(getId(slot))) {
			p.frames.sendWeight();
		}
	}

	@Override
	void updateItems() {
		if (p.interfaceChildId == 197) {
			p.frames.setItems(7423, this);
		}
		if (p.interfaceChildId == 5063) {
			p.frames.setItems(5064, this);
		}
		if (p.interfaceChildId == 3321) {
			p.frames.setItems(3322, this);
		}
		p.frames.setItems(3214, this);
		p.frames.sendWeight();
	}

}
