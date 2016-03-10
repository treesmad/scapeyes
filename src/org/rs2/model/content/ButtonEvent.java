package org.rs2.model.content;

public abstract class ButtonEvent {
	private int id;

	public ButtonEvent() {
	}

	public ButtonEvent(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	abstract void action();
}