
package br.ufrn.sd.project2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Store program properties at a file
 */
public class Config {
	
	private static final String TAG = "Config";
	
	public static String CONFIG_EXPORT_GRAPH_DIR = "config.exportGraph";
	
	private static final String CONFIG_FILE = PathsUtilities.FILES_OUTPUT +
						"/config.properties";
	
	public static boolean saveConfig(String property, String value) {
		
		File configFile = new File(CONFIG_FILE);
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException ex) {
				Log.e(TAG, "Creating config file at: " + CONFIG_FILE, ex);
				
				return false;
			}
		}
		
		Properties prop = new Properties();
		
		InputStream input = null;
		
		try {
			input = new FileInputStream(configFile);
			// load properties
			prop.load(input);
		} catch (IOException io) {
			Log.e(TAG, "Reading config file", io);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					
				}
			}
		}
		
		FileOutputStream output = null;
		
		try {
			output = new FileOutputStream(configFile);
			
			// set the properties value
			prop.setProperty(property, value);

			// save properties to project root folder
			prop.store(output, null);
			
			return true;
		} catch (IOException io) {
			Log.e(TAG, "Reading config file", io);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					
				}
			}
		}
		
		return false;
	}
	
	public static String getConfig(String property) {
		return getConfig(property, null);
	}
	
	public static String getConfig(String property, String defaultValue) {
		
		File configFile = new File(CONFIG_FILE);
		if (!configFile.exists()) {
			return defaultValue;
		}
		
		Properties prop = new Properties();
		InputStream config = null;
		
		try {
			config = new FileInputStream(configFile);
			
			// load properties
			prop.load(config);
			
			// set the properties value
			return prop.getProperty(property, defaultValue);
		} catch (IOException io) {
			Log.e(TAG, "Reading config file", io);
		} finally {
			if (config != null) {
				try {
					config.close();
				} catch (IOException e) {
					
				}
			}
		}
		
		return "defaultValue";
	}
	
}
