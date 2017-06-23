package com.alcatel.smartlinkv3.utils;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by qianli.ma on 2017/6/23.
 */

public abstract class ActionbarSetting {

    public void settingActionbarAttr(Context context, ActionBar actionBar, int layoutId) {
        // initView action bar
        ActionBar supportActionBar = actionBar;
        View inflate = View.inflate(context, layoutId, null);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
        supportActionBar.setCustomView(inflate, lp);
        disableABCShowHideAnimation(supportActionBar);
        supportActionBar.setDisplayShowHomeEnabled(false);
        supportActionBar.setDisplayShowCustomEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        supportActionBar.show();
        findActionbarView(supportActionBar.getCustomView());
    }

    public abstract void findActionbarView(View view);

    /**
     * H9.消除ActionBar隐藏|显示时候的动画
     *
     * @param actionBar
     */
    private void disableABCShowHideAnimation(ActionBar actionBar) {
        try {
            actionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(actionBar, false);
        } catch (Exception exception) {
            try {
                Field mActionBarField = actionBar.getClass().getSuperclass().getDeclaredField("mActionBar");
                mActionBarField.setAccessible(true);
                Object icsActionBar = mActionBarField.get(actionBar);
                Field mShowHideAnimationEnabledField = icsActionBar.getClass().getDeclaredField("mShowHideAnimationEnabled");
                mShowHideAnimationEnabledField.setAccessible(true);
                mShowHideAnimationEnabledField.set(icsActionBar, false);
                Field mCurrentShowAnimField = icsActionBar.getClass().getDeclaredField("mCurrentShowAnim");
                mCurrentShowAnimField.setAccessible(true);
                mCurrentShowAnimField.set(icsActionBar, null);
            } catch (Exception e) {
            }
        }
    }

}
