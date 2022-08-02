package main.java.de.voidtech.alison.utils;

import org.hibernate.Session;

import main.java.de.voidtech.alison.ephemeral.DatabaseConnection;
import main.java.de.voidtech.alison.ephemeral.IgnoredUser;

public class PrivacyManager {
	private static final String DATABASE = "jdbc:sqlite:Alison.db";
	private static DatabaseConnection Connection = null;

	public static boolean UserHasOptedOut(String userID) {
		return GetUser(userID) != null;
	}
	
	private static IgnoredUser GetUser(String userID) {
		try (Session session = Connection.getSessionFactory().openSession()) {
            final IgnoredUser user = (IgnoredUser) session.createQuery("FROM IgnoredUser WHERE userID = :userID")
            		.setParameter("userID", userID)
            		.uniqueResult();
            return user;
        }
	}
	
	public static void OptOut(String userID) {
		try(Session session = Connection.getSessionFactory().openSession())
		{
			session.getTransaction().begin();
			session.saveOrUpdate(new IgnoredUser(userID));
			session.getTransaction().commit();
		}
	}
	
	public static void OptIn(String userID) {
		try(Session session = Connection.getSessionFactory().openSession())
		{
			session.getTransaction().begin();
			session.delete(GetUser(userID));
			session.getTransaction().commit();
		}
	}

	public static void Connect() {
		Connection = DatabaseManager.getSessionFactory(DATABASE);
	}
	
}
