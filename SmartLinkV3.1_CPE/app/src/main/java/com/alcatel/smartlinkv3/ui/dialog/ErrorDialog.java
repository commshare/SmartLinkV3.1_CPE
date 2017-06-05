package com.alcatel.smartlinkv3.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

public class ErrorDialog implements OnClickListener {

    private /*static*/ volatile ErrorDialog m_instance = null;
    //	Do not place Android context classes in static fields (static reference to ErrorDialog which has field m_context pointing to Context); this is a memory leak (and also breaks Instant Run) less... (Ctrl+F1)
    //	A static field will leak contexts.
    //	private Context m_context;
    private View m_vError = null;
    private Dialog m_dlgError = null;
    private Button m_btnRetry = null;
    private TextView m_tvTitle = null;
    private ImageView m_image = null;
    private AnimationDrawable m_anim = null;
    public static boolean m_bIsShow = false;
    private OnClickBtnRetry m_callback;
    private OnClickBtnCancel mCancelCallback = null;

    public synchronized static ErrorDialog getInstance(Context context) {
        //		if(m_instance != null){
        //			m_instance.destroyDialog();
        //		}

        //		m_instance = new ErrorDialog(context);
        return new ErrorDialog(context);
    }

    private ErrorDialog(Context context) {
        createDialog(context);
    }

    private void createDialog(Context context) {
        LayoutInflater factory = LayoutInflater.from(context);
        m_vError = factory.inflate(R.layout.error_popup_dialog, null);

        m_dlgError = new Dialog(context, R.style.dialog);
        Window window = m_dlgError.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_dlgError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_dlgError.setContentView(m_vError);
        m_dlgError.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);

        m_tvTitle = (TextView) m_vError.findViewById(R.id.error_dialog_view_title);

        m_btnRetry = (Button) m_vError.findViewById(R.id.ID_BUTTON_RETRY);
        m_btnRetry.setOnClickListener(this);

        Button ivClose = (Button) m_vError.findViewById(R.id.error_dialog_close_btn);
        ivClose.setOnClickListener(this);

        m_dlgError.setCancelable(false);

        m_image = (ImageView) m_vError.findViewById(R.id.ID_ERROR_IMAGE_VIEW);
        m_anim = (AnimationDrawable) m_image.getBackground();
        //anim.start();
        m_image.post(new Runnable() {
            @Override
            public void run() {
                m_anim.start();
            }
        });


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ID_BUTTON_RETRY:
                closeDialog();
                if (null != m_callback) {
                    m_callback.onRetry();
                }
                break;
            case R.id.error_dialog_close_btn:
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
        mCancelCallback = null;
    }

    public void closeDialog() {
        if (m_dlgError != null && m_dlgError.isShowing()) {
            m_dlgError.dismiss();
            m_bIsShow = false;
        }
    }

    public void showDialog(@StringRes int msgId, OnClickBtnRetry callback) {
        showDialog(m_dlgError.getContext().getResources().getString(msgId), callback);
    }

    public void showDialog(String strMessage, OnClickBtnRetry callback) {
        if (m_dlgError != null) {
            m_callback = callback;
            m_tvTitle.setText(strMessage);
            if (!m_bIsShow) {
                m_dlgError.show();
            }
            m_bIsShow = true;
        }
    }

    public void setCancelCallback(OnClickBtnCancel cb) {
        mCancelCallback = cb;
    }

    //
    public interface OnClickBtnRetry {
        void onRetry();
    }

    public interface OnClickBtnCancel {
        void onCancel();
    }
}
