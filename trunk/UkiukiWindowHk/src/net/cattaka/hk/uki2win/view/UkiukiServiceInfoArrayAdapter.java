package net.cattaka.hk.uki2win.view;

import java.util.List;

import net.cattaka.hk.uki2win.R;
import net.cattaka.hk.uki2win.cloud.UkiukiServiceInfo;
import net.cattaka.hk.uki2win.utils.StringUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UkiukiServiceInfoArrayAdapter extends ArrayAdapter<UkiukiServiceInfo> {

	public UkiukiServiceInfoArrayAdapter(Context context, List<UkiukiServiceInfo> objects) {
		super(context, R.layout.spinner_item_textview, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.spinner_item_textview, null);
		}
		UkiukiServiceInfo usInfo = getItem(position);
		((TextView) convertView).setText(usInfo.getServiceName());
		
		return convertView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.service_info_line, null);
		}
		UkiukiServiceInfo usInfo = getItem(position);
		((TextView) convertView.findViewById(R.id.ServiceNameLabel)).setText(StringUtil.parseString(usInfo.getServiceName()));
		((TextView) convertView.findViewById(R.id.ServiceIdLabel)).setText(StringUtil.parseString(usInfo.getSid()));
		((TextView) convertView.findViewById(R.id.ServiceExplainLabel)).setText(StringUtil.parseString(usInfo.getExplain()));
		((TextView) convertView.findViewById(R.id.ServiceCorporationLabel)).setText(StringUtil.parseString(usInfo.getCorporation()));
		
		return convertView;
	}
}
