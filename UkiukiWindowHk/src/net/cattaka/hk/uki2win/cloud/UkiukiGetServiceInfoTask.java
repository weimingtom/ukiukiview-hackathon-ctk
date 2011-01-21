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

import android.util.Log;

public class UkiukiGetServiceInfoTask extends CloudAsyncTask<Void, Object, List<UkiukiServiceInfo>> {
	private OnGetServiceInfoListener listener;
	private UkiukiCloudState ukiukiCloudState;
	
	public static interface OnGetServiceInfoListener {
		public void onGetServiceInfo(List<UkiukiServiceInfo> usInfoList);
		public void onCancel();
	}

	public UkiukiGetServiceInfoTask(OnGetServiceInfoListener listener, WebCacheUtil webCacheUtil, UkiukiCloudState ukiukiCloudState) {
		super(webCacheUtil);
		this.listener = listener;
		this.ukiukiCloudState = ukiukiCloudState;
	}
	
	@Override
	protected List<UkiukiServiceInfo> doInBackground(Void... params) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
		paramMap.put("model",ukiukiCloudState.getModel());
		
		BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_GET_SERVICE_LIST, paramMap);
		if (isCancelled() || sb == null) {
			return null;
		}

		// 取得結果を変換
		List<UkiukiServiceInfo> usInfoList = new ArrayList<UkiukiServiceInfo>();
		try {
			JSONObject jsonObject = new JSONObject(sb);
			JSONObject results = jsonObject.getJSONObject("results");
			JSONArray servicesArray = results.getJSONArray("services");
			
			for (int i=0;i<servicesArray.length();i++) {
				JSONObject service = servicesArray.getJSONObject(i);
				UkiukiServiceInfo serviceInfo = new UkiukiServiceInfo();
				serviceInfo.setSid(service.getString("id"));
				serviceInfo.setServiceName(service.getString("name"));
				serviceInfo.setIconUri(service.getString("icon"));
				serviceInfo.setExplain(service.getString("explain"));
				serviceInfo.setCorporation(service.getString("corporation"));
				serviceInfo.setCatchCopy(service.getString("catch"));
				usInfoList.add(serviceInfo);
			}
		} catch (JSONException e) {
			Log.d(UkiukiWindowConstants.TAG, e.getMessage(), e);
		}
		
		if (isCancelled()) {
			return null;
		}
		
		return usInfoList;
	}
	
	@Override
	protected void onPostExecute(List<UkiukiServiceInfo> result) {
		this.listener.onGetServiceInfo(result);
	}
	@Override
	protected void onCancelled() {
		this.listener.onCancel();
	}
}
