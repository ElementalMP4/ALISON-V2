package main.java.de.voidtech.alison.utils;

import main.java.de.voidtech.alison.Alison;

import java.time.Instant;

public class StatusLogger {

	public static void sendStartupMessage() {
		sendMessage("ALISON has logged in at <t:" + Instant.now().getEpochSecond() + ">");
		Runtime.getRuntime().addShutdownHook(new Thread(StatusLogger::sendShutdownMessage, "Shutdown Alert"));
	}

	private static void sendShutdownMessage() {
		sendMessage("ALISON has gone to sleep at <t:" + Instant.now().getEpochSecond() + ">");
	}

	private static void sendMessage(String message) {
		String webhookUrl = Alison.getConfig().getWebhookUrl();
		if (webhookUrl == null)
			return;
		WebhookManager.sendWebhookMessage(webhookUrl, message,
				Alison.getBot().getJDA().getSelfUser().getName() + " Status",
				Alison.getBot().getJDA().getSelfUser().getAvatarUrl());
	}
}