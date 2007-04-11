package edu.washington.apl.weather.packet;

public class StatusPacket extends Packet {
	
/*
Example: 0f 80 07 09 03 00 a0

Byte	Nibble	Bit	Meaning
0f. 0	0f		Hour Chime
0f. 1	BB		Status. High bit == battery low
0f. 2	DD		Hour 
0f. 3	DD		Day 
0f. 4	DD		Month 
0f. 5	DD		Year 

Example: 7AM, 9-March-2000
*/

	private int battery;
	private int hour;
	private int day;
	private int month;
	private int year;
	
	public StatusPacket(byte[] packet) {
		super(STATUS_T, packet);
		
		if(data.length != 6 || data[0] != STATUS) {
			logger.warn("Attempt to instantiate STATUS with invalid packet type");	
			type = INVALID_T;
			return;		
		}
		
		battery = data[1] >> 4;
		hour = ((data[2] >> 4) * 10) + (data[2] & 0x0F);
		day = ((data[3] >> 4) * 10) + (data[3] & 0x0F);
		month = ((data[4] >> 4) * 10) + (data[4] & 0x0F);
		year = ((data[5] >> 4) * 10) + (data[5] & 0x0F);
	}
	
	public String getInsertStatement() {
		return null;	
	}
	
	public String toString() {
		return super.toString() +"{" + 
			" battery: " + battery + 
			", hour: " + hour + 
			", date: " + month + "-" + day + "-" + year + 
			" }";
	}
}
