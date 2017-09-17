package com.alcatel.wifilink.ui.home.helper.main;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.ui.home.helper.cons.Cons;

/**
 * Created by qianli.ma on 2017/9/16.
 */

public class SignalHelper {

    /**
     * 显示信号强度
     *
     * @param iv
     * @param strength
     */
    public static void showSignalStregth(ImageView iv, int strength) {
        switch (strength) {
            case Cons.NOSERVICE:
                iv.setBackgroundResource(R.drawable.home_4g_none);
                break;
            case Cons.LEVEL_0:
                iv.setBackgroundResource(R.drawable.home_4g_none);
                break;
            case Cons.LEVEL_1:
                iv.setBackgroundResource(R.drawable.home_4g1);
                break;
            case Cons.LEVEL_2:
                iv.setBackgroundResource(R.drawable.home_4g2);
                break;
            case Cons.LEVEL_3:
                iv.setBackgroundResource(R.drawable.home_4g3);
                break;
            case Cons.LEVEL_4:
                iv.setBackgroundResource(R.drawable.home_4g4);
                break;
            case Cons.LEVEL_5:
                iv.setBackgroundResource(R.drawable.home_4g5);
                break;
            default:
                iv.setBackgroundResource(R.drawable.home_4g_none);
                break;
        }
    }

    /**
     * 显示信号类型:4G\3G\2G...
     *
     * @param context
     * @param tv
     * @param type
     */
    public static void showSignalType(Context context, TextView tv, int type) {
        String typeStr = "";// 2G\3G...
        String split = " ";
        String defaultStr = context.getString(R.string.signal);// SIGNAL

        if (type == Cons.NOSERVER) {
            tv.setText(context.getString(R.string.signal));
        } else if (type >= Cons.GPRS && type <= Cons.EDGE) {
            typeStr = context.getString(R.string.home_network_type_2g);
        } else if (type >= Cons.HSPA && type <= Cons.DC_HSPA_PLUS) {
            typeStr = context.getString(R.string.home_network_type_3g);
        } else if (type >= Cons.LTE && type <= Cons.LTE_PLUS) {
            typeStr = context.getString(R.string.home_network_type_4g);
        } else if (type >= Cons.CDMA && type <= Cons.GSM) {
            typeStr = context.getString(R.string.home_network_type_2g);
        } else if (type == Cons.EVDO) {
            typeStr = context.getString(R.string.home_network_type_3g);
        } else if (type >= Cons.LTE_FDD && type <= Cons.LTE_TDD) {
            typeStr = context.getString(R.string.home_network_type_4g);
        } else if (type == Cons.CDMA_Ehrpd) {
            typeStr = context.getString(R.string.home_network_type_3g_plus);
        } else {
            tv.setText(context.getString(R.string.signal));
        }

        tv.setText(typeStr + split + defaultStr);
    }

    /**
     * 显示信号类型颜色
     *
     * @param context
     * @param tv
     * @param type
     */
    public static void showSignalTextColor(Context context, TextView tv, int type) {
        if (type <= Cons.NOSERVER) {
            tv.setTextColor(context.getResources().getColor(R.color.grey_text));
        } else {
            tv.setTextColor(context.getResources().getColor(R.color.AA009AFF));
        }
    }
}
