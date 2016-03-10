package org.rs2.io.packets;

import org.rs2.Engine;
import org.rs2.io.Packet;
import org.rs2.model.players.Player;
import org.rs2.world.cache.region.Region;

public class RegionChange implements Packet {

	@Override
	public void packet(Player p, int id, int size) {
		if (p == null || p.stream == null) {
			return;
		}
		p.region = Region.getRegion(p.absX, p.absY);
		Engine.objects.updateRegion(p);
		Engine.ground.updateRegion(p);
	}

}
