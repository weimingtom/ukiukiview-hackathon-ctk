package net.cattaka.hk.uki2win.cloud;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo.CategoryInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo.RangeInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiGetContentsTask.OnGetSceneObjectInfoListener;
import net.cattaka.hk.uki2win.json.BlockedCharSequence;
import net.cattaka.hk.uki2win.json.JSONArray;
import net.cattaka.hk.uki2win.json.JSONException;
import net.cattaka.hk.uki2win.json.JSONObject;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;

import android.net.Uri;
import android.util.Log;

import com.google.android.maps.GeoPoint;


public class UkiukiGetServiceDataTask extends CloudAsyncTask<GeoPoint, Object, List<SceneObjectInfo>> {
	private OnGetSceneObjectInfoListener listener;
	private UkiukiCloudState ukiukiCloudState;
	private float range;
	private String sid;
	private int maxSceneObjectNum;
	private ServiceSearchCondition serviceSearchCondition;
	
	public UkiukiGetServiceDataTask(OnGetSceneObjectInfoListener listener, WebCacheUtil webCacheUtil, UkiukiCloudState ukiukiCloudState, String sid, float range, int maxSceneObjectNum, ServiceSearchCondition serviceSearchCondition) {
		super(webCacheUtil);
		this.listener = listener;
		this.ukiukiCloudState = ukiukiCloudState;
		this.sid = sid;
		// 今は使ってない
		this.range = range;
		this.maxSceneObjectNum = maxSceneObjectNum;
		this.serviceSearchCondition = serviceSearchCondition;
	}
	
	@Override
	protected List<SceneObjectInfo> doInBackground(GeoPoint... params) {
		// サービスデータ取得処理
		GeoPoint geoPoint = params[0];
		DecimalFormat df = new DecimalFormat("0.000000");
		String lat = df.format((double)geoPoint.getLatitudeE6() / 1E6d);
		String lon = df.format((double)geoPoint.getLongitudeE6() / 1E6d);
		
		// サービスジャンルの取得
		UkiukiServiceGenreInfo ukiukiServiceGenreInfo = ukiukiCloudState.getUkiukiServiceGenreInfo(sid);
		if (ukiukiServiceGenreInfo == null) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
			paramMap.put("sid",sid);
			paramMap.put("range_mode","radius");
			paramMap.put("icon","1");
			paramMap.put("model",ukiukiCloudState.getModel());
			
			if (isCancelled()) {
				return null;
			}
			
			BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_GET_SERVICE_GENRE, paramMap);
			if (isCancelled() || sb == null) {
				return null;
			}
			
