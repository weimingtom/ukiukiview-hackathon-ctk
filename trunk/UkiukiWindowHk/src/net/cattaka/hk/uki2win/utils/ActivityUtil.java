package net.cattaka.hk.uki2win.utils;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

public class ActivityUtil {
	public static boolean isDebugAble(Context ctx) {
		PackageManager manager = ctx.getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = manager.getApplicationInfo(ctx.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(UkiukiWindowConstants.TAG, e.toString());
			return false;
		}
		if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
			return true;
		} else {
			return false;
		}
	}
	public static String getModel() {
		String model;
		model = Build.MODEL + "__" + Build.VERSION.RELEASE;
		return model;
	}
}
