package com.odistagon.glone;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

public class GlStripe
{
	private FloatBuffer	m_fbVtxStrp;
	private FloatBuffer	m_fbTexStrp; 
//	private FloatBuffer	m_buffColor;
	private FloatBuffer	m_fbVtxNums;
	private FloatBuffer	m_fbTexNums; 

	// hour stripe
	private static final float	CF_VTXHUR_L = 0.0f;
	private static final float	CF_VTXHUR_T = 0.0f;
	private static final float	CF_VTXHUR_R = 0.5f;	// width of an hour
	private static final float	CF_VTXHUR_B = 0.2f;	// height of an hour
	private static final float	CF_TEXHUR_L = 0.0f;
	private static final float	CF_TEXHUR_T = 0.0f;
	private static final float	CF_TEXHUR_R = 96f / 1024f;
	private static final float	CF_TEXHUR_H = 40f / 1024f;
	// numbers
	private static final float	CF_VTXNUM_L = 0.0f;
	private static final float	CF_VTXNUM_T = 0.0f;
	private static final float	CF_VTXNUM_R = 0.2f;
	private static final float	CF_VTXNUM_B = 0.2f;
	private static final float	CF_TEXNUM_L = 96f / 1024f;
	private static final float	CF_TEXNUM_T = 0.0f;
	private static final float	CF_TEXNUM_R = 192f / 1024f;
	private static final float	CF_TEXNUM_H = 96f / 1024f;

	public GlStripe() {
	}

	public static float getVtxHeightOfOneHour() {
		return	CF_VTXHUR_B;	// TODO take scaling into account
	}

	/** call this in Renderer#onSurfaceCreated()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		FloatBuffer	fb0[] = null;
		// Stripe
		fb0 = makeVertBuffs(24,
				new RectF(CF_VTXHUR_L, CF_VTXHUR_T, CF_VTXHUR_R, CF_VTXHUR_B), 0.9f,
				new RectF(CF_TEXHUR_L, CF_TEXHUR_T, CF_TEXHUR_R, CF_TEXHUR_H));
		m_fbVtxStrp = fb0[0];	m_fbTexStrp = fb0[1];
		// Numbers
		fb0 = makeVertBuffs(10,
				new RectF(CF_VTXNUM_L, CF_VTXNUM_T, CF_VTXNUM_R, CF_VTXNUM_B), 0.2f,
				new RectF(CF_TEXNUM_L, CF_TEXNUM_T, CF_TEXNUM_R, CF_TEXNUM_H));
		m_fbVtxNums = fb0[0];	m_fbTexNums = fb0[1];
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

	public void drawStripe(GL10 gl) {
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
		for(int i = 0; i < 24; i++) {
			gl.glNormal3f(0, 0, 1.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i * 4, 4);
			gl.glTranslatef(0.0f, CF_VTXNUM_B, 0.0f);
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
			gl.glTranslatef(CF_VTXNUM_R * -1f, 0.0f, 0.0f);
			if(narg < 10)
				break;
			narg /= 10;
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
}
