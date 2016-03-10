package org.rs2.model.content;

import java.util.ArrayList;

public class DialogueOption {
	/**
	 * Buttons this options menu contains.
	 */
	public ArrayList<DialogueButton> buttons = new ArrayList<DialogueButton>(5);

	/**
	 * Add a button to this menu.
	 * @param button Button to add.
	 */
	public DialogueOption add(DialogueButton button) {
		buttons.add(button);
		return this;
	}

	/**
	 * Returns the number of buttons on the options menu.
	 */
	public int size() {
		return buttons.size();
	}

	/**
	 * Returns the button object at the specified index.
	 * @param option Index to get object from.
	 */
	public DialogueButton get(int option) {
		return buttons.get(option);
	}
	
	/**
	 * Returns the label at the specified button index.
	 * @param DialogueButton index to get the label from.
	 */
	public String getLabel(int option) {
		return get(option).getLabel();
	}
	
	/**
	 * Button objects for the options menu to display and manage.
	 */
	public static class DialogueButton {
		/**
		 * Label of this button.
		 */
		private String label;
		/**
		 * Set to true if this button closes the options
		 * interface when clicked.
		 */
		private boolean autoClose;

		/**
		 * Construct a new DialogueButton.
		 * @param label Text to display on button.
		 */
		public DialogueButton(String label) {
			this.label = label;
		}

		/**
		 * Construct a new DialogueButton.
		 * @param label Text to display on button.
		 * @param autoClose Closes all open interfaces when this
		 *            button is clicked.
		 */
		public DialogueButton(String label, boolean autoClose) {
			this.label = label;
			this.autoClose = autoClose;
		}

		/**
		 * Returns the label for this button.
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * Returns whether or not the button will close the
		 * options interface when clicked.
		 */
		public boolean isAutoClose() {
			return autoClose;
		}

		/**
		 * Called when the button is clicked.
		 */
		public void action() {
		}

	}

}