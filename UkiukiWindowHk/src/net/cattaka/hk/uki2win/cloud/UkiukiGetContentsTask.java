package net.cattaka.hk.uki2win.cloud;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.json.BlockedCharSequence;
import net.cattaka.hk.uki2win.json.JSONArray;
import net.cattaka.hk.uki2win.json.JSONException;
import net.cattaka.hk.uki2win.json.JSONObject;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class UkiukiGetContentsTask extends CloudAsyncTask<GeoPoint, Object, List<SceneObjectInfo>> {
	private OnGetSceneObjectInfoListener listener;
	private UkiukiCloudState ukiukiCloudState;
	private float range;
	private int maxSceneObjectNum;
	
	public static interface OnGetSceneObjectInfoListener {
		public void onGetSceneObjectInfo(List<SceneObjectInfo> soInfoList, boolean finished);
		public void onCancel();
	}

	public UkiukiGetContentsTask(OnGetSceneObjectInfoListener listener, WebCacheUtil webCacheUtil, UkiukiCloudState ukiukiCloudState, float range, int maxSceneObjectNum) {
		super(webCacheUtil);
		this.listener = listener;
		this.ukiukiCloudState = ukiukiCloudState;
		this.range = range;
		this.maxSceneObjectNum = maxSceneObjectNum;
	}
	
	@Override
	protected List<SceneObjectInfo> doInBackground(GeoPoint... params) {
		// コンテンツ取得処理
		GeoPoint geoPoint = params[0];
		DecimalFormat df = new DecimalFormat("0.000000");
		String lat = df.format((double)geoPoint.getLatitudeE6() / 1E6d);
		String lon = df.format((double)geoPoint.getLongitudeE6() / 1E6d);
		
		int offset = 0;
		final int count = 50;
		
		do {
			// HTTP通信をして取得する
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
			paramMap.put("model",ukiukiCloudState.getModel());
			paramMap.put("lat",lat);
			paramMap.put("lng",lon);
			paramMap.put("range",ukiukiCloudState.getUkiukiContentsUsageInfo().getAdequateRangeParam(range));
			paramMap.put("sort","1");
			paramMap.put("term","full");
			paramMap.put("offset",String.valueOf(offset));
			paramMap.put("count",String.valueOf(count));
			
			BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_GET_CONTENTS, paramMap);
			if (isCancelled() || sb == null) {
				return null;
			}
	
			// 取得結果を変換
			List<SceneObjectInfo> ubInfoList = new ArrayList<SceneObjectInfo>();
			try {
				JSONObject jsonObject = new JSONObject(sb);
				JSONObject results = jsonObject.getJSONObject("results");
				JSONArray messageArray = results.getJSONArray("clusters");
				
				for (int i=0;i<messageArray.length();i++) {
					JSONObject message = messageArray.getJSONObject(i);
					SceneObjectInfo soInfo = UkiukiCloudClient.parseSceneObjectFromUkiukiServer(message, ukiukiCloudState.getUkiukiContentsUsageInfo());
					if (soInfo == null) {
						continue;
					}
					ubInfoList.add(soInfo);
					
					if (isCancelled() || ubInfoList.size() > maxSceneObjectNum) {
						break;
					}
				}
			} catch (JSONException e) {
				Log.d(UkiukiWindowConstants.TAG, e.getMessage(), e);
			}
			
			if (isCancelled()) {
				break;
			}
			
			offset += ubInfoList.size();
			if (ubInfoList.size() == count && offset < maxSceneObjectNum) {
				// 次があるので続けて取得
				publishProgress(ubInfoList);
			} else {
				return ubInfoList;
			}
		} while (!isCancelled());
		
		// キャンセルされるここにくる
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void onProgressUpdate(Object... values) {
		this.listener.onGetSceneObjectInfo((List<SceneObjectInfo>)(values[0]), false);
	};
	
	@Override
	protected void onPostExecute(List<SceneObjectInfo> result) {
		this.listener.onGetSceneObjectInfo(result, true);
	}
	@Override
	protected void onCancelled() {
		this.listener.onCancel();
	}
}
