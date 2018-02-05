package com.syezon.note_xh.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.utils.DisplayUtils;
import com.syezon.note_xh.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * "添加图片"弹框
 */
public class CopyPopWindow extends PopupWindow {

    @BindView(R.id.tv_copy)
    TextView tvCopy;
    @BindView(R.id.tv_edit)
    TextView tvEdit;

    public CopyPopWindow(final Context mContext, final String content, final View editView) {
        @SuppressLint("InflateParams") final
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_copy, null);
        ButterKnife.bind(this, view);

        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringUtils.copyStr(mContext,content);
                dismiss();
            }
        });

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                editView.performClick();
            }
        });
         /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(view);
        // 设置弹出窗体的宽和高
        this.setHeight(DisplayUtils.dip2px(mContext, 50));
        this.setWidth(DisplayUtils.dip2px(mContext, 150));

        // 设置弹出窗体显示时的动画，从底部向上弹出
//        this.setAnimationStyle(R.style.take_photo_anim);
    }
}
