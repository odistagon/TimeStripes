package com.odistagon.glone;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;

public class DefSurfaceView extends GLSurfaceView
{
	private DefRenderer		m_renderer;
	private GestureDetector	m_gdtctr;

	public DefSurfaceView(AyTop aytop) {
		super(aytop);
//		final AyTop		aytop0 = aytop;
//		WindowManager	wm0 = (WindowManager)aytop0.getSystemService(android.content.Context.WINDOW_SERVICE);
//		final Display	disp0 = wm0.getDefaultDisplay();

		m_renderer = new DefRenderer(GloneApp.getDoc());
		setRenderer(m_renderer);

		OnGestureListener ogl0 = new OnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float fcx, float fcy) {
				// reverse calc pixels -> time
				float	fmoved = (fcy / (float)m_renderer.getHeight()); 
				long	ltimemoved = (long)(fmoved * (60f * 60f * 1000f) / GlStripe.getVtxHeightOfOneHour());
//				Log.d(getClass().getName(), "moved (" + fmoved + " / " + m_renderer.getHeight() + ")");
//				Log.d(getClass().getName(), "scroll(" + fcy + " -> " + ltimemoved + ")");
				GloneApp.getDoc().addTimeOffset(ltimemoved * -1L, false);

				float	fhorz = (fcx / (float)m_renderer.getWidth());
				m_renderer.addHorizontalShift(fhorz * -1f);
//				Log.d("(X)", "onScroll() action: " + e2.getAction());

				return	false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				// velocity = pixels / sec.
				float	fcy = velocityY * ((float)GlOneDoc.CL_ANIMPERD / 1000.0f);
				// reverse calc pixels -> time
				float	fmoved = (fcy / (float)m_renderer.getHeight()); 
				long	ltimemoved = (long)(fmoved * (60f * 60f * 1000f) / GlStripe.getVtxHeightOfOneHour());

//				Log.d(getClass().getName(), "moved (" + fmoved + " / " + m_renderer.getHeight() + ")");
//				Log.d(getClass().getName(), "fling (" + fcy + " -> " + ltimemoved + ")");
				GloneApp.getDoc().addTimeOffset(ltimemoved * +1L, true);

				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return	false;
			}
		};
		m_gdtctr = new GestureDetector(getContext(), ogl0);

		setLongClickable(true);	// this is important to enable ACTION_UP event
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
//				Log.d("XXXX", "ACTION_(" + event.getAction());
				switch(event.getAction()) {
				case MotionEvent.ACTION_UP:
					m_renderer.releaseHorizontal();
					break;
				}
				m_gdtctr.onTouchEvent(event);
				return	false;
			}
		});
	}

	public void zoomIn(float frelative) {
		m_renderer.zoomIn(frelative);
	}

	// pinch gesture zooming
	// from API Level 8
	// http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
//	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//		@Override
//		public boolean onScale(ScaleGestureDetector detector) {
//			mScaleFactor *= detector.getScaleFactor();
//
//			// Don't let the object get too small or too large.
//			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
//
//			invalidate();
//			return true;
//		}
//	}

}