package main.java.de.voidtech.alison.entities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sentiment {

	public static final List<String> COMMON_WORDS;

	private final List<String> positives;
	private final List<String> negatives;
	private final String originalWords;
	private final List<String> tokens;

	private final int score;
	private String pack;

	static {
		COMMON_WORDS = new ResourceLoader().getResource("common-words.txt");
	}

	public Sentiment(List<String> positives, List<String> negatives, String originalWords) {
		this.positives = positives.stream().filter(COMMON_WORDS::contains).collect(Collectors.toList());
		this.negatives = negatives.stream().filter(COMMON_WORDS::contains).collect(Collectors.toList());
		this.score = this.positives.size() - this.negatives.size();
		this.originalWords = originalWords;
		this.tokens = Stream.concat(this.positives.stream(), this.negatives.stream()).collect(Collectors.toList());
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
		return this.originalWords.split(" ").length;
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
		return this.originalWords;
	}
}