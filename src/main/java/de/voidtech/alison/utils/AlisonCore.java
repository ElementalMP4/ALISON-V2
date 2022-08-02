package main.java.de.voidtech.alison.utils;

import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.Sentiment;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class AlisonCore {

	public static void LearnOnDemand(Message message) {
		String wordPack = message.getAuthor().getId();
		String content = message.getContentRaw();
		LearnSentence(wordPack, content);
	}

	private static void LearnSentence(String wordPack, String content) {
		AlisonModel model = PackManager.GetPack(wordPack);
		model.learn(content);
	}
	
	public static String Imitate(String wordPack) {
		AlisonModel model = PackManager.GetPack(wordPack);
		String message = model.createSentence();
		return message;
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
}