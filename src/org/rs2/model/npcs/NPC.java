package org.rs2.model.npcs;

import org.rs2.Engine;
import org.rs2.world.cache.region.Region;

public class NPC {
	/**
	 * Index in NPC Array.
	 */
	public int npcId;
	/**
	 * NPC Id.
	 */
	public int npcType;
	/**
	 * Name of the NPC.
	 */
	public String name;
	/**
	 * Absolute positions.
	 */
	public int absX, absY;
	/**
	 * The coordinates the NPC is going to walk to.
	 */
	public int moveX, moveY;
	/**
	 * Maximum walking range.
	 */
	public int moveRangeX1, moveRangeX2;
	public int moveRangeY1, moveRangeY2;
	/**
	 * Height level.
	 */
	public int heightLevel;
	/**
	 * Current health and max health.
	 */
	public int health = 10, maxHealth = 10;
	/**
	 * How many ticks must pass before the NPC respawns after death.
	 */
	public int respawnDelay = 10;

	public int attackDamage, attackStyle, attackSpeed, attackRange;
	public int weakness;

	public int walkAnim, turnAnim, attackAnim, deathAnim;
	/**
	 * Set to true if NPC updating is required.
	 */
	public boolean updateReq;
	/**
	 * Text above head updating.
	 */
	public boolean textUpdateReq;
	public String text = "";
	/**
	 * Animation updating.
	 */
	public boolean animUpdateReq;
	public int animReq = -1, animDelay;
	/**
	 * Hit updating.
	 */
	public boolean hitUpdateReq1, hitUpdateReq2;
	public int hitDiff1, hitDiff2;
	public int poisonHit1, poisonHit2;
	/**
	 * Face coord updating.
	 */
	public boolean faceToUpdateReq;
	public int faceToX, faceToY;
	/**
	 * Face direction updating.
	 */
	public boolean faceDirUpdateReq;
	public int faceDir;
	/**
	 * GFX updating.
	 */
	public int gfxReq = -1;
	public int gfxDelay;
	public boolean gfxUpdateReq;
	/**
	 * Set to true if the NPC is hidden.
	 */
	public boolean hidden;

	public NPC(int npcId, int npcType) {
		this.npcId = npcId;
		this.npcType = npcType;
		absX = 3222;
		absY = 3218;
	}

	/**
	 * Runs every tick.
	 */
	public void tick() {

	}

	public void requestGfx(int id, int delay) {
		gfxReq = id;
		gfxDelay = delay;
		gfxUpdateReq = true;
		updateReq = true;
	}

	public void requestGfx(int id, int delay, int height) {
		requestGfx(id, delay + (65536 * height));
	}

	/**
	 * Returns true if the NPC is dead.
	 */
	public boolean dead() {
		if (health < 1) {
			return true;
		}
		return false;
	}

	public void handleClipping(int id) {
		NPC npc = Engine.npcs[id];

		if (npc.moveX == 1 && npc.moveY == 1) {
			if ((Region.getClipping(npc.absX + 1, npc.absY + 1, npc.heightLevel) & 0x12801e0) != 0) {
				npc.moveX = 0;
				npc.moveY = 0;
				if ((Region.getClipping(npc.absX, npc.absY + 1, npc.heightLevel) & 0x1280120) == 0) {
					npc.moveY = 1;
				} else {
					npc.moveX = 1;
				}
			}
		} else if (npc.moveX == -1 && npc.moveY == -1) {
			if ((Region.getClipping(npc.absX - 1, npc.absY - 1, npc.heightLevel) & 0x128010e) != 0) {
				npc.moveX = 0;
				npc.moveY = 0;
				if ((Region.getClipping(npc.absX, npc.absY - 1, npc.heightLevel) & 0x1280102) == 0) {
					npc.moveY = -1;
				} else {
					npc.moveX = -1;
				}
			}
		} else if (npc.moveX == 1 && npc.moveY == -1) {
			if ((Region.getClipping(npc.absX + 1, npc.absY - 1, npc.heightLevel) & 0x1280183) != 0) {
				npc.moveX = 0;
				npc.moveY = 0;
				if ((Region.getClipping(npc.absX, npc.absY - 1, npc.heightLevel) & 0x1280102) == 0) {
					npc.moveY = -1;
				} else {
					npc.moveX = 1;
				}
			}
		} else if (npc.moveX == -1 && npc.moveY == 1) {
			if ((Region.getClipping(npc.absX - 1, npc.absY + 1, npc.heightLevel) & 0x128013) != 0) {
				npc.moveX = 0;
				npc.moveY = 0;
				if ((Region.getClipping(npc.absX, npc.absY + 1, npc.heightLevel) & 0x1280120) == 0) {
					npc.moveY = 1;
				} else {
					npc.moveX = -1;
				}
			}
		}

		if (npc.moveY == -1) {
			if ((Region.getClipping(npc.absX, npc.absY - 1, npc.heightLevel) & 0x1280102) != 0) {
				npc.moveY = 0;
			}
		} else if (npc.moveY == 1) {
			if ((Region.getClipping(npc.absX, npc.absY + 1, npc.heightLevel) & 0x1280120) != 0) {
				npc.moveY = 0;
			}
		}
		if (npc.moveX == 1) {
			if ((Region.getClipping(npc.absX + 1, npc.absY, npc.heightLevel) & 0x1280180) != 0) {
				npc.moveX = 0;
			}
		} else if (npc.moveX == -1) {
			if ((Region.getClipping(npc.absX - 1, npc.absY, npc.heightLevel) & 0x1280108) != 0) {
				npc.moveX = 0;
			}
		}
	}

	/**
	 * Loads NPC and NPC spawn definitions.
	 */
	public static void loadDefinitions() {

	}

}
