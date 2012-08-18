package com.odistagon.glone;

import android.os.Bundle;
import android.preference.PreferenceActivity;

//PreferenceFragment is not included in support.v4 library...
public class AyPrefMain extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefmain);

//		PreferenceScreen	ps0 = (PreferenceScreen)findPreference(
//				getResources().getString(R.string.prefkey_lauabt));
//		ps0.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
////				Intent	i0 = new Intent(GloneApp.getContext(), AyTemp.class);
////				GloneApp.getContext().startActivity(i0);	// -> Android RuntimeException
//
//				return false;
//			}
//		});
	}
}
