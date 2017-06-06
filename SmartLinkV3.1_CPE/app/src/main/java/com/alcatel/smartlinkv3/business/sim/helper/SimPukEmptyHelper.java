package com.alcatel.smartlinkv3.business.sim.helper;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by qianli.ma on 2017/6/6.
 */

public abstract class SimPukEmptyHelper implements TextWatcher {

    private Context context;
    private EditText[] eds;

    public SimPukEmptyHelper(Context context, EditText... eds) {
        this.context = context;
        this.eds = eds;
        setFocus();
    }

    public abstract void getEmpty(boolean isEmpty);

    public void setFocus() {
        for (EditText ed : eds) {
            // 1.为每个ed设置焦点监听
            ed.addTextChangedListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 2.当字符发生改变--> 判断所有的ED--> 判断结果回调
        getEmpty(isEmpty(eds));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    /* ------------------------------------------------- helper -------------------------------------------------*/
    /* ------------------------------------------------- helper -------------------------------------------------*/

    /**
     * H1.判断是否有空值
     *
     * @param eds
     * @return
     */
    public boolean isEmpty(EditText... eds) {
        boolean empty = false;
        for (EditText ed : eds) {
            String content = ed.getText().toString().trim().replace(" ", "");
            if (TextUtils.isEmpty(content)) {
                empty = true;
            }
        }
        return empty;
    }
}



