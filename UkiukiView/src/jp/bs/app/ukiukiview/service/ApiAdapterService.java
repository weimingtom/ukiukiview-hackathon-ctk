package jp.bs.app.ukiukiview.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.bs.app.ukiukiview.HotPepperApi;
import jp.bs.app.ukiukiview.Uki2ServerApi;
import jp.co.brilliantservice.app.openar.data.ApiAdapter;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

public class ApiAdapterService extends Service {

	public static final String INTENT_ACTION = "myService_Update";
	private Timer timer;
	private Handler handler;
	public Handler mLoader;
	private List<ServiceList> mServiceList = null;

	class ServiceList {
		int serviceId;
		String serviceName;
		ApiAdapter adapter;
		ServiceList(int serviceId, String serviceName, ApiAdapter adapter) {
			this.serviceId = serviceId;
			this.serviceName = serviceName;
			this.adapter = adapter;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ApiAdapter adapter = null;
		handler = new Handler();
		mLoader = new Handler(){
			public void handleMessage(Message msg) {
				int serviceId = -1;
				if (msg != null && msg.obj != null) {
					serviceId = Integer.parseInt(((String)msg.obj));
					if (serviceId >=0 && serviceId < mServiceList.size()) {
//						mServiceList.get(serviceId).adapter.retrieveData(location, range);
					}
				}
			}
		};
		mServiceList = new ArrayList<ServiceList>();
		adapter = new Uki2ServerApi(getResources());
		mServiceList.add(new ServiceList(0, adapter.getApiName(), adapter));
		adapter = new HotPepperApi(getResources());
		mServiceList.add(new ServiceList(1, adapter.getApiName(), adapter));
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		updateTask(250);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mServiceBinder;
	}

	private void updateTask(long period) {
		if( timer != null ) {
			timer.cancel();
		}
		timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						reportProgress();
					}
				});
			}
		};
		timer.schedule(task, 0, period);
	}

	private void reportProgress() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if( timer != null ) {
			timer.cancel();
			timer = null;
		}
	}

	private final IApiAdapterService.Stub mServiceBinder = new IApiAdapterService.Stub() {
		public void stopService() throws RemoteException {
			if( timer != null ) {
				timer.cancel();
				timer = null;
			}
		}

		public String getServiceName(int serviceId) throws RemoteException {
			if (serviceId >= 0 && serviceId < mServiceList.size()) {
				return mServiceList.get(serviceId).serviceName;
			}
			return null;
		}

		public int getServicesAmount() throws RemoteException {
			return mServiceList.size();
		}

		public void loadPoiObjects(int serviceId) throws RemoteException {
			handler.post(new Runnable() {
				public void run() {

				}
			});
		}

		public int postPoiObject(int serviceId, String summary,
				String mimeType, String content, String parentUid,
				String language) throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		public void registerCallback(IApiAdapterServiceCallback cb)
				throws RemoteException {
			// TODO Auto-generated method stub

		}

		public void unregisterCallback(IApiAdapterServiceCallback cb)
				throws RemoteException {
			// TODO Auto-generated method stub

		}
	};
}
