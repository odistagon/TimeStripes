package com.odistagon.glone;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AyPrefMain extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefmain);
	}

}
