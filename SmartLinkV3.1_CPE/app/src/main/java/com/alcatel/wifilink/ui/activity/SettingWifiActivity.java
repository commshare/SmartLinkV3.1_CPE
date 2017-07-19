package com.alcatel.wifilink.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.ConnectivityUtils;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM.SecurityMode;
import com.alcatel.wifilink.common.ENUM.SsidHiddenEnum;
import com.alcatel.wifilink.common.ENUM.WEPEncryption;
import com.alcatel.wifilink.common.ENUM.WModeEnum;
import com.alcatel.wifilink.common.ENUM.WPAEncryption;
import com.alcatel.wifilink.common.ENUM.WlanFrequency;
import com.alcatel.wifilink.common.ENUM.WlanSupportMode;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.wifilink.ui.dialog.InquireReplaceDialog;
import com.alcatel.wifilink.ui.dialog.InquireReplaceDialog.OnInquireApply;
import com.alcatel.wifilink.ui.dialog.InquireReplaceDialog.OnInquireCancle;

/*
This activity is used for MiFi product to setting Wi-Fi password , encrypt, SSID, frequency etc,.
 */
public class SettingWifiActivity extends BaseFragmentActivity implements OnClickListener {
    private boolean m_blPasswordOpened = false;
    private WlanFrequency m_nPreWlanAPMode = WlanFrequency.UNKNOWN;
    private WlanFrequency m_nWlanAPMode = WlanFrequency.UNKNOWN;
    private String m_strPreSsid = "";
    private String m_strSsid = "";
    private SecurityMode m_nPreSecurityMode = SecurityMode.Disable;
    private SecurityMode m_nSecurityMode = SecurityMode.Disable;
    private int m_nPreType = 0;
    private int m_nType = 0;
    private String m_strPreKey = "";
    private String m_strKey = "";
    private EditText m_et_password;
    private EditText m_et_ssid;
    private ImageButton m_ib_show_password;
    private ImageButton m_ib_hide_password;
    private ImageButton m_ib_back;
    private TextView m_tv_back;
    private TextView m_tv_edit;
    private TextView m_tv_done;
    private FrameLayout m_fl_titlebar;
    private RadioGroup m_rg_wifi_mode;
    private RadioButton m_rb_2point4G_wifi;
    private RadioButton m_rb_5G_wifi;
    private TextView m_tv_no_password;
    private TextView m_btn_psd_switch;
    private ProgressBar m_pb_waiting = null;
    private TextView m_tv_ssid;
    private LinearLayout m_ll_edit_ssid_broadcast;
    private TextView m_btn_ssid_broadcast_switch;
    private SsidHiddenEnum m_pre_ssid_status = SsidHiddenEnum.SsidHidden_Disable;
    private SsidHiddenEnum m_ssid_status = SsidHiddenEnum.SsidHidden_Disable;
    //spiners
    private FrameLayout m_ll_security;
    private FrameLayout m_ll_encryption;
    private LinearLayout m_ll_password_status;
    private TextView m_passwordPrompt;
    private TextView m_encryptionPrompt;
    private String m_password = "";
    private CommonErrorInfoDialog m_err_dialog;
    private LinearLayout m_content_container;
    private TextView m_security_type;
    private TextView m_encription_mode;
    private boolean m_isTypeSelecttionShown;
    private LinearLayout m_ssid_broadcast_container;
    private ImageView m_divider_under_ssid;

    //	private boolean m_continue_to_change_to_5g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_setting_wifi);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);

