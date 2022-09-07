package main.java.de.voidtech.alison.commands.sentiment;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.entities.Sentiment;
import main.java.de.voidtech.alison.utils.TextAnalytics;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LeaderboardCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		List<Sentiment> allMembers = TextAnalytics.analyseServer(context.getGuild());
		String leaderboard = createLeaderboardString(allMembers);
		MessageEmbed leaderboardEmbed = new EmbedBuilder()
				.setColor(Color.ORANGE)
				.setTitle("Toxicity leaderboard for " + context.getGuild().getName())
				.setDescription(leaderboard)
				.build();
		context.reply(leaderboardEmbed);
	}
	
	private String createLeaderboardString(List<Sentiment> allMembers) {
		StringBuilder leaderboard = new StringBuilder("**Top 5 members**\n");
		List<Sentiment> topFive = allMembers.stream().limit(5).collect(Collectors.toList());
		Collections.reverse(allMembers);
		List<Sentiment> bottomFive = allMembers.stream().limit(5).collect(Collectors.toList());
		Collections.reverse(allMembers);
		Collections.reverse(bottomFive);
		
		for (Sentiment sentiment : topFive) {
			leaderboard.append(intToEmojiString(allMembers.indexOf(sentiment) + 1));
			leaderboard.append("<@").append(sentiment.getPack()).append("> - `");
			leaderboard.append(sentiment.getAdjustedScore()).append("`\n");
		}
		leaderboard.append("**Bottom 5 Members**\n");
		for (Sentiment sentiment : bottomFive) {
			leaderboard.append(intToEmojiString(allMembers.indexOf(sentiment) + 1));
			leaderboard.append("<@").append(sentiment.getPack()).append("> - `");
			leaderboard.append(sentiment.getAdjustedScore()).append("`\n");
		}
		
		return leaderboard.toString();
	}

	public static String intToEmojiString(int position) {
		String digits = String.valueOf(position);
		StringBuilder result = new StringBuilder();
		for (String digit : digits.split("")) {
			switch (digit) {
				case "1":
					result.append(":one:");
					break;
				case "2":
					result.append(":two:");
					break;
				case "3":
					result.append(":three:");
					break;
				case "4":
					result.append(":four:");
					break;
				case "5":
					result.append(":five:");
					break;
				case "6":
					result.append(":six:");
					break;
				case "7":
					result.append(":seven:");
					break;
				case "8":
					result.append(":eight:");
					break;
				case "9":
					result.append(":nine:");
					break;
				case "10":
					result.append(":ten:");
					break;
				default:
					result.append(":zero:");
					break;
			}
		}
		return result.toString();
	}

	@Override
	public String getName() {
		return "leaderboard";
	}

	@Override
	public String getUsage() {
		return "leaderboard";
	}

	@Override
	public String getDescription() {
		return "Allows you to see the top 5 and bottom 5 members of your server ranked by sentiment score!";
	}

	@Override
	public String getShorthand() {
		return "lb";
	}

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.SENTIMENT_ANALYSIS;
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
