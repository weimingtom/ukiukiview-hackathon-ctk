package net.cattaka.hk.uki2win.math;

import net.cattaka.hk.uki2win.UkiukiWindowConstants;

import com.google.android.maps.GeoPoint;

public class Hubeny {
//	// 長半径(WGS84)
//	private static final double a = 6378137D;
//	// 扁平率(WGS84)
//	private static final double f = 1D / 298.257222101D;

	// 日本測地系の場合
	// 長半径(WGS84)
	// private static double a = 6377397.155D;
	// 扁平率(WGS84)
	// private static double f = 1D / 299.152813D;

	// GoogleMap系の場合
	// 長半径(WGS84)
	private static double a = UkiukiWindowConstants.CIRCUMFERENCE_OF_EARTH / (Math.PI*2);
	// 扁平率(WGS84)
	private static double f = 0;

	/**
	 * 
	 * @param dst dst[0]=dx,dst[1]=dy
	 * @param from
	 * @param to
	 */
	public static void convertToMeter(float[] dst, GeoPoint start, GeoPoint end) {
		double latStart = start.getLatitudeE6() / 1E6;
		double lonStart = start.getLongitudeE6() / 1E6;
		double latEnd = end.getLatitudeE6() / 1E6;
		double lonEnd = end.getLongitudeE6() / 1E6;
		
		// 緯度経度をラジアンに変換
		double radLatStart = latStart * Math.PI / 180D;
		double radLonStart = lonStart * Math.PI / 180D;
		double radLatEnd = latEnd * Math.PI / 180D;
		double radLonEnd = lonEnd * Math.PI / 180D;

		// 二点間の平均緯度（ラジアン）
		double avgLat = (radLatStart + radLatEnd) / 2D;

		// 第一離心率
		double e;
		if (f != 0) {
			// 扁平率の逆数
			double F = 1D / f;
			e = (Math.sqrt(2 * F - 1)) / F;
		} else {
			e = 0;
		}

		double W = Math.sqrt(1 - Math.pow(e, 2) * Math.pow(Math.sin(avgLat), 2));

		// 子午線曲率半径
		double M = (a * (1 - Math.pow(e, 2))) / Math.pow(W, 3);

		// 卯酉線曲率半径
		double N = a / W;

		// 2点間の緯度差(ラジアン)
		double dLat = radLatStart - radLatEnd;

		// 2点間の経度差(ラジアン)
		double dLon = radLonStart - radLonEnd;
		
		dst[0] = (float)(N * Math.cos(avgLat) * dLon);
		dst[1] = (float)(M * dLat);
	}

	public static double getDistance(float[] dst, GeoPoint start, GeoPoint end) {
		double latStart = start.getLatitudeE6() / 1E6;
		double lonStart = start.getLongitudeE6() / 1E6;
		double latEnd = end.getLatitudeE6() / 1E6;
		double lonEnd = end.getLongitudeE6() / 1E6;
		
		// 緯度経度をラジアンに変換
		double radLatStart = latStart * Math.PI / 180D;
		double radLonStart = lonStart * Math.PI / 180D;
		double radLatEnd = latEnd * Math.PI / 180D;
		double radLonEnd = lonEnd * Math.PI / 180D;

		// 二点間の平均緯度（ラジアン）
		double avgLat = (radLatStart + radLatEnd) / 2D;

		// 第一離心率
		double e;
		if (f != 0) {
			// 扁平率の逆数
			double F = 1D / f;
			e = (Math.sqrt(2 * F - 1)) / F;
		} else {
			e = 0;
		}

		double W = Math.sqrt(1 - Math.pow(e, 2) * Math.pow(Math.sin(avgLat), 2));

		// 子午線曲率半径
		double M = (a * (1 - Math.pow(e, 2))) / Math.pow(W, 3);

		// 卯酉線曲率半径
		double N = a / W;

		// 2点間の緯度差(ラジアン)
		double dLat = radLatStart - radLatEnd;

		// 2点間の経度差(ラジアン)
		double dLon = radLonStart - radLonEnd;

		dst[0] = (float)(N * Math.cos(avgLat) * dLon);
		dst[1] = (float)(M * dLat);

		// 2点間の距離（メートル）
		double d = Math.sqrt(Math.pow(M * dLat, 2)
				+ Math.pow(N * Math.cos(avgLat) * dLon, 2));

		return d;
	}
	
	/**
	 * 
	 * @param dst dst[0]=dx,dst[1]=dy
	 * @param from
	 * @param to
	 */
	public static GeoPoint convertToGeoPoint(GeoPoint start, float[] delta3f) {
		double lat = start.getLatitudeE6() / 1E6;
		//double lon = start.getLongitudeE6() / 1E6;
		
		// 緯度経度をラジアンに変換
		double radLat = lat * Math.PI / 180D;
		//double radLon = lon * Math.PI / 180D;

		// 第一離心率
		double e;
		if (f != 0) {
			// 扁平率の逆数
			double F = 1D / f;
			e = (Math.sqrt(2 * F - 1)) / F;
		} else {
			e = 0;
		}

		double W = Math.sqrt(1 - Math.pow(e, 2) * Math.pow(Math.sin(radLat), 2));

		// 子午線曲率半径
		double M = (a * (1 - Math.pow(e, 2))) / Math.pow(W, 3);

		// 卯酉線曲率半径
		double N = a / W;

		// 2点間の緯度差(ラジアン)
		double dLat = delta3f[1] / M;

		// 2点間の経度差(ラジアン)
		double dLon = delta3f[0] / (N * Math.cos(radLat));
		
		int intLat = start.getLatitudeE6() + (int)(dLat*1E6 * 180D / Math.PI);
		int intLon = start.getLongitudeE6() + (int)(dLon*1E6 * 180D / Math.PI);
		
		return new GeoPoint(intLat, intLon);
	}
}
