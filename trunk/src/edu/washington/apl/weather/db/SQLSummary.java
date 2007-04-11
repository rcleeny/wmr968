package edu.washington.apl.weather.db;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class SQLSummary {

	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String protocol = "jdbc:derby:";
	private static String database = "WMR968";
	
	private static Logger logger = Logger.getLogger(SQLSetup.class);
	
	public SQLSummary() {}
		
	public static void main(String[] args) {	
		/*
		try {
	  	String insert = "INSERT INTO Wind VALUES('" + (new java.sql.Timestamp(new java.util.Date().getTime())) + "', 5, 265, 33.5, 23.5, 225)";
	  	logger.debug(insert);
	  	s.execute(insert);

	  } catch(SQLException e) {
	  	logger.error("INSERT ERROR", e);	
	  }
	  */
	  
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
	    
		try {	
	    ResultSet rs = s.executeQuery("SELECT * from Wind");
	    
	    logger.debug("Wind Results -- ");
	    for(int i = 0; rs.next(); i++) {
	    	String wind = "[Wind: " + i + "] Date: " + rs.getString("Date") + 
	    		"; Battery: " + rs.getInt("Battery") + 
	    		"; Bearing: " + rs.getInt("Bearing") + 
	    		"; Gust: " + rs.getFloat("Gust") + 
	    		"; Avgerage: " + rs.getFloat("Average") + 
	    		"; Chill: " + rs.getInt("Chill");
	    		
	    	logger.debug(wind);
	    }
	   
	    rs.close();
		} catch(SQLException e) {
			logger.error("Wind ERROR", e);	
		}
		
		try {	
	    ResultSet rs = s.executeQuery("SELECT * from Rain");
	    
	    logger.debug("Rain Results -- ");
	    for(int i = 0; rs.next(); i++) {
	    	String rain = "[Rain: " + i + "] Date: " + rs.getString("Date") + 
	    		"; Battery: " + rs.getInt("Battery") + 
	    		"; Rate: " + rs.getInt("Rate") + 
	    		"; Total: " + rs.getInt("Total") + 
	    		"; Yesterday: " + rs.getInt("Yesterday") + 
	    		"; Reset: " + rs.getString("Reset");
	    		
	    	logger.debug(rain);
	    }
	   
	    rs.close();
		} catch(SQLException e) {
			logger.error("Rain ERROR", e);	
		}
		
		try {	
	    ResultSet rs = s.executeQuery("SELECT * from Temperature");
	    
	    logger.debug("Temperature Results -- ");
	    for(int i = 0; rs.next(); i++) {
	    	String temp = "[Temp: " + i + "] Date: " + rs.getString("Date") + 
	    		"; Battery: " + rs.getInt("Battery") + 
	    		"; Sensor: " + rs.getInt("Sensor") + 
	    		"; Temperature: " + rs.getFloat("Temperature") + 
	    		"; Humidity: " + rs.getInt("Humidity") + 
	    		"; Dew: " + rs.getInt("Dew") +
	    		"; Pressure: " + rs.getInt("Pressure") +
	    		"; Tendency: " + rs.getString("Tendency");
	    		
	    	logger.debug(temp);
	    }
	   
	    rs.close();
		} catch(SQLException e) {
			logger.error("Temperature ERROR", e);	
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