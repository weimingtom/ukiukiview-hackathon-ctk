package net.cattaka.hk.uki2win;

import java.util.List;

import net.cattaka.hk.uki2win.R;
import net.cattaka.hk.uki2win.cloud.UkiukiCloudClient;
import net.cattaka.hk.uki2win.cloud.UkiukiGetServiceInfoTask;
import net.cattaka.hk.uki2win.cloud.UkiukiServiceInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiGetServiceInfoTask.OnGetServiceInfoListener;
import net.cattaka.hk.uki2win.setting.BasicSetting;
import net.cattaka.hk.uki2win.setting.ServiceInfoSetting;
import net.cattaka.hk.uki2win.utils.ActivityUtil;
import net.cattaka.hk.uki2win.view.UkiukiServiceInfoArrayAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

public class UkiukiBasicSetting extends Activity
	implements View.OnClickListener,
		DialogInterface.OnDismissListener
{
	public static final int DIALOG_UPDATE_SERVICE_LIST_PROGRESS = 1;
	
	private OnGetServiceInfoListener onGetServiceInfoListener = new UkiukiGetServiceInfoTask.OnGetServiceInfoListener() {
		public void onGetServiceInfo(List<UkiukiServiceInfo> usInfoList) {
			serviceInfoSetting.getUkiukiServiceInfoList().clear();
			serviceInfoSetting.getUkiukiServiceInfoList().addAll(usInfoList);
			updateDefaultServiceSpinner(true);
			Toast.makeText(UkiukiBasicSetting.this, R.string.msg_downloading_service_info_succeed, Toast.LENGTH_LONG).show();
			finish();
		}
		public void onCancel() {
			finish();
		}
		private void finish() {
			if (ukiukiGetServiceInfoTask != null) {
				ukiukiGetServiceInfoTask = null;
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	};
	
	private ProgressDialog progressDialog;
	private Spinner maxSceneObjectNumSpinner;
	private Spinner defaultServiceSpinner;
	
	private UkiukiCloudClient ukiukiCloudClient;
	private UkiukiGetServiceInfoTask ukiukiGetServiceInfoTask;
	
	private BasicSetting basicSetting;
	private ServiceInfoSetting serviceInfoSetting;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basic_setting);
		
		Resources resources = getResources();
		String apiKeyUkiukiView = resources.getString(R.string.api_key_ukiuki_view);

		this.ukiukiCloudClient = new UkiukiCloudClient(ActivityUtil.getModel(), apiKeyUkiukiView);
		
		this.maxSceneObjectNumSpinner = (Spinner) findViewById(R.id.MaxSceneObjectNumSpinner);
		this.defaultServiceSpinner = (Spinner) findViewById(R.id.DefaultServiceSpinner);
		
		// イベントハンドラを設定
		findViewById(R.id.UpdateServiceList).setOnClickListener(this);
		findViewById(R.id.OkButton).setOnClickListener(this);
		findViewById(R.id.CancelButton).setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		loadPreference();
		
		for (int i=0;i<this.maxSceneObjectNumSpinner.getCount();i++) {
			if (this.maxSceneObjectNumSpinner.getItemAtPosition(i).equals(String.valueOf(this.basicSetting.getMaxSceneObjectNum()))) {
				this.maxSceneObjectNumSpinner.setSelection(i);
				break;
			}
		}
		
		updateDefaultServiceSpinner(false);
		this.defaultServiceSpinner.setSelection(this.defaultServiceSpinner.getSelectedItemPosition());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_UPDATE_SERVICE_LIST_PROGRESS) {
			Resources resources = getResources();
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setCancelable(true);
			this.progressDialog.setMessage(resources.getString(R.string.msg_downloading_service_info_now));
			this.progressDialog.setOnDismissListener(this);
			return this.progressDialog;
		} else {
			// error
			return super.onCreateDialog(id);
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == DIALOG_UPDATE_SERVICE_LIST_PROGRESS) {
			this.ukiukiGetServiceInfoTask = ukiukiCloudClient.getServiceList(this.onGetServiceInfoListener);
		} else {
			// error
			super.onPrepareDialog(id, dialog);
		}
	}
	
	public void onDismiss(DialogInterface dialog) {
		if (ukiukiGetServiceInfoTask != null) {
			ukiukiGetServiceInfoTask.cancel(false);
		}
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.UpdateServiceList:
		{
			showDialog(DIALOG_UPDATE_SERVICE_LIST_PROGRESS);
			break;
		}
		case R.id.OkButton:
		{
			savePreference();
			finish();
			break;
		}
		case R.id.CancelButton:
		{
			finish();
			break;
		}
		}
	}
	
	/**
	 * @param direct バグ対応、これを入れておかないと正しく更新されなかったり、レイアウトが崩れる
	 */
	private void updateDefaultServiceSpinner(boolean direct) {
		String currentSelectedSid = this.serviceInfoSetting.getSelectedSid();
		UkiukiServiceInfo currentUsInfo = (UkiukiServiceInfo)this.defaultServiceSpinner.getSelectedItem();
		if (currentUsInfo != null && currentUsInfo.getSid() != null) {
			currentSelectedSid = currentUsInfo.getSid();
		}
		
		UkiukiServiceInfoArrayAdapter adapter = new UkiukiServiceInfoArrayAdapter(this, this.serviceInfoSetting.getUkiukiServiceInfoList());
		this.defaultServiceSpinner.setAdapter(adapter);
		if (currentSelectedSid != null) {
			// 現在選択中の物を選択状態にしておく（nullに鳴ることは無いはずだが、、）
			for (int i=0;i<this.serviceInfoSetting.getUkiukiServiceInfoList().size();i++) {
				UkiukiServiceInfo usInfo = this.serviceInfoSetting.getUkiukiServiceInfoList().get(i);
				if (currentSelectedSid.equals(usInfo.getSid())) {
					if (direct) {
						this.defaultServiceSpinner.setSelection(i, false);
					} else {
						this.defaultServiceSpinner.setSelection(i);
					}
					break;
				}
			}
		}
	}
	
	private void loadPreference() {
		this.basicSetting = BasicSetting.loadPreference(PreferenceManager.getDefaultSharedPreferences(this), true);
		this.serviceInfoSetting = ServiceInfoSetting.loadPreference(this.getSharedPreferences(UkiukiWindowConstants.PREF_NAME_SERVICE_INFO, MODE_PRIVATE), this.getResources(), true);
	}
	private void savePreference() {
		try {
			this.basicSetting.setMaxSceneObjectNum(Integer.parseInt(String.valueOf(this.maxSceneObjectNumSpinner.getSelectedItem())));
		} catch (NumberFormatException e) {
			// 落ちたら知らん
		}
		UkiukiServiceInfo currentUsInfo = (UkiukiServiceInfo)this.defaultServiceSpinner.getSelectedItem();
		if (currentUsInfo != null && currentUsInfo.getSid() != null) {
			this.serviceInfoSetting.setSelectedSid(currentUsInfo.getSid());
		}
		
		BasicSetting.savePreference(PreferenceManager.getDefaultSharedPreferences(this), this.basicSetting);
		ServiceInfoSetting.savePreference(this.getSharedPreferences(UkiukiWindowConstants.PREF_NAME_SERVICE_INFO, MODE_PRIVATE), this.serviceInfoSetting);
	}
}
