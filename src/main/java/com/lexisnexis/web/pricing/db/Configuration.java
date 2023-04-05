package com.lexisnexis.web.pricing.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class Configuration {
		
	public static String getValue(String name, String filePath){
		Properties prop;
		File file = new File(filePath);
		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		prop = new Properties();
		
		//load properties file
		try {
			prop.load(fileInput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop.getProperty(name);
	}
	
	public static void setValue(String name, String value, String filePath) throws IOException{
		Properties prop;
		File file = new File(filePath);
		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		prop = new Properties();
		
		//load properties file
		try {
			prop.load(fileInput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		prop.setProperty(name, value);
		
		File f = new File(filePath);
        OutputStream out = new FileOutputStream( f );
        prop.store(out, "This file was updated last: ");
		
	}
}
