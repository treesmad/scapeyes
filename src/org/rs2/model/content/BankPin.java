package org.rs2.model.content;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.rs2.model.players.Player;
import org.rs2.util.Misc;

/**
 * Bank PIN system.
 */
public class BankPin {
	/**
	 * Player this class belongs to.
	 */
	private Player p;

	/**
	 * Current bank PIN.
	 */
	public ArrayList<Integer> currentPin = new ArrayList<Integer>(4);
	/**
	 * Pending bank PIN.
	 */
	public ArrayList<Integer> pendingPin = new ArrayList<Integer>(4);
	/**
	 * Number of days before the pending PIN will take effect.
	 */
	public int pendingDelay;
	/**
	 * Date the pending PIN takes effect.
	 */
	public Date pendingDate;
	/**
	 * True if the PIN has been correctly entered this login session.
	 */
	public boolean entered;
	/**
	 * Number of failed PIN attempts. Times out after 15 minutes.
	 */
	public int attempts;
	/**
	 * Last failed bank PIN attempt.
	 */
	public long lastAttempt;

	/**
	 * PIN that has been entered on the numpad interface.
	 */
	private ArrayList<Integer> enteredPin = new ArrayList<Integer>(4);
	private ArrayList<Integer> confirmedPin = new ArrayList<Integer>(4);
	/**
	 * List of numbers available on the bank PIN interface.
	 */
	private ArrayList<Integer> numbers = new ArrayList<Integer>(10);
	/**
	 * Upon reaching this number of attempts, the player must wait 20 minutes
	 * before attempting to use the bank again.
	 */
	private static final int MAX_ATTEMPTS = 4;

	public enum Action {
		SETUP,
		CHANGE,
		DELETE,
		RECOVERY
	}

	public Action action;

	public int actionStage;
	
	private boolean promptShown;

	/**
	 * Construct a new BankPin.
	 * @param p Player to assign this class to.
	 */
	public BankPin(Player p) {
		this.p = p;
	}

	/**
	 * Handles clicking on buttons within the numpad and settings interfaces.
	 */
	public void handleButton(int buttonId) {
		switch (buttonId) {
		/* Numpad buttons */
			case 14873:
				selectNum(0);
				break;
			case 14874:
				selectNum(1);
				break;
			case 14875:
				selectNum(2);
				break;
			case 14876:
				selectNum(3);
				break;
			case 14877:
				selectNum(4);
				break;
			case 14878:
				selectNum(5);
				break;
			case 14879:
				selectNum(6);
				break;
			case 14880:
				selectNum(7);
				break;
			case 14881:
				selectNum(8);
				break;
			case 14882:
				selectNum(9);
				break;

			/* Set a new pin */
			case 15075:
				action = Action.SETUP;
				showPrompt("Do you really wish to set a PIN on your bank account?",
					"Yes, I really want a bank PIN. I will never forget it!",
					"No, I might forget it!");
				break;

			/* Change active pin */
			case 15078:
				action = Action.CHANGE;
				showPrompt("Do you really wish to change your bank PIN?",
					"Yes, I really want to change my bank PIN.",
					"No thanks, I'd rather keep my current PIN.");
				break;

			/* Delete active pin */
			case 15079:
				action = Action.DELETE;
				showPrompt("Do you really wish to delete your Bank PIN?",
					"Yes, I don't need a PIN anymore.",
					"No thanks, I'd rather keep the extra security.");
				break;

			/* Bank pin prompt (yes) */
			case 15171:
				actionStage++;
				hidePrompt();
				show();
				break;

			/* Bank pin prompt (no) */
			case 15176:
				action = null;
				hidePrompt();
				break;

			/* Change recovery delay */
			case 15080:
			case 15076:
				if (currentPin.isEmpty() || p.bankPin.entered) {
					p.bankPin.changeRecoveryDelay();
				} else {
					action = Action.RECOVERY;
					show();
				}
				break;

			/* Exit */
			case 14922:
				cancel();
				break;

			/* Forgot PIN button */
			case 14921:
				pendingPin.clear();
				setPendingDate(pendingDelay);
				action = null;

				p.frames.sendChatStatement(
					"Since you don't know your PIN, it will be deleted @dre@in "
						+ pendingDaysLeft() + " days@bla@.",
					"If you wish to cancel this change, you may do so by entering",
					"your PIN correctly next time you attempt to use your bank.");
				break;
		}
	}

