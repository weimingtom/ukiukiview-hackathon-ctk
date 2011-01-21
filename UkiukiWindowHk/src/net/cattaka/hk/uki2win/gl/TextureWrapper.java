package net.cattaka.hk.uki2win.gl;

import net.cattaka.hk.uki2win.net.ImageCache;

public class TextureWrapper {
	private boolean updateFlag = false;
	private int textureId = -1;
	private ImageCache imageCache;
	
	public boolean isUpdateFlag() {
		return updateFlag;
	}
	public void setUpdateFlag(boolean updateFlag) {
		this.updateFlag = updateFlag;
	}
	public int getTextureId() {
		return textureId;
	}
	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}
	public ImageCache getImageCache() {
		return imageCache;
	}
	public void setImageCache(ImageCache imageCache) {
		this.imageCache = imageCache;
	}
}
