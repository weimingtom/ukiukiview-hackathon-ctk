package jp.bs.app.ukiukiview;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.bs.app.ukiukiview.R;
import jp.co.brilliantservice.app.openar.data.ARObject;
import jp.co.brilliantservice.app.openar.data.ApiAdapter;
import jp.co.brilliantservice.app.openar.data.PoiObject;
import jp.co.brilliantservice.utility.SdLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class Uki2ServerApi extends ApiAdapter {
	private static final String SERVICENAME = "Ukiuki Server";
	private static final String SERVICESUMMARY = "Ukiuki Server APIで周辺情報の検索を行います";

//	public static final String SERVER = "uki2server.appspot.com";	// GAEサーバー
	public static final String SERVER = "www.uki2view.jp/uki2";		// EC2サーバー
	
	public Location mLocation = null;
	private String mSessionId = null;
	private String mUserId = null;
	private String mChallenge = null;
	private String mAccount = null;
	private String mPassword = null;
	private String mModelAndVersionParam = "model="+Uri.encode(Build.MODEL+"__"+ Build.VERSION.RELEASE);
	public int mResponseCode = RESPONSE_OK;
	public static final int RESPONSE_OK = 200;
	public static final int RESPONSE_BAD_REQUEST = 400;
	public static final int RESPONSE_FORBIDDEN = 403;
	public static final int RESPONSE_DUPLICATE_ACCOUNT = 405;
	public static final int RESPONSE_SERVER_ERROR = 500;

	private boolean mRequesting = false;
	private boolean mCanceled = false;

	private ARObject postedItem = null;

	private static final int[] mIcons = {
		 R.drawable.joy
		 , R.drawable.angry
		 , R.drawable.sad
	};
	private static final String[] mGenreName = {
		"嬉"
		,"怒"
		,"哀"
	};

//	private MapView mMapView = null;

	public Uki2ServerApi(Resources resource) {
		super(resource);
	}

	public static final String APIKEY = "966262dcfc73e380ab5e24b3d54d3fcc";
	
	public byte[] httpConnection(String path, String method) throws Exception {
		mRequesting = true;
		byte[] storeData = new byte[100];
		int size;
		HttpURLConnection http = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;

		try {
			URL url = new URL(path);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod(method);
			http.connect();
			in = http.getInputStream();
			out = new ByteArrayOutputStream();
			while (!mCanceled) {
				size = in.read(storeData);
				if (size <= 0) {
					break;
				}
				out.write(storeData, 0, size);
			}
			out.close();
			in.close();
			if (!mCanceled) {
				mResponseCode = http.getResponseCode();
				SdLog.put("response code="+mResponseCode);
			} else {
				mCanceled = false;
				mRequesting = false;
				Exception e = new Exception("user canceled");
				http.disconnect();
				throw e;
			}
			http.disconnect();
			mRequesting = false;
		} catch (Exception e) {
			try {
				if (http != null) {
					mResponseCode = http.getResponseCode();
					SdLog.put("response code="+mResponseCode);
					http.disconnect();
				}
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Exception e2) {
			}
			mRequesting = false;
			mCanceled = false;
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
//		String request = "http://"+SERVER+"/version/ukiukiview.txt";
		String request = "http://sites.google.com/site/uki2view/uki2client/version.txt";

		SdLog.put("request="+request);
		byte[] response;
		String res = null;
		try {
			response = httpConnection(request, "GET");
			res = new String(response);
			SdLog.put("response="+response);
			res = res.replace("version:","");
			res = res.replace(".","");
			SdLog.put("version="+res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public int getIconId(int index) {
		if (getMaxKind()>index) {
			return mIcons[index];
		}
		return -1;
	}

	@Override
	public int getMaxKind() {
		return mIcons.length;
	}

	private int convertRange(int range) {
		int ret = 6;
		if (range<=10) {
			ret = 1;
		} else if (range<=30) {
			ret = 2;
		} else if (range<=100) {
			ret = 3;
		} else if (range<=300) {
			ret = 4;
		} else if (range<=500) {
			ret = 5;
		} else if (range<=1000) {
			ret = 6;
		} else if (range<=2000) {
			ret = 7;
		} else if (range<=5000) {
			ret = 8;
		} else if (range<=10000) {
			ret = 9;
		} else if (range>10000) {
			ret = 9;
		}
		return ret;
	}

	public List<ARObject> retrieveData(Location location, int range) {
		List<ARObject> items= null;
		mLocation = location;
		String query = "apikey=" + APIKEY
			+ "&lat=" + location.getLatitude()
			+ "&lng=" + location.getLongitude()
			+ "&range=" + convertRange(range)
			+ "&count=100";

		try {
			String request = "http://"+SERVER+"/message/message/v3/?"
				+ query
				+ "&"+mModelAndVersionParam;
			SdLog.put("request="+request);
			byte[] response = httpConnection(request, "GET");
			String str = new String(response);
			SdLog.put("uki2server json="+str);
			items = getItemListFromJson(str);
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
		if (index < mGenreName.length) {
			return mGenreName[index];
		}
		return null;
	}

	@Override
	public boolean havePost() {
		return true;
	}

	@Override
	public boolean postData(PoiObject obj) {
		boolean ret = false;
		int retryCount = 1;
		if (mSessionId == null) {
			return false;
		}
		while (ret==false && retryCount>0) {
			try {
				String contentEncoded = Uri.encode(obj.mContent);
//				String request = "http://"+SERVER+"/message/regist/v4/?"
				String request = "http://"+SERVER+"/message/regist/v3/?"
					+ "apikey=" + APIKEY
					+ "&session=" + mSessionId
					+ "&lat=" + obj.mLatitude
					+ "&lng=" + obj.mLongitude
					+ "&type=" + obj.mMimeType
					+ "&content=" + contentEncoded
					+ "&language=" + obj.mLanguage
					+ "&"+mModelAndVersionParam;
				if (obj.mSummary!=null) {
					request += "&title=" + Uri.encode(obj.mSummary);
				}
				if (obj.mParentUid!=null) {
					request += "&parentid=" + obj.mParentUid;
				}
				SdLog.put("request="+request);
				byte[] response = httpConnection(request,"GET");

				String str = new String(response);
				SdLog.put("uki2server response="+str);
//				List<ARObject> items = getItemListFromJson(str);
//				if (items !=null && items.size()>0) {
//					postedItem = items.get(0);
//				}
				ret = true;
			} catch (Exception e) {
				Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
			}
			SdLog.put("postData ret="+ret);
			if (ret==false) {
				login(mAccount,mPassword);
			}
			retryCount--;
		}
		return ret;
	}

	private String getMd5String(String source) {
		String ret = null;
		MessageDigest aMd5Digester;
		try {
			aMd5Digester = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			SdLog.put("NoSuchAlgorithmException "+e.getLocalizedMessage());
			return ret;
		}
		byte aSignatureBytes[] = aMd5Digester.digest(source.getBytes());

		StringBuffer aDigestedString = new StringBuffer();
		for (byte aByte : aSignatureBytes) {
	        aDigestedString.append(String.format("%02x", 0xFF & aByte));
		}
		return aDigestedString.toString();
	}

	public String registerAccount(String nickname, String password) {
		if (mUserId!=null) {
			return null;
		}
		String nickNameEncoded = null;
		nickNameEncoded = Uri.encode(nickname);
		String passwordMd5 = getMd5String(password);
		try {
			String request = "https://"+SERVER+"/account/regist/v3/?"
				+ "apikey=" + APIKEY
				+ "&account=" + nickNameEncoded
				+ "&nickname=" + nickNameEncoded
				+ "&password=" + passwordMd5
				+ "&"+mModelAndVersionParam;
			SdLog.put("request="+request);
			byte[] response = httpConnection(request,"GET");

			String str = new String(response);
			SdLog.put("uki2server response="+str);
			mUserId = str;
			if (mOnRegisterListener!=null) {
				mOnRegisterListener.onRegisterListener(mUserId);
			}
			return mUserId;
		} catch (Exception e) {
			Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
			if (mOnRegisterListener!=null) {
				mOnRegisterListener.onRegisterListener(null);
			}
			return null;
		}
	}

	private String requestChallenge(String nickName) {
		String ret = null;
		String nickNameEncoded = Uri.encode(nickName);

		if (nickName==null) {
			return null;
		}
		try {
			String request = "https://"+SERVER+"/account/request/v3/?"
				+ "apikey=" + APIKEY
				+ "&account=" + nickNameEncoded
				+ "&"+mModelAndVersionParam;
			SdLog.put("request="+request);
			byte[] response = httpConnection(request,"GET");
			String str = new String(response);
			SdLog.put("uki2server response="+str);
			ret = str;
		} catch (Exception e) {
			Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
		}
		return ret;
	}

	public boolean login(String userid, String password) {
		String challengeResponse = null;
		if (userid==null || password==null) {
			return false;
		}
		mAccount = userid;
		mPassword = password;
		boolean ret = false;
		mChallenge = requestChallenge(userid);
		if (mChallenge==null) {
			return false;
		}
		String useridEncoded = Uri.encode(userid);
		challengeResponse = getMd5String(useridEncoded + getMd5String(password) + mChallenge);
		try {
			String request = "https://"+SERVER+"/auth/login/v3/?"
				+ "apikey=" + APIKEY
				+ "&account=" + useridEncoded
				+ "&password=" + challengeResponse
				+ "&"+mModelAndVersionParam;
			SdLog.put("request="+request);
			byte[] response = httpConnection(request,"GET");
			String str = new String(response);
			SdLog.put("uki2server response="+str);
			mSessionId = str;
			ret = true;
			if (mOnLoginListener!=null) {
				mOnLoginListener.onLoginListener(true);
			}
			mUserId = requestUserId();
		} catch (Exception e) {
			Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
			if (mOnLoginListener!=null) {
				mOnLoginListener.onLoginListener(false);
			}
		}
		return ret;
	}

	@Override
	public String getNickname(String id) {
		String ret = null;
		try {
			String request = "https://"+SERVER+"/account/get/v2/?"
				+ "apikey=" + APIKEY
				+ "&userid=" + id
				+ "&"+mModelAndVersionParam;
			if (mSessionId!=null) {
				request += "&session=" + mSessionId;
			} else {
				SdLog.put("session=null");
			}
			SdLog.put("request="+request);
			byte[] response = httpConnection(request,"GET");
			String str = new String(response);
			SdLog.put("uki2server json="+str);
			str = str.substring(str.indexOf('['), str.lastIndexOf(']') + 1);
			JSONArray json = new JSONArray(str);
			ret = json.getJSONObject(0).getString("nickname");
			SdLog.put("uki2server response=["+ret+"]");
		} catch (Exception e) {
			Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
		}
		return ret;
	}

	@Override
	public List<ARObject> getChildren(String parentId) {
		List<ARObject> items = new ArrayList<ARObject>();
		String query = "apikey=" + APIKEY
			+ "&parentid=" + parentId;
//			+ "&count=100"; TODO:

		if (mSessionId!=null) {
			query += "&session=" + mSessionId;
		}

		try {
			String request = "http://"+SERVER+"/message/parent/v2/?"
				+ query
				+ "&sort=1"
				+ "&"+mModelAndVersionParam;
			SdLog.put("request="+request);
			byte[] response = httpConnection(request,"GET");
			String str = new String(response);
			SdLog.put("uki2server json="+str);
			str = str.substring(str.indexOf('['), str.lastIndexOf(']') + 1);
			JSONArray jsonsHotPepper = new JSONArray(str);
			DateFormat fmt = DateFormat.getDateTimeInstance();
			fmt.setLenient(false);
			SimpleDateFormat fmtsimple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Location itemLocation = new Location("manual");

			int length = jsonsHotPepper.length();
			for (int i = 0; i < length; i++) {
				JSONObject storeObj = jsonsHotPepper.getJSONObject(i);
				ARObject arObject = new ARObject();
				arObject.mUid = storeObj.getString("objectid");
				PoiObject obj = new PoiObject(
						Double.parseDouble(storeObj.getString("lat"))
						, Double.parseDouble(storeObj.getString("lng"))
						, null
						, null
						, null
						, null
						, Locale.getDefault().getLanguage());
				arObject.mObj = obj;
				itemLocation.setLatitude(arObject.mObj.mLatitude);
				itemLocation.setLongitude(arObject.mObj.mLongitude);
				arObject.mDistance = itemLocation.distanceTo(mLocation);
				arObject.mSummary = storeObj.getString("title");
				arObject.mCreatedDateTime = fmtsimple.parse(storeObj.getString("registtime"));
				String mime = storeObj.getString("mtype");
				if (mime.compareTo("emotion/joy")==0) {
					arObject.mType = 0;
				} else if (mime.compareTo("emotion/angry")==0) {
					arObject.mType = 1;
				} else if (mime.compareTo("emotion/sad")==0) {
					arObject.mType = 2;
				}
				arObject.mObj.mMimeType = mime;
				arObject.mIconId = getIconId(arObject.mType);
				arObject.mApiAdapter = this;
				arObject.mName = null;
				items.add(i, arObject);
			}
		} catch (Exception e) {
			Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
		}
		return items;
	}

	private String requestUserId() {
		if (mSessionId==null) {
			return null;
		}
		try {
			String request = "https://"+SERVER+"/account/user/v2/?"
				+ "apikey=" + APIKEY
				+ "&session=" + mSessionId
				+ "&"+mModelAndVersionParam;
			SdLog.put("request="+request);
			byte[] response = httpConnection(request,"GET");

			String str = new String(response);
			SdLog.put("uki2server response="+str);
			return str;
		} catch (Exception e) {
			Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
			return null;
		}
	}

	public String getUserId() {
		return mUserId;
	}

	public boolean deleteObject() {
		boolean ret = false;
		return ret;
	}

	@Override
	public boolean cancel() {
		if (mRequesting) {
			mCanceled = true;
		}
		return false;
	}

	@Override
	public boolean delete(String id) {
		if (mSessionId==null) {
			return false;
		}
		try {
			String request = "http://"+SERVER+"/message/delete/v2/?"
				+ "apikey=" + APIKEY
				+ "&session=" + mSessionId
				+ "&objid=" + id
				+ "&"+mModelAndVersionParam;
			SdLog.put("request="+request);
			byte[] response = httpConnection(request,"DELETE");

			String str = new String(response);
			SdLog.put("uki2server response="+str);
			return true;
		} catch (Exception e) {
			Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
			return false;
		}
	}

	private List<ARObject> getItemListFromJson(String json) throws JSONException, ParseException {
		List<ARObject> items = new ArrayList<ARObject>();
		if (json.indexOf('[')!=-1 && json.lastIndexOf(']')!=-1) {
			json = json.substring(json.indexOf('['), json.lastIndexOf(']') + 1);
		}
		JSONArray jsonArray = new JSONArray(json);
		DateFormat fmt = DateFormat.getDateTimeInstance();
		fmt.setLenient(false);
		SimpleDateFormat fmtsimple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Location itemLocation = new Location("manual");

		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			JSONObject storeObj = jsonArray.getJSONObject(i);
			ARObject arObject = new ARObject();
			arObject.mUid = storeObj.getString("objectid");
			arObject.mOwnerId = storeObj.getString("userid");
			PoiObject obj = new PoiObject(
					Double.parseDouble(storeObj.getString("lat"))
					, Double.parseDouble(storeObj.getString("lng"))
					, null
					, null
					, null
					, null
					, Locale.getDefault().getLanguage());
			arObject.mObj = obj;
			itemLocation.setLatitude(arObject.mObj.mLatitude);
			itemLocation.setLongitude(arObject.mObj.mLongitude);
			arObject.mDistance = itemLocation.distanceTo(mLocation);
			arObject.mSummary = storeObj.getString("title");
			arObject.mOwnerName = storeObj.getString("nickname");
			SdLog.put("mOwnerName="+arObject.mOwnerName);
			arObject.mCreatedDateTime = fmtsimple.parse(storeObj.getString("registtime"));
			String mime = storeObj.getString("mtype");
			if (mime.compareTo("emotion/joy")==0) {
				arObject.mType = 0;
			} else if (mime.compareTo("emotion/angry")==0) {
				arObject.mType = 1;
			} else if (mime.compareTo("emotion/sad")==0) {
				arObject.mType = 2;
			}
			arObject.mObj.mMimeType = mime;
			arObject.mIconId = getIconId(arObject.mType);
			arObject.mApiAdapter = this;
			arObject.mName = null;
			items.add(i, arObject);
		}
		return items;
	}

	public ARObject getLastPostedItem() {
		return postedItem;
	}
}