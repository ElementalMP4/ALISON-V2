package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;

import main.java.de.voidtech.alison.GlobalConstants;
import main.java.de.voidtech.alison.entities.CommandContext;
import main.java.de.voidtech.alison.utils.ModelManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class InfoCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		long guildCount = context.getJDA().getGuildCache().size();
		long memberCount = context.getJDA().getGuildCache().stream().mapToInt(Guild::getMemberCount).sum();
		long wordCount = ModelManager.getWordCount();
		long modelCount = ModelManager.getModelCount();
		
		MessageEmbed informationEmbed = new EmbedBuilder()
				.setColor(Color.ORANGE)
				.setTitle("ALISON - Automatic Learning Intelligent Sentence Organising Network", GlobalConstants.REPO_URL)
				.addField("Guild Count", "```" + guildCount + "```", true)
				.addField("Member Count", "```" + memberCount + "```", true)
				.addField("Active Threads", "```" + Thread.activeCount() + "```", true)
				.addField("Total Word Count", "```" + wordCount + "```", true)
				.addField("Model Count", "```" + modelCount + "```", true)
				.setDescription("**Important Privacy Notice**\n"
						+ "Data collected by ALISON is only available whilst you are opted in to the data collection program."
						+ " To stop data collection, use the optout command."
						+ " Once you are opted out, you can still use ALISON, but messages you send will not be processed or persisted.")
				.setThumbnail(context.getJDA().getSelfUser().getAvatarUrl())
				.setFooter("Use the help command to see what else I can do!\nVersion: " + GlobalConstants.VERSION,
						context.getJDA().getSelfUser().getAvatarUrl())
				.build();
		context.reply(informationEmbed);
	}

	@Override
	public String getDescription() {
		return "Provides information about ALISON";
	}

	@Override
	public String getUsage() {
		return "info";
	}

	@Override
	public String getName() {
		return "info";
	}

	@Override
	public String getShorthand() {
		return "stats";
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
