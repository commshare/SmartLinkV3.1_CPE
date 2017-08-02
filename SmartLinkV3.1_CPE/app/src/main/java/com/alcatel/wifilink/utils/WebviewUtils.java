package com.alcatel.wifilink.utils;

import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public abstract class WebviewUtils {

    private WebView wv_detailNews;
    private String url;

    public WebviewUtils(WebView wv_detailNews, String url) {
        this.wv_detailNews = wv_detailNews;
        this.url = url;
        set();
    }

    public void set() {
        // 设置参数
        wv_detailNews.loadUrl(url);
        WebSettings wv_setting = wv_detailNews.getSettings();
        wv_setting.setJavaScriptEnabled(true);
        wv_setting.setDomStorageEnabled(true);
        wv_setting.setBuiltInZoomControls(true);
        wv_setting.setUseWideViewPort(true);
        wv_detailNews.setInitialScale(25);
        wv_detailNews.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebView.HitTestResult hit = view.getHitTestResult();
                if (hit != null) {// 如果链接不再重定向或者转发--> 则最后把数据抛出
                    Log.d("ma_mv", "ma_url: " + url);
                    respondDirect(url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    public abstract void respondDirect(String respondDirect);

}
