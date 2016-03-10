package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;

public class ItemMove implements Packet {

	private static final int INVENTORY = 3214, INVENTORY_BANK = 5064;
	private static final int DEPOSIT_BOX = 7423;
	private static final int BANK = 5382;

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int interfaceId = p.stream.readSignedWordBigEndianA();
		boolean insertMode = p.stream.readSignedByteC() == 1;
		int itemFrom = p.stream.readSignedWordBigEndianA();
		int itemTo = p.stream.readSignedWordBigEndian();

		if (interfaceId == INVENTORY || interfaceId == INVENTORY_BANK || interfaceId == DEPOSIT_BOX) {
			p.inventory.swap(itemFrom, itemTo);
		}
		if (interfaceId == BANK) {
			if (insertMode) {
				p.bank.insert(itemFrom, itemTo);
			} else {
				p.bank.swap(itemFrom, itemTo);
			}
		}

		/*if (interfaceId == INVENTORY || interfaceId == INVENTORY_2 || interfaceId == DEPOSIT_BOX) {
			int tempItem = p.items[itemFrom];
			int tempAmount = p.itemsN[itemFrom];

			p.items[itemFrom] = p.items[itemTo];
			p.itemsN[itemFrom] = p.itemsN[itemTo];
			p.items[itemTo] = tempItem;
			p.itemsN[itemTo] = tempAmount;

			p.frames.setItems(interfaceId, p.items, p.itemsN);
			if (interfaceId == DEPOSIT_BOX) {
				p.frames.setItems(INVENTORY, p.items, p.itemsN);
			}
		}
		if (interfaceId == BANK) {
			if (insertMode) {
				if (itemTo != -1 && p.bankItems[itemTo] == -1) {
					swapBankItem(p, itemFrom, itemTo);
				}
				int tempFrom = itemFrom;

				for (int tempTo = itemTo; tempFrom != tempTo;) {
					if (tempFrom > tempTo) {
						swapBankItem(p, tempFrom, tempFrom - 1);
						tempFrom--;
					} else if (tempFrom < tempTo) {
						swapBankItem(p, tempFrom, tempFrom + 1);
						tempFrom++;
					}
				}
			} else {
				swapBankItem(p, itemFrom, itemTo);
			}
			p.frames.setItems(interfaceId, p.bankItems, p.bankItemsN);
		}*/
	}

	/*private void swapBankItem(Player p, int from, int to) {
		int tempItem = p.bankItems[from];
		int tempAmount = p.bankItemsN[from];
		p.bankItems[from] = p.bankItems[to];
		p.bankItemsN[from] = p.bankItemsN[to];
		p.bankItems[to] = tempItem;
		p.bankItemsN[to] = tempAmount;
	}*/

}
