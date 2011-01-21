package net.cattaka.hk.uki2win.cloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo.CategoryInfo;
import net.cattaka.hk.uki2win.cloud.UkiukiContentsUsageInfo.RangeInfo;

public class UkiukiServiceGenreInfo {
	private String sid;

	private List<CategoryInfo> categoryInfoList;
	private Map<String, CategoryInfo> categoryInfoMap;
	private List<RangeInfo> rangeInfoList;

	public UkiukiServiceGenreInfo() {
		this.categoryInfoList = new ArrayList<CategoryInfo>();
		this.categoryInfoMap = new HashMap<String, CategoryInfo>();
		this.rangeInfoList = new ArrayList<RangeInfo>();
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getAdequateRangeParam(float range) {
		if (this.rangeInfoList.size() > 0) {
			RangeInfo lastRangeInfo = this.rangeInfoList.get(0);;
			for (int i=0;i<this.rangeInfoList.size();i++) {
				lastRangeInfo = this.rangeInfoList.get(i);
				if (range < lastRangeInfo.distance) {
					break;
				}
			}
			//Log.d(UkiukiWindowConstants.TAG, String.valueOf(range) + " : " + lastRangeInfo.param);
			return lastRangeInfo.param;
		} else {
			// 取り合えず1000mを返しておく
			return "6";
		}
	}

	public void addCategoryInfo(CategoryInfo categoryInfo) {
		this.categoryInfoList.add(categoryInfo);
		this.categoryInfoMap.put(categoryInfo.code, categoryInfo);
	}
	
	public CategoryInfo getCategoryInfo(String code) {
		return categoryInfoMap.get(code);
	}
	
	public List<CategoryInfo> getCategoryInfoList() {
		return categoryInfoList;
	}

	public List<RangeInfo> getRangeInfoList() {
		return rangeInfoList;
	}
	public void sortRangeInfoList() {
		Collections.sort(this.rangeInfoList);
	}
}
