package net.cattaka.hk.uki2win.setting;

import com.google.android.maps.GeoPoint;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.cloud.ServiceSearchCondition;
import net.cattaka.hk.uki2win.cloud.UkiukiServiceInfo;
import net.cattaka.hk.uki2win.math.CtkMath;

public class UserInterfaceState {
	private float iconSizeRate = UkiukiWindowConstants.DEFAULT_ICON_SIZE_RATE;
	private float cameraZoomLevel = UkiukiWindowConstants.DEFAULT_CAMERA_ZOOM_LEVEL;
	private int mapZoomLevel = UkiukiWindowConstants.DEFAULT_MAP_ZOOM_LEVEL;
	private float locationUpdateDistance = 0;
	private GeoPoint lastGeoPoint;
	private GeoPoint currentGeoPoint;
	private GeoPoint lastGetServiceDataGeoPoint;
	private GeoPoint lastGetContentsGeoPoint;
	private boolean ukiukiBallVisibility = true;
	private UkiukiServiceInfo ukiukiServiceInfo;
	private ServiceSearchCondition serviceSearchCondition;
	private float[] cameraUpDirection = CtkMath.createVector3f();
	private float[] cameraFrontDirection = CtkMath.createVector3f();
	private float[] cameraPosition = CtkMath.createVector3f();

	// アニメーション用
	private float animCameraZoomLevel = UkiukiWindowConstants.DEFAULT_CAMERA_ZOOM_LEVEL;

	public void stepAnimation() {
		if (animCameraZoomLevel == cameraZoomLevel) {
			// OK
		} else if (animCameraZoomLevel - cameraZoomLevel > UkiukiWindowConstants.ANIMATE_CAMERA_ZOOM_LEVEL_STEP) {
			cameraZoomLevel += UkiukiWindowConstants.ANIMATE_CAMERA_ZOOM_LEVEL_STEP;
		} else if (animCameraZoomLevel - cameraZoomLevel < -UkiukiWindowConstants.ANIMATE_CAMERA_ZOOM_LEVEL_STEP) {
			cameraZoomLevel -= UkiukiWindowConstants.ANIMATE_CAMERA_ZOOM_LEVEL_STEP;
		} else {
			// アニメーション終了
			cameraZoomLevel = animCameraZoomLevel;
		}
	}
	
	// アニメーション用
	public float getAnimCameraZoomLevel() {
		return animCameraZoomLevel;
	}

	public void setAnimCameraZoomLevel(float animCameraZoomLevel) {
		this.animCameraZoomLevel = animCameraZoomLevel;
	}
	
	// 以下、GetterとSetter
	public float getIconSizeRate() {
		return iconSizeRate;
	}
	public void setIconSizeRate(float iconSizeRate) {
		this.iconSizeRate = iconSizeRate;
	}
	public float getCameraZoomLevel() {
		return cameraZoomLevel;
	}
	public void setCameraZoomLevel(float cameraZoomLevel) {
		this.cameraZoomLevel = cameraZoomLevel;
		this.animCameraZoomLevel = cameraZoomLevel;
	}
	public int getMapZoomLevel() {
		return mapZoomLevel;
	}
	public void setMapZoomLevel(int mapZoomLevel) {
		this.mapZoomLevel = mapZoomLevel;
	}
	public float getLocationUpdateDistance() {
		return locationUpdateDistance;
	}
	public void setLocationUpdateDistance(float locationUpdateDistance) {
		this.locationUpdateDistance = locationUpdateDistance;
	}
	public GeoPoint getLastGeoPoint() {
		return lastGeoPoint;
	}
	public void setLastGeoPoint(GeoPoint lastGeoPoint) {
		this.lastGeoPoint = lastGeoPoint;
	}
	public GeoPoint getCurrentGeoPoint() {
		return currentGeoPoint;
	}
	public void setCurrentGeoPoint(GeoPoint currentGeoPoint) {
		this.currentGeoPoint = currentGeoPoint;
	}
	public GeoPoint getLastGetServiceDataGeoPoint() {
		return lastGetServiceDataGeoPoint;
	}
	public void setLastGetServiceDataGeoPoint(GeoPoint lastGetServiceDataGeoPoint) {
		this.lastGetServiceDataGeoPoint = lastGetServiceDataGeoPoint;
	}
	public GeoPoint getLastGetContentsGeoPoint() {
		return lastGetContentsGeoPoint;
	}
	public void setLastGetContentsGeoPoint(GeoPoint lastGetContentsGeoPoint) {
		this.lastGetContentsGeoPoint = lastGetContentsGeoPoint;
	}
	public boolean isUkiukiBallVisibility() {
		return ukiukiBallVisibility;
	}
	public void setUkiukiBallVisibility(boolean ukiukiBallVisibility) {
		this.ukiukiBallVisibility = ukiukiBallVisibility;
	}
	public UkiukiServiceInfo getUkiukiServiceInfo() {
		return ukiukiServiceInfo;
	}
	public void setUkiukiServiceInfo(UkiukiServiceInfo ukiukiServiceInfo) {
		this.ukiukiServiceInfo = ukiukiServiceInfo;
	}
	public ServiceSearchCondition getServiceSearchCondition() {
		return serviceSearchCondition;
	}
	public void setServiceSearchCondition(
			ServiceSearchCondition serviceSearchCondition) {
		this.serviceSearchCondition = serviceSearchCondition;
	}
	public void getCameraUpDirection(float[] dest) {
		CtkMath.copy3F(dest, cameraUpDirection);
	}
	public void setCameraUpDirection(float[] src) {
		CtkMath.copy3F(cameraUpDirection, src);
	}
	public void getCameraFrontDirection(float[] dest) {
		CtkMath.copy3F(dest, cameraFrontDirection);
	}
	public void setCameraFrontDirection(float[] src) {
		CtkMath.copy3F(cameraFrontDirection, src);
	}
	public void getCameraPosition(float[] dest) {
		CtkMath.copy3F(dest, cameraPosition);
	}
	public void setCameraPosition(float[] src) {
		CtkMath.copy3F(cameraPosition, src);
	}
}
