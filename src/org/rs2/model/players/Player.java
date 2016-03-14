package org.rs2.model.players;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import org.rs2.Engine;
import org.rs2.Server;
import org.rs2.io.Frames;
import org.rs2.model.content.ButtonManager;
import org.rs2.model.content.DialogueContainer;
import org.rs2.model.content.SkillGuide;
import org.rs2.model.items.ItemDef;
import org.rs2.model.npcs.NPC;
import org.rs2.model.players.containers.Bank;
import org.rs2.model.players.containers.Equipment;
import org.rs2.model.players.containers.Inventory;
import org.rs2.model.players.containers.Trade;
import org.rs2.model.players.skills.Firemaking;
import org.rs2.model.players.skills.Fletching;
import org.rs2.model.players.skills.Magic;
import org.rs2.model.players.skills.Mining;
import org.rs2.model.players.skills.Prayer;
import org.rs2.model.players.skills.Woodcutting;
import org.rs2.net.PlayerSocket;
import org.rs2.task.Task;
import org.rs2.util.Skills;
import org.rs2.util.Stream;
import org.rs2.util.TextUtils;
import org.rs2.world.cache.region.Region;

public class Player {
	/**
	 * Player socket.
	 */
	public PlayerSocket socket;
	/**
	 * Stream for storing and converting bytes.
	 */
	public Stream stream = new Stream(500, 5000);
	/**
	 * Access to the frames class.
	 */
	public Frames frames = new Frames(this);
	/**
	 * Dialogue container.
	 */
	public DialogueContainer dialogue = new DialogueContainer(this);
	/**
	 * Actions currently assigned to the chatbox options.
	 */
	public ButtonManager buttons = new ButtonManager(this);
	/**
	 * Skill guides.
	 */
	public SkillGuide skillGuide = new SkillGuide(this);
	/**
	 * Access to skill classes.
	 */
	public Mining mining = new Mining(this);
	public Magic magic = new Magic(this);
	public Prayer prayer = new Prayer(this);
	public Woodcutting woodcutting = new Woodcutting(this);
	public Firemaking firemaking = new Firemaking(this);
	public Fletching fletching = new Fletching(this);
	/**
	 * Login stage
	 */
	public int loginStage;
	/**
	 * Player index.
	 */
	public int playerId;
	/**
	 * Player rights.
	 */
	public int rights;
	/**
	 * Last date the player logged in.
	 */
	public Date lastLogin;
	/**
	 * Last IP the player connected from.
	 */
	public String lastIP;
	/**
	 * Player name and password.
	 */
	public String playerName, playerPass;
	/**
	 * Auto-retaliating.
	 */
	public boolean autoRetaliate = true;
	/**
	 * Combat style.
	 */
	public int combatStyle;
	/**
	 * Settings.
	 */
	public int brightness = 2;
	public boolean singleMouseButton, chatEffects = true, splitPrivateChat, acceptAid;
	public int volumeMusic, volumeEffects;

	public int[] skillLevel = new int[21];
	public double[] skillXP = new double[21];
	/**
	 * Skill names.
	 */
	public static String[] skillNames = { "Attack", "Defence", "Strength", "Hitpoints", "Ranged",
		"Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking",
		"Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming",
		"Runecrafting" };
	/**
	 * Skill regeneration and degeneration.
	 */
	public long skillRegen = System.currentTimeMillis();
	public long skillDegen = System.currentTimeMillis();
	/**
	 * Inventory items.
	 */
	public Inventory inventory = new Inventory(this);
	/**
	 * Bank items.
	 */
	public Bank bank = new Bank(this);
	/**
	 * Bank withdraw mode.
	 */
	public boolean withdrawNoted;
	/**
	 * Worn equipment.
	 */
	public Equipment equipment = new Equipment(this);
	/**
	 * Equipment bonuses.
	 */
	public int[] equipmentBonus = new int[12];
	/**
	 * Trade items.
	 */
	public Trade trade = new Trade(this);
	/**
	 * Id of player being traded with.
	 */
	public int tradeId = -1;
	/**
	 * Quest data.
	 */
	public int[] quests = new int[10];
	/**
	 * Quest points QP the player currently has.
	 */
	public int questPoints;
	/**
	 * Friend and Ignore lists.
	 */
	public ArrayList<Long> friends = new ArrayList<Long>(200);
	public ArrayList<Long> ignores = new ArrayList<Long>(100);
	/**
	 * Chat settings.
	 */
	public int chatPublic, chatPrivate, chatTrade;
	/**
	 * Private message index.
	 */
	public int privateMessageId = 1;
	/**
	 * Player height level.
	 */
	public int heightLevel;
	/**
	 * Teleport coordinates.
	 */
	public int teleportToX, teleportToY;
	/**
	 * Region the player is currently in.
	 */
	public Region region;
	/**
	 * Absolute coordinates of player.
	 */
	public int absX, absY;
	/**
	 * Map region coordinates.
	 */
	public int mapRegionX, mapRegionY;
	/**
	 * Current player coordinates relative to map region.
	 */
	public int currentX, currentY;
	/**
	 * Set to true if map region changed.
	 */
	public boolean mapRegionChanged;
	/**
	 * Set to true if player teleported.
	 */
	public boolean teleported;
	/**
	 * Direction character is moving.
	 */
	public int walkDir, runDir;
	/**
	 * Walking data.
	 */
	public static int WALKING_QUEUE_SIZE = 50;

