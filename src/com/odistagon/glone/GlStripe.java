package com.odistagon.glone;

import java.nio.FloatBuffer;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;
import android.util.Log;

public class GlStripe
{
	private FloatBuffer	m_fbVtxStrp;
	private FloatBuffer	m_fbTexStrp; 
	private FloatBuffer	m_fbVtxNums;
	private FloatBuffer	m_fbTexNums; 
	private FloatBuffer	m_fbVtxMons;
	private FloatBuffer	m_fbTexMons; 
	private FloatBuffer	m_fbVtxAbcs;
	private FloatBuffer	m_fbTexAbcs; 
//	private FloatBuffer	m_buffColor;

	private static final float	CFTEXSIZ = 1024f;
	// hour stripe
	public static final RectF	CRECTF_VTXHUR = new RectF(0.0f, 0.0f, 0.5f, 0.2f);
	public static final float	CF_VTXHUR_Z = 1.2f;
	private static final RectF	CRECTF_TEXHUR = new RectF(0.0f / CFTEXSIZ, 0.0f, 96f / CFTEXSIZ, 40f / CFTEXSIZ);
	// numbers
	public static final RectF	CRECTF_VTXNUM = new RectF(0.0f, 0.0f, 0.2f, 0.15f);
	public static final float	CF_VTXNUM_Z = 1.2f;
	private static final RectF	CRECTF_TEXNUM = new RectF(480f / CFTEXSIZ, 0.0f, (480f + 96f) / CFTEXSIZ, 96f / CFTEXSIZ);
	// name of months
	public static final RectF	CRECTF_VTXMON = new RectF(0.0f, 0.0f, 0.3f, 0.15f);
	public static final float	CF_VTXMON_Z = 1.2f;
	private static final RectF	CRECTF_TEXMON = new RectF(192f / CFTEXSIZ, 0.0f, (192f + 96f) / CFTEXSIZ, 48f / CFTEXSIZ);
	// alphabets
	public static final RectF	CRECTF_VTXABC = new RectF(0.0f, 0.0f, 0.1f, 0.1f);
	public static final float	CF_VTXABC_Z = 1.2f;
	private static final RectF	CRECTF_TEXABC = new RectF(864f / CFTEXSIZ, 0.0f, (864f + 32f) / CFTEXSIZ, 32f / CFTEXSIZ);

	public GlStripe() {
	}

	public static float getVtxHeightOfOneHour() {
		return	CRECTF_VTXHUR.bottom;	// TODO take scaling into account
	}

	/** call this in Renderer#onSurfaceCreated()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		FloatBuffer	fb0[] = null;
		// Stripe
		fb0 = makeVertBuffs(24, CRECTF_VTXHUR, true, CF_VTXHUR_Z, CRECTF_TEXHUR);
		m_fbVtxStrp = fb0[0];	m_fbTexStrp = fb0[1];
		// Numbers
		fb0 = makeVertBuffs(10, CRECTF_VTXNUM, false, CF_VTXNUM_Z, CRECTF_TEXNUM);
		m_fbVtxNums = fb0[0];	m_fbTexNums = fb0[1];
		// Name of months
		fb0 = makeVertBuffs(12, CRECTF_VTXMON, false, CF_VTXMON_Z, CRECTF_TEXMON);
		m_fbVtxMons = fb0[0];	m_fbTexMons = fb0[1];
		// Alphabets
		fb0 = makeVertBuffs(26 + 6, CRECTF_VTXABC, false, CF_VTXABC_Z, CRECTF_TEXABC);
		m_fbVtxAbcs = fb0[0];	m_fbTexAbcs = fb0[1];
	}

	/** Makes coords for vertically arranged tile textures
	 * @param nelems
	 * @param fvert stores vert LT, RT, LB, RB
	 * @param ftex stores texture left, top, width of a element, height of a element
	 * @return
	 */
	private static FloatBuffer[] makeVertBuffs(int nelems, RectF fvert, boolean bstacktiledvtx, float fzarg, RectF ftex) {
		int			nidx = 0;
		float[]		aftemp = null;
		FloatBuffer	afbret[] = new FloatBuffer[2];

		// vertex buffer
		nidx = 0;
		aftemp = new float[nelems * 4 * 3];	// nelems * 4(rectangle) * 3(xyz)
		for(int i = 0; i < nelems; i++) {
			float	ftop, fbottom;
			if(bstacktiledvtx) {	// make vertically tiled vertices?
				ftop = (fvert.top + fvert.bottom * (float)i);
				fbottom = (ftop + fvert.bottom);
			} else {
				ftop = fvert.top;
				fbottom = fvert.bottom;
			}
			// coords: TL TR BL BR
			aftemp[nidx++] = fvert.left;	aftemp[nidx++] = ftop;		aftemp[nidx++] = fzarg;
			aftemp[nidx++] = fvert.right;	aftemp[nidx++] = ftop;		aftemp[nidx++] = fzarg;
			aftemp[nidx++] = fvert.left;	aftemp[nidx++] = fbottom;	aftemp[nidx++] = fzarg;
			aftemp[nidx++] = fvert.right;	aftemp[nidx++] = fbottom;	aftemp[nidx++] = fzarg;
		}
		afbret[0] = GloneUtils.makeFloatBuffer(aftemp);
//		for(int i = 0; i < 24; i++) {
//			Log.d("dump: ", ": [" + i + "](" + aftemp[i * 3 + 0] + ", " + aftemp[i * 3 + 1] + ", " + aftemp[i * 3 + 2]);
//		}

		// texture coods buffer
		nidx = 0;
		aftemp = new float[nelems * 4 * 2];	// nelems * 4 (rectangle) + (x, y)
		for(int i = 0; i < nelems; i++) {
			float	ftop, fbottom;
			if(bstacktiledvtx) {
				ftop = (((float)(nelems - i - 1)) * ftex.bottom);
				fbottom = (((float)(nelems - i)) * ftex.bottom);
			} else {
				ftop = (((float)(i + 0)) * ftex.bottom);
				fbottom = (((float)(i + 1)) * ftex.bottom);
			}
			aftemp[nidx++] = ftex.left;		aftemp[nidx++] = fbottom;
			aftemp[nidx++] = ftex.right;	aftemp[nidx++] = fbottom;
			aftemp[nidx++] = ftex.left;		aftemp[nidx++] = ftop;
			aftemp[nidx++] = ftex.right;	aftemp[nidx++] = ftop;
		}
		afbret[1] = GloneUtils.makeFloatBuffer(aftemp);

		return	afbret;
	}