        //control title bar
        controlTitlebar();
        //create controls
        createControls();
    }

    private void controlTitlebar() {
        m_ib_back = (ImageButton) findViewById(R.id.ib_title_back);
        m_fl_titlebar = (FrameLayout) findViewById(R.id.fl_edit_or_done);
        m_fl_titlebar.setVisibility(View.VISIBLE);
        m_tv_edit = (TextView) findViewById(R.id.tv_titlebar_edit);
        m_tv_done = (TextView) findViewById(R.id.tv_titlebar_done);
        m_tv_back = (TextView) findViewById(R.id.tv_title_back);
        m_tv_edit.setVisibility(View.VISIBLE);
        m_tv_done.setVisibility(View.GONE);
        m_tv_edit.setOnClickListener(this);
        m_tv_done.setOnClickListener(this);
        m_tv_back.setOnClickListener(this);
        m_ib_back.setOnClickListener(this);
    }

    private void set_Web_Mode(boolean is_2G) {
        SecurityMode securityMode;

        if (is_2G == true)
            securityMode = SecurityMode.build(SecurityMode.antiBuild(BusinessManager.getInstance().getSecurityMode()));
        else
            securityMode = SecurityMode.build(SecurityMode.antiBuild(BusinessManager.getInstance().getSecurityMode_5G()));

        if (securityMode == SecurityMode.Disable) {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            m_et_password.setVisibility(View.GONE);
            m_ib_hide_password.setVisibility(View.GONE);
            m_ib_show_password.setVisibility(View.GONE);
            m_ll_encryption.setVisibility(View.GONE);
            m_ll_security.setVisibility(View.GONE);
            setAllDividerVisibility(View.GONE);
            setOneDividerVisibility(View.VISIBLE);
            m_blPasswordOpened = false;
            m_btn_psd_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
        } else {
            m_blPasswordOpened = true;
            m_et_password.setVisibility(View.VISIBLE);
            m_et_password.setEnabled(true);
            m_ib_hide_password.setVisibility(View.VISIBLE);
            m_ib_show_password.setVisibility(View.GONE);
            m_btn_psd_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
            m_ll_encryption.setVisibility(View.VISIBLE);
            m_ll_security.setVisibility(View.VISIBLE);
            m_ll_password_status.setVisibility(View.VISIBLE);
            setAllDividerVisibility(View.VISIBLE);

        }

        if (securityMode == SecurityMode.WEP) {
            m_security_type.setText(R.string.setting_wifi_wep);
        } else if (securityMode == SecurityMode.WPA) {
            m_security_type.setText(R.string.setting_wifi_wpa);
        } else if (securityMode == SecurityMode.WPA2) {
            m_security_type.setText(R.string.setting_wifi_wpa2);
        } else if (securityMode == SecurityMode.WPA_WPA2) {
            m_security_type.setText(R.string.setting_wifi_wpa_or_wpa2);
        } else {
            m_security_type.setText(R.string.setting_wifi_wep);
        }

        if (securityMode == SecurityMode.WEP) {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            WEPEncryption wepType;
            if (is_2G == true)
                wepType = BusinessManager.getInstance().getWEPEncryption();
            else
                wepType = BusinessManager.getInstance().getWEPEncryption_5G();

            if (wepType == WEPEncryption.Open) {
                m_encription_mode.setText(R.string.setting_wifi_open);
            } else {
                m_encription_mode.setText(R.string.setting_wifi_share);
            }

        } else {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            WPAEncryption wpaType;
            if (is_2G == true)
                wpaType = BusinessManager.getInstance().getWPAEncryption();
            else
                wpaType = BusinessManager.getInstance().getWPAEncryption_5G();

            if (wpaType == WPAEncryption.AUTO) {
                m_encription_mode.setText(R.string.setting_network_mode_auto);
            } else if (wpaType == WPAEncryption.AES) {
                m_encription_mode.setText(R.string.setting_wifi_aes);
            } else {
                m_encription_mode.setText(R.string.setting_wifi_tkip);
            }
        }
    }

    private void createControls() {
        //		m_continue_to_change_to_5g = false;
        m_isTypeSelecttionShown = false;
        m_et_ssid = (EditText) findViewById(R.id.edit_ssid);
        m_et_password = (EditText) findViewById(R.id.edit_password);
        m_ib_show_password = (ImageButton) findViewById(R.id.ib_show_password);
        m_ib_hide_password = (ImageButton) findViewById(R.id.ib_hide_password);
        m_rg_wifi_mode = (RadioGroup) findViewById(R.id.rg_wifi_mode);
        setViewGroupVisibility(m_rg_wifi_mode, View.VISIBLE);
        m_rb_2point4G_wifi = (RadioButton) findViewById(R.id.rb_2point4G_wifi);
        m_rb_5G_wifi = (RadioButton) findViewById(R.id.rb_5G_wifi);
        m_rb_2point4G_wifi.setOnClickListener(this);
        m_rb_5G_wifi.setOnClickListener(this);
        /*m_rb_2point4G_wifi.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				m_et_ssid.setText(BusinessManager.getInstance().getSsid());
				m_et_password.setText(BusinessManager.getInstance().getWifiPwd());
				
				if(BusinessManager.getInstance().getSsidStatus().equals(SsidHiddenEnum.SsidHidden_Disable)){
					m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
				}
				else if(BusinessManager.getInstance().getSsidStatus().equals(SsidHiddenEnum.SsidHidden_Enable)){
					m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
				}
						
				boolean IS_2G=true;
				set_Web_Mode(IS_2G);
				m_rb_2point4G_wifi.setChecked(true);
				m_rb_5G_wifi.setChecked(false);
		
			}
		});
		
		
		//m_rb_5G_wifi.setOnClickListener(this);
		m_rb_5G_wifi.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
						
				m_et_ssid.setText(BusinessManager.getInstance().getSsid_5G());
				m_et_password.setText( BusinessManager.getInstance().getWifiPwd_5G());
				
				if(BusinessManager.getInstance().getSsidStatus_5G().equals(SsidHiddenEnum.SsidHidden_Disable)){
					m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
				}
				else if(BusinessManager.getInstance().getSsidStatus_5G().equals(SsidHiddenEnum.SsidHidden_Enable)){
					m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
				}
				boolean IS_2G=false;
				set_Web_Mode(IS_2G);
				m_rb_2point4G_wifi.setChecked(false);
				m_rb_5G_wifi.setChecked(true);

					
			}
		});*/
        //
        if (!m_ib_show_password.isShown()) {
            m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

        m_et_password.setMinWidth(100);

        m_ib_hide_password.setOnClickListener(this);
        m_ib_show_password.setOnClickListener(this);
        //
        m_tv_no_password = (TextView) findViewById(R.id.tv_no_psd);
        m_btn_psd_switch = (TextView) findViewById(R.id.btn_psd_switch);
        m_btn_psd_switch.setOnClickListener(this);

        m_err_dialog = CommonErrorInfoDialog.getInstance(this);//
        m_pb_waiting = (ProgressBar) findViewById(R.id.pb_wifi_waiting_progress);
        m_tv_ssid = (TextView) findViewById(R.id.tv_ssid);

        m_ll_edit_ssid_broadcast = (LinearLayout) findViewById(R.id.ll_edit_ssid_broadcast);
        m_ll_edit_ssid_broadcast.setOnClickListener(this);
        m_ll_edit_ssid_broadcast.setEnabled(false);
        m_btn_ssid_broadcast_switch = (TextView) findViewById(R.id.btn_ssid_broadcast_switch);
        //		m_pre_ssid_status = SsidHiddenEnum.SsidHidden_Enable;
        //init spiner
        m_content_container = (LinearLayout) findViewById(R.id.setting_network_wifi_content);
        m_security_type = (TextView) findViewById(R.id.set_wifi_security_mode);
        m_security_type.setOnClickListener(this);
        m_encription_mode = (TextView) findViewById(R.id.set_wifi_security_encription_type);
        m_encription_mode.setOnClickListener(this);

        m_ssid_broadcast_container = (LinearLayout) findViewById(R.id.ll_ssid_broadcast_status);
        m_divider_under_ssid = (ImageView) findViewById(R.id.divider0);

        m_ssid_broadcast_container.setVisibility(View.GONE);
        m_divider_under_ssid.setVisibility(View.GONE);

        initSpiners();
    }

    private void ShowWaiting(boolean blShow) {
        m_pb_waiting.setVisibility(blShow ? View.VISIBLE : View.GONE);
        m_tv_edit.setEnabled(!blShow);
        m_tv_done.setEnabled(!blShow);
    }

    private void initSpiners() {
        m_encryptionPrompt = (TextView) findViewById(R.id.tv_encryption);
        m_passwordPrompt = (TextView) findViewById(R.id.tv_psd_type_title);
        m_ll_security = (FrameLayout) findViewById(R.id.ll_security);
        m_ll_encryption = (FrameLayout) findViewById(R.id.ll_encryption);
        m_ll_password_status = (LinearLayout) findViewById(R.id.ll_password_status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_hide_password:
                m_ib_show_password.setVisibility(View.VISIBLE);
                m_ib_hide_password.setVisibility(View.GONE);
                m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                break;
            case R.id.ib_show_password:
                m_ib_hide_password.setVisibility(View.VISIBLE);
                m_ib_show_password.setVisibility(View.GONE);
                m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case R.id.ib_title_back:
                //finish();
                ChangeActivity.toActivity(this, LoadingActivity.class, true, true, false, 0);
                break;
            case R.id.tv_title_back:
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive();
                if (isOpen) {
                    try {
                        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                SettingWifiActivity.this.onBackPressed();
                break;
            case R.id.tv_titlebar_edit:
                onBtnEdit();
                break;
            case R.id.tv_titlebar_done:
                m_tv_done.setText(R.string.done);
                onBtnDone();
                break;

            case R.id.btn_psd_switch:
                onBtnPasswordSwitch();
                break;

            case R.id.ll_edit_ssid_broadcast:
                onBtnSSIDSwitch();
                break;

            case R.id.rb_2point4G_wifi:
                onWifModeChanged();
                break;
            case R.id.rb_5G_wifi:
                if (m_nPreWlanAPMode == WlanFrequency.Frequency_24GHZ) {
                    popInquireyDialog();
                }
                //			if(!m_continue_to_change_to_5g){
                //				if (WlanFrequency.antiBuild(WlanFrequency.Frequency_24GHZ) == m_nPreWlanAPMode) {
                //					m_rb_2point4G_wifi.setChecked(true);
                //					m_rb_5G_wifi.setChecked(false);
                //				}else{
                //					m_rb_5G_wifi.setChecked(true);
                //					m_rb_2point4G_wifi.setChecked(false);
                //				}
                //				break;
                //			}
                //			else{
                //				m_continue_to_change_to_5g = false;
                //			}
                //			onWifModeChanged();
                break;
            case R.id.set_wifi_security_mode:
                goToWifiSettingFragment();
                m_tv_done.setText(R.string.setting_wifi_save);
                break;
            case R.id.set_wifi_security_encription_type:
                goToWifiSettingFragment();
                m_tv_done.setText(R.string.setting_wifi_save);
                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        revertWifiModeSetting();
    }

    private void popInquireyDialog() {
        final InquireReplaceDialog inquireDlg = new InquireReplaceDialog(SettingWifiActivity.this);
        inquireDlg.setCancelDisabled();
        inquireDlg.m_titleTextView.setText(R.string.dialog_change_to_5g);
        inquireDlg.m_contentTextView.setText(R.string.dialog_warning_5g);
        inquireDlg.m_confirmBtn.setText(R.string.continue_anyway);
        inquireDlg.showDialog(new OnInquireApply() {

            @Override
            public void onInquireApply() {
                onWifModeChanged();
                inquireDlg.closeDialog();
            }
        }, new OnInquireCancle() {

            @Override
            public void onInquireCancel() {
                if (WlanFrequency.Frequency_24GHZ == m_nPreWlanAPMode) {
                    m_rb_2point4G_wifi.setChecked(true);
                    m_rb_5G_wifi.setChecked(false);
                } else {
                    m_rb_5G_wifi.setChecked(true);
                    m_rb_2point4G_wifi.setChecked(false);
                }
                inquireDlg.closeDialog();
            }

        });
    }

    private void goToWifiSettingFragment() {
        setContentVisibility(View.GONE);

        m_tv_edit.setVisibility(View.GONE);
        //		m_tv_done.setVisibility(View.GONE);
        //		m_tv_back.setVisibility(View.GONE);
        //		m_ib_back.setVisibility(View.GONE);

        setTypeSelectionFragmentVisible(true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("Security_Mode", m_nSecurityMode.ordinal());
        dataBundle.putInt("Mode_Type", m_nType);
        FragmentWifiSettingTypeSelection fg = new FragmentWifiSettingTypeSelection();
        fg.setArguments(dataBundle);
        //		ft.replace(R.id.setting_network_wifi_content_container, fg);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void onBtnSSIDSwitch() {
        if (m_ssid_status == SsidHiddenEnum.SsidHidden_Disable) {
            m_ssid_status = SsidHiddenEnum.SsidHidden_Enable;
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
        } else if (m_ssid_status == SsidHiddenEnum.SsidHidden_Enable) {
            m_ssid_status = SsidHiddenEnum.SsidHidden_Disable;
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
        }
    }

    private void onWifModeChanged() {
        m_nPreWlanAPMode = BusinessManager.getInstance().getWlanFrequency();

        if (m_rb_5G_wifi.isChecked()) {
            m_nWlanAPMode = WlanFrequency.Frequency_5GHZ;
            m_strPreSsid = BusinessManager.getInstance().getSsid_5G();
            m_nPreSecurityMode = BusinessManager.getInstance().getSecurityMode_5G();
            if (SecurityMode.Disable == m_nPreSecurityMode) {
                m_nPreType = -1;
            } else if (SecurityMode.WEP == m_nPreSecurityMode) {
                m_nPreType = WEPEncryption.antiBuild(BusinessManager.getInstance().getWEPEncryption_5G());
            } else {
                m_nPreType = WPAEncryption.antiBuild(BusinessManager.getInstance().getWPAEncryption_5G());
                if (SecurityMode.Disable == m_nPreSecurityMode) {
                    m_strPreKey = "";
                } else {
                    m_strPreKey = BusinessManager.getInstance().getWifiPwd_5G();
                }
            }
        } else {
            m_nWlanAPMode = WlanFrequency.Frequency_24GHZ;
            m_strPreSsid = BusinessManager.getInstance().getSsid();
            m_nPreSecurityMode = BusinessManager.getInstance().getSecurityMode();
            if (SecurityMode.Disable == m_nPreSecurityMode) {
                m_nPreType = -1;
            } else if (SecurityMode.WEP == m_nPreSecurityMode) {
                m_nPreType = WEPEncryption.antiBuild(BusinessManager.getInstance().getWEPEncryption());
            } else {
                m_nPreType = WPAEncryption.antiBuild(BusinessManager.getInstance().getWPAEncryption());
                if (SecurityMode.Disable == m_nPreSecurityMode) {
                    m_strPreKey = "";
                } else {
                    m_strPreKey = BusinessManager.getInstance().getWifiPwd();
                }
            }
        }

        m_strSsid = m_strPreSsid;
        m_nSecurityMode = m_nPreSecurityMode;
        m_nType = m_nPreType;
        m_strKey = m_strPreKey;
        initSpinersUI();
    }

    private void onBtnPasswordSwitch() {
        if (m_blPasswordOpened) {
            m_blPasswordOpened = false;
            m_btn_psd_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
            m_et_password.setText("");
            m_et_password.setEnabled(false);
            m_et_password.setVisibility(View.GONE);
            m_ib_hide_password.setVisibility(View.GONE);
            m_ib_show_password.setVisibility(View.GONE);
            m_ll_security.setVisibility(View.GONE);
            m_ll_encryption.setVisibility(View.GONE);
            setAllDividerVisibility(View.GONE);
            setOneDividerVisibility(View.VISIBLE);
            //m_tv_psd_type_title.setVisibility(View.GONE);
        } else {
            m_blPasswordOpened = true;
            m_btn_psd_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
            m_et_password.setText(m_strKey);
            m_et_password.setEnabled(true);
            m_et_password.setVisibility(View.VISIBLE);
            //m_et_password.setBackgroundResource(R.drawable.selector_edit_bg);
            m_ib_hide_password.setVisibility(View.VISIBLE);
            m_ib_show_password.setVisibility(View.GONE);
            m_ll_security.setVisibility(View.VISIBLE);
            m_ll_encryption.setVisibility(View.VISIBLE);
            setAllDividerVisibility(View.VISIBLE);
            //m_tv_psd_type_title.setVisibility(View.VISIBLE);
            setDefaultSecurityType();
        }
        //
        if (!m_ib_show_password.isShown()) {
            m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    private void onBtnEdit() {
        m_tv_done.setVisibility(View.VISIBLE);
        m_ll_edit_ssid_broadcast.setEnabled(true);

        m_ssid_broadcast_container.setVisibility(View.VISIBLE);
        m_divider_under_ssid.setVisibility(View.VISIBLE);

        m_tv_edit.setFocusable(false);
        m_tv_edit.setFocusableInTouchMode(false);
        m_tv_edit.setVisibility(View.GONE);
        m_et_ssid.setEnabled(true);
        m_et_ssid.setVisibility(View.VISIBLE);
        m_tv_ssid.setVisibility(View.GONE);
        m_et_password.setEnabled(true);
        m_et_password.setVisibility(View.VISIBLE);
        //m_et_password.setBackgroundResource(R.drawable.selector_edit_bg);
        //		m_et_ssid.setBackgroundResource(R.drawable.selector_edit_bg);
        //m_et_password.setPadding(0, 0, 200, 0);
        if (m_rg_wifi_mode.isShown()) {
            m_rb_2point4G_wifi.setEnabled(true);
            m_rb_5G_wifi.setEnabled(true);
        }
        //
        //hide text of 'no passwrod'
        m_tv_no_password.setVisibility(View.GONE);
        //show password switch
        m_btn_psd_switch.setVisibility(View.VISIBLE);
        //SecurityMode securityMode = BusinessManager.getInstance().getSecurityMode();
        if (SecurityMode.Disable == m_nSecurityMode) {
            m_blPasswordOpened = false;
            m_btn_psd_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
            m_et_password.setText("");
            m_et_password.setEnabled(false);
            m_et_password.setVisibility(View.GONE);
            //m_et_password.setBackgroundDrawable(null);
            m_ll_encryption.setVisibility(View.GONE);
            m_ll_security.setVisibility(View.GONE);
            setAllDividerVisibility(View.GONE);
            setOneDividerVisibility(View.VISIBLE);
            //m_tv_psd_type_title.setVisibility(View.GONE);
        } else {
            m_blPasswordOpened = true;
            m_btn_psd_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
            m_et_password.setText(m_strKey);
            m_ll_encryption.setVisibility(View.VISIBLE);
            m_ll_security.setVisibility(View.VISIBLE);
            setAllDividerVisibility(View.VISIBLE);
            //m_tv_psd_type_title.setVisibility(View.VISIBLE);
        }
        m_ll_password_status.setVisibility(View.VISIBLE);
        m_et_ssid.setFocusable(true);
        m_et_ssid.setFocusableInTouchMode(true);
        m_et_ssid.requestFocus();
    }

    private void onBtnDone() {
        if (!m_isTypeSelecttionShown) {
            m_strSsid = m_et_ssid.getText().toString();
            if (m_strSsid.length() == 0) {
                m_et_ssid.setText(m_strPreSsid);
                m_tv_ssid.setText(m_strPreSsid);
                String strTost = getString(R.string.setting_wifi_ssid_can_not_be_empty);
                Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
                return;
            }
            boolean blHasChange = isSettingsChanged();
            if (blHasChange) {
                boolean blCheckSsid = ConnectivityUtils.checkSsid(m_strSsid);
                if (!blCheckSsid) {
                    String m_strErrorInfo = getString(R.string.setting_ssid_invalid);
                    m_err_dialog.showDialog(getString(R.string.setting_wifi_error_title), m_strErrorInfo);
                    return;
                }
                if (SecurityMode.Disable != m_nSecurityMode) {
                    boolean blCheckPsd = ConnectivityUtils.checkPassword(m_strKey, m_nSecurityMode);
                    if (!blCheckPsd) {
                        m_err_dialog.setCancelCallback(() -> revertWifiModeSetting());

                        String error = getString(R.string.setting_wpa_password_error_prompt);
                        if (SecurityMode.WEP == m_nSecurityMode)
                            error = getString(R.string.setting_wep_password_error_prompt);
                        m_err_dialog.showDialog(getString(R.string.setting_wifi_error_title), error);
                        return;
                    }
                }
                setWlanSettingItems();
            }
            m_ll_edit_ssid_broadcast.setEnabled(false);

            m_ssid_broadcast_container.setVisibility(View.GONE);
            m_divider_under_ssid.setVisibility(View.GONE);
            //
            synchValues();
            setControlsDoneStatus();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("deprecation")
    private void setControlsDoneStatus() {
        if (WlanSupportMode.Mode2Point4GAnd5G != BusinessManager.getInstance().getWlanSupportMode()) {
            m_rg_wifi_mode.setVisibility(View.GONE);
        } else {
            m_rg_wifi_mode.setVisibility(View.VISIBLE);
        }

        if (WlanFrequency.Frequency_24GHZ == m_nWlanAPMode) {
            m_rb_2point4G_wifi.setChecked(true);
        } else {
            m_rb_5G_wifi.setChecked(true);
        }

        m_tv_edit.setVisibility(View.VISIBLE);
        m_tv_done.setVisibility(View.GONE);
        m_et_ssid.setEnabled(false);
        m_tv_ssid.setText(m_strPreSsid);
        m_tv_ssid.setVisibility(View.VISIBLE);
        m_et_ssid.setVisibility(View.GONE);
        m_et_password.setEnabled(false);
        //m_et_password.setVisibility(View.GONE);
        //m_et_password.setBackgroundDrawable(null);
        //		m_et_ssid.setBackgroundDrawable(null);
        //		m_et_password.setPadding(0, 0, 200, 0);
        if (m_rg_wifi_mode.isShown()) {
            m_rb_2point4G_wifi.setEnabled(false);
            m_rb_5G_wifi.setEnabled(false);
        }
        //show password switch
        m_btn_psd_switch.setVisibility(View.GONE);
        if (SecurityMode.Disable == m_nSecurityMode) {
            m_blPasswordOpened = false;
            m_btn_psd_switch.setBackgroundResource(R.drawable.switch_off);
            m_btn_psd_switch.setVisibility(View.GONE);
            m_tv_no_password.setVisibility(View.VISIBLE);
            m_et_password.setText("");
            m_ib_hide_password.setVisibility(View.GONE);
            m_ib_show_password.setVisibility(View.GONE);

        } else {
            m_blPasswordOpened = true;
            m_btn_psd_switch.setBackgroundResource(R.drawable.switch_on);
            m_btn_psd_switch.setVisibility(View.GONE);
            m_tv_no_password.setVisibility(View.GONE);
            m_ib_hide_password.setVisibility(View.VISIBLE);
            m_ib_show_password.setVisibility(View.GONE);
            if (!m_ib_show_password.isShown()) {
                m_et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                m_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        }
        m_ll_encryption.setVisibility(View.GONE);
        m_ll_security.setVisibility(View.GONE);
        m_ll_password_status.setVisibility(View.GONE);
        setOneDividerVisibility(View.GONE);
        setAllDividerVisibility(View.GONE);
        //m_tv_psd_type_title.setVisibility(View.GONE);
        m_tv_edit.setFocusable(true);
        m_tv_edit.setFocusableInTouchMode(true);
        m_tv_edit.requestFocus();
    }

    private boolean isSettingsChanged() {
        boolean blChanged = false;
        m_nWlanAPMode = WlanFrequency.Frequency_24GHZ;
        if (!m_rb_2point4G_wifi.isChecked()) {
            m_nWlanAPMode = WlanFrequency.Frequency_5GHZ;
        }

        m_strSsid = m_et_ssid.getText().toString();

        if (m_blPasswordOpened) {
            //			m_nSecurityMode = SecurityMode.antiBuild(SecurityMode.WEP);
            //			m_nType = WEPEncryption.antiBuild(WEPEncryption.Open);
            m_strKey = m_et_password.getText().toString();
        } else {
            m_nSecurityMode = SecurityMode.Disable;
            m_nType = -1;
        }

        if (SecurityMode.Disable != m_nSecurityMode) {
            if (m_nPreWlanAPMode != m_nWlanAPMode || 0 != m_strSsid.compareToIgnoreCase(m_strPreSsid) || m_nSecurityMode != m_nPreSecurityMode || m_nType != m_nPreType || 0 != m_strKey.compareToIgnoreCase(m_strPreKey) || !m_pre_ssid_status.equals(m_ssid_status)) {
                blChanged = true;
            }
        } else {
            if (m_nPreWlanAPMode != m_nWlanAPMode || 0 != m_strSsid.compareToIgnoreCase(m_strPreSsid) || m_nSecurityMode != m_nPreSecurityMode || !m_pre_ssid_status.equals(m_ssid_status)) {
                blChanged = true;
            }
        }
        return blChanged;
    }

    private void setWlanSettingItems() {
        DataValue data = new DataValue();
        data.addParam("WlanAPMode", m_nWlanAPMode);
        data.addParam("Ssid", m_strSsid);
        data.addParam("Password", m_strKey);
        data.addParam("Security", m_nSecurityMode);
        data.addParam("Encryption", m_nType);
        data.addParam("SsidStatus", SsidHiddenEnum.antiBuild(m_ssid_status));
        BusinessManager.getInstance().sendRequestMessage(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET, data);
        ShowWaiting(true);
    }

    private void initSpinersUI() {
        if (m_ssid_status.equals(SsidHiddenEnum.SsidHidden_Disable)) {
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
        } else if (m_ssid_status.equals(SsidHiddenEnum.SsidHidden_Enable)) {
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
        }
        SecurityMode securityMode = m_nPreSecurityMode;
        m_et_ssid.setText(m_strPreSsid);
        if (securityMode == SecurityMode.Disable) {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            if (WlanFrequency.Frequency_24GHZ == m_nPreWlanAPMode) {
                m_password = BusinessManager.getInstance().getWifiPwd();
            } else {
                m_password = BusinessManager.getInstance().getWifiPwd_5G();
            }
            m_et_password.setVisibility(View.GONE);
            m_ib_hide_password.setVisibility(View.GONE);
            m_ib_show_password.setVisibility(View.GONE);
            m_ll_encryption.setVisibility(View.GONE);
            m_ll_security.setVisibility(View.GONE);
            //			m_ll_password_status.setVisibility(View.GONE);
            //m_tv_psd_type_title.setVisibility(View.GONE);
            setAllDividerVisibility(View.GONE);
            setOneDividerVisibility(View.VISIBLE);
            m_blPasswordOpened = false;
            m_btn_psd_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
            return;
        } else {
            m_blPasswordOpened = true;
            m_et_password.setVisibility(View.VISIBLE);
            m_et_password.setEnabled(true);
            m_ib_hide_password.setVisibility(View.VISIBLE);
            m_ib_show_password.setVisibility(View.GONE);
            m_btn_psd_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
            m_ll_encryption.setVisibility(View.VISIBLE);
            m_ll_security.setVisibility(View.VISIBLE);
            m_ll_password_status.setVisibility(View.VISIBLE);
            setAllDividerVisibility(View.VISIBLE);
            //m_tv_psd_type_title.setVisibility(View.VISIBLE);
        }

        if (securityMode == SecurityMode.WEP) {
            m_security_type.setText(R.string.setting_wifi_wep);
        } else if (securityMode == SecurityMode.WPA) {
            m_security_type.setText(R.string.setting_wifi_wpa);
        } else if (securityMode == SecurityMode.WPA2) {
            m_security_type.setText(R.string.setting_wifi_wpa2);
        } else if (securityMode == SecurityMode.WPA_WPA2) {
            m_security_type.setText(R.string.setting_wifi_wpa_or_wpa2);
        } else {
            m_security_type.setText(R.string.setting_wifi_wep);
        }

        //
        if (securityMode == SecurityMode.WEP) {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            WEPEncryption wepType = BusinessManager.getInstance().getWEPEncryption();
            m_password = BusinessManager.getInstance().getWifiPwd();
            if (WlanFrequency.Frequency_5GHZ == m_nPreWlanAPMode) {
                wepType = BusinessManager.getInstance().getWEPEncryption_5G();
                m_password = BusinessManager.getInstance().getWifiPwd_5G();
            }
            m_encription_mode.setText(wepType == WEPEncryption.Open ? R.string.setting_wifi_open : R.string.setting_wifi_share);
            m_et_password.setText(m_password);
        } else {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            WPAEncryption wpaType = BusinessManager.getInstance().getWPAEncryption();
            WModeEnum wmode = BusinessManager.getInstance().getWMode();
            m_password = BusinessManager.getInstance().getWifiPwd();
            if (WlanFrequency.Frequency_5GHZ == m_nPreWlanAPMode) {
                wpaType = BusinessManager.getInstance().getWPAEncryption_5G();
                wmode = BusinessManager.getInstance().getWMode_5G();
                m_password = BusinessManager.getInstance().getWifiPwd_5G();
            }
            if (wpaType == WPAEncryption.AUTO) {
                m_encription_mode.setText(R.string.setting_network_mode_auto);
            } else if (wpaType == WPAEncryption.AES) {
                m_encription_mode.setText(R.string.setting_wifi_aes);
            } else {
                m_encription_mode.setText(R.string.setting_wifi_tkip);
            }
            //			if(wmode == WModeEnum.WMode_802_11a_n || wmode == WModeEnum.WMode_802_11g_n) {
            //				
            //			}else{
            //				if(wpaType == WPAEncryption.AUTO){
            //					m_encription_mode.setText(R.string.setting_network_mode_auto);
            //				}
            //				else if(wpaType == WPAEncryption.AES){
            //					m_encription_mode.setText(R.string.setting_wifi_aes);
            //				}
            //				else{
            //					m_encription_mode.setText(R.string.setting_wifi_tkip);
            //				}
            //			}
            m_et_password.setText(m_password);
        }

        if (m_ssid_status.equals(SsidHiddenEnum.SsidHidden_Disable)) {
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
        } else if (m_ssid_status.equals(SsidHiddenEnum.SsidHidden_Enable)) {
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
        }
    }

    private void initValues() {
        m_nPreWlanAPMode = BusinessManager.getInstance().getWlanFrequency();
        m_nWlanAPMode = m_nPreWlanAPMode;

        if (WlanFrequency.Frequency_24GHZ == m_nWlanAPMode) {
            m_pre_ssid_status = BusinessManager.getInstance().getSsidStatus();
            m_strPreSsid = BusinessManager.getInstance().getSsid();

            m_nPreSecurityMode = BusinessManager.getInstance().getSecurityMode();
            if (SecurityMode.Disable == m_nPreSecurityMode) {
                m_nPreType = -1;
            } else if (SecurityMode.WEP == m_nPreSecurityMode) {
                m_nPreType = WEPEncryption.antiBuild(BusinessManager.getInstance().getWEPEncryption());
            } else {
                m_nPreType = WPAEncryption.antiBuild(BusinessManager.getInstance().getWPAEncryption());
            }
            m_strPreKey = BusinessManager.getInstance().getWifiPwd();
            //			if (SecurityMode.Disable == mode) {
            //				m_strPreKey = "";
            //			}else {
            //				m_strPreKey = BusinessManager.getInstance().getWifiPwd();
            //			}		
        } else {
            m_pre_ssid_status = BusinessManager.getInstance().getSsidStatus_5G();
            m_strPreSsid = BusinessManager.getInstance().getSsid_5G();
            SecurityMode mode = BusinessManager.getInstance().getSecurityMode_5G();
            m_nPreSecurityMode = mode;
            if (SecurityMode.Disable == mode) {
                m_nPreType = -1;
            } else if (SecurityMode.WEP == mode) {
                m_nPreType = WEPEncryption.antiBuild(BusinessManager.getInstance().getWEPEncryption_5G());
            } else {
                m_nPreType = WPAEncryption.antiBuild(BusinessManager.getInstance().getWPAEncryption_5G());
            }
            m_strPreKey = BusinessManager.getInstance().getWifiPwd_5G();
            //			if (SecurityMode.Disable == mode) {
            //				m_strPreKey = "";
            //			}else {
            //				m_strPreKey = BusinessManager.getInstance().getWifiPwd_5G();
            //			}
        }
        m_ssid_status = m_pre_ssid_status;

        m_strSsid = m_strPreSsid;

        m_nSecurityMode = m_nPreSecurityMode;

        m_nType = m_nPreType;

        m_strKey = m_strPreKey;
    }

    private void synchValues() {
        m_nPreWlanAPMode = m_nWlanAPMode;
        m_strPreSsid = m_strSsid;
        m_nPreSecurityMode = m_nSecurityMode;
        m_nPreType = m_nType;
        m_strPreKey = m_strKey;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        m_bNeedBack = false;
        super.onResume();
        registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET));

        registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET));
        registerReceiver(m_msgReceiver, new IntentFilter(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET));


        initValues();
        //init controls state
        //		initSpinersUI();
        setControlsDoneStatus();
        BusinessManager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET);
        BusinessManager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET, null);
        ShowWaiting(true);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        m_err_dialog.closeDialog();
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
        Boolean ok = response != null && response.isOk();
        super.onBroadcastReceive(context, intent);
        if (ok && intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET)) {
            initValues();
            //init controls state
            initSpinersUI();
            setControlsDoneStatus();
            ShowWaiting(false);
        }

        if (ok && intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET)) {
            //init controls state
            initSpinersUI();
            setControlsDoneStatus();
        }

        if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)) {
            String strTost = getString(R.string.setting_wifi_set_failed);
            if (ok) {
                strTost = getString(R.string.setting_wifi_set_success);
            } else {
                initValues();
                initSpinersUI();
                setControlsDoneStatus();
            }

            Toast.makeText(this, strTost, Toast.LENGTH_SHORT).show();
            ShowWaiting(false);
        }
    }


    private void setViewGroupVisibility(ViewGroup view, final int visibility) {
        final int count = view.getChildCount();
        for (int i = 0; i < count; ++i) {
            View temp = view.getChildAt(i);
            temp.setVisibility(visibility);
        }
        view.setVisibility(visibility);
    }

    private void setAllDividerVisibility(final int visibility) {
        findViewById(R.id.divider1).setVisibility(visibility);
        findViewById(R.id.divider2).setVisibility(visibility);
        findViewById(R.id.divider3).setVisibility(visibility);
    }

    private void setOneDividerVisibility(final int visibility) {
        findViewById(R.id.divider3).setVisibility(visibility);
    }

    public void setContentVisibility(final int visibility) {
        m_content_container.setVisibility(visibility);
    }

    public void setEditButtonVisibility(final int visibility) {
        m_tv_edit.setVisibility(visibility);

    }

    public void setDoneButtonVisibility(final int visibility) {
        m_tv_done.setVisibility(visibility);
    }

    public void setBackButtonVisibility(final int visibility) {
        m_tv_back.setVisibility(visibility);
        m_ib_back.setVisibility(visibility);
    }

    public void setTypeSelectionFragmentVisible(final boolean isShown) {
        m_isTypeSelecttionShown = isShown;
    }

    public void revertWifiModeSetting() {
        m_nSecurityMode = m_nPreSecurityMode;
        m_nType = m_nPreType;
        setWifiMode(m_nSecurityMode, m_nType);
        m_et_password.setText(m_strPreKey);
    }

    public void setWifiMode(SecurityMode SecurityMode, int Type) {
        m_nSecurityMode = SecurityMode;
        m_nType = Type;
        if (m_nType < 0) {
            if (m_nSecurityMode == SecurityMode.WEP)
                m_nType = 0;
            else
                m_nType = 2;
        }
        switch (m_nSecurityMode) {
            case WEP:
                m_security_type.setText(R.string.setting_wifi_wep);
                switch (m_nType) {
                    case 0:
                        m_encription_mode.setText(R.string.setting_wifi_open);
                        break;
                    case 1:
                        m_encription_mode.setText(R.string.setting_wifi_share);
                        break;
                    default:
                        break;
                }
                return;
            case WPA:
                m_security_type.setText(R.string.setting_wifi_wpa);
                break;
            case WPA2:
                m_security_type.setText(R.string.setting_wifi_wpa2);
                break;
            case WPA_WPA2:
                m_security_type.setText(R.string.setting_wifi_wpa_or_wpa2);
                break;
            case Disable:
                m_security_type.setText(R.string.setting_wifi_wep); //TODO
                break;
            default:
                break;
        }

        switch (m_nType) {
            case 0:
                m_encription_mode.setText(R.string.setting_wifi_tkip);
                break;
            case 1:
                m_encription_mode.setText(R.string.setting_wifi_aes);
                break;
            case 2:
                m_encription_mode.setText(R.string.setting_network_mode_auto);
                break;
            default:
                break;
        }
    }

    private void setDefaultSecurityType() {
        m_nSecurityMode = SecurityMode.WEP;
        m_security_type.setText(R.string.setting_wifi_wep);

        m_nType = 0;
        m_encription_mode.setText(R.string.setting_wifi_open);
    }
}
