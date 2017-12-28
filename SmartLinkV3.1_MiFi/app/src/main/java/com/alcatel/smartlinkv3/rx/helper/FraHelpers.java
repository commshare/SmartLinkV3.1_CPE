package com.alcatel.smartlinkv3.rx.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qianli.ma on 2017/9/8.
 */
public class FraHelpers {

    private Class initClass;
    private int contain;
    private FragmentActivity activity;
    private Class[] clazzs;// fragment类集合
    private Map<String, Class> tagMap;// TAG对应的class
    private Map<Class, String> classMap;// class对应的tag
    private List<String> tags;// TAG集合
    private FragmentManager fm;

    /**
     * fragment切换辅助类
     *
     * @param activity  必须为FragmentActivity
     * @param clazzs    fragment集合,如[ AFragment.class, BFragment.class... ]
     * @param initClass 初始的fragment class,如:AFragment.class
     * @param contain   fragment容器ID,如:R.id.fragmentlayout
     */
    public FraHelpers(FragmentActivity activity, Class[] clazzs, Class initClass, int contain) {
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
     * 重新加载
     * @param initClass 需要显示的首屏fragment
     */
    public void reload(Class initClass) {
        try {
            Map<String, Fragment> fragmentMap = new HashMap<>();
            Fragment showFragment = null;// 需要显示的fragment
            FragmentTransaction ft = fm.beginTransaction();
            // 1. 重新填充fragment到contain
            for (String tag : tagMap.keySet()) {
                Fragment fragment_temp = fm.findFragmentByTag(tag);
                if (fragment_temp != null) {
                    // 删除原先的fragment
                    ft.remove(fragment_temp);
                    // 再通过字节码文件进行新创
                    fragment_temp = (Fragment) tagMap.get(tag).newInstance();
                    if (showFragment == null) {// 此处防止空指针
                        showFragment = fragment_temp;
                    }
                    // 重新添加到容器中
                    ft.add(contain, fragment_temp, tag);
                    // 装载进临时集合(在下一步显示指定界面做准备)
                    fragmentMap.put(tag, fragment_temp);
                }
            }
            // 2.通过initClass字节码名称找出对应的fragment
            for (String tag : fragmentMap.keySet()) {
                if (tag.equalsIgnoreCase(initClass.getSimpleName())) {
                    showFragment = fragmentMap.get(tag);
                }
            }
            // 3.提交
            ft.show(showFragment);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
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
        tagMap = new HashMap<>();
        classMap = new HashMap<>();
        for (Class clazz : clazzs) {
            tags.add(clazz.getSimpleName());
            tagMap.put(clazz.getSimpleName(), clazz);
            classMap.put(clazz, clazz.getSimpleName());
        }
        return tags;
    }

}
