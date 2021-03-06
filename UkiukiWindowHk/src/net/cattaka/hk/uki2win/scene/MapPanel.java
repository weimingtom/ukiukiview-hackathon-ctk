package net.cattaka.hk.uki2win.scene;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.RectF;
import android.net.Uri;

import net.cattaka.hk.uki2win.R;
import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.gl.TextureWrapper;
import net.cattaka.hk.uki2win.gl.shape.ShapeSquare;
import net.cattaka.hk.uki2win.math.CtkMath;
import net.cattaka.hk.uki2win.net.WebCacheUtil;

public class MapPanel extends AbstractSceneObject {
	private ShapeSquare shapeSquare;
	
	public MapPanel() {
		this.setFrontDirection(0,0,1);
		this.setUpDirection(0,1,0);
		
		RectF rect = new RectF(-0.5f, 0.5f, 0.5f,-0.5f);
		this.shapeSquare = new ShapeSquare(rect);
	}
	
	@Override
	public void initialize(Resources resources, SceneState sceneState, WebCacheUtil webCacheUtil) {
		this.setTextureWrapperArray(new TextureWrapper[] {
				webCacheUtil.getTextureWrapper(sceneState, Uri.parse(UkiukiWindowConstants.RESOURCE_URI_BASE + R.drawable.logo256))
		});
	}
	
	@Override
	public void draw(GL10 gl, SceneState sceneState) {
		gl.glColor4f(1f,1f,1f,getAlpha());
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.getTextureWrapperArray()[0].getTextureId());
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,GL10.GL_REPEAT);
		
		gl.glPushMatrix();
		float[] m = CtkMath.createMatrix4f();
		float[] m2 = CtkMath.createMatrix4f();
		float[] scale = CtkMath.createVector3f();
		getScale(scale);
		calcLookAtM(m);
		CtkMath.inverseMatrix4f(m2, m);
		gl.glMultMatrixf(m2, 0);
		gl.glScalef(scale[0],scale[1],scale[2]);
		this.shapeSquare.draw(gl);
		gl.glPopMatrix();
	}
	
	public TextureWrapper getMapTextureWrapper() {
		return this.getTextureWrapperArray()[0];
	}
}
