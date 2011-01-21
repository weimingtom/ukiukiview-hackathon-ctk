/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cattaka.hk.uki2win.gl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.google.android.maps.GeoPoint;

import net.cattaka.hk.uki2win.math.CtkMath;
import net.cattaka.hk.uki2win.math.Hubeny;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.scene.AbstractSceneObject;
import net.cattaka.hk.uki2win.scene.Scene;
import net.cattaka.hk.uki2win.scene.SceneCameraHandler;
import net.cattaka.hk.uki2win.scene.SceneHandler;
import net.cattaka.hk.uki2win.scene.SceneObject;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;
import net.cattaka.hk.uki2win.scene.SceneUtil;
import net.cattaka.hk.uki2win.utils.CtkGL;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

public class SceneRenderer implements GLSurfaceView.Renderer {
	public interface SceneRendererProcess {
		public void preDraw(CtkGL ctkGl, SceneHandler sceneHandler);
		public void postDraw(CtkGL ctkGl, SceneHandler sceneHandler);
	}

	private SceneHandler sceneHandler = new SceneHandler() {
		public void setMarkerPosition(GeoPoint markerGeoPoint) {
			float[] pos = CtkMath.createVector3f();
			if (SceneRenderer.this.geoPoint != null) {
				Hubeny.convertToMeter(pos, markerGeoPoint, SceneRenderer.this.geoPoint);
				scene.setMarkerPosition(pos);
			}
		}
		public SceneCameraHandler getSceneCameraHandler() {
			return SceneRenderer.this.scene.getSceneCameraHandler();
		}
		public void pickMapPosition(float[] dst, float x, float y) {
			float[] pos = CtkMath.createVector3f();
			float[] dir = CtkMath.createVector3f();
			ctkGl.calcPickRay(pos, dir, x/width, y/height);
//			Log.d(UkiukiWindowConstants.TAG, 
//				String.format("(%1.2f,%1.2f,%1.2f),(%1.2f,%1.2f,%1.2f)",pos[0],pos[1],pos[2],dir[0],dir[1],dir[2])
//			);
			
			CtkMath.pickPoint(dst, pos, dir, CtkMath.createVector4f(0, 0, 1, 0));
//			Log.d(UkiukiWindowConstants.TAG, 
//					String.format("Picked(%1.2f,%1.2f,%1.2f)",dst[0],dst[1],dst[2])
//				);
		};
		public List<SceneObject> pickSceneObject(float x, float y) {
			float[] pos = CtkMath.createVector3f();
			float[] dir = CtkMath.createVector3f();
			ctkGl.calcPickRay(pos, dir, x/width, y/height);
			
			return scene.pickSceneObject(pos, dir);
		};
		public void setSelectedObjectIdSet(Set<String> objectIdSet) {
			scene.setSelectedObjectIdSet(objectIdSet);
		};
		public void calcMapSize(float[] dst) {
			if (mapStartGeoPoint != null && mapEndGeoPoint != null) {
				Hubeny.convertToMeter(dst, mapStartGeoPoint, mapEndGeoPoint);
			} else {
				dst[0] = 0;
				dst[1] = 0;
				dst[2] = 0;
			}
		}
		public float calcMapSizeLength() {
			float[] mapSize = CtkMath.createVector3f();
			calcMapSize(mapSize);
			return CtkMath.length3F(mapSize);
		}
		public GeoPoint getMapCenterGeoPoint() {
			return geoPoint;
		};
		public void setMaxSceneObjectNum(int maxSceneObjectNum) {
			scene.setMaxSceneObjectNum(maxSceneObjectNum);
		}
	};

	private SceneRendererProcess sceneRendererProcess;
	private Scene scene;
	private GeoPoint geoPoint;
	private GeoPoint mapStartGeoPoint;
	private GeoPoint mapEndGeoPoint;
	private CtkGL ctkGl;
	private Object mutex = new Object();
	private volatile boolean pauseFlag = false;
	
