package com.odistagon.glone;

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

	public void addTime(long ltime) {
		m_ltime += ltime;
	}

}
