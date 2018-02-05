package com.syezon.note_xh.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.syezon.note_xh.R;
import com.syezon.note_xh.utils.LogUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataOutputActivity extends BaseUmengAnalysisActivity {

    private static final String TAG = DataOutputActivity.class.getName();
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.ll_phone)
    LinearLayout llPhone;
    @BindView(R.id.ll_file)
    LinearLayout llFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_cancel, R.id.ll_phone, R.id.ll_file})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.ll_phone:
                Intent intent_phone = new Intent(this, DataOutputPhoneActivity.class);
                startActivity(intent_phone);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.ll_file:
                Intent intent_file = new Intent(this, DataOutputFileActivity.class);
                startActivity(intent_file);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);
            File file = new File(img_path);
            LogUtil.e(TAG, "获取到文件路径：" + file.getAbsolutePath());
        }
    }
}
