package jp.bs.app.ukiukiview;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.bs.app.ukiukiview.R;
import jp.co.brilliantservice.app.openar.data.ARObject;
import jp.co.brilliantservice.app.openar.data.ApiAdapter;
import jp.co.brilliantservice.app.openar.data.PoiObject;
import jp.co.brilliantservice.utility.SdLog;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

public class HotPepperApi extends ApiAdapter {
	private static final String SERVICENAME = "Hot Pepper";
	private static final String SERVICESUMMARY = "Hot Pepper APIでグルメ店舗の検索を行います";

	private static final int[] mStoreIcons = {
		 R.drawable.ic_store_1_izakaya
		,R.drawable.ic_store_2_dbar
		,R.drawable.ic_store_3_sousaku
		,R.drawable.ic_store_4_washoku
		,R.drawable.ic_store_5_youshoku
		,R.drawable.ic_store_6_itarian
		,R.drawable.ic_store_7_tyuuka
		,R.drawable.ic_store_8_yakiniku
		,R.drawable.ic_store_9_ajian
		,R.drawable.ic_store_10_kakukoku
		,R.drawable.ic_store_11_karaoke
		,R.drawable.ic_store_12_bar
		,R.drawable.ic_store_13_ramen
		,R.drawable.ic_store_14_okonomi
		,R.drawable.ic_store_15_kafe
		,R.drawable.ic_store_16_sonota
		,R.drawable.ic_store_1_izakaya
	};
	private static final String[] mGenreName = {
		"居酒屋",
		"ダイニングバー",
		"創作料理",
		"和食",
		"洋食",
		"イタリア料理",
		"中華料理",
		"焼き肉",
		"アジアン料理",
		"各国料理",
		"カラオケ",
		"バー",
		"ラーメン",
		"お好み焼き",
		"カフェ",
		"その他",
		"携帯クーポンあり"
	};

	public HotPepperApi(Resources resource) {
		super(resource);
	}

	private final String APIKEY = "04a2115e4cf1cfd8";
	private final String FORMAT = "json";
	private String KEYWORD = "";
	private final String COUNT = "20";
	private final String ORDER = "0";

	public byte[] httpConnection(String path) throws Exception {
		byte[] storeData = new byte[100];
		int size;
		HttpURLConnection http = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;

		try {
			URL url = new URL(path);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			in = http.getInputStream();
			out = new ByteArrayOutputStream();
			while (true) {
				size = in.read(storeData);
				if (size <= 0) {
					break;
				}
				out.write(storeData, 0, size);
			}
			out.close();
			in.close();
			http.disconnect();
		} catch (Exception e) {
			try {
				if (http != null)
					http.disconnect();
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Exception e2) {
			}
			throw e;
		}
		return out.toByteArray();
	}

	@Override
	protected int getApiIconId() {
		return R.drawable.webapi_hotpepper;
	}

	public String getApiName() {
		return SERVICENAME;
	}

	public String getApiSummary() {
		return SERVICESUMMARY;
	}

	public String getServerVersion() {
		return "dummy";
	}

	public String getSuitableClientVersion() {
		return "";
	}

	@Override
	public int getIconId(int index) {
		if (getMaxKind()>index) {
			return mStoreIcons[index];
		}
		return -1;
	}

	@Override
	public int getMaxKind() {
		return mStoreIcons.length;
	}
	public boolean havePost() {
		return false;
	}
	public boolean postData(PoiObject obj) {
		return false;
	}

	private int convertRange(int range) {
		int ret = 2;
		if (range<=300) {
			ret = 1;
		} else if (range<=500) {
			ret = 2;
		} else if (range<=1000) {
			ret = 3;
		} else if (range<=2000) {
			ret = 4;
		} else if (range<=3000) {
			ret = 5;
		} else if (range>3000) {
			ret = 5;
		}
		return ret;
	}

