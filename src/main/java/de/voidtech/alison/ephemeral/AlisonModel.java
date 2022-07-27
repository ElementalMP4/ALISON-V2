package main.java.de.voidtech.alison.ephemeral;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import main.java.de.voidtech.alison.utils.DatabaseInterface;

public class AlisonModel {
	
	private static final String GET_WORD_COUNT = "SELECT COUNT(*) FROM AlisonWord";
	private static final String GET_RANDOM_WORD = "SELECT * FROM AlisonWord WHERE next != 'StopWord' ORDER BY Random() LIMIT 1";
	private static final String GET_ALL_WORDS = "SELECT * FROM AlisonWord";
	private static final String GET_WORD_GROUP = "SELECT * FROM AlisonWord WHERE word = '%s'";
	private static final String CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS AlisonWord (word TEXT, next TEXT)";
	
	private Connection databaseConnection;
	
	public AlisonModel(String pack) {
		try {
			String modelPath = "models/" + pack + "/";
			File modelFile = new File(modelPath);
			if (!modelFile.exists()) {
				modelFile.mkdirs();
			}
			this.databaseConnection = DriverManager.getConnection("jdbc:sqlite:" + modelPath + "model.db");
			DatabaseInterface.executeUpdate(this.databaseConnection, CREATE_DATABASE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void sleep() {
		DatabaseInterface.shutdownConnection(this.databaseConnection);
	}

	public void learn(String content) {
        List<String> tokens = Arrays.asList(content.split(" "));
        List<AlisonWord> words = new ArrayList<AlisonWord>();
        for (int i = 0; i < tokens.size(); ++i) {
            if (i == tokens.size() - 1) words.add(new AlisonWord(tokens.get(i), "StopWord"));
            else words.add(new AlisonWord(tokens.get(i), tokens.get(i + 1)));
        }
        words.stream().forEach(word -> {
        	DatabaseInterface.executeUpdate(this.databaseConnection, word.toCreateStatement());
        });
	}
	
    private AlisonWord getRandomWord(final List<AlisonWord> words) {
        return words.get(new Random().nextInt(words.size()));
    }
	
	private List<AlisonWord> resultsToList(ResultSet set) {
		List<AlisonWord> words = new ArrayList<AlisonWord>();
		try {
			while (set.next()) {
				words.add(new AlisonWord(set.getString("word"), set.getString("next")));
			}
			set.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return words;
	}
	
	private AlisonWord singleResultToAlison(ResultSet set) {
		try {
			if (set.isClosed()) return null;
			AlisonWord word = new AlisonWord(set.getString("word"), set.getString("next"));
			set.close();
			return word;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String createSentence() {
		List<String> result = new ArrayList<String>();
		AlisonWord next = singleResultToAlison(DatabaseInterface.queryDatabase(databaseConnection, GET_RANDOM_WORD));
		if (next == null) return null;
		while (!next.isStopWord()) {
			result.add(next.getWord());
			List<AlisonWord> potentials = resultsToList(DatabaseInterface.queryDatabase(databaseConnection,
					String.format(GET_WORD_GROUP, next.getNext())));
			next = getRandomWord(potentials);
		}
		result.add(next.getWord());
		return String.join(" ", result);
	}
	
	public List<String> getAllWords() {
		ResultSet wordSet = DatabaseInterface.queryDatabase(this.databaseConnection, GET_ALL_WORDS);
		List<AlisonWord> words = resultsToList(wordSet);
		return words.stream().map(AlisonWord::getWord).collect(Collectors.toList());
	}
	
	public int getWordCount() {
		try {
			ResultSet result = DatabaseInterface.queryDatabase(this.databaseConnection, GET_WORD_COUNT);
			return result.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}