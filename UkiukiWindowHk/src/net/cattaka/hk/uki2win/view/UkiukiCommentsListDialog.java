package net.cattaka.hk.uki2win.view;

import java.util.List;

import net.cattaka.hk.uki2win.R;
import net.cattaka.hk.uki2win.cloud.UkiukiCloudClient;
import net.cattaka.hk.uki2win.cloud.UkiukiGetChildContentsTask;
import net.cattaka.hk.uki2win.net.ImageCache;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import net.cattaka.hk.uki2win.scene.SceneObjectInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UkiukiCommentsListDialog
	implements View.OnClickListener,
		CtkDialogInterface
{
	public interface OnUkiukiCommentsListener {
		public void onUkiukiCommentsDelete(String objectId);
	}
	
	private UkiukiGetChildContentsTask.OnGetChildContentsListener onGetChildContentsListener = new UkiukiGetChildContentsTask.OnGetChildContentsListener() {
		public void onGetSceneObjectInfo(List<SceneObjectInfo> soInfoList, boolean finished) {
			if (soInfoList != null) {
				for (SceneObjectInfo soInfo : soInfoList) {
					adapter.add(soInfo);
				}
			}
			if (finished) {
				ukiukiGetChildContentsTask = null;
				setSpinnerVisible(false);
			}
		}
		
		public void onCancel() {
			ukiukiGetChildContentsTask = null;
		}
	};
	
	private Dialog dialog;
	private OnUkiukiCommentsListener onUkiukiCommentsListener;
	
	private static class UkiukiCommentsLineBundle {
		ImageView imageView;
		TextView labelView;
		TextView detailView;
		ImageButton deleteButton;
		SceneObjectInfo sceneObjectInfo;

		void updateView(UkiukiCloudClient ukiukiCloudClient, WebCacheUtil webCacheUtil) {
			ImageCache ic = webCacheUtil.getImageCache(sceneObjectInfo.getIconUri());
			
			ic = webCacheUtil.filterImageCache(ic);
			imageView.setImageBitmap(ic.getBitmap());
			labelView.setText(sceneObjectInfo.getTitle());
			detailView.setText(sceneObjectInfo.getDetail());
			
			String account = ukiukiCloudClient.getAccountUkiukiView();
			if (account != null && account.equals(sceneObjectInfo.getOwnerNickname())) {
				deleteButton.setVisibility(View.VISIBLE);
			} else {
				deleteButton.setVisibility(View.GONE);
			}
		}
	};
	
	private class ArrayAdapterEx extends ArrayAdapter<SceneObjectInfo> {
		private final LayoutInflater layoutInflater;

		public ArrayAdapterEx(Context context) {
			super(context, 0);
			layoutInflater = LayoutInflater.from(context);
		}

		// 画面に表示される毎に呼び出される
		@Override
		public View getView(int position, View convertView,ViewGroup parent) {
			SceneObjectInfo soInfo = getItem(position);
			UkiukiCommentsLineBundle bundle = null;

			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.ukiuki_comments_list_dialog_line,parent, false);

				bundle = new UkiukiCommentsLineBundle();
				bundle.imageView = (ImageView) convertView.findViewById(R.id.SceneObjectImageView);
				bundle.labelView = (TextView) convertView.findViewById(R.id.SceneObjectLabel);
				bundle.detailView = (TextView) convertView.findViewById(R.id.SceneObjectDetail);
				bundle.deleteButton = (ImageButton) convertView.findViewById(R.id.DeleteSceneObjectButton);
				bundle.deleteButton.setOnClickListener(UkiukiCommentsListDialog.this);

				convertView.setTag(bundle);
			} else {
				bundle = (UkiukiCommentsLineBundle) convertView.getTag();
			}
			bundle.sceneObjectInfo = soInfo;
			bundle.deleteButton.setTag(bundle);
			bundle.updateView(ukiukiCloudClient, webCacheUtil);
			return convertView;
		}
	}

	private Context context;
	private WebCacheUtil webCacheUtil;
	private UkiukiCloudClient ukiukiCloudClient;
	private SceneObjectInfo parentSceneObjectInfo;
	private UkiukiGetChildContentsTask ukiukiGetChildContentsTask;
	private ArrayAdapterEx adapter;
	
	public UkiukiCommentsListDialog(Context context, WebCacheUtil webCacheUtil, UkiukiCloudClient ukiukiCloudClient) {
		super();
		this.context = context;
		this.webCacheUtil = webCacheUtil;
		this.ukiukiCloudClient = ukiukiCloudClient;
	}
	
	public Dialog onCreateDialog() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.ukiuki_comments_list_dialog, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		this.dialog = builder.create();
		
		view.findViewById(R.id.CloseButton).setOnClickListener(this);
		
		return dialog;
	}
	
	public void onPrepareDialog(int id, Dialog dialog) {
		this.adapter = new ArrayAdapterEx(context);
		if (this.parentSceneObjectInfo != null) {
			adapter.add(parentSceneObjectInfo);
			// FIXME 最大数を決めうちしてるのを直す
			if (this.ukiukiGetChildContentsTask != null) {
				// ありえないが、古いのが残っていれば解放する
				ukiukiGetChildContentsTask.cancel(false);
				ukiukiGetChildContentsTask = null;
			}
			this.ukiukiGetChildContentsTask = ukiukiCloudClient.getChildContents(this.onGetChildContentsListener, this.parentSceneObjectInfo.getObjectId(), 200);
			setSpinnerVisible(true);
		}
		
		ListView listView = (ListView) dialog.findViewById(R.id.UkiukiCommentsListView);
		listView.setAdapter(adapter);
	}
	
	public void onDismiss(int id, DialogInterface dialog) {
		if (this.ukiukiGetChildContentsTask != null) {
			// 通信中なら止める
			ukiukiGetChildContentsTask.cancel(false);
			ukiukiGetChildContentsTask = null;
		}
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.CloseButton) {
			dialog.dismiss();
		} else if (v.getTag() != null && v.getTag() instanceof UkiukiCommentsLineBundle){
			UkiukiCommentsLineBundle bundle = (UkiukiCommentsLineBundle) v.getTag();
			dialog.dismiss();
			//Log.d(UkiukiWindowConstants.TAG, bundle.sceneObjectInfo.getTitle());
			if (this.onUkiukiCommentsListener != null) {
				this.onUkiukiCommentsListener.onUkiukiCommentsDelete(bundle.sceneObjectInfo.getObjectId());
			}
		}
	}
	
	private void setSpinnerVisible(boolean visible) {
		if (visible) {
			dialog.findViewById(R.id.LoadingCommentsProgressBar).setVisibility(View.VISIBLE);
		} else {
			dialog.findViewById(R.id.LoadingCommentsProgressBar).setVisibility(View.GONE);
		}
	}
	
	public void setParentSceneObjectInfo(SceneObjectInfo parentSceneObjectInfo) {
		this.parentSceneObjectInfo = parentSceneObjectInfo;
	}

	public void setOnUkiukiCommentsListener(
			OnUkiukiCommentsListener onUkiukiCommentsListener) {
		this.onUkiukiCommentsListener = onUkiukiCommentsListener;
	}

}
