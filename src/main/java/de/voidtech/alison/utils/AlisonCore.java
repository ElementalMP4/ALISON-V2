package main.java.de.voidtech.alison.utils;

import java.util.Map;

import main.java.de.voidtech.alison.entities.Sentiment;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class AlisonCore {

	public static void LearnOnDemand(Message message) {
		LearnSentence(message.getAuthor().getId(), message.getContentRaw());
	}

	private static void LearnSentence(String wordPack, String content) {
		PackManager.GetPack(wordPack).learn(content);
	}
	
	public static String Imitate(String wordPack) {
		return PackManager.GetPack(wordPack).createSentence();
	}
	
	public static Map<String, Long> GetTopFiveForPack(String pack) {
		return PackManager.GetPack(pack).getTopFiveWords();
	}
	
	public static Sentiment RankSentence(String sentence) {
		return TextAnalytics.AnalyseSentence(sentence);
	}
	
	public static Sentiment RankPack(String pack) {
		return TextAnalytics.AnalysePack(pack);
	}

	public static void ClearUser(String id) {
		PackManager.DeletePack(id);
	}
	
	public static long GetTotalWordCount() {
		return PackManager.GetWordCount();
	}

	public static Sentiment RankServer(Guild guild) {
		return TextAnalytics.AnalyseServer(guild);
	}

	public static long GetPackWordCount(String ID) {
		return PackManager.GetPack(ID).getWordCount();
	}
}