package org.rs2.model.items.types;

public enum Bones {
	BONES(512, 4.5),
	BURNT_BONES(528, 4.5),
	WOLF_BONES(2859, 4.5),
	MONKEY_BONES(3179, 5),
	MONKEY_BONES2(3180, 5),
	MONKEY_BONES3(3181, 5),
	MONKEY_BONES4(3182, 5),
	MONKEY_BONES5(3183, 5),
	MONKEY_BONES6(3184, 5),
	MONKEY_BONES7(3185, 5),
	MONKEY_BONES8(3186, 5),
	MONKEY_BONES9(3187, 5),
	BAT_BONES(530, 5.3),
	BIG_BONES(532, 15),
	ZOGRE_BONES(4812, 22.5),
	BABYDRAGON_BONES(534, 30),
	DRAGON_BONES(536, 72),
	FAYRG_BONES(4830, 84),
	RAURG_BONES(4832, 96),
	OURG_BONES(4834, 140);

	/**
	 * Id of the bone.
	 */
	int id = -1;

	/**
	 * XP received for burying a bone.
	 */
	double xp;

	Bones(int id, double xp) {
		this.id = id;
		this.xp = xp;
	}

	public int getId() {
		return id;
	}

	public double getXP() {
		return xp;
	}

}
