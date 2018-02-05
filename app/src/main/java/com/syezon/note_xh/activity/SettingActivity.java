package com.syezon.note_xh.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.event.ThemeChangeEvent;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_cancel)
    ImageView mIvCancel;
    @BindView(R.id.linear_sort)
    LinearLayout linearSort;
    @BindView(R.id.linear_privacy)
    LinearLayout linearPrivacy;
    @BindView(R.id.linear_about)
    LinearLayout linearAbout;
    @BindView(R.id.tv_sort)
    TextView mTvSort;
    @BindView(R.id.tv_lock)
    TextView mTvLock;
    @BindView(R.id.tv_about)
    TextView mTvAbout;
    @BindView(R.id.tv_skin)
    TextView mTvSkin;
    @BindView(R.id.linear_skin)
    LinearLayout linearSkin;
    @BindView(R.id.tv_font)
    TextView mTvFont;
    @BindView(R.id.linear_font)
    LinearLayout linearFont;
    @BindView(R.id.linear_migration)
    LinearLayout mLinearMigration;
    @BindView(R.id.tv_migration)
    TextView mTvMigration;


    private boolean skinChange = false;

    private static final int FROM_SETTING = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingActivity");
        MobclickAgent.onResume(this);
        if (skinChange) {
            skinChange=false;
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 收到改变皮肤事件这将skinChange置于true，当activity走onResume（）时会执行onCreate（）方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onThemeChangeEvent(ThemeChangeEvent event) {
        skinChange = true;
    }

    private void init() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        mTvSort.setTypeface(iconfont);
        mTvLock.setTypeface(iconfont);
        mTvAbout.setTypeface(iconfont);
        mTvSkin.setTypeface(iconfont);
        mTvFont.setTypeface(iconfont);
        mTvFont.setTypeface(iconfont);
        mTvMigration.setTypeface(iconfont);
    }

    private void setListener() {
        mIvCancel.setOnClickListener(this);
        linearAbout.setOnClickListener(this);
        linearSort.setOnClickListener(this);
        linearPrivacy.setOnClickListener(this);
        linearSkin.setOnClickListener(this);
        linearFont.setOnClickListener(this);
        mLinearMigration.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                onBackPressed();
                break;
            case R.id.linear_sort:
                Intent intent = new Intent(this, EditSortActivity.class);
                intent.putExtra("from_where", FROM_SETTING);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.linear_about:
                Intent intent1 = new Intent(this, AboutUsActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.linear_privacy:
                Intent intent2 = new Intent(this, PrivacyActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.linear_skin:
                Intent intent3 = new Intent(this, ChooseSkinActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.linear_font:
                Intent intent4 = new Intent(this, SetFontActivity.class);
                startActivity(intent4);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.linear_migration:
                Intent intent5 = new Intent(this, DataMigration.class);
                startActivity(intent5);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            default:
                break;
        }
    }
}