	public List<ARObject> retrieveData(Location location, int range) {
		List<ARObject> items = new ArrayList<ARObject>();
		try {
			byte[] hotPepperData = httpConnection("http://webservice.recruit.co.jp/hotpepper/gourmet/v1/"
															+ "?key=" + APIKEY
															+ "&format=" + FORMAT
															+ "&keyword=" + KEYWORD
															+ "&count=" + COUNT
															+ "&lat=" + location.getLatitude()
															+ "&lng=" + location.getLongitude()
															+ "&order=" + ORDER
															+ "&range=" + convertRange(range));

			String str = new String(hotPepperData);
			str = str.substring(str.indexOf('['), str.lastIndexOf(']') + 1);
			JSONArray jsonsHotPepper = new JSONArray(str);
			SdLog.put("json="+str);

			int length = jsonsHotPepper.length();
			for (int i = 0; i < length; i++) {
				JSONObject storeObj = jsonsHotPepper.getJSONObject(i);
				ARObject storeData = new ARObject("hotpepper_"+storeObj.getString("id"));
				storeData.mName = storeObj.getString("name");
				SdLog.put("name="+storeData.mName);
				PoiObject obj = new PoiObject(
						Double.parseDouble(storeObj.getString("lat"))
						, Double.parseDouble(storeObj.getString("lng"))
						, null
						, null
						, null
						, null
						, Locale.getDefault().getLanguage());
				storeData.mObj = obj;
//				storeData.mObj.mLatitude = Double.parseDouble(storeObj.getString("lat"));
//				storeData.mObj.mLongitude = Double.parseDouble(storeObj.getString("lng"));
				storeData.mType = Integer.valueOf(storeObj.getJSONObject("genre").getString("code").substring(1, 4)) - 1;
				storeData.mSummary = storeObj.getString("catch");
//				address.setUrl(storeObj.getJSONObject("urls").getString("pc"));
				storeData.mUri = Uri.parse((storeObj.getJSONObject("urls").getString("pc")));
//				String station = storeObj.getString("station_name");
				String photo = storeObj.getJSONObject("photo").getJSONObject("mobile").getString("s");
				String genre = storeObj.getJSONObject("genre").getString("name");
				String capacity = storeObj.getString("capacity");
				String average = storeObj.getJSONObject("budget").getString("average");
				String access = storeObj.getString("mobile_access");
//				String mobileSite = (storeObj.getJSONObject("urls").getString("mobile"));
//				address.setLocality(storeObj.getString("address"));
				storeData.mObj.mContent =
					"<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'></head><body>"
					+"<table border='0'><tr><td>"
					+"<img src='"+photo+"' align='left' />"
					+"</td><td>"
					+genre+"<br />"
					+storeData.mSummary+"<br />"
					+access+"<br />"
					+capacity+"席、"+average+"<br />"
					+"<a href='"+storeData.mUri+"' >PCサイト</a> "
//					+"<a href='"+mobileSite+"' >Mobileサイト</a><br />"
					+"</td><tr></table>"
					+"</body></html>";
				SdLog.put("content="+storeData.mObj.mContent);
				storeData.mObj.mMimeType ="text/html";
				storeData.mApiAdapter = this;
				storeData.mIconId = getIconId(storeData.mType);
//				storeData.setIcon(getIcon(storeData.mType));
//				storeData.updateProjection(mMapView, mX, mY);
//				SdLog.put("name="+storeData.mName);

				items.add(i, storeData);
			}
		} catch (Exception e) {
			Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
		}
		return items;
	}

	@Override
	public void setAllFilter(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFilter(int arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFilterName(int index) {
		//TODO retrieve from http://webservice.recruit.co.jp/hotpepper/genre/v1/?key=api_key
		if (index < mGenreName.length) {
			return mGenreName[index];
		}
		return null;
	}

	@Override
	public boolean login(String uid, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String registerAccount(String userName, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNickname(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ARObject> getChildren(String parentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(String id) {
		return false;
	}

	@Override
	public boolean cancel() {
		return false;
	}
}