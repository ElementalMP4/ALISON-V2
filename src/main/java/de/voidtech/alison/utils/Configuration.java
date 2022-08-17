package main.java.de.voidtech.alison.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
	private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());

	private final Properties config = new Properties();

	public Configuration() {
		File configFile = new File("AlisonConfig.properties");
		try (FileInputStream fis = new FileInputStream(configFile)) {
			config.load(fis);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "An error has occurred while reading the config: " + e.getMessage());
		}
	}

	public String getToken() {
		return config.getProperty("Token");
	}

	public String getPrefix() {
		return config.getProperty("Prefix");
	}

	public String getMasterId() {
		return config.getProperty("Master");
	}

	public String getWebhookUrl() {
		return config.getProperty("Webhook");
	}

	public String getApiUrl() {
		String url = config.getProperty("API"); 
		return url != null ? url : "http://localhost:3000/api/";
	}
	
	public boolean classifierEnabled() {
		String enabled = config.getProperty("ClassifierEnabled"); 
		return enabled != null ? false : Boolean.valueOf(enabled);
	}
}