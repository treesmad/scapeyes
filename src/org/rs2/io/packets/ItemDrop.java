package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.util.Sounds;

/**
 * Called when the user selects the "Drop" or "Destroy" option on an item.
 */
public class ItemDrop implements Packet {

	/**
	 * Drop item on the ground.
	 */
	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int itemId = p.stream.readUnsignedWordA();
		int unknown = p.stream.readUnsignedByte() + p.stream.readUnsignedByte();
		int slot = p.stream.readUnsignedWordA();
		int amount = p.inventory.getAmt(slot);
		
		if (p.inventory.getId(slot) != itemId) {
			return;
		}
		if (p.isBusy() || p.disguiseId != -1) {
			return;
		}
		p.resetActionTask();
		p.frames.closeAllInterfaces();
		p.resetAnim();

		if (ItemDef.destroyable(itemId)) {
			p.destroySlot = slot;
			p.frames.setItem(14171, new Item(itemId), 0);
			p.frames.setString(ItemDef.getName(itemId), 14184);
			
			String[] message = ItemDef.getDestroyMsg(itemId);
			p.frames.setString(message.length > 0 ? message[0] : "", 14182);
			p.frames.setString(message.length > 1 ? message[1] : "", 14183);
			p.frames.sendChatInterface(14170);
			return;
		}
		p.inventory.drop(slot);
		Sounds.play(p, Sounds.DROP, 0, 10);
	}

}
