package net.cattaka.hk.uki2win.view;

import net.cattaka.hk.uki2win.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class ContentDialog
	implements View.OnClickListener,
		CtkDialogInterface 
{
	private Dialog dialog;
	private Context context;
	private TextView titleView;
	private WebView contentView;
	private String title = "";
	private String content = "";
	
	public ContentDialog(Context context) {
		super();
		this.context = context;
	}
	
	public Dialog onCreateDialog() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_content, null);
		this.titleView = (TextView) view.findViewById(R.id.TitleView);
		this.contentView = (WebView) view.findViewById(R.id.ContentView);
		
		view.findViewById(R.id.CloseButton).setOnClickListener(this);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		
		this.dialog = builder.create();
		return dialog;
	}
	
	public void onPrepareDialog(int id, Dialog dialog) {
		if (this.title != null) {
			this.titleView.setText(title);
			this.titleView.setVisibility(View.VISIBLE);
		} else {
			this.titleView.setVisibility(View.GONE);
		}
		if (this.content != null) {
			this.contentView.getSettings().setJavaScriptEnabled(false);
			//this.contentView.loadData(this.content, "text/html", "utf-8");
			this.contentView.loadDataWithBaseURL(null, this.content,"text/html", "UTF-8", null);
			this.contentView.setVisibility(View.VISIBLE);
		} else {
			this.contentView.setVisibility(View.GONE);
		}
	}

	public void onDismiss(int id, DialogInterface dialog) {
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void onClick(View v) {
		if (v.getId() == R.id.CloseButton) {
			if (dialog != null) {
				dialog.dismiss();
			}
		}
	}
}
