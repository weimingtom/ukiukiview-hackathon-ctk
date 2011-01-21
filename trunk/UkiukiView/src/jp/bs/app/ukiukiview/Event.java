package jp.bs.app.ukiukiview;

import jp.co.brilliantservice.app.openar.data.ApiAdapter;
import jp.co.brilliantservice.app.openar.data.PoiObject;

public class Event {
	public static final int MESSAGE_NONE = -1;
	public static final int MESSAGE_POST_POI = 0;
	public static final int MESSAGE_POST_POI_WITH_PARENT = 1;
	public static final int MESSAGE_DELETE = 2;
	public static final int MESSAGE_FINISH = 100;
	public int mEventId = MESSAGE_NONE;
	public PoiObject mPoiObject = null;
	public ApiAdapter mAdapter = null;

	Event(int id, PoiObject object, ApiAdapter adapter) {
		mEventId = id;
		mPoiObject = object;
		mAdapter = adapter;
	}
}
