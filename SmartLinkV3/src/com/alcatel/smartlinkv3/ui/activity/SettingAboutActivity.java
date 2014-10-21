package com.alcatel.smartlinkv3.ui.activity;

import com.alcatel.smartlinkv3.R;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

public class SettingAboutActivity extends Activity {

	private TextView m_tv_title = null;
	private TextView m_tv_official;
	private TextView m_tv_management;
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
	}

	private void controlTitlebar(){
		m_tv_title = (TextView)findViewById(R.id.tv_title_title);
		m_tv_title.setText(R.string.setting_about);
	}
	
	private void createControls(){
		m_tv_official = (TextView)findViewById(R.id.tv_link_official);
		m_tv_management = (TextView)findViewById(R.id.tv_link_management_page);
//		m_tv_official.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//		m_tv_management.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		String strOfficial = getString(R.string.setting_about_official_website);
		String strHtmlOfficial = "<u>"+strOfficial+"</u>";
		m_tv_official.setText(Html.fromHtml(strHtmlOfficial));
		String strManagement = getString(R.string.setting_about_management_page);
		String strHtmlManagement = "<u>"+strManagement+"</u>";
		m_tv_management.setText(Html.fromHtml(strHtmlManagement));
	}
}