	public int wQueueReadPtr, wQueueWritePtr;

	public int walkingQueueX[] = new int[WALKING_QUEUE_SIZE],
		walkingQueueY[] = new int[WALKING_QUEUE_SIZE];
	public int walkingQueue[] = new int[WALKING_QUEUE_SIZE];

	public int newWalkCmdX[] = new int[WALKING_QUEUE_SIZE],
		newWalkCmdY[] = new int[WALKING_QUEUE_SIZE];
	public int newWalkCmdSteps;
	public boolean newWalkCmdRunning;

	public int newWalkDestX, newWalkDestY;

	public int travelBackX[] = new int[WALKING_QUEUE_SIZE],
		travelBackY[] = new int[WALKING_QUEUE_SIZE];
	public int numTravelBackSteps;

	public int poimiX, poimiY;

	/**
	 * Set to true if the player is running.
	 */
	public boolean running;
	/**
	 * Player energy, used for running
	 */
	public double runEnergy = 100;
	/**
	 * Set to true if the player energy requires updating.
	 */
	public boolean runEnergyUpdateReq;
	/**
	 * Energy regen.
	 */
	public long runEnergyRegen = System.currentTimeMillis();
	/**
	 * List of players in the area.
	 */
	public Player playerList[] = new Player[Engine.players.length];
	public byte[] playersInList = new byte[Engine.players.length];
	public int playerListSize;
	/**
	 * List of NPCs in the area.
	 */
	public NPC[] npcList = new NPC[Engine.npcs.length];
	public byte[] npcsInList = new byte[Engine.npcs.length];
	public int npcListSize;
	/**
	 * Set to true if player should disconnect.
	 */
	public boolean disconnected;
	/**
	 * Set to true if the player is finished login.
	 */
	public boolean online;
	/**
	 * Default player emotes.
	 */
	public int standAnim = 0x328;
	public int walkAnim = 0x333;
	public int runAnim = 0x338;
	public int blockEmote = 424;
	/**
	 * Set to true if player updating is required.
	 */
	public boolean updateReq = true;
	/**
	 * Graphics updating.
	 */
	public boolean gfxUpdateReq;
	public int gfxReq = -1, gfxDelay, gfxHeight;
	/**
	 * Animation updating.
	 */
	public boolean animUpdateReq;
	public int animReq = -1, animDelay;
	/**
	 * Forced chat.
	 */
	public boolean forcedChatUpdateReq;
	public String forcedChatText;
	/**
	 * Chat updating.
	 */
	public boolean chatTextUpdateReq;
	public byte chatText[] = new byte[4096];
	public byte chatTextSize;
	public int chatTextEffects, chatTextColor;
	/**
	 * Facing entity updating.
	 */
	public boolean faceEntityUpdateReq;
	public int faceEntityId;
	/**
	 * Player appearance updating.
	 */
	public boolean appearanceUpdateReq;
	/**
	 * Player appearance data.
	 */
	public int[] looks = new int[7], colors = new int[5];
	/**
	 * Player gender. 0 = male.
	 */
	public int gender;
	/**
	 * Head icons
	 */
	public int headIcon = -1;
	public int skullIcon = -1;
	/**
	 * Facing coordinate updating.
	 */
	public boolean faceDirUpdateReq;
	public int faceDirX, faceDirY;
	/**
	 * Hit updating.
	 */
	public boolean hitUpdateReq1, hitUpdateReq2;
	public int hitDiff1, hitDiff2;
	public int poisonHit1, poisonHit2;
	/**
	 * Ticks since last attack.
	 */
	public int lastAttack;
	/**
	 * Last time the player received damage.
	 */
	public long lastHit;
	/**
	 * Disguise as an NPC.
	 */
	public int disguiseId = -1;
	/**
	 * Report data
	 */
	public long lastReport;
	public String lastReportedPlayer;
	/**
	 * Id of the currently open interface.
	 */
	public int interfaceId = -1;
	/**
	 * Id of the currently open interface child (tab area
	 * overlay).
	 */
	public int interfaceChildId = -1;
	/**
	 * Id of the shop the player is currently accessing.
	 */
	public int shopId = -1;
	/**
	 * Remove X.
	 */
	public int removeXSlot;
	public int removeXInterfaceId;
	public int removeXId;
	/**
	 * Slot of the item that is being requested to be
	 * destroyed.
	 */
	public int destroySlot = -1;
	/**
	 * Amount of special energy the player has.
	 */
	public int specialEnergy;
	/**
	 * Used for regenerating special energy.
	 */
	private int specialEnergyRegen = Engine.currentTicks();
	/**
	 * Appearance interface variables.
	 */
	public int newHair, newBeard;
	public int newGender;
	public int newTorso;
	public int newArms;
	public int newLegs;
	public int newColor;
	/**
	 * Music player mode.
	 */
	public int musicPlayerMode;
	/**
	 * Music player looping.
	 */
	public boolean musicPlayerLoop;
	/**
	 * Last tick food was eaten at.
	 */
	public int lastTickFood;
	/**
	 * Last tick bones were buried at.
	 */
	public int lastTickBones;
	/**
	 * Last tick that an alchemy spell was cast.
	 */
	public int lastAlchCast;
	/**
	 * Set to true if the player is too busy to perform an
	 * action.
	 */
	public boolean busy;
	/**
	 * Tasks.
	 */
	public Task actionTask;
	public Task teleportTask;
	public Task walkingToTask;
	public Task kegTask;
	public Task boneBuryTask;
	public Task preachingTask;

