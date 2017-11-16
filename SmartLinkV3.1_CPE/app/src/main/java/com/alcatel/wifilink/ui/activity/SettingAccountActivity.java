package com.alcatel.wifilink.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.alcatel.wifilink.utils.EncryptionUtil;
import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.CA;
import com.alcatel.wifilink.common.LinkAppSettings;
import com.alcatel.wifilink.common.ToastUtil_m;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import com.alcatel.wifilink.utils.OtherUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingAccountActivity extends BaseActivityWithBack implements OnClickListener {

    public static final String LOGOUT_FLAG = "LogoutFlag";
    public static final String TAG = "SettingAccountActivity";
    private EditText mCurrentPassword;
    private EditText mNewPassword;
    private EditText mConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
        setTitle(R.string.setting_account);
        // getWindow().setBackgroundDrawable(null);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
        initUi();
    }

    private void initUi() {
        mCurrentPassword = (EditText) findViewById(R.id.current_password);
        mNewPassword = (EditText) findViewById(R.id.new_password);
        mConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        findViewById(R.id.password_notice).setOnClickListener(this);
    }

    private void doneChangePassword() {
        // 1.get the psd info
        String currentPwd = mCurrentPassword.getText().toString();
        String newPwd = mNewPassword.getText().toString();
        String confirmPwd = mConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(currentPwd)) {
            Toast.makeText(this, getString(R.string.input_current_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(this, getString(R.string.inconsistent_new_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if (confirmPwd.length() < 4 || confirmPwd.length() > 16) {
            Toast.makeText(this, getString(R.string.change_passowrd_invalid_password), Toast.LENGTH_SHORT).show();
            return;
        }

        OtherUtils otherUtils = new OtherUtils();
        otherUtils.setOnCustomizedVersionListener(new OtherUtils.OnCustomizedVersionListener() {
            @Override
            public void getCustomizedStatus(boolean isCustomized) {
                // isCustomized: true--> 则为定制版
                if (isCustomized) {
                    // setting the direct condition psd
                    String splChrs = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\\-\\+\\!\\^\\$\\@\\#\\&\\*])[A-Za-z0-9\\-\\+\\!\\^\\$\\@\\#\\&\\*]{4,16}$";
                    Pattern pattern = Pattern.compile(splChrs);
                    Matcher matcher = pattern.matcher(confirmPwd);
                    if (matcher.find()) {
                        // 是否需要加密
                        otherUtils.setOnSwVersionListener(needToEncrypt -> changePsd(needToEncrypt, LinkAppSettings.USER_NAME, currentPwd, confirmPwd));
                        otherUtils.getDeviceSwVersion();
                    } else {
                        Toast.makeText(SettingAccountActivity.this, getString(R.string.login_invalid_password), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 是否需要加密
                    otherUtils.setOnSwVersionListener(needToEncrypt -> changePsd(needToEncrypt, LinkAppSettings.USER_NAME, currentPwd, confirmPwd));
                    otherUtils.getDeviceSwVersion();
                }
            }
        });
        otherUtils.isCustomVersion();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mCurrentPassword.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mNewPassword.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mConfirmPassword.getWindowToken(), 0);
    }

    private void showDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        int nID = v.getId();
        switch (nID) {
            case R.id.password_notice:
                showDialog(getString(R.string.login_forgotpassword_des));
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            doneChangePassword();
        }
        return super.onOptionsItemSelected(item);
    }

    public void changePsd(boolean needEncrypt, String UserName, String CurrentPassword, String NewPassword) {

        // TODO: 2017/7/13 加密算法--> FW完成加密机制后将下一句代码注释
        //needEncrypt = false;// FW完成加密机制后将该句代码注释

        UserName = needEncrypt ? EncryptionUtil.encryptUser(UserName) : UserName;
        CurrentPassword = needEncrypt ? EncryptionUtil.encryptUser(CurrentPassword) : CurrentPassword;

        NewPassword = needEncrypt ? EncryptionUtil.encrypt(NewPassword) : NewPassword;

        API.get().changePassword(UserName, CurrentPassword, NewPassword, new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingAccountActivity.this, R.string.succeed, Toast.LENGTH_SHORT).show();
                logout();
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                if ("010101".equals(error.getCode())) {
                }
                Toast.makeText(SettingAccountActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onFailure() {
                Toast.makeText(SettingAccountActivity.this, R.string.setting_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * A1.登出
     */
    private void logout() {
        // 2. logout action
        API.get().logout(new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                CA.toActivity(SettingAccountActivity.this, LoginActivity.class, false, true, false, 0);
            }

            @Override
            protected void onFailure() {
                ToastUtil_m.show(SettingAccountActivity.this, getString(R.string.login_logout_failed));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
