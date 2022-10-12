package main.java.de.voidtech.alison.commands.information;

import main.java.de.voidtech.alison.GlobalConstants;
import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.commands.CommandRegistry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class HelpCommand extends AbstractCommand {

	private static final List<AbstractCommand> COMMANDS = CommandRegistry.getAllCommands();
	
	private static final String TRUE_EMOTE = "\u2705";
	private static final String FALSE_EMOTE = "\u274C";
	
	private String capitaliseFirstLetter(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1);
	}
	
	private void showCategoryList(CommandContext command) {

		int fieldCounter = 1;
		
		EmbedBuilder categoryListEmbedBuilder = new EmbedBuilder()
				.setColor(Color.ORANGE)
				.setDescription("**Important Privacy Notice**\n"
						+ "Data collected by ALISON is only available whilst you are opted in to the data collection program."
						+ " To stop data collection, use the optout command."
						+ " Once you are opted out, you can still use ALISON, but messages you send will not be processed or persisted.")
				.setTitle("Alison's Lovely Commands")
				.setThumbnail(command.getJDA().getSelfUser().getAvatarUrl())
				.setFooter("Alison Version " + GlobalConstants.VERSION + "\nCommand Count: " + COMMANDS.size(),
						command.getJDA().getSelfUser().getAvatarUrl());
		
		for (CommandCategory commandCategory : CommandCategory.values()) {
			String title = capitaliseFirstLetter(commandCategory.getCategory()) + " " + commandCategory.getIcon();
			String description = "```\nhelp " + commandCategory.getCategory() + "\n```";
			categoryListEmbedBuilder.addField(title, description, true);
			fieldCounter++;
			if (fieldCounter == 3) {
				fieldCounter = 1;
			}
		}
		
		categoryListEmbedBuilder.addField("Any Command :clipboard: ", "```\nhelp [command]\n```", true);	
		command.reply(categoryListEmbedBuilder.build());
	}
	
	private boolean isCommandCategory(String categoryName) {
		for (CommandCategory commandCategory : CommandCategory.values()) {
			if (commandCategory.getCategory().equals(categoryName))
				return true;
		}
		return false;
	}
	
	
	private String getCategoryIconByName(String name) {
		for (CommandCategory commandCategory : CommandCategory.values()) {
			if (commandCategory.getCategory().equals(name))
				return commandCategory.getIcon();
		}
		return "";
	}
	
	private boolean isCommand(String commandName) {
		return getCommand(commandName) != null;
	}
	
	private void showCommandsFromCategory(CommandContext context, String categoryName) {
        StringBuilder commandListBuilder = new StringBuilder();
        for (AbstractCommand command : COMMANDS) {
			if(command.getCommandCategory().getCategory().equals(categoryName))
				commandListBuilder.append("`").append(command.getName()).append("`, ");
		}
        String commandList = commandListBuilder.toString();
        commandList = commandList.substring(0, commandList.length() - 2);
		
		MessageEmbed commandHelpEmbed = new EmbedBuilder()
				.setColor(Color.ORANGE)
				.setTitle(capitaliseFirstLetter(categoryName) + " Help")
				.addField(capitaliseFirstLetter(categoryName) + " Commands " + getCategoryIconByName(categoryName), commandList, false)
				.setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
				.setFooter("Alison Version " + GlobalConstants.VERSION + "\nCommand Count: " + COMMANDS.size(),
						context.getJDA().getSelfUser().getAvatarUrl())
				.build();
		context.reply(commandHelpEmbed);
	}

	private String displayCommandCategoryOrNull(CommandCategory category) {
		return category == null ? "No Category" : capitaliseFirstLetter(category.getCategory());
	}
	
	private AbstractCommand getCommand(String name) {
		return COMMANDS.stream()
				.filter(c -> c.getName().equals(name) | c.getShorthand().equals(name))
				.findFirst().orElse(null);
	}
	
	private void showCommand(CommandContext context, String commandName) {
		AbstractCommand commandToBeDisplayed = getCommand(commandName);	

		MessageEmbed commandHelpEmbed = new EmbedBuilder()
				.setColor(Color.ORANGE)
				.setTitle("How it works: " + capitaliseFirstLetter(commandToBeDisplayed.getName()) + " Command")
				.setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
				.addField("Command Name", "```" + capitaliseFirstLetter(commandToBeDisplayed.getName()) + "```", true)
				.addField("Category", "```" + displayCommandCategoryOrNull(commandToBeDisplayed.getCommandCategory()) + "```", true)
				.addField("Description", "```" + commandToBeDisplayed.getDescription() + "```", false)
				.addField("Usage", "```" + commandToBeDisplayed.getUsage() + "```", false)
				.addField("Requires Arguments", "```" + booleanToEmote(commandToBeDisplayed.requiresArguments()) + "```", true)
				.addField("Is DM Capable", "```" + booleanToEmote(commandToBeDisplayed.isDmCapable()) + "```", true)
				.addField("Shorthand Name", "```" + commandToBeDisplayed.getShorthand() + "```", false)
				.setFooter("Alison Version " + GlobalConstants.VERSION + "\nCommand Count: " + COMMANDS.size(),
						context.getJDA().getSelfUser().getAvatarUrl())
				.build();
		context.reply(commandHelpEmbed);
	}

	private String booleanToEmote(boolean option) {
		return option ? TRUE_EMOTE : FALSE_EMOTE;
	}

	@Override
	public void execute(CommandContext context, List<String> args) {
		if (args.size() == 0) showCategoryList(context);
		else {
			String itemToBeQueried = args.get(0).toLowerCase();
			if (isCommandCategory(itemToBeQueried)) showCommandsFromCategory(context, itemToBeQueried);
			else if (isCommand(itemToBeQueried)) showCommand(context, itemToBeQueried);
			else context.reply("**That command/category could not be found!**");
		}
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

	@Override
	public boolean isLongCommand() {
		return false;
	}

}