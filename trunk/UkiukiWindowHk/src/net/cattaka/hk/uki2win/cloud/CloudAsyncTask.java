package net.cattaka.hk.uki2win.cloud;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo.CategoryInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo.RangeInfo;
import net.cattaka.hk.uki2win.json.BlockedCharSequence;
import net.cattaka.hk.uki2win.json.JSONArray;
import net.cattaka.hk.uki2win.json.JSONException;
import net.cattaka.hk.uki2win.json.JSONObject;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public abstract class CloudAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	private HttpClient httpClient;
	private WebCacheUtil webCacheUtil;
	
	public CloudAsyncTask(WebCacheUtil webCacheUtil) {
		super();
		this.webCacheUtil = webCacheUtil;
	}

	protected HttpClient getHttpClient() {
		if (httpClient == null) {
			this.httpClient = new DefaultHttpClient();  
			HttpParams httpParams = httpClient.getParams();  
			HttpConnectionParams.setConnectionTimeout(httpParams, UkiukiWindowConstants.WEB_CONNECTION_TIMEOUT); //接続のタイムアウト  
			HttpConnectionParams.setSoTimeout(httpParams, UkiukiWindowConstants.WEB_CONNECTION_TIMEOUT);
		}
		return this.httpClient;
	}
	
	protected BlockedCharSequence requestHttp(String baseUrl, Map<String, String> paramMap) {
		String urlString;
		urlString = baseUrl + '?' + UkiukiCloudClient.createParamString(paramMap);
		//Log.d(UkiukiWindowConstants.TAG, urlString);

		try {
			HttpGet objGet = new HttpGet(urlString);
			HttpResponse objResponse = getHttpClient().execute(objGet);
			if (objResponse.getStatusLine().getStatusCode() < 400) {
				BlockedCharSequence sb = new BlockedCharSequence(2<<12);
				Reader in = new InputStreamReader(objResponse.getEntity().getContent());
				char[] buf = new char[2 << 12];
				int r;
				while ((r = in.read(buf, 0, buf.length)) != -1) {
					sb.append(buf, 0, r);
					if (isCancelled()) {
						break;
					}
				}
				in.close();
				return sb;
			} else {
				return null;
			}
		} catch (IOException e) {
			Log.d(UkiukiWindowConstants.TAG, e.getMessage(), e);
		}
		return null;
	}
	
	protected UkiukiContentsUsageInfo requestUkiukiContentsUsageInfo(UkiukiCloudState ukiukiCloudState) {
		// コンテンツサマリが無い場合は取得する
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
		paramMap.put("icon","1");
		paramMap.put("range_mode","radius");
		paramMap.put("model",ukiukiCloudState.getModel());
		
		if (isCancelled()) {
			return null;
		}
		
		BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_GET_CONTENTS_USAGE, paramMap);
		if (isCancelled() || sb == null) {
			return null;
		}
		
		// 取得結果を変換
		UkiukiContentsUsageInfo ukiukiContentsUsageInfo = new UkiukiContentsUsageInfo();
		try {
			JSONObject jsonObject = new JSONObject(sb);
			JSONObject results = jsonObject.getJSONObject("results");
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
					ukiukiContentsUsageInfo.addCategoryInfo(categoryInfo);
					if (webCacheUtil != null) {
						webCacheUtil.getImageCache(categoryInfo.iconUri);
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
					ukiukiContentsUsageInfo.getRangeInfoList().add(rangeInfo);
					if (isCancelled()) {
						break;
					}
				}
				ukiukiContentsUsageInfo.sortRangeInfoList();
			}
		} catch (JSONException e) {
			ukiukiContentsUsageInfo = null;
			Log.d(UkiukiWindowConstants.TAG, e.getMessage(), e);
		}
		if (isCancelled()) {
			return null;
		}
		return ukiukiContentsUsageInfo;
	}

	protected WebCacheUtil getWebCacheUtil() {
		return webCacheUtil;
	}
}
