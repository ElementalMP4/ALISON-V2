package main.java.de.voidtech.alison.utils;

import main.java.de.voidtech.alison.Alison;
import net.dv8tion.jda.api.entities.Message;

public class MessagePairHarvester {
    private static final String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS MessagePairs (message TEXT, reply TEXT)";
    private static final String ADD_MESSAGES = "INSERT INTO MessagePairs VALUES ('%s', '%s')";

    static {
        Alison.getDatabase().executeUpdate(CREATE_MESSAGE_TABLE);
    }

    public static void addMessages(Message message) {
        if (message.getReferencedMessage() != null) {
            Alison.getDatabase().executeUpdate(String.format(ADD_MESSAGES,
                    message.getReferencedMessage().getContentRaw(), message.getContentRaw()));
        }
    }
}