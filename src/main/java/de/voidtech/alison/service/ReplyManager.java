package main.java.de.voidtech.alison.service;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.AlisonWord;
import main.java.de.voidtech.alison.entities.QueryBuilder;
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
    private static final String ADD_MESSAGES = "INSERT INTO MessagePairs VALUES (:message, :reply)";
    private static final String GET_MESSAGE_POOL = "SELECT * FROM MessagePairs WHERE UPPER(message) LIKE UPPER(:word)";
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

    private static String formatMessage(Message message) {
        return message.getContentRaw().replaceAll("<[^>]*>", "").replaceAll("'", "/@/").trim();
    }

    public static void addMessages(Message message) {
        if (messageCanBeAdded(message)) {
            String query = new QueryBuilder(ADD_MESSAGES)
                    .setParameter("message", formatMessage(message.getReferencedMessage()))
                    .setParameter("reply", formatMessage(message))
                    .build();
            Alison.getDatabase().executeUpdate(query);
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

    public static String createReply(String message) {
        return createReply(message, AlisonModel.MAX_MESSAGE_LENGTH);
    }

    public static String createReply(String message, int length) {
        List<String> existingResponseSentences = getExistingResponseSentences(message);
        if (existingResponseSentences.isEmpty()) return "Huh";
        List<AlisonWord> tokenizedWords = new ArrayList<>();
        List<AlisonWord> finalWordList = new ArrayList<>();
        for (String response : existingResponseSentences) {
            tokenizedWords = Stream.concat(tokenizedWords.stream(), AlisonModel.stringToAlisonWords(response)
                    .stream()).collect(Collectors.toList());
        }
        for (AlisonWord word : tokenizedWords) {
            AlisonWord wordInModel = finalWordList.stream()
                    .filter(w -> w.getWord().equals(word.getWord()) && w.getNext().equals(word.getNext()))
                    .findFirst()
                    .orElse(null);
            if (wordInModel == null) finalWordList.add(word);
            else wordInModel.incrementCount();
        }
        String reply = AlisonModel.createProbableSentenceUnderLength(finalWordList, length);
        return reply == null ? "Huh" : QueryBuilder.unescapeString(reply);
    }

    private static List<String> getExistingResponseSentences(String message) {
        String[] tokens = message.split(" ");
        List<String> sentences = new ArrayList<>();
        for (String token : tokens) {
            String query = new QueryBuilder(GET_MESSAGE_POOL)
                    .setParameter("word", "% " + token + " %")
                    .build();
            ResultSet results = Alison.getDatabase().queryDatabase(query);
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