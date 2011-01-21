package net.cattaka.hk.uki2win.scene;

import java.util.List;
import java.util.Set;

import com.google.android.maps.GeoPoint;

public interface SceneHandler {
	public void setMarkerPosition(GeoPoint geoPoint);
	public SceneCameraHandler getSceneCameraHandler();
	public void pickMapPosition(float[] dst, float x, float y);
	public List<SceneObject> pickSceneObject(float x, float y);
	public void setSelectedObjectIdSet(Set<String> objectIdSet);
	public void calcMapSize(float[] dst);
	public float calcMapSizeLength();
	public GeoPoint getMapCenterGeoPoint();
	public void setMaxSceneObjectNum(int maxSceneObjectNum);
}
