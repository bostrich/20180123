package com.syezon.note_xh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.syezon.note_xh.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataImportActivity extends BaseUmengAnalysisActivity {

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.ll_phone)
    LinearLayout llPhone;
    @BindView(R.id.ll_file)
    LinearLayout llFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_import);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_cancel, R.id.ll_phone, R.id.ll_file})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.ll_phone:
                Intent intent_phone = new Intent(this, DataImportPhoneActivity.class);
                startActivity(intent_phone);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.ll_file:
                Intent intent_file = new Intent(this, DataImportFileActivity.class);
                startActivity(intent_file);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
    }
}
