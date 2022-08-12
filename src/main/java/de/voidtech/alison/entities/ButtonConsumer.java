package main.java.de.voidtech.alison.entities;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class ButtonConsumer {
	
	public static final String TRUE_EMOTE = "\u2705";
	public static final String FALSE_EMOTE = "\u274C";
	
	private Message message;
	private ButtonClickEvent button;
	
	public ButtonConsumer(ButtonClickEvent button, Message message) {
		this.message = message;
		this.button = button;
	}
	
	public Message getMessage() {
		return this.message;
	}
	
	public ButtonClickEvent getButton() {
		return this.button;
	}
}
