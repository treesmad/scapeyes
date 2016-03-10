package org.rs2.model.content;

import org.rs2.model.players.Player;

public class DialogueContainer {
	/**
	 * Player this class belongs to.
	 */
	private Player p;
	/**
	 * Multiple choice menu that is currently being displayed.
	 */
	public DialogueOption options;
	
	/**
	 * Emotion animations.
	 */
	public enum Emotion {
		HAPPY(588),
		CALM(589),
		CALM_CONTINUED(590),
		DEFAULT(591),
		EVIL(592),
		EVIL_CONTINUED(593),
		DELIGHTED_EVIL(594),
		ANNOYED(595),
		DISTRESSED(596),
		DISTRESSED_CONTINUED(597),
		NEAR_TEARS(598),
		SAD(599),
		DISORIENTED_LEFT(600),
		DISORIENTED_RIGHT(601),
		UNINTERESTED(602),
		SLEEPY(603),
		PLAIN_EVIL(604),
		LAUGHING(605),
		LONGER_LAUGHING(606),
		LONGER_LAUGHING_2(607),
		LAUGHING_2(608),
		EVIL_LAUGH_SHORT(609),
		SLIGHTLY_SAD(610),
		VERY_SAD(611),
		OTHER(612),
		NEAR_TEARS_2(613),
		ANGRY_1(614),
		ANGRY_2(615),
		ANGRY_3(616),
		ANGRY_4(617);

		int id;

		Emotion(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	/**
	 * Constructs a new DialogueContainer
	 * @param p Assign player to
	 */
	public DialogueContainer(Player p) {
		this.p = p;
	}
	
	public void send(DialogueContainer d) {
		
	}

	/**
	 * Sends the multiple choice dialogue option interface.
	 * @param d Dialogue actions to send.
	 * @param s Titles for each button.
	 */
	public void send(DialogueOption d) {
		if (d.size() < 2 || d.size() > 5) {
			return;
		}
		if (d.size() == 2) {
			p.frames.setString(d.getLabel(0), 2461);
			p.frames.setString(d.getLabel(1), 2462);
			p.frames.sendChatInterface(2459);
		} else if (d.size() == 3) {
			p.frames.setString(d.getLabel(0), 2471);
			p.frames.setString(d.getLabel(1), 2472);
			p.frames.setString(d.getLabel(2), 2473);
			p.frames.sendChatInterface(2469);
		} else if (d.size() == 4) {
			p.frames.setString(d.getLabel(0), 2482);
			p.frames.setString(d.getLabel(1), 2483);
			p.frames.setString(d.getLabel(2), 2484);
			p.frames.setString(d.getLabel(3), 2485);
			p.frames.sendChatInterface(2480);
		} else if (d.size() == 5) {
			p.frames.setString(d.getLabel(0), 2494);
			p.frames.setString(d.getLabel(1), 2495);
			p.frames.setString(d.getLabel(2), 2496);
			p.frames.setString(d.getLabel(3), 2497);
			p.frames.setString(d.getLabel(4), 2498);
			p.frames.sendChatInterface(2492);
		}
		options = d;
	}

}
