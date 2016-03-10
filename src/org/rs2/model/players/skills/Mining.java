package org.rs2.model.players.skills;

import org.rs2.Engine;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.util.Misc;
import org.rs2.util.Skills;

public class Mining {
	/**
	 * Player this class belongs to.
	 */
	private Player p;
	/**
	 * Position of the rock currently being mined.
	 */
	private int x, y;
	/**
	 * Current 'health' of the rock.
	 */
	private int health;

	/**
	 * Constructs a new Mining class.
	 * @param p Player to assign this class to.
	 */
	public Mining(Player p) {
		this.p = p;
	}

	public void start(int id, int x, int y, int rot) {
		p.frames.closeAllInterfaces();

		if (skill() < Rocks.forId(id).getReq()) {
			p.frames.sendMessage("You need a Mining level of at least " + Rocks.forId(id).getReq() + " to mine that.");
			p.resetAnim();
			stop();
			return;
		}
		this.x = x;
		this.y = y;
		health = Misc.random(1, Rocks.forId(id).getMaxHealth());

		Engine.task.schedule(p.actionTask = new Task(2, true) {
			int ticks;

			@Override
			protected void execute() {
				if (p == null || !p.online || p.disconnected) {
					stop();
					return;
				}
				if (!hasPickaxe()) {
					p.mining.stop();
					return;
				}
				/*if (p.item.emptySlots() < 1) {
					p.frames.sendMessage("Your inventory is too full to hold any more ore.");
					p.resetAnim();
					p.mining.stop();
					return;
				}*/
				if (ticks == 0 || ticks % 4 == 0) {
					p.requestAnim(getPickaxeAnim());
				}
				ticks++;
			}
		});
	}

	public void stop() {
		p.resetActionTask();
		p.resetAnim();
		x = 0;
		y = 0;
	}

	public enum Rocks {
		COPPER(2090, 1, 17.5, 436, 1, 10),
		TIN(2094, 1, 17.5, 438, 1, 10);

		int id;
		int req;
		double xp;
		int ore;
		int maxHealth;
		int chance;

		Rocks(int id, int req, double xp, int ore, int maxHealth, int chance) {
			this.id = id;
			this.req = req;
			this.xp = xp;
			this.ore = ore;
			this.maxHealth = maxHealth;
			this.chance = chance;
		}

		public int getId() {
			return id;
		}

		public int getReq() {
			return req;
		}

		public double getXP() {
			return xp;
		}

		public int getOre() {
			return ore;
		}

		public int getMaxHealth() {
			return maxHealth;
		}

		public int getChance() {
			return chance;
		}

		public static Rocks forId(int id) {
			for (Rocks r : Rocks.values()) {
				if (r.getId() == id) {
					return r;
				}
			}
			return null;
		}
	}

	public enum Pickaxes {
		RUNE_PICKAXE(1275, 41, 624, 5),
		ADAMANT_PICKAXE(1271, 31, 628, 4),
		MITHRIL_PICKAXE(1273, 21, 629, 3),
		STEEL_PICKAXE(1269, 6, 627, 2),
		IRON_PICKAXE(1267, 1, 626, 1),
		BRONZE_PICKAXE(1265, 1, 625, 0);

		int id, req, anim;
		double bonus;

		Pickaxes(int id, int req, int anim, double bonus) {
			this.id = id;
			this.req = req;
			this.bonus = bonus;
			this.anim = anim;
		}

		public int getId() {
			return id;
		}

		public int getReq() {
			return req;
		}

		public double getBonus() {
			return bonus;
		}

		public int getAnim() {
			return anim;
		}

		public static Pickaxes forId(int id) {
			for (Pickaxes p : Pickaxes.values()) {
				if (p.getId() == id) {
					return p;
				}
			}
			return null;
		}
	}

	public double getPickaxeBonus() {
		for (Pickaxes a : Pickaxes.values()) {
			if (hasPickaxe(a.getId())) {
				return a.getBonus();
			}
		}
		return 0;
	}

	public int getPickaxeAnim() {
		for (Pickaxes a : Pickaxes.values()) {
			if (hasPickaxe(a.getId())) {
				return a.getAnim();
			}
		}
		return -1;
	}

	public boolean hasPickaxe(int axeId) {
		/*if (p.equipment[3] == axeId || p.item.getCount(axeId) > 0) {
			if (skill() < Pickaxes.forId(axeId).getReq()) {
				return false;
			}
			return true;
		}*/
		return false;
	}

	public boolean hasPickaxe() {
		int[] ids = { 1265, 1267, 1269, 1273, 1271, 1275 };

		/*for (int i = 0; i < ids.length; i++) {
			if (p.equip.hasEquipped(ids[i]) || p.item.hasItem(ids[i])) {
				if (skill() < Pickaxes.forId(ids[i]).getReq()) {
					p.frames.sendMessage("You do not have a pickaxe which you have the mining level to use.");
					return false;
				}
				return true;
			}
		}*/
		p.frames.sendMessage("You need a pickaxe to mine this rock.");
		return false;
	}

	private int skill() {
		return p.skillLevel[Skills.MINING];
	}

}
