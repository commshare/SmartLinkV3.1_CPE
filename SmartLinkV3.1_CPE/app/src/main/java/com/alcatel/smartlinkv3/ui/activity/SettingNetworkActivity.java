package com.alcatel.smartlinkv3.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.model.WanConnectStatusModel;
import com.alcatel.smartlinkv3.business.model.ConnectionSettingsModel;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.business.model.UsageSettingModel;
import com.alcatel.smartlinkv3.business.profile.HttpGetProfileList.ProfileItem;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM;
import com.alcatel.smartlinkv3.common.ENUM.ConnectionStatus;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;

import java.util.List;
import java.util.Stack;

public class SettingNetworkActivity extends BaseFragmentActivity implements OnClickListener {

    public final String TAG_FRAGMENT_NETWORK_MODE = "FRAGMENT_NETWORK_MODE";
    public final String TAG_FRAGMENT_NETWORK_SELECTION = "FRAGMENT_NETWORK_SELECTION";
    public final String TAG_FRAGMENT_PROFILE_MANAGEMENT = "FRAGMENT_NETWORK_MANAGEMENT";
    public final String TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL = "FRAGMENT_NETWORK_MANAGEMENT_DETAIL";

    //////////////////////////////profile tags start/////////////////////////////////
    public final String TAG_OPERATION = "operation";
    public final String TAG_OPERATION_EDIT_PROFILE = "edit_profile";
    public final String TAG_OPERATION_ADD_PROFILE = "add_profile";
    public final String TAG_PROFILE_NAME = "name";
    public final String TAG_APN = "apn";
    public final String TAG_USER_NAME = "usre_name";
    public final String TAG_PASSWORD = "password";
    public final String TAG_DEFAULT = "default";
    public final String TAG_PROFILE_ID = "profile_id";
    public final String TAG_IS_PREDEFINE = "is_predefine";
    public final String TAG_AUTH_TYPE = "auth_type";
    public final String TAG_DAIL_NUMBER = "dial_number";
    public final String TAG_IP_ADDRESS = "ip_address";
//////////////////////////////profile tags end/////////////////////////////////

    private FragmentNetworkSelection m_fragment_network_selection = null;
    private FragmentProfileManagement m_fragment_profile_management = null;
    private FragmentProfileManagementDetail m_fragment_profile_management_detail = null;

    private Stack<String> m_fragment_tag_stack = null;

    private TextView m_tv_done = null;
    private TextView m_tv_edit = null;
    private FrameLayout m_edit_or_done_container = null;


    private TextView m_tv_title = null;
    private ImageButton m_ib_back = null;
    private TextView m_tv_back = null;
    private RelativeLayout m_network_set_data_plan_container = null;
    private RelativeLayout m_network_change_pin_container = null;
    private RelativeLayout m_network_mode_container = null;
    private RelativeLayout m_network_selection_container = null;
    private RelativeLayout m_network_profile_management = null;
    private LinearLayout m_level_one_menu = null;
    private LinearLayout m_add_and_delete_container = null;

    private TextView m_add_profile;
    private TextView m_delete_profile;
    private List<ProfileItem> m_profile_list_data = null;

    private FragmentManager m_fragment_manager;
    private FragmentTransaction m_transaction;

    private RelativeLayout m_waiting_circle;
    private TextView m_mode_desc;
    private TextView m_selection_desc;
    private TextView m_selected_profile;

    private IntentFilter m_get_network_setting_filter;
    private IntentFilter m_set_network_setting_filter;
    private NetworkSettingReceiver m_network_setting_receiver;


    private RadioGroup m_network_mode_radiogroup = null;
    private final int MODE_ERROR = -1;
    private int m_current_mode = MODE_ERROR;
    private final int MODE_AUTO = 0;
    private final int MODE_2G_ONLY = 1;
    private final int MODE_3G_ONLY = 2;
    private final int MODE_LTE_ONLY = 3;
    private int m_current_network_selection_mode = -1;

    private RadioButton mode_auto = null;
    private RadioButton mode_2g_only = null;
    private RadioButton mode_3g_only = null;
    private RadioButton mode_lte_only = null;

    private boolean m_delete_menu = false;
    private RelativeLayout mRoamingContainer;
    private TextView mRoamingSwitch;
    private TextView mMobileDataSwitch;
    private LinearLayout mSetNetworkModeContainer;

    private boolean m_bIsRoamingDisconnectedEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_setting_network);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);

        controlTitlebar();
        initUi();
    }

    private void controlTitlebar() {
        m_tv_title = (TextView) findViewById(R.id.tv_title_title);
        m_tv_title.setText(R.string.setting_network);
        //back button and text
        m_ib_back = (ImageButton) findViewById(R.id.ib_title_back);
        m_tv_back = (TextView) findViewById(R.id.tv_title_back);
        m_ib_back.setOnClickListener(this);
        m_tv_back.setOnClickListener(this);
    }

    public void changeTitlebar(int title) {
        m_tv_title.setText(title);
    }

    private void initUi() {

        m_get_network_setting_filter = new IntentFilter(MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST);
        m_set_network_setting_filter = new IntentFilter(MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST);
        m_get_network_setting_filter.addAction(MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST);
        m_set_network_setting_filter.addAction(MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST);
        m_network_setting_receiver = new NetworkSettingReceiver();

        m_network_set_data_plan_container = (RelativeLayout) findViewById(R.id.network_set_data_plan);

        m_network_change_pin_container = (RelativeLayout) findViewById(R.id.network_change_pin);


        m_network_mode_container = (RelativeLayout) findViewById(R.id.network_mode);
        m_network_selection_container = (RelativeLayout) findViewById(R.id.network_selection);
        m_network_profile_management = (RelativeLayout) findViewById(R.id.network_profile_management);
        mRoamingContainer = (RelativeLayout) findViewById(R.id.network_roaming_container);
        mRoamingSwitch = (TextView) findViewById(R.id.network_roaming_switch);
        mMobileDataSwitch = (TextView) findViewById(R.id.network_mobile_data_switch);
        m_selected_profile = (TextView) findViewById(R.id.network_selected_profile);

        m_mode_desc = (TextView) findViewById(R.id.network_mode_desc);
        m_selection_desc = (TextView) findViewById(R.id.network_selection_desc);

        m_waiting_circle = (RelativeLayout) findViewById(R.id.waiting_progressbar);

        m_network_set_data_plan_container.setOnClickListener(this);
        m_network_mode_container.setOnClickListener(this);
        m_network_change_pin_container.setOnClickListener(this);
        m_network_selection_container.setOnClickListener(this);
        m_network_profile_management.setOnClickListener(this);
        mRoamingSwitch.setOnClickListener(this);
        mMobileDataSwitch.setOnClickListener(this);


        m_level_one_menu = (LinearLayout) findViewById(R.id.level_one_menu);
        m_fragment_manager = this.getSupportFragmentManager();

        m_add_and_delete_container = (LinearLayout) findViewById(R.id.fl_add_and_delete);
        m_add_and_delete_container.setVisibility(View.GONE);

        m_add_profile = (TextView) findViewById(R.id.tv_titlebar_add);
        m_add_profile.setOnClickListener(this);
        m_delete_profile = (TextView) findViewById(R.id.tv_titlebar_delete);
        m_delete_profile.setOnClickListener(this);


        m_edit_or_done_container = (FrameLayout) findViewById(R.id.fl_edit_or_done);
        m_edit_or_done_container.setVisibility(View.GONE);
        m_tv_edit = (TextView) findViewById(R.id.tv_titlebar_edit);
        m_tv_edit.setVisibility(View.GONE);
        m_tv_done = (TextView) findViewById(R.id.tv_titlebar_done);
        m_tv_done.setVisibility(View.VISIBLE);
        m_tv_done.setOnClickListener(this);

        m_fragment_tag_stack = new Stack<String>();

        m_fragment_network_selection = new FragmentNetworkSelection();
        m_fragment_profile_management = new FragmentProfileManagement();
        m_fragment_profile_management_detail = new FragmentProfileManagementDetail();


//		changeTitlebar(R.string.setting_network_mode);
        mSetNetworkModeContainer = (LinearLayout) findViewById(R.id.setting_network_mode_container);
        m_network_mode_radiogroup = (RadioGroup) findViewById(R.id.setting_network_mode);
        m_current_mode = BusinessManager.getInstance().getNetworkManager().getNetworkMode();
        m_current_network_selection_mode = BusinessManager.getInstance().getNetworkManager().getNetworkSelection();

        mode_auto = (RadioButton) findViewById(R.id.mode_auto);
        mode_2g_only = (RadioButton) findViewById(R.id.mode_2g);
        mode_3g_only = (RadioButton) findViewById(R.id.mode_3g);
        mode_lte_only = (RadioButton) findViewById(R.id.mode_lte);

        m_network_mode_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                switch (checkedId) {
                    case R.id.mode_auto:
                        UserSetNetworkMode(MODE_AUTO);
                        break;
                    case R.id.mode_2g:
                        UserSetNetworkMode(MODE_2G_ONLY);
                        break;
                    case R.id.mode_3g:
                        UserSetNetworkMode(MODE_3G_ONLY);
                        break;
                    case R.id.mode_lte:
                        UserSetNetworkMode(MODE_LTE_ONLY);
                        break;
                    default:
                        break;
                }
            }

        });

