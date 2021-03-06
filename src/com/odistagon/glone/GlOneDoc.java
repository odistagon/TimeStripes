package com.odistagon.glone;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.FloatMath;

public class GlOneDoc
{
	private long				m_lTimeOffset, m_lTimePrev;
	private long				m_lTimePreserved;			// preserved system time ms. used when paused.
	private long				m_lTimeAnimStart;
	private GloneTz				m_tzSystem;
	private ArrayList<GloneTz>	m_artzs;
	private int					m_nClockTz;					// which clock kind to be shown?
	private int					m_nFgTrans;					// Stripes transparency
	private int					m_nVwAngle;					// View angle
	private String				m_sBgKind;
	private boolean				m_bdebug;

	private boolean				m_bShowFfwd;
	private boolean				m_bShowStwm;
	private boolean				m_bShowRewd;
	private boolean				m_bShowSett;
	private boolean				m_bShowZoin;
	private boolean				m_bShowZout;

	public static final long	CL_ANIMPERD = 2500L;		// ms. until fling anim stops

	public GlOneDoc() {
		m_lTimeOffset = 0L;
		m_tzSystem = GloneTz.getInstance(
				(GregorianCalendar.getInstance()).getTimeZone().getID());

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

	public boolean isOnAnimation() {
		return	(m_lTimeAnimStart != 0);
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
		} else {
			m_lTimeAnimStart = 0L;	// reset
		}
		return	(m_lTimePreserved > 0 ? 
				lret + m_lTimePreserved :
				lret + System.currentTimeMillis());
	}

	public GloneTz getSystemTz() {
		return	m_tzSystem;
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
		Resources			res0 = ctxa.getResources();
		m_bdebug = pref.getBoolean(res0.getString(R.string.prefkey_debug_), false);
		m_sBgKind = pref.getString(res0.getString(R.string.prefkey_bg_sel),
				res0.getString(R.string.pfval_bg_sel_1));
		// if wp img cache does not exist, fallback to flat background
		if(!GloneUtils.existsWallpaperCache(ctxa))
			m_sBgKind = res0.getString(R.string.pfval_bg_sel_1);
		m_nFgTrans = pref.getInt(res0.getString(R.string.prefkey_fgtrns), 100);
		m_nVwAngle = pref.getInt(res0.getString(R.string.prefkey_vwslnt), 0);
		// toolbar buttons
		m_bShowFfwd = pref.getBoolean(res0.getString(R.string.prefkey_btns_ffwd), true);
		m_bShowStwm = pref.getBoolean(res0.getString(R.string.prefkey_btns_stwm), true);
		m_bShowRewd = pref.getBoolean(res0.getString(R.string.prefkey_btns_rewd), true);
		m_bShowSett = pref.getBoolean(res0.getString(R.string.prefkey_btns_sett), true);
		m_bShowZoin = pref.getBoolean(res0.getString(R.string.prefkey_btns_zoin), true);
		m_bShowZout = pref.getBoolean(res0.getString(R.string.prefkey_btns_zout), true);
	}

//	public void setDebug(boolean bdebug) {
//		m_bdebug = bdebug;
//	}

	public void setClockTz(int nClockTz) {
		m_nClockTz = nClockTz;
	}

	public int getClockTz() {
		return	m_nClockTz;
	}

	public int getFgTrans() {
		return	m_nFgTrans;
	}

	public int getViewAngle() {
		return	m_nVwAngle;
	}

	public boolean bgKind(String sarg) {
		return	m_sBgKind.equals(sarg);
	}

	public boolean isDebug() {
		return	m_bdebug;
	}

	public boolean isShowFfwd() {
		return	m_bShowFfwd;
	}

	public boolean isShowStwm() {
		return	m_bShowStwm;
	}

	public boolean isShowRewd() {
		return	m_bShowRewd;
	}

	public boolean isShowSett() {
		return	m_bShowSett;
	}

	public boolean isShowZoin() {
		return	m_bShowZoin;
	}

	public boolean isShowZout() {
		return	m_bShowZout;
	}
}
