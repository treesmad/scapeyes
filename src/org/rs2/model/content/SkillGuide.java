package org.rs2.model.content;

import java.io.FileReader;
import org.rs2.model.items.Item;
import org.rs2.model.players.Player;
import com.google.gson.stream.JsonReader;

public class SkillGuide {
	/**
	 * Player this class belongs to.
	 */
	private Player p;
	/**
	 * Id of the currently open guide.
	 */
	public int guideId = -1;
	/**
	 * Id of the last guide that was open.
	 */
	public int lastGuideId = -1;
	/**
	 * Id of the currently open page.
	 */
	public int pageId = -1;
	/**
	 * Last time a page or guide id was changed.
	 */
	public long lastUpdate;

	/**
	 * Maximum number of pages.
	 */
	private static final int MAX_PAGES = 13;
	/**
	 * Maximum number of entries on the skillguide
	 * interface.
	 */
	private static final int MAX_ENTRIES = 40;

	/**
	 * Array containing all the skill guides and pages.
	 */
	public static Guide[][] guide = new Guide[21][MAX_PAGES];
	/**
	 * Names of each skill guide.
	 */
	private static String[] guideName = new String[21];

	static class Guide {
		private String title = "untitled";
		private boolean members = false;
		private int[] level = new int[MAX_ENTRIES];
		private Item[] item = new Item[MAX_ENTRIES];
		private String[] desc = new String[MAX_ENTRIES];
	}

	/**
	 * Construct a new SkillGuide
	 * @param p Player to assign this class to.
	 */
	public SkillGuide(Player p) {
		this.p = p;
	}

	/**
	 * Handles button clicking.
	 * @param buttonId
	 */
	public void handleButton(int buttonId) {
		switch (buttonId) {

			case 8654:	// Attack
				open(0, 0);
				break;

			case 8660:	// Defence
				open(1, 0);
				break;

			case 8657:	// Strength
				open(2, 0);
				break;

			case 8655:	// Hitpoints
				open(3, 0);
				break;

			case 8669:
				open(6, 0);	// Magic
				break;

			case 8672: // Runecrafting
				open(20, 0);
				break;

			case 12162:	// Slayer
				open(18, 0);
				break;

			/**
			 * Skillguide pages
			 */
			case 8846:
				setPage(0);
				break;

			case 8823:
				setPage(1);
				break;

			case 8824:
				setPage(2);
				break;

			case 8827:
				setPage(3);
				break;
		}
	}

	public String[] getTitles(int id) {
		String[] titles = new String[guide[id].length];

		for (int i = 0; i < guide[id].length; i++) {
			if (guide[id][i] == null) {
				continue;
			}
			titles[i] = guide[id][i].title;
		}
		return titles;
	}

	public void open(int guideId, int page) {
		if (lastGuideId == guideId) {
			p.frames.sendInterface(8714);
			return;
		}
		if (System.currentTimeMillis() - lastUpdate < 500) {
			return;
		}
		if (this.guideId == guideId && pageId == page) {
			return;
		}
		p.frames.setString(guideName[guideId], 8716);
		setButtons(getTitles(guideId));
		setPage(guideId, page);

		if (this.guideId == -1) {
			p.frames.sendInterface(8714);
		}
		this.guideId = guideId;
		lastGuideId = guideId;
	}

	public void setPage(int guideId, int page) {
		if (System.currentTimeMillis() - lastUpdate < 500) {
			return;
		}
		if (pageId == page) {
			return;
		}
		p.frames.setString(guide[guideId][page].members ? "Members only" : "", 8849);
		p.frames.setScrollbarPosition(8717, 0);
		p.frames.setItems(8847, guide[guideId][page].item);

		for (int i = 0; i < MAX_ENTRIES; i++) {
			if (guide[guideId][page].level[i] != 0) {
				p.frames.setString(guide[guideId][page].level[i], 8720 + i);
			} else {
				p.frames.setString("", 8720 + i);
			}
			if (guide[guideId][page].desc[i] != null) {
				p.frames.setString(guide[guideId][page].desc[i], 8760 + i);
			} else {
				p.frames.setString("", 8760 + i);
			}
		}
		lastUpdate = System.currentTimeMillis();
	}

	public void setPage(int page) {
		setPage(guideId, page);
	}

	public void setButtons(String... names) {
		int[] buttonNames = { 8846, 8823, 8824, 8827, 8837, 8840, 8843, 8859, 8862, 8865, 15303,
			15306, 15309 };
		int[] buttons = { 15307, 15304, 15294, 8863, 8860, 8850, 8841, 8838, 8828, 8825, 8813,
			8844, 8800 };

		/**
		 * FIXME: Calculate number of shown buttons, set
		 * button text, and hide all buttons when there is
		 * only one page.
		 */
		/*int numButtons = 0;

		for (int i = 0; i < names.length; i++) {
			if (names[i] == null) {
				continue;
			}
			numButtons++;
		}
		for (int i = 0; i < buttons.length; i++) {
			if (i < 13 - numButtons) {
				if (names[i] != null) {
					p.frames.setString(names[i], buttonNames[i]);
				} else {
					p.frames.setString("", buttonNames[i]);
				}
				p.frames.hideChild(buttons[i], 1);
			} else {
				p.frames.hideChild(buttons[i], 0);
			}
		}*/
	}

	public static void load() {
		JsonReader reader;

		int id = 0;
		int pageId = 0;

		try {
			reader = new JsonReader(new FileReader("./data/skillguides.json"));
			reader.beginObject();
			String name = reader.nextName();

			if (name.equals("guides")) {
				reader.beginArray();

				while (reader.hasNext()) {
					reader.beginObject();

					while (reader.hasNext()) {
						name = reader.nextName();

						if (name.equals("id")) {
							id = reader.nextInt();
						} else if (name.equals("name")) {
							guideName[id] = reader.nextString();

						} else if (name.equals("pages")) {
							reader.beginArray();

							while (reader.hasNext()) {
								reader.beginObject();

								if (guide[id][pageId] == null) {
									guide[id][pageId] = new Guide();
								}
								while (reader.hasNext()) {
									name = reader.nextName();

									if (name.equals("title")) {
										guide[id][pageId].title = reader.nextString();

									} else if (name.equals("members")) {
										guide[id][pageId].members = reader.nextBoolean();

									} else if (name.equals("entries")) {
										reader.beginArray();

										int entry = 0;
										while (reader.hasNext()) {
											reader.beginObject();

											while (reader.hasNext()) {
												name = reader.nextName();

												if (name.equals("level")) {
													guide[id][pageId].level[entry] = reader
														.nextInt();

												} else if (name.equals("item")) {
													guide[id][pageId].item[entry] = new Item(
														reader.nextInt());

												} else if (name.equals("desc")) {
													guide[id][pageId].desc[entry] = reader
														.nextString();

												} else {
													reader.skipValue();
												}
											}
											entry++;
											reader.endObject();
										}
										entry = 0;
										reader.endArray();

									} else {
										reader.skipValue();
									}
								}
								pageId++;
								reader.endObject();
							}
							pageId = 0;
							reader.endArray();
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				}
				reader.endArray();
			}
			reader.endObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}