package com.lexisnexis.web.pricing.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseManager {

	public static final Logger logger = LogManager.getLogger(DatabaseManager.class);

	public static Connection getSybaseConnection() {
		Connection conn = null;
		try {
			System.setProperty(DBConfig.getProperty("jdbc_driver"), DBConfig.getProperty("sybase_driver"));
			conn = DriverManager.getConnection(DBConfig.getProperty("url"), DBConfig.getDBProperties());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return conn;
	}
	
	public static Connection getSybaseConnection(String dbName) {
		Connection conn = null;
		try {
			System.setProperty(DBConfig.getProperty("jdbc_driver"), DBConfig.getProperty("sybase_driver"));
			//TODO: Edit to incorporate dnName in URL
			conn = DriverManager.getConnection(DBConfig.getProperty("url"), DBConfig.getDBProperties());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return conn;
	}
	
	
}
