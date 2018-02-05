package com.syezon.note_xh.activity;

import com.umeng.analytics.MobclickAgent;

/**
 *
 */

public class BaseUmengAnalysisActivity extends BaseActivity {


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }
}
