package net.cattaka.hk.uki2win.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfirmDialog implements CtkDialogInterface {
	public static final int BUTTON_YES = 1 << 1;
	public static final int BUTTON_NO = 1 << 2;
	public static final int BUTTON_OK = 1 << 3;
	public static final int BUTTON_CANCEL = 1 << 4;
	
	public interface OnConfirmListener {
		public void onConfirm(int dialogId, int button);
	}
	
	class OnClickListenerImpl implements DialogInterface.OnClickListener {
		private int buttonType;
		public OnClickListenerImpl(int buttonType) {
			super();
			this.buttonType = buttonType;
		}
		public void onClick(DialogInterface dialog, int which) {
			if (onConfirmListener != null) {
				onConfirmListener.onConfirm(dialogId, buttonType);
			}
			dialog.dismiss();
		}
	}
	
	private Dialog dialog;
	private Context context;
	private OnConfirmListener onConfirmListener;
	private int dialogId;
	private int buttonType;
	private int iconResourceId = -1;
	private int titleResourceId = -1;
	private int messageResourceId = -1;
	
	public ConfirmDialog(Context context, int dialogId, int buttonType, int iconResourceId, int titleResourceId, int messageResourceId) {
		super();
		this.context = context;
		this.dialogId = dialogId;
		this.buttonType = buttonType;
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
		
		if ((buttonType & BUTTON_YES) != 0) {
			builder.setPositiveButton(android.R.string.yes, new OnClickListenerImpl(BUTTON_YES));
		}
		if ((buttonType & BUTTON_OK) != 0) {
			builder.setPositiveButton(android.R.string.ok, new OnClickListenerImpl(BUTTON_OK));
		}
		if ((buttonType & BUTTON_NO) != 0) {
			builder.setNegativeButton(android.R.string.no, new OnClickListenerImpl(BUTTON_NO));
		}
		if ((buttonType & BUTTON_CANCEL) != 0) {
			builder.setNegativeButton(android.R.string.cancel, new OnClickListenerImpl(BUTTON_CANCEL));
		}
			
		this.dialog = builder.create();
		return dialog;
	}
	
	public void onPrepareDialog(int id, Dialog dialog) {
	}
	public void onDismiss(int id, DialogInterface dialog) {
	}

	public OnConfirmListener getOnConfirmListener() {
		return onConfirmListener;
	}

	public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
		this.onConfirmListener = onConfirmListener;
	}
	
}
