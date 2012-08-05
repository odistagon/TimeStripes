package com.odistagon.glone;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.FloatMath;
import android.util.Log;

public class GlOneDoc
{
	private long				m_lTimeOffset, m_lTimePrev;
	private long				m_lTimePreserved;			// preserved system time ms. used when paused.
	private long				m_lTimeAnimStart;
	private ArrayList<GloneTz>	m_artzs;
	private boolean				m_bdebug;

	public static final long	CL_ANIMPERD = 2500L;		// ms. until fling anim stops

	public GlOneDoc() {
		m_lTimeOffset = 0L;

		readConfig();
	}

	public static final String	PREFNAME_CONFJSON = "PREFNAME_CONFJSON";
// example of config json
//	{
//		"globalbg" : "bg.png",
//		"usethemebg" : true,
//		"curr-tzset" : "default tz set",
//		"curr-theme" : "default theme",
//		"tzset-list" : [
//			{
//				"name" : "default tz set",
//				"tz-list" : [
//					{
//						"name" : "JP",
//						"timezone" : "Japan",
//						"color" : "#FF000000"
//					},
//					{
//						...
//					}
//				]
//			}
//		],
//		"theme-list" : [
//			{
//				"name" : "default theme",
//				"origin" : "builtin:/",
//				"texpng" : "tex.png",
//				"bgimg" : "abcd.png"
//			} 
//		]
//	}

	private void makeDefaultConfig() {
		m_artzs = new ArrayList<GloneTz>();
		addTzToList(GloneTz.getInstance("Japan"));
		addTzToList(GloneTz.getInstance("GMT"));
		addTzToList(GloneTz.getInstance("America/Los_Angeles"));
	}

