package net.cattaka.hk.uki2win.scene;

import net.cattaka.hk.uki2win.net.MimeType;
import android.net.Uri;
import com.google.android.maps.GeoPoint;

public class SceneObjectInfo {
	private GeoPoint geoPoint;
	private String objectId;
	private String parentId;
	private String ownerNickname;
	private MimeType mimeType;
	private String title;
	private String detail;
	private String content;
	private Uri infoUri;
	private Uri iconUri;
	private Uri couponUri;
	private boolean commentable;
	private int numOfComments;
	
	public SceneObjectInfo() {
	}

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getOwnerNickname() {
		return ownerNickname;

	}
	public void setOwnerNickname(String ownerNickname) {
		this.ownerNickname = ownerNickname;
	}

	public MimeType getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDetail() {
		return detail;
	}
	
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Uri getInfoUri() {
		return infoUri;
	}
	
	public void setInfoUri(Uri infoUri) {
		this.infoUri = infoUri;
	}
	public Uri getIconUri() {
		return iconUri;
	}
	public void setIconUri(Uri iconUri) {
		this.iconUri = iconUri;
	}
	public Uri getCouponUri() {
		return couponUri;
	}
	public void setCouponUri(Uri couponUri) {
		this.couponUri = couponUri;
	}
	public boolean isCommentable() {
		return commentable;
	}
	public void setCommentable(boolean commentable) {
		this.commentable = commentable;
	}
	public int getNumOfComments() {
		return numOfComments;
	}
	public void setNumOfComments(int numOfComments) {
		this.numOfComments = numOfComments;
	}
}
