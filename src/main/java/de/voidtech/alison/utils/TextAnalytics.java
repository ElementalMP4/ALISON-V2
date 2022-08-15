package main.java.de.voidtech.alison.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.ResourceLoader;
import main.java.de.voidtech.alison.entities.Sentiment;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class TextAnalytics {
	
	private static List<String> NegativeWords = new ArrayList<String>();
	private static List<String> PositiveWords = new ArrayList<String>();
	
	private static final List<String> POSITIVE_EMOTES = Arrays.asList(new String[] {"♥", "🥰", "😘"});
	private static final List<String> NEGATIVE_EMOTES = Arrays.asList(new String[] {"💔", "😔", "😭"});

	private static void populateLexicon() {
		NegativeWords.clear();
		PositiveWords.clear();
		ResourceLoader loader = new ResourceLoader();
		NegativeWords = loader.getResource("negative-words.txt");
		PositiveWords = loader.getResource("positive-words.txt");
	}
	
	public static Sentiment analysePack(String pack) {
		if (!ModelManager.modelExists(pack)) return null;
		AlisonModel model = ModelManager.getModel(pack);
		String words = String.join(" ", model.getAllWords());
		Sentiment sentiment = analyseSentence(words);
		return sentiment;
	}

	public static Sentiment analyseSentence(String sentence) {
		if (NegativeWords.isEmpty() | PositiveWords.isEmpty()) populateLexicon();
		
		String tokenisedSentence = sentence.replaceAll("([^a-zA-Z\\d\\s:])", "").toLowerCase();
		List<String> negativeTokens = findTokens(NegativeWords, tokenisedSentence);
		List<String> positiveTokens = findTokens(PositiveWords, tokenisedSentence);
		
		return new Sentiment(positiveTokens, negativeTokens, sentence);
	}

	private static List<String> findTokens(List<String> wordList, String sentence) {
		List<String> results = new ArrayList<String>();
		 wordList.stream().forEach(token -> {
			 for (int i = 0; i < StringUtils.countMatches(sentence, token); i++) {
				 results.add(token);
			 }
		 });
		return results;
	}
	
	public static Sentiment analyseServer(Guild guild) {
		List<String> words = guild.getMembers().stream()
				.map(Member::getId)
				.filter(memberID -> !PrivacyManager.userHasOptedOut(memberID))
				.filter(ModelManager::modelExists)
				.map(ModelManager::getModel)
				.map(AlisonModel::getAllWords)
				.reduce(new ArrayList<String>(), (a, b) -> Stream.concat(a.stream(), b.stream()).collect(Collectors.toList()));
		return analyseSentence(String.join(" ", words));		
	}
	
	private static String getRandomEmote(List<String> emotes) {
        return emotes.get(new Random().nextInt(emotes.size()));
    }
	
	public static void respondToAlisonMention(Message message) {
		if (!message.getContentRaw().toLowerCase().contains("alison")) return;
		String intent = NodeUtils.getAlisonMentionIntent(message.getContentRaw().toLowerCase());
		if (intent == "None") return;
		message.addReaction(intent.equals("Positive") ? getRandomEmote(POSITIVE_EMOTES) : getRandomEmote(NEGATIVE_EMOTES)).queue();
	}
}