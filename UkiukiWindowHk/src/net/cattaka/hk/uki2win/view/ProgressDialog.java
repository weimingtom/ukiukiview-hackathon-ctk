package net.cattaka.hk.uki2win.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class ProgressDialog implements DialogInterface.OnClickListener {
	public static final int BUTTON_CANCEL = 1 << 4;
	
	private Dialog dialog;
	private Context context;
	private boolean cancelable = false;
	private int iconResourceId = -1;
	private int titleResourceId = -1;
	private int messageResourceId = -1;
	
	public ProgressDialog(Context context, int dialogId, boolean cancelable, int iconResourceId, int titleResourceId, int messageResourceId) {
		super();
		this.context = context;
		this.cancelable = cancelable;
		this.iconResourceId = iconResourceId;
		this.titleResourceId = titleResourceId;
		this.messageResourceId = messageResourceId;
	}
	
	public Dialog onCreateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (iconResourceId != -1) {
			builder.setIcon(iconResourceId);
		}
		if (titleResourceId != -1) {
			builder.setTitle(titleResourceId);
		}
		if (messageResourceId != -1) {
			builder.setMessage(messageResourceId);
		}
		
		if (cancelable) {
			builder.setNegativeButton(android.R.string.cancel, this);
		}
		
		this.dialog = builder.create();
		this.dialog.setCancelable(cancelable);
		return dialog;
	}
	
	public void onPrepareDialog(Dialog dialog) {
		// none
	}

	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
	}
}
