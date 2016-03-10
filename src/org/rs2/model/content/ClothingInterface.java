package org.rs2.model.content;

public class ClothingInterface {
	public enum Torsos {
		// Male
		PLAIN(3010, 18, 1),
		LIGHT_BUTTONS(3011, 19, 2),
		JACKET(3012, 21, 3),
		DARK_BITTONS(3013, 20, 4),
		STITCHING(3014, 23, 5),
		SHIRT(3015, 22, 6),
		TWO_TONED(3016, 25, 7),

		// Female
		PLAIN_F(3186, 56),
		CROP_TOP(3187, 57),
		POLO_NECK(3188, 58),
		SIMPLE(3189, 59),
		TORN(3190, 60);

		int button;
		int look;
		int config;

		Torsos(int button, int look) {
			this.button = button;
			this.look = look;
		}

		Torsos(int button, int look, int config) {
			this.button = button;
			this.look = look;
			this.config = config;
		}

		public int getButton() {
			return button;
		}

		public int getLook() {
			return look;
		}

		public int getConfig() {
			return config;
		}
	}

	public enum Arms {
		// Male
		REGULAR(3017, 26),
		MUSCLEBOUND(3018, 27),
		LOOSE_SLEEVED(3019, 28),
		LARGE_CUFFED(3020, 29),
		THIN_SLEEVED(3021, 30),
		SHOULDER_PADS(3022, 31),

		// Female
		SHORT_SLEEVES(3191, 61),
		BARE_ARMS(3192, 62),
		MUSCLEY(3193, 63),
		LONG_SLEEVED_F(3194, 64),
		LARGE_CUFFS(3195, 65);

		int button;
		int look;

		Arms(int button, int look) {
			this.button = button;
			this.look = look;
		}

		public int getButton() {
			return button;
		}

		public int getLook() {
			return look;
		}
	}

	public enum Legs {
		// Male
		PLAIN(130, 36),
		SHORTS(131, 37),
		FLARES(132, 38),
		TURN_UPS(133, 39),

		// Female
		PLAIN_F(19001, 70),
		SHORTS_F(19002, 71),
		FLARES_F(19003, 72),
		LONG_NARROW_SKIRT(19004, 73),
		LONG_SKIRT(19005, 74),
		TURN_UPS_F(19006, 75),
		SHORT_SKIRT(19007, 76);

		int button;
		int look;

		Legs(int button, int look) {
			this.button = button;
			this.look = look;
		}

		public int getButton() {
			return button;
		}

		public int getLook() {
			return look;
		}
	}

	public enum Shoes {
		BROWN(10045, 0),
		KHAKI(10046, 0),
		ASHEN(10047, 0),
		DARK(10048, 0),
		TERRACOTTA(10049, 0),
		GREY(10050, 0);

		int button;
		int shoe;

		Shoes(int button, int shoe) {
			this.button = button;
			this.shoe = shoe;
		}

		public int getButton() {
			return button;
		}

		public int getShoe() {
			return shoe;
		}
	}

	public enum Colors {
		EMERALD(new int[] { 2943, 107, 3130, 4838 }, 0),
		KHAKI(new int[] { 2944, 92, 3131, 4823 }, 1),
		BLACK(new int[] { 2945, 93, 3132, 4824 }, 2),
		CRIMSON(new int[] { 2946, 94, 3133, 4825 }, 3),
		NAVY(new int[] { 2947, 95, 3134, 4826 }, 4),
		STRAW(new int[] { 2948, 96, 3135, 4827 }, 5),
		WHITE(new int[] { 2949, 97, 3136, 4828 }, 6),
		RED(new int[] { 2950, 98, 3137, 4829 }, 7),
		BLUE(new int[] { 2951, 99, 3138, 4830 }, 8),
		GREEN(new int[] { 2952, 100, 3139, 4831 }, 9),
		YELLOW(new int[] { 2953, 101, 3140, 4832 }, 10),
		PURPLE(new int[] { 2954, 102, 3141, 4833 }, 11),
		ORANGE(new int[] { 2955, 103, 3142, 4834 }, 12),
		ROSE(new int[] { 2956, 104, 3143, 4835 }, 13),
		LIME(new int[] { 2957, 105, 3144, 4836 }, 14),
		CYAN(new int[] { 2958, 106, 3145, 4837 }, 15);

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
