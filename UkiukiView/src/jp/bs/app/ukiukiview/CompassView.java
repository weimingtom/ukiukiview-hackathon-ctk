package jp.bs.app.ukiukiview;

import jp.bs.app.ukiukiview.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {
	boolean mIsInit = false;
    private Bitmap image1;
    private Bitmap image2;
    private Bitmap image3;
    private double mAzimuth;

    public CompassView(Context context) {
		super(context);
		init(context);
	}

	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		if (!mIsInit) {
			Resources resource = context.getResources();
			image1=BitmapFactory.decodeResource(resource,R.drawable.news1);
			image2=BitmapFactory.decodeResource(resource,R.drawable.news2);
			image3=BitmapFactory.decodeResource(resource,R.drawable.news3);
			mIsInit = true;
		}
	}

	public void setAzimuth(double azimuth) {
		mAzimuth = azimuth;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
        canvas.drawBitmap(image1,0,0,null);
        canvas.drawBitmap(image3,0,0,null);
       	canvas.rotate((float)(-1 * mAzimuth), image2.getWidth() / 2, image2.getHeight() / 2);
        canvas.drawBitmap(image2,0,0,null);
	}
}