//		refresButton();


    }

    public void showFragmentNetworkSelection() {
        showFragment(m_fragment_network_selection, TAG_FRAGMENT_NETWORK_SELECTION);
    }

    public void showFragmentProfileManagement() {
        showFragment(m_fragment_profile_management, TAG_FRAGMENT_PROFILE_MANAGEMENT);
    }

    public void showFragmentProfileManagementDetail(Bundle data) {
        addToFragmentTagStack(TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL);
        m_level_one_menu.setVisibility(View.GONE);
        m_transaction = m_fragment_manager.beginTransaction();
        m_fragment_profile_management_detail.setArguments(null);
        m_fragment_profile_management_detail.setArguments(data);
        m_transaction.replace(R.id.setting_network_content, m_fragment_profile_management_detail, TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL);
        m_transaction.addToBackStack(null);
        m_transaction.commit();
        m_edit_or_done_container.setVisibility(View.VISIBLE);
    }

    public void showAddFragmentProfileManagementDetail() {

        Bundle dataBundle = new Bundle();
        dataBundle.putString(TAG_OPERATION, TAG_OPERATION_ADD_PROFILE);
        m_fragment_profile_management_detail.setArguments(dataBundle);
        showFragment(m_fragment_profile_management_detail, TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL);
        m_edit_or_done_container.setVisibility(View.VISIBLE);
    }

    private void showFragment(Fragment menu, String fragmentTag) {
        addToFragmentTagStack(fragmentTag);
        m_level_one_menu.setVisibility(View.GONE);
        m_transaction = m_fragment_manager.beginTransaction();
        menu.setArguments(null);
        m_transaction.replace(R.id.setting_network_content, menu, fragmentTag);
        m_transaction.addToBackStack(null);
        m_transaction.commit();
    }

    public FragmentManager getSettingNetworkFragmentManager() {
        return m_fragment_manager;
    }

    public String getCurrentFragmentTag() {
        return m_fragment_tag_stack.peek();
    }

    public void addToFragmentTagStack(String fragmentTag) {
        m_fragment_tag_stack.push(fragmentTag);
    }

    public void popFragmentTagStack() {
        if (!m_fragment_tag_stack.isEmpty())
            m_fragment_tag_stack.pop();
    }

    public void setAddAndDeleteVisibility(final int visibility) {
        m_add_and_delete_container.setVisibility(visibility);
    }

    public void setEditOrDoneVisibility(final int visibility) {
        m_edit_or_done_container.setVisibility(visibility);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int nID = v.getId();
        WanConnectStatusModel internetConnState = BusinessManager.getInstance().getWanConnectStatus();
        switch (nID) {
            case R.id.tv_title_back:
            case R.id.ib_title_back:
                m_fragment_profile_management.setDeletePosition(-1);
                if (mSetNetworkModeContainer.getVisibility() == View.VISIBLE) {
                    mSetNetworkModeContainer.setVisibility(View.GONE);
                    changeTitlebar(R.string.setting_network);
                } else {
                    this.onBackPressed();
                }
                break;
            case R.id.network_mode:
//			showFragment(m_fragment_network_mode, TAG_FRAGMENT_NETWORK_MODE);
//			BusinessManager.getInstance().getProfileManager().startAddNewProfile(null);
//			BusinessManager.getInstance().getProfileManager().startDeleteProfile(null);
                if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected) {
                    mSetNetworkModeContainer.setVisibility(View.VISIBLE);
                    changeTitlebar(R.string.setting_network_mode);
                } else if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
                    String strInfo = getString(R.string.setting_network_try_again);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                } else {
                    String strInfo = getString(R.string.setting_network_disconnect_first);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.network_selection:
                if (BusinessManager.getInstance().getSimStatus().m_SIMState == SIMState.Accessable) {
                    if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected)
                        showFragment(m_fragment_network_selection, TAG_FRAGMENT_NETWORK_SELECTION);
                    else if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
                        String strInfo = getString(R.string.setting_network_try_again);
                        Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                    } else {
                        String strInfo = getString(R.string.setting_network_disconnect_first);
                        Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                    }
                } else if (BusinessManager.getInstance().getSimStatus().m_SIMState == SIMState.PinRequired) {
                    String strInfo = getString(R.string.home_sim_loched);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                } else if (BusinessManager.getInstance().getSimStatus().m_SIMState == SIMState.PukRequired) {
                    String strInfo = getString(R.string.home_sim_loched);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                } else {
                    String strInfo = getString(R.string.home_sim_not_accessible);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.network_profile_management:
                if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnected)
                    showFragment(m_fragment_profile_management, TAG_FRAGMENT_PROFILE_MANAGEMENT);
                else if (internetConnState.m_connectionStatus == ConnectionStatus.Disconnecting) {
                    String strInfo = getString(R.string.setting_network_try_again);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                } else {
                    String strInfo = getString(R.string.setting_network_disconnect_first);
                    Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.network_roaming_switch:
                onBtnRoamingAutoDisconnectClick();
                break;
            case R.id.network_mobile_data_switch:
                onBtnMobileDataClick();
                break;
            case R.id.network_set_data_plan:
                onBtnNetWorkDataPlanClick();
                break;
            case R.id.network_change_pin:
                onBtnNetWorkChangePinClick();
                break;
            case R.id.tv_titlebar_add:
                if (m_fragment_profile_management.getIsDeleting()) {
                    return;
                }
                if (m_fragment_tag_stack.size() > 0) {
                    Log.v("STACKFRAGMENT", m_fragment_tag_stack.peek());
                }
//			setAddAndDeleteVisibility(View.GONE);
                Bundle dataBundle = new Bundle();
                dataBundle.putString(TAG_OPERATION, TAG_OPERATION_ADD_PROFILE);
                showFragmentProfileManagementDetail(dataBundle);
                m_add_and_delete_container.setVisibility(View.GONE);
                break;
            case R.id.tv_titlebar_delete:
                if (m_fragment_profile_management.getIsDeleting()) {
                    return;
                }
                if (m_fragment_tag_stack.size() > 0) {
                    Log.v("STACKFRAGMENT", m_fragment_tag_stack.peek());
                }
                m_add_and_delete_container.setVisibility(View.GONE);
                m_edit_or_done_container.setVisibility(View.VISIBLE);
                m_tv_title.setText(R.string.setting_network_profile_management_delete_profile);
                m_delete_menu = true;

                //更新UI
                FragmentProfileManagement.ProfileListAdapter adapterDone = m_fragment_profile_management.getAdapter();
                if (adapterDone != null) {
                    adapterDone.notifyDataSetChanged();
                }
                break;
            case R.id.tv_titlebar_done:
                if (m_fragment_profile_management.getIsDeleting()) {
                    return;
                }
                if (!m_fragment_tag_stack.isEmpty()) {
                    String FragmentTag = m_fragment_tag_stack.peek();
                    if (FragmentTag.equals(TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL)) {
                        m_fragment_profile_management_detail.AddOrDeleteProfile();
//					Log.v("AddOrEditProfile", "TEST");
//					this.onBackPressed();
                    }
                    if (FragmentTag.equals(TAG_FRAGMENT_PROFILE_MANAGEMENT)) {
                        m_edit_or_done_container.setVisibility(View.INVISIBLE);
                        m_add_and_delete_container.setVisibility(View.VISIBLE);
                        m_tv_title.setText(R.string.setting_network_profile_management);
                        m_delete_menu = false;
                    }
                }

                //删除选中的配置
                m_fragment_profile_management.setIsDeleting(true);
                m_fragment_profile_management.deleteProfile();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        m_fragment_tag_stack.clear();
        m_fragment_tag_stack = null;
        m_fragment_network_selection = null;
        m_fragment_profile_management = null;
        m_fragment_profile_management_detail = null;

        try {
            unregisterReceiver(m_network_setting_receiver);
        } catch (Exception e) {

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        popFragmentTagStack();
        setEditOrDoneVisibility(View.GONE);
        if (!m_fragment_tag_stack.isEmpty()) {
            m_level_one_menu.setVisibility(View.GONE);
            String FragmentTag = m_fragment_tag_stack.peek();

            if (FragmentTag.equals(TAG_FRAGMENT_PROFILE_MANAGEMENT)) {
                changeTitlebar(R.string.setting_network_profile_list);
                setAddAndDeleteVisibility(View.VISIBLE);
            } else if (FragmentTag.equals(TAG_FRAGMENT_NETWORK_SELECTION)) {
                changeTitlebar(R.string.setting_network_selection);
                setAddAndDeleteVisibility(View.GONE);
            } else if (FragmentTag.equals(TAG_FRAGMENT_PROFILE_MANAGEMENT_DETAIL)) {
                changeTitlebar(R.string.setting_network_profile_management_profile_detail);
                setAddAndDeleteVisibility(View.GONE);
            } else if (FragmentTag.equals(TAG_FRAGMENT_NETWORK_MODE)) {
                changeTitlebar(R.string.setting_network_mode);
                setAddAndDeleteVisibility(View.GONE);
            }
        } else {
            m_level_one_menu.setVisibility(View.VISIBLE);
            changeTitlebar(R.string.setting_network);
            setAddAndDeleteVisibility(View.GONE);
        }
    }

    private void UserGetNetworkSetting() {
        registerReceiver(m_network_setting_receiver, m_get_network_setting_filter);
        registerReceiver(m_network_setting_receiver, m_set_network_setting_filter);

        /*--------------- add start 2017.1.6 ---------------*/
        registerReceiver(m_network_setting_receiver, new IntentFilter(
                MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET));
        registerReceiver(m_network_setting_receiver, new IntentFilter(
                MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET));
        registerReceiver(m_network_setting_receiver, new IntentFilter(
                MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET));
        registerReceiver(m_network_setting_receiver, new IntentFilter(
                MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET));
        /*--------------- add end 2017.1.6 ---------------*/

        BusinessManager.getInstance().sendRequestMessage(
                MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST, null);
        m_waiting_circle.setVisibility(View.VISIBLE);
        m_network_mode_container.setEnabled(false);
        m_network_selection_container.setEnabled(false);
        m_network_profile_management.setEnabled(false);
    }

    public boolean isDeleteMode() {
        return m_delete_menu;
    }


    private void refresButton() {
        switch (BusinessManager.getInstance().getNetworkManager().getNetworkMode()) {
            case MODE_AUTO:
                mode_auto.setChecked(true);

                break;
            case MODE_2G_ONLY:

                mode_2g_only.setChecked(true);

                break;
            case MODE_3G_ONLY:

                mode_3g_only.setChecked(true);

                break;
            case MODE_LTE_ONLY:

                mode_lte_only.setChecked(true);
                break;
            default:
                break;
        }
    }

    private void UserSetNetworkMode(final int mode) {
        if (BusinessManager.getInstance().getNetworkManager().getNetworkSelection() != MODE_ERROR) {
            DataValue data = new DataValue();
            data.addParam("network_mode", mode);
            data.addParam("netselection_mode", BusinessManager.getInstance().getNetworkManager().getNetworkSelection());
            BusinessManager.getInstance().sendRequestMessage(
                    MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST, data);
        }
        mSetNetworkModeContainer.setVisibility(View.GONE);
        changeTitlebar(R.string.setting_network);
        m_waiting_circle.setVisibility(View.VISIBLE);
        m_network_mode_container.setEnabled(false);
        m_network_selection_container.setEnabled(false);
        m_network_profile_management.setEnabled(false);
    }

    public void setDeleteProfle(boolean enable) {
        m_delete_menu = enable;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
//		BusinessManager.getInstance().getNetworkManager().GetNetworkSettings(null);

//		DataValue data = new DataValue();
//		data.addParam("network_mode", 0);
//		data.addParam("netselection_mode", 0);
//		BusinessManager.getInstance().sendRequestMessage(
//				MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST, data);
        UserGetNetworkSetting();
        showRoamingAutoDisconnectBtn();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        m_bIsRoamingDisconnectedEdit = false;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        this.registerReceiver(m_network_setting_receiver, new IntentFilter(MessageUti.PROFILE_GET_PROFILE_LIST_REQUEST));
        BusinessManager.getInstance().getProfileManager().startGetProfileList(null);
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    private class NetworkSettingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
            Boolean ok = response != null && response.isOk();

            if (intent.getAction().equals(MessageUti.STATISTICS_GET_USAGE_SETTINGS_ROLL_REQUSET)) {
                if (ok) {
                    showRoamingAutoDisconnectBtn();
                }
            } else if (intent.getAction().equals(MessageUti.WAN_GET_CONNECT_STATUS_ROLL_REQUSET)
                    || intent.getAction().equals(MessageUti.WAN_CONNECT_REQUSET)
                    || intent.getAction().equals(MessageUti.WAN_DISCONNECT_REQUSET)) {
                if (ok) {
                    showRoamingAutoDisconnectBtn();
                }
            }

            if (intent.getAction().equalsIgnoreCase(
                    MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST)) {
                if (ok) {
                    switch (BusinessManager.getInstance().getNetworkManager().getNetworkMode()) {

                        case 0:
                            m_mode_desc.setText("Auto");
                            mode_auto.setChecked(true);

                            break;
                        case 1:
                            m_mode_desc.setText("2G only");
                            mode_2g_only.setChecked(true);

                            break;
                        case 2:
                            m_mode_desc.setText("3G only");
                            mode_3g_only.setChecked(true);

                            break;
                        case 3:
                            m_mode_desc.setText("4G only");
                            mode_lte_only.setChecked(true);
                            break;
                        default:
                            m_mode_desc.setText("Error");
                            break;

                    }

                    switch (BusinessManager.getInstance().getNetworkManager().getNetworkSelection()) {
                        case 0:
                            m_selection_desc.setText("Auto");
                            break;
                        case 1:
                            m_selection_desc.setText("Manual");
                            break;
                        default:
                            break;
                    }

//					current_network_mode = BusinessManager.getInstance().getNetworkManager().getNetworkMode();
//					current_network_selection_mode = BusinessManager.getInstance().getNetworkManager().getNetworkSelection();

                    m_network_mode_container.setEnabled(true);
                    m_network_selection_container.setEnabled(true);
                    m_network_profile_management.setEnabled(true);
                    m_waiting_circle.setVisibility(View.GONE);
                } else if (response.isValid()) {
                    //Log
                    String strInfo = getString(R.string.unknown_error);
                    Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
                }
                m_waiting_circle.setVisibility(View.GONE);
            }

            if (intent.getAction().equalsIgnoreCase(
                    MessageUti.NETWORK_SET_NETWORK_SETTING_REQUEST)) {
                if (ok) {
                    BusinessManager.getInstance().sendRequestMessage(
                            MessageUti.NETWORK_GET_NETWORK_SETTING_REQUEST, null);
                } else if (response.isValid()) {
                    //Log
                    String strInfo = getString(R.string.unknown_error);
                    Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
                }
                m_waiting_circle.setVisibility(View.GONE);
            }

            if (intent.getAction().equalsIgnoreCase(
                    MessageUti.PROFILE_GET_PROFILE_LIST_REQUEST)) {

                if (ok) {
//					m_network_search_result_list = BusinessManager.getInstance().getNetworkManager().getNetworkList();
                    m_profile_list_data = BusinessManager.getInstance().getProfileManager().GetProfileListData();
                    for (ProfileItem a : m_profile_list_data) {
                        if (a.Default == 1) {
                            m_selected_profile.setText(a.ProfileName);
                            break;
                        }
                    }
                } else if (response.isValid()) {
                    String strInfo = getString(R.string.unknown_error);
                    Toast.makeText(context, strInfo, Toast.LENGTH_SHORT).show();
                }
                m_waiting_circle.setVisibility(View.GONE);
            } else if (intent.getAction().equals(
                    MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET)) {
                m_bIsRoamingDisconnectedEdit = false;
                showRoamingAutoDisconnectBtn();
            } else if (intent.getAction().equals(
                    MessageUti.WAN_GET_CONNTCTION_SETTINGS_ROLL_REQUSET)) {
                if (ok) {
                    showRoamingAutoDisconnectBtn();
                }
            }

        }
    }

    private void showRoamingAutoDisconnectBtn() {

        SimStatusModel simState = BusinessManager.getInstance().getSimStatus();
        //if (simState.m_SIMState == SIMState.Accessable) {
        UsageSettingModel usageSetting = BusinessManager.getInstance().getUsageSettings();
        ConnectionSettingsModel connectionSetting = BusinessManager.getInstance().getConnectSettings();
        if (m_bIsRoamingDisconnectedEdit == false) {
            //				if (usageSetting.HMonthlyPlan > 0) {
            mRoamingSwitch.setEnabled(true);
            if (connectionSetting.HRoamingConnect == ENUM.OVER_ROAMING_STATE.Disable) {
                // off
                mRoamingSwitch
                        .setBackgroundResource(R.drawable.general_btn_off);
            } else {
                // on
                mRoamingSwitch
                        .setBackgroundResource(R.drawable.general_btn_on);
            }
            //				} else {
            //					m_roamingDisconnectBtn.setEnabled(false);
            //					m_roamingDisconnectBtn
            //							.setBackgroundResource(R.drawable.switch_off);
            //				}
        }
        //		} else {
        //			m_usageAutoDisconnectBtn.setEnabled(false);
        //			// set disable pic
        //			m_usageAutoDisconnectBtn
        //					.setBackgroundResource(R.drawable.switch_off);
        //		}
    }

    private void onBtnRoamingAutoDisconnectClick() {
        m_bIsRoamingDisconnectedEdit = true;
        ConnectionSettingsModel connectionSetting = BusinessManager.getInstance().getConnectSettings();
        DataValue data = new DataValue();
        if (connectionSetting.HRoamingConnect == ENUM.OVER_ROAMING_STATE.Disable) {
            mRoamingSwitch
                    .setBackgroundResource(R.drawable.general_btn_on);
            data.addParam("roaming_connect_flag", ENUM.OVER_ROAMING_STATE.Enable);
        } else {
            mRoamingSwitch
                    .setBackgroundResource(R.drawable.general_btn_off);
            data.addParam("roaming_connect_flag", ENUM.OVER_ROAMING_STATE.Disable);
        }
        BusinessManager.getInstance().sendRequestMessage(
                MessageUti.WAN_SET_ROAMING_CONNECT_FLAG_REQUSET,
                data);

    }

    private void onBtnNetWorkDataPlanClick() {
        Intent intent = new Intent(this, UsageSettingActivity.class);
        this.startActivity(intent);
    }

    private void onBtnNetWorkChangePinClick() {
        Intent intent = new Intent(this, SettingChangePinActivity.class);
        this.startActivity(intent);
    }

    private void onBtnMobileDataClick() {
        // TODO : need test
        if (true) {
            WanConnectStatusModel internetConnState = BusinessManager.getInstance()
                    .getWanConnectStatus();

            if (internetConnState.m_connectionStatus == ConnectionStatus.Connected) {
                mMobileDataSwitch.setBackgroundResource(R.drawable.switch_off);
                BusinessManager.getInstance().sendRequestMessage(
                        MessageUti.WAN_DISCONNECT_REQUSET);
            } else {
                mMobileDataSwitch.setBackgroundResource(R.drawable.switch_on);
                BusinessManager.getInstance().sendRequestMessage(MessageUti.WAN_CONNECT_REQUSET);
            }
        }

//        UsageSettingModel usageSetting = BusinessManager.getInstance()
//                .getUsageSettings();
//        if (usageSetting.HMonthlyPlan > 0) {
//            //m_bIsAutoDisconnectedEdit = true;
//            DataValue data = new DataValue();
//
//            if (usageSetting.HAutoDisconnFlag == ENUM.OVER_DISCONNECT_STATE.Disable) {
//                mMobileDataSwitch
//                        .setBackgroundResource(R.drawable.switch_on);
//                data.addParam("auto_disconn_flag", ENUM.OVER_DISCONNECT_STATE.Enable);
//            } else {
//                mMobileDataSwitch
//                        .setBackgroundResource(R.drawable.switch_off);
//                data.addParam("auto_disconn_flag", ENUM.OVER_DISCONNECT_STATE.Disable);
//            }
//            BusinessManager.getInstance().sendRequestMessage(
//                    MessageUti.STATISTICS_SET_AUTO_DISCONN_FLAG_REQUSET,
//                    data);
//        }
    }

}
