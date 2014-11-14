package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessMannager;
import com.alcatel.smartlinkv3.common.CommonUtil;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingAboutActivity extends BaseActivity implements OnClickListener{

	private TextView m_tv_title = null;
	private TextView m_tv_official;
	private TextView m_tv_app_version;
	private TextView m_tv_management;
	private ImageButton m_ib_title_back;
	private TextView m_tv_title_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_setting_about);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		//control title bar
		controlTitlebar();
		//create controls
		createControls();
		//app version
		String strVersionString = BusinessMannager.getInstance().getAppVersion();
		String strTemp = getString(R.string.setting_about_version) + " " + strVersionString;
		m_tv_app_version.setText(strTemp);

	}

	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_about);
		m_ib_title_back = (ImageButton)findViewById(R.id.ib_title_back);
		m_tv_title_back = (TextView)findViewById(R.id.tv_title_back);
		m_ib_title_back.setOnClickListener(this);
		m_tv_title_back.setOnClickListener(this);
	}

	private void createControls(){
		m_tv_app_version = (TextView)findViewById(R.id.tv_about_app_version);
		m_tv_official = (TextView)findViewById(R.id.tv_link_official);
		m_tv_management = (TextView)findViewById(R.id.tv_link_management_page);
		//		m_tv_official.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		//		m_tv_management.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		//set text
		String strOfficial = getString(R.string.setting_about_official_website);
		String strHtmlOfficial = "<u>"+strOfficial+"</u>";
		m_tv_official.setText(Html.fromHtml(strHtmlOfficial));
		String strManagement = getString(R.string.setting_about_management_page);
		String strHtmlManagement = "<u>"+strManagement+"</u>";
		m_tv_management.setText(Html.fromHtml(strHtmlManagement));
		
		//set click event
		m_tv_official.setOnClickListener(this);
		m_tv_management.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int nId = v.getId();
		switch (nId) {
		case R.id.ib_title_back:
		case R.id.tv_title_back:
			SettingAboutActivity.this.finish();
			break;
		case R.id.tv_link_official:
			CommonUtil.openWebPage(this, "http://www.alcatelonetouch.com");
			break;
		case R.id.tv_link_management_page:
			CommonUtil.openWebPage(this, "http://192.168.1.1");
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		m_bNeedBack = false;
		super.onResume();
	}
}
