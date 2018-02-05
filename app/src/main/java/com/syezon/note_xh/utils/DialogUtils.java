package com.syezon.note_xh.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.bean.AdInfo;
import com.syezon.note_xh.view.CustomDialog;

import java.io.File;

/**
 *
 */

public class DialogUtils {

    public static void showMigrationConfirmFolder(Context context, final File file, final DialogListener<File> listener){
        final CustomDialog dialog = new CustomDialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_download_hint);
        TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
        tvName.setText("确定将文件保存在："+ file.getAbsolutePath());
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.cancel();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.confirm(file);
            }
        });
        dialog.show();
    }

    public static void showMigrationImportFile(Context context, final File file, final DialogListener<File> listener){
        final CustomDialog dialog = new CustomDialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_download_hint);
        TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
        tvName.setText("是否确定将导入："+ file.getAbsolutePath());
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.cancel();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.confirm(file);
            }
        });
        dialog.show();
    }

    public static void showDownloadHint(final Context context, final AdInfo adInfo, final DialogListener<AdInfo> listener){
        final CustomDialog dialog = new CustomDialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_download_hint);
        TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
        tvName.setText("确定打开：“"+ adInfo.getName() + "”？");
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.cancel();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.confirm(adInfo);
            }
        });
        dialog.show();

    }

    public interface DialogListener<T>{
        void confirm(T bean);
        void cancel();
    }

}
