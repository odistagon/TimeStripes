package com.odistagon.glone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FgMain extends Fragment
{
	private DefSurfaceView	m_gv;

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// main layout
		View	vret = inflater.inflate(R.layout.main, container, false);
		setHasOptionsMenu(true);	// important!

		RelativeLayout	rl0 = (RelativeLayout)vret.findViewById(R.id.lo_main_rl0);
		// insert GL view into the main layout
		m_gv = new DefSurfaceView();
		rl0.addView(m_gv, 0);

		//bottom toolbar buttons
		View	v0 = vret.findViewById(R.id.iv_main_menu);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent	i0 = new Intent(GloneApp.getContext(), (new AyPrefMain()).getClass());
				i0.setAction(Intent.ACTION_VIEW);
				startActivityForResult(i0, 0);
			}
		});
		v0 = vret.findViewById(R.id.iv_tb_zoomin);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_gv.zoomIn(-7f);
			}
		});
		v0 = vret.findViewById(R.id.iv_tb_zoomou);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_gv.zoomIn(+7f);
			}
		});
		// side toolbar buttons
		v0 = vret.findViewById(R.id.iv_tb_fastfw);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GloneApp.getDoc().addTimeOffset(24 * 60 * 60 * 1000 * +1L, true);
			}
		});
		v0 = vret.findViewById(R.id.iv_tb_rewind);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GloneApp.getDoc().addTimeOffset(24 * 60 * 60 * 1000 * -1L, true);
			}
		});
		v0 = vret.findViewById(R.id.iv_tb_thewld);
		v0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GloneApp.getDoc().togglePause();
				((ImageView)v).setImageResource(GloneApp.getDoc().isPaused()
						? R.drawable.ic_menu_stwp : R.drawable.ic_menu_stwm);
			}
		});
		v0.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				GloneApp.getDoc().zeroOffset();
				return	true;
			}
		});

//		return	super.onCreateView(inflater, container, savedInstanceState);
		return	vret;
	}

	@Override
	public void onPause() {
		super.onPause();
		m_gv.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		m_gv.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.xml.opme_aytop, menu);
//		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.opme_aytop_shwtxt:
			getActivity().showDialog(GloneUtils.NC_DLGID_SHWTXT);
			break;
		case R.id.opme_aytop_jmpabs:
			getActivity().showDialog(GloneUtils.NC_DLGID_DATPIC);
			break;
		case R.id.opme_aytop_fdstne:
		case R.id.opme_aytop_fdstpr:	{
			GloneTz	gtz0 = GloneApp.getDoc().getTzList().get(0);
			long	lgoto = gtz0.findNextDstChange(GloneApp.getDoc().getTime(),
				item.getItemId() == R.id.opme_aytop_fdstne);
			if(lgoto > 0)
				GloneApp.getDoc().setTimeAbsolute(lgoto, true);
		}	break;
		case R.id.opme_aytop_prefes:	{
			Intent	i0 = new Intent(GloneApp.getContext(), (new AyPrefMain()).getClass());
			i0.setAction(Intent.ACTION_VIEW);
			startActivityForResult(i0, 0);
		}	break;
		case R.id.opme_aytop_about_:	{
			FragmentManager		fm0 = getActivity().getSupportFragmentManager();
			FragmentTransaction	trans0 = fm0.beginTransaction();
			trans0.replace(R.id.fg_aymain_body, new FgAbout());
			trans0.addToBackStack(null);
			trans0.commit();
		}	break;
//		case GloneUtils.CMID_GLONE_SYSDAT:
//			startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
//			break;
		}
		return super.onOptionsItemSelected(item);
	}
}