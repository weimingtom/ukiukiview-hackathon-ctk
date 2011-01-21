package net.cattaka.hk.uki2win.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.gl.TextureWrapper;
import net.cattaka.hk.uki2win.math.CtkMath;
import net.cattaka.hk.uki2win.net.ImageCache;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.utils.BitmapUtil;
import net.cattaka.hk.uki2win.utils.CtkGL;

public class Scene {
	private Object mutex = new Object();
	private Resources resources;
	private int maxSceneObjectNum = UkiukiWindowConstants.DEFAULT_SCENE_OBJECT_NUM;
	
	// 描画用のオブジェクト
	private SceneCamera sceneCamera;
	private MapPanel mapPanel;
	private MarkerObject markerObject;
	private List<Map<String,AbstractSceneObject>> sceneObjectMapList;
	private List<AbstractSceneObject> sortedSceneObjectList;
	private Set<String> selectedObjectIdSet;
	
	private WebCacheUtil webCacheUtil;
	private SceneState sceneState;
	
	public Scene(WebCacheUtil webCacheUtil) {
		this.sceneObjectMapList = new ArrayList<Map<String,AbstractSceneObject>>();
		this.sceneObjectMapList.add(new HashMap<String,AbstractSceneObject>());
		this.sceneObjectMapList.add(new HashMap<String,AbstractSceneObject>());
		this.sceneObjectMapList.add(new HashMap<String,AbstractSceneObject>());
		this.sortedSceneObjectList = new ArrayList<AbstractSceneObject>();
		this.selectedObjectIdSet = new HashSet<String>();
		this.webCacheUtil = webCacheUtil;

		this.sceneState = new SceneState(this.webCacheUtil);
		this.sceneState.setLoadingIconTextureWrapper(this.webCacheUtil.getTextureWrapper(sceneState, UkiukiWindowConstants.RESOURCE_LOADING_URI));
		this.sceneState.setErrorIconTextureWrapper(this.webCacheUtil.getTextureWrapper(sceneState, UkiukiWindowConstants.RESOURCE_ERROR_URI));
	}
	
	public void initialize(Resources resources) {
		this.resources = resources;
		this.mapPanel = new MapPanel();
		this.markerObject = new MarkerObject();
		this.sceneCamera = new SceneCamera();
		this.sceneCamera.setPosition(0,0,100);
		
		this.markerObject.setInvisible(true);
		
		mapPanel.initialize(resources, sceneState, webCacheUtil);
		markerObject.initialize(resources, sceneState, webCacheUtil);
		sceneCamera.initialize(resources, sceneState, webCacheUtil);
		
		this.sceneState.setSceneCamera(this.sceneCamera);
	}
	
	public void onPause(CtkGL gl) {
		// 全てのテクスチャを一端リリース
		for (TextureWrapper tw:sceneState.getTextureWrapperMap().values()) {
			tw.setUpdateFlag(true);
			if (tw.getTextureId() != -1) {
				//gl.glDeleteTextures(1, new int[tw.getTextureId()], 0);
				tw.setTextureId(-1);
			}
		}
	}
	
