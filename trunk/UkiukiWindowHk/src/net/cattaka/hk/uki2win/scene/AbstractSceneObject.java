package net.cattaka.hk.uki2win.scene;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;

import net.cattaka.hk.uki2win.gl.TextureWrapper;
import net.cattaka.hk.uki2win.math.CtkMath;
import net.cattaka.hk.uki2win.net.WebCacheUtil;

public class AbstractSceneObject {
	private String objectId;
	private String name;
	private float[] position;
	private float[] frontDirection;
	private float[] upDirection;
	private float[] scale;
	private float alpha;
	private float distanceFromCamera;
	private TextureWrapper[] textureWrapperArray;
	
	public AbstractSceneObject() {
		this.position = CtkMath.createVector3f();
		this.frontDirection = CtkMath.createVector3f(0,-1,0);
		this.upDirection = CtkMath.createVector3f(0,0,1);
		this.scale = CtkMath.createVector3f(1,1,1);
		this.alpha = 1.0f;
		this.textureWrapperArray = new TextureWrapper[0];
	}
	
	public void initialize(Resources resources, SceneState sceneState, WebCacheUtil webCacheUtil) {
	}
	
	public void draw(GL10 gl, SceneState sceneState) {
		
	}
	
	public void calcLookAtM(float[] m) {
		if (!CtkMath.isEqualDir(frontDirection, upDirection)) {
			CtkMath.lookAtDirectionM(m, position, frontDirection, upDirection); 
		} else {
			CtkMath.makeIdentityMatrix4f(m);
		}
	}
	
	public void getPosition(float[] dst) {
		CtkMath.copy3F(dst, position);
	}

	public void setPosition(float[] src) {
		CtkMath.copy3F(position, src);
	}
	
	public void setPosition(float x, float y, float z) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}

	public void getFrontDirection(float[] dst) {
		CtkMath.copy3F(dst, frontDirection);
	}

	public void setFrontDirection(float[] src) {
		CtkMath.copy3F(frontDirection, src);
	}

	public void setFrontDirection(float x, float y, float z) {
		frontDirection[0] = x;
		frontDirection[1] = y;
		frontDirection[2] = z;
	}


	public void getUpDirection(float[] dst) {
		CtkMath.copy3F(dst, upDirection);
	}

	public void setUpDirection(float[] src) {
		CtkMath.copy3F(upDirection, src);
	}

	public void setUpDirection(float x, float y, float z) {
		upDirection[0] = x;
		upDirection[1] = y;
		upDirection[2] = z;
	}

	public TextureWrapper[] getTextureWrapperArray() {
		return textureWrapperArray;
	}

	public void setTextureWrapperArray(TextureWrapper[] textureWrapperArray) {
		this.textureWrapperArray = textureWrapperArray;
	}

	public void getScale(float[] dst) {
		CtkMath.copy3F(dst, scale);
	}

	public void setScale(float[] src) {
		CtkMath.copy3F(scale, src);
	}

	public void setScale(float x, float y, float z) {
		scale[0] = x;
		scale[1] = y;
		scale[2] = z;
	}

	public void setScale(float scaleXYZ) {
		scale[0] = scaleXYZ;
		scale[1] = scaleXYZ;
		scale[2] = scaleXYZ;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getDistanceFromCamera() {
		return distanceFromCamera;
	}

	public void setDistanceFromCamera(float distanceFromCamera) {
		this.distanceFromCamera = distanceFromCamera;
	}
}
