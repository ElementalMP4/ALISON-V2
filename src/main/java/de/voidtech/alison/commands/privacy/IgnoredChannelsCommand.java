package main.java.de.voidtech.alison.commands.privacy;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
		if (context.getMessage().getMentionedChannels().isEmpty()) {
			context.reply("You need to mention a channel to remove from the blacklist!");
			return;
		}
		String channelID = context.getMessage().getMentionedChannels().get(0).getId();
		if (PrivacyManager.channelIsIgnored(channelID, context.getGuild().getId())) {
			PrivacyManager.unignoreChannel(channelID, context.getGuild().getId());
			context.reply("Channel <#" + channelID + "> has been blacklisted!");
		} else {
			context.reply("Channel <#" + channelID + "> is not blacklisted!");
		}
	}

	private void addToBlacklist(CommandContext context) {
		if (context.getMessage().getMentionedChannels().isEmpty()) {
			context.reply("You need to mention a channel to add to the blacklist!");
			return;
		}
		String channelID = context.getMessage().getMentionedChannels().get(0).getId();
		if (PrivacyManager.channelIsIgnored(channelID, context.getGuild().getId())) {
			context.reply("Channel <#" + channelID + "> is already blacklisted!");
		} else {
			PrivacyManager.ignoreChannel(channelID, context.getGuild().getId());
			context.reply("Channel <#" + channelID + "> has been blacklisted!");
		}
	}

	private void showIgnoredChannels(CommandContext context) {
		List<String> channels = PrivacyManager.getIgnoredChannelsForServer(context.getGuild().getId());
		String channelList = channels.isEmpty() ? "No channels blacklisted!" :
			channels.stream().map(c -> "<#" + c + ">").collect(Collectors.joining("\n"));
		MessageEmbed blacklistEmbed = new EmbedBuilder()
				.setColor(Color.ORANGE)
				.setTitle("Blacklisted channels in " + context.getGuild().getName())
				.setThumbnail(context.getGuild().getIconUrl())
				.setDescription(channelList)
				.build();
		context.reply(blacklistEmbed);
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
