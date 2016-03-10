package org.rs2.io;

import org.rs2.Engine;
import org.rs2.io.packets.*;
import org.rs2.model.players.Player;

public class PacketManager {

	public static Packet[] packet = new Packet[256];

	// @formatter:off
	public static final int packetSizes[] = {
			0, 0, 0, 1, -1, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 8, 0, 6, 2, 2, 0,	// 10
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0,	// 20
			0, 0, 0, 0, 0, 8, 4, 0, 0, 2,	// 30
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0,	// 40
			0, 0, 0, 12, 0, 0, 0, 0, 8, 0,	// 50
			0, 8, 0, 0, 0, 0, 0, 0, 0, 0,	// 60
			6, 0, 2, 2, 8, 6, 0, -1, 0, 6,	// 70
			0, 0, 0, 0, 0, 1, 4, 6, 0, 0,	// 80
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0,	// 90
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0,	// 100
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0,	// 110
			1, 0, 6, 0, 0, 0, -1, 0, 2, 6,	// 120
			0, 4, 6, 8, 0, 6, 0, 0, 0, 2,	// 130
			0, 0, 0, 0, 0, 6, 0, 0, 0, 0,	// 140
			0, 0, 1, 2, 0, 2, 6, 0, 0, 0,	// 150
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0,	// 160
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 170
			0, 8, 0, 3, 0, 2, 0, 0, 8, 1,	// 180
			0, 0, 12, 0, 0, 0, 0, 0, 0, 0,	// 190
			2, 0, 0, 0, 0, 0, 0, 0, 4, 0,	// 200
			4, 0, 0, 0, 7, 8, 0, 0, 10, 0,	// 210
			0, 0, 0, 0, 0, 0, -1, 0, 6, 0,	// 220
			1, 0, 0, 0, 6, 0, 6, 8, 1, 0,	// 230
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4,	// 240
			0, 0, 6, 6, 0, 0, 0				// 250
	};
	// @formatter:on

	static {
		packet[202] = new PlayerIdleLogout();
		packet[4] = new PlayerChat();
		packet[103] = new PlayerCommands();
		packet[95] = new PlayerPrivacyOptions();
		packet[39] = new PlayerFollow();

		packet[41] = new PlayerEquipItem();

		packet[101] = new PlayerDesign();

		packet[185] = new ButtonClick();

		packet[121] = new RegionLoad();
		packet[210] = new RegionChange();

		packet[218] = new ReportAbuse();

		packet[130] = new CloseInterface();

		packet[188] = new AddFriend();
		packet[133] = new AddIgnore();
		packet[215] = new RemoveFriend();
		packet[74] = new RemoveIgnore();
		packet[126] = new PrivateMessage();

		PlayerWalking walking = new PlayerWalking();
		packet[248] = walking;
		packet[164] = walking;
		packet[98] = walking;

		packet[145] = new RemoveItem();		// Remove 1 item / Unequip item
		packet[117] = new RemoveItem5();	// Remove 5 items / Sell 1 item
		packet[43] = new RemoveItem10();	// Remove 10 items / Sell 5 items
		packet[129] = new RemoveItemAll();	// Remove all items / Sell 10 items
		RemoveItemX removeItemX = new RemoveItemX();
		packet[135] = removeItemX;			// Remove x items
		packet[208] = removeItemX;			// Remove x items part 2

		packet[155] = new NPCOption1();
		packet[17] = new NPCOption2();
		packet[21] = new NPCOption3();
		packet[40] = new NPCDialogue();
		
		packet[132] = new ObjectOption1();
		packet[252] = new ObjectOption2();
		packet[70] = new ObjectOption3();

		packet[72] = new AttackNPC();
		packet[128] = new AttackPlayer();

		packet[53] = new ItemOnItem();
		packet[192] = new ItemOnObject();
		packet[14] = new ItemOnPlayer();
		packet[25] = new ItemOnGroundItem();
		packet[236] = new ItemPickup();
		packet[87] = new ItemDrop();
		packet[214] = new ItemMove();
		packet[253] = new ItemLight();
		packet[122] = new ItemOption1();
		packet[16] = new ItemOption2();
		packet[75] = new ItemOption3();

		packet[181] = new MagicOnGroundItem();
		packet[249] = new MagicOnPlayer();
		packet[237] = new MagicOnItem();

		packet[73] = new TradeRequest();
		packet[139] = new TradeAnswer();
	}

	/**
	 * Interpret incoming packets.
	 */
	public void parseIncomingPackets() {
		int avail = 0;
		int packetId = 0;
		int packetSize = 0;

		for (Player p : Engine.players) {
			if (p == null || !p.online || p.disconnected) {
				continue;
			}
			try {
				for (int i = 0; i < 10; i++) {
					avail = p.socket.input.available();

					if (avail < 1) {
						break;
					}
					packetId = p.socket.input.read() & 0xff;
					avail--;

					if (packetId < 0 || packetId > 255) {
						break;
					}
					packetSize = packetSizes[packetId];

					if (packetSize == -1) {
						if (avail > 0) {
							packetSize = p.socket.input.read() & 0xff;
							avail--;
						} else {
							break;
						}
					}
					if (packetSize > avail) {
						break;
					}
					if (packetSize >= 500) {
						break;
					}
					p.stream.inOffset = 0;
					p.socket.fillStream(p, packetSize);
					managePacket(p, packetId, packetSize);
				}
			} catch (Exception e) {
				p.disconnected = true;
			}
		}
	}

	public void managePacket(Player p, int id, int size) {
		if (id == -1) {
			return;
		}
		try {
			if (id < packet.length) {
				if (packet[id] != null) {
					System.out.println("Packet called: " + packet[id].getClass().getSimpleName() + " (" + id + ")");
				} else {
					//System.out.println("Packet called: unknown " + id);
				}
				packet[id].packet(p, id, size);
			}
		} catch (Exception e) {
		}
	}

}
