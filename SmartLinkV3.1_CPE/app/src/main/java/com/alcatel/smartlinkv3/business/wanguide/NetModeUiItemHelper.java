package com.alcatel.smartlinkv3.business.wanguide;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alcatel.smartlinkv3.R;

/**
 * Created by qianli.ma on 2017/5/23.
 */

public class NetModeUiItemHelper {
    private static NetModeUiItemHelper netmodeItemHelper;
    private static Context context;

    public static NetModeUiItemHelper getInstance(Context context) {
        if (netmodeItemHelper == null) {
            netmodeItemHelper = new NetModeUiItemHelper(context);
        }
        return netmodeItemHelper;
    }

    public NetModeUiItemHelper(Context context) {
        super();
        this.context = context.getApplicationContext();
    }

    /**
     * 填充子项视图并返回子项控件
     *
     * @param resId 需要显示的标题
     * @return [view, tv_title, ed_content]
     */
    public static ItemNetMode show(int resId) {
        View item_Account_pppoe = View.inflate(context, R.layout.item_netmode, null);
        TextView mTv_PPPOE_Title = (TextView) item_Account_pppoe.findViewById(R.id.mTv_netmode_item_title);
        TextView mTv_PPPOE_SubTitle = (TextView) item_Account_pppoe.findViewById(R.id.mTv_netmode_item_subTitle);
        EditText mEd_PPPOE_Content = (EditText) item_Account_pppoe.findViewById(R.id.mEd_netmode_item_content);
        mTv_PPPOE_Title.setText(context.getResources().getString(resId));

        ItemNetMode itemNetMode = new ItemNetMode();
        itemNetMode.itemView = item_Account_pppoe;
        itemNetMode.itemTv = mTv_PPPOE_Title;
        itemNetMode.itemSubTv = mTv_PPPOE_SubTitle;
        itemNetMode.itemEd = mEd_PPPOE_Content;
        return itemNetMode;
    }

    public static class ItemNetMode {
        public View itemView;// 视图
        public TextView itemTv;// 子项标题
        public TextView itemSubTv;// 子项副标题
        public EditText itemEd;// 子项编辑框
    }

}
