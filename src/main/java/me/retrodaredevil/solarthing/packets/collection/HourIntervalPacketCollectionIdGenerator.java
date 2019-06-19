package me.retrodaredevil.solarthing.packets.collection;

import java.util.Calendar;

/**
 * A {@link PacketCollectionIdGenerator} that generates a certain number of unique IDs per hour.
 */
public final class HourIntervalPacketCollectionIdGenerator implements PacketCollectionIdGenerator {
	
	private final int uniqueIdsInOneHour;
	private final Integer uniqueCode;
	
	/**
	 * @param uniqueIdsInOneHour The number of unique ids in an hour.
	 */
	public HourIntervalPacketCollectionIdGenerator(int uniqueIdsInOneHour, Integer uniqueCode) {
		this.uniqueIdsInOneHour = uniqueIdsInOneHour;
		if(uniqueIdsInOneHour <= 0){
			throw new IllegalArgumentException("uniqueIdsInOneHour cannot be <= 0. It is: " + uniqueIdsInOneHour);
		}
		this.uniqueCode = uniqueCode;
	}
	
	@Override
	public String generateId(Calendar cal) {
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH) + 1; // [1..12]
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int hour = cal.get(Calendar.HOUR_OF_DAY);
		final int minute = cal.get(Calendar.MINUTE);
		final int second = cal.get(Calendar.SECOND);
		final int millisecond = cal.get(Calendar.MILLISECOND);
		final double percent = (millisecond + 1000 * (second + (60 * minute))) / (1000.0 * 60.0 * 60.0);
		String r = "" + year + "," + month + "," + day + "," +
			hour + ",(" + ((int) (percent * uniqueIdsInOneHour) + 1) + "/" + uniqueIdsInOneHour + ")";
		if(uniqueCode != null){
			r += ",[" + Integer.toHexString(uniqueCode) + "]";
		}
		return r;
	}
}
