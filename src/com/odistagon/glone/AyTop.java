package com.odistagon.glone;

import java.util.ArrayList;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class AyTop extends Activity
{
	private GlOneDoc		m_doc;
	private DefSurfaceView	m_gv;

	private static final int	CMID_GLONE_TEST01 = 901;
	private static final int	CMID_GLONE_TEST02 = 902;
	private static final int	CMID_GLONE_TEST03 = 903;
	private static final int	NC_DLGID_TEST01 = 9;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// document have to be instantiated before views
		m_doc = new GlOneDoc();
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
				showDialog(NC_DLGID_TEST01);
			}
		});

		// TZ select spinner
		{
			ArrayAdapter<CharSequence>	adapter =
				new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			String[]			astzids = TimeZone.getAvailableIDs();
			ArrayList<String>	altz = new ArrayList<String>();
			String				sdeftz = TimeZone.getDefault().getDisplayName();
			int					nidxdef = -1;
			for(int i = 0; i < astzids.length; i++) {
				String	s0 = TimeZone.getTimeZone(astzids[i]).getDisplayName();
				if(!(altz.contains(s0))) {
					altz.add(s0);
				}
				if(nidxdef < 0 && altz.equals(sdeftz)) {
					nidxdef = i;
				}
			}
			for(int i = 0; i < altz.size(); i++) {
				adapter.add(altz.get(i));
			}
			final Spinner	spinn0 = (Spinner)findViewById(R.id.sp_main_test01);
			spinn0.setAdapter(adapter);
			spinn0.setSelection(nidxdef);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// onFling seems only occurs for Events on Activity (not available on View)
		m_gv.fireTouchEvent(event);

		return super.onTouchEvent(event);
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
		case NC_DLGID_TEST01:
			AlertDialog	adlg0 = (AlertDialog)dialog;
			adlg0.setTitle("debug");
			adlg0.setMessage("XXX1\nXXX1\nXXX1\nXXX1\n");
			break;
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog	dret = null;
		switch(id) {
		case NC_DLGID_TEST01:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String[] ITEM = new String[]{"Xxxx", "Ô", "Â", "—Î", "‰©", "Ž‡"};
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
		default:
			dret = super.onCreateDialog(id);
		}
		return	dret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem	mi0 = null;
		mi0 = menu.add(0, CMID_GLONE_TEST01, 0, "System Date Setting");
		mi0.setIcon(android.R.drawable.stat_notify_sync);
		mi0 = menu.add(0, CMID_GLONE_TEST02, 0, "Reload All");
		mi0.setIcon(android.R.drawable.stat_notify_sync);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case CMID_GLONE_TEST01:
			startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
			break;
		case CMID_GLONE_TEST02:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Menu");
		menu.setHeaderIcon(android.R.drawable.ic_media_ff);
		menu.add(0, CMID_GLONE_TEST01, 0, "Show Profile ...");

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CMID_GLONE_TEST01:
			return	true;
		default:
		}
		return super.onContextItemSelected(item); 
	}

	/** provides fast access path to document object for views, etc.
	 */
	public GlOneDoc getDoc() {
		return	m_doc;
	}
}