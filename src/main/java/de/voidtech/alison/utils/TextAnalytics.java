package main.java.de.voidtech.alison.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.ResourceLoader;
import main.java.de.voidtech.alison.entities.Sentiment;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class TextAnalytics {
	
	private static List<String> NegativeWords = new ArrayList<String>();
	private static List<String> PositiveWords = new ArrayList<String>();

	private static void PopulateLexicon() {
		NegativeWords.clear();
		PositiveWords.clear();
		ResourceLoader loader = new ResourceLoader();
		NegativeWords = loader.getResource("negative-words.txt");
		PositiveWords = loader.getResource("positive-words.txt");
	}
	
	public static Sentiment AnalysePack(String pack) {
		if (!PackManager.PackExists(pack)) return null;
		AlisonModel model = PackManager.GetPack(pack);
		String words = String.join(" ", model.getAllWords());
		Sentiment sentiment = AnalyseSentence(words);
		return sentiment;
	}

	public static Sentiment AnalyseSentence(String sentence) {
		if (NegativeWords.isEmpty() | PositiveWords.isEmpty()) PopulateLexicon();
		
		String tokenisedSentence = sentence.replaceAll("([^a-zA-Z\\d\\s:])", "").toLowerCase();
		List<String> negativeTokens = FindTokens(NegativeWords, tokenisedSentence);
		List<String> positiveTokens = FindTokens(PositiveWords, tokenisedSentence);
		
		return new Sentiment(positiveTokens, negativeTokens, sentence);
	}

	private static List<String> FindTokens(List<String> wordList, String sentence) {
		List<String> results = new ArrayList<String>();
		 wordList.stream().forEach(token -> {
			 for (int i = 0; i < StringUtils.countMatches(sentence, token); i++) {
				 results.add(token);
			 }
		 });
		return results;
	}
	
	public static Sentiment AnalyseServer(Guild guild) {
		List<String> words = guild.getMembers().stream()
				.map(Member::getId)
				.filter(memberID -> !PrivacyManager.UserHasOptedOut(memberID))
				.filter(PackManager::PackExists)
				.map(PackManager::GetPack)
				.map(AlisonModel::getAllWords)
				.reduce(new ArrayList<String>(), (a, b) -> Stream.concat(a.stream(), b.stream()).collect(Collectors.toList()));
		return AnalyseSentence(String.join(" ", words));		
	}
}