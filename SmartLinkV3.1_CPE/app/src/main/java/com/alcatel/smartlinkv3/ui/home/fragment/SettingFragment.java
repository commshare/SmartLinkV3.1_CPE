package com.alcatel.smartlinkv3.ui.home.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.ChangeActivity;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.common.ToastUtil;
import com.alcatel.smartlinkv3.common.ToastUtil_m;
import com.alcatel.smartlinkv3.ui.activity.EthernetWanConnectionActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingAboutActivity;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.network.ResponseBody;
import com.alcatel.smartlinkv3.ui.activity.AboutActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingAccountActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingDeviceActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingNetworkActivity;
import com.alcatel.smartlinkv3.ui.activity.SettingShareActivity;
import com.alcatel.smartlinkv3.ui.home.helper.setting.settingBroadcast;

/**
 * Created by qianli.ma on 2017/6/16.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {

    public static boolean isFtpSupported = false;
    public static boolean isDlnaSupported = false;
    public static boolean isSharingSupported = false;
    public static boolean m_blFirst = true;
    private settingBroadcast m_receiver;
    private int upgradeStatus;

    public static final String ISDEVICENEWVERSION = "IS_DEVICE_NEW_VERSION";
    private RelativeLayout mLoginPassword;
    private RelativeLayout mMobileNetwork;
    private RelativeLayout mEthernetWan;
    private RelativeLayout mSharingService;
    private RelativeLayout mFirmwareUpgrade;
    private RelativeLayout mRestart;
    private RelativeLayout mBackup;
    private RelativeLayout mAbout;
    private View m_view;

    private static final int RESTART_RESET = 1;
    private static final int Backup_Restore = 2;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = View.inflate(getActivity(), R.layout.fragment_home_setting, null);
        init();
        initEvent();
        initBroadcast();
        return m_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(m_receiver);
        } catch (Exception e) {

        }
    }

    private void registerReceiver() {
        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.UPDATE_SET_DEVICE_STOP_UPDATE));
        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.UPDATE_GET_DEVICE_NEW_VERSION));
        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.SHARING_GET_DLNA_SETTING_REQUSET));
        getActivity().registerReceiver(m_receiver, new IntentFilter(MessageUti.SHARING_GET_FTP_SETTING_REQUSET));
    }

    private void initBroadcast() {
        m_receiver = new settingBroadcast();
    }

    private void init() {
        m_view = LayoutInflater.from(getActivity()).inflate(R.layout.view_setting, null);
        mLoginPassword = (RelativeLayout) m_view.findViewById(R.id.setting_login_password);
        mMobileNetwork = (RelativeLayout) m_view.findViewById(R.id.setting_mobile_network);
        mEthernetWan = (RelativeLayout) m_view.findViewById(R.id.setting_ethernet_wan);
        mSharingService = (RelativeLayout) m_view.findViewById(R.id.setting_sharing_service);
        mFirmwareUpgrade = (RelativeLayout) m_view.findViewById(R.id.setting_firmware_upgrade);
        mRestart = (RelativeLayout) m_view.findViewById(R.id.setting_restart);
        mBackup = (RelativeLayout) m_view.findViewById(R.id.setting_backup);
        mAbout = (RelativeLayout) m_view.findViewById(R.id.setting_about);
    }

    private void initEvent() {
        mLoginPassword.setOnClickListener(this);
        mMobileNetwork.setOnClickListener(this);
        mEthernetWan.setOnClickListener(this);
        mSharingService.setOnClickListener(this);
        mFirmwareUpgrade.setOnClickListener(this);
        mRestart.setOnClickListener(this);
        mBackup.setOnClickListener(this);
        mAbout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_login_password:
                goToAccountSettingPage();
                break;
            case R.id.setting_mobile_network:
                // TODO
                goToMobileNetworkSettingPage();
                break;
            case R.id.setting_ethernet_wan:
                goEthernetWanConnectionPage();
                break;
            case R.id.setting_sharing_service:
                if (isSharingSupported) {
                    goToShareSettingPage();
                } else {
                    ToastUtil_m.show(getActivity(), getString(R.string.setting_not_support_sharing_service));
                }
                break;
            case R.id.setting_firmware_upgrade:
                goToDeviceSettingPage();
                break;
            case R.id.setting_restart:
                popDialogFromBottom(RESTART_RESET);
                break;
            case R.id.setting_backup:
                popDialogFromBottom(Backup_Restore);
                break;
            case R.id.setting_about:
                goToAboutSettingPage();
                break;
            default:
                break;
        }
    }

    private void goEthernetWanConnectionPage() {
        ChangeActivity.toActivity(getActivity(), EthernetWanConnectionActivity.class, true, true, false, 0);
    }


    private void popDialogFromBottom(int itemType) {
        PopupWindow popupWindow = new PopupWindow(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_from_bottom, null);

        TextView mFirstTxt = (TextView) view.findViewById(R.id.first_txt);
        TextView mSecondTxt = (TextView) view.findViewById(R.id.second_txt);
        TextView mCancelTxt = (TextView) view.findViewById(R.id.cancel_txt);
        popupWindow.setContentView(view);
        if (itemType == RESTART_RESET) {
            mFirstTxt.setText(R.string.restart);
            mSecondTxt.setText(R.string.reset_to_factory_settings);

        } else {
            mFirstTxt.setText(R.string.backup);
            mSecondTxt.setText(R.string.restore);
        }

        mFirstTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemType == RESTART_RESET) {
                    restartDevice();
                } else {
                    backupDevice();
                }
            }
        });
        mSecondTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemType == RESTART_RESET) {
                    showDialogResetFactorySetting();
                } else {
                    restore();
                }
            }
        });
        mCancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                backgroundAlpha(getActivity(), 1f);
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(getActivity(), 1f);
            }
        });
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.orange));
        backgroundAlpha(getActivity(), 0.5f);
        popupWindow.setAnimationStyle(R.style.dialogStyle);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

    }

    private void backupDevice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.backup_current_settings_to);
        EditText editText = new EditText(getActivity());
        editText.setText("TCL/LINKHUB/Backup");
        builder.setView(editText);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.backup, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                backup();
            }
        });

    }


    private void showDialogResetFactorySetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.reset_device);
        builder.setMessage(R.string.this_will_erase_all_settings_from_your_device_and_reset_to_factory_defaults_this_action_cannot_be_undone);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetDevice();

            }
        });
        builder.show();
    }

    private void restartDevice() {
        API.get().restartDevice(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                ToastUtil.showMessage(getActivity(), "restart device success");
            }

            @Override
            protected void onFailure() {
                super.onFailure();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
            }
        });
    }


    private void resetDevice() {
        API.get().resetDevice(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                dismissLoadingDialog();
                showSuccessDialog();
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                dismissLoadingDialog();
                showFailedDialog(R.string.couldn_t_reset_try_again);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                dismissLoadingDialog();
                showFailedDialog(R.string.couldn_t_reset_try_again);
            }
        });

    }

    private void backup() {
        API.get().bakcupDevice(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                dismissLoadingDialog();
            }

            @Override
            protected void onFailure() {
                super.onFailure();
                dismissLoadingDialog();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                super.onResultError(error);
                dismissLoadingDialog();
            }
        });

    }

    private void restore() {
    }


    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.complete));
        builder.setCancelable(true);
        builder.show();
    }

    private void showFailedDialog(int stringId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(stringId));
        builder.setCancelable(true);
        builder.show();
    }

    private void showLoadingDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mProgressDialog.setMessage("Resetting … 50%");
        mProgressDialog.show();

    }

    private void dismissLoadingDialog() {
        mProgressDialog.dismiss();
    }

    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }


    /* -------------------------------------------- HELPER -------------------------------------------- */
    /* -------------------------------------------- HELPER -------------------------------------------- */
    private void goToShareSettingPage() {
        Intent intent = new Intent(getActivity(), SettingShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("FtpSupport", isFtpSupported);
        bundle.putBoolean("DlnaSupport", isDlnaSupported);
        intent.putExtra("Sharing", bundle);
        getActivity().startActivity(intent);
    }

    private void goToAccountSettingPage() {
        // to setting account activity
        ChangeActivity.toActivity(getActivity(), SettingAccountActivity.class, true, true, false, 0);
    }

    private void goToMobileNetworkSettingPage() {
        // to setting network activity
        ChangeActivity.toActivity(getActivity(), SettingNetworkActivity.class, true, true, false, 0);
    }

    private void goToDeviceSettingPage() {
        // to setting device activity
        ChangeActivity.toActivity(getActivity(), SettingDeviceActivity.class, true, true, false, 0);
    }

    private void goToAboutSettingPage() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        intent.putExtra("upgradeStatus", upgradeStatus);
        getActivity().startActivity(intent);
    }

}
