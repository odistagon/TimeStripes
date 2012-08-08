package com.odistagon.glone;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Calendar;
import java.util.TimeZone;

import android.view.View;
import android.widget.TextView;

public class GloneUtils
{
	// constants
	// context menu id
	public static final int		CMID_GLONE_TEST01 = 901;
	public static final int		CMID_GLONE_TEST02 = 902;
	public static final int		CMID_GLONE_TEST03 = 903;
	public static final int		CMID_GLONE_TEST04 = 904;
	public static final int		CMID_GLONE_TEST05 = 905;
	public static final int		CMID_GLONE_PRFMAI = 101;	// Preference Activity
	public static final int		CMID_GLONE_GTZEDI = 102;	// GloneTz edit
	public static final int		CMID_GLONE_GTZDEL = 103;	// GloneTz delete
	public static final int		CMID_GLONE_GTZUPR = 111;	// GloneTz move upper
	public static final int		CMID_GLONE_GTZLWR = 112;	// GloneTz move lower
	public static final int		CMID_GLONE_SHWTXT = 141;	// Show date times by text
	public static final int		CMID_GLONE_SYSDAT = 191;	// System date setting
	public static final int		CMID_GLONE_ABOUT_ = 199;	// About
	public static final int		CMID_GLONE_TGSEDI = 111;	// Tz set edit
	public static final int		CMID_GLONE_JMPABS = 121;	// Jump absolute date
	public static final int		CMID_GLONE_ZOOMIN = 131;	// Zoom in
	public static final int		CMID_GLONE_ZOOMOU = 132;	// Zoom out
	public static final int		CMID_GLONE_FDSTNE = 133;	// Find next dst change
	public static final int		CMID_GLONE_FDSTPR = 134;	// Find prev dst change
	// dlg id
	public static final int		NC_DLGID_TEST01 = 9;
	public static final int		NC_DLGID_TZSET_ = 10;
	public static final int		NC_DLGID_SELETZ = 11;		// timezone selector
	public static final int		NC_DLGID_DATPIC = 12;		// date picker dlg
	public static final int		NC_DLGID_SHWTXT = 13;		// show date times by text
	// prefs
	public static final String	PK_CLOCKTZ = "PK_CLOKTZ";	// show date times by text
	public static final int		NC_PREF_CLOCKTZ_NONE = 0;	// don't show clock
	public static final int		NC_PREF_CLOCKTZ_FIRT = 1;	// first timezone
	public static final int		NC_PREF_CLOCKTZ_SYST = 2;	// system timezone

	public static FloatBuffer makeFloatBuffer(float[] values) {
		ByteBuffer bb = ByteBuffer.allocateDirect(values.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(values);
		fb.position(0);
		return	fb;
	}

	/** Update GloneTz list item.
	 * @param vliarg
	 * @param gtzarg
	 */
	public static void setGloneTzListItem(View vliarg, GloneTz gtzarg) {
		TextView	tvName = (TextView)vliarg.findViewById(R.id.tv_ligtz_name);
		TextView	tvId = (TextView)vliarg.findViewById(R.id.tv_ligtz_id);
		if(gtzarg != null) {
			TimeZone	tz0 = gtzarg.getTimeZone();
			tvName.setText(tz0.getDisplayName());
			float		foffset = (float)tz0.getOffset(System.currentTimeMillis()) / (60f * 60f * 1000f);
			tvId.setText(gtzarg.getTimeZoneId() + (foffset >= 0f ? " GMT+" : " GMT")
					+ foffset + " " + (tz0.useDaylightTime() ? " [+DST]" : ""));
			vliarg.setTag(gtzarg);
		} else {
			// "+add new" item
			tvName.setText(R.string.li_addnew);
			tvId.setText("");
			vliarg.setTag(null);
		}
	}
}
