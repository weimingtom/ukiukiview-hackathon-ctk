package net.cattaka.hk.uki2win.cloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.Uri;

public class UkiukiContentsUsageInfo {
	public static class CategoryInfo {
		public String code;
		public Uri iconUri;
		public String name;
	}
	
	public static class RangeInfo implements Comparable<RangeInfo> {
		public String param;
		public float distance;
		
		public int compareTo(RangeInfo another) {
			if (this.distance < another.distance) {
				return -1;
			} else if (this.distance > another.distance) {
				return 1;
			} else {
				// NaNやInfinityは無視
				return 0;
			}
		}
	}
	
	private List<CategoryInfo> categoryInfoList;
	private Map<String, CategoryInfo> categoryInfoMap;
	private List<RangeInfo> rangeInfoList;

	public UkiukiContentsUsageInfo() {
		this.categoryInfoList = new ArrayList<CategoryInfo>();
		this.categoryInfoMap = new HashMap<String, CategoryInfo>();
		this.rangeInfoList = new ArrayList<RangeInfo>();
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
