package net.cattaka.hk.uki2win.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.maps.GeoPoint;

import net.cattaka.hk.uki2win.math.CtkMath;
import net.cattaka.hk.uki2win.math.Hubeny;

public class SceneUtil {
	
	public static SceneObject createSceneObject(SceneObjectInfo soInfo, GeoPoint geoPoint, float size) {
		float[] position = CtkMath.createVector3f();
		Hubeny.convertToMeter(position, soInfo.getGeoPoint(), geoPoint);
		
		SceneObject so = new SceneObject();
		so.setPosition(position);
		so.setObjectId(soInfo.getObjectId());
		so.setName(soInfo.getTitle());
		so.setSceneObjectInfo(soInfo);
		return so;
	}
	
	public static int calcUkiukiRangeFromSize(float size) {
		final float S = (float)Math.sqrt(2.0);
		if (size <= 10) {
			return 1;
		} else if (size*S <= 30) {
			return 2;
		} else if (size*S <= 100) {
			return 3;
		} else if (size*S <= 300) {
			return 4;
		} else if (size*S <= 500) {
			return 5;
		} else if (size*S <= 1000) {
			return 6;
		} else if (size*S <= 2000) {
			return 7;
		} else if (size*S <= 5000) {
			return 8;
		} else {
			return 9;
		}
	}
	
	public static int calcHotpepperRangeFromSize(float size) {
		final float S = (float)Math.sqrt(2.0);
		if (size*S <= 300) {
			return 1;
		} else if (size*S <= 500) {
			return 2;
		} else if (size*S <= 1000) {
			return 3;
		} else if (size*S <= 2000) {
			return 4;
		} else {
			return 5;
		}
	}
	
	public static float getCameraZoom(int zoomLevel) {
		if (zoomLevel < 0) {
			zoomLevel = 0;
		}
		return (float) (Math.pow(2, 16) / Math.pow(2, zoomLevel));
	}
	static class SoEntry implements Comparable<SoEntry> {
		public float distance;
		public AbstractSceneObject so;
		public int compareTo(SoEntry arg) {
			if (this.distance > arg.distance) {
				return 1;
			} else if (this.distance < arg.distance) {
				return -1;
			} else {
				return 0;
			}
		};
	}

	public static void sortSceneObjectListByDistance(List<AbstractSceneObject> soList, float[] cameraPos) {
		// 全SceneObjectのposからの距離を計算し、Entryのリストとして格納する
		List<SoEntry> soEntryList = new ArrayList<SoEntry>(soList.size());
		float[] soPos = CtkMath.createVector3f();
		for (AbstractSceneObject so : soList) {
			SoEntry soEntry = new SoEntry();
			so.getPosition(soPos);
			soEntry.distance = CtkMath.distanceSquare(cameraPos, soPos);
			soEntry.so = so;
			soEntryList.add(soEntry);
		}
		
		// Entryのリストを近いもの順に並び替える
		Collections.sort(soEntryList);
		
		// 結果を書き出す
		soList.clear();
		for (SoEntry soEntry : soEntryList) {
			soList.add(soEntry.so);
		}
	}
	
	public static void sortSceneObjectListByDirection(List<AbstractSceneObject> soList, float[] cameraPos, float[] cameraDir) {
		// 全SceneObjectのposからの距離を計算し、Entryのリストとして格納する
		List<SoEntry> soEntryList = new ArrayList<SoEntry>(soList.size());
		float[] soPos = CtkMath.createVector3f();
		for (AbstractSceneObject so : soList) {
			so.getPosition(soPos);
			CtkMath.subEq3F(soPos, cameraPos);
			float distance = CtkMath.dot3F(soPos, cameraDir);
			SoEntry soEntry = new SoEntry();
			soEntry.distance = distance;
			soEntry.so = so;
			soEntryList.add(soEntry);
		}
		
		// Entryのリストを近いもの順に並び替える
		Collections.sort(soEntryList);
		
		// 結果を書き出す
		soList.clear();
		for (SoEntry soEntry : soEntryList) {
			soList.add(soEntry.so);
		}
	}
}
