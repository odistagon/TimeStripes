package com.odistagon.glone;

import java.util.Calendar;

public class GlOneDoc
{
	private long			m_ltime;

	public GlOneDoc() {
		m_ltime = System.currentTimeMillis();
	}

	public void setTime(long ltime) {
		m_ltime = ltime;
	}

	public long getTime() {
		return	m_ltime;
	}

	/* @returns time offset (0.0f-24.0f)
	 */
	public float getTimeOffset() {
		// TODO cache hour number (don't calc every time)
		Calendar	c0 = Calendar.getInstance();
		c0.setTimeInMillis(m_ltime);
		float		fret = (((float)c0.get(Calendar.HOUR_OF_DAY)));	// TODO consider DST
		return	fret;
	}

	public void addTime(long ltime) {
		m_ltime += ltime;
	}

}
