package com.odistagon.glone;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

public class DefRenderer implements Renderer
{
	private int			m_nWidth;
	private int			m_nHeight;
	float				m_fscrw;						// screen width in opengl unit
	float				m_fscrh;						// screen height in opengl unit

	private int[]		m_nTextures = new int[1];
	private int			m_nTextureId = 0;

	private TestCube	m_testcube = null;//new TestCube();
	private long		m_lLastRendered;
	private float		m_fStripeScaleH;
	private GlOneDoc	m_doc;
	private Gl2DString	m_glstr;
	private GlStripe	m_glstripe;
	private boolean		m_bNeedPersSet = true;			// perspective set.
	private float		m_fHorzShift = 0f;
	private long		m_lHorzReld = 0L;				// time when horizontal shift touch released

	public static float			CF_PERS_FOVY = 45f;
	public static final float	CF_PERS_NEAR = 2.0f;	// distance from eye point to near plane
	public static final float	CF_PERS_FAR_ = 6.0f;	// distance from eye point to far plane
	public static final float	CF_LOOK_EYZ = 4.0f;		// eye point

	private static final long	CL_FRAMPERD = 16L;		// constant frame period
	public static final long	CL_HORZRELD = 800L;		// time to released horizontal shift go back
	private static final float	CF_RIGHMRGN = 0.1f;		// right margin where stripes not being drawn

	public DefRenderer(GlOneDoc doc) {
		m_lLastRendered = System.currentTimeMillis();
		m_fStripeScaleH = 1.0f;
		m_doc = doc;
	}

