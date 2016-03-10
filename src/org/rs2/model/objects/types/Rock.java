package org.rs2.model.objects.types;

public class Rock {
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

	public Rock(int id, int x, int y, int rot, int height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.rot = rot;
		this.height = height;
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

	public int getRot() {
		return rot;
	}

	public int getHeight() {
		return height;
	}

	public long getTimer() {
		return timer;
	}

	/**
	 * Returns the respawn time of the specified rock.
	 * @param id Id of object.
	 * @return Respawn time in milliseconds.
	 */
	public static long getRespawnTime(int id) {
		return 30000;
	}
}
