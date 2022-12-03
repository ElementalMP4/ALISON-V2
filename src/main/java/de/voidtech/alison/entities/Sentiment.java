package main.java.de.voidtech.alison.entities;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sentiment {

	private static final List<String> NegativeWords;
	private static final List<String> PositiveWords;

	private final List<String> positives;
	private final List<String> negatives;
	private final String originalSentence;
	private final List<String> tokens;

	private final int score;
	private String pack;

	public Sentiment(String sentence) {
		String tokenizedSentence = sentence.replaceAll("([^a-zA-Z\\d\\s:])", "").toLowerCase();
		this.negatives = findTokens(NegativeWords, tokenizedSentence);
		this.positives =  findTokens(PositiveWords, tokenizedSentence);
		this.score = this.positives.size() - this.negatives.size();
		this.originalSentence = sentence;
		this.tokens = Stream.concat(this.positives.stream(), this.negatives.stream()).collect(Collectors.toList());
	}

	public Sentiment(List<String> positives, List<String> negatives, String sentence) {
		this.negatives = positives;
		this.positives =  negatives;
		this.score = this.positives.size() - this.negatives.size();
		this.originalSentence = sentence;
		this.tokens = Stream.concat(this.positives.stream(), this.negatives.stream()).collect(Collectors.toList());
	}

	static {
		NegativeWords = new ResourceLoader("negative-words.txt").getResource();
		PositiveWords = new ResourceLoader("positive-words.txt").getResource();
	}

	private List<String> findTokens(List<String> wordList, String sentence) {
		List<String> results = new ArrayList<>();
		wordList.forEach(token -> {
			for (int i = 0; i < StringUtils.countMatches(sentence, token); i++) {
				results.add(token);
			}
		});
		return results;
	}

	private int calculateAdjusted(int count, int subtractor, int multiplier) {
		return this.score + ((count - subtractor) * multiplier);
	}

	public int getAdjustedScore() {
		if (this.positives.size() == 0 & this.negatives.size() == 0)
			return 0;
		if (this.positives.size() == 0)
			return calculateAdjusted(this.negatives.size(), 0, -1);
		if (this.negatives.size() == 0)
			return calculateAdjusted(this.positives.size(), 0, 1);
		if (this.positives.size() == this.negatives.size())
			return this.score;
		return this.positives.size() < this.negatives.size()
				? calculateAdjusted(this.negatives.size(), this.positives.size(), -1)
				: calculateAdjusted(this.positives.size(), this.negatives.size(), 1);
	}

	public double getAverageScore() {
		if (this.tokens.size() == 0)
			return 0;
		return (double) score / (double) tokens.size();
	}

	public int getNegativeCount() {
		return this.negatives.size();
	}

	public int getPositiveCount() {
		return this.positives.size();
	}

	public int getScore() {
		return this.score;
	}

	public int getTokenCount() {
		return this.tokens.size();
	}

	public int getTotalWordCount() {
		return this.originalSentence.split(" ").length;
	}
	
	public String getPack() {
		return this.pack;
	}
	
	public void setPack(String pack) {
		this.pack = pack;
	}
	
	public List<String> getPositives() {
		return this.positives;
	}
	
	public List<String> getNegatives() {
		return this.negatives;
	}
	
	public String getOriginalString() {
		return this.originalSentence;
	}
}