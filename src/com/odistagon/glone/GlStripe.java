package com.odistagon.glone;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class GlStripe
{
	private FloatBuffer	m_buffVerts;
	private FloatBuffer	m_buffColor;

	public GlStripe() {
//		float afvertices[] = {
//				// 
//				-0.7f, -0.7f, 0.2f,
//				 0.7f, -0.7f, 0.2f,
//				-0.7f,  0.0f, 0.2f,
//				 0.7f,  0.0f, 0.2f,
//				// 
//				-0.7f,  0.0f, -0.2f,
//				 0.7f,  0.0f, -0.2f,
//				-0.7f,  0.7f, -0.2f,
//				 0.7f,  0.7f, -0.2f,
//				// 
//				-0.7f,  0.7f,  0.1f,
//				 0.7f,  0.7f,  0.1f,
//				-0.7f,  1.0f,  0.1f,
//				 0.7f,  1.0f,  0.1f,
//		};
//		m_buffVerts = makeBuffer(afvertices);
		makeStripesVerts();

//		float afcolors[] = {
//				1.0f, 1.0f, 0.0f, 1.0f,	1.0f, 1.0f, 0.0f, 1.0f,	1.0f, 1.0f, 0.0f, 1.0f,	1.0f, 1.0f, 0.0f, 1.0f,
//				1.0f, 1.0f, 0.0f, 1.0f,	1.0f, 1.0f, 0.0f, 1.0f,	1.0f, 1.0f, 0.0f, 1.0f,	1.0f, 1.0f, 0.0f, 1.0f,
//				1.0f, 1.0f, 0.0f, 1.0f,	1.0f, 1.0f, 0.0f, 1.0f,	1.0f, 1.0f, 0.0f, 1.0f,	1.0f, 1.0f, 0.0f, 1.0f,
//		};
		float[]		afcolors = new float[24 * 4 * 4];	// 24 * 4(rectangle)
		int			nidx = 0;
		final float	FALP = 0.8f;
		for(int i = 0; i < 24; i++) {
			afcolors[nidx++] = (float)(i % 2) * 0.8f;	afcolors[nidx++] = 0.8f;	afcolors[nidx++] = 0.8f;	afcolors[nidx++] = FALP;
			afcolors[nidx++] = (float)(i % 2) * 0.8f;	afcolors[nidx++] = 0.0f;	afcolors[nidx++] = 0.0f;	afcolors[nidx++] = FALP;
			afcolors[nidx++] = (float)(i % 2) * 0.8f;	afcolors[nidx++] = 0.8f;	afcolors[nidx++] = 0.8f;	afcolors[nidx++] = FALP;
			afcolors[nidx++] = (float)(i % 2) * 0.8f;	afcolors[nidx++] = 0.0f;	afcolors[nidx++] = 0.0f;	afcolors[nidx++] = FALP;
		}
		m_buffColor = makeBuffer(afcolors);
	}

	private void makeStripesVerts() {
		float[]		aftemp = new float[24 * 4 * 3];	// 24 * 4(rectangle) * 3(xyz)
		final float	FWIDTH = 0.5f, FHEIGHT = 0.2f, foffs = -2.4f;
		int			nbase = 0;
		for(int i = 0; i < 24; i++) {
			// coords: TL TR BL BR
			float	fbaseh = foffs + FHEIGHT * (float)i;
//			int		nbase = (i * (4 * 3));
			aftemp[nbase++] = FWIDTH * 0.0f;	aftemp[nbase++] = fbaseh + FHEIGHT * 0.0f;	aftemp[nbase++] = 0.7f;
			aftemp[nbase++] = FWIDTH * 1.0f;	aftemp[nbase++] = fbaseh + FHEIGHT * 0.0f;	aftemp[nbase++] = 0.7f;
			aftemp[nbase++] = FWIDTH * 0.0f;	aftemp[nbase++] = fbaseh + FHEIGHT * 1.0f;	aftemp[nbase++] = 0.7f;
			aftemp[nbase++] = FWIDTH * 1.0f;	aftemp[nbase++] = fbaseh + FHEIGHT * 1.0f;	aftemp[nbase++] = 0.7f;
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
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_buffVerts);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_buffColor);
		// 
		for(int i = 0; i < 24; i++) {
			gl.glNormal3f(0, 0, 1.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i * 4, 4);
		}
		// 
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}
}
