package main.java.de.voidtech.alison.service;

import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.Sentiment;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.*;
import java.util.stream.Collectors;

public class TextAnalytics {
	private static final List<String> POSITIVE_EMOTES = Arrays.asList("❤", "🥰", "😘", "😄");
	private static final List<String> NEGATIVE_EMOTES = Arrays.asList("💔", "😔", "😭", "😢");

	public static Sentiment analysePack(String pack) {
		if (!ModelManager.modelExists(pack)) return null;
		AlisonModel model = ModelManager.getModel(pack);
		String words = String.join(" ", model.getAllWords());
		Sentiment sentiment = analyseSentence(words);
		sentiment.setPack(pack);
		return sentiment;
	}

	public static Sentiment analyseSentence(String sentence) {
		return new Sentiment(sentence);
	}

	public static List<Sentiment> analyseServer(Guild guild) {
		List<Member> members = guild.loadMembers().get();
		List<Sentiment> sentiments = members.stream()
				.map(Member::getId)
				.filter(memberID -> !PrivacyManager.userHasOptedOut(memberID))
				.filter(ModelManager::modelExists)
				.map(TextAnalytics::analysePack)
				.sorted(Comparator.comparing(sentiment -> sentiment != null ? sentiment.getAdjustedScore() : 0))
				.collect(Collectors.toList());
		Collections.reverse(sentiments);
		return sentiments;
	}
	
	public static Sentiment averageSentiment(List<Sentiment> sentiments) {
		List<String> positives = sentiments.stream()
				.map(Sentiment::getPositives)
				.flatMap(List::stream)
		        .collect(Collectors.toList());
		List<String> negatives = sentiments.stream()
				.map(Sentiment::getNegatives)
				.flatMap(List::stream)
		        .collect(Collectors.toList());
		String original = sentiments.stream()
				.map(Sentiment::getOriginalString)
				.collect(Collectors.joining(" "));
		return new Sentiment(positives, negatives, original);
	}
	
	private static String getRandomEmote(List<String> emotes) {
        return emotes.get(new Random().nextInt(emotes.size()));
    }
	
	public static void respondToAlisonMention(Message message) {
		if (!message.getContentRaw().toLowerCase().contains("alison")) return;
		Sentiment sentiment = TextAnalytics.analyseSentence(message.getContentRaw().toLowerCase());
		if (sentiment.getAdjustedScore() >= 3) message.addReaction(getRandomEmote(POSITIVE_EMOTES)).queue();
		else if (sentiment.getAdjustedScore() <= -3) message.addReaction(getRandomEmote(NEGATIVE_EMOTES)).queue();
	}
}
