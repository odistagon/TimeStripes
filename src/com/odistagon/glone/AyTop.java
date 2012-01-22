package com.odistagon.glone;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class AyTop extends Activity
{
	private GlOneDoc		m_doc;
	private DefSurfaceView	m_gv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// document have to be instantiated before views
		m_doc = new GlOneDoc();
		m_gv = new DefSurfaceView(this);

//		setContentView(R.layout.main);
		setContentView(m_gv);
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

	/** provides fast access path to document object for views, etc.
	 */
	public GlOneDoc getDoc() {
		return	m_doc;
	}
}