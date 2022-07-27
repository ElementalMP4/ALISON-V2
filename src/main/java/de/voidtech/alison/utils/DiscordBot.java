package main.java.de.voidtech.alison.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import main.java.de.voidtech.alison.listeners.MessageListener;
import main.java.de.voidtech.alison.listeners.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {
	
	private static final Logger LOGGER = Logger.getLogger(DiscordBot.class.getName());
	
	public static EventWaiter Waiter = new EventWaiter();
	
	public JDA buildDiscordClient(String token) {
		try {
			return JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES)
					.addEventListeners(new ReadyListener(), new MessageListener(), Waiter)
					.setActivity(Activity.watching("you"))
					.build();
			
		} catch (LoginException e) {
			LOGGER.log(Level.SEVERE, "Failed to build JDA: " + e.getMessage());
		}
		return null;
	}

}
