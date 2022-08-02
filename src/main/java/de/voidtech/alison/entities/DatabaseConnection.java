package main.java.de.voidtech.alison.entities;

import java.util.Properties;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;

public class DatabaseConnection {	
	private SessionFactory sessionFactory;
	
	public DatabaseConnection(String database) {
		this.sessionFactory = makeSessionFactory(database);
	}
	
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}
	
	private SessionFactory makeSessionFactory(String database) {
		Configuration configuration = new Configuration();
		
		Properties settings = new Properties();
		settings.put(Environment.DRIVER, "org.sqlite.JDBC");
        settings.put(Environment.URL, database);
        settings.put(Environment.DIALECT, "org.sqlite.hibernate.dialect.SQLiteDialect");
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(Environment.HBM2DDL_AUTO, "update");
        settings.put(Environment.LOG_JDBC_WARNINGS, "false");
        
        configuration.setProperties(settings);
        getAllEntities().forEach(configuration::addAnnotatedClass);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        
        return configuration.buildSessionFactory(serviceRegistry);
	}
	
	private Set<Class<?>> getAllEntities()
	{
		return new Reflections("main.java.de.voidtech.alison").getTypesAnnotatedWith(Entity.class);
	}
}
