package com.alcatel.wifilink.ui.dialog;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.model.SimStatusModel;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.ENUM.SIMState;
import com.alcatel.wifilink.common.MessageUti;

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
import android.widget.TextView;

public class PinStateDialog implements OnClickListener, OnKeyListener, TextWatcher {
	public static boolean m_isShow = false;
	private Dialog m_pin_dialog = null;
	private ProgressDialog m_progress_dialog = null;
	private Context m_context;
	private View m_pin_view = null;
	private TextView m_remain_times;
	private EditText m_pin_edit;
	private boolean m_last_pin_success = true;
	private Button m_confirmBtn;
	private boolean m_is_user_close = false;
	private Animation m_shake = null;
	private OnPINError m_callback;
	private static PinStateDialog m_instance;

	public static PinStateDialog getInstance(Context context) {
		//if (m_instance == null)
		m_instance = new PinStateDialog(context);

		return m_instance;
	}

	private PinStateDialog(Context context) {
		m_context = context;
		createDialog();
	}

	public void showDialog(int n, OnPINError callBack)
    {
		m_callback = callBack;
        if(m_pin_dialog != null && !m_isShow)
        {
            m_confirmBtn.setEnabled(false);
            m_pin_edit.setText("");           
            m_remain_times.setText(formatRemainTimes(n));
            m_pin_dialog.show();
            m_isShow = true;           
        }
    }

	public void showDialog() {
		if (m_pin_dialog != null && !m_isShow) {
			m_confirmBtn.setEnabled(false);
			m_pin_edit.setText("");
			m_pin_dialog.show();
			m_isShow = true;
		}
	}

	public void destroyDialog() {
		closeDialog();
		m_instance = null;
	}

	public void closeDialog() {
		if (m_progress_dialog != null && m_progress_dialog.isShowing()) {
			m_progress_dialog.dismiss();
		}

		if (m_pin_dialog.isShowing()) {
			m_pin_dialog.dismiss();
			m_isShow = false;
		}
	}

	private void createDialog() {
		LayoutInflater factory = LayoutInflater.from(m_context);
		m_pin_view = factory.inflate(R.layout.pin_view, null);

		m_pin_view.setOnKeyListener(this);
		m_pin_dialog = new Dialog(m_context, R.style.dialog);
		Window window = m_pin_dialog.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		window.requestFeature(Window.FEATURE_NO_TITLE);

		m_pin_dialog.setContentView(m_pin_view);
		m_pin_dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);

		m_remain_times = (TextView) m_pin_view
				.findViewById(R.id.pin_remain_times);
		m_pin_edit = (EditText) m_pin_view.findViewById(R.id.pin_edit_view);
		m_pin_edit.setOnKeyListener(this);
		m_pin_edit.addTextChangedListener(this);

		m_confirmBtn = (Button) m_pin_view.findViewById(R.id.pin_apply_btn);
		m_confirmBtn.setOnClickListener(this);
		// m_confirmBtn.setTypeface(ViewUtilities.getTypeface());

		Button cancelBtn = (Button) m_pin_view
				.findViewById(R.id.pin_close_btn);
		cancelBtn.setOnClickListener(this);

		m_pin_dialog.setCancelable(true);
		m_pin_dialog.setCanceledOnTouchOutside(false);
		// initFonts();

		m_shake = AnimationUtils.loadAnimation(m_context, R.anim.input_error_shake);
		m_shake.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				closeDialog();
				m_callback.onPinError();
			}
		});
		
	}
	
	private void apply() {
		InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(m_pin_view.getWindowToken(), 0);
		
		String strTitle = m_context.getString(R.string.IDS_PIN_LOCKED);
		String strMsg = m_context
				.getString(R.string.IDS_PIN_CHECK_PIN_PROGRESS);
		strMsg += "...";
		m_progress_dialog = ProgressDialog.show(m_context, strTitle, strMsg,
				true, false);

		DataValue data = new DataValue();
		data.addParam("pin", m_pin_edit.getText().toString());
		if(BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.PinEnableVerified){
			data.addParam("state", 0);
		}
		else if(BusinessManager.getInstance().getSimStatus().m_PinState == ENUM.PinState.Disable){
			data.addParam("state", 1);
		}
		BusinessManager.getInstance().sendRequestMessage(
				MessageUti.SIM_CHANGE_PIN_STATE_REQUEST, data);
	}

	private void cancel() {
		closeDialog();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pin_apply_btn:
			apply();
			break;
		case R.id.pin_close_btn:
			m_is_user_close = true;
			cancel();
			break;
		}
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			m_pin_dialog.dismiss();
			m_isShow = false;
		}

		return false;
	}

	public void onSimStatusReady( SimStatusModel sim_status) {
		//
		if (null != sim_status) {
			if (sim_status.m_SIMState== SIMState.PinRequired
					|| !m_last_pin_success) {
				if (!m_last_pin_success) {
					// String message = "PIN Failed!";
					m_pin_edit.setText("");
					m_remain_times.setText(formatRemainTimes(sim_status.m_nPinRemainingTimes));
					m_last_pin_success = true;
					if (null != m_progress_dialog
							&& m_progress_dialog.isShowing()) {
						m_progress_dialog.dismiss();
					}
			        m_pin_view.startAnimation(m_shake);
					//closeDialog();
				}
			}
		}
	}

	public void onEnterPinResponse(boolean result_code) {
			m_last_pin_success = result_code;
	}

	public void cancelUserClose() {
		m_is_user_close = false;
	}

	public boolean isUserClose() {
		return m_is_user_close;
	}

	public void updateRemainTimes(int n) {
		m_remain_times.setText(formatRemainTimes(n));
	}

	public void afterTextChanged(Editable s) {
		if (s.length() >= 4) {
			m_confirmBtn.setEnabled(true);
		} else {
			m_confirmBtn.setEnabled(false);
		}
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	private String formatRemainTimes(int nTimes) {
		String format = this.m_context.getResources().getString(
				R.string.remain_times);
		return String.format(format, nTimes);
	}

	public interface OnPINError
	{
		public void onPinError();
	}

}
