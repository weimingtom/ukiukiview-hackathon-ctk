package net.cattaka.hk.uki2win.scene;

public interface SceneCameraHandler {
	public void getPosition(float[] dst);
	public void setPosition(float[] src);
	public void setPosition(float x, float y, float z);
	public void getFrontDirection(float[] dst);
	public void setFrontDirection(float[] src);
	public void setFrontDirection(float x, float y, float z);
	public void getUpDirection(float[] dst);
	public void setUpDirection(float[] src);
	public void setUpDirection(float x, float y, float z);
	public float getZoom();
	public void setZoom(float zoom);
	public float getIconSizeRate();
	public void setIconSizeRate(float iconSizeRate);
}
