package com.alcatel.wifilink.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
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
import com.alcatel.wifilink.business.FeatureVersionManager;
import com.alcatel.wifilink.business.user.LoginHelper;
import com.alcatel.wifilink.common.CPEConfig;
import com.alcatel.wifilink.common.ChangeActivity;
import com.alcatel.wifilink.common.Const;
import com.alcatel.wifilink.common.DataValue;
import com.alcatel.wifilink.common.ErrorCode;
import com.alcatel.wifilink.common.LinkAppSettings;
import com.alcatel.wifilink.common.MessageUti;
import com.alcatel.wifilink.httpservice.BaseResponse;
import com.alcatel.wifilink.ui.activity.QuickSetupActivity;
import com.alcatel.wifilink.ui.activity.SettingAccountActivity;
import com.alcatel.wifilink.ui.activity.SmartLinkV3App;
import com.alcatel.wifilink.ui.dialog.AutoForceLoginProgressDialog.OnAutoForceLoginFinishedListener;
import com.alcatel.wifilink.ui.dialog.ErrorDialog.OnClickBtnCancel;
import com.alcatel.wifilink.ui.dialog.ErrorDialog.OnClickBtnRetry;
import com.alcatel.wifilink.ui.dialog.ForceLoginSelectDialog.OnClickButtonConfirm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginDialog implements OnClickListener, OnKeyListener, TextWatcher {
    private static OnLoginFinishedListener m_loginCallback;
    private static String m_password = CPEConfig.getInstance().getLoginPassword();
    public boolean m_bIsShow = false;
    public boolean m_bIsApply = false;
    OnAutoForceLoginFinishedListener call;
    private Context m_context;
    private Dialog m_dlgLogin = null;
    private ProgressDialog m_dlgProgress = null;
    private View m_vLogin = null;
    private EditText m_etPassword;
    private Button m_btnApply;
    private TextView m_tvPasswordError;
    private Animation m_shake = null;
    // private static LoginDialog m_instance;
    private String m_strMsgInvalidPassword;
    private String m_strMsgWrongPassword;
    private String m_strMsgOtherUserLogined;
    private String m_strMsgLoginTimeUsedOut;
    private AuthenticationBroadcastReceiver m_auReceiver;
    private CancelLoginListener mCancelCallback = null;
    private boolean m_bOtherUserLoginError = false;
    private boolean m_bLoginTimeUsedOutError = false;
    private boolean m_bLoginPasswordError = false;
    private AutoForceLoginProgressDialog m_ForceloginDlg = null;

    //	private CommonErrorInfoSelectDialog m_dialog_err_selece;


    /*	private static boolean m_bAlreadyLogin = false;
                    
                    public static void  setAlreadyLogin(boolean bAlreadyLogin) {
                                    m_bAlreadyLogin = bAlreadyLogin;
                    }
                    
                    public static boolean getAlreadyLogin() {
                                    return m_bAlreadyLogin;
                    }*/
    private CommonErrorInfoDialog m_dialog_err_info;
    private TextView m_tvForgotPassword;
    private OnLoginStatusLisener onLoginStatusListener;

    public LoginDialog(Context context) {
        m_context = context;
        m_auReceiver = new AuthenticationBroadcastReceiver();

        if (null == m_dialog_err_info) {
            m_dialog_err_info = CommonErrorInfoDialog.getInstance(m_context);
        }

        if (null == m_ForceloginDlg) {
            m_ForceloginDlg = new AutoForceLoginProgressDialog(m_context);
        }
        createDialog();
    }

    public boolean getOtherUserLoginErrorStatus() {
        if (m_bOtherUserLoginError) {
            return true;
        } else {
            return false;
        }
    }

    public String getOtherUserLoginString() {
        return m_strMsgOtherUserLogined;
    }


    public boolean getLoginTimeUsedOutErrorStatus() {
        if (m_bLoginTimeUsedOutError) {
            return true;
        } else {
            return false;
        }
    }

    public String getLoginTimeUsedOutString() {
        return m_strMsgLoginTimeUsedOut;
    }

    public boolean getLoginPasswordErrorStatus() {
        if (m_bLoginPasswordError) {
            return true;
        } else {
            return false;
        }
    }

    public String getLoginPasswordErrorString() {
        return m_strMsgWrongPassword;
    }

    public CommonErrorInfoDialog getCommonErrorInfoDialog() {
        return m_dialog_err_info;
    }

    //@StringRes int titleId, @StringRes int msgId
    public void showTimeout() {
        getCommonErrorInfoDialog().showDialog(m_context.getResources().getString(R.string.other_login_warning_title), getLoginTimeUsedOutString());
    }

    public void showOtherLogin() {
        getCommonErrorInfoDialog().showDialog(m_context.getResources().getString(R.string.other_login_warning_title), getOtherUserLoginString());
    }


    public void showDialog(OnLoginFinishedListener callback) {
        m_loginCallback = callback;
                /*	if (getAlreadyLogin()) {
                                                m_callback.onLoginFinished();
		} else {*/

        if (m_dlgLogin != null) {
            m_tvPasswordError.setVisibility(View.GONE);
            m_btnApply.setEnabled(false);
            m_etPassword.setText("");
            m_etPassword.requestFocus();
            m_dlgLogin.show();
            setDialogFullscreen();


            m_bIsShow = true;
            m_bIsApply = false;
        }
        m_vLogin.clearAnimation();
        //}
    }

    //设置dialog宽度满屏用
    private void setDialogFullscreen() {
        WindowManager windowManager = ((Activity) m_context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = m_dlgLogin.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        m_dlgLogin.getWindow().setAttributes(lp);
    }

    public void showDialogDoLogin() {
        if (m_dlgLogin != null) {
            m_tvPasswordError.setVisibility(View.GONE);
            m_btnApply.setEnabled(false);
            m_etPassword.setText("");
            m_etPassword.requestFocus();
            m_dlgLogin.show();
            setDialogFullscreen();
            m_bIsShow = true;
            m_bIsApply = false;
        }
        m_vLogin.clearAnimation();

    }

    private void SetErrorMsg(int nResponseResult, String err) {
        if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
            m_dlgProgress.dismiss();
        }

        if (m_dlgLogin != null) {
            m_tvPasswordError.setVisibility(View.VISIBLE);
            if (BaseResponse.RESPONSE_OK == nResponseResult && err.equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)) {
                m_tvPasswordError.setText(m_strMsgOtherUserLogined);
            } else if (BaseResponse.RESPONSE_OK == nResponseResult && err.equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) {
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

        if (m_dlgLogin != null) {
            m_btnApply.setEnabled(false);
            m_etPassword.setText("");
            m_etPassword.requestFocus();
            m_tvPasswordError.setText("");
            if (!m_bIsShow) {
                m_dlgLogin.show();
                setDialogFullscreen();
            }
            m_bIsShow = true;
            m_bIsApply = false;
        }
        m_vLogin.clearAnimation();
    }

    public void closeDialog() {
        InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_vLogin.getWindowToken(), 0);

        if (m_dlgProgress != null && m_dlgProgress.isShowing())
            m_dlgProgress.dismiss();

        if (m_dlgLogin.isShowing()) {
            m_dlgLogin.dismiss();
        }

        m_bIsShow = false;
        m_bIsApply = false;
    }

    public void destroyDialog() {
        closeDialog();
        m_dialog_err_info.closeDialog();
        m_ForceloginDlg.closeDialog();
        try {
            m_context.unregisterReceiver(m_auReceiver);
        } catch (Exception e) {

        }
    }

    private void createDialog() {
        m_strMsgInvalidPassword = m_context.getResources().getString(R.string.login_invalid_password);
        m_strMsgWrongPassword = m_context.getResources().getString(R.string.login_prompt_str);
        m_strMsgOtherUserLogined = m_context.getResources().getString(R.string.login_other_user_logined_error_msg);
        m_strMsgLoginTimeUsedOut = m_context.getResources().getString(R.string.login_login_time_used_out_msg);

        LayoutInflater factory = LayoutInflater.from(m_context);
        m_vLogin = factory.inflate(R.layout.login_view, null, false);

        m_vLogin.setOnKeyListener(this);
        m_dlgLogin = new Dialog(m_context, R.style.dialog);
        Window window = m_dlgLogin.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_dlgLogin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_dlgLogin.setContentView(m_vLogin);
        m_dlgLogin.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);

        m_tvPasswordError = (TextView) m_vLogin.findViewById(R.id.login_label_prompt);

        m_etPassword = (EditText) m_vLogin.findViewById(R.id.login_edit_view);
        m_etPassword.setOnKeyListener(this);
        m_etPassword.addTextChangedListener(this);
        //try to fix  android:hint font width scale large
        m_etPassword.setTypeface(Typeface.DEFAULT);
        m_etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Const.LOGIN_PASSWD_MAX)});

        m_btnApply = (Button) m_vLogin.findViewById(R.id.login_apply_btn);
        m_btnApply.setOnClickListener(this);

        Button closeBtn = (Button) m_vLogin.findViewById(R.id.login_close_btn);
        closeBtn.setOnClickListener(this);

        // TOAT: 忘记密码按钮
        m_tvForgotPassword = (TextView) m_vLogin.findViewById(R.id.login_forget_password);
        m_tvForgotPassword.setOnClickListener(this);

        m_dlgLogin.setCancelable(false);

        m_context.registerReceiver(m_auReceiver, new IntentFilter(MessageUti.USER_LOGIN_REQUEST));
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
                    m_dlgLogin.dismiss();
                }
                m_bIsShow = false;
                // Show error dialog
                String strTitle = "";
                if (m_bOtherUserLoginError) {
                    strTitle = m_strMsgOtherUserLogined;
                } else if (m_bLoginTimeUsedOutError) {
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
                            mCancelCallback.onCancelLogin();
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

        m_password = m_etPassword.getText().toString();
        SmartLinkV3App.getInstance().setLoginPassword(m_password);
        SmartLinkV3App.getInstance().setLoginUsername(LinkAppSettings.USER_NAME);
        Pattern pattern = Pattern.compile(Const.REG_STR);
        Matcher matcher = pattern.matcher(m_password);
        if (matcher.find()) {
            m_tvPasswordError.setText(m_strMsgInvalidPassword);
            m_tvPasswordError.setVisibility(View.VISIBLE);
            return;
        }

        InputMethodManager imm = (InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_vLogin.getWindowToken(), 0);

        String strTitle = m_context.getString(R.string.login_check_dialog_title);
        String strContent = m_context.getString(R.string.login_check_dialog_title) + "...";
        m_dlgProgress = ProgressDialog.show(m_context, strTitle, strContent, true, false);
        m_dlgProgress.setCancelable(true);
        m_tvPasswordError.setVisibility(View.GONE);

        DataValue data = new DataValue();
        data.addParam("user_name", LinkAppSettings.USER_NAME);
        data.addParam("password", m_password);
        if (SmartLinkV3App.getInstance().IsForcesLogin()) {
            Log.d("ConnectTypeSelect", "forceLogin");
            BusinessManager.getInstance().sendRequestMessage(MessageUti.USER_FORCE_LOGIN_REQUEST, data);
            closeDialog();
            if (call == null) {
                call = new OnAutoForceLoginFinishedListener() {
                    public void onLoginSuccess() {
                        closeDialog();
                        if (null != m_loginCallback) {
                            m_loginCallback.onLoginFinished();
                            m_loginCallback = null;
                        }
                    }

                    public void onLoginFailed(String error_code) {
                        if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD)) {
                            ErrorDialog.getInstance(m_context).showDialog(m_context.getString(R.string.login_psd_error_msg), new OnClickBtnRetry() {
                                @Override
                                public void onRetry() {
                                    showDialog();
                                    //showDialog();
                                }
                            });
                        } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT)) {
                            m_dialog_err_info.showDialog(m_context.getString(R.string.other_login_warning_title), m_strMsgLoginTimeUsedOut);
                        }
                    }
                };
            }
            m_ForceloginDlg.setCallback(call);
        } else {
            Log.d("ConnectTypeSelect", "not forceLogin");
            //BusinessManager.getInstance().sendRequestMessage(MessageUti.USER_LOGIN_REQUEST, data);
            new LoginHelper(m_context) {
                @Override
                public void getLoginStatus(boolean success) {
                    Log.d("ConnectTypeSelect", "getLoginStatus: " + success);
                    if (onLoginStatusListener != null) {
                        onLoginStatusListener.isSuccess(success);
                    }
                }
            }.login(data);
        }
        m_bIsApply = true;
    }

    public interface OnLoginStatusLisener {
        void isSuccess(boolean success);
    }

    public void setOnLoginStatusListener(OnLoginStatusLisener onLoginStatusListener) {
        this.onLoginStatusListener = onLoginStatusListener;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() >= Const.LOGIN_PASSWD_MIN) {
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
            m_dlgLogin.dismiss();
            m_bIsShow = false;
            if (mCancelCallback != null) {
                mCancelCallback.onCancelLogin();
            }
        }

        return false;
    }

    public void setCancelCallback(CancelLoginListener l) {
        mCancelCallback = l;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login_forget_password:// 忘记密码按钮
                ChangeActivity.toActivity(m_context, SettingAccountActivity.class, false, true, false, 0);
                break;

            case R.id.login_apply_btn:
                apply();
                break;

            case R.id.login_close_btn:
                closeDialog();
                if (mCancelCallback != null) {
                    mCancelCallback.onCancelLogin();
                }
                break;
        }
    }

    public interface OnLoginFinishedListener {
        void onLoginFinished();
    }

    public interface CancelLoginListener {
        void onCancelLogin();
    }

    private class AuthenticationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //            String action = intent.getAction();
            BaseResponse response = intent.getParcelableExtra(MessageUti.HTTP_RESPONSE);
            Boolean ok = response != null && response.isOk();
            if (intent.getAction().equalsIgnoreCase(MessageUti.SIM_GET_SIM_STATUS_ROLL_REQUSET)) {
                boolean bCPEWifiConnected = DataConnectManager.getInstance().getCPEWifiConnected();
                if (!bCPEWifiConnected) {
                    closeDialog();
                }
            } else if (intent.getAction().equalsIgnoreCase(MessageUti.USER_LOGIN_REQUEST)) {
                m_bIsApply = false;
                if (!m_dlgLogin.isShowing())
                    return;
                if (ok) {
                    CPEConfig.getInstance().setLoginPassword(m_password);
                    CPEConfig.getInstance().setLoginUsername(LinkAppSettings.USER_NAME);
                    SmartLinkV3App.getInstance().setLoginPassword("");
                    SmartLinkV3App.getInstance().setLoginUsername("");
                    //setAlreadyLogin(true);
                    m_bOtherUserLoginError = false;
                    m_bLoginTimeUsedOutError = false;
                    m_bLoginPasswordError = false;
                    closeDialog();
                    if (null != m_loginCallback) {
                        m_loginCallback.onLoginFinished();
                        m_loginCallback = null;
                    }
                } else if (response.isValid() && response.getErrorCode().equalsIgnoreCase(ErrorCode.ERR_USER_OTHER_USER_LOGINED)) {
                    m_bOtherUserLoginError = true;
                    m_bLoginTimeUsedOutError = false;
                    m_bLoginPasswordError = false;
                    if (!"QuickSetupActivity".equals(QuickSetupActivity.pageName)) {
                        if (!FeatureVersionManager.getInstance().isSupportForceLogin()) {
                            m_dialog_err_info.showDialog(m_context.getString(R.string.other_login_warning_title), m_strMsgOtherUserLogined);
                        } else {
                            ForceLoginSelectDialog.getInstance(m_context).showDialog(new OnClickButtonConfirm() {
                                public void onConfirm() {

                                    m_ForceloginDlg.autoForceLoginAndShowDialog(new OnAutoForceLoginFinishedListener() {
                                        public void onLoginSuccess() {
                                            closeDialog();
                                        }

                                        public void onLoginFailed(String error_code) {
                                            if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_USERNAME_OR_PASSWORD)) {
                                                ErrorDialog.getInstance(m_context).showDialog(m_context.getString(R.string.login_psd_error_msg), new OnClickBtnRetry() {
                                                    @Override
                                                    public void onRetry() {
                                                        showDialog();
                                                    }
                                                });
                                            } else if (error_code.equalsIgnoreCase(ErrorCode.ERR_FORCE_LOGIN_TIMES_USED_OUT)) {
                                                m_dialog_err_info.showDialog(m_context.getString(R.string.other_login_warning_title), m_strMsgLoginTimeUsedOut);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }


                    if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
                        m_dlgProgress.dismiss();
                    }
                    closeDialog();
                    //					SetErrorMsg(nRet, strErrorCode);
                    //					m_vLogin.startAnimation(m_shake);
                    //setAlreadyLogin(false);

                } else if (response.isValid() && response.getErrorCode().equalsIgnoreCase(ErrorCode.ERR_LOGIN_TIMES_USED_OUT)) {
                    m_bLoginTimeUsedOutError = true;
                    m_bOtherUserLoginError = false;
                    m_bLoginPasswordError = false;
                    m_dialog_err_info.showDialog(m_context.getString(R.string.other_login_warning_title), m_strMsgLoginTimeUsedOut);
                    if (null != m_dlgProgress && m_dlgProgress.isShowing()) {
                        m_dlgProgress.dismiss();
                    }
                    closeDialog();
                    //			SetErrorMsg(nRet, strErrorCode);
                    //			m_vLogin.startAnimation(m_shake);
                    //setAlreadyLogin(false);

                } else {
                    m_bLoginPasswordError = true;
                    m_bOtherUserLoginError = false;
                    m_bLoginTimeUsedOutError = false;
                    //SetErrorMsg(nRet, strErrorCode);
                    if (CPEConfig.getInstance().getAutoLoginFlag()) {
                        // Show error dialog
                        String strTitle = "";
                        if (m_bOtherUserLoginError) {
                            strTitle = m_strMsgOtherUserLogined;
                        } else if (m_bLoginTimeUsedOutError) {
                            strTitle = m_strMsgLoginTimeUsedOut;
                        } else {
                            strTitle = m_context.getString(R.string.login_psd_error_msg);
                        }
                        if (m_dlgProgress != null && m_dlgProgress.isShowing())
                            m_dlgProgress.dismiss();
                        ErrorDialog.getInstance(m_context).showDialog(strTitle, new OnClickBtnRetry() {

                            @Override
                            public void onRetry() {
                                showDialog();
                            }
                        });
                    } else {
                        m_vLogin.startAnimation(m_shake);
                    }
                    //setAlreadyLogin(false);
                }
            }
        }
    }

}
