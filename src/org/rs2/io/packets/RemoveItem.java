package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.util.Misc;
import org.rs2.util.Sounds;
import org.rs2.world.Shop;

/**
 * Remove one item from an interface. Used for unequipping items, shopping, and
 * banking.
 */
public class RemoveItem implements Packet {

	private final int EQUIPMENT = 1688;
	private final int TO_BANK = 5064, FROM_BANK = 5382;
	private final int DEPOSIT_BOX = 7423;
	private final int FROM_SHOP = 3900, TO_SHOP = 3823;
	private final int TO_TRADE = 3322, FROM_TRADE = 3415;

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int interfaceId = p.stream.readUnsignedWordA();
		int removeSlot = p.stream.readUnsignedWordA();
		int removeId = p.stream.readUnsignedWordA();

		if (p.teleportTask != null) {
			return;
		}
		if (p.isBusy()) {
			return;
		}
		if ((interfaceId == TO_BANK || interfaceId == DEPOSIT_BOX) && p.inventory.getId(removeSlot) == removeId) {
			p.bank.deposit(p.inventory, removeSlot, 1);
		}
		if (interfaceId == FROM_BANK && p.bank.getId(removeSlot) == removeId) {
			p.bank.withdraw(p.inventory, removeSlot, 1);
		}
		if (interfaceId == TO_TRADE && p.inventory.getId(removeSlot) == removeId) {
			//p.trade.offer(removeSlot, 1);
		}
		if (interfaceId == FROM_TRADE && p.trade.getId(removeSlot) == removeId) {
			//p.trade.withdraw(removeSlot, 1);
		}
		
		if (interfaceId == EQUIPMENT && p.equipment.getId(removeSlot) == removeId) {
			p.resetActionTask();
			p.frames.closeAllInterfaces();
			p.equipment.remove(removeSlot);
			p.equipment.sendEquipSound(removeId);
		}

		/*if (p.teleportTask != null) {
			return;
		}
		if (p.preventWalk) {
			return;
		}
		if (interfaceId == FROM_SHOP && Shop.shopDef[p.shopId].items[removeSlot] == removeId) {
			int cost = Shop.getBuyPrice(p.shopId, removeSlot);
			p.frames.sendMessage(ItemDef.getName(removeId) + ": currently costs " + cost + " "
				+ Shop.shopDef[p.shopId].getCurrencyName());
		}
		if (interfaceId == TO_SHOP && p.items[removeSlot] == removeId) {
			if (!Shop.canSell(p, removeId)) {
				return;
			}
			try {
				int value = Shop.getSellPrice(p.shopId, removeId);
				p.frames.sendMessage(ItemDef.getName(removeId) + ": shop will buy for " + value + " "
					+ Shop.shopDef[p.shopId].getCurrencyName() + ".");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (interfaceId == TO_BANK && p.items[removeSlot] == removeId) {
			p.bank.deposit(removeSlot, 1);
		}
		if (interfaceId == FROM_BANK && p.bankItems[removeSlot] == removeId) {
			p.bank.withdraw(removeSlot, 1);
		}
		if (interfaceId == DEPOSIT_BOX && p.items[removeSlot] == removeId) {
			p.bank.deposit(removeSlot, 1);
			p.frames.setItems(7423, p.items, p.itemsN);
		}

		if (interfaceId == TO_TRADE && p.items[removeSlot] == removeId) {
			p.trade.offer(removeSlot, 1);
		}
		if (interfaceId == FROM_TRADE && p.tradeItems[removeSlot] == removeId) {
			p.trade.remove(removeSlot, 1);
		}

		if (interfaceId == EQUIPMENT && p.equipment[removeSlot] == removeId) {
			p.resetActionTask();
			p.frames.closeAllInterfaces();

			if (p.item.addItem(removeId, p.equipmentN[removeSlot])) {
				p.frames.removeEquipment(removeSlot);
				p.equip.calculateBonuses();
				p.frames.sendBonuses();
				p.frames.sendWeight();
				p.frames.sendSound(Sounds.EQUIP_SOUNDS[Misc.random(Sounds.EQUIP_SOUNDS.length - 1)], 0, 10);
			} else {
				p.frames.sendMessage("You don't have enough free inventory space to do that.");
			}
		}*/
	}

}
