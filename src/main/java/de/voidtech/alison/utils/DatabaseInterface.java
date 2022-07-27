package main.java.de.voidtech.alison.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInterface {
	
	private static final Logger LOGGER = Logger.getLogger(DatabaseInterface.class.getSimpleName());

	public static ResultSet queryDatabase(Connection databaseConnection, String query) {
		try {
			Statement statement = databaseConnection.createStatement();
			ResultSet result = statement.executeQuery(query);
			return result;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "An SQL Exception has occurred: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static void executeUpdate(Connection databaseConnection, String query) {
		try {
			Statement statement = databaseConnection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "An SQL Exception has occurred: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void shutdownConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "An error occurred during connection closing: " + e.getMessage());
			e.printStackTrace();
		}
	}
}