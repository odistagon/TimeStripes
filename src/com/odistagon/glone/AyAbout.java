package com.odistagon.glone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AyAbout extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);

		View	v0 = findViewById(R.id.iv_ot_cc_by_nd);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String	surl = getResources().getString(R.string.icdesc_cc_url);
				Uri		uri0 = Uri.parse(surl);
				Intent	i0 = new Intent(Intent.ACTION_VIEW, uri0);
				startActivity(i0);
			}
		});
	}
}
