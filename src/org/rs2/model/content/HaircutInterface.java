package org.rs2.model.content;

public class HaircutInterface {
	public enum Hairs {
		// Male
		BALD(2789, 0),
		DREADLOCKS(2790, 1),
		LONG_HAIR(2791, 2),
		MEDIUM_HAIR(2792, 3),
		TONSURE(2793, 4),
		SHORT(2794, 5),
		CROPPED(2795, 6),
		WILD_SPIKES(2796, 7),
		SPIKES(2797, 8),

		// Female
		BALD_F(2643, 45),
		BUN(2644, 46),
		DREADLOCKS_F(2645, 47),
		LONG_HAIR_F(2646, 48),
		MEDIUM_HAIR_F(2647, 49),
		PIGTAILS(2648, 50),
		SHORT_F(2649, 51),
		CROPPED_F(2650, 52),
		WILD_SPIKES_F(2651, 53),
		SPIKES_F(2652, 54);

		int button;
		int hair;

		Hairs(int button, int hair) {
			this.button = button;
			this.hair = hair;
		}

		public int getButton() {
			return button;
		}

		public int getHair() {
			return hair;
		}
	}

	public enum Beards {
		LONG_BEARD(2148, 11, 1),
		GOATEE(2149, 10, 2),
		MOUSTACHE(2150, 13, 3),
		NECKBEARD(2151, 15, 4),
		SPLIT_BEARD(2152, 17, 5),
		MEDIUM_BEARD(2153, 12, 6),
		CLEAN(2154, 14, 7),
		SHORT_BEARD(2155, 16, 8);

		int button;
		int beard;
		int config;

		Beards(int button, int beard, int config) {
			this.button = button;
			this.beard = beard;
			this.config = config;
		}

		public int getButton() {
			return button;
		}

		public int getBeard() {
			return beard;
		}

		public int getConfig() {
			return config;
		}
	}

	public enum Colors {
		BROWN(new int[] { 2777, 2136, 2631 }, 0),
		WHITE(new int[] { 2778, 2137, 2632 }, 1),
		GREY(new int[] { 2779, 2138, 2633 }, 2),
		BLACK(new int[] { 2780, 2139, 2634 }, 3),
		ORANGE(new int[] { 2781, 2140, 2635 }, 4),
		BLONDE(new int[] { 2782, 2141, 2636 }, 5),
		BEIGE(new int[] { 2783, 2142, 2637 }, 6),
		LIGHT_BROWN(new int[] { 2784, 2143, 2638 }, 7),
		CYAN(new int[] { 2785, 2144, 2639 }, 8),
		GREEN(new int[] { 2786, 2145, 2640 }, 9),
		RED(new int[] { 2787, 2146, 2641 }, 10),
		PURPLE(new int[] { 2788, 2147, 2642 }, 11);

		int[] button;
		int color;

		Colors(int[] button, int color) {
			this.button = button;
			this.color = color;
		}

		public int[] getButton() {
			return button;
		}

		public int getColor() {
			return color;
		}
	}
}
