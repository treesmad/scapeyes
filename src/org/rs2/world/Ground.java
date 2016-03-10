package org.rs2.world;

import java.util.ArrayList;
import org.rs2.Engine;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.world.cache.region.Region;

/**
 * Handles the spawning and de-spawning of dropped items.
 */
public class Ground {
	/**
	 * List of all items on the ground.
	 */
	private ArrayList<GroundItem> items = new ArrayList<GroundItem>();

	/**
	 * Runs every game tick.
	 */
	public void tick() {
		for (GroundItem g : items) {
			if (g == null) {
				continue;
			}
			if (!g.isPublic() && Engine.currentTicks() - g.getTime() >= 100) {
				if (ItemDef.tradeable(g.getItem().getId())) {
					showItem(g);
				} else {
					g.setPublic(true);
				}
			}
			if (g.isPublic() && Engine.currentTicks() - g.getTime() >= 300) {
				removeItem(g);
				break;
			}
		}
	}

	/**
	 * Returns true if item exists.
	 * @param id Id of item.
	 * @param x X position.
	 * @param y Y position.
	 * @param height Heightlevel;
	 */
	public boolean itemExists(int id, int x, int y, int height) {
		if (id < 0 || id >= ItemDef.cache.length) {
			return false;
		}
		for (GroundItem g : items) {
			if (g == null) {
				continue;
			}
			if (g.getItem().getId() == id && g.getX() == x && g.getY() == y
				&& g.getHeight() == height) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds an item to the ground.
	 * @param g Ground item to add.
	 */
	public void addItem(GroundItem g) {
		if (g == null) {
			return;
		}
		items.add(g);

		for (Player p : Engine.players) {
			if (p == null) {
				continue;
			}
			if (p.heightLevel == g.getHeight() && p.region == Region.getRegion(g.getX(), g.getY())) {
				if (g.getOwner() == null || p.playerName.equals(g.getOwner())) {
					p.frames.addGroundItem(g.getItem(), g.getX(), g.getY());
				}
			}
		}
	}

	/**
	 * Removes a ground item.
	 * @param g Ground item to remove.
	 */
	private void removeItem(GroundItem g) {
		if (g == null) {
			return;
		}
		items.remove(g);

		for (Player p : Engine.players) {
			if (p == null) {
				continue;
			}
			if (p.heightLevel == g.getHeight() && p.region == Region.getRegion(g.getX(), g.getY())) {
				p.frames.removeGroundItem(g.getItem().getId(), g.getX(), g.getY());
			}
		}
	}

	/**
	 * Removes a ground item from the specified coordinates.
	 * @param id Id of item.
	 * @param x X position of item.
	 * @param y Y position of item.
	 * @param height Height of item.
	 */
	public void removeItem(int id, int x, int y, int height) {
		for (GroundItem g : items) {
			if (g == null) {
				continue;
			}
			if (g.getItem().getId() == id && g.getX() == x && g.getY() == y
				&& g.getHeight() == height) {
				removeItem(g);
				break;
			}
		}
	}

	/**
	 * Shows ground item to all players in the region.
	 * @param g Ground item to show.
	 */
	public void showItem(GroundItem g) {
		if (g == null) {
			return;
		}
		g.setPublic(true);

		for (Player p : Engine.players) {
			if (p == null) {
				continue;
			}
			if (!p.playerName.equals(g.getOwner()) && p.heightLevel == g.getHeight()
				&& p.region == Region.getRegion(g.getX(), g.getY())) {
				p.frames.addGroundItem(g.getItem(), g.getX(), g.getY());
			}
		}
	}

	/**
	 * Returns the amount of a ground item.
	 * @param id Id of item.
	 * @param x X ite
	 * @param y
	 * @param height
	 * @return
	 */
	public int getItemAmount(int id, int x, int y, int height) {
		if (id < 0 || id >= ItemDef.cache.length) {
			return 0;
		}
		for (GroundItem g : items) {
			if (g == null) {
				continue;
			}
			if (g.getItem().getId() == id && g.getX() == x && g.getY() == y
				&& g.getHeight() == height) {
				return g.getItem().getAmt();
			}
		}
		return 0;
	}

	/**
	 * Updates ground items in the region.
	 * @param p Player to update for.
	 */
	public void updateRegion(Player p) {
		for (GroundItem g : items) {
			if (g == null) {
				continue;
			}
			if ((g.isPublic() || p.playerName.equals(g.getOwner()))
				&& p.heightLevel == g.getHeight()
				&& p.region == Region.getRegion(g.getX(), g.getY())) {
				p.frames.addGroundItem(g.getItem(), g.getX(), g.getY());
			}
		}
	}

}
