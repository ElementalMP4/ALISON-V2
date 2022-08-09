package main.java.de.voidtech.alison.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;

public class AlisonModel {

	private List<AlisonWord> words = new ArrayList<AlisonWord>(); 
	private String dataDir;
	
	@SuppressWarnings("unchecked")
	public AlisonModel(String pack) {
		dataDir = "models/" + pack + "/words.alison";
		File modelFile = new File(dataDir);
		if (modelFile.exists()) {
			try {
				FileInputStream fileInStream = new FileInputStream(dataDir);
				ObjectInputStream objectInStream = new ObjectInputStream(fileInStream);
				words = (List<AlisonWord>) objectInStream.readObject();
				objectInStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			try {
				modelFile.getParentFile().mkdirs();
				modelFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String createNickname() {
		String name = createSentence();
		if (name == null) return null;
		if (name.length() > 32) {
			Stack<String> words = new Stack<String>();
			Arrays.asList(name.split(" ")).stream().forEach(word -> words.push(word));
			name = "";
			while (name.trim().length() + words.peek().length() < 32) {
				name += words.pop() + " ";
			}
		}
		return name.trim();
	}

	public void learn(String content) {
        List<String> tokens = Arrays.asList(content.split(" "));
        List<AlisonWord> newWords = new ArrayList<AlisonWord>();
        for (int i = 0; i < tokens.size(); ++i) {
            if (i == tokens.size() - 1) words.add(new AlisonWord(tokens.get(i), "StopWord"));
            else words.add(new AlisonWord(tokens.get(i), tokens.get(i + 1)));
        }
        newWords.stream().forEach(word -> words.add(word));
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
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, HashMap::new));
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
	
	public long getWordCount() {
		return words.size();
	}
	
	public void save() {
		try {
			FileOutputStream fileOutStream = new FileOutputStream(dataDir);
			ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream);
			objectOutStream.writeObject(words);
			objectOutStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}