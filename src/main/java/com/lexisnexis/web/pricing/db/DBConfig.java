package com.lexisnexis.web.pricing.db;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lexisnexis.web.pricing.util.PricingConstants;

public class DBConfig {

	public static final Logger logger = LogManager.getLogger(DBConfig.class);
	
	private static Properties configFile;
	private static DBConfig instance;
	private static String DB_PROP_FILE;
	private static String environment;
	
	public DBConfig(String env) {
		instance = this;
		if(PricingConstants.DEV.equals(env)) {
			DB_PROP_FILE = System.getProperty("user.dir") + "\\src\\main\\resources\\dev_db.properties";
			environment = env;
		} else if(PricingConstants.QA.equals(env)) {
			DB_PROP_FILE = System.getProperty("user.dir") + "\\src\\main\\resources\\qa_db.properties";
			environment = env;
		} else {
			DB_PROP_FILE = System.getProperty("user.dir") + "\\src\\main\\resources\\dev_db.properties";
			environment = env;
		}
		
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
			instance = new DBConfig(environment);
		return instance.getValue(key);
	}
	
	public static Properties getDBProperties() {
		if (instance == null)
			instance = new DBConfig(environment);
		
		Properties prop = new Properties();
		prop.put("user", configFile.get("user"));
		prop.put("password", configFile.get("password"));
		
		return prop;
	}
}
