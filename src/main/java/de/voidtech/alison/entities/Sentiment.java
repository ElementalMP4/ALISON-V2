package main.java.de.voidtech.alison.entities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sentiment {
	
	private List<String> positives;
	private List<String> negatives;
	private String originalWords;
	private List<String> tokens;
	
	private int score;
	
	public Sentiment(List<String> positives, List<String> negatives, String originalWords) {
		this.positives = positives;
		this.negatives = negatives;
		this.score = this.positives.size() - this.negatives.size();
		this.originalWords = originalWords;
		this.tokens = Stream.concat(this.positives.stream(), this.negatives.stream()).collect(Collectors.toList());
	}
	
	private int calculateAdjusted(int count, int subtractor, int multiplier) {
		return this.score + ((count - subtractor) * multiplier);
	}
	
	public int getAdjustedScore() {
		if (this.positives.size() == 0 & this.negatives.size() == 0) return 0;
		if (this.positives.size() == 0) return calculateAdjusted(this.negatives.size(), this.positives.size(), -1);
		if (this.negatives.size() == 0) return calculateAdjusted(this.positives.size(), this.negatives.size(), 1);
		if (this.positives.size() == this.negatives.size()) return this.score;
		return this.positives.size() < this.negatives.size() 
				? calculateAdjusted(this.negatives.size(), this.positives.size(), -1) 
				: calculateAdjusted(this.positives.size(), this.negatives.size(), 1);
	}
	
	public double getAverageScore() {
		if (this.tokens.size() == 0) return 0;
		return (double)score / (double)tokens.size();
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
}
