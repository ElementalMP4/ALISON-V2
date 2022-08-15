package main.java.de.voidtech.alison.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class AlisonModel {

	private static final int NICKNAME_LENGTH = 32;
	private static final int QUOTE_LENGTH = 100;
	
	private List<AlisonWord> words = new ArrayList<AlisonWord>(); 
	private AlisonMetadata meta = null;
	private String dataDir;

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
	
	private String createStringUnderLength(int length) {
		String text = createSentence();
		if (text == null) return null;
		if (text.length() > length) {
			Stack<String> words = new Stack<String>();
			Arrays.asList(text.split(" ")).stream().forEach(word -> words.push(word));
			text = "";
			while (text.trim().length() + words.peek().length() < length) {
				text += words.pop() + " ";
			}
		}
		return text.trim();
	}
	
	public String createNickname() {
		return createStringUnderLength(NICKNAME_LENGTH);
	}
	
	public String createQuote() {
		return createStringUnderLength(QUOTE_LENGTH).replaceAll("\n", " ");
	}

	public void learn(String content) {
        List<String> tokens = Arrays.asList(content.split(" "));
        for (int i = 0; i < tokens.size(); ++i) {
            if (i == tokens.size() - 1) words.add(new AlisonWord(tokens.get(i), "StopWord"));
            else words.add(new AlisonWord(tokens.get(i), tokens.get(i + 1)));
        }
        save();
	}

	private AlisonWord getRandomWord(final List<AlisonWord> words) {
        return words.get(new Random().nextInt(words.size()));
    }
	
	public Map<String, Long> getTopFiveWords() {
		Map<String, Long> countedWords = (Map<String, Long>) words.stream()
				.collect(Collectors.groupingBy(word -> word.getWord(), Collectors.counting()));	
		Map<String, Long> topFiveWords = countedWords.entrySet().stream()
				.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(5)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return topFiveWords;
	}
	
	public String createSentence() {
		if (words.isEmpty()) return null;
		List<String> result = new ArrayList<String>();
		AlisonWord next = getRandomStartWord();
		if (next == null) return null;
		while (!next.isStopWord()) {
			result.add(next.getWord());
			List<AlisonWord> potentials = getWordList(next.getNext());
			next = getRandomWord(potentials);
		}
		result.add(next.getWord());
		return String.join(" ", result);
	}
	
	private AlisonWord getRandomStartWord() {
		List<AlisonWord> wordsWithFollows = words.stream().filter(word -> !word.getNext().equals("StopWord")).collect(Collectors.toList());
		if (wordsWithFollows.size() < 2) return wordsWithFollows.get(0);
		else return wordsWithFollows.get(new Random().nextInt(wordsWithFollows.size() - 1));
	}
	
	public List<AlisonWord> getWordList(String wordToFind) {
    	return words.stream().filter(word -> word.getWord().equals(wordToFind)).collect(Collectors.toList());
    }

	public List<String> getAllWords() {
		return words.stream().map(AlisonWord::getWord).collect(Collectors.toList());
	}
	
	public List<AlisonWord> getAllAlisonWords() {
		return this.words;
	}
	
	public void overwriteModel(List<AlisonWord> words) {
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