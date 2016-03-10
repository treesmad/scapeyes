package org.rs2.model.players.update;

import java.util.LinkedList;
import org.rs2.Engine;
import org.rs2.model.players.Player;
import org.rs2.util.Misc;
import org.rs2.util.Stream;
import org.rs2.world.cache.region.Region;

public class PlayerMovement {

	public void updateMovement(Player p) {
		if (p.newWalkCmdSteps > 0) {
			int firstX = p.newWalkCmdX[0], firstY = p.newWalkCmdY[0];
			int lastDir = 0;
			boolean found = false;
			p.numTravelBackSteps = 0;
			int ptr = p.wQueueReadPtr;
			int dir = Misc.direction(p.currentX, p.currentY, firstX, firstY);

			if (dir != -1 && (dir & 1) != 0) {
				do {
					lastDir = dir;
					if (--ptr < 0) {
						ptr = Player.WALKING_QUEUE_SIZE - 1;
					}
					p.travelBackX[p.numTravelBackSteps] = p.walkingQueueX[ptr];
					p.travelBackY[p.numTravelBackSteps++] = p.walkingQueueY[ptr];
					dir = Misc.direction(p.walkingQueueX[ptr], p.walkingQueueY[ptr], firstX, firstY);

					if (lastDir != dir) {
						found = true;
						break;
					}
				} while (ptr != p.wQueueWritePtr);
			} else {
				found = true;
			}
			if (found) {
				p.wQueueWritePtr = p.wQueueReadPtr;
				addToWalkingQueue(p, p.currentX, p.currentY);

				if (dir != -1 && (dir & 1) != 0) {
					for (int i = 0; i < p.numTravelBackSteps - 1; i++) {
						addToWalkingQueue(p, p.travelBackX[i], p.travelBackY[i]);
					}
					int wayPointX2 = p.travelBackX[p.numTravelBackSteps - 1], wayPointY2 = p.travelBackY[p.numTravelBackSteps - 1];
					int wayPointX1, wayPointY1;

					if (p.numTravelBackSteps == 1) {
						wayPointX1 = p.currentX;
						wayPointY1 = p.currentY;
					} else {
						wayPointX1 = p.travelBackX[p.numTravelBackSteps - 2];
						wayPointY1 = p.travelBackY[p.numTravelBackSteps - 2];
					}
					dir = Misc.direction(wayPointX1, wayPointY1, wayPointX2, wayPointY2);

					if (dir == -1 || (dir & 1) != 0) {
						System.out.println("Fatal: The walking queue is corrupt! wp1=(" + wayPointX1 + ", "
							+ wayPointY1 + "), " + "wp2=(" + wayPointX2 + ", " + wayPointY2 + ")");
					} else {
						dir >>= 1;
						found = false;
						int x = wayPointX1, y = wayPointY1;

						while (x != wayPointX2 || y != wayPointY2) {
							x += Misc.directionDeltaX[dir];
							y += Misc.directionDeltaY[dir];
							if ((Misc.direction(x, y, firstX, firstY) & 1) == 0) {
								found = true;
								break;
							}
						}
						if (found) {
							addToWalkingQueue(p, wayPointX1, wayPointY1);
						}
					}
				} else {
					for (int i = 0; i < p.numTravelBackSteps; i++) {
						addToWalkingQueue(p, p.travelBackX[i], p.travelBackY[i]);
					}
				}
				for (int i = 0; i < p.newWalkCmdSteps; i++) {
					addToWalkingQueue(p, p.newWalkCmdX[i], p.newWalkCmdY[i]);
				}
			}
		}
		p.newWalkCmdSteps = 0;
	}

