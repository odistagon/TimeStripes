package com.odistagon.glone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DlgSelTz extends Dialog
{

	public DlgSelTz(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dlgseltz);
		Resources	r0 = getContext().getResources();
		setTitle(r0.getString(R.string.rs_seltzdlg_title));

		{
			final EditText	et0 = (EditText)findViewById(R.id.et_seltz_find);
			Button			btn0 = (Button)findViewById(R.id.bt_seltz_find);
//			LinearLayout	lo0 = (LinearLayout)findViewById(R.id.lo_seltz_main);
			btn0.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ListView	lv0 = (ListView)findViewById(R.id.lv_seltz_main);
					GloneTzListAdapter	la0 = (GloneTzListAdapter)lv0.getAdapter();
					la0.filter(et0.getText().toString());
				}
			});
		}

		ListView	lv0 = (ListView)findViewById(R.id.lv_seltz_main);
		lv0.setAdapter(new GloneTzListAdapter());

		super.onCreate(savedInstanceState);
	}

	class GloneTzListAdapter extends BaseAdapter implements ListAdapter {	//implements Filterable {
		ArrayList<GloneTz>	m_atz;
		ArrayList<GloneTz>	m_atzsub;

		GloneTzListAdapter() {
			ArrayList<GloneTz>	al0 = new ArrayList<GloneTz>();
			String[]	astzids = TimeZone.getAvailableIDs();
			String		sprev = null;
			for(int i = 0; i < astzids.length; i++) {
				String	s0 = TimeZone.getTimeZone(astzids[i]).getDisplayName();
				if(sprev != null && sprev.equals(s0))
					continue;
				al0.add(GloneTz.getInstance(astzids[i]));
				sprev = s0;
			}
			m_atz = al0;
			m_atzsub = m_atz;
		}

		@Override
		public int getCount() {
			return	m_atzsub.size();
		}

		@Override
		public Object getItem(int arg0) {
			return	m_atzsub.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return	m_atzsub.get(arg0).hashCode();
		}

		@Override
		public View getView(int position, View vConv, ViewGroup parent) {
			if (vConv == null) {
				LayoutInflater	inf0 = (LayoutInflater)
					GloneApp.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vConv = inf0.inflate(R.layout.liglonetz, null);
				registerForContextMenu(vConv);
			}
			TextView	tv0 = (TextView)vConv.findViewById(R.id.tv_ligtz_name);
			tv0.setText(m_atzsub.get(position).getTimeZoneId());
			return	vConv;
		}

		public void filter(String sarg) {
			if(sarg.length() == 0) {
				m_atzsub = m_atz;
				notifyDataSetChanged();
				return;
			}

			ArrayList<GloneTz> al0 = new ArrayList<GloneTz>();
			Iterator<GloneTz>	it0 = m_atz.iterator();
			while(it0.hasNext()) {
				GloneTz	tz0 = it0.next();
				if(tz0.getTimeZoneId().toUpperCase().indexOf(sarg.toUpperCase()) >= 0)
					al0.add(tz0);
			}
			m_atzsub = al0;
			notifyDataSetChanged();
		}
	}
}
