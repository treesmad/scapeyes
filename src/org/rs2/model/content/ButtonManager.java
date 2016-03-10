package org.rs2.model.content;

import java.util.ArrayList;
import org.rs2.model.players.Player;

/**
 * Allows you to assign an action to an interface button.
 */
public class ButtonManager {
	/**
	 * Player this class belongs to.
	 */
	private Player p;
	/**
	 * Actions assigned to each button.
	 */
	public ArrayList<ButtonEvent> events = new ArrayList<ButtonEvent>();

	/**
	 * Construct a new InterfaceManager.
	 * @param p Player to assign this class to.
	 */
	public ButtonManager(Player p) {
		this.p = p;
	}

	public void addAction(ButtonEvent... buttonAction) {
		for (int i = 0; i < buttonAction.length; i++) {
			events.add(buttonAction[i]);
		}
	}

	public boolean performAction(int id) {
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i) == null) {
				continue;
			}
			if (events.get(i).getId() == id) {
				events.get(i).action();
				events.remove(i);
				return true;
			}
		}
		return false;
	}

}
