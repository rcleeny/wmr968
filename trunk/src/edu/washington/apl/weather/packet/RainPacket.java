package edu.washington.apl.weather.packet;

public class RainPacket extends Packet {
	
/*

Example: 01 00 92 62 02 00 00 00 15 21 09 03 00 37

01. 0	01		Rain guage packet
01. 1	Bx		Battery status. Higher value == lower battery volt
01. 1 xB		Unknown
01. 2	DD		Rain rate, bc of 0<abc<999 mm/hr
01. 3	xD		Rain rate, a  of 0<abc<999 mm/hr
01. 3	Dx		Bucket tips since ??
01. 4	DD		Rain Total, cd of 0<abcd<9999 mm (?)
01. 5	DD		Rain Total, ab of 0<abcd<9999 mm (?)
01. 6	DD		Rain Yesterday, cd of 0<abdc<9999 mm (?)
01. 7	DD		Rain Yesterday, ab of 0<abcd<9999 mm (?)
01. 8	DD		Total reset minute
01. 9	DD		Total reset hour
01.10	DD		Total reset day
01.11	DD		Total reset month
01.12	DD		Total reset year

Example: 292 mm/hr, 0 yesterday, 2mm total since 21:15 9-Mar-2000
*/

	private int battery;
	private int rate;
	private int tips;
	private int total;
	private int yesterday;
	private int minute;
	private int hour;
	private int day;
	private int month;
	private int year;
	
	public RainPacket(byte[] packet) {
		super(RAIN_T, packet);
		
		if(data.length != 13 || data[0] != RAIN) {
			logger.warn("Attempt to instantiate RAIN with invalid packet type");	
			type = INVALID_T;
			return;		
		}
		
		battery = data[1] >> 4;
		rate = ((data[3] & 0x0F) * 100) + ((data[2] >> 4) * 10) + (data[2] & 0x0F);
		tips = data[3] >> 4;
		total = ((data[5] >> 4) * 1000) + ((data[5] & 0x0F) * 100) + ((data[4] >> 4) * 10) + (data[4] & 0x0F);
		yesterday = ((data[7] >> 4) * 1000) + ((data[7] & 0x0F) * 100) + ((data[6] >> 4) * 10) + (data[6] & 0x0F);
		
		minute = ((data[8] >> 4) * 10) + (data[8] & 0x0F);
		hour = ((data[9] >> 4) * 10) + (data[9] & 0x0F);
		day = ((data[10] >> 4) * 10) + (data[10] & 0x0F);
		month = ((data[11] >> 4) * 10) + (data[11] & 0x0F);
		year = ((data[12] >> 4) * 10) + (data[12] & 0x0F);
	}
	
	public String getInsertStatement() {
		if(type == INVALID_T) return null;
		
		// ** doesn't work, 2005 comes back as 5, not 105 
		// int pre = (year < 100) ? 19 : 20;
		
		// after 2099 this won't work :)
		int pre = (year == 99) ? 19 : 20;

		return 
			"INSERT INTO rain VALUES('" + getTimestamp() + "'," + 
				battery + "," +
				rate + "," + 
				total + "," +
				yesterday + "," +
				tips + "," + 
				"'" + pre + "" + String.format("%02d", year) + "-" + month + "-" + day + " " + String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":00'" +
			")";
	}
	
	public String toString() {
		return super.toString() + "{" + 
			" battery: " + battery +
			", rate: " + rate + "mm/hr" + 
			", total rain: " + total + "mm" +  
			", rain yesterday: " + yesterday + "mm" +  
			", bucket tips: " + tips + 
			", reset date: " + hour + ":" + minute + ", " + day + "-" + month + "-" + year +
			" }";
	}
}
