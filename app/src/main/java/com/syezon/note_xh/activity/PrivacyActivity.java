package com.syezon.note_xh.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.event.ProtectionActivated;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_switch)
    ImageView ivSwitch;
    @BindView(R.id.tv_privacy_lock)
    TextView tvPrivacyLock;

    private static final int OPEN = 2;
    private static final int CLOSE = 1;
    //开关状态
    private int tag = CLOSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PrivacyActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PrivacyActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        tvPrivacyLock.setTypeface(iconfont);

        if (!TextUtils.isEmpty(SharedPerferencesUtil.getStringData(this, PreferenceKeyUtils.PASSWORD, ""))) {
            ivSwitch.setBackgroundResource(R.mipmap.btno);
            tag = OPEN;
        }
    }

    private void setListener() {
        ivBack.setOnClickListener(this);
        ivSwitch.setOnClickListener(this);
    }

    //登录成功就将开关置于开状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProtectionActivated(ProtectionActivated event) {
        ivSwitch.setBackgroundResource(R.mipmap.btno);
        tag = OPEN;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_switch:
                if (tag == CLOSE) {
                    //跳到设置密码界面
                    Intent intent = new Intent(this, LockToProtectActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                } else {
                    //将开关置于关，并清空SP里储存的密码信息
                    ivSwitch.setBackgroundResource(R.mipmap.btnc);
                    SharedPerferencesUtil.saveStringData(this, PreferenceKeyUtils.PASSWORD, "");
                    SharedPerferencesUtil.saveStringData(this, PreferenceKeyUtils.QUESTION, "");
                    tag = CLOSE;
                }
                break;
        }
    }

}
