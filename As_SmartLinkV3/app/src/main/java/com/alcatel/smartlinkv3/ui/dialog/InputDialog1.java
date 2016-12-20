package com.alcatel.smartlinkv3.ui.dialog;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
//import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class InputDialog1 implements OnClickListener, OnKeyListener{
	private Dialog m_input_dialog = null;
	private Context m_context;
	private View m_input_view = null;
	private OnBtnClick m_callBack= null;
	
	public TextView m_titleTextView = null;
	public TextView m_promptView = null;
	public EditText m_inputEdit = null;
	public Button m_okBtn = null;
	public Button m_closeBtn = null;


	public InputDialog1(Context context) {
		m_context = context;
		LayoutInflater factory = LayoutInflater.from(m_context);
		m_input_view = factory.inflate(R.layout.dialog_input_1_view, null);
		//m_inquire_view.setOnKeyListener(this);
		
		m_input_dialog = new Dialog(m_context, R.style.dialog);
		Window window = m_input_dialog.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		window.requestFeature(Window.FEATURE_NO_TITLE);

		m_input_dialog.setContentView(m_input_view);
		m_input_dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		m_titleTextView = (TextView) m_input_view.findViewById(R.id.title);
		m_promptView = (TextView) m_input_view.findViewById(R.id.prompt);
		m_inputEdit = (EditText) m_input_view.findViewById(R.id.input);
		m_okBtn = (Button) m_input_view.findViewById(R.id.ok_btn);
		m_okBtn.setOnClickListener(this);
		m_closeBtn = (Button) m_input_view.findViewById(R.id.close_btn);
		m_closeBtn.setOnClickListener(this);
		
		m_input_dialog.setCancelable(true);
		//initFonts();
	}

	public void showDialog(OnBtnClick callBack)
    {
		m_callBack = callBack;
        if(m_input_dialog != null && m_input_dialog.isShowing() == false)
        	m_input_dialog.show();        
    }

	public void destroyDialog() {
		closeDialog();
	}

	public void closeDialog() {
		if (m_input_dialog.isShowing()) {
			m_input_dialog.dismiss();
		}
	}
	
	private void apply() {
		m_callBack.onBtnClick();
	}

	private void cancel() {
		closeDialog();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_btn:
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
			m_input_dialog.dismiss();
		}

		return false;
	}

	public interface OnBtnClick
	{
		public void onBtnClick();
	}

}
