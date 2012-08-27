package com.odistagon.glone;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FgAbout extends Fragment
{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View	vret = inflater.inflate(R.layout.about, container, false);

		View	v0 = vret.findViewById(R.id.iv_ot_cc_by_nd);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String	surl = getResources().getString(R.string.icdesc_cc_url);
				Uri		uri0 = Uri.parse(surl);
				Intent	i0 = new Intent(Intent.ACTION_VIEW, uri0);
				startActivity(i0);
			}
		});

		PackageInfo pi0 = null;
		try {
			pi0 = GloneApp.getContext().getPackageManager().getPackageInfo(
					GloneApp.getContext().getPackageName(), PackageManager.GET_META_DATA);
			TextView	tv0 = (TextView)vret.findViewById(R.id.tv_about_vers);
			tv0.setText(Float.toString((float)pi0.versionCode / 10f));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		//		return	super.onCreateView(inflater, container, savedInstanceState);
		return	vret;
	}
}
