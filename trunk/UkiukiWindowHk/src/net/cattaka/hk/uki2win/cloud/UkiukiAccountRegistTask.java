package net.cattaka.hk.uki2win.cloud;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.util.Log;

public class UkiukiAccountRegistTask extends AsyncTask<Void, Object, Boolean> {
	private OnAccountRegistListener listener;
	private UkiukiCloudState ukiukiCloudState;
	private String account;
	private String password;
	
	public static interface OnAccountRegistListener {
		public void onSucceed();
		public void onFailed();
		public void onCancel();
	}

	public UkiukiAccountRegistTask(OnAccountRegistListener listener, UkiukiCloudState ukiukiCloudState, String account, String password) {
		super();
		this.listener = listener;
		this.ukiukiCloudState = ukiukiCloudState;
		this.account = account;
		this.password = password;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		boolean succeedFlag = false;
		{
			// HTTP通信をして取得する
			HttpClient objHttp = new DefaultHttpClient();  
			HttpParams httpParams = objHttp.getParams();  
			HttpConnectionParams.setConnectionTimeout(httpParams, UkiukiWindowConstants.WEB_CONNECTION_TIMEOUT); //接続のタイムアウト  
			HttpConnectionParams.setSoTimeout(httpParams, UkiukiWindowConstants.WEB_CONNECTION_TIMEOUT);
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
			paramMap.put("account",account);
			paramMap.put("password", password);
			paramMap.put("model",ukiukiCloudState.getModel());
			
			String urlString;
			urlString = UkiukiCloudClient.URL_ACCOUNT_REGIST + '?' + UkiukiCloudClient.createParamString(paramMap);
			//Log.d(UkiukiWindowConstants.TAG,urlString);
			
	       try {
	    	   HttpGet objGet = new HttpGet(urlString);
	    	   HttpResponse objResponse = objHttp.execute(objGet);
	    	   if (objResponse.getStatusLine().getStatusCode() == 200) {
					succeedFlag = true;
	    	   	}
				Reader in = new InputStreamReader(objResponse.getEntity().getContent());
				StringBuffer sb = new StringBuffer();
				int r;
				while ((r = in.read()) != -1) {
					sb.append((char)r);
				}
				in.close();
			} catch (IOException e) {
				Log.d(UkiukiWindowConstants.TAG, e.getMessage(), e);
			}
		}
		
		//Log.d(UkiukiWindowConstants.TAG,"sessionCode:" + sessionCode);
		return succeedFlag;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			this.listener.onSucceed();
		} else {
			this.listener.onFailed();
		}
	}
	
	@Override
	protected void onCancelled() {
		this.listener.onCancel();
	}
}