	public void getNextPlayerMovement(Player p) {
		if (p == null) {
			return;
		}
		p.mapRegionChanged = false;
		p.teleported = false;
		p.walkDir = -1;
		p.runDir = -1;

		if (p.teleportToX != -1 && p.teleportToY != -1) {
			p.mapRegionChanged = true;

			if (p.mapRegionX != -1 && p.mapRegionY != -1) {
				int relX = p.teleportToX - p.mapRegionX * 8;
				int relY = p.teleportToY - p.mapRegionY * 8;
				if (relX >= 2 * 8 && relX < 11 * 8 && relY >= 2 * 8 && relY < 11 * 8) {
					p.mapRegionChanged = false;
				}
			}

			if (p.mapRegionChanged) {
				p.mapRegionX = (p.teleportToX >> 3) - 6;
				p.mapRegionY = (p.teleportToY >> 3) - 6;
			}

			p.currentX = p.teleportToX - 8 * p.mapRegionX;
			p.currentY = p.teleportToY - 8 * p.mapRegionY;
			p.absX = p.teleportToX;
			p.absY = p.teleportToY;

			resetWalkingQueue(p);

			p.teleportToX = -1;
			p.teleportToY = -1;
			p.teleported = true;
		} else {
			p.walkDir = getNextWalkingDirection(p);

			if (p.walkDir == -1) {
				return;
			}
			if (p.running) {
				p.runDir = getNextWalkingDirection(p);
			}
			int deltaX = 0, deltaY = 0;

			if (p.currentX < 2 * 8) {
				deltaX = 4 * 8;
				p.mapRegionX -= 4;
				p.mapRegionChanged = true;
			} else if (p.currentX >= 11 * 8) {
				deltaX = -4 * 8;
				p.mapRegionX += 4;
				p.mapRegionChanged = true;
			}
			if (p.currentY < 2 * 8) {
				deltaY = 4 * 8;
				p.mapRegionY -= 4;
				p.mapRegionChanged = true;
			} else if (p.currentY >= 11 * 8) {
				deltaY = -4 * 8;
				p.mapRegionY += 4;
				p.mapRegionChanged = true;
			}
			if (p.mapRegionChanged) {
				p.currentX += deltaX;
				p.currentY += deltaY;

				for (int i = 0; i < Player.WALKING_QUEUE_SIZE; i++) {
					p.walkingQueueX[i] += deltaX;
					p.walkingQueueY[i] += deltaY;
				}
			}
		}
	}

	public int getNextWalkingDirection(Player p) {
		if (p == null) {
			return -1;
		}
		if (p.wQueueReadPtr == p.wQueueWritePtr) {
			return -1;
		}
		int dir;
		do {
			dir = Misc.direction(p.currentX, p.currentY, p.walkingQueueX[p.wQueueReadPtr],
				p.walkingQueueY[p.wQueueReadPtr]);
			if (dir == -1) {
				p.wQueueReadPtr = (p.wQueueReadPtr + 1) % Player.WALKING_QUEUE_SIZE;
			} else if ((dir & 1) != 0) {
				resetWalkingQueue(p);
				return -1;
			}
		} while (dir == -1 && p.wQueueReadPtr != p.wQueueWritePtr);

		if (dir == -1) {
			return -1;
		}
		dir >>= 1;

		p.currentX += Misc.directionDeltaX[dir];
		p.currentY += Misc.directionDeltaY[dir];
		p.absX += Misc.directionDeltaX[dir];
		p.absY += Misc.directionDeltaY[dir];
		return dir;
	}

	public void updateThisPlayerMovement(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		if (p.mapRegionChanged) {
			p.frames.setMapRegion();
		}
		if (p.teleported) {
			p.frames.teleport();
			return;
		}
		if (p.walkDir == -1) {
			p.frames.noMovement();
		} else {
			p.frames.updateMovement();
		}
	}

	public void updatePlayerMovement(Player p, Stream stream) {
		if (p == null || stream == null) {
			return;
		}
		if (p.walkDir == -1) {
			if (p.updateReq) {
				stream.writeBits(1, 1);
				stream.writeBits(2, 0);
			} else {
				stream.writeBits(1, 0);
			}
		} else if (p.runDir == -1) {
			stream.writeBits(1, 1);
			stream.writeBits(2, 1);
			stream.writeBits(3, Misc.xlateDirectionToClient[p.walkDir]);
			stream.writeBits(1, (p.updateReq) ? 1 : 0);
		} else {
			stream.writeBits(1, 1);
			stream.writeBits(2, 2);
			stream.writeBits(3, Misc.xlateDirectionToClient[p.walkDir]);
			stream.writeBits(3, Misc.xlateDirectionToClient[p.runDir]);
			stream.writeBits(1, (p.updateReq) ? 1 : 0);
		}
	}

