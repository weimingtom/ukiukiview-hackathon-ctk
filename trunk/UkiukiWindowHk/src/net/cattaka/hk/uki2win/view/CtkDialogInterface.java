package net.cattaka.hk.uki2win.view;

import android.app.Dialog;
import android.content.DialogInterface;

public interface CtkDialogInterface {
	public Dialog onCreateDialog();
	public void onPrepareDialog(int id, Dialog dialog);
	public void onDismiss(int id, DialogInterface dialog);
}
