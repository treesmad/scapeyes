package org.rs2.model.items.types;

import org.rs2.model.players.Player;
import org.rs2.util.Skills;

public enum Food {
	POTATO(1942, 1),
	ONION(1957, 1),
	CABBAGE(1965, 1),
	SHRIMPS(315, 3),
	SARDINE(325, 4),
	COOKED_CHICKEN(2140, 4),
	COOKED_MEAT(2142, 4),
	CAKE(1891, 1893, 1895, 4),
	CHOCOLATE_CAKE(1897, 1899, 1901, 5),
	BREAD(2309, 5),
	HERRING(347, 5),
	MACKEREL(355, 6),
	TROUT(333, 7),
	PLAIN_PIZZA(2289, 2290, 7),
	MEAT_PIZZA(2293, 2295, 7),
	PEACH(6883, 8),
	SALMON(329, 9),
	ANCHOVY_PIZZA(2297, 2299, 9),
	TUNA(361, 10),
	LOBSTER(379, 12),
	BASS(365, 13),
	SWORDFISH(373, 14),
	EASTER_EGG(1961, 14),
	CHILLI_POTATO(7054, 14),
	EGG_POTATO(7056, 16),
	MONKFISH(7946, 16),
	COOKED_KARAMBWAN(3144, 18),
	SHARK(385, 20),
	MUSHROOM_POTATO(7058, 20),
	SEA_TURTLE(397, 21),
	MANTA_RAY(391, 22),
	ADMIRAL_PIE_2(7198, 7200, 8),
	ADMIRAL_PIE_1(7200, 8),
	WILD_PIE(7208, 7210, 11, new Effect() {
		@Override
		public void execute(Player p) {
			p.increaseSkill(Skills.SLAYER, 5, 1);
			p.increaseSkill(Skills.RANGED, 4, 1);
		}
	}),
	TUNA_POTATO(7060, 22);

	/**
	 * Ids of the items. Ids 2 and 3 are for multi-bite items.
	 */
	private int id = -1, id2 = -1, id3 = -1;

	/**
	 * Amount of healing the food will do.
	 */
	private int heal;

	/**
	 * Message(s) to override the default eating messages with.
	 */
	private String[] msg;

	/**
	 * Additional effect alongside healing.
	 */
	private Effect effect;

	Food(int id, int heal) {
		this.id = id;
		this.heal = heal;
	}

	Food(int id, int id2, int heal) {
		this.id = id;
		this.id2 = id2;
		this.heal = heal;
	}

	Food(int id, int id2, int id3, int heal) {
		this.id = id;
		this.id2 = id2;
		this.id3 = id3;
		this.heal = heal;
	}

	Food(int id, int id2, int heal, Effect effect) {
		this.id = id;
		this.id2 = id2;
		this.heal = heal;
		this.setEffect(effect);
	}

	public int getId() {
		return id;
	}

	public int getId2() {
		return id2;
	}

	public int getId3() {
		return id3;
	}

	public int getHeal() {
		return heal;
	}

	public interface Effect {
		public abstract void execute(Player p);
	}

	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public static boolean isFood(int id) {
		for (Food f : Food.values()) {
			if (f.getId() == id || f.getId2() == id || f.getId3() == id) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFood(Food f, int id) {
		if (f.getId() == id || f.getId2() == id || f.getId3() == id) {
			return true;
		}
		return false;
	}

}