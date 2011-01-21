package net.cattaka.hk.uki2win.view;

import android.content.Context;

public class ConfirmForDeleteDialog extends ConfirmDialog {
	private String objectId;
	
	public ConfirmForDeleteDialog(Context context, int dialogId,
			int buttonType, int iconResourceId, int titleResourceId,
			int messageResourceId) {
		super(context, dialogId, buttonType, iconResourceId, titleResourceId,
				messageResourceId);
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
}
