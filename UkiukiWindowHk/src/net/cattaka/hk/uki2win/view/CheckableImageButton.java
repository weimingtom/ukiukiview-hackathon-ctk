package net.cattaka.hk.uki2win.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageButton;

public class CheckableImageButton extends ImageButton implements Checkable {
	private boolean checked = false;
   private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

	public CheckableImageButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckableImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableImageButton(Context context) {
		super(context);
	}

	public boolean isChecked() {
		return this.checked;
	}

	public void setChecked(boolean checked) {
		if (this.checked != checked) {
			this.checked = checked;
			refreshDrawableState();
		}
	}

	public void toggle() {
		this.checked = !this.checked;
		refreshDrawableState();
	}

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
    }
}
