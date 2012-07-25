package com.odistagon.glone;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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
//	public static final int		CMID_GLONE_GTZADD = 101;	// GloneTz add
	public static final int		CMID_GLONE_GTZEDI = 102;	// GloneTz edit
	public static final int		CMID_GLONE_GTZDEL = 103;	// GloneTz delete
	// dlg id
	public static final int		NC_DLGID_TEST01 = 9;
	public static final int		NC_DLGID_TZSET_ = 10;
	public static final int		NC_DLGID_SELETZ = 11;

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
			tv0.setText(gtzarg.getTimeZone().getDisplayName());
			tv0 = (TextView)vliarg.findViewById(R.id.tv_ligtz_id);
			tv0.setText(gtzarg.getTimeZoneId());
			vliarg.setTag(gtzarg);
		} else {
			tv0.setText(" + add new");
		}
	}
}
