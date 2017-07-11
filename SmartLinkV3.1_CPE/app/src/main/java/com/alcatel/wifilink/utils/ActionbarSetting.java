package com.alcatel.wifilink.utils;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by qianli.ma on 2017/6/23.
 */

public abstract class ActionbarSetting {

    private ActionBar actionBar;

    /**
     * 配置Actionbar
     *
     * @param context
     * @param actionBar
     * @param layoutId
     */
    public void settingActionbarAttr(Context context, ActionBar actionBar, int layoutId) {
        this.actionBar = actionBar;
        // initView action bar
        ActionBar supportActionBar = actionBar;
        supportActionBar.setDisplayOptions(0, 0);
        supportActionBar.setDisplayShowHomeEnabled(false);// set home button gone
        supportActionBar.setDisplayHomeAsUpEnabled(false);// set home button gone
        supportActionBar.setDefaultDisplayHomeAsUpEnabled(false);// set home button gone
        supportActionBar.setHomeButtonEnabled(false);// set home button gone
        disableABCShowHideAnimation(supportActionBar);// clear the animation for action when show or hide

        View inflate = View.inflate(context, layoutId, null);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        supportActionBar.setCustomView(inflate, lp);

        supportActionBar.setDisplayShowCustomEnabled(true);/* show customview */
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);/* show customview */
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.show();

        findActionbarView(supportActionBar.getCustomView());// you can deliver the customview to outside
    }

    /**
     * 显示Actionbar
     *
     * @param actionBar
     */
    public void showActionbar(ActionBar actionBar) {
        disableABCShowHideAnimation(actionBar);
        actionBar.show();
    }

    /**
     * 隐藏ACTION BAR
     *
     * @param actionBar
     */
    public void hideActionbar(ActionBar actionBar) {
        disableABCShowHideAnimation(actionBar);
        actionBar.hide();
    }

    /**
     * 获取当前actionbar
     *
     * @return
     */
    public ActionBar getBar() {
        return actionBar;
    }

    protected abstract void findActionbarView(View view);

    /**
     * 消除ActionBar隐藏|显示时候的动画
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
