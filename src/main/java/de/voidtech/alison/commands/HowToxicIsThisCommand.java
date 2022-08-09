package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;

import main.java.de.voidtech.alison.entities.Sentiment;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import main.java.de.voidtech.alison.utils.Responder;
import main.java.de.voidtech.alison.utils.TextAnalytics;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class HowToxicIsThisCommand extends AbstractCommand {

	@Override
	public void execute(Message message, List<String> args) {
		if (args.isEmpty()) {
			if (message.getReferencedMessage() == null) {
				message.reply("You need to give me a message to analyse! Either reply to someone else's message or provide me with some text!")
				.mentionRepliedUser(false).queue();
				return;
			} else {
				if (PrivacyManager.UserHasOptedOut(message.getReferencedMessage().getAuthor().getId())) {
					Responder.SendAsReply(message, "This user has chosen not to be analysed!");
					return;
				}
				analyse(message.getReferencedMessage().getContentRaw(), message);
			}
		} else analyse(String.join(" ", args), message);
	}
	
	private void analyse(String text, Message message) {
		Sentiment howToxic = TextAnalytics.AnalyseSentence(text);
		MessageEmbed toxicityEmbed = new EmbedBuilder()
				.setColor(getColour(howToxic))
				.setTitle("Message Analysis")
				.setDescription("I searched `" + howToxic.getTotalWordCount() + "` words. From this, I found `" + howToxic.getTokenCount() + "` words with meaning.")
				.addField("Positive words found", "```\n" + howToxic.getPositiveCount() + "\n```", true)
				.addField("Negative words found",  "```\n" + howToxic.getNegativeCount() + "\n```", true)
				.addField("Total Score (higher is better!)",  "```\n" + howToxic.getScore() + "\n```", true)
				.addField("Average Score (higher is better!)",  "```\n" + howToxic.getAverageScore() + "\n```", true)
				.addField("Adjusted Score (higher is better!)",  "```\n" + howToxic.getAdjustedScore() + "\n```", true)
				.build();
		Responder.SendAsReply(message, toxicityEmbed);
	}
	
	private Color getColour(Sentiment howToxic) {
		return howToxic.getAdjustedScore() < -2 ? Color.RED 
				: howToxic.getAdjustedScore() < 2 ? Color.ORANGE
				: Color.GREEN;
	}

	@Override
	public String getName() {
		return "howtoxicisthis";
	}

	@Override
	public String getUsage() {
		return "howtoxicisthis [message]";
	}

	@Override
	public String getDescription() {
		return "Tell me something interesting and I'll tell you how nasty or nice it is!";
	}

	@Override
	public String getShorthand() {
		return "htthis";
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
