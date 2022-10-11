package main.java.de.voidtech.alison.entities;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class AlisonModel {

	public static final int NICKNAME_LENGTH = 32;
	public static final int QUOTE_LENGTH = 100;
	public static final int SEARCH_LENGTH = 50;
	public static final int MAX_MESSAGE_LENGTH = 2000;
	
	private List<AlisonWord> words = new ArrayList<>();
	private AlisonMetadata meta = null;
	private final String dataDir;

	public AlisonModel(String pack) {
		dataDir = "models/" + pack;
		File modelFile = new File(dataDir + "/words.alison");
		if (modelFile.exists()) load();
		else {
			try {
				modelFile.getParentFile().mkdirs();
				modelFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		try {
			FileInputStream fileInStream = new FileInputStream(dataDir + "/words.alison");
			ObjectInputStream objectInStream = new ObjectInputStream(fileInStream);
			words = (List<AlisonWord>) objectInStream.readObject();
			objectInStream.close();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		File metaFile = new File(dataDir + "/meta.json");
		if (metaFile.exists()) {
			try {
				Gson gson = new Gson();
				this.meta = gson.fromJson(Files.newBufferedReader(Paths.get(metaFile.getPath())), AlisonMetadata.class);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void save() {
		try {
			FileOutputStream fileOutStream = new FileOutputStream(dataDir + "/words.alison");
			ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream);
			objectOutStream.writeObject(words);
			objectOutStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void learn(String content) {
		List<AlisonWord> newWords = stringToAlisonWords(content);
		for (AlisonWord word : newWords) {
			AlisonWord wordInModel = words.stream()
					.filter(w -> w.getWord().equals(word.getWord()) && w.getNext().equals(word.getNext()))
					.findFirst()
					.orElse(null);
			if (wordInModel == null) words.add(word);
			else wordInModel.incrementCount();
		}

		save();
	}

	public static String createRandomStringUnderLength(List<AlisonWord> words, int length) {
		if (words.isEmpty()) return null;
		StringBuilder result = new StringBuilder();
		AlisonWord next = getRandomStartWord(words);
		if (next == null) return null;
		while (next.isStopWord()) {
			if (result.length() + (next.getWord() + " ").length() > length) break;
			result.append(next.getWord()).append(" ");
			List<AlisonWord> potentials = getWordList(words, next.getNext());
			next = getRandomWord(potentials);
		}
		if (result.length() + next.getWord().length() <= length) result.append(next.getWord());
		return result.toString().replaceAll("<[^>]*>", "").replaceAll("@", "``@``");
	}

	public static String createProbableSentenceUnderLength(List<AlisonWord> words, int length) {
		if (words.isEmpty()) return null;
		StringBuilder result = new StringBuilder();
		AlisonWord next = getMostLikely(words);
		if (next == null) return null;
		while (next.isStopWord()) {
			if (result.length() + (next.getWord() + " ").length() > MAX_MESSAGE_LENGTH) break;
			result.append(next.getWord()).append(" ");
			List<AlisonWord> potentials = getWordList(words, next.getNext());
			next = getMostLikely(potentials);
		}
		if (result.length() + next.getWord().length() <= MAX_MESSAGE_LENGTH) result.append(next.getWord());
		return result.toString();
	}

	private static AlisonWord getMostLikely(List<AlisonWord> potentials) {
		return potentials.stream()
				.sorted(Comparator.comparing(AlisonWord::getFrequency))
				.collect(Collectors.toList()).get(0);
	}

	public static List<AlisonWord> stringToAlisonWords(String content) {
		List<String> tokens = Arrays.asList(content.split(" "));
		List<AlisonWord> words = new ArrayList<>();
		for (int i = 0; i < tokens.size(); ++i) {
			if (i == tokens.size() - 1) words.add(new AlisonWord(tokens.get(i), "StopWord"));
			else words.add(new AlisonWord(tokens.get(i), tokens.get(i + 1)));
		}
		return words;
	}

	private static AlisonWord getRandomWord(List<AlisonWord> words) {
		List<AlisonWord> correctedWordList = new ArrayList<>();
		words.forEach(w -> {for (int i = 0; i < w.getFrequency(); i++) {correctedWordList.add(w);}});
		return correctedWordList.get(new Random().nextInt(correctedWordList.size()));
	}

	private static AlisonWord getRandomStartWord(List<AlisonWord> words) {
		List<AlisonWord> wordsWithFollows = words.stream()
				.filter(word -> !word.getNext().equals("StopWord"))
				.collect(Collectors.toList());
		if (wordsWithFollows.size() < 2) return null;
		else return wordsWithFollows.get(new Random().nextInt(wordsWithFollows.size() - 1));
	}

	private static List<AlisonWord> getWordList(List<AlisonWord> words, String wordToFind) {
		return words.stream().filter(word -> word.getWord().equals(wordToFind)).collect(Collectors.toList());
	}

	public String createNickname() {
		return createRandomStringUnderLength(words, NICKNAME_LENGTH);
	}
	
	public String createQuote() {
		return createRandomStringUnderLength(words, QUOTE_LENGTH).replaceAll("\n", " ");
	}
	
	public String createSearch() {
		return createRandomStringUnderLength(words, SEARCH_LENGTH);
	}
	
	public String createImitate() {
		return createRandomStringUnderLength(words, MAX_MESSAGE_LENGTH);
	}
	
	public Map<String, Long> getTopFiveWords() {
		Map<String, Long> countedWords = words.stream()
				.collect(Collectors.groupingBy(AlisonWord::getWord, Collectors.counting()));
		return countedWords.entrySet().stream()
				.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(5)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public List<String> getAllWords() {
		return words.stream().map(AlisonWord::getWord).collect(Collectors.toList());
	}
	
	public List<AlisonWord> getAllAlisonWords() {
		return this.words;
	}
	
	public void overwriteModel(List<AlisonWord> words) {
		this.words.clear();
		this.words = words;
		save();
	}
	
	public long getWordCount() {
		return words.size();
	}
	
	public boolean hasMeta() {
		return this.meta != null;
	}
	
	public AlisonMetadata getMeta() {
		return this.meta;
	}
}