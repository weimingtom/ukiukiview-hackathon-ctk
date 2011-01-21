package jp.bs.app.ukiukiview;

import jp.bs.app.ukiukiview.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
public class SettingActivity extends PreferenceActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.pref);
    }
}