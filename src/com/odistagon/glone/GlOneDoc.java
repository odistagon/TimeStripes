package com.odistagon.glone;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.FloatMath;
import android.util.Log;

public class GlOneDoc
{
	private long				m_lTimeCurr, m_lTimePrev;
	private long				m_lTimeAnimStart;
	private ArrayList<GloneTz>	m_artzs;

	public static final long	CL_ANIMPERD = 2500L;	// ms. until fling anim stops

	public GlOneDoc() {
		m_lTimeCurr = System.currentTimeMillis();

		readConfig();
	}

	public void readConfig() {
		String sJson = "{"
			+ "	\"globalbg\" : \"bg.png\","
			+ "	\"usethemebg\" : true,"
			+ "	\"curr-tzset\" : \"default tz set\","
			+ "	\"curr-theme\" : \"default theme\","
			+ "	\"tzset-list\" : ["
			+ "		{"
			+ "			\"name\" : \"default tz set\","
			+ "			\"tz-list\" : ["
			+ "				{"
			+ "					\"name\" : \"JP\","
			+ "					\"timezone\" : \"Japan\","
			+ "					\"color\" : \"#FF000000\""
			+ "				},"
			+ "				{"
			+ "					\"name\" : \"London\","
			+ "					\"timezone\" : \"GMT\","
			+ "					\"color\" : \"#FF000000\""
			+ "				},"
			+ "				{"
			+ "					\"name\" : \"Los Angeles\","
			+ "					\"timezone\" : \"America/Los_Angeles\","
			+ "					\"color\" : \"#FF000000\""
			+ "				}"
			+ "			]"
			+ "		}"
			+ "	],"
			+ "	\"theme-list\" : ["
			+ "		{"
			+ "			\"name\" : \"default theme\","
			+ "			\"origin\" : \"builtin:/\","
			+ "			\"texpng\" : \"tex.png\","
			+ "			\"bgimg\" : \"abcd.png\""
			+ "		}" 
			+ "	]"
			+ "}";

		try {
			JSONObject	obj = new JSONObject(sJson);

			m_artzs = new ArrayList<GloneTz>();
			JSONArray	ja1 = obj.getJSONArray("tzset-list");
			for(int i = 0; i < ja1.length(); i++) {
				JSONObject	o0 = ja1.getJSONObject(i);
				JSONArray	a0 = o0.getJSONArray("tz-list");
				for(int ii = 0; ii < a0.length(); ii++) {
					JSONObject	o00 = a0.getJSONObject(ii);
					Log.d("XXXX", o00.getString("name"));
					m_artzs.add(GloneTz.getInstance(o00.getString("timezone")));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTime(long ltime) {
		m_lTimeCurr = ltime;
	}

	public long getTime() {
		long	ldiff = System.currentTimeMillis() - m_lTimeAnimStart;
		if(ldiff < CL_ANIMPERD) {
			float	f0 = 1.0f - ((float)ldiff / (float)CL_ANIMPERD);	// linear 1f->0f
			if(true)	// use smooth stop?
				f0 = FloatMath.cos((f0 + 2) * 0.5f * (float)Math.PI) + 1f;	// use a part of sin curve for smooth flick stop
			long	l0 = (long)((float)(m_lTimeCurr - m_lTimePrev) * f0);
			Log.d(getClass().getName(), "getTime r(" + f0 + ")[" + m_lTimeCurr + " * " + l0 + "]");
			return	m_lTimeCurr - l0;
		}
		return	m_lTimeCurr;
	}

	public ArrayList<GloneTz> getTzList() {
		return	m_artzs;
	}

	public void addTzToList(GloneTz gtzarg) {
		m_artzs.add(gtzarg);
	}

	public void updateTzInList(String sTzId, GloneTz gtzarg) {
		Iterator<GloneTz>	it0 = m_artzs.iterator();
		while(it0.hasNext()) {
			GloneTz	gtz0 = it0.next();
			if(sTzId.equals(gtz0.getTimeZoneId()))
				gtz0.update(gtzarg);
		}
	}

	public void removeTzFromList(String sTzId) {
		Iterator<GloneTz>	it0 = m_artzs.iterator();
		while(it0.hasNext()) {
			GloneTz	gtz0 = it0.next();
			if(sTzId.equals(gtz0.getTimeZoneId()))
				it0.remove();
		}
	}

	public void addTime(long ltime, boolean bAnim) {
		if(bAnim) {
			m_lTimePrev = m_lTimeCurr;
			m_lTimeAnimStart = System.currentTimeMillis();
		} else {
			m_lTimeCurr = getTime();
			m_lTimeAnimStart = 0L;
		}
		m_lTimeCurr += ltime;
	}

}