	/**
	 * Available magic books.
	 */
	public enum MagicBooks {
		NORMAL(1151),
		ANCIENTS(12855);

		int id;

		MagicBooks(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}
	/**
	 * Currently active magic book.
	 */
	public MagicBooks magicBook = MagicBooks.NORMAL;
	public int npcDialogueId = -1;
	public int dialogueId;

	public int lastDummyHit;

	/**
	 * Constructs a new Player.
	 * @param Socket to tie player to.
	 * @param Player index.
	 */
	public Player(Socket socket, int id) {
		this.socket = new PlayerSocket(this, socket);
		playerId = id;

		for (int i = 0; i < skillLevel.length; i++) {
			if (i == 3) {
				skillLevel[i] = 10;
				skillXP[i] = 1155;
			} else {
				skillLevel[i] = 1;
				skillXP[i] = 0;
			}
		}
		looks[1] = 10;
		looks[2] = 18;
		looks[3] = 26;
		looks[4] = 33;
		looks[5] = 36;
		looks[6] = 42;

		teleportToX = 3094;
		teleportToY = 3107;
		heightLevel = 0;
		Engine.playerMovement.resetWalkingQueue(this);
	}

	/**
	 * Prepare player for disconnection.
	 */
	public void destruct() {
		for (Player op : Engine.players) {
			if (op == this || op == null || !op.online) {
				continue;
			}
			if (op.friends.contains(TextUtils.stringToLong(playerName))) {
				op.frames.sendFriend(TextUtils.stringToLong(playerName), 0);
			}
			if (op.tradeId == playerId) {
				op.tradeId = -1;
			}
		}
		Server.socketListener.connectedIPs.remove(lastIP);

		stream = null;
		try {
			socket.input.close();
			socket.output.close();
			socket.socket.close();
		} catch (Exception e) {
		}
		socket.input = null;
		socket.output = null;
		socket.socket = null;
		socket = null;
		absX = -1;
		absY = -1;
		mapRegionX = -1;
		mapRegionY = -1;
		Engine.playerMovement.resetWalkingQueue(this);

		System.out.println(playerName + " - logout.");
	}

	/**
	 * Player processing.
	 */
	public void tick() {
		if (prayer.activePrayers.size() > 0) {
			prayer.tick();
		}

		/**
		 * Skill degeneration.
		 */
		if (System.currentTimeMillis() - skillDegen >= 60000) {
			for (int i = 0; i < skillLevel.length; i++) {
				if (getLevelForXP(i) < skillLevel[i]) {
					skillLevel[i] -= 1;
					frames.sendSkillLevel(i);
				}
			}
			skillDegen = System.currentTimeMillis();
		}

		/**
		 * Skill regeneration.
		 */
		if (System.currentTimeMillis() - skillRegen >= 60000) {
			for (int i = 0; i < skillLevel.length; i++) {
				if (i == Skills.PRAYER) {
					continue;
				}
				if (getLevelForXP(Skills.PRAYER) > skillLevel[i]) {
					skillLevel[i] += 1;
					frames.sendSkillLevel(i);
				}
			}
			skillRegen = System.currentTimeMillis();
		}

		/**
		 * Special energy regen.
		 */
		if (specialEnergy < 100 && Engine.currentTicks() - specialEnergyRegen >= 50) {
			specialEnergy += 10;

			if (specialEnergy > 100) {
				specialEnergy = 100;
			}
			specialEnergyRegen = Engine.currentTicks();
		}

		/**
		 * Energy regen.
		 */
		if (runEnergy < 100
			&& System.currentTimeMillis() - runEnergyRegen >= (7500 - ((skillLevel[16] - 1) * 70))) {
			runEnergy++;
			runEnergyRegen = System.currentTimeMillis();
			runEnergyUpdateReq = true;
		}

		if (runEnergyUpdateReq) {
			if (runEnergy < 1) {
				runEnergy = 0;
				resetRun();
			}
			if (runEnergy > 254) {
				runEnergy = 100;
			}
			frames.sendEnergy();
			runEnergyUpdateReq = false;
		}
	}

