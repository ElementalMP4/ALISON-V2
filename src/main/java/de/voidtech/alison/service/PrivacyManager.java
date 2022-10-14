package main.java.de.voidtech.alison.service;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.entities.QueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrivacyManager {

	//User SQL
	private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS IgnoredUsers (userID TEXT)";
	private static final String OPT_IN = "DELETE FROM IgnoredUsers WHERE userID = :userid";
	private static final String OPT_OUT = "INSERT INTO IgnoredUsers VALUES (:userid)";
	private static final String USER_IS_OPTED_OUT = "SELECT * FROM IgnoredUsers WHERE userID = :userid";
	
	//Channel SQL
	private static final String CREATE_CHANNEL_TABLE = "CREATE TABLE IF NOT EXISTS IgnoredChannels (channelID TEXT, guildID TEXT)";
	private static final String UNIGNORE_CHANNEL = "DELETE FROM IgnoredChannels WHERE channelID = :channelid AND guildID = :guildid";
	private static final String IGNORE_CHANNEL = "INSERT INTO IgnoredChannels VALUES (:channelid, :guildid)";
	private static final String CHANNEL_IS_IGNORED = "SELECT * FROM IgnoredChannels WHERE channelID = :channelid AND guildID = :guildid";
	private static final String GET_ALL_CHANNELS = "SELECT * FROM IgnoredChannels WHERE guildID = :guildid";

	static {
		Alison.getDatabase().executeUpdate(CREATE_USER_TABLE);
		Alison.getDatabase().executeUpdate(CREATE_CHANNEL_TABLE);
	}

	public static boolean userHasOptedOut(String userID) {
		try {
			String query = new QueryBuilder(USER_IS_OPTED_OUT).setParameter("userid", userID).build();
			ResultSet result = Alison.getDatabase().queryDatabase(query);
			return !result.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void optOut(String userID) {
		String query = new QueryBuilder(OPT_OUT)
				.setParameter("userid", userID)
				.build();
		Alison.getDatabase().executeUpdate(query);
	}

	public static void optIn(String userID) {
		String query = new QueryBuilder(OPT_IN)
				.setParameter("userid", userID)
				.build();
		Alison.getDatabase().executeUpdate(query);
	}
	
	public static boolean channelIsIgnored(String channelID, String guildID) {
		try {
			String query = new QueryBuilder(CHANNEL_IS_IGNORED)
					.setParameter("channelid", channelID)
					.setParameter("guildid", guildID)
					.build();
			ResultSet result = Alison.getDatabase().queryDatabase(query);
			return !result.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void ignoreChannel(String channelID, String guildID) {
		String query = new QueryBuilder(IGNORE_CHANNEL)
				.setParameter("channelid", channelID)
				.setParameter("guildid", guildID)
				.build();
		Alison.getDatabase().executeUpdate(query);
	}
	
	public static void unignoreChannel(String channelID, String guildID) {
		String query = new QueryBuilder(UNIGNORE_CHANNEL)
				.setParameter("channelid", channelID)
				.setParameter("guildid", guildID)
				.build();
		Alison.getDatabase().executeUpdate(query);
	}
	
	public static List<String> getIgnoredChannelsForServer(String guildID) {
		List<String> channels = new ArrayList<>();
		try {
			String query = new QueryBuilder(GET_ALL_CHANNELS)
					.setParameter("guildid", guildID)
					.build();
			ResultSet result = Alison.getDatabase().queryDatabase(query);
			while (result.next()) {
				channels.add(result.getString("channelID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return channels;
	}
}