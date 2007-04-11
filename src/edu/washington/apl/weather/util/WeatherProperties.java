package edu.washington.apl.weather.util;

import java.util.Properties;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;


public class WeatherProperties extends Properties {
	
	private static final String DEVICE_DEFAULT = "/dev/ttyS0";

	private static final String WIND_RATE_DEFAULT = "5";
	private static final String INDOOR_RATE_DEFAULT = "1";
	private static final String OUTDOOR_RATE_DEFAULT = "1";
	private static final String RAIN_RATE_DEFAULT = "2";

	private static final String DB_ENABLE_DEFAULT = "false";

	private static final String DB_DRIVER_DEFAULT = "com.mysql.jdbc.Driver";	
	private static final String DB_PROTOCOL_DEFAULT = "jdbc:mysql:";
	private static final String DB_SERVER_DEFAULT = "endif";
	private static final String DB_DATABASE_DEFAULT = "weather";
	private static final String DB_USERNAME_DEFAULT = "weather";
	private static final String DB_PASSWORD_DEFAULT = "wmr968";
	
	private static Logger logger = Logger.getLogger(WeatherProperties.class);
	
	private static String device = null;
	
	private static double windRate = 0;
	private static double indoorRate = 0;
	private static double outdoorRate = 0;
	private static double rainRate = 0;

	private static boolean enable = false;
	
	private static String driver = null;
	private static String protocol = null;
	private static String server = null;
	private static String database = null;
	private static String username = null;
	private static String password = null;
	
	private static WeatherProperties singleton = new WeatherProperties();
	
	private WeatherProperties() {}
	
	public static void load(String pfile) throws FileNotFoundException, IOException {
		InputStream pstream = WeatherProperties.class.getClassLoader().getResourceAsStream(pfile);
		if(pstream == null) throw new FileNotFoundException("Could not find properties file: " + pfile);
		
		singleton.load(pstream);
		
		device = singleton.getProperty("wmr.device", DEVICE_DEFAULT);
		
		windRate = Double.parseDouble(singleton.getProperty("wind.rate", WIND_RATE_DEFAULT));
		indoorRate = Double.parseDouble(singleton.getProperty("indoor.rate", INDOOR_RATE_DEFAULT));
		outdoorRate = Double.parseDouble(singleton.getProperty("outdoor.rate", OUTDOOR_RATE_DEFAULT));
		rainRate = Double.parseDouble(singleton.getProperty("rain.rate", RAIN_RATE_DEFAULT));

		enable = Boolean.parseBoolean(singleton.getProperty("db.enable", DB_ENABLE_DEFAULT));
		
		driver = singleton.getProperty("db.driver", DB_DRIVER_DEFAULT);
		protocol = singleton.getProperty("db.protocol", DB_SERVER_DEFAULT);
		server = singleton.getProperty("db.server", DB_SERVER_DEFAULT);
		database = singleton.getProperty("db.database", DB_DATABASE_DEFAULT);
		username = singleton.getProperty("db.username", DB_USERNAME_DEFAULT);
		password = singleton.getProperty("db.password", DB_PASSWORD_DEFAULT);
	}
	
	public static String getDevice() {
		return singleton.device;
	}

	public static double getWindRate() {
		return singleton.windRate;
	}
	
	public static double getIndoorRate() {
		return singleton.indoorRate;
	}
	
	public static double getOutdoorRate() {
		return singleton.outdoorRate;
	}
	
	public static double getRainRate() {
		return singleton.rainRate;	
	}
	
	public static String getDriver() {
		return singleton.driver;
	} 

	public static boolean isDatabaseEnabled() {
		return singleton.enable;
	}

	public static String getConnectionString() {
		return singleton.protocol + "//" + singleton.server + "/" + singleton.database + "?user=" + singleton.username + "&password=" + singleton.password;
	}
	
	public static Properties getInstance() {
		return singleton;
	}

}
