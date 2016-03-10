package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.world.Shop;

public class RemoveItem5 implements Packet {

	private final int TO_BANK = 5064, FROM_BANK = 5382;
	private final int DEPOSIT_BOX = 7423;
	private final int FROM_SHOP = 3900, TO_SHOP = 3823;
	private final int TO_TRADE = 3322, FROM_TRADE = 3415;

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int interfaceId = p.stream.readSignedWordBigEndianA();
		int removeId = p.stream.readSignedWordBigEndianA();
		int removeSlot = p.stream.readSignedWordBigEndian();

		
		if ((interfaceId == TO_BANK || interfaceId == DEPOSIT_BOX) && p.inventory.getId(removeSlot) == removeId) {
			p.bank.deposit(p.inventory, removeSlot, 5);
		}
		if (interfaceId == FROM_BANK && p.bank.getId(removeSlot) == removeId) {
			p.bank.withdraw(p.inventory, removeSlot, 5);
		}
		if (interfaceId == TO_TRADE && p.inventory.getId(removeSlot) == removeId) {
			//p.trade.offer(removeSlot, 5);
		}
		if (interfaceId == FROM_TRADE && p.trade.getId(removeSlot) == removeId) {
			//p.trade.withdraw(removeSlot, 5);
		}
		
		/*if (interfaceId == FROM_SHOP && Shop.shopDef[p.shopId].items[removeSlot] == removeId) {
			Shop.buyItem(p, removeId, removeSlot, 1);
		}
		if (interfaceId == TO_SHOP && p.items[removeSlot] == removeId) {
			Shop.sellItem(p, removeId, 1);
		}
		if (interfaceId == TO_BANK && p.items[removeSlot] == removeId) {
			p.bank.deposit(removeSlot, 5);
		}
		if (interfaceId == FROM_BANK && p.bankItems[removeSlot] == removeId) {
			p.bank.withdraw(removeSlot, 5);
		}
		if (interfaceId == DEPOSIT_BOX && p.items[removeSlot] == removeId) {
			p.bank.deposit(removeSlot, 5);
			p.frames.setItems(7423, p.items, p.itemsN);
		}
		if (interfaceId == TO_TRADE && p.items[removeSlot] == removeId) {
			p.trade.offer(removeSlot, 5);
		}
		if (interfaceId == FROM_TRADE && p.tradeItems[removeSlot] == removeId) {
			p.trade.remove(removeSlot, 5);
		}*/
	}

}
