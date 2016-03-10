package org.rs2.util;

import org.rs2.model.players.Player;

public enum Sounds {
	EAT_FOOD(317),
	DRINK_POTION(334),
	BURY_BONES(380),

	EQUIP_HELMET(1342),
	EQUIP_PLATEBODY(1346),
	EQUIP_SHIELD(1343),
	EQUIP_ARMOR(1347),

	EQUIP_AXE(1351),
	EQUIP_PICKAXE(43),
	EQUIP_MACE(1352),
	EQUIP_WEAPON(1344),

	DROP(376),
	PICKUP(356),
	DESTROY(364, 365),

	HIGH_ALCH(97),
	LOW_ALCH(98),

	TELEPORT(202);

	int[] sound;

	Sounds(int... sound) {
		this.sound = sound;
	}

	public int[] getSound() {
		return sound;
	}

	public static void play(Player p, Sounds sound, int delay, int volume) {
		int[] sounds = sound.getSound();

		if (sounds.length < 2) {
			p.frames.sendSound(sounds[0], delay, volume);
		} else {
			p.frames.sendSound(sounds[Misc.random(sounds.length - 1)], delay, volume);
		}
	}
}