package net.cattaka.hk.uki2win.setting;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

import net.cattaka.hk.uki2win.R;
import net.cattaka.hk.uki2win.UkiukiWindowConstants;
import net.cattaka.hk.uki2win.cloud.UkiukiServiceInfo;

public class ServiceInfoSetting {
	private String selectedSid;
	private List<UkiukiServiceInfo> ukiukiServiceInfoList;

	/**
	 * Spinner用に「表示」のアイテムを追加したリストを作成します。
	 * @param src
	 * @param resources
	 * @return
	 */
	public List<UkiukiServiceInfo> createUkiukiServiceInfoForSpinner(Resources resources) {
		List<UkiukiServiceInfo> result = new ArrayList<UkiukiServiceInfo>();
		
		UkiukiServiceInfo invisibleInfo = new UkiukiServiceInfo();
		invisibleInfo.setSid(UkiukiWindowConstants.SID_INVISIBLE);
		invisibleInfo.setServiceName(resources.getString(R.string.item_invisible));
		invisibleInfo.setServiceNumber("");
		invisibleInfo.setIconUri(null);
		invisibleInfo.setExplain("");
		invisibleInfo.setCorporation("");
		invisibleInfo.setCatchCopy("");
		
		result.add(invisibleInfo);
		result.addAll(this.ukiukiServiceInfoList);
		return result;
	}
	
	public ServiceInfoSetting() {
		this.ukiukiServiceInfoList = new ArrayList<UkiukiServiceInfo>();
	}

	public List<UkiukiServiceInfo> getUkiukiServiceInfoList() {
		return ukiukiServiceInfoList;
	}
	
	public UkiukiServiceInfo getSelectedUkiukiServiceInfo() {
		if (selectedSid == null) {
			return null;
		}
		for (UkiukiServiceInfo usInfo : this.ukiukiServiceInfoList) {
			if (selectedSid.equals(usInfo.getSid())) {
				return usInfo;
			}
		}
		return null;
	}

	public String getSelectedSid() {
		return selectedSid;
	}

	public void setSelectedSid(String selectedSid) {
		this.selectedSid = selectedSid;
	}

