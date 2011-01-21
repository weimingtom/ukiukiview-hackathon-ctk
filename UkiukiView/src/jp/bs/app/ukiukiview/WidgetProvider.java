package jp.bs.app.ukiukiview;

/*
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
*/

//import jp.co.brilliantservice.app.openar.data.PoiObject;
//import jp.co.brilliantservice.utility.SdLog;

import jp.bs.app.ukiukiview.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
    private static final int BUTTON_JOY = 0;
    private static final int BUTTON_ANGRY = 1;
    private static final int BUTTON_SAD = 2;

    @Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
        RemoteViews view = buildUpdate(context, -1);

        for (int i = 0; i < appWidgetIds.length; i++) {
            appWidgetManager.updateAppWidget(appWidgetIds[i], view);
        }
	}

    static RemoteViews buildUpdate(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.btnJoy, getLaunchPendingIntent(context, appWidgetId, BUTTON_JOY));
        views.setOnClickPendingIntent(R.id.btnAngry, getLaunchPendingIntent(context, appWidgetId, BUTTON_ANGRY));
        views.setOnClickPendingIntent(R.id.btnSad, getLaunchPendingIntent(context, appWidgetId, BUTTON_SAD));
        return views;
    }

    private static PendingIntent getLaunchPendingIntent(Context context, int appWidgetId,
            int buttonId) {
        Intent launchIntent = new Intent();
        launchIntent.setClass(context, WidgetProvider.class);
        launchIntent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        launchIntent.setData(Uri.parse("custom:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, launchIntent, 0);
        return pi;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            Log.e("widget","onReceive button="+buttonId);
            if (buttonId == BUTTON_JOY) {
            } else if (buttonId == BUTTON_ANGRY) {
            } else if (buttonId == BUTTON_SAD) {
            }
        }
    }

/*
	private String httpConnection(String path) throws Exception {
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
		return new String(out.toByteArray());
	}
*/
/*
	private void postData(PoiObject obj) {
		try {
			String request = "https://"+Uki2ServerApi.SERVER+"/message/regist?"
				+ "apikey=" + Uki2ServerApi.APIKEY
				+ "&userid=" + "7000"
				+ "&lat=" + obj.mLatitude
				+ "&lng=" + obj.mLongitude
				+ "&type=" + obj.mMimeType
				+ "&content=" + obj.mContent
				+ "&language=" + "ja";
			SdLog.put("request="+request);
			String response = httpConnection(request);
			SdLog.put("uki2server response="+response);
		} catch (Exception e) {
			Log.e("Exception", "Exception!!!!!!:"+e.getLocalizedMessage());
		}
	}
*/
}
