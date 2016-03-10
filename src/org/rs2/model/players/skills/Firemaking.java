package org.rs2.model.players.skills;

import org.rs2.Engine;
import org.rs2.model.items.ItemDef;
import org.rs2.model.objects.Object;
import org.rs2.model.objects.types.Fire;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.util.Misc;
import org.rs2.util.Skills;
import org.rs2.world.cache.region.Region;

public class Firemaking {
	/**
	 * Player this class belongs to.
	 */
	private Player p;

	/**
	 * Construct a new Firemaking class.
	 * @param p Player to assign this class to.
	 */
	public Firemaking(Player p) {
		this.p = p;
	}

	public boolean prohibitedLocation() {
		return false;
	}

	/**
	 * Starts firemaking if the player isn't currently busy and meets the
	 * firemaking requirement.
	 * @param log Log to light.
	 * @param slot Slot to drop log from.
	 */
	public void start(final int log, final int slot) {
		if (p.skillLevel[Skills.FIREMAKING] < Logs.forId(log).getReq()) {
			p.frames.sendMessage("You need a " + Player.skillNames[Skills.FIREMAKING] + " level of "
				+ Logs.forId(log).getReq() + " to burn " + ItemDef.getName(log).toLowerCase());
			return;
		}
		if (Fire.exists(p.absX, p.absY, p.heightLevel) || prohibitedLocation()) {
			p.frames.sendMessage("You can't light a fire here.");
			return;
		}
		p.frames.sendMessage("You attempt to light the logs.");
		p.requestAnim(733);
		p.frames.sendSound(375, 600, 10);

		if (slot != -1) {
			//p.item.dropItem(log, 1);
		}
		int x = p.absX;
		int y = p.absY;
		int height = p.heightLevel;

		Engine.task.schedule(p.actionTask = new Task(2, true) {
			int ticks;

			@Override
			protected void execute() {
				if (p == null || !p.online || p.disconnected) {
					stop();
					return;
				}
				/*if (!DroppedItem.exists(log, 1, x, y, height)) {
					stop();
					return;
				}*/
				if (ticks > 0
					&& Misc.random(100) < Logs.forId(log).getChance() + ((p.skillLevel[Skills.FIREMAKING]) + 1) * 0.2) {
					p.frames.sendMessage("The fire catches and the logs begin to burn.");

					p.addSkillXP(Skills.FIREMAKING, Logs.forId(log).getXP());
					//DroppedItem.remove(log, 1, x, y, height);
					p.frames.addObject(3038, p.absX, p.absY, p.heightLevel, 0, 10);
					//Object.fires.add(new Fire(p.absX, p.absY, p.heightLevel));

					p.resetAnim();
					p.frames.sendSound(240, 0, 10);
					if (!Region.blockedWest(p.absX, p.absY, p.heightLevel)) {
						p.requestWalk(-1, 0);
					} else {
						p.requestWalk(1, 0);
					}
					stop();
					return;
				}
				if (ticks % 2 == 0 && ticks > 0) {
					p.requestAnim(733);
					p.frames.sendSound(375, 600, 10);
				}
				ticks++;
			}

		});
	}

	/**
	 * Start firemaking without dropping a log.
	 * @param log Log to light.
	 */
	public void start(int log) {
		start(log, -1);
	}

	/**
	 * Force player to stop firemaking.
	 */
	public void stop() {
		p.resetActionTask();
		p.resetAnim();
	}

	public enum Logs {
		LOGS(1511, 1, 40, 20),
		OAK_LOGS(1521, 15, 60, 18),
		WILLOW_LOGS(1519, 30, 90, 14);

		private int id = -1;
		private int req;
		private double xp;
		private double chance;

		Logs(int id, int req, double xp, double chance) {
			this.id = id;
			this.req = req;
			this.xp = xp;
			this.chance = chance;
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

		public double getChance() {
			return chance;
		}

		public static Logs forId(int id) {
			for (Logs l : Logs.values()) {
				if (l.getId() == id) {
					return l;
				}
			}
			return null;
		}

		public static boolean isLog(int id) {
			for (Logs l : Logs.values()) {
				if (l.getId() == id) {
					return true;
				}
			}
			return false;
		}
	}

}
