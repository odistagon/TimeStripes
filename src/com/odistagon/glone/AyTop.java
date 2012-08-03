package com.odistagon.glone;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.RelativeLayout;

public class AyTop extends Activity
{
	private DefSurfaceView	m_gv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		m_gv = new DefSurfaceView(this);
		setContentView(R.layout.main);

		// main layout
		RelativeLayout	rl0 = (RelativeLayout)findViewById(R.id.lo_main_rl0);

		// insert GL view into the main layout
		rl0.addView(m_gv, 0);

//		final Activity	atop = this;
		View	v0 = findViewById(R.id.iv_main_menu);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				atop.openContextMenu(v);
				showDialog(GloneUtils.NC_DLGID_TEST01);
			}
		});
		v0 = findViewById(R.id.iv_tb_zoomin);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_gv.zoomIn(1.1f);
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
				boolean	bpaused = GloneApp.getDoc().isPaused();
				v.setBackgroundColor(bpaused ? Color.YELLOW : Color.TRANSPARENT);
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
		case GloneUtils.NC_DLGID_TEST01:
			break;
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
		case GloneUtils.NC_DLGID_TEST01:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String[] ITEM = new String[]{"Xxxx", "ê‘", "ê¬", "óŒ", "â©", "éá"};
			builder.setTitle("debug");
//			builder.setMessage("XXXX");
			builder.setItems(ITEM, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.v("Alert", "Item No : " + which);
				}
			});
			dret = builder.create();
			break;
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
				}, 0, 0, 0);	// date will be set onPrepareDialog()
			dlg0.setTitle("Pick a goal date");
			dret = dlg0;
			break;
		default:
			Log.e(getClass().getName(), "onCreateDialog() called with invalid id.");
		}
		return	dret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem	mi0 = null;
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_SYSDAT, 0, "System Date Setting");
		mi0.setIcon(android.R.drawable.stat_notify_sync);
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_JMPABS, 0, "Jump absolute");
		mi0.setIcon(android.R.drawable.stat_notify_sync);
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_ZOOMOU, 0, "Zoom out");
		mi0.setIcon(android.R.drawable.stat_notify_sync);
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_ZOOMIN, 0, "Zoom in");
		mi0.setIcon(android.R.drawable.stat_notify_sync);
		mi0 = menu.add(0, GloneUtils.CMID_GLONE_TGSEDI, 0, "Edit timezones");
		mi0.setIcon(android.R.drawable.stat_notify_sync);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case GloneUtils.CMID_GLONE_SYSDAT:
			startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
			break;
		case GloneUtils.CMID_GLONE_JMPABS:
			showDialog(GloneUtils.NC_DLGID_DATPIC);
			break;
		case GloneUtils.CMID_GLONE_ZOOMOU:
			m_gv.zoomIn(-1.0f);
			break;
		case GloneUtils.CMID_GLONE_ZOOMIN:
			m_gv.zoomIn(+1.0f);
			break;
		case GloneUtils.CMID_GLONE_TGSEDI:
			Intent	i0 = new Intent(GloneApp.getContext(), (new AyTzSet()).getClass());
			i0.setAction(Intent.ACTION_VIEW);
			startActivityForResult(i0, 0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Menu");
		menu.setHeaderIcon(android.R.drawable.ic_media_ff);
		menu.add(0, GloneUtils.CMID_GLONE_TEST01, 0, "Show Profile ...");

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case GloneUtils.CMID_GLONE_TEST01:
			return	true;
		default:
		}
		return super.onContextItemSelected(item); 
	}
}