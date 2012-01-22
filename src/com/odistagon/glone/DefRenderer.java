package com.odistagon.glone;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class DefRenderer implements Renderer
{
	private TestCube	m_testcube = new TestCube();
	private long		m_lLastRendered, m_lLastMoved;
	private float		m_fx, m_fy, m_fxmove, m_fymove;
	private GlOneDoc	m_doc;
	private GlSpriteTex	m_gltext;

	public DefRenderer(GlOneDoc doc) {
		m_lLastRendered = System.currentTimeMillis();
		m_doc = doc;
	}

	@Override
	public void onSurfaceCreated(GL10 gl0, EGLConfig arg1) {
		gl0.glEnable(GL10.GL_DEPTH_TEST);
		gl0.glDepthFunc(GL10.GL_LEQUAL);

		m_gltext = new GlSpriteTex();
		m_gltext.onSurfaceCreated(gl0, arg1);
	}

	@Override
	public void onSurfaceChanged(GL10 gl0, int width, int height) {
		gl0.glViewport(0, 0, width, height);

		gl0.glMatrixMode(GL10.GL_PROJECTION);
		gl0.glLoadIdentity();    
		GLU.gluPerspective(gl0, 45f,(float) width / height, 1f, 50f);
		GLU.gluLookAt(gl0, 0, 0, 3, 0, 0, 0, 0, 1, 0);

		gl0.glClearColor(0.2f, 0.0f, 0.0f, 1.0f);	// set background color (RGBA)
	}

	@Override
	public void onDrawFrame(GL10 gl0) {
		gl0.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl0.glMatrixMode(GL10.GL_MODELVIEW);
		gl0.glLoadIdentity();
		gl0.glTranslatef(0, 0, -3f);

		// make constant fps
		// http://stackoverflow.com/questions/4772693/
		long	ldt = System.currentTimeMillis() - m_lLastRendered;
		try {
			if(ldt < 33L)
				Thread.sleep(33L - ldt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		m_lLastRendered = System.currentTimeMillis();

		// draw cube
		m_fxmove /= 1.4f;
		m_fymove /= 1.4f;
		m_fx += m_fxmove;
		m_fy += m_fymove;
		gl0.glPushMatrix();
		gl0.glRotatef(m_fx, 0, 1, 0);
		gl0.glRotatef(m_fy, 1, 0, 0);
		//
		gl0.glEnable(GL10.GL_LIGHTING);
		gl0.glEnable(GL10.GL_LIGHT0);
		m_testcube.draw(gl0);
		gl0.glDisable(GL10.GL_LIGHTING);
		gl0.glDisable(GL10.GL_LIGHT0);
		gl0.glPopMatrix();

		// draw text
		gl0.glRotatef(0.0f, 1, 1, 0);
		Calendar	cal0 = Calendar.getInstance();
		cal0.setTimeInMillis(m_doc.getTime());
		SimpleDateFormat	sdf0 = new SimpleDateFormat("mm:ss:SSS");
		String	stemp0 = (cal0.getTimeInMillis() % 1000
				+ " " + sdf0.format(cal0.getTime()));
		m_gltext.setTextString(stemp0);
		m_gltext.draw(gl0);

		//
		m_lLastRendered = System.currentTimeMillis();
	}

	public void moveObject(float fxarg, float fyarg) {
		m_fxmove = fxarg;
		m_fymove = fyarg;
		m_lLastMoved = System.currentTimeMillis();
	}
}
