package edu.washington.apl.weather.packet;

import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.washington.apl.weather.db.SQLPacket;

public abstract class Packet implements SQLPacket {

	public static final byte ANENOMETER = 0x00;
	public static final byte RAIN = 0x01;
	public static final byte OUTDOOR_1 = 0x02;
	public static final byte OUTDOOR_2 = 0x03;
	public static final byte INDOOR_1 = 0x05;
	public static final byte INDOOR_2 = 0x06;
	public static final byte SEQUENCE = 0x0e;
	public static final byte STATUS = 0x0f;
	public static final byte TOTAL = 0x10;
	
	public static final int ANENOMETER_T = 0;
	public static final int INDOOR_T = 1;
	public static final int OUTDOOR_T = 2;
	public static final int RAIN_T = 3;
	public static final int SEQUENCE_T = 4;
	public static final int STATUS_T = 5;
	public static final int UNKNOWN_T = 6;
	public static final int INVALID_T = 7;
	
	public static final String[] PACKET_TYPES = {
		"Anenometer", "Indoor", "Outdoor", "Rain", "Sequence", "Status", "Unknown", "Invalid"
	};
	
	public static final HashMap<Byte, String> PACKET_MAP = new HashMap<Byte, String>();
	
	static {
		PACKET_MAP.put(ANENOMETER, "Anenometer");
		PACKET_MAP.put(RAIN, "Rain");
		PACKET_MAP.put(OUTDOOR_1, "Outdoor");
		PACKET_MAP.put(OUTDOOR_2, "Outdoor");
		PACKET_MAP.put(INDOOR_1, "Indoor");
		PACKET_MAP.put(INDOOR_2, "Indoor");
		PACKET_MAP.put(SEQUENCE, "Sequence");
		PACKET_MAP.put(STATUS, "Status");
	}

	protected static Logger logger = Logger.getLogger(Packet.class);
	protected byte[] packet = null;
	protected int[] data = null;
	protected int type = UNKNOWN_T;
	protected long timestamp = 0;
	
	public Packet(int type, byte[] packet) {
		this.type = type;
		this.packet = packet;
		
		// data is int expanded array of byte packet array
		this.data = convert(packet);
		
		this.timestamp = System.currentTimeMillis();
	}
	
	// for SQLPacket implementation
	public abstract String getInsertStatement();
	
	protected Timestamp getTimestamp() {
		return new Timestamp(timestamp);		
	}

	public boolean checksum() {
		return Packet.checksum(packet, packet.length);
	}

	public static boolean checksum(byte[] packet) {
		if(packet == null) return false;
		return Packet.checksum(packet, packet.length);
	}
	
	public static boolean checksum(byte[] packet, int length) {
		if(packet == null || length == 0) return false;

		int total = 0;
		
		for(int i = 0; i < length - 1; i++) total += (packet[i] & 0xFF);
	
		// checksum is last two bytes only
		if((total & 0xFF) == (packet[length - 1] & 0xFF)) return true;
		return false; 
	}
	
	// convert bytes to ints (easier to shift bits), remove leading 0xFFs, and trailing checksum
	protected static int[] convert(byte[] packet) {
		int[] p = new int[packet.length - 3];
		
		// would arrayscopy work here?
		for(int i = 2; i < packet.length - 1; i++) {
			p[i - 2] = packet[i] & 0xFF;
		}
		
		return p;
	}
	
	public int getType() {
		return type;
	}
	
	public String toString() {
		if(packet == null) return "no packet data";
		return PACKET_TYPES[type] + " -- " + toString(packet);
	}
	
	public static String toString(byte[] packet) {
		if(packet == null) return null;
		return toString(packet, packet.length);	
	}
	
	public static String toString(byte[] packet, int length) {
		StringBuffer buffer = new StringBuffer("length [" + length + "]: ");
		for(int i = 0; i < length; i++) {
			buffer.append(String.format("%02X ", packet[i]));
		}
		return buffer.toString();
	}
}
