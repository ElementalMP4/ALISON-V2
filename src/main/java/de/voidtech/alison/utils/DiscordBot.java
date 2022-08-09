package main.java.de.voidtech.alison.utils;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import main.java.de.voidtech.alison.listeners.MessageListener;
import main.java.de.voidtech.alison.listeners.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class DiscordBot {
	
	private EventWaiter waiter = new EventWaiter();
	private JDA jda;
	
	public DiscordBot(String token) {
		try {
			jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.addEventListeners(new ReadyListener(), new MessageListener(), waiter)
					.setActivity(Activity.watching("you"))
					.setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
					.build();
			
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	public JDA getJDA() {
		return this.jda;
	}
	
	public EventWaiter getEventWaiter() {
		return this.waiter;
	}

}
