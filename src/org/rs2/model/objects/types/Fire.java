package org.rs2.model.objects.types;

import org.rs2.model.objects.Object;
import org.rs2.util.Misc;

public class Fire {
	/**
	 * X and Y coordinates of fire.
	 */
	private int x, y;
	/**
	 * Height level of the fire.
	 */
	private int height;
	/**
	 * Time when the fire was removed.
	 */
	private long timer;
	/**
	 * Time at which the fire will despawn;
	 */
	private long despawnTime;

	public Fire(int x, int y, int height) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.timer = System.currentTimeMillis();
		this.despawnTime = Misc.random(30000, 120000);
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

	public long getTimer() {
		return timer;
	}

	public long getDespawnTime() {
		return despawnTime;
	}

	public static boolean exists(int x, int y, int height) {
		/*for (Fire f : Object.fires) {
			if (f == null) {
				continue;
			}
			if (f.getX() == x && f.getY() == y && f.getHeight() == height) {
				return true;
			}
		}*/
		return false;
	}
}
