package main.java.de.voidtech.alison.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.de.voidtech.alison.Alison;

public class PrivacyManager {

	//User SQL
	private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS IgnoredUsers (userID TEXT)";
	private static final String OPT_IN = "DELETE FROM IgnoredUsers WHERE userID = '%s'";
	private static final String OPT_OUT = "INSERT INTO IgnoredUsers VALUES ('%s')";
	private static final String USER_IS_OPTED_OUT = "SELECT * FROM IgnoredUsers WHERE userID = '%s'";
	
	//Channel SQL
	private static final String CREATE_CHANNEL_TABLE = "CREATE TABLE IF NOT EXISTS IgnoredChannels (channelID TEXT, guildID TEXT)";
	private static final String UNIGNORE_CHANNEL = "DELETE FROM IgnoredChannels WHERE channelID = '%s' AND guildID = '%s'";
	private static final String IGNORE_CHANNEL = "INSERT INTO IgnoredChannels VALUES ('%s', '%s')";
	private static final String CHANNEL_IS_IGNORED = "SELECT * FROM IgnoredChannels WHERE channelID = '%s' AND guildID = '%s'";
	private static final String GET_ALL_CHANNELS = "SELECT * FROM IgnoredChannels WHERE guildID = '%s'";

	public static void connect() {
		Alison.getDatabase().executeUpdate(CREATE_USER_TABLE);
		Alison.getDatabase().executeUpdate(CREATE_CHANNEL_TABLE);
	}

	public static boolean userHasOptedOut(String userID) {
		try {
			ResultSet result = Alison.getDatabase().queryDatabase(String.format(USER_IS_OPTED_OUT, userID));
			return !result.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void optOut(String userID) {
		Alison.getDatabase().executeUpdate(String.format(OPT_OUT, userID));
	}

	public static void optIn(String userID) {
		Alison.getDatabase().executeUpdate(String.format(OPT_IN, userID));
	}
	
	public static boolean channelIsIgnored(String channelID, String guildID) {
		try {
			ResultSet result = Alison.getDatabase().queryDatabase(String.format(CHANNEL_IS_IGNORED, channelID, guildID));
			return !result.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void ignoreChannel(String channelID, String guildID) {
		Alison.getDatabase().executeUpdate(String.format(IGNORE_CHANNEL, channelID, guildID));
	}
	
	public static void unignoreChannel(String channelID, String guildID) {
		Alison.getDatabase().executeUpdate(String.format(UNIGNORE_CHANNEL, channelID, guildID));
	}
	
	public static List<String> getIgnoredChannelsForServer(String guildID) {
		List<String> channels = new ArrayList<String>();
		try {
			ResultSet result = Alison.getDatabase().queryDatabase(String.format(GET_ALL_CHANNELS, guildID));
			while (result.next()) {
				channels.add(result.getString("channelID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return channels;
	}
}