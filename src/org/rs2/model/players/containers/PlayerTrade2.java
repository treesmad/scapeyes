package org.rs2.model.players.containers;

import org.rs2.Engine;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.util.TextUtils;

public class PlayerTrade2 {
	/**
	 * Player this class belongs to.
	 */
	private Player p;

	/**
	 * Construct a new PlayerTrade.
	 * @param p Player to assign this class to.
	 */
	public PlayerTrade2(Player p) {
		this.p = p;
	}

	/**
	 * Trading stages.
	 */
	public enum TradeStage {
		/**
		 * Player not currently in a trade.
		 */
		NOT_TRADING,
		/**
		 * Player is on the first screen.
		 */
		TRADING,
		/**
		 * Player accepted the first screen.
		 */
		TRADE_ACCEPTED,
		/**
		 * Player is on the second screen.
		 */
		CONFIRMING,
		/**
		 * Player accepted the second screen.
		 */
		CONFIRM_ACCEPTED
	}
	/**
	 * Current trade stage.
	 */
	public TradeStage stage = TradeStage.NOT_TRADING;

	/**
	 * Accepting the first trade screen.
	 */
	public void accept() {
		/*if (p.tradeId == -1 || stage != TradeStage.TRADING) {
			return;
		}
		Player op = Engine.players[p.tradeId];

		if (op != null) {
			for (int i = 0; i < op.tradeItems.length; i++) {
				if (op.tradeItems[i] == -1) {
					continue;
				}
				if (p.item.getCount(op.tradeItems[i]) + op.trade.getCount(op.tradeItems[i]) < 0
					|| p.item.emptySlots() < (28 - op.trade.emptySlots())) {
					p.frames.sendMessage("You do not have enough inventory space to complete this trade.");
					return;
				}
			}
			for (int i = 0; i < p.tradeItems.length; i++) {
				if (p.tradeItems[i] == -1) {
					continue;
				}
				if (op.item.getCount(p.tradeItems[i]) + p.trade.getCount(p.tradeItems[i]) < 0
					|| op.item.emptySlots() < (28 - p.trade.emptySlots())) {
					p.frames
						.sendMessage(op.playerName + " does not have enough inventory space to complete the trade.");
					return;
				}
			}
			p.frames.setString("Waiting for other player...", 3431);
			op.frames.setString("Other player has accepted.", 3431);

			setStage(TradeStage.TRADE_ACCEPTED);

			if (op.trade.stage == TradeStage.TRADE_ACCEPTED) {
				setStage(TradeStage.CONFIRMING);
				op.trade.setStage(TradeStage.CONFIRMING);
				
				p.frames.sendTradeConfirm();
				op.frames.sendTradeConfirm();
			}
		}*/
	}

	/**
	 * Declines trade and returns items to owners.
	 */
	public void decline() {
		/*if (p.tradeId == -1) {
			return;
		}
		Player op = Engine.players[p.tradeId];

		if (op != null) {
			for (int i = 0; i < p.tradeItems.length; i++) {
				p.item.addItem(p.tradeItems[i], getCount(p.tradeItems[i]));
				deleteItem(p.tradeItems[i], getCount(p.tradeItems[i]), i);
			}
			for (int i = 0; i < op.tradeItems.length; i++) {
				op.item.addItem(op.tradeItems[i], op.trade.getCount(op.tradeItems[i]));
				op.trade.deleteItem(op.tradeItems[i], op.trade.getCount(op.tradeItems[i]), i);
			}
			op.frames.sendMessage("Player declined trade.");
			resetTrade();
		}*/
	}

	/**
	 * Accepting the second trade screen.
	 */
	public void confirm() {
		/*if (p.tradeId == -1 || stage != TradeStage.CONFIRMING) {
			return;
		}
		Player op = Engine.players[p.tradeId];

		if (op != null) {
			p.frames.setString("Waiting for other player...", 3535);
			op.frames.setString("Other player has accepted.", 3535);

			setStage(TradeStage.CONFIRM_ACCEPTED);
			if (op.trade.stage == TradeStage.CONFIRM_ACCEPTED) {
				for (int i = 0; i < op.tradeItems.length; i++) {
					p.item.addItem(op.tradeItems[i], op.trade.getCount(op.tradeItems[i]));
					op.trade.deleteItem(op.tradeItems[i], op.trade.getCount(op.tradeItems[i]), i);
				}
				for (int i = 0; i < p.tradeItems.length; i++) {
					op.item.addItem(p.tradeItems[i], getCount(p.tradeItems[i]));
					deleteItem(p.tradeItems[i], getCount(p.tradeItems[i]), i);
				}
				p.frames.sendMessage("Accepted trade.");
				op.frames.sendMessage("Accepted trade.");
				resetTrade();
			}
		}*/
	}

