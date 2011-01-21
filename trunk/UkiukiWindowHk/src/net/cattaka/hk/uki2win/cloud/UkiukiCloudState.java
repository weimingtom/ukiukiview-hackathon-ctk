package net.cattaka.hk.uki2win.cloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UkiukiCloudState {
	private String apiKeyUkiukiView;
	private String sessionCodeUkiukiView;
	private String accountUkiukiView; 
	private String model;
	private UkiukiContentsUsageInfo ukiukiContentsUsageInfo;
	private Map<String, UkiukiServiceGenreInfo> ukiukiServiceGenreInfoMap;
	
	/**
	 * SID順の並べ替え用
	 * @author cattaka
	 */
	static class UkiukiServiceGenreInfoComplarator implements Comparator<UkiukiServiceGenreInfo> {
		public int compare(UkiukiServiceGenreInfo o1, UkiukiServiceGenreInfo o2) {
			if (o1.getSid() == null) {
				if (o2.getSid() == null) {
					return -1;
				} else {
					return -1;
				}
			} else {
				if (o2.getSid() == null) {
					return 1;
				} else {
					return o1.getSid().compareTo(o2.getSid());
				}
			}
		}
	}
	
	public UkiukiCloudState() {
		this.ukiukiServiceGenreInfoMap = new HashMap<String, UkiukiServiceGenreInfo>();
	}
	
	public String getApiKeyUkiukiView() {
		return apiKeyUkiukiView;
	}
	public void setApiKeyUkiukiView(String apiKeyUkiukiView) {
		this.apiKeyUkiukiView = apiKeyUkiukiView;
	}
	public String getSessionCodeUkiukiView() {
		return sessionCodeUkiukiView;
	}
	public void setSessionCodeUkiukiView(String sessionCodeUkiukiView) {
		this.sessionCodeUkiukiView = sessionCodeUkiukiView;
	}
	public String getAccountUkiukiView() {
		return accountUkiukiView;
	}
	public void setAccountUkiukiView(String accountUkiukiView) {
		this.accountUkiukiView = accountUkiukiView;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public void addUkiukiServiceGenreInfo(UkiukiServiceGenreInfo usgInfo) {
		ukiukiServiceGenreInfoMap.put(usgInfo.getSid(), usgInfo);
	}
	public UkiukiContentsUsageInfo getUkiukiContentsUsageInfo() {
		return ukiukiContentsUsageInfo;
	}
	public void setUkiukiContentsUsageInfo(
			UkiukiContentsUsageInfo ukiukiContentsUsageInfo) {
		this.ukiukiContentsUsageInfo = ukiukiContentsUsageInfo;
	}
	public UkiukiServiceGenreInfo getUkiukiServiceGenreInfo(String key) {
		return ukiukiServiceGenreInfoMap.get(key);
	}
	public List<UkiukiServiceGenreInfo> getSortedUkiukiServiceGenreInfoList() {
		ArrayList<UkiukiServiceGenreInfo> result = new ArrayList<UkiukiServiceGenreInfo>();
		result.addAll(ukiukiServiceGenreInfoMap.values());
		Collections.sort(result, new UkiukiServiceGenreInfoComplarator());
		
		return result;
	}
}