	public void draw(CtkGL gl, float width, float height) {
		synchronized (mutex) {
			// カメラのFOVを設定
			float[] mapScale = CtkMath.createVector3f();
			this.mapPanel.getScale(mapScale);
			float mapSize = CtkMath.length3F(mapScale);
			
			float scale = (float) Math.pow(2, -this.sceneCamera.getZoom());
			float ratio = width / height;
			float far = mapSize;
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glFrustumf(-ratio*scale, ratio*scale, -scale, scale, 2f, far);
			
			// 必要があればテクスチャの更新を行う
			for (TextureWrapper tw:sceneState.getTextureWrapperMap().values()) {
				if (tw.isUpdateFlag()) {
					ImageCache ic = tw.getImageCache();
					if (ic.getStatus() == ImageCache.STATUS_READY) {
						// OpenGL内のTextureIdを取得する
						if (tw.getTextureId() == -1) {
							int[] textures = new int[1];
							gl.glGenTextures(1, textures, 0);
							tw.setTextureId(textures[0]);
						}
						final BitmapUtil bitmapUtil = new BitmapUtil();
						Bitmap bitmap = bitmapUtil.resizeForTexture(ic.getBitmap());
						updateTexture(gl, tw.getTextureId(), bitmap);
						tw.setUpdateFlag(false);
					} else {
						// ローディング中かエラーなので放置
					}
				}
			}

			// 各種クリア
			gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			
			// オブジェクトを並べ替え
			sortSceneObjectByDirection();
			
			// 描画
			if (UkiukiWindowConstants.SWITCH_3D) {
				float[] cameraMatrix = CtkMath.createMatrix4f();
				for (int lr=0;lr<2;lr++) {
					float[] position = CtkMath.createVector3f();
					float[] frontDirection = CtkMath.createVector3f();
					float[] upDirection = CtkMath.createVector3f();
					float[] leftDirection = CtkMath.createVector3f();
					sceneCamera.getPosition(position);
					sceneCamera.getFrontDirection(frontDirection);
					sceneCamera.getUpDirection(upDirection);
					CtkMath.cross3F(leftDirection, upDirection, frontDirection);
					CtkMath.normalizeEq3F(leftDirection);
					// TODO 定数化
					float eyeWidth = mapSize * 0.005f;
					
					if (lr == 0) {
						// 左目用
						gl.glViewport(0, 0, (int)(width/2), (int)(height));
						CtkMath.scaleAddEq3F(position, -eyeWidth, leftDirection);
					} else {
						// 右目用
						gl.glViewport((int)(width/2), 0, (int)(width/2), (int)(height));
						CtkMath.scaleAddEq3F(position, eyeWidth, leftDirection);
					}
					// カメラの視点を設定
					CtkMath.lookAtDirectionM(cameraMatrix, position, frontDirection, upDirection); 
					gl.loadCameraMatrix(cameraMatrix);
					
					// 描画
					drawInner(gl, mapSize);
				}
			} else {
				// カメラの視点を設定
				float[] cameraMatrix = CtkMath.createMatrix4f();
				sceneCamera.calcLookAtM(cameraMatrix);
				gl.loadCameraMatrix(cameraMatrix);
				
				// 描画
				drawInner(gl, mapSize);
			}
		}
	}
	
	private void drawInner(CtkGL gl, float mapSize) {
		// マップの描画
		this.mapPanel.draw(gl, sceneState);
		// すべてのオブジェクトを描画
		float[] upDirection = CtkMath.createVector3f();
		float objectScale = mapSize * sceneCamera.getIconSizeRate();
		this.sceneCamera.getUpDirection(upDirection);
		this.markerObject.setScale(objectScale);
		this.markerObject.setUpDirection(upDirection);
		this.markerObject.draw(gl, sceneState);
		if (selectedObjectIdSet.size() > 0) {
			for (int i=sortedSceneObjectList.size() - 1;i>=0;i--) {
				AbstractSceneObject so = sortedSceneObjectList.get(i);
				if (selectedObjectIdSet.contains(so.getObjectId())) {
					// 選択中のもの
					so.setAlpha(1f);
					so.setScale(objectScale * 1.5f);
				} else {
					// 未選択のもの
					so.setAlpha(0.25f);
					so.setScale(objectScale);
				}
				so.setUpDirection(upDirection);
				so.draw(gl, sceneState);
			}
		} else {
			for (int i=sortedSceneObjectList.size() - 1;i>=0;i--) {
				AbstractSceneObject so = sortedSceneObjectList.get(i);
				so.setAlpha(1.0f);
				so.setScale(objectScale);
				so.setUpDirection(upDirection);
				so.draw(gl, sceneState);
			}
		}
	}
	
	public void updateMap(Bitmap textureBitmap, float[] size, float[] offset) {
		synchronized (mutex) {
			this.mapPanel.getMapTextureWrapper().getImageCache().setBitmap(textureBitmap);
			this.mapPanel.getMapTextureWrapper().setUpdateFlag(true);
			this.mapPanel.setScale(size);
			
			float[] pos = CtkMath.createVector3f();
			for (Map<String,AbstractSceneObject> soMap: sceneObjectMapList) {
				Iterator<AbstractSceneObject> it = soMap.values().iterator();
				while(it.hasNext()) {
					AbstractSceneObject so = it.next();
					so.getPosition(pos);
					//Log.d(UkiukiWindowConstants.TAG, "FROM: " + so.getObjectId() + ":" + pos[0] + "," + pos[1]);
					CtkMath.addEq3F(pos, offset);
					so.setPosition(pos);
				}
			}
		}
	}
	
