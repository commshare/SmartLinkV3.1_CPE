package com.alcatel.wifilink.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qianli.ma on 2017/9/8.
 */
public class FraHelper {

    private Class[] clazzs;
    private Class initClass;
    private int contain;
    private FragmentActivity activity;
    private List<String> tags;
    private FragmentManager fm;

    /**
     * fragment切换辅助类
     *
     * @param activity  必须为FragmentActivity
     * @param clazzs    fragment集合,如[ AFragment.class, BFragment.class... ]
     * @param initClass 初始的fragment class,如:AFragment.class
     * @param contain   fragment容器ID,如:R.id.fragmentlayout
     */
    public FraHelper(FragmentActivity activity, Class[] clazzs, Class initClass, int contain) {
        this.activity = activity;
        this.fm = activity.getSupportFragmentManager();
        this.clazzs = clazzs;
        this.initClass = initClass;
        this.contain = contain;
        this.tags = getTags();
        init(initClass);// 初始化fragment
    }

    /**
     * 初始化fragment
     *
     * @param clazz 初始化fragment
     */
    private void init(Class clazz) {
        try {
            FragmentTransaction ft = fm.beginTransaction();// 开启事务
            Fragment fragment = (Fragment) clazz.newInstance();// 通过字节码创建碎片
            ft.replace(contain, fragment, clazz.getSimpleName()).commit();// 以类名为tag--> 提交事务
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 切换fragment
     *
     * @param clazz 需要切换的fragment class
     */
    public void transfer(Class clazz) {
        Fragment fragment;
        FragmentTransaction ft = fm.beginTransaction();// 开启事务

        // 隐藏全部的fragment
        for (String tag : tags) {
            Fragment fragment_temp = fm.findFragmentByTag(tag);
            if (fragment_temp != null) {
                ft.hide(fragment_temp);
            }
        }

        // 以类名为tag, 查找对应的fragment
        String tag = clazz.getSimpleName();
        fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            try {
                // 创建fragment
                fragment = (Fragment) clazz.newInstance();
                // 添加到容器, 以类名为tag
                ft.add(contain, fragment, tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 直接显示fragment
            ft.show(fragment);
        }
        ft.commit();// 提交事务
    }

    /**
     * 把所有fragment字节码文件提取出tag集合
     *
     * @return tag集合 { Afragment.class --> Afragment }
     */
    private List<String> getTags() {
        List<String> tags = new ArrayList<>();
        for (Class clazz : clazzs) {
            tags.add(clazz.getSimpleName());
        }
        return tags;
    }

}
