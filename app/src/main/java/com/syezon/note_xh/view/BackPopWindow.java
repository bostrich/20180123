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
 * Created by admin on 2016/10/30.
 */
public class BackPopWindow extends PopupWindow {

    @BindView(R.id.quit_edit)
    TextView quitEdit;
    @BindView(R.id.save_edit)
    TextView saveEdit;

    public BackPopWindow(Context mContext, View.OnClickListener itemOnClickListener) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_back, null);
        ButterKnife.bind(this, view);

        quitEdit.setOnClickListener(itemOnClickListener);
        saveEdit.setOnClickListener(itemOnClickListener);

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