			// 取得結果を変換
			ukiukiServiceGenreInfo = new UkiukiServiceGenreInfo();
			try {
				JSONObject jsonObject = new JSONObject(sb);
				JSONObject results = jsonObject.getJSONObject("results");
				// Code2Nameを取得する
				HashMap<String, String> code2NameMap = new HashMap<String, String>();
				try {
					JSONObject genreObject = results.getJSONObject("genre");
					JSONArray nameArray = genreObject.getJSONArray("name");
					JSONArray codeArray = genreObject.getJSONArray("param");
					
					for (int i=0;i<nameArray.length() && i<codeArray.length();i++) {
						String name = nameArray.getString(i);
						String code = codeArray.getString(i);
						code2NameMap.put(code, name);
					}
				} catch (JSONException e) {
					// 無くてもOK
				}
				
				// アイコン情報の取得
				{
					JSONObject iconObject = results.getJSONObject("icon");
					JSONArray iconArray = iconObject.getJSONArray("icon");
					JSONArray codeArray = iconObject.getJSONArray("code");
					String baseurl = iconObject.getString("baseurl");
					
					for (int i=0;i<iconArray.length();i++) {
						CategoryInfo categoryInfo = new CategoryInfo();
						categoryInfo.code = codeArray.getString(i);
						categoryInfo.iconUri = Uri.parse(baseurl + '/' + iconArray.getString(i));
						categoryInfo.name = code2NameMap.get(categoryInfo.code);
						
						ukiukiServiceGenreInfo.addCategoryInfo(categoryInfo);
						if (getWebCacheUtil() != null) {
							getWebCacheUtil().getImageCache(categoryInfo.iconUri);
						}
						if (isCancelled()) {
							break;
						}
					}
				}
				
				// レンジ情報の取得
				{
					JSONObject rangeObject = results.getJSONObject("range");
					JSONArray paramArray = rangeObject.getJSONArray("param");
					JSONArray distanceArray = rangeObject.getJSONArray("name");
					
					for (int i=0;i<paramArray.length() && i<distanceArray.length();i++) {
						RangeInfo rangeInfo = new RangeInfo();
						rangeInfo.param = paramArray.getString(i);
						rangeInfo.distance = (float) distanceArray.getDouble(i);
						ukiukiServiceGenreInfo.getRangeInfoList().add(rangeInfo);
						if (isCancelled()) {
							break;
						}
					}
					ukiukiServiceGenreInfo.sortRangeInfoList();
				}
			} catch (JSONException e) {
				ukiukiServiceGenreInfo = null;
				Log.d(UkiukiWindowConstants.TAG, e.getMessage(), e);
			}
			if (isCancelled() || ukiukiServiceGenreInfo == null) {
				return null;
			}
			if (sid != null) {
				ukiukiServiceGenreInfo.setSid(sid);
				ukiukiCloudState.addUkiukiServiceGenreInfo(ukiukiServiceGenreInfo);
			}
		}
		
		// サービスデータの取得
		int offset = 1;
		final int count = 50;
		
		do {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
			paramMap.put("sid",sid);
			paramMap.put("lat",lat);
			paramMap.put("lng",lon);
			paramMap.put("range",ukiukiServiceGenreInfo.getAdequateRangeParam(range));
			paramMap.put("offset",String.valueOf(offset));
			paramMap.put("count",String.valueOf(count));
			paramMap.put("model",ukiukiCloudState.getModel());
			
			// 検索条件があればキーワードとジャンルを追加する
			if (serviceSearchCondition != null) {
				if (serviceSearchCondition.getKeyword() != null && serviceSearchCondition.getKeyword().length() > 0) {
					paramMap.put("keyword", serviceSearchCondition.getKeyword());
				}
				StringBuilder sb = new StringBuilder(); 
				for (String code : serviceSearchCondition.getGenreCodeList()) {
					if (sb.length() > 0) {
						sb.append(',');
					}
					sb.append(code);
				}
				if (sb.length() > 0) {
					paramMap.put("genre", sb.toString());
				}
			}
			
			BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_GET_SERVICE_DATA, paramMap);
			if (isCancelled() || sb == null) {
				return null;
			}
				
			// 取得結果を変換
			List<SceneObjectInfo> ubInfoList = new ArrayList<SceneObjectInfo>();
			try {
				JSONObject jsonObject = new JSONObject(sb);
				JSONObject results = jsonObject.getJSONObject("results");
				JSONArray dataArray = results.getJSONArray("data");
				
				for (int i=0;i<dataArray.length();i++) {
					JSONObject data = dataArray.getJSONObject(i);
					SceneObjectInfo soInfo = UkiukiCloudClient.parseSceneObjectForService(data, ukiukiServiceGenreInfo);
					
					ubInfoList.add(soInfo);
					if (isCancelled()) {
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
			if (ubInfoList.size() == count && offset <= maxSceneObjectNum) {
				// 次があるのでprogressとして途中結果を送る。残りは続けて取得する。
				publishProgress(ubInfoList);
			} else {
				// 成功
				return ubInfoList;
			}
		} while (!isCancelled());
		
		// キャンセルされるとここにくる
		return null;
	}
	
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
