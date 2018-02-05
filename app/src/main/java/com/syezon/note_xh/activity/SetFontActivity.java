package com.syezon.note_xh.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetFontActivity extends BaseActivity {

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.tv_lower_case)
    TextView tvLowerCase;
    @BindView(R.id.tv_upper_case)
    TextView tvUpperCase;
    @BindView(R.id.sb_text_font)
    SeekBar sbTextFont;
    @BindView(R.id.tv_test)
    TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_font);
        ButterKnife.bind(this);
        setListener();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
        MobclickAgent.onPause(this);
    }

    private void init() {
        int textSize = SharedPerferencesUtil.getIntData(this, PreferenceKeyUtils.SP_KEY_NOTE_TEXT_SIZE, 18);
        sbTextFont.setProgress(textSize - 15);
        tvTest.setTextSize(textSize);
    }

    private void setListener() {
        sbTextFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTest.setTextSize(progress + 15);
                SharedPerferencesUtil.saveIntData(SetFontActivity.this, PreferenceKeyUtils.SP_KEY_NOTE_TEXT_SIZE, 15 + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
