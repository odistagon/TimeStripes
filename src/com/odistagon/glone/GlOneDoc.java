package com.odistagon.glone;

import java.util.ArrayList;

public class GlOneDoc
{
	private long				m_lTimeCurr;
	private long				m_lTimeAnimStart;
	private ArrayList<GloneTz>	m_artzs;

	public static final long	CL_ANIMPERD = 2500L;	// ms. until fling anim stops

	public GlOneDoc() {
		m_lTimeCurr = System.currentTimeMillis();

		// debug set
		m_artzs = new ArrayList<GloneTz>();
		m_artzs.add(GloneTz.getInstance("Japan"));
		m_artzs.add(GloneTz.getInstance("GMT"));
		m_artzs.add(GloneTz.getInstance("America/Los_Angeles"));
	}

	public void setTime(long ltime) {
		m_lTimeCurr = ltime;
	}

	public long getTime() {
		long	ldiff = System.currentTimeMillis() - m_lTimeAnimStart;
		if(ldiff < CL_ANIMPERD) {
			float	f0 = 1.0f - ((float)ldiff / (float)CL_ANIMPERD);
//			Log.d(getClass().getName(), "getTime r(" + f0 + ")");
			return	m_lTimeCurr - (long)((m_lTimeCurr - m_lTimeAnimStart) * f0);
		}
		return	m_lTimeCurr;
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

	public void addTime(long ltime, boolean bAnim) {
		if(bAnim) {
			m_lTimeAnimStart = System.currentTimeMillis();
		}
		m_lTimeCurr += ltime;
	}

}
