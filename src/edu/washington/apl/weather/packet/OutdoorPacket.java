package edu.washington.apl.weather.packet;

public class OutdoorPacket extends Packet {
	
/*

Example: 02 81 71 00 87 05 ff

02. 0	02		Outside temp/humidity data
02. 1	Bx		Battery status. Higher value == lower battery volt
02. 1 xB		Unknown
02. 1	xB		Sensor number bit encoded, 4=3 2=2 1=1
02. 2	DD		Outside temp, bc of -?<ab.c<? Celsius
02. 3	xD		Outside temp, a  of -?<ab.c<? Celsius
02. 3	Bx		Sign of outside temp, 1 = negative
02. 4	DD		Relative humidity, ab of ?<ab<? percent
02. 5	DD		Dew point, ab of 0<ab<? Celsius

Example: 7.1 Celsius, 87% humidity, dew point 5 Celsius

----

Example: 03 01 71 00 87 05 ff

03. 0	03		Outside temp/humidity data
03. 1	Bx		Battery status. Higher value == lower battery volt
03. 1 xB		Unknown
03. 2	DD		Outside temp, bc of -?<ab.c<? Celsius
03. 3	xD		Outside temp, a  of -?<ab.c<? Celsius
03. 3	Bx		Sign of outside temp, 1 = negative
03. 4	DD		Relative humidity, ab of ?<ab<? percent
03. 5	DD		Dew point, ab of 0<ab<? Celsius

Example: 7.1 Celsius, 87% humidity, dew point 5 Celsius

*/
	
	private int battery;
	private double temp;
	private int tempSign;
	private int humidity;
	private int dewpoint;
	
	public OutdoorPacket(byte[] packet) {
		super(OUTDOOR_T, packet);
		
		if(data.length != 6 || data[0] != OUTDOOR_2) {
			logger.warn("Attempt to instantiate OUTDOOR with invalid packet type");	
			type = INVALID_T;
			return;		
		}
		
		battery = data[1] >> 4;
		temp = ((data[3] & 0x0F) * 10) + (data[2] >> 4) +  ((data[2] & 0x0F) / 10.0);
		tempSign = ((data[3] >> 4) == 1) ? -1 : 1;
		humidity = ((data[4] >> 4) * 10) + (data[4] & 0x0F);
		dewpoint = ((data[5] >> 4) * 10) + (data[5] & 0x0F);
	}
	
	public String getInsertStatement() {
		if(type == INVALID_T) return null;
		return 
			"INSERT INTO temperature(date,battery,sensor,temperature,humidity,dew) VALUES('" + getTimestamp() + "'," + 
				battery + "," +
				OUTDOOR_2 + "," + 
				(temp * tempSign) + "," +
				humidity + "," + 
				dewpoint + 
			")";	
	}
		
	public String toString() {
		return super.toString()  + "{" + 
			" battery: " + battery + 
			", outside temp: " + (temp * tempSign) + "C" +  
			", humidity: " + humidity + "%" + 
			", dew point: " + dewpoint + "C" + 
			" }";

	}
}
