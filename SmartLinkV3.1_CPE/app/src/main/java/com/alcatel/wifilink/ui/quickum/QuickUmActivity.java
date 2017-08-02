package com.alcatel.wifilink.ui.quickum;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.alcatel.wifilink.R;
import com.alcatel.wifilink.utils.WebviewUtils;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuickUmActivity extends Activity {

    @BindView(R.id.wv)
    WebView wv_detailNews;
    @BindView(R.id.tbs_view)
    com.tencent.smtt.sdk.WebView tbsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_um);
        ButterKnife.bind(this);
        // TODO: 2017/8/2  
        String url = "http://www.alcatel-move.com/um/url.html?project=HH70&custom=Generic&lang=en";
        WebviewUtils wvUtils = new WebviewUtils(wv_detailNews, url) {
            @Override
            public void respondDirect(String respondDirect) {
                // get the pdf file
                // getPdfFile(respondDirect);
                settting(tbsView, respondDirect);
            }
        };


    }

    public void settting(com.tencent.smtt.sdk.WebView wb, String url) {
        // 1.播放视频防止闪烁
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        WebSettings settings = wb.getSettings();
        settings.setJavaScriptEnabled(true);// 显示js样式

    /* 设置网址 */
        wb.loadUrl(url);

        // 2.设置加载进来的页面自适应手机屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);

        // 3.UA代理(可选)
        //settings.setUserAgent( );

        // 4.设置响应回调
        wb.setWebViewClient(new WebViewClient() {

            // public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //     view.loadUrl(url);
            //     return true;
            // }


            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
        });
    }

    private void getPdfFile(String respondDirect) {
        //www.alcatel-move.com/um/url.html?project=HH70&custom=Generic&lang=en
        Log.d("ma_mv", "response: " + respondDirect);
        // Map<String, String> map = new HashMap<>();
        // map.put("project", "HH70");
        // map.put("custom", "Generic");
        // map.put("lang", "en");

    }
}
