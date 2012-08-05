package com.odistagon.glone;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
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
	// dlg id
	public static final int		NC_DLGID_TEST01 = 9;
	public static final int		NC_DLGID_TZSET_ = 10;
	public static final int		NC_DLGID_SELETZ = 11;		// timezone selector
	public static final int		NC_DLGID_DATPIC = 12;		// date picker dlg
	public static final int		NC_DLGID_SHWTXT = 13;		// show date times by text

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
		TextView	tv0 = null;
		tv0 = (TextView)vliarg.findViewById(R.id.tv_ligtz_name);
		if(gtzarg != null) {
			TimeZone	tz0 = gtzarg.getTimeZone();
			tv0.setText(tz0.getDisplayName());
			tv0 = (TextView)vliarg.findViewById(R.id.tv_ligtz_id);
			float		foffset = (float)tz0.getOffset(System.currentTimeMillis()) / (60f * 60f * 1000f);
			tv0.setText(gtzarg.getTimeZoneId() + (foffset >= 0f ? " GMT+" : " GMT")
					+ foffset + " " + (tz0.useDaylightTime() ? " [+DST]" : ""));
			vliarg.setTag(gtzarg);
		} else {
			tv0.setText(" + add new");
		}
	}
}
