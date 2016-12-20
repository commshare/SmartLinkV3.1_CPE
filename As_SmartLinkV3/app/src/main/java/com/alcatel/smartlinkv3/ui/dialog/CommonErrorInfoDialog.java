package com.alcatel.smartlinkv3.ui.dialog;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.ui.activity.MainActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CommonErrorInfoDialog implements OnClickListener{

	private static CommonErrorInfoDialog m_instance;
	private Context m_context;
	private View m_vError = null;
	private Dialog m_dlgError = null;
	private Button m_btnOK = null;
	private TextView m_tvTitle = null;
	private TextView m_tvErrorInfo=null;
	public static boolean m_bIsShow = false;
	
	public static CommonErrorInfoDialog getInstance(Context context)
	{
		//if(m_instance == null){
		m_instance = new CommonErrorInfoDialog(context);			
		//}
		return m_instance;
	}

	private CommonErrorInfoDialog(Context context)
	{
		m_context = context;
		createDialog();
	}
	
	private void createDialog()
	{
		LayoutInflater factory = LayoutInflater.from(m_context);
		m_vError = factory.inflate(R.layout.error_info_popup_dialog, null);

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
		
		m_dlgError.setCancelable(false);
		
		m_tvErrorInfo = (TextView)m_vError.findViewById(R.id.tv_error_info);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ID_BUTTON_OK:
			closeDialog();
			Intent intent2= new Intent(MainActivity.PAGE_TO_VIEW_HOME);
			m_context.sendBroadcast(intent2);
			break;

		default:
			break;
		}
	}

	public void destroyDialog(){
		closeDialog();
		m_instance = null;
	}
	
	public void closeDialog()
	{
		if(m_dlgError != null && m_dlgError.isShowing()){
			m_dlgError.dismiss();
			m_bIsShow = false;			
		}
	}
	
	public void showDialog(String strTitle, String strMessage){
		if(m_dlgError != null)
		{
			m_tvTitle.setText(strTitle);
			m_tvErrorInfo.setText(strMessage);
			if(!m_bIsShow){
				m_dlgError.show();
			}
			m_bIsShow = true;
		}
	}
}
