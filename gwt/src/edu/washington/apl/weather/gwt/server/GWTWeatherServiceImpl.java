package edu.washington.apl.weather.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.washington.apl.weather.gwt.client.service.GWTWeatherService;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;

public class GWTWeatherServiceImpl extends RemoteServiceServlet implements GWTWeatherService {
	
	private static final Logger logger = Logger.getLogger(GWTWeatherServiceImpl.class);
	private static final String propfile = "gwt-weather.properties"; // should put into initparam
	private static Connection connection = null;
	
	private static final int CLEAR = 0x0C;
	private static final int PT_CLOUDY = 0x06;
	private static final int CLOUDY = 0x02;
	private static final int RAIN = 0x03;
	
	private static final HashMap<Integer, String> TENDENCY_MAP = new HashMap<Integer, String>();
	
	static {
		TENDENCY_MAP.put(CLEAR, "Clear");
		TENDENCY_MAP.put(PT_CLOUDY, "Partly Cloudy");
		TENDENCY_MAP.put(CLOUDY, "Cloudy");
		TENDENCY_MAP.put(RAIN, "Rain");
	}
	
	public void init() throws ServletException {
		try {
			GWTWeatherProperties.load(propfile);
			logger.debug(GWTWeatherProperties.getInstance());
		} catch(Exception e) {
			logger.error("Could not find properties file: " + propfile, e);
			throw new ServletException("Could not find properties file: " + propfile);
		}	
		
		try {
			Class.forName(GWTWeatherProperties.getDriver()).newInstance();
		} catch (ClassNotFoundException e) {
			logger.error("SQL Driver Not Found", e);
			throw new ServletException("SQL Driver Not Found");
		} catch (InstantiationException e) {
			logger.error("Could Not Instantiate Driver", e);
			throw new ServletException("Could Not Instantiate Driver");
		} catch (IllegalAccessException e) {
			logger.error("Illegal Access", e);
			throw new ServletException("Illegal Access");
		}	  
	}
	
	public void destroy() {
		if(connection != null) {
			try {
				connection.close();
			} catch(SQLException e) {
				logger.error("Error Closing Connection", e);	
			}
		}
	}
	
	public String getIndoor() {
		resetConnection();
		
		try {	
			if(connection == null) return "Indoor ERROR";
			Statement statement = connection.createStatement();
			
	    ResultSet rs = statement.executeQuery("select date, temperature, tendency from temperature where sensor = 6 order by date desc limit 1");
	    String text = null;
	    
	    if(rs.next()) text = "Temp " + rs.getFloat("temperature") + "C with tendency " + TENDENCY_MAP.get(rs.getInt("tendency")) + " @ " + rs.getString("date");
	    else text = "No indoor data avaialbe";
	   
	    rs.close();
	    statement.close();
	    
	    return text;

		} catch(SQLException e) {
			logger.error("Indoor ERROR", e);	
		}
		return "Indoor ERROR";
	}
	
  public String getOutdoor() {
  	resetConnection();
  	
  	try {	
  		if(connection == null) return "Indoor ERROR";
			Statement statement = connection.createStatement();
			
	    ResultSet rs = statement.executeQuery("select date, temperature from temperature where sensor = 3 order by date desc limit 1");
	    String text = null;
	    
	    if(rs.next()) text = "Temp  " + rs.getFloat("temperature") + "C @ " + rs.getString("date");
	    else text = "No outdoor data avaialbe";
	   
	    rs.close();
	    statement.close();
	    
	    return text;

		} catch(SQLException e) {
			logger.error("Outdoor ERROR", e);
		}
		return "Outdoor ERROR";
  }
  
  public String getWind() {
  	resetConnection();
  	
  	try {	
  		if(connection == null) return "Indoor ERROR";
			Statement statement = connection.createStatement();
			
	    ResultSet rs = statement.executeQuery("select date, average, bearing from wind order by date desc limit 1");
	    String text = null;
	    
	    if(rs.next()) text = "Average " + rs.getFloat("average") + " m/s heading " + rs.getInt("bearing") + " @ " + rs.getString("date");
	    else text = "No wind data avaialbe";
	   
	    rs.close();
	    statement.close();
	    
	    return text;

		} catch(SQLException e) {
			logger.error("Wind ERROR", e);
		}
		return "Wind ERROR";		
  }
  
  public String getRain() {
  	resetConnection();
  	
  	try {	
  		if(connection == null) return "Indoor ERROR";
			Statement statement = connection.createStatement();
			
	    ResultSet rs = statement.executeQuery("select date, rate, total from rain order by date desc limit 1");
	    String text = null;
	    
	    if(rs.next()) text = "Rate " + rs.getInt("rate") + " mm/hr and total " + rs.getInt("total") + " mm @ " + rs.getString("date");
	    else text = "No rain data avaialbe";
	   
	    rs.close();
	    statement.close();
	    
	    return text;

		} catch(SQLException e) {
			logger.error("Rain ERROR", e);
		}
		return "Rain ERROR";
  }
  
  /* not working reliably ... make new connection each request */
  /*
  private void checkConnection() {
  	try {
	  	if(connection == null || connection.isClosed()) resetConnection();
  	} catch(SQLException e) {
  		logger.error("Could Not Check Connection State", e);
  	}
  }
  */
  
  private void resetConnection() {
  	 try {
	  	connection = DriverManager.getConnection(GWTWeatherProperties.getConnectionString());
	  } catch(SQLException e) {
	  	logger.error("Could Not Make Connection To: " + GWTWeatherProperties.getConnectionString(), e);
	  }
  }
}