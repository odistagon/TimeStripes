package com.odistagon.glone;

import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class GloneTz
{
	private String			m_sTzId;
	private TimeZone		m_tz;
	private Calendar		m_cal;			// cache: recycle this instance and don't use Calendar.getInstance()
	private int[]			m_andate;		// cache:
//	private static final String	CS_TZSTRTEST = "America/Los Angeles";
	private FloatBuffer		m_fbvtx;
	private FloatBuffer		m_fbtex;

	private static SimpleDateFormat	sdf0 = new SimpleDateFormat("MMM/dd HH:mm:ss Z");

	public static GloneTz getInstance(String sTz) {
		GloneTz	tz0 = new GloneTz();
		tz0.m_sTzId = sTz;
		tz0.m_tz = TimeZone.getTimeZone(sTz);

		GlStripe.makeTzNameBuffs(tz0);

		return	tz0;
	}

	public String getTimeZoneId() {
		return	m_sTzId;
	}

	public TimeZone getTimeZone() {
		return	m_tz;
	}

	/** Calendar.getInstance cost much so recycle the instance.
	 * @return
	 */
	private Calendar recycleCalendarInstance() {
		if(m_cal == null)
			m_cal = Calendar.getInstance(m_tz);
		return	m_cal;
	}

	/**
	 * @returns (0.0f-24.0f)
	 */
	public float getTimeOffsetInADay(long ltime) {
		Calendar	c0 = recycleCalendarInstance();
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
		Calendar	c0 = recycleCalendarInstance();
		c0.setTimeInMillis(ltime);
		if(m_andate == null)
			m_andate = new int[7];
		m_andate[0] = c0.get(Calendar.YEAR);
		m_andate[1] = c0.get(Calendar.MONTH) + 1;		// -> 1 based
		m_andate[2] = c0.get(Calendar.DAY_OF_MONTH);
		m_andate[3] = c0.get(Calendar.DAY_OF_WEEK);
		m_andate[4] = c0.get(Calendar.HOUR_OF_DAY);
		m_andate[5] = c0.get(Calendar.MINUTE);
//		m_andate[6] = c0.get(Calendar.DST_OFFSET);		// daylight savings offset in milliseconds
		return	m_andate;
	}

	public float getHoursFromMidNight(long ltime) {
		Calendar	c0 = recycleCalendarInstance();
		c0.setTimeInMillis(ltime);
		c0.set(Calendar.HOUR_OF_DAY, 0);
		c0.set(Calendar.MINUTE, 0);
		return	((float)ltime - c0.getTimeInMillis()) / (60f * 60f * 1000);
	}

	/** Calculate how many offset time the specified day has.
	 * @param ltime
	 * @return
	 */
	public float getDSTOffsetInTheDay(long ltime, int noffset) {
		Calendar	c0 = recycleCalendarInstance();
		c0.setTimeInMillis(ltime);
		c0.add(Calendar.DATE, noffset);
		c0.set(Calendar.HOUR_OF_DAY, 0);
		int			n0 = c0.get(Calendar.DST_OFFSET);
		c0.set(Calendar.HOUR_OF_DAY, 3);
		float	fret = (float)(n0 - c0.get(Calendar.DST_OFFSET)) / (float)(60 * 60 * 1000);
		return	fret;
	}

	public String getDebugString(long ltime) {
		Calendar	c0 = recycleCalendarInstance();
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

		Calendar	c0 = recycleCalendarInstance();
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
			c0.add(Calendar.DATE, (bnext ? +1 : -1));
		}
		c0.set(Calendar.HOUR_OF_DAY, 1);
		return	c0.getTimeInMillis();
	}

	public FloatBuffer getFbVtx() {
		return	m_fbvtx;
	}

	public FloatBuffer getFbTex() {
		return	m_fbtex;
	}

	public void setFbVtx(FloatBuffer fbvtx) {
		m_fbvtx = fbvtx;
	}

	public void setFbTex(FloatBuffer fbtex) {
		m_fbtex = fbtex;
	}
}
