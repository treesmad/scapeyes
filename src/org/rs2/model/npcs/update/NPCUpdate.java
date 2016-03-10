package org.rs2.model.npcs.update;

import org.rs2.Engine;
import org.rs2.model.npcs.NPC;
import org.rs2.model.players.Player;
import org.rs2.util.Stream;

public class NPCUpdate extends NPCUpdateMasks {

	private Stream updateBlock = new Stream(500, 5000);

	public void updateNPC(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		updateBlock.outOffset = 0;

		stream.createFrameVarSizeWord(65);
		stream.initBitAccess();
		stream.writeBits(8, p.npcListSize);

		int size = p.npcListSize;
		p.npcListSize = 0;
		for (int i = 0; i < size; i++) {
			if (!withinDistance(p, p.npcList[i]) || p.npcList[i] == null || p.teleported) {
				p.npcsInList[p.npcList[i].npcId] = 0;
				stream.writeBits(1, 1);
				stream.writeBits(2, 3);
			} else {
				Engine.npcMovement.updateNPCMovement(p.npcList[i], stream);

				if (p.npcList[i].updateReq) {
					appendNPCUpdateMasks(p.npcList[i], updateBlock);
				}
				p.npcList[p.npcListSize++] = p.npcList[i];
			}
		}
		for (NPC n : Engine.npcs) {
			if (n == null || !withinDistance(p, n) || p.npcsInList[n.npcId] == 1) {
				continue;
			}
			addNewNPC(p, n, stream);
		}
		if (updateBlock.outOffset > 0) {
			stream.writeBits(14, 16383);
			stream.finishBitAccess();
			stream.writeBytes(updateBlock.outBuffer, updateBlock.outOffset, 0);
		} else {
			stream.finishBitAccess();
		}
		stream.endFrameVarSizeWord();
	}

	private void addNewNPC(Player p, NPC n, Stream stream) {
		if (p == null || n == null || stream == null) {
			return;
		}
		p.npcsInList[n.npcId] = 1;
		p.npcList[p.npcListSize++] = n;

		stream.writeBits(14, n.npcId);

		int y = n.absY - p.absY;
		if (y < 0) {
			y += 32;
		}
		int x = n.absX - p.absX;
		if (x < 0) {
			x += 32;
		}
		stream.writeBits(5, y);
		stream.writeBits(5, x);
		stream.writeBits(1, 0);
		stream.writeBits(12, n.npcType);
		stream.writeBits(1, n.updateReq ? 1 : 0);
		appendNPCUpdateMasks(n, stream);
	}

	public void appendNPCUpdateMasks(NPC n, Stream stream) {
		if (n == null || stream == null) {
			return;
		}
		if (!n.updateReq) {
			return;
		}
		int mask = 0;

		if (n.textUpdateReq) {
			mask |= 1;
		}
		if (n.animUpdateReq) {
			mask |= 0x10;
		}
		if (n.hitUpdateReq1) {
			mask |= 0x40;
		}
		/*
		 * if (n.hitUpdateReq2) { mask |= 0x400; }
		 */
		if (n.faceToUpdateReq) {
			mask |= 0x20;
		}
		if (n.faceDirUpdateReq) {
			mask |= 0x20;
		}
		stream.writeByte(mask);

		if (n.textUpdateReq) {
			appendNPCText(n, stream);
		}
		if (n.animUpdateReq) {
			appendNPCAnim(n, stream);
		}
		if (n.hitUpdateReq1) {
			appendNPCHit1(n, stream);
		}
		/*
		 * if (n.hitUpdateReq2) { appendNPCHit2(n, stream); }
		 */
		if (n.faceToUpdateReq) {
			appendNPCFaceTo(n, stream);
		}
		if (n.faceDirUpdateReq) {
			appendNPCFaceDir(n, stream);
		}
	}

	public void clearUpdateReqs(NPC n) {
		if (n == null) {
			return;
		}
		n.updateReq = false;
		n.textUpdateReq = false;
		n.animUpdateReq = false;
		n.hitUpdateReq1 = false;
		n.hitUpdateReq2 = false;
		n.faceToUpdateReq = false;
		n.faceDirUpdateReq = false;
		n.text = "";
		n.animReq = -1;
		n.gfxReq = -1;
		n.gfxDelay = 0;
		n.moveX = 0;
		n.moveY = 0;
		n.faceDir = -1;
		n.faceToX = -1;
		n.faceToY = -1;
		n.hitDiff1 = 0;
		n.poisonHit1 = 0;
	}

	/**
	 * Check if the NPC is within distance.
	 * @param p The player to check the distance with.
	 * @param n The NPC to check the distance with.
	 */
	private boolean withinDistance(Player p, NPC n) {
		if (n != null && p != null) {
			if (p.heightLevel != n.heightLevel || n.hidden) {
				return false;
			}
			int deltaX = n.absX - p.absX, deltaY = n.absY - p.absY;
			return (deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16);
		}
		return false;
	}
}
