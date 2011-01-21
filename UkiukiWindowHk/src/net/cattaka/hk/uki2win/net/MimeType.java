package net.cattaka.hk.uki2win.net;

public class MimeType {
	private String type;
	private String subType;
	
	public MimeType() {
	}
	
	public MimeType(String type, String subType) {
		super();
		this.type = type;
		this.subType = subType;
	}

	public static MimeType parse(String arg) {
		if (arg == null) {
			return null;
		}
		String[] tmp = arg.split("/");
		MimeType mi = new MimeType();
		mi.setType((tmp.length >= 1) ? tmp[0] : "");
		mi.setSubType((tmp.length >= 2) ? tmp[1] : "");
		return mi;
	}
	
	@Override
	public String toString() {
		return String.valueOf(type) + "/" + String.valueOf(subType);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}
}
