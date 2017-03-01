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
import android.widget.Toast;


public class PukDialog implements OnClickListener, OnKeyListener, TextWatcher {
	
	public static boolean m_isShow = false;
    private Context m_context;
    private Dialog m_puk_dialog = null;
    private ProgressDialog m_progress_dialog = null;
    private View m_puk_view = null;
    private EditText m_puk_edit;
    private EditText m_new_pin_edit;
    private EditText m_confirm_pin_edit;
    private TextView m_puk_remain_tv;
    private boolean m_last_puk_success = true;
    private Button m_confirmBtn;
    private Button m_ivCancelBtn = null;
    private Button m_btnCancel = null;
    private boolean m_is_user_close = false;
    private OnPUKError m_callback = null;
    private Animation m_dlgPukShake = null;
    
    private static PukDialog m_instance;
    public static PukDialog getInstance(Context context)
    {
        //if(m_instance == null)
        m_instance = new PukDialog(context);
        return m_instance;
    }
    
    private PukDialog(Context context)
    {
        m_context = context;
        createDialog();
    }
    
    private void createDialog()
    {        
        LayoutInflater factory = LayoutInflater.from(m_context);
        m_puk_view = factory.inflate(R.layout.puk_view, null);
        
        m_puk_dialog = new Dialog(m_context, R.style.dialog);
        Window window = m_puk_dialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        window.requestFeature(Window.FEATURE_NO_TITLE);
        m_puk_dialog.setContentView(m_puk_view);
        m_puk_dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);      
           
        m_puk_remain_tv = (TextView)m_puk_view.findViewById(R.id.puk_remained_times);
        m_puk_edit = (EditText)m_puk_view.findViewById(R.id.puk_code_edit);
        m_puk_edit.setOnKeyListener(this);
        m_puk_edit.addTextChangedListener(this);
        
        m_new_pin_edit = (EditText)m_puk_view.findViewById(R.id.new_pin_edit);
        m_new_pin_edit.setOnKeyListener(this);
        m_new_pin_edit.addTextChangedListener(this);
        
        m_confirm_pin_edit = (EditText)m_puk_view.findViewById(R.id.confirm_pin_edit);
        m_confirm_pin_edit.setOnKeyListener(this);
        m_confirm_pin_edit.addTextChangedListener(this);
                
        m_confirmBtn = (Button)m_puk_view.findViewById(R.id.puk_apply_btn);
        m_confirmBtn.setOnClickListener(this);
        //m_confirmBtn.setTypeface(ViewUtilities.getTypeface());
        
        m_ivCancelBtn = (Button)m_puk_view.findViewById(R.id.puk_close_btn);
        m_ivCancelBtn.setOnClickListener(this);
        //
//        m_btnCancel = (Button)m_puk_view.findViewById(R.id.puk_cancel_btn);
//        m_btnCancel.setOnClickListener(this);
        
        m_puk_dialog.setCancelable(false);
        m_puk_dialog.setCanceledOnTouchOutside(false);
        //initFonts();
        
