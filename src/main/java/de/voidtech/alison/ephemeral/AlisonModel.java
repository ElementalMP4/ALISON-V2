package main.java.de.voidtech.alison.ephemeral;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;

import main.java.de.voidtech.alison.utils.DatabaseManager;

public class AlisonModel {
	
	private DatabaseConnection databaseConnection;
	
	public AlisonModel(String pack) {
		String modelPath = "models/" + pack + "/";
		File modelFile = new File(modelPath);
		if (!modelFile.exists()) {
			modelFile.mkdirs();
		}
		String database = "jdbc:sqlite:" + modelPath + "model.db";
		this.databaseConnection = DatabaseManager.getSessionFactory(database);
	}

	public void learn(String content) {
        List<String> tokens = Arrays.asList(content.split(" "));
        List<AlisonWord> words = new ArrayList<AlisonWord>();
        for (int i = 0; i < tokens.size(); ++i) {
            if (i == tokens.size() - 1) words.add(new AlisonWord(tokens.get(i), "StopWord"));
            else words.add(new AlisonWord(tokens.get(i), tokens.get(i + 1)));
        }
        words.stream().forEach(word -> addWord(word));
	}
	
    private void addWord(AlisonWord word) {
    	try(Session session = this.databaseConnection.getSessionFactory().openSession())
		{
			session.getTransaction().begin();
			session.saveOrUpdate(word);
			session.getTransaction().commit();
		}
	}

	private AlisonWord getRandomWord(final List<AlisonWord> words) {
        return words.get(new Random().nextInt(words.size()));
    }
	
	public String createSentence() {
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
		try (Session session = this.databaseConnection.getSessionFactory().openSession()) {
            final AlisonWord alisonWord = (AlisonWord) session.createQuery("FROM AlisonWord WHERE next != 'StopWord' ORDER BY RANDOM()")
            		.setMaxResults(1)
            		.uniqueResult();
            return alisonWord;
        }
	}
	
    @SuppressWarnings("unchecked")
	public List<AlisonWord> getWordList(String word) {
    	try (Session session = this.databaseConnection.getSessionFactory().openSession()) {
            final List<AlisonWord> list = (List<AlisonWord>) session.createQuery("FROM AlisonWord WHERE word = :word")
            		.setParameter("word", word)
            		.list();
            return list;
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<AlisonWord> getEveryWord() {
    	try (Session session = this.databaseConnection.getSessionFactory().openSession()) {
            final List<AlisonWord> list = (List<AlisonWord>) session.createQuery("FROM AlisonWord")
            		.list();
            return list;
        }
    }

	public List<String> getAllWords() {
		List<AlisonWord> words = getEveryWord();
		return words.stream().map(AlisonWord::getWord).collect(Collectors.toList());
	}
	
	public long getWordCount() {
		try(Session session = this.databaseConnection.getSessionFactory().openSession())
		{
			@SuppressWarnings("rawtypes")
			Query query = session.createQuery("select count(*) from AlisonWord");
			long count = ((long)query.uniqueResult());
			session.close();
			return count;
		}
	}
}