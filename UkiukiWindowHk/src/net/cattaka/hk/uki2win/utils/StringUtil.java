package net.cattaka.hk.uki2win.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
	public static String parseString(String src) {
		return (src != null) ? src : "";
	}
	
	public static String getMd5String(String source) {
		String ret = null;
		MessageDigest aMd5Digester;

		try {
			aMd5Digester = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return ret;
		}
		byte aSignatureBytes[] = aMd5Digester.digest(source.getBytes());

		StringBuffer aDigestedString = new StringBuffer();
		for (byte aByte : aSignatureBytes) {
			aDigestedString.append(String.format("%02x", 0xFF & aByte));
		}
		return aDigestedString.toString();
	}
}
