package com.syezon.note_xh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.syezon.note_xh.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataMigration extends BaseUmengAnalysisActivity {

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.rl_import)
    LinearLayout rlImport;
    @BindView(R.id.rl_output)
    LinearLayout rlOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_migration);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_cancel, R.id.rl_import, R.id.rl_output})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.rl_import:
                Intent intent = new Intent(this, DataImportActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.rl_output:
                Intent intent_output = new Intent(this, DataOutputActivity.class);
                startActivity(intent_output);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
    }
}
