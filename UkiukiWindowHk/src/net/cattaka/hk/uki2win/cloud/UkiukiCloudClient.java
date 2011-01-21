package net.cattaka.hk.uki2win.cloud;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.cloud.UkiukiAccountRegistTask.OnAccountRegistListener;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo.CategoryInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiDeleteUkiukiBallTask.OnDeleteUkiukiBallListener;
import net.cattaka.hk.uki2win.cloud.UkiukiGetContentsTask.OnGetSceneObjectInfoListener;
import net.cattaka.hk.uki2win.cloud.UkiukiGetContentsUsageTask.OnGetContentsUsageListener;
import net.cattaka.hk.uki2win.cloud.UkiukiGetServiceInfoTask.OnGetServiceInfoListener;
import net.cattaka.hk.uki2win.cloud.UkiukiLoginTask.OnLoginListener;
import net.cattaka.hk.uki2win.cloud.UkiukiSubmitUkiukiBallTask.OnSubmitUkiukiBallListener;
import net.cattaka.hk.uki2win.json.JSONException;
import net.cattaka.hk.uki2win.json.JSONObject;
import net.cattaka.hk.uki2win.net.MimeType;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;
import android.net.Uri;

import com.google.android.maps.GeoPoint;

public class UkiukiCloudClient {
	public static final String URL_GET_CONTENTS_USAGE = "https://www.uki2view.jp/uki2/message/genre/v1/";
	public static final String URL_GET_CONTENTS = "https://www.uki2view.jp/uki2/message/message/v5/";
	public static final String URL_GET_CHILD_CONTENTS = "https://www.uki2view.jp/uki2/message/summary/child/v3/";
	public static final String URL_GET_SERVICE_LIST = "http://www.uki2view.jp/uki2/service/list/v3/";
	public static final String URL_GET_SERVICE_GENRE = "http://www.uki2view.jp/uki2/service/genre/light/v3/";
	public static final String URL_GET_SERVICE_DATA = "http://www.uki2view.jp/uki2/service/search/v3/";
	public static final String URL_ACCOUNT_REGIST = "https://www.uki2view.jp/uki2/account/regist/v3/";
	public static final String URL_ACCOUNT_REQUEST = "https://www.uki2view.jp/uki2/account/request/v3/";
	public static final String URL_LOGIN = "https://www.uki2view.jp/uki2/auth/login/v3/";
	public static final String URL_SUBMIT_UKIUKI_BALL = "http://www.uki2view.jp/uki2/message/regist/v3/";
	public static final String URL_DELETE_UKIUKI_BALL = "http://www.uki2view.jp/uki2/message/delete/v2/";
	
	public UkiukiContentsUsageInfo getUkiukiContentsUsageInfo() {
		return ukiukiCloudState.getUkiukiContentsUsageInfo();
	}

	public UkiukiServiceGenreInfo getUkiukiServiceGenreInfo(String sid) {
		if (sid == null) {
			return null;
		}
		return ukiukiCloudState.getUkiukiServiceGenreInfo(sid);
	}

	public String getSessionCodeUkiukiView() {
		return ukiukiCloudState.getSessionCodeUkiukiView();
	}
	
	public String getAccountUkiukiView() {
		return ukiukiCloudState.getAccountUkiukiView();
	}
	
	private WebCacheUtil webCacheUtil;
	private UkiukiCloudState ukiukiCloudState; 
	
	public UkiukiCloudClient(String model, String apiKeyUkiukiView) {
		this.ukiukiCloudState = new UkiukiCloudState();
		this.ukiukiCloudState.setModel(model);
		this.ukiukiCloudState.setApiKeyUkiukiView(apiKeyUkiukiView);
	}
	
	public UkiukiCloudClient(WebCacheUtil webCacheUtil, String model, String apiKeyUkiukiView) {
		this.ukiukiCloudState = new UkiukiCloudState();
		this.ukiukiCloudState.setModel(model);
		this.ukiukiCloudState.setApiKeyUkiukiView(apiKeyUkiukiView);
		this.webCacheUtil = webCacheUtil;
	}

	public boolean isEssentialInfoReady() {
		return (this.ukiukiCloudState.getUkiukiContentsUsageInfo() != null);
	}

	public UkiukiGetContentsUsageTask getContentsUsage(OnGetContentsUsageListener listener) {
		UkiukiGetContentsUsageTask task = new UkiukiGetContentsUsageTask(listener, webCacheUtil, ukiukiCloudState);
		task.execute();
		return task;
	}

	public UkiukiGetContentsTask getContents(OnGetSceneObjectInfoListener listener, GeoPoint geoPoint, float range, int maxSceneObjectNum) {
		UkiukiGetContentsTask task = new UkiukiGetContentsTask(listener, webCacheUtil, ukiukiCloudState, range, maxSceneObjectNum);
		task.execute(geoPoint);
		return task;
	}

