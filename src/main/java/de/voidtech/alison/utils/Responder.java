package main.java.de.voidtech.alison.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;

public class Responder {

	public static void SendAsReply(Message message, String content) {
		message.reply(content).mentionRepliedUser(false).queue();
	}
	
	public static void SendAsReply(Message message, MessageEmbed embed) {
		message.replyEmbeds(embed).mentionRepliedUser(false).queue();
	}
	
	public static void SendAsWebhook(Message message, String content, String avatar, String title) {
		Webhook hook = WebhookManager.getOrCreateWebhook(message.getTextChannel(), "ALISON", message.getJDA().getSelfUser().getId());
		WebhookManager.sendWebhookMessage(hook, content, title, avatar);
	}
}
