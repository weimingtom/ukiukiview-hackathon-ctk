package net.cattaka.hk.uki2win;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

import net.cattaka.hk.uki2win.R;
import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.cloud.ServiceSearchCondition;
import net.cattaka.hk.uki2win.cloud.UkiukiCloudClient;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiDeleteUkiukiBallTask;
import net.cattaka.hk.uki2win.cloud.UkiukiGetContentsTask;
import net.cattaka.hk.uki2win.cloud.UkiukiGetContentsUsageTask;
import net.cattaka.hk.uki2win.cloud.UkiukiGetServiceDataTask;
import net.cattaka.hk.uki2win.cloud.UkiukiLoginTask;
import net.cattaka.hk.uki2win.cloud.UkiukiServiceGenreInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiServiceInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiSubmitUkiukiBallTask;
import net.cattaka.hk.uki2win.gl.SceneRenderer;
import net.cattaka.hk.uki2win.gl.SceneRendererHandle;
import net.cattaka.hk.uki2win.gl.SceneRenderer.SceneRendererProcess;
import net.cattaka.hk.uki2win.math.CtkMath;
import net.cattaka.hk.uki2win.math.Hubeny;
import net.cattaka.hk.uki2win.net.ImageCache;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.scene.SceneCameraHandler;
import net.cattaka.hk.uki2win.scene.SceneHandler;
import net.cattaka.hk.uki2win.scene.SceneObject;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;
import net.cattaka.hk.uki2win.setting.BasicSetting;
import net.cattaka.hk.uki2win.setting.ServiceInfoSetting;
import net.cattaka.hk.uki2win.setting.UserInterfaceState;
import net.cattaka.hk.uki2win.utils.ActivityUtil;
import net.cattaka.hk.uki2win.utils.CtkGL;
import net.cattaka.hk.uki2win.utils.SceneTouchHandler;
import net.cattaka.hk.uki2win.utils.SceneTouchHandler.SingleTouchMode;
import net.cattaka.hk.uki2win.view.ConfirmDialog;
import net.cattaka.hk.uki2win.view.ConfirmForDeleteDialog;
import net.cattaka.hk.uki2win.view.ContentDialog;
import net.cattaka.hk.uki2win.view.CtkDialogInterface;
import net.cattaka.hk.uki2win.view.DropSceneObjectDialog;
import net.cattaka.hk.uki2win.view.SceneObjectListDialog;
import net.cattaka.hk.uki2win.view.ServiceSearchConditionDialog;
import net.cattaka.hk.uki2win.view.UkiukiCommentsListDialog;
import net.cattaka.hk.uki2win.view.UkiukiServiceInfoArrayAdapter;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UkiukiWindow extends MapActivity
	implements SceneTouchHandler.OnPickSceneObjectListener,
		SceneObjectListDialog.OnSceneObjectSelectListener,
		DropSceneObjectDialog.OnDropSceneObjectListener,
		ConfirmDialog.OnConfirmListener,
		ServiceSearchConditionDialog.OnServiceSearchConditionListener,
		View.OnClickListener,
		WebCacheUtil.WebCacheListener,
		UkiukiCommentsListDialog.OnUkiukiCommentsListener
{
	public static final int DIALOG_SCENE_OBJECT_LIST = 1;
	public static final int DIALOG_ABOUT = 2;
	public static final int DIALOG_DROP_SCENE_OBJECT = 3;
	public static final int DIALOG_CONFIRM_DELETE_SCENE_OBJECT = 4;
	public static final int DIALOG_CONTENT_DIALOG = 5;
	public static final int DIALOG_UKIUKI_COMMENTS_DIALOG = 6;
	public static final int DIALOG_SERVICE_SEARCH_CONDITION_DIALOG = 7;
	
	public class OnDismissListenerEx implements DialogInterface.OnDismissListener {
		private CtkDialogInterface ctkDialogInterface;
		private int id;

		public OnDismissListenerEx(int id) {
			super();
			this.id = id;
		}
		
		public OnDismissListenerEx(int id, CtkDialogInterface ctkDialogInterface) {
			super();
			this.id = id;
			this.ctkDialogInterface = ctkDialogInterface;
		}
		
		public void onDismiss(DialogInterface dialog) {
			if (ctkDialogInterface != null) {
				ctkDialogInterface.onDismiss(id, dialog);
			}
			sceneRendererHandle.setSleepRenderer(false);
		}
	}
	
	class OnGetContentsUsageListenerEx implements UkiukiGetContentsUsageTask.OnGetContentsUsageListener {
		private int retryCount;
		public OnGetContentsUsageListenerEx(int retryCount) {
			this.retryCount = retryCount;
		}
		public void onGetContentsUsageInfo(UkiukiContentsUsageInfo ucuInfo) {
			if (ucuInfo != null) {
				((ViewGroup) findViewById(R.id.EssentialStatusLayout)).setVisibility(View.INVISIBLE);
				requestContents(false);
				requestServiceData(false, false, userInterfaceState.getUkiukiServiceInfo(), userInterfaceState.getServiceSearchCondition());
				if (basicSetting.getAccount() != null && basicSetting.getAccount().length() > 0) {
					requestLogin();
				}
			} else if (this.retryCount > 0) {
				this.retryCount--;
				getContentsUsageTask = ukiukiConnectionUtil.getContentsUsage(this);
			} else {
				Toast.makeText(UkiukiWindow.this, R.string.msg_downloading_essential_info_failed, Toast.LENGTH_LONG);
			}
		}
		public void onCancel() {
			// none
		}
	};
	
	private SensorManager sensorManager;
	private Sensor magneticSensor;
	private Sensor accelSensor;
	private PowerManager.WakeLock wakeLock;

	private MapView mapView;
	private SceneRendererHandle sceneRendererHandle;
	private WebCacheUtil webCacheUtil;
	
	private SceneObjectListDialog sceneObjectListDialog;
	private DropSceneObjectDialog dropSceneObjectDialog;
	private ConfirmForDeleteDialog confirmDeleteDialog;
	private ContentDialog contentDialog;
	private UkiukiCommentsListDialog ukiukiCommentsListDialog;
	private ServiceSearchConditionDialog serviceSearchConditionDialog;
	
	// 設定関連
	// TODO ===============
	private UserInterfaceState userInterfaceState = new UserInterfaceState();
	private float locationUpdateDistance = 0;
	private GeoPoint lastMarkerGeoPoint;
	private GeoPoint currentGeoPoint;
	private GeoPoint lastGetServiceDataGeoPoint;
	private GeoPoint lastGetContentsGeoPoint;
	// TODO ===============
	
	private BasicSetting basicSetting;
	private ServiceInfoSetting serviceInfoSetting;

	private Bitmap mapBitmap;
	
	private SceneObjectInfo selectedSceneObjectInfo;
	
	private UkiukiCloudClient ukiukiConnectionUtil;
	private UkiukiGetContentsUsageTask getContentsUsageTask;
	private UkiukiGetServiceDataTask getServiceDataTask;
	private UkiukiGetContentsTask getContentsTask;
	private UkiukiLoginTask loginTask;
	private UkiukiSubmitUkiukiBallTask submitUkiukiBallTask;
	private UkiukiDeleteUkiukiBallTask deleteUkiukiBallTask;
	
	// UI State
	private SceneTouchHandler sceneTouchHandler;
	
	private SensorEventListenerImpl sensorEventListener = new SensorEventListenerImpl();
	class SensorEventListenerImpl implements SensorEventListener {
		private float[][] orient;
		private int currentOrient;
		private float[][] accel;
		private int currentAccel;
		
		public void onSensorChanged(SensorEvent event) {
			float[] ev = { event.values[0], event.values[1], event.values[2] };
			boolean updateFlag = false;
			
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				if (accel == null) {
					currentAccel = 0;
					accel = new float[20][3];
					for (int i = 0; i < accel.length; i++) {
						accel[i][0] = ev[0];
						accel[i][1] = ev[1];
						accel[i][2] = ev[2];
					}
				} else {
					currentAccel++;
					if (currentAccel >= accel.length) {
						currentAccel = 0;
					}
					accel[currentAccel][0] = ev[0];
					accel[currentAccel][1] = ev[1];
					accel[currentAccel][2] = ev[2];
				}
			} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				if (orient == null) {
					currentOrient = 0;
					orient = new float[20][3];
					for (int i = 0; i < orient.length; i++) {
						orient[i][0] = ev[0];
						orient[i][1] = ev[1];
						orient[i][2] = ev[2];
					}
				} else {
					currentOrient++;
					if (currentOrient >= orient.length) {
						currentOrient = 0;
					}
					orient[currentOrient][0] = ev[0];
					orient[currentOrient][1] = ev[1];
					orient[currentOrient][2] = ev[2];
				}
	
				updateFlag = (accel != null);
			}
			if (updateFlag) {
				// センサーの値から向きを算出する
				float[] avrAccel = new float[3];
				for (int i = 0; i < accel.length; i++) {
					avrAccel[0] += accel[i][0];
					avrAccel[1] += accel[i][1];
					avrAccel[2] += accel[i][2];
				}
				avrAccel[0] /= (float) accel.length;
				avrAccel[1] /= (float) accel.length;
				avrAccel[2] /= (float) accel.length;
				
				float[] avrOrient = new float[3];
				for (int i = 0; i < orient.length; i++) {
					avrOrient[0] += orient[i][0];
					avrOrient[1] += orient[i][1];
					avrOrient[2] += orient[i][2];
				}
				avrOrient[0] /= (float) orient.length;
				avrOrient[1] /= (float) orient.length;
				avrOrient[2] /= (float) orient.length;
				updateCameraState(avrOrient, avrAccel);
			}
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
	private SceneRenderer.SceneRendererProcess sceneRendererProcess = new SceneRendererProcess() {
		private float[] tVec = CtkMath.createVector3f();
		
		public void preDraw(CtkGL ctkGl, SceneHandler sceneHandler) {
			// アニメーション用に値を進める
			userInterfaceState.stepAnimation();
			
			// カメラの値を更新する
			SceneCameraHandler scHandler = sceneHandler.getSceneCameraHandler();
			scHandler.setZoom(userInterfaceState.getCameraZoomLevel());
			scHandler.setIconSizeRate(userInterfaceState.getIconSizeRate());
			userInterfaceState.getCameraUpDirection(tVec);
			scHandler.setUpDirection(tVec);
			userInterfaceState.getCameraFrontDirection(tVec);
			scHandler.setFrontDirection(tVec);
			userInterfaceState.getCameraPosition(tVec);
			scHandler.setPosition(tVec);
		}
		public void postDraw(CtkGL ctkGl, SceneHandler sceneHandler) {
			// TODO
		}
	};
	
	private UpdateEtcTask updateEtcTask;
	class UpdateEtcTask extends AsyncTask<Void, Void, Void> {
		private long lastMapBitmapUpdate = 0;
		@Override
		protected Void doInBackground(Void... params) {
			while(!isCancelled()) {
				publishProgress();
				try {
					Thread.sleep(UkiukiWindowConstants.UPDATE_ETC_INTERVAL);
				} catch(InterruptedException e) {
				}
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... values) {
			long elapsedRealtime = SystemClock.elapsedRealtime();
			if (elapsedRealtime - lastMapBitmapUpdate > UkiukiWindowConstants.MAP_BITMAP_UPDATE_INTERVAL) {
				//Log.d(UkiukiWindowConstants.TAG, "update");
				lastMapBitmapUpdate = elapsedRealtime;
				updateMapBitmap();
				updateMapPosition();
			}
		}
	};

	private MyLocationOverlayEx myLocationOverlayEx;
	class MyLocationOverlayEx extends MyLocationOverlay {
		public MyLocationOverlayEx(Context context, MapView mapView) {
			super(context, mapView);
		}

		@Override
		public synchronized void onLocationChanged(Location location) {
			Log.d(UkiukiWindowConstants.TAG,String.valueOf(location));
			GeoPoint newGeoPoint = new GeoPoint((int) (location.getLatitude() * 1000000), (int) (location.getLongitude() * 1000000));
			//updateGeoPoint(currentGeoPoint, newGeoPoint);
			updateGeoPoint(newGeoPoint, newGeoPoint);
			
			// カメラを規定位置に戻す
			float[] cameraPos = CtkMath.createVector3f();
			userInterfaceState.getCameraPosition(cameraPos);
			cameraPos[0] = 0;
			cameraPos[1] = 0;
			userInterfaceState.setCameraPosition(cameraPos);
		}
	};

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ukiuki_window);
		
		Resources resources = getResources();
		String apiKeyMapView = resources.getString(R.string.api_key_map_view);
		String apiKeyUkiukiView = resources.getString(R.string.api_key_ukiuki_view);

		this.mapView = new MapView(this, apiKeyMapView);
		ScrollView mapScrollView = (ScrollView) findViewById(R.id.MapScrollView);
		mapScrollView.addView(this.mapView);
		
		// ネットワーク系のUtil
		this.webCacheUtil = new WebCacheUtil(this);
		this.ukiukiConnectionUtil = new UkiukiCloudClient(this.webCacheUtil, ActivityUtil.getModel(), apiKeyUkiukiView);
		
		// ダイアログ系の作成
		this.sceneObjectListDialog = new SceneObjectListDialog(this, this.webCacheUtil);
		this.sceneObjectListDialog.setOnSceneObjectSelectListener(this);
		this.dropSceneObjectDialog = new DropSceneObjectDialog(this);
		this.dropSceneObjectDialog.setOnDropSceneObjectListener(this);
		this.confirmDeleteDialog = new ConfirmForDeleteDialog(
				this,
				DIALOG_CONFIRM_DELETE_SCENE_OBJECT,
				ConfirmDialog.BUTTON_OK | ConfirmDialog.BUTTON_CANCEL,
				android.R.drawable.ic_menu_help,
				R.string.title_delete_ukiuki_ball,
				R.string.msg_confirm_delete_emotion_ball);
		this.confirmDeleteDialog.setOnConfirmListener(this);
		this.contentDialog = new ContentDialog(this);
		this.ukiukiCommentsListDialog = new UkiukiCommentsListDialog(this, this.webCacheUtil, this.ukiukiConnectionUtil);
		this.ukiukiCommentsListDialog.setOnUkiukiCommentsListener(this);
		this.serviceSearchConditionDialog = new ServiceSearchConditionDialog(this, webCacheUtil);
		this.serviceSearchConditionDialog.setOnServiceSearchConditionListener(this);
		
		GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.View01);
		glSurfaceView.setGLWrapper(new GLSurfaceView.GLWrapper() {
			public GL wrap(GL gl) {
				return new CtkGL(gl);
			}
		});
		this.sceneRendererHandle = new SceneRendererHandle(this, glSurfaceView, sceneRendererProcess, this.webCacheUtil);
		this.sceneTouchHandler = new SceneTouchHandler(this.sceneRendererHandle.getSceneHandler(), this.userInterfaceState.getIconSizeRate());
		this.sceneTouchHandler.setOnPickSceneObjectListener(this);
		
		// OnClickListener一式の設定
		findViewById(R.id.PresentLocationButton).setOnClickListener(this);
		findViewById(R.id.ZoomInButton).setOnClickListener(this);
		findViewById(R.id.ZoomOutButton).setOnClickListener(this);
		findViewById(R.id.DropUki2BallButton).setOnClickListener(this);
		findViewById(R.id.MenuOpenCloseButton).setOnClickListener(this);
		findViewById(R.id.SceoneObjectCloseButton).setOnClickListener(this);
		findViewById(R.id.ServiceSearchButton).setOnClickListener(this);
		
		// マップのズームレベル変更
		SeekBar mapZoomLevelBar = (SeekBar) findViewById(R.id.MapZoomLevelBar);
		mapZoomLevelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (!fromUser) {
					return;
				}
				int zoomLevel = UkiukiWindowConstants.MIN_MAP_ZOOM_LEVEL + progress;
				zoomLevel = (UkiukiWindowConstants.MIN_MAP_ZOOM_LEVEL > zoomLevel) ? UkiukiWindowConstants.MIN_MAP_ZOOM_LEVEL : zoomLevel;
				zoomLevel = (UkiukiWindowConstants.MAX_MAP_ZOOM_LEVEL < zoomLevel) ? UkiukiWindowConstants.MAX_MAP_ZOOM_LEVEL: zoomLevel;
				userInterfaceState.setMapZoomLevel(zoomLevel);
				mapView.getController().setZoom(zoomLevel);
				updateMapBitmap();
			}
		});
		mapZoomLevelBar.setMax(UkiukiWindowConstants.MAX_MAP_ZOOM_LEVEL - UkiukiWindowConstants.MIN_MAP_ZOOM_LEVEL);
		mapZoomLevelBar.setProgress(UkiukiWindowConstants.DEFAULT_MAP_ZOOM_LEVEL - UkiukiWindowConstants.MIN_MAP_ZOOM_LEVEL);

		// アイコンサイズ変更
		SeekBar iconSizeBar = (SeekBar) findViewById(R.id.IconSizeBar);
		iconSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (!fromUser) {
					return;
				}
				float iconSizeRate;
				iconSizeRate = UkiukiWindowConstants.MIN_ICON_SIZE_RATE + progress * UkiukiWindowConstants.STEP_ICON_SIZE_RATE;
				iconSizeRate = (UkiukiWindowConstants.MIN_ICON_SIZE_RATE > iconSizeRate) ? UkiukiWindowConstants.MIN_ICON_SIZE_RATE : iconSizeRate;
				iconSizeRate = (UkiukiWindowConstants.MAX_ICON_SIZE_RATE < iconSizeRate) ? UkiukiWindowConstants.MAX_ICON_SIZE_RATE: iconSizeRate;
				userInterfaceState.setIconSizeRate(iconSizeRate);
			}
		});
		iconSizeBar.setMax((int)((UkiukiWindowConstants.MAX_ICON_SIZE_RATE - UkiukiWindowConstants.MIN_ICON_SIZE_RATE) / UkiukiWindowConstants.STEP_ICON_SIZE_RATE));
		iconSizeBar.setProgress((int)((UkiukiWindowConstants.DEFAULT_ICON_SIZE_RATE - UkiukiWindowConstants.MIN_ICON_SIZE_RATE) / UkiukiWindowConstants.STEP_ICON_SIZE_RATE));
		
		// 感情玉表示/非表示のSpinnerの設定
		{
			Spinner ukiukiBallVisibilitySpinner = (Spinner) findViewById(R.id.UkiukiBallVisibilitySpinner);
			ukiukiBallVisibilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
					boolean ukiukiBallVisibility = (position != 0);
					if (userInterfaceState.isUkiukiBallVisibility() != ukiukiBallVisibility) {
						userInterfaceState.setUkiukiBallVisibility(ukiukiBallVisibility);
						requestContents(true);
					}
				}
				public void onNothingSelected(AdapterView<?> arg0) {
					// none
				}
			});
		}
		
		// サービスののSpinnerの設定
		{
			Spinner serviceSpinner = (Spinner) findViewById(R.id.ServiceSpinner);
			serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
					UkiukiServiceInfo usInfo = (UkiukiServiceInfo) adapter.getItemAtPosition(position);
					if (usInfo != null && usInfo.getSid() != null) {
						requestServiceData(false, false, usInfo, userInterfaceState.getServiceSearchCondition());
					}
				}
				public void onNothingSelected(AdapterView<?> view) {
					// none
				}
			});
		}
		
		findViewById(R.id.SceneObjectInfoImageButton).setOnClickListener(this);
		findViewById(R.id.DeleteSceneObjectButton).setOnClickListener(this);
		findViewById(R.id.CommentAddButton).setOnClickListener(this);
		findViewById(R.id.CommentMoreButton).setOnClickListener(this);
		
		myLocationOverlayEx = new MyLocationOverlayEx(this, mapView);
		mapView.getOverlays().add(this.myLocationOverlayEx);
		
		// 初期値の設定
		loadPreference();
		userInterfaceState.setUkiukiServiceInfo(serviceInfoSetting.getSelectedUkiukiServiceInfo()); 
		userInterfaceState.setServiceSearchCondition(new ServiceSearchCondition());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.MenuAccount) {
			Intent intent = new Intent(this, UkiukiAccount.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			return true;
		} else if (item.getItemId() == R.id.MenuSetting) {
			Intent intent = new Intent(this, UkiukiBasicSetting.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			return true;
		} else if (item.getItemId() == R.id.MenuReload) {
			requestContents(true);
			requestServiceData(true, true, userInterfaceState.getUkiukiServiceInfo(), userInterfaceState.getServiceSearchCondition());
			return true;
		} else if (item.getItemId() == R.id.MenuAbout) {
			showDialog(DIALOG_ABOUT);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		loadPreference();
		
		// WakeLock
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		this.wakeLock = powerManager.newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK
						| PowerManager.ON_AFTER_RELEASE,
				UkiukiWindowConstants.TAG);
		this.wakeLock.acquire();

		// センサーの取得
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (magneticSensor != null) {
			sensorManager.registerListener(sensorEventListener, magneticSensor,SensorManager.SENSOR_DELAY_GAME);
		}
		if (accelSensor != null) {
			sensorManager.registerListener(sensorEventListener, accelSensor,SensorManager.SENSOR_DELAY_GAME);
		}
		
		// 標準のズームレベルを設定
		mapView.getController().setZoom(this.userInterfaceState.getMapZoomLevel());
		
		// シーンの設定
		this.sceneRendererHandle.getSceneHandler().setMaxSceneObjectNum(this.basicSetting.getMaxSceneObjectNum());
		
		// 感情玉表示/非表示のSpinnerの設定
		{
			Spinner ukiukiBallVisibilitySpinner = (Spinner) findViewById(R.id.UkiukiBallVisibilitySpinner);
			if (userInterfaceState.isUkiukiBallVisibility()) {
				ukiukiBallVisibilitySpinner.setSelection(1);
			} else {
				ukiukiBallVisibilitySpinner.setSelection(0);
			}
		}
		
		// サービス選択Spinnerに値を設定
		{
			UkiukiServiceInfoArrayAdapter adapter = new UkiukiServiceInfoArrayAdapter(this, serviceInfoSetting.createUkiukiServiceInfoForSpinner(this.getResources()));
			Spinner serviceSpinner = (Spinner) findViewById(R.id.ServiceSpinner);
			serviceSpinner.setAdapter(adapter);
			
			String sid = (userInterfaceState.getUkiukiServiceInfo() != null) ? userInterfaceState.getUkiukiServiceInfo().getSid() : null;
			if (sid == null) {
				sid = UkiukiWindowConstants.SID_INVISIBLE;
			}
			for (int i=0;i<adapter.getCount();i++) {
				UkiukiServiceInfo usInfo = adapter.getItem(i);
				if (sid.equals(usInfo.getSid())) {
					serviceSpinner.setSelection(i);
					break;
				}
			}
		}
		
		// 描画開始
		this.sceneRendererHandle.onResume();
		
		// 初回の画面の更新を行う
		// MapViewのサイズが0だと何もできないので、仕方なくビジーウェイト
		AsyncTask<Void, Void, Void> firstUpdateLocationTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				float[] mapSize = CtkMath.createVector3f();
				while (true) {
					GeoPoint center = mapView.getMapCenter();
					int lonSpan = mapView.getLongitudeSpan() / 2;
					int latSpan = mapView.getLatitudeSpan() / 2;
					GeoPoint start = new GeoPoint(center.getLatitudeE6() + latSpan, center.getLongitudeE6() + lonSpan);
					GeoPoint end = new GeoPoint(center.getLatitudeE6() - latSpan, center.getLongitudeE6() - lonSpan);
					Hubeny.convertToMeter(mapSize, start, end);
					if (!CtkMath.isZero3F(mapSize) && mapView.getWidth() > 0 && mapView.getHeight() > 0) {
						break;
					} else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				requestUpdateLocation();
				requestContentsUsageTask(UkiukiWindowConstants.RETRY_COUNT_GET_CONTENTS_USAGE);
			}
		};
		firstUpdateLocationTask.execute();
		updateEtcTask = new UpdateEtcTask();
		updateEtcTask.execute();
		
		webCacheUtil.startTask();
		updateServiceIcon(false);
		
		if (UkiukiWindowConstants.SWITCH_3D) {
			GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.View01);
			try {
				Class<?> surfaceControllerClass = Class.forName("jp.co.sharp.android.stereo3dlcd.SurfaceController");
				Constructor<?> surfaceControllerConstructor = surfaceControllerClass.getConstructor(SurfaceView.class);
				Object object = surfaceControllerConstructor.newInstance(glSurfaceView);
				Method method = object.getClass().getMethod("setStereoView", boolean.class);
				method.invoke(object, true);
			} catch (Exception e) {
				Log.d(UkiukiWindowConstants.TAG, e.getMessage(), e);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		webCacheUtil.stopTask();

		if (updateEtcTask != null) {
			updateEtcTask.cancel(false);
			updateEtcTask = null;
		}
		if (loginTask != null) {
			loginTask.cancel(false);
			loginTask = null;
		}
		if (getServiceDataTask != null) {
			getServiceDataTask.cancel(false);
			getServiceDataTask = null;
		}
		if (getContentsTask != null) {
			getContentsTask.cancel(false);
			getContentsTask = null;
		}

		if (getContentsUsageTask != null) {
			getContentsUsageTask.cancel(false);
			getContentsUsageTask = null;
		}

		// WakeLock解除
		if (this.wakeLock != null && this.wakeLock.isHeld()) {
			this.wakeLock.release();
		}

		// 描画停止
		this.sceneRendererHandle.onPause();
		
		// ロケーションマネージャの解放
		//mapView.getOverlays().remove(this.myLocationOverlayEx);
		
		// センサーの解放
		if (magneticSensor != null) {
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Rect rect= new Rect();
		Window window= getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rect);
		return this.sceneTouchHandler.onTouchEvent(event, new RectF(rect));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == DIALOG_SCENE_OBJECT_LIST) {
			dialog = sceneObjectListDialog.onCreateDialog();
			dialog.setOnDismissListener(new OnDismissListenerEx(id, sceneObjectListDialog));
		} else if (id == DIALOG_DROP_SCENE_OBJECT) {
			dialog = dropSceneObjectDialog.onCreateDialog();
			dialog.setOnDismissListener(new OnDismissListenerEx(id, dropSceneObjectDialog));
		} else if (id == DIALOG_CONFIRM_DELETE_SCENE_OBJECT) {
			dialog = confirmDeleteDialog.onCreateDialog();
			dialog.setOnDismissListener(new OnDismissListenerEx(id, confirmDeleteDialog));
		} else if (id == DIALOG_CONTENT_DIALOG) {
			dialog = contentDialog.onCreateDialog();
			dialog.setOnDismissListener(new OnDismissListenerEx(id, contentDialog));
		} else if (id == DIALOG_UKIUKI_COMMENTS_DIALOG) {
			dialog = ukiukiCommentsListDialog.onCreateDialog();
			dialog.setOnDismissListener(new OnDismissListenerEx(id, ukiukiCommentsListDialog));
		} else if (id == DIALOG_SERVICE_SEARCH_CONDITION_DIALOG) {
			dialog = serviceSearchConditionDialog.onCreateDialog();
			dialog.setOnDismissListener(new OnDismissListenerEx(id, serviceSearchConditionDialog));
		} else if (id == DIALOG_ABOUT) {
			String title = getResources().getString(R.string.app_name) + " " + getVersion();
			CharSequence titleSec = Html.fromHtml(title);
			String message = getResources().getString(R.string.about_message);
			CharSequence messageSec = Html.fromHtml(message);
			
			dialog = new AlertDialog.Builder(this)
				.setTitle(	titleSec)
				.setMessage(messageSec)
				.setNeutralButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							/* User clicked Something so do some stuff */
						}
					})
				.create();
			dialog.setOnDismissListener(new OnDismissListenerEx(id, null));
		}
		
		return dialog;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == DIALOG_SCENE_OBJECT_LIST) {
			sceneObjectListDialog.onPrepareDialog(id, dialog);
		} else if (id == DIALOG_DROP_SCENE_OBJECT) {
			dropSceneObjectDialog.setWebCacheUtil(this.webCacheUtil);
			dropSceneObjectDialog.setUkiukiContentsUsageInfo(this.ukiukiConnectionUtil.getUkiukiContentsUsageInfo());
			dropSceneObjectDialog.onPrepareDialog(id, dialog);
		} else if (id == DIALOG_CONFIRM_DELETE_SCENE_OBJECT) {
			confirmDeleteDialog.onPrepareDialog(id, dialog);
		} else if (id == DIALOG_CONTENT_DIALOG) {
			contentDialog.onPrepareDialog(id, dialog);
		} else if (id == DIALOG_UKIUKI_COMMENTS_DIALOG) {
			ukiukiCommentsListDialog.onPrepareDialog(id, dialog);
		} else if (id == DIALOG_SERVICE_SEARCH_CONDITION_DIALOG) {
			serviceSearchConditionDialog.onPrepareDialog(id, dialog);
		}
		sceneRendererHandle.setSleepRenderer(true);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.PresentLocationButton:
			ImageButton presentLocationButton = (ImageButton) findViewById(R.id.PresentLocationButton);
			if (myLocationOverlayEx.isMyLocationEnabled())  {
				myLocationOverlayEx.disableMyLocation();
				presentLocationButton.setImageResource(R.drawable.btn_present_location_off);
			} else {
				myLocationOverlayEx.enableMyLocation();
				presentLocationButton.setImageResource(R.drawable.btn_present_location);
				requestUpdateLocation();
			}
			break;
		case R.id.ZoomInButton:
		{
			float cameraZoomLevel = userInterfaceState.getAnimCameraZoomLevel();
			cameraZoomLevel += UkiukiWindowConstants.STEP_CAMERA_ZOOM_LEVEL;
			cameraZoomLevel = (cameraZoomLevel > UkiukiWindowConstants.MAX_CAMERA_ZOOM_LEVEL) ? UkiukiWindowConstants.MAX_CAMERA_ZOOM_LEVEL : cameraZoomLevel;
			userInterfaceState.setAnimCameraZoomLevel(cameraZoomLevel);
			break;
		}
		case R.id.ZoomOutButton:
		{
			float cameraZoomLevel = userInterfaceState.getAnimCameraZoomLevel();
			cameraZoomLevel -= UkiukiWindowConstants.STEP_CAMERA_ZOOM_LEVEL;
			cameraZoomLevel = (cameraZoomLevel < UkiukiWindowConstants.MIN_CAMERA_ZOOM_LEVEL) ? UkiukiWindowConstants.MIN_CAMERA_ZOOM_LEVEL : cameraZoomLevel;
			userInterfaceState.setAnimCameraZoomLevel(cameraZoomLevel);
			break;
		}
		case R.id.DropUki2BallButton:
			dropUkiukiBall();
			break;
		case R.id.ServiceSearchButton:
			serviceSearch();
			break;
		case R.id.MenuOpenCloseButton:
			ImageButton button = (ImageButton) findViewById(R.id.MenuOpenCloseButton);
			View menuLayout = findViewById(R.id.MenuLayout);
			if (menuLayout.getVisibility() == View.VISIBLE) {
				menuLayout.setVisibility(View.GONE);
				button.setImageResource(R.drawable.btn_menu_open);
			} else {
				menuLayout.setVisibility(View.VISIBLE);
				button.setImageResource(R.drawable.btn_menu_close);
			}
			break;
		case R.id.SceneObjectInfoImageButton:
			if (selectedSceneObjectInfo != null && 
					(selectedSceneObjectInfo.getInfoUri() != null
					 || selectedSceneObjectInfo.getCouponUri() != null
					 || selectedSceneObjectInfo.getContent() != null))
			{
				contentDialog.setTitle(selectedSceneObjectInfo.getTitle());
				contentDialog.setContent(selectedSceneObjectInfo.getContent());
				showDialog(DIALOG_CONTENT_DIALOG);
			}
			break;
		case R.id.SceoneObjectCloseButton:
		{	
			onSceneObjectSelect(null);
			break;
		}
		case R.id.DeleteSceneObjectButton:
			if (selectedSceneObjectInfo != null && selectedSceneObjectInfo.getObjectId() != null) {
				confirmDeleteDialog.setObjectId(selectedSceneObjectInfo.getObjectId());
				showDialog(DIALOG_CONFIRM_DELETE_SCENE_OBJECT);
			}
			break;
		case R.id.CommentAddButton:
			if (selectedSceneObjectInfo != null && selectedSceneObjectInfo.getObjectId() != null) {
				dropSceneObjectDialog.setParentId(selectedSceneObjectInfo.getObjectId());
				dropSceneObjectDialog.setGeoPoint(selectedSceneObjectInfo.getGeoPoint());
				showDialog(DIALOG_DROP_SCENE_OBJECT);
			}
			break;
		case R.id.CommentMoreButton:
			if (selectedSceneObjectInfo != null && selectedSceneObjectInfo.getObjectId() != null) {
				ukiukiCommentsListDialog.setParentSceneObjectInfo(selectedSceneObjectInfo);
				showDialog(DIALOG_UKIUKI_COMMENTS_DIALOG);
			}
			break;
		}
	}
	
	/**
	 * SceneObjectのピック結果の処理を行う
	 */
	public void onPickSceneObject(List<SceneObject> soList) {
		List<SceneObjectInfo> soInfoList = new ArrayList<SceneObjectInfo>();
		for(SceneObject so:soList) {
			if (so.getSceneObjectInfo() != null) {
				soInfoList.add(so.getSceneObjectInfo());
			}
		}
		if (soInfoList.size() == 0) {
			// 0件ならnullにする
			onSceneObjectSelect(null);
		} else if (soInfoList.size() == 1) {
			// 1件ならそのまま選択へ
			SceneObjectInfo soInfo = soInfoList.get(0);
			onSceneObjectSelect(soInfo);
		} else {
			// 1件越えなら選択ダイアログを表示
			sceneObjectListDialog.setSceneObjectInfoList(soInfoList);
			showDialog(DIALOG_SCENE_OBJECT_LIST);
		}
	}
	
	/**
	 * マップのピック時の処理を行う。
	 * 主に感情玉を落とす処理
	 */
	public void onPickMapPoint(GeoPoint geoPoint) {
		// Sceneタッチのモードを戻しておく
		sceneTouchHandler.setSingleTouchMode(SingleTouchMode.PICK_SCENE_OBJECT);
		
		// ダイアログを表示する
		dropSceneObjectDialog.setParentId(null);
		dropSceneObjectDialog.setGeoPoint(geoPoint);
		showDialog(DIALOG_DROP_SCENE_OBJECT);
	}
	
	/**
	 * マップをドラッグした場合の処理を行う
	 */
	public void onDragMapPoint(float[] diff) {
		float[] pos = CtkMath.createVector3f();
		userInterfaceState.getCameraPosition(pos);
		CtkMath.addEq3F(pos, diff);
		userInterfaceState.setCameraPosition(pos);
	}
	
	/**
	 * DropSceneObjectDialogで、SceneObjectInfoが作られたら呼び出される処理。
	 * onPickMapPointの後に呼び出される
	 */
	public void onSceneObjectInfoCreated(SceneObjectInfo soInfo) {
		submitUkiukiBall(soInfo);
	}
	
	public void onSceneObjectSelect(SceneObjectInfo soInfo) {
		selectedSceneObjectInfo = soInfo;
		
		View view = findViewById(R.id.SelectedSceneObjectInfo);
		HashSet<String> selectedSceneObjectidSet = new HashSet<String>();
		if (selectedSceneObjectInfo == null) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			ImageView imageView = (ImageView) view.findViewById(R.id.SceneObjectImageView);
			TextView labelView = (TextView) view.findViewById(R.id.SceneObjectLabel);
			TextView detailView = (TextView) view.findViewById(R.id.SceneObjectDetail);
			
			ImageButton commentAddButton = (ImageButton) view.findViewById(R.id.CommentAddButton);
			ImageButton commentMoreButton = (ImageButton) view.findViewById(R.id.CommentMoreButton);
			ImageButton infoButton = (ImageButton) view.findViewById(R.id.SceneObjectInfoImageButton);
			ImageButton deleteButton = (ImageButton) view.findViewById(R.id.DeleteSceneObjectButton);

			labelView.setText(soInfo.getTitle());
			detailView.setText(soInfo.getDetail());
			
			// ICONの表示
			Uri iconUri = soInfo.getIconUri();
			if (iconUri != null) {
				ImageCache ic = this.webCacheUtil.getImageCache(iconUri);
				ic = this.webCacheUtil.filterImageCache(ic);
				imageView.setImageBitmap(ic.getBitmap());
				imageView.setVisibility(View.VISIBLE);
			} else {
				imageView.setVisibility(View.INVISIBLE);
			}
			
			// Infoボタンの表示
			if (soInfo.getInfoUri() != null) {
				infoButton.setVisibility(View.VISIBLE);
			} else {
				infoButton.setVisibility(View.GONE);
			}
			
			// Deleteボタンの表示(感情玉限定)
			if (soInfo.getOwnerNickname() != null && soInfo.getOwnerNickname().equals(ukiukiConnectionUtil.getAccountUkiukiView())) {
				deleteButton.setVisibility(View.VISIBLE);
			} else {
				deleteButton.setVisibility(View.GONE);
			}
			
			// CommentAddボタンの表示
			if (ukiukiConnectionUtil.getSessionCodeUkiukiView() != null && soInfo.isCommentable()) {
				commentAddButton.setVisibility(View.VISIBLE);
			} else {
				commentAddButton.setVisibility(View.GONE);
			}
			
			// CommentMoreボタンの表示
			if (soInfo.getNumOfComments() > 0) {
				commentMoreButton.setVisibility(View.VISIBLE);
			} else {
				commentMoreButton.setVisibility(View.GONE);
			}
			
			selectedSceneObjectidSet.add(selectedSceneObjectInfo.getObjectId());
		}
		sceneRendererHandle.getSceneHandler().setSelectedObjectIdSet(selectedSceneObjectidSet);
	}
	
	public void onUkiukiCommentsDelete(String objectId) {
		confirmDeleteDialog.setObjectId(objectId);
		showDialog(DIALOG_CONFIRM_DELETE_SCENE_OBJECT);
	}
	
	/**
	 * ServiceSearchConditionDialogで検索条件が作成された場合に呼び出される
	 */
	public void onCreateServiceSearchCondition(
			ServiceSearchCondition serviceSearchCondition) {
		userInterfaceState.setServiceSearchCondition(serviceSearchCondition);
		requestServiceData(true, true, userInterfaceState.getUkiukiServiceInfo(), userInterfaceState.getServiceSearchCondition());
	}
	
	public void onConfirm(int dialogId, int button) {
		if (dialogId == DIALOG_CONFIRM_DELETE_SCENE_OBJECT) {
			if (button == ConfirmDialog.BUTTON_OK) {
				String objectId = confirmDeleteDialog.getObjectId();
				if (objectId != null && objectId.length() > 0) {
					deleteUkiukiBall(objectId);
				}
			}
		}
	}
	
	private void updateCameraState(float[] magnetic, float[] accel) {
		float[] m = CtkMath.createMatrix4f();
		float[] rm = CtkMath.createMatrix4f();
		SensorManager.getRotationMatrix(rm, null, accel, magnetic);
		CtkMath.inverseMatrix4f(m, rm);
		Matrix.rotateM(m, 0, - 90, 0f, 0f, 1f);

		// カメラの座標と向きを設定する
		SceneHandler sceneHandler = sceneRendererHandle.getSceneHandler();
		float[] tVec = CtkMath.createVector4f();
		Matrix.multiplyMV(tVec, 0, m, 0, CtkMath.createVector4f( 0, 1, 0, 0), 0);
		userInterfaceState.setCameraUpDirection(tVec);
		Matrix.multiplyMV(tVec, 0, m, 0, CtkMath.createVector4f( 0, 0,-1, 0), 0);
		userInterfaceState.setCameraFrontDirection(tVec);
		float[] cameraPos = CtkMath.createVector3f();
		userInterfaceState.getCameraPosition(cameraPos);
		cameraPos[2] = sceneHandler.calcMapSizeLength() * UkiukiWindowConstants.CAMERA_ALTITUDE_RATE;
		userInterfaceState.setCameraPosition(cameraPos);
	}

	private void updateGeoPoint(GeoPoint newGeoPoint, GeoPoint newMarkerGeoPoint) {
		this.currentGeoPoint = newGeoPoint;

		mapView.getController().setCenter(currentGeoPoint);
		updateMapBitmap();
		SceneHandler sceneHandler = sceneRendererHandle.getSceneHandler();
		if (newMarkerGeoPoint != null) {
			this.lastMarkerGeoPoint = newMarkerGeoPoint;
			sceneHandler.setMarkerPosition(lastMarkerGeoPoint);
		}
		
		requestServiceData(false, false, this.userInterfaceState.getUkiukiServiceInfo(), this.userInterfaceState.getServiceSearchCondition());
		requestContents(false);
	}
	
	private void requestUpdateLocation() {
//		((ViewGroup) findViewById(R.id.GpsStatusLayout)).setVisibility(View.VISIBLE);
		if (getServiceDataTask != null) {
			getServiceDataTask.cancel(false);
			getServiceDataTask = null;
		}
		if (getContentsTask != null) {
			getContentsTask.cancel(false);
			getContentsTask = null;
		}
		if (getContentsUsageTask != null) {
			getContentsUsageTask.cancel(false);
			getContentsUsageTask = null;
		}
		lastGetContentsGeoPoint = null;
		lastGetServiceDataGeoPoint = null;
		
		if (lastMarkerGeoPoint != null) {
			updateGeoPoint(lastMarkerGeoPoint, lastMarkerGeoPoint);
		} else {
			GeoPoint nextGeoPoint = mapView.getMapCenter();
			updateGeoPoint(nextGeoPoint, null);
		}
		
		// カメラを規定位置に戻す
		float[] cameraPos = CtkMath.createVector3f();
		userInterfaceState.getCameraPosition(cameraPos);
		cameraPos[0] = 0;
		cameraPos[1] = 0;
		userInterfaceState.setCameraPosition(cameraPos);
	}
	private void requestContentsUsageTask(int retryCount) {
		if (this.getContentsUsageTask != null) {
			this.getContentsUsageTask.cancel(false);
			this.getContentsUsageTask = null;
		}
		
		((ViewGroup) findViewById(R.id.EssentialStatusLayout)).setVisibility(View.VISIBLE);
		this.getContentsUsageTask = ukiukiConnectionUtil.getContentsUsage(new OnGetContentsUsageListenerEx(retryCount));
	}

	private void requestServiceData(boolean force, boolean clearAll, UkiukiServiceInfo usInfo, ServiceSearchCondition serviceSearchCondition) {
		if (!ukiukiConnectionUtil.isEssentialInfoReady()) {
			return;
		}
		boolean sidChanged = (userInterfaceState.getUkiukiServiceInfo() == null || !userInterfaceState.getUkiukiServiceInfo().equals(usInfo));
		boolean needUpdate = (currentGeoPoint != null && isNeedUpdate(lastGetServiceDataGeoPoint,currentGeoPoint));
		if (force || (sidChanged || needUpdate)) {
			lastGetServiceDataGeoPoint = currentGeoPoint;
			if (getServiceDataTask != null) {
				getServiceDataTask.cancel(false);
				getServiceDataTask = null;
			}
			
			if (sidChanged) {
				// もしServiceIDが変更されていれば一旦クリアする
				userInterfaceState.setUkiukiServiceInfo(usInfo);
				userInterfaceState.setServiceSearchCondition(new ServiceSearchCondition());
				sceneRendererHandle.setSceneObjectInfoList(1, new ArrayList<SceneObjectInfo>());
				updateServiceIcon(true);
			} else if (clearAll) {
				sceneRendererHandle.setSceneObjectInfoList(1, new ArrayList<SceneObjectInfo>());
			}
			
			if (UkiukiWindowConstants.SID_INVISIBLE.equals(userInterfaceState.getUkiukiServiceInfo().getSid())) {
				// サービス非表示のSIDが選択されているので何もしない
			} else {
				float mapSize = this.sceneRendererHandle.getSceneHandler().calcMapSizeLength();
				//Log.d(UkiukiWindowConstants.TAG, "MapSize:" + mapSize + ", range:"+range);
				
				((ViewGroup) findViewById(R.id.GetServiceDataStatusLayout)).setVisibility(View.VISIBLE);
				getServiceDataTask = ukiukiConnectionUtil.getServiceData(new UkiukiGetContentsTask.OnGetSceneObjectInfoListener() {
					public void onGetSceneObjectInfo(List<SceneObjectInfo> soInfoList, boolean finished) {
						if (soInfoList != null) {
							sceneRendererHandle.addSceneObjectInfoList(1, soInfoList);
						}
						if (finished) {
							((ViewGroup) findViewById(R.id.GetServiceDataStatusLayout)).setVisibility(View.INVISIBLE);
							getServiceDataTask = null;
							//Log.d(UkiukiWindowConstants.TAG, "GetServiceDataTask succeed:" + soInfoList.size());
						}
					}
					public void onCancel() {
						((ViewGroup) findViewById(R.id.GetServiceDataStatusLayout)).setVisibility(View.INVISIBLE);
						getServiceDataTask = null;
						//Log.d(UkiukiWindowConstants.TAG, "GetServiceDataTask canceled.");
					}
				}, userInterfaceState.getUkiukiServiceInfo().getSid(), currentGeoPoint, mapSize/2, this.basicSetting.getMaxSceneObjectNum(), serviceSearchCondition);
				//Log.d(UkiukiWindowConstants.TAG, "GetServiceDataTask started:" + geoPoint + "," + range);
			}
		}
	}
	private void requestContents(boolean force) {
		if (!ukiukiConnectionUtil.isEssentialInfoReady()) {
			return;
		}

		boolean needUpdate = (currentGeoPoint != null && isNeedUpdate(lastGetContentsGeoPoint,currentGeoPoint));
		if (force || needUpdate) {
			lastGetContentsGeoPoint = currentGeoPoint;
			if (getContentsTask != null) {
				getContentsTask.cancel(false);
				getContentsTask = null;
			}
			
			// 感情玉の表示がオフの場合はクリアして終了する
			if (!userInterfaceState.isUkiukiBallVisibility()) {
				sceneRendererHandle.setSceneObjectInfoList(0, new ArrayList<SceneObjectInfo>());
				return;
			}
			
			float mapSize = this.sceneRendererHandle.getSceneHandler().calcMapSizeLength();
			
			((ViewGroup) findViewById(R.id.GetContentsStatusLayout)).setVisibility(View.VISIBLE);
			getContentsTask = ukiukiConnectionUtil.getContents(new UkiukiGetContentsTask.OnGetSceneObjectInfoListener() {
				public void onGetSceneObjectInfo(List<SceneObjectInfo> soInfoList, boolean finished) {
					if (soInfoList != null) {
						sceneRendererHandle.addSceneObjectInfoList(0,soInfoList);
					}
					if (finished) {
						((ViewGroup) findViewById(R.id.GetContentsStatusLayout)).setVisibility(View.INVISIBLE);
						getServiceDataTask = null;
						//Log.d(UkiukiWindowConstants.TAG, "GetContentsTask succeed:" + soInfoList.size());
					}
				}
				public void onCancel() {
					((ViewGroup) findViewById(R.id.GetContentsStatusLayout)).setVisibility(View.INVISIBLE);
					getServiceDataTask = null;
					//Log.d(UkiukiWindowConstants.TAG, "GetContentsTask canceled.");
				}
			}, currentGeoPoint, mapSize/2, this.basicSetting.getMaxSceneObjectNum());
			//Log.d(UkiukiWindowConstants.TAG, "GetContentsTask started:" + geoPoint + "," + range);
		}
	}
	private void dropUkiukiBall() {
		if (ukiukiConnectionUtil.getSessionCodeUkiukiView() == null) {
			if (!this.ukiukiConnectionUtil.isEssentialInfoReady()) {
				// 必須情報取得中なので、その旨のToastを表示する
				Toast.makeText(UkiukiWindow.this, R.string.msg_downloading_essential_info_now, Toast.LENGTH_LONG).show();
			} else if (this.loginTask != null) {
				// ログイン中なので、その旨のToastを表示する
				Toast.makeText(UkiukiWindow.this, R.string.msg_loging_in_now, Toast.LENGTH_LONG).show();
			} else {
				// ログインを行う
				requestLogin();
			}
		} else {
			Toast.makeText(UkiukiWindow.this, R.string.msg_touch_point_to_drop, Toast.LENGTH_LONG).show();
			sceneTouchHandler.setSingleTouchMode(SingleTouchMode.PICK_MAP_POINT);
		}
	}
	
	private void serviceSearch() {
		UkiukiServiceGenreInfo usgInfo = ukiukiConnectionUtil.getUkiukiServiceGenreInfo(userInterfaceState.getUkiukiServiceInfo().getSid());
		if (usgInfo != null) {
			this.serviceSearchConditionDialog.setCategoryInfoList(usgInfo.getCategoryInfoList());
			this.serviceSearchConditionDialog.setServiceSearchCondition(userInterfaceState.getServiceSearchCondition());
			showDialog(DIALOG_SERVICE_SEARCH_CONDITION_DIALOG);
		} else {
			// UkiukiServiceGenreInfoが取得できてない場合は使用不可
			Toast.makeText(UkiukiWindow.this, R.string.msg_downloading_service_info_now, Toast.LENGTH_LONG).show();
		}
	}
	
	private void requestLogin() {
		if (this.loginTask != null) {
			this.loginTask.cancel(false);
			this.loginTask = null;
		}
		
		if (this.basicSetting.getAccount() == null || this.basicSetting.getAccount().length() == 0) {
			Toast.makeText(UkiukiWindow.this, R.string.msg_account_info_is_not_set, Toast.LENGTH_LONG).show();
			return;
		}
		
		findViewById(R.id.LoginStatusLayout).setVisibility(View.VISIBLE);
		
		this.loginTask = ukiukiConnectionUtil.login(new UkiukiLoginTask.OnLoginListener() {
			public void onLogin(String sessionCode) {
				ImageButton button = (ImageButton) findViewById(R.id.DropUki2BallButton);
				button.setImageResource(R.drawable.btn_drop);
				finish();
			}
			
			public void onFailed() {
				Toast.makeText(UkiukiWindow.this, R.string.msg_login_failed, Toast.LENGTH_LONG).show();
				onCancel();
			}
			
			public void onCancel() {
				ImageButton button = (ImageButton) findViewById(R.id.DropUki2BallButton);
				button.setImageResource(R.drawable.btn_drop_off);
				finish();
			}
			
			private void finish() {
				findViewById(R.id.LoginStatusLayout).setVisibility(View.INVISIBLE);
			}
		}, basicSetting.getAccount(), basicSetting.getPassword());
	}
	
	private void submitUkiukiBall(SceneObjectInfo soInfo) {
		if (submitUkiukiBallTask != null) {
			submitUkiukiBallTask.cancel(false);
			submitUkiukiBallTask = null;
		}

		findViewById(R.id.SubmitStatusLayout).setVisibility(View.VISIBLE);
		
		this.submitUkiukiBallTask = ukiukiConnectionUtil.submitUkiukiBall(new UkiukiSubmitUkiukiBallTask.OnSubmitUkiukiBallListener() {
			public void onSucceed(SceneObjectInfo soInfo) {
				List<SceneObjectInfo> soInfoList = new ArrayList<SceneObjectInfo>();
				soInfoList.add(soInfo);
				requestContents(true);
				finish();
			}
			
			public void onFailed() {
				Toast.makeText(UkiukiWindow.this, R.string.msg_submitting_emotion_ball_failed, Toast.LENGTH_LONG).show();
				onCancel();
			}
			
			public void onCancel() {
				finish();
			}
			
			private void finish() {
				findViewById(R.id.SubmitStatusLayout).setVisibility(View.INVISIBLE);
			}
		}, soInfo);
	}
	
	private void deleteUkiukiBall(String objectId) {
		if (deleteUkiukiBallTask != null) {
			deleteUkiukiBallTask.cancel(false);
			deleteUkiukiBallTask = null;
		}

		//findViewById(R.id.DeleteStatusLayout).setVisibility(View.VISIBLE);
		this.deleteUkiukiBallTask = ukiukiConnectionUtil.deleteUkiukiBall(new UkiukiDeleteUkiukiBallTask.OnDeleteUkiukiBallListener() {
			public void onSucceed(String objectId) {
				Toast.makeText(UkiukiWindow.this, R.string.msg_deleting_emotion_ball_succeed, Toast.LENGTH_LONG).show();
				sceneRendererHandle.removeSceneObject(0, objectId);
				requestContents(true);
				// 選択を解除
				onSceneObjectSelect(null);
			}
			public void onFailed() {
				Toast.makeText(UkiukiWindow.this, R.string.msg_deleting_emotion_ball_failed, Toast.LENGTH_LONG).show();
				onCancel();
			}
			public void onCancel() {
				// なし
			}
		}, objectId);
	}
	private void updateMapBitmap() {
		// マップのテクスチャ用のビットマップ作成
		if (mapBitmap == null || mapBitmap.getWidth() != mapView.getWidth() || mapBitmap.getHeight() != mapView.getHeight()) {
			if (mapBitmap != null) {
				mapBitmap.recycle();
			}
			// TODO ここ、どうにかしたい、、
			int w = (mapView.getWidth() > 0) ? mapView.getWidth() : 512;
			int h = (mapView.getHeight() > 0) ? mapView.getHeight() : 512;
			mapBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
		}

		Canvas canvas = new Canvas(mapBitmap);
		mapView.getController().stopAnimation(true);
		try {
			mapView.draw(canvas);
		} catch (IllegalStateException e) {
			// TODO お行儀が悪いが、発生する理由がイマイチ分からないので対症療法
			Log.w(UkiukiWindowConstants.TAG, e.getMessage(), e);
		}
		float[] mapSize = CtkMath.createVector3f(1,1,1);
		{
			GeoPoint center = mapView.getMapCenter();
			int lonSpan = mapView.getLongitudeSpan() / 2;
			int latSpan = mapView.getLatitudeSpan() / 2;
			GeoPoint start = new GeoPoint(center.getLatitudeE6() + latSpan, center.getLongitudeE6() + lonSpan);
			GeoPoint end = new GeoPoint(center.getLatitudeE6() - latSpan, center.getLongitudeE6() - lonSpan);
			Hubeny.convertToMeter(mapSize, start, end);
		}
		float mapSizeLength = CtkMath.length3F(mapSize);
		
		GeoPoint center = mapView.getMapCenter();
		int lonSpan = mapView.getLongitudeSpan() / 2;
		int latSpan = mapView.getLatitudeSpan() / 2;
		GeoPoint start = new GeoPoint(center.getLatitudeE6() + latSpan, center.getLongitudeE6() + lonSpan);
		GeoPoint end = new GeoPoint(center.getLatitudeE6() - latSpan, center.getLongitudeE6() - lonSpan);

		sceneRendererHandle.updateMap(mapBitmap, mapSize, currentGeoPoint, start,end);
		//Log.d(UkiukiWindowConstants.TAG, "MapView:" + mapSize[0] + "," + mapSize[1]);
		
		locationUpdateDistance = mapSizeLength * UkiukiWindowConstants.MIN_LOCATION_UPDATE_DISTANCE_RATE;
	}
	private void updateMapPosition() {
		if (currentGeoPoint != null) {
			SceneCameraHandler scHandler = sceneRendererHandle.getSceneHandler().getSceneCameraHandler();
			float[] pos = CtkMath.createVector3f();
			scHandler.getPosition(pos);
			pos[2] = 0;
			
			GeoPoint newGeoPoint = Hubeny.convertToGeoPoint(currentGeoPoint, pos);
			//Log.d(UkiukiWindowConstants.TAG, String.format("(%d,%d)-(%d,%d)", currentGeoPoint.getLatitudeE6(),currentGeoPoint.getLongitudeE6(), newGeoPoint.getLatitudeE6(),newGeoPoint.getLongitudeE6()));
			
			updateGeoPoint(newGeoPoint, lastMarkerGeoPoint);
			// カメラを規定位置に戻す
			float[] cameraPos = CtkMath.createVector3f();
			userInterfaceState.getCameraPosition(cameraPos);
			cameraPos[0] = 0;
			cameraPos[1] = 0;
			userInterfaceState.setCameraPosition(cameraPos);
		}
	}

	private boolean isNeedUpdate(GeoPoint oldPoint, GeoPoint newPoint) {
		if (oldPoint == null || locationUpdateDistance == 0) {
			return true;
		}
		// マップの1/4以上移動してなければコンテンツの再取得はしない
		float[] vec = CtkMath.createVector3f();
		float distance = (float) Hubeny.getDistance(vec, oldPoint, newPoint);
		//Log.d(UkiukiWindowConstants.TAG,String.valueOf(distance) + " : "  + locationUpdateDistance);
		return (distance >= locationUpdateDistance);
	}
	
	public void updateServiceIcon(boolean reset) {
		ImageView imageView = (ImageView) findViewById(R.id.LogoServiceView);
		if (reset) {
			imageView.setImageBitmap(null);
		}
		
		String iconUriString = this.userInterfaceState.getUkiukiServiceInfo().getIconUri();
		Uri iconUri = null;
		if (iconUriString != null) {
			iconUri = Uri.parse(iconUriString);
		}
		if (iconUri != null) {
			ImageCache imageCache = webCacheUtil.getImageCache(iconUri, this);
			if (imageCache.getStatus() == ImageCache.STATUS_READY) {
				imageView.setImageBitmap(imageCache.getBitmap());
			}
		}
	}
	
	public void onImageCacheLoaded(ImageCache imageCache) {
		String iconUriString = this.userInterfaceState.getUkiukiServiceInfo().getIconUri();
		if (iconUriString != null) {
			Uri iconUri = Uri.parse(iconUriString);
			if (iconUri != null && imageCache.getUri().equals(Uri.parse(iconUriString))) {
				updateServiceIcon(false);
			}
		}
	}
	
	private void loadPreference() {
		this.basicSetting = BasicSetting.loadPreference(PreferenceManager.getDefaultSharedPreferences(this), true);
		this.serviceInfoSetting = ServiceInfoSetting.loadPreference(this.getSharedPreferences(UkiukiWindowConstants.PREF_NAME_SERVICE_INFO, MODE_PRIVATE), this.getResources(), true);
	}
	
	public String getVersion() {
		String versionName = "";
		String packageName = getClass().getPackage().getName();
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = null;
			info = pm.getPackageInfo(packageName, 0);
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
		}
		return versionName;
	}
}
