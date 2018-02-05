package com.syezon.note_xh.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.syezon.note_xh.R;
import com.syezon.note_xh.utils.PreferenceKeyUtils;
import com.syezon.note_xh.utils.SharedPerferencesUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.et_promot)
    EditText et_promot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ButterKnife.bind(this);
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("QuestionActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("QuestionActivity");
        MobclickAgent.onPause(this);
    }

    private void setListener() {
        ivBack.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_complete:
                SharedPerferencesUtil.saveStringData(this, PreferenceKeyUtils.QUESTION, et_promot.getText().toString());
                Toast.makeText(this,"提示问题保存成功",Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
