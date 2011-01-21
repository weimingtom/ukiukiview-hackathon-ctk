package jp.bs.app.ukiukiview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import jp.bs.app.ukiukiview.R;
import jp.bs.app.ukiukiview.MapOverlayItem.MapItemListener;
import jp.bs.app.ukiukiview.TextInputListener.OnInputListener;
import jp.co.brilliantservice.app.openar.data.ARObject;
import jp.co.brilliantservice.app.openar.data.ApiAdapter;
import jp.co.brilliantservice.app.openar.data.Orient;
import jp.co.brilliantservice.app.openar.data.PoiObject;
import jp.co.brilliantservice.app.openar.data.ScreenItem;
import jp.co.brilliantservice.app.openar.data.ApiAdapter.OnDeletedListener;
import jp.co.brilliantservice.app.openar.data.ApiAdapter.OnPostedDataListener;
import jp.co.brilliantservice.app.openar.data.ApiAdapter.OnRetrievedDataListener;
import jp.co.brilliantservice.app.openar.view.ArMapView;
import jp.co.brilliantservice.app.openar.view.CameraView;
import jp.co.brilliantservice.app.openar.view.DataView;
import jp.co.brilliantservice.app.openar.view.TransparentPanel;
import jp.co.brilliantservice.app.openar.view.ArMapView.OnLocationChangedListener;
import jp.co.brilliantservice.utility.SdLog;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class FrameActivity extends MapActivity {
	private LocationManager locationManager = null;
	private SensorManager sensorManager = null;
	private Location mLocation = null;
	private LocationListener locationListener = null;
	private SensorEventListener sensorEventListener = null;
	private CameraView mCameraView = null;
	private DataView mDataView = null;
	private CompassView mCompassView = null;
	private ArMapView mMapView = null;
	private TransparentPanel mSearchPanel = null;
	private ImageButton mBtnSearch = null;
	private MapController mMapController = null;
	private Handler mHandler = null;
	private Handler mDataHandler = null;
	private ApiAdapter mHotpepper = null;
	private ApiAdapter mUki2Server = null;
	private boolean mIsMapMode = false;
	private long mUptime = 0;
	private List<Address> mAddress = null;
	private ImageButton mJoyButton = null;
	private ImageButton mAngryButton = null;
	private ImageButton mSadButton = null;
	private ImageButton mToggleButton = null;
	private ImageButton mMyLocationButton = null;
	private ImageButton mPlusButton = null;
	private ImageButton mMinusButton = null;
	private ImageButton mPlayPauseButton = null;
	private int mRange = 1000;
	private int requestCount = 0;
	private ImageView mSplash = null;
	private boolean mIsRegister = false;
	private boolean mIsLogin = false;
	private CustomDialog mRegisterLoginDialog = null;
	private CustomDialog mAccountInputDialog = null;
	private CustomDialog mNoticeDialog = null;
	private CustomDialog mTouchDialog = null;
	private CustomDialog mCommentDialog = null;
	private boolean isCreate = false;
	private Setting mSetting = null;
	private String mAccountName = null;
	private String mPassword = null;
	private ProgressDialog mWaitDialog = null;
	private String mDialogText = null;
	private String mPostEmotionType = null;
	private List<MapOverlayItem> mMapItemOverlays = null;
//	private List<ARObject> mItems;
	private GeomagneticField mGeomagnetic;
	private String mToast = null;
	private boolean mIsPause = false;
	private BackgroundEventHandler bgEvent = null;
	private LinkedList<Event> mQueue = null;
	private int mWidth = 0;
	private int mHeight = 0;
	private static final int UPDATE_ORIENTATION = 1;
	private static final int INVALIDATE = 2;
	private boolean mIsRestart = false;
	private ARObject mSelectedObject = null;

//	private boolean zFlg = false;

//	private int dmeoImageCount = 0;

	/*
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if( event.getAction() == MotionEvent.ACTION_DOWN) {
			ImageView image = (ImageView)findViewById(R.id.imageDemo);
			dmeoImageCount = (dmeoImageCount+1)%4;
			switch (dmeoImageCount) {
			case 0:
				image.setImageResource(R.drawable.screen);
				break;
			case 1:
				image.setImageResource(R.drawable.screen2);
				break;
			case 2:
				image.setImageResource(R.drawable.screen3);
				break;
			case 3:
				image.setImageResource(R.drawable.screen4);
				break;
			}
		}
		return super.onTrackballEvent(event);
	}
	*/

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mSetting = new Setting(this);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (mDataView!=null) {
					if (msg.what==UPDATE_ORIENTATION) {
						Orient orient = (Orient)msg.obj;
						mDataView.updateOrientation(orient.mAzimuth, orient.mPitch, orient.mRoll);
						long uptimeNew = System.currentTimeMillis();
						if (uptimeNew > (mUptime + 50)) {
							mUptime = uptimeNew;
							mDataView.invalidate();
						}
					} else if (msg.what==INVALIDATE) {
						mDataView.invalidate();
					}
				}
			}
		};
		bgEvent = new BackgroundEventHandler();
		mQueue = new LinkedList<Event>();
		mHotpepper = new HotPepperApi(getResources());
		mHotpepper.setOnRetrievedDataListener(new OnRetrievedDataListener() {
			public void onRetrievedDataListener(List<ARObject> items) {
				SdLog.put("onRetrievedDataListener size="+items.size());
				if (requestCount == 2) {
					mDataView.clearAllItems();
				}
				requestCount--;
//				Message msg = new Message();
//				msg.obj = (Object)items;
//				mDataHandler.sendMessage(msg);
				Message msg = new Message();
				msg.obj = (Object)items;
				msg.arg1 = 3;
				mDataHandler.sendMessage(msg);
			}
		});

		mUki2Server = new Uki2ServerApi(getResources());
		mUki2Server.setOnRetrievedDataListener(new OnRetrievedDataListener() {
			public void onRetrievedDataListener(List<ARObject> items) {
				if (items!=null) {
					SdLog.put("mUki2Server onRetrievedDataListener size="+items.size());
					if (requestCount == 2) {
						mDataView.clearAllItems();
					}
					Message msg = new Message();
					msg.obj = (Object)items;
					msg.arg1 = 0;
					mDataHandler.sendMessage(msg);
				}
				requestCount--;
			}
		});
		mUki2Server.setOnPostedDataListener(new OnPostedDataListener() {
			public void onPostedDataListener(PoiObject item, boolean success) {
				SdLog.put("onPostedDataListener success="+success);
				dismissWaitDialog();
				if (success) {
//					ARObject arObject = ((Uki2ServerApi)(mUki2Server)).getLastPostedItem();
					ARObject arObject = new ARObject();
					arObject.mApiAdapter = mUki2Server;
					arObject.mSummary = item.mSummary;
					arObject.mName = null;
					arObject.mObj = item;
					if (item.mMimeType.compareTo("emotion/joy")==0) {
						arObject.mType = 0;
					} else if (item.mMimeType.compareTo("emotion/angry")==0) {
						arObject.mType = 1;
					} else if (item.mMimeType.compareTo("emotion/sad")==0) {
						arObject.mType = 2;
					}
//					arObject.mUid = "dummy";
//					mDataView.addItem(arObject);
					if (mCommentDialog!=null) {
						mCommentDialog.dismiss();
					}
					if (mTouchDialog!=null) {
						mTouchDialog.dismiss();
					}
//					mMapItemOverlays.get(arObject.mType).addPoint(
//							arObject, new GeoPoint((int)(arObject.mObj.mLatitude*1e6), (int)(arObject.mObj.mLongitude*1e6)));
//					mMapView.postInvalidate();
					mHandler.sendEmptyMessage(INVALIDATE);
				} else {
					toast(R.string.toast_failed_to_post);
				}
			}
		});

		new Thread(new Runnable(){
			public void run() {
				String version = "1"+mUki2Server.getSuitableClientVersion();
				version = version.trim();
				try {
					PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
					String currentVersion = "1"+info.versionName;
					currentVersion = currentVersion.replace(".", "");
					SdLog.put("currentVersion="+currentVersion+" newVersion="+version);
					if (Integer.parseInt(version) > Integer.parseInt(currentVersion)) {
						mHandler.post(new Runnable() {
							public void run() {
								mNoticeDialog = new CustomDialog(FrameActivity.this);
								mNoticeDialog.setContentView(R.layout.notice);
								mNoticeDialog.setTitle(R.string.dialog_notice_title);
								mNoticeDialog.findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										Uri uri = Uri.parse("market://details?id=jp.bs.app.UkiukiView");
										Intent it = new Intent(Intent.ACTION_VIEW, uri);
										startActivity(it);
										mNoticeDialog.dismiss();
										finish();
									}
								});
								mNoticeDialog.show();
							}
						});
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
		initViews();
		mDataHandler = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				if (msg != null && msg.obj != null) {
					List<ARObject> items = null;
					items = (List<ARObject>)msg.obj;
					mDataView.addItems(items);
//					if (msg.arg1 != 0) {
//						return;
//					}

//					while ((mMapView.getOverlays().size()+items.size())>100) {
//						mMapView.getOverlays().remove(0);
//					}
					for (int i=0;i<items.size();i++) {
						GeoPoint pt = new GeoPoint((int)(items.get(i).mObj.mLatitude*1e6), (int)(items.get(i).mObj.mLongitude*1e6));
						mMapItemOverlays.get(items.get(i).mType+msg.arg1).addPoint(items.get(i), pt);
						if (!mMapView.getOverlays().contains(mMapItemOverlays.get(items.get(i).mType+msg.arg1))) {
							mMapView.getOverlays().add(mMapItemOverlays.get(items.get(i).mType+msg.arg1));
							mMapItemOverlays.get(items.get(i).mType+msg.arg1).setMapItemListener(new MapItemListener() {
								public boolean onTap(int index, ARObject item) {
									showTouchDialog(item);
									return true;
								}
							});
						}
					}
					mMapView.postInvalidate();
				}
			}
		};
	}

	private void initViews() {
		setContentView(R.layout.frame);
		mSplash = (ImageView)findViewById(R.id.imageSplash);
		mSplash.bringToFront();
		mHandler.post(new Runnable() {
			public void run() {
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);

				mCameraView = (CameraView)findViewById(R.id.viewCamera);
				mCompassView = (CompassView)findViewById(R.id.compass);
				mMapView = (ArMapView)findViewById(R.id.viewMap);
				mMapView.setClickable(true);
				mMapView.setEnabled(true);
				mMapView.setBuiltInZoomControls(false);
				mMapController = mMapView.getController();
				mMapView.setOnLocationChangedListener(new OnLocationChangedListener() {
					public void onLocationChangedListener(Location location) {
						if (locationListener!=null) {
							locationListener.onLocationChanged(location);
						}
					}
				});
				mMapView.setVisibility(View.GONE);
				mMapView.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							GeoPoint p = mMapView.getMapCenter();
							Location location = new Location("manual");
							location.setLatitude(p.getLatitudeE6()/1E6);
							location.setLongitude(p.getLongitudeE6()/1E6);
							updateLocation(location);
						}
						return false;
					}
				});
				createOverlay(R.drawable.joy);
				createOverlay(R.drawable.angry);
				createOverlay(R.drawable.sad);
				createOverlay(R.drawable.ic_store_1_izakaya);
				createOverlay(R.drawable.ic_store_2_dbar);
				createOverlay(R.drawable.ic_store_3_sousaku);
				createOverlay(R.drawable.ic_store_4_washoku);
				createOverlay(R.drawable.ic_store_5_youshoku);
				createOverlay(R.drawable.ic_store_6_itarian);
				createOverlay(R.drawable.ic_store_7_tyuuka);
				createOverlay(R.drawable.ic_store_8_yakiniku);
				createOverlay(R.drawable.ic_store_9_ajian);
				createOverlay(R.drawable.ic_store_10_kakukoku);
				createOverlay(R.drawable.ic_store_11_karaoke);
				createOverlay(R.drawable.ic_store_12_bar);
				createOverlay(R.drawable.ic_store_13_ramen);
				createOverlay(R.drawable.ic_store_14_okonomi);
				createOverlay(R.drawable.ic_store_15_kafe);
				createOverlay(R.drawable.ic_store_16_sonota);
				createOverlay(R.drawable.ic_store_1_izakaya);

				mMapView.getOverlays().add(new Overlay(){
					@Override
					public boolean onTap(GeoPoint p, MapView mapView) {
//						Location location = new Location("manual");
//						location.setLatitude(p.getLatitudeE6()/1E6);
//						location.setLongitude(p.getLongitudeE6()/1E6);
//						updateLocation(location);
						return super.onTap(p, mapView);
					}
				});
				mSearchPanel = (TransparentPanel)findViewById(R.id.viewTransparent);
				mSearchPanel.setPadding(10, 6, 4, 4);
				mBtnSearch = (ImageButton)findViewById(R.id.btnSearch);
				mBtnSearch.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						v.setEnabled(false);
						InputMethodManager inputMethodManager =
							(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
						mSearchPanel.setVisibility(View.GONE);
						search();
					}
				});
				EditText textSearch = (EditText)findViewById(R.id.editSearch);
				textSearch.setPadding(8, 8, 4, 8);
				textSearch.setFreezesText(true);
				textSearch.setOnKeyListener(new OnKeyListener(){
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if (event.getAction() == KeyEvent.ACTION_DOWN
							&& keyCode == KeyEvent.KEYCODE_ENTER) {
							InputMethodManager inputMethodManager =
								(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
							search();
						}
						return false;
					}
				});
				mSearchPanel.setVisibility(View.GONE);

				mDataView = (DataView)findViewById(R.id.viewData);
				mDataView.setRange(mRange);
				mDataView.setViewSize(mWidth, mHeight);
				mDataView.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						if (mDataView.mItems.size()<=0) {
							return false;
						}
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							boolean find = false;
							int i = 0;
							float x = event.getX();
							float y = event.getY();
							SdLog.put("touch x="+x+" y="+y);
							ARObject item = null;
							for (i=(mDataView.mItems.size()-1);i>=0;i--) {
								item = mDataView.mItems.get(i);
								SdLog.put("i="+i+" item.mX="+item.mX+" item.mY="+item.mY);
								if (item.mX > -30 && item.mY > 0 && item.mFrameRect!=null) {
									SdLog.put("item left="+mDataView.mItems.get(i).mFrameRect.left+" top="+mDataView.mItems.get(i).mFrameRect.top+" right"+mDataView.mItems.get(i).mFrameRect.right+" bottom"+mDataView.mItems.get(i).mFrameRect.bottom);
									if ((((item.mFrameRect.top-5) < y) && ((item.mFrameRect.bottom+5) > y))
									&& (((item.mFrameRect.left-5) < x) && ((item.mFrameRect.right+5) > x))
									) {
										find = true;
										break;
									}
								}
							}
							if (find) {
								SdLog.put("onTouchListener name="+mDataView.mItems.get(i).mName);
//								if (item.mSummary!=null && item.mSummary.length()>0) {
									showTouchDialog(item);
//								} else {
//									showReplyDialog(item);
//								}
							}
						}
						return false;
					}
				});
				mJoyButton = (ImageButton)findViewById(R.id.btnJoy);
				mJoyButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						postEmotion("joy");
					}
				});
				mAngryButton = (ImageButton)findViewById(R.id.btnAngry);
				mAngryButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						postEmotion("angry");
					}
				});
				mSadButton = (ImageButton)findViewById(R.id.btnSad);
				mSadButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						postEmotion("sad");
					}
				});
				mJoyButton.setVisibility(View.GONE);
				mAngryButton.setVisibility(View.GONE);
				mSadButton.setVisibility(View.GONE);
				mToggleButton = (ImageButton)findViewById(R.id.btnToggle);
				mToggleButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (mIsMapMode) {
							mIsMapMode = false;
							mCameraView.setVisibility(View.VISIBLE);
							mDataView.setVisibility(View.VISIBLE);
							mMapView.setVisibility(View.GONE);
							mPlusButton.setVisibility(View.GONE);
							mMinusButton.setVisibility(View.GONE);
							mToggleButton.setImageResource(R.drawable.ic_menu_mapmode);
						} else {
							mIsMapMode = true;
							mMapView.setVisibility(View.VISIBLE);
							mDataView.setVisibility(View.GONE);
							mCameraView.setVisibility(View.GONE);
							mPlusButton.setVisibility(View.VISIBLE);
							mMinusButton.setVisibility(View.VISIBLE);
							mToggleButton.setImageResource(R.drawable.ic_menu_camera);
						}
					}
				});
				mMyLocationButton = (ImageButton)findViewById(R.id.btnMyLocation);
				mMyLocationButton.setImageResource(R.drawable.ic_menu_mylocation_mode);
				mMyLocationButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (mMapView.isMyLocationOn()) {
							mMapView.disableMyLocation();
							mMyLocationButton.setImageResource(R.drawable.ic_menu_mylocation_mode_off);
						} else {
							mMapView.enableMyLocation();
							mMyLocationButton.setImageResource(R.drawable.ic_menu_mylocation_mode);
						}
					}
				});
				mPlayPauseButton = (ImageButton)findViewById(R.id.btnPlayPause);
				mPlayPauseButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (mIsPause) {
							mPlayPauseButton.setImageResource(R.drawable.pause);
						} else {
							mPlayPauseButton.setImageResource(R.drawable.play);
						}
						mIsPause = !mIsPause;
					}
				});
				mPlusButton = (ImageButton)findViewById(R.id.btnZoomPlus);
				mPlusButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mMapController.zoomIn();
						GeoPoint pt = mMapView.getProjection().fromPixels(0, 0);
						GeoPoint pt2 = mMapView.getProjection().fromPixels(0, mHeight);
						mRange = (int)getDistance(pt.getLatitudeE6()/1E6, pt.getLongitudeE6()/1E6
								, pt2.getLatitudeE6()/1E6, pt2.getLongitudeE6()/1E6);
						SdLog.put("zoomIn mRange="+mRange);
					}
				});
				mMinusButton = (ImageButton)findViewById(R.id.btnZoomMinus);
				mMinusButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mMapController.zoomOut();
						GeoPoint pt = mMapView.getProjection().fromPixels(0, 0);
						GeoPoint pt2 = mMapView.getProjection().fromPixels(0, mHeight);
						mRange = (int)getDistance(pt.getLatitudeE6()/1E6, pt.getLongitudeE6()/1E6
								, pt2.getLatitudeE6()/1E6, pt2.getLongitudeE6()/1E6);
						SdLog.put("zoomOut mRange="+mRange);
					}
				});
				mPlusButton.setVisibility(View.GONE);
				mMinusButton.setVisibility(View.GONE);
				ImageButton setting = (ImageButton)findViewById(R.id.btnSetting);
				setting.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
