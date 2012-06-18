package com.odistagon.glone;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class GlStripe
{
	private FloatBuffer	m_buffVerts;
	// texture coords (up side down)
	private float[]		m_afTexCoords;
	private FloatBuffer	m_fbTexCoordBuff; 
//	private FloatBuffer	m_buffColor;
	private int[]		m_nTextures = new int[1];
	private int			m_nTextureId = 0;

	public GlStripe() {
	}

	/** call this in Renderer#onSurfaceCreated()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// vertex buffer
		makeStripesVerts();

		// texture coods buffer
		float[]	aftexcoods = new float[24 * 4 * 2];	// 24 * 4 (rectangle) + (x, y)
		for(int i = 0; i < 24; i++) {
			aftexcoods[i * 8 + 0] = 0.0f;	aftexcoods[i * 8 + 1] = (((float)(i + 1)) * (1.0f / 24.0f));
			aftexcoods[i * 8 + 2] = 1.0f;	aftexcoods[i * 8 + 3] = (((float)(i + 1)) * (1.0f / 24.0f));
			aftexcoods[i * 8 + 4] = 0.0f;	aftexcoods[i * 8 + 5] = (((float)(i + 0)) * (1.0f / 24.0f));
			aftexcoods[i * 8 + 6] = 1.0f;	aftexcoods[i * 8 + 7] = (((float)(i + 0)) * (1.0f / 24.0f));
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
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm0, 0);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		bm0.recycle();
	}

	public static final float	FC_CY_1HR = 1.0f;	// height of an hour

	private void makeStripesVerts() {
		float[]		aftemp = new float[24 * 4 * 3];	// 24 * 4(rectangle) * 3(xyz)
		final float	FWIDTH = 0.5f, FHEIGHT = 0.2f, foffs = -2.4f;
		int			nbase = 0;
		for(int i = 0; i < 24; i++) {
			float	fbaseh = foffs + FHEIGHT * (float)i;
//			int		nbase = (i * (4 * 3));
			// coords: TL TR BL BR
			aftemp[nbase++] = FWIDTH * 0.0f;		aftemp[nbase++] = fbaseh + FHEIGHT * 0.0f;		aftemp[nbase++] = 0.7f;
			aftemp[nbase++] = FWIDTH * FC_CY_1HR;	aftemp[nbase++] = fbaseh + FHEIGHT * 0.0f;		aftemp[nbase++] = 0.7f;
			aftemp[nbase++] = FWIDTH * 0.0f;		aftemp[nbase++] = fbaseh + FHEIGHT * FC_CY_1HR;	aftemp[nbase++] = 0.7f;
			aftemp[nbase++] = FWIDTH * FC_CY_1HR;	aftemp[nbase++] = fbaseh + FHEIGHT * FC_CY_1HR;	aftemp[nbase++] = 0.7f;
		}
		m_buffVerts = makeBuffer(aftemp);
//		for(int i = 0; i < 24; i++) {
//			Log.d("dump: ", ": [" + i + "](" + aftemp[i * 3 + 0] + ", " + aftemp[i * 3 + 1] + ", " + aftemp[i * 3 + 2]);
//		}
	}

	private FloatBuffer makeBuffer(float[] afarg) {
		ByteBuffer	bb0 = ByteBuffer.allocateDirect(afarg.length * 4);
		bb0.order(ByteOrder.nativeOrder());
		FloatBuffer	fb0 = bb0.asFloatBuffer();
		fb0.put(afarg);
		fb0.position(0);
		return	fb0;
	}

	public void draw(GL10 gl) {
		// prepare drawing
		gl.glEnable(GL10.GL_BLEND);
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
		gl.glDisable(GL10.GL_BLEND);
	}
}
