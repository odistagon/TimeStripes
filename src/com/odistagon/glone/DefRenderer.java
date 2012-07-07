package com.odistagon.glone;

import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

public class DefRenderer implements Renderer
{
	private TestCube	m_testcube = new TestCube();
	private long		m_lLastRendered, m_lLastMoved;
	private float		m_foffsx, m_foffsy;	// offset
	private float		m_fvelox, m_fveloy;	// velocity
	private float		m_fStripeScaleH;
	private GlOneDoc	m_doc;
//	private GlSpriteTex	m_gltext;
	private Gl2DString	m_glstr;
	private GlStripe	m_glstripe;

	public DefRenderer(GlOneDoc doc) {
		m_lLastRendered = System.currentTimeMillis();
		m_fStripeScaleH = 1.0f;
		m_doc = doc;
	}

	@Override
	public void onSurfaceCreated(GL10 gl0, EGLConfig arg1) {
		gl0.glEnable(GL10.GL_DEPTH_TEST);
		gl0.glDepthFunc(GL10.GL_LEQUAL);

//		m_gltext = new GlSpriteTex();
//		m_gltext.onSurfaceCreated(gl0, arg1);
		m_glstr = new Gl2DString();
		m_glstr.onSurfaceCreated(gl0, arg1);

		m_glstripe = new GlStripe();
		m_glstripe.onSurfaceCreated(gl0, arg1);

		makeOrgBuffs();
	}

	@Override
	public void onSurfaceChanged(GL10 gl0, int width, int height) {
		gl0.glViewport(0, 0, width, height);

		gl0.glMatrixMode(GL10.GL_PROJECTION);
		gl0.glLoadIdentity();
		gl0.glPushMatrix();
		GLU.gluPerspective(gl0, 45f, (float)width / (float)height, 1f, 50.0f);
		GLU.gluLookAt(gl0, 0, 0, 1.0f, 0, 0, 0, 0, 1.0f, 0);
		gl0.glPopMatrix();

		gl0.glClearColor(0.2f, 0.0f, 0.0f, 1.0f);	// set background color (RGBA)
	}

