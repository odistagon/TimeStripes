package com.odistagon.glone;

import java.nio.FloatBuffer;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLUtils;

/** (test class - currently not used)
 */
public class GlSpriteTex
{
	// vertices
	private float	m_afVtx[] = new float[] {
			-1.5f, -1.5f,
			 1.5f, -1.5f,
			-1.5f,  1.5f,
			 1.5f,  1.5f,
	};
	private FloatBuffer	m_fbfVtxBuff;
	// texture coords (up side down)
	private float	m_afTexCoords[] = new float[] {
			0.0f, 1.0f,
			1.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f,
	};
	private FloatBuffer	m_fbTexCoordBuff; 

	private int[]	m_nTextures = new int[1];
	private int		m_nTextureId = 0;

	private float	m_fxpos, m_fypos;	// text pos
	private float	m_fRotZ = 0.0f;		// rotation angle
	private int		m_nBmpWidth, m_nBmpHeight;	// texture size

	private static final float	TEXT_SIZE = 20.0f;			// text size
	private float	m_fTextWidth = 0.0f;	// text width
	private String	m_sText;
	private Paint	m_paint = null;
	private int		m_nLastDrawSecond = -1;	// time last drawn

	public GlSpriteTex() {
		;
	}

	/** call this in Renderer#onSurfaceCreated()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		m_paint = new Paint();
		m_paint.setTextSize(TEXT_SIZE);
//		m_paint.setAlpha(0);
		m_paint.setAntiAlias(true);

//		// calc bitmap width and height
//		m_sbText.append(formatString());
//		float[]	afWidths = new float[m_sbText.length()];
//		m_paint.getTextWidths(m_sbText.toString(), afWidths);
//		m_fTextWidth = 0.0f;
//		for (int i = 0; i < afWidths.length; i++){
//			m_fTextWidth += afWidths[i];
//		}
//		// calc pow(2) width can accomodate texts
//		m_nBmpWidth = 2;
//		while(m_fTextWidth > m_nBmpWidth){
//			m_nBmpWidth *= 2;
//		}
//		m_nBmpHeight = m_nBmpWidth;	// assume width equals height
		m_fTextWidth = 100.0f;
		m_nBmpWidth = 256;
		m_nBmpHeight = 256;

		// centering texts
		m_fxpos = ((float)m_nBmpWidth - m_fTextWidth) / 2.0f;
		m_fypos = ((float)m_nBmpHeight + TEXT_SIZE) / 2.0f;

		// generate buffer
		m_fbfVtxBuff = GloneUtils.makeFloatBuffer(m_afVtx);
		m_fbTexCoordBuff = GloneUtils.makeFloatBuffer(m_afTexCoords);
	}

	/**
	 * call this in Renderer#onDrawFrame()
	 */
	public void draw(GL10 gl) {
		Calendar	cal0 = Calendar.getInstance();
		int			nSecond = cal0.get(Calendar.SECOND);

		// use sub-seconds as angle
		m_fRotZ = ((float)nSecond + (float)cal0.get(Calendar.MILLISECOND) / 1000.0f) * 6.0f;

		// redraw for each one second
		if (m_nLastDrawSecond != nSecond){
			m_nLastDrawSecond = nSecond;
			if (m_nTextureId != 0){
				gl.glDeleteTextures(1, m_nTextures, 0);
			}

			gl.glGenTextures(1, m_nTextures, 0);
			m_nTextureId = m_nTextures[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, m_nTextureId);

			// generate a bitmap
			Bitmap	bm0 = Bitmap.createBitmap(m_nBmpWidth, m_nBmpHeight, Bitmap.Config.ARGB_8888);
			Canvas	canvas0 = new Canvas(bm0);
//			canvas0.drawColor(Color.TRANSPARENT);
			canvas0.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR );

			// draw text
			m_paint.setColor(0xBEFFFFFF);
			canvas0.drawText(m_sText, m_fxpos, m_fypos - TEXT_SIZE * 1, m_paint);
			m_paint.setColor(0xFFFFFFFF);
			canvas0.drawText(m_sText, m_fxpos, m_fypos + TEXT_SIZE * 0, m_paint);
			m_paint.setColor(0xBEFFFFFF);
			canvas0.drawText(m_sText, m_fxpos, m_fypos + TEXT_SIZE * 1, m_paint);

			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm0, 0);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			bm0.recycle();
		}

		// prepare drawing
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);

		// enable texture
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, m_nTextureId);
		// set vertex array
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, m_fbfVtxBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// set texture array
		gl.glTexCoordPointer(2 ,GL10.GL_FLOAT, 0, m_fbTexCoordBuff);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// draw
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, 1.0f);
		gl.glRotatef(m_fRotZ, 0.0f, 0.0f, -1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// disable things back
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}

	public void setTextString(String sarg) {
		m_sText = sarg;
	}
}
