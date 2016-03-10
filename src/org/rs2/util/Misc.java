package org.rs2.util;

import java.text.DecimalFormat;

public class Misc {

	public static byte directionDeltaX[] = new byte[] { 0, 1, 1, 1, 0, -1, -1, -1 };
	public static byte directionDeltaY[] = new byte[] { 1, 1, 0, -1, -1, -1, 0, 1 };

	public static byte xlateDirectionToClient[] = new byte[] { 1, 2, 4, 7, 6, 5, 3, 0 };

	/**
	 * Finds a direction from the specified coordinates.
	 * @param dx X coordinate to get direction from.
	 * @param dy Y coordinate to get direction from.
	 */
	public static int direction(int dx, int dy) {
		if (dx < 0) {
			if (dy < 0) {
				return 5;
			} else if (dy > 0) {
				return 0;
			} else {
				return 3;
			}
		} else if (dx > 0) {
			if (dy < 0) {
				return 7;
			} else if (dy > 0) {
				return 2;
			} else {
				return 4;
			}
		} else {
			if (dy < 0) {
				return 6;
			} else if (dy > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	public static int direction(int srcX, int srcY, int destX, int destY) {
		int dx = destX - srcX;
		int dy = destY - srcY;

		if (dx < 0) {
			if (dy < 0) {
				if (dx < dy) {
					return 11;
				} else if (dx > dy) {
					return 9;
				} else {
					return 10;
				}
			} else if (dy > 0) {
				if (-dx < dy) {
					return 15;
				} else if (-dx > dy) {
					return 13;
				} else {
					return 14;
				}
			} else {
				return 12;
			}
		} else if (dx > 0) {
			if (dy < 0) {
				if (dx < -dy) {
					return 7;
				} else if (dx > -dy) {
					return 5;
				} else {
					return 6;
				}
			} else if (dy > 0) {
				if (dx < dy) {
					return 1;
				} else if (dx > dy) {
					return 3;
				} else {
					return 2;
				}
			} else {
				return 4;
			}
		} else {
			if (dy < 0) {
				return 8;
			} else if (dy > 0) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	public static String hex(byte data[]) {
		return hex(data, 0, data.length);
	}

	public static String hex(byte data[], int offset, int len) {
		String temp = "";

		for (int cntr = 0; cntr < len; cntr++) {
			int num = data[offset + cntr] & 0xFF;
			String myStr;

			if (num < 16) {
				myStr = "0";
			} else {
				myStr = "";
			}
			temp += myStr + Integer.toHexString(num) + " ";
		}
		return temp.toUpperCase().trim();
	}

	public static int hexToInt(byte data[], int offset, int len) {
		int temp = 0;
		int i = 1000;

		for (int cntr = 0; cntr < len; cntr++) {
			int num = (data[offset + cntr] & 0xFF) * i;
			temp += (int) num;

			if (i > 1) {
				i = i / 1000;
			}
		}
		return temp;
	}

	/**
	 * Returns a random number from 0 to the specified range.
	 * @param range Maximum return value.
	 */
	public static int random(int range) {
		return (int) (Math.random() * (range + 1));
	}

	/**
	 * Returns a random number from 0 to the specified range.
	 * @param range Maximum return value.
	 */
	public static double random(double range) {
		return (Math.random() * (range + 1));
	}

	/**
	 * Returns a random number from the specified range.
	 * @param min Minimum return value.
	 * @param max Maximum return value.
	 */
	public static int random(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}
	
}