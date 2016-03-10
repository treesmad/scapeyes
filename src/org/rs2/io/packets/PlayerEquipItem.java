package org.rs2.io.packets;

import java.util.ArrayList;
import org.rs2.io.Packet;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import static org.rs2.util.EquipSlot.*;
import static org.rs2.util.Skills.*;

/**
 * Sent when the player clicks item context menu option "wear" or "wield".
 */
public class PlayerEquipItem implements Packet {

	private static class Requirement {
		int skill;
		int level;

		Requirement(int skill, int level) {
			this.skill = skill;
			this.level = level;
		}

		public int getSkill() {
			return skill;
		}

		public int getLevel() {
			return level;
		}
	}

	private static ArrayList<Requirement> reqs = new ArrayList<Requirement>(2);

	/**
	 * Adds a skill and a level to the list of requirements.
	 * @param skill Skill required.
	 * @param level Level of skill required.
	 */
	public static void addReq(int skill, int level) {
		reqs.add(new Requirement(skill, level));
	}

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int itemId = p.stream.readUnsignedWord();
		int itemSlot = p.stream.readUnsignedWordA();
		int interfaceId = p.stream.readUnsignedWordA();

		if (p.inventory.getId(itemSlot) != itemId || ItemDef.getEquipSlot(itemId) == -1) {
			return;
		}
		if (p.isBusy()) {
			return;
		}
		p.resetActionTask();
		p.frames.closeAllInterfaces();

