package net.cattaka.hk.uki2win.cloud;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.json.BlockedCharSequence;
import net.cattaka.hk.uki2win.json.JSONException;
import net.cattaka.hk.uki2win.json.JSONObject;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;

import android.util.Log;


public class UkiukiSubmitUkiukiBallTask extends CloudAsyncTask<Void, Object, SceneObjectInfo> {
	private OnSubmitUkiukiBallListener listener;
	private UkiukiCloudState ukiukiCloudState;
	private SceneObjectInfo soInfo;
	
	public static interface OnSubmitUkiukiBallListener {
		public void onSucceed(SceneObjectInfo soInfo);
		public void onFailed();
		public void onCancel();
	}

	public UkiukiSubmitUkiukiBallTask(OnSubmitUkiukiBallListener listener, WebCacheUtil webCacheUtil, UkiukiCloudState ukiukiCloudState, SceneObjectInfo soInfo) {
		super(webCacheUtil);
		this.listener = listener;
		this.ukiukiCloudState = ukiukiCloudState;
		this.soInfo = soInfo;
	}
	
	@Override
	protected SceneObjectInfo doInBackground(Void... params) {
		// 投稿処理
		DecimalFormat df = new DecimalFormat("0.000000");
		String lat = df.format((double)soInfo.getGeoPoint().getLatitudeE6() / 1E6d);
		String lon = df.format((double)soInfo.getGeoPoint().getLongitudeE6() / 1E6d);
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
		paramMap.put("session",ukiukiCloudState.getSessionCodeUkiukiView());
		paramMap.put("lat",lat);
		paramMap.put("lng",lon);
		paramMap.put("title", soInfo.getTitle());
		// TODO
		//paramMap.put("language", "ja");
		paramMap.put("type", soInfo.getMimeType().getType());
		paramMap.put("content", soInfo.getMimeType().getSubType());
		paramMap.put("model",ukiukiCloudState.getModel());
		if (soInfo.getParentId() != null && soInfo.getParentId().length() > 0) {
			// 親IDはある場合のみ付加する
			paramMap.put("parentid",soInfo.getParentId());
		}
		
		BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_SUBMIT_UKIUKI_BALL, paramMap);
		if (sb == null) {
			return null;
		}
		
		// 取得結果を変換
		SceneObjectInfo soInfo = null;
		try {
			JSONObject jsonObject = new JSONObject(sb);
			JSONObject results = jsonObject.getJSONObject("results");
			JSONObject message = results.getJSONObject("message");
			
			soInfo = UkiukiCloudClient.parseSceneObjectFromUkiukiServer(message, ukiukiCloudState.getUkiukiContentsUsageInfo());
		} catch (JSONException e) {
			soInfo = null;
			Log.d(UkiukiWindowConstants.TAG, e.getMessage(), e);
		}

		return soInfo;
	}
	
	@Override
	protected void onPostExecute(SceneObjectInfo result) {
		if (result != null) {
			this.listener.onSucceed(result);
		} else {
			this.listener.onFailed();
		}
	}
	
	@Override
	protected void onCancelled() {
		this.listener.onCancel();
	}
}
