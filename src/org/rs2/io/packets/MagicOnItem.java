package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.items.Item;
import org.rs2.model.items.ItemDef;
import org.rs2.model.players.Player;
import org.rs2.task.Task;

public class MagicOnItem implements Packet {

	private static final int LOW_ALCH = 1162;
	private static final int HIGH_ALCH = 1178;

	private static final int ENCHANT_6 = 6003;

	@Override
	public void packet(final Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int itemSlot = p.stream.readSignedWord();
		int itemId = p.stream.readSignedWordA();
		int frameId = p.stream.readSignedWord();
		int spellId = p.stream.readSignedWordA();

		System.out.println(spellId);

		if (p.inventory.getId(itemSlot) != itemId) {
			return;
		}
		if (spellId == LOW_ALCH || spellId == HIGH_ALCH) {
			if (Engine.currentTicks() - p.lastAlchCast >= 3) {
				if (itemId == 995) {
					p.frames.sendMessage("Coins are already made of gold.");
					return;
				}
				if (ItemDef.getValue(itemId) == -1) {
					p.frames.sendMessage("You cannot use alchemy on that item.");
					return;
				}
				/*p.item.deleteItem(itemId, 1, itemSlot);

				if (spellId == LOW_ALCH) {
					p.item.addItem(995, (int) (ItemDef.getValue(itemId) * .4));

					p.requestAnim(712, 0);
					p.requestGfx(112, 0, 90);
					p.frames.sendSound(224, 450, 10);
				} else if (spellId == HIGH_ALCH) {
					p.item.addItem(995, (int) (ItemDef.getValue(itemId) * .6));

					p.requestAnim(713, 0);
					p.requestGfx(113, 0, 90);
					p.frames.sendSound(223, 450, 10);
				}
				p.preventWalk = true;

				Engine.task.schedule(new Task(1) {
					@Override
					protected void execute() {
						p.frames.showTab(6);
						p.preventWalk = false;
						stop();
					}
				});*/
				p.lastAlchCast = Engine.currentTicks();
			}
			return;
		}
		p.frames.sendMessage("Nothing interesting happens.");
	}
}