	private void selectNum(int number) {
		enteredPin.add(numbers.get(number));

		if (enteredPin.size() == 4) {
			switch (action) {
				case SETUP:
					switch (actionStage) {
						case 1:
							break;
						case 2:

							break;
					}
					break;
			}
		}
		show();
	}

	public void show() {
		numbers.clear();

		for (int i = 0; i < 10; i++) {
			numbers.add(i);
		}
		Collections.shuffle(numbers);

		for (int i = 0; i < numbers.size(); i++) {
			if (enteredPin.size() < 4) {
				p.frames.setString("" + numbers.get(i), 14883 + i);
				p.frames.moveChild(14883 + i, Misc.random(46), -Misc.random(42));
			} else {
				p.frames.setString("", 14883 + i);
			}
		}

		String[] instructions = { "First click the FIRST digit", "Then click the SECOND digit",
			"Then click the THIRD digit", "And lastly click the FOURTH digit" };
		p.frames.setString(instructions[enteredPin.size()], 15313);

		for (int i = 0; i < 4; i++) {
			p.frames.setString(enteredPin.size() > i ? "*" : "?", 14913 + i);
		}

		switch (action) {
			case SETUP:
			case CHANGE:
				switch (actionStage) {
					case 1:
						p.frames.setString("Set new PIN", 14923);
						p.frames.setString(
							"Please choose a new FOUR DIGIT PIN using the buttons below", 14920);
						p.frames.moveChild(14922, 0, 10);
						p.frames.moveChild(14921, 0, 500);

					case 2:
						p.frames.setString("Confirm new PIN", 14923);
						p.frames.setString("Now please enter that number again.", 14920);
						break;
				}
				break;

			case DELETE:
				p.frames.setString("Bank of RuneScape", 14923);
				p.frames.setString("Please enter your FOUR DIGIT PIN using the buttons below.",
					14920);
				p.frames.moveChild(14922, 0, 0);
				p.frames.moveChild(14921, 0, 0);
				break;

			default:
				if (removing()) {
					p.frames.setString(
						"YOUR PIN WILL BE DELETED IN " + pendingDaysLeft() + " DAYS", 14923);
				} else {
					p.frames.setString("Bank of RuneScape", 14923);
				}
				if (!currentPin.isEmpty() || pending()) {
					p.frames.moveChild(14922, 0, 0);
					p.frames.moveChild(14921, 0, 0);
				} else {
					p.frames.moveChild(14922, 0, 10);
					p.frames.moveChild(14921, 0, 500);
				}
				break;
		}
		p.frames.sendInterface(7424);
	}
	
	public void showSettings() {
		if (currentPin.isEmpty()) {
			p.frames.setString("Customers are reminded", 15038);
			p.frames.setString("That they should NEVER", 15039);
			p.frames.setString("Tell anyone their bank", 15040);
			p.frames.setString("PINs or passwords, nor", 15041);
			p.frames.setString("should they ever enter", 15042);
			p.frames.setString("their PINs on any website", 15043);
			p.frames.setString("form.", 15044);
			p.frames.setString("", 15045);
			p.frames.setString("Have you read the PIN", 15046);
			p.frames.setString("Frequently Asked", 15047);
			p.frames.setString("Questions on the", 15048);
			p.frames.setString("website?", 15049);
		}
		updateButtons();
		p.frames.setString(p.bankPin.pendingDelay + " days", 15107);
		p.frames.sendInterface(14924);
	}
	
