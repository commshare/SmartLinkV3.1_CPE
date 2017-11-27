package com.alcatel.wifilink.common;/**
 * Created by Administrator on 2016/11/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

/**
 * 作用:转场Activity
 * 作者:Administrator
 * 日期:2016/11/17
 * 时间:0:08
 * 项目:LiyiBuy
 * 作者:Administrator
 */
public class CA {


    /**
     * 获取序列化对象
     *
     * @param intent
     * @return
     */
    public static Parcelable getParcel(Intent intent) {
        return intent.getParcelableExtra("parcelable");
    }

    /**
     * 获取json字符串
     *
     * @param intent
     * @return
     */
    public static String getJson(Intent intent) {
        return intent.getStringExtra("json");
    }

    /**
     * 普通的AC跳转
     *
     * @param context
     * @param clazz
     * @param isFinish
     * @param overAnim
     */
    public static void toAcMainThread(Context context, Class clazz, boolean isFinish, boolean overAnim) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
        if (!overAnim) {
            activity.overridePendingTransition(0, 0);
        }
        if (isFinish) {
            activity.finish();
        }
    }

    /**
     * 普通传输
     *
     * @param context
     * @param clazz
     * @param isSingleTop
     * @param isFinish
     * @param delay
     */
    public static void toActivity(final Context context,// 上下文
                                  final Class<?> clazz,// 跳轉的類
                                  final boolean isSingleTop,// 是否為獨立任務棧
                                  final boolean isFinish,// 是否結束當前介面
                                  boolean overridepedding, // 是否設置轉場動畫
                                  final int delay) {// 延遲跳轉時間
        final Activity activity = (Activity) context;
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        Intent intent = new Intent(context, clazz);
                        if (isSingleTop) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        }
                        context.startActivity(intent);
                        if (!overridepedding) {
                            activity.overridePendingTransition(0, 0);
                        }
                        if (isFinish) {
                            activity.finish();
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    /**
     * 结束当前Activity
     *
     * @param activity
     * @param delay
     */
    public static void finishActivity(final Activity activity, final int delay, final boolean isKilled) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.finish();
                            if (isKilled) {
                                killed();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 附带容量500K以下字符的传输跳转
     *
     * @param context
     * @param clazz
     * @param json
     * @param isSingleTop
     * @param isFinish
     * @param delay
     */
    public static void toActivityJson(final Context context, final Class<?> clazz, final String json, final boolean isSingleTop, final boolean isFinish, final int delay) {
        final Activity activity = (Activity) context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = new Intent(context, clazz);
                            if (json != null) {
                                intent.putExtra("json", json);
                            }
                            if (isSingleTop) {
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            }
                            context.startActivity(intent);
                            if (isFinish) {
                                activity.finish();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 传递序列化对象(容量500K以下)
     *
     * @param context     上下文
     * @param clazz       要跳转过去的activity
     * @param parcelable  序列对象
     * @param isSingleTop 是否问单独任务栈
     * @param isFinish    是否结束当前
     * @param delay       延迟时间
     */
    public static void toActivityParcel(final Context context, final Class<?> clazz, final Parcelable parcelable, final boolean isSingleTop, final boolean isFinish, final int delay) {
        final Activity activity = (Activity) context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = new Intent(context, clazz);
                            if (parcelable != null) {
                                intent.putExtra("parcelable", parcelable);
                            }
                            if (isSingleTop) {
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            }
                            context.startActivity(intent);
                            //activity.overridePendingTransition(R.anim.overin, R.anim.overout);
                            if (isFinish) {
                                activity.finish();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    /**
     * 结束当前进程
     */
    public static void killed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
