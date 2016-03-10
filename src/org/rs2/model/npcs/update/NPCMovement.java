package org.rs2.model.npcs.update;

import org.rs2.model.npcs.NPC;
import org.rs2.util.Misc;
import org.rs2.util.Stream;

public class NPCMovement {

	public void getNextNPCMovement(NPC n) {
		if (n == null) {
			return;
		}
        if (n.moveX == 0 && n.moveY == 0) {
            return;
        }
		int dir = Misc.direction(n.absX, n.absY, (n.absX + n.moveX), (n.absY + n.moveY));

		if (dir == -1) {
			return;
		}
		dir >>= 1;

		n.absX += n.moveX;
		n.absY += n.moveY;
	}

	public void updateNPCMovement(NPC n, Stream stream) {
		if (n == null || stream == null) {
			return;
		}
		if (n.faceDir == -1) {
			if (n.updateReq) {
				stream.writeBits(1, 1);
				stream.writeBits(2, 0);
			} else {
				stream.writeBits(1, 0);
			}
		} else {
			stream.writeBits(1, 1);
			stream.writeBits(2, 1);
			stream.writeBits(3, Misc.xlateDirectionToClient[n.faceDir]);
			stream.writeBits(1, n.updateReq ? 1 : 0);
		}
	}

}
