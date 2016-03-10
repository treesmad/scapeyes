package org.rs2.model.players.update;

import org.rs2.Engine;
import org.rs2.model.players.Player;
import org.rs2.util.Stream;

public class PlayerUpdate extends PlayerUpdateMasks {

	public void updatePlayer(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		byte[] newPlayerIds = new byte[Engine.players.length];

		Engine.playerMovement.updateThisPlayerMovement(p, stream);
		stream.writeBits(8, p.playerListSize);
		int size = p.playerListSize;

		p.playerListSize = 0;
		for (int i = 0; i < size; i++) {
			if (p.playerList[i] == null || !withinDistance(p, p.playerList[i]) || p.playerList[i].teleported) {
				if (p.playerList[i] != null) {
					p.playersInList[p.playerList[i].playerId] = 0;
				}
				stream.writeBits(1, 1);
				stream.writeBits(2, 3);
			} else {
				Engine.playerMovement.updatePlayerMovement(p.playerList[i], stream);
				p.playerList[p.playerListSize++] = p.playerList[i];
			}
		}
		for (Player p2 : Engine.players) {
			if (p2 == null || p2.playerId == p.playerId || !p2.online) {
				continue;
			}
			if (p.playersInList[p2.playerId] == 1 || !withinDistance(p, p2)) {
				continue;
			}
			newPlayerIds[p.playerListSize] = 1;
			addNewPlayer(p, p2, stream);
		}
		stream.writeBits(11, 2047);
		stream.finishBitAccess();

		boolean chatUpdateFlag = p.chatTextUpdateReq;
		p.chatTextUpdateReq = false;
		appendPlayerUpdateMasks(p, stream);
		p.chatTextUpdateReq = chatUpdateFlag;

		for (int i = 0; i < p.playerListSize; i++) {
			Player p2 = p.playerList[i];

			if (newPlayerIds[i] == 1) {
				boolean appearanceFlag = p2.appearanceUpdateReq;
				boolean updateFlag = p2.updateReq;

				p2.appearanceUpdateReq = true;
				p2.updateReq = true;
				appendPlayerUpdateMasks(p2, stream);
				p2.appearanceUpdateReq = appearanceFlag;
				p2.updateReq = updateFlag;
			} else {
				appendPlayerUpdateMasks(p.playerList[i], stream);
			}
		}
		stream.endFrameVarSizeWord();
	}

	public void addNewPlayer(Player p, Player p2, Stream stream) {
		if (p == null || p2 == null || stream == null) {
			return;
		}
		p.playersInList[p2.playerId] = 1;
		p.playerList[p.playerListSize++] = p2;

		stream.writeBits(11, p2.playerId);
		stream.writeBits(1, 1);
		stream.writeBits(1, 1);

		int y = p2.absY - p.absY;
		if (y < 0) {
			y += 32;
		}
		int x = p2.absX - p.absX;
		if (x < 0) {
			x += 32;
		}
		stream.writeBits(5, y);
		stream.writeBits(5, x);
	}

	public void appendPlayerUpdateMasks(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		if (!p.updateReq) {
			return;
		}
		int mask = 0;

		if (p.gfxUpdateReq) {
			mask |= 0x100;
		}
		if (p.animUpdateReq) {
			mask |= 0x8;
		}
		if (p.forcedChatUpdateReq) {
			mask |= 0x4;
		}
		if (p.chatTextUpdateReq) {
			mask |= 0x80;
		}
		if (p.faceEntityUpdateReq) {
			mask |= 0x1;
		}
		if (p.appearanceUpdateReq) {
			mask |= 0x10;
		}
		if (p.faceDirUpdateReq) {
			mask |= 0x2;
		}
		if (p.hitUpdateReq1) {
			mask |= 0x20;
		}
		if (p.hitUpdateReq2) {
			mask |= 0x200;
		}
		if (mask >= 0x100) {
			mask |= 0x40;
			stream.writeByte(mask & 0xFF);
			stream.writeByte(mask >> 8);
		} else {
			stream.writeByte(mask);
		}
		if (p.gfxUpdateReq) {
			appendPlayerGFX(p, stream);
		}
		if (p.animUpdateReq) {
			appendPlayerAnim(p, stream);
		}
		if (p.forcedChatUpdateReq) {
			appendPlayerForcedChat(p, stream);
		}
		if (p.chatTextUpdateReq) {
			appendPlayerChatText(p, stream);
		}
		if (p.faceEntityUpdateReq) {
			appendPlayerFaceEntity(p, stream);
		}
		if (p.appearanceUpdateReq) {
			appendPlayerAppearance(p, stream);
		}
		if (p.faceDirUpdateReq) {
			appendPlayerFaceDir(p, stream);
		}
		if (p.hitUpdateReq1) {
			appendPlayerHit1(p, stream);
		}
		if (p.hitUpdateReq2) {
			appendPlayerHit2(p, stream);
		}
	}

	public void clearUpdateReqs(Player p) {
		if (p == null) {
			return;
		}
		p.updateReq = false;
		p.gfxUpdateReq = false;
		p.animUpdateReq = false;
		p.forcedChatUpdateReq = false;
		p.chatTextUpdateReq = false;
		p.faceEntityUpdateReq = false;
		p.appearanceUpdateReq = false;
		p.faceDirUpdateReq = false;
		p.hitUpdateReq1 = false;
		p.hitUpdateReq2 = false;
		p.animReq = -1;
		p.animDelay = 0;
		p.gfxReq = -1;
		p.gfxDelay = 0;
		p.hitDiff1 = 0;
		p.poisonHit1 = 0;
		p.hitDiff2 = 0;
		p.poisonHit2 = 0;
		p.faceEntityId = 65535;
	}

	/**
	 * Check if the player is within distance.
	 * @param p The player to check the distance with.
	 * @param n The second player to check the distance with.
	 */
	public boolean withinDistance(Player p, Player p2) {
		if (p == null || p2 == null) {
			return false;
		}
		if (p.heightLevel != p2.heightLevel) {
			return false;
		}
		int deltaX = p2.absX - p.absX;
		int deltaY = p2.absY - p.absY;

		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public void update(Player p) {
		if (p == null || p.disconnected) {
			return;
		}
		updatePlayer(p, p.stream);
		Engine.npcUpdate.updateNPC(p, p.stream);
	}

}
