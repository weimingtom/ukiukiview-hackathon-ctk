package net.cattaka.hk.uki2win.view;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

import net.cattaka.hk.uki2win.R;
import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo.CategoryInfo;
import net.cattaka.hk.uki2win.net.ImageCache;
import net.cattaka.hk.uki2win.net.MimeType;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class DropSceneObjectDialog implements OnClickListener, CtkDialogInterface  {
	public interface OnDropSceneObjectListener {
		public void onSceneObjectInfoCreated(SceneObjectInfo soInfo);
	}
	
	private static class RadioButtonInfo {
		public CheckableImageButton imageButton;
		public String code;
		public Bitmap bitmap;
	}

	private Dialog dialog;

	private Context context;
	private GeoPoint geoPoint;
	private String parentId;
	private OnDropSceneObjectListener onDropSceneObjectListener;
	private UkiukiContentsUsageInfo ukiukiContentsUsageInfo;
	private WebCacheUtil webCacheUtil;
	
	private List<RadioButtonInfo> radioButtonInfoList;
	
	public DropSceneObjectDialog(Context context) {
		super();
		this.context = context;
	}
	
	public Dialog onCreateDialog() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.drop_scene_object_dialog, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		this.dialog = builder.create();
		
		Button submitButton = (Button) view.findViewById(R.id.SubmitButton);
		Button cancelButton = (Button) view.findViewById(R.id.CancelButton);
		submitButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		
		return dialog;
	}
	
	public void onPrepareDialog(int id, Dialog dialog) {
		this.radioButtonInfoList = new ArrayList<RadioButtonInfo>();
		
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout ukiukiBallLinearLayout = (LinearLayout) dialog.findViewById(R.id.UkiukiBallRadioGroup);
		ukiukiBallLinearLayout.removeAllViews();
		for (CategoryInfo categoryInfo : ukiukiContentsUsageInfo.getCategoryInfoList()) {
			RadioButtonInfo rdInfo = new RadioButtonInfo();
			ImageCache imageCache = webCacheUtil.getImageCache(categoryInfo.iconUri);
			if (imageCache.getStatus() == ImageCache.STATUS_READY) {
				rdInfo.bitmap = imageCache.getBitmap();
			} else if (imageCache.getStatus() == ImageCache.STATUS_LOADING) {
				rdInfo.bitmap = webCacheUtil.getImageCache(UkiukiWindowConstants.RESOURCE_LOADING_URI).getBitmap();
			} else if (imageCache.getStatus() == ImageCache.STATUS_ERROR) {
				rdInfo.bitmap = webCacheUtil.getImageCache(UkiukiWindowConstants.RESOURCE_ERROR_URI).getBitmap();
			} else {
				rdInfo.bitmap = webCacheUtil.getImageCache(UkiukiWindowConstants.RESOURCE_UNKNOWN_URI).getBitmap();
			}
			
			rdInfo.imageButton = (CheckableImageButton) inflater.inflate(R.layout.drop_scene_object_dialog_item, null);
			rdInfo.imageButton.setImageBitmap(rdInfo.bitmap);
			rdInfo.imageButton.setTag(rdInfo);
			rdInfo.imageButton.setOnClickListener(this);
			rdInfo.code = categoryInfo.code;
			ukiukiBallLinearLayout.addView(rdInfo.imageButton);
			if (this.radioButtonInfoList.size() == 0) {
				rdInfo.imageButton.setChecked(true);
			}
			this.radioButtonInfoList.add(rdInfo);
		}

	
		EditText ukiukiBallCommentEdit = (EditText) dialog.findViewById(R.id.UkiukiBallCommentEdit);
		ukiukiBallCommentEdit.setText("");
	}
	
	public void onDismiss(int id, DialogInterface dialog) {
		// none
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.SubmitButton) {
			MimeType mimeInfo = new MimeType();
			{
				RadioButtonInfo rbInfo = null;
				for (RadioButtonInfo tRbInfo : this.radioButtonInfoList) {
					if (tRbInfo.imageButton.isChecked()) {
						rbInfo = tRbInfo;
						break;
					}
				}
				if (rbInfo == null) {
					// ありえないが念のため落としておく
					return;
				}
				mimeInfo.setType(UkiukiWindowConstants.UKIUKI_MIME_TYPE);
				mimeInfo.setSubType(rbInfo.code);
			}
			String comment;
			{
				EditText titleEdit = (EditText) dialog.findViewById(R.id.UkiukiBallCommentEdit);
				comment = titleEdit.getText().toString();
			}
			
			
			SceneObjectInfo soInfo = new SceneObjectInfo();
			soInfo.setObjectId("");
			soInfo.setGeoPoint(this.geoPoint);
			soInfo.setTitle(comment);
			soInfo.setMimeType(mimeInfo);
			soInfo.setParentId(this.parentId);
			if (onDropSceneObjectListener != null) {
				onDropSceneObjectListener.onSceneObjectInfoCreated(soInfo);
			}
			dialog.dismiss();
		} else if (v.getId() == R.id.CancelButton){
			dialog.dismiss();
		} else if (v.getTag() != null && v.getTag() instanceof RadioButtonInfo) {
			RadioButtonInfo rbInfo = (RadioButtonInfo) v.getTag();
			for (RadioButtonInfo tRbInfo : this.radioButtonInfoList) {
				tRbInfo.imageButton.setChecked(rbInfo == tRbInfo);
			}
		}
	}

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public OnDropSceneObjectListener getOnDropSceneObjectListener() {
		return onDropSceneObjectListener;
	}

	public void setOnDropSceneObjectListener(
			OnDropSceneObjectListener onDropSceneObjectListener) {
		this.onDropSceneObjectListener = onDropSceneObjectListener;
	}

	public void setUkiukiContentsUsageInfo(
			UkiukiContentsUsageInfo ukiukiContentsUsageInfo) {
		this.ukiukiContentsUsageInfo = ukiukiContentsUsageInfo;
	}

	public void setWebCacheUtil(WebCacheUtil webCacheUtil) {
		this.webCacheUtil = webCacheUtil;
	}
}
