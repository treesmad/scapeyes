package org.rs2.model.content;

public class GenderInterface {
	public enum Skins {
		SKIN_1(5556, 7),
		SKIN_2(5549, 0),
		SKIN_3(5550, 1),
		SKIN_4(5551, 2),
		SKIN_5(5552, 3),
		SKIN_6(5553, 4),
		SKIN_7(5554, 5),
		SKIN_8(5555, 6);

		private int button;
		private int skin;

		Skins(int button, int skin) {
			this.button = button;
			this.skin = skin;
		}

		public int getButton() {
			return button;
		}

		public int getSkin() {
			return skin;
		}
	}
}
