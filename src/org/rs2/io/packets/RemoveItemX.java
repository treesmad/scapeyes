package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

public class RemoveItemX implements Packet {

	private static final int REMOVE_ITEM_X = 135, REMOVE_ITEM_X2 = 208;

	private static final int TO_BANK = 5064, FROM_BANK = 5382;
	private static final int DEPOSIT_BOX = 7423;
	private static final int TO_TRADE = 3322, FROM_TRADE = 3415;

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		if (id == REMOVE_ITEM_X) {
			p.removeXSlot = p.stream.readSignedWordBigEndian();
			p.removeXInterfaceId = p.stream.readUnsignedWordA();
			p.removeXId = p.stream.readSignedWordBigEndian();

			if ((p.removeXInterfaceId == TO_BANK || p.removeXInterfaceId == TO_TRADE || p.removeXInterfaceId == DEPOSIT_BOX)
				&& p.inventory.getId(p.removeXSlot) != p.removeXId) {
				return;
			}
			if (p.removeXInterfaceId == FROM_BANK && p.bank.getId(p.removeXSlot) != p.removeXId) {
				return;
			}
			if (p.removeXInterfaceId == FROM_TRADE && p.trade.getId(p.removeXSlot) != p.removeXId) {
				return;
			}
			p.stream.createFrame(27);
		}
		if (id == REMOVE_ITEM_X2) {
			int amount = p.stream.readDWord();

			if (amount == -1) {
				amount = Integer.MAX_VALUE;
			}
			if ((p.removeXInterfaceId == TO_BANK || p.removeXInterfaceId == DEPOSIT_BOX)
				&& p.inventory.getId(p.removeXSlot) == p.removeXId) {
				p.bank.deposit(p.inventory, p.removeXSlot, amount);
			}
			if (p.removeXInterfaceId == FROM_BANK && p.bank.getId(p.removeXSlot) == p.removeXId) {
				p.bank.withdraw(p.inventory, p.removeXSlot, amount);
			}
			if (p.removeXInterfaceId == TO_TRADE && p.inventory.getId(p.removeXSlot) == p.removeXId) {
				//p.trade.offer(p.removeXSlot, amount);
			}
			if (p.removeXInterfaceId == FROM_TRADE && p.trade.getId(p.removeXSlot) == p.removeXId) {
				//p.trade.withdraw(p.removeXSlot, amount);
			}

			/*if (p.removeXInterfaceId == TO_BANK && p.items[p.removeXSlot] == p.removeXId) {
				p.bank.deposit(p.removeXSlot, amount);
			}
			if (p.removeXInterfaceId == FROM_BANK && p.bankItems[p.removeXSlot] == p.removeXId) {
				p.bank.withdraw(p.removeXSlot, amount);
			}
			if (p.removeXInterfaceId == DEPOSIT_BOX && p.items[p.removeXSlot] == p.removeXId) {
				p.bank.deposit(p.removeXSlot, amount);
				p.frames.setItems(7423, p.items, p.itemsN);
			}
			if (p.removeXInterfaceId == TO_TRADE && p.items[p.removeXSlot] == p.removeXId) {
				p.trade.offer(p.removeXSlot, amount);
			}
			if (p.removeXInterfaceId == FROM_TRADE && p.tradeItems[p.removeXSlot] == p.removeXId) {
				p.trade.remove(p.removeXSlot, amount);
			}*/
		}
	}

}
