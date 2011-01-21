package net.cattaka.hk.uki2win.cloud;

import java.util.HashMap;
import java.util.Map;

import net.cattaka.hk.uki2win.json.BlockedCharSequence;
import net.cattaka.hk.uki2win.net.WebCacheUtil;

public class UkiukiDeleteUkiukiBallTask extends CloudAsyncTask<Void, Object, Boolean> {
	private OnDeleteUkiukiBallListener listener;
	private UkiukiCloudState ukiukiCloudState;
	private String objectId;

	public static interface OnDeleteUkiukiBallListener {
		public void onSucceed(String objectId);
		public void onFailed();
		public void onCancel();
	}

	public UkiukiDeleteUkiukiBallTask(OnDeleteUkiukiBallListener listener, WebCacheUtil webCacheUtil, UkiukiCloudState ukiukiCloudState, String objectId) {
		super(webCacheUtil);
		this.listener = listener;
		this.ukiukiCloudState = ukiukiCloudState;
		this.objectId = objectId;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("apikey",ukiukiCloudState.getApiKeyUkiukiView());
		paramMap.put("session",ukiukiCloudState.getSessionCodeUkiukiView());
		paramMap.put("objid", objectId);
		paramMap.put("model",ukiukiCloudState.getModel());
		
		BlockedCharSequence sb = requestHttp(UkiukiCloudClient.URL_DELETE_UKIUKI_BALL, paramMap);
		if (sb == null) {
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			this.listener.onSucceed(objectId);
		} else {
			this.listener.onFailed();
		}
	}
	
	@Override
	protected void onCancelled() {
		this.listener.onCancel();
	}
}