	private void updateTexture(GL10 gl, int TextureId, Bitmap bitmap) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, TextureId);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_REPLACE);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	}
	
	public void getMarkerPosition(float[] dst) {
		markerObject.getPosition(dst);
	}
	public void setMarkerPosition(float[] src) {
		markerObject.setInvisible(false);
		markerObject.setPosition(src);
	}
	
	public SceneCameraHandler getSceneCameraHandler() {
		return sceneCamera;
	}
	
	public List<SceneObject> pickSceneObject(float[] pos, float[] dir) {
		synchronized (mutex) {
			List<SceneObject> result = new ArrayList<SceneObject>();
			float[] mapScale = CtkMath.createVector3f();
			this.mapPanel.getScale(mapScale);
			float mapSize = CtkMath.length3F(mapScale);
			float objectScale = mapSize * sceneCamera.getIconSizeRate();
			float[] targetPos = CtkMath.createVector3f();
			for (AbstractSceneObject aso: sortedSceneObjectList) {
				if (aso instanceof SceneObject) {
					SceneObject so = (SceneObject) aso;
					so.getPosition(targetPos);
					float dist = CtkMath.calcDistance(pos, dir, targetPos);
					if (dist<objectScale) {
						result.add(so);
					}
				}
			}
			Collections.reverse(result);
			return result;
		}
	}
	
	public void addSceneObjectList(int idx, List<? extends AbstractSceneObject> sceneObjectList) {
		synchronized (mutex) {
			// 新しいSceneObjectを初期化してリストに追加
			Map<String,AbstractSceneObject> soMap = this.sceneObjectMapList.get(idx);
			for (AbstractSceneObject so:sceneObjectList) {
				so.initialize(this.resources, this.sceneState, this.webCacheUtil);
				soMap.put(so.getObjectId(), so);
				
//				float[] pos = CtkMath.createVector3f();
//				so.getPosition(pos);
//				Log.d(UkiukiWindowConstants.TAG, "Add : " + so.getObjectId() + ":" + pos[0] + "," + pos[1]);
			}
			sortSceneObject();
		}
	}
	
	public void removeSceneObject(int idx, String objectId) {
		if (objectId == null) {
			return;
		}
		synchronized (mutex) {
			Map<String,AbstractSceneObject> soMap = this.sceneObjectMapList.get(idx);
			soMap.remove(objectId);
			sortSceneObject();
		}
	}

	public void setSceneObjectList(int idx, List<AbstractSceneObject> sceneObjectList) {
		synchronized (mutex) {
			// 新しいSceneObjectを初期化してリストに追加
			Map<String,AbstractSceneObject> soMap = this.sceneObjectMapList.get(idx);
			soMap.clear();
			for (AbstractSceneObject so:sceneObjectList) {
				so.initialize(this.resources, this.sceneState, this.webCacheUtil);
				soMap.put(so.getObjectId(), so);
			}
			sortSceneObject();
		}
	}
	private void sortSceneObject() {
		float[] mapScale = CtkMath.createVector3f();
		this.mapPanel.getScale(mapScale);
		float mapSize = CtkMath.length3F(mapScale);

		// カメラの近い順に並べ直す
		float[] cameraPos = CtkMath.createVector3f();
		float[] pos = CtkMath.createVector3f();
		this.sceneCamera.getPosition(cameraPos);
		sortedSceneObjectList.clear();
		sortedSceneObjectList.add(this.markerObject);
		for (Map<String,AbstractSceneObject> soMap: sceneObjectMapList) {
			Iterator<AbstractSceneObject> it = soMap.values().iterator();
			while(it.hasNext()) {
				AbstractSceneObject so = it.next();
				so.getPosition(pos);
				if (CtkMath.distance(cameraPos, pos) > mapSize * 2) {
					it.remove();
				} else {
					sortedSceneObjectList.add(so);
				}
			}
		}
		
		SceneUtil.sortSceneObjectListByDistance(sortedSceneObjectList, cameraPos);
		for (int i=sortedSceneObjectList.size()-1;i>=maxSceneObjectNum;i--) {
			sortedSceneObjectList.remove(i);
		}
		//Log.d(UkiukiWindowConstants.TAG,"Objects:" + sortedSceneObjectList.size());
	}
	private void sortSceneObjectByDirection() {
		float[] cameraPos = CtkMath.createVector3f();
		float[] cameraDir = CtkMath.createVector3f();
		this.sceneCamera.getPosition(cameraPos);
		this.sceneCamera.getFrontDirection(cameraDir);
		SceneUtil.sortSceneObjectListByDirection(sortedSceneObjectList, cameraPos, cameraDir);
	}
	public void setSelectedObjectIdSet(Set<String> objectIdSet) {
		synchronized (mutex) {
			this.selectedObjectIdSet.clear();
			this.selectedObjectIdSet.addAll(objectIdSet);
		}
	}

	public int getMaxSceneObjectNum() {
		return maxSceneObjectNum;
	}

	public void setMaxSceneObjectNum(int maxSceneObjectNum) {
		this.maxSceneObjectNum = maxSceneObjectNum;
	}
}
