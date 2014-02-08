package com.odistagon.glone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Window;
import android.widget.DatePicker;

public class AyMain extends FragmentActivity
{
	private long			m_lprevdatepick;		// last time date picked (bug workaround)
//	private ProgressDialog	m_dlgprog;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

//		m_dlgprog = new ProgressDialog(this);
//		m_dlgprog.setTitle("タイトル");
//		m_dlgprog.setMessage("メッセージ");
//		m_dlgprog.setIndeterminate(false);
//		m_dlgprog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//ProgressDialog.STYLE_HORIZONTAL);
//		m_dlgprog.setCancelable(true);
//		m_dlgprog.show();

        SharedPreferences	pref = PreferenceManager.getDefaultSharedPreferences(GloneApp.getContext());
		boolean				bShowHdr = pref.getBoolean(
				getResources().getString(R.string.prefkey_usehdr), false);
		if(!bShowHdr)
			requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.aymain);

		FragmentManager		fm0 = getSupportFragmentManager();
		Fragment			f0 = fm0.findFragmentById(R.id.fg_aymain_body); 
		if(f0 == null) {
			FragmentTransaction	trans0 = fm0.beginTransaction();
			trans0.add(R.id.fg_aymain_body, new FgMain());
			trans0.commit();
		}
	}

	@Override
	protected void onPostResume() {
		GloneApp.getDoc().syncPreference(this);

		SharedPreferences	pref = PreferenceManager.getDefaultSharedPreferences(GloneApp.getContext());

		// show DISCLAIMER once
		int					nDscAgLv = pref.getInt(
				getResources().getString(R.string.prefkey_dscllv), 0);
		if(nDscAgLv < GloneUtils.NC_DISCLAIMER_VER)
			showDialog(GloneUtils.NC_DLGID_DISCLA);

		String				s0 = pref.getString(
				getResources().getString(R.string.prefkey_clcktz),
				Integer.toString(GloneUtils.NC_PREF_CLOCKTZ_FIRT));
		int					nClockTz = Integer.parseInt(s0);
		GloneApp.getDoc().setClockTz(nClockTz);

//		m_dlgprog.dismiss();

		super.onPostResume();
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
					e0.putInt(getResources().getString(R.string.prefkey_dscllv), GloneUtils.NC_DISCLAIMER_VER);
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
						// called twice by framework bug ?
						// http://stackoverflow.com/questions/11383592/android-android-4-1-emulator-invoking-ondateset-twice-from-datepicker-dialog
						if(m_lprevdatepick + 1000L > System.currentTimeMillis())
							return;
						// Jump absolute
						final GloneTz	gtz1 = GloneApp.getDoc().getTzList().get(0);
						Calendar		c0 = Calendar.getInstance(gtz1.getTimeZone());
						c0.set(nyer, nmnt, nday);
						GloneApp.getDoc().setTimeAbsolute(c0.getTimeInMillis(), true);
						m_lprevdatepick = System.currentTimeMillis();
					}
				}, 2000, 1, 1);	// date will be set onPrepareDialog()
								// but 0,0,0 or 1970,1,1 are not acceptable for some android versions even a tiny moment...
			dlg0.setTitle(R.string.dlg_datpic_t);
			dret = dlg0;
			break;
		case GloneUtils.NC_DLGID_SHWTXT: {
			boolean	bUseAmPm = false;
			SharedPreferences	pref = PreferenceManager.getDefaultSharedPreferences(GloneApp.getContext());
			if(pref != null)
				bUseAmPm = pref.getBoolean(
						getResources().getString(R.string.prefkey_usampm), false);

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
				sb0.append(gtz0.getDebugString(lcurr, bUseAmPm));
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
}