	/**
	 * Offsers an item up for trade.
	 * @param slot
	 * @param amount
	 */
	public void offer(int slot, int amount) {
		/*if (amount < 1 || p.tradeId == -1) {
			return;
		}
		if (stage != TradeStage.TRADING && stage != TradeStage.TRADE_ACCEPTED) {
			return;
		}
		if (!ItemDef.tradeable(p.items[slot])) {
			p.frames.sendMessage("You cannot trade that item.");
			return;
		}
		Player op = Engine.players[p.tradeId];

		if (op != null) {
			p.frames.setString("", 3431);
			op.frames.setString("", 3431);
			setStage(TradeStage.TRADING);
			//op.trade.setStage(TradeStage.TRADING);

			if (amount > p.item.getCount(p.items[slot])) {
				amount = p.item.getCount(p.items[slot]);
			}
			addItem(p.items[slot], amount);
			p.item.deleteItem(p.items[slot], amount);

			//p.frames.setItems(3322, p.items, p.itemsN);
			//p.frames.setItems(3415, p.tradeItems, p.tradeItemsN);
			//op.frames.setItems(3416, p.tradeItems, p.tradeItemsN);
		}*/
	}

	/**
	 * Remove an item from the trade interface.
	 * @param slot Slot to remove the item from.
	 * @param amount Amount of item to remove.
	 */
	public void remove(int slot, int amount) {
		/*if (amount < 1 || p.tradeId == -1) {
			return;
		}
		if (stage != TradeStage.TRADING && stage != TradeStage.TRADE_ACCEPTED) {
			return;
		}
		Player op = Engine.players[p.tradeId];

		if (op != null) {
			p.frames.setString("", 3431);
			op.frames.setString("", 3431);
			setStage(TradeStage.TRADING);
			//op.trade.setStage(TradeStage.TRADING);

			if (amount > getCount(p.tradeItems[slot])) {
				amount = getCount(p.tradeItems[slot]);
			}
			//p.item.addItem(p.tradeItems[slot], amount);
			deleteItem(p.tradeItems[slot], amount, slot);

			for (int i = 0; i < p.tradeItems.length; i++) {
				if (i > 0 && p.tradeItems[i] != -1 && p.tradeItems[i - 1] == -1) {
					int newSlot = p.trade.getFreeSlot();
					p.tradeItems[newSlot] = p.tradeItems[i];
					p.tradeItemsN[newSlot] = p.tradeItemsN[i];
					p.tradeItems[i] = -1;
					p.tradeItemsN[i] = 0;
				}
			}
			//p.frames.setItems(3322, p.items, p.itemsN);
			//p.frames.setItems(3415, p.tradeItems, p.tradeItemsN);
			//op.frames.setItems(3416, p.tradeItems, p.tradeItemsN);
		}*/
	}

	private void addItem(int id, int amount) {
		/*if (ItemDef.stackable(id)) {
			int slot = -1;

			if (hasItem(id)) {
				slot = getLastSlot(id);
			} else {
				slot = getFreeSlot();
			}
			p.tradeItems[slot] = id;

			if (amount + p.tradeItemsN[slot] < Integer.MAX_VALUE && amount + p.tradeItemsN[slot] > 0) {
				p.tradeItemsN[slot] += amount;
			} else {
				p.tradeItemsN[slot] = Integer.MAX_VALUE;
			}
		} else {
			for (int i = 0; i < amount; i++) {
				int slot = getFreeSlot();
				p.tradeItems[slot] = id;
				p.tradeItemsN[slot] = 1;
			}
		}*/
	}

