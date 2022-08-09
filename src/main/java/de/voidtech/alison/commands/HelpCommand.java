package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import main.java.de.voidtech.alison.GlobalConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class HelpCommand extends AbstractCommand {

	private static final List<AbstractCommand> COMMANDS = CommandRegistry.getAllCommands();
	
	@Override
	public void execute(Message message, List<String> args) {
		if (args.size() == 0) showAllCommands(message);
		else {
			AbstractCommand commandOpt = COMMANDS.stream()
					.filter(c -> c.getName().equals(args.get(0)) | c.getShorthand().equals(args.get(0)))
					.findFirst()
					.orElse(null);
			if (commandOpt == null) message.reply("I couldn't find that command :(").mentionRepliedUser(false).queue();
			else showCommandHelp(commandOpt, message);
		}
	}

	private void showAllCommands(Message message) {
		String commandsList = String.join("\n", COMMANDS.stream()
				.map(c -> addFormatting(c.getName()))
				.collect(Collectors.toList()));
		MessageEmbed helpEmbed = new EmbedBuilder()
				.setTitle("ALISON Commands")
				.setColor(Color.ORANGE)
				.setDescription(commandsList)
				.setThumbnail(message.getJDA().getSelfUser().getAvatarUrl())
				.setFooter(GlobalConstants.VERSION, message.getJDA().getSelfUser().getAvatarUrl())
				.build();
		message.replyEmbeds(helpEmbed).mentionRepliedUser(false).queue();
	}

	private String addFormatting(String input) {
		return "```\n" + input + "\n```";
	}
	
	private void showCommandHelp(AbstractCommand command, Message message) {
		MessageEmbed helpEmbed = new EmbedBuilder()
				.setTitle("How to use " + command.getName())
				.setColor(Color.ORANGE)
				.setDescription(addFormatting(command.getDescription()))
				.addField("Usage", addFormatting(command.getUsage()), true)
				.addField("Name", addFormatting(command.getName()), true)
				.addField("Short name", addFormatting(command.getShorthand()), true)
				.setThumbnail(message.getJDA().getSelfUser().getAvatarUrl())
				.setFooter(GlobalConstants.VERSION, message.getJDA().getSelfUser().getAvatarUrl())
				.build();
		message.replyEmbeds(helpEmbed).mentionRepliedUser(false).queue();
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

}
