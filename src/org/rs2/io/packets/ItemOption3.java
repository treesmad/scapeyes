package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.content.DialogueOption;
import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;
import org.rs2.model.items.types.Potions;
import org.rs2.model.players.Player;
import org.rs2.util.TextUtils;

public class ItemOption3 implements Packet {

	@Override
	public void packet(final Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int interfaceId = p.stream.readUnsignedWordBigEndianA();
		int itemSlot = p.stream.readUnsignedWordBigEndianA() - 128;
		int itemId = p.stream.readUnsignedWordA();

		if (p.inventory.getId(itemSlot) != itemId) {
			return;
		}
		if (p.isBusy()) {
			return;
		}
		String name = ItemDef.getName(itemId).toLowerCase();

		/**
		 * Emptying potions/vial of water.
		 */
		if (Potions.isPotion(itemId) || name.contains("vial")) {
			p.inventory.replace(229, itemSlot);
			p.frames.sendMessage("You empty the vial.");
			return;
		}
		
		if (name.contains("amulet of glory")) {
			if (itemId == 1704) {
				p.frames.sendMessage("Your amulet of glory is out of charges, you must recharge it.");
				return;
			}
			/*p.dialogue.sendOptions(new DialogueEvent()
				.add(new DialogueButton("Edgeville", true) {
					public void click() {
						p.magic.teleport(3087, 3491, 0);
						gloryDrain(p, itemId, itemSlot);
					}
				})
				.add(new DialogueButton("Karamja", true) {
					public void click() {
						p.magic.teleport(2774, 3126, 0);
						gloryDrain(p, itemId, itemSlot);
					}
				})
				.add(new DialogueButton("Draynor Village", true) {
					public void click() {
						p.magic.teleport(3109, 3295, 0);
						gloryDrain(p, itemId, itemSlot);
					}
				})
				.add(new DialogueButton("Al-Kharid", true) {
					public void click() {
						p.magic.teleport(3298, 3216, 0);
						gloryDrain(p, itemId, itemSlot);
					}
				})
			);*/
			return;
		}
		
		if (name.contains("ring of duelling")) {
			/*p.dialogue.sendOptions(new DialogueEvent()
				.add(new DialogueButton("Duel Arena", true) {
					public void click() {
						p.magic.teleport(3321, 3236, 0);
						duelDrain(p, itemId, itemSlot);
					}
				})
				.add(new DialogueButton("Duel Arena", true) {
					public void click() {
						p.magic.teleport(2441, 3090, 0);
						duelDrain(p, itemId, itemSlot);
					}
				})
				
				
			);*/
			return;
		}
		
		p.frames.sendMessage("Nothing interesting happens.");
	}

	private void gloryDrain(Player p, int itemId, int itemSlot) {
		int newItem = (itemId > 1704) ? itemId - 2 : -1;
		p.inventory.remove(itemSlot, 1);
		p.inventory.add(new Item(newItem, 1));

		int charges = TextUtils.containsNumbers(ItemDef.getName(newItem)) ? TextUtils
			.extractNumbers(ItemDef.getName(newItem)) : 0;

		if (charges > 0) {
			p.frames.sendMessage("Your amulet of glory has " + TextUtils.numerName(charges)
				+ " use" + (charges > 1 ? "s" : "") + " left.");
		} else {
			p.frames.sendMessage("Your amulet of glory has run out of charges.");
		}
	}

	private void duelDrain(Player p, int itemId, int itemSlot) {
		int newItem = (itemId < 2566) ? itemId + 2 : -1;
		p.inventory.remove(itemSlot, 1);
		p.inventory.add(new Item(newItem, 1));

		int charges = TextUtils.containsNumbers(ItemDef.getName(newItem)) ? TextUtils
			.extractNumbers(ItemDef.getName(newItem)) : 0;

		if (charges > 0) {
			p.frames.sendMessage("Your ring of duelling has " + TextUtils.numerName(charges)
				+ " use" + (charges > 1 ? "s" : "") + " left.");
		} else {
			p.frames.sendMessage("Your ring of duelling crumbles into dust.");
		}
	}

}