	private void deleteItem(int id, int amount, int slot) {
		/*if (ItemDef.stackable(id)) {
			if (p.tradeItemsN[slot] > amount) {
				p.tradeItemsN[slot] -= amount;
			} else {
				p.tradeItems[slot] = -1;
				p.tradeItemsN[slot] = 0;
			}
		} else {
			for (int i = 0; i < amount; i++) {
				slot = getLastSlot(id);
				p.tradeItems[slot] = -1;
				p.tradeItemsN[slot] = 0;
			}
		}*/
	}

	/**
	 * Resets trading for both the player and the opponent.
	 */
	public void resetTrade() {
		/*Player op = Engine.players[p.tradeId];

		for (int i = 0; i < p.tradeItems.length; i++) {
			p.tradeItems[i] = -1;
			p.tradeItemsN[i] = 0;
		}
		p.tradeId = -1;
		setStage(TradeStage.NOT_TRADING);
		p.frames.closeAllInterfaces();

		if (op != null) {
			for (int i = 0; i < op.tradeItems.length; i++) {
				op.tradeItems[i] = -1;
				op.tradeItemsN[i] = 0;
			}
			op.tradeId = -1;
			//op.trade.setStage(TradeStage.NOT_TRADING);
			op.frames.closeAllInterfaces();
		}*/
	}

	/**
	 * Returns the list of items displayed on the final trade screen.
	 */
	public String getItemList() {
		/*String items = "";
		int itemAmount = (28 - emptySlots());

		for (int i = 0; i < p.tradeItems.length; i++) {
			if (p.tradeItems[i] != -1) {
				items = items
					+ ItemDef.getName(p.tradeItems[i])
					+ (ItemDef.stackable(p.tradeItems[i]) ? " x @cya@" + TextUtils.intToKOrMil(p.tradeItemsN[i])
						+ " @whi@(" + TextUtils.formatNumber(p.tradeItemsN[i], "#,###") + ")" : "")
					// + "\\n";
					+ (itemAmount < 19 ? "\\n" : (i % 2 == 0 ? "  " : "\\n"));
			}
		}
		if (itemAmount == 0) {
			items = "Absolutely nothing!";
		}
		return items;*/
		return "";
	}

	/**
	 * Returns the amount of empty slots in the trade interface.
	 */
	public int emptySlots() {
		/*int slots = 0;

		for (int i = 0; i < p.tradeItems.length; i++) {
			if (p.tradeItems[i] == -1) {
				slots++;
			}
		}
		return slots;*/
		return 0;
	}

	/**
	 * Returns true if the item is currently being offered.
	 * @param id Id of the item to find.
	 */
	public boolean hasItem(int id) {
		/*if (id < 0 || id >= ItemDef.itemDefs.length) {
			return false;
		}
		for (int i = 0; i < p.tradeItems.length; i++) {
			if (p.tradeItems[i] == id) {
				return true;
			}
		}*/
		return false;
	}

	/**
	 * Returns the amount of a specified item in player inventory.
	 * @param id Id of item to count.
	 */
	public int getCount(int id) {
		/*if (id < 0 || id >= ItemDef.itemDefs.length) {
			return 0;
		}
		int amount = 0;

		for (int i = 0; i < p.tradeItems.length; i++) {
			if (p.tradeItems[i] == id) {
				if (p.tradeItems[i] == id) {
					amount += p.tradeItemsN[i];
				}
			}
		}
		return amount;*/
		return 0;
	}

	/**
	 * Returns the last slot the item found at in the trade container.
	 * @param p Player to check the item for.
	 * @param id Id of the item to check for.
	 */
	public int getLastSlot(int id) {
		/*int slot = -1;

		for (int i = 0; i < p.tradeItems.length; i++) {
			if (p.tradeItems[i] == id) {
				slot = i;
			}
		}
		return slot;*/
		return 0;
	}

	/**
	 * Returns the first free slot available.
	 */
	public int getFreeSlot() {
		/*for (int i = 0; i < p.tradeItems.length; i++) {
			if (p.tradeItems[i] == -1) {
				return i;
			}
		}
		return -1;*/
		return 0;
	}

	/**
	 * Sets the trade stage.
	 */
	public void setStage(TradeStage stage) {
		this.stage = stage;
	}
}
