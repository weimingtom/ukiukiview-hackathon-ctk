package net.cattaka.hk.uki2win.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.gl.TextureWrapper;
import net.cattaka.hk.uki2win.scene.SceneState;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class WebCacheUtil {
	public static final int TASK_NUM = 3;
	
	public static interface WebCacheListener {
		public void onImageCacheLoaded(ImageCache imageCache);
	}
	
	static class PostRunnnable implements Runnable {
		private WebCacheListener webCacheListener;
		private ImageCache imageCache;
		
		PostRunnnable(WebCacheListener webCacheListener, ImageCache imageCache) {
			this.webCacheListener = webCacheListener;
			this.imageCache = imageCache;
		}

		public void run() {
			if (webCacheListener != null) {
				webCacheListener.onImageCacheLoaded(imageCache);
			}
		}
	};
	
	private Context context = null;
	private HashMap<Uri, ImageCache> imageCachemMap;
	private BlockingQueue<ImageCache> imageCacheQueue;
	private List<LoadImageCacheTask> taskList = new ArrayList<LoadImageCacheTask>();
	
	private Handler handler = new Handler();
	
	class LoadImageCacheTask extends Thread {
		public void run() {
			while(!isInterrupted()) {
				ImageCache imageCache;
				try {
					imageCache = imageCacheQueue.take();
				} catch (InterruptedException e) {
					break;
				}
				imageCache.setStatus(ImageCache.STATUS_LOADING);
				imageCache.getUri();

				HttpClient objHttp = new DefaultHttpClient();  
				HttpParams httpParams = objHttp.getParams();  
				HttpConnectionParams.setConnectionTimeout(httpParams, UkiukiWindowConstants.WEB_CONNECTION_TIMEOUT); //接続のタイムアウト  
				HttpConnectionParams.setSoTimeout(httpParams, UkiukiWindowConstants.WEB_CONNECTION_TIMEOUT);

				HttpGet objGet = new HttpGet(imageCache.getUri().toString());
				try {
					HttpResponse objResponse = objHttp.execute(objGet);
					if (objResponse.getStatusLine().getStatusCode() == 200) {
						InputStream in = null;
						try {
							in = objResponse.getEntity().getContent();
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							{
								// 読み込み
								int r;
								while((r = in.read()) != -1 && !isInterrupted()) {
									bout.write(r);
								}
								if (isInterrupted()) {
									// 割り込みによる終了
									imageCacheQueue.add(imageCache);
									break;
								}
							}
							
							Bitmap bitmap = BitmapFactory.decodeByteArray(bout.toByteArray(), 0, bout.size());
							if (bitmap != null) {
								imageCache.setBitmap(bitmap);
								imageCache.setStatus(ImageCache.STATUS_READY);
							} else {
								// 画像ファイルが壊れているので諦め
								imageCache.setStatus(ImageCache.STATUS_ERROR);
							}
						} catch (IOException e) {
							// 通信エラーの場合はやり直し
							imageCache.setStatus(ImageCache.STATUS_LOADING);
						} finally {
							if (in != null) {
								try {
									in.close();
								} catch(IOException e) {
									// 無視
								}
							}
						}
					} else {
						// HTTPステータスがエラーだったので諦め
						imageCache.setStatus(ImageCache.STATUS_ERROR);
					}
				} catch (IOException e) {
					imageCache.setStatus(ImageCache.STATUS_ERROR);
				}
				
				if (imageCache.getStatus() == ImageCache.STATUS_LOADING) {
					// やり直すのでキューに戻す
					imageCacheQueue.add(imageCache);
				}
				if (imageCache.getStatus() == ImageCache.STATUS_READY) {
					if (imageCache.getWebCacheListener() != null) {
						handler.post(new PostRunnnable(imageCache.getWebCacheListener(), imageCache));
					}
				}
			}
		}
	}
	
	public WebCacheUtil(Context context) {
		this.context = context;
		this.imageCachemMap = new HashMap<Uri, ImageCache>();
		this.imageCacheQueue = new LinkedBlockingQueue<ImageCache>();
		
		// ローディングとエラーはあらかじめ取得しておく
		getImageCache(UkiukiWindowConstants.RESOURCE_LOADING_URI);
		getImageCache(UkiukiWindowConstants.RESOURCE_ERROR_URI);
		getImageCache(UkiukiWindowConstants.RESOURCE_UNKNOWN_URI);
	}
	
	public TextureWrapper getTextureWrapper(SceneState sceneState, Uri iconUri) {
		TextureWrapper tw = sceneState.getTextureWrapperMap().get(iconUri);
		if (tw == null) {
			tw = new TextureWrapper();
			tw.setImageCache(this.getImageCache(iconUri));
			tw.setUpdateFlag(true);
			sceneState.getTextureWrapperMap().put(iconUri, tw);
			return tw;
		}
		return tw;
	}
	
	public Bitmap createBitmap(Uri iconUri) {
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			if (UkiukiWindowConstants.RESOURCE_URI_SCHEMA.equals(iconUri.getScheme())) {
				is = context.getContentResolver().openInputStream(iconUri);
			} else {
				// TODO ちゃんと別スレッド化できるようにすること
				URL url = new URL(iconUri.toString());
				is = url.openStream();
			}
			bitmap = BitmapFactory.decodeStream(is);
		} catch(IOException e) {
			// TODO ログ出しておく？
			Log.d(UkiukiWindowConstants.TAG, e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return bitmap;
	}
	
	public ImageCache getImageCache(Uri uri) {
		return this.getImageCache(uri, null);
	}
	
	public ImageCache getImageCache(Uri uri,WebCacheListener webCacheListener) {
		if (uri == null) {
			uri = UkiukiWindowConstants.RESOURCE_UNKNOWN_URI;
		}
		
		ImageCache ic = imageCachemMap.get(uri);
		if (ic == null) {
			ic = new ImageCache(uri);
			ic.setWebCacheListener(webCacheListener);
			if (UkiukiWindowConstants.RESOURCE_URI_SCHEMA.equals(uri.getScheme())) {
				// ローカルの場合のみ、その場で読み込む
				ic.setBitmap(createBitmap(uri));
				ic.setStatus(ImageCache.STATUS_READY);
			} else {
				ic.setStatus(ImageCache.STATUS_INIT);
				imageCacheQueue.add(ic);
			}
			imageCachemMap.put(uri, ic);
		}
		return ic;
	}

	/**
	 * ImageCacheの状態を見て、必要なら差し替えた物を返す
	 * @param ic
	 * @return
	 */
	public ImageCache filterImageCache(ImageCache ic) {
		if (ic == null) {
			return this.getImageCache(UkiukiWindowConstants.RESOURCE_UNKNOWN_URI);
		} else if (ic.getStatus() == ImageCache.STATUS_READY) {
			return ic;
		} else if (ic.getStatus() == ImageCache.STATUS_ERROR) {
			return this.getImageCache(UkiukiWindowConstants.RESOURCE_ERROR_URI);
		} else {
			return this.getImageCache(UkiukiWindowConstants.RESOURCE_LOADING_URI);
		}
	}
	
	public void startTask() {
		if (taskList.size() > 0) {
			stopTask();
		}
		for (int i=0;i<TASK_NUM;i++) {
			LoadImageCacheTask task = new LoadImageCacheTask();
			task.start();
			taskList.add(task);
		}
	}
	
	public void stopTask() {
		for (LoadImageCacheTask task : taskList) {
			task.interrupt();
		}
		for (LoadImageCacheTask task : taskList) {
			try {
				task.join();
			} catch (InterruptedException e) {
			}
		}
		taskList.clear();
	}
}
