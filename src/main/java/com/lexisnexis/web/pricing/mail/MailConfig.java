package com.lexisnexis.web.pricing.mail;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MailConfig {

	public static final Logger logger = LogManager.getLogger(MailConfig.class);
	
	private static Properties configFile;
	private static MailConfig instance;
	private static final String DB_PROP_FILE = System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties";
	
	static {
		instance = new MailConfig();
	}

	private MailConfig() {
		configFile = new Properties();
		try {
			FileInputStream ip = new FileInputStream(DB_PROP_FILE);
			configFile.load(ip);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("Failed to load database config file.");
		}
	}

	private String getValue(String key) {
		return configFile.getProperty(key);
	}

	public static String getProperty(String key) {
		if (instance == null)
			instance = new MailConfig();
		return instance.getValue(key);
	}
	
	public static Properties getMailProperties() {
		if (instance == null)
			instance = new MailConfig();
		
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", configFile.get("auth"));
		prop.put("mail.smtp.starttls.enable", configFile.get("enableStarttls"));
		prop.put("mail.smtp.host", configFile.get("host"));
		prop.put("mail.smtp.port", configFile.get("port"));
		
		return prop;
	}
}
