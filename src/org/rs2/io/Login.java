package org.rs2.io;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.rs2.Engine;
import org.rs2.Server;
import org.rs2.model.objects.Object;
import org.rs2.model.players.Player;
import org.rs2.world.Ground;

/**
 * Login protocol.
 */
public class Login {

	public int returnCode = 2;

	public void login(Player p) {
		if (p == null || p.stream == null) {
			return;
		}
		long serverSessionKey = ((long) (Math.random() * 99999999D) << 32)
			+ (long) (Math.random() * 99999999D);

		if (p.loginStage == -1) {
			p.disconnected = true;
		}
		if (p.loginStage == 0) {
			try {
				fillStream(p, 2);
			} catch (Exception e) {
				return;
			}
			p.stream.readUnsignedByte();
			p.stream.readUnsignedByte();
			p.loginStage++;
		}
		if (p.loginStage == 1) {
			for (int i = 0; i < 8; i++) {
				p.stream.writeByte(0);
			}
			p.stream.writeByte(0);
			p.stream.writeQWord(serverSessionKey);
			directFlushStream(p);
			p.loginStage++;
		}
		if (p.loginStage == 2) {
			try {
				fillStream(p, 2);
			} catch (Exception e) {
				return;
			}
			int connectStatus = p.stream.readUnsignedByte();

			if (connectStatus != 16 && connectStatus != 18) {
				p.loginStage = -1;
				return;
			}
			int loginPacketSize = p.stream.readUnsignedByte();

			try {
				fillStream(p, loginPacketSize);
			} catch (Exception e) {
				return;
			}
			if (p.stream.readUnsignedByte() != 255 || p.stream.readUnsignedWord() != 317) {
				p.loginStage = -1;
				return;
			}
			p.stream.readUnsignedByte();

			for (int i = 0; i < 9; i++) {
				p.stream.readDWord();
			}
			p.stream.readUnsignedByte();

			p.stream.readQWord();
			serverSessionKey = p.stream.readQWord();

			p.stream.readDWord();

			p.playerName = p.stream.readString();

			if (p.playerName == null) {
				p.loginStage = -1;
				return;
			}
			for (int i = 0; i < p.playerName.length(); i++) {
				Character c = new Character(p.playerName.charAt(i));

				if (!Character.isLetterOrDigit(c) && !Character.isSpaceChar(c)) {
					p.loginStage = -1;
					p.playerName = "";
					return;
				}
			}
			String playerPass = p.stream.readString();
			playerPass = FileManager.md5(playerPass.toLowerCase());

			if (playerPass == null) {
				p.loginStage = -1;
				return;
			}
			for (int i = 0; i < playerPass.length(); i++) {
				Character c = new Character(playerPass.charAt(i));
				if (!Character.isLetterOrDigit(c) && !Character.isSpaceChar(c)) {
					p.loginStage = -1;
					return;
				}
			}
			FileManager.profileLoad(p);

			if (p.playerName == null || playerPass == null || p.playerName.length() < 1
				|| p.playerName.length() > 11 || playerPass.length() < 5) {
				System.out.println(p.playerName + " - login failed, invalid username or password.");
				returnCode = 3;

			} else if (playerOnline(p, p.playerName) || p.disconnected) {
				System.out.println(p.playerName + " - login failed, account already logged in.");
				returnCode = 5;

			} else if (Engine.playerCount() == Engine.players.length) {
				System.out.println(p.playerName + " - login failed, server full.");
				returnCode = 7;

			} else if (Server.socketListener.connectedIPs.contains(p.socket.socket.getInetAddress()
				.getHostAddress()) && p.rights < 2) {
				System.out.println(p.playerName
					+ " - login failed, too many connections from address.");
				returnCode = 9;

			} else if (Engine.updating && Engine.updateTime < 1) {
				System.out.println(p.playerName + " - login failed, update running.");
				returnCode = 14;

			} else {
				if (playerPass != null && p.playerPass != null
					&& !p.playerPass.equalsIgnoreCase(playerPass)) {
					System.out.println(p.playerName + " - login failed, invalid password.");
					returnCode = 3;
				} else {
					System.out.println(p.playerName + " - login succeeded.");
					p.playerPass = playerPass;
				}
			}
			p.loginStage++;
		}
		if (p.loginStage == 3) {
			p.stream.writeByte(returnCode);
			p.stream.writeByte(p.rights);
			p.stream.writeByte(0);

			Engine.playerMovement.getNextPlayerMovement(p);
			p.frames.setMapRegion();
			directFlushStream(p);

			if (returnCode != 2) {
				p.loginStage = -1;
				return;
			}

			p.stream.createFrame(249);
			p.stream.writeByteA(1);
			p.stream.writeWordBigEndianA(p.playerId);
			p.stream.createFrame(107);

			/*buildWelcomeScreen(p);
			p.frames.sendInterface(15244);*/

			p.frames.sendMessage("Welcome to RuneScape.");

			p.lastIP = p.socket.socket.getInetAddress().getHostAddress();
			Server.socketListener.connectedIPs.add(p.lastIP);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			try {
				p.lastLogin = formatter.parse(formatter.format(date));
			} catch (ParseException e) {
			}

			p.frames.setTab(0, 5855);
			p.frames.setTab(1, 3917);
			p.frames.setTab(2, 638);
			p.frames.setTab(3, 3213);
			p.frames.setTab(4, 1644);
			p.frames.setTab(5, 5608);
			p.frames.setTab(6, p.magicBook.getId());
			p.frames.setTab(7, -1);
			p.frames.setTab(8, 5065);
			p.frames.setTab(9, 5715);
			p.frames.setTab(10, 2449);
			p.frames.setTab(11, 904);
			p.frames.setTab(12, 147);
			p.frames.setTab(13, 962);

			p.frames.sendContextMenu("null", 1);
			p.frames.sendContextMenu("Trade with", 3);
			p.frames.sendContextMenu("Follow", 5);

			p.frames.setConfig(166, p.brightness);
			p.frames.setConfig(170, p.singleMouseButton ? 1 : 0);
			p.frames.setConfig(171, p.chatEffects ? 0 : 1);
			p.frames.setConfig(287, p.splitPrivateChat ? 1 : 0);
			p.frames.setConfig(427, p.acceptAid ? 1 : 0);
			p.frames.setConfig(168, 4 - p.volumeMusic);
			p.frames.setConfig(169, 4 - p.volumeEffects);
			p.frames.setPrivacyOptions(p.chatPublic, p.chatPrivate, p.chatTrade);

			p.frames.setConfig(172, p.autoRetaliate ? 0 : 1);
			p.frames.setConfig(173, p.running ? 1 : 0);

			p.frames.setConfig(304, 0);		// Rearrange mode
			p.frames.setConfig(115, 0);		// Withdraw mode

			for (int i = 83; i < 101; i++) {
				p.frames.setConfig(i, 0);
			}
			p.frames.sendQuestPoints();

			for (int i = 0; i < p.skillLevel.length; i++) {
				p.frames.sendSkillLevel(i);
			}
			p.frames.setItems(3214, p.inventory);
			p.frames.setItems(1688, p.equipment);
			p.equipment.sendStances();
			p.equipment.sendWeapon();
			p.calculateBonuses();
			p.frames.sendBonuses();
			p.frames.sendWeight();

			p.frames.sendEnergy();

			if (Engine.updating) {
				p.frames.sendSystemUpdate(Engine.updateTime);
			}
			p.frames.setFriendsListStatus(2);

			for (Long friend : p.friends) {
				p.frames.sendFriend(friend, p.getWorld(friend));
			}
			p.frames.sendIgnores(p.ignores);
			p.updateFriends();

			p.appearanceUpdateReq = true;
			p.updateReq = true;
			p.online = true;
			Engine.playerUpdate.update(p);
			Engine.playerMovement.resetWalkingQueue(p);
		}
	}

