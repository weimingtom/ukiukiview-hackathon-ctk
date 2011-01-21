package jp.bs.app.ukiukiview;

import java.util.ArrayList;
import java.util.List;

import jp.co.brilliantservice.app.openar.data.ARObject;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapOverlayItem extends ItemizedOverlay<OverlayItem> {
	private MapItemListener mListener = null;
	private List<GeoPoint> points = new ArrayList<GeoPoint>();
	private List<ARObject> items = new ArrayList<ARObject>();

	public MapOverlayItem(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		shadow = false;
		super.draw(canvas, mapView, shadow);
	}

	@Override
	protected MyOverlayItem createItem(int i) {
		GeoPoint point = points.get(i);
		return new MyOverlayItem(point);
	}

	@Override
	public int size() {
		return points.size();
	}

	public void addPoint(ARObject item, GeoPoint point) {
		for (int i=0;i<items.size();i++) {
			if (items.get(i).mUid.equals(item.mUid)) {
				//ignore already registered item.
				return;
			}
		}
		if (points.size()>=30) {
			points.remove(0);
			items.remove(0);
		}
		this.points.add(point);
		this.items.add(item);
		populate();
	}

	public void removePoint(ARObject item) {
		for (int i=0;i<items.size();i++) {
			if (items.get(i).mUid.equals(item.mUid)) {
				this.points.remove(i);
				this.items.remove(i);
				return;
			}
		}
		populate();
	}

	public void clearPoint() {
		this.points.clear();
		this.items.clear();
		populate();
		setLastFocusedIndex(-1);
	}

	class MyOverlayItem extends OverlayItem {
		public MyOverlayItem(GeoPoint point) {
			super(point, "", "");
		}
	}

	@Override
	protected boolean onTap(int index) {
		if (mListener!=null && index<items.size()) {
			return mListener.onTap(index, items.get(index));
		}
		return super.onTap(index);
	}

	public void setMapItemListener(MapItemListener listener) {
		mListener = listener;
	}

	public interface MapItemListener {
		boolean onTap(int index, ARObject item);
	}

	public class MapItem extends GeoPoint {
		int mSize = 12;
		public MapItem(int latitudeE6, int longitudeE6, int size) {
			super(latitudeE6, longitudeE6);
			mSize = size;
		}
	}
}
