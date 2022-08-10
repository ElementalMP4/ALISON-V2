package main.java.de.voidtech.alison.utils;

import main.java.de.voidtech.alison.Alison;

public class WebhookLogger {
	public static void sendMessage(String message) {
		String webhookUrl = Alison.getConfig().getWebhookUrl();
		if (webhookUrl == null) return;
		WebhookManager.sendWebhookMessage(webhookUrl, message,
				Alison.getBot().getJDA().getSelfUser().getName() + " Alerts",
				Alison.getBot().getJDA().getSelfUser().getAvatarUrl());
	}
}
