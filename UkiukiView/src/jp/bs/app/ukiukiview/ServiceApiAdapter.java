package jp.bs.app.ukiukiview;





import android.content.Context;

public abstract class ServiceApiAdapter {
//	private Context mContext = null;
	/*
	private OnRetrievedDataListener mOnRetrievedDataListener = null;
	private OnPostedDataListener mOnPostedDataListener = null;
	private PoiObject mPoiObject = null;
	protected int mRange = 0;
	protected OnLoginListener mOnLoginListener = null;
	protected OnRegisterListener mOnRegisterListener = null;
	protected Bitmap[] iconCache = new Bitmap[getMaxKind()];
	protected Bitmap[][] scaledIconCache = new Bitmap[getMaxKind()][6];
*/
	public ServiceApiAdapter(Context context) {
//		mContext = context;
	}
/*
	public void getServiceProperty() {

	}

	public Bitmap getApiIcon() {
		Bitmap ret = BitmapFactory.decodeResource(mResources , getApiIconId());
		return ret;
	}

	protected abstract int getApiIconId();
	public Bitmap getIcon(int kindIndex) {
		Bitmap ret = null;
		if (iconCache[kindIndex]==null) {
			iconCache[kindIndex] = BitmapFactory.decodeResource(mResources , getIconId(kindIndex));
			ret = iconCache[kindIndex];
		} else {
			ret = iconCache[kindIndex];
		}
		return ret;
	}
	public Bitmap getScaledIcon(int kindIndex, int distanceLevel) {
		Bitmap ret = null;
		if (scaledIconCache[kindIndex][distanceLevel]==null) {
	        Matrix matrix = new Matrix();
	        matrix.postScale((float)(1.0F-(distanceLevel)/5F+0.05), (float)(1-(distanceLevel)/5F+0.05));
	        scaledIconCache[kindIndex][distanceLevel] = Bitmap.createBitmap(getIcon(kindIndex), 0, 0, getIcon(kindIndex).getWidth(), getIcon(kindIndex).getHeight(), matrix, true);
	        ret = scaledIconCache[kindIndex][distanceLevel];
		} else {
			ret = scaledIconCache[kindIndex][distanceLevel];
		}
		return ret;
	}
	public abstract int getIconId(int kindIndex);
	public abstract String getApiName();
	public abstract String getApiSummary();
	public abstract String getServerVersion();
	public abstract String getSuitableClientVersion();
	public abstract int getMaxKind();
	public abstract void setFilter(int kindIndex, boolean isOn);
	public abstract void setAllFilter(boolean isOn);
	public abstract String getFilterName(int kindIndex);
	public void updateData(Location location, int range) {
		if (!mIsRunning) {
			mRange = range;
			mLocation = location;
			mIsRunning = true;
			new Thread(new Runnable() {
				public void run() {
					List<ARObject> items = retrieveData(mLocation, mRange);
					if (mOnRetrievedDataListener != null) {
						mOnRetrievedDataListener.onRetrievedDataListener(items);
					}
					mIsRunning = false;
				}
			}).start();
		}
	}

	public void setOnRetrievedDataListener(OnRetrievedDataListener listener) {
		mOnRetrievedDataListener = listener;
	}

	public interface OnRetrievedDataListener {
		void onRetrievedDataListener(List<ARObject> items);
	}

	public void setOnPostedDataListener(OnPostedDataListener listener) {
		mOnPostedDataListener = listener;
	}

	public interface OnPostedDataListener {
		void onPostedDataListener(PoiObject item, boolean success);
	}

	public boolean isRunning() {
		return mIsRunning;
	}

	public abstract List<ARObject> retrieveData(Location location, int range);
	public abstract boolean havePost();
	public abstract boolean postData(PoiObject obj);
	public abstract String registerAccount(String userName, String password);
	public abstract boolean login(String userName, String password);
	public abstract String getNickname(String id);
	public abstract List<ARObject> getChildren(String parentId);
	public void startPostData(PoiObject obj) {
		if (!mIsRunning) {
			mIsRunning = true;
			mPoiObject = obj;
			new Thread(new Runnable() {
				public void run() {
					boolean success = postData(mPoiObject);
					SdLog.put("postData ret="+success);
					if (mOnPostedDataListener != null) {
						SdLog.put("call onPostedDataListener");
						mOnPostedDataListener.onPostedDataListener(mPoiObject, success);
					}
					mIsRunning = false;
				}
			}).start();
		}
	}
	public void getNicknameAsync(String userid, OnRetrievedNicknameListener listener) {
		mUserDataQueue.add(new UserData(userid, null));
		mRetrievedNicknameListenerQueue.add(listener);
		new Thread(new Runnable() {
			public void run() {
				UserData userdata = mUserDataQueue.poll();
				OnRetrievedNicknameListener listener = mRetrievedNicknameListenerQueue.poll();
				userdata.nickname = getNickname(userdata.userid);
				SdLog.put("nickname ret="+userdata.nickname);
				if (listener != null) {
					SdLog.put("call onRetrievedNicknameListener");
					listener.onRetrievedNicknameListener(userdata.userid, userdata.nickname);
				}
				mIsRunning = false;
			}
		}).start();
	}
	public void setOnLoginListener(OnLoginListener onLoginListener) {
		mOnLoginListener = onLoginListener;
	}
	public interface OnLoginListener {
		void onLoginListener(boolean isSuccess);
	}
	public void setOnRegisterListener(OnRegisterListener onRegisterListener) {
		mOnRegisterListener = onRegisterListener;
	}
	public interface OnRegisterListener {
		void onRegisterListener(String userId);
	}
	public interface OnRetrievedNicknameListener {
		void onRetrievedNicknameListener(String userid, String nickname);
	}
	*/
}