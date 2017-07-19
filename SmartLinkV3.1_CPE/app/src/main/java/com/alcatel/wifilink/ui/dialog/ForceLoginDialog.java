package com.alcatel.wifilink.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.business.BusinessManager;
import com.alcatel.wifilink.business.DataConnectManager;
import com.alcatel.wifilink.common.CPEConfig;
import com.alcatel.wifilink.common.Const;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ErrorCode;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.ui.dialog.ErrorDialog.OnClickBtnCancel;
import com.alcatel.wifilink.ui.dialog.ErrorDialog.OnClickBtnRetry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForceLoginDialog implements OnClickListener, OnKeyListener, TextWatcher {
    private Context m_context;
    public boolean m_bIsShow = false;
    public boolean m_bIsApply = false;
    private Dialog m_dlgForceLogin = null;
    private ProgressDialog m_dlgProgress = null;
    private View m_vForceLogin = null;
    private EditText m_etPassword;
    private Button m_btnApply;
    private TextView m_tvPasswordError;
    private Animation m_shake = null;

    // private static LoginDialog m_instance;
    private String m_strMsgWrongPassword;
    private String m_strMsgLoginTimeUsedOut;
    private AuthenticationBroadcastReceiver m_auReceiver;
    private static OnForceLoginFinishedListener m_forceloginCallback;
    private CancelForceLoginListener mCancelCallback = null;
    private boolean m_bForceLoginTimeUsedOutError = false;
    private boolean m_bForceLoginPasswordError = false;

    private String m_forcelogin_password;

    // just for test
    public String USER_NAME = "admin";
    private CommonErrorInfoDialog m_dialog_err_info;
    private TextView m_tvForgotPassword;


    public ForceLoginDialog(Context context) {
        m_context = context;
        m_auReceiver = new AuthenticationBroadcastReceiver();

        if (null == m_dialog_err_info) {
            m_dialog_err_info = CommonErrorInfoDialog.getInstance(m_context);
        }
        createDialog();
    }

    private class AuthenticationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
            Boolean ok = response != null && response.isOk();

            if (intent.getAction().equalsIgnoreCase(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
                boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
                if (!bCPEWifiConnected) {
                    closeDialog();
                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.USER_FORCE_LOGIN_REQUEST)) {
                m_bIsApply = false;
                if (!m_dlgForceLogin.isShowing())
                    return;
                if (ok) {
                    CPEConfig.getInstance().setLoginPassword(m_forcelogin_password);
                    CPEConfig.getInstance().setLoginUsername(USER_NAME);
                    //setAlreadyLogin(true);
                    m_bForceLoginTimeUsedOutError = false;
                    m_bForceLoginPasswordError = false;
                    closeDialog();
                    if (null != m_forceloginCallback) {
                        m_forceloginCallback.onForceLoginFinished();
                        m_forceloginCallback = null;
                    }

                } else if (response.isValid() && response.getErrorCode().equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT)) {
                    m_bForceLoginTimeUsedOutError = true;
                    m_bForceLoginPasswordError = false;
                    m_dialog_err_info.showDialog(m_context.getString(R.string.other_login_warning_title), m_strMsgLoginTimeUsedOut);
                    if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
                        m_dlgProgress.dismiss();
                    }
                    closeDialog();
                    //			SetErrorMsg(nRet, strErrorCode);
                    //			m_vLogin.startAnimation(m_shake);
                    //setAlreadyLogin(false);

                } else {
                    m_bForceLoginPasswordError = true;
                    m_bForceLoginTimeUsedOutError = false;
                    //SetErrorMsg(nRet, strErrorCode);
                    m_vForceLogin.startAnimation(m_shake);

                    //setAlreadyLogin(false);
                }
            }
        }
    }

    //	public CommonErrorInfoDialog getCommonErrorInfoDialog()
    //	{
    //			return m_dialog_err_info;
    //	}


    public void showDialog(OnForceLoginFinishedListener callback) {
        m_forceloginCallback = callback;
                /*	if (getAlreadyLogin()) {
                                                m_callback.onLoginFinished();
		} else {*/

        if (m_dlgForceLogin != null) {
            m_tvPasswordError.setVisibility(View.GONE);
            m_btnApply.setEnabled(false);
            m_etPassword.setText("");
            m_etPassword.requestFocus();
            m_dlgForceLogin.show();
            m_bIsShow = true;
            m_bIsApply = false;
        }
        m_vForceLogin.clearAnimation();
        //}
    }

    private void SetErrorMsg(int nResponseResult, String err) {
        if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
            m_dlgProgress.dismiss();
        }

        if (m_dlgForceLogin != null) {
            m_tvPasswordError.setVisibility(View.VISIBLE);
            if (BaseResponse.RESPONSE_OK == nResponseResult && err.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT)) {
                m_tvPasswordError.setText(m_strMsgLoginTimeUsedOut);
            } else {
                m_tvPasswordError.setText(m_strMsgWrongPassword);
            }
            m_btnApply.setEnabled(false);
            m_etPassword.setText("");
            m_etPassword.requestFocus();
        }
    }

    public void showDialog() {
        if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
            m_dlgProgress.dismiss();
        }

        if (m_dlgForceLogin != null) {
            m_btnApply.setEnabled(false);
            m_etPassword.setText("");
            m_etPassword.requestFocus();
            m_tvPasswordError.setText("");
            if (!m_bIsShow) {
                m_dlgForceLogin.show();
            }
            m_bIsShow = true;
            m_bIsApply = false;
        }
        m_vForceLogin.clearAnimation();
    }

    public void closeDialog() {
        InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_vForceLogin.getWindowToken(), 0);

        if (m_dlgProgress != null && m_dlgProgress.isShowing())
            m_dlgProgress.dismiss();

        if (m_dlgForceLogin.isShowing()) {
            m_dlgForceLogin.dismiss();
        }

        m_bIsShow = false;
        m_bIsApply = false;
    }

    public void destroyDialog() {
        closeDialog();
        m_dialog_err_info.closeDialog();
        try {
            m_context.unregisterReceiver(m_auReceiver);
        } catch (Exception e) {

        }
    }

    private void createDialog() {
        m_strMsgWrongPassword = m_context.getResources().getString(R.string.login_prompt_str);
        m_strMsgLoginTimeUsedOut = m_context.getResources().getString(R.string.login_login_time_used_out_msg);

        LayoutInflater factory = LayoutInflater.from(m_context);
        m_vForceLogin = factory.inflate(R.layout.login_view, null);

        m_vForceLogin.setOnKeyListener(this);
        m_dlgForceLogin = new Dialog(m_context, R.style.dialog);
        Window window = m_dlgForceLogin.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_dlgForceLogin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_dlgForceLogin.setContentView(m_vForceLogin);
        m_dlgForceLogin.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);

        m_tvPasswordError = (TextView) m_vForceLogin.findViewById(R.id.login_label_prompt);

        m_etPassword = (EditText) m_vForceLogin.findViewById(R.id.login_edit_view);
        m_etPassword.setOnKeyListener(this);
        m_etPassword.addTextChangedListener(this);
        //try to fix  android:hint font width scale large
        m_etPassword.setTypeface(Typeface.DEFAULT);

        m_btnApply = (Button) m_vForceLogin.findViewById(R.id.login_apply_btn);
        m_btnApply.setOnClickListener(this);

        Button closeBtn = (Button) m_vForceLogin.findViewById(R.id.login_close_btn);
        closeBtn.setOnClickListener(this);

        // TOAT: 忘记密码按钮
        m_tvForgotPassword = (TextView) m_vForceLogin.findViewById(R.id.login_forget_password);
        m_tvForgotPassword.setOnClickListener(this);

        m_dlgForceLogin.setCancelable(false);

        m_context.registerReceiver(m_auReceiver, new IntentFilter(MessageUti.USER_FORCE_LOGIN_REQUEST));
        m_context.registerReceiver(m_auReceiver, new IntentFilter(MessageUti.CPE_WIFI_CONNECT_CHANGE));

        m_shake = AnimationUtils.loadAnimation(m_context, R.anim.input_error_shake);
        m_shake.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Hide admin dialog
                if (m_bIsShow) {
                    m_dlgForceLogin.dismiss();
                }
                m_bIsShow = false;
                // Show error dialog
                String strTitle = "";
                if (m_bForceLoginTimeUsedOutError) {
                    strTitle = m_strMsgLoginTimeUsedOut;
                } else {
                    strTitle = m_context.getString(R.string.login_psd_error_msg);
                }
                if (m_dlgProgress != null && m_dlgProgress.isShowing())
                    m_dlgProgress.dismiss();

                ErrorDialog errorDialog = ErrorDialog.getInstance(m_context);
                if (mCancelCallback != null) {
                    errorDialog.setCancelCallback(new OnClickBtnCancel() {

                        @Override
                        public void onCancel() {
                            mCancelCallback.onCancelForceLogin();
                        }

                    });
                }
                errorDialog.showDialog(strTitle, new OnClickBtnRetry() {

                    @Override
                    public void onRetry() {
                        showDialog();
                    }
                });
            }
        });

    }

    private void apply() {
        if (m_bIsApply)
            return;

        m_forcelogin_password = m_etPassword.getText().toString();
        Pattern pattern = Pattern.compile(Const.REG_STR);
        Matcher matcher = pattern.matcher(m_forcelogin_password);
        if (matcher.find()) {
            m_tvPasswordError.setText(m_context.getString(R.string.login_invalid_password));
            m_tvPasswordError.setVisibility(View.VISIBLE);
            return;
        }

        InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_vForceLogin.getWindowToken(), 0);

        String strTitle = m_context.getString(R.string.login_check_dialog_title);
        String strContent = m_context.getString(R.string.login_check_dialog_title) + "...";
        m_dlgProgress = ProgressDialog.show(m_context, strTitle, strContent, true, false);
        m_dlgProgress.setCancelable(true);
        m_tvPasswordError.setVisibility(View.GONE);

        DataValue data = new DataValue();
        data.addParam("user_name", USER_NAME);
        data.addParam("password", m_forcelogin_password);
        BusinessManager.getInstance().sendRequestMessage(MessageUti.USER_FORCE_LOGIN_REQUEST, data);
        m_bIsApply = true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() >= 1) {
            m_btnApply.setEnabled(true);
        } else {
            m_btnApply.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            m_dlgForceLogin.dismiss();
            m_bIsShow = false;
            if (mCancelCallback != null) {
                mCancelCallback.onCancelForceLogin();
            }
        }

        return false;
    }

    public void setCancelCallback(CancelForceLoginListener l) {
        mCancelCallback = l;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_forget_password:// 忘记密码按钮
                // TOAT: 弹出提示
                ToastUtil_m.show(m_context, "i had forgot");
                break;

            case R.id.login_apply_btn:
                apply();
                break;

            case R.id.login_close_btn:
                closeDialog();
                if (mCancelCallback != null) {
                    mCancelCallback.onCancelForceLogin();
                }
                break;
        }
    }

    //
    public interface OnForceLoginFinishedListener {
        public void onForceLoginFinished();
    }

    public interface CancelForceLoginListener {
        public void onCancelForceLogin();
    }

    //	public static boolean isLoginSwitchOff() {
    //		String strLoginFile = Environment.getExternalStorageDirectory()
    //				.getAbsolutePath() + "/CPE/LoginDisable";
    //		File f = new File(strLoginFile);
    //		return f.exists();
    //	}


}
