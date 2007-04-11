package edu.washington.apl.weather.gwt.server;

import java.util.Properties;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GWTWeatherProperties extends Properties {
	
	private static final String DB_DRIVER_DEFAULT = "com.mysql.jdbc.Driver";	
	private static final String DB_PROTOCOL_DEFAULT = "jdbc:mysql:";
	private static final String DB_SERVER_DEFAULT = "10.208.79.148";
	private static final String DB_DATABASE_DEFAULT = "weather";
	private static final String DB_USERNAME_DEFAULT = "weather";
	private static final String DB_PASSWORD_DEFAULT = "WMR968";
	
	private static String driver = null;
	private static String protocol = null;
	private static String server = null;
	private static String database = null;
	private static String username = null;
	private static String password = null;
	
	private static GWTWeatherProperties singleton = new GWTWeatherProperties();
	
	private GWTWeatherProperties() {}
	
	public static void load(String pfile) throws FileNotFoundException, IOException {
		InputStream pstream = GWTWeatherProperties.class.getClassLoader().getResourceAsStream(pfile);
		if(pstream == null) throw new FileNotFoundException("Could not find properties file: " + pfile);
		
		singleton.load(pstream);
		
		driver = singleton.getProperty("db.driver", DB_DRIVER_DEFAULT);
		protocol = singleton.getProperty("db.protocol", DB_SERVER_DEFAULT);
		server = singleton.getProperty("db.server", DB_SERVER_DEFAULT);
		database = singleton.getProperty("db.database", DB_DATABASE_DEFAULT);
		username = singleton.getProperty("db.username", DB_USERNAME_DEFAULT);
		password = singleton.getProperty("db.password", DB_PASSWORD_DEFAULT);
	}
	
	public static String getDriver() {
		return singleton.driver;
	} 

	public static Properties getInstance() {
		return singleton;
	}

	public static String getConnectionString() {
		return singleton.protocol + "//" + singleton.server + "/" + singleton.database + "?user=" + singleton.username + "&password=" + singleton.password;
	}
}
