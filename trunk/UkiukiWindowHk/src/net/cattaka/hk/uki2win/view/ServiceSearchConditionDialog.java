package net.cattaka.hk.uki2win.view;

import java.util.List;

import net.cattaka.hk.uki2win.R;
import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.cloud.ServiceSearchCondition;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo.CategoryInfo;
import net.cattaka.hk.uki2win.net.ImageCache;
import net.cattaka.hk.uki2win.net.WebCacheUtil;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class ServiceSearchConditionDialog
	implements CtkDialogInterface,
		CompoundButton.OnCheckedChangeListener,
		View.OnClickListener
{
	public interface OnServiceSearchConditionListener {
		public void onCreateServiceSearchCondition(ServiceSearchCondition serviceSearchCondition);
	}
	private static class ServiceGenreItem {
		public String code;
		public Uri iconUri;
		public String name;
		public boolean checked;
	}
	
	private class ArrayAdapterEx extends ArrayAdapter<ServiceGenreItem> {
		private final LayoutInflater layoutInflater;

		public ArrayAdapterEx(Context context) {
			super(context, 0);
			layoutInflater = LayoutInflater.from(context);
		}

		// 画面に表示される毎に呼び出される
		@Override
		public View getView(int position, View convertView,ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.service_search_genre_line, null);
			}
			
			ServiceGenreItem serviceGenreItem = getItem(position);
			convertView.setTag(serviceGenreItem);
			updateServiceGenreListView(convertView);
			
			return convertView;
		}
	}

	private Dialog dialog;
	private OnServiceSearchConditionListener onServiceSearchConditionListener;
	private Context context;
	private WebCacheUtil webCacheUtil;
	private ServiceSearchCondition serviceSearchCondition;
	private List<CategoryInfo> categoryInfoList;
	
	public ServiceSearchConditionDialog(Context context, WebCacheUtil webCacheUtil) {
		super();
		this.context = context;
		this.webCacheUtil = webCacheUtil;
	}
	
	public Dialog onCreateDialog() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.service_search_dialog, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		this.dialog = builder.create();
		
		view.findViewById(R.id.OkButton).setOnClickListener(this);
		view.findViewById(R.id.CancelButton).setOnClickListener(this);
		view.findViewById(R.id.SelectAllButton).setOnClickListener(this);
		view.findViewById(R.id.DeselectAllButton).setOnClickListener(this);
		
		return dialog;
	}
	
	public void onPrepareDialog(int id, Dialog dialog) {
		ArrayAdapterEx adapter = new ArrayAdapterEx(context);
		if (this.categoryInfoList != null) {
			for (CategoryInfo categoryInfo : this.categoryInfoList) {
				ServiceGenreItem serviceGenreItem = new ServiceGenreItem();
				serviceGenreItem.code = categoryInfo.code;
				serviceGenreItem.iconUri = categoryInfo.iconUri;
				serviceGenreItem.name = categoryInfo.name;
				serviceGenreItem.checked = false;
				
				adapter.add(serviceGenreItem);
			}
		}
		
		// 元となるServiceSearchConditionが与えられていれば、
		// それに合わせて初期値を更新する。
		if (this.serviceSearchCondition != null) {
			EditText keywordEdit = (EditText) dialog.findViewById(R.id.KeywordEdit);
			if (this.serviceSearchCondition.getKeyword() != null) {
				keywordEdit.setText(this.serviceSearchCondition.getKeyword());
			}
			for (int i=0;i<adapter.getCount();i++) {
				ServiceGenreItem item = adapter.getItem(i);
				item.checked = serviceSearchCondition.getGenreCodeList().contains(item.code);
			}
		}
		
		ListView listView = (ListView) dialog.findViewById(R.id.ServiceGenreListView);
		listView.setAdapter(adapter);
	}
	
	public void onDismiss(int id, DialogInterface dialog) {
		// none
	}

	public void onClick(View v) {
		if (v.getId() == R.id.OkButton) {
			if (this.onServiceSearchConditionListener != null) {
				ServiceSearchCondition serviceSearchCondition = createServiceSearchCondition();
				this.onServiceSearchConditionListener.onCreateServiceSearchCondition(serviceSearchCondition);
			}
			dialog.dismiss();
		} else if (v.getId() == R.id.CancelButton) {
			dialog.dismiss();
		} else if (v.getId() == R.id.SelectAllButton) {
			ListView listView = (ListView) dialog.findViewById(R.id.ServiceGenreListView);
			for (int i=0;i<listView.getCount();i++) {
				ServiceGenreItem item = (ServiceGenreItem) listView.getItemAtPosition(i);
				item.checked = true;
			}
			updateServiceGenreListView();
		} else if (v.getId() == R.id.DeselectAllButton) {
			ListView listView = (ListView) dialog.findViewById(R.id.ServiceGenreListView);
			for (int i=0;i<listView.getCount();i++) {
				ServiceGenreItem item = (ServiceGenreItem) listView.getItemAtPosition(i);
				item.checked = false;
			}
			updateServiceGenreListView();
		}
	}
	
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		ServiceGenreItem item  = (ServiceGenreItem) buttonView.getTag();
		item.checked = buttonView.isChecked();
	}
	
	private ServiceSearchCondition createServiceSearchCondition() {
		ServiceSearchCondition result = new ServiceSearchCondition();
		EditText keywordEdit = (EditText) dialog.findViewById(R.id.KeywordEdit);
		CharSequence keyword = keywordEdit.getText();
		if (keyword != null && keyword.length() > 0) {
			result.setKeyword(keyword.toString());
		}
		
		ListView listView = (ListView) dialog.findViewById(R.id.ServiceGenreListView);
		for (int i=0;i<listView.getCount();i++) {
			ServiceGenreItem item = (ServiceGenreItem) listView.getItemAtPosition(i);
			if (item.checked) {
				result.getGenreCodeList().add(item.code);
			}
		}
		return result;
	}
	
	private void updateServiceGenreListView() {
		ListView listView = (ListView) dialog.findViewById(R.id.ServiceGenreListView);
		for (int i=0;i<listView.getChildCount();i++) {
			updateServiceGenreListView(listView.getChildAt(i));
		}
	}

	private void updateServiceGenreListView(View view) {
		ImageView imageView = (ImageView) view.findViewById(R.id.GenreImageView);
		CheckBox nameView = (CheckBox) view.findViewById(R.id.GenreCheckBox);
		ServiceGenreItem item  = (ServiceGenreItem) view.getTag();
		
		nameView.setText(item.name);
		nameView.setChecked(item.checked);
		nameView.setOnCheckedChangeListener(this);
		nameView.setTag(item);
		
		ImageCache imageCache = webCacheUtil.getImageCache(item.iconUri);
		if (imageCache.getStatus() == ImageCache.STATUS_READY) {
			// OK
		} else if (imageCache.getStatus() == ImageCache.STATUS_INIT) {
			imageCache = webCacheUtil.getImageCache(UkiukiWindowConstants.RESOURCE_LOADING_URI);
		} else if (imageCache.getStatus() == ImageCache.STATUS_LOADING) {
			imageCache = webCacheUtil.getImageCache(UkiukiWindowConstants.RESOURCE_LOADING_URI);
		} else if (imageCache.getStatus() == ImageCache.STATUS_ERROR) {
			imageCache = webCacheUtil.getImageCache(UkiukiWindowConstants.RESOURCE_ERROR_URI);
		} else {
			imageCache = webCacheUtil.getImageCache(UkiukiWindowConstants.RESOURCE_UNKNOWN_URI);
		}
		imageView.setImageBitmap(imageCache.getBitmap());
	}

	public OnServiceSearchConditionListener getOnServiceSearchConditionListener() {
		return onServiceSearchConditionListener;
	}

	public void setOnServiceSearchConditionListener(
			OnServiceSearchConditionListener onServiceSearchConditionListener) {
		this.onServiceSearchConditionListener = onServiceSearchConditionListener;
	}

	public void setCategoryInfoList(List<CategoryInfo> categoryInfoList) {
		this.categoryInfoList = categoryInfoList;
	}

	public void setServiceSearchCondition(
			ServiceSearchCondition serviceSearchCondition) {
		this.serviceSearchCondition = serviceSearchCondition;
	}
}
