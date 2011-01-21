package net.cattaka.hk.uki2win.gl.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

public class ShapeSquare {
	private final static int VERTS = 4;

	private FloatBuffer mFVertexBuffer;
	private FloatBuffer mTexBuffer;
	private ShortBuffer mIndexBuffer;
	// A unit-sided equalateral triangle centered on the origin.
	private float[] verCoords;
	//private float scale = 1;
	
	private static final float[] texCoords = {
			// X, Y, Z
			 0.5f,-0.5f, 0, 
			-0.5f,-0.5f, 0,
			 0.5f, 0.5f, 0,
			-0.5f, 0.5f, 0
			};

	public ShapeSquare(RectF rect) {
		verCoords = new float[] {
			// X, Y, Z
			rect.left,		rect.top, 		0, 
			rect.right,	rect.top, 		0,
			rect.left,		rect.bottom, 	0,
			rect.right,	rect.bottom, 	0
		};
		
		// Buffers to be passed to gl*Pointer() functions
		// must be direct, i.e., they must be placed on the
		// native heap where the garbage collector cannot
		// move them.
		//
		// Buffers with multi-byte datatypes (e.g., short, int, float)
		// must have their byte order set to native order

		ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		mFVertexBuffer = vbb.asFloatBuffer();

		ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
		tbb.order(ByteOrder.nativeOrder());
		mTexBuffer = tbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();

		updateVerCoods();
		
		for (int i = 0; i < VERTS; i++) {
			for (int j = 0; j < 2; j++) {
				mTexBuffer.put(texCoords[i * 3 + j] + 0.5f);
			}
		}

		for (int i = 0; i < VERTS; i++) {
			mIndexBuffer.put((short) i);
		}

		mTexBuffer.position(0);
		mIndexBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glFrontFace(GL10.GL_CCW);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, VERTS,
				GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	}
	
	
//	public float getScale() {
//		return scale;
//	}
//
//	public void setScale(float scale) {
//		this.scale = scale;
//		updateVerCoods();
//	}

	private void updateVerCoods() {
		mFVertexBuffer.clear();
		for (int i = 0; i < VERTS; i++) {
			for (int j = 0; j < 3; j++) {
				mFVertexBuffer.put(verCoords[i * 3 + j]);
				//mFVertexBuffer.put(verCoords[i * 3 + j] * scale);
			}
		}
		mFVertexBuffer.position(0);
	}
}