	public UkiukiGetChildContentsTask getChildContents(UkiukiGetChildContentsTask.OnGetChildContentsListener listener, String objectId, int maxSceneObjectNum) {
		UkiukiGetChildContentsTask task = new UkiukiGetChildContentsTask(listener, webCacheUtil, ukiukiCloudState, objectId, maxSceneObjectNum);
		task.execute();
		return task;
	}

	public UkiukiGetServiceInfoTask getServiceList(OnGetServiceInfoListener listener) {
		UkiukiGetServiceInfoTask task = new UkiukiGetServiceInfoTask(listener, webCacheUtil, ukiukiCloudState);
		task.execute();
		return task;
	}

	public UkiukiGetServiceDataTask getServiceData(OnGetSceneObjectInfoListener listener, String sid, GeoPoint geoPoint, float range, int maxSceneObjectNum, ServiceSearchCondition serviceSearchCondition) {
		UkiukiGetServiceDataTask task = new UkiukiGetServiceDataTask(listener, webCacheUtil, ukiukiCloudState, sid, range, maxSceneObjectNum, serviceSearchCondition);
		task.execute(geoPoint);
		return task;
	}

	public UkiukiLoginTask login(OnLoginListener listener, String account, String password) {
		// TODO sessionCodeUkiukiViewなどの持ち方が鈍くさいので直すこと
		ukiukiCloudState.setSessionCodeUkiukiView(null);
		ukiukiCloudState.setAccountUkiukiView(null);
		
		UkiukiLoginTask task = new UkiukiLoginTask(listener, webCacheUtil, ukiukiCloudState, account, password);
		task.execute();
		return task;
	}

	public UkiukiAccountRegistTask accountRegist(OnAccountRegistListener listener, String account, String password) {
		UkiukiAccountRegistTask task = new UkiukiAccountRegistTask(listener, ukiukiCloudState, account, password);
		task.execute();
		return task;
	}

	public UkiukiSubmitUkiukiBallTask submitUkiukiBall(OnSubmitUkiukiBallListener listener, SceneObjectInfo soInfo) {
		UkiukiSubmitUkiukiBallTask task = new UkiukiSubmitUkiukiBallTask(listener, webCacheUtil, ukiukiCloudState, soInfo);
		task.execute();
		return task;
	}

	public UkiukiDeleteUkiukiBallTask deleteUkiukiBall(OnDeleteUkiukiBallListener listener, String objectId) {
		UkiukiDeleteUkiukiBallTask task = new UkiukiDeleteUkiukiBallTask(listener, webCacheUtil, ukiukiCloudState,objectId);
		task.execute();
		return task;
	}

	public static SceneObjectInfo parseSceneObjectFromUkiukiServer(JSONObject message, UkiukiContentsUsageInfo ukiukiContentsUsageInfo) throws JSONException {
		int ubLat = (int)(message.getDouble("lat")*1E6);
		int ubLon = (int)(message.getDouble("lng")*1E6);
		GeoPoint gp = new GeoPoint(ubLat, ubLon);
		String objectId = message.getString("objectid");
		String name = message.getString("title");
		String nickname = message.getString("nickname");
		String mimeTypeStr = message.getString("mtype");
		String registtime = formatDateString(message.getString("registtime"));
		int numOfComments = message.getInt("moderate");
		String detail = nickname + "\n" + registtime + ", " + numOfComments + " comments";
		
		Uri iconUri = null;
		MimeType mimeType = MimeType.parse(mimeTypeStr);
		if (mimeType != null && mimeType.getSubType().length() > 0) {
			CategoryInfo categoryInfo = ukiukiContentsUsageInfo.getCategoryInfo(mimeType.getSubType());
			iconUri = (categoryInfo != null) ? categoryInfo.iconUri : null;
		}
		if (iconUri == null) {
			// 無ければUnknownにしておく
			iconUri = UkiukiWindowConstants.RESOURCE_UNKNOWN_URI;
		}
		
		SceneObjectInfo soInfo = new SceneObjectInfo();
		soInfo.setObjectId(objectId);
		soInfo.setGeoPoint(gp);
		soInfo.setCommentable(true);
		soInfo.setTitle(name);
		soInfo.setDetail(detail);
		soInfo.setOwnerNickname(nickname);
		soInfo.setMimeType(mimeType);
		soInfo.setIconUri(iconUri);
		soInfo.setNumOfComments(numOfComments);
		return soInfo;
	}

