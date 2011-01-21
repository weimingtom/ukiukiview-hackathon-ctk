package jp.bs.app.ukiukiview;

import jp.co.brilliantservice.utility.SdLog;
import android.text.InputFilter;
import android.text.Spanned;

public class TextInputListener implements InputFilter {
	OnInputListener mOnInputListener = null;

	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		SdLog.put("filter["+source+"]");
		if (mOnInputListener!=null) {
			mOnInputListener.onInputListner(source);
		}
		return null;
	}

	public void setOnInputListener(OnInputListener onInputListner) {
		mOnInputListener = onInputListner;
	}

	public interface OnInputListener {
		public void onInputListner(CharSequence source);
	}
}
