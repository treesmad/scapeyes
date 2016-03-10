package org.rs2.model.players.skills;

import org.rs2.model.players.Player;

public class Fletching {
	/**
	 * Player this class belongs to.
	 */
	private Player p;

	private int itemId = -1;
	private int amount;

	/**
	 * Construct a new Fletching class
	 * @param p Player to assign this class to.
	 */
	public Fletching(Player p) {
		this.p = p;
	}

	public void showFletchInterface(int itemId) {
		this.itemId = itemId;
		p.frames.sendChatInterface(8880);
		p.frames.sendInterfaceItemModel(8800, 200, 1511);
	}

	public void startFletching(int itemId, int amount) {
	}

	public enum Logs {
		none(-1, new Object[][] {});

		int id;
		Object[][] data;

		Logs(int id, Object[][] data) {
			this.id = id;
			this.data = data;
		}

		public int getId() {
			return id;
		}
	}

}
