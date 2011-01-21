package net.cattaka.hk.uki2win.cloud;

import java.util.ArrayList;
import java.util.List;

public class ServiceSearchCondition {
	private List<String> genreCodeList = new ArrayList<String>();
	private String keyword;
	
	public List<String> getGenreCodeList() {
		return genreCodeList;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
