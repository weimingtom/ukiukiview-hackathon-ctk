package net.cattaka.hk.uki2win.net;

import net.cattaka.hk.uki2win.net.WebCacheUtil.WebCacheListener;
import android.graphics.Bitmap;
import android.net.Uri;

public class ImageCache {
	public static final int STATUS_INIT = 0;
	public static final int STATUS_LOADING = 1;
	public static final int STATUS_READY = 2;
	public static final int STATUS_ERROR = 3;
	
	private Uri uri;
	private int status = STATUS_INIT;
	private Bitmap bitmap = null;
	private WebCacheListener webCacheListener;
	
	ImageCache(Uri uri) {
		this.uri = uri;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public WebCacheListener getWebCacheListener() {
		return webCacheListener;
	}

	public void setWebCacheListener(WebCacheListener webCacheListener) {
		this.webCacheListener = webCacheListener;
	}
}
