package com.odistagon.glone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class AyTop extends Activity
{
	private DefSurfaceView	m_gv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// show DISCLAIMER once
		SharedPreferences	pref = PreferenceManager.getDefaultSharedPreferences(GloneApp.getContext());
		int					nDscAgLv = pref.getInt(GloneUtils.PK_DSCLALV, 0);
		if(nDscAgLv < GloneUtils.NC_DISCLAIMER_VER)
			showDialog(GloneUtils.NC_DLGID_DISCLA);

		// main layout
		setContentView(R.layout.main);
		RelativeLayout	rl0 = (RelativeLayout)findViewById(R.id.lo_main_rl0);
//		LinearLayout	rl0 = (LinearLayout)findViewById(R.id.lo_main_vert);
		// insert GL view into the main layout
		m_gv = new DefSurfaceView(this);
		rl0.addView(m_gv, 0);

//		final Activity	atop = this;

		//bottom toolbar buttons
		View	v0 = findViewById(R.id.iv_main_menu);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent	i0 = new Intent(GloneApp.getContext(), (new AyPrefMain()).getClass());
				i0.setAction(Intent.ACTION_VIEW);
				startActivityForResult(i0, 0);
			}
		});
		v0 = findViewById(R.id.iv_tb_zoomin);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_gv.zoomIn(-7f);
			}
		});
		v0 = findViewById(R.id.iv_tb_zoomou);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_gv.zoomIn(+7f);
			}
		});
		// side toolbar buttons
		v0 = findViewById(R.id.iv_tb_fastfw);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GloneApp.getDoc().addTimeOffset(24 * 60 * 60 * 1000 * +1L, true);
			}
		});
		v0 = findViewById(R.id.iv_tb_rewind);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GloneApp.getDoc().addTimeOffset(24 * 60 * 60 * 1000 * -1L, true);
			}
		});
		v0 = findViewById(R.id.iv_tb_thewld);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GloneApp.getDoc().togglePause();
				((ImageView)v).setImageResource(GloneApp.getDoc().isPaused()
						? R.drawable.ic_menu_stwp : R.drawable.ic_menu_stwm);
			}
		});
		v0.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				GloneApp.getDoc().zeroOffset();
				return	true;
			}
		});
	}

	@Override
	protected void onPostResume() {
		GloneApp.getDoc().syncPreference(this);

		SharedPreferences	pref = PreferenceManager.getDefaultSharedPreferences(this);
		String				s0 = pref.getString(
				GloneUtils.PK_CLOCKTZ, Integer.toString(GloneUtils.NC_PREF_CLOCKTZ_FIRT));
		int					nClockTz = Integer.parseInt(s0);
		m_gv.setClockTz(nClockTz);

		super.onPostResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		m_gv.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		m_gv.onResume();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id) {
		case GloneUtils.NC_DLGID_DATPIC:
			final GloneTz		gtz1 = GloneApp.getDoc().getTzList().get(0);
			int[]				andt = gtz1.getTimeNumbers(GloneApp.getDoc().getTime());
			((DatePickerDialog)dialog).updateDate(andt[0], andt[1] - 1, andt[2]);
			break;
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog	dret = null;
		switch(id) {
		case GloneUtils.NC_DLGID_DISCLA: {
			AlertDialog.Builder	dlgbldr = new AlertDialog.Builder(this);
			dlgbldr.setTitle(R.string.dlg_discla_t);
			dlgbldr.setMessage(R.string.dlg_discla_m);
			dlgbldr.setPositiveButton(R.string.dlg_discla_agre, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences	pref = PreferenceManager.getDefaultSharedPreferences(GloneApp.getContext());
					Editor	e0 = pref.edit();
					e0.putInt(GloneUtils.PK_DSCLALV, GloneUtils.NC_DISCLAIMER_VER);
					e0.commit();
				}
			});
			dret = dlgbldr.create();
		}	break;
		case GloneUtils.NC_DLGID_DATPIC:
			DatePickerDialog	dlg0 = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int nyer, int nmnt, int nday) {
						// Jump absolute
						final GloneTz	gtz1 = GloneApp.getDoc().getTzList().get(0);
						Calendar		c0 = Calendar.getInstance(gtz1.getTimeZone());
						c0.set(nyer, nmnt, nday);
						GloneApp.getDoc().setTimeAbsolute(c0.getTimeInMillis(), true);
					}
				}, 2000, 1, 1);	// date will be set onPrepareDialog()
								// but 0,0,0 or 1970,1,1 are not acceptable for some android versions even a tiny moment...
			dlg0.setTitle(R.string.dlg_datpic_t);
			dret = dlg0;
			break;
		case GloneUtils.NC_DLGID_SHWTXT: {
			AlertDialog.Builder	dlgbldr = new AlertDialog.Builder(this);
			dlgbldr.setTitle(R.string.dlg_shwtxt_t);
			long				lcurr = GloneApp.getDoc().getTime();
			StringBuilder		sb0 = new StringBuilder();
			ArrayList<GloneTz>	altz0 = GloneApp.getDoc().getTzList();
			Iterator<GloneTz>	it0 = altz0.iterator();
			while(it0.hasNext()) {
				GloneTz	gtz0 = it0.next();
				sb0.append(gtz0.getTimeZoneId());
				sb0.append("\n  ");
				sb0.append(gtz0.getDebugString(lcurr));
				sb0.append("\n");
			}
			final String		smsg = sb0.toString();
			dlgbldr.setMessage(smsg);
			dlgbldr.setNeutralButton(R.string.dlgbtn_copycb, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ClipboardManager	cbm0 = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE); 
//					int					nSdkVer = android.os.Build.VERSION.SDK_INT;
//					if(nSdkVer >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//						ClipData		cd0 = ClipData.newPlainText("label", "Text to copy");
//						cbm0.setPrimaryClip(cd0);
//					} else {
						cbm0.setText(smsg);
//					}
				}
			});
			dlgbldr.setNegativeButton(R.string.dlgbtn_close_, null);
			dret = dlgbldr.create();
		}	break;
		default:
			Log.e(getClass().getName(), "onCreateDialog() called with invalid id.");
		}
		return	dret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem	mi0 = null;
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_SHWTXT, 0, R.string.mi_shwtxt);
		mi0.setIcon(android.R.drawable.stat_notify_sync);
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_JMPABS, 0, R.string.mi_jmpabs);
		mi0.setIcon(android.R.drawable.stat_notify_sync);
