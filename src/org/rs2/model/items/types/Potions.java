package org.rs2.model.items.types;

import org.rs2.model.players.Player;
import org.rs2.util.Skills;

public enum Potions {
	ATTACK(new int[] { 125, 123, 121, 2428 }, Skills.ATTACK),
	DEFENCE(new int[] { 137, 135, 133, 2432 }, Skills.DEFENCE),
	STRENGTH(new int[] { 119, 117, 115, 113 }, Skills.STRENGTH),
	RANGING(new int[] { 173, 171, 169, 2444 }, Skills.RANGED),
	PRAYER(new int[] { 143, 141, 139, 2434 }, new Effect() {
		@Override
		public void execute(Player p) {
			p.skillLevel[Skills.PRAYER] += Math.floor(7 + p.getLevelForXP(Skills.PRAYER) / 4);

			if (p.skillLevel[Skills.PRAYER] > p.getLevelForXP(Skills.PRAYER)) {
				p.skillLevel[Skills.PRAYER] = p.getLevelForXP(Skills.PRAYER);
			}
			p.frames.sendSkillLevel(Skills.PRAYER);
		}
	}),
	MAGIC(new int[] { 3046, 3044, 3042, 3040 }, Skills.MAGIC),
	ENERGY(new int[] { 3014, 3012, 3010, 3008 }, new Effect() {
		@Override
		public void execute(Player p) {
			p.runEnergy += 10;

			if (p.runEnergy > 100) {
				p.runEnergy = 100;
			}
			p.frames.sendEnergy();
		}
	}),
	SUPER_ATTACK(new int[] { 149, 147, 145, 2436 }, Skills.ATTACK),
	SUPER_STRENGTH(new int[] { 161, 159, 157, 2440 }, Skills.STRENGTH),
	SUPER_ENERGY(new int[] { 3022, 3020, 3018, 3016 }, new Effect() {
		@Override
		public void execute(Player p) {
			p.runEnergy += 20;

			if (p.runEnergy > 100) {
				p.runEnergy = 100;
			}
			p.frames.sendEnergy();
		}
	}),
	SARADOMIN_BREW(new int[] { 6691, 6689, 6687, 6685 }, new Effect() {
		@Override
		public void execute(Player p) {
			p.increaseSkill(Skills.HITPOINTS, 1.15, 0);
			p.increaseSkill(Skills.DEFENCE, 1.20, 2);
			p.reduceSkill(Skills.ATTACK, 0, 0.9, 0);
			p.reduceSkill(Skills.STRENGTH, 0, 0.9, 0);
			p.reduceSkill(Skills.MAGIC, 0, 0.9, 0);
			p.reduceSkill(Skills.RANGED, 0, 0.9, 0);
		}
	});

	/**
	 * Id of potion doses 1 to 4
	 */
	private int[] id;

	/**
	 * Skill to increase.
	 */
	private int skill;

	/**
	 * Effect to apply when consumed.
	 */
	private Effect effect;

	Potions(final int[] id, final int skill) {
		this.id = id;
		this.skill = skill;
		this.setEffect(new Effect() {
			@Override
			public void execute(Player p) {
				if (name().contains("SUPER")) {
					p.increaseSkill(skill, 1.15, 5);
				} else {
					p.increaseSkill(skill, 1.1, 3);
				}
			}
		});
	}

	Potions(final int[] id, int skill, Effect effect) {
		this.id = id;
		this.skill = skill;
		this.setEffect(effect);
	}

	Potions(final int[] id, Effect effect) {
		this.id = id;
		this.setEffect(effect);
	}

	public int getId(int dose) {
		return id[dose];
	}

	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public interface Effect {
		abstract void execute(Player p);
	}

	public static Potions forId(int id) {
		for (Potions p : Potions.values()) {
			if (p.getId(0) == id || p.getId(1) == id || p.getId(2) == id || p.getId(3) == id) {
				return p;
			}
		}
		return null;
	}

	public static boolean isPotion(int id) {
		for (Potions p : Potions.values()) {
			if (p.getId(0) == id || p.getId(1) == id || p.getId(2) == id || p.getId(3) == id) {
				return true;
			}
		}
		return false;
	}
}