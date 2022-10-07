package main.java.de.voidtech.alison.commands.privacy;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.utils.PrivacyManager;

import java.util.List;

public class OptInCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		String ID = context.getAuthor().getId();
		if (PrivacyManager.userHasOptedOut(ID)) {
			context.reply("You have been re-opted in to the learning program! I will learn from your messages again!");
			PrivacyManager.optIn(ID);
		} else context.reply("You have already opted in to the learning program! (Users are opted in by default!)");
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

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.PRIVACY;
	}

	@Override
	public boolean isLongCommand() {
		return false;
	}
}