package com.odistagon.glone;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class TestCube {

	private final FloatBuffer mVertexBuffer;

	public TestCube(){

		float vertices[] = {
				// front
				-0.5f, -0.5f, 0.5f,
				0.5f, -0.5f, 0.5f,
				-0.5f, 0.5f, 0.5f,
				0.5f, 0.5f, 0.5f,
				// back
				-0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, -0.5f,
				-0.5f, 0.5f, -0.5f,
				0.5f, 0.5f, -0.5f,
				// left
				-0.5f, -0.5f, 0.5f,
				-0.5f, -0.5f, -0.5f,
				-0.5f, 0.5f, 0.5f,
				-0.5f, 0.5f, -0.5f,
				// right
				0.5f, -0.5f, 0.5f,
				0.5f, -0.5f, -0.5f,
				0.5f, 0.5f, 0.5f,
				0.5f, 0.5f, -0.5f,
				// top
				-0.5f, 0.5f, 0.5f,
				0.5f, 0.5f, 0.5f,
				-0.5f, 0.5f, -0.5f,
				0.5f, 0.5f, -0.5f,
				// bottom
				-0.5f, -0.5f, 0.5f,
				0.5f, -0.5f, 0.5f,
				-0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, -0.5f
		};

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

	}

	public void draw(GL10 gl) {
		int	i = 0;

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		// Front
		gl.glNormal3f(0, 0, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i++ * 4, 4);
		// Back
		gl.glNormal3f(0, 0, -1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i++ * 4, 4);
		// Left
		gl.glNormal3f(-1.0f, 0, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i++ * 4, 4);
		// Right
		gl.glNormal3f(1.0f, 0, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i++ * 4, 4);
		// Top
		gl.glNormal3f(0, 1.0f, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i++ * 4, 4);
		// Right
		gl.glNormal3f(0, -1.0f, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i++ * 4, 4);

	}
}
