package com.odistagon.glone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/** UI for editing tzset.
 * 
 */
public class AyTzSet extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dlgtzset);
		Resources	r0 = GloneApp.getContext().getResources();
		setTitle(r0.getString(R.string.rs_seltzdlg_title));

		ListView	lv0 = (ListView)findViewById(R.id.lv_seltz_main);
		lv0.setAdapter(new GloneTzSetListAdapter(this));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Menu");
		menu.setHeaderIcon(android.R.drawable.ic_media_ff);
		menu.add(0, GloneUtils.CMID_GLONE_GTZEDI, 0, "Edit ...");
		menu.setHeaderIcon(android.R.drawable.ic_media_ff);
		menu.add(0, GloneUtils.CMID_GLONE_GTZDEL, 0, "Remove ...");

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case GloneUtils.CMID_GLONE_GTZEDI:	{
			showDialog(GloneUtils.NC_DLGID_SELETZ);
//			View	v0 = LayoutInflater.from(GloneApp.getContext()).inflate(R.layout.dlgseltz, null);
//			AlertDialog	dlg0 = new AlertDialog.Builder(GloneApp.getContext())
//				.setIcon(R.drawable.icon)
//				.setTitle("1234")
//				.setView(v0).create();
//			dlg0.show();
			return	true;
		}
		default:
		}
		return super.onContextItemSelected(item); 
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id) {
		case GloneUtils.NC_DLGID_SELETZ: {
		}	break;
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog	dret = null;
		switch(id) {
		case GloneUtils.NC_DLGID_SELETZ: {
			Dialog	dlg0 = new DlgSelTz(this);
			dret = dlg0;
		}	break;
		default:
			Log.e(getClass().getName(), "onCreateDialog() called with invalid id.");
		}
		return	dret;
	}

	class GloneTzSetListAdapter extends BaseAdapter implements ListAdapter
	{
		private AyTzSet	m_dlgparent;

		public GloneTzSetListAdapter(AyTzSet dparent) {
			m_dlgparent = dparent;
		}

		@Override
		public int getCount() {
			return	GloneApp.getDoc().getTzList().size();
		}

		@Override
		public Object getItem(int arg0) {
			return	GloneApp.getDoc().getTzList().get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return	GloneApp.getDoc().getTzList().get(arg0).hashCode();
		}

		@Override
		public View getView(int arg0, View vConv, ViewGroup arg2) {
			GloneTz	gtz0 = GloneApp.getDoc().getTzList().get(arg0);

			if (vConv == null) {
				LayoutInflater	inf0 = (LayoutInflater)
					GloneApp.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vConv = inf0.inflate(R.layout.liglonetz, null);
				registerForContextMenu(vConv);
			}
			TextView	tv0 = (TextView)vConv.findViewById(R.id.tv_ligtz_name);
			tv0.setText(gtz0.getTimeZone().getDisplayName());
			vConv.setTag(gtz0);
			vConv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View varg) {
					m_dlgparent.openContextMenu(varg);
//					GloneTz	gtz0 = (GloneTz)varg.getTag();
//					Log.d("XXXX", "selected: " + gtz0.getTimeZoneId());
				}
			});
			return	vConv;
		}
	}

}