	public static ServiceInfoSetting loadPreference(SharedPreferences pref, Resources resources, boolean clearOnError) {
		ServiceInfoSetting serviceInfoSetting = new ServiceInfoSetting();
		try {
			String selectedSid = pref.getString(UkiukiWindowConstants.KEY_SELECTED_SID, resources.getString(R.string.default_service_id));
			serviceInfoSetting.setSelectedSid(selectedSid);
			
			int num = pref.getInt(UkiukiWindowConstants.KEY_SERVICE_INFO_NUM, 0);
			for (int i=0;i<num;i++) {
				UkiukiServiceInfo serviceInfo = new UkiukiServiceInfo();
				serviceInfo.setSid(pref.getString(UkiukiWindowConstants.KEY_SI_SID_BASE + i, ""));
				serviceInfo.setServiceName(pref.getString(UkiukiWindowConstants.KEY_SI_SERVICE_NAME_BASE + i, ""));
				serviceInfo.setServiceNumber(pref.getString(UkiukiWindowConstants.KEY_SI_SERVICE_NUMBER_BASE + i, ""));
				serviceInfo.setIconUri(pref.getString(UkiukiWindowConstants.KEY_SI_ICON_URI_BASE + i, ""));
				serviceInfo.setExplain(pref.getString(UkiukiWindowConstants.KEY_SI_EXPLAIN_BASE + i, ""));
				serviceInfo.setCorporation(pref.getString(UkiukiWindowConstants.KEY_SI_CORPORATION_BASE + i, ""));
				serviceInfo.setCatchCopy(pref.getString(UkiukiWindowConstants.KEY_SI_CATCH_COPY_BASE + i, ""));
				if (serviceInfo.getSid() != null && serviceInfo.getSid().length() > 0) {
					// SIDが空だと困るのでそこだけはチェックする
					serviceInfoSetting.getUkiukiServiceInfoList().add(serviceInfo);
				}
			}
		} catch (ClassCastException e) {
			if (clearOnError) {
				SharedPreferences.Editor editor = pref.edit();
				editor.clear();
				editor.commit();
			}
			// エラーの場合は初期値のみのものを返す
			serviceInfoSetting.setSelectedSid(resources.getString(R.string.default_service_id));
			serviceInfoSetting.getUkiukiServiceInfoList().clear();
			serviceInfoSetting.getUkiukiServiceInfoList().addAll(createInitialUkiukiServiceInfoList(resources));
		}
		
		if (serviceInfoSetting.getUkiukiServiceInfoList().size() == 0) {
			serviceInfoSetting.getUkiukiServiceInfoList().addAll(createInitialUkiukiServiceInfoList(resources));
		}
		
		// SelectedSidがサービス情報のリストに無いと困るので、無い場合は捏造しておく
		{
			boolean existsSelectedSid = false;
			for (UkiukiServiceInfo usInfo : serviceInfoSetting.getUkiukiServiceInfoList()) {
				if (usInfo.getSid().equals(serviceInfoSetting.getSelectedSid())) {
					existsSelectedSid = true;
					break;
				}
			}
			if (!existsSelectedSid) {
				UkiukiServiceInfo usInfo = new UkiukiServiceInfo();
				usInfo.setSid(serviceInfoSetting.getSelectedSid());
				usInfo.setServiceName(serviceInfoSetting.getSelectedSid());
				serviceInfoSetting.getUkiukiServiceInfoList().add(usInfo);
			}
		}
		
		return serviceInfoSetting;
	}
	public static void savePreference(SharedPreferences pref, ServiceInfoSetting serviceInfoSetting) {
		Editor editor = pref.edit();
		editor.putString(UkiukiWindowConstants.KEY_SELECTED_SID, serviceInfoSetting.getSelectedSid());
		
		editor.putInt(UkiukiWindowConstants.KEY_SERVICE_INFO_NUM, serviceInfoSetting.getUkiukiServiceInfoList().size());
		for (int i=0;i<serviceInfoSetting.getUkiukiServiceInfoList().size();i++) {
			UkiukiServiceInfo serviceInfo = serviceInfoSetting.getUkiukiServiceInfoList().get(i);
			editor.putString(UkiukiWindowConstants.KEY_SI_SID_BASE + i, serviceInfo.getSid());
			editor.putString(UkiukiWindowConstants.KEY_SI_SERVICE_NAME_BASE + i, serviceInfo.getServiceName());
			editor.putString(UkiukiWindowConstants.KEY_SI_SERVICE_NUMBER_BASE + i, serviceInfo.getServiceNumber());
			editor.putString(UkiukiWindowConstants.KEY_SI_ICON_URI_BASE + i, serviceInfo.getIconUri());
			editor.putString(UkiukiWindowConstants.KEY_SI_EXPLAIN_BASE + i, serviceInfo.getExplain());
			editor.putString(UkiukiWindowConstants.KEY_SI_CORPORATION_BASE + i, serviceInfo.getCorporation());
			editor.putString(UkiukiWindowConstants.KEY_SI_CATCH_COPY_BASE + i, serviceInfo.getCatchCopy());
		}
		editor.commit();
		return;
	}
	
	public static List<UkiukiServiceInfo> createInitialUkiukiServiceInfoList(Resources resources) {
		List<UkiukiServiceInfo> usInfoList = new ArrayList<UkiukiServiceInfo>();
		String[] sidArray				= resources.getStringArray(R.array.initial_sid_array);
		String[] serviceNumberArray	= resources.getStringArray(R.array.initial_service_number_array);
		String[] serviceNameArray		= resources.getStringArray(R.array.initial_service_name_array);
		String[] corporationArray		= resources.getStringArray(R.array.initial_corporation_array);
		String[] explainArray			= resources.getStringArray(R.array.initial_explain_array);
		String[] catchCopyArray			= resources.getStringArray(R.array.initial_catch_copy_array);
		String[] iconUriArray 			= resources.getStringArray(R.array.initial_icon_uri_array);
		for (int i=0;i<sidArray.length;i++) {
			String sid				= (i < sidArray.length) ? sidArray[i] : "";
			String serviceNumber	= (i < serviceNumberArray.length) ? serviceNameArray[i] : "";
			String serviceName		= (i < serviceNameArray.length) ? serviceNameArray[i] : "";
			String corporation		= (i < corporationArray.length) ? corporationArray[i] : "";
			String explain			= (i < explainArray.length) ? explainArray[i] : "";
			String catchCopy			= (i < catchCopyArray.length) ? catchCopyArray[i] : "";
			String iconUri 			= (i < iconUriArray.length) ? iconUriArray[i] : "";
			
			UkiukiServiceInfo usInfo = new UkiukiServiceInfo();
			usInfo.setSid(sid);
			usInfo.setServiceNumber(serviceNumber);
			usInfo.setServiceName(serviceName);
			usInfo.setCorporation(corporation);
			usInfo.setExplain(explain);
			usInfo.setCatchCopy(catchCopy);
			usInfo.setIconUri(iconUri);
			
			usInfoList.add(usInfo);
		}
		return usInfoList;
	}
}
