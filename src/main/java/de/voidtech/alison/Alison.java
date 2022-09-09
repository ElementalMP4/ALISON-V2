package main.java.de.voidtech.alison;

import main.java.de.voidtech.alison.entities.DatabaseInterface;
import main.java.de.voidtech.alison.utils.Configuration;
import main.java.de.voidtech.alison.utils.DiscordBot;

public class Alison {

	private static Configuration Config;
	private static DiscordBot Bot;
	private static DatabaseInterface Database;

	public static void main(String[] args) {
		Config = new Configuration();
		Bot = new DiscordBot(Config.getToken());
		Database = new DatabaseInterface(Config.getDatabaseUrl());
	}

	public static Configuration getConfig() {
		return Config;
	}

	public static DiscordBot getBot() {
		return Bot;
	}
	
	public static DatabaseInterface getDatabase() {
		return Database;
	}
}