package org.rs2.model.players.update;

import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.util.Skills;
import org.rs2.util.Stream;
import org.rs2.util.TextUtils;

public class PlayerUpdateMasks {

	public void appendPlayerGFX(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		stream.writeWordBigEndian(p.gfxReq);
		stream.writeDWord(p.gfxDelay);
	}

	public void appendPlayerAnim(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		stream.writeWordBigEndian(p.animReq);
		stream.writeByteC(p.animDelay);
	}

	public void appendPlayerForcedChat(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		stream.writeString(p.forcedChatText);
	}

	public void appendPlayerChatText(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		stream.writeWordBigEndian(((p.chatTextColor & 0xFF) << 8) + (p.chatTextEffects & 0xFF));
		stream.writeByte(p.rights);
		stream.writeByteC(p.chatTextSize);
		stream.writeBytes_reverse(p.chatText, p.chatTextSize, 0);
	}

	public void appendPlayerFaceEntity(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		stream.writeWordBigEndian(p.faceEntityId);
	}

	public void appendPlayerFaceDir(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		stream.writeWordBigEndianA(p.faceDirX);
		stream.writeWordBigEndian(p.faceDirY);
	}

	public void appendPlayerHit1(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		stream.writeByte(p.hitDiff1);

		if (p.poisonHit1 == 0) {
			if (p.hitDiff1 > 0) {
				stream.writeByteA(1);
			} else {
				stream.writeByteA(0);
			}
		} else {
			stream.writeByteA(2);
		}
		int newHP = (p.skillLevel[3] - p.hitDiff1);

		if (newHP <= 0) {
			newHP = 0;
		}
		stream.writeByteC(newHP);
		stream.writeByte(p.getLevelForXP(Skills.HITPOINTS));
	}

	public void appendPlayerHit2(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		stream.writeByte(p.hitDiff2);

		if (p.poisonHit2 == 0) {
			if (p.hitDiff2 > 0) {
				stream.writeByteS(1);
			} else {
				stream.writeByteS(0);
			}
		} else {
			stream.writeByteS(2);
		}
		int newHP = (p.skillLevel[3] - p.hitDiff2);

		if (newHP <= 0) {
			newHP = 0;
		}
		stream.writeByte(newHP);
		stream.writeByteC(p.getLevelForXP(Skills.HITPOINTS));
	}

