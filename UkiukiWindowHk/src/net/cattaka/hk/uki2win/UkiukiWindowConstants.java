package net.cattaka.hk.uki2win;

import android.net.Uri;
import net.cattaka.hk.uki2win.R;

public class UkiukiWindowConstants {
	public static final String UKIUKI_MIME_TYPE = "emotion";
	
	public static final int WEB_CONNECTION_TIMEOUT = 15000;
	public static final int RETRY_COUNT_GET_CONTENTS_USAGE = 3;
	
	public static final String TAG = "UkiukiWindow";
	public static final String RESOURCE_URI_SCHEMA = "android.resource";
	public static final String RESOURCE_URI_BASE = "android.resource://" + UkiukiWindowConstants.class.getPackage().getName() + "/";

	/** サービス非表示時用のダミーSID */
	public static final String SID_INVISIBLE = "";
	
	public static final Uri RESOURCE_LOADING_URI = Uri.parse(RESOURCE_URI_BASE + R.drawable.loading);
	public static final Uri RESOURCE_ERROR_URI = Uri.parse(RESOURCE_URI_BASE + R.drawable.error);
	public static final Uri RESOURCE_UNKNOWN_URI = Uri.parse(RESOURCE_URI_BASE + R.drawable.unknown);

	public static final String PREF_NAME_SERVICE_INFO = "service_info";
	public static final String KEY_MAX_SCENE_OBJECT_NUM = "max_scene_object_num";
	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_SELECTED_SID = "selected_sid";
	public static final String KEY_SERVICE_INFO_NUM = "service_info_num";
	public static final String KEY_SI_SID_BASE = "si_sid_";
	public static final String KEY_SI_SERVICE_NAME_BASE = "si_service_name_";
	public static final String KEY_SI_SERVICE_NUMBER_BASE = "si_service_number_";
	public static final String KEY_SI_ICON_URI_BASE = "si_icon_uri_";
	public static final String KEY_SI_EXPLAIN_BASE = "si_explain_";
	public static final String KEY_SI_CORPORATION_BASE = "si_corporation_";
	public static final String KEY_SI_CATCH_COPY_BASE = "si_catch_copy_";

	public static final int DEFAULT_SCENE_OBJECT_NUM = 100;
	public static final int DEFAULT_MAP_ZOOM_LEVEL = 17;
	public static final int MIN_MAP_ZOOM_LEVEL = 12;
	public static final int MAX_MAP_ZOOM_LEVEL = 19;

	public static final float STEP_ICON_SIZE_RATE = 0.005f;
	public static final float DEFAULT_ICON_SIZE_RATE = 0.03f;
	public static final float MIN_ICON_SIZE_RATE = 0.01f;
	public static final float MAX_ICON_SIZE_RATE = 0.10f;

	public static final float STEP_CAMERA_ZOOM_LEVEL = 0.5f;
	public static final float DEFAULT_CAMERA_ZOOM_LEVEL = -1.0f;
	public static final float MIN_CAMERA_ZOOM_LEVEL = -2.0f;
	public static final float MAX_CAMERA_ZOOM_LEVEL = 2.0f;
	public static final float ANIMATE_CAMERA_ZOOM_LEVEL_STEP = STEP_CAMERA_ZOOM_LEVEL / 6f;
	
	public static final int MIN_LOCATION_UPDATE_INTERVAL = 3000;
	public static final long MAP_BITMAP_UPDATE_INTERVAL = 3000;
	public static final float MIN_LOCATION_UPDATE_DISTANCE_RATE = 0.25f;

	public static final long REQUEST_LOCATION_TIMEOUT = 10000;
	public static final long UPDATE_ETC_INTERVAL = 100;

	public static final float MOVE_LIMIT_DISTANCE_RATE = 0.25f;

	// 地球一周のサイズ(仮)
	public static final double CIRCUMFERENCE_OF_EARTH = 40960000;
	
	public static final float CAMERA_ALTITUDE_RATE = 0.1f;
	
	public static final int SINGLE_TOUCH_INTERVAL = 100;
	
	public static final boolean SWITCH_3D = false;
}
