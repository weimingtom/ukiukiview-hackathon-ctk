package net.cattaka.hk.uki2win.scene;

import java.util.HashMap;
import java.util.Map;
import android.net.Uri;
import net.cattaka.hk.uki2win.gl.TextureWrapper;
import net.cattaka.hk.uki2win.net.ImageCache;
import net.cattaka.hk.uki2win.net.WebCacheUtil;

public class SceneState {
	private Map<Uri, TextureWrapper> textureWrapperMap;
	private SceneCamera sceneCamera;
	private TextureWrapper loadingIconTextureWrapper;
	private TextureWrapper errorIconTextureWrapper;
	
	public SceneState(WebCacheUtil webCacheUtil) {
		this.textureWrapperMap = new HashMap<Uri, TextureWrapper>();
	}

	public Map<Uri, TextureWrapper> getTextureWrapperMap() {
		return textureWrapperMap;
	}

	public SceneCamera getSceneCamera() {
		return sceneCamera;
	}

	public void setSceneCamera(SceneCamera sceneCamera) {
		this.sceneCamera = sceneCamera;
	}

	public TextureWrapper getLoadingIconTextureWrapper() {
		return loadingIconTextureWrapper;
	}

	public void setLoadingIconTextureWrapper(
			TextureWrapper loadingIconTextureWrapper) {
		this.loadingIconTextureWrapper = loadingIconTextureWrapper;
	}

	public TextureWrapper getErrorIconTextureWrapper() {
		return errorIconTextureWrapper;
	}

	public void setErrorIconTextureWrapper(TextureWrapper errorIconTextureWrapper) {
		this.errorIconTextureWrapper = errorIconTextureWrapper;
	}
	
	/**
	 * テクスチャの状態を見て必要なら差し替えた物を返す。
	 * @param tw
	 * @return
	 */
	public TextureWrapper filterTextureWrapper(TextureWrapper tw) {
		if (tw.getImageCache().getStatus() == ImageCache.STATUS_READY) {
			return tw;
		} else if (tw.getImageCache().getStatus() == ImageCache.STATUS_ERROR) {
			// エラーアイコンを返す
			return getErrorIconTextureWrapper();
		} else {
			// ローディングアイコンを返す
			return getLoadingIconTextureWrapper();
		}
	}
}
