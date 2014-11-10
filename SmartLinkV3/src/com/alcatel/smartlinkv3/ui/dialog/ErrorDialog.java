package com.alcatel.smartlinkv3.ui.dialog;

import com.alcatel.smartlinkv3.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ErrorDialog implements OnClickListener{

	private static ErrorDialog m_instance;
	private Context m_context;
	private View m_vError = null;
	private Dialog m_dlgError = null;
	private Button m_btnRetry = null;
	private Button m_ivClose = null;
	private TextView m_tvTitle = null;
	private ImageView m_image = null;
	private AnimationDrawable m_anim = null;
	public static boolean m_bIsShow = false;
	private OnClickBtnRetry m_callback;
	
	public static ErrorDialog getInstance(Context context)
	{
		//if(m_instance == null){
		m_instance = new ErrorDialog(context);			
		//}
		return m_instance;
	}

	private ErrorDialog(Context context)
	{
		m_context = context;
		createDialog();
	}
	
	private void createDialog()
	{
		LayoutInflater factory = LayoutInflater.from(m_context);
		m_vError = factory.inflate(R.layout.error_popup_dialog, null);

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

		m_btnRetry = (Button)m_vError.findViewById(R.id.ID_BUTTON_RETRY);
		m_btnRetry.setOnClickListener(this);
		
		m_ivClose = (Button)m_vError.findViewById(R.id.error_dialog_close_btn);
		m_ivClose.setOnClickListener(this);
		
		m_dlgError.setCancelable(false);
		
		m_image = (ImageView)m_vError.findViewById(R.id.ID_ERROR_IMAGE_VIEW);
		m_anim = (AnimationDrawable) m_image.getBackground();  
		//anim.start();
		m_image.post(new Runnable()
		{
			@Override
			public void run()
			{
				m_anim.start();
			}
		});


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ID_BUTTON_RETRY:
			if(null != m_callback){
				m_callback.onRetry();
			}
			closeDialog();
			break;
		case R.id.error_dialog_close_btn:
			closeDialog();
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
	
	public void showDialog(String strMessage, OnClickBtnRetry callback){
		if(m_dlgError != null)
		{
			m_callback = callback;
			m_tvTitle.setText(strMessage);
			if(!m_bIsShow){
				m_dlgError.show();
			}
			m_bIsShow = true;
		}
	}
	
	//
	public interface OnClickBtnRetry
	{
		public void onRetry();
	}
}
