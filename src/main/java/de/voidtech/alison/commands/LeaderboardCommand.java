package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import main.java.de.voidtech.alison.entities.CommandContext;
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
				.setTitle("Tosicity leaderboard for " + context.getGuild().getName())
				.setDescription(leaderboard)
				.build();
		context.reply(leaderboardEmbed);
	}
	
	private String createLeaderboardString(List<Sentiment> allMembers) {
		String leaderboard = "**Top 5 members**\n";
		List<Sentiment> topFive = allMembers.subList(0, allMembers.size() < 5 ? allMembers.size() - 1 : 4);
		Collections.reverse(allMembers);
		List<Sentiment> bottomFive = allMembers.subList(0, allMembers.size() < 5 ? allMembers.size() - 1 : 4);
		Collections.reverse(allMembers);
		
		for (Sentiment sentiment : topFive) {
			leaderboard += intToEmojiString(allMembers.indexOf(sentiment) + 1);
			leaderboard += "<@" + sentiment.getPack() + "> - `";
			leaderboard += sentiment.getAdjustedScore() + "`\n";
		}
		leaderboard += "**Bottom 5 Members**\n";
		for (Sentiment sentiment : bottomFive) {
			leaderboard += intToEmojiString(allMembers.indexOf(sentiment) + 1);
			leaderboard += "<@" + sentiment.getPack() + "> - `";
			leaderboard += sentiment.getAdjustedScore() + "`\n";
		}
		
		return leaderboard;
	}

	public static String intToEmojiString(int position) {
		String digits = String.valueOf(position);
		String result = "";
		for (String digit : digits.split("")) {
			switch (digit) {
				case "0":
					result += ":zero:";
					break;
				case "1":
					result += ":one:";
					break;
				case "2":
					result += ":two:";
					break;
				case "3":
					result += ":three:";
					break;
				case "4":
					result += ":four:";
					break;
				case "5":
					result += ":five:";
					break;
				case "6":
					result += ":six:";
					break;
				case "7":
					result += ":seven:";
					break;
				case "8":
					result += ":eight:";
					break;
				case "9":
					result += ":nine:";
					break;
				case "10":
					result += ":ten:";
					break;
				default:
					result += ":zero:";
					break;
			}
		}
		return result;
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
