package main.java.de.voidtech.alison.listeners;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandRegistry;
import main.java.de.voidtech.alison.entities.CommandContext;
import main.java.de.voidtech.alison.utils.LevenshteinCalculator;
import main.java.de.voidtech.alison.utils.PackManager;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class MessageListener implements EventListener {
	
	public static final Logger LOGGER = Logger.getLogger(MessageListener.class.getSimpleName());
	
	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof MessageReceivedEvent) {
			MessageReceivedEvent message = (MessageReceivedEvent)event;
			if (message.isWebhookMessage()) return;
			handleMessage(message.getMessage());
		}
	}

    private boolean shouldHandleAsChatCommand(String prefix, Message message) {
        String messageRaw = message.getContentRaw();
        return messageRaw.startsWith(prefix) && messageRaw.length() > prefix.length();
    }
	
	private void handleMessage(Message message) {
		String prefix = Alison.getConfig().getPrefix();
        if (!shouldHandleAsChatCommand(prefix, message)) {
        	if (message.getContentRaw().equals("")) return;
        	if (PrivacyManager.userHasOptedOut(message.getAuthor().getId())) return;
        	PackManager.getPack(message.getAuthor().getId()).learn(message.getContentRaw());
        	return;
        };
        String messageContent = message.getContentRaw().substring(prefix.length());
        List<String> messageArray = Arrays.asList(messageContent.trim().split("\\s+"));
        
        AbstractCommand commandOpt = CommandRegistry.getCommand(messageArray.get(0).toLowerCase());
        if (commandOpt == null) {
            LOGGER.log(Level.INFO, "Command not found: " + messageArray.get(0));
            tryLevenshteinOptions(message, messageArray.get(0));
            return;
        } else {
        	LOGGER.log(Level.INFO, "Command executed: " + commandOpt.getName() + " by " + message.getAuthor().getAsTag());
        	commandOpt.run(new CommandContext(message), messageArray.subList(1, messageArray.size()));
        }
	}
	
	private MessageEmbed createLevenshteinEmbed(List<String> possibleOptions) {
		EmbedBuilder levenshteinResultEmbed = new EmbedBuilder()
				.setColor(Color.RED)
				.setTitle("I couldn't find that command! Did you mean `" + String.join("` or `", possibleOptions) + "`?");
		return levenshteinResultEmbed.build();
	}
	
    private void tryLevenshteinOptions(Message message, String commandName) {
        List<String> possibleOptions = new ArrayList<>();
        possibleOptions = CommandRegistry.getAllCommands().stream()
                .map(AbstractCommand::getName)
                .filter(name -> LevenshteinCalculator.calculate(commandName, name) <= 2)
                .collect(Collectors.toList());
        if (!possibleOptions.isEmpty())
            message.getChannel().sendMessageEmbeds(createLevenshteinEmbed(possibleOptions)).queue();
    }
}