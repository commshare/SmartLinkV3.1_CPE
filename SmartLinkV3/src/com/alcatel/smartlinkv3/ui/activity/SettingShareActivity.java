package com.alcatel.smartlinkv3.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ENUM.EnumDeviceCheckingStatus;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

public class SettingShareActivity extends BaseActivity implements OnClickListener{
	
		private TextView m_tv_title = null;
		private ImageButton m_ib_back=null;
		private TextView m_tv_back=null;
		private TextView m_tv_done;
		private FrameLayout m_fl_titlebar;
		private TextView m_ftp_button;
		private TextView m_dlna_button;
		private RelativeLayout m_waiting;
		private FrameLayout m_ftp_container;
		private FrameLayout m_dlna_container;
		
		private boolean m_ftp_supported;
		private boolean m_dlna_supported;
		private boolean m_ftp_enabled = false;
		private boolean m_dlna_enabled = false;
		private String m_dlnaDeviceName;
		private ImageView m_divider;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.activity_setting_share);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
			controlTitlebar();
			initUI();
		}
		
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			m_bNeedBack = false;
			super.onResume();
			registerReceiver(m_msgReceiver, 
					new IntentFilter(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET));
			registerReceiver(m_msgReceiver, 
					new IntentFilter(MessageUti.SHARING_GET_FTP_SETTING_REQUSET));
			registerReceiver(m_msgReceiver, 
					new IntentFilter(MessageUti.SHARING_SET_DLNA_SETTING_REQUSET));
			registerReceiver(m_msgReceiver, 
					new IntentFilter(MessageUti.SHARING_SET_FTP_SETTING_REQUSET));
		}
		
		private void controlTitlebar(){
			m_tv_title = (TextView)findViewById(R.id.tv_title_title);
			m_tv_title.setText(R.string.setting_sharing);
			//back button and text
			m_ib_back = (ImageButton)findViewById(R.id.ib_title_back);
			m_tv_back = (TextView)findViewById(R.id.tv_title_back);
			
			m_fl_titlebar = (FrameLayout)findViewById(R.id.fl_edit_or_done);
			m_fl_titlebar.setVisibility(View.VISIBLE);
			
			m_tv_done = (TextView)findViewById(R.id.tv_titlebar_done);
			m_tv_done.setVisibility(View.GONE);
			
			findViewById(R.id.tv_titlebar_edit).setVisibility(View.GONE);
			
			m_ib_back.setOnClickListener(this);
			m_tv_back.setOnClickListener(this);
			m_tv_done.setOnClickListener(this);
		}
		
		private void initUI(){
			m_ftp_container = (FrameLayout) findViewById(R.id.setting_ftp_container);
			m_dlna_container = (FrameLayout) findViewById(R.id.setting_dlna_container);
			m_divider = (ImageView) findViewById(R.id.setting_sharing_divider);
			
			m_ftp_button = (TextView) findViewById(R.id.btn_ftp_switch);
			m_ftp_button.setOnClickListener(this);
			m_dlna_button = (TextView) findViewById(R.id.btn_dlna_switch);
			m_dlna_button.setOnClickListener(this);
			m_waiting = (RelativeLayout) findViewById(R.id.setting_sharing_waiting);
			m_ftp_supported = getIntent().getBundleExtra("Sharing").getBoolean("FtpSupport");
			m_dlna_supported = getIntent().getBundleExtra("Sharing").getBoolean("DlnaSupport");
			if(!m_ftp_supported){
				m_ftp_container.setVisibility(View.GONE);
				m_divider.setVisibility(View.GONE);
			}
			if(!m_dlna_supported){
				m_dlna_container.setVisibility(View.GONE);
				m_divider.setVisibility(View.GONE);
			}
			updateUI();
		}
		
		private void updateUI(){
			m_ftp_enabled = BusinessMannager.getInstance().getFtpSettings().FtpStatus == 1;
			m_dlna_enabled = BusinessMannager.getInstance().getDlnaSettings().DlnaStatus == 1;
			m_dlnaDeviceName = BusinessMannager.getInstance().getDlnaSettings().DlnaName;
			if(m_ftp_enabled){
				m_ftp_button.setBackgroundResource(R.drawable.pwd_switcher_on);
			}
			else{
				m_ftp_button.setBackgroundResource(R.drawable.pwd_switcher_off);
			}
			if(m_dlna_enabled){
				m_dlna_button.setBackgroundResource(R.drawable.pwd_switcher_on);
			}
			else{
				m_dlna_button.setBackgroundResource(R.drawable.pwd_switcher_off);
			}
			m_waiting.setVisibility(View.GONE);
		}
		
		private void setFailed(){
			String strInfo = getString(R.string.unknown_error);
			Toast.makeText(SettingShareActivity.this, strInfo, Toast.LENGTH_SHORT).show();
			m_waiting.setVisibility(View.GONE);
		}
		
		private void changeFtpSetting(){
			DataValue data = new DataValue();
			if(m_ftp_enabled)
				data.addParam("FtpStatus", 0);
			else
				data.addParam("FtpStatus", 1);
			
			m_waiting.setVisibility(View.VISIBLE);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.SHARING_SET_FTP_SETTING_REQUSET, data);
		}
		
		private void changeDlnaSetting(){
			DataValue data = new DataValue();
			if(m_dlna_enabled)
				data.addParam("DlnaStatus", 0);
			else
				data.addParam("DlnaStatus", 1);
			data.addParam("DlnaName", m_dlnaDeviceName);
			BusinessMannager.getInstance().sendRequestMessage(
					MessageUti.SHARING_SET_DLNA_SETTING_REQUSET, data);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int nID = v.getId();
			switch (nID) {
			case R.id.tv_title_back:
			case R.id.ib_title_back:
				SettingShareActivity.this.finish();
				break;
			case R.id.btn_ftp_switch:
				changeFtpSetting();
				break;
			case R.id.btn_dlna_switch:
				changeDlnaSetting();
				break;
			default:
				break;
			}
		}
		
		@Override
		protected void onBroadcastReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			super.onBroadcastReceive(context, intent);
			if(intent.getAction().equalsIgnoreCase(MessageUti.SHARING_SET_FTP_SETTING_REQUSET)){
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
					updateUI();
				}
				else{
					setFailed();
				}
			}
			
			if(intent.getAction().equalsIgnoreCase(MessageUti.SHARING_SET_DLNA_SETTING_REQUSET)){
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
					updateUI();
				}
				else{
					setFailed();
				}
			}
			
			if(intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_FTP_SETTING_REQUSET)){
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
					
				}
			}
			
			if(intent.getAction().equalsIgnoreCase(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET)){
				int nResult = intent.getIntExtra(MessageUti.RESPONSE_RESULT, BaseResponse.RESPONSE_OK);
				String strErrorCode = intent.getStringExtra(MessageUti.RESPONSE_ERROR_CODE);
				if (BaseResponse.RESPONSE_OK == nResult && 0 == strErrorCode.length()) {
					
				}
			}
		}
}
