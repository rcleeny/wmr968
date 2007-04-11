package edu.washington.apl.weather.db;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class SQLSetup {

	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String protocol = "jdbc:derby:";
	private static String database = "WMR968";
	
	private static Logger logger = Logger.getLogger(SQLSetup.class);
	
	public SQLSetup() {}
		
	public static void main(String[] args) {	
		try {
			Class.forName(driver).newInstance();
		} catch (ClassNotFoundException e) {
			logger.error("SQL Driver Not Found", e);
			return;	
		} catch (InstantiationException e) {
			logger.error("Could NOt Instantiate Driver", e);
			return;	
		} catch (IllegalAccessException e) {
			logger.error("Illegal Access", e);
			return;	
		}
		
	  Connection conn = null;
	  
	  try {
	    conn = DriverManager.getConnection(protocol + database + ";create=true");
	  } catch(SQLException e) {
	  	logger.error("Could Not Make Connection To: " + protocol + database, e);
	  	return;	
	  }

		Statement s = null;
		
		try {
			s = conn.createStatement();
		} catch(SQLException e) {
			logger.error("Could Not Create Statement", e);
	  	return;	
		}
	    	    
		String rain = "CREATE TABLE Rain (" + 
		  	"Date TIMESTAMP NOT NULL PRIMARY KEY, " +
		  	"Battery INT NOT NULL DEFAULT 0, " +
			  "Rate INT NOT NULL DEFAULT 0, " +
			  "Total INT NOT NULL DEFAULT 0, " +
			  "Yesterday INT NOT NULL DEFAULT 0, " +
		  	"Reset TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'" +
			")";
			
		try {
    	s.execute("DROP TABLE Rain");
	  } catch(SQLException e) {
	  	logger.warn("Error Dropping 'Rain' Table", e);	
	  }
	  
	  try {
    	s.execute(rain);
	  } catch(SQLException e) {
	  	logger.warn("Error Creating 'Rain' Table", e);	
	  }
	  
	  String wind = "CREATE TABLE Wind (" + 
				"Date TIMESTAMP NOT NULL PRIMARY KEY, " + 
				"Battery INT NOT NULL DEFAULT 0, " + 
				"Bearing INT NOT NULL DEFAULT 0, " + 
				"Gust FLOAT NOT NULL DEFAULT 0, " + 
	  		"Average FLOAT NOT NULL DEFAULT 0, " + 
				"Chill INT NOT NULL DEFAULT 0" +
			")";
		
		try {
    	s.execute("DROP TABLE Wind");
	  } catch(SQLException e) {
	  	logger.warn("Error Dropping 'Wind' Table", e);	
	  }
	  
	  try {
    	s.execute(wind);
	  } catch(SQLException e) {
	  	logger.warn("Error Creating 'Wind' Table", e);	
	  }
	  
	  String temp = "CREATE TABLE Temperature (" + 
				"Date TIMESTAMP NOT NULL," + 
				"Battery INT NOT NULL DEFAULT 0," + 
				"Sensor INT NOT NULL DEFAULT 0," + 
				"Temperature FLOAT NOT NULL DEFAULT 0," + 
				"Humidity INT NOT NULL DEFAULT 0," + 
				"Dew INT NOT NULL DEFAULT 0," + 
				"Pressure INT DEFAULT NULL," + 
				"Tendency VARCHAR(100) DEFAULT NULL, " + 
				"PRIMARY KEY (Date, Sensor)" + 
			")";
			
		try {
    	s.execute("DROP TABLE Temperature");
	  } catch(SQLException e) {
	  	logger.warn("Error Dropping 'Temperature' Table", e);	
	  }
	  
	  try {
    	s.execute(temp);
	  } catch(SQLException e) {
	  	logger.warn("Error Creating 'Temperature' Table", e);	
	  }
	  
	 try {
			s.close();
			conn.close();
		} catch(SQLException e) {
			logger.error("Error Closing Statement and/or Connection", e);	
		}
    
    // will throw exeption even on correct shutdown
    try {
			DriverManager.getConnection(protocol + ";shutdown=true");
		} catch (SQLException e) {
			logger.warn("NOTE: ** Shutdown Exception 'Derby system shutdown' Expected", e);
		}
		
	}
}