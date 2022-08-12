package main.java.de.voidtech.alison;

import main.java.de.voidtech.alison.utils.Configuration;
import main.java.de.voidtech.alison.utils.DiscordBot;
import main.java.de.voidtech.alison.utils.PrivacyManager;

public class Alison {
	
	private static Configuration Config;
	private static DiscordBot Bot;
	
	public static void main(String[] args) {
		Config = new Configuration();
        Bot = new DiscordBot(Config.getToken());
        PrivacyManager.Connect();
    }
	
	public static Configuration getConfig() {
		return Config;
	}
	
	public static DiscordBot getBot() {
		return Bot;
	}
}