//						startActivity(new Intent(FrameActivity.this, SettingActivity.class));
					}
				});
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		mIsRestart = true;
	}


	@Override
	protected void onStart() {
		super.onStart();
		if (!mIsRestart || (mIsRestart && mMapView!=null && mMapView.isMyLocationOn())) {
			mHandler.postDelayed(new Runnable() {
				public void run() {
					mMapView.enableMyLocation();
				}
			},500);
			mIsRestart = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (sensorManager!=null && sensorEventListener!=null) {
			sensorManager.unregisterListener(sensorEventListener);
		}
		if (mMapView!=null && mMapView.isMyLocationOn()) {
			mMapView.disableMyLocation();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mMapView!=null) {
			outState.putBoolean("mylocation", mMapView.isMyLocationOn());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		boolean isMylocationOn = savedInstanceState.getBoolean("mylocation");
		if (isMylocationOn && mMapView != null) {
			mMapView.enableMyLocation();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!(mSetting.get("account").length()>0)) {
			mAccountName = mSetting.get("nickname");
			mSetting.set("account", mAccountName);
		} else {
			mAccountName = mSetting.get("account");
		}
		mPassword = mSetting.get("password");
		mIsRegister = mSetting.getBoolean("register");
		SdLog.put("login start 00");
		if (mIsRegister) {
			SdLog.put("login start 0");
			new Thread(new Runnable() {
				public void run() {
					showWaitDialog(R.string.dialog_login);
					SdLog.put("login start");
					mIsLogin = mUki2Server.login(mAccountName, mPassword);
					SdLog.put("login end="+mIsLogin);
					if (!mIsLogin) {
						toast(R.string.toast_failed_to_login);
					}
					dismissWaitDialog();
				}
			}).start();
		}
/*
		if (locationManager == null) {
			locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		}
*/
		if (sensorManager == null) {
			sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		}
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					public void run() {
						mSplash.setVisibility(View.GONE);
						sensorRequest();
					}
				});
			}
		}, 3 * 1000);
	}

	Timer mTimer = new Timer();

	private void sensorRequest() {
		locationListener = new LocationListener() {
			float accuracy = 5000;
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
			public void onProviderEnabled(String provider) {
			}
			public void onProviderDisabled(String provider) {
			}
			public void onLocationChanged(Location location) {
				SdLog.put("onLocationChanged");
				if (location.getAccuracy() < accuracy || (mLocation!=null && mLocation.distanceTo(location)>30) ) {
					accuracy = location.getAccuracy();
					if (!mIsPause) {
						updateLocation(location);
					}
					SdLog.put("new location lat="+location.getLatitude()+" lon="+location.getLongitude()+"********");
				}
			}
		};
		List<Sensor> sensors = this.sensorManager.getSensorList(Sensor.TYPE_ORIENTATION | Sensor.TYPE_ACCELEROMETER);

		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			sensorEventListener = new SensorEventListener() {
				int median = 5;
				float[] xValue = new float[median], yValue = new float[median];

				public void onSensorChanged(SensorEvent event) {
					double roll;

					switch(event.sensor.getType()){
					case Sensor.TYPE_ACCELEROMETER:
						/*
						if (event.values[SensorManager.DATA_Z] > 0) {
							zFlg = true;
						} else {
							zFlg = false;
						}
						*/
						break;
					case Sensor.TYPE_ORIENTATION:
						if (mIsPause) {
							return;
						}
						float xRot = event.values[SensorManager.DATA_X];

						float yRot = 60 - event.values[SensorManager.DATA_Z];
						if(event.values[SensorManager.DATA_Y] < -5){
							yRot = 0;
						}
						if(yRot < - 20){
							yRot = -20;
						}

						for(int i=0; i<(median-1); i++){
							xValue[i]=xValue[i+1];
							yValue[i]=yValue[i+1];
						}
						xRot += 90;
						if (mGeomagnetic != null) {
							xRot = xRot + mGeomagnetic.getDeclination();
						}
						if (xRot > 360 || xRot < 0) {
							xRot = (xRot + 360) % 360;
						}
						xValue[median-1]=xRot;
						yValue[median-1]=yRot;
						float[] xBuf = xValue.clone();
						float[] yBuf = yValue.clone();
						Arrays.sort(xBuf);
						Arrays.sort(yBuf);

						xRot = xBuf[(median-1) / 2];
						yRot = yBuf[(median-1) / 2];

						roll = event.values[SensorManager.DATA_Z];
						mCompassView.setAzimuth(xRot);
						mCompassView.postInvalidate();
						SdLog.put("******** xRot="+xRot);
//						SdLog.put("******** xRot="+xRot+" yRot="+yRot+" DATA_Z="+event.values[SensorManager.DATA_Z]+" DATA_Y="+event.values[SensorManager.DATA_Y]);
						if (mLocation!=null) {
							Message msg = new Message();
							msg.obj = new Orient(xRot, yRot, roll);
							msg.what = UPDATE_ORIENTATION;
							mHandler.sendMessage(msg);
						}
						break;
					}
				}
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
				}
			};
			sensorManager.registerListener(sensorEventListener,
				sensor,
				SensorManager.SENSOR_DELAY_UI);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mSearchPanel != null && mSearchPanel.getVisibility() == View.VISIBLE) {
				mSearchPanel.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onSearchRequested() {
		mSearchPanel.setVisibility(ViewGroup.VISIBLE);
		return true;
	}

	private void search() {
		String query;
		query = ((EditText)mSearchPanel.findViewById(R.id.editSearch)).getText().toString();
		if (query.length()<=0) {
			mBtnSearch.setEnabled(true);
			return;
		}
		try {
			Geocoder geocoder = new Geocoder(this);
			mAddress = geocoder.getFromLocationName(query, 20);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			mBtnSearch.setEnabled(true);
			return;
		}
		mSearchPanel.setVisibility(View.GONE);
		if(mAddress == null || mAddress.isEmpty()) {
			Toast.makeText(this, getString(R.string.toast_no_address_match, query), Toast.LENGTH_LONG).show();
		} else {
			if (mAddress.size()>1) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Search Result");
				CharSequence[] charaItem = null;
				ArrayList<CharSequence> charaItemArrayList = new ArrayList<CharSequence>();
				for (Address addr : mAddress) {
					String desc = new String();
					desc = addr.getFeatureName();
					for (int j=0; addr.getAddressLine(j)!=null; j++) {
						desc += "\n" + addr.getAddressLine(j);
					}
					charaItemArrayList.add(desc);
				}
				charaItem = charaItemArrayList.toArray(new CharSequence[charaItemArrayList.size()]);
				builder.setSingleChoiceItems(charaItem, 0, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						Location location = new Location("manual");
						location.setLatitude(mAddress.get(which).getLatitude());
						location.setLongitude(mAddress.get(which).getLongitude());
						dialog.dismiss();
						mMapView.disableMyLocation();
						mMyLocationButton.setImageResource(R.drawable.ic_menu_mylocation_mode_off);
						updateLocation(location);
					}
				});
				builder.setNegativeButton("Cancel", null);
				builder.show();
			} else {
				Location location = new Location("manual");
				location.setLatitude(mAddress.get(0).getLatitude());
				location.setLongitude(mAddress.get(0).getLongitude());
				mMapView.disableMyLocation();
				mMyLocationButton.setImageResource(R.drawable.ic_menu_mylocation_mode_off);
				updateLocation(location);
			}
		}
		mBtnSearch.setEnabled(true);
	}

	public Double double2dms(Double d){
		int deg,min,sec;
		deg = (int)Math.floor(d);
		min = (int)Math.floor((d - deg) * 60);
		sec = (int)Math.floor(((d - deg) * 60 - min) *60);
		return (Double.valueOf(String.valueOf(deg) + "." + String.valueOf(min) + String.valueOf(sec)));
	}

	private void updateLocation(Location location) {
		if (mLocation==null) {
			mJoyButton.setVisibility(View.VISIBLE);
			mAngryButton.setVisibility(View.VISIBLE);
			mSadButton.setVisibility(View.VISIBLE);
		}
		mLocation = location;
		mDataView.updateLocation(location);
		float latitude = new Double(location.getLatitude()).floatValue();
		float longitude = new Double(location.getLongitude()).floatValue();
		float altitude = new Double(location.getAltitude()).floatValue();
		mGeomagnetic = new GeomagneticField(latitude, longitude, altitude, new Date().getTime());
		changeMapLocation(location);
		updatePoiData(location);
	}
	private synchronized void updatePoiData(Location location) {
		requestCount = 2;
		new Thread(new Runnable() {
			public void run() {
				mHotpepper.updateData(mLocation, mRange);
				mUki2Server.updateData(mLocation, mRange);
			}
		}).start();
	}

	private void preparePostPoiComment(double latitude,double longitude, String parentid, String mimeType, String content, String comment) {
		SdLog.put("preparePostPoiComment");
		PoiObject obj = new PoiObject(
				latitude
				, longitude
				, comment
				, mimeType
				, content
				, parentid
				, Locale.getDefault().getLanguage());
		Event event = new Event(Event.MESSAGE_POST_POI, obj, mUki2Server);
		mQueue.offer(event);
	}

