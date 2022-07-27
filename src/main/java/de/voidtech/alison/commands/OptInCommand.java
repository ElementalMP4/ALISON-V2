package main.java.de.voidtech.alison.commands;

import java.util.List;

import main.java.de.voidtech.alison.utils.PrivacyManager;
import main.java.de.voidtech.alison.utils.Responder;
import net.dv8tion.jda.api.entities.Message;

public class OptInCommand extends AbstractCommand {

	@Override
	public void execute(Message message, List<String> args) {
		String ID = message.getAuthor().getId();
		if (PrivacyManager.UserHasOptedOut(ID)) {
			Responder.SendAsReply(message, "You have been re-opted in to the learning program! I will learn from your messages again!");
			PrivacyManager.OptIn(ID);
		} else Responder.SendAsReply(message, "You have already opted in to the learning program! (Users are opted in by default!)");
	}

	@Override
	public String getName() {
		return "optin";
	}

	@Override
	public String getUsage() {
		return "optin";
	}

	@Override
	public String getDescription() {
		return "Allows ALISON to learn from your messages. By default, you will be opted in."
				+ " You can use the optout command to stop ALISON from learning from you, and the clear command to delete all your learnt words.";
	}

	@Override
	public String getShorthand() {
		return "in";
	}

	@Override
	public boolean isDmCapable() {
		return true;
	}

	@Override
	public boolean requiresArguments() {
		return false;
	}	
}