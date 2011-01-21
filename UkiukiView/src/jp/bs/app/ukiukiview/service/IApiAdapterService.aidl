package jp.bs.app.ukiukiview.service;

import jp.bs.app.ukiukiview.service.IApiAdapterServiceCallback;

interface IApiAdapterService {
	void registerCallback(IApiAdapterServiceCallback cb);
	void unregisterCallback(IApiAdapterServiceCallback cb);
	int getServicesAmount();
	String getServiceName(int serviceId);
	void loadPoiObjects(int serviceId);
	int postPoiObject(int serviceId, String summary, String mimeType, String content, String parentUid, String language);
	void stopService();
}