//	private void preparePostReplyPoiComment(double latitude,double longitude, String parentid, String mimeType, String content, String comment) {
//		SdLog.put("preparePostReplyPoiComment");
//		PoiObject obj = new PoiObject(
//				latitude
//				, longitude
//				, comment
//				, mimeType
//				, content
//				, parentid
//				, Locale.getDefault().getLanguage());
//		Event event = new Event(Event.MESSAGE_POST_POI_WITH_PARENT, obj, mUki2Server);
//		mQueue.offer(event);
//	}

	private void postQueuedPoiComment(String comment) {
		Event event = mQueue.poll();
//		Event event = mQueue.peek();
		if (event!=null) {
			event.mPoiObject.mSummary = comment;
			bgEvent.addEvent(event);
		}
	}


	private void changeMapLocation(Location location) {
		mMapController.animateTo(
				new GeoPoint((int)(location.getLatitude()*1E6)
						,(int)(location.getLongitude()*1E6))
				);
		mLocation = location;
	}

	private void updateOkButton(int index, String text) {
		String name = ((EditText)mAccountInputDialog.findViewById(R.id.editName)).getText().toString();
		String pass = ((EditText)mAccountInputDialog.findViewById(R.id.editPassword)).getText().toString();
		SdLog.put("updateOkButton name=["+name+"] "+"pass=["+"] index="+index+" text=["+text+"]");
		if ( ((pass != null && pass.length() > 0) || (index == 1 && text.length() > 0))
		&& ((name != null && name.length() > 0) || (index == 0 && text.length() > 0))) {
			mAccountInputDialog.setEnabled(R.id.btnOk, true);
		} else {
			mAccountInputDialog.setEnabled(R.id.btnOk, false);
		}
	}

	private void postEmotion(String type) {
		mPostEmotionType = type;
		if (!mIsRegister) {
			mRegisterLoginDialog = new CustomDialog(FrameActivity.this);
			mRegisterLoginDialog.setContentView(R.layout.create_or_login);
			mRegisterLoginDialog.setTitle(R.string.title_create_or_login);
			mRegisterLoginDialog.findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					isCreate = true;
					mRegisterLoginDialog.dismiss();
					showAccountInputDialog();
				}
			});
			mRegisterLoginDialog.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					isCreate = false;
					mRegisterLoginDialog.dismiss();
					showAccountInputDialog();
				}
			});
			mRegisterLoginDialog.show();
			return;
		} else if (mLocation != null) {
			if (!mIsLogin) {
				new Thread(new Runnable() {
					public void run() {
						boolean isOk = mUki2Server.login(mAccountName, mPassword);
						if (isOk) {
							mIsLogin = true;
							showCommentDialog(mPostEmotionType);
						} else {
							mIsRegister = false;
							mIsLogin = false;
							toast(R.string.toast_failed_to_login);
						}
					}
				}).start();
			} else {
				showCommentDialog(mPostEmotionType);
			}
		}
	}

	private void showAccountInputDialogFromOtherThread() {
		mHandler.post(new Runnable() {
			public void run() {
				showAccountInputDialog();
			}
		});
	}

	private void showAccountInputDialog() {
		mAccountInputDialog = new CustomDialog(FrameActivity.this);
		mAccountInputDialog.setContentView(R.layout.account_edit);
		if (isCreate) {
			mAccountInputDialog.setTitle(R.string.register_account);
		} else {
			mAccountInputDialog.setTitle(R.string.login_account);
		}
		mAccountInputDialog.setEnabled(R.id.btnOk, false);
		EditText edit = (EditText)mAccountInputDialog.findViewById(R.id.editName);

		TextInputListener filter = new TextInputListener();
		filter.setOnInputListener(new OnInputListener() {
			public void onInputListner(CharSequence source) {
				updateOkButton(0, source.toString());
			}
		});
		edit.setFilters(new InputFilter[]{filter});
		edit = (EditText)mAccountInputDialog.findViewById(R.id.editPassword);
		filter = new TextInputListener();
		filter.setOnInputListener(new OnInputListener() {
			public void onInputListner(CharSequence source) {
				updateOkButton(1, source.toString());
			}
		});
		edit.setFilters(new InputFilter[]{filter});
		mAccountInputDialog.findViewById(R.id.btnOk).setOnClickListener(
			new OnClickListener() {
				public void onClick(View v) {
					String account = ((EditText)mAccountInputDialog.findViewById(R.id.editName)).getText().toString();
					if (account.length() < account.getBytes().length) {
						mSetting.set("account", "");
						mSetting.set("password", "");
						toast(R.string.account_notice);
						return;
					}
					new Thread(new Runnable(){
						public void run() {
							boolean isOk = false;
							String userid = null;
							String account = ((EditText)mAccountInputDialog.findViewById(R.id.editName)).getText().toString();
							String password = ((EditText)mAccountInputDialog.findViewById(R.id.editPassword)).getText().toString();
							if (isCreate) {
								showWaitDialog(R.string.dialog_register);
								userid = mUki2Server.registerAccount(account, password);
								dismissWaitDialog();
								if (userid!=null) {
									mIsRegister = true;
									mSetting.setBoolean("register", true);
								} else {
									if (((Uki2ServerApi)mUki2Server).mResponseCode==Uki2ServerApi.RESPONSE_DUPLICATE_ACCOUNT) {
										toast(R.string.toast_failed_to_register_duplicated);
									} else {
										toast(R.string.toast_failed_to_register);
									}
									showAccountInputDialogFromOtherThread();
									return;
								}
								showWaitDialog(R.string.dialog_login);
								isOk = mUki2Server.login(account, password);
								SdLog.put("login ret="+isOk);
								dismissWaitDialog();
							} else {
								showWaitDialog(R.string.dialog_login);
								isOk = mUki2Server.login(account, password);
								SdLog.put("login ret="+isOk);
								dismissWaitDialog();
							}
							if (isOk) {
								mIsLogin = true;
								mIsRegister = true;
								mSetting.set("account", account);
								mSetting.set("password", password);
								mSetting.setBoolean("register", true);
								showCommentDialog(mPostEmotionType);
							} else {
								mIsLogin = false;
								mIsRegister = false;
								toast(R.string.toast_failed_to_login);
							}
						}
					}).start();
					mAccountInputDialog.dismiss();
				}
			}
		);
		mAccountInputDialog.findViewById(R.id.btnCancel).setOnClickListener(
			new OnClickListener() {
				public void onClick(View v) {
					mAccountInputDialog.dismiss();
				}
			}
		);
		mAccountInputDialog.show();
	}

	private void showWaitDialog(int resId) {
		showWaitDialog(getString(resId));
	}

	private void showWaitDialog(String text) {
		mDialogText = text;
		mHandler.post(new Runnable() {
			public void run() {
				mWaitDialog = new ProgressDialog(FrameActivity.this);
				mWaitDialog.setMessage(mDialogText);
				mWaitDialog.setIndeterminate(true);
				mWaitDialog.setCancelable(true);
				mWaitDialog.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						if (mUki2Server != null) {
							mUki2Server.cancel();
						}
					}
				});
				mWaitDialog.show();
			}
		});
	}

	private void dismissWaitDialog() {
		mHandler.post(new Runnable() {
			public void run() {
				mWaitDialog.dismiss();
			}
		});
	}

	private void showTouchDialog(ARObject item) {
		mTouchDialog = new CustomDialog(this);
		SdLog.put("item.mObj.mMimeType=["+item.mObj.mMimeType+"]");
		if (item.mObj.mMimeType.equals("text/html")) {
			mTouchDialog.setContentView(R.layout.storedata);
			mTouchDialog.setTitle(item.mName);
			WebView webview = (WebView)mTouchDialog.findViewById(R.id.webview);
			webview.getSettings().setJavaScriptEnabled(false);
			webview.loadData(item.mObj.mContent, item.mObj.mMimeType, "utf-8");
			mTouchDialog.findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mTouchDialog.dismiss();
				}
			});
		} else if (item.mObj.mMimeType.startsWith("emotion/",0)) {
			/*
			mUki2Server.getNicknameAsync(item.mOwnerId, new OnRetrievedNicknameListener() {
				public void onRetrievedNicknameListener(String userid, String nickname) {
					if (mTouchDialog!=null && nickname!=null) {
						mNicknameQueue.offer(nickname);
						//TODO:cache nickname
						mHandler.post(new Runnable() {
							public void run() {
								String nickname = mNicknameQueue.poll();
								if (mTouchDialog!=null && nickname!=null) {
									TextView text = (TextView)mTouchDialog.findViewById(R.id.textNickname);
									if (text!=null) {
										text.setText(nickname);
									}
								}
							}
						});
					}
				}
			});
			*/
			mTouchDialog.setContentView(R.layout.emotion_text);
			TextView text = (TextView)mTouchDialog.findViewById(R.id.textNickname);
			text.setText(item.mOwnerName);
			ImageView image = (ImageView)mTouchDialog.findViewById(R.id.imageEmotion);
			image.setImageResource(item.mApiAdapter.getIconId(item.mType));
			text = (TextView)mTouchDialog.findViewById(R.id.textDescription);
			text.setText(item.mSummary);
			text = (TextView)mTouchDialog.findViewById(R.id.textDatetime);
			Calendar cal = Calendar.getInstance(TimeZone.getDefault());
			cal.setTime(item.mCreatedDateTime);
			Date date = cal.getTime();
			date.setTime(date.getTime()+TimeZone.getDefault().getRawOffset());
			String textDateTime = String.format("%d/%02d/%02d %02d:%02d"
					, date.getYear()+1900
					, date.getMonth()+1
					, date.getDate()
					, date.getHours()
					, date.getMinutes()
					);
			text.setText(textDateTime);
			mSelectedObject = item;
			Button button = (Button)mTouchDialog.findViewById(R.id.btnOk);
			if (item.mOwnerId.equals(((Uki2ServerApi)mUki2Server).getUserId())) {
				button.setText(R.string.edit_text_delete);
				button.setEnabled(true);
				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						new Thread(new Runnable() {
							public void run() {
								showWaitDialog(R.string.dialog_deleting);
								SdLog.put("delete start");
								mUki2Server.deleteObjectAsync(mSelectedObject.mUid,
									new OnDeletedListener() {
										public void onDeletedListener(boolean deleted) {
											SdLog.put("onDeletedListener deleted="+deleted);
											if (deleted) {
												mMapItemOverlays.get(mSelectedObject.mType).removePoint(mSelectedObject);
												mMapView.postInvalidate();
												mSelectedObject.setDisplay(ScreenItem.OUT);
												mTouchDialog.dismiss();
											} else {
												toast(R.string.toast_failed_to_delete);
											}
											dismissWaitDialog();
										}
									}
								);
							}
						}).start();
					}
				});
			} else {
				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						showReplyDialog();
					}
				});
			}
			mTouchDialog.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mTouchDialog.dismiss();
				}
			});
		}
		mTouchDialog.show();
	}

	private void showCommentDialog(String type) {
		mHandler.post(new Runnable() {
			public void run() {
				mCommentDialog = new CustomDialog(FrameActivity.this);
				mCommentDialog.setContentView(R.layout.edit_text);
				preparePostPoiComment(mLocation.getLatitude()
						, mLocation.getLongitude()
						, null, "emotion", mPostEmotionType, null);
				ImageView image = (ImageView)mCommentDialog.findViewById(R.id.imageEmotion);
				image.setImageResource(getEmotionResourceId(mPostEmotionType));
				TextView text = (TextView)mCommentDialog.findViewById(R.id.textNickname);
				text.setText(mAccountName);

				Button button = (Button)mCommentDialog.findViewById(R.id.btnOk);
				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						EditText text = (EditText)mCommentDialog.findViewById(R.id.textDescription);
						postQueuedPoiComment(text.getText().toString());
						showWaitDialog(R.string.dialog_post);
					}
				});
				button = (Button)mCommentDialog.findViewById(R.id.btnCancel);
				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mCommentDialog.dismiss();
					}
				});
				mCommentDialog.show();
			}
		});
	}

	private void showReplyDialog() {
		Event event = mQueue.peek();
		if (event!=null) {
			mCommentDialog = new CustomDialog(FrameActivity.this);
			mCommentDialog.setContentView(R.layout.edit_reply_text);
			ImageButton image = (ImageButton)mCommentDialog.findViewById(R.id.imageButtonEmotion);
			event.mPoiObject.mContent = "joy";
			image.setImageResource(getEmotionResourceId(event.mPoiObject.mContent));
			TextView text = (TextView)mCommentDialog.findViewById(R.id.textNickname);
			text.setText(mAccountName);
			Button button = (Button)mCommentDialog.findViewById(R.id.btnOk);
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					EditText text = (EditText)mCommentDialog.findViewById(R.id.textDescription);
					postQueuedPoiComment(text.getText().toString());
					showWaitDialog(R.string.dialog_post);
					mCommentDialog.dismiss();
				}
			});
			button = (Button)mCommentDialog.findViewById(R.id.btnCancel);
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mCommentDialog.dismiss();
				}
			});
			mCommentDialog.show();
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMapView!=null) {
			mMapView.disableMyLocation();
		}
		if (locationManager!=null && locationListener!=null) {
			locationManager.removeUpdates(locationListener);
		}
		if (sensorManager!=null && sensorEventListener!=null) {
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void createOverlay(int iconId) {
		Drawable icon = getResources().getDrawable(iconId);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
		if (mMapItemOverlays==null) {
			mMapItemOverlays = new ArrayList<MapOverlayItem>();
		}
		MapOverlayItem overlayItem = new MapOverlayItem(icon);
		mMapItemOverlays.add(overlayItem);
		overlayItem.setMapItemListener(new MapItemListener() {
			public boolean onTap(int index, ARObject item) {
				return false;
			}
		});
	}

	private double getDistance(double lat1, double lon1, double lat2, double lon2) {
		double retDistance = 0;
		Location a = new Location("manual");
		a.setLatitude(lat1);
		a.setLongitude(lon1);
		Location b = new Location("manual");
		b.setLatitude(lat2);
		b.setLongitude(lon2);
		retDistance = a.distanceTo(b);
		return retDistance;
	}

	private int getEmotionResourceId(String content) {
		int ret = content.compareTo("joy")==0?R.drawable.joy:content.compareTo("angry")==0?R.drawable.angry:content.compareTo("sad")==0?R.drawable.sad:R.drawable.joy;
		SdLog.put("content="+content+"ret="+ret);
		return ret;
	}

	private void toast(int resId) {
		toast(getString(resId));
	}

	private void toast(String notice) {
		mToast = notice;
		mHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(FrameActivity.this, mToast, Toast.LENGTH_LONG).show();
			}
		});
	}
}