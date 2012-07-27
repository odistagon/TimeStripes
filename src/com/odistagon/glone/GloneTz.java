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
		int			anret[] = new int[6];
		anret[0] = c0.get(Calendar.YEAR);
		anret[1] = c0.get(Calendar.MONTH) + 1;
		anret[2] = c0.get(Calendar.DAY_OF_MONTH);
		anret[3] = c0.get(Calendar.DAY_OF_WEEK);
		anret[4] = c0.get(Calendar.HOUR_OF_DAY);
		anret[5] = c0.get(Calendar.MINUTE);
		return	anret;
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
}