	private void buildWelcomeScreen(Player p) {

		if (p.lastLogin != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			try {
				date = formatter.parse(formatter.format(date));
			} catch (ParseException e) {
			}
			long dateDiff = date.getTime() - p.lastLogin.getTime();

			if (date.equals(p.lastLogin)) {
				p.frames.setString("You last logged today from " + p.lastIP, 15258);
			} else {
				p.frames.setString(
					"You last logged in " + TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS)
						+ " days ago from " + p.lastIP, 15258);
			}
		} else {
			p.frames.setString("This is your first time playing!", 15258);
		}

		Calendar today = Calendar.getInstance();

		if (today.get(Calendar.MONTH) == 11) {
			p.frames.sendInterface(15819);
		} else {
			int[] interfaces = { 17511, 15812, 15801, 15791, 15774, 15767 };
			p.frames.sendInterface(interfaces[new Random().nextInt(interfaces.length)]);
		}
	}

	private boolean playerOnline(Player p, String name) {
		for (Player p2 : Engine.players) {
			if (p2 != null && p2.online && p.playerId != p2.playerId) {
				if (p2.playerName.equalsIgnoreCase(name)) {
					return true;
				}
			}
		}
		return false;
	}

	private void fillStream(Player p, int read) throws Exception {
		if (p == null) {
			return;
		}
		p.stream.inOffset = 0;
		p.socket.input.read(p.stream.inBuffer, 0, read);
	}

	private void directFlushStream(Player p) {
		if (p == null) {
			return;
		}
		try {
			p.socket.output.write(p.stream.outBuffer, 0, p.stream.outOffset);
			p.stream.outOffset = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
