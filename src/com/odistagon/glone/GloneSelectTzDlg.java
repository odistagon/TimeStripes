package com.odistagon.glone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GloneSelectTzDlg extends Dialog
{

	public GloneSelectTzDlg(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.selecttzdlg);
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

		// TZ select list
//		{
//			ArrayAdapter<CharSequence>	adapter =
//				new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_list_item_1);
//			adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
//			String[]	astzids = TimeZone.getAvailableIDs();
//			String		sprev = null;
//			for(int i = 0; i < astzids.length; i++) {
//				String	s0 = TimeZone.getTimeZone(astzids[i]).getDisplayName();
//				if(sprev != null && sprev.equals(s0))
//					continue;
//				adapter.add(s0);
//				sprev = s0;
//			}
//			ListView	lv0 = (ListView)findViewById(R.id.lv_seltz_main);
//			lv0.setAdapter(adapter);
////			lv0.setSelection(nidxdef);	// have default tz been selected?
//		}
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
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView	v0 = new TextView(getContext());
			v0.setText(m_atzsub.get(position).getTimeZoneId());
			return	v0;
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
