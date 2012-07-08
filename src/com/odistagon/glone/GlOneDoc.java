package com.odistagon.glone;

import java.util.ArrayList;
import java.util.Calendar;

public class GlOneDoc
{
	private long				m_ltime;
	private ArrayList<GloneTz>	m_artzs;

	public GlOneDoc() {
		m_ltime = System.currentTimeMillis();

		// debug set
		m_artzs = new ArrayList<GloneTz>();
		m_artzs.add(GloneTz.getInstance("Japan"));
		m_artzs.add(GloneTz.getInstance("GMT"));
		m_artzs.add(GloneTz.getInstance("America/Los_Angeles"));
	}

	public void setTime(long ltime) {
		m_ltime = ltime;
	}

	public long getTime() {
		return	m_ltime;
	}

	public ArrayList<GloneTz> getTzList() {
		return	m_artzs;
	}

//	/* @returns time offset (0.0f-24.0f)
//	 */
//	public float getTimeOffset() {
//		// TODO cache hour number (don't calc every time)
//		Calendar	c0 = Calendar.getInstance();
//		c0.setTimeInMillis(m_ltime);
//		float		fret = (((float)c0.get(Calendar.HOUR_OF_DAY)));	// TODO consider DST
//		return	fret;
//	}

	public void addTime(long ltime) {
		m_ltime += ltime;
	}

}