	public void appendPlayerAppearance(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		Stream playerProps = new Stream(1, 100);

		playerProps.writeByte(p.gender);
		playerProps.writeByte(p.headIcon);
		playerProps.writeByte(p.skullIcon);

		if (p.disguiseId == -1) {
			// Hat, cape, amulet, and weapon
			for (int i = 0; i < 4; i++) {
				if (p.equipment.get(i) != null) {
					playerProps.writeWord(0x200 + p.equipment.getId(i));
				} else {
					playerProps.writeByte(0);
				}
			}
			// Torso
			if (p.equipment.get(4) != null) {
				playerProps.writeWord(0x200 + p.equipment.getId(4));
			} else {
				playerProps.writeWord(0x100 + p.looks[2]);
			}
			// Shield
			if (p.equipment.get(5) != null) {
				playerProps.writeWord(0x200 + p.equipment.getId(5));
			} else {
				playerProps.writeByte(0);
			}
			// Arms
			if (!ItemDef.fullBody(p.equipment.getId(4))) {
				playerProps.writeWord(0x100 + p.looks[3]);
			} else {
				playerProps.writeByte(0);
			}
			// Legs
			if (p.equipment.get(7) != null) {
				playerProps.writeWord(0x200 + p.equipment.getId(7));
			} else {
				playerProps.writeWord(0x100 + p.looks[5]);
			}
			// Head
			if (!ItemDef.fullHelmet(p.equipment.getId(0)) && !ItemDef.fullMask(p.equipment.getId(0))) {
				playerProps.writeWord(0x100 + p.looks[0]);
			} else {
				playerProps.writeByte(0);
			}
			// Hands
			if (!ItemDef.fullTorso(p.equipment.getId(4))) {
				if (p.equipment.get(9) != null) {
					playerProps.writeWord(0x200 + p.equipment.getId(9));
				} else {
					playerProps.writeWord(0x100 + p.looks[4]);
				}
			} else {
				playerProps.writeWord(0x200 + p.equipment.getId(4));
			}
			// Feet
			if (!ItemDef.fullLegs(p.equipment.getId(7))) {
				if (p.equipment.get(10) != null) {
					playerProps.writeWord(0x200 + p.equipment.getId(10));
				} else {
					playerProps.writeWord(0x100 + p.looks[6]);
				}
			} else {
				playerProps.writeWord(0x200 + p.equipment.getId(7));
			}
			// Beard
			if (p.gender == 0 && !ItemDef.fullMask(p.equipment.getId(0))) {
				playerProps.writeWord(0x100 + p.looks[1]);
			} else {
				playerProps.writeByte(0);
			}
			
			
			/*// Hat, cape, amulet, and weapon
			for (int i = 0; i < 4; i++) {
				if (p.equipment[i] != -1) {
					playerProps.writeWord(0x200 + p.equipment[i]);
				} else {
					playerProps.writeByte(0);
				}
			}
			// Torso
			if (p.equipment[4] != -1) {
				playerProps.writeWord(0x200 + p.equipment[4]);
			} else {
				playerProps.writeWord(0x100 + p.looks[2]);
			}
			// Shield
			if (p.equipment[5] != -1) {
				playerProps.writeWord(0x200 + p.equipment[5]);
			} else {
				playerProps.writeByte(0);
			}
			// Arms
			if (!ItemDef.fullBody(p.equipment[4])) {
				playerProps.writeWord(0x100 + p.looks[3]);
			} else {
				playerProps.writeByte(0);
			}
			// Legs
			if (p.equipment[7] != -1) {
				playerProps.writeWord(0x200 + p.equipment[7]);
			} else {
				playerProps.writeWord(0x100 + p.looks[5]);
			}
			// Head
			if (!ItemDef.fullHelmet(p.equipment[0]) && !ItemDef.fullMask(p.equipment[0])) {
				playerProps.writeWord(0x100 + p.looks[0]);
			} else {
				playerProps.writeByte(0);
			}
			// Hands
			if (!ItemDef.fullTorso(p.equipment[4])) {
				if (p.equipment[9] != -1) {
					playerProps.writeWord(0x200 + p.equipment[9]);
				} else {
					playerProps.writeWord(0x100 + p.looks[4]);
				}
			} else {
				playerProps.writeWord(0x200 + p.equipment[4]);
			}
			// Feet
			if (!ItemDef.fullLegs(p.equipment[7])) {
				if (p.equipment[10] != -1) {
					playerProps.writeWord(0x200 + p.equipment[10]);
				} else {
					playerProps.writeWord(0x100 + p.looks[6]);
				}
			} else {
				playerProps.writeWord(0x200 + p.equipment[7]);
			}
			// Beard
			if (p.gender == 0 && !ItemDef.fullMask(p.equipment[0])) {
				playerProps.writeWord(0x100 + p.looks[1]);
			} else {
				playerProps.writeByte(0);
			}*/
		} else {
			playerProps.writeWord(-1);
			playerProps.writeWord(p.disguiseId);
		}
		for (int i = 0; i < 5; i++) {
			playerProps.writeByte(p.colors[i]);
		}
		int turnEmote[] = { 0x337, 0x334, 0x335, 0x336 };

		playerProps.writeWord(p.standAnim);
		playerProps.writeWord(turnEmote[0]); // stand turn
		playerProps.writeWord(p.walkAnim);
		playerProps.writeWord(turnEmote[1]); // turn 180
		playerProps.writeWord(turnEmote[2]); // turn 90 cw
		playerProps.writeWord(turnEmote[3]); // turn 90 cc
		playerProps.writeWord(p.runAnim);

		playerProps.writeQWord(TextUtils.stringToInt64(p.playerName));
		playerProps.writeByte(calculateCombatLevel(p));
		playerProps.writeWord(0);
		stream.writeByteC(playerProps.outOffset);
		stream.writeBytes(playerProps.outBuffer, playerProps.outOffset, 0);

		playerProps = null;
	}

	public int calculateCombatLevel(Player p) {
		if (p == null) {
			return 0;
		}
		double melee = (p.getLevelForXP(Skills.ATTACK) + p.getLevelForXP(Skills.STRENGTH)) * 0.325;
		double range = (p.getLevelForXP(Skills.RANGED) * 1.5) * 0.325;
		double mage = (p.getLevelForXP(Skills.MAGIC) * 1.5) * 0.325;

		int combatLevel = (int) ((p.getLevelForXP(Skills.ATTACK)
			+ p.getLevelForXP(Skills.HITPOINTS) + Math.floor(p.getLevelForXP(Skills.PRAYER) / 2)) * 0.25) + 1;

		if (melee >= range && melee >= mage) {
			combatLevel += melee;
		} else if (range >= melee && range >= mage) {
			combatLevel += range;
		} else if (mage >= range && mage >= melee) {
			combatLevel += mage;
		}
		return combatLevel;
	}

}