	/**
	 * Finds a route and walks to the specified coordinates.
	 * @param destX
	 * @param destY
	 * @param moveNear
	 * @param xLength
	 * @param yLength
	 */
	public void findRoute(Player p, int destX, int destY, boolean moveNear, int xLength, int yLength) {
		if (destX == p.absX - 8 * p.mapRegionX && destY == p.absY - 8 * p.mapRegionY && !moveNear) {
			return;
		}
		destX = destX - 8 * p.mapRegionX;
		destY = destY - 8 * p.mapRegionY;

		int[][] via = new int[104][104];
		int[][] cost = new int[104][104];

		LinkedList<Integer> tileQueueX = new LinkedList<Integer>();
		LinkedList<Integer> tileQueueY = new LinkedList<Integer>();

		for (int xx = 0; xx < 104; xx++) {
			for (int yy = 0; yy < 104; yy++) {
				cost[xx][yy] = 99999999;
			}
		}
		int curX = p.absX - 8 * p.mapRegionX;
		int curY = p.absY - 8 * p.mapRegionY;

		via[curX][curY] = 99;
		cost[curX][curY] = 0;

		int tail = 0;
		tileQueueX.add(curX);
		tileQueueY.add(curY);

		boolean foundPath = false;
		int pathLength = 4000;
		while (tail != tileQueueX.size() && tileQueueX.size() < pathLength) {
			curX = tileQueueX.get(tail);
			curY = tileQueueY.get(tail);

			int curAbsX = p.mapRegionX * 8 + curX;
			int curAbsY = p.mapRegionY * 8 + curY;

			if (curX == destX && curY == destY) {
				foundPath = true;
				break;
			}
			tail = (tail + 1) % pathLength;

			int thisCost = cost[curX][curY] + 1;

			if (curY > 0 && via[curX][curY - 1] == 0
				&& (Region.getClipping(curAbsX, curAbsY - 1, p.heightLevel) & 0x1280102) == 0) {
				tileQueueX.add(curX);
				tileQueueY.add(curY - 1);
				via[curX][curY - 1] = 1;
				cost[curX][curY - 1] = thisCost;
			}
			if (curX > 0 && via[curX - 1][curY] == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY, p.heightLevel) & 0x1280108) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY);
				via[curX - 1][curY] = 2;
				cost[curX - 1][curY] = thisCost;
			}
			if (curY < 104 - 1 && via[curX][curY + 1] == 0
				&& (Region.getClipping(curAbsX, curAbsY + 1, p.heightLevel) & 0x1280120) == 0) {
				tileQueueX.add(curX);
				tileQueueY.add(curY + 1);
				via[curX][curY + 1] = 4;
				cost[curX][curY + 1] = thisCost;
			}
			if (curX < 104 - 1 && via[curX + 1][curY] == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY, p.heightLevel) & 0x1280180) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY);
				via[curX + 1][curY] = 8;
				cost[curX + 1][curY] = thisCost;
			}
			if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY - 1, p.heightLevel) & 0x128010e) == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY, p.heightLevel) & 0x1280108) == 0
				&& (Region.getClipping(curAbsX, curAbsY - 1, p.heightLevel) & 0x1280102) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY - 1);
				via[curX - 1][curY - 1] = 3;
				cost[curX - 1][curY - 1] = thisCost;
			}
			if (curX > 0 && curY < 104 - 1 && via[curX - 1][curY + 1] == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY + 1, p.heightLevel) & 0x1280138) == 0
				&& (Region.getClipping(curAbsX - 1, curAbsY, p.heightLevel) & 0x1280108) == 0
				&& (Region.getClipping(curAbsX, curAbsY + 1, p.heightLevel) & 0x1280120) == 0) {
				tileQueueX.add(curX - 1);
				tileQueueY.add(curY + 1);
				via[curX - 1][curY + 1] = 6;
				cost[curX - 1][curY + 1] = thisCost;
			}
			if (curX < 104 - 1 && curY > 0 && via[curX + 1][curY - 1] == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY - 1, p.heightLevel) & 0x1280183) == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY, p.heightLevel) & 0x1280180) == 0
				&& (Region.getClipping(curAbsX, curAbsY - 1, p.heightLevel) & 0x1280102) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY - 1);
				via[curX + 1][curY - 1] = 9;
				cost[curX + 1][curY - 1] = thisCost;
			}
			if (curX < 104 - 1 && curY < 104 - 1 && via[curX + 1][curY + 1] == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY + 1, p.heightLevel) & 0x12801e0) == 0
				&& (Region.getClipping(curAbsX + 1, curAbsY, p.heightLevel) & 0x1280180) == 0
				&& (Region.getClipping(curAbsX, curAbsY + 1, p.heightLevel) & 0x1280120) == 0) {
				tileQueueX.add(curX + 1);
				tileQueueY.add(curY + 1);
				via[curX + 1][curY + 1] = 12;
				cost[curX + 1][curY + 1] = thisCost;
			}
		}

		if (!foundPath) {
			if (moveNear) {
				int i_223_ = 1000;
				int thisCost = 100;
				int i_225_ = 10;
				for (int x = destX - i_225_; x <= destX + i_225_; x++) {
					for (int y = destY - i_225_; y <= destY + i_225_; y++) {
						if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < 100) {
							int i_228_ = 0;
							if (x < destX) {
								i_228_ = destX - x;
							} else if (x > destX + xLength - 1) {
								i_228_ = x - (destX + xLength - 1);
							}
							int i_229_ = 0;
							if (y < destY) {
								i_229_ = destY - y;
							} else if (y > destY + yLength - 1) {
								i_229_ = y - (destY + yLength - 1);
							}
							int i_230_ = i_228_ * i_228_ + i_229_ * i_229_;
							if (i_230_ < i_223_ || (i_230_ == i_223_ && (cost[x][y] < thisCost))) {
								i_223_ = i_230_;
								thisCost = cost[x][y];
								curX = x;
								curY = y;
							}
						}
					}
				}
				if (i_223_ == 1000) {
					return;
				}
			} else {
				return;
			}
		}
		tail = 0;
		tileQueueX.set(tail, curX);
		tileQueueY.set(tail++, curY);
		int l5;
		for (int j5 = l5 = via[curX][curY]; curX != p.absX - 8 * p.mapRegionX || curY != p.absY - 8 * p.mapRegionY; j5 = via[curX][curY]) {
			if (j5 != l5) {
				l5 = j5;
				tileQueueX.set(tail, curX);
				tileQueueY.set(tail++, curY);
			}
			if ((j5 & 2) != 0) {
				curX++;
			} else if ((j5 & 8) != 0) {
				curX--;
			}
			if ((j5 & 1) != 0) {
				curY++;
			} else if ((j5 & 4) != 0) {
				curY--;
			}
		}
		Engine.playerMovement.resetWalkingQueue(p);
		int size = tail--;
		int pathX = p.mapRegionX * 8 + tileQueueX.get(tail);
		int pathY = p.mapRegionY * 8 + tileQueueY.get(tail);
		Engine.playerMovement.addToWalkingQueue(p, localize(pathX, p.mapRegionX), localize(pathY, p.mapRegionY));

		for (int i = 1; i < size; i++) {
			tail--;
			pathX = p.mapRegionX * 8 + tileQueueX.get(tail);
			pathY = p.mapRegionY * 8 + tileQueueY.get(tail);
			Engine.playerMovement.addToWalkingQueue(p, localize(pathX, p.mapRegionX), localize(pathY, p.mapRegionY));
		}
	}

	public int localize(int x, int mapRegion) {
		return x - 8 * mapRegion;
	}

	public void addToWalkingQueue(Player p, int x, int y) {
		if (p == null) {
			return;
		}
		int next = (p.wQueueWritePtr + 1) % Player.WALKING_QUEUE_SIZE;

		if (next == p.wQueueWritePtr) {
			return;
		}
		p.walkingQueueX[p.wQueueWritePtr] = x;
		p.walkingQueueY[p.wQueueWritePtr] = y;
		p.wQueueWritePtr = next;
	}

	public void resetWalkingQueue(Player p) {
		if (p == null) {
			return;
		}
		p.wQueueReadPtr = 0;
		p.wQueueWritePtr = 0;

		for (int i = 0; i < Player.WALKING_QUEUE_SIZE; i++) {
			p.walkingQueueX[i] = p.currentX;
			p.walkingQueueY[i] = p.currentY;
		}
	}
}
