package org.rs2.model.objects;

import org.rs2.Engine;

public class Respawnable {
	/**
	 * Id of object.
	 */
	private int id;
	/**
	 * X and Y positions of object.
	 */
	private int x, y;
	/**
	 * Rotation of object.
	 */
	private int rot;
	/**
	 * Height level of object.
	 */
	private int height;
	/**
	 * 'Health' of object.
	 */
	private int health = -1;
	/**
	 * Time (in millis) at which the object will respawn at.
	 */
	private long respawn = -1;

	public enum Type {
		TREE,
		ROCK,
		DOOR,
		FIRE
	}

	public Respawnable(int id, int x, int y, int rot, int height, int health, int respawn) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.rot = rot;
		this.height = height;
		this.health = health;
		this.respawn = respawn;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getRot() {
		return rot;
	}

	public void setRot(int rot) {
		this.rot = rot;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public long getRespawn() {
		return respawn;
	}

	public void setRespawn(int respawn) {
		this.respawn = respawn;
	}

}
