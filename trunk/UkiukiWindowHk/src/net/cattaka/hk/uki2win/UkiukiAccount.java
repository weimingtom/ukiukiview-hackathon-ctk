package net.cattaka.hk.uki2win;

import net.cattaka.hk.uki2win.R;
import net.cattaka.hk.uki2win.cloud.UkiukiAccountRegistTask;
import net.cattaka.hk.uki2win.cloud.UkiukiCloudClient;
import net.cattaka.hk.uki2win.cloud.UkiukiAccountRegistTask.OnAccountRegistListener;
import net.cattaka.hk.uki2win.utils.ActivityUtil;
import net.cattaka.hk.uki2win.utils.StringUtil;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UkiukiAccount extends Activity
	implements View.OnClickListener,
		DialogInterface.OnDismissListener
{
	public static final int DIALOG_CREATE_PROGRESS = 1;
	
	private ProgressDialog progressDialog;
	private UkiukiAccountRegistTask accountRegistTask;
	private OnAccountRegistListener onAccountRegistListener = new OnAccountRegistListener() {
		public void onSucceed() {
			Toast.makeText(UkiukiAccount.this, R.string.msg_registering_account_succeed, Toast.LENGTH_LONG).show();
			saveAndDissmiss();
			finish();
		}
		
		public void onFailed() {
			Toast.makeText(UkiukiAccount.this, R.string.msg_registering_account_failed, Toast.LENGTH_LONG).show();
			finish();
		}
		
		public void onCancel() {
			finish();
		}
		private void finish() {
			if (accountRegistTask != null) {
				accountRegistTask = null;
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	};
	private UkiukiCloudClient ukiukiCloudClient;
	
	private EditText accountEdit;
	private EditText passwordEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.account_setting);

		Resources resources = getResources();
		String apiKeyUkiukiView = resources.getString(R.string.api_key_ukiuki_view);

		this.ukiukiCloudClient = new UkiukiCloudClient(ActivityUtil.getModel(), apiKeyUkiukiView);

		this.accountEdit = (EditText) findViewById(R.id.NicknameEdit);
		this.passwordEdit = (EditText) findViewById(R.id.PasswordEdit);
		
		Button createButton = (Button) findViewById(R.id.CreateButton);
		Button applyButton = (Button) findViewById(R.id.ApplyButton);
		createButton.setOnClickListener(this);
		applyButton.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String account = pref.getString(UkiukiWindowConstants.KEY_ACCOUNT, "");
		String password = pref.getString(UkiukiWindowConstants.KEY_PASSWORD, "");
		
		this.accountEdit.setText(account);
		this.passwordEdit.setText(password);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.CreateButton) {
			showDialog(DIALOG_CREATE_PROGRESS);
		} else if (v.getId() == R.id.ApplyButton) {
			saveAndDissmiss();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_CREATE_PROGRESS) {
			Resources resources = getResources();
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setCancelable(true);
			this.progressDialog.setMessage(resources.getString(R.string.msg_registering));
			this.progressDialog.setOnDismissListener(this);
			return this.progressDialog;
		}
		return null;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == DIALOG_CREATE_PROGRESS) {
			String account = String.valueOf(accountEdit.getText());
			String password = StringUtil.getMd5String(String.valueOf(passwordEdit.getText()));
			
			this.accountRegistTask = this.ukiukiCloudClient.accountRegist(this.onAccountRegistListener, account, password);
		}
	}
	
	public void onDismiss(DialogInterface dialog) {
		if (accountRegistTask != null) {
			accountRegistTask.cancel(false);
		}
	}
	
	private void saveAndDissmiss() {
		super.onResume();
		String account = String.valueOf(this.accountEdit.getText());
		String password = String.valueOf(this.passwordEdit.getText());
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		editor.putString(UkiukiWindowConstants.KEY_ACCOUNT, account);
		editor.putString(UkiukiWindowConstants.KEY_PASSWORD, password);
		editor.commit();
		
		this.finish();
	}
}
