package com.alcatel.wifilink.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.alcatel.wifilink.R;
import com.alcatel.wifilink.common.LinkAppSettings;
import com.alcatel.wifilink.network.API;
import com.alcatel.wifilink.network.MySubscriber;
import com.alcatel.wifilink.network.ResponseBody;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingAccountActivity extends BaseActivityWithBack implements OnClickListener {

    public static final String LOGOUT_FLAG = "LogoutFlag";
    private EditText mCurrentPassword;
    private EditText mNewPassword;
    private EditText mConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
        setTitle(R.string.setting_account);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
        initUi();
    }

    private void initUi() {
        mCurrentPassword = (EditText) findViewById(R.id.current_password);
        mNewPassword = (EditText) findViewById(R.id.new_password);
        mConfirmPassword = (EditText) findViewById(R.id.confirm_password);
        findViewById(R.id.password_notice).setOnClickListener(this);
    }

    private void doneChangePassword() {
        String currentPwd = mCurrentPassword.getText().toString();
        String newPwd = mNewPassword.getText().toString();
        String confirmPwd = mConfirmPassword.getText().toString();


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

        mCurrentPassword.setText(null);
        mNewPassword.setText(null);
        mConfirmPassword.setText(null);

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
        // TODO Auto-generated method stub
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

    public void userChangePassword(String UserName, String CurrentPassword, String NewPassword) {
        API.get().changePassword(UserName, CurrentPassword, NewPassword ,new MySubscriber() {
            @Override
            protected void onSuccess(Object result) {
                Toast.makeText(SettingAccountActivity.this, R.string.new_password_saved, Toast.LENGTH_SHORT).show();
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
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
