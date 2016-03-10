package org.rs2.io.packets;

import org.rs2.io.Packet;
import org.rs2.model.content.HaircutInterface;
import org.rs2.model.content.ClothingInterface.Arms;
import org.rs2.model.content.ClothingInterface.Colors;
import org.rs2.model.content.ClothingInterface.Legs;
import org.rs2.model.content.ClothingInterface.Torsos;
import org.rs2.model.content.DialogueOption.DialogueButton;
import org.rs2.model.content.GenderInterface.Skins;
import org.rs2.model.content.HaircutInterface.Beards;
import org.rs2.model.content.HaircutInterface.Hairs;
import org.rs2.model.players.Player;
import org.rs2.util.Sounds;

/**
 * Actions that each interface button performs when clicked.
 */
public class ButtonClick implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int buttonId = p.stream.readUnsignedWord();

		if (p.buttons.performAction(buttonId)) {
			return;
		}
		p.prayer.activate(buttonId);
		p.magic.cast(buttonId);
		p.bankPin.handleButton(buttonId);
		p.skillGuide.handleButton(buttonId);

		switch (buttonId) {
			/**
			 * Trade accept
			 */
			case 3420:
				p.trade.accept();
				break;

			/**
			 * Trade confirm
			 */
			case 3546:
				p.trade.confirm();
				break;

			/**
			 * Destroy item (Yes)
			 */
			case 14175:
				p.inventory.remove(p.destroySlot);
				Sounds.play(p, Sounds.DESTROY, 0, 10);
				p.frames.closeAllInterfaces();
				break;

			/**
			 * Destroy item (No)
			 */
			case 14176:
				p.frames.closeAllInterfaces();
				break;

			/**
			 * Chatbox buttons.
			 */
			case 2461:
			case 2471:
			case 2482:
			case 2494: {
				DialogueButton button = p.dialogue.options.get(0);

				if (button != null) {
					button.action();

					if (button.isAutoClose()) {
						p.frames.closeAllInterfaces();
					}
				}
				break;
			}

			case 2462:
			case 2472:
			case 2483:
			case 2495: {
				DialogueButton button = p.dialogue.options.get(1);

				if (button != null) {
					button.action();

					if (button.isAutoClose()) {
						p.frames.closeAllInterfaces();
					}
				}
				break;
			}

			case 2473:
			case 2484:
			case 2496: {
				DialogueButton button = p.dialogue.options.get(2);

				if (button != null) {
					button.action();

					if (button.isAutoClose()) {
						p.frames.closeAllInterfaces();
					}
				}
				break;
			}

			case 2485:
			case 2497: {
				DialogueButton button = p.dialogue.options.get(3);

				if (button != null) {
					button.action();

					if (button.isAutoClose()) {
						p.frames.closeAllInterfaces();
					}
				}
				break;
			}

			case 2498: {
				DialogueButton button = p.dialogue.options.get(4);

				if (button != null) {
					button.action();

					if (button.isAutoClose()) {
						p.frames.closeAllInterfaces();
					}
				}
				break;
			}

			/**
			 * Emotes.
			 */
			case 168:
				p.requestWalk(0, 0);
				p.requestAnim(855, 0);
				break;

			case 169:
				p.requestWalk(0, 0);
				p.requestAnim(856, 0);
				break;

			case 162:
				p.requestWalk(0, 0);
				p.requestAnim(857, 0);
				break;

			case 164:
				p.requestWalk(0, 0);
				p.requestAnim(858, 0);
				break;

			case 165:
				p.requestWalk(0, 0);
				p.requestAnim(859, 0);
				break;

			case 161:
				p.requestWalk(0, 0);
				p.requestAnim(860, 0);
				break;

			case 170:
				p.requestWalk(0, 0);
				p.requestAnim(861, 0);
				break;

			case 171:
				p.requestWalk(0, 0);
				p.requestAnim(862, 0);
				break;

			case 167:
				p.requestWalk(0, 0);
				p.requestAnim(864, 0);
				break;

			case 163:
				p.requestWalk(0, 0);
				p.requestAnim(863, 0);
				break;

			case 172:
				p.requestWalk(0, 0);
				p.requestAnim(865, 0);
				break;

			case 166:
				p.requestWalk(0, 0);
				p.requestAnim(866, 0);
				break;

			case 13362:
				p.requestWalk(0, 0);
				p.requestAnim(2105, 0);
				break;

			case 13363:
				p.requestWalk(0, 0);
				p.requestAnim(2106, 0);
				break;

			case 13364:
				p.requestWalk(0, 0);
				p.requestAnim(2107, 0);
				break;

			case 13365:
				p.requestWalk(0, 0);
				p.requestAnim(2108, 0);
				break;

			case 13366:
				p.requestWalk(0, 0);
				p.requestAnim(2109, 0);
				break;

			case 13367:
				p.requestWalk(0, 0);
				p.requestAnim(2110, 0);
				break;

			case 13368:
				p.requestWalk(0, 0);
				p.requestAnim(2111, 0);
				break;

			case 13369:
				p.requestWalk(0, 0);
				p.requestAnim(2112, 0);
				break;

			case 13370:
				p.requestWalk(0, 0);
				p.requestAnim(2113, 0);
				break;

			case 11100:
				p.requestWalk(0, 0);
				p.requestAnim(1368, 0);
				break;

			/**
			 * Combat styles.
			 */
			case 5860:	// Accurate (melee), Accurate (ranged), Accurate (magic)
			case 2429:
			case 8466:
			case 4711:
			case 1704:
			case 2282:
			case 5574:
			case 12298:
				p.combatStyle = 0;
				break;

			case 5862:	// Aggressive (melee), rapid (ranged), Aggressive
						// (magic)
			case 2432:
			case 8468:
			case 4714:
			case 1707:
			case 2285:
			case 5579:
			case 12297:
				p.combatStyle = 1;
				break;

			case 5861:	// Controlled (melee), longrange (ranged), Defensive
						// (magic)
			case 2431:
			case 8487:
			case 4713:
			case 1706:
			case 2284:
			case 5578:
			case 12296:
				p.combatStyle = 2;
				break;

			case 2430:
			case 4712:
			case 1705:
			case 2283:
			case 5577:
				p.combatStyle = 3;
				break;

			/**
			 * Auto retaliate.
			 */
			case 150:
				p.autoRetaliate = true;
				break;

			case 151:
				p.autoRetaliate = false;
				break;

			/**
			 * Run and walk buttons.
			 */
			case 152:
				p.running = false;
				break;

			case 153:
				if (p.runEnergy < 1) {
					p.resetRun();
					break;
				}
				p.running = true;
				break;

			/**
			 * Brightness buttons.
			 */
			case 906:
				p.brightness = 1;
				break;

			case 908:
				p.brightness = 2;
				break;

			case 910:
				p.brightness = 3;
				break;

			case 912:
				p.brightness = 4;
				break;

			/**
			 * Single mouse button.
			 */
			case 914:
				p.singleMouseButton = true;
				break;

			case 913:
				p.singleMouseButton = false;
				break;

			/**
			 * Chat effects.
			 */
			case 915:
				p.chatEffects = true;
				break;

			case 916:
				p.chatEffects = false;
				break;

			/**
			 * Split private chat.
			 */
			case 957:
				p.splitPrivateChat = true;
				break;

			case 958:
				p.splitPrivateChat = false;
				break;

			/**
			 * Accept aid.
			 */
			case 12464:
				p.acceptAid = true;
				break;

			case 12465:
				p.acceptAid = false;
				break;

			/**
			 * Music volume.
			 */
			case 930:
				p.volumeMusic = 0;
				break;

			case 931:
				p.volumeMusic = 1;
				break;

			case 932:
				p.volumeMusic = 2;
				break;

			case 933:
				p.volumeMusic = 3;
				break;

			case 934:
				p.volumeMusic = 4;
				break;

			/**
			 * Effect volume.
			 */
			case 941:
				p.volumeEffects = 0;
				break;

			case 942:
				p.volumeEffects = 1;
				break;

			case 943:
				p.volumeEffects = 2;
				break;

			case 944:
				p.volumeEffects = 3;
				break;

			case 945:
				p.volumeEffects = 4;
				break;

			/**
			 * Music player
			 */
			case 6269:
				p.musicPlayerMode = 0;
				break;

			case 6270:
				p.musicPlayerMode = 1;
				break;

			case 9925:
				p.musicPlayerLoop = !p.musicPlayerLoop;
				break;

			/**
			 * Logout button
			 */
			case 2458:
				if (p.teleportTask != null) {
					return;
				}
				if (System.currentTimeMillis() - p.lastHit >= 10000) {
					p.frames.logout();
				} else {
					p.frames
						.sendMessage("You can't log out until 10 seconds after the end of combat.");
				}
				break;

			/**
			 * Close interface buttons.
			 */
			case 10162:
				p.frames.closeAllInterfaces();
				break;

			/**
			 * Bank pin
			 */
			case 14922:		// Exit
				//p.bankPin.cancel();
				break;

			/*case 14921:		// Forgotten
				p.bankPin.newPin.clear();
				p.bankPin.setDate(p.bankPin.recoveryDelay);
				p.bankPin.setState(States.UNKNOWN);
				p.bankPin.setAction(Actions.NONE);
				p.frames.sendChatStatement("Since you don't know your PIN, it will be deleted @dre@in "
					+ p.bankPin.recoveryDelay + " days@bla@.",
					"If you wish to cancel this change, you may do so by entering",
					"your PIN correctly next time you attempt to use your bank.");
				break;*/

			/**
			 * Has no pin
			 */
			/*case 15075:		// Set pin
				p.bankPin.setAction(Actions.SETUP);
				p.frames.showPinPrompt("Do you really wish to set a PIN on your bank account?",
					"Yes, I really want a bank PIN. I will never forget it!", "No, I might forget it!");
				break;*/

			/**
			 * Has active pin
			 */
			/*case 15078:		// Change pin
				p.bankPin.setAction(Actions.CHANGE);
				p.frames.showPinPrompt("Do you really wish to change your bank PIN?",
					"Yes, I really want to change my bank PIN.", "No thanks, I'd rather keep my current PIN.");
				break;

			case 15079:		// Delete pin
				p.bankPin.setAction(Actions.DELETE);
				p.frames.showPinPrompt("Do you really wish to delete your Bank PIN?",
					"Yes, I don't need a PIN anymore.", "No thanks, I'd rather keep the extra security.");
				break;*/

			/**
			 * Has active pin or no pin.
			 */
			/*case 15080:		// Recovery delay
			case 15076:
				if (p.bankPin.setPin.isEmpty() || p.bankPin.entered) {
					p.bankPin.changeRecoveryDelay();
				} else {
					p.bankPin.setAction(Actions.RECOVERY_DELAY);
					p.frames.sendPin();
				}
				break;*/

			/**
			 * Cancel pending pin
			 */
			/*case 15082:
				if (p.bankPin.state == States.UNKNOWN) {
					p.bankPin.setAction(Actions.CANCEL);
					p.frames.sendPin();
				} else if (p.bankPin.state == States.SETUP || p.bankPin.state == States.CHANGED
					|| p.bankPin.state == States.DELETED) {
					p.bankPin.newPin.clear();
					p.bankPin.clearDate();
					p.bankPin.setState(States.CANCELLED);
					p.frames.sendPinSettings();
				}
				break;*/

			/**
			 * Bank pin prompt
			 */
			/*case 15171:		// Yes
				if (p.bankPin.action == Actions.SETUP) {
					p.bankPin.setAction(Actions.SETUP_STAGE_1);
					p.frames.sendPin();

				} else if (p.bankPin.action == Actions.CHANGE) {
					p.bankPin.setAction(Actions.CHANGE_STAGE_1);
					p.frames.sendPin();

				} else if (p.bankPin.action == Actions.DELETE) {
					if (p.bankPin.entered) {
						p.bankPin.setPin.clear();
						p.bankPin.newPin.clear();
						p.bankPin.setState(States.DELETED);
						p.bankPin.clearDate();
					} else {
						p.bankPin.setAction(Actions.DELETE_STAGE_1);
						p.frames.sendPin();
						break;
					}
					p.frames.sendPinSettings();
				}
				break;

			case 15176:		// No
				p.bankPin.setAction(Actions.NONE);
				p.frames.hidePinPrompt();
				break;*/

			/**
			 * Bank pin numbers.
			 */
			/*case 14873:
				p.bankPin.selectNum(0);
				break;
			case 14874:
				p.bankPin.selectNum(1);
				break;
			case 14875:
				p.bankPin.selectNum(2);
				break;
			case 14876:
				p.bankPin.selectNum(3);
				break;
			case 14877:
				p.bankPin.selectNum(4);
				break;
			case 14878:
				p.bankPin.selectNum(5);
				break;
			case 14879:
				p.bankPin.selectNum(6);
				break;
			case 14880:
				p.bankPin.selectNum(7);
				break;
			case 14881:
				p.bankPin.selectNum(8);
				break;
			case 14882:
				p.bankPin.selectNum(9);
				break;*/

			/**
			 * Bank
			 */
			case 5387:		// Withdraw as item
				p.withdrawNoted = false;
				break;

			case 5386:		// Withdraw noted
				p.withdrawNoted = true;
				break;

			/**
			 * Skill guides
			 */

			/*case 8654:
				SkillGuide.show(p, 0, 0);
				break;
			case 8657:
				SkillGuide.show(p, 0, 1);
				break;
			case 8660:
				SkillGuide.show(p, 0, 2);
				break;
			case 8655:
				SkillGuide.show(p, Skills.HITPOINTS, 0);
				break;
			case 8666:
				SkillGuide.show(p, Skills.PRAYER, 0);
				break;
			case 8669:
				SkillGuide.show(p, Skills.MAGIC, 0);
				break;
			case 12162:
				SkillGuide.show(p, Skills.SLAYER, 0);
			case 8671:
				SkillGuide.show(p, Skills.WOODCUTTING, 0);
				break;
			case 33224:
				SkillGuide.show(p, Skills.RUNECRAFTING, 0);
				break;
			case 33211:
				break;*/

			/**
			 * Skill guide submenus
			 */

			/*case 8846:
				SkillGuide.show(p, p.skillGuide, 0);
				break;
			case 8823:
				SkillGuide.show(p, p.skillGuide, 1);
				break;
			case 8824:
				SkillGuide.show(p, p.skillGuide, 2);
				break;
			case 34123:
				SkillGuide.show(p, p.skillGuide, 3);
				break;*/

			/**
			 * Unmorph
			 */
			case 6020:
				p.resetDisguise();
				break;

			default:
				break;
		}

		/**
		 * Clothing changing interface.
		 */
		for (Torsos torso : Torsos.values()) {
			if (buttonId == torso.getButton()) {
				p.newTorso = torso.getLook();
				return;
			}
		}
		for (Arms arm : Arms.values()) {
			if (buttonId == arm.getButton()) {
				p.newArms = arm.getLook();
				return;
			}
		}
		for (Legs leg : Legs.values()) {
			if (buttonId == leg.getButton()) {
				p.newLegs = leg.getLook();
				return;
			}
		}
		for (Colors color : Colors.values()) {
			if (buttonId == color.getButton()[0] || buttonId == color.getButton()[1]
				|| buttonId == color.getButton()[2] || buttonId == color.getButton()[3]) {
				p.newColor = color.getColor();
				return;
			}
		}
		if (buttonId == 2959 || buttonId == 3146) {
			p.looks[2] = p.newTorso;
			p.looks[3] = p.newArms;
			p.colors[1] = p.newColor;

			p.updateReq = true;
			p.appearanceUpdateReq = true;
			p.frames.closeAllInterfaces();
			return;
		}
		if (buttonId == 108 || buttonId == 4839) {
			p.looks[5] = p.newLegs;
			p.colors[2] = p.newColor;

			p.updateReq = true;
			p.appearanceUpdateReq = true;
			p.frames.closeAllInterfaces();
			return;
		}

		/**
		 * Haircut interface.
		 */
		for (Hairs hair : Hairs.values()) {
			if (buttonId == hair.getButton()) {
				p.newHair = hair.getHair();
				return;
			}
		}
		for (Beards beard : Beards.values()) {
			if (buttonId == beard.getButton()) {
				p.newBeard = beard.getBeard();
				return;
			}
		}
		for (HaircutInterface.Colors color : HaircutInterface.Colors.values()) {
			if (buttonId == color.getButton()[0] || buttonId == color.getButton()[1]
				|| buttonId == color.getButton()[2]) {
				p.newColor = color.getColor();
				return;
			}
		}
		if (buttonId == 2753 || buttonId == 2606) {
			p.looks[0] = p.newHair;
			p.colors[0] = p.newColor;

			if (p.gender == 1) {
				p.looks[1] = -1;
			}
			p.updateReq = true;
			p.appearanceUpdateReq = true;
			p.frames.closeAllInterfaces();
			return;
		}
		if (buttonId == 2113) {
			p.looks[1] = p.newBeard;
			p.colors[0] = p.newColor;

			p.updateReq = true;
			p.appearanceUpdateReq = true;
			p.frames.closeAllInterfaces();
			return;
		}

		/**
		 * Gender interface.
		 */
		for (Skins skin : Skins.values()) {
			if (buttonId == skin.getButton()) {
				p.newColor = skin.getSkin();
				return;
			}
		}
		if (buttonId == 5568) {
			p.newGender = 0;
			return;
		}
		if (buttonId == 5569) {
			p.newGender = 1;
			return;
		}
		if (buttonId == 5544) {
			if (p.gender == 0 && p.newGender == 1) {
				p.looks[0] = p.looks[0] + 45;
				p.looks[1] = -1;
				p.looks[2] = p.looks[2] + 38;

				if (p.looks[2] > 60) {
					p.looks[2] = 60;
				}
				p.looks[3] = p.looks[3] + 35;
				p.looks[4] = p.looks[4] + 34;
				p.looks[5] = p.looks[5] + 34;
				p.looks[6] = p.looks[6] + 37;
			} else if (p.gender == 1 && p.newGender == 0) {
				p.looks[0] = p.looks[0] - 45;
				p.looks[1] = 14;
				p.looks[2] = p.looks[2] - 38;
				p.looks[3] = p.looks[3] - 35;
				p.looks[4] = p.looks[4] - 34;
				p.looks[5] = p.looks[5] - 34;

				if (p.looks[5] > 40) {
					p.looks[5] = 40;
				}
				p.looks[6] = p.looks[6] - 37;
			}
			p.colors[4] = p.newColor;
			p.gender = p.newGender;

			p.updateReq = true;
			p.appearanceUpdateReq = true;
			p.frames.closeAllInterfaces();
			return;
		}
	}
}
