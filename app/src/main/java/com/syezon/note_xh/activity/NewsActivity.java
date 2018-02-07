package com.syezon.note_xh.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.syezon.note_xh.R;
import com.syezon.note_xh.fragment.NewsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewsActivity extends BaseUmengAnalysisActivity {

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        Fragment fragment = NewsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.rl_content, fragment).show(fragment).commit();
    }

    @OnClick(R.id.iv_cancel)
    public void onViewClicked() {
        finish();
    }
}