	private float width = 100;
	private float height = 100;

	public SceneRenderer(Context context, SceneRendererProcess sceneRendererProcess, WebCacheUtil webCacheUtil) {
		this.sceneRendererProcess = sceneRendererProcess;
		this.scene = new Scene(webCacheUtil);
		this.scene.initialize(context.getResources());
	}
	
	public void onResume() {
		synchronized (mutex) {
			this.pauseFlag = false;
		}
	}
	
	public void onPause() {
		synchronized (mutex) {
			this.pauseFlag = true;
			scene.onPause(this.ctkGl);
		}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		this.ctkGl = (CtkGL) gl;
		gl.glClearColor(.5f, .5f, .5f, 1);
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glShadeModel(GL10.GL_SMOOTH);
	}

	public void onDrawFrame(GL10 gl) {
		synchronized (mutex) {
			if (this.pauseFlag) {
				// pause中は描画させない
				// scene.draw()が走るとテクスチャのロードも走ってしまう
				return;
			}

			CtkGL ctkGl = (CtkGL)gl;
			gl.glDisable(GL10.GL_DITHER);
			gl.glDisable(GL10.GL_DEPTH_TEST);
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	
			this.sceneRendererProcess.preDraw(ctkGl, this.sceneHandler);
			scene.draw(ctkGl, width, height);
			this.sceneRendererProcess.postDraw(ctkGl, this.sceneHandler);
		}
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		this.width = w;
		this.height = h;
		gl.glViewport(0, 0, w, h);
	}
	
	public void updateMap(Bitmap textureBitmap, float[] size, GeoPoint newGeoPoint, GeoPoint newMapStartGeoPoint, GeoPoint newMapEndGeoPoint) {
		float[] offset = CtkMath.createVector3f();
		if (this.geoPoint != null && newGeoPoint != null) {
			Hubeny.convertToMeter(offset, this.geoPoint, newGeoPoint);
			//Log.d(UkiukiWindowConstants.TAG, size[0] + "," + size[1] + " : " + offset[0] + ","+offset[1]);
		}
		scene.updateMap(textureBitmap, size, offset);
		this.geoPoint = newGeoPoint;
		this.mapStartGeoPoint = newMapStartGeoPoint;
		this.mapEndGeoPoint = newMapEndGeoPoint;
		//Log.d(UkiukiWindowConstants.TAG, "New Geo Point:" + newGeoPoint);
	}
	
	public void addSceneObjectInfoList(int idx, List<SceneObjectInfo> soInfoList) {
		List<AbstractSceneObject> soObjectList = new ArrayList<AbstractSceneObject>();
		for (SceneObjectInfo ubInfo:soInfoList) {
			SceneObject ubObject = SceneUtil.createSceneObject(ubInfo, geoPoint, 0);
			soObjectList.add(ubObject);
			//Log.d(UkiukiWindowConstants.TAG, ubInfo.getObjectId() + ":" + this.geoPoint+":"+ubInfo.getGeoPoint());
		}
		scene.addSceneObjectList(idx, soObjectList);
		//Log.d(UkiukiWindowConstants.TAG, "Current Geo Point:" + this.geoPoint);
	}
	public void removeSceneObject(int idx, String objectId) {
		scene.removeSceneObject(idx, objectId);
	}
	public void setSceneObjectInfoList(int idx, List<SceneObjectInfo> soInfoList) {
		List<AbstractSceneObject> soObjectList = new ArrayList<AbstractSceneObject>();
		for (SceneObjectInfo ubInfo:soInfoList) {
			SceneObject ubObject = SceneUtil.createSceneObject(ubInfo, geoPoint, 0);
			soObjectList.add(ubObject);
		}
		scene.setSceneObjectList(idx, soObjectList);
	}
	public SceneHandler getSceneHandler() {
		return this.sceneHandler;
	}
	
}
