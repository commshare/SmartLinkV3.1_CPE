package com.alcatel.smartlinkv3.fileexplorer;

import com.alcatel.smartlinkv3.R;

import android.app.Dialog;
import android.content.Context;

public class TransparentWaitDialog {

    private Context mContext;
    private Dialog waitDialog;
    
    public TransparentWaitDialog (Context context) {
        this.mContext = context;
        this.waitDialog = new Dialog(mContext, R.style.TRANSDIALOG);
        this.waitDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
    }
    
    public void show () {
        waitDialog.show();
        waitDialog.setContentView(R.layout.trans_dialog);
    }
    
    public void dismiss() {
        waitDialog.dismiss();
    }
}