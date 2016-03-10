package org.rs2.model.npcs.update;

import org.rs2.model.npcs.NPC;
import org.rs2.util.Stream;

public class NPCUpdateMasks {

	public void appendNPCFaceDir(NPC n, Stream stream) {
		if (n == null || stream == null) {
			return;
		}
		stream.writeWord(n.faceDir);
	}

	public void appendNPCFaceTo(NPC n, Stream stream) {
		if (n == null || stream == null) {
			return;
		}
		stream.writeWordBigEndian(n.faceToX);
		stream.writeWordBigEndian(n.faceToY);
	}

	public void appendNPCHit1(NPC n, Stream stream) {
		if (n == null || stream == null) {
			return;
		}
		stream.writeByteS(n.hitDiff1);

		if (n.poisonHit1 == 0) {
			if (n.hitDiff1 > 0) {
				stream.writeByteC(1);
			} else {
				stream.writeByteC(0);
			}
		} else {
			stream.writeByteC(2);
		}
		stream.writeByteS(n.health);
		stream.writeByte(n.maxHealth);
	}

	public void appendNPCHit2(NPC n, Stream stream) {
		if (n == null || stream == null) {
			return;
		}
		stream.writeByteS(n.hitDiff2);

		if (n.poisonHit2 == 0) {
			if (n.hitDiff2 > 0) {
				stream.writeByteC(1);
			} else {
				stream.writeByteC(0);
			}
		} else {
			stream.writeByteC(2);
		}
		stream.writeByteS(n.health);
		stream.writeByte(n.maxHealth);
	}

	public void appendNPCAnim(NPC n, Stream stream) {
		if (n == null || stream == null) {
			return;
		}
		stream.writeWordBigEndian(n.animReq);
		stream.writeByte(1);
	}

	public void appendNPCText(NPC n, Stream stream) {
		if (n == null || stream == null) {
			return;
		}
		stream.writeString(n.text);
	}

}
