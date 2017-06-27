package com.alcatel.smartlinkv3.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.Constants;
import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.CPEConfig;
import com.alcatel.smartlinkv3.common.ChangeActivity;
import com.alcatel.smartlinkv3.model.user.LoginResult;
import com.alcatel.smartlinkv3.network.API;
import com.alcatel.smartlinkv3.network.MySubscriber;
import com.alcatel.smartlinkv3.network.ResponseBody;
import com.alcatel.smartlinkv3.ui.home.allsetup.HomeActivity;
import com.alcatel.smartlinkv3.ui.setupwizard.allsetup.SetupWizardActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private Button mApplyBtn;
    private EditText mPasswdEdit;

    private TextView mPromptText;

    private int mRemainingTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRemainingTimes = getIntent().getIntExtra("remain_times", 0);
        mApplyBtn = (Button) findViewById(R.id.login_apply_btn);
        mApplyBtn.setOnClickListener(this);

        mPasswdEdit = (EditText) findViewById(R.id.login_edit_view);
        mPasswdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.d(TAG, "beforeTextChanged, s:" + s + ", start:" + start + ", count:" + count + ", after:" + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d(TAG, "onTextChanged, s:" + s + ", start:" + start + ", before:" + before + ", count:" + count);
                String passwd = mPasswdEdit.getText().toString().trim();
                mApplyBtn.setEnabled(passwd.length() >= 5);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.d(TAG, "afterTextChanged, s:" + s);

            }
        });

        mPromptText = (TextView) findViewById(R.id.tv_time_remain);
        updatePromptText(mRemainingTimes);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_apply_btn:
                String passwd = mPasswdEdit.getText().toString().trim();
                API.get().login(Constants.USER_NAME_ADMIN, passwd, new MySubscriber<LoginResult>() {
                    @Override
                    protected void onSuccess(LoginResult result) {
                        Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        boolean quickSetupFlag = CPEConfig.getInstance().getQuickSetupFlag();
                        if (quickSetupFlag) {
                            launchHomeActivity();
                        } else {
                            ChangeActivity.toActivity(LoginActivity.this, SetupWizardActivity.class, true, true, false, 0);
                        }
                        Log.d(TAG, "token = " + result.getToken());
                        API.get().updateToken(result.getToken());
                    }

                    @Override
                    protected void onResultError(ResponseBody.Error error) {
                        if ("010101".equals(error.getCode())) {
                            mRemainingTimes--;
                            updatePromptText(mRemainingTimes);
                        }
                    }
                });
                break;
        }
    }

    private void updatePromptText(int remainingTimes) {
        String text;
        if (remainingTimes == 0) {
            text = getString(R.string.times_remain_zero);
        } else if (remainingTimes == 1) {
            text = getString(R.string.times_remain_one);
        } else {
            text = getString(R.string.times_remain_many, remainingTimes);
        }
        mPromptText.setText(text);
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }
}
