package com.odistagon.glone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class GloneTz
{
	private String		m_sTzId;
	private TimeZone	m_tz;

	private static SimpleDateFormat	sdf0 = new SimpleDateFormat("MMM/dd HH:mm:ss Z");

	public static GloneTz getInstance(String sTz) {
		GloneTz	tz0 = new GloneTz();
		tz0.m_sTzId = sTz;
		tz0.m_tz = TimeZone.getTimeZone(sTz);
		return	tz0;
	}

	public String getTimeZoneId() {
		return	m_sTzId;
	}

	public TimeZone getTimeZone() {
		return	m_tz;
	}

	/**
	 * @returns (0.0f-24.0f)
	 */
	public float getTimeOffsetInADay(long ltime) {
		Calendar	c0 = Calendar.getInstance(m_tz);
		c0.setTimeInMillis(ltime);
		float		fret = (((float)c0.get(Calendar.HOUR_OF_DAY))
				+ (float)c0.get(Calendar.MINUTE) / 60.0f);	// TODO consider DST
		return	fret;
	}

//	/**
//	 * @returns offset min (0.0f-1.0f)
//	 */
//	public float getTimeOffsetInMinute(long ltime) {
//		Calendar	c0 = Calendar.getInstance(m_tz);
//		c0.setTimeInMillis(ltime);
//		float		fret = ((float)c0.get(Calendar.MINUTE) / 60.0f);
//		return	fret;
//	}

	/**
	 * @return year, month, day, day of month, hour, minute
	 */
	public int[] getTimeNumbers(long ltime) {
		Calendar	c0 = Calendar.getInstance(m_tz);
		c0.setTimeInMillis(ltime);
		int			anret[] = new int[7];
		anret[0] = c0.get(Calendar.YEAR);
		anret[1] = c0.get(Calendar.MONTH) + 1;		// -> 1 based
		anret[2] = c0.get(Calendar.DAY_OF_MONTH);
		anret[3] = c0.get(Calendar.DAY_OF_WEEK);
		anret[4] = c0.get(Calendar.HOUR_OF_DAY);
		anret[5] = c0.get(Calendar.MINUTE);
//		anret[6] = c0.get(Calendar.DST_OFFSET);		// daylight savings offset in milliseconds
		return	anret;
	}

	/**
	 * @param ltime
	 * @return DST offset at the moment in milliseconds.
	 */
	public long getDSTOffsetToNextDay(long ltime) {
		Calendar	c0 = Calendar.getInstance(m_tz);
		c0.setTimeInMillis(ltime);
		long		l0 = c0.get(Calendar.DST_OFFSET);
		c0.setTimeInMillis(ltime + (24 * 60 * 60 * 1000));
		return	(l0 - c0.get(Calendar.DST_OFFSET));
	}

	public float getHoursFromMidNight(long ltime) {
		Calendar	c0 = Calendar.getInstance(getTimeZone());
		c0.setTimeInMillis(ltime);
		c0.set(Calendar.HOUR_OF_DAY, 0);
		c0.set(Calendar.MINUTE, 0);
		return	((float)ltime - c0.getTimeInMillis()) / (60f * 60f * 1000);
	}

	/** Calculate how many offset time the specified day has.
	 * @param ltime
	 * @return
	 */
	public float getDSTOffsetInTheDay(long ltime) {
		Calendar	c0 = Calendar.getInstance(getTimeZone());
		c0.setTimeInMillis(ltime);
		long		lhr0 = ltime - (c0.get(Calendar.HOUR_OF_DAY) * (60 * 60 * 1000));	// 0 or 1 a.m.
		c0.setTimeInMillis(lhr0);
		int			n0 = c0.get(Calendar.DST_OFFSET);
		long		lhr3 = lhr0 + (3 * (60 * 60 * 1000));	// 3 hrs later from 0 a.m. (2, 3 or 4 a.m.)
		c0.setTimeInMillis(lhr3);
		return	(float)(n0 - c0.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000);
	}

	public String getDebugString(long ltime) {
		Calendar	c0 = Calendar.getInstance(m_tz);
		c0.setTimeInMillis(ltime);
		sdf0.setTimeZone(m_tz);
		return	sdf0.format(c0.getTime());
	}

	public void update(GloneTz gtz) {
		m_sTzId = gtz.m_sTzId;
		m_tz = gtz.m_tz;
	}

	/**
	 * @param ltime
	 * @param bnext
	 * @return next or previous dst offset change date in milliseconds.
	 */
	public long findNextDstChange(long ltime, boolean bnext) {
		if(!getTimeZone().useDaylightTime())
			return	-1;

		Calendar	c0 = Calendar.getInstance(getTimeZone());
		c0.setTimeInMillis(ltime);
		long		lcurr = c0.get(Calendar.DST_OFFSET);
		c0.set(Calendar.HOUR_OF_DAY, (bnext ? 3 : 1));
		c0.set(Calendar.MINUTE, 0);
		for(int i = 0; i < 300; i++) {	// note Calendar.DAY_OF_YEAR is not the number of days within year
			long	l0 = c0.get(Calendar.DST_OFFSET);
//			Log.d("XXXX", "(" + c0.getTime() + ", " + lcurr + ", " + l0 + ")");
			if(lcurr != l0)
				break;
//			c0.roll(Calendar.DAY_OF_MONTH, bnext);	// never rolls month. completely useless.
			c0.setTimeInMillis(c0.getTimeInMillis()
					+ (24L * 60L * 60L * 1000L) * (bnext ? +1L : -1L));
		}
		return	c0.getTimeInMillis();
	}
}
