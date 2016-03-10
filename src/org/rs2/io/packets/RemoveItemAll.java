package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.world.Shop;

public class RemoveItemAll implements Packet {

	private final int TO_BANK = 5064, FROM_BANK = 5382;
	private final int DEPOSIT_BOX = 7423;
	private final int FROM_SHOP = 3900, TO_SHOP = 3823;
	private final int TO_TRADE = 3322, FROM_TRADE = 3415;

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int removeSlot = p.stream.readUnsignedWordA();
		int interfaceId = p.stream.readUnsignedWord();
		int removeId = p.stream.readUnsignedWordA();

		if ((interfaceId == TO_BANK || interfaceId == DEPOSIT_BOX)
			&& p.inventory.getId(removeSlot) == removeId) {
			p.bank.deposit(p.inventory, removeSlot, p.inventory.getCount(removeId));
		}
		if (interfaceId == FROM_BANK && p.bank.getId(removeSlot) == removeId) {
			p.bank.withdraw(p.inventory, removeSlot, p.bank.getAmt(removeSlot));
		}
		if (interfaceId == TO_TRADE && p.inventory.getId(removeSlot) == removeId) {
			//p.trade.offer(removeSlot, p.inventory.getCount(removeId));
		}
		if (interfaceId == FROM_TRADE && p.trade.getId(removeSlot) == removeId) {
			//p.trade.withdraw(removeSlot, p.trade.getCount(removeId));
		}
	}

}
