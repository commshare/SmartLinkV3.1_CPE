package com.alcatel.wifilink.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alcatel.wifilink.R;

public class CommonErrorInfoDialog implements OnClickListener {

    public static boolean m_bIsShow = false;
    //	private static CommonErrorInfoDialog m_instance;
    private Context m_context;
    private Dialog m_dlgError = null;
    private TextView m_tvTitle = null;
    private TextView m_tvErrorInfo = null;
    private OnClickConfirmButton mConfirmCallback = null;

    private CommonErrorInfoDialog(Context context) {
        m_context = context;
        createDialog();
    }

    public static CommonErrorInfoDialog getInstance(Context context) {
        return new CommonErrorInfoDialog(context);
    }

    private void createDialog() {
        LayoutInflater factory = LayoutInflater.from(m_context);
        View vError = factory.inflate(R.layout.error_info_popup_dialog, null);

        m_dlgError = new Dialog(m_context, R.style.dialog);
        Window window = m_dlgError.getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        m_dlgError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_dlgError.setContentView(vError);
        m_dlgError.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);

        m_tvTitle = (TextView) vError.findViewById(R.id.error_dialog_view_title);

        Button btnOK = (Button) vError.findViewById(R.id.ID_BUTTON_OK);
        btnOK.setOnClickListener(this);

        m_dlgError.setCancelable(false);
        m_tvErrorInfo = (TextView) vError.findViewById(R.id.tv_error_info);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ID_BUTTON_OK:
                closeDialog();
                if (mConfirmCallback != null) {
                    mConfirmCallback.onConfirm();
                    mConfirmCallback = null;
                }
                break;

            default:
                break;
        }
    }

    public void destroyDialog() {
        closeDialog();
        mConfirmCallback = null;
    }

    public void closeDialog() {
        if (m_dlgError != null && m_dlgError.isShowing()) {
            m_dlgError.dismiss();
            m_bIsShow = false;
        }
    }

    public void showDialog(String strTitle, String strMessage) {
        if (m_dlgError != null) {
            m_tvTitle.setText(strTitle);
            m_tvErrorInfo.setText(strMessage);
            if (!m_bIsShow) {
                m_dlgError.show();
            }
            m_bIsShow = true;
        }
    }

    public void setCancelCallback(OnClickConfirmButton cb) {
        mConfirmCallback = cb;
    }

    public interface OnClickConfirmButton {
        void onConfirm();
    }
}
