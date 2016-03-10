package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.content.DialogueContainer.Emotion;
import org.rs2.model.npcs.NPC;
import org.rs2.model.players.Player;
import org.rs2.task.Task;
import org.rs2.world.cache.region.Region;

/**
 * Sent when a player clicks or selects the first context menu option on an NPC
 * (excluding attack).
 */
public class NPCOption1 implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		int npcId = p.stream.readSignedWordBigEndian();
		NPC npc = Engine.npcs[npcId];
		p.npcDialogueId = npc.npcType;
		
		if (p.distanceToPoint(npc.absX, npc.absY) == 1) {
			p.requestFaceDir(npc.absX, npc.absY);
		} else {
			p.requestFaceNPC(npc.npcId);
		}
		
		if (p.npcDialogueId == 0) {
			/*p.dialogue.create()
				.npc("Hey, I love sucking dick!", Emotion.ANGRY_1)
				.player("Me too, sucking dick is great", Emotion.CALM)
				.player("Honestly, there's nothing more exhilarating than putting a warm, black dingus into your mouth", Emotion.DELIGHTED_EVIL)
				.npc("I know what you mean, man", Emotion.DELIGHTED_EVIL)
				.npc("peace out, homo", Emotion.DELIGHTED_EVIL);*/
			return;
		}
		
		
		
		
		
		/*Engine.task.schedule(p.walkingToTask = new Task(true) {

			@Override
			protected void execute() {
				if (p.distanceToPoint(npc.absX, npc.absY) == 0) {
					if (!Region.blockedWest(p.absX, p.absY, p.heightLevel)) {
						p.requestWalk(-1, 0);
					} else if (!Region.blockedEast(p.absX, p.absY, p.heightLevel)) {
						p.requestWalk(1, 0);
					} else if (!Region.blockedSouth(p.absX, p.absY, p.heightLevel)) {
						p.requestWalk(0, -1);
					} else if (!Region.blockedNorth(p.absX, p.absY, p.heightLevel)) {
						p.requestWalk(0, 1);
					}
					p.requestFaceDir(npc.absX, npc.absY);
					return;
				}
				if (p.distanceToPoint(npc.absX, npc.absY) > 1) {
					return;
				}
				p.requestFaceNPC(-1);				
			}
			
		});*/
	}

}
