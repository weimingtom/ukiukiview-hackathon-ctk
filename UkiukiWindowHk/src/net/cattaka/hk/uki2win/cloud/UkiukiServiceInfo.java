package net.cattaka.hk.uki2win.cloud;

/**
 * サービス一覧取得用
 * @author cattaka
 */
public class UkiukiServiceInfo {
	/** サービスID */
	private String sid;
	/** サービス名 */
	private String serviceName;
	/** サービスカテゴリ */
	private String serviceNumber;
	/** サービスアイコン */
	private String iconUri;
	/** サービス説明 */
	private String explain;
	/** サービス提供会社 */
	private String corporation;
	/** キャッチコピー */
	private String catchCopy;
	
	public UkiukiServiceInfo() {
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceNumber() {
		return serviceNumber;
	}

	public void setServiceNumber(String serviceNumber) {
		this.serviceNumber = serviceNumber;
	}

	public String getIconUri() {
		return iconUri;
	}

	public void setIconUri(String iconUri) {
		this.iconUri = iconUri;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public String getCorporation() {
		return corporation;
	}

	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}

	public String getCatchCopy() {
		return catchCopy;
	}

	public void setCatchCopy(String catchCopy) {
		this.catchCopy = catchCopy;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof UkiukiServiceInfo) {
			UkiukiServiceInfo usInfo = (UkiukiServiceInfo) o;
			if (this.sid != null && usInfo.getSid() != null) {
				return this.sid.equals(usInfo.getSid());
			}
		}
		return false;
	}
}