	/**
	 * Disguise the player as an NPC. Walking will break the
	 * disguise.
	 * @param id Id of NPC to disguise as.
	 */
	public void setDisguise(int id) {
		frames.sendInventoryInterface(6014);
		requestWalk(0, 0);

		disguiseId = id;
		appearanceUpdateReq = true;
		updateReq = true;
	}

	/**
	 * Resets the player disguise.
	 */
	public void resetDisguise() {
		frames.closeAllInterfaces();

		disguiseId = -1;
		appearanceUpdateReq = true;
		updateReq = true;
	}

	/**
	 * Force chat text.
	 */
	public void requestForcedChat(String s) {
		forcedChatText = s;
		forcedChatUpdateReq = true;
		updateReq = true;
	}

	/**
	 * Request a graphic.
	 * @param id Id of graphic.
	 * @param delay Delay or height of graphic.
	 */
	public void requestGfx(int id, int delay) {
		gfxReq = id;
		gfxDelay = delay;
		gfxUpdateReq = true;
		updateReq = true;
	}

	public void requestGfx(int id, int delay, int height) {
		requestGfx(id, delay + (65536 * height));
	}

	/**
	 * Face an NPC.
	 * @param id Id of NPC to face.
	 */
	public void requestFaceNPC(int id) {
		faceEntityId = id;
		faceEntityUpdateReq = true;
		updateReq = true;
	}

	/**
	 * Face a player.
	 * @param id Id of player to face.
	 */
	public void requestFacePlayer(int id) {
		faceEntityId = (id != -1) ? 32768 + id : -1;
		faceEntityUpdateReq = true;
		updateReq = true;
	}

	/**
	 * Face specified coordinates.
	 * @param guideId Id of entity to face.
	 */
	public void requestFaceDir(int x, int y) {
		faceDirX = 2 * x + 1;
		faceDirY = 2 * y + 1;
		faceDirUpdateReq = true;
		updateReq = true;
	}

	/**
	 * Request an animation.
	 * @param id Id of animation.
	 * @param delay Delay before animation.
	 */
	public void requestAnim(int id, int delay) {
		animReq = id;
		animDelay = delay;
		animUpdateReq = true;
		updateReq = true;
	}

	public void requestAnim(int id) {
		animReq = id;
		animDelay = 0;
		animUpdateReq = true;
		updateReq = true;
	}

	/**
	 * Resets the current animation
	 */
	public void resetAnim() {
		requestAnim(-1, 0);
	}

	/**
	 * Forces the player to walk.
	 */
	public void resetRun() {
		running = false;
		frames.setConfig(173, 0);
	}

	/**
	 * Request a head icon (prayer).
	 * @param id
	 */
	public void requestHeadIcon(int id) {
		headIcon = id;
		appearanceUpdateReq = true;
		updateReq = true;
	}

	/**
	 * Request a skull icon.
	 * @param id
	 */
	public void requestSkullIcon(int id) {
		skullIcon = id;
		appearanceUpdateReq = true;
		updateReq = true;
	}

	/**
	 * Requests a walking queue.
	 * @param x Queue X.
	 * @param y Queue Y.
	 */
	public void requestWalk(int x, int y) {
		newWalkCmdSteps = (Math.abs((x + y)));

		if (newWalkCmdSteps % 1 != 0) {
			newWalkCmdSteps /= 1;
		}
		if (++newWalkCmdSteps > WALKING_QUEUE_SIZE) {
			newWalkCmdSteps = 0;
		}
		int firstStepX = absX;
		firstStepX -= mapRegionX * 8;

		for (int i = 1; i < newWalkCmdSteps; i++) {
			newWalkCmdX[i] = x;
			newWalkCmdY[i] = y;
		}
		newWalkCmdX[0] = 0;
		newWalkCmdY[0] = 0;

		int firstStepY = absY;
		firstStepY -= mapRegionY * 8;

		newWalkCmdRunning = ((stream.readSignedByteC() == 1) && runEnergy > 0);

		for (int i = 0; i < newWalkCmdSteps; i++) {
			newWalkCmdX[i] += firstStepX;
			newWalkCmdY[i] += firstStepY;
		}
		frames.resetDestination();
	}

