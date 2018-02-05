package com.syezon.note_xh.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.adapter.FileFolderAdapter;
import com.syezon.note_xh.utils.DataMigrationUtil;
import com.syezon.note_xh.utils.DialogUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.ZipUtils;
import com.syezon.note_xh.view.CustomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataOutputFileActivity extends BaseUmengAnalysisActivity {

    private static final String TAG = DataOutputFileActivity.class.getName();

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.rv_folder)
    RecyclerView rvFolder;
    @BindView(R.id.ll_floder)
    LinearLayout llFolder;


    private RecyclerView.Adapter adapter;
    private List<File> files = new ArrayList<>();
    private Dialog dialogMigration;
    private TextView tvDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output_file);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        File file = Environment.getExternalStorageDirectory();
        File[] folders = file.listFiles();
        for (int i = 0; i < folders.length; i++) {
            if (folders[i].isDirectory()) {
                files.add(folders[i]);
            }
        }
        files.add(0,file);
        adapter.notifyDataSetChanged();
        addFolder(file);
    }

    private void addFolder(File file) {
        llFolder.removeAllViews();
        do{
            ViewGroup view = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.item_bluetooth_device, null, false);
            TextView folder = (TextView) view.findViewById(R.id.tv_bluetooth);
            folder.setText(file.getName());
            view.setTag(file);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    files.clear();
                    File file = (File) v.getTag();
                    File[] folders = file.listFiles();
                    for (int i = 0; i < folders.length; i++) {
                        if (folders[i].isDirectory()) {
                            files.add(folders[i]);
                        }
                    }
                    files.add(0,file);
                    adapter.notifyDataSetChanged();
                    addFolder(file);
                }
            });
            llFolder.addView(view, 0);
            file = file.getParentFile();

        }while(!file.getAbsoluteFile().equals(Environment.getExternalStorageDirectory().getParentFile()));
    }

    private void initView() {
        adapter = new FileFolderAdapter(this, files, new FileFolderAdapter.FileItemClick() {
            @Override
            public void click(File file, int position) {
                File[] folders = file.listFiles();
                boolean hasDirectory = false;
                int firstPosition = 0;
                for (int i = 0; i < folders.length; i++) {
                    if (folders[i].isDirectory()) {
                        if(!hasDirectory){
                            firstPosition = i;
                        }
                        hasDirectory = true;
                        if(firstPosition == i) files.clear();
                        files.add(folders[i]);
                    }
                }
                if(hasDirectory && position != 0){
                    files.add(0,file);
                    adapter.notifyDataSetChanged();
                    addFolder(file);
                }else{

                    DialogUtils.showMigrationConfirmFolder(DataOutputFileActivity.this, file, new DialogUtils.DialogListener<File>() {
                        @Override
                        public void confirm(File bean) {
                            dataMigration(bean);
                        }

                        @Override
                        public void cancel() {

                        }
                    });

                }
            }
        });
        rvFolder.setAdapter(adapter);
        rvFolder.setLayoutManager(new LinearLayoutManager(this));
        dialogMigration = new CustomDialog(this, R.style.DialogTheme);
        dialogMigration.setContentView(R.layout.dialog_output_file_progress);
        tvDialog = (TextView) dialogMigration.findViewById(R.id.tv);
    }


    private void dataMigration(File file) {
        //显示压缩等待动画
        dialogMigration.show();
        if (DataMigrationUtil.migrationData()) {

            try {
                ZipUtils.zipFolder(Environment.getExternalStorageDirectory() + "/" + DataMigrationUtil.BRIEFNOTE
                        , file.getAbsolutePath() + "/briefnote.zip");
                LogUtil.e(TAG, "文件压缩成功：");
                //显示压缩成功动画
                tvDialog.setText("导出到文件夹成功");
                if(!dialogMigration.isShowing()) dialogMigration.show();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //显示迁移失败动画
        tvDialog.setText("导出到问价失败");
        if(!dialogMigration.isShowing()) dialogMigration.show();

    }

    @OnClick(R.id.iv_cancel)
    public void onViewClicked() {
        finish();
    }

}
