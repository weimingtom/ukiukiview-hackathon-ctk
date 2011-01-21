package net.cattaka.hk.uki2win.scene;

import javax.microedition.khronos.opengles.GL10;

import net.cattaka.hk.uki2win.gl.TextureWrapper;
import net.cattaka.hk.uki2win.gl.shape.ShapeSquare;
import net.cattaka.hk.uki2win.math.CtkMath;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import android.content.res.Resources;
import android.graphics.RectF;

public class SceneObject extends AbstractSceneObject {
	private ShapeSquare shapeSquare;
	private SceneObjectInfo sceneObjectInfo;
	
	public SceneObject() {
		RectF rect = new RectF(-0.5f, 0.5f, 0.5f, -0.5f);
		this.shapeSquare = new ShapeSquare(rect);
	}
	
	@Override
	public void initialize(Resources resources, SceneState sceneState, WebCacheUtil webCacheUtil) {
		TextureWrapper[] twArray = new TextureWrapper[1];
		twArray[0] = webCacheUtil.getTextureWrapper(sceneState, this.getSceneObjectInfo().getIconUri());
		
		this.setTextureWrapperArray(twArray);
	}

	@Override
	public void draw(GL10 gl, SceneState sceneState) {
		float[] dir = CtkMath.createVector3f();
		sceneState.getSceneCamera().getFrontDirection(dir);
		CtkMath.scaleEq3F(dir, -1);
		this.setFrontDirection(dir);
		
		gl.glColor4f(1f,1f,1f,getAlpha());
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glActiveTexture(GL10.GL_TEXTURE0);
		{
			// テクスチャの設定
			TextureWrapper tw = sceneState.filterTextureWrapper(getTextureWrapperArray()[0]);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, tw.getTextureId());
		}
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

	public ShapeSquare getShapeSquare() {
		return shapeSquare;
	}

	public void setShapeSquare(ShapeSquare shapeSquare) {
		this.shapeSquare = shapeSquare;
	}

	public SceneObjectInfo getSceneObjectInfo() {
		return sceneObjectInfo;
	}

	public void setSceneObjectInfo(SceneObjectInfo sceneObjectInfo) {
		this.sceneObjectInfo = sceneObjectInfo;
	}
}
