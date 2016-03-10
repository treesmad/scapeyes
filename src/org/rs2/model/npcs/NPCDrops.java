package org.rs2.model.npcs;

public class NPCDrops {

	public static NPCDrops[] npcDrops = new NPCDrops[5000];

	/**
	 * Id of the NPC the droptable belongs to.
	 */
	public static int npcId;

	/**
	 * Id of the item to drop.
	 */
	public static int dropId;

	/**
	 * Amount of the item to drop (with optional min and max values for
	 * randomized quantities).
	 */
	public static int dropN, dropMinN, dropMaxN;

	/**
	 * Chance for the item(s) to drop (independent of the quantity
	 * randomization).
	 */
	public static double dropChance;

	public static void loadDefinitions() {

	}

}
