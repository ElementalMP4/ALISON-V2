package main.java.de.voidtech.alison.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import main.java.de.voidtech.alison.Alison;

public class StatusLogger {
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public static void init() {
		sendStartupMessage();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            sendShutdownMessage();
	        }
	    }, "Shutdown Alert"));
	}
	
	private static void sendStartupMessage() {
		sendMessage("ALISON has logged in at " + formatter.format(new Date()));
	}
	
	private static void sendShutdownMessage() {
		sendMessage("ALISON has gone to sleep at " + formatter.format(new Date()));
	}
	
	private static void sendMessage(String message) {
		String webhookUrl = Alison.getConfig().getWebhookUrl();
		if (webhookUrl == null) return;
		WebhookManager.sendWebhookMessage(webhookUrl, message,
				Alison.getBot().getJDA().getSelfUser().getName() + " Status",
				Alison.getBot().getJDA().getSelfUser().getAvatarUrl());
	}
}
