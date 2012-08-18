package com.odistagon.glone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

//PreferenceFragment is not included in support.v4 library...
public class AyPrefMain extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefmain);

		PreferenceScreen	ps0 = (PreferenceScreen)findPreference(
				getResources().getString(R.string.prefkey_wallpa));
		ps0.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent	i0 = new Intent();
				i0.setType("image/*");
				i0.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(i0, "Complete action using"), CN_AY_GALLRY);
				return false;
			}
		});
	}

	private static final int		CN_AY_GALLRY = 2;
	private static final int		CN_AY_CRPIMG = 3;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case CN_AY_GALLRY:
			if(resultCode == RESULT_OK) {
				doCrop(data.getData());
			}
			break;
		case CN_AY_CRPIMG:
			if(resultCode == RESULT_OK) {
				Bitmap	bm0 = data.getExtras().getParcelable("data");
				try {
					GloneUtils.saveWallpaperCache(this, bm0);
				} catch(IOException e) {
					Toast.makeText(this, R.string.toas_savcri, Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void doCrop(Uri uridata) {
		Intent	i0 = new Intent("com.android.camera.action.CROP");
		i0.setType("image/*");

		List<ResolveInfo>	lresi = getPackageManager().queryIntentActivities(i0, 0);
		int					nresis = lresi.size();
		if(nresis == 0) {
			Toast.makeText(this, R.string.toas_nofcra, Toast.LENGTH_LONG).show();
			return;
		}
		i0.setData(uridata);
		i0.putExtra("outputX", 256);
		i0.putExtra("outputY", 256);
		i0.putExtra("aspectX", 1);
		i0.putExtra("aspectY", 1);
		i0.putExtra("scale", true);
		i0.putExtra("return-data", true);
		if(nresis == 1) {
			Intent	in0 = new Intent(i0);
			ResolveInfo res = lresi.get(0);
			in0.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			startActivityForResult(in0, CN_AY_CRPIMG);
		} else {
			final ArrayList<CropOption>	alcopts = new ArrayList<CropOption>();
			for(ResolveInfo res : lresi) {
				final CropOption		copt = new CropOption();
				copt.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
				copt.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
				copt.appIntent = new Intent(i0);
				copt.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
				alcopts.add(copt);
			}
// show crop app selection: not implemented
//			CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("Choose Crop App");
//			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int item) {
//					startActivityForResult(cropOptions.get(item).appIntent, CN_AY_CRPIMG);
//				}
//			});
//			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					if(uri0 != null) {
//						getContentResolver().delete(uri0, null, null);
//						uri0 = null;
//					}
//				}
//			});
//			AlertDialog alert = builder.create();
//			alert.show();
			startActivityForResult(alcopts.get(0).appIntent, CN_AY_CRPIMG);
		}
	}

	/** Attirubtes holder for when multiple crop intents found.
	 */
	public class CropOption {
		public CharSequence		title;
		public Drawable			icon;
		public Intent			appIntent;
	}

}
