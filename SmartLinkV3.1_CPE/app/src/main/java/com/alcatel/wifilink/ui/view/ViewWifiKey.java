package com.alcatel.wifilink.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.alcatel.wifilink.common.ConnectivityUtils;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ENUM;
import com.alcatel.wifilink.common.ENUM.SecurityMode;
import com.alcatel.wifilink.common.ENUM.WlanFrequency;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.ui.activity.FragmentWifiSettingTypeSelection;
import com.alcatel.wifilink.ui.dialog.CommonErrorInfoDialog;
import com.alcatel.wifilink.ui.dialog.InquireReplaceDialog;


public class ViewWifiKey extends BaseViewImpl implements OnClickListener {
    private final static String TAG = "ViewWifiKey";
    protected ActivityBroadcastReceiver m_msgReceiver;
    private boolean m_blPasswordOpened = false;
    private WlanFrequency m_nPreWlanAPMode = WlanFrequency.UNKNOWN;
    private WlanFrequency m_nWlanAPMode = WlanFrequency.UNKNOWN;
    private SecurityMode m_nPreSecurityMode = SecurityMode.Disable;
    private SecurityMode m_nSecurityMode = SecurityMode.Disable;
    private String m_strPreSsid = "";
    private String m_strSsid = "";
    private String m_strPreCountryCode = "";
    private String m_strCountryCode = "";
    //    private int m_nPreSecurityMode = 0;
//    private int m_nSecurityMode = 0;
    private int m_nPreType = 0;
    private int m_nType = 0;
    private String m_strPreKey = "";
    private String m_strKey = "";
    //
    private EditText m_et_password;
    private EditText m_et_ssid;
    private ImageButton m_ib_show_password;
    private ImageButton m_ib_hide_password;
    private RadioGroup m_rg_wifi_mode;
    private RadioButton m_rb_2point4G_wifi;
    private RadioButton m_rb_5G_wifi;
    private TextView m_tv_no_password;
    private TextView m_btn_psd_switch;
    private ProgressBar m_pb_waiting = null;
    private TextView m_tv_ssid;

    private LinearLayout m_ll_edit_ssid_broadcast;
    private TextView m_btn_ssid_broadcast_switch;
    private ENUM.SsidHiddenEnum m_pre_ssid_status = ENUM.SsidHiddenEnum.SsidHidden_Disable;
    private ENUM.SsidHiddenEnum m_ssid_status = ENUM.SsidHiddenEnum.SsidHidden_Disable;
    //spiners
    private FrameLayout m_ll_security;
    private FrameLayout m_ll_encryption;
    private LinearLayout m_ll_password_status;
    private TextView m_tv_psd_type_title;
    private TextView m_passwordPrompt;
    private TextView m_encryptionPrompt;
    private String m_curWPAPassword = "";
    private String m_curWEPPassword = "";

    //
    private CommonErrorInfoDialog m_err_dialog;
    private LinearLayout m_content_container;

    private TextView m_security_type;
    private TextView m_encription_mode;

    private boolean m_isTypeSelecttionShown;

    private android.app.FragmentManager fm;
    private LinearLayout m_ssid_broadcast_container;
    private ImageView m_divider_under_ssid;
    private LinearLayout mWifiDoneLinear;
    private boolean m_isTypeSelecttionDone;
    private LinearLayout m_source_configure;
    private FrameLayout mChannel;
    private FrameLayout mCountry;
    private TextView mChannelMode;
    private TextView mCountryType;
    //	private boolean m_continue_to_change_to_5g;
    private OnBackPressCallback onBackPressCallback;

    public ViewWifiKey(Context context) {
        super(context);
        init();
    }

    public ViewWifiKey(Context context, android.app.FragmentManager fm) {
        super(context);
        this.fm = fm;
        init();
    }

    @Override
    protected void init() {
        m_view = LayoutInflater.from(m_context).inflate(R.layout.activity_setting_wifi, null);
        mWifiDoneLinear = ((LinearLayout) m_view.findViewById(R.id.ll_ssid_broadcast));

        //create controls
        createControls();
    }

