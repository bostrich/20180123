package com.syezon.note_xh.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.syezon.note_xh.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteMigrationActivity extends BaseActivity {


    @BindView(R.id.btn_send)
    Button mBtnSend;
    @BindView(R.id.btn_receive)
    Button mBtnReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_migration);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_send, R.id.btn_receive})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                break;
            case R.id.btn_receive:
                break;
        }
    }
}
