package com.alcatel.smartlinkv3.ui.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.appwidget.RippleView;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.UserLoginStatus;
import com.alcatel.smartlinkv3.common.ErrorCode;
import com.alcatel.smartlinkv3.common.LinkAppSettings;
import com.alcatel.smartlinkv3.common.MessageUti;
import com.alcatel.smartlinkv3.httpservice.BaseResponse;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.network.ResponseBody;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingAccountActivity extends BaseActivity implements OnClickListener {

    public static final String LOGOUT_FLAG = "LogoutFlag";

    private TextView m_tv_title = null;
    private ImageButton m_ib_back = null;
    private TextView m_tv_back = null;
    private TextView m_tv_done;
    private LinearLayout m_inputpwd;
    private TextView m_notice;
    private FrameLayout m_fl_titlebar;

    private EditText m_current_password;
    private EditText m_new_password;
    private EditText m_confirm_password;

    private IntentFilter m_change_password_filter;

    private Dialog mTipsDialog;
    private RippleView mRpForgotPassword;
    private RelativeLayout mRlForgotPasswordPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_setting_account);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
        controlTitlebar();
        initUi();
    }

    private void controlTitlebar() {
        m_tv_title = (TextView) findViewById(R.id.tv_title_title);
        m_tv_title.setText(R.string.setting_account);
        //back button and text
        m_ib_back = (ImageButton) findViewById(R.id.ib_title_back);
        m_tv_back = (TextView) findViewById(R.id.tv_title_back);

        m_fl_titlebar = (FrameLayout) findViewById(R.id.fl_edit_or_done);
        m_fl_titlebar.setVisibility(View.VISIBLE);

        m_tv_done = (TextView) findViewById(R.id.tv_titlebar_done);
        m_tv_done.setVisibility(View.VISIBLE);

        findViewById(R.id.tv_titlebar_edit).setVisibility(View.GONE);

        mRlForgotPasswordPop = (RelativeLayout) findViewById(R.id.mRl_forgotPassword_pop);
        mRpForgotPassword = (RippleView) findViewById(R.id.mRp_forgotPassword);
        mRpForgotPassword.setOnClickListener(this);

        m_ib_back.setOnClickListener(this);
        m_tv_back.setOnClickListener(this);
        m_tv_done.setOnClickListener(this);
    }

    private void initUi() {
        m_notice = (TextView) findViewById(R.id.password_notice);

        m_current_password = (EditText) findViewById(R.id.current_password);

        m_new_password = (EditText) findViewById(R.id.new_password);

        m_confirm_password = (EditText) findViewById(R.id.confirm_password);

        m_change_password_filter = new IntentFilter(MessageUti.USER_CHANGE_PASSWORD_REQUEST);
        m_change_password_filter.addAction(MessageUti.USER_CHANGE_PASSWORD_REQUEST);
    }

    private void doneChangePassword() {
        String currentPwd = m_current_password.getText().toString();
        String newPwd = m_new_password.getText().toString();
        String confirmPwd = m_confirm_password.getText().toString();
        if (currentPwd.length() == 0) {
            String strInfo = getString(R.string.input_current_password);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            String strInfo = getString(R.string.inconsistent_new_password);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        if (confirmPwd.length() < 4 || confirmPwd.length() > 16) {
            String strInfo = getString(R.string.change_passowrd_invalid_password);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        String splChrs = "[^a-zA-Z0-9-\\+!@\\$#\\^&\\*]";
        Pattern pattern = Pattern.compile(splChrs);
        Matcher matcher = pattern.matcher(confirmPwd);
        if (matcher.find()) {
            String strInfo = getString(R.string.login_invalid_password);
            Toast.makeText(this, strInfo, Toast.LENGTH_SHORT).show();
            return;
        }

        userChangePassword(LinkAppSettings.USER_NAME, currentPwd, confirmPwd);

        m_current_password.setText(null);
        m_new_password.setText(null);
        m_confirm_password.setText(null);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_current_password.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(m_new_password.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(m_confirm_password.getWindowToken(), 0);
    }

    private void showUpgradeDialog() {
        mTipsDialog = new Dialog(this, R.style.UpgradeMyDialog);
        mTipsDialog.setCanceledOnTouchOutside(false);
        mTipsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout deleteDialogLLayout = (RelativeLayout) View.inflate(this, R.layout.dialog_change_password_success, null);

        TextView okBtn = (TextView) deleteDialogLLayout.findViewById(R.id.change_password_ok_btn);

        mTipsDialog.setContentView(deleteDialogLLayout);
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissTipsDialog();
                finish();
            }
        });
        mTipsDialog.show();
    }

    private void dismissTipsDialog() {
        if (mTipsDialog != null && mTipsDialog.isShowing()) {
            mTipsDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int nID = v.getId();
        switch (nID) {

            case R.id.mRp_forgotPassword:
                mRlForgotPasswordPop.setVisibility(View.INVISIBLE);
                break;

            case R.id.tv_title_back:
            case R.id.ib_title_back:
                SettingAccountActivity.this.finish();
                break;
            case R.id.tv_titlebar_done:
                doneChangePassword();
                break;
            default:
                break;
        }
    }

    public void userChangePassword(String UserName, String CurrentPassword, String NewPassword) {
        API.get().changePassword(UserName, CurrentPassword, NewPassword ,new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingAccountActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                if ("010101".equals(error.getCode())) {
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        m_bNeedBack = false;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
