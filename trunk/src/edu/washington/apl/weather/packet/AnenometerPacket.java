package edu.washington.apl.weather.packet;

public class AnenometerPacket extends Packet {
	
/*
Example: 00 00 90 01 00 00 00 07 96

Byte	Nibble	Bit	Meaning
00. 0	00		Anemometer data packet
00. 1	Bx		Battery status. Higher value == lower battery volt
00. 1 xB		Unknown
00. 2	DD		Gust direction, bc of 0<abc<359 degrees
00. 3	xD		Gust direction, a  of 0<abc<359 degrees
00. 3	Dx		Gust speed, c  of 0<ab.c<56 m/s
00. 4	DD		Gust speed, ab of 0<ab.c<56 m/s
00. 5	DD		Average speed, bc  of 0<ab.c<56 m/s
00. 6	xD		Average speed, a of 0<ab.c<56 m/s
00. 6	Bx		Sign of wind chill, 1 = negative
00. 7	DD		Wind chill

Example: gust at 190 degrees, 0 m/s, average 0, wind chill 7 Celsius
*/

	private int battery;
	private int bearing;
	private double gustSpeed;
	private double avgSpeed;
	private int windChill;
	private int windChillSign;
	
	public AnenometerPacket(byte[] packet) {
		super(ANENOMETER_T, packet);
		
		if(data.length != 8 || data[0] != ANENOMETER) {
			logger.warn("Attempt to instantiate ANENOMETER with invalid packet type");	
			type = INVALID_T;
			return;		
		}
		
		battery = data[1] >> 4;
		bearing = ((data[3] & 0x0F) * 100) + ((data[2] >> 4) * 10) + (data[2] & 0x0F);
		gustSpeed = ((data[4] >> 4) * 10) + (data[4] & 0x0F) + ((data[3] >> 4) / 10.0);
		avgSpeed = ((data[6] & 0x0F) * 10) + (data[5] >> 4) +  ((data[5] & 0x0F) / 10.0);
		windChillSign = ((data[6] >> 4) == 1) ? -1 : 1;
		windChill = ((data[7] >> 4) * 10) + (data[7] & 0x0F);
	}
	
	public String getInsertStatement() {
		if(type == INVALID_T) return null;
		return 
			"INSERT INTO wind VALUES('" + getTimestamp() + "'," + 
				battery + "," +
				bearing + "," + 
				gustSpeed + "," +
				avgSpeed + "," + 
				(windChill * windChillSign) + 
			")";
	}
		
	public String toString() {
		return super.toString() + "{" + 
			" battery: " + battery + 
			", bearing: " + bearing + 
			", gust speed: " + gustSpeed + 
			", average speed: " + avgSpeed + 
			", wind chill: " + (windChill * windChillSign) + "C" +
			" }";
	}
}
