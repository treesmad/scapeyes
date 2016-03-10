package org.rs2.model.players.containers;

import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.util.EquipSlot;
import org.rs2.util.Sounds;

public class Equipment extends Container {

	private Player p;

	public Equipment(Player p) {
		super(14);
		this.p = p;
	}

	/**
	 * Returns true if the specified slot is valid.
	 * @param slot Slot to check.
	 */
	@Override
	protected boolean validSlot(int slot) {
		if (slot < 0 || slot >= capacity()) {
			return false;
		}
		if (slot == 8 || slot == 11) {
			return false;
		}
		return true;
	}

	/**
	 * Equips an item from the inventory.
	 */
	public void wear(int slot) {
		Item item = p.inventory.get(slot);

		if (item == null) {
			return;
		}
		int equipSlot = ItemDef.getEquipSlot(item.getId());

		if (ItemDef.twoHanded(item.getId()) && p.inventory.emptySlots() < 1) {
			if (p.equipment.get(EquipSlot.WEAPON) != null
				&& p.equipment.get(EquipSlot.SHIELD) != null) {
				p.frames.sendMessage("You don't have enough free inventory space to do that.");
				return;
			}
		}
		if (ItemDef.stackable(item.getId())) {
			p.inventory.transfer(slot, this);
		} else {
			p.inventory.swap(slot, equipSlot, this);
		}
		if (equipSlot == EquipSlot.WEAPON) {
			if (ItemDef.twoHanded(item.getId()) && get(EquipSlot.SHIELD) != null) {
				remove(EquipSlot.SHIELD);
			}
		} else if (equipSlot == EquipSlot.SHIELD) {
			if (ItemDef.twoHanded(getId(EquipSlot.WEAPON)) && get(EquipSlot.WEAPON) != null) {
				remove(EquipSlot.WEAPON);
			}
		}
	}

	@Override
	public boolean add(Item item) {
		if (item == null) {
			return false;
		}
		add(item, ItemDef.getEquipSlot(item.getId()));
		return true;
	}

	@Override
	public boolean add(Item item, int slot) {
		if (item == null) {
			return false;
		}
		if (!validSlot(slot)) {
			if (ItemDef.getEquipSlot(item.getId()) != -1) {
				slot = ItemDef.getEquipSlot(item.getId());
			} else {
				return false;
			}
		}
		item = copy(item);

		if (getId(slot) == item.getId()) {
			if (ItemDef.stackable(item.getId()) && getAmt(slot) + item.getAmt() < 0) {
				setAmt(Integer.MAX_VALUE - item.getAmt(), slot);
			} else {
				setAmt(getAmt(slot) + item.getAmt(), slot);
			}
		} else {
			set(item, slot);
		}
		updateItem(slot);
		return true;
	}

	@Override
	public void remove(int slot) {
		if (!p.inventory.holdItem(get(slot))) {
			p.frames.sendMessage("You don't have enough free inventory space to do that.");
			return;
		}
		transfer(slot, p.inventory);
	}

	@Override
	public void shift() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void updateItem(int slot) {
		p.frames.setItem(1688, slot, this);

		if (slot == EquipSlot.WEAPON) {
			sendWeapon();
			sendStances();
		}
		p.appearanceUpdateReq = true;
		p.updateReq = true;
		p.calculateBonuses();
		p.frames.sendBonuses();
		p.frames.sendWeight();
	}

	@Override
	void updateItems() {
		p.frames.setItems(1688, this);
		sendWeapon();
		sendStances();
		p.appearanceUpdateReq = true;
		p.updateReq = true;
		p.calculateBonuses();
		p.frames.sendBonuses();
		p.frames.sendWeight();
	}

