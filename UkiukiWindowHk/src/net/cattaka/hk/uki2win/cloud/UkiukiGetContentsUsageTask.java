package net.cattaka.hk.uki2win.cloud;

import net.cattaka.hk.uki2win.net.WebCacheUtil;

public class UkiukiGetContentsUsageTask extends CloudAsyncTask<Void, Void, UkiukiContentsUsageInfo> {
	private OnGetContentsUsageListener listener;
	private UkiukiCloudState ukiukiCloudState;
	
	public static interface OnGetContentsUsageListener {
		public void onGetContentsUsageInfo(UkiukiContentsUsageInfo ucuInfo);
		public void onCancel();
	}

	public UkiukiGetContentsUsageTask(OnGetContentsUsageListener listener, WebCacheUtil webCacheUtil, UkiukiCloudState ukiukiCloudState) {
		super(webCacheUtil);
		this.listener = listener;
		this.ukiukiCloudState = ukiukiCloudState;
	}
	
	@Override
	protected UkiukiContentsUsageInfo doInBackground(Void... params) {
		// 取得コンテンツ利用パラメータの準備
		UkiukiContentsUsageInfo ukiukiContentsUsageInfo = ukiukiCloudState.getUkiukiContentsUsageInfo();

		ukiukiContentsUsageInfo = requestUkiukiContentsUsageInfo(this.ukiukiCloudState);
		ukiukiCloudState.setUkiukiContentsUsageInfo(ukiukiContentsUsageInfo);
		
		return ukiukiCloudState.getUkiukiContentsUsageInfo();
	}
	
	@Override
	protected void onPostExecute(UkiukiContentsUsageInfo result) {
		this.listener.onGetContentsUsageInfo(result);
	}
	@Override
	protected void onCancelled() {
		this.listener.onCancel();
	}
}
