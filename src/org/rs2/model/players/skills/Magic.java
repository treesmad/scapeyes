package org.rs2.model.players.skills;

import org.rs2.Engine;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.util.Misc;
import org.rs2.world.cache.region.Region;

public class Magic {
	/**
	 * Player this class belongs to.
	 */
	private Player p;

	public static final int NONE = 0, FIRE = 554, WATER = 555, AIR = 556, EARTH = 557, LAW = 563, NATURE = 561;

	/**
	 * Construct a new Magic class.
	 * @param p Player to assign this class to.
	 */
	public Magic(Player p) {
		this.p = p;
	}

	public boolean cast(int spellId) {
		for (Spells spell : Spells.values()) {
			if (spellId == spell.getId()) {
				if (p.skillLevel[6] < spell.getReq()) {
					p.frames.sendMessage("You need a magic level of " + spell.getReq() + " to cast that spell.");
					return false;
				}
				//String weapon = ItemDef.getName(p.equipment[3]);

				for (int[] rune : spell.getRunes()) {
					/*if (weapon.contains("Staff")) {
						if (rune[0] == AIR && weapon.contains("air")) {
							continue;
						}
						if (rune[0] == WATER && weapon.contains("water")) {
							continue;
						}
						if (rune[0] == EARTH && weapon.contains("earth")) {
							continue;
						}
						if (rune[0] == FIRE && weapon.contains("fire")) {
							continue;
						}
					}
					if (p.item.getCount(rune[0]) < rune[1]) {
						p.frames.sendMessage("You do not have enough runes to cast that spell.");
						return false;
					}
					p.item.deleteItem(rune[0], rune[1]);*/
					return true;
				}
			}
		}
		return true;
	}

	/**
	 * Teleports the player to the specified x/y coordinates and height level.
	 * @param x New X coordinate.
	 * @param y New Y coordinate.
	 * @param height New height level.
	 * @param range Range of the teleport randomization.
	 */
	public void teleport(final int x, final int y, final int height) {
		if (p.teleportTask != null) {
			return;
		}
		if (p.dead()) {
			return;
		}
		p.requestWalk(0, 0);
		p.requestAnim(714, 0);
		p.frames.sendSound(202, 100, 10);
		p.requestGfx(308, 50, 100);

		Engine.task.schedule(p.teleportTask = new Task() {
			int ticks = 0;

			@Override
			protected void execute() {
				if (ticks == 2) {
					int newX = x + Misc.random(-2, 2);
					int newY = y + Misc.random(-2, 2);

					if (Region.getClipping(newX, newY, height) != 0) {
						p.teleportToX = x;
						p.teleportToY = y;
					} else {
						p.teleportToX = newX;
						p.teleportToY = newY;
					}
					p.heightLevel = height;
					p.requestAnim(715, 0);
				}
				if (ticks == 4) {
					stop();
					p.teleportTask = null;
				}
				ticks++;
			}
		});
	}

	public enum Spells {
		LOW_ALCHEMY(1162, 25, 35, new int[][] { { FIRE, 3 }, { NATURE, 1 } }),

		VARROCK_TELEPORT(1164, 25, 35, new int[][] { { FIRE, 1 }, { AIR, 3 }, { LAW, 1 } }),
		LUMBRIDGE_TELEPORT(1167, 31, 41, new int[][] { { EARTH, 1 }, { AIR, 3 }, { LAW, 1 } }),
		FALADOR_TELEPORT(1170, 37, 48, new int[][] { { WATER, 1 }, { AIR, 3 }, { LAW, 1 } }),
		CAMELOT_TELEPORT(1174, 45, 55.5, new int[][] { { AIR, 5 }, { LAW, 1 } }),
		ARDOUGNE_TELEPORT(1540, 51, 61, new int[][] { { WATER, 2 }, { LAW, 2 } }),
		WATCHTOWER_TELEPORT(1541, 58, 68, new int[][] { { EARTH, 2 }, { LAW, 2 } }),
		TROLLHEIM_TELEPORT(7455, 61, 68, new int[][] { { FIRE, 2 }, { LAW, 2 } });

		int id;
		int req;
		double xp;
		int[][] runes;

		Spells(int id, int req, double xp, int[][] runes) {
			this.id = id;
			this.req = req;
			this.xp = xp;
			this.runes = runes;
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

		public int[][] getRunes() {
			return runes;
		}
	}

}
