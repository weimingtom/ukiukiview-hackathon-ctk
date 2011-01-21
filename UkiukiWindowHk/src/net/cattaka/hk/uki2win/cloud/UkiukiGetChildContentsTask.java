package net.cattaka.hk.uki2win.cloud;

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

public class UkiukiGetChildContentsTask extends CloudAsyncTask<Void, Object, List<SceneObjectInfo>> {
	private OnGetChildContentsListener listener;
	private UkiukiCloudState ukiukiCloudState;
	private String objectId;
	private int maxSceneObjectNum;
	
	public static interface OnGetChildContentsListener {
		public void onGetSceneObjectInfo(List<SceneObjectInfo> soInfoList, boolean finished);
		public void onCancel();
	}

	public UkiukiGetChildContentsTask(OnGetChildContentsListener listener, WebCacheUtil webCacheUtil, UkiukiCloudState ukiukiCloudState, String objectId, int maxSceneObjectNum) {
		super(webCacheUtil);
		this.listener = listener;
		this.ukiukiCloudState = ukiukiCloudState;
		this.objectId = objectId;
		this.maxSceneObjectNum = maxSceneObjectNum;
	}
	
	@Override
	protected List<SceneObjectInfo> doInBackground(Void... params) {
		// コンテンツ取得処理
		int offset = 0;
		final int count = 50;
		
		do {
			// HTTP通信をして取得する
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
			paramMap.put("model",ukiukiCloudState.getModel());
			paramMap.put("objectid",objectId);
			paramMap.put("term","full");
			paramMap.put("offset",String.valueOf(offset));
			paramMap.put("count",String.valueOf(count));
			
			BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_GET_CHILD_CONTENTS, paramMap);
			if (isCancelled() || sb == null) {
				return null;
			}
	
			// 取得結果を変換
			List<SceneObjectInfo> ubInfoList = new ArrayList<SceneObjectInfo>();
			try {
				JSONObject jsonObject = new JSONObject(sb);
				JSONObject results = jsonObject.getJSONObject("message");
				JSONArray messageArray = results.getJSONArray("cluster");
				
				for (int i=0;i<messageArray.length();i++) {
					JSONObject message = messageArray.getJSONObject(i);
					SceneObjectInfo soInfo = UkiukiCloudClient.parseSceneObjectFromUkiukiServerComment(message, ukiukiCloudState.getUkiukiContentsUsageInfo());
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
