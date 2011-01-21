package jp.bs.app.ukiukiview;

import android.content.Context;
import android.content.SharedPreferences;

public class Setting {
	final static String PREFS = "jp.bs.app.UkiukiView_preferences";
	private SharedPreferences prefs;

	public Setting(Context context){
		prefs = context.getSharedPreferences(PREFS, 0);
	}

	public void set(String key, String value) {
		SharedPreferences.Editor e = prefs.edit();
		e.putString(key, value);
		e.commit();
	}

	public void setInt(String key, int value) {
		SharedPreferences.Editor e = prefs.edit();
		e.putInt(key, value);
		e.commit();
	}

	public void setBoolean(String key, boolean value) {
		SharedPreferences.Editor e = prefs.edit();
		e.putBoolean(key, value);
		e.commit();
	}

	public String get(String key) {
		return prefs.getString(key, "");
	}

	public int getInt(String key) {
		return prefs.getInt(key, 0);
	}

	public boolean getBoolean(String key) {
		return prefs.getBoolean(key, false);
	}
}