	public void sendEquipSound(int itemId) {
		String name = ItemDef.getName(itemId);
		int equipSlot = ItemDef.getEquipSlot(itemId);
		Sounds sound = null;

		if (equipSlot == EquipSlot.WEAPON) {
			if (name.contains("axe")) {
				if (name.endsWith("battleaxe") || name.endsWith("pickaxe")
					|| name.endsWith("greataxe")) {
					sound = Sounds.EQUIP_PICKAXE;
				} else {
					sound = Sounds.EQUIP_AXE;
				}
			} else if (name.contains("mace")) {
				sound = Sounds.EQUIP_MACE;
			} else {
				sound = Sounds.EQUIP_WEAPON;
			}
		} else {
			if (name.contains("helm") || name.contains("helmet")) {
				sound = Sounds.EQUIP_HELMET;
			} else if (name.contains("platebody") || name.contains("chainbody")) {
				sound = Sounds.EQUIP_PLATEBODY;
			} else if (name.contains("shield")) {
				sound = Sounds.EQUIP_SHIELD;
			} else {
				sound = Sounds.EQUIP_ARMOR;
			}
		}
		Sounds.play(p, sound, 0, 10);
	}

	/**
	 * Sends weapon attack styles to the combat tab.
	 * @param id Id of weapon to get styles for.
	 */
	public void sendWeapon() {
		if (p == null) {
			return;
		}
		String name = ItemDef.getName(getId(EquipSlot.WEAPON)).toLowerCase();
		int interfaceId = -1;
		boolean update = false;
		boolean special = false;

		if ((name.contains("dagger") || name.contains("sword")) && !name.contains("2h")) {
			interfaceId = 2276;
		}
		if (name.endsWith("scimitar")) {
			interfaceId = 2423;
		}
		if (name.endsWith("2h sword")) {
			interfaceId = 4705;
		}
		if (name.endsWith("mace") || name.equals("veracs flail")) {
			interfaceId = 4796;
		}
		if (name.endsWith("axe")) {
			interfaceId = 1698;
		}
		if (name.endsWith("pickaxe")) {
			interfaceId = 5570;
		}
		if (name.endsWith("warhammer") || name.equals("granite maul")
			|| name.equals("tzhaar ket-om") || name.equals("rubber chicken")) {
			interfaceId = 425;
		}
		if (name.endsWith("spear")) {
			interfaceId = 4679;
			update = true;
		}
		if (name.endsWith("halberd")) {
			interfaceId = 8460;
			update = true;
		}
		if (name.endsWith("claws")) {
			interfaceId = 7762;
			update = true;
		}
		if (name.endsWith("bow")) {
			interfaceId = 1764;
			update = true;
		}
		if (name.endsWith("dart")) {
			interfaceId = 4446;
			update = true;
		}
		if (name.contains("staff")) {
			interfaceId = 328;
		}
		if (name.equals("abyssal whip")) {
			interfaceId = 12290;
			special = true;
			update = true;
		}
		if (getId(EquipSlot.WEAPON) == -1) {
			p.frames.setTab(0, 5855);
			p.frames.setString("Unarmed", 5857);
			update = true;
		} else {
			p.frames.setTab(0, interfaceId);
			p.frames.sendInterfaceItemModel(interfaceId + 1, 200, getId(EquipSlot.WEAPON));
			p.frames.setString(ItemDef.getName(getId(EquipSlot.WEAPON)), interfaceId + 3);
		}

		if (update && p.combatStyle > 2) {
			p.combatStyle = 2;
			p.frames.setConfig(43, p.combatStyle);
		}
	}

	/**
	 * Chooses animation sets based on the currently equipped weapon.
	 * @param id Id of weapon to check.
	 */
	public void sendStances() {
		if (p == null) {
			return;
		}
		String name = ItemDef.getName(getId(EquipSlot.WEAPON)).toLowerCase();

		p.standAnim = 0x328;
		p.walkAnim = 0x333;
		p.runAnim = 0x338;

		if (name.endsWith("2h sword") || name.contains("dharok") || name.equals("tzhaar ket-om")) {
			p.standAnim = 2065;
			p.walkAnim = 2064;
		}
		if (name.contains("staff") || name.contains("spear") || name.endsWith("halberd")) {
			p.standAnim = 809;
		}
		if (name.equals("abyssal whip")) {
			p.walkAnim = 1660;
			p.runAnim = 1661;
		}
		if (name.contains("verac")) {
			p.standAnim = 2061;
			p.walkAnim = 2060;
		}
		if (name.equals("granite maul")) {
			p.standAnim = 1662;
			p.walkAnim = 1663;
			p.runAnim = 1664;
		}
		p.appearanceUpdateReq = true;
		p.updateReq = true;
	}

	/**
	 * TODO: Move weapon methods to combat class.
	 */

