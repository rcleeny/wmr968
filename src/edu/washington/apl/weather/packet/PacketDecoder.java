package edu.washington.apl.weather.packet;

import edu.washington.apl.weather.util.WeatherProperties;

import org.apache.log4j.Logger;

import java.util.HashMap;

public class PacketDecoder {
	protected static Logger logger = Logger.getLogger(Packet.class);
	private boolean[] packetsReady = new boolean[] { true, true, true, true, false, false };
	private boolean[] packetsAlways = new boolean[] { false, false, false, false, false, false };
	
	private static final int MILLIS_PER_HOUR = 60 * 60 * 1000;
	
	public PacketDecoder() {
		
		HashMap<Integer, Double> rateMap = new HashMap<Integer, Double>();
		rateMap.put(Packet.ANENOMETER_T, WeatherProperties.getWindRate());
		rateMap.put(Packet.INDOOR_T, WeatherProperties.getIndoorRate());
		rateMap.put(Packet.OUTDOOR_T, WeatherProperties.getOutdoorRate());
		rateMap.put(Packet.RAIN_T, WeatherProperties.getRainRate());
		
		for(int type: rateMap.keySet()) {
			double rate = rateMap.get(type);
			
			if(rate > 0) new SleepTimer(type, getSleepInterval(rate)).start();
			else if(rate == 0) packetsAlways[type] = true;
			else packetsReady[type] = false;
		} 
	}
	
	// input: reads per hour
	// output: sleep duration in millis (millis per read)
	private static int getSleepInterval(double readsPerHour) {
		return (int)((1.0 / readsPerHour) * MILLIS_PER_HOUR);
	}
	
	// strip off two leading 0xFF and make packet array correct length
	private static byte[] strip(byte[] packet, int length) {
		byte[] p = new byte[length - 2];
		System.arraycopy(packet, 2, p, 0, length - 2);
		return p;
	}
	
	// trim packet array
	private static byte[] trim(byte[] packet, int length) {
		byte[] p = new byte[length];
		System.arraycopy(packet, 0, p, 0, length);
		return p;
	}
	
	public Packet decode(byte[] packet) {
		if(packet == null) return null;
		return decode(packet, packet.length);	
	}

	// packet should come in as valid checksummed packet
	public Packet decode(byte[] packet, int length) {
		if(packet == null || length <= 3 || ((packet[0] & 0xFF)  != 0xFF) || ((packet[1] & 0xFF)  != 0xFF)) {
			logger.error("packet format error");
			return null;
		}
	
		// make packet array actual length
		packet = trim(packet, length);
	
		/* packet should already be checksummed and valid 
		if(! Packet.checksum(packet)) {
			logger.error("packet checksum error");
			return null;
		}
		*/
		
		switch(packet[2]) {
			case Packet.ANENOMETER:
				if(packetsReady[Packet.ANENOMETER_T] || packetsAlways[Packet.ANENOMETER_T]) {
					packetsReady[Packet.ANENOMETER_T] = false;
					return new AnenometerPacket(packet);
				}
				break;
								
			case Packet.RAIN:
				if(packetsReady[Packet.RAIN_T] || packetsAlways[Packet.RAIN_T]) {
					packetsReady[Packet.RAIN_T] = false;
					return new RainPacket(packet);
				}
				break;
			
			case Packet.OUTDOOR_1:
			case Packet.OUTDOOR_2:
				if(packetsReady[Packet.OUTDOOR_T] || packetsAlways[Packet.OUTDOOR_T]) {
					packetsReady[Packet.OUTDOOR_T] = false;
					return new OutdoorPacket(packet);
				}
				break;
				
			case Packet.INDOOR_1:
			case Packet.INDOOR_2:
				if(packetsReady[Packet.INDOOR_T] || packetsAlways[Packet.INDOOR_T]) {
					packetsReady[Packet.INDOOR_T] = false;
					return new IndoorPacket(packet);
				}
				break;
							
			case Packet.SEQUENCE: 
				if(packetsReady[Packet.SEQUENCE_T] || packetsAlways[Packet.SEQUENCE_T]) {
					packetsReady[Packet.SEQUENCE_T] = false;
					return new SequencePacket(packet);
				}
				break;
				
			case Packet.STATUS:
				if(packetsReady[Packet.STATUS_T] || packetsAlways[Packet.STATUS_T]) {
					packetsReady[Packet.STATUS_T] = false;
					return new StatusPacket(packet);
				}
				break;
				
			default:
				logger.error("packet type error: " + packet[2]);
				return null;
		}
		
		logger.debug("packet type '" + Packet.PACKET_MAP.get(packet[2]) + "' not ready or not intended to be saved");
		return null;
	}
	
	private synchronized void timerRing(int type) {
		packetsReady[type] = true;
	}

	// this could be implemented better 
	private class SleepTimer extends Thread {
		private int type = 0;
		private int millis = 0;
		
		public SleepTimer(int type, int millis) {
			this.type = type;
			this.millis = millis;	
		}	
		
		public void run() {
			try {
				while(true) {
					this.sleep(millis);
					timerRing(type);
				}
			} catch(Exception e) {
				logger.error("timer thread death from sleep error", e);
			}
		}
	}
}
