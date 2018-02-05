package com.syezon.note_xh.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.note_xh.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * "添加图片"弹框
 */
public class AddPicPopWindow extends PopupWindow {
    @BindView(R.id.camera)
    TextView camera;
    @BindView(R.id.local)
    TextView local;
    @BindView(R.id.cancel)
    TextView cancel;

    public AddPicPopWindow(Context mContext, View.OnClickListener itemOnClickListener) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_add_pic, null);
        ButterKnife.bind(this, view);
        camera.setOnClickListener(itemOnClickListener);
        local.setOnClickListener(itemOnClickListener);
        cancel.setOnClickListener(itemOnClickListener);

         /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(view);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.take_photo_anim);
    }
}
