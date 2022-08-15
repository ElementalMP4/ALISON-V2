package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;

import main.java.de.voidtech.alison.entities.CommandContext;
import main.java.de.voidtech.alison.entities.Sentiment;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import main.java.de.voidtech.alison.utils.TextAnalytics;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

public class HowToxicAmICommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		if (args.isEmpty()) analyse(context.getAuthor(), context);
    	else {
    		String userID = args.get(0).replaceAll("([^0-9])", "");
    		if (userID.equals("")) {
                context.reply("I couldn't find that user :(");
                return;
            }
            
            Result<User> userResult = context.getJDA().retrieveUserById(userID).mapToResult().complete();
            if (userResult.isSuccess()) analyse(userResult.get(), context);
            else context.reply("I couldn't find that user :(");
    	}
	}
	
	private void analyse(User user, CommandContext context) {
		if (PrivacyManager.userHasOptedOut(user.getId())) {
			context.reply("This user has chosen not to be analysed!");
			return;
		}
		Sentiment howToxic = TextAnalytics.analysePack(user.getId());
		if (howToxic == null) {
			context.reply("I couldn't find any data to analyse!");
			return;
		}
		MessageEmbed toxicityEmbed = new EmbedBuilder()
				.setColor(getColour(howToxic))
				.setTitle("How toxic is " + user.getName() + "?")
				.setDescription("I searched `" + howToxic.getTotalWordCount() + "` words. From this, I found `" + howToxic.getTokenCount() + "` words with meaning.")
				.addField("Positive words found", "```\n" + howToxic.getPositiveCount() + "\n```", true)
				.addField("Negative words found",  "```\n" + howToxic.getNegativeCount() + "\n```", true)
				.addField("Total Score (higher is better!)",  "```\n" + howToxic.getScore() + "\n```", true)
				.addField("Average Score (higher is better!)",  "```\n" + howToxic.getAverageScore() + "\n```", true)
				.addField("Adjusted Score (higher is better!)",  "```\n" + howToxic.getAdjustedScore() + "\n```", true)
				.setFooter(getMessage(howToxic))
				.build();
		context.reply(toxicityEmbed);
	}

	private String getMessage(Sentiment howToxic) {
		return howToxic.getAdjustedScore() < -2 ? "You are a right asshole, you should be nicer >:("
				: howToxic.getAdjustedScore() < 2 ? "You're an alright person, but could be better." 
				: "Everyone loves you! You say all the nicest things!";
	}

	private Color getColour(Sentiment howToxic) {
		return howToxic.getAdjustedScore() < -2 ? Color.RED 
				: howToxic.getAdjustedScore() < 2 ? Color.ORANGE
				: Color.GREEN;
	}

	@Override
	public String getName() {
		return "howtoxicami";
	}

	@Override
	public String getUsage() {
		return "howtoxicami";
	}

	@Override
	public String getDescription() {
		return "Using everything I know about you, I will determine how toxic you are!"
				+ " Optionally, you can use another member's ID or mention to see their sentiment!";
	}

	@Override
	public String getShorthand() {
		return "htme";
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
		return CommandCategory.SENTIMENT_ANALYSIS;
	}
}