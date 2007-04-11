package edu.washington.apl.weather;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.SerialPortEvent;

import org.apache.log4j.Logger;

import edu.washington.apl.weather.packet.Packet;
import edu.washington.apl.weather.packet.PacketDecoder;
import edu.washington.apl.weather.util.WeatherProperties;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import java.io.InputStream;

public class WMR968 implements SerialPortEventListener, Runnable {
	
	// make them members so we can close them with shutdown signal hook
  private SerialPort port = null;
  private InputStream stream = null;
  
 	private Connection connection = null;
	private PacketDecoder decoder = null;
  
  private static Logger logger = Logger.getLogger(WMR968.class);
  
  public WMR968() {
  	this("weather.properties");	
  }
    
	public WMR968(String pfile) {
		Runtime.getRuntime().addShutdownHook(new Thread(this));
		
		try {
			WeatherProperties.load(pfile);
			logger.debug(WeatherProperties.getInstance());
		} catch(Exception e) {
			logger.error("Could not find properties file: " + pfile, e);
			return;
		}
		
		try {
			Class.forName(WeatherProperties.getDriver()).newInstance();
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
		
	  try {
	  	connection = DriverManager.getConnection(WeatherProperties.getConnectionString());
	  } catch(SQLException e) {
	  	logger.error("Could Not Make Connection To: " + WeatherProperties.getConnectionString(), e);
	  	return;	
	  }
	  
	  decoder = new PacketDecoder();
	  		
		try {
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(WeatherProperties.getDevice());
			port = (SerialPort) portId.open("WMR968", 100000);
			stream = port.getInputStream();
						
			// default settings
			port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			
			// killed reads ... would block on read forever when these were set
      // port.setDTR(false);
      // port.setRTS(false);
			
			port.notifyOnDataAvailable(true);
			port.addEventListener(this);

		} catch(Exception e) {
			logger.error("ERROR Setting Up Ports & Streams", e);
		}
	}

	// packet buffer and length
	private byte[] buffer = new byte[128];
	private int blength = 0;

	// single packet and length
	private byte[] packet = new byte[128];
	private int plength = 0;

	// length of validated packet
	private int length = 0;

	public void serialEvent(SerialPortEvent e) {
		if(e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			
			try {
				plength = stream.read(packet);
				// logger.debug("packet: " + Packet.toString(packet, plength));

				// check for an invalid buffer ... we have a non-zero buffer but new packet comes in
				// note: this will fail if the two 0xFF 0xFF headers don't come together (very possible, normal in mac testing)
				if(plength >= 2 && blength > 0 && ((packet[0] & 0xFF) == 0xFF) && ((packet[1] & 0xFF) == 0xFF)) {
					logger.debug("invalid packet in buffer -- clearing buffer");
					blength = 0;
				}

				System.arraycopy(packet, 0, buffer, blength, plength);
				blength += plength;
				// logger.debug("buffer: " + Packet.toString(buffer, blength));

				// if we get a valid packet we want to save the length and clear the buffer
				if(Packet.checksum(buffer, blength)) {
					length = blength;
					blength = 0;
				// otherwise keep buffering
				} else return;
	
				Packet p = decoder.decode(buffer, length);
				
				if(p == null || p.getType() == Packet.INVALID_T) {
					logger.debug("packet could not be created");
					return;
				}
				logger.info(p);

				Statement s = connection.createStatement();

				String insert = p.getInsertStatement();
				logger.debug(insert);
				
				if(insert == null) {
					logger.debug("insert statement could not be created");	
					return;
				}
				
				s.execute(insert);
				s.close();

			} catch(SQLException exp) {
					logger.error("Problem With SQL", exp);
			  	return;	
			} catch(Exception exp) {
				logger.error("Problem With Packet", exp);
			}
		}
	}
	
	public static void main(String[] args) {
		new WMR968();
	}
	
	// runnable method for cleaning up when shutdown initiated
	public void run() {
		logger.info("Closing Databases, Streams, & Ports ...");
		
		try {
			if(stream != null) stream.close();
			if(port != null) port.close();	
			if(connection != null) connection.close();
		} catch(Exception e) {
			logger.error("ERROR Closing Databases, Streams, & Ports", e);
		}
		
		logger.info("All closed.");
	}
}