	@Override
	public void onDrawFrame(GL10 gl0) {
		gl0.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//		gl0.glOrthof(-1, 1, -1 / ratio, 1 / ratio, 0.01f, 100.0f);
//		gl0.glViewport(0, 0, (int) _width, (int) _height);
		gl0.glMatrixMode(GL10.GL_MODELVIEW);
		gl0.glLoadIdentity();
		gl0.glTranslatef(0, 0, +10.0f);

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

		// calc velocity (TODO should be calculated by time scale)
		m_fvelox /= 1.4f;
		m_fveloy /= 1.4f;
		// move time forward/ backward
		m_doc.addTime((long)m_fveloy * 1000L * 1000L);

		gl0.glEnable(GL10.GL_BLEND);
		gl0.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
//		gl0.glShadeModel(GL10.GL_SMOOTH);	// GL_FLAT

		// Lighting
//		final float[] afLightAmbient = { 1.0f, 1.0f, 1.0f, 0.6f };
//		final float[] afLightDiffuse = { 1.0f, 1.0f, 0.3f, 0.6f };
//		final float[] afLightPosition = { 0.0f, 0.0f, 3.0f, 0.6f };
//		gl0.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, afLightAmbient, 0);
//		gl0.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, afLightDiffuse, 0);
//		gl0.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, afLightPosition, 0);
		gl0.glEnable(GL10.GL_LIGHTING);
		gl0.glEnable(GL10.GL_LIGHT0);
//		final float[] matAmbient = { 0.3f, 0.3f, 0.3f, 0.6f };
//		final float[] matDiffuse = { 0.6f, 0.6f, 0.6f, 0.6f };
//		gl0.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
//		gl0.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
//		gl0.glEnable(GL10.GL_COLOR_MATERIAL);
//		gl0.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

		// draw objects

		// calc rotation
		m_foffsx += m_fvelox;
		m_foffsy += m_fveloy;
		gl0.glPushMatrix();
		gl0.glRotatef(m_foffsx, 0, 1, 0);
		gl0.glRotatef(m_foffsy, 1, 0, 0);
		// cube
		m_testcube.draw(gl0);
		gl0.glPopMatrix();

		gl0.glDisable(GL10.GL_LIGHTING);
		gl0.glDisable(GL10.GL_LIGHT0);

		gl0.glPushMatrix();
		gl0.glScalef(1.0f, m_fStripeScaleH, 1.0f);
		// stripes
		gl0.glTranslatef(0.0f, m_glstripe.getVtxHeightOfOneHour() * m_doc.getTimeOffset() * -1.0f, 0.0f);
		m_glstripe.draw(gl0);
		gl0.glTranslatef(-0.9f, m_glstripe.getVtxHeightOfOneHour() * -1.0f, 0.0f);
		m_glstripe.draw(gl0);
		gl0.glPopMatrix();

		// org
		drawOrg(gl0);

		// draw text
		gl0.glRotatef(0.0f, 1, 1, 0);
		Calendar			cal0 = Calendar.getInstance();
		cal0.setTimeInMillis(m_doc.getTime());
		SimpleDateFormat	sdf0 = new SimpleDateFormat("MMM/dd hh:mm:ss.SSS");
		String				stemp0 = "XXXX";
		sdf0.setTimeZone(TimeZone.getTimeZone("Japan"));
		stemp0 += sdf0.format(cal0.getTime()) + "\r\n";
		sdf0.setTimeZone(TimeZone.getTimeZone("Pacific"));
		stemp0 += sdf0.format(cal0.getTime()) + "\r\n";
//		m_gltext.setTextString(stemp0);
//		m_gltext.draw(gl0);
		m_glstr.setTextString(gl0, stemp0);
		m_glstr.setRotatef(0.0f, 0.0f,
				((float)cal0.get(Calendar.SECOND) + (float)cal0.get(Calendar.MILLISECOND) / 1000.0f) * 6.0f);	// use sub-seconds as angle
		m_glstr.setColor(0xFFFFFFFF);
		m_glstr.draw(gl0);

		//
		m_lLastRendered = System.currentTimeMillis();
	}

	public void changeVelocity(float fxarg, float fyarg) {
		m_fvelox = fxarg;
		m_fveloy = fyarg;
		m_lLastMoved = System.currentTimeMillis();
	}

	public void addTime(float fxarg, float fyarg) {
		m_doc.addTime((long)fyarg * 1000L);
	}

	public void zoomIn(float frelative) {
		m_fStripeScaleH *= frelative;
		Log.d("X", "zoom (" + m_fStripeScaleH  + ")");
	}

	private FloatBuffer	m_buffOrgVerts = null;
	private FloatBuffer	m_buffOrgColrs = null;

	private void makeOrgBuffs() {
		// vertices
		float[]	aftemp = new float[] {
				-0.9f,  0.0f, -0.8f,	// LT
				+0.9f,  0.0f, -0.8f,	// RT
				-0.9f, +1.0f, -0.8f,	// LB
				+0.9f, +1.0f, -0.8f,	// RB
		};
		m_buffOrgVerts = GloneUtils.makeFloatBuffer(aftemp);
		// colors RGBA
		aftemp = new float[] {
				+0.5f, 0.0f, 0.5f, 0.4f,
				+0.5f, 0.0f, 0.5f, 0.4f,
				+0.5f, 1.0f, 0.5f, 0.4f,
				+0.5f, 1.0f, 0.5f, 0.4f,
		};
		m_buffOrgColrs = GloneUtils.makeFloatBuffer(aftemp);
	}

	/** 
	 * @param gl
	 */
	private void drawOrg(GL10 gl) {
		// prepare drawing
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// set vertex array
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_buffOrgVerts);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// set color array
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_buffOrgColrs);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		// draw
		for(int i = 0; i < 1; i++) {
			gl.glNormal3f(0, 0, 1.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i * 4, 4);
		}

		// disable things back
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}
}
