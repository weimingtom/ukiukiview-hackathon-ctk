package net.cattaka.hk.uki2win.setting;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import net.cattaka.hk.uki2win.UkiukiWindowConstants;

/**
 * 
 * @author cattaka
 */
public class BasicSetting {
	private String account;
	private String password;
	private int maxSceneObjectNum = UkiukiWindowConstants.DEFAULT_SCENE_OBJECT_NUM;
	
	public BasicSetting() {
	}
	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxSceneObjectNum() {
		return maxSceneObjectNum;
	}
	public void setMaxSceneObjectNum(int maxSceneObjectNum) {
		this.maxSceneObjectNum = maxSceneObjectNum;
	}

	public static BasicSetting loadPreference(SharedPreferences pref, boolean clearOnError) {
		BasicSetting basicSetting = new BasicSetting();
		try {
			basicSetting.setAccount(pref.getString(UkiukiWindowConstants.KEY_ACCOUNT, ""));
			basicSetting.setPassword(pref.getString(UkiukiWindowConstants.KEY_PASSWORD, ""));
			basicSetting.setMaxSceneObjectNum(pref.getInt(UkiukiWindowConstants.KEY_MAX_SCENE_OBJECT_NUM, UkiukiWindowConstants.DEFAULT_SCENE_OBJECT_NUM));
		} catch (ClassCastException e) {
			if (clearOnError) {
				Editor editor = pref.edit();
				editor.clear();
				editor.commit();
			}
			basicSetting.setAccount("");
			basicSetting.setPassword("");
			basicSetting.setMaxSceneObjectNum(UkiukiWindowConstants.DEFAULT_SCENE_OBJECT_NUM);
		}
		
		return basicSetting;
	}
	public static void savePreference(SharedPreferences pref, BasicSetting basicSetting) {
		Editor editor = pref.edit();
		editor.putString(UkiukiWindowConstants.KEY_ACCOUNT, basicSetting.getAccount());
		editor.putString(UkiukiWindowConstants.KEY_PASSWORD, basicSetting.getPassword());
		editor.putInt(UkiukiWindowConstants.KEY_MAX_SCENE_OBJECT_NUM, basicSetting.getMaxSceneObjectNum());
		editor.commit();
		return;
	}
}
