package com.syezon.note_xh.activity;

import android.os.Handler;

import com.syezon.note_xh.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 *
 */

public class BaseUmengAnalysisActivity extends BaseActivity {

    public Handler mHandler;

    public void initHandler(){}

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
//        LogUtil.e("page", this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }
}
