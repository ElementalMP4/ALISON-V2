package main.java.de.voidtech.alison.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrivacyManager {

	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS IgnoredUsers (userID TEXT)";
	private static final String OPT_IN = "DELETE FROM IgnoredUsers WHERE userID = '%s'";
	private static final String OPT_OUT = "INSERT INTO IgnoredUsers VALUES ('%s')";
	private static final String USER_IS_OPTED_OUT = "SELECT * FROM IgnoredUsers WHERE userID = '%s'";

	private static Connection DatabaseConnection = null;

	public static void connect() {
		try {
			DatabaseConnection = DriverManager.getConnection("jdbc:sqlite:Alison.db");
			DatabaseInterface.executeUpdate(DatabaseConnection, CREATE_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean userHasOptedOut(String userID) {
		try {
			ResultSet result = DatabaseInterface.queryDatabase(DatabaseConnection,
					String.format(USER_IS_OPTED_OUT, userID));
			return !result.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void optOut(String userID) {
		DatabaseInterface.executeUpdate(DatabaseConnection, String.format(OPT_OUT, userID));
	}

	public static void optIn(String userID) {
		DatabaseInterface.executeUpdate(DatabaseConnection, String.format(OPT_IN, userID));
	}

}