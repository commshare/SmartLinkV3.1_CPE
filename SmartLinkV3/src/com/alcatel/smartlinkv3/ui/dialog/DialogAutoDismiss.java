package com.alcatel.smartlinkv3.ui.dialog;

import java.util.Timer;
import java.util.TimerTask;

import com.alcatel.smartlinkv3.R;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.widget.TextView;

public class DialogAutoDismiss implements OnKeyListener{
	private Dialog m_dialog = null;
	private Context m_context;
	private View m_view = null;
	
	private Timer m_timer = new Timer();
	private int m_nShowDuration = 0;
	
	public TextView m_contentTextView = null;

	public DialogAutoDismiss(Context context) {
		m_context = context;
		LayoutInflater factory = LayoutInflater.from(m_context);
		m_view = factory.inflate(R.layout.dialog_auto_dismiss_view, null);
		//m_inquire_view.setOnKeyListener(this);
		
		m_dialog = new Dialog(m_context, R.style.dialog);
		m_dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
		Window window = m_dialog.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		window.requestFeature(Window.FEATURE_NO_TITLE);

		m_dialog.setContentView(m_view);
		m_dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		m_contentTextView = (TextView) m_view.findViewById(R.id.content);
		m_dialog.setCancelable(true);
		//initFonts();
	}

	public void showDialog(int nDuration)
    {
		m_nShowDuration = nDuration;
        if(m_dialog != null && m_dialog.isShowing() == false) {
        	m_dialog.show();
        	if(m_nShowDuration > 0) {
	        	DismissTask dismissTask = new DismissTask();
				m_timer.schedule(dismissTask, m_nShowDuration);
        	}
        }
    }

	public void destroyDialog() {
		closeDialog();
	}

	public void closeDialog() {
		if (m_dialog.isShowing()) {
			m_dialog.dismiss();
		}
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			m_dialog.dismiss();
		}

		return false;
	}
	
	class DismissTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(m_dialog != null && m_dialog.isShowing()) {
				m_dialog.dismiss();
			}
		} 
	}
}
