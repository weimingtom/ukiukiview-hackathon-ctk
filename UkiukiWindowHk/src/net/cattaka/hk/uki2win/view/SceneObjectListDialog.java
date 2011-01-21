package net.cattaka.hk.uki2win.view;

import java.util.List;

import net.cattaka.hk.uki2win.R;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SceneObjectListDialog implements CtkDialogInterface, View.OnClickListener {
	public interface OnSceneObjectSelectListener {
		public void onSceneObjectSelect(SceneObjectInfo soInfo);
	}
	private AdapterView.OnItemClickListener onItemClickListenerImpl = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			ArrayAdapterEx adapter = (ArrayAdapterEx) parent.getAdapter();
			if (0 <= position && position < adapter.getCount()) {
				SceneObjectInfo soInfo = adapter.getItem(position);
				if (onSceneObjectSelectListener != null) {
					onSceneObjectSelectListener.onSceneObjectSelect(soInfo);
				}
				//Log.d(UkiukiWindowConstants.TAG, soInfo.getName());
				dialog.dismiss();
			}
		}
	};
	
	private Dialog dialog;
	private OnSceneObjectSelectListener onSceneObjectSelectListener;
	
	private static class SceneObjectLineBundle {
		ImageView imageView;
		TextView labelView;
		TextView detailView;

		void updateView(SceneObjectInfo soInfo, WebCacheUtil webCacheUtil) {
			ImageCache ic = webCacheUtil.getImageCache(soInfo.getIconUri());
			
			ic = webCacheUtil.filterImageCache(ic);
			imageView.setImageBitmap(ic.getBitmap());
			labelView.setText(soInfo.getTitle());
			detailView.setText(soInfo.getDetail());
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
			SceneObjectLineBundle bundle = null;

			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.scene_object_dialog_line,parent, false);

				bundle = new SceneObjectLineBundle();
				bundle.imageView = (ImageView) convertView.findViewById(R.id.SceneObjectImageView);
				bundle.labelView = (TextView) convertView	.findViewById(R.id.SceneObjectLabel);
				bundle.detailView = (TextView) convertView	.findViewById(R.id.SceneObjectDetail);

				convertView.setTag(bundle);
			} else {
				bundle = (SceneObjectLineBundle) convertView.getTag();
			}

			bundle.updateView(soInfo, webCacheUtil);
			return convertView;
		}
	}

	private Context context;
	private WebCacheUtil webCacheUtil;
	private List<SceneObjectInfo> sceneObjectInfoList;
	
	public SceneObjectListDialog(Context context, WebCacheUtil webCacheUtil) {
		super();
		this.context = context;
		this.webCacheUtil = webCacheUtil;
	}
	
	public Dialog onCreateDialog() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.scene_object_list_dialog, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		this.dialog = builder.create();
		
		ListView sceneObjectListView = (ListView) view.findViewById(R.id.SceneObjectListView);
		sceneObjectListView.setOnItemClickListener(onItemClickListenerImpl);
		
		view.findViewById(R.id.CloseButton).setOnClickListener(this);

		return dialog;
	}
	
	public void onPrepareDialog(int id, Dialog dialog) {
		ArrayAdapterEx adapter = new ArrayAdapterEx(context);
		if (this.sceneObjectInfoList != null) {
			for (SceneObjectInfo soInfo : this.sceneObjectInfoList) {
				adapter.add(soInfo);
			}
		}
		
		ListView listView = (ListView) dialog.findViewById(R.id.SceneObjectListView);
		listView.setAdapter(adapter);
	}
	
	public void onDismiss(int id, DialogInterface dialog) {
		// none
	}

	public void onClick(View v) {
		if (v.getId() == R.id.CloseButton) {
			dialog.dismiss();
		}
	}
	

	public OnSceneObjectSelectListener getOnSceneObjectSelectListener() {
		return onSceneObjectSelectListener;
	}

	public void setOnSceneObjectSelectListener(
			OnSceneObjectSelectListener onSceneObjectSelectListener) {
		this.onSceneObjectSelectListener = onSceneObjectSelectListener;
	}

	public void setSceneObjectInfoList(List<SceneObjectInfo> sceneObjectInfoList) {
		this.sceneObjectInfoList = sceneObjectInfoList;
	}
}
