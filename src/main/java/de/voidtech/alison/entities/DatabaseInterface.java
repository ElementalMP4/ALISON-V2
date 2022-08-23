package main.java.de.voidtech.alison.entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInterface {

	private static final Logger LOGGER = Logger.getLogger(DatabaseInterface.class.getSimpleName());

	private Connection connection;
	
	public DatabaseInterface(String url) {
		try {
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet queryDatabase(String query) {
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);
			return result;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "An SQL Exception has occurred: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void executeUpdate(String query) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "An SQL Exception has occurred: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void shutdownConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "An error occurred during connection closing: " + e.getMessage());
			e.printStackTrace();
		}
	}
}