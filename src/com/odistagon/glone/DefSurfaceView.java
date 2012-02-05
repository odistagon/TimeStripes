package com.odistagon.glone;

import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;

public class DefSurfaceView extends GLSurfaceView
{
	private DefRenderer		m_renderer;
	private GestureDetector	m_gdtctr;

	public DefSurfaceView(AyTop aytop) {
		super(aytop);
		final AyTop aytop0 = aytop;

		m_renderer = new DefRenderer(aytop.getDoc());
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
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

				m_renderer.changeVelocity(distanceX * -1.0f, distanceY * -1.0f);

				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

				m_renderer.changeVelocity(velocityX, velocityY);

				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {

				m_renderer.changeVelocity(0, 0);

				return false;
			}
		};
		m_gdtctr = new GestureDetector(getContext(), ogl0);
	}

	public boolean fireTouchEvent(MotionEvent event) {
		m_gdtctr.onTouchEvent(event);

		return super.onTouchEvent(event);
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