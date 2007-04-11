package edu.washington.apl.weather.packet;

public class SequencePacket extends Packet {
	
/*
Example: 0e 81 8d

Byte	Nibble	Bit	Meaning
0e. 0	0e		Sequence number packet
0e. 1	Bx		Status, high bit, battery for main unit
0e. 1	DD		After removing high bit, minute chime

Example: status = 1, time is hh:01

RJC Example: 0E 04 10
** no idea what other field is

*/
	
	private int battery;
	
	public SequencePacket(byte[] packet) {
		super(SEQUENCE_T, packet);
		
		if(data.length != 2 || data[0] != SEQUENCE) {
			logger.warn("Attempt to instantiate SEQUENCE with invalid packet type");	
			type = INVALID_T;
			return;		
		}
		
		battery = data[1] >> 4;
	}
	
	public String getInsertStatement() {
		return null;	
	}
	
	public String toString() {
		return super.toString()  +"{" + 
			" battery: " + battery + 
			" }";
	}
}
