package main.java.de.voidtech.alison.utils;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.AlisonWord;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReplyManager {
    private static final String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS MessagePairs (message TEXT, reply TEXT)";
    private static final String ADD_MESSAGES = "INSERT INTO MessagePairs VALUES ('%s', '%s')";
    private static final String GET_MESSAGE_POOL = "SELECT * FROM MessagePairs WHERE UPPER(message) LIKE UPPER('% word %')";
    private static final String GET_CONVERSATION_COUNT = "SELECT COUNT(*) FROM MessagePairs";

    static {
        Alison.getDatabase().executeUpdate(CREATE_MESSAGE_TABLE);
    }

    public static int getConversationCount() {
        try {
            ResultSet result = Alison.getDatabase().queryDatabase(GET_CONVERSATION_COUNT);
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addMessages(Message message) {
        if (messageCanBeAdded(message)) {
            Alison.getDatabase().executeUpdate(String.format(ADD_MESSAGES,
                    message.getReferencedMessage().getContentRaw().replaceAll("<[^>]*>", "").trim(),
                    message.getContentRaw().replaceAll("<[^>]*>", "").trim()));
        }
    }

    private static boolean messageCanBeAdded(Message message) {
        return message.getReferencedMessage() != null
                && !message.getContentRaw().equals("")
                && !message.getReferencedMessage().getContentRaw().equals("");
    }

    public static void replyToMessage(Message message) {
        if (message.getMentionedUsers().contains(message.getJDA().getSelfUser())
                | message.getChannel().getType().equals(ChannelType.PRIVATE)) {
            message.getChannel().sendMessage(createReply(message.getContentDisplay())).queue();
        }
    }

    private static String createReply(String message) {
        List<String> existingResponseSentences = getExistingResponseSentences(message);
        if (existingResponseSentences.isEmpty()) return "Huh";
        List<AlisonWord> tokenizedWords = new ArrayList<>();
        List<AlisonWord> finalWordList = new ArrayList<>();
        for (String response : existingResponseSentences) {
            tokenizedWords = Stream.concat(tokenizedWords.stream(), AlisonModel.stringToAlisonWords(response).stream()).collect(Collectors.toList());
        }
        for (AlisonWord word : tokenizedWords) {
            AlisonWord wordInModel = finalWordList.stream()
                    .filter(w -> w.getWord().equals(word.getWord()) && w.getNext().equals(word.getNext()))
                    .findFirst()
                    .orElse(null);
            if (wordInModel == null) finalWordList.add(word);
            else wordInModel.incrementCount();
        }
        String reply = AlisonModel.createRandomStringUnderLength(finalWordList, AlisonModel.MAX_MESSAGE_LENGTH);
        return reply == null ? "Huh" : reply;
    }

    private static List<String> getExistingResponseSentences(String message) {
        String[] tokens = message.split(" ");
        List<String> sentences = new ArrayList<>();
        for (String token : tokens) {
            ResultSet results = Alison.getDatabase().queryDatabase(GET_MESSAGE_POOL.replace("word", token));
            sentences = Stream.concat(sentences.stream(), resultsToList(results).stream()).collect(Collectors.toList());
        }
        return sentences;
    }

    private static List<String> resultsToList(ResultSet results) {
        List<String> sentences = new ArrayList<>();
        try {
             while (results.next()) {
                sentences.add(results.getString("reply"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sentences;
    }
}