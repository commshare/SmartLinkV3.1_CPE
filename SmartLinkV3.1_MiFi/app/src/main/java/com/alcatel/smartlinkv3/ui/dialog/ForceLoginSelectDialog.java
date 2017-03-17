package com.alcatel.smartlinkv3.ui.dialog;

import com.alcatel.smartlinkv3.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ForceLoginSelectDialog implements OnClickListener{

	private static ForceLoginSelectDialog m_instance;
	private Context m_context;
	private View m_vError = null;
	private Dialog m_dlgError = null;
	private Button m_btnOK = null;
	private Button m_btnCancle = null;
	private TextView m_tvTitle = null;
	private TextView m_tvErrorInfo=null;
	public static boolean m_bIsShow = false;
  private OnClickBottonConfirm mConfirmCallback=null;
  private OnClickBtnCancel mCancleCallback=null;
	
	public static ForceLoginSelectDialog getInstance(Context context)
	{
		//if(m_instance == null){
		m_instance = new ForceLoginSelectDialog(context);			
		//}
		return m_instance;
	}

	private ForceLoginSelectDialog(Context context)
	{
		m_context = context;
		createDialog();
	}
	
	private void createDialog()
	{
		LayoutInflater factory = LayoutInflater.from(m_context);
		m_vError = factory.inflate(R.layout.force_login_select_dialog, null);

		m_dlgError = new Dialog(m_context, R.style.dialog);
		Window window = m_dlgError.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		m_dlgError.requestWindowFeature(Window.FEATURE_NO_TITLE);
		m_dlgError.setContentView(m_vError);
		m_dlgError.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		
		m_tvTitle = (TextView)m_vError.findViewById(R.id.error_dialog_view_title);

		m_btnOK = (Button)m_vError.findViewById(R.id.ID_BUTTON_OK);
		m_btnOK.setOnClickListener(this);
		
		m_btnCancle = (Button)m_vError.findViewById(R.id.ID_BUTTON_CANCLE);
		m_btnCancle.setOnClickListener(this);
		
		m_dlgError.setCancelable(false);
		
		m_tvErrorInfo = (TextView)m_vError.findViewById(R.id.tv_error_info);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ID_BUTTON_OK:
			closeDialog();
			if (mConfirmCallback != null) {
				mConfirmCallback.onConfirm();
				mConfirmCallback = null;
			}
			break;
		case R.id.ID_BUTTON_CANCLE:
			closeDialog();
			if (mCancleCallback != null){
				mCancleCallback.onCancle();
				mCancleCallback = null;
			}
			break;	
		default:
			break;
		}
	}

	public void destroyDialog(){
		closeDialog();
		m_instance = null;
    mConfirmCallback = null;
    mCancleCallback = null;
	}
	
	public void closeDialog()
	{
		if(m_dlgError != null && m_dlgError.isShowing()){
			m_dlgError.dismiss();
			m_bIsShow = false;	
		}
	}
	
	public void showDialog(String strTitle, String strMessage, OnClickBottonConfirm cb){
		
		if(m_dlgError != null)
		{
			mConfirmCallback = cb;
			m_tvTitle.setText(strTitle);
			m_tvErrorInfo.setText(strMessage);
			if(!m_bIsShow){
				m_dlgError.show();
			}
			m_bIsShow = true;
		}
	}
public void showDialogAndCancel(String strTitle, String strMessage, OnClickBottonConfirm cb,OnClickBtnCancel cc){
		
		if(m_dlgError != null)
		{
			mConfirmCallback = cb;
			mCancleCallback = cc;
			m_tvTitle.setText(strTitle);
			m_tvErrorInfo.setText(strMessage);
			if(!m_bIsShow){
				m_dlgError.show();
			}
			m_bIsShow = true;
		}
	}

	 public interface OnClickBottonConfirm {	 
	    public void onConfirm();
	 }
	 
	 public interface OnClickBtnCancel {	 
		    public void onCancle();
		 }

}
