package org.rs2.io;

import java.util.ArrayList;
import org.rs2.model.content.ClothingInterface.Torsos;
import org.rs2.model.content.HaircutInterface.Beards;
import org.rs2.model.items.Item;
import org.rs2.model.players.Player;
import org.rs2.model.players.containers.Container;
import org.rs2.util.Misc;
import org.rs2.world.Shop;

public class Frames {
	/**
	 * Player the Frames class belongs to.
	 */
	private Player p;

	/**
	 * Construct a new Frames.
	 * @param p Player to assign Frames to.
	 */
	public Frames(Player p) {
		this.p = p;
	}

	/**
	 * Resets all animations in the region.
	 */
	public void resetLocalAnimations() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(1);
	}

	/**
	 * Send a message to the chatbox.
	 * @param s Text to send to player.
	 */
	public void sendMessage(String s) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSize(253);
		p.stream.writeString(s);
		p.stream.endFrameVarSize();
	}
	
	public void sendMessage(int i) {
		sendMessage(Integer.toString(i));
	}

	/**
	 * Set a string on an interface.
	 * @param s String we're placing.
	 * @param id Id of the string we're setting.
	 */
	public void setString(String s, int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSizeWord(126);
		p.stream.writeString(s);
		p.stream.writeWordA(id);
		p.stream.endFrameVarSizeWord();
	}
	
	public void setString(int i, int id) {
		setString(Integer.toString(i), id);
	}
	
	public void resetString(int id) {
		setString("", id);
	}

	public void setStringColor(int id, int color) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(122);
		p.stream.writeWordBigEndianA(id);
		p.stream.writeWordBigEndianA(color);
	}

	/**
	 * Set the scrollbar position on an interface.
	 * @param id interface to set scrollbar position for.
	 * @param position Position in pixels.
	 */
	public void setScrollbarPosition(int id, int position) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(79);
		p.stream.writeWordBigEndian(id);
		p.stream.writeWordA(position);
	}

	/**
	 * Sets the time until system update. When timer reaches 0, all players will
	 * be disconnected and will not be able to log in until server has been
	 * restarted.
	 * @param secs Seconds until system update.
	 */
	public void sendSystemUpdate(int secs) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(114);
		p.stream.writeWordBigEndian(secs * 50 / 30);
	}

	/**
	 * Change an interface config (ie; brightness, combat style).
	 * @param id Id of the config to change.
	 * @param value Value to change to.
	 */
	public void setConfig(int id, int value) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(36);
		p.stream.writeWordBigEndian(id);
		p.stream.writeByte(value);
	}

	/**
	 * TODO: figure out the point of this. it seems to be the same thing as
	 * packet 36.
	 */
	public void sendFrame87(int i, int k) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(87);
		p.stream.writeWordBigEndian(i);
		p.stream.writeDWord_v1(k);
	}

	/**
	 * Set's the status of the friends list.
	 * @param status Status, loading: 0, connecting: 1, connected: 2
	 */
	public void setFriendsListStatus(int status) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(221);
		p.stream.writeByte(status);
	}

	/**
	 * Set the world for a specific friend. 0 = offline.
	 */
	public void sendFriend(long friend, int world) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		if (world != 0) {
			world += 9;
		}
		p.stream.createFrame(50);
		p.stream.writeQWord(friend);
		p.stream.writeByte(world);
	}

	/**
	 * Sends ignores to the ignore interface.
	 * @param ignores
	 */
	public void sendIgnores(ArrayList<Long> ignores) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSizeWord(214);
		for (Long ignore : ignores) {
			p.stream.writeQWord(ignore);
		}
		p.stream.endFrameVarSizeWord();
	}

	/**
	 * Log the player out.
	 */
	public void logout() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(109);
	}

	/**
	 * Opens interface.
	 * @param id Interface ID to open.
	 */
	public void sendInterface(int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(97);
		p.stream.writeWord(id);
		p.interfaceId = id;
	}

	/**
	 * Sends the name interface.
	 */
	public void sendNameInterface() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(187);
	}

	/**
	 * TODO: Move interface methods.
	 */

	/**
	 * Shows the torso interface.
	 */
	public void sendTorsoInterface() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.newTorso = p.looks[2];
		p.newArms = p.looks[3];
		p.newColor = p.colors[1];

		setConfig(263, p.colors[1] + 1);
		if (p.gender == 0) {
			int torsoConfig = 1;
			for (Torsos t : Torsos.values()) {
				if (t.getLook() == p.looks[2]) {
					torsoConfig = t.getConfig();
					break;
				}
			}
			setConfig(261, torsoConfig);
			setConfig(262, p.looks[3] - 26 + 1);
			sendInterfaceOverlay(2851, 5063);
		} else {
			setConfig(261, p.looks[2] - 56 + 1);
			setConfig(262, p.looks[3] - 61 + 1);
			sendInterfaceOverlay(3038, 5063);
		}
	}

	/**
	 * Shows the legs interface.
	 */
	public void sendLegsInterface() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.newLegs = p.looks[5];
		p.newColor = p.colors[2];

		setConfig(263, p.colors[2] + 1);
		if (p.gender == 0) {
			setConfig(261, p.looks[5] - 36 + 1);
			sendInterfaceOverlay(0, 5063);
		} else {
			setConfig(261, p.looks[5] - 70 + 1);
			sendInterfaceOverlay(4731, 5063);
		}
	}

	/**
	 * Shows the haircut interface.
	 */
	public void sendHaircutInterface() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.newHair = p.looks[0];
		p.newColor = p.colors[0];

		setConfig(262, p.colors[0] + 1);
		if (p.gender == 0) {
			setConfig(261, p.looks[0] + 1);
			sendInterfaceOverlay(2653, 5063);
		} else {
			setConfig(261, p.looks[0] - 44);
			sendInterfaceOverlay(2505, 5063);
		}
	}

	/**
	 * Shows the shaving interface.
	 */
	public void sendShavingInterface() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		if (p.gender > 0) {
			sendMessage("You are not a man!");
			return;
		}
		p.newBeard = p.looks[1];
		p.newColor = p.colors[0];

		int beardConfig = 1;
		for (Beards b : Beards.values()) {
			if (b.getBeard() == p.looks[1]) {
				beardConfig = b.getConfig();
				break;
			}
		}
		setConfig(262, p.colors[0] + 1);
		setConfig(261, beardConfig);
		sendInterfaceOverlay(2007, 5063);
	}

	/**
	 * Show gender/skin interface.
	 */
	public void sendGenderInterface() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.newColor = p.colors[4];
		p.newGender = p.gender;

		setConfig(262, p.colors[4] + 1);
		setConfig(261, p.gender + 1);
		sendInterfaceOverlay(5454, 5063);
	}

	/**
	 * Opens a walkable interface
	 * @param id Interface ID to open.
	 */
	public void sendWalkableInterface(int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(208);
		p.stream.writeWordBigEndian_dup(id);
	}

	/**
	 * TODO: move all bank methods into the banking class.
	 */

	/**
	 * Opens the bank interface.
	 */
	public void sendBank() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		/*p.bankPin.applyNewPIN();

		if (p.bankPin.lockedOut()) {
			return;
		}
		if (!p.bankPin.setPin.isEmpty() && !p.bankPin.entered) {
			p.bankPin.open();
			return;
		}*/
		//setItems(5064, p.items, p.itemsN);
		//setItems(5382, p.bankItems, p.bankItemsN);
		p.bank.shift();
		//setItems(5382, p.bank.getItems());
		//setItems(5064, p.inventory.getItems());

		setItems(5382, p.bank);
		setItems(5064, p.inventory);
		sendInterfaceOverlay(5292, 5063);
	}

	/**
	 * Sends the bank PIN interface.
	 */
	/*public void sendPin() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.bankPin.numbers.clear();

		for (int i = 0; i < 10; i++) {
			p.bankPin.numbers.add(i);
		}
		Collections.shuffle(p.bankPin.numbers);

		String[] instructions = { "First click the FIRST digit", "Then click the SECOND digit",
			"Then click the THIRD digit", "And lastly click the FOURTH digit" };

		setString(instructions[p.bankPin.enteredPin.size()], 15313);

		if (p.bankPin.action == Actions.SETUP_STAGE_1 || p.bankPin.action == Actions.CHANGE_STAGE_1) {
			setString("Set new PIN", 14923);
			setString("Please choose a new FOUR DIGIT PIN using the buttons below", 14920);

			moveChild(14922, 0, 10);
			moveChild(14921, 0, 500);

		} else if (p.bankPin.action == Actions.SETUP_STAGE_2
			|| p.bankPin.action == Actions.CHANGE_STAGE_2) {
			setString("Confirm new PIN", 14923);
			setString("Now please enter that number again.", 14920);

		} else if (p.bankPin.action == Actions.DELETE_STAGE_1) {
			setString("Bank of RuneScape", 14923);
			setString("Please enter your FOUR DIGIT PIN using the buttons below.", 14920);
			moveChild(14922, 0, 0);
			moveChild(14921, 0, 0);

		} else {
			if (p.bankPin.deleteRequested() || p.bankPin.state == States.CHANGED
				|| p.bankPin.state == States.UNKNOWN) {
				setString("YOUR PIN WILL BE DELETED IN " + p.bankPin.getDaysLeft() + " DAYS", 14923);
			} else {
				setString("Bank of RuneScape", 14923);
			}
			setString("Please enter your FOUR DIGIT PIN using the buttons below.", 14920);

			if (p.bankPin.state == States.SET || p.bankPin.state == States.CHANGED) {
				moveChild(14922, 0, 0);
				moveChild(14921, 0, 0);
			} else {
				moveChild(14922, 0, 10);
				moveChild(14921, 0, 500);
			}
		}
		for (int i = 0; i < 4; i++) {
			setString(p.bankPin.enteredPin.size() > i ? "*" : "?", 14913 + i);
		}
		for (int i = 0; i < p.bankPin.numbers.size(); i++) {
			if (p.bankPin.enteredPin.size() < 4) {
				setString("" + p.bankPin.numbers.get(i), 14883 + i);
				moveChild(14883 + i, Misc.random(46), -Misc.random(42));
			} else {
				setString("", 14883 + i);
			}
		}
		sendInterface(7424);
	}*/

	/**
	 * Sends the bank PIN settings interface.
	 */
	/*public void sendPinSettings() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.bankPin.applyNewPIN();

		if (p.bankPin.lockedOut()) {
			return;
		}
		hideChild(15108, 1);
		moveChild(15171, 0, 500);
		moveChild(15176, 0, 500);

		if (p.bankPin.state == States.CANCELLED) {
			if (p.bankPin.setPin.isEmpty()) {
				setString("The PIN has been", 15038);
				setString("cancelled and will NOT be", 15039);
				setString("set.", 15040);
				setString("", 15041);
				setString("You still do not have a", 15042);
				setString("Bank PIN.", 15043);
				setString("", 15044);
				setString("", 15045);
				setString("", 15046);
				setString("", 15047);
				setString("", 15048);
				setString("", 15049);
				p.bankPin.setState(States.NOT_SET);
			} else {
				setString("The PIN has been", 15038);
				setString("cancelled and will NOT be", 15039);
				setString("set.", 15040);
				setString("", 15041);
				setString("Your PIN will remain", 15042);
				setString("unchanged.", 15043);
				setString("", 15044);
				setString("", 15045);
				setString("", 15046);
				setString("", 15047);
				setString("", 15048);
				setString("", 15049);
				p.bankPin.setState(States.SET);
			}

		} else if (p.bankPin.state == States.SETUP) {
			setString("You have requested that", 15038);
			setString("a PIN be set on your", 15039);
			setString("bank account. This will", 15040);
			setString("Take effect in " + p.bankPin.getDaysLeft() + " days.", 15041);
			setString("", 15042);
			setString("If you wish to cancel", 15043);
			setString("this PIN, please use the", 15044);
			setString("button on the left.", 15045);
			setString("", 15046);
			setString("", 15047);
			setString("", 15048);
			setString("", 15049);

		} else if (p.bankPin.state == States.CHANGED) {
			setString("You have requested a", 15038);
			setString("new PIN be set on your", 15039);
			setString("bank account. This will", 15040);
			setString("Take effect in " + p.bankPin.getDaysLeft() + " days.", 15041);
			setString("", 15042);
			setString("If you did not request", 15043);
			setString("this, your account may", 15044);
			setString("be compromised and you", 15045);
			setString("should change your", 15046);
			setString("password immediately!", 15047);
			setString("", 15048);
			setString("", 15049);

		} else if (p.bankPin.state == States.DELETED) {
			setString("Your Bank PIN has now", 15038);
			setString("been deleted.", 15039);
			setString("", 15040);
			setString("This means that there is", 15041);
			setString("no PIN protection on", 15042);
			setString("your bank account.", 15043);
			setString("", 15044);
			setString("", 15045);
			setString("", 15046);
			setString("", 15047);
			setString("", 15048);
			setString("", 15049);
			p.bankPin.setState(States.NOT_SET);

		} else {
			if (p.bankPin.deleteRequested()) {
				setString("You have requested that", 15038);
				setString("your PIN be removed", 15039);
				setString("from your account. This", 15040);
				setString("will take effect in " + p.bankPin.getDaysLeft(), 15041);
				setString("days.", 15042);
				setString("", 15043);
				setString("If you wish to cancel", 15044);
				setString("this PIN, please use the", 15045);
				setString("button on the left.", 15046);
				setString("", 15047);
				setString("", 15048);
				setString("", 15049);
			} else {
				setString("Customers are reminded", 15038);
				setString("That they should NEVER", 15039);
				setString("Tell anyone their bank", 15040);
				setString("PINs or passwords, nor", 15041);
				setString("should they ever enter", 15042);
				setString("their PINs on any website", 15043);
				setString("form.", 15044);
				setString("", 15045);
				setString("Have you read the PIN", 15046);
				setString("Frequently Asked", 15047);
				setString("Questions on the", 15048);
				setString("website?", 15049);
			}
		}
		setPinSettingsButtons();
		setString(p.bankPin.recoveryDelay + " days", 15107);
		sendInterface(14924);
	}*/

	/**
	 * Sets the visible buttons on the bank PIN interface.
	 */
	/*public void setPinSettingsButtons() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		int[] options = { 15075, 15076, 15078, 15079, 15080, 15082 };

		if (p.bankPin.state == States.NOT_SET) {
			setString("No PIN set", 15105);

			moveChild(options[2], 0, 100);
			moveChild(options[3], 0, 100);
			moveChild(options[4], 0, 100);
			moveChild(options[5], 0, 100);
			moveChild(options[0], 0, 0);
			moveChild(options[1], 0, 0);

		} else if (p.bankPin.state == States.SET || p.bankPin.state == States.DELETED) {
			setString("You have a PIN", 15105);

			moveChild(options[0], 0, 100);
			moveChild(options[1], 0, 100);
			moveChild(options[5], 0, 100);
			moveChild(options[2], 0, 0);
			moveChild(options[3], 0, 0);
			moveChild(options[4], 0, 0);

		} else if (p.bankPin.state == States.UNKNOWN) {
			setString("You have a PIN", 15105);

			moveChild(options[0], 0, 100);
			moveChild(options[1], 0, 100);
			moveChild(options[2], 0, 100);
			moveChild(options[3], 0, 100);
			moveChild(options[4], 0, 100);
			moveChild(options[5], 0, 0);

		} else if (p.bankPin.state == States.SETUP || p.bankPin.state == States.CHANGED) {
			setString("PIN coming soon", 15105);

			moveChild(options[0], 0, 100);
			moveChild(options[1], 0, 100);
			moveChild(options[2], 0, 100);
			moveChild(options[3], 0, 100);
			moveChild(options[4], 0, 100);
			moveChild(options[5], 0, 0);
		}
	}*/

	/**
	 * Shows a prompt within the bank PIN settings interface.
	 * @param question Question to display.
	 * @param yes Yes answer to question.
	 * @param no No answer to question.
	 */
	/*public void showPinPrompt(String question, String yes, String no) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		int[] options = { 15075, 15076, 15078, 15079, 15080, 15082 };

		for (int i = 0; i < options.length; i++) {
			moveChild(options[i], 0, 100);
		}
		setString(question, 15110);
		setString(yes, 15171);
		setString(no, 15176);

		hideChild(15108, 0);
		moveChild(15171, 0, 0);
		moveChild(15176, 0, 0);
	}*/

	/**
	 * Hides the bank PIN prompt.
	 */
	/*public void hidePinPrompt() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		setPinSettingsButtons();
		hideChild(15108, 1);
		moveChild(15171, 0, 500);
		moveChild(15176, 0, 500);
	}*/

	/**
	 * Opens the deposit box interface.
	 */
	public void sendDepositBox() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		sendInterfaceOverlay(4465, 197);
		setItems(7423, p.inventory);
		//setItems(7423, p.items, p.itemsN);
	}

	/**
	 * Sends the shop interface.
	 * @param shopId Id of shop to send.
	 */
	public void sendShop(int shopId) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		if (Shop.shopDef[shopId] == null) {
			return;
		}
		p.shopId = shopId;
		setString(Shop.shopDef[shopId].getName(), 3901);
		sendInterfaceOverlay(3824, 3822);
		//setItems(3823, p.items, p.itemsN);
		//setItems(3900, Shop.shopDef[shopId].items, Shop.shopDef[shopId].itemsN);
	}

	/**
	 * Opens and interface and overlays an interface over the inventory (such as
	 * in trading and banking).
	 * @param id Interface to open.
	 * @param overlayId Interface to overlay the inventory.
	 */
	public void sendInterfaceOverlay(int id, int overlayId) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(248);
		p.stream.writeWordA(id);
		p.stream.writeWord(overlayId);
		p.interfaceId = id;
		p.interfaceChildId = overlayId;
	}

	/**
	 * Closes currently opened interface for a player.
	 */
	public void closeAllInterfaces() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(219);
		p.resetInterfaces();
	}

	/**
	 * Opens chatbox interface.
	 * @param id Interface ID.
	 */
	public void sendChatInterface(int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(164);
		p.stream.writeWordBigEndian_dup(id);
		p.interfaceId = id;
	}

	/**
	 * Sends an inventory interface.
	 * @param id Id of interface to show.
	 */
	public void sendInventoryInterface(int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(142);
		p.stream.writeWordBigEndian(id);
		p.interfaceId = id;
	}

	/**
	 * Sends the multiple options interface.
	 * @param s Option text.
	 */
	public void sendChatOptions(String... s) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		if (s.length < 2 || s.length > 5) {
			return;
		}
		if (s.length == 2) {
			setString(s[0], 2461);
			setString(s[1], 2462);
			sendChatInterface(2459);
		} else if (s.length == 3) {
			setString(s[0], 2471);
			setString(s[1], 2472);
			setString(s[2], 2473);
			sendChatInterface(2469);
		} else if (s.length == 4) {
			setString(s[0], 2482);
			setString(s[1], 2483);
			setString(s[2], 2484);
			setString(s[3], 2485);
			sendChatInterface(2480);
		} else if (s.length == 5) {
			setString(s[0], 2494);
			setString(s[1], 2495);
			setString(s[2], 2496);
			setString(s[3], 2497);
			setString(s[4], 2498);
			sendChatInterface(2492);
		}
	}

	/**
	 * Sends the statement interface.
	 * @param s Lines of text to display.
	 */
	public void sendChatStatement(String... s) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		if (s.length < 1 || s.length > 4) {
			return;
		}
		if (s.length == 1) {
			setString(s[0], 357);
			sendChatInterface(356);
		} else if (s.length == 2) {
			setString(s[0], 360);
			setString(s[1], 361);
			sendChatInterface(359);
		} else if (s.length == 3) {
			setString(s[0], 364);
			setString(s[1], 365);
			setString(s[2], 366);
			sendChatInterface(363);
		} else if (s.length == 4) {
			setString(s[0], 369);
			setString(s[1], 370);
			setString(s[2], 371);
			setString(s[3], 372);
			sendChatInterface(368);
		}
	}

	/**
	 * Sends a timed chat statement.
	 * @param secs Seconds for statement to stay open
	 * @param s Lines of text to display.
	 */
	/*public void sendTimedChatStatement(int secs, String... s) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		if (s.length < 1 || s.length > 4) {
			return;
		}
		if (s.length == 1) {
			setString(s[0], 12789);
			sendChatInterface(12788);
		} else if (s.length == 2) {
			setString(s[0], 12791);
			setString(s[1], 12792);
			sendChatInterface(12790);
		} else if (s.length == 3) {
			setString(s[0], 12794);
			setString(s[1], 12795);
			setString(s[2], 12796);
			sendChatInterface(12793);
		} else if (s.length == 4) {
			setString(s[0], 12798);
			setString(s[1], 12799);
			setString(s[2], 12800);
			setString(s[3], 12801);
			sendChatInterface(12797);
		} else if (s.length == 4) {
			setString(s[0], 12803);
			setString(s[1], 12804);
			setString(s[2], 12805);
			setString(s[3], 12806);
			setString(s[4], 12807);
			sendChatInterface(12802);
		}
	}*/

	/**
	 * Add a context menu option for example "Trade-with."
	 * @param s String to add to the context menu.
	 * @param slot Slot position of the option.
	 */
	public void sendContextMenu(String s, int slot) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSize(104);
		p.stream.writeByteC(slot);
		p.stream.writeByteA(0);
		p.stream.writeString(s);
		p.stream.endFrameVarSize();
	}

	/**
	 * Sets a new tab to a designated spot on the interface.
	 * @param tab Which tab to set.
	 * @param id Id of the interface we're placing.
	 */
	public void setTab(int tab, int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(71);
		p.stream.writeWord(id);
		p.stream.writeByteA(tab);
	}

	/**
	 * Makes the specified tab flash.
	 * @param id Id of tab to flash.
	 */
	public void flashTab(int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(24);
		p.stream.writeByteS(id);
	}

	/**
	 * Switches to the specified tab.
	 * @param id Id of tab to switch to.
	 */
	public void showTab(int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(106);
		p.stream.writeByteC(id);
	}

	/**
	 * Set the chat/trade options.
	 * @param pub Public chat setting.
	 * @param priv Private chat setting.
	 * @param trade Trade request setting.
	 */
	public void setPrivacyOptions(int pub, int priv, int trade) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(206);
		p.stream.writeByte(pub);
		p.stream.writeByte(priv);
		p.stream.writeByte(trade);
	}

	/**
	 * Hide or show an interface child.
	 * @param id Child Id.
	 * @param hide Set to 1 to hide.
	 */
	public void hideChild(int id, int hide) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(171);
		p.stream.writeByte(hide);
		p.stream.writeWord(id);
	}

	/**
	 * Offsets an interface child.
	 * @param id Id of child to offset.
	 * @param offsetX X axis offset.
	 * @param offsetY Y axis offset.
	 */
	public void moveChild(int id, int offsetX, int offsetY) {
		p.stream.createFrame(70);
		p.stream.writeWord(offsetX);
		p.stream.writeWordBigEndian(offsetY);
		p.stream.writeWordBigEndian(id);
	}

	// @formatter:off
	/**
	 * Sets the minimap state.
	 * @param state State to set minimap to.
	 *  0 - Normal: Viewable and clickable.
	 *  1 - Locked: Viewable and locked.
	 *  2 - Black: Black out and locked.
	 */
	// @formatter:on
	public void setMinimapState(int state) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(99);
		p.stream.writeByte(state);
	}

	/**
	 * Resets the camera's position.
	 */
	public void resetCamera() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(107);
	}

	/**
	 * Plays GFX at the specified coordinates.
	 * @param id Id of GFX.
	 * @param delay Delay before GFX.
	 * @param x GFX X Coordinate.
	 * @param y GFX Y Coordinate.
	 */
	public void sendGfx(int id, int delay, int x, int y) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		sendCoordinates(x, y);
		p.stream.createFrame(4);
		p.stream.writeByte(0);
		p.stream.writeWord(id);
		p.stream.writeByte(0);
		p.stream.writeWord(delay);
	}

	/**
	 * Plays GFX at specified height and coordinates.
	 * @param id Id of GFX.
	 * @param delay Delay before GFX.
	 * @param x GFX X Coordinate.
	 * @param y GFX Y Coordinate.
	 * @param height Height of GFX.
	 */
	public void sendGfx(int id, int delay, int x, int y, int height) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		sendCoordinates(x, y);
		p.stream.createFrame(4);
		p.stream.writeByte(0);
		p.stream.writeWord(id);
		p.stream.writeByte(height);
		p.stream.writeWord(delay);
	}

	/**
	 * Play sound only for one player.
	 * @param sound Sound Id
	 * @param delay Sound delay
	 * @param volume Sound volume
	 */
	public void sendSound(int sound, int delay, int volume) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(174);
		p.stream.writeWord(sound);
		p.stream.writeWord(delay);
		p.stream.writeWord(volume);
	}

	/**
	 * Sets the next song to be played and plays it.
	 * @param id
	 */
	public void sendSong(int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(74);
		p.stream.writeWordBigEndian(id);
	}

	public void sendQuickSong(int id, int delay) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(121);
		p.stream.writeWordBigEndianA(id);
		p.stream.writeWordA(delay);
	}

	/**
	 * Place new world object.
	 * @param id Id of new object.
	 * @param x X coordinate of object.
	 * @param y Y coordinate of object.
	 * @param height Height level of the object.
	 * @param rot Rotation of object.
	 * @param type Type of object.
	 */
	public void addObject(int id, int x, int y, int height, int rot, int type) {
		sendCoordinates(x, y);
		p.stream.createFrame(151);
		p.stream.writeByteA(height);
		p.stream.writeWordBigEndian(id);
		p.stream.writeByteS((type << 2) + (rot & 3));

		// Region.addObject(id, x, y, height, type, rot);
	}

	/**
	 * Replace world objects.
	 * @param id Id of new object.
	 * @param x X coordinate of object.
	 * @param y Y coordinate of object.
	 * @param height Height level of object.
	 * @param rot Rotation of new object.
	 * @param type Type of new object.
	 */
	public void replaceObject(int id, int x, int y, int height, int rot, int type) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		sendCoordinates(x, y);
		p.stream.createFrame(101);
		p.stream.writeByteC((type << 2) + (rot & 3));
		p.stream.writeByte(height);

		if (id != -1) {
			p.stream.createFrame(151);
			p.stream.writeByteS(height);
			p.stream.writeWordBigEndian(id);
			p.stream.writeByteS((type << 2) + (rot & 3));
			// Region.addObject(id, x, y, height, type, rot);
		} else {
			/**
			 * FIXME: Remove region object clipping
			 */
			// Region.removeObject(id, x, y, height, type, rot);
		}
	}

	/**
	 * Sends the coordinates for an object, item, or graphic.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 */
	public void sendCoordinates(int x, int y) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(85);
		p.stream.writeByteC(y - (p.mapRegionY * 8));
		p.stream.writeByteC(x - (p.mapRegionX * 8));
	}

	/**
	 * Set map region.
	 */
	public void setMapRegion() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(73);
		p.stream.writeWordA(p.mapRegionX + 6);
		p.stream.writeWord(p.mapRegionY + 6);
	}

	/**
	 * Teleport.
	 */
	public void teleport() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSizeWord(81);
		p.stream.initBitAccess();
		p.stream.writeBits(1, 1);
		p.stream.writeBits(2, 3);
		p.stream.writeBits(2, p.heightLevel);
		p.stream.writeBits(1, 1);
		p.stream.writeBits(1, p.updateReq ? 1 : 0);
		p.stream.writeBits(7, p.currentY);
		p.stream.writeBits(7, p.currentX);
	}

	/**
	 * Sent when player isn't moving.
	 */
	public void noMovement() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSizeWord(81);
		p.stream.initBitAccess();

		if (p.updateReq) {
			p.stream.writeBits(1, 1);
			p.stream.writeBits(2, 0);
		} else {
			p.stream.writeBits(1, 0);
		}
	}

	/**
	 * Update player movement
	 */
	public void updateMovement() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSizeWord(81);
		p.stream.initBitAccess();
		p.stream.writeBits(1, 1);

		if (p.runDir == -1) {
			p.stream.writeBits(2, 1);
			p.stream.writeBits(3, Misc.xlateDirectionToClient[p.walkDir]);
			p.stream.writeBits(1, p.updateReq ? 1 : 0);
		} else {
			p.stream.writeBits(2, 2);
			p.stream.writeBits(3, Misc.xlateDirectionToClient[p.walkDir]);
			p.stream.writeBits(3, Misc.xlateDirectionToClient[p.runDir]);
			p.stream.writeBits(1, p.updateReq ? 1 : 0);
			
			if (p.runEnergy > 0) {
				p.runEnergy -= (0.67 + (p.getTotalWeight() / 100));
				p.runEnergyUpdateReq = true;
			}
			p.runEnergyRegen = System.currentTimeMillis();
		}
	}

	/**
	 * Adds an item to the ground.
	 * @param p Player to show the item for.
	 * @param guideId Id of item.
	 * @param amount Amount of item.
	 * @param x X-coordinate of item.
	 * @param y Y-coordinate of item.
	 */
	/*public void addGroundItem(int id, int amount, int x, int y) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		sendCoordinates(x, y);
		p.stream.createFrame(44);
		p.stream.writeWordBigEndianA(id);
		p.stream.writeWord(amount);
		p.stream.writeByte(0);
	}*/
	public void addGroundItem(Item item, int x, int y) {
		if (p.stream == null || p.disconnected || item == null) {
			return;
		}
		sendCoordinates(x, y);
		p.stream.createFrame(44);
		p.stream.writeWordBigEndianA(item.getId());
		p.stream.writeWord(item.getAmt());
		p.stream.writeByte(0);
	}

	/**
	 * Removes an item from the ground.
	 * @param p Player to remove item for.
	 * @param id Id of item.
	 * @param x X-coordinate of item.
	 * @param y Y-coordinate of item.
	 */
	public void removeGroundItem(int id, int x, int y) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		sendCoordinates(x, y);
		p.stream.createFrame(156);
		p.stream.writeByteS(0);
		p.stream.writeWord(id);
	}

	/**
	 * Removes destination flag from world map.
	 */
	public void resetDestination() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(78);
	}

	/**
	 * Set items on an interface.
	 * @param id Interface to set items to.
	 * @param items Items to add.
	 * @param amount Amount of items to add.
	 */
	public void setItems(int id, Container c) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSizeWord(53);
		p.stream.writeWord(id);
		p.stream.writeWord(c.getItems().length);

		for (int i = 0; i < c.getItems().length; i++) {
			if (c.getAmt(i) > 254) {
				p.stream.writeByte(255);
				p.stream.writeDWord_v2(c.getAmt(i));
			} else {
				p.stream.writeByte(c.getAmt(i));
			}
			p.stream.writeWordBigEndianA(c.getId(i) + 1);
		}
		p.stream.endFrameVarSizeWord();
	}
	
	
	public void setItems(int id, Item[] items) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSizeWord(53);
		p.stream.writeWord(id);
		p.stream.writeWord(items.length);

		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				p.stream.writeByte(0);
				p.stream.writeWordBigEndianA(0);
				continue;
			}
			if (items[i].getAmt() > 254) {
				p.stream.writeByte(255);
				p.stream.writeDWord_v2(items[i].getAmt());
			} else {
				p.stream.writeByte(items[i].getAmt());
			}
			p.stream.writeWordBigEndianA(items[i].getId() + 1);
		}
		p.stream.endFrameVarSizeWord();
	}

	/**
	 * Send an item to an interface.
	 * @param id Interface to update.
	 * @param slot Slot to update.
	 * @param c Container to get item from.
	 */
	public void setItem(int id, int slot, Container c) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSizeWord(34);
		p.stream.writeWord(id);
		p.stream.writeSmarts(slot);
		p.stream.writeWord(c.getId(slot) + 1);

		int amount = c.getAmt(slot);
		if (amount > 254) {
			p.stream.writeByte(255);
			p.stream.writeDWord(amount);
		} else {
			p.stream.writeByte(amount);
		}
		p.stream.endFrameVarSizeWord();
	}

	/**
	 * Send an item to an interface.
	 * @param id Interface to send item to.
	 * @param item Item to send.
	 * @param slot Slot to send item to.
	 */
	public void setItem(int id, Item item, int slot) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrameVarSizeWord(34);
		p.stream.writeWord(id);
		p.stream.writeSmarts(slot);
		if (item != null) {
			p.stream.writeWord(item.getId() + 1);

			if (item.getAmt() > 254) {
				p.stream.writeByte(255);
				p.stream.writeDWord(item.getAmt());
			} else {
				p.stream.writeByte(item.getAmt());
			}
		} else {
			p.stream.writeWord(0);
			p.stream.writeByte(0);
		}
		p.stream.endFrameVarSizeWord();
	}

	/**
	 * Send an item model to an interface.
	 * @param id Interface to send the item to.
	 * @param modelZoom Item's model zoom.
	 * @param item Item Id to send to interface.
	 */
	public void sendInterfaceItemModel(int id, int modelZoom, int item) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(246);
		p.stream.writeWordBigEndian(id);
		p.stream.writeWord(modelZoom);
		p.stream.writeWord(item);
	}

	/**
	 * Removes all items from an interface.
	 * @param id
	 */
	public void resetItems(int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(72);
		p.stream.writeWordBigEndian(id);
	}

	/**
	 * Sends skill level to the skill interface.
	 * @param id Id of skill to update on the interface.
	 */
	public void sendSkillLevel(int id) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(134);
		p.stream.writeByte(id);
		p.stream.writeDWord_v1((int) p.skillXP[id]);
		p.stream.writeByte(p.skillLevel[id]);
		p.appearanceUpdateReq = true;
		p.updateReq = true;
	}

	/**
	 * Set an icon to show on the bottom right of the HUD.
	 * @param icon Id of icon to show.
	 */
	public void sendHudIcon(int icon) {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(61);
		p.stream.writeByte(icon);
	}

	/**
	 * Writes all equipment bonuses to the interface.
	 */
	public void sendBonuses() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		int offset = 0;
		String bonusName[] = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash",
			"Crush", "Magic", "Range", "Strength", "Prayer" };

		for (int i = 0; i < p.equipmentBonus.length; i++) {
			if (i == 10) {
				offset = 1;
			}
			setString(bonusName[i] + (p.equipmentBonus[i] >= 0 ? ": +" : ": ")
				+ p.equipmentBonus[i], (1675 + i + offset));
		}
	}

	/**
	 * Sends player energy to the client.
	 */
	public void sendEnergy() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(110);
		p.stream.writeByte((int) Math.ceil(p.runEnergy));
	}

	/**
	 * Sends quest points to the interface.
	 */
	public void sendQuestPoints() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		setString("QP: " + p.questPoints, 3985);
	}

	/**
	 * Sends player weight to the client.
	 */
	public void sendWeight() {
		if (p.stream == null || p.disconnected) {
			return;
		}
		p.stream.createFrame(240);
		p.stream.writeWord((int) Math.ceil(p.getTotalWeight()));
	}

}
