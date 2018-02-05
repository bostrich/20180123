package com.syezon.note_xh.activity;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.syezon.note_xh.R;
import com.syezon.note_xh.utils.SystemUtils;
import com.syezon.note_xh.utils.ToastUtils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity{
    @BindView(R.id.advice_pic)
    TextView advicePic;
    @BindView(R.id.tv_version_name)
    TextView mTvVersionName;

    private int postern = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        init();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AboutUsActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AboutUsActivity");
        MobclickAgent.onPause(this);
    }

    private void init() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        advicePic.setTypeface(iconfont);

        mTvVersionName.setText(SystemUtils.getVersionName(this));

    }

    private void setListener() {

    }

    @OnClick({R.id.iv_cancel, R.id.iv_logo, R.id.tv_advice, R.id.advice_pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                onBackPressed();
                break;
            case R.id.iv_logo:
                if (postern == 3) {
                    postern = 0;
                    String brand = Build.BRAND;// 手机品牌
                    String model = Build.MODEL;// 手机型号
                    String channel = AnalyticsConfig.getChannel(AboutUsActivity.this);
                    int versionCode = SystemUtils.getVersionCode(AboutUsActivity.this);
                    ToastUtils.showUniqueToast(AboutUsActivity.this, brand + "    " + model + "    " + channel + "    " + versionCode);
                } else {
                    postern++;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            postern = 0;
                        }
                    }, 1000);
                }
                break;
            case R.id.tv_advice:
                //设置反馈并进入反馈界面
                FeedbackAPI.openFeedbackActivity();
                break;
            case R.id.advice_pic:
                //设置反馈并进入反馈界面
                FeedbackAPI.openFeedbackActivity();
                break;
        }
    }
}