	/**
	 * Returns animation ID depending on weapon and attack stlye.
	 */
	public int getAttackAnim() {
		if (p == null) {
			return -1;
		}
		if (get(EquipSlot.WEAPON) == null) {
			if (p.combatStyle == 1) {
				return 423;
			}
			return 422;
		}
		String name = ItemDef.getName(getId(EquipSlot.WEAPON)).toLowerCase();

		if (name.contains("dagger") || name.endsWith("sword")) {
			if (name.endsWith("2h sword")) {
				if (p.combatStyle == 2) {
					return 406;
				}
				return 407;
			}
			if (name.contains("dragon dagger")) {
				if (p.combatStyle == 2) {
					return 395;
				}
				return 402;
			}
			if (p.combatStyle == 2) {
				return 451;
			}
			return 412;
		}
		if (name.endsWith("scimitar")) {
			if (p.combatStyle == 2) {
				return 412;
			}
			return 451;
		}
		if (name.equals("abyssal whip")) {
			return 1658;
		}
		if (name.contains("dharoks greataxe") || name.contains("dharoks axe")) {
			if (p.combatStyle == 2) {
				return 2066;
			}
			return 2067;
		}
		if (name.contains("ahrims staff")) {
			return 2078;
		}
		if (name.contains("veracs flail")) {
			return 2062;
		}
		if (name.equals("granite maul")) {
			return 1665;
		}
		if (name.contains("bow")) {
			return 426;
		}
		return -1;
	}

	public int getBlockAnim() {
		if (p == null) {
			return -1;
		}
		String weapon = ItemDef.getName(getId(EquipSlot.WEAPON)).toLowerCase();
		String shield = ItemDef.getName(getId(EquipSlot.SHIELD)).toLowerCase();

		if (weapon.endsWith("dagger") || weapon.endsWith("sword") || weapon.endsWith("scimitar")) {
			if (weapon.endsWith("2h sword") || weapon.equals("dharoks greataxe")
				|| weapon.equals("dharoks axe")) {
				return 410;
			}

			if (shield.contains("shield")) {
				return 403;
			}

			return 404;
		}
		if (weapon.equals("abyssal whip")) {
			return 1659;
		}

		if (weapon.equals("ahrims staff")) {
			return 2079;
		}

		if (weapon.equals("veracs flail")) {
			return 2063;
		}
		return 424;
	}

	/**
	 * Returns the attack speed in game ticks for the player's currently
	 * equipped weapon.
	 */
	public int getWeapSpeed() {
		if (p == null) {
			return -1;
		}
		String name = ItemDef.getName(getId(EquipSlot.WEAPON)).toLowerCase();

		String[] fast = { "dart", "throwing", "toktz-xil-ul" };
		for (String s : fast) {
			if (name.contains(s)) {
				return 3;
			}
		}

		String[] slow = { "longsword", "mace", "axe", "pickaxe", "spear", "torags hammers",
			"flail", "battlestaff", "staff", "crystal bow", "throwing axe", "composite",
			"silverlight", "darklight", "excalibur" };
		for (String s : slow) {
			if (name.endsWith("battleaxe") || name.equals("ahrims staff")) {
				continue;
			}
			if (name.contains(s)) {
				return 5;
			}
		}

		String[] slower = { "battleaxe", "warhammer", "javelin", "longbow", "crossbow",
			"ahrims staff" };
		for (String s : slower) {
			if (name.contains(s)) {
				return 6;
			}
		}

		String[] verySlow = { "2h sword", "halberd", "maul", "tzhaar-ket-om", "dharoks greataxe",
			"dharoks axe" };
		for (String s : verySlow) {
			if (name.contains(s)) {
				return 7;
			}
		}

		String[] slowest = { "orge bow" };
		for (String s : slowest) {
			if (name.contains(s)) {
				return 8;
			}
		}
		return 4;
	}

	public int getWeapRange() {
		if (p == null) {
			return 1;
		}
		String name = ItemDef.getName(getId(EquipSlot.WEAPON)).toLowerCase();

		if (name.contains("halberd")) {
			return 2;
		}
		if (name.contains("shortbow")) {
			if (p.combatStyle == 2) {
				return 7;
			}
			return 5;
		}
		return 1;
	}
}
