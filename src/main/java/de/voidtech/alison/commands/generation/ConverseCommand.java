package main.java.de.voidtech.alison.commands.generation;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.utils.ReplyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class ConverseCommand extends AbstractCommand {

    private static final String ERROR_STRING = "I'm not sure how to respond to that.";

    @Override
    public void execute(CommandContext commandContext, List<String> args) {
        String startSentence = "";
        if (args.isEmpty()) {
            AlisonModel model = new AlisonModel(Alison.getConfig().getMasterId());
            startSentence = model.createQuote();
        } else startSentence = String.join(" ", args);
        commandContext.reply(generateConversation(startSentence));
    }

    private MessageEmbed generateConversation(String startSentence) {
        String promptForGavin = startSentence;
        String promptForAlison;
        StringBuilder convoBuilder = new StringBuilder();
        EmbedBuilder convoEmbedBuilder = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("A conversation between ALISON and Gavin with the prompt '" + startSentence + "'");

        for (int i = 0; i < 10; i++) {
            promptForAlison = getGavinResponse(promptForGavin);
            convoBuilder.append("Gavin: ").append(promptForAlison).append("\n\n");
            promptForGavin = ReplyManager.createReply(promptForAlison, AlisonModel.QUOTE_LENGTH);
            convoBuilder.append("Alison: ").append(promptForGavin).append("\n\n");
        }

        convoEmbedBuilder.setDescription("```\n" + convoBuilder + "\n```");
        return convoEmbedBuilder.build();
    }

    private String getGavinResponse(String message) {
        try {
            String payload = new JSONObject().put("data", message).toString();
            URL requestURL = new URL(Alison.getConfig().getGavinUrl());
            HttpURLConnection con = (HttpURLConnection) requestURL.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            con.setRequestProperty("Accept", "application/json");

            OutputStream os = con.getOutputStream();
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String output = in.lines().collect(Collectors.joining());
            con.disconnect();
            return new JSONObject(output).getString("message").replaceAll("\n", " ");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return ERROR_STRING;
    }

    @Override
    public String getName() {
        return "converse";
    }

    @Override
    public String getUsage() {
        return "converse";
    }

    @Override
    public String getDescription() {
        return "Generate a short conversation between Gavin and Alison";
    }

    @Override
    public String getShorthand() {
        return "con";
    }

    @Override
    public CommandCategory getCommandCategory() {
        return CommandCategory.TEXT_GENERATION;
    }

    @Override
    public boolean isDmCapable() {
        return true;
    }

    @Override
    public boolean requiresArguments() {
        return false;
    }

    @Override
    public boolean isLongCommand() {
        return true;
    }
}