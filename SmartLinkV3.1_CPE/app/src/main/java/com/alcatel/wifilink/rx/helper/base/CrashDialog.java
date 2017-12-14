package com.alcatel.wifilink.rx.helper.base;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.utils.OtherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrashDialog extends Activity {


    @BindView(R.id.bt_crash_ok)
    Button btCrashOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        ButterKnife.bind(this);
        CrashDialog.this.setFinishOnTouchOutside(false);
    }

    @OnClick(R.id.bt_crash_ok)
    public void onViewClicked() {
        finish();
        OtherUtils.kill();
    }
}