	/**
	 * @param gtz
	 * @param ltime
	 * @param fscrhgt logical height of screen
	 */
	public void drawStripe(GL10 gl, GloneTz gtz, long ltime, float fscrhgt) {
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_fbVtxStrp);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_buffColor);
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glTexCoordPointer(2 ,GL10.GL_FLOAT, 0, m_fbTexStrp);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// draw
		float	fhrsfrommn = gtz.getHoursFromMidNight(ltime);				// hour number is useless because sometimes 1 a.m. comes twice
		gl.glTranslatef(0.0f, GlStripe.getVtxHeightOfOneHour()
				* (fhrsfrommn * -1f), 0.0f);								// apply hour, minute offset
		Calendar	c0 = Calendar.getInstance(gtz.getTimeZone());
		c0.setTimeInMillis(ltime);
		// start from a day before
		c0.roll(Calendar.DAY_OF_MONTH, false);
		float	fdstoffset = gtz.getDSTOffsetInTheDay(c0.getTimeInMillis());
		float	fhrs = (24f + fdstoffset);
		gl.glTranslatef(0.0f, GlStripe.getVtxHeightOfOneHour() * (fhrs * -1f), 0.0f);
		// loop 3 days
		for(int i = 0; i < 3; i++) {
			drawAStripe(gl, gtz, fdstoffset);
			gl.glTranslatef(0.0f, GlStripe.getVtxHeightOfOneHour() * 24f, 0.0f);
			// go to the next day
			c0.roll(Calendar.DAY_OF_MONTH, true);
			fdstoffset = gtz.getDSTOffsetInTheDay(c0.getTimeInMillis());
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	private static void drawAStripe(GL10 gl, GloneTz gtz, float fdstoffset) {
		int	n0 = 0;
		// draw 0, 1
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4 * 0, 4 * 2);
		// draw from 2
		if(fdstoffset > 0) {
			gl.glTranslatef(0.0f, GlStripe.getVtxHeightOfOneHour() * 1f, 0.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4 * 1, 4 * 1);	// duplicate 1
			gl.glTranslatef(0.0f, GlStripe.getVtxHeightOfOneHour() * -1f, 0.0f);
		} else
		if(fdstoffset < 0) {
			// don't cover 1 and restart with 3.
			n0 = 1;
		}
		gl.glTranslatef(0.0f, GlStripe.getVtxHeightOfOneHour() * (fdstoffset), 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4 * (2 + n0), 4 * (24 - 2 - n0));
	}

	/**
	 * @param narg
	 * @param nmindigits minimum digits number (0 fill)
	 */
	public void drawNumberString(GL10 gl, int narg, int nmindigits) {
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_fbVtxNums);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glTexCoordPointer(2 ,GL10.GL_FLOAT, 0, m_fbTexNums);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		int		nd = 0;
		while(true) {
			int	n0 = narg % 10;
			gl.glNormal3f(0, 0, 1.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, n0 * 4, 4);
			gl.glTranslatef(CRECTF_VTXNUM.right * -1f, 0.0f, 0.0f);
			nd++;
			if(narg < 10)
				break;
			narg /= 10;
		}
		for(int i = nd; i < nmindigits; i++) {
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0 * 4, 4);
			gl.glTranslatef(CRECTF_VTXNUM.right * -1f, 0.0f, 0.0f);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	public void drawMonth(GL10 gl, int narg) {
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_fbVtxMons);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glTexCoordPointer(2 ,GL10.GL_FLOAT, 0, m_fbTexMons);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glNormal3f(0, 0, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, narg * 4, 4);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	/**
	 * @param gl
	 * @param sarg
	 * @param nmax maximum number of characters drawing
	 */
	public void drawAbcString(GL10 gl, String sarg, int nmax) {
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_fbVtxAbcs);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glTexCoordPointer(2 ,GL10.GL_FLOAT, 0, m_fbTexAbcs);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		for(int i = 0; i < sarg.length(); i++) {
			char	c0 = sarg.charAt(i);
			if(c0 >= 'A' && c0 <= 'Z')
				c0 = (char)('a' + (c0 - 'A'));	// de-capitalize
			else if(c0 < 'a' || c0 > 'z')
				c0 = 'z' + 1;
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, (c0 - 'a') * 4, 4);
			gl.glTranslatef(0.0f, CRECTF_VTXABC.bottom * -1f, 0.0f);
			if(i > nmax)	// maximum number of chars
				break;
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
}