    private void set_Web_Mode(boolean is_2G) {
        ENUM.SecurityMode securityMode;

        if (is_2G == true)
            securityMode = ENUM.SecurityMode.build(ENUM.SecurityMode.antiBuild(BusinessManager.getInstance().getSecurityMode()));
        else
            securityMode = ENUM.SecurityMode.build(ENUM.SecurityMode.antiBuild(BusinessManager.getInstance().getSecurityMode_5G()));

        if (securityMode == ENUM.SecurityMode.Disable) {
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

        if (securityMode == ENUM.SecurityMode.WEP) {
            m_security_type.setText(R.string.setting_wifi_wep);
        } else if (securityMode == ENUM.SecurityMode.WPA) {
            m_security_type.setText(R.string.setting_wifi_wpa);
        } else if (securityMode == ENUM.SecurityMode.WPA2) {
            m_security_type.setText(R.string.setting_wifi_wpa2);
        } else if (securityMode == ENUM.SecurityMode.WPA_WPA2) {
            m_security_type.setText(R.string.setting_wifi_wpa_or_wpa2);
        } else {
            m_security_type.setText(R.string.setting_wifi_wep);
        }

        //
        if (securityMode == ENUM.SecurityMode.WEP) {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            ENUM.WEPEncryption wepType;
            if (is_2G == true)
                wepType = BusinessManager.getInstance().getWEPEncryption();
            else
                wepType = BusinessManager.getInstance().getWEPEncryption_5G();

            if (wepType == ENUM.WEPEncryption.Open) {
                m_encription_mode.setText(R.string.setting_wifi_open);
            } else {
                m_encription_mode.setText(R.string.setting_wifi_share);
            }

        } else {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            ENUM.WPAEncryption wpaType;
            if (is_2G)
                wpaType = BusinessManager.getInstance().getWPAEncryption();
            else
                wpaType = BusinessManager.getInstance().getWPAEncryption_5G();

            if (wpaType == ENUM.WPAEncryption.AUTO) {
                m_encription_mode.setText(R.string.setting_network_mode_auto);
            } else if (wpaType == ENUM.WPAEncryption.AES) {
                m_encription_mode.setText(R.string.setting_wifi_aes);
            } else {
                m_encription_mode.setText(R.string.setting_wifi_tkip);
            }
        }
    }

    private void createControls() {
//		m_continue_to_change_to_5g = false;
        m_isTypeSelecttionShown = false;
        m_isTypeSelecttionDone = false;
        m_et_ssid = (EditText) m_view.findViewById(R.id.edit_ssid);
        m_et_password = (EditText) m_view.findViewById(R.id.edit_password);
        m_ib_show_password = (ImageButton) m_view.findViewById(R.id.ib_show_password);
        m_ib_hide_password = (ImageButton) m_view.findViewById(R.id.ib_hide_password);
        m_rg_wifi_mode = (RadioGroup) m_view.findViewById(R.id.rg_wifi_mode);
        setViewGroupVisibility(m_rg_wifi_mode, View.VISIBLE);
        m_rb_2point4G_wifi = (RadioButton) m_view.findViewById(R.id.rb_2point4G_wifi);
        m_rb_5G_wifi = (RadioButton) m_view.findViewById(R.id.rb_5G_wifi);
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
        m_tv_no_password = (TextView) m_view.findViewById(R.id.tv_no_psd);
        m_btn_psd_switch = (TextView) m_view.findViewById(R.id.btn_psd_switch);
        m_btn_psd_switch.setOnClickListener(this);

        m_err_dialog = CommonErrorInfoDialog.getInstance(m_context);//
        m_pb_waiting = (ProgressBar) m_view.findViewById(R.id.pb_wifi_waiting_progress);
        m_tv_ssid = (TextView) m_view.findViewById(R.id.tv_ssid);

        m_ll_edit_ssid_broadcast = (LinearLayout) m_view.findViewById(R.id.ll_edit_ssid_broadcast);
        m_ll_edit_ssid_broadcast.setOnClickListener(this);
        m_ll_edit_ssid_broadcast.setEnabled(false);
        m_btn_ssid_broadcast_switch = (TextView) m_view.findViewById(R.id.btn_ssid_broadcast_switch);
//		m_pre_ssid_status = SsidHiddenEnum.SsidHidden_Enable;
        //init spiner
        m_content_container = (LinearLayout) m_view.findViewById(R.id.setting_network_wifi_content);
        m_security_type = (TextView) m_view.findViewById(R.id.set_wifi_security_mode);
        m_encription_mode = (TextView) m_view.findViewById(R.id.set_wifi_security_encription_type);

        m_ssid_broadcast_container = (LinearLayout) m_view.findViewById(R.id.ll_ssid_broadcast_status);
        m_divider_under_ssid = (ImageView) m_view.findViewById(R.id.divider0);
        m_source_configure = ((LinearLayout) m_view.findViewById(R.id.source_configure));

        mChannel = (FrameLayout) m_view.findViewById(R.id.ll_channel);
        mChannel.setOnClickListener(this);
        mChannelMode = (TextView) m_view.findViewById(R.id.set_wifi_channel_mode);
        mChannelMode.setText("Auto");//这个暂时写死
        //TODO:
        mCountry = (FrameLayout) m_view.findViewById(R.id.ll_country);
        mCountry.setOnClickListener(this);
        mCountryType = (TextView) m_view.findViewById(R.id.set_wifi_security_country_type);

        m_ssid_broadcast_container.setVisibility(View.GONE);
        m_divider_under_ssid.setVisibility(View.GONE);
        m_source_configure.setVisibility(View.GONE);

        initSpiners();
    }

    private void ShowWaiting(boolean blShow) {
        if (blShow) {
            m_pb_waiting.setVisibility(View.VISIBLE);
        } else {
            m_pb_waiting.setVisibility(View.GONE);
        }
    }

    private void initSpiners() {
        m_encryptionPrompt = (TextView) m_view.findViewById(R.id.tv_encryption);
        m_passwordPrompt = (TextView) m_view.findViewById(R.id.tv_psd_type_title);
        m_ll_security = (FrameLayout) m_view.findViewById(R.id.ll_security);
        m_ll_security.setOnClickListener(this);
        m_ll_encryption = (FrameLayout) m_view.findViewById(R.id.ll_encryption);
        m_ll_encryption.setOnClickListener(this);
        m_ll_password_status = (LinearLayout) m_view.findViewById(R.id.ll_password_status);
    }

    @Override
    public void onClick(View v) {
        int nbtnID = v.getId();
        switch (nbtnID) {
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
            case R.id.ll_security:
                goToWifiSettingFragment();
                break;
            case R.id.ll_encryption:
                goToWifiSettingFragment();
                break;
            case R.id.ll_channel:
//                Toast.makeText(m_context, "channel", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_country:
//                Toast.makeText(m_context, "country", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }
    }

    private void popInquireyDialog() {
        final InquireReplaceDialog inquireDlg = new InquireReplaceDialog(
                m_context);
        inquireDlg.setCancelDisabled();
        inquireDlg.m_titleTextView.setText(R.string.dialog_change_to_5g);
        inquireDlg.m_contentTextView
                .setText(R.string.dialog_warning_5g);
        inquireDlg.m_confirmBtn.setText(R.string.continue_anyway);
        inquireDlg.showDialog(new InquireReplaceDialog.OnInquireApply() {

            @Override
            public void onInquireApply() {
                onWifModeChanged();
                inquireDlg.closeDialog();
            }
        }, new InquireReplaceDialog.OnInquireCancle() {

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
        setTypeSelectionFragmentVisible(true);
        setM_isTypeSelecttionDone(true);
        android.app.FragmentTransaction ft = fm.beginTransaction();
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("Security_Mode", m_nSecurityMode.ordinal());
        dataBundle.putInt("Mode_Type", m_nType);
        FragmentWifiSettingTypeSelection fg = new FragmentWifiSettingTypeSelection();
        fg.setArguments(dataBundle);
        ft.replace(R.id.setting_network_wifi_content_container, fg);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void onBtnSSIDSwitch() {
        if (m_ssid_status == ENUM.SsidHiddenEnum.SsidHidden_Disable) {
            m_ssid_status = ENUM.SsidHiddenEnum.SsidHidden_Enable;
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
        } else if (m_ssid_status == ENUM.SsidHiddenEnum.SsidHidden_Enable) {
            m_ssid_status = ENUM.SsidHiddenEnum.SsidHidden_Disable;
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
        }
    }

    private void onWifModeChanged() {
        m_nPreWlanAPMode = BusinessManager.getInstance().getWlanFrequency();
        if (m_rb_5G_wifi.isChecked()) {
            m_nWlanAPMode = WlanFrequency.Frequency_5GHZ;

            m_strPreSsid = BusinessManager.getInstance().getSsid_5G();
            m_nPreSecurityMode = BusinessManager.getInstance().getSecurityMode_5G();
            if (ENUM.SecurityMode.Disable == m_nPreSecurityMode) {
                m_nPreType = -1;
            } else if (ENUM.SecurityMode.WEP == m_nPreSecurityMode) {
                m_nPreType = ENUM.WEPEncryption.antiBuild(
                        BusinessManager.getInstance().getWEPEncryption_5G());
            } else {
                m_nPreType = ENUM.WPAEncryption.antiBuild(
                        BusinessManager.getInstance().getWPAEncryption_5G());
                if (ENUM.SecurityMode.Disable == m_nPreSecurityMode) {
                    m_strPreKey = "";
                } else {
                    m_strPreKey = BusinessManager.getInstance().getWifiPwd_5G();
                }
            }
        } else {
            m_nWlanAPMode = WlanFrequency.Frequency_24GHZ;
            m_strPreSsid = BusinessManager.getInstance().getSsid();
            m_nPreSecurityMode = BusinessManager.getInstance().getSecurityMode();
            if (ENUM.SecurityMode.Disable == m_nPreSecurityMode) {
                m_nPreType = -1;
            } else if (ENUM.SecurityMode.WEP == m_nPreSecurityMode) {
                m_nPreType = ENUM.WEPEncryption.antiBuild(
                        BusinessManager.getInstance().getWEPEncryption());
            } else {
                m_nPreType = ENUM.WPAEncryption.antiBuild(
                        BusinessManager.getInstance().getWPAEncryption());
                if (ENUM.SecurityMode.Disable == m_nPreSecurityMode) {
                    m_strPreKey = "";
                } else {
                    m_strPreKey = BusinessManager.getInstance().getWifiPwd();
                }
            }
        }

        m_strSsid = m_strPreSsid;
        m_strPreCountryCode = BusinessManager.getInstance().getCountryCode();
        m_strCountryCode = m_strPreCountryCode;
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
        m_ll_edit_ssid_broadcast.setEnabled(true);

        m_ssid_broadcast_container.setVisibility(View.VISIBLE);
        m_divider_under_ssid.setVisibility(View.VISIBLE);
        m_ll_password_status.setVisibility(View.VISIBLE);
        m_source_configure.setVisibility(View.VISIBLE);

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
        if (ENUM.SecurityMode.Disable == m_nSecurityMode) {
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
        m_et_ssid.setFocusable(true);
        m_et_ssid.setFocusableInTouchMode(true);
        m_et_ssid.requestFocus();
    }

    private void onBtnDone() {
//		m_Btnbar.setText(R.string.wifi_key_edit);
        if (!m_isTypeSelecttionShown) {
            if (!m_isTypeSelecttionDone) {
                m_strSsid = m_et_ssid.getText().toString();
                if (m_strSsid.length() == 0) {
                    m_et_ssid.setText(m_strPreSsid);
                    m_tv_ssid.setText(m_strPreSsid);
                    String strTost = m_context.getString(R.string.setting_wifi_ssid_can_not_be_empty);
                    Toast.makeText(m_context, strTost, Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean blHasChange = isSettingsChanged();
                if (blHasChange) {
                    boolean blCheckSsid = ConnectivityUtils.checkSsid(m_strSsid);
                    if (!blCheckSsid) {
                        m_err_dialog.showDialog(m_context.getString(R.string.setting_wifi_error_title),
                                m_context.getString(R.string.setting_ssid_invalid));
                        return;
                    }
                    if (ENUM.SecurityMode.Disable != m_nSecurityMode) {
                        boolean blCheckPsd = ConnectivityUtils.checkPassword(m_strKey, m_nSecurityMode);
                        if (!blCheckPsd) {
                            m_err_dialog.setCancelCallback(new CommonErrorInfoDialog.OnClickConfirmButton() {

                                @Override
                                public void onConfirm() {
                                    revertWifiModeSetting();
                                }

                            });

                            String error = m_context.getString(R.string.setting_wpa_password_error_prompt);
                            if (SecurityMode.WEP == m_nSecurityMode)
                                error = m_context.getString(R.string.setting_wep_password_error_prompt);
                            m_err_dialog.showDialog(m_context.getString(R.string.setting_wifi_error_title), error);
                            return;
                        }
                    }
                    setWlanSettingItems();
                }
                m_ll_edit_ssid_broadcast.setEnabled(false);
                m_ssid_broadcast_container.setVisibility(View.GONE);
                m_divider_under_ssid.setVisibility(View.GONE);
                m_source_configure.setVisibility(View.GONE);
                synchValues();
                setControlsDoneStatus();
            } else {
                onBackPressCallback.onBackPress();
                revertWifiModeSetting();
            }
        } else {
//			super.onBackPressed();
            m_isTypeSelecttionShown = false;
            onBackPressCallback.onBackPress();
            synchValues();
            revertWifiModeSetting();
        }
    }

    public void setOnBackPressCallback(OnBackPressCallback onBackPressCallback) {
        this.onBackPressCallback = onBackPressCallback;
    }

    @SuppressWarnings("deprecation")
    private void setControlsDoneStatus() {
        if (ENUM.WlanSupportMode.Mode2Point4GAnd5G != BusinessManager.getInstance().getWlanSupportMode()) {
            m_rg_wifi_mode.setVisibility(View.GONE);
        } else {
            m_rg_wifi_mode.setVisibility(View.VISIBLE);
        }

        if (WlanFrequency.Frequency_24GHZ == m_nWlanAPMode) {
            m_rb_2point4G_wifi.setChecked(true);
        } else {
            m_rb_5G_wifi.setChecked(true);
        }

        m_et_ssid.setEnabled(false);
        m_tv_ssid.setText(m_strPreSsid);
        mCountryType.setText(m_strPreCountryCode);
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
        ENUM.SecurityMode securityMode = m_nSecurityMode;
        if (ENUM.SecurityMode.Disable == securityMode) {
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
            if (m_nPreWlanAPMode != m_nWlanAPMode
                    || 0 != m_strSsid.compareToIgnoreCase(m_strPreSsid)
                    || m_nSecurityMode != m_nPreSecurityMode
                    || m_nType != m_nPreType
                    || 0 != m_strKey.compareToIgnoreCase(m_strPreKey)
                    || !m_pre_ssid_status.equals(m_ssid_status)) {
                blChanged = true;
            }
        } else {
            if (m_nPreWlanAPMode != m_nWlanAPMode
                    || 0 != m_strSsid.compareToIgnoreCase(m_strPreSsid)
                    || m_nSecurityMode != m_nPreSecurityMode
                    || !m_pre_ssid_status.equals(m_ssid_status)) {
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
        data.addParam("SsidStatus", m_ssid_status.ordinal());
        data.addParam("CountryCode", m_strCountryCode);
        BusinessManager.getInstance().sendRequestMessage(
                MessageUti.WLAN_SET_WLAN_SETTING_REQUSET, data);
        ShowWaiting(true);
    }

    private void initSpinersUI() {
        if (m_ssid_status.equals(ENUM.SsidHiddenEnum.SsidHidden_Disable)) {
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
        } else if (m_ssid_status.equals(ENUM.SsidHiddenEnum.SsidHidden_Enable)) {
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
        }
        ENUM.SecurityMode securityMode = m_nPreSecurityMode;
        m_et_ssid.setText(m_strPreSsid);
        mCountryType.setText(m_strPreCountryCode);
        if (securityMode == ENUM.SecurityMode.Disable) {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            if (WlanFrequency.Frequency_24GHZ == m_nPreWlanAPMode) {
                m_curWPAPassword = BusinessManager.getInstance().getWifiPwd();
                m_curWEPPassword = BusinessManager.getInstance().getWifiPwd();
            } else {
                m_curWPAPassword = BusinessManager.getInstance().getWifiPwd_5G();
                m_curWEPPassword = BusinessManager.getInstance().getWifiPwd_5G();
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

        if (securityMode == ENUM.SecurityMode.WEP) {
            m_security_type.setText(R.string.setting_wifi_wep);
        } else if (securityMode == ENUM.SecurityMode.WPA) {
            m_security_type.setText(R.string.setting_wifi_wpa);
        } else if (securityMode == ENUM.SecurityMode.WPA2) {
            m_security_type.setText(R.string.setting_wifi_wpa2);
        } else if (securityMode == ENUM.SecurityMode.WPA_WPA2) {
            m_security_type.setText(R.string.setting_wifi_wpa_or_wpa2);
        } else {
            m_security_type.setText(R.string.setting_wifi_wep);
        }

        //
        if (securityMode == ENUM.SecurityMode.WEP) {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            ENUM.WEPEncryption wepType = BusinessManager.getInstance().getWEPEncryption();
            m_curWPAPassword = BusinessManager.getInstance().getWifiPwd();
            m_curWEPPassword = BusinessManager.getInstance().getWifiPwd();
            if (WlanFrequency.Frequency_5GHZ == m_nPreWlanAPMode) {
                wepType = BusinessManager.getInstance().getWEPEncryption_5G();
                m_curWPAPassword = BusinessManager.getInstance().getWifiPwd_5G();
                m_curWEPPassword = BusinessManager.getInstance().getWifiPwd_5G();
            }
            if (wepType == ENUM.WEPEncryption.Open) {
                m_encription_mode.setText(R.string.setting_wifi_open);
            } else {
                m_encription_mode.setText(R.string.setting_wifi_share);
            }
            m_et_password.setText(m_curWEPPassword);
        } else {
            m_encryptionPrompt.setText(R.string.setting_wifi_password_wep_encryption_tip);
            m_passwordPrompt.setText(R.string.setting_wifi_password_wep_psw_tip);
            ENUM.WPAEncryption wpaType = BusinessManager.getInstance().getWPAEncryption();
            ENUM.WModeEnum wmode = BusinessManager.getInstance().getWMode();
            m_curWPAPassword = BusinessManager.getInstance().getWifiPwd();
            m_curWEPPassword = BusinessManager.getInstance().getWifiPwd();
            if (WlanFrequency.Frequency_5GHZ == m_nPreWlanAPMode) {
                wpaType = BusinessManager.getInstance().getWPAEncryption_5G();
                wmode = BusinessManager.getInstance().getWMode_5G();
                m_curWPAPassword = BusinessManager.getInstance().getWifiPwd_5G();
                m_curWEPPassword = BusinessManager.getInstance().getWifiPwd_5G();
            }
            if (wpaType == ENUM.WPAEncryption.AUTO) {
                m_encription_mode.setText(R.string.setting_network_mode_auto);
            } else if (wpaType == ENUM.WPAEncryption.AES) {
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
            m_et_password.setText(m_curWPAPassword);
        }

        if (m_ssid_status.equals(ENUM.SsidHiddenEnum.SsidHidden_Disable)) {
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_on);
        } else if (m_ssid_status.equals(ENUM.SsidHiddenEnum.SsidHidden_Enable)) {
            m_btn_ssid_broadcast_switch.setBackgroundResource(R.drawable.pwd_switcher_off);
        }

    }

    private void initValues() {
        m_nPreWlanAPMode = BusinessManager.getInstance().getWlanFrequency();
        m_nWlanAPMode = m_nPreWlanAPMode;

        if (WlanFrequency.Frequency_24GHZ == m_nWlanAPMode) {
            m_pre_ssid_status = BusinessManager.getInstance().getSsidStatus();
            m_strPreSsid = BusinessManager.getInstance().getSsid();
            m_strPreCountryCode = BusinessManager.getInstance().getCountryCode();

            m_nPreSecurityMode = BusinessManager.getInstance().getSecurityMode();
            if (ENUM.SecurityMode.Disable == m_nPreSecurityMode) {
                m_nPreType = -1;
            } else if (ENUM.SecurityMode.WEP == m_nPreSecurityMode) {
                m_nPreType = ENUM.WEPEncryption.antiBuild(
                        BusinessManager.getInstance().getWEPEncryption());
            } else {
                m_nPreType = ENUM.WPAEncryption.antiBuild(
                        BusinessManager.getInstance().getWPAEncryption());
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
            m_strPreCountryCode = BusinessManager.getInstance().getCountryCode();

            m_nPreSecurityMode = BusinessManager.getInstance().getSecurityMode_5G();
            if (ENUM.SecurityMode.Disable == m_nPreSecurityMode) {
                m_nPreType = -1;
            } else if (ENUM.SecurityMode.WEP == m_nPreSecurityMode) {
                m_nPreType = ENUM.WEPEncryption.antiBuild(
                        BusinessManager.getInstance().getWEPEncryption_5G());
            } else {
                m_nPreType = ENUM.WPAEncryption.antiBuild(
                        BusinessManager.getInstance().getWPAEncryption_5G());
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
        m_strCountryCode = m_strPreCountryCode;
        m_nSecurityMode = m_nPreSecurityMode;
        m_nType = m_nPreType;
        m_strKey = m_strPreKey;
    }

    private void synchValues() {
        m_nPreWlanAPMode = m_nWlanAPMode;
        m_strPreSsid = m_strSsid;
        m_strPreCountryCode = m_strCountryCode;
        m_nPreSecurityMode = m_nSecurityMode;
        m_nPreType = m_nType;
        m_strPreKey = m_strKey;
    }

    @Override
    public void onResume() {
        m_msgReceiver = new ActivityBroadcastReceiver();
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST));
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST));
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET));
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET));
        m_context.registerReceiver(m_msgReceiver,
                new IntentFilter(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET));


        initValues();
        //init controls state
//		initSpinersUI();
        setControlsDoneStatus();
        BusinessManager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET);
        BusinessManager.getInstance().sendRequestMessage(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET, null);
        ShowWaiting(true);
        createControls();
    }

    @Override
    public void onPause() {
        m_err_dialog.closeDialog();
        try {
            m_context.unregisterReceiver(m_msgReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
//        m_context.unregisterReceiver(m_msgReceiver);
    }

    private void setAllDividerVisibility(final int visibility) {
        m_view.findViewById(R.id.divider1).setVisibility(visibility);
        m_view.findViewById(R.id.divider2).setVisibility(visibility);
        m_view.findViewById(R.id.divider3).setVisibility(visibility);
    }

    private void setOneDividerVisibility(final int visibility) {
        m_view.findViewById(R.id.divider3).setVisibility(visibility);
    }

    public void setContentVisibility(final int visibility) {
        m_content_container.setVisibility(visibility);
    }

    public boolean getM_isTypeSelecttionDone() {
        return m_isTypeSelecttionDone;
    }

    public void setM_isTypeSelecttionDone(boolean m_isTypeSelecttionDone) {
        this.m_isTypeSelecttionDone = m_isTypeSelecttionDone;
    }

    public boolean getTypeSelectionFragmentVisible() {
        return m_isTypeSelecttionShown;
    }

    public void setTypeSelectionFragmentVisible(final boolean isShown) {
        m_isTypeSelecttionShown = isShown;
    }

    public void revertWifiModeSetting() {
        m_nSecurityMode = m_nPreSecurityMode;
        m_nType = m_nPreType;
        setWifiMode(m_nSecurityMode, m_nType);
        m_et_password.setText(m_strPreKey);
        m_content_container.setVisibility(View.VISIBLE);
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
                m_security_type.setText(R.string.setting_wifi_wep);
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

    public interface OnBackPressCallback {
        void onBackPress();
    }

    private class ActivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "get action is " + action);
            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
            Boolean ok = response != null && response.isOk();
            if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SETTING_REQUSET)) {
                if (ok) {
                    initValues();
                    //init controls state
                    initSpinersUI();
                    setControlsDoneStatus();
                    ShowWaiting(false);
                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_GET_WLAN_SUPPORT_MODE_REQUSET)) {
                if (ok) {
                    //init controls state
                    initSpinersUI();
                    setControlsDoneStatus();
                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.WLAN_SET_WLAN_SETTING_REQUSET)) {
                String strTost = m_context.getString(R.string.setting_wifi_set_failed);
                if (ok) {
                    strTost = m_context.getString(R.string.setting_wifi_set_success);
                } else {
                    initValues();
                    initSpinersUI();
                    setControlsDoneStatus();
                }

                Toast.makeText(m_context, strTost, Toast.LENGTH_SHORT).show();
                ShowWaiting(false);
            } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_UNEDIT_LIST_REQUEST)) {
                onBtnDone();
            } else if (intent.getAction().equals(MessageUti.WIFI_KEY_GET_EDIT_LIST_REQUEST)) {
                onBtnEdit();
            }
        }
    }
}