	public void readConfig() {
		SharedPreferences	pref = GloneApp.getContext().getSharedPreferences(
				GloneApp.class.getCanonicalName(), Context.MODE_PRIVATE);
		String	sConf = pref.getString(PREFNAME_CONFJSON, null);
		try {
			if(sConf == null) {
				makeDefaultConfig();
				JSONObject	obj = serializeConf();
				sConf = obj.toString();
			}

			JSONObject	obj = new JSONObject(sConf);

			m_artzs = new ArrayList<GloneTz>();
			JSONArray	ja1 = obj.getJSONArray("tzset-list");
			for(int i = 0; i < ja1.length(); i++) {
				JSONObject	o0 = ja1.getJSONObject(i);
				JSONArray	a0 = o0.getJSONArray("tz-list");
				for(int ii = 0; ii < a0.length(); ii++) {
					JSONObject	o00 = a0.getJSONObject(ii);
					m_artzs.add(GloneTz.getInstance(o00.getString("timezone")));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveConifg() {
		SharedPreferences	pref = GloneApp.getContext().getSharedPreferences(
				GloneApp.class.getCanonicalName(), Context.MODE_PRIVATE);
		JSONObject jo0 = null;
		try {
			jo0 = serializeConf();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Editor	ed0 = pref.edit();
		ed0.putString(PREFNAME_CONFJSON, jo0.toString());
		ed0.commit();
	}

	public JSONObject serializeConf()
	throws JSONException {
		JSONObject		jo0 = new JSONObject();
		jo0.put("globalbg", "bg.png");
		jo0.put("usethemebg", true);
		jo0.put("curr-tzset", "default tz set");
		jo0.put("curr-theme", "default theme");
		JSONArray		jatzsets = new JSONArray();
		{
			JSONObject	jotzset = new JSONObject();
			jotzset.put("name", "default tz set");
			JSONArray	jatzs = new JSONArray();
			Iterator<GloneTz>	it0 = m_artzs.iterator();
			while(it0.hasNext()) {
				GloneTz	gtz0 = it0.next();
				JSONObject	jotz = new JSONObject();
				jotz.put("name", gtz0.getTimeZoneId());
				jotz.put("timezone", gtz0.getTimeZone().getID());
				jotz.put("color", "#FF000000");
				jatzs.put(jotz);
			}
			jotzset.put("tz-list", jatzs);
			jatzsets.put(jotzset);
		}
		jo0.put("tzset-list", jatzsets);
		JSONArray		jathemes = new JSONArray();
		{
			JSONObject	jotheme = new JSONObject();
			jotheme.put("name", "default theme");
			jotheme.put("origin", "builtin:/");
			jotheme.put("texpng", "tex.png");
			jotheme.put("bgimg", "abcd.png");
		}
		jo0.put("theme-list", jathemes);
		return	jo0;
	}

	public void togglePause() {
		if(isPaused()) {
			long	lgap = (System.currentTimeMillis() - m_lTimePreserved);
			m_lTimePreserved = 0L;
			addTimeOffset(lgap, true);
		} else {
			m_lTimePreserved = System.currentTimeMillis();
		}
	}

	public boolean isPaused() {
		return	(m_lTimePreserved > 0);
	}

	public void zeroOffset() {
		addTimeOffset(m_lTimeOffset * -1L + CL_ANIMPERD, true);
	}

	/** Set absolute time goal.
	 */
	public void setTimeAbsolute(long ltime, boolean bAnim) {
		addTimeOffset(ltime - getTime(), true);
	}

	public void addTimeOffset(long ltime, boolean bAnim) {
		if(bAnim) {
			m_lTimePrev = m_lTimeOffset;
			m_lTimeAnimStart = System.currentTimeMillis();
		} else {
			m_lTimeOffset = getTime() - System.currentTimeMillis();
			m_lTimeAnimStart = 0L;
		}
		m_lTimeOffset += ltime;
	}

	public long getTime() {
		long	ldiff = System.currentTimeMillis() - m_lTimeAnimStart;
		long	lret = m_lTimeOffset;
		if(ldiff < CL_ANIMPERD) {
			float	f0 = 1.0f - ((float)ldiff / (float)CL_ANIMPERD);	// linear 1f->0f
			if(true)	// use smooth stop?
				f0 = FloatMath.cos((f0 + 2) * 0.5f * (float)Math.PI) + 1f;	// use a part of sin curve for smooth flick stop
			long	l0 = (long)((float)(m_lTimeOffset - m_lTimePrev) * f0);
			lret -= l0;
		}
		return	(m_lTimePreserved > 0 ? 
				lret + m_lTimePreserved :
				lret + System.currentTimeMillis());
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

	public void moveTzOrder(String sTzId, int nMove) {
		if(nMove == 0)
			return;

		int	i = 0, nTarget = -1;
		Iterator<GloneTz>	it0 = m_artzs.iterator();
		while(it0.hasNext()) {
			GloneTz	gtz0 = it0.next();
			if(sTzId.equals(gtz0.getTimeZoneId()))
				nTarget = i;
			i++;
		}
		if((nTarget == 0 && nMove < 0) ||
				(nTarget == (m_artzs.size() - 1) && nMove > 0))
			return;	// can't move over start or end
		GloneTz	gtz0 = m_artzs.get(nTarget);
		m_artzs.remove(nTarget);
		m_artzs.add(nTarget + nMove, gtz0);
	}

	/** Change array order as shifting all elements left/right.
	 * @param bpush
	 */
	public void shiftTzOrder(boolean bpush) {
		ArrayList<GloneTz>	altmp = new ArrayList<GloneTz>();
		altmp.addAll(m_artzs);
		if(bpush) {
			GloneTz	gtz0 = altmp.get(altmp.size() - 1);
			altmp.remove(altmp.size() - 1);
			altmp.add(0, gtz0);
		} else {
			GloneTz	gtz0 = altmp.get(0);
			altmp.remove(0);
			altmp.add(gtz0);
		}
		m_artzs = altmp;
	}

	public void syncPreference(Context ctxa) {
		SharedPreferences	pref = PreferenceManager.getDefaultSharedPreferences(ctxa);
		m_bdebug = pref.getBoolean("PK_DEBUG_", false);
	}

//	public void setDebug(boolean bdebug) {
//		m_bdebug = bdebug;
//	}

	public boolean isDebug() {
		return	m_bdebug;
	}
}