//		mi0 = menu.add(0, GloneUtils.CMID_GLONE_ZOOMIN, 0, R.string.mi_zoomin);
//		mi0.setIcon(R.drawable.ic_menu_zoin);
//		mi0 = menu.add(0, GloneUtils.CMID_GLONE_ZOOMOU, 0, R.string.mi_zoomou);
//		mi0.setIcon(R.drawable.ic_menu_zout);
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_FDSTNE, 0, R.string.mi_fdstne);
		mi0.setIcon(R.drawable.ic_menu_ffwd);
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_FDSTPR, 0, R.string.mi_fdstpr);
		mi0.setIcon(R.drawable.ic_menu_rewd);
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_PRFMAI, 0, R.string.mi_prefes);
		mi0.setIcon(R.drawable.ic_menu_sett);
//		mi0 = menu.add(0, GloneUtils.CMID_GLONE_TGSEDI, 0, "Edit timezones");
//		mi0.setIcon(android.R.drawable.stat_notify_sync);
//		mi0 = menu.add(0, GloneUtils.CMID_GLONE_ABOUT_, 0, "About this app");
//		mi0.setIcon(android.R.drawable.stat_notify_sync);
//		mi0 = menu.add(0, GloneUtils.CMID_GLONE_SYSDAT, 0, "System Date Setting");
//		mi0.setIcon(android.R.drawable.stat_notify_sync);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case GloneUtils.CMID_GLONE_SHWTXT:
			showDialog(GloneUtils.NC_DLGID_SHWTXT);
			break;
		case GloneUtils.CMID_GLONE_JMPABS:
			showDialog(GloneUtils.NC_DLGID_DATPIC);
			break;
//		case GloneUtils.CMID_GLONE_ZOOMIN:
//			m_gv.zoomIn(+1.0f);
//			break;
//		case GloneUtils.CMID_GLONE_ZOOMOU:
//			m_gv.zoomIn(-1.0f);
//			break;
		case GloneUtils.CMID_GLONE_FDSTNE:
		case GloneUtils.CMID_GLONE_FDSTPR:	{
			GloneTz	gtz0 = GloneApp.getDoc().getTzList().get(0);
			long	lgoto = gtz0.findNextDstChange(GloneApp.getDoc().getTime(),
				item.getItemId() == GloneUtils.CMID_GLONE_FDSTNE);
			if(lgoto > 0)
				GloneApp.getDoc().setTimeAbsolute(lgoto, true);
		}	break;
		case GloneUtils.CMID_GLONE_PRFMAI:	{
			Intent	i0 = new Intent(GloneApp.getContext(), (new AyPrefMain()).getClass());
			i0.setAction(Intent.ACTION_VIEW);
			startActivityForResult(i0, 0);
		}	break;
//		case GloneUtils.CMID_GLONE_TGSEDI:	{
//			Intent	i0 = new Intent(GloneApp.getContext(), (new AyTzSet()).getClass());
//			i0.setAction(Intent.ACTION_VIEW);
//			startActivityForResult(i0, 0);
//		}	break;
//		case GloneUtils.CMID_GLONE_ABOUT_:	{
//			Intent	i0 = new Intent(GloneApp.getContext(), (new AyAbout()).getClass());
//			i0.setAction(Intent.ACTION_VIEW);
//			startActivityForResult(i0, 0);
//		}	break;
//		case GloneUtils.CMID_GLONE_SYSDAT:
//			startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
//			break;
		}
		return super.onOptionsItemSelected(item);
	}
}