	@Override
	public void onSurfaceCreated(GL10 gl0, EGLConfig arg1) {
		gl0.glEnable(GL10.GL_DEPTH_TEST);
		gl0.glDepthFunc(GL10.GL_LEQUAL);

		m_glstr = new Gl2DString();
		m_glstr.onSurfaceCreated(gl0, arg1);

		m_glstripe = new GlStripe();
		m_glstripe.onSurfaceCreated(gl0, arg1);

		makeOrgBuffs();

		// generate texture buffer
		if (m_nTextureId != 0){
			gl0.glDeleteTextures(1, m_nTextures, 0);
		}
		gl0.glGenTextures(1, m_nTextures, 0);
		m_nTextureId = m_nTextures[0];
		gl0.glBindTexture(GL10.GL_TEXTURE_2D, m_nTextureId);
		// NOTE if image is read from another dpi resource, that will be resized automatically. 
		Bitmap	bm0 = BitmapFactory.decodeResource(GloneApp.getContext().getResources(), R.drawable.timestr_m);
		Log.d(getClass().getName(), "texture size: (u, v each must be x^2) (" + bm0.getWidth() + ", " + bm0.getHeight() + ")");
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm0, 0);
		gl0.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl0.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		bm0.recycle();
	}

	@Override
	public void onSurfaceChanged(GL10 gl0, int width, int height) {
		m_nWidth = width;
		m_nHeight = height;
		gl0.glViewport(0, 0, width, height);
		gl0.glClearColor(0.85f, 0.85f, 0.85f, 1.0f);	// set background color (RGBA)

		m_fscrh = calcClipHeight(GlStripe.CF_VTXHUR_Z);
		m_fscrw = calcClipWidth(GlStripe.CF_VTXHUR_Z);
		m_bNeedPersSet = true;
	}

	@Override
	public void onDrawFrame(GL10 gl0) {
		if(m_bNeedPersSet) {
			gl0.glMatrixMode(GL10.GL_PROJECTION);
			gl0.glLoadIdentity();
			GLU.gluPerspective(gl0, CF_PERS_FOVY, (float)m_nWidth / (float)m_nHeight, CF_PERS_NEAR, CF_PERS_FAR_);
			GLU.gluLookAt(gl0, 0, 0, CF_LOOK_EYZ, 0, 0, 0, 0, 1.0f, 0);
			m_bNeedPersSet = false;
		}

		gl0.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//		gl0.glOrthof(-1, 1, -1 / ratio, 1 / ratio, 0.01f, 100.0f);
//		gl0.glViewport(0, 0, (int) _width, (int) _height);
		gl0.glMatrixMode(GL10.GL_MODELVIEW);
		gl0.glLoadIdentity();

		// make constant fps
		// http://stackoverflow.com/questions/4772693/
		long	ldt = System.currentTimeMillis() - m_lLastRendered;
		try {
			if(ldt < CL_FRAMPERD)
				Thread.sleep(CL_FRAMPERD - ldt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
		ArrayList<GloneTz>	altz = m_doc.getTzList();
		GloneTz				gtz1 = altz.get(0);
		int[]				andt = gtz1.getTimeNumbers(m_doc.getTime());

		// calc rotation
		if(m_testcube != null) {
			gl0.glPushMatrix();
			gl0.glScalef(0.3f, 0.3f, 0.3f);
			gl0.glRotatef((((float)andt[4]) / 24f) * 360f, 1f, 0f, 0f);
			gl0.glTranslatef(-2.0f, -1.5f, -0.5f);
			// cube
			m_testcube.draw(gl0);
			gl0.glPopMatrix();
		}

		gl0.glDisable(GL10.GL_LIGHTING);
		gl0.glDisable(GL10.GL_LIGHT0);
		gl0.glEnable(GL10.GL_BLEND);

		gl0.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl0.glEnable(GL10.GL_TEXTURE_2D);
		gl0.glBindTexture(GL10.GL_TEXTURE_2D, m_nTextureId);

		gl0.glPushMatrix();
		gl0.glScalef(1.0f, m_fStripeScaleH, 1.0f);
		// stripes
		Iterator<GloneTz>	it0 = altz.iterator();
		final float			frmgn = m_fscrw * CF_RIGHMRGN;	// right margin
		float				fm0 = (m_fscrw - frmgn) / altz.size();
		final int			ncharstz = 8;
		float				frabc = (m_fscrh / 2f) / (GlStripe.CRECTF_VTXABC.bottom * (float)ncharstz);
		float				fhorz = 0f;
		if(m_lHorzReld == 0) {
			fhorz = m_fHorzShift;
		} else
		if(m_lHorzReld + CL_HORZRELD > System.currentTimeMillis()) {
			float	f1 = ((float)(CL_HORZRELD - (System.currentTimeMillis() - m_lHorzReld)));
			fhorz = (m_fHorzShift * (f1 / CL_HORZRELD));
		} else {
			m_fHorzShift = 0f;
			m_lHorzReld = 0L;
		}
		gl0.glTranslatef(m_fscrw / 2f - (frmgn + GlStripe.CRECTF_VTXHUR.right)
				+ fhorz, 0.0f, 0.0f);	// draw from right toward left edge 
		while(it0.hasNext()) {
			GloneTz	gtz0 = it0.next();
			gl0.glPushMatrix();
			m_glstripe.drawStripe(gl0, gtz0, m_doc.getTime(), m_fscrh);
			gl0.glPopMatrix();
			// timezone names
			String	s0 = gtz0.getTimeZoneId();
			gl0.glPushMatrix();
			gl0.glTranslatef(GlStripe.CRECTF_VTXHUR.right - GlStripe.CRECTF_VTXABC.right,
					m_fscrh / 2 - GlStripe.CRECTF_VTXABC.bottom * 2f, 0f);
			gl0.glScalef(frabc, frabc, 1f);
			m_glstripe.drawAbcString(gl0, s0, ncharstz);
			gl0.glPopMatrix();
			gl0.glTranslatef(fm0 * -1f, 0.0f, 0.0f);	// -> left
		}
		gl0.glPopMatrix();

		gl0.glDisable(GL10.GL_TEXTURE_2D);

		gl0.glLoadIdentity();
		// org
		gl0.glPushMatrix();
		drawOrg(gl0);
		gl0.glPopMatrix();

		gl0.glEnable(GL10.GL_TEXTURE_2D);

		// date string
		gl0.glTranslatef(m_fscrw / -2f + GlStripe.CRECTF_VTXNUM.right, -0.20f, 0f);
		gl0.glPushMatrix();
		m_glstripe.drawNumberString(gl0, andt[2], 2);	// day
		gl0.glTranslatef(GlStripe.CRECTF_VTXNUM.right * 2f + 0.2f, 0f, 0f);
		m_glstripe.drawMonth(gl0, andt[1] - 1);		// month name
		gl0.glTranslatef(GlStripe.CRECTF_VTXMON.right + 0.2f + GlStripe.CRECTF_VTXNUM.right * 2f, 0f, 0f);
		m_glstripe.drawNumberString(gl0, andt[0], 4);	// year
		gl0.glPopMatrix();
		gl0.glLoadIdentity();
		float	fscale0 = (m_fscrw / 4f) / GlStripe.CRECTF_VTXNUM.right;
		gl0.glTranslatef(m_fscrw * 1f / 4f, GlStripe.CRECTF_VTXNUM.bottom * fscale0 * -1f + -0.2f, 0f);
		gl0.glScalef(fscale0, fscale0, 1.0f);
		m_glstripe.drawNumberString(gl0, andt[4] * 100 + andt[5], 4);	// hour+min.

		gl0.glDisable(GL10.GL_TEXTURE_2D);

		// draw debug text
		m_glstr.setColor(0xFF0000FF);
		gl0.glScalef(0.3f, 0.3f, 1.0f);
		gl0.glTranslatef(1.4f, 0.9f, 1.2f);
		altz = m_doc.getTzList();
		it0 = altz.iterator();
		while(it0.hasNext()) {
			GloneTz	tz0 = it0.next();
			String	s0 = Float.toString(tz0.getDSTOffsetInTheDay(GloneApp.getDoc().getTime()));
			m_glstr.setTextString(gl0, s0 + "*" + tz0.getDebugString(m_doc.getTime()));
			m_glstr.draw(gl0);
			gl0.glTranslatef(-0.1f, 0.2f, 0.0f);
		}

		//
		m_lLastRendered = System.currentTimeMillis();

		gl0.glDisable(GL10.GL_BLEND);
	}

	public void zoomIn(float frelative) {
		addFovy(frelative * -10f);
		m_bNeedPersSet = true;
	}

	public void addFovy(float fovy) {
		CF_PERS_FOVY += fovy;
		m_fscrh = calcClipHeight(GlStripe.CF_VTXHUR_Z);
		m_fscrw = calcClipWidth(GlStripe.CF_VTXHUR_Z);
	}

	public void addHorizontalShift(float farg) {
		m_fHorzShift += farg;
		m_lHorzReld = 0L;

		// change order when distance go over a threshold
		float	frmgn = m_fscrw * CF_RIGHMRGN;
		float	fm0 = (m_fscrw - frmgn) / m_doc.getTzList().size();
//		Log.d("XXXX", "add (" + m_fHorzShift + ", " + f0);
		if(m_fHorzShift > fm0) {
			m_fHorzShift -= fm0;
			m_doc.shiftTzOrder(false);
		} else
		if(m_fHorzShift < -fm0) {
			m_fHorzShift += fm0;
			m_doc.shiftTzOrder(true);
		}
	}

	public void releaseHorizontal() {
		m_lHorzReld = System.currentTimeMillis();
	}

	/**
	 * @return view width in pixels
	 */
	public int getWidth() {
		return	m_nWidth;
	}

	/**
	 * @return view height in pixels
	 */
	public int getHeight() {
		return	m_nHeight;
	}

	public float calcClipWidth(float fz) {
		return	calcClipHeight(fz) * (float)m_nWidth / (float)m_nHeight;
	}

	/** Calculate and return the height in logical unit, how tall things are drew in view.
	 */
	public static float calcClipHeight(float fz) {
		float	fradian = (float)Math.PI * ((CF_PERS_FOVY / 2f) / 180f);
		float	fret = (float)Math.tan(fradian) * (CF_LOOK_EYZ - fz);
		return	fret * 2;
	}

	private FloatBuffer	m_buffOrgVerts = null;
	private FloatBuffer	m_buffOrgColrs = null;

	private void makeOrgBuffs() {
		// vertices
		float[]	aftemp = new float[] {
				-0.9f,  0.0f, GlStripe.CF_VTXHUR_Z,	// LT
				+0.9f,  0.0f, GlStripe.CF_VTXHUR_Z,	// RT
				-0.9f, -0.9f, GlStripe.CF_VTXHUR_Z,	// LB
				+0.9f, -0.9f, GlStripe.CF_VTXHUR_Z,	// RB
		};
		m_buffOrgVerts = GloneUtils.makeFloatBuffer(aftemp);
		// colors RGBA
		aftemp = new float[] {
				+0.0f, 0.0f, 0.0f, 0.6f,
				+0.0f, 0.0f, 0.0f, 0.6f,
				+0.5f, 0.2f, 0.2f, 0.1f,
				+0.5f, 0.2f, 0.2f, 0.1f,
		};
		m_buffOrgColrs = GloneUtils.makeFloatBuffer(aftemp);
	}

	/** 
	 * @param gl
	 */
	private void drawOrg(GL10 gl) {
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_buffOrgVerts);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_buffOrgColrs);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glScalef(m_fscrw, m_fscrh, 1f);
		gl.glNormal3f(0, 0, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}
}
