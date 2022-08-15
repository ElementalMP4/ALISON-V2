package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import main.java.de.voidtech.alison.GlobalConstants;
import main.java.de.voidtech.alison.entities.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class HelpCommand extends AbstractCommand {

	private static final List<AbstractCommand> COMMANDS = CommandRegistry.getAllCommands();
	
	@Override
	public void execute(CommandContext context, List<String> args) {
		if (args.size() == 0) showAllCommands(context);
		else {
			AbstractCommand commandOpt = COMMANDS.stream()
					.filter(c -> c.getName().equals(args.get(0)) | c.getShorthand().equals(args.get(0)))
					.findFirst()
					.orElse(null);
			if (commandOpt == null) context.reply("I couldn't find that command :(");
			else showCommandHelp(commandOpt, context);
		}
	}

	private void showAllCommands(CommandContext context) {
		String commandsList = String.join("\n", COMMANDS.stream()
				.map(c -> addFormatting(c.getName()))
				.collect(Collectors.toList()));
		MessageEmbed helpEmbed = new EmbedBuilder()
				.setTitle("ALISON Commands")
				.setColor(Color.ORANGE)
				.setDescription(commandsList)
				.setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
				.setFooter(GlobalConstants.VERSION, context.getJDA().getSelfUser().getAvatarUrl())
				.build();
		context.reply(helpEmbed);
	}
	private String addFormatting(String input) {
		return "```\n" + input + "\n```";
	}
	
	private void showCommandHelp(AbstractCommand command, CommandContext context) {
		MessageEmbed helpEmbed = new EmbedBuilder()
				.setTitle("How to use " + command.getName())
				.setColor(Color.ORANGE)
				.setDescription(addFormatting(command.getDescription()))
				.addField("Usage", addFormatting(command.getUsage()), true)
				.addField("Name", addFormatting(command.getName()), true)
				.addField("Short name", addFormatting(command.getShorthand()), true)
				.addField("Can be used in DMs?", booleanToEmote(command.isDmCapable()), true)
				.addField("Requires arguments?", booleanToEmote(command.requiresArguments()), true)
				.addField("Category", command.getCommandCategory().getIcon(), true)
				.setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
				.setFooter(GlobalConstants.VERSION, context.getJDA().getSelfUser().getAvatarUrl())
				.build();
		context.reply(helpEmbed);
	}

	private String booleanToEmote(boolean b) {
		return addFormatting(b ? "✅" : "❌");
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getUsage() {
		return "help [command]";
	}

	@Override
	public String getDescription() {
		return "Shows you how to use all the commands!";
	}

	@Override
	public String getShorthand() {
		return "h";
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
		return CommandCategory.INFORMATION;
	}

}
