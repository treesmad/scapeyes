package org.rs2;

import java.net.Socket;
import org.rs2.io.FileManager;
import org.rs2.io.Login;
import org.rs2.io.PacketManager;
import org.rs2.model.content.SkillGuide;
import org.rs2.model.items.ItemDef;
import org.rs2.model.npcs.NPC;
import org.rs2.model.npcs.update.NPCMovement;
import org.rs2.model.npcs.update.NPCUpdate;
import org.rs2.model.objects.Object;
import org.rs2.model.players.Player;
import org.rs2.model.players.update.PlayerMovement;
import org.rs2.model.players.update.PlayerUpdate;
import org.rs2.task.Task;
import org.rs2.task.TaskScheduler;
import org.rs2.world.Ground;
import org.rs2.world.Shop;
import org.rs2.world.cache.region.ObjectDef;
import org.rs2.world.cache.region.Region;

public class Engine implements Runnable {
	/**
	 * Array of players.
	 */
	public static Player[] players = new Player[2000];
	/**
	 * Player updating.
	 */
	public static PlayerUpdate playerUpdate = new PlayerUpdate();
	/**
	 * Player movement.
	 */
	public static PlayerMovement playerMovement = new PlayerMovement();
	/**
	 * Array of NPCs.
	 */
	public static NPC[] npcs = new NPC[10000];
	/**
	 * NPC updating.
	 */
	public static NPCUpdate npcUpdate = new NPCUpdate();
	/**
	 * NPC movement.
	 */
	public static NPCMovement npcMovement = new NPCMovement();
	/**
	 * Access to objects class.
	 */
	public static Object objects = new Object();
	/**
	 * Ground items.
	 */
	public static Ground ground = new Ground();
	/**
	 * Access to the packet manager.
	 */
	public static PacketManager packetManager = new PacketManager();
	/**
	 * Task scheduler for cycle based tasks.
	 */
	public static TaskScheduler task = new TaskScheduler();
	/**
	 * Thread the engine runs on.
	 */
	private Thread engineThread = new Thread(this);
	/**
	 * Set to true if the engine should run.
	 */
	private boolean engineRunning = true;
	/**
	 * Set to true if a server update has started
	 */
	public static boolean updating;
	/**
	 * When the time reaches zero, the server will disconnect all players and
	 * stop accepting logins.
	 */
	public static int updateTime;
	/**
	 * For every second that passes, decrement updateTime.
	 */
	public static long lastUpdate = System.currentTimeMillis();
	/**
	 * Track the number of game ticks since server initialization.
	 */
	public static int ticks;

	/**
	 * Constructs a new Engine
	 */
	public Engine() {
		engineThread.start();
	}

	@Override
	public void run() {
		/**
		 * TODO: Remove
		 */
		newNPC(0, 3222, 3218, 0, -1, false);
		
		ItemDef.loadDefinitions();
		Shop.loadDefinitions();
		SkillGuide.load();
		ObjectDef.loadConfig();
		Region.load();

		task.schedule(new Task() {
			@Override
			protected void execute() {
				Shop.tick();
				objects.tick();
				ground.tick();

				for (Player p : players) {
					if (p == null || !p.online) {
						continue;
					}
					if (p.disconnected) {
						removePlayer(p.playerId);
						continue;
					}
					p.tick();
					playerMovement.updateMovement(p);
					playerMovement.getNextPlayerMovement(p);
				}
				for (Player p : players) {
					if (p == null || !p.online) {
						continue;
					}
					playerUpdate.update(p);
				}
				for (Player p : players) {
					if (p == null || !p.online) {
						continue;
					}
					playerUpdate.clearUpdateReqs(p);
					Server.socketListener.writeBuffer(p);
				}
				for (NPC n : npcs) {
					if (n == null) {
						continue;
					}
					npcUpdate.clearUpdateReqs(n);
				}
				for (NPC n : npcs) {
					if (n == null) {
						continue;
					}
					n.tick();
				}
				ticks++;
			}
		});
		long curTime;

		while (engineRunning) {
			curTime = System.currentTimeMillis();
			connectAwaitingPlayers();
			packetManager.parseIncomingPackets();

			if (updating && System.currentTimeMillis() - lastUpdate >= 1000) {
				updateTime--;

				if (updateTime < 1) {
					for (Player p : players) {
						if (p == null || !p.online) {
							continue;
						}
						p.disconnected = true;
					}
				}
				lastUpdate = System.currentTimeMillis();
			}
			try {
				Thread.sleep(100 - (System.currentTimeMillis() - curTime));
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Run the login protocol for any players that haven't completed it.
	 */
	public void connectAwaitingPlayers() {
		Login login = null;

		for (Player p : players) {
			if (p == null || p.online) {
				continue;
			}
			if (login == null) {
				login = new Login();
			}
			login.login(p);
		}
	}

	/**
	 * Add a connection for a player.
	 * @param socket Socket to assign to player.
	 * @param id Index of player.
	 */
	public void addConnection(Socket socket, int id) {
		if (id == 0) {
			Server.socketListener.removeSocket(id);
			return;
		}
		players[id] = new Player(socket, id);
	}

	/**
	 * Remove a player.
	 * @param id Index of player to remove.
	 */
	public void removePlayer(int id) {
		Player p = players[id];

		if (p == null) {
			return;
		}
		/**
		 * Finish teleport before logging out.
		 */
		if (p.teleportTask != null) {
			p.absX = p.teleportToX;
			p.absY = p.teleportToY;
			return;
		}
		/**
		 * Cancel the active trade.
		 */
		//p.trade.decline();
		/**
		 * Prevent logging out if player has taken damage in the last 30
		 * seconds.
		 */
		if (System.currentTimeMillis() - p.lastHit < 30000) {
			return;
		}
		/**
		 * Save profile.
		 */
		FileManager.profileSave(p);
		/**
		 * Clean up player before removal.
		 */
		p.destruct();
		players[id] = null;

		Server.socketListener.removeSocket(id);
	}
	
	public void newNPC(int type, int absX, int absY, int height, int face, boolean walk) {
		int id = -1;
		
		for (int i = 0; i < Engine.npcs.length; i++) {
			if (Engine.npcs[i] == null) {
				id = i;
			}
		}
		if (id == -1) {
			System.out.println("Too many NPCs!");
			return;
		}
		Engine.npcs[id] = new NPC(id, type);
		NPC npc = Engine.npcs[id];
		npc.absX = absX;
		npc.absY = absY;
		npc.heightLevel = height;
	}
	
	/**
	 * Returns the amount of game ticks that have passed.
	 */
	public static int currentTicks() {
		return ticks;
	}

	/**
	 * Returns the number of players currently online.
	 */
	public static int playerCount() {
		int count = 0;

		for (Player p : players) {
			if (p != null) {
				count++;
			}
		}
		return count;
	}

}