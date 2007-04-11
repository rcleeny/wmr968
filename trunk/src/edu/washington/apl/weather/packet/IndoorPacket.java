package edu.washington.apl.weather.packet;

import java.util.HashMap;

public class IndoorPacket extends Packet {
	
/*
	Example: 05 00 11 02 46 09 dc 0c 50 79 16

Byte	Nibble	Bit	Meaning
05. 0	05		Inside sensor data
05. 1	Bx		Battery status. Higher value == lower battery volt
05. 1 xB		Unknown
05. 2	DD		Inside temp, bc of -?<ab.c<? Celsius
05. 3	xD		Inside temp, a  of -?<ab.c<? Celsius
05. 3	Bx		Sign of temp, 1 = negative
05. 4	DD		Relative humidity, ab of ?<ab<? percent
05. 5	DD		Dew point, ab of 0<ab<? Celsius
05. 6	HH		Baro pressure, convert to decimal and add 795. mb.
05. 7	Bx		Unknown
05. 7	xB		Encoded 'tendency' 0x0c=clear 0x06=partly cloudy 0x02=cloudy 0x03=rain
05. 8	DD		Sea level reference, cd of <abc.d>. 
05. 9	DD		Sea level reference, ab of <abc.d>. Add this to raw bp from byte 6 to get sea level pressure.

Example: 21.1 Celsius, 46% humidity, dew point 9 Celsius, BP 1015 mb, sea level pressure 1015 mb.

----

Example: 06 00 29 02 41 09 8b 61 90 33 06 2e
         06 20 24 02 47 10 A4 21 00 00 06 6C
          0  1  2  3  4  5  6  7  8  9 10 11

Byte	Nibble	Bit	Meaning
06. 0	06		Inside sensor data
06. 1	Bx		Battery status. Higher value == lower battery volt
06. 1 xB		Unknown
06. 2	DD		Inside temp, bc of -?<ab.c<? Celsius
06. 3	xD		Inside temp, a  of -?<ab.c<? Celsius
06. 3	Bx		Sign of temp, 1 = negative
06. 4	DD		Relative humidity, ab of ?<ab<? percent
06. 5	DD		Dew point, ab of 0<ab<? Celsius
06. 6	HH		Baro pressure, low byte (bc of abc)
06. 7	xB		Baro pressure, a of abc. Convert abc from hex to decimal, add 600. to get mb. a is low bit only. Upper three bits may be encoded BP tendency.
06. 7	Bx		Encoded 'tendency' 0x0c=clear 0x06=partly cloudy 0x02=cloudy 0x03=rain
06. 8	DD		Sea level reference, ef of <abcd.ef>. 
06. 9	DD		Sea level reference, cd of <abcd.ef>. 
06.10 DD    Sea level reference, ab of <abcd.ef> Add this to raw bp from byte 6 and 7 to get sea level pressure.

	Example: 22.9 Celsius, 41% humidity, dew point 9 Celsius, BP 995 mb, sea level pressure 1028.9 mb.
*/

	private static final int CLEAR = 0x0C;
	private static final int PT_CLOUDY = 0x06;
	private static final int CLOUDY = 0x02;
	private static final int RAIN = 0x03;
	
	private int battery;
	private double temp;
	private int tempSign;
	private int humidity;
	private int dewpoint;
	private int pressure;
	private int tendency;
	private double searef;
	private double seapress;
	
	public IndoorPacket(byte[] packet) {
		super(INDOOR_T, packet);
		
		if(data.length != 11 || data[0] != INDOOR_2) {
			logger.warn("Attempt to instantiate INDOOR with invalid packet type");	
			type = INVALID_T;
			return;		
		}
		
		battery = data[1] >> 4;
		temp = ((data[3] & 0x0F) * 10) + (data[2] >> 4) +  ((data[2] & 0x0F) / 10.0);
		tempSign = ((data[3] >> 4) == 1) ? -1 : 1;
		humidity = ((data[4] >> 4) * 10) + (data[4] & 0x0F);
		dewpoint = ((data[5] >> 4) * 10) + (data[5] & 0x0F);
		pressure = (((data[7] & 0xF) << 8) | data[6]) + 600;
		tendency = data[7] >> 4;

		searef = ((data[10] >> 4) * 1000) + ((data[10] & 0x0F) * 100);
		searef += ((data[9] >> 4) * 10) + ((data[9] & 0x0F) * 1);
		searef += (((data[8] >> 4) * 10) + ((data[8] & 0x0F) * 1)) / 100.0;
		
		seapress = pressure + searef;
	}
	
	public String getInsertStatement() {
		if(type == INVALID_T) return null;
		return 
			"INSERT INTO temperature VALUES('" + getTimestamp() + "'," + 
				battery + "," +
				INDOOR_2 + "," + 
				(temp * tempSign) + "," +
				humidity + "," + 
				dewpoint + "," + 
				pressure + "," + 
				tendency + 
			")";
	}
	
	public String toString() {
		return super.toString() + "{" + 
			" battery: " + battery + 
			", inside temp: " + (temp * tempSign) + "C" +  
			", humidity: " + humidity + "%" + 
			", dew point: " + dewpoint + "C" + 
			", baro pressure: " + pressure + "mb" + 
			", tendency: " + tendency + 
			", sea level reference: " + searef + 
			", sea level pressure: " + seapress + "mb" + 
			" }";
	}
}