	public static SceneObjectInfo parseSceneObjectFromUkiukiServerComment(JSONObject message, UkiukiContentsUsageInfo ukiukiContentsUsageInfo) throws JSONException {
		String objectId = extractStringWithCheck(message, "objectid", "");
		String name = extractStringWithCheck(message, "title", "");
		String nickname = extractStringWithCheck(message, "nickname", "");
		String mimeTypeStr = extractStringWithCheck(message, "mtype", null);
		String registtime = formatDateString(extractStringWithCheck(message, "registtime", null));
		String detail = nickname + " " + registtime;
		
		Uri iconUri = null;
		MimeType mimeType = MimeType.parse(mimeTypeStr);
		if (mimeType != null && mimeType.getSubType().length() > 0) {
			CategoryInfo categoryInfo = ukiukiContentsUsageInfo.getCategoryInfo(mimeType.getSubType());
			iconUri = (categoryInfo != null) ? categoryInfo.iconUri : null;
		}
		if (iconUri == null) {
			// 無ければUnknownにしておく
			iconUri = UkiukiWindowConstants.RESOURCE_UNKNOWN_URI;
		}
		
		SceneObjectInfo soInfo = new SceneObjectInfo();
		soInfo.setObjectId(objectId);
		soInfo.setCommentable(true);
		soInfo.setTitle(name);
		soInfo.setDetail(detail);
		soInfo.setOwnerNickname(nickname);
		soInfo.setMimeType(mimeType);
		soInfo.setIconUri(iconUri);
		return soInfo;
	}

	public static SceneObjectInfo parseSceneObjectForService(JSONObject message, UkiukiServiceGenreInfo ukiukiServiceGenreInfo) throws JSONException {
		int ubLat = (int)(message.getDouble("lat")*1E6);
		int ubLon = (int)(message.getDouble("lng")*1E6);
		GeoPoint gp = new GeoPoint(ubLat, ubLon);
		String objectId = extractStringWithCheck(message, "objectid", "");
		String title = extractStringWithCheck(message, "title", "");
		String mimeType = extractStringWithCheck(message, "mimeType", null);
		String infoUri = extractStringWithCheck(message, "uri", null);
		String coupon = extractStringWithCheck(message, "coupon", null);
		String category = extractStringWithCheck(message, "category", "");
		String content = extractStringWithCheck(message, "content", "");
		String detail = extractCatchCopy(content);

		Uri iconUri;
		{
			CategoryInfo categoryInfo = ukiukiServiceGenreInfo.getCategoryInfo(category);
			iconUri = (categoryInfo != null) ? categoryInfo.iconUri : null;
			if (iconUri == null) {
				// 無ければUnknownにしておく
				iconUri = UkiukiWindowConstants.RESOURCE_UNKNOWN_URI;
			}
		}
		
		SceneObjectInfo soInfo = new SceneObjectInfo();
		soInfo.setObjectId(objectId);
		soInfo.setGeoPoint(gp);
		soInfo.setTitle(title);
		soInfo.setDetail("");
		soInfo.setOwnerNickname("");	// TODO 提供元を入れる
		soInfo.setMimeType(MimeType.parse(mimeType));
		soInfo.setIconUri(iconUri);
		soInfo.setInfoUri(parseUri(infoUri));
		soInfo.setCouponUri(parseUri(coupon));
		soInfo.setContent(content);
		soInfo.setDetail(detail);
		return soInfo;
	}

	public static String createParamString(Map<String, String> paramMap) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry:paramMap.entrySet()) {
			if (sb.length() > 0) {
				sb.append('&');
			}
			sb.append(Uri.encode(entry.getKey()));
			sb.append('=');
			sb.append(Uri.encode(entry.getValue()));
		}
		return sb.toString();
	}
	
	public static String extractStringWithCheck(JSONObject jsonObject, String name, String defaultValue) throws JSONException {
		return (jsonObject.has(name)) ? jsonObject.getString(name) : defaultValue;
	}
	
	public static String formatDateString(String str) {
		final DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final DateFormat outputFormat = DateFormat.getDateTimeInstance();
		if (str == null) {
			return "";
		}
		try {
			Date date = parseFormat.parse(str);
			return outputFormat.format(date);
		} catch (ParseException e) {
		}
		return "";
	}
	
	public static Uri parseUri(String arg) {
		if (arg != null && arg.length() > 0) {
			return Uri.parse(arg);
		} else {
			return null;
		}
	}
	
	public static String extractGenreCode(String content) {
		Pattern ptn = Pattern.compile("<meta .*?name=['\"]genrecode['\"].*?>");
		Matcher mt = ptn.matcher(content);
		if (mt.find()) {
			String metaTag = content.substring(mt.start()+1, mt.end()-1);
			
			String[] tmp = metaTag.split("\\s+");
			for (String t:tmp) {
				if (t.matches("content=['\"].*['\"]")) {
					return t.substring(9, t.length() - 1);
				}
			}
		}
		return null;
	}
	
	public static String extractCatchCopy(String content) {
		Pattern ptn = Pattern.compile("<div .*?name=['\"]catch['\"].*?>.*</div>");
		Matcher mt = ptn.matcher(content);
		if (mt.find()) {
			String divTag = content.substring(mt.start()+1, mt.end()-1);
			int start = divTag.indexOf('>') + 1;
			int end = divTag.indexOf('<');
			if (0 < start && 0 < end && start < end) {
				return divTag.substring(start, end);
			}
		}
		return null;
	}
}
