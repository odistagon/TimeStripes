package com.odistagon.glone;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

public class GlStripe
{
	private FloatBuffer	m_fbVtxStrp;
	private FloatBuffer	m_fbTexStrp; 
	private FloatBuffer	m_fbVtxNums;
	private FloatBuffer	m_fbTexNums; 
	private FloatBuffer	m_fbVtxMons;
	private FloatBuffer	m_fbTexMons; 
//	private FloatBuffer	m_buffColor;

	private static final float	CFTEXSIZ = 1024f;
	// hour stripe
	private static final RectF	CRECTF_VTXHUR = new RectF(0.0f, 0.0f, 0.5f, 0.2f);
	private static final float	CF_VTXHUR_Z = 0.9f;
	private static final RectF	CRECTF_TEXHUR = new RectF(0.0f, 0.0f, 96f / CFTEXSIZ, 40f / CFTEXSIZ);
	// numbers
	private static final RectF	CRECTF_VTXNUM = new RectF(0.0f, 0.0f, 0.2f, 0.15f);
	private static final float	CF_VTXNUM_Z = 0.2f;
	private static final RectF	CRECTF_TEXNUM = new RectF(96f / CFTEXSIZ, 0.0f, 192f / CFTEXSIZ, 96f / CFTEXSIZ);
	// name of months
	private static final RectF	CRECTF_VTXMON = new RectF(0.0f, 0.0f, 0.3f, 0.15f);
	private static final float	CF_VTXMON_Z = 0.2f;
	private static final RectF	CRECTF_TEXMON = new RectF(192f / CFTEXSIZ, 0.0f, 288f / CFTEXSIZ, 48f / CFTEXSIZ);

	public GlStripe() {
	}

	public static float getVtxHeightOfOneHour() {
		return	CRECTF_VTXHUR.bottom;	// TODO take scaling into account
	}

	public static RectF getVertexRectNumbers() {
		return	CRECTF_VTXNUM;
	}

	public static RectF getVertexRectMonthNames() {
		return	CRECTF_VTXMON;
	}

	/** call this in Renderer#onSurfaceCreated()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		FloatBuffer	fb0[] = null;
		// Stripe
		fb0 = makeVertBuffs(24, CRECTF_VTXHUR, CF_VTXHUR_Z, CRECTF_TEXHUR);
		m_fbVtxStrp = fb0[0];	m_fbTexStrp = fb0[1];
		// Numbers
		fb0 = makeVertBuffs(10, CRECTF_VTXNUM, CF_VTXNUM_Z, CRECTF_TEXNUM);
		m_fbVtxNums = fb0[0];	m_fbTexNums = fb0[1];
		// Name of months
		fb0 = makeVertBuffs(10, CRECTF_VTXMON, CF_VTXMON_Z, CRECTF_TEXMON);
		m_fbVtxMons = fb0[0];	m_fbTexMons = fb0[1];
	}

	/** Makes coords for vertically arranged tile textures
	 * @param nelems
	 * @param fvert stores vert LT, RT, LB, RB
	 * @param ftex stores texture left, top, width of a element, height of a element
	 * @return
	 */
	private static FloatBuffer[] makeVertBuffs(int nelems, RectF fvert, float fzarg, RectF ftex) {
		int			nidx = 0;
		float[]		aftemp = null;
		FloatBuffer	afbret[] = new FloatBuffer[2];

		// vertex buffer
		nidx = 0;
		aftemp = new float[nelems * 4 * 3];	// nelems * 4(rectangle) * 3(xyz)
		for(int i = 0; i < nelems; i++) {
			// coords: TL TR BL BR
			aftemp[nidx++] = fvert.left;	aftemp[nidx++] = fvert.top;		aftemp[nidx++] = fzarg;
			aftemp[nidx++] = fvert.right;	aftemp[nidx++] = fvert.top;		aftemp[nidx++] = fzarg;
			aftemp[nidx++] = fvert.left;	aftemp[nidx++] = fvert.bottom;	aftemp[nidx++] = fzarg;
			aftemp[nidx++] = fvert.right;	aftemp[nidx++] = fvert.bottom;	aftemp[nidx++] = fzarg;
		}
		afbret[0] = GloneUtils.makeFloatBuffer(aftemp);
//		for(int i = 0; i < 24; i++) {
//			Log.d("dump: ", ": [" + i + "](" + aftemp[i * 3 + 0] + ", " + aftemp[i * 3 + 1] + ", " + aftemp[i * 3 + 2]);
//		}

		// texture coods buffer
		nidx = 0;
		aftemp = new float[nelems * 4 * 2];	// nelems * 4 (rectangle) + (x, y)
		for(int i = 0; i < nelems; i++) {
			aftemp[nidx++] = ftex.left;		aftemp[nidx++] = (((float)(i + 1)) * ftex.bottom);
			aftemp[nidx++] = ftex.right;	aftemp[nidx++] = (((float)(i + 1)) * ftex.bottom);
			aftemp[nidx++] = ftex.left;		aftemp[nidx++] = (((float)(i + 0)) * ftex.bottom);
			aftemp[nidx++] = ftex.right;	aftemp[nidx++] = (((float)(i + 0)) * ftex.bottom);
		}
		afbret[1] = GloneUtils.makeFloatBuffer(aftemp);

		return	afbret;
	}

	public void drawStripe(GL10 gl, GloneTz gtz, long ltime) {
		// set vertex array
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_fbVtxStrp);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// set color array - not using
//		gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_buffColor);
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		// set texture array
		gl.glTexCoordPointer(2 ,GL10.GL_FLOAT, 0, m_fbTexStrp);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// draw
		int	nhours = (int)(DefRenderer.calcClipHeight(CF_VTXHUR_Z) / CRECTF_VTXHUR.bottom);	// how many hours are in screen height
		int	antime[] = gtz.getTimeNumbers(ltime);
		gl.glTranslatef(0.0f, -1.0f + GlStripe.getVtxHeightOfOneHour()
				* ((float)antime[5] / 60.0f) * +1.0f, 0.0f);
		for(int i = 0; i < nhours; i++) {	// TODO should be done by 1 or 2 glDrawArrays
			gl.glNormal3f(0, 0, 1.0f);
			int	n0 = (antime[4] + nhours / 2) - i - 1;
			n0 = 23 - (n0 + 24) % 24;
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, n0 * 4, 4);
			gl.glTranslatef(0.0f, CRECTF_VTXHUR.bottom, 0.0f);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	public void drawNumberString(GL10 gl, int narg) {
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_fbVtxNums);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glTexCoordPointer(2 ,GL10.GL_FLOAT, 0, m_fbTexNums);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		while(true) {
			int	n0 = narg % 10;
			gl.glNormal3f(0, 0, 1.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, n0 * 4, 4);
			gl.glTranslatef(CRECTF_VTXNUM.right * -1f, 0.0f, 0.0f);
			if(narg < 10)
				break;
			narg /= 10;
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
}
