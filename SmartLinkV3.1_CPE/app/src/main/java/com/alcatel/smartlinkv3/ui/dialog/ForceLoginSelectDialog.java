package com.alcatel.smartlinkv3.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

public class ForceLoginSelectDialog implements OnClickListener {
    public boolean m_bIsShow = false;
    //	private static ForceLoginSelectDialog m_instance;
    private Context m_context;
    private Dialog m_dlgError = null;
    private TextView m_tvTitle = null;
    private TextView m_tvErrorInfo = null;
    private OnClickButtonConfirm mConfirmCallback = null;
    private OnClickBtnCancel mCancelCallback = null;

    private ForceLoginSelectDialog(Context context) {
        m_context = context;
        createDialog();
    }

    public static ForceLoginSelectDialog getInstance(Context context) {
        return new ForceLoginSelectDialog(context);
    }

    private void createDialog() {
        LayoutInflater factory = LayoutInflater.from(m_context);
        View vError = factory.inflate(R.layout.force_login_select_dialog, null);

        m_dlgError = new Dialog(m_context, R.style.dialog);
        Window window = m_dlgError.getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        m_dlgError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_dlgError.setContentView(vError);
        m_dlgError.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);

        m_tvTitle = (TextView) vError.findViewById(R.id.error_dialog_view_title);

        Button btnOK = (Button) vError.findViewById(R.id.ID_BUTTON_OK);
        btnOK.setOnClickListener(this);

        Button btnCancel = (Button) vError.findViewById(R.id.ID_BUTTON_CANCLE);
        btnCancel.setOnClickListener(this);

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
            case R.id.ID_BUTTON_CANCLE:
                closeDialog();
                if (mCancelCallback != null) {
                    mCancelCallback.onCancel();
                    mCancelCallback = null;
                }
                break;
            default:
                break;
        }
    }

    public void destroyDialog() {
        closeDialog();
//		m_instance = null;
        mConfirmCallback = null;
        mCancelCallback = null;
    }

    public void closeDialog() {
        if (m_dlgError != null && m_dlgError.isShowing()) {
            m_dlgError.dismiss();
            m_bIsShow = false;
        }
    }

    public void showDialog(OnClickButtonConfirm cb) {
        showDialog(R.string.other_login_warning_title,
                R.string.login_other_user_logined_error_forcelogin_msg,
                cb);
    }

    public void showDialog(@StringRes int titleId, @StringRes int messageId, OnClickButtonConfirm cb) {

        if (m_dlgError != null) {
            mConfirmCallback = cb;
            m_tvTitle.setText(m_context.getString(titleId));
            m_tvErrorInfo.setText(m_context.getString(messageId));
            if (!m_bIsShow) {
                m_dlgError.show();
            }
            m_bIsShow = true;
        }
    }

    public void showDialogAndCancel(String strTitle, String strMessage, OnClickButtonConfirm cb, OnClickBtnCancel cc) {

        if (m_dlgError != null) {
            mConfirmCallback = cb;
            mCancelCallback = cc;
            m_tvTitle.setText(strTitle);
            m_tvErrorInfo.setText(strMessage);
            if (!m_bIsShow) {
                m_dlgError.show();
            }
            m_bIsShow = true;
        }
    }

    public interface OnClickButtonConfirm {
        void onConfirm();
    }

    public interface OnClickBtnCancel {
        void onCancel();
    }

}
