package org.rs2.model.objects;

import java.util.ArrayList;
import org.rs2.Engine;
import org.rs2.model.players.Player;
import org.rs2.world.cache.region.Region;

public class Object {
	/**
	 * List of respawning objects queued to respawn.
	 */
	public ArrayList<Respawnable> respawnObjs = new ArrayList<Respawnable>();

	/**
	 * Increases object respawn times based on server population.
	 */
	private static double RESPAWN_TIME_POPULATION_MULT = 2;

	/**
	 * Runs every server tick.
	 */
	public void tick() {
		for (Respawnable r : respawnObjs) {
			if (r == null) {
				continue;
			}
			if (System.currentTimeMillis() - r.getRespawn() >= (Engine.playerCount() * RESPAWN_TIME_POPULATION_MULT)) {
				replace(r);
				respawnObjs.remove(r);
				break;
			}
		}
	}
	
	public void replace(Respawnable r) {
		for (Player p : Engine.players) {
			if (p == null || !p.online) {
				continue;
			}
			if (p.heightLevel != r.getHeight() || p.region != Region.getRegion(r.getX(), r.getY())) {
				continue;
			}
			p.frames.replaceObject(r.getId(), r.getX(), r.getY(), r.getHeight(), r.getRot(), 10);
		}
	}
	
	public void replace(int id, int x, int y, int height, int rot) {
		for (Player p : Engine.players) {
			if (p == null || !p.online) {
				continue;
			}
			if (p.heightLevel != height || p.region != Region.getRegion(x, y)) {
				continue;
			}
			p.frames.replaceObject(id, x, y, height, rot, 10);
		}
	}
	
	public void updateRegion(Player p) {
		/*for (Respawnable r : respawnObjs) {
			if (r == null) {
				continue;
			}
			if (p.heightLevel != r.getHeight() || p.region != Region.getRegion(r.getX(), r.getY())) {
				continue;
			}
			p.frames.replaceObject(r.getId(), r.getX(), r.getY(), r.getHeight(), r.getRot(), 10);
		}*/
	}

}
