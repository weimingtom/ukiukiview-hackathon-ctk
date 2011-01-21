package net.cattaka.hk.uki2win.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BitmapUtil {
	private Paint paint;
	
	public BitmapUtil() {
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setFilterBitmap(true);
	}
	
	public Bitmap resizeForTexture(Bitmap bitmap) {
		Bitmap result = bitmap;
		int bits = 1;
		int w = Math.max(bitmap.getWidth(), bitmap.getHeight()) - 1;
		while((w=w/2) >= 1) {
			bits ++;
		}
		int l = (int) Math.pow(2, bits);
		
		if (bitmap.getWidth() != l || bitmap.getHeight() != l) {
			if (bitmap.getConfig() != null) {
				result = Bitmap.createBitmap(l, l, bitmap.getConfig());
			} else {
				result = Bitmap.createBitmap(l, l, Bitmap.Config.ARGB_4444);
			}
			Canvas canvas = new Canvas(result);
			canvas.drawBitmap(
					bitmap,
					new Rect(0,0, bitmap.getWidth(), bitmap.getHeight()),
					new Rect(0,0, result.getWidth(), result.getHeight()),
					this.paint
					);
		}
		return result;
	}
}
