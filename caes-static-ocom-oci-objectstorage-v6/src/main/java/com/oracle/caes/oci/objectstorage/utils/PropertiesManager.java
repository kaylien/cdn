package com.oracle.caes.oci.objectstorage.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesManager {
	private final Logger LOG = LogManager.getLogger(PropertiesManager.class);
	Properties prop = null;
	// default constructor
	public PropertiesManager(String name) {
		readProperties(name);
	}
	
	// default constructor
	public PropertiesManager(File cFile) {
		readPropertiesFromPath(cFile);
	}
		
	
	/**
	 * Reading properties
	 */
	private void readProperties(String name) {
		try (InputStream input =  PropertiesManager.class.getClassLoader().getResourceAsStream(name)) {
			prop = new Properties();
            prop.load(input);
        } catch (Exception ex) {
        	LOG.error("Exception loading properties file", ex);
            ex.printStackTrace();
        }
	}
	
	/**
	 * Reading properties
	 */
	private void readPropertiesFromPath(File cFile) {
		try (InputStream input = new FileInputStream(cFile) ) {
			prop = new Properties();
            prop.load(input);
        } catch (Exception ex) {
        	LOG.error("Exception loading properties file", ex);
            ex.printStackTrace();
        }
	}
	
	/**
	 * Get property
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		if(prop != null) {
			return prop.getProperty(key);
		} 
		return null;
	}
}

