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
		float		fret = (((float)c0.get(Calendar.HOUR_OF_DAY)));	// TODO consider DST
		return	fret;
	}

	public String getDebugString(long ltime) {
		Calendar	c0 = Calendar.getInstance(m_tz);
		c0.setTimeInMillis(ltime);
		return	sdf0.format(c0.getTime());
	}
}
