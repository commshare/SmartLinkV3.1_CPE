package com.alcatel.smartlinkv3.ui.activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.business.BusinessManager;
import com.alcatel.smartlinkv3.business.model.SimStatusModel;
import com.alcatel.smartlinkv3.common.DataValue;
import com.alcatel.smartlinkv3.common.ENUM.SIMState;
import com.alcatel.smartlinkv3.common.MessageUti;

//import android.graphics.Typeface;


public class SettingChangePinActivity extends BaseActivity implements OnClickListener, OnKeyListener, TextWatcher {

    public static boolean m_isShow = false;
    private ProgressDialog m_progress_dialog = null;
    private EditText m_puk_edit;
    private EditText m_new_pin_edit;
    private EditText m_confirm_pin_edit;
    private TextView m_puk_remain_tv;
    private boolean m_last_puk_success = true;
    private Button m_confirmBtn;
    private Button m_ivCancelBtn = null;
    private boolean m_is_user_close = false;

    private TextView m_tv_title = null;
    private ImageButton m_ib_back = null;
    private TextView m_tv_back = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_setting_change_pin);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);

        controlTitlebar();
        initUi();
    }

    private void controlTitlebar() {
        m_tv_title = (TextView) findViewById(R.id.tv_title_title);
        m_tv_title.setText(R.string.pin_change_pin_title);
        //back button and text
        m_ib_back = (ImageButton) findViewById(R.id.ib_title_back);
        m_tv_back = (TextView) findViewById(R.id.tv_title_back);
        m_ib_back.setOnClickListener(this);
        m_tv_back.setOnClickListener(this);
    }

    private void initUi() {
        m_puk_remain_tv = (TextView) this.findViewById(R.id.puk_remained_times);
        m_puk_edit = (EditText) this.findViewById(R.id.puk_code_edit);
        m_puk_edit.setOnKeyListener(this);
        m_puk_edit.addTextChangedListener(this);

        m_new_pin_edit = (EditText) this.findViewById(R.id.new_pin_edit);
        m_new_pin_edit.setOnKeyListener(this);
        m_new_pin_edit.addTextChangedListener(this);

        m_confirm_pin_edit = (EditText) this.findViewById(R.id.confirm_pin_edit);
        m_confirm_pin_edit.setOnKeyListener(this);
        m_confirm_pin_edit.addTextChangedListener(this);

        m_confirmBtn = (Button) this.findViewById(R.id.puk_apply_btn);
        m_confirmBtn.setOnClickListener(this);
        //m_confirmBtn.setTypeface(ViewUtilities.getTypeface());

        m_ivCancelBtn = (Button) this.findViewById(R.id.puk_close_btn);
        m_ivCancelBtn.setOnClickListener(this);

//        m_btnCancel = (Button)this.findViewById(R.id.puk_cancel_btn);
//        m_btnCancel.setOnClickListener(this);

    }


    private void apply() {
        String puk = m_puk_edit.getText().toString();
        String new_pin = m_new_pin_edit.getText().toString();
        String confirm_pin = m_confirm_pin_edit.getText().toString();

        if (puk.length() < 8) {
            m_puk_edit.requestFocus();
            String message = getApplicationContext().getResources().getString(R.string.puk_format_wrong_msg);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            return;
        }

        if (!new_pin.equals(confirm_pin)) {
            m_confirm_pin_edit.setFocusable(true);
            m_confirm_pin_edit.requestFocus();
            String message = getApplicationContext().getResources().getString(R.string.puk_prompt_str);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            return;
        }

//        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(this.getWindowToken(), 0);

        String strTitle = getApplicationContext().getString(R.string.IDS_PUK_LOCKED);
        String strMsg = getApplicationContext().getString(R.string.IDS_PIN_CHECK_PUK_PROGRESS);
        strMsg += "...";
        m_progress_dialog = ProgressDialog.show(getApplicationContext(), strTitle, strMsg, true, false);

        DataValue dataPuk = new DataValue();
        dataPuk.addParam("puk", puk);
        dataPuk.addParam("pin", new_pin);
        BusinessManager.getInstance().sendRequestMessage(MessageUti.SIM_UNLOCK_PUK_REQUEST, dataPuk);
    }

    public void onSimStatusReady(SimStatusModel simStatusData) {
        if (null != simStatusData) {
            if (SIMState.PukRequired == simStatusData.m_SIMState
                    || !m_last_puk_success) {
                if (!m_last_puk_success) {
                    //String message = "PUK Failed!";

                    m_puk_edit.setText("");
                    m_new_pin_edit.setText("");
                    m_confirm_pin_edit.setText("");
                    m_puk_remain_tv.setText(formatRemainTimes(simStatusData.m_nPukRemainingTimes));
                    m_puk_edit.requestFocus();
                    m_last_puk_success = true;

                    if (null != m_progress_dialog && m_progress_dialog.isShowing()) {
                        m_progress_dialog.dismiss();
                    }
                    // this.startAnimation(m_dlgPukShake);
                    //closeDialog();
                }
            }
        }
    }

    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        if (m_puk_edit.getText().length() >= 4 && m_new_pin_edit.getText().length() >= 4 && m_confirm_pin_edit.getText().length() >= 4) {
            m_confirmBtn.setEnabled(true);
        } else {
            m_confirmBtn.setEnabled(false);
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub

    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            m_isShow = false;
        }

        return false;
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.puk_apply_btn:
                apply();
                break;
            case R.id.puk_close_btn:
                m_is_user_close = true;
                break;
            case R.id.tv_title_back:
            case R.id.ib_title_back:
                this.onBackPressed();
                break;
            default:
                break;
        }
    }

    private String formatRemainTimes(int nTimes) {
        String format = this.getApplicationContext().getResources().getString(
                R.string.remain_times);
        return String.format(format, nTimes);
    }

}