		if (!meetsRequirements(p, itemId)) {
			return;
		}
		p.equipment.wear(itemSlot);
		p.equipment.sendEquipSound(itemId);
	}

	/**
	 * Returns true if the player meets the requirement(s) for the specified
	 * item.
	 * @param p Player to check for.
	 * @param id Id of item to check requirements for.
	 */
	public static boolean meetsRequirements(Player p, int id) {
		if (p == null || id == -1) {
			return false;
		}
		String name = ItemDef.getName(id).toLowerCase();
		reqs.clear();

		if (name.startsWith("steel")) {
			if (ItemDef.getEquipSlot(id) == WEAPON && ItemDef.getEquipSlot(id) != ARROWS) {
				addReq(ATTACK, 5);
			} else {
				addReq(DEFENCE, 5);
			}
		}
		if (name.startsWith("black") && !name.contains("cavalier") && !name.contains("d'hide")) {
			if (ItemDef.getEquipSlot(id) == WEAPON && ItemDef.getEquipSlot(id) != ARROWS) {
				addReq(ATTACK, 10);
			} else {
				addReq(DEFENCE, 10);
			}
		}
		if (name.startsWith("mithril")) {
			if (ItemDef.getEquipSlot(id) == WEAPON && ItemDef.getEquipSlot(id) != ARROWS) {
				addReq(ATTACK, 20);
			} else {
				addReq(DEFENCE, 20);
			}
		}
		if (name.startsWith("adamant")) {
			if (ItemDef.getEquipSlot(id) == WEAPON && ItemDef.getEquipSlot(id) != ARROWS) {
				addReq(ATTACK, 30);
			} else {
				addReq(DEFENCE, 30);
			}
		}
		if (name.startsWith("rune")) {
			if (ItemDef.getEquipSlot(id) == WEAPON && ItemDef.getEquipSlot(id) != ARROWS) {
				addReq(ATTACK, 40);
			} else {
				addReq(DEFENCE, 40);
			}
		}
		if (name.startsWith("dragon")) {
			if (ItemDef.getEquipSlot(id) == WEAPON && ItemDef.getEquipSlot(id) != ARROWS) {
				addReq(ATTACK, 60);
			} else {
				addReq(DEFENCE, 60);
			}
		}
		if (name.endsWith("halberd")) {
			for (int i = 0; i < reqs.size(); i++) {
				if (reqs.get(i).getSkill() == ATTACK) {
					addReq(STRENGTH, reqs.get(i).getLevel() / 2);
					break;
				}
			}
		}
		if (name.startsWith("dharok") || name.startsWith("torag")) {
			if (ItemDef.getEquipSlot(id) == WEAPON) {
				addReq(ATTACK, 70);
				addReq(STRENGTH, 70);
			} else {
				addReq(DEFENCE, 70);
			}
		}
		if (name.startsWith("guthan") || name.startsWith("verac")) {
			if (ItemDef.getEquipSlot(id) == WEAPON) {
				addReq(ATTACK, 70);
			} else {
				addReq(DEFENCE, 70);
			}
		}
		if (name.startsWith("ahrim")) {
			if (ItemDef.getEquipSlot(id) == WEAPON) {
				addReq(ATTACK, 70);
			} else {
				addReq(DEFENCE, 70);
			}
			addReq(MAGIC, 70);
		}
		if (name.startsWith("karil")) {
			if (ItemDef.getEquipSlot(id) != WEAPON) {
				addReq(DEFENCE, 70);
			}
			addReq(RANGED, 70);
		}

		if (name.equals("abyssal whip")) {
			addReq(ATTACK, 70);
		}
		if (name.equals("granite maul")) {
			addReq(STRENGTH, 50);
		}
		if (name.equals("tzhaar ket-om")) {
			addReq(STRENGTH, 60);
		}
		if (name.equals("toktz-ket-xil")) {
			addReq(DEFENCE, 60);
		}

		if (name.startsWith("green d'")) {
			addReq(RANGED, 40);
		}
		if (name.startsWith("blue d'")) {
			addReq(RANGED, 50);
		}
		if (name.startsWith("red d'")) {
			addReq(RANGED, 60);
		}
		if (name.startsWith("black d'")) {
			addReq(RANGED, 70);
		}
		if (name.contains("d'hide body")) {
			for (int i = 0; i < reqs.size(); i++) {
				if (reqs.get(i).getSkill() == RANGED) {
					addReq(DEFENCE, reqs.get(i).getLevel() - 10);
					break;
				}
			}
		}

		if (reqs.isEmpty()) {
			return true;
		}
		if (p.getLevelForXP(reqs.get(0).getSkill()) < reqs.get(0).getLevel()
			|| (reqs.size() == 2 && p.getLevelForXP(reqs.get(1).getSkill()) < reqs.get(1)
				.getLevel())) {
			p.frames.sendMessage("You are not a high enough level to use this item.");

			String message = String.format("You need to have %s %s level of %d.",
				Player.skillNames[reqs.get(0).getSkill()].toLowerCase().startsWith("a") ? "an"
					: "a", Player.skillNames[reqs.get(0).getSkill()].toLowerCase(), reqs.get(0)
					.getLevel());

			if (reqs.size() == 2
				&& p.getLevelForXP(reqs.get(1).getSkill()) < reqs.get(1).getLevel()) {
				if (reqs.get(0).getLevel() != reqs.get(1).getLevel()) {
					message = String
						.format(
							"You need to have %s %s level of %d and %s %s level of %d.",
							Player.skillNames[reqs.get(0).getSkill()].toLowerCase().startsWith("a") ? "an"
								: "a", Player.skillNames[reqs.get(0).getSkill()].toLowerCase(),
							reqs.get(0).getLevel(), Player.skillNames[reqs.get(1).getSkill()]
								.toLowerCase().startsWith("a") ? "an" : "a", Player.skillNames[reqs
								.get(1).getSkill()].toLowerCase(), reqs.get(1).getLevel());
				} else {
					message = String
						.format(
							"You need to have %s %s and %s level of %d.",
							Player.skillNames[reqs.get(0).getSkill()].toLowerCase().startsWith("a") ? "an"
								: "a", Player.skillNames[reqs.get(0).getSkill()].toLowerCase(),
							Player.skillNames[reqs.get(1).getSkill()].toLowerCase(), reqs.get(0)
								.getLevel());
				}
			}
			p.frames.sendMessage(message);
			return false;
		}
		return true;
	}
}
