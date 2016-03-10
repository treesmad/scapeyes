package org.rs2.model.objects.types;

import org.rs2.util.Misc;

public class Tree {
	/**
	 * Id of tree.
	 */
	private int id = -1;
	/**
	 * X and Y coordinates of tree.
	 */
	private int x, y;
	/**
	 * Height level of the tree.
	 */
	private int height;
	/**
	 * Rotation of tree.
	 */
	private int rot;
	/**
	 * Time when the tree was removed.
	 */
	private long timer;

	public Tree(int id, int x, int y, int height, int rot) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.rot = rot;
		this.timer = System.currentTimeMillis();
	}

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}

	public int getRot() {
		return rot;
	}

	public long getTimer() {
		return timer;
	}

	/**
	 * Returns the respawn time of the specified tree.
	 * @param id Id of object.
	 * @return Respawn time (ms).
	 */
	public static long getRespawnTime(int id) {
		// Normal tree
		if (id >= 1276 && id <= 1278) {
			return Misc.random(30000, 60000);
		}

		// Oak
		if (id == 1281) {
			return 13200;
		}

		// Willow
		if (id == 1308) {
			return 13200;
		}

		// Yew
		if (id == 1309) {
			return 97500;
		}

		return 30000;
	}

	/**
	 * Gets the stump id for the specified tree.
	 * @param id Id of tree.
	 * @return Stump id.
	 */
	public static int getStump(int id) {
		// Normal
		if (id == 1276) {
			return 1342;
		}
		if (id == 1277) {
			return 1341;
		}
		if (id == 1278) {
			return 1342;
		}
		if (id == 1281) {
			return 1357;
		}

		// Willow
		if (id == 1308) {
			return 7399;
		}

		return 1342;
	}

}
