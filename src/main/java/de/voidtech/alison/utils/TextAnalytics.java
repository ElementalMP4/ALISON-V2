package main.java.de.voidtech.alison.utils;

import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.ResourceLoader;
import main.java.de.voidtech.alison.entities.Sentiment;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TextAnalytics {
	
	private static final List<String> NegativeWords;
	private static final List<String> PositiveWords;
	
	private static final List<String> POSITIVE_EMOTES = Arrays.asList("❤", "🥰", "😘", "😄");
	private static final List<String> NEGATIVE_EMOTES = Arrays.asList("💔", "😔", "😭", "😢");

	static {
		ResourceLoader loader = new ResourceLoader();
		NegativeWords = loader.getResource("negative-words.txt");
		PositiveWords = loader.getResource("positive-words.txt");
	}
	
	public static Sentiment analysePack(String pack) {
		if (!ModelManager.modelExists(pack)) return null;
		AlisonModel model = ModelManager.getModel(pack);
		String words = String.join(" ", model.getAllWords());
		Sentiment sentiment = analyseSentence(words);
		sentiment.setPack(pack);
		return sentiment;
	}

	public static Sentiment analyseSentence(String sentence) {
		String tokenizedSentence = sentence.replaceAll("([^a-zA-Z\\d\\s:])", "").toLowerCase();
		List<String> negativeTokens = findTokens(NegativeWords, tokenizedSentence);
		List<String> positiveTokens = findTokens(PositiveWords, tokenizedSentence);
		
		return new Sentiment(positiveTokens, negativeTokens, sentence);
	}

	private static List<String> findTokens(List<String> wordList, String sentence) {
		List<String> results = new ArrayList<>();
		 wordList.forEach(token -> {
			 for (int i = 0; i < StringUtils.countMatches(sentence, token); i++) {
				 results.add(token);
			 }
		 });
		return results;
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
		if (sentiment.getAdjustedScore() >= 2) message.addReaction(getRandomEmote(POSITIVE_EMOTES)).queue();
		else if (sentiment.getAdjustedScore() <= -2) message.addReaction(getRandomEmote(NEGATIVE_EMOTES)).queue();
	}
}