        m_dlgPukShake = AnimationUtils.loadAnimation(m_context, R.anim.input_error_shake);
        m_dlgPukShake.setAnimationListener(new AnimationListener() {
			
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
				m_callback.onPukError();
			}
		});

	}



    public void showDialog(int n, OnPUKError callback)
    {        
    	m_callback = callback;
        if(m_puk_dialog != null && !m_isShow)
        {
            m_confirmBtn.setEnabled(false);
            m_puk_edit.setText("");            
            m_new_pin_edit.setText("");
            m_confirm_pin_edit.setText("");
            m_puk_remain_tv.setText(formatRemainTimes(n));
            m_puk_edit.requestFocus();
            m_puk_dialog.show();
            m_isShow = true;
        }
    }
    
    public void showDialog()
    {        
        if(m_puk_dialog != null && !m_isShow)
        {
            m_confirmBtn.setEnabled(false);
            m_puk_edit.setText("");            
            m_new_pin_edit.setText("");
            m_confirm_pin_edit.setText("");
            m_puk_edit.requestFocus();
            m_puk_dialog.show();
            m_isShow = true;
        }
    }

    public void destroyDialog(){
    	closeDialog();
    	m_instance = null;
    }

    public void closeDialog()
    {
        if(m_progress_dialog != null && m_progress_dialog.isShowing())
            m_progress_dialog.dismiss();
        
        if(m_puk_dialog.isShowing())
        {
            m_puk_dialog.dismiss();
            m_isShow = false;
        }
    }
    
    private void apply()
    {
        String puk = m_puk_edit.getText().toString();
        String new_pin = m_new_pin_edit.getText().toString();
        String confirm_pin = m_confirm_pin_edit.getText().toString();
        
        if(puk.length() < 8)
        {
            m_puk_edit.requestFocus();
            String message = m_context.getResources().getString(R.string.puk_format_wrong_msg);
            Toast.makeText(m_context, message, Toast.LENGTH_LONG).show();
            return;
        }
        
        if(!new_pin.equals(confirm_pin))
        {
            m_confirm_pin_edit.setFocusable(true);
            m_confirm_pin_edit.requestFocus();
            String message = m_context.getResources().getString(R.string.puk_prompt_str);
            Toast.makeText(m_context, message, Toast.LENGTH_LONG).show();
            return;
        }
        
        InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(m_puk_view.getWindowToken(), 0);
		
        String strTitle = m_context.getString(R.string.IDS_PUK_LOCKED);
    	String strMsg = m_context.getString(R.string.IDS_PIN_CHECK_PUK_PROGRESS);
    	strMsg += "...";
        m_progress_dialog = ProgressDialog.show(m_context, strTitle, strMsg, true, false);        
        
        DataValue dataPuk = new DataValue();
        dataPuk.addParam("puk", puk);
        dataPuk.addParam("pin", new_pin);
        BusinessMannager.getInstance().sendRequestMessage(MessageUti.SIM_UNLOCK_PUK_REQUEST, dataPuk);
    }
    
    private void cancel()
    {
        closeDialog();
    }
    
    public void onSimStatusReady( SimStatusModel simStatusData){
    	if(null != simStatusData){
    		if(SIMState.PukRequired == simStatusData.m_SIMState
    		   || !m_last_puk_success){
    			if(!m_last_puk_success){
    				//String message = "PUK Failed!";
    				
    				m_puk_edit.setText("");
                    m_new_pin_edit.setText("");
                    m_confirm_pin_edit.setText("");
                    m_puk_remain_tv.setText(formatRemainTimes(simStatusData.m_nPukRemainingTimes));
                    m_puk_edit.requestFocus();
                    m_last_puk_success = true;
                    
                    if(null != m_progress_dialog && m_progress_dialog.isShowing()){
                    	m_progress_dialog.dismiss();
                    }
                    m_puk_view.startAnimation(m_dlgPukShake);
                    //closeDialog();
    			}
    		}
    	}
    }

	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if(m_puk_edit.getText().length() >= 4 && m_new_pin_edit.getText().length() >= 4 && m_confirm_pin_edit.getText().length() >= 4)
        {
            m_confirmBtn.setEnabled(true);
        }
        else
        {
            m_confirmBtn.setEnabled(false);
        }
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) 
        {
            m_puk_dialog.dismiss();
            m_isShow = false;
        } 
        
		return false;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
        {
            case R.id.puk_apply_btn:
                apply();
                break;

            case R.id.puk_close_btn:
                m_is_user_close = true;                
                cancel();
                break;
        }
	}
	
	public void onEnterPukResponse(boolean result_code) 
    {
        m_last_puk_success = result_code;
    }

    public void cancelUserClose()
    {
        m_is_user_close = false;
    }
    
    public boolean isUserClose()
    {
        return m_is_user_close;
    }
    
    public void updateRemainTimes(int n)
    {
        m_puk_remain_tv.setText(formatRemainTimes(n)); 
    }
    
	private String formatRemainTimes(int nTimes) {
		String format = this.m_context.getResources().getString(
				R.string.remain_times);
		return String.format(format, nTimes);
	}
	
	public interface OnPUKError
	{
		public void onPukError();
	}

}
