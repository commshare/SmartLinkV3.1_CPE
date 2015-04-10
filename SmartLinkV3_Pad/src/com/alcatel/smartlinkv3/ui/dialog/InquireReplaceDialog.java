package com.alcatel.smartlinkv3.ui.dialog;

import com.alcatel.smartlinkv3.R;

import android.app.Dialog;
import android.content.Context;
//import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InquireReplaceDialog implements OnClickListener, OnKeyListener{
	private Dialog m_inquire_dialog = null;
	private Context m_context;
	private View m_inquire_view = null;
	private OnInquireApply m_callBackApply= null;
	private OnInquireCancle m_callBackCancle= null;
	
	public TextView m_titleTextView = null;
	public TextView m_contentTextView = null;
	public Button m_confirmBtn = null;
	public Button m_closeBtn = null;
	

	public InquireReplaceDialog(Context context) {
		m_context = context;
		LayoutInflater factory = LayoutInflater.from(m_context);
		m_inquire_view = factory.inflate(R.layout.inquire_replace_view, null);
		//m_inquire_view.setOnKeyListener(this);
		
		m_inquire_dialog = new Dialog(m_context, R.style.dialog);
		Window window = m_inquire_dialog.getWindow();
		//window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		//window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); 
		WindowManager.LayoutParams params = window.getAttributes();  
		params.dimAmount = 0.6f; 
		window.setAttributes(params); 

		m_inquire_dialog.setContentView(m_inquire_view);
		m_inquire_dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		m_titleTextView = (TextView) m_inquire_view.findViewById(R.id.title);
		m_contentTextView = (TextView) m_inquire_view.findViewById(R.id.inquire_content);
		m_confirmBtn = (Button) m_inquire_view.findViewById(R.id.inquire_btn);
		m_confirmBtn.setOnClickListener(this);
		m_closeBtn = (Button) m_inquire_view.findViewById(R.id.close_btn);
		m_closeBtn.setOnClickListener(this);
		
		m_inquire_dialog.setCancelable(true);
		//initFonts();
	}

	public void showDialog(OnInquireApply callBack, OnInquireCancle callBackTwo)
    {
		m_callBackApply = callBack;
		m_callBackCancle = callBackTwo;
        if(m_inquire_dialog != null && m_inquire_dialog.isShowing() == false)
        	m_inquire_dialog.show();        
    }

	public void destroyDialog() {
		closeDialog();
	}

	public void closeDialog() {
		if (m_inquire_dialog.isShowing()) {
			m_inquire_dialog.dismiss();
		}
	}
	
	private void apply() {
		m_callBackApply.onInquireApply();
	}

	private void cancel() {
		m_callBackCancle.onInquireCancel();
		//closeDialog();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.inquire_btn:
			apply();
			break;
			
		case R.id.close_btn:
			cancel();
			break;
		}
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			m_inquire_dialog.dismiss();
		}

		return false;
	}

	public interface OnInquireApply
	{
		public void onInquireApply();
	}
	
	public interface OnInquireCancle
	{
		public void onInquireCancel();
	}

}
