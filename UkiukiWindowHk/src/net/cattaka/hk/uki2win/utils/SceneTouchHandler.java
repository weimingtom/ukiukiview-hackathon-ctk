package net.cattaka.hk.uki2win.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.android.maps.GeoPoint;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.math.CtkMath;
import net.cattaka.hk.uki2win.math.Hubeny;
import net.cattaka.hk.uki2win.scene.SceneHandler;
import net.cattaka.hk.uki2win.scene.SceneObject;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;
import android.graphics.RectF;
import android.view.MotionEvent;

public class SceneTouchHandler {
	public enum SingleTouchMode {
		PICK_SCENE_OBJECT,
		PICK_MAP_POINT
	}
	
	public static interface OnPickSceneObjectListener {
		public void onPickSceneObject(List<SceneObject> soList);
		public void onPickMapPoint(GeoPoint geoPoint);
		public void onDragMapPoint(float[] diff);
	}
	private SceneHandler sceneHandler;
	private OnPickSceneObjectListener onPickSceneObjectListener;
	
	private boolean touchedFlag = false;
	private float[] lastPoint = CtkMath.createVector3f();
	private SingleTouchMode singleTouchMode = SingleTouchMode.PICK_SCENE_OBJECT;
	
	public SceneTouchHandler(SceneHandler sceneHandler, float iconSizeRate) {
		this.sceneHandler = sceneHandler;
	}
	
	public boolean onTouchEvent(MotionEvent event, RectF rect) {
		//Log.d(UkiukiWindowConstants.TAG, ""+top+"."+left+event.toString());
		float eventX = event.getX() - rect.left;
		float eventY = event.getY() - rect.top;
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			lastPoint[0] = eventX;
			lastPoint[1] = eventY;
			lastPoint[2] = 0;
			touchedFlag = true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// シングルタップの場合
			if (event.getEventTime() - event.getDownTime() <= UkiukiWindowConstants.SINGLE_TOUCH_INTERVAL) {
				if (singleTouchMode == SingleTouchMode.PICK_SCENE_OBJECT) {
					// オブジェクトをピックするモード
					List<SceneObject> soList = sceneHandler.pickSceneObject(eventX, eventY);
					
					List<SceneObjectInfo> soInfoList = new ArrayList<SceneObjectInfo>();
					Set<String> objectIdSet = new HashSet<String>();
					for (SceneObject so:soList) {
						if (so.getObjectId() != null) {
							objectIdSet.add(so.getObjectId());
							
							if (so.getSceneObjectInfo() != null) {
								soInfoList.add(so.getSceneObjectInfo());
							}
						}
					}
					
					if (this.onPickSceneObjectListener != null) {
						this.onPickSceneObjectListener.onPickSceneObject(soList);
					}
				} else if (singleTouchMode == SingleTouchMode.PICK_MAP_POINT) {
					// マップの座標をピックするモード
					float[] pickPos = CtkMath.createVector3f();
					GeoPoint mapCenterGeoPoint = sceneHandler.getMapCenterGeoPoint();
					sceneHandler.pickMapPosition(pickPos, eventX, eventY);
					GeoPoint targetGeoPoint = Hubeny.convertToGeoPoint(mapCenterGeoPoint, pickPos);
					
					if (this.onPickSceneObjectListener != null) {
						this.onPickSceneObjectListener.onPickMapPoint(targetGeoPoint);
					}
				}
			}
			
			touchedFlag = false;
		} else if (touchedFlag && event.getAction() == MotionEvent.ACTION_MOVE) {
			float[] startPos = CtkMath.createVector3f();
			float[] endPos = CtkMath.createVector3f();
			
			float sx = eventX;
			float sy = eventY;
			float ex = lastPoint[0];
			float ey = lastPoint[1];
			sceneHandler.pickMapPosition(startPos, sx, sy);
			sceneHandler.pickMapPosition(endPos, ex, ey);
			//Log.d(UkiukiWindowConstants.TAG, String.format("(%1.1f,%1.1f)-(%1.1f,%1.1f)", event.getHistoricalX(s), event.getHistoricalY(s), event.getX(), event.getY()));
			
			float[] diff = CtkMath.createVector3f();
			CtkMath.sub3F(diff, endPos, startPos);
			float len = CtkMath.length3F(diff);
			float mapSize = sceneHandler.calcMapSizeLength();
			if (Float.isInfinite(len) || Float.isNaN(len) || len > mapSize * UkiukiWindowConstants.MOVE_LIMIT_DISTANCE_RATE) {
				// 移動距離が不自然に大きいので何もしない
			} else {
				if (this.onPickSceneObjectListener != null) {
					this.onPickSceneObjectListener.onDragMapPoint(diff);
				}
			}

			lastPoint[0] = eventX;
			lastPoint[1] = eventY;
			lastPoint[2] = 0;
		}
		return false;
	}

	public OnPickSceneObjectListener getOnPickSceneObjectListener() {
		return onPickSceneObjectListener;
	}

	public void setOnPickSceneObjectListener(
			OnPickSceneObjectListener onPickSceneObjectListener) {
		this.onPickSceneObjectListener = onPickSceneObjectListener;
	}

	public SingleTouchMode getSingleTouchMode() {
		return singleTouchMode;
	}

	public void setSingleTouchMode(SingleTouchMode singleTouchMode) {
		this.singleTouchMode = singleTouchMode;
	}
}
