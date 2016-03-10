package org.rs2.model.players.skills;

import java.util.ArrayList;
import org.rs2.model.players.Player;
import org.rs2.util.Skills;

public class Prayer {
	/**
	 * Player this class belongs to.
	 */
	private Player p;

	/**
	 * List of active prayers.
	 */
	public ArrayList<Prayers> activePrayers = new ArrayList<Prayers>();

	/**
	 * Rate that your prayer points will drain at.
	 */
	public double drainRate = 0;

	/**
	 * Assign class to player.
	 * @param p Player to assign to.
	 */
	public Prayer(Player p) {
		this.p = p;
	}

	/**
	 * Activates a prayer.
	 * @param id Id of prayer to activate.
	 */
	public void activate(int id) {
		for (Prayers prayer : Prayers.values()) {
			if (id == prayer.getId()) {
				if (p.getLevelForXP(Skills.PRAYER) < prayer.getReq()) {
					p.frames.sendChatStatement("You need a @dbl@Prayer@bla@ level of " + prayer.getReq()
						+ " to use @dbl@" + prayer.getName() + "@bla@.");
					p.frames.setConfig(prayer.getConfig(), 0);
					return;
				}
				if (p.skillLevel[Skills.PRAYER] < 1) {
					p.frames.sendMessage("You need to recharge your Prayer at an altar.");
					deactivate(prayer);
					return;
				}
				switch (prayer) {
					case PROTECT_FROM_MAGIC:
						deactivate(Prayers.PROTECT_FROM_MELEE);
						deactivate(Prayers.PROTECT_FROM_MISSILES);
						deactivate(Prayers.SMITE);
						deactivate(Prayers.REDEMPTION);
						deactivate(Prayers.RETRIBUTION);
						p.requestHeadIcon(2);
						break;
					case PROTECT_FROM_MELEE:
						deactivate(Prayers.PROTECT_FROM_MAGIC);
						deactivate(Prayers.PROTECT_FROM_MISSILES);
						deactivate(Prayers.SMITE);
						deactivate(Prayers.REDEMPTION);
						deactivate(Prayers.RETRIBUTION);
						p.requestHeadIcon(0);
						break;
					case PROTECT_FROM_MISSILES:
						deactivate(Prayers.PROTECT_FROM_MELEE);
						deactivate(Prayers.PROTECT_FROM_MAGIC);
						deactivate(Prayers.SMITE);
						deactivate(Prayers.REDEMPTION);
						deactivate(Prayers.RETRIBUTION);
						p.requestHeadIcon(1);
						break;
					case SMITE:
						deactivate(Prayers.PROTECT_FROM_MELEE);
						deactivate(Prayers.PROTECT_FROM_MISSILES);
						deactivate(Prayers.PROTECT_FROM_MAGIC);
						deactivate(Prayers.REDEMPTION);
						deactivate(Prayers.RETRIBUTION);
						p.requestHeadIcon(4);
						break;
					case REDEMPTION:
						deactivate(Prayers.PROTECT_FROM_MELEE);
						deactivate(Prayers.PROTECT_FROM_MISSILES);
						deactivate(Prayers.PROTECT_FROM_MAGIC);
						deactivate(Prayers.SMITE);
						deactivate(Prayers.RETRIBUTION);
						p.requestHeadIcon(5);
						break;
					case RETRIBUTION:
						deactivate(Prayers.PROTECT_FROM_MELEE);
						deactivate(Prayers.PROTECT_FROM_MISSILES);
						deactivate(Prayers.PROTECT_FROM_MAGIC);
						deactivate(Prayers.SMITE);
						deactivate(Prayers.REDEMPTION);
						p.requestHeadIcon(3);
						break;
					default:
						break;
				}
				if (!activePrayers.contains(prayer)) {
					activePrayers.add(prayer);
				} else {
					deactivate(prayer);
				}
				break;
			}
		}
	}

	public void deactivate(Prayers prayer) {
		switch (prayer) {
			case PROTECT_FROM_MAGIC:
			case PROTECT_FROM_MELEE:
			case PROTECT_FROM_MISSILES:
			case SMITE:
			case REDEMPTION:
			case RETRIBUTION:
				p.requestHeadIcon(-1);
			default:
				break;
		}
		activePrayers.remove(prayer);
		p.frames.setConfig(prayer.getConfig(), 0);
	}

	public void tick() {
		for (Prayers prayer : activePrayers) {
			drainRate += 0.6 / prayer.getDrain();
		}
		if (drainRate >= 1) {
			p.skillLevel[5] -= 1;
			p.frames.sendSkillLevel(Skills.PRAYER);
			drainRate = 0;
		}
		if (p.skillLevel[Skills.PRAYER] < 1) {
			for (Prayers prayer : Prayers.values()) {
				deactivate(prayer);
			}
			p.frames.sendSound(437, 0, 10);
			p.frames.sendMessage("You have run out of prayer points, you must recharge at an altar.");
			drainRate = 0;
		}
	}

	public enum Prayers {
		THICK_SKIN(5609, 83, 1, 12, "Thick Skin"),
		BURST_OF_STRENGTH(5610, 84, 4, 12, "Burst of Strength"),
		CLARITY_OF_THOUGHT(5611, 85, 7, 12, "Clarity of Thought"),
		ROCK_SKIN(5612, 86, 10, 6, "Rock Skin"),
		SUPERHUMAN_STRENGTH(5613, 87, 13, 6, "Superhuman Strength"),
		IMPROVED_REFLEXES(5614, 88, 16, 6, "Improved Reflexes"),
		RAPID_RESTORE(5615, 89, 19, 26, "Rapid Restore"),
		RAPID_HEAL(5616, 90, 22, 18, "Rapid Heal"),
		PROTECT_ITEMS(5617, 91, 25, 18, "Protect Items"),
		STEEL_SKIN(5618, 92, 28, 3, "Steel Skin"),
		ULTIMATE_STRENGTH(5619, 93, 31, 3, "Ultimate Strength"),
		INCREDIBLE_REFLEXES(5620, 94, 34, 3, "Incredible Reflexes"),
		PROTECT_FROM_MAGIC(5621, 95, 37, 3, "Protect from Magic"),
		PROTECT_FROM_MISSILES(5622, 96, 40, 3, "Protect from Missiles"),
		PROTECT_FROM_MELEE(5623, 97, 43, 3, "Protect from Melee"),
		RETRIBUTION(683, 98, 46, 12, "Retribution"),
		REDEMPTION(684, 99, 49, 6, "Redemption"),
		SMITE(685, 100, 52, 1.8, "Smite");

		int id;
		int req;
		int config;
		double drain;
		int[] conflicts;
		String name;

		Prayers(int id, int config, int req, double drain, String name) {
			this.id = id;
			this.req = req;
			this.config = config;
			this.drain = drain;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public int getReq() {
			return req;
		}

		public int getConfig() {
			return config;
		}

		public double getDrain() {
			return drain;
		}

		public int[] getConflicts() {
			return conflicts;
		}

		public String getName() {
			return name;
		}
	}

}
