package jp.bs.app.ukiukiview;

import jp.bs.app.ukiukiview.R;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CustomDialog extends Dialog {
	OnUserInputListener mOnUserInputListener = null;
	Context mContext = null;
	String mTitle = null;
	View mView = null;
	LinearLayout mRoot = null;

	public CustomDialog(Context context) {
		super(context, R.style.Theme_CustomDialog);
		mContext = context;
	}

    public void setEnabled(int resId, boolean enabled) {
    	findViewById(resId).setEnabled(enabled);
    	findViewById(resId).setFocusable(enabled);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	if (mOnUserInputListener!=null) {
    		mOnUserInputListener.onUserInputListener();
    	}
    	return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (mOnUserInputListener!=null) {
    		mOnUserInputListener.onUserInputListener();
    	}
    	return super.onTouchEvent(event);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
    	if (mOnUserInputListener!=null) {
    		mOnUserInputListener.onUserInputListener();
    	}
    	return super.onTrackballEvent(event);
    }

    public void setOnUserInputListener(OnUserInputListener onUserInputListener) {
    	mOnUserInputListener = onUserInputListener;
	}
	public interface OnUserInputListener {
		void onUserInputListener();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title.toString();
	}

	@Override
	public void setTitle(int titleId) {
		mTitle = mContext.getString(titleId);
	}

	@Override
	public void setContentView(int layoutResID) {
		View view = getLayoutInflater().inflate(layoutResID, null);
		mRoot = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_root, null);
		mRoot.addView(view);
		super.setContentView(mRoot);
	}

	@Override
	public void setContentView(View view) {
		mRoot = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_root, null);
		mRoot.addView(view);
		super.setContentView(mRoot);
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		//TODO:LayoutParams support
//		mView = view;
		mRoot = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_root, null);
		mRoot.addView(view);
		super.setContentView(mRoot);
	}

	@Override
	public void show() {
		TextView text = (TextView)findViewById(android.R.id.title);
		text.setVisibility(View.GONE);
		text = (TextView)findViewById(R.id.textTitle);
		if (mTitle!=null && text!=null) {
			text.setText(mTitle);
		} else if (text!=null) {
			text.setVisibility(View.GONE);
		}
		super.show();
	}
}
