package net.cattaka.hk.uki2win.scene;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;

public class SceneCamera extends AbstractSceneObject implements SceneCameraHandler {
	private float zoom = 1;
	private float iconSizeRate = UkiukiWindowConstants.DEFAULT_CAMERA_ZOOM_LEVEL;
	
	public SceneCamera() {
	}
	
	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public float getIconSizeRate() {
		return iconSizeRate;
	}

	public void setIconSizeRate(float iconSizeRate) {
		this.iconSizeRate = iconSizeRate;
	}
}
