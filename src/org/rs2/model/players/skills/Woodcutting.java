package org.rs2.model.players.skills;

import org.rs2.Engine;
import org.rs2.model.items.ItemDef;
import org.rs2.model.objects.Object;
import org.rs2.model.objects.Respawnable;
import org.rs2.model.objects.types.Tree;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.util.Misc;
import org.rs2.util.Skills;
import org.rs2.world.cache.region.Region;

public class Woodcutting {

	private Player p;

	public Woodcutting(Player p) {
		this.p = p;
	}

	public void start() {
		for (Trees t : Trees.values()) {

		}
	}

	public void removeTree(int id, int x, int y, int rot, int height) {
		for (Player p : Engine.players) {
			if (p == null || !p.online) {
				continue;
			}
			if (p.heightLevel != height || p.region != Region.getRegion(x, y)) {
				continue;
			}

		}
		Engine.objects.respawnObjs.add(new Respawnable(id, x, y, height, rot, -1, -1));
	}

	public enum Trees {
		NORMAL(1276, 1, 25, 1511, 1, 10);

		int id;
		int req;
		double xp;
		int logId;
		int maxHealth;
		int baseChance;

		Trees(int id, int req, double xp, int logId, int maxHealth, int baseChance) {
			this.id = id;
			this.req = req;
			this.xp = xp;
			this.logId = logId;
			this.maxHealth = maxHealth;
			this.baseChance = baseChance;
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

		public int getLogId() {
			return logId;
		}

		public int getMaxHealth() {
			return maxHealth;
		}

		public int getBaseChance() {
			return baseChance;
		}
	}

}
