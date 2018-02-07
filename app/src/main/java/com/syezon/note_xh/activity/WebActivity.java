package com.syezon.note_xh.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.WebHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class WebActivity extends BaseUmengAnalysisActivity {

    // 消息
    public static final int MSG_LOAD_URL_OK = 0; // 加载成功
    public static final int MSG_LOAD_URL_FAILED = 1; // 加载失败
    public static final int MSG_DIMISS_LOAD = 2;
    private static final int GET_RECOMMEND_FOR_YOU = 121;

    private static WebHelper.WebLoadCallBack webLoadCallBack;
    // 控件-标题栏
    private LinearLayout mLayoutBack;
    private TextView tvTitle;
    // 控件-内容栏
    private FrameLayout mLayoutWeb;
    private WebView webView;
    // 控件-加载栏
    private RelativeLayout rlytLoad;
    private View vLoad;
    private TextView tvLoad;
    private boolean isExit = false;
    // 记录数据
    private String mTitle;
    private String mUrl;
    private ImageView imgBack;
    private boolean backToNews;


    private Handler mHandler;

    /**
     * 设置页面加载回调
     */
    public static void setWebLoadCallBack(WebHelper.WebLoadCallBack callBack) {
        webLoadCallBack = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mTitle = getIntent().getStringExtra(WebHelper.ARG_TITLE);
        mUrl = getIntent().getStringExtra(WebHelper.ARG_URL);
        backToNews = getIntent().getBooleanExtra(WebHelper.ARG_BACK_NEWS, false);
        initView();
        initHandler();

        // 加载页面
        loadUrl(mUrl);
    }


    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_LOAD_URL_OK:
                        if (mLayoutWeb.getVisibility() != View.VISIBLE) {
                            mLayoutWeb.setVisibility(View.VISIBLE);
                        }
                        vLoad.clearAnimation();
                        rlytLoad.setVisibility(View.GONE);
                        break;
                    case MSG_LOAD_URL_FAILED:
                        showErrorPage();
                        isExit = true;
                        break;

                    case MSG_DIMISS_LOAD:
                        if (rlytLoad.getVisibility() != View.GONE) {
                            vLoad.clearAnimation();
                            rlytLoad.setVisibility(View.GONE);
                        }
                        if (mLayoutWeb.getVisibility() != View.VISIBLE) {
                            mLayoutWeb.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        };
    }


    protected void initView() {
        // 控件-标题栏
        imgBack = (ImageView) findViewById(R.id.iv_cancel);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    if (backToNews) {
                        // 跳转到新闻页面
                        Intent intent = new Intent(WebActivity.this, NewsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    finish();
                }
            }
        });
        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (mTitle != null) {
            tvTitle.setText(mTitle);
        }
        // 控件-内容栏
        mLayoutWeb = (FrameLayout) findViewById(R.id.layout_webView);
        initWebSettings();
        // 加载栏
        rlytLoad = (RelativeLayout) findViewById(R.id.rlyt_load);
        rlytLoad.setVisibility(View.VISIBLE);
        vLoad = findViewById(R.id.v_load);
        tvLoad = (TextView) findViewById(R.id.tv_load);
    }

    private void initWebSettings() {
        webView = new WebView(this);
        // 设置Client
        mLayoutWeb.addView(webView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        WebSettings webSetting = webView.getSettings();
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(5 * 1024 * 1024);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setBlockNetworkImage(true);
    }



    /**
     * 展示加载界面
     */
    private void showLoadPage() {
        mLayoutWeb.setVisibility(View.GONE);
        rlytLoad.setVisibility(View.VISIBLE);
    }

    /**
     * 展示错误界面
     */
    private void showErrorPage() {
        final FrameLayout frameLayout = (FrameLayout) getWindow().getDecorView();
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        final View errorView = LayoutInflater.from(this).inflate(R.layout.layout_web_error, null);
        frameLayout.addView(errorView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        errorView.setVisibility(View.VISIBLE);
        mLayoutWeb.setVisibility(View.GONE);
        rlytLoad.setVisibility(View.GONE);
        vLoad.clearAnimation();
        RelativeLayout layout = (RelativeLayout) errorView.findViewById(R.id.layout_clean_total_tip);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.bottomMargin = DisplayUtils.getRealHeight(this) - DisplayUtils.getScreenHeight(this);
        layout.setLayoutParams(params);
        errorView.findViewById(R.id.surfing_nowifi_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null && webView.getUrl() != null) {
                    webView.reload();
                    frameLayout.removeView(errorView);
                }
            }
        });
        View viewFill = errorView.findViewById(R.id.view_fill);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewFill.getLayoutParams();
        layoutParams.height = statusBarHeight;
        viewFill.setLayoutParams(layoutParams);
        errorView.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 加载页面
     *
     * @param url
     */
    private void loadUrl(String url) {
        if (url == null || url.equals("")) {
            mHandler.sendEmptyMessage(MSG_LOAD_URL_FAILED);
            return;
        }

        showLoadPage();
        // 开启加载动画
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        vLoad.startAnimation(rotateAnimation);
        // 加载地址内容
        webView.loadUrl(url);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 退出当前页面
     */
    private void exit() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void finish() {
        webLoadCallBack = null;
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
            if(backToNews){
                // 跳转到新闻页面
                Intent intent = new Intent(WebActivity.this, NewsActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        } catch (Exception e) {
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        try {
            mLayoutWeb.removeView(webView);
            webView.removeAllViews();
            webView.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * Web视图
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if ("alipays".equals(uri.getScheme()) || "weixin".equals(uri.getScheme())) {//支付宝支付或微信支付
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException notFoundEx) {//尝试h5网页支付
                    return true;
                }
            }
            if (url.startsWith("tel:")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.getSettings().setBlockNetworkImage(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String
                failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (errorCode == -2) {
                view.stopLoading();
                view.removeAllViews();
                view.clearView();
                mHandler.sendEmptyMessage(MSG_LOAD_URL_FAILED);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            try {
                handler.proceed();
                mHandler.sendEmptyMessage(MSG_LOAD_URL_FAILED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class MyWebChromeClient extends WebChromeClient {

        private boolean isSendLoadUrlOk = false;

        @Override
        public void onReceivedTitle(WebView view, String title) {
            tvTitle.setText(title);
        }

        @Override
        public void onProgressChanged(WebView webView, int progress) {
            super.onProgressChanged(webView, progress);
            if (progress >= 50) {
                mHandler.sendEmptyMessage(MSG_DIMISS_LOAD);
            }
            if ((progress > 80 || progress == 100) && !isSendLoadUrlOk) {
                mHandler.sendEmptyMessage(MSG_LOAD_URL_OK);
                if (webLoadCallBack != null) {
                    webLoadCallBack.loadComplete(mUrl);
                }
                isSendLoadUrlOk = true;
            }
        }
    }
}
