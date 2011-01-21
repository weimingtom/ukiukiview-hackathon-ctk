package net.cattaka.hk.uki2win.cloud;

import java.util.HashMap;
import java.util.Map;

import net.cattaka.hk.uki2win.json.BlockedCharSequence;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.utils.StringUtil;

public class UkiukiLoginTask extends CloudAsyncTask<Void, Object, String> {
	private OnLoginListener listener;
	private UkiukiCloudState ukiukiCloudState;
	private String account;
	private String password;

	public static interface OnLoginListener {
		public void onLogin(String sessionCode);
		public void onFailed();
		public void onCancel();
	}

	public UkiukiLoginTask(OnLoginListener listener, WebCacheUtil webCacheUtil, UkiukiCloudState ukiukiCloudState, String account, String password) {
		super(webCacheUtil);
		this.listener = listener;
		this.ukiukiCloudState = ukiukiCloudState;
		this.account = account;
		this.password = password;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		String challenge = null;
		{
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
			paramMap.put("account",account);
			paramMap.put("model",ukiukiCloudState.getModel());
			
			BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_ACCOUNT_REQUEST, paramMap);
			if (isCancelled() || sb == null) {
				return null;
			}
			challenge = sb.toString();
		}
		
		{
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
			paramMap.put("account",account);
			paramMap.put("password",StringUtil.getMd5String(account + StringUtil.getMd5String(password) + challenge));
			paramMap.put("model",ukiukiCloudState.getModel());
			
			BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_LOGIN, paramMap);
			if (isCancelled() || sb == null) {
				return null;
			}
			
			ukiukiCloudState.setSessionCodeUkiukiView(sb.toString());
			ukiukiCloudState.setAccountUkiukiView(account);
		}

		//Log.d(UkiukiWindowConstants.TAG,"sessionCode:" + sessionCode);
		return ukiukiCloudState.getSessionCodeUkiukiView();
	}
	
	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
			this.listener.onLogin(result);
		} else {
			this.listener.onFailed();
		}
	}
	
	@Override
	protected void onCancelled() {
		this.listener.onCancel();
	}
}
