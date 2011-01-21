package net.cattaka.hk.uki2win.gl;

import java.util.List;

import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.scene.SceneHandler;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

public class SceneRendererHandle {
	private GLSurfaceView glSurfaceView;
	private SceneRenderer sceneRenderer;
	private boolean sleepRenderer;

	public SceneRendererHandle(Context context, GLSurfaceView glSurfaceView, SceneRenderer.SceneRendererProcess sceneRendererProcess, WebCacheUtil webCacheUtil) {
		this.glSurfaceView = glSurfaceView;
		this.sceneRenderer = new SceneRenderer(context, sceneRendererProcess, webCacheUtil);
		this.glSurfaceView.setRenderer(this.sceneRenderer);
	}

	public void onResume() {
		sceneRenderer.onResume();
		this.glSurfaceView.onResume();
	}

	public void onPause() {
		this.glSurfaceView.onPause();
		sceneRenderer.onPause();
	}

	public void updateMap(Bitmap textureBitmap, float[] size, GeoPoint geoPoint, GeoPoint newMapStartGeoPoint, GeoPoint newMapEndGeoPoint) {
		sceneRenderer.updateMap(textureBitmap, size, geoPoint, newMapStartGeoPoint, newMapEndGeoPoint);
	}
	
	public void addSceneObjectInfoList(int idx, List<SceneObjectInfo> soInfoList) {
		sceneRenderer.addSceneObjectInfoList(idx,soInfoList);
	}
	
	public void removeSceneObject(int idx, String objectId) {
		sceneRenderer.removeSceneObject(idx, objectId);
	}

	public void setSceneObjectInfoList(int idx, List<SceneObjectInfo> soInfoList) {
		sceneRenderer.setSceneObjectInfoList(idx,soInfoList);
	}

	public SceneHandler getSceneHandler() {
		return sceneRenderer.getSceneHandler();
	}

	public boolean isSleepRenderer() {
		return this.sleepRenderer;
	}

	public void setSleepRenderer(boolean sleepRenderer) {
		if (!this.sleepRenderer && sleepRenderer) {
			onPause();
		} else if (this.sleepRenderer && !sleepRenderer) {
			onResume();
		}
		this.sleepRenderer = sleepRenderer;
	}
}