	public void showPrompt(String question, String yes, String no) {
		int[] options = { 15075, 15076, 15078, 15079, 15080, 15082 };

		for (int i = 0; i < options.length; i++) {
			p.frames.moveChild(options[i], 0, 100);
		}
		p.frames.setString(question, 15110);
		p.frames.setString(yes, 15171);
		p.frames.setString(no, 15176);

		p.frames.hideChild(15108, 0);
		p.frames.moveChild(15171, 0, 0);
		p.frames.moveChild(15176, 0, 0);
		promptShown = true;
	}
	
	public void hidePrompt() {
		updateButtons();
		p.frames.hideChild(15108, 1);
		p.frames.moveChild(15171, 0, 500);
		p.frames.moveChild(15176, 0, 500);
		promptShown = false;
	}

	public void cancel() {

	}

	/**
	 * Sets the visible buttons on the bank PIN interface.
	 */
	public void updateButtons() {
		int[] buttons = { 15075, 15076, 15078, 15079, 15080, 15082 };
		
		if (currentPin.isEmpty()) {
			p.frames.setString("No PIN set", 15105);

			p.frames.moveChild(buttons[2], 0, 100);
			p.frames.moveChild(buttons[3], 0, 100);
			p.frames.moveChild(buttons[4], 0, 100);
			p.frames.moveChild(buttons[5], 0, 100);
			p.frames.moveChild(buttons[0], 0, 0);
			p.frames.moveChild(buttons[1], 0, 0);
		} else {
			if (awaiting() || pending()) {
				p.frames.setString("PIN coming soon", 15105);

				p.frames.moveChild(buttons[0], 0, 100);
				p.frames.moveChild(buttons[1], 0, 100);
				p.frames.moveChild(buttons[2], 0, 100);
				p.frames.moveChild(buttons[3], 0, 100);
				p.frames.moveChild(buttons[4], 0, 100);
				p.frames.moveChild(buttons[5], 0, 0);
			} else {
				p.frames.setString("You have a PIN", 15105);
				
				p.frames.moveChild(buttons[0], 0, 100);
				p.frames.moveChild(buttons[1], 0, 100);
				p.frames.moveChild(buttons[5], 0, 100);
				p.frames.moveChild(buttons[2], 0, 0);
				p.frames.moveChild(buttons[3], 0, 0);
				p.frames.moveChild(buttons[4], 0, 0);
			}
		}
	}

	/**
	 * Sets the date for when a pin change takes effect.
	 */
	public void setPendingDate(int days) {
		Date recoveryDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(recoveryDate);
		cal.add(Calendar.DATE, days);
		recoveryDate = cal.getTime();
		pendingDate = recoveryDate;
	}

	/**
	 * Returns the days left until PIN changes.
	 */
	public long pendingDaysLeft() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();

		try {
			today = formatter.parse(formatter.format(today));
		} catch (ParseException e) {
		}
		long diff = pendingDate.getTime() - today.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	/**
	 * Toggle the recovery delay between 3 and 7 days.
	 */
	public void changeRecoveryDelay() {
		pendingDelay = (pendingDelay == 3) ? 7 : 3;
		action = null;

		p.frames.sendChatStatement("Your PIN recovery delay has been changed to @dre@"
			+ pendingDelay + " days@bla@.");

		/*p.dialogue.sendDialogue(new DialogueEvent() {
			@Override
			public void action() {
				showSettings();
			}
		});*/
	}

	public boolean changeRequested() {
		return pendingDate != null;
	}

	public boolean awaiting() {
		return changeRequested() && currentPin.isEmpty() && !pendingPin.isEmpty();
	}

	public boolean pending() {
		return changeRequested() && !pendingPin.isEmpty();
	}

	public boolean removing() {
		return changeRequested() && pendingPin.isEmpty();
	}

	public boolean lockedOut() {
		return attempts == MAX_ATTEMPTS;
	}

}
