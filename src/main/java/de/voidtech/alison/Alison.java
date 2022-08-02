package main.java.de.voidtech.alison;

import main.java.de.voidtech.alison.utils.Configuration;
import main.java.de.voidtech.alison.utils.DiscordBot;

public class Alison{
	
	public static Configuration Config;

	public static void main(String[] args) {
        Config = new Configuration();
        DiscordBot bot = new DiscordBot();
        bot.buildDiscordClient(Config.getToken());
    }
}
