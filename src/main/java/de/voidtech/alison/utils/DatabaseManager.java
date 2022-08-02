package main.java.de.voidtech.alison.utils;

import java.util.HashMap;
import java.util.Map;

import main.java.de.voidtech.alison.ephemeral.DatabaseConnection;

public class DatabaseManager {

	private static Map<String, DatabaseConnection> Connections = new HashMap<String, DatabaseConnection>();
	
	public static DatabaseConnection getSessionFactory(String database) {
		if (!Connections.containsKey(database)) Connections.put(database, new DatabaseConnection(database));
		return Connections.get(database);
	}

	public static Map<String, DatabaseConnection> getConnections() {
		return Connections;
	}
}