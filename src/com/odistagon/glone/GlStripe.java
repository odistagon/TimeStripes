package com.odistagon.glone;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class GlStripe
{
	private FloatBuffer	m_buffVerts;
	// texture coords (up side down)
	private float[]		m_afTexCoords;
	private FloatBuffer	m_fbTexCoordBuff; 
//	private FloatBuffer	m_buffColor;
	private int[]		m_nTextures = new int[1];
	private int			m_nTextureId = 0;
	private static final float	CF_TEXHEIGONEHR = (1.0f / 24.0f);	// texture height of one hour
	private static final float	CF_VTXCX_STRIPE = 0.5f;	// width of an hour
	private static final float	CF_VTXCY_1HRSTR = 0.2f;	// height of an hour

	public GlStripe() {
	}

	public static float getVtxHeightOfOneHour() {
		return	CF_VTXCY_1HRSTR;	// TODO take scaling into account
	}

	public static float getTexHeightOfOneHour() {
		return	CF_TEXHEIGONEHR;
	}

	// hour stripe
	private static final float	CF_TEXHUR_L = 0.0f;
	private static final float	CF_TEXHUR_W = 96f / 1024f;
	private static final float	CF_TEXHUR_T = 0.0f;
	private static final float	CF_TEXHUR_H = 40f / 1024f;

	/** call this in Renderer#onSurfaceCreated()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// vertex buffer
		float[]		aftemp = new float[24 * 4 * 3];	// 24 * 4(rectangle) * 3(xyz)
		int			nbase = 0;
		for(int i = 0; i < 24; i++) {
			float	fbaseh = CF_VTXCY_1HRSTR * (float)i;
//			int		nbase = (i * (4 * 3));
			// coords: TL TR BL BR
			aftemp[nbase++] = 0.0f;				aftemp[nbase++] = fbaseh + 0.0f;			aftemp[nbase++] = 0.9f;
			aftemp[nbase++] = CF_VTXCX_STRIPE;	aftemp[nbase++] = fbaseh + 0.0f;			aftemp[nbase++] = 0.9f;
			aftemp[nbase++] = 0.0f;				aftemp[nbase++] = fbaseh + CF_VTXCY_1HRSTR;	aftemp[nbase++] = 0.9f;
			aftemp[nbase++] = CF_VTXCX_STRIPE;	aftemp[nbase++] = fbaseh + CF_VTXCY_1HRSTR;	aftemp[nbase++] = 0.9f;
		}
		m_buffVerts = GloneUtils.makeFloatBuffer(aftemp);
//		for(int i = 0; i < 24; i++) {
//			Log.d("dump: ", ": [" + i + "](" + aftemp[i * 3 + 0] + ", " + aftemp[i * 3 + 1] + ", " + aftemp[i * 3 + 2]);
//		}

		// texture coods buffer
		float[]	aftexcoods = new float[24 * 4 * 2];	// 24 * 4 (rectangle) + (x, y)
		for(int i = 0; i < 24; i++) {
			aftexcoods[i * 8 + 0] = CF_TEXHUR_L;	aftexcoods[i * 8 + 1] = (((float)(i + 1)) * CF_TEXHUR_H);
			aftexcoods[i * 8 + 2] = CF_TEXHUR_W;	aftexcoods[i * 8 + 3] = (((float)(i + 1)) * CF_TEXHUR_H);
			aftexcoods[i * 8 + 4] = CF_TEXHUR_L;	aftexcoods[i * 8 + 5] = (((float)(i + 0)) * CF_TEXHUR_H);
			aftexcoods[i * 8 + 6] = CF_TEXHUR_W;	aftexcoods[i * 8 + 7] = (((float)(i + 0)) * CF_TEXHUR_H);
		}
		m_afTexCoords = aftexcoods;

		// generate texture buffer
		m_fbTexCoordBuff = GloneUtils.makeFloatBuffer(m_afTexCoords);
		if (m_nTextureId != 0){
			gl.glDeleteTextures(1, m_nTextures, 0);
		}
		gl.glGenTextures(1, m_nTextures, 0);
		m_nTextureId = m_nTextures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, m_nTextureId);
		Bitmap	bm0 = BitmapFactory.decodeResource(GloneApp.getContext().getResources(), R.drawable.timestr_m);
		// TODO: reason why bitmap size is silently changed
		Log.d(getClass().getName(), "texture size: (u, v each must be x^2) (" + bm0.getWidth() + ", " + bm0.getHeight() + ")");
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm0, 0);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		bm0.recycle();
	}

	public void draw(GL10 gl) {
		// prepare drawing
		gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);

		// enable texture
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, m_nTextureId);
		// set vertex array
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_buffVerts);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// set color array - not using
//		gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_buffColor);
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		// set texture array
		gl.glTexCoordPointer(2 ,GL10.GL_FLOAT, 0, m_fbTexCoordBuff);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// draw
		for(int i = 0; i < 24; i++) {
			gl.glNormal3f(0, 0, 1.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i * 4, 4);
		}
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// disable things back
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
}
