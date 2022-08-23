package main.java.de.voidtech.alison.commands.privacy;

import java.util.List;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import net.dv8tion.jda.api.Permission;

public class IgnoredChannelsCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		
		if (!context.getMember().hasPermission(Permission.MANAGE_SERVER)) {
			context.reply("You need the `Manage Server` permission to use this command!");
			return;
		}
		
		String command = !args.isEmpty() ? args.get(0) : "list";		
		
		switch (command) {
		case "list":
			showIgnoredChannels(context);
			break;
		case "add":
			addToBlacklist(context);
			break;
		case "remove":
			removeFromBlacklist(context);
			break;
		default:
			context.reply("You need to specify one of these subcommands:\n" + this.getUsage());
				
		}
	}

	private void removeFromBlacklist(CommandContext context) {
		// TODO Auto-generated method stub
		
	}

	private void addToBlacklist(CommandContext context) {
		// TODO Auto-generated method stub
		
	}

	private void showIgnoredChannels(CommandContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "ignoredchannels";
	}

	@Override
	public String getUsage() {
		return "ignoredchannels add [channel mention]"
				+ "ignoredchannels remove [channel mention]"
				+ "ignoredchannels remove all"
				+ "ignoredchannels list";
	}

	@Override
	public String getDescription() {
		return "Allows a list of channels to be created which will be ignored by Alison."
				+ " Alison will not respond to commands and she will not learn from any messages sent in these channels.";
	}

	@Override
	public String getShorthand() {
		return "ic";
	}

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.PRIVACY;
	}

	@Override
	public boolean isDmCapable() {
		return false;
	}

	@Override
	public boolean requiresArguments() {
		return false;
	}

}