	/**
	 * Finds a route and walks to the specified coordinates.
	 */
	public void findRoute(int destX, int destY, boolean moveNear, int xLength, int yLength) {
		if (destX == absX - 8 * mapRegionX && destY == absY - 8 * mapRegionY && !moveNear) {
			return;
		}
		destX = destX - 8 * mapRegionX;
		destY = destY - 8 * mapRegionY;

		int[][] via = new int[104][104];
		int[][] cost = new int[104][104];

		LinkedList<Integer> tileQueueX = new LinkedList<Integer>();
		LinkedList<Integer> tileQueueY = new LinkedList<Integer>();

		for (int xx = 0; xx < 104; xx++) {
			for (int yy = 0; yy < 104; yy++) {
				cost[xx][yy] = 99999999;
			}
		}
		int curX = absX - 8 * mapRegionX;
		int curY = absY - 8 * mapRegionY;

		via[curX][curY] = 99;
		cost[curX][curY] = 0;

		int tail = 0;
		tileQueueX.add(curX);
		tileQueueY.add(curY);

		boolean foundPath = false;
		int pathLength = 4000;
		while (tail != tileQueueX.size() && tileQueueX.size() < pathLength) {
			curX = tileQueueX.get(tail);
			curY = tileQueueY.get(tail);

			int curAbsX = mapRegionX * 8 + curX;
			int curAbsY = mapRegionY * 8 + curY;

			if (curX == destX && curY == destY) {
				foundPath = true;
				break;
			}
			tail = (tail + 1) % pathLength;

			int thisCost = cost[curX][curY] + 1;

			if (curY > 0 && via[curX][curY - 1] == 0
				&& (Region.getClipping(curAbsX, curAbsY - 1, heightLevel) & 0x1280102) == 0) {
				tileQueueX.add(curX);
				tileQueueY.add(curY - 1);
				via[curX][curY - 1] = 1;
				cost[curX][curY - 1] = thisCost;
			}
			if (curX > 0 && via[curX - 1][curY] == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY, heightLevel) & 0x1280108) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY);
				via[curX - 1][curY] = 2;
				cost[curX - 1][curY] = thisCost;
			}
			if (curY < 104 - 1 && via[curX][curY + 1] == 0
				&& (Region.getClipping(curAbsX, curAbsY + 1, heightLevel) & 0x1280120) == 0) {
				tileQueueX.add(curX);
				tileQueueY.add(curY + 1);
				via[curX][curY + 1] = 4;
				cost[curX][curY + 1] = thisCost;
			}
			if (curX < 104 - 1 && via[curX + 1][curY] == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY, heightLevel) & 0x1280180) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY);
				via[curX + 1][curY] = 8;
				cost[curX + 1][curY] = thisCost;
			}
			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY - 1, heightLevel) & 0x128010e) == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY, heightLevel) & 0x1280108) == 0
				&& (Region.getClipping(curAbsX, curAbsY - 1, heightLevel) & 0x1280102) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY - 1);
				via[curX - 1][curY - 1] = 3;
				cost[curX - 1][curY - 1] = thisCost;
			}
			if (curX > 0 && curY < 104 - 1 && via[curX - 1][curY + 1] == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY + 1, heightLevel) & 0x1280138) == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY, heightLevel) & 0x1280108) == 0
				&& (Region.getClipping(curAbsX, curAbsY + 1, heightLevel) & 0x1280120) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY + 1);
				via[curX - 1][curY + 1] = 6;
				cost[curX - 1][curY + 1] = thisCost;
			}
			if (curX < 104 - 1 && curY > 0 && via[curX + 1][curY - 1] == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY - 1, heightLevel) & 0x1280183) == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY, heightLevel) & 0x1280180) == 0
				&& (Region.getClipping(curAbsX, curAbsY - 1, heightLevel) & 0x1280102) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY - 1);
				via[curX + 1][curY - 1] = 9;
				cost[curX + 1][curY - 1] = thisCost;
			}
			if (curX < 104 - 1 && curY < 104 - 1 && via[curX + 1][curY + 1] == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY + 1, heightLevel) & 0x12801e0) == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY, heightLevel) & 0x1280180) == 0
				&& (Region.getClipping(curAbsX, curAbsY + 1, heightLevel) & 0x1280120) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY + 1);
				via[curX + 1][curY + 1] = 12;
				cost[curX + 1][curY + 1] = thisCost;
			}
		}

		if (!foundPath) {
			if (moveNear) {
				int i_223_ = 1000;
				int thisCost = 100;
				int i_225_ = 10;
				for (int x = destX - i_225_; x <= destX + i_225_; x++) {
					for (int y = destY - i_225_; y <= destY + i_225_; y++) {
						if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < 100) {
							int i_228_ = 0;
							if (x < destX) {
								i_228_ = destX - x;
							} else if (x > destX + xLength - 1) {
								i_228_ = x - (destX + xLength - 1);
							}
							int i_229_ = 0;
							if (y < destY) {
								i_229_ = destY - y;
							} else if (y > destY + yLength - 1) {
								i_229_ = y - (destY + yLength - 1);
							}
							int i_230_ = i_228_ * i_228_ + i_229_ * i_229_;
							if (i_230_ < i_223_ || (i_230_ == i_223_ && (cost[x][y] < thisCost))) {
								i_223_ = i_230_;
								thisCost = cost[x][y];
								curX = x;
								curY = y;
							}
						}
					}
				}
				if (i_223_ == 1000) {
					return;
				}
			} else {
				return;
			}
		}
		tail = 0;
		tileQueueX.set(tail, curX);
		tileQueueY.set(tail++, curY);
		int l5;
		for (int j5 = l5 = via[curX][curY]; curX != absX - 8 * mapRegionX
			|| curY != absY - 8 * mapRegionY; j5 = via[curX][curY]) {
			if (j5 != l5) {
				l5 = j5;
				tileQueueX.set(tail, curX);
				tileQueueY.set(tail++, curY);
			}
			if ((j5 & 2) != 0) {
				curX++;
			} else if ((j5 & 8) != 0) {
				curX--;
			}
			if ((j5 & 1) != 0) {
				curY++;
			} else if ((j5 & 4) != 0) {
				curY--;
			}
		}
		Engine.playerMovement.resetWalkingQueue(this);
		int size = tail--;
		int pathX = mapRegionX * 8 + tileQueueX.get(tail);
		int pathY = mapRegionY * 8 + tileQueueY.get(tail);
		Engine.playerMovement.addToWalkingQueue(this, localize(pathX, mapRegionX),
			localize(pathY, mapRegionY));

		for (int i = 1; i < size; i++) {
			tail--;
			pathX = mapRegionX * 8 + tileQueueX.get(tail);
			pathY = mapRegionY * 8 + tileQueueY.get(tail);
			Engine.playerMovement.addToWalkingQueue(this, localize(pathX, mapRegionX),
				localize(pathY, mapRegionY));
		}
	}

	private int localize(int x, int mapRegion) {
		return x - 8 * mapRegion;
	}

	/**
	 * Resets the player's current action task.
	 */
	public void resetActionTask() {
		if (actionTask != null) {
			actionTask.stop();
			actionTask = null;
		}
	}

	/**
	 * Returns true if the player is too busy to perform any
	 * actions.
	 */
	public boolean isBusy() {
		if (busy) {
			return true;
		}
		if (dead()) {
			return true;
		}
		if (teleportTask != null) {
			return true;
		}
		if (boneBuryTask != null) {
			return true;
		}
		if (preachingTask != null) {
			return true;
		}
		return false;
	}

	/**
	 * Apply damage.
	 * @param damage Damage dealt.
	 * @param poison Poison damage dealt.
	 */
	public void appendHit(int damage, int poison) {
		if (damage > skillLevel[3]) {
			damage = skillLevel[3];
		}
		updateHP(damage, false);

		if (!hitUpdateReq1) {
			hitDiff1 = damage;
			poisonHit1 = poison;
			hitUpdateReq1 = true;
		} else {
			hitDiff2 = damage;
			poisonHit2 = poison;
			hitUpdateReq2 = true;
		}
		updateReq = true;
	}

	/**
	 * Update player HP.
	 * @param amount Amount to affect HP by.
	 * @param heal Set to true if healing player.
	 */
	public void updateHP(int amount, boolean heal) {
		if (heal) {
			skillLevel[3] += amount;

			if (skillLevel[3] > getLevelForXP(Skills.HITPOINTS)) {
				skillLevel[3] = getLevelForXP(Skills.HITPOINTS);
			}
		} else {
			skillLevel[3] -= amount;

			/*if (dead()) {
				preventWalk = true;
				skillLevel[3] = 0;
				Engine.playerMovement.resetWalkingQueue(this);
				requestAnim(2304, 0);

				final int[] deathItems = new int[42];
				final int[] deathItemsN = new int[42];
				final int[] keep = new int[4];

				for (int i = 0; i < deathItems.length; i++) {
					deathItems[i] = (i < 28) ? items[i] : equipment[i - 28];
					deathItemsN[i] = (i < 28) ? itemsN[i] : equipmentN[i - 28];

					if (ItemDef.getDropItem(deathItems[i]) != -1) {
						deathItems[i] = ItemDef.getDropItem(deathItems[i]);
					}
				}
				for (int i = 0; i < keep.length; i++) {
					keep[i] = -1;
				}
				for (int i = 0; i < keep.length; i++) {
					int maxValue = -1;
					int item = -1;

					for (int j = 0; j < deathItems.length; j++) {
						if (ItemDef.getValue(deathItems[j]) > maxValue) {
							maxValue = ItemDef.getValue(deathItems[j]);
							item = j;
						}
					}
					if (maxValue > -1) {
						keep[i] = deathItems[item];
						deathItemsN[item]--;

						if (deathItemsN[item] < 1) {
							deathItems[item] = -1;
						}
					}
				}

				Engine.task.schedule(new Task() {
					int count = 0;

					@Override
					public void execute() {
						count++;

						if (count == 2) {
							teleportToX = 3222;
							teleportToY = 3218;

							for (int i = 0; i < skillLevel.length; i++) {
								skillLevel[i] = getLevelForXP(i);
								frames.sendSkillLevel(i);
							}
							for (int i = 0; i < equipment.length; i++) {
								equipment[i] = -1;
								equipmentN[i] = -1;
								frames.sendEquipment(equipment[i], equipmentN[i], i);
							}
							for (int i = 0; i < items.length; i++) {
								item.deleteItem(items[i], itemsN[i]);
							}
							for (int i = 0; i < deathItems.length; i++) {
								if (deathItems[i] == -1) {
									continue;
								}
								item.dropItem(deathItems[i], deathItemsN[i]);
							}
							frames.sendBonuses();

							energy = 100;
							resetRun();
							resetAnim();

							frames.sendMessage("Oh dear, you died!");
							for (int i = 0; i < keep.length; i++) {
								item.addItem(keep[i], 1);
							}
							preventWalk = false;
							stop();
						}
					}
				});
			}*/
			lastHit = System.currentTimeMillis();
		}
		frames.sendSkillLevel(Skills.HITPOINTS);
	}

	public boolean drainSpecial(int special) {
		if (specialEnergy >= special) {
			specialEnergy -= special;
			return true;
		}
		return false;
	}

	/**
	 * Returns true if player is dead.
	 */
	public boolean dead() {
		if (skillLevel[3] <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Returns level for amount of XP acquired in a specific
	 * skill.
	 * @param id SKill ID.
	 */
	public int getLevelForXP(int id) {
		double xp = skillXP[id];
		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl < 99; lvl++) {
			points += Math.floor((double) lvl + 300.0 * Math.pow(2.0, (double) lvl / 7.0));
			output = (int) Math.floor(points / 4);

			if (output - 1 >= xp) {
				return lvl;
			}
		}
		return 99;
	}

	/**
	 * Add experience points to the specified skill.
	 * @param skill Skill Id.
	 * @param exp Experience to add.
	 */
	public void addSkillXP(int skill, double exp) {
		if (skill > skillXP.length || skill < 0) {
			return;
		}
		int oldLevel = getLevelForXP(skill);
		skillXP[skill] += exp;
		int newLevel = getLevelForXP(skill);

		if (oldLevel < newLevel) {
			skillLevel[skill] += (newLevel - oldLevel);
			sendLevelUp(skill);
		}
		frames.sendSkillLevel(skill);
	}

	/**
	 * Increases a skill.
	 * @param skill Skill to increase.
	 * @param mult Amount to multiply skill by.
	 * @param mod Amount to add to skill.
	 */
	public void increaseSkill(int skill, double mult, int mod) {
		if (mod < 0 || mult < 1) {
			return;
		}
		int newLevel = getLevelForXP(skill)
			+ (int) Math.floor(getLevelForXP(skill) * mult - getLevelForXP(skill) + mod);

		if (newLevel > skillLevel[skill]) {
			skillLevel[skill] = newLevel;
			frames.sendSkillLevel(skill);
		}
	}

	/**
	 * Reduces a skill.
	 * @param skill Skill to reduce.
	 * @param mult Amount to multiply skill by.
	 * @param mod Amount to subtract from skill.
	 * @param floor Minimum level of the skill.
	 */
	public void reduceSkill(int skill, double mult, int mod) {
		if (mod > 0 || mult >= 1) {
			return;
		}
		int newLevel = skillLevel[skill]
			+ (int) Math.floor(getLevelForXP(skill) * mult - getLevelForXP(skill) + mod);

		if (newLevel < skillLevel[skill]) {
			skillLevel[skill] = newLevel;
			frames.sendSkillLevel(skill);
		}
	}

	/**
	 * Reduces a skill.
	 * @param skill Skill to reduce.
	 * @param mod Amount to subtract from skill.
	 * @param mult Amount to multiply skill by.
	 * @param floor Minimum level of the skill.
	 */
	public void reduceSkill(int skill, int mod, double mult, int floor) {
		if (mod > 0 || mult >= 1) {
			return;
		}
		int newLevel = getLevelForXP(skill)
			+ (int) Math.floor(getLevelForXP(skill) * mult - getLevelForXP(skill) + mod);

		if (newLevel < skillLevel[skill]) {
			if (newLevel < floor) {
				newLevel = floor;
			}
			skillLevel[skill] = newLevel;
			frames.sendSkillLevel(skill);
		}
	}

	/**
	 * Send level up interface for skill.
	 * @param skill Skill to show interface for.
	 */
	public void sendLevelUp(int skill) {
		int[][] interfaces = { { 6247, 6248, 6249 }, { 6253, 6254, 6255 }, { 6206, 6207, 6208 },
			{ 6216, 6217, 6218 }, { 4443, 5453, 6114 }, { 6242, 6243, 6244 }, { 6211, 6212, 6213 },
			{ 6226, 6227, 6228 }, { 4272, 4273, 4274 }, { 6231, 6232, 6233 }, { 6258, 6259, 6260 },
			{ 4282, 4283, 4284 }, { 6263, 6264, 6265 }, { 6221, 6222, 6223 }, { 4416, 4417, 4438 },
			{ 6237, 6238, 6239 }, { 4277, 4278, 4279 }, { 4261, 4263, 4264 },
			{ 12122, 12123, 12124 }, { 4887, 4268, 4269 }, { 4267, 4268, 4269 } };
		frames.setString(
			"@dbl@Congratulations, you just advanced "
				+ (skillNames[skill].startsWith("a") ? "an" : "a") + " "
				+ skillNames[skill].toLowerCase() + " level.", interfaces[skill][1]);
		frames.setString("Your " + skillNames[skill].toLowerCase() + " level is now "
			+ getLevelForXP(skill) + ".", interfaces[skill][2]);
		frames.sendChatInterface(interfaces[skill][0]);

		frames.sendMessage("Congratulations, you just advanced "
			+ (skillNames[skill].startsWith("a") ? "an" : "a") + " "
			+ skillNames[skill].toLowerCase() + " level.");
		requestGfx(199, 0, 50);
	}

	/**
	 * Calculates the amount of 'squares' between the player
	 * and the specified coordinates.
	 * @param x Coordinate X.
	 * @param y Coordinate Y.
	 */
	public int distanceToPoint(int x, int y) {
		return (int) Math.sqrt(Math.pow(absX - x, 2) + Math.pow(absY - y, 2));
	}

	public boolean withinDistance(int playerX, int playerY, int objectX, int objectY, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX
					&& ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX
					&& ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX
					&& ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if the p2 is within viewing distance of
	 * this player.
	 * @param p2 Player to check for.
	 */
	public boolean withinDistance(Player p2) {
		if (heightLevel != p2.heightLevel) {
			return false;
		}
		int deltaX = p2.absX - absX;
		int deltaY = p2.absY - absY;
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	/**
	 * Gets the world for a friend.
	 * @param friend Friend to get world for.
	 */
	public int getWorld(Long friend) {
		for (Player p : Engine.players) {
			if (p == null || p.playerName == null) {
				continue;
			}
			if (friend == TextUtils.stringToLong(p.playerName)) {
				/**
				 * Moderators and admins bypass chat
				 * privacy.
				 */
				if (rights > 0) {
					return 1;
				}
				if (p.chatPrivate == 2) {
					return 0;
				}
				if (p.chatPrivate == 1 && !p.friends.contains(TextUtils.stringToLong(playerName))) {
					return 0;
				}
				return 1;
			}
		}
		return 0;
	}

	/**
	 * Updates the friends lists of every player with this
	 * player added.
	 */
	public void updateFriends() {
		long name = TextUtils.stringToLong(playerName);

		for (Player op : Engine.players) {
			if (op == null || op.stream == null || op.disconnected) {
				continue;
			}
			if (op.friends.contains(name)) {
				op.frames.sendFriend(name, op.getWorld(name));
			}
		}
	}

	/**
	 * Calculates equipment bonuses in preparation for
	 * writing them to the interface
	 */
	public void calculateBonuses() {
		for (int i = 0; i < equipmentBonus.length; i++) {
			equipmentBonus[i] = 0;
		}
		for (int i = 0; i < equipment.capacity(); i++) {
			if (equipment.get(i) == null) {
				continue;
			}
			for (int j = 0; j < equipmentBonus.length; j++) {
				equipmentBonus[j] += ItemDef.getBonus(equipment.getId(i), j);
			}
		}
	}

	/**
	 * Gets the combined weight of equipment and inventory.
	 */
	public double getTotalWeight() {
		double weight = 0;

		for (int i = 0; i < inventory.capacity(); i++) {
			if (ItemDef.stackable(inventory.getId(i))) {
				continue;
			}
			weight += ItemDef.getWeight(inventory.getId(i));
		}
		for (int i = 0; i < equipment.capacity(); i++) {
			if (ItemDef.stackable(equipment.getId(i))) {
				continue;
			}
			weight += ItemDef.getWeightEq(equipment.getId(i));
		}
		return weight;
	}

	/**
	 * Resets interface vars. TODO: Move to interface
	 * listener.
	 */
	public void resetInterfaces() {
		interfaceId = -1;
		interfaceChildId = -1;
		shopId = -1;
		skillGuide.guideId = -1;
		skillGuide.pageId = -1;
		destroySlot = -1;
		if (dialogue.options != null) {
			dialogue.options = null;
		}
		buttons.events.clear();
	}

	/**
	 * Returns true if the player reaches their walking
	 * destination.
	 */
	public boolean destinationReached() {
		if (newWalkDestX == 0 && newWalkDestY == 0) {
			return true;
		}
		return absX - mapRegionX * 8 == newWalkDestX && absY - mapRegionY * 8 == newWalkDestY;
	}

	public void teleport(int x, int y, int height) {
		teleportToX = x;
		teleportToY = y;
		heightLevel = height;
	}

	public void setHeightLevel(int heightLevel) {
		teleport(absX, absY, heightLevel);
	}
}
