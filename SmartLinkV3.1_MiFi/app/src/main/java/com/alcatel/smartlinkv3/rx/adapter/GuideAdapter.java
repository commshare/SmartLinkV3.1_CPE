package com.alcatel.smartlinkv3.rx.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alcatel.smartlinkv3.R;
import com.alcatel.smartlinkv3.common.Conn;
import com.alcatel.smartlinkv3.rx.impl.login.LoginState;
import com.alcatel.smartlinkv3.rx.tools.API;
import com.alcatel.smartlinkv3.rx.tools.MySubscriber;
import com.alcatel.smartlinkv3.rx.tools.ResponseBody;
import com.alcatel.smartlinkv3.rx.ui.LoginRxActivity;
import com.alcatel.smartlinkv3.ui.activity.RefreshWifiActivity;
import com.alcatel.smartlinkv3.utils.ChangeActivity;
import com.alcatel.smartlinkv3.utils.OtherUtils;
import com.alcatel.smartlinkv3.utils.SPUtils;
import com.alcatel.smartlinkv3.utils.ScreenSize;
import com.alcatel.smartlinkv3.utils.ToastUtil_m;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qianli.ma on 2017/10/26 0026.
 */

public class GuideAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private List<View> pagers;
    private Context context;
    private ViewPager viewPager;
    private LinearLayout ll_points;
    private ProgressDialog waitDialog;

    public GuideAdapter(Context context, ViewPager viewPager, LinearLayout ll_points, List<View> pagers) {
        this.context = context;
        this.viewPager = viewPager;
        this.ll_points = ll_points;

        // 1.采集数据
        if (pagers != null && pagers.size() > 0) {
            this.pagers = pagers;
        } else {
            this.pagers = new ArrayList<>();
            View page1 = View.inflate(context, R.layout.guiderx_view1, null);
            View page2 = View.inflate(context, R.layout.guiderx_view2, null);
            View page3 = View.inflate(context, R.layout.guiderx_view3, null);
            this.pagers.add(page1);
            this.pagers.add(page2);
            this.pagers.add(page3);
        }
        // 2.生成轮播点
        createRollPoint();
        // 3.viewpager关联监听器
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * 生成轮播点
     */
    private void createRollPoint() {
        int height = ScreenSize.getSize(context).height;
        int ll_height = (int) (height * 0.03f);// 1.轮播点布局高
        for (int i = 0; i < pagers.size(); i++) {
            ImageView iv = new ImageView(context);
            int h_point = (int) (ll_height * 0.4f);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(h_point, h_point);
            if (i != 0) {
                lp.setMargins((int) (ll_height * 0.4f), 0, 0, 0);// 2.设置左边距
                iv.setBackgroundResource(R.drawable.corner_circle_bg_unchecked);// 3.设置未选中状态
            } else {
                iv.setBackgroundResource(R.drawable.corner_circle_bg_checked);// 3.设置选中状态
            }
            iv.setLayoutParams(lp);
            ll_points.addView(iv);
        }
    }

    /**
     * 手动更新
     *
     * @param pagers
     */
    public void notifys(List<View> pagers) {
        this.pagers = pagers;
        viewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    /* -------------------------------------------- 适配器重写 -------------------------------------------- */
    @Override
    public int getCount() {
        return pagers != null ? pagers.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = pagers.get(position);
        if (position == pagers.size() - 1) {
            Button bt = (Button) view.findViewById(R.id.bt_guiderx3);
            // TOAT: 按钮点击事件
            bt.setOnClickListener(v -> {
                waitDialog = OtherUtils.showProgressPop(context);
                // 提交引导标记
                SPUtils.getInstance(context).putBoolean(Conn.GUIDE_FLAG, true);
                // 检查是否连接硬件正常
                checkBoard();
            });
        }
        container.addView(view);
        return view;
    }

    /**
     * 检查是否连接硬件正常
     */
    private void checkBoard() {
        API.get().getLoginState(new MySubscriber<LoginState>() {
            @Override
            protected void onSuccess(LoginState result) {
                // 跳转到登陆界面
                OtherUtils.hideProgressPop(waitDialog);
                ChangeActivity.toActivityNormal((Activity) context, LoginRxActivity.class, true);
            }

            @Override
            protected void onResultError(ResponseBody.Error error) {
                // 提示没有连接
                OtherUtils.hideProgressPop(waitDialog);
                ToastUtil_m.show(context, context.getString(R.string.setting_upgrade_no_connection));
                ChangeActivity.toActivity(context, RefreshWifiActivity.class, false, true, false, 0);
            }
        });
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /* -------------------------------------------- Viewpager实现onPagerListener -------------------------------------------- */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 遍历轮播点布局的子元素
        for (int i = 0; i < ll_points.getChildCount(); i++) {
            ImageView point = (ImageView) ll_points.getChildAt(i);
            if (i == position) {// 若为当前选中--> 背景设置为白色
                point.setBackgroundResource(R.drawable.corner_circle_bg_checked);
            } else {// 否则黑色
                point.setBackgroundResource(R.drawable.corner_circle_bg_unchecked);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
