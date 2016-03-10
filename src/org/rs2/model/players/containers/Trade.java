package org.rs2.model.players.containers;

import org.rs2.Engine;
import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.util.TextUtils;

public class Trade extends Container {

	private Player p;

	/**
	 * Trading states
	 */
	public enum State {
		NOT_TRADING,
		TRADING,
		CONFIRMING,
	}
	/**
	 * Current trade state.
	 */
	public State state = State.NOT_TRADING;

	/**
	 * Set to true if trade has been accepted.
	 */
	public boolean accepted;

	/**
	 * Construct a new Inventory container.
	 * @param p Player to assign this container to.
	 */
	public Trade(Player p) {
		super(28);
		this.p = p;
	}

	/**
	 * Offer an item from the inventory up for trade.
	 * @param slot Slot to offer item from.
	 * @param amount Amount to offer.
	 */
	public void offer(int slot, int amount) {
		//p.inventory.transfer(this, slot, amount);
	}

	/**
	 * Remove item from trade window.
	 */
	public void withdraw(int slot, int amount) {
		//transfer(p.inventory, slot, amount);
	}

	/**
	 * Starts the trade.
	 */
	public void start() {
		Player op = Engine.players[p.tradeId];

		if (op == null) {
			return;
		}
		send();
		op.trade.send();
	}
	
	/**
	 * Sends the trading interface.
	 */
	public void send() {
		Player op = Engine.players[p.tradeId];

		if (op == null || state != State.NOT_TRADING) {
			return;
		}
		p.frames.setString("Trading With: " + op.playerName, 3417);
		p.frames.setString("", 3431);
		p.frames.resetItems(3415);
		p.frames.resetItems(3416);
		p.frames.setItems(3322, p.inventory);
		p.frames.sendInterfaceOverlay(3323, 3321);
		
		p.trade.state = State.TRADING;
	}

	/**
	 * Sends the confirm trade interface.
	 */
	public void sendConfirm() {
		Player op = Engine.players[p.tradeId];

		if (op == null || state != State.TRADING) {
			return;
		}
		p.frames.setString("Are you sure you want to make this trade?", 3535);
		p.frames.setString(p.trade.getItemList(), 3557);
		p.frames.setString(op.trade.getItemList(), 3558);
		p.frames.sendInterfaceOverlay(3443, 3213);
		
		p.trade.state = State.CONFIRMING;
		p.trade.accepted = false;
	}

	public void accept() {
		Player op = Engine.players[p.tradeId];

		if (op == null || state != State.TRADING) {
			return;
		}
		if (p.inventory.emptySlots() < op.trade.size()) {
			p.frames.sendMessage("You do not have enough inventory space to complete this trade.");
			return;
		}
		if (op.inventory.emptySlots() < size()) {
			p.frames.sendMessage(op.playerName
				+ " does not have enough inventory space to complete the trade.");
			return;
		}
		accepted = true;
		p.frames.setString("Waiting for other player...", 3431);
		op.frames.setString("Other player has accepted.", 3431);

		if (op.trade.accepted) {
			sendConfirm();
			op.trade.sendConfirm();
		}
	}

	public void confirm() {
		Player op = Engine.players[p.tradeId];

		if (op == null || state != State.CONFIRMING) {
			return;
		}
		accepted = true;
		p.frames.setString("Waiting for other player...", 3535);
		op.frames.setString("Other player has accepted.", 3535);
		
		if (op.trade.accepted) {
			System.out.println("Finishing trade");
			for (int i = 0; i < op.trade.getItems().length; i++) {
				Item item = new Item(op.trade.getId(i), op.trade.getCount(op.trade.getId(i)));
				p.inventory.add(item);
				op.trade.remove(item);
			}
			for (int i = 0; i < getItems().length; i++) {
				Item item = new Item(getId(i), getCount(getId(i)));
				op.inventory.add(item);
				remove(i, getCount(i));
			}
			p.frames.sendMessage("Accepted trade.");
			op.frames.sendMessage("Accepted trade.");
			op.trade.reset();
			reset();
		}
	}
	
	public void decline() {
		Player op = Engine.players[p.tradeId];

		if (op == null) {
			return;
		}
		for (int i = 0; i < capacity(); i++) {
			/*Item item = new Item(getId(i), getCount(getId(i)));
			p.inventory.add(item);
			remove(item);*/
			Item item = copy(i);
			p.inventory.add(item);
			remove(item);
		}
		for (int i = 0; i < op.trade.capacity(); i++) {
			/*Item item = new Item(op.trade.getId(i), op.trade.getCount(getId(i)));
			op.inventory.add(item);
			op.trade.remove(item);*/
			Item item = op.trade.copy(i);
			op.inventory.add(item);
			op.trade.remove(item);
		}
		op.frames.sendMessage("Player declined trade.");
		op.trade.reset();
		reset();
	}

	public void reset() {
		clear();
		accepted = false;
		state = State.NOT_TRADING;
		p.tradeId = -1;
		p.frames.closeAllInterfaces();
	}

	@Override
	public void remove(int slot, int amount) {
		super.remove(slot, amount);

		if (get(slot) == null) {
			shift();
		}
	}

	/**
	 * Returns the list of items displayed on the final trade screen.
	 */
	public String getItemList() {
		String items = "";

		for (int i = 0; i < getItems().length; i++) {
			if (get(i) == null) {
				continue;
			}
			items = items
				+ ItemDef.getName(getId(i))
				+ (ItemDef.stackable(getId(i)) ? " x @cya@" + TextUtils.intToKOrMil(getAmt(i))
					+ " @whi@(" + TextUtils.formatNumber(getAmt(i), "#,###") + ")" : "")
				+ (size() < 19 ? "\\n" : (i % 2 == 0 ? "  " : "\\n"));
		}
		if (size() == 0) {
			items = "Absolutely nothing!";
		}
		return items;
	}

	@Override
	void updateItem(int slot) {
		Player op = Engine.players[p.tradeId];
		
		if (op == null) {
			return;
		}
		p.frames.setItem(3415, slot, this);
		op.frames.setItem(3416, slot, this);
	}

	@Override
	void updateItems() {
		Player op = Engine.players[p.tradeId];
		
		if (op == null) {
			return;
		}
		p.frames.setItems(3415, this);
		op.frames.setItems(3416, this);
	}
}
