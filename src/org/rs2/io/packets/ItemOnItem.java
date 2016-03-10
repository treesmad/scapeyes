package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.items.Item;
import org.rs2.model.items.types.Potions;
import org.rs2.model.players.Player;
import org.rs2.model.players.skills.Firemaking.Logs;

/**
 * Handles using an item on another item in your inventory, such as fletching or
 * potion decanting.
 */
public class ItemOnItem implements Packet {

	private int itemId1, itemSlot1;
	private int itemId2, itemSlot2;

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		itemSlot1 = p.stream.readUnsignedWord();
		itemSlot2 = p.stream.readSignedWordA();
		itemId1 = p.stream.readSignedWordBigEndianA();
		int interfaceId = p.stream.readUnsignedWord();
		itemId2 = p.stream.readSignedWordBigEndian();

		if (p.inventory.getId(itemSlot1) != itemId1 || p.inventory.getId(itemSlot2) != itemId2) {
			return;
		}
		p.resetActionTask();
		p.requestWalk(0, 0);
		p.frames.closeAllInterfaces();

		/**
		 * Firemaking
		 */
		for (Logs l : Logs.values()) {
			if (itemId1 == l.getId() && itemId2 == 590) {
				p.firemaking.start(itemId1, itemSlot1);
				return;
			}
			if (itemId2 == l.getId() && itemId1 == 590) {
				p.firemaking.start(itemId2, itemSlot2);
				return;
			}
		}

		/**
		 * Decanting
		 */
		for (Potions potion : Potions.values()) {
			boolean decant = false;

			if (itemId2 == potion.getId(0) && itemId1 == potion.getId(0)) {
				p.inventory.replace(229, itemSlot2);
				p.inventory.replace(potion.getId(1), itemSlot1);
				decant = true;
			} else if (itemId2 == potion.getId(0) && itemId1 == potion.getId(1)) {
				p.inventory.replace(229, itemSlot2);
				p.inventory.replace(potion.getId(2), itemSlot1);
				decant = true;
			} else if (itemId2 == potion.getId(0) && itemId1 == potion.getId(2)) {
				p.inventory.replace(229, itemSlot2);
				p.inventory.replace(potion.getId(3), itemSlot1);
				decant = true;
			} else if (itemId2 == potion.getId(1) && itemId1 == potion.getId(0)) {
				p.inventory.replace(229, itemSlot2);
				p.inventory.replace(potion.getId(2), itemSlot1);
				decant = true;
			} else if (itemId2 == potion.getId(1) && itemId1 == potion.getId(1)) {
				p.inventory.replace(229, itemSlot2);
				p.inventory.replace(potion.getId(3), itemSlot1);
				decant = true;
			} else if (itemId2 == potion.getId(2) && itemId1 == potion.getId(0)) {
				p.inventory.replace(229, itemSlot2);
				p.inventory.replace(potion.getId(3), itemSlot1);
				decant = true;
			} else if (itemId2 == potion.getId(1) && itemId1 == potion.getId(2)) {
				p.inventory.replace(potion.getId(0), itemSlot2);
				p.inventory.replace(potion.getId(3), itemSlot1);
				decant = true;
			} else if (itemId2 == potion.getId(2) && itemId1 == potion.getId(1)) {
				p.inventory.replace(potion.getId(0), itemSlot2);
				p.inventory.replace(potion.getId(3), itemSlot1);
				decant = true;
			} else if (itemId2 == potion.getId(2) && itemId1 == potion.getId(2)) {
				p.inventory.replace(potion.getId(1), itemSlot2);
				p.inventory.replace(potion.getId(3), itemSlot1);
				decant = true;
			}
			if (decant) {
				p.frames.sendMessage("You pour from one container into the other.");
				return;
			}
		}

		// Tinderbox + Candle
		if (itemOnItem(590, 36)) {
			p.inventory.remove(new Item(36));
			p.inventory.add(new Item(33));
			p.frames.sendMessage("You ignite the candle.");
			return;
		}
		// Tinderbox + Black Candle
		if (itemOnItem(590, 38)) {
			p.inventory.remove(new Item(38));
			p.inventory.add(new Item(33));
			p.frames.sendMessage("You ignite the candle.");
			return;
		}

		/*if (itemId2 == potion.getId()[0] && itemId1 == potion.getId()[0]) {
			p.item.replaceItem(229, 1, itemSlot2);
			p.item.replaceItem(potion.getId()[1], 1, itemSlot1);
			decant = true;
		} else if (itemId2 == potion.getId()[0] && itemId1 == potion.getId()[1]) {
			p.item.replaceItem(229, 1, itemSlot2);
			p.item.replaceItem(potion.getId()[2], 1, itemSlot1);
			decant = true;
		} else if (itemId2 == potion.getId()[0] && itemId1 == potion.getId()[2]) {
			p.item.replaceItem(229, 1, itemSlot2);
			p.item.replaceItem(potion.getId()[3], 1, itemSlot1);
			decant = true;
		} else if (itemId2 == potion.getId()[1] && itemId1 == potion.getId()[0]) {
			p.item.replaceItem(229, 1, itemSlot2);
			p.item.replaceItem(potion.getId()[2], 1, itemSlot1);
			decant = true;
		} else if (itemId2 == potion.getId()[1] && itemId1 == potion.getId()[1]) {
			p.item.replaceItem(229, 1, itemSlot2);
			p.item.replaceItem(potion.getId()[3], 1, itemSlot1);
			decant = true;
		} else if (itemId2 == potion.getId()[1] && itemId1 == potion.getId()[2]) {
			p.item.replaceItem(potion.getId()[0], 1, itemSlot2);
			p.item.replaceItem(potion.getId()[3], 1, itemSlot1);
			decant = true;
		} else if (itemId2 == potion.getId()[2] && itemId1 == potion.getId()[0]) {
			p.item.replaceItem(229, 1, itemSlot2);
			p.item.replaceItem(potion.getId()[3], 1, itemSlot1);
			decant = true;
		} else if (itemId2 == potion.getId()[2] && itemId1 == potion.getId()[1]) {
			p.item.replaceItem(potion.getId()[0], 1, itemSlot2);
			p.item.replaceItem(potion.getId()[3], 1, itemSlot1);
			decant = true;
		} else if (itemId2 == potion.getId()[2] && itemId1 == potion.getId()[2]) {
			p.item.replaceItem(potion.getId()[1], 1, itemSlot2);
			p.item.replaceItem(potion.getId()[3], 1, itemSlot1);
			decant = true;
		}
		if (decant) {
			p.frames.sendMessage("You pour from one container into the other.");
			return;
		}
		}*/

		// Tinderbox + Candle
		/*if (itemOnItem(590, 36)) {
			p.item.deleteItem(36, 1);
			p.item.addItem(33, 1);
			p.frames.sendMessage("You ignite the candle.");
			return;
		}
		// Tinderbox + Black Candle
		if (itemOnItem(590, 38)) {
			p.item.deleteItem(38, 1);
			p.item.addItem(33, 1);
			p.frames.sendMessage("You ignite the candle.");
			return;
		}*/

		p.frames.sendMessage("Nothing interesting happens.");
	}

	/**
	 * Use item on an item.
	 * @param itemId1 Item using ona/item being used on.
	 * @param itemId2 Item being used on/Item using on.
	 */
	public boolean itemOnItem(int itemId1, int itemId2) {
		if ((this.itemId2 == itemId1 && this.itemId1 == itemId2)
			|| (this.itemId2 == itemId2 && this.itemId1 == itemId1)) {
			return true;
		}
		return false;
